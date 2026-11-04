package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PeopleDatabase
import com.example.data.SampleDataProvider
import com.example.model.AuditLog
import com.example.model.DashboardStats
import com.example.model.Person
import com.example.util.CsvExporter
import com.example.util.IdGenerator
import com.example.util.PdfExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppScreen {
    DASHBOARD,
    PEOPLE_LIST,
    ID_CARD_PREVIEW,
    ID_VERIFICATION,
    AUDIT_LOGS,
    USER_PORTAL,
    SETTINGS
}

enum class VerificationStatus {
    IDLE,
    VERIFIED_ACTIVE,
    VERIFIED_INACTIVE,
    SUSPENDED,
    NOT_FOUND
}

data class VerificationResult(
    val status: VerificationStatus,
    val person: Person? = null,
    val searchedId: String = ""
)

data class FilterCriteria(
    val query: String = "",
    val status: String = "All",
    val department: String = "All",
    val role: String = "All",
    val verified: String = "All",
    val sort: String = "Newest"
)

class PeopleViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PeopleDatabase.getDatabase(application, viewModelScope)
    private val personDao = database.personDao()
    private val auditLogDao = database.auditLogDao()

    // Navigation and portal mode
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isAdminMode = MutableStateFlow(true)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _selectedPerson = MutableStateFlow<Person?>(null)
    val selectedPerson: StateFlow<Person?> = _selectedPerson.asStateFlow()

    // Active user for User Portal
    private val _userPortalPerson = MutableStateFlow<Person?>(null)
    val userPortalPerson: StateFlow<Person?> = _userPortalPerson.asStateFlow()

    // Search and Filters
    val searchQuery = MutableStateFlow("")
    val filterStatus = MutableStateFlow("All") // All, Active, Inactive, Suspended
    val filterDepartment = MutableStateFlow("All")
    val filterRole = MutableStateFlow("All")
    val filterVerified = MutableStateFlow("All") // All, Verified, Unverified
    val sortBy = MutableStateFlow("Newest") // Newest, Oldest, Name, ID

    // Verification Tool
    val verifySearchInput = MutableStateFlow("")
    private val _verificationResult = MutableStateFlow(VerificationResult(VerificationStatus.IDLE))
    val verificationResult: StateFlow<VerificationResult> = _verificationResult.asStateFlow()

    // ID Prefix setting
    val idPrefixSetting = MutableStateFlow("PCD")
    val isDarkThemeForced = MutableStateFlow<Boolean?>(null) // null = system, true = dark, false = light

    // All people from DB
    val allPeople: StateFlow<List<Person>> = personDao.getAllPeople()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All logs from DB
    val allAuditLogs: StateFlow<List<AuditLog>> = auditLogDao.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val filterCriteria = combine(
        searchQuery,
        filterStatus,
        filterDepartment,
        filterRole,
        filterVerified
    ) { query, status, dept, role, ver ->
        FilterCriteria(query, status, dept, role, ver, sortBy.value)
    }.combine(sortBy) { criteria, sort ->
        criteria.copy(sort = sort)
    }

    // Filtered People List
    val filteredPeople: StateFlow<List<Person>> = allPeople.combine(filterCriteria) { people, criteria ->
        var list = people

        if (criteria.query.isNotBlank()) {
            val q = criteria.query.trim().lowercase(Locale.ROOT)
            list = list.filter {
                it.fullName.lowercase(Locale.ROOT).contains(q) ||
                        it.uniqueId.lowercase(Locale.ROOT).contains(q) ||
                        it.email.lowercase(Locale.ROOT).contains(q) ||
                        it.department.lowercase(Locale.ROOT).contains(q) ||
                        it.phone.contains(q)
            }
        }

        if (criteria.status != "All") {
            list = list.filter { it.status.equals(criteria.status, ignoreCase = true) }
        }

        if (criteria.department != "All") {
            list = list.filter { it.department.equals(criteria.department, ignoreCase = true) }
        }

        if (criteria.role != "All") {
            list = list.filter { it.role.equals(criteria.role, ignoreCase = true) }
        }

        if (criteria.verified != "All") {
            val wantVerified = criteria.verified.equals("Verified", ignoreCase = true)
            list = list.filter { it.isVerified == wantVerified }
        }

        when (criteria.sort) {
            "Newest" -> list.sortedByDescending { it.registrationDate }
            "Oldest" -> list.sortedBy { it.registrationDate }
            "Name" -> list.sortedBy { it.fullName.lowercase(Locale.ROOT) }
            "ID" -> list.sortedBy { it.uniqueId }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Real-Time Dashboard Statistics
    val dashboardStats: StateFlow<DashboardStats> = allPeople.combine(allPeople) { people, _ ->
        computeStats(people)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    init {
        // Automatically assign first person to User Portal if none selected
        viewModelScope.launch {
            allPeople.collect { people ->
                if (_userPortalPerson.value == null && people.isNotEmpty()) {
                    _userPortalPerson.value = people.first()
                }
            }
        }
    }

    private fun computeStats(people: List<Person>): DashboardStats {
        if (people.isEmpty()) return DashboardStats()

        val total = people.size
        val active = people.count { it.status.equals("Active", ignoreCase = true) }
        val inactive = people.count { it.status.equals("Inactive", ignoreCase = true) }
        val suspended = people.count { it.status.equals("Suspended", ignoreCase = true) }
        val verified = people.count { it.isVerified }
        val unverified = total - verified

        val male = people.count { it.gender.equals("Male", ignoreCase = true) }
        val female = people.count { it.gender.equals("Female", ignoreCase = true) }
        val other = total - (male + female)

        val cal = Calendar.getInstance()
        val now = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val sevenDaysAgo = cal.timeInMillis

        cal.timeInMillis = now
        cal.add(Calendar.DAY_OF_YEAR, -30)
        val thirtyDaysAgo = cal.timeInMillis

        val thisWeekCount = people.count { it.registrationDate >= sevenDaysAgo }
        val thisMonthCount = people.count { it.registrationDate >= thirtyDaysAgo }

        val growthRate = if (total > thisMonthCount && (total - thisMonthCount) > 0) {
            (thisMonthCount.toDouble() / (total - thisMonthCount)) * 100.0
        } else {
            18.5
        }

        val deptMap = people.groupingBy { it.department }.eachCount()

        // Monthly bucket for registration trends (last 6 months)
        val monthFormat = SimpleDateFormat("MMM", Locale.US)
        val monthBuckets = mutableMapOf<String, Int>()
        for (i in 5 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.MONTH, -i)
            val monthLabel = monthFormat.format(c.time)
            monthBuckets[monthLabel] = 0
        }

        for (person in people) {
            val m = monthFormat.format(Date(person.registrationDate))
            if (monthBuckets.containsKey(m)) {
                monthBuckets[m] = (monthBuckets[m] ?: 0) + 1
            }
        }

        return DashboardStats(
            totalPeople = total,
            activeCount = active,
            inactiveCount = inactive,
            suspendedCount = suspended,
            verifiedCount = verified,
            unverifiedCount = unverified,
            maleCount = male,
            femaleCount = female,
            otherGenderCount = other,
            newRegistrationsThisMonth = thisMonthCount,
            newRegistrationsThisWeek = thisWeekCount,
            growthRatePercent = growthRate,
            departmentBreakdown = deptMap,
            monthlyRegistrations = monthBuckets.toList()
        )
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectPersonForPreview(person: Person) {
        _selectedPerson.value = person
        _currentScreen.value = AppScreen.ID_CARD_PREVIEW
    }

    fun selectPersonForUserPortal(person: Person) {
        _userPortalPerson.value = person
        _isAdminMode.value = false
        _currentScreen.value = AppScreen.USER_PORTAL
    }

    fun toggleAdminMode(isAdmin: Boolean) {
        _isAdminMode.value = isAdmin
        if (isAdmin) {
            if (_currentScreen.value == AppScreen.USER_PORTAL) {
                _currentScreen.value = AppScreen.DASHBOARD
            }
        } else {
            if (_userPortalPerson.value == null && allPeople.value.isNotEmpty()) {
                _userPortalPerson.value = allPeople.value.first()
            }
            _currentScreen.value = AppScreen.USER_PORTAL
        }
    }

    fun registerNewPerson(
        fullName: String,
        email: String,
        phone: String,
        gender: String,
        department: String,
        role: String,
        status: String,
        isVerified: Boolean,
        dateOfBirth: String,
        address: String,
        emergencyContact: String,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val latest = personDao.getLatestUniqueId()
            val nextId = IdGenerator.generateNextId(latest, idPrefixSetting.value)

            // Random vibrant accent color for avatar
            val colors = listOf(0xFFFF6D00, 0xFF3B82F6, 0xFF10B981, 0xFF8B5CF6, 0xFFEC4899, 0xFF06B6D4, 0xFFF59E0B)
            val randomColor = colors.random()

            val newPerson = Person(
                uniqueId = nextId,
                fullName = fullName.trim(),
                email = email.trim(),
                phone = phone.trim(),
                gender = gender,
                department = department,
                role = role,
                status = status,
                isVerified = isVerified,
                dateOfBirth = dateOfBirth,
                registrationDate = System.currentTimeMillis(),
                avatarColorHex = randomColor,
                address = address.trim(),
                emergencyContact = emergencyContact.trim(),
                notes = notes.trim()
            )

            personDao.insertPerson(newPerson)

            auditLogDao.insertLog(
                AuditLog(
                    action = "REGISTER",
                    targetUniqueId = nextId,
                    targetName = newPerson.fullName,
                    details = "Registered new user in $department as $role with status $status",
                    performedBy = if (_isAdminMode.value) "Admin (admin@peoplehub.io)" else "Self-Registration",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun updatePerson(updated: Person) {
        viewModelScope.launch(Dispatchers.IO) {
            personDao.updatePerson(updated)
            if (_selectedPerson.value?.id == updated.id) {
                _selectedPerson.value = updated
            }
            if (_userPortalPerson.value?.id == updated.id) {
                _userPortalPerson.value = updated
            }

            auditLogDao.insertLog(
                AuditLog(
                    action = "UPDATE",
                    targetUniqueId = updated.uniqueId,
                    targetName = updated.fullName,
                    details = "Updated profile information (Dept: ${updated.department}, Role: ${updated.role})",
                    performedBy = if (_isAdminMode.value) "Admin (admin@peoplehub.io)" else "User (${updated.fullName})",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun togglePersonStatus(person: Person) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStatus = when (person.status) {
                "Active" -> "Inactive"
                "Inactive" -> "Suspended"
                else -> "Active"
            }
            val updated = person.copy(status = newStatus)
            personDao.updatePerson(updated)
            if (_selectedPerson.value?.id == person.id) _selectedPerson.value = updated

            auditLogDao.insertLog(
                AuditLog(
                    action = "STATUS_CHANGE",
                    targetUniqueId = person.uniqueId,
                    targetName = person.fullName,
                    details = "Status changed from ${person.status} to $newStatus",
                    performedBy = "Admin (admin@peoplehub.io)",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun toggleVerification(person: Person) {
        viewModelScope.launch(Dispatchers.IO) {
            val newVerified = !person.isVerified
            val updated = person.copy(isVerified = newVerified)
            personDao.updatePerson(updated)
            if (_selectedPerson.value?.id == person.id) _selectedPerson.value = updated

            auditLogDao.insertLog(
                AuditLog(
                    action = "VERIFY",
                    targetUniqueId = person.uniqueId,
                    targetName = person.fullName,
                    details = if (newVerified) "Account verified and official badge approved" else "Verification revoked",
                    performedBy = "Admin (admin@peoplehub.io)",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deletePerson(person: Person) {
        viewModelScope.launch(Dispatchers.IO) {
            personDao.deletePerson(person)
            if (_selectedPerson.value?.id == person.id) {
                _selectedPerson.value = null
            }
            if (_userPortalPerson.value?.id == person.id) {
                _userPortalPerson.value = allPeople.value.firstOrNull { it.id != person.id }
            }

            auditLogDao.insertLog(
                AuditLog(
                    action = "DELETE",
                    targetUniqueId = person.uniqueId,
                    targetName = person.fullName,
                    details = "Account permanently removed from directory",
                    performedBy = "Admin (admin@peoplehub.io)",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun performIdVerification(rawQuery: String) {
        val query = rawQuery.trim()
        verifySearchInput.value = query
        if (query.isBlank()) {
            _verificationResult.value = VerificationResult(VerificationStatus.IDLE)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val found = personDao.getPersonByUniqueId(query)
                ?: personDao.getAllPeople().stateIn(viewModelScope).value.firstOrNull {
                    it.uniqueId.equals(query, ignoreCase = true) || it.email.equals(query, ignoreCase = true)
                }

            val status = when {
                found == null -> VerificationStatus.NOT_FOUND
                found.status.equals("Suspended", ignoreCase = true) -> VerificationStatus.SUSPENDED
                !found.status.equals("Active", ignoreCase = true) -> VerificationStatus.VERIFIED_INACTIVE
                else -> VerificationStatus.VERIFIED_ACTIVE
            }

            _verificationResult.value = VerificationResult(status, found, query)

            auditLogDao.insertLog(
                AuditLog(
                    action = "VERIFY_LOOKUP",
                    targetUniqueId = query,
                    targetName = found?.fullName ?: "Unknown / Not Found",
                    details = "Verification lookup performed. Result: $status",
                    performedBy = "Security Scanner",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun exportRegistryToPdf(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val people = allPeople.value
            val stats = dashboardStats.value
            val file = PdfExporter.generateRegistryReportPdf(context, people, stats)

            auditLogDao.insertLog(
                AuditLog(
                    action = "EXPORT",
                    targetUniqueId = "REGISTRY",
                    targetName = "All Registered People",
                    details = "Exported ${people.size} records to PDF registry report",
                    performedBy = "Admin (admin@peoplehub.io)",
                    timestamp = System.currentTimeMillis()
                )
            )

            launch(Dispatchers.Main) {
                PdfExporter.sharePdf(context, file, "Share PeopleHub PDF Registry Report")
            }
        }
    }

    fun exportIdCardToPdf(context: Context, person: Person) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = PdfExporter.generateIdCardPdf(context, person)

            auditLogDao.insertLog(
                AuditLog(
                    action = "EXPORT_ID_CARD",
                    targetUniqueId = person.uniqueId,
                    targetName = person.fullName,
                    details = "Generated high-resolution printable ID card PDF",
                    performedBy = if (_isAdminMode.value) "Admin" else person.fullName,
                    timestamp = System.currentTimeMillis()
                )
            )

            launch(Dispatchers.Main) {
                PdfExporter.sharePdf(context, file, "Print / Share ID Card for ${person.fullName}")
            }
        }
    }

    fun exportRegistryToCsv(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val people = allPeople.value
            val file = CsvExporter.exportPeopleToCsv(context, people)

            auditLogDao.insertLog(
                AuditLog(
                    action = "EXPORT",
                    targetUniqueId = "REGISTRY",
                    targetName = "All Registered People",
                    details = "Exported ${people.size} records to CSV format",
                    performedBy = "Admin (admin@peoplehub.io)",
                    timestamp = System.currentTimeMillis()
                )
            )

            launch(Dispatchers.Main) {
                CsvExporter.shareCsv(context, file)
            }
        }
    }

    fun resetToDemoData() {
        viewModelScope.launch(Dispatchers.IO) {
            personDao.deleteAll()
            auditLogDao.clearLogs()
            personDao.insertAll(SampleDataProvider.samplePeople)
            auditLogDao.insertAll(SampleDataProvider.sampleLogs)

            auditLogDao.insertLog(
                AuditLog(
                    action = "SYSTEM_RESET",
                    targetUniqueId = "SYSTEM",
                    targetName = "Database",
                    details = "Reset sample data to initial state",
                    performedBy = "Admin",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
