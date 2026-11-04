package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AddEditPersonDialog
import com.example.ui.screens.AuditLogsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.IdCardPreviewScreen
import com.example.ui.screens.IdVerificationScreen
import com.example.ui.screens.PeopleListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UserPortalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryVariant
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PeopleViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PeopleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkForced by viewModel.isDarkThemeForced.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = isDarkForced ?: systemDark

            MyApplicationTheme(darkTheme = darkTheme) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val screen: AppScreen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: PeopleViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    var showRegisterDialog by remember { mutableStateOf(false) }

    // Navigation Back Handling for custom state switching
    if (currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.USER_PORTAL) {
        BackHandler {
            viewModel.navigateTo(if (isAdminMode) AppScreen.DASHBOARD else AppScreen.USER_PORTAL)
        }
    }

    val adminNavItems = listOf(
        NavItem(AppScreen.DASHBOARD, "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        NavItem(AppScreen.PEOPLE_LIST, "People", Icons.Filled.Group, Icons.Outlined.Group),
        NavItem(AppScreen.ID_VERIFICATION, "Verify ID", Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner),
        NavItem(AppScreen.AUDIT_LOGS, "Audit Logs", Icons.Filled.History, Icons.Outlined.History),
        NavItem(AppScreen.SETTINGS, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    val userNavItems = listOf(
        NavItem(AppScreen.USER_PORTAL, "My ID", Icons.Filled.Badge, Icons.Outlined.Person),
        NavItem(AppScreen.ID_VERIFICATION, "Verify ID", Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner),
        NavItem(AppScreen.SETTINGS, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    val activeNavItems = if (isAdminMode) adminNavItems else userNavItems

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OrangePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "P",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PeopleHub",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = if (isAdminMode) "ADMIN CONTROL PANEL" else "USER PORTAL",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OrangePrimaryVariant,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // Portal Mode Switcher Pill (Admin Mode <-> User Portal)
                        PortalModeTogglePill(
                            isAdminMode = isAdminMode,
                            onToggle = { viewModel.toggleAdminMode(it) }
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        if (isAdminMode) {
                            IconButton(
                                onClick = { showRegisterDialog = true },
                                modifier = Modifier.testTag("topbar_add_person_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "Register Person",
                                    tint = OrangePrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        activeNavItems.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.screen) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = OrangePrimary,
                                    indicatorColor = OrangePrimary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_tab_${item.title.lowercase().replace(" ", "_")}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Wide Screen Navigation Rail (Tablet / Desktop / Landscape)
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        activeNavItems.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.screen) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(item.title) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = OrangePrimary,
                                    indicatorColor = OrangePrimary
                                )
                            )
                        }
                    }
                }

                // Main Content Screen Switching
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    when (currentScreen) {
                        AppScreen.DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            onOpenRegisterDialog = { showRegisterDialog = true }
                        )
                        AppScreen.PEOPLE_LIST -> PeopleListScreen(
                            viewModel = viewModel,
                            onOpenRegisterDialog = { showRegisterDialog = true }
                        )
                        AppScreen.ID_CARD_PREVIEW -> IdCardPreviewScreen(
                            viewModel = viewModel,
                            onBack = {
                                viewModel.navigateTo(if (isAdminMode) AppScreen.PEOPLE_LIST else AppScreen.USER_PORTAL)
                            }
                        )
                        AppScreen.ID_VERIFICATION -> IdVerificationScreen(
                            viewModel = viewModel
                        )
                        AppScreen.AUDIT_LOGS -> AuditLogsScreen(
                            viewModel = viewModel
                        )
                        AppScreen.USER_PORTAL -> UserPortalScreen(
                            viewModel = viewModel
                        )
                        AppScreen.SETTINGS -> SettingsScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    // Add / Register Person Dialog
    if (showRegisterDialog) {
        AddEditPersonDialog(
            initialPerson = null,
            onDismiss = { showRegisterDialog = false },
            onSave = { fullName, email, phone, gender, department, role, status, isVerified, dateOfBirth, address, emergencyContact, notes ->
                viewModel.registerNewPerson(
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    gender = gender,
                    department = department,
                    role = role,
                    status = status,
                    isVerified = isVerified,
                    dateOfBirth = dateOfBirth,
                    address = address,
                    emergencyContact = emergencyContact,
                    notes = notes
                )
            }
        )
    }
}

@Composable
fun PortalModeTogglePill(
    isAdminMode: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Admin Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isAdminMode) OrangePrimary else Color.Transparent)
                    .clickable { onToggle(true) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("mode_admin_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = if (isAdminMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Admin",
                        color = if (isAdminMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // User Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (!isAdminMode) OrangePrimary else Color.Transparent)
                    .clickable { onToggle(false) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("mode_user_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = if (!isAdminMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "User",
                        color = if (!isAdminMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
