package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class Person(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uniqueId: String, // e.g. "PCD-2026-000001"
    val fullName: String,
    val email: String,
    val phone: String,
    val gender: String, // "Male", "Female", "Non-Binary", "Other"
    val department: String, // "Engineering", "Design", "Marketing", "Finance", "HR", "Operations", "Executive"
    val role: String, // "Admin", "Manager", "Staff", "Member", "Guest"
    val status: String, // "Active", "Inactive", "Suspended"
    val isVerified: Boolean = true,
    val dateOfBirth: String, // "YYYY-MM-DD"
    val registrationDate: Long = System.currentTimeMillis(),
    val avatarColorHex: Long = 0xFFFF6D00, // Argb color
    val address: String = "123 Innovation Way, Suite 400",
    val emergencyContact: String = "Jane Doe (+1-555-0192)",
    val notes: String = ""
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String, // "REGISTER", "UPDATE", "STATUS_CHANGE", "DELETE", "VERIFY", "EXPORT"
    val targetUniqueId: String,
    val targetName: String,
    val details: String,
    val performedBy: String = "Admin (admin@peoplehub.io)",
    val timestamp: Long = System.currentTimeMillis()
)

data class DashboardStats(
    val totalPeople: Int = 0,
    val activeCount: Int = 0,
    val inactiveCount: Int = 0,
    val suspendedCount: Int = 0,
    val verifiedCount: Int = 0,
    val unverifiedCount: Int = 0,
    val maleCount: Int = 0,
    val femaleCount: Int = 0,
    val otherGenderCount: Int = 0,
    val newRegistrationsThisMonth: Int = 0,
    val newRegistrationsThisWeek: Int = 0,
    val growthRatePercent: Double = 0.0,
    val departmentBreakdown: Map<String, Int> = emptyMap(),
    val monthlyRegistrations: List<Pair<String, Int>> = emptyList()
)
