package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Person
import com.example.ui.theme.InactiveRed
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryVariant
import com.example.ui.theme.PendingYellow
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PeopleViewModel

@Composable
fun PeopleListScreen(
    viewModel: PeopleViewModel,
    onOpenRegisterDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val people by viewModel.filteredPeople.collectAsState()
    val allPeople by viewModel.allPeople.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val filterDept by viewModel.filterDepartment.collectAsState()
    val filterRole by viewModel.filterRole.collectAsState()
    val filterVer by viewModel.filterVerified.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    var personToEdit by remember { mutableStateOf<Person?>(null) }
    var personToDelete by remember { mutableStateOf<Person?>(null) }
    var personForDetailDialog by remember { mutableStateOf<Person?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("people_list_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Search Bar & Sort Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("people_search_input"),
                        placeholder = { Text("Search by name, ID, email, dept...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = OrangePrimary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort By", tint = OrangePrimary)
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            listOf("Newest", "Oldest", "Name", "ID").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text("Sort: $option") },
                                    onClick = {
                                        viewModel.sortBy.value = option
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 2. Filter Chips Horizontal Scroll
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Status Filter Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Active", "Inactive", "Suspended").forEach { st ->
                            val isSelected = filterStatus == st
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.filterStatus.value = st },
                                label = { Text(st) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OrangePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        // Verified Filter
                        listOf("Verified", "Unverified").forEach { ver ->
                            val isSelected = filterVer == ver
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.filterVerified.value = if (isSelected) "All" else ver
                                },
                                label = { Text(ver) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VerifiedGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Department Filter Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Engineering", "Design", "Marketing", "Finance", "HR", "Operations").forEach { dept ->
                            val isSelected = filterDept == dept
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.filterDepartment.value = dept },
                                label = { Text(dept) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF3B82F6),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // 3. Count Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REGISTERED PERSONNEL (${people.size} of ${allPeople.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    if (searchQuery.isNotEmpty() || filterStatus != "All" || filterDept != "All" || filterVer != "All") {
                        Text(
                            text = "Reset Filters",
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.clickable {
                                viewModel.searchQuery.value = ""
                                viewModel.filterStatus.value = "All"
                                viewModel.filterDepartment.value = "All"
                                viewModel.filterRole.value = "All"
                                viewModel.filterVerified.value = "All"
                            }
                        )
                    }
                }
            }

            // 4. People Items
            if (people.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No matching people found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try adjusting your search query or reset filter parameters.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(people, key = { it.id }) { person ->
                    PersonCardItem(
                        person = person,
                        onViewIdCard = { viewModel.selectPersonForPreview(person) },
                        onEdit = { personToEdit = person },
                        onToggleStatus = { viewModel.togglePersonStatus(person) },
                        onToggleVerify = { viewModel.toggleVerification(person) },
                        onDelete = { personToDelete = person },
                        onCardClick = { personForDetailDialog = person }
                    )
                }
            }

            // Bottom space for FAB
            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }

        // Floating Action Button to Register New Person
        FloatingActionButton(
            onClick = onOpenRegisterDialog,
            containerColor = OrangePrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("people_list_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Register New Person")
        }
    }

    // Edit Person Dialog
    if (personToEdit != null) {
        AddEditPersonDialog(
            initialPerson = personToEdit,
            onDismiss = { personToEdit = null },
            onSave = { fullName, email, phone, gender, department, role, status, isVerified, dateOfBirth, address, emergencyContact, notes ->
                val updated = personToEdit!!.copy(
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    gender = gender,
                    department = department,
                    role = role,
                    status = status,
                    isVerified = isVerified,
                    dateOfBirth = dateOfBirth,
                    address = address,
                    emergencyContact = emergencyContact,
                    notes = notes
                )
                viewModel.updatePerson(updated)
            }
        )
    }

    // Delete Confirmation Dialog
    if (personToDelete != null) {
        val target = personToDelete!!
        AlertDialog(
            onDismissRequest = { personToDelete = null },
            title = { Text("Delete Personnel Record?") },
            text = {
                Text("Are you sure you want to permanently delete ${target.fullName} (${target.uniqueId}) from the directory? This action will be audited.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePerson(target)
                        personToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InactiveRed)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { personToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Person Detail Dialog
    if (personForDetailDialog != null) {
        val p = personForDetailDialog!!
        PersonProfileDetailDialog(
            person = p,
            onDismiss = { personForDetailDialog = null },
            onViewIdCard = {
                personForDetailDialog = null
                viewModel.selectPersonForPreview(p)
            },
            onEdit = {
                val temp = p
                personForDetailDialog = null
                personToEdit = temp
            }
        )
    }
}

@Composable
fun PersonCardItem(
    person: Person,
    onViewIdCard: () -> Unit,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    onToggleVerify: () -> Unit,
    onDelete: () -> Unit,
    onCardClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onCardClick() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circle
                val initials = person.fullName.split(" ")
                    .filter { it.isNotEmpty() }
                    .take(2)
                    .map { it.first() }
                    .joinToString("")
                    .uppercase()

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(person.avatarColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = person.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        if (person.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = VerifiedGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = person.uniqueId,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                        Text(
                            text = "•",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${person.role} (${person.department})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status chip
                val statusBg = when (person.status) {
                    "Active" -> VerifiedGreen.copy(alpha = 0.15f)
                    "Inactive" -> PendingYellow.copy(alpha = 0.15f)
                    else -> InactiveRed.copy(alpha = 0.15f)
                }
                val statusColor = when (person.status) {
                    "Active" -> VerifiedGreen
                    "Inactive" -> PendingYellow
                    else -> InactiveRed
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = person.status.uppercase(),
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Dropdown menu for more actions
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Digital ID Card") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = OrangePrimary) },
                            onClick = {
                                menuExpanded = false
                                onViewIdCard()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Information") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Toggle Status (${if (person.status == "Active") "Deactivate" else "Activate"})") },
                            leadingIcon = { Icon(Icons.Default.PowerSettingsNew, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onToggleStatus()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (person.isVerified) "Revoke Verification" else "Approve Verification") },
                            leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onToggleVerify()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Account", color = InactiveRed) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = InactiveRed) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Quick Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewIdCard,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Digital ID", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PersonProfileDetailDialog(
    person: Person,
    onDismiss: () -> Unit,
    onViewIdCard: () -> Unit,
    onEdit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(person.avatarColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = person.fullName.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = person.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = person.uniqueId, style = MaterialTheme.typography.bodySmall, color = OrangePrimary, fontFamily = FontFamily.Monospace)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow("Email", person.email)
                DetailRow("Phone", person.phone.ifBlank { "N/A" })
                DetailRow("Department", person.department)
                DetailRow("Role", person.role)
                DetailRow("Gender", person.gender)
                DetailRow("Status", person.status)
                DetailRow("Verified Badge", if (person.isVerified) "Verified (Official)" else "Unverified")
                DetailRow("Date of Birth", person.dateOfBirth)
                DetailRow("Address", person.address.ifBlank { "N/A" })
                DetailRow("Emergency Contact", person.emergencyContact.ifBlank { "N/A" })
                if (person.notes.isNotBlank()) {
                    DetailRow("Notes", person.notes)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onViewIdCard,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Open ID Card")
            }
        },
        dismissButton = {
            TextButton(onClick = onEdit) {
                Text("Edit Profile")
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
