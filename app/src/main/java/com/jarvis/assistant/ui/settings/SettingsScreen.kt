package com.jarvis.assistant.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jarvis.assistant.data.preferences.JarvisPreferences
import com.jarvis.assistant.ui.components.SectionHeader
import com.jarvis.assistant.ui.components.SliderRow
import com.jarvis.assistant.ui.components.ToggleRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferences: JarvisPreferences,
    onBack: () -> Unit,
    onDeveloperClick: () -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val isDarkTheme by preferences.isDarkTheme.collectAsState(initial = true)
    val isVoiceEnabled by preferences.isVoiceEnabled.collectAsState(initial = true)
    val temperature by preferences.temperature.collectAsState(initial = 0.7f)
    val maxTokens by preferences.maxTokens.collectAsState(initial = 512)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader(title = "General")

            SettingsNavigationRow(
                icon = Icons.Default.DarkMode,
                title = "Theme",
                subtitle = if (isDarkTheme) "Dark" else "Light",
                onClick = onThemeClick
            )

            SettingsNavigationRow(
                icon = Icons.Default.Language,
                title = "Language",
                subtitle = "English",
                onClick = onLanguageClick
            )

            SectionHeader(title = "Voice")

            ToggleRow(
                icon = Icons.Default.Mic,
                title = "Voice Responses",
                subtitle = "Jarvis speaks responses aloud",
                checked = isVoiceEnabled,
                onCheckedChange = { scope.launch { preferences.setVoiceEnabled(it) } }
            )

            SliderRow(
                label = "Speech Speed",
                value = 0.5f,
                onValueChange = { },
                valueRange = 0.1f..2.0f
            )

            SliderRow(
                label = "Pitch",
                value = 1.0f,
                onValueChange = { },
                valueRange = 0.5f..2.0f
            )

            SettingsNavigationRow(
                icon = Icons.Default.Tune,
                title = "Voice Settings",
                subtitle = "TTS, STT, wake word",
                onClick = onVoiceClick
            )

            SectionHeader(title = "Model")

            SliderRow(
                label = "Temperature",
                value = temperature,
                onValueChange = { scope.launch { preferences.setTemperature(it) } },
                valueRange = 0.1f..2.0f,
                steps = 18
            )

            SliderRow(
                label = "Max Tokens",
                value = maxTokens.toFloat(),
                onValueChange = { scope.launch { preferences.setMaxTokens(it.toInt()) } },
                valueRange = 64f..2048f,
                steps = 31,
                valueFormatter = { "${it.toInt()}" }
            )

            SettingsNavigationRow(
                icon = Icons.Default.Speed,
                title = "Model Management",
                subtitle = "Download, benchmark, manage models",
                onClick = onDeveloperClick
            )

            SectionHeader(title = "Memory")

            SettingsNavigationRow(
                icon = Icons.Default.Delete,
                title = "Clear Memory",
                subtitle = "Delete all stored memories",
                onClick = { }
            )

            SettingsNavigationRow(
                icon = Icons.Default.Memory,
                title = "Export Data",
                subtitle = "Export conversations and memories",
                onClick = { }
            )

            SectionHeader(title = "Tools")

            ToggleRow(
                icon = Icons.Default.Code,
                title = "Calculator",
                subtitle = "Enable calculator tool",
                checked = true,
                onCheckedChange = { }
            )

            ToggleRow(
                icon = Icons.Default.Code,
                title = "Timer",
                subtitle = "Enable timer tool",
                checked = true,
                onCheckedChange = { }
            )

            ToggleRow(
                icon = Icons.Default.Code,
                title = "Notes",
                subtitle = "Enable notes tool",
                checked = true,
                onCheckedChange = { }
            )

            ToggleRow(
                icon = Icons.Default.Code,
                title = "Weather",
                subtitle = "Enable weather tool",
                checked = false,
                onCheckedChange = { }
            )

            SectionHeader(title = "Developer")

            SettingsNavigationRow(
                icon = Icons.Default.Speed,
                title = "Developer Diagnostics",
                subtitle = "View model status and performance",
                onClick = onDeveloperClick
            )

            SectionHeader(title = "About")

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Jarvis Assistant",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Made with care by the Jarvis Team",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
