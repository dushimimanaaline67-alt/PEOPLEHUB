package com.example.util

import java.util.Calendar

object IdGenerator {

    /**
     * Generates a unique ID in format PCD-YYYY-000001
     * @param prefix Default is "PCD"
     * @param latestId The most recent unique ID string from database (e.g. "PCD-2026-000012")
     */
    fun generateNextId(latestId: String?, prefix: String = "PCD"): String {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        var nextNumber = 1

        if (!latestId.isNullOrBlank()) {
            val parts = latestId.split("-")
            if (parts.size >= 3) {
                val numPart = parts.last().toIntOrNull()
                if (numPart != null) {
                    nextNumber = numPart + 1
                }
            }
        }

        val paddedNum = String.format("%06d", nextNumber)
        return "$prefix-$currentYear-$paddedNum"
    }
}
