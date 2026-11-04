package com.example.data

import com.example.model.AuditLog
import com.example.model.Person
import java.util.Calendar

object SampleDataProvider {

    private fun getTimestampMonthsAgo(monthsAgo: Int, dayOffset: Int = 0): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -monthsAgo)
        cal.add(Calendar.DAY_OF_MONTH, dayOffset)
        return cal.timeInMillis
    }

    val samplePeople = listOf(
        Person(
            id = 1,
            uniqueId = "PCD-2026-000001",
            fullName = "Sarah J. Chen",
            email = "sarah.chen@peoplehub.io",
            phone = "+1 (555) 234-5678",
            gender = "Female",
            department = "Engineering",
            role = "Staff",
            status = "Active",
            isVerified = true,
            dateOfBirth = "1994-06-15",
            registrationDate = getTimestampMonthsAgo(5, 4),
            avatarColorHex = 0xFFFF6D00,
            address = "742 Evergreen Terrace, San Francisco, CA",
            emergencyContact = "Marcus Chen (+1-555-9821)",
            notes = "Lead full-stack architect on cloud migration initiative."
        ),
        Person(
            id = 2,
            uniqueId = "PCD-2026-000002",
            fullName = "Alexandre Dubois",
            email = "alex.dubois@peoplehub.io",
            phone = "+1 (555) 345-6789",
            gender = "Male",
            department = "Executive",
            role = "Admin",
            status = "Active",
            isVerified = true,
            dateOfBirth = "1988-11-23",
            registrationDate = getTimestampMonthsAgo(5, 10),
            avatarColorHex = 0xFF3B82F6,
            address = "1200 Market Street, San Francisco, CA",
            emergencyContact = "Claire Dubois (+1-555-8712)",
            notes = "Chief Information Security Officer."
        ),
        Person(
            id = 3,
            uniqueId = "PCD-2026-000003",
            fullName = "Elena Rostova",
            email = "elena.r@peoplehub.io",
            phone = "+1 (555) 456-7890",
            gender = "Female",
            department = "Design",
            role = "Manager",
            status = "Active",
            isVerified = true,
            dateOfBirth = "1992-04-03",
            registrationDate = getTimestampMonthsAgo(4, 2),
            avatarColorHex = 0xFF8B5CF6,
            address = "88 King St, Seattle, WA",
            emergencyContact = "Dmitri Rostov (+1-555-6632)",
            notes = "Design systems director and UX lead."
        ),
        Person(
            id = 4,
            uniqueId = "PCD-2026-000004",
            fullName = "Marcus Aurelius Vance",
            email = "marcus.v@peoplehub.io",
            phone = "+1 (555) 567-8901",
            gender = "Male",
            department = "Finance",
            role = "Staff",
            status = "Active",
            isVerified = true,
            dateOfBirth = "1990-08-19",
            registrationDate = getTimestampMonthsAgo(3, 14),
            avatarColorHex = 0xFF10B981,
            address = "45 Wall Street, New York, NY",
            emergencyContact = "Olivia Vance (+1-555-2311)",
            notes = "Senior financial auditor and budget controller."
        ),
        Person(
            id = 5,
            uniqueId = "PCD-2026-000005",
            fullName = "Amina Al-Mansoor",
            email = "amina.m@peoplehub.io",
            phone = "+1 (555) 678-9012",
            gender = "Female",
            department = "Marketing",
            role = "Staff",
            status = "Active",
            isVerified = true,
            dateOfBirth = "1995-12-07",
            registrationDate = getTimestampMonthsAgo(2, 5),
            avatarColorHex = 0xFFEC4899,
            address = "300 Peachtree Rd, Atlanta, GA",
            emergencyContact = "Tariq Mansoor (+1-555-7761)",
            notes = "Global brand strategy & growth campaign manager."
        ),
        Person(
            id = 6,
            uniqueId = "PCD-2026-000006",
            fullName = "David K. O'Connor",
            email = "david.oc@peoplehub.io",
            phone = "+1 (555) 789-0123",
            gender = "Male",
            department = "Operations",
            role = "Manager",
            status = "Inactive",
            isVerified = false,
            dateOfBirth = "1985-02-14",
            registrationDate = getTimestampMonthsAgo(2, 18),
            avatarColorHex = 0xFF64748B,
            address = "520 North Michigan Ave, Chicago, IL",
            emergencyContact = "Fiona O'Connor (+1-555-9012)",
            notes = "On sabbatical leave until Q1 2027."
        ),
        Person(
            id = 7,
            uniqueId = "PCD-2026-000007",
            fullName = "Priya Narayanan",
            email = "priya.n@peoplehub.io",
            phone = "+1 (555) 890-1234",
            gender = "Female",
            department = "HR",
            role = "Staff",
            status = "Active",
            isVerified = true,
            dateOfBirth = "1993-09-30",
            registrationDate = getTimestampMonthsAgo(1, 8),
            avatarColorHex = 0xFF06B6D4,
            address = "180 Mission St, San Francisco, CA",
            emergencyContact = "Rohan Narayanan (+1-555-3498)",
            notes = "People operations and talent acquisition specialist."
        ),
        Person(
            id = 8,
            uniqueId = "PCD-2026-000008",
            fullName = "Jordan Taylor",
            email = "jordan.t@peoplehub.io",
            phone = "+1 (555) 901-2345",
            gender = "Non-Binary",
            department = "Engineering",
            role = "Member",
            status = "Active",
            isVerified = true,
            dateOfBirth = "1998-03-22",
            registrationDate = getTimestampMonthsAgo(1, 20),
            avatarColorHex = 0xFF14B8A6,
            address = "404 Silicon Ave, Austin, TX",
            emergencyContact = "Sam Taylor (+1-555-8934)",
            notes = "DevOps and Kubernetes infrastructure engineer."
        ),
        Person(
            id = 9,
            uniqueId = "PCD-2026-000009",
            fullName = "Kenji Sato",
            email = "kenji.sato@peoplehub.io",
            phone = "+1 (555) 012-3456",
            gender = "Male",
            department = "Engineering",
            role = "Staff",
            status = "Active",
            isVerified = false,
            dateOfBirth = "1991-07-11",
            registrationDate = getTimestampMonthsAgo(0, -6), // This month
            avatarColorHex = 0xFFF59E0B,
            address = "77 2nd Ave, San Jose, CA",
            emergencyContact = "Hana Sato (+1-555-6214)",
            notes = "Pending background check documentation."
        ),
        Person(
            id = 10,
            uniqueId = "PCD-2026-000010",
            fullName = "Isabella Rossi",
            email = "isabella.r@peoplehub.io",
            phone = "+1 (555) 123-4567",
            gender = "Female",
            department = "Design",
            role = "Staff",
            status = "Active",
            isVerified = true,
            dateOfBirth = "1996-10-05",
            registrationDate = getTimestampMonthsAgo(0, -2), // This week
            avatarColorHex = 0xFFFF6D00,
            address = "215 Ocean Boulevard, Miami, FL",
            emergencyContact = "Matteo Rossi (+1-555-4309)",
            notes = "UI motion graphics and product design."
        ),
        Person(
            id = 11,
            uniqueId = "PCD-2026-000011",
            fullName = "Lucas Morales",
            email = "lucas.m@peoplehub.io",
            phone = "+1 (555) 234-9876",
            gender = "Male",
            department = "Marketing",
            role = "Guest",
            status = "Suspended",
            isVerified = false,
            dateOfBirth = "1989-01-18",
            registrationDate = getTimestampMonthsAgo(3, 1),
            avatarColorHex = 0xFFEF4444,
            address = "900 Broadway, New York, NY",
            emergencyContact = "Carmen Morales (+1-555-1982)",
            notes = "Account temporarily locked pending security review."
        ),
        Person(
            id = 12,
            uniqueId = "PCD-2026-000012",
            fullName = "Zoe Washington",
            email = "zoe.w@peoplehub.io",
            phone = "+1 (555) 345-0987",
            gender = "Female",
            department = "Finance",
            role = "Staff",
            status = "Active",
            isVerified = true,
            dateOfBirth = "1997-05-29",
            registrationDate = getTimestampMonthsAgo(0, 0), // Today
            avatarColorHex = 0xFF6366F1,
            address = "550 Pennsylvania Ave, Washington, DC",
            emergencyContact = "James Washington (+1-555-8723)",
            notes = "Newly registered financial analyst."
        )
    )

    val sampleLogs = listOf(
        AuditLog(
            action = "REGISTER",
            targetUniqueId = "PCD-2026-000012",
            targetName = "Zoe Washington",
            details = "New member registered into Finance department",
            performedBy = "Admin (admin@peoplehub.io)",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 45 // 45 mins ago
        ),
        AuditLog(
            action = "VERIFY",
            targetUniqueId = "PCD-2026-000010",
            targetName = "Isabella Rossi",
            details = "Identity verification approved and digital badge issued",
            performedBy = "Admin (admin@peoplehub.io)",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 180 // 3 hours ago
        ),
        AuditLog(
            action = "STATUS_CHANGE",
            targetUniqueId = "PCD-2026-000011",
            targetName = "Lucas Morales",
            details = "Status changed from Active to Suspended (Policy check)",
            performedBy = "Security Officer",
            timestamp = System.currentTimeMillis() - 1000 * 3600 * 24 // 1 day ago
        ),
        AuditLog(
            action = "EXPORT",
            targetUniqueId = "SYSTEM",
            targetName = "All People Records",
            details = "Exported 11 personnel records to CSV format",
            performedBy = "Admin (admin@peoplehub.io)",
            timestamp = System.currentTimeMillis() - 1000 * 3600 * 48 // 2 days ago
        ),
        AuditLog(
            action = "REGISTER",
            targetUniqueId = "PCD-2026-000010",
            targetName = "Isabella Rossi",
            details = "Registered into Design team as Staff",
            performedBy = "Admin (admin@peoplehub.io)",
            timestamp = System.currentTimeMillis() - 1000 * 3600 * 72 // 3 days ago
        )
    )
}
