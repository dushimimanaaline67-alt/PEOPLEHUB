package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Person
import com.example.ui.components.DigitalIdCardView
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryVariant
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PeopleViewModel

@Composable
fun UserPortalScreen(
    viewModel: PeopleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentPerson by viewModel.userPortalPerson.collectAsState()
    val allPeople by viewModel.allPeople.collectAsState()

    var showAccountSwitcher by remember { mutableStateOf(false) }

    if (currentPerson == null) {
        Column(
            modifier = modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(54.dp))
            Spacer(modifier = Modifier.height(14.dp))
            Text("No user accounts found in registry", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.toggleAdminMode(true) },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Go to Admin Directory")
            }
        }
        return
    }

    val person = currentPerson!!

    var editPhone by remember(person.phone) { mutableStateOf(person.phone) }
    var editAddress by remember(person.address) { mutableStateOf(person.address) }
    var editEmergency by remember(person.emergencyContact) { mutableStateOf(person.emergencyContact) }
    var editNotes by remember(person.notes) { mutableStateOf(person.notes) }
    var isEditingDetails by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("user_portal_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. User Header & Switch Account Banner
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
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(person.avatarColorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = person.fullName.take(1).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = person.fullName,
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${person.uniqueId} • ${person.role}",
                                        color = OrangePrimaryVariant,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = { showAccountSwitcher = !showAccountSwitcher },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.SwitchAccount, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Switch User", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        // Account Switcher Pill Carousel
                        if (showAccountSwitcher) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Select Account To Preview Portal:",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allPeople.forEach { u ->
                                    val isSelected = u.id == person.id
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) OrangePrimary else Color(0xFF27272A))
                                            .clickable {
                                                viewModel.selectPersonForUserPortal(u)
                                                showAccountSwitcher = false
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = u.fullName,
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Official Digital ID Card with 3D Flip
        item {
            Text(
                text = "YOUR OFFICIAL DIGITAL ID BADGE",
                style = MaterialTheme.typography.labelSmall,
                color = OrangePrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            DigitalIdCardView(
                person = person,
                onExportPdf = { viewModel.exportIdCardToPdf(context, person) },
                onVerifyClick = {
                    viewModel.performIdVerification(person.uniqueId)
                    viewModel.navigateTo(AppScreen.ID_VERIFICATION)
                }
            )
        }

        // 3. User Permitted Information Editor
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Personal Profile & Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Keep contact & emergency information up to date",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { isEditingDetails = !isEditingDetails }) {
                            Icon(
                                imageVector = if (isEditingDetails) Icons.Default.Lock else Icons.Default.Edit,
                                contentDescription = "Edit Details",
                                tint = OrangePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isEditingDetails) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                label = { Text("Contact Phone") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = OrangePrimary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = editAddress,
                                onValueChange = { editAddress = it },
                                label = { Text("Mailing / Residential Address") },
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = OrangePrimary) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = editEmergency,
                                onValueChange = { editEmergency = it },
                                label = { Text("Emergency Contact (Name & Phone)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = editNotes,
                                onValueChange = { editNotes = it },
                                label = { Text("Personal Notes") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )

                            Button(
                                onClick = {
                                    val updated = person.copy(
                                        phone = editPhone,
                                        address = editAddress,
                                        emergencyContact = editEmergency,
                                        notes = editNotes
                                    )
                                    viewModel.updatePerson(updated)
                                    isEditingDetails = false
                                    Toast.makeText(context, "Profile details updated", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            UserDetailItem("Email Address", person.email)
                            UserDetailItem("Phone", person.phone.ifBlank { "Not specified" })
                            UserDetailItem("Department", person.department)
                            UserDetailItem("Role", person.role)
                            UserDetailItem("Account Status", person.status)
                            UserDetailItem("Verification", if (person.isVerified) "Verified Badge Issued" else "Pending Review")
                            UserDetailItem("Date of Birth", person.dateOfBirth)
                            UserDetailItem("Address", person.address.ifBlank { "Not specified" })
                            UserDetailItem("Emergency Contact", person.emergencyContact.ifBlank { "Not specified" })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
