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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.components.QrCodeView
import com.example.ui.theme.InactiveRed
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryVariant
import com.example.ui.theme.PendingYellow
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.PeopleViewModel
import com.example.viewmodel.VerificationStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IdVerificationScreen(
    viewModel: PeopleViewModel,
    modifier: Modifier = Modifier
) {
    val verificationResult by viewModel.verificationResult.collectAsState()
    val allPeople by viewModel.allPeople.collectAsState()
    var inputQuery by remember { mutableStateOf(viewModel.verifySearchInput.value) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("id_verification_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                color = Color(0xFF141418)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OrangePrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "IDENTITY VERIFICATION PORTAL",
                                color = OrangePrimaryVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Validate Official Digital Badges",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Scan QR codes or enter the unique identification number (e.g., PCD-2026-000001) to verify real-time authenticity.",
                            color = Color(0xFF94A3B8),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // 2. Input Box & Quick Verification Lookup
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Enter Identification Number or Email",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verification_input"),
                        placeholder = { Text("e.g. PCD-2026-000001 or sarah.chen@...") },
                        leadingIcon = {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = OrangePrimary)
                        },
                        trailingIcon = {
                            if (inputQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    inputQuery = ""
                                    viewModel.performIdVerification("")
                                }) {
                                    Icon(Icons.Default.Cancel, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.performIdVerification(inputQuery) },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verify_now_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verify ID Authenticity", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick-test suggestions from database
                    Text(
                        text = "Quick Test Sample Credentials:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allPeople.take(5).forEach { person ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        inputQuery = person.uniqueId
                                        viewModel.performIdVerification(person.uniqueId)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${person.uniqueId} (${person.fullName.split(" ").first()})",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )
                            }
                        }

                        // Add an invalid test chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    inputQuery = "PCD-2026-999999"
                                    viewModel.performIdVerification("PCD-2026-999999")
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "PCD-999999 (Invalid Test)",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = InactiveRed
                            )
                        }
                    }
                }
            }
        }

        // 3. Verification Result Display
        item {
            when (verificationResult.status) {
                VerificationStatus.IDLE -> {
                    // Friendly prompt
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Awaiting Verification Query",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Select a sample ID above or type an ID number to check official directory standing.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                VerificationStatus.VERIFIED_ACTIVE -> {
                    val person = verificationResult.person!!
                    VerificationResultCard(
                        person = person,
                        title = "VALID & OFFICIALLY VERIFIED",
                        subtitle = "This credential is authentic, issued by PeopleHub, and actively in good standing.",
                        bannerColor = VerifiedGreen,
                        icon = Icons.Default.CheckCircle,
                        onViewIdCard = { viewModel.selectPersonForPreview(person) }
                    )
                }

                VerificationStatus.VERIFIED_INACTIVE -> {
                    val person = verificationResult.person!!
                    VerificationResultCard(
                        person = person,
                        title = "ACCOUNT INACTIVE",
                        subtitle = "Identity confirmed in directory, but the user account status is currently marked as INACTIVE.",
                        bannerColor = PendingYellow,
                        icon = Icons.Default.Warning,
                        onViewIdCard = { viewModel.selectPersonForPreview(person) }
                    )
                }

                VerificationStatus.SUSPENDED -> {
                    val person = verificationResult.person!!
                    VerificationResultCard(
                        person = person,
                        title = "ACCESS SUSPENDED",
                        subtitle = "Identity matched in registry, but credentials have been temporarily LOCKED or SUSPENDED.",
                        bannerColor = InactiveRed,
                        icon = Icons.Default.Warning,
                        onViewIdCard = { viewModel.selectPersonForPreview(person) }
                    )
                }

                VerificationStatus.NOT_FOUND -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, InactiveRed, RoundedCornerShape(16.dp)),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = InactiveRed,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "INVALID IDENTIFICATION",
                                        color = InactiveRed,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "ID: ${verificationResult.searchedId}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "The requested identification number was NOT FOUND in the PeopleHub registry. Please confirm the number or re-scan.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VerificationResultCard(
    person: Person,
    title: String,
    subtitle: String,
    bannerColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onViewIdCard: () -> Unit
) {
    val dateStr = remember(person.registrationDate) {
        val df = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        df.format(Date(person.registrationDate))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, bannerColor, RoundedCornerShape(18.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Status Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = bannerColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        color = bannerColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Person Card Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(person.avatarColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = person.fullName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = person.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = person.uniqueId,
                        color = OrangePrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${person.role} • ${person.department}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                QrCodeView(
                    content = "https://peoplehub.internal/verify/${person.uniqueId}",
                    size = 56.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Credentials Detail Table
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VerifyRow("Issuance Date", dateStr)
                VerifyRow("Status", "${person.status.uppercase()} (${if (person.isVerified) "VERIFIED" else "PENDING"})")
                VerifyRow("Department", person.department)
                VerifyRow("Email", person.email)
                VerifyRow("Phone", person.phone.ifBlank { "N/A" })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onViewIdCard,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Complete Digital ID Card", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun VerifyRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
