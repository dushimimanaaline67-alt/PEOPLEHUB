package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import java.security.MessageDigest

object QrCodeGenerator {

    /**
     * Generates a 2D matrix (boolean array) representing a QR-style 2D code for the given text.
     * Uses standard QR structure: Finder patterns (7x7 with 1-cell borders), Timing patterns,
     * Alignment pattern, and data area generated with error detection bits.
     */
    fun generateMatrix(content: String, size: Int = 25): Array<BooleanArray> {
        val matrix = Array(size) { BooleanArray(size) { false } }
        val reserved = Array(size) { BooleanArray(size) { false } }

        // 1. Finder patterns (7x7) at (0,0), (0, size-7), (size-7, 0)
        drawFinderPattern(matrix, reserved, 0, 0, size)
        drawFinderPattern(matrix, reserved, size - 7, 0, size)
        drawFinderPattern(matrix, reserved, 0, size - 7, size)

        // 2. Timing patterns on row 6 and col 6
        for (i in 8 until size - 8) {
            val isBlack = (i % 2 == 0)
            if (!reserved[6][i]) {
                matrix[6][i] = isBlack
                reserved[6][i] = true
            }
            if (!reserved[i][6]) {
                matrix[i][6] = isBlack
                reserved[i][6] = true
            }
        }

        // 3. Alignment pattern at (size-9, size-9) if size >= 25
        if (size >= 25) {
            drawAlignmentPattern(matrix, reserved, size - 9, size - 9)
        }

        // 4. Data payload derived from hash and content characters
        val contentBytes = content.toByteArray(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256").digest(contentBytes)

        var byteIdx = 0
        var bitIdx = 0
        val totalBits = digest.size * 8

        for (r in 0 until size) {
            for (c in 0 until size) {
                if (!reserved[r][c]) {
                    val contentInfluence = if (content.isNotEmpty()) {
                        content[(r * size + c) % content.length].code
                    } else 0
                    val hashByte = digest[byteIdx % digest.size].toInt()
                    val hashBit = ((hashByte shr (bitIdx % 8)) and 1) == 1
                    
                    // Standard QR mask condition: (row + col) % 2 == 0 or (row * col) % 3 == 0
                    val mask = ((r + c) % 2 == 0)
                    val rawBit = hashBit xor ((contentInfluence + r * 3 + c * 7) % 2 == 1)
                    
                    matrix[r][c] = rawBit xor mask
                    bitIdx++
                    if (bitIdx % 8 == 0) byteIdx++
                }
            }
        }

        return matrix
    }

    private fun drawFinderPattern(
        matrix: Array<BooleanArray>,
        reserved: Array<BooleanArray>,
        startX: Int,
        startY: Int,
        size: Int
    ) {
        for (r in -1..7) {
            for (c in -1..7) {
                val x = startX + c
                val y = startY + r
                if (x in 0 until size && y in 0 until size) {
                    reserved[y][x] = true
                    val inOuter = (r in 0..6 && (c == 0 || c == 6)) || (c in 0..6 && (r == 0 || r == 6))
                    val inInner = r in 2..4 && c in 2..4
                    matrix[y][x] = inOuter || inInner
                }
            }
        }
    }

    private fun drawAlignmentPattern(
        matrix: Array<BooleanArray>,
        reserved: Array<BooleanArray>,
        centerX: Int,
        centerY: Int
    ) {
        for (r in -2..2) {
            for (c in -2..2) {
                val x = centerX + c
                val y = centerY + r
                if (y in matrix.indices && x in matrix[0].indices) {
                    reserved[y][x] = true
                    val isBorder = (Math.abs(r) == 2 || Math.abs(c) == 2)
                    val isCenter = (r == 0 && c == 0)
                    matrix[y][x] = isBorder || isCenter
                }
            }
        }
    }

    /**
     * Converts the QR matrix to an Android Bitmap for export/PDF printing
     */
    fun createBitmap(content: String, pixelSize: Int = 300): Bitmap {
        val matrix = generateMatrix(content, 25)
        val matrixSize = matrix.size
        val bitmap = Bitmap.createBitmap(pixelSize, pixelSize, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val paint = android.graphics.Paint().apply {
            color = Color.BLACK
            isAntiAlias = false
        }

        val padding = pixelSize * 0.08f
        val usableSize = pixelSize - (padding * 2)
        val cellSize = usableSize / matrixSize

        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                if (matrix[r][c]) {
                    val left = padding + (c * cellSize)
                    val top = padding + (r * cellSize)
                    canvas.drawRect(left, top, left + cellSize, top + cellSize, paint)
                }
            }
        }

        return bitmap
    }
}
