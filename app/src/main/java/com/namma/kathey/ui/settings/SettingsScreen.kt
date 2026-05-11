package com.namma.kathey.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val useKannada by vm.useKannada.collectAsState()
    val darkMode   by vm.darkMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (useKannada) "ಸೆಟ್ಟಿಂಗ್ಸ್" else "Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White
                )
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)) {

            SectionLabel(if (useKannada) "ಭಾಷೆ" else "Language")
            ToggleRow(
                icon = Icons.Default.Language,
                title = if (useKannada) "ಕನ್ನಡ ಮೋಡ್" else "Kannada Mode",
                subtitle = if (useKannada) "ಕನ್ನಡದಲ್ಲಿ ತೋರಿಸಲಾಗುತ್ತಿದೆ" else "Show content in Kannada",
                checked = useKannada, onCheck = vm::toggleKannada
            )

            Spacer(Modifier.height(16.dp))
            SectionLabel(if (useKannada) "ಗೋಚರ" else "Appearance")
            ToggleRow(
                icon = Icons.Default.DarkMode,
                title = if (useKannada) "ಡಾರ್ಕ್ ಮೋಡ್" else "Dark Mode",
                subtitle = if (useKannada) "ಡಾರ್ಕ್ ಥೀಮ್" else "Use dark theme",
                checked = darkMode, onCheck = vm::toggleDarkMode
            )

            Spacer(Modifier.height(24.dp))
            SectionLabel(if (useKannada) "ಅಪ್ ಬಗ್ಗೆ" else "About")
            Card(shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(16.dp)) {
                    InfoRow(Icons.Default.Apps, "App", "Namma Kathey")
                    InfoRow(Icons.Default.Info, "Version", "1.0.0")
                    InfoRow(Icons.Default.Language, "Languages", "English + ಕನ್ನಡ")
                    InfoRow(Icons.Default.SmartToy, "AI Model", "Google Gemini 1.5 Flash")
                    InfoRow(Icons.Default.Map, "Heroes", "10 Karnataka Heroes")
                    InfoRow(Icons.Default.EmojiEvents, "Badges", "Up to 10 Heritage Badges")
                }
            }

            Spacer(Modifier.height(16.dp))
            Card(shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFFFF6F00), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (useKannada)
                            "ಈ ಅಪ್ ಕರ್ನಾಟಕದ ಐತಿಹಾಸಿಕ ನಾಯಕರ ಬಗ್ಗೆ ಮಕ್ಕಳಿಗೆ ಶಿಕ್ಷಣ ನೀಡಲು ರಚಿಸಲಾಗಿದೆ."
                        else
                            "This app is designed to educate children about Karnataka's historical heroes through stories and quizzes.",
                        style = MaterialTheme.typography.bodySmall, color = Color(0xFF795548)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text.uppercase(), style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun ToggleRow(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheck: (Boolean) -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Switch(checked = checked, onCheckedChange = onCheck)
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        Spacer(Modifier.width(8.dp))
        Text("$label:", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.width(80.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}
