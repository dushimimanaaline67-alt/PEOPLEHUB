package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Person
import com.example.ui.theme.OrangeDarkContainer
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryVariant
import com.example.ui.theme.VerifiedGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DigitalIdCardView(
    person: Person,
    onExportPdf: () -> Unit,
    onVerifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "cardFlip"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ID Card Surface with 3D Flip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable { isFlipped = !isFlipped }
        ) {
            if (rotation <= 90f) {
                // FRONT OF CARD
                IdCardFront(person = person)
            } else {
                // BACK OF CARD (Counter-rotated so text reads normally)
                Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                    IdCardBack(person = person)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Flip indicator hint
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { isFlipped = !isFlipped }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FlipCameraAndroid,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isFlipped) "Tap to view Front" else "Tap to flip & view Security Details",
                style = MaterialTheme.typography.bodySmall,
                color = OrangePrimaryVariant,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onExportPdf,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Download PDF",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            OutlinedButton(
                onClick = onVerifyClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verify Status",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun IdCardFront(person: Person) {
    val context = LocalContext.current
    val issueDateStr = remember(person.registrationDate) {
        val df = SimpleDateFormat("MMM yyyy", Locale.US)
        df.format(Date(person.registrationDate))
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(OrangePrimary, Color(0xFF3F3F46), OrangePrimaryVariant)
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        color = Color(0xFF141418),
        tonalElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle security watermark pattern
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 30.dp.toPx()
                for (i in -10..20) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.02f),
                        start = Offset(i * step, 0f),
                        end = Offset(i * step + size.height, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OrangePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "P",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "PEOPLEHUB",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "OFFICIAL IDENTITY",
                                color = OrangePrimaryVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // NFC & Chip symbol
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Nfc,
                            contentDescription = "NFC Enabled",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(26.dp, 20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFD4AF37)) // Gold chip
                                .border(1.dp, Color(0xFFB8860B), RoundedCornerShape(4.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Middle Content: Avatar + Details + QR Code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Initials
                    val initials = person.fullName.split(" ")
                        .filter { it.isNotEmpty() }
                        .take(2)
                        .map { it.first() }
                        .joinToString("")
                        .uppercase()

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, OrangePrimary, CircleShape)
                            .background(Color(person.avatarColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Person Text Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = person.fullName,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${person.role} • ${person.department}",
                            color = OrangePrimaryVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Unique ID Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF27272A))
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("ID", person.uniqueId))
                                    Toast.makeText(context, "ID copied to clipboard", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = person.uniqueId,
                                color = Color(0xFFFFDCC2),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy ID",
                                tint = OrangePrimary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    // QR Code View
                    QrCodeView(
                        content = "https://peoplehub.internal/verify/${person.uniqueId}",
                        size = 68.dp,
                        backgroundColor = Color.White,
                        qrColor = Color.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Footer: Verification status & Expiry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (person.isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (person.isVerified) VerifiedGreen else Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (person.isVerified) "VERIFIED (${person.status.uppercase()})" else "PENDING VERIFICATION",
                            color = if (person.isVerified) VerifiedGreen else Color(0xFFF59E0B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "ISSUED: $issueDateStr",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "EXP: DEC 2030",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IdCardBack(person: Person) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, Color(0xFF3F3F46), RoundedCornerShape(20.dp)),
        color = Color(0xFF141418),
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Magnetic Stripe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(Color(0xFF09090B))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Contact & Emergency Info
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "EMERGENCY CONTACT & ADDRESS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimaryVariant
                    )
                    Text(
                        text = person.emergencyContact,
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = person.address,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "Email: ${person.email}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Security disclaimer
                Text(
                    text = "This credential remains property of PeopleHub. Loss or theft must be reported immediately. Unauthorized duplication is strictly prohibited.",
                    fontSize = 8.5.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 11.sp
                )

                // Simulated Barcode & Security stamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(160.dp, 28.dp)) {
                        val barCount = 42
                        val barStep = size.width / barCount
                        for (i in 0 until barCount) {
                            val isThick = (i % 3 == 0 || i % 7 == 0)
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(i * barStep, 0f),
                                size = Size(if (isThick) 3.dp.toPx() else 1.5.dp.toPx(), size.height)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "SEC-HASH",
                            fontSize = 8.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "AUTH-${person.id * 7431}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }
                }
            }
        }
    }
}
