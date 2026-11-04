package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AuditLog
import com.example.ui.components.ChartSlice
import com.example.ui.components.DonutPieChartCard
import com.example.ui.components.InteractiveBarChartCard
import com.example.ui.components.StatCard
import com.example.ui.components.SummaryMetricsBanner
import com.example.ui.components.TrendLineChartCard
import com.example.ui.theme.InactiveRed
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryVariant
import com.example.ui.theme.PendingYellow
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PeopleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: PeopleViewModel,
    onOpenRegisterDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stats by viewModel.dashboardStats.collectAsState()
    val auditLogs by viewModel.allAuditLogs.collectAsState()

    // Department Slices for Donut Chart
    val deptSlices = remember(stats.departmentBreakdown) {
        val colorPalette = listOf(
            OrangePrimary,
            Color(0xFF3B82F6),
            VerifiedGreen,
            Color(0xFF8B5CF6),
            Color(0xFFEC4899),
            Color(0xFF06B6D4),
            Color(0xFFF59E0B)
        )
        stats.departmentBreakdown.entries.mapIndexed { idx, (dept, count) ->
            ChartSlice(
                label = dept,
                value = count.toFloat(),
                color = colorPalette[idx % colorPalette.size]
            )
        }
    }

    // Gender Slices for Donut Chart
    val genderSlices = remember(stats) {
        listOf(
            ChartSlice("Female", stats.femaleCount.toFloat(), Color(0xFFEC4899)),
            ChartSlice("Male", stats.maleCount.toFloat(), Color(0xFF3B82F6)),
            ChartSlice("Other", stats.otherGenderCount.toFloat(), Color(0xFF14B8A6))
        ).filter { it.value > 0 }
    }

    // Verification Slices
    val verificationSlices = remember(stats) {
        listOf(
            ChartSlice("Verified", stats.verifiedCount.toFloat(), VerifiedGreen),
            ChartSlice("Unverified", stats.unverifiedCount.toFloat(), PendingYellow)
        ).filter { it.value > 0 }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Summary Banner
        item {
            SummaryMetricsBanner(stats = stats)
        }

        // 2. Quick Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenRegisterDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_register_button")
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Register Person", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.ID_VERIFICATION) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Verify ID", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }

        // 3. Grid of Key Metric Cards (2x2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Active Directory",
                    value = "${stats.activeCount}",
                    subtitle = "${stats.inactiveCount} inactive / suspended",
                    icon = Icons.Default.CheckCircle,
                    iconBgColor = VerifiedGreen.copy(alpha = 0.15f),
                    iconTint = VerifiedGreen,
                    badgeText = "${if (stats.totalPeople > 0) (stats.activeCount * 100 / stats.totalPeople) else 0}%",
                    badgeColor = VerifiedGreen,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Growth Velocity",
                    value = "+${String.format(Locale.US, "%.1f", stats.growthRatePercent)}%",
                    subtitle = "+${stats.newRegistrationsThisMonth} this month",
                    icon = Icons.Default.TrendingUp,
                    iconBgColor = OrangePrimary.copy(alpha = 0.15f),
                    iconTint = OrangePrimary,
                    badgeText = "+${stats.newRegistrationsThisWeek} wk",
                    badgeColor = OrangePrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Verified Accounts",
                    value = "${stats.verifiedCount}",
                    subtitle = "${stats.unverifiedCount} pending verification",
                    icon = Icons.Default.VerifiedUser,
                    iconBgColor = Color(0xFF3B82F6).copy(alpha = 0.15f),
                    iconTint = Color(0xFF3B82F6),
                    badgeText = "OFFICIAL",
                    badgeColor = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Gender Balance",
                    value = "${stats.femaleCount}F / ${stats.maleCount}M",
                    subtitle = "${stats.otherGenderCount} non-binary/other",
                    icon = Icons.Default.Group,
                    iconBgColor = Color(0xFFEC4899).copy(alpha = 0.15f),
                    iconTint = Color(0xFFEC4899),
                    badgeText = "DIVERSE",
                    badgeColor = Color(0xFFEC4899),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Interactive Bar Chart: Monthly Registrations
        item {
            InteractiveBarChartCard(
                title = "Monthly Registration Velocity (2026)",
                data = stats.monthlyRegistrations
            )
        }

        // 5. Interactive Donut Chart: Department Distribution
        item {
            DonutPieChartCard(
                title = "Department & Category Breakdown",
                slices = deptSlices
            )
        }

        // 6. Interactive Donut Chart: Verification & Gender Ratio
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DonutPieChartCard(
                    title = "Account Verification",
                    slices = verificationSlices,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 7. Trend Line Chart: Registration Cumulative Momentum
        item {
            TrendLineChartCard(
                title = "Growth & Registration Performance",
                dataPoints = stats.monthlyRegistrations
            )
        }

        // 8. Admin Reports & Export Actions
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
                        text = "ADMIN EXPORTS & REPORTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Generate Compliance & Analytics Documents",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Export personnel records to PDF audit report or CSV spreadsheet for external processing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.exportRegistryToPdf(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E24)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, OrangePrimary, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export PDF Report", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.exportRegistryToCsv(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E24)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export CSV", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 9. Recent Activity & Audit Trail Preview
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live Activity & Audit Trail",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "View All",
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.AUDIT_LOGS) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val recentLogs = auditLogs.take(4)
                    if (recentLogs.isEmpty()) {
                        Text(
                            text = "No recorded activities yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        recentLogs.forEach { log ->
                            AuditLogItem(log = log)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditLogItem(log: AuditLog) {
    val timeStr = remember(log.timestamp) {
        val df = SimpleDateFormat("MMM dd, HH:mm", Locale.US)
        df.format(Date(log.timestamp))
    }

    val actionColor = when (log.action) {
        "REGISTER" -> OrangePrimary
        "VERIFY" -> VerifiedGreen
        "STATUS_CHANGE" -> PendingYellow
        "DELETE" -> InactiveRed
        "EXPORT" -> Color(0xFF3B82F6)
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(actionColor)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${log.action} • ${log.targetUniqueId}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = actionColor
                )
                Text(
                    text = timeStr,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${log.targetName}: ${log.details}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
