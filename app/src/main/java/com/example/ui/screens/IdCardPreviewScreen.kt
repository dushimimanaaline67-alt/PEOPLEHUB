package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DigitalIdCardView
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PeopleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdCardPreviewScreen(
    viewModel: PeopleViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedPerson by viewModel.selectedPerson.collectAsState()

    if (selectedPerson == null) {
        Column(
            modifier = modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Badge, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(54.dp))
            Spacer(modifier = Modifier.height(14.dp))
            Text("No person selected", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) {
                Text("Return to Directory")
            }
        }
        return
    }

    val person = selectedPerson!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Digital ID Card",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${person.fullName} • ${person.uniqueId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OrangePrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 3D Flippable Digital ID Card
            item {
                DigitalIdCardView(
                    person = person,
                    onExportPdf = { viewModel.exportIdCardToPdf(context, person) },
                    onVerifyClick = {
                        viewModel.performIdVerification(person.uniqueId)
                        viewModel.navigateTo(AppScreen.ID_VERIFICATION)
                    }
                )
            }

            // Information & Verification Badge Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = OrangePrimary)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Credential Verification Specifications",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "This digital identification credential contains an encoded cryptographic QR matrix linked to the PeopleHub verification node. It is compliant with ISO/IEC 7810 ID-1 form factors.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        DetailCardRow("Cardholder Name", person.fullName)
                        DetailCardRow("Identification Code", person.uniqueId)
                        DetailCardRow("Department", person.department)
                        DetailCardRow("Role Level", person.role)
                        DetailCardRow("Account Status", person.status)
                        DetailCardRow("Official Badge", if (person.isVerified) "Verified & Active" else "Pending Review")
                        DetailCardRow("Email Address", person.email)
                        DetailCardRow("Contact Phone", person.phone.ifBlank { "N/A" })
                        DetailCardRow("Emergency Contact", person.emergencyContact.ifBlank { "N/A" })
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailCardRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
