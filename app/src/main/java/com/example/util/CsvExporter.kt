package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.model.Person
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun exportPeopleToCsv(context: Context, people: List<Person>): File {
        val outputDir = File(context.cacheDir, "csv_exports").apply { mkdirs() }
        val outputFile = File(outputDir, "PeopleHub_Export_${System.currentTimeMillis()}.csv")

        val writer = FileOutputStream(outputFile).bufferedWriter()
        // Header
        writer.write("Unique ID,Full Name,Email,Phone,Gender,Department,Role,Status,Verified,Date of Birth,Registration Date,Address,Emergency Contact,Notes\n")

        for (person in people) {
            val regDateStr = dateFormat.format(Date(person.registrationDate))
            val line = listOf(
                escapeCsv(person.uniqueId),
                escapeCsv(person.fullName),
                escapeCsv(person.email),
                escapeCsv(person.phone),
                escapeCsv(person.gender),
                escapeCsv(person.department),
                escapeCsv(person.role),
                escapeCsv(person.status),
                if (person.isVerified) "TRUE" else "FALSE",
                escapeCsv(person.dateOfBirth),
                escapeCsv(regDateStr),
                escapeCsv(person.address),
                escapeCsv(person.emergencyContact),
                escapeCsv(person.notes)
            ).joinToString(",")
            writer.write(line + "\n")
        }

        writer.flush()
        writer.close()
        return outputFile
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    fun shareCsv(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.name)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share CSV Export")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
