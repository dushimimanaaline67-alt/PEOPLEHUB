package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.DashboardStats
import com.example.model.Person
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    /**
     * Generates a printable high-resolution PDF ID Card
     */
    fun generateIdCardPdf(context: Context, person: Person): File {
        val document = PdfDocument()
        val cardWidth = 500
        val cardHeight = 315

        val pageInfo = PdfDocument.PageInfo.Builder(cardWidth, cardHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        // Background
        val bgPaint = Paint().apply {
            color = Color.parseColor("#121215")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, cardWidth.toFloat(), cardHeight.toFloat(), bgPaint)

        // Orange accent top banner
        val orangePaint = Paint().apply {
            color = Color.parseColor("#FF6D00")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, cardWidth.toFloat(), 48f, orangePaint)

        // Top banner title
        val topTitlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("PEOPLEHUB // OFFICIAL ID BADGE", 24f, 30f, topTitlePaint)

        val yearTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 12f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("VALID 2026 - 2030", cardWidth - 140f, 30f, yearTextPaint)

        // Accent line below banner
        val stripePaint = Paint().apply {
            color = Color.parseColor("#FF9100")
            strokeWidth = 3f
        }
        canvas.drawLine(0f, 48f, cardWidth.toFloat(), 48f, stripePaint)

        // Left Avatar Circle
        val avatarRadius = 45f
        val avatarCenterX = 70f
        val avatarCenterY = 120f

        val avatarBorderPaint = Paint().apply {
            color = Color.parseColor("#FF6D00")
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawCircle(avatarCenterX, avatarCenterY, avatarRadius + 4, avatarBorderPaint)

        val avatarBgPaint = Paint().apply {
            color = person.avatarColorHex.toInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(avatarCenterX, avatarCenterY, avatarRadius, avatarBgPaint)

        // Initials in avatar
        val initials = person.fullName.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it.first() }
            .joinToString("")
            .uppercase()

        val initialsPaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(initials, avatarCenterX, avatarCenterY + 10f, initialsPaint)

        // Holder Details (Center-Left)
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 19f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(person.fullName, 135f, 95f, namePaint)

        val rolePaint = Paint().apply {
            color = Color.parseColor("#FF9100")
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("${person.role.uppercase()} • ${person.department.uppercase()}", 135f, 116f, rolePaint)

        val idBadgePaint = Paint().apply {
            color = Color.parseColor("#26262E")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val idRect = RectF(135f, 126f, 320f, 150f)
        canvas.drawRoundRect(idRect, 6f, 6f, idBadgePaint)

        val idTextPaint = Paint().apply {
            color = Color.parseColor("#FFDCC2")
            textSize = 13f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(person.uniqueId, 145f, 143f, idTextPaint)

        // Extra info
        val labelPaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        val valuePaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        canvas.drawText("STATUS", 30f, 195f, labelPaint)
        val statusColor = if (person.isVerified) Color.parseColor("#10B981") else Color.parseColor("#F59E0B")
        val statusPaint = Paint().apply {
            color = statusColor
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val statusText = if (person.isVerified) "VERIFIED (${person.status.uppercase()})" else "UNVERIFIED"
        canvas.drawText(statusText, 30f, 212f, statusPaint)

        canvas.drawText("ISSUED DATE", 170f, 195f, labelPaint)
        canvas.drawText(dateFormat.format(Date(person.registrationDate)), 170f, 212f, valuePaint)

        canvas.drawText("CONTACT / EMAIL", 30f, 235f, labelPaint)
        val emailSnippet = if (person.email.length > 28) person.email.take(26) + "..." else person.email
        canvas.drawText(emailSnippet, 30f, 252f, valuePaint)

        // Right side: QR Code
        val qrBitmap = QrCodeGenerator.createBitmap("https://peoplehub.internal/verify/${person.uniqueId}", 140)
        canvas.drawBitmap(qrBitmap, 335f, 75f, null)

        val scanHintPaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 9f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText("SCAN TO VERIFY", 405f, 230f, scanHintPaint)

        // Bottom Security Stripe
        val bottomStripePaint = Paint().apply {
            color = Color.parseColor("#1E1E24")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 275f, cardWidth.toFloat(), cardHeight.toFloat(), bottomStripePaint)

        val securityTextPaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText("SECURITY CODE: PCD-SEC-${person.id * 8912} // OFFICIAL ISSUANCE", 20f, 296f, securityTextPaint)

        document.finishPage(page)

        // Save to file
        val outputDir = File(context.cacheDir, "id_cards").apply { mkdirs() }
        val outputFile = File(outputDir, "ID_Card_${person.uniqueId}.pdf")
        val outStream = FileOutputStream(outputFile)
        document.writeTo(outStream)
        outStream.close()
        document.close()

        return outputFile
    }

    /**
     * Generates a full Personnel Audit & Registry Report in PDF
     */
    fun generateRegistryReportPdf(context: Context, people: List<Person>, stats: DashboardStats): File {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val bgPaint = Paint().apply { color = Color.WHITE; style = Paint.Style.FILL }
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

        // Header Banner
        val headerPaint = Paint().apply { color = Color.parseColor("#0F172A"); style = Paint.Style.FILL }
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 80f, headerPaint)

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("PEOPLE MANAGEMENT REGISTRY & AUDIT REPORT", 30f, 42f, titlePaint)

        val subTitlePaint = Paint().apply {
            color = Color.parseColor("#FF6D00")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("GENERATED ON ${dateFormat.format(Date())} • TOTAL REGISTERED: ${people.size}", 30f, 62f, subTitlePaint)

        // Summary Metric Boxes
        var metricX = 30f
        val boxWidth = 120f
        val boxHeight = 55f
        val metrics = listOf(
            Pair("Total People", "${stats.totalPeople}"),
            Pair("Active Users", "${stats.activeCount}"),
            Pair("Verified", "${stats.verifiedCount}"),
            Pair("Growth Rate", "+${String.format(Locale.US, "%.1f", stats.growthRatePercent)}%")
        )

        val boxPaint = Paint().apply { color = Color.parseColor("#F8F9FA"); style = Paint.Style.FILL }
        val boxBorder = Paint().apply { color = Color.parseColor("#E2E8F0"); style = Paint.Style.STROKE; strokeWidth = 1f }
        val mLabelPaint = Paint().apply { color = Color.parseColor("#64748B"); textSize = 9f; isAntiAlias = true }
        val mValuePaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 17f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        metrics.forEach { (label, value) ->
            val rect = RectF(metricX, 95f, metricX + boxWidth, 95f + boxHeight)
            canvas.drawRoundRect(rect, 8f, 8f, boxPaint)
            canvas.drawRoundRect(rect, 8f, 8f, boxBorder)
            canvas.drawText(label, metricX + 12f, 114f, mLabelPaint)
            canvas.drawText(value, metricX + 12f, 138f, mValuePaint)
            metricX += boxWidth + 15f
        }

        // Table Header
        val tableTop = 175f
        val thBgPaint = Paint().apply { color = Color.parseColor("#E2E8F0"); style = Paint.Style.FILL }
        canvas.drawRect(30f, tableTop, pageWidth - 30f, tableTop + 24f, thBgPaint)

        val thTextPaint = Paint().apply {
            color = Color.parseColor("#334155")
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        canvas.drawText("UNIQUE ID", 40f, tableTop + 16f, thTextPaint)
        canvas.drawText("NAME", 150f, tableTop + 16f, thTextPaint)
        canvas.drawText("DEPARTMENT", 270f, tableTop + 16f, thTextPaint)
        canvas.drawText("ROLE", 370f, tableTop + 16f, thTextPaint)
        canvas.drawText("STATUS", 440f, tableTop + 16f, thTextPaint)
        canvas.drawText("VERIFIED", 505f, tableTop + 16f, thTextPaint)

        // Table Rows
        var currentY = tableTop + 42f
        val rowTextPaint = Paint().apply { color = Color.parseColor("#1E293B"); textSize = 9.5f; isAntiAlias = true }
        val rowIdPaint = Paint().apply {
            color = Color.parseColor("#FF6D00")
            textSize = 9f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }
        val linePaint = Paint().apply { color = Color.parseColor("#F1F5F9"); strokeWidth = 1f }

        people.take(24).forEach { person ->
            canvas.drawText(person.uniqueId, 40f, currentY, rowIdPaint)
            val nameTrunc = if (person.fullName.length > 20) person.fullName.take(18) + ".." else person.fullName
            canvas.drawText(nameTrunc, 150f, currentY, rowTextPaint)
            canvas.drawText(person.department, 270f, currentY, rowTextPaint)
            canvas.drawText(person.role, 370f, currentY, rowTextPaint)
            canvas.drawText(person.status, 440f, currentY, rowTextPaint)
            val verText = if (person.isVerified) "YES" else "NO"
            canvas.drawText(verText, 515f, currentY, rowTextPaint)

            canvas.drawLine(30f, currentY + 6f, pageWidth - 30f, currentY + 6f, linePaint)
            currentY += 24f
        }

        // Footer
        val footerPaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 9f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("PeopleHub Management System • Page 1 of 1 • Confidential", pageWidth / 2f, pageHeight - 25f, footerPaint)

        document.finishPage(page)

        val outputDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val outputFile = File(outputDir, "PeopleHub_Registry_Report_${System.currentTimeMillis()}.pdf")
        val outStream = FileOutputStream(outputFile)
        document.writeTo(outStream)
        outStream.close()
        document.close()

        return outputFile
    }

    /**
     * Triggers the Android system share sheet for the generated PDF
     */
    fun sharePdf(context: Context, file: File, chooserTitle: String = "Share PDF") {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.name)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, chooserTitle)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
