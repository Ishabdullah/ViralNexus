package com.viralnexus.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viralnexus.game.data.GraphicsQuality
import com.viralnexus.game.data.UserPreferences
import com.viralnexus.game.models.Difficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    onBack: () -> Unit
) {
    var showResetConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B263B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Audio Settings
            item {
                SettingsSection(
                    title = "Audio",
                    content = {
                        SliderSetting(
                            label = "Master Volume",
                            value = preferences.masterVolume,
                            onValueChange = { preferences.masterVolume = it }
                        )

                        SliderSetting(
                            label = "Music Volume",
                            value = preferences.musicVolume,
                            onValueChange = { preferences.musicVolume = it }
                        )

                        SliderSetting(
                            label = "Sound Effects",
                            value = preferences.sfxVolume,
                            onValueChange = { preferences.sfxVolume = it }
                        )

                        SwitchSetting(
                            label = "Vibration",
                            description = "Haptic feedback for events",
                            checked = preferences.vibrationEnabled,
                            onCheckedChange = { preferences.vibrationEnabled = it }
                        )
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Graphics Settings
            item {
                SettingsSection(
                    title = "Graphics",
                    content = {
                        DropdownSetting(
                            label = "Graphics Quality",
                            options = GraphicsQuality.values().toList(),
                            selected = preferences.graphicsQuality,
                            onSelect = { preferences.graphicsQuality = it },
                            displayText = { it.displayName }
                        )

                        SwitchSetting(
                            label = "Particle Effects",
                            description = "Visual effects for infection spread",
                            checked = preferences.particleEffects,
                            onCheckedChange = { preferences.particleEffects = it }
                        )

                        SwitchSetting(
                            label = "Screen Shake",
                            description = "Camera shake on major events",
                            checked = preferences.screenShake,
                            onCheckedChange = { preferences.screenShake = it }
                        )
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Gameplay Settings
            item {
                SettingsSection(
                    title = "Gameplay",
                    content = {
                        DropdownSetting(
                            label = "Default Difficulty",
                            options = Difficulty.values().toList(),
                            selected = preferences.defaultDifficulty,
                            onSelect = { preferences.defaultDifficulty = it },
                            displayText = { it.displayName }
                        )

                        SwitchSetting(
                            label = "Auto-Save",
                            description = "Automatically save game progress",
                            checked = preferences.autoSaveEnabled,
                            onCheckedChange = { preferences.autoSaveEnabled = it }
                        )

                        SwitchSetting(
                            label = "Show Tutorial",
                            description = "Display tutorial on new games",
                            checked = preferences.showTutorial,
                            onCheckedChange = { preferences.showTutorial = it }
                        )

                        SwitchSetting(
                            label = "News Ticker",
                            description = "Show scrolling news events",
                            checked = preferences.newsTicker,
                            onCheckedChange = { preferences.newsTicker = it }
                        )
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Statistics
            item {
                SettingsSection(
                    title = "Statistics",
                    content = {
                        TextSetting(
                            label = "Games Played",
                            value = preferences.gamesPlayed.toString()
                        )

                        TextSetting(
                            label = "Games Won",
                            value = preferences.gamesWon.toString()
                        )

                        val winRate = if (preferences.gamesPlayed > 0) {
                            (preferences.gamesWon.toFloat() / preferences.gamesPlayed * 100).toInt()
                        } else {
                            0
                        }
                        TextSetting(
                            label = "Win Rate",
                            value = "$winRate%"
                        )
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // About
            item {
                SettingsSection(
                    title = "About",
                    content = {
                        TextSetting(label = "Version", value = "1.0.0")
                        TextSetting(label = "Build", value = "Alpha")

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "ViralNexus - A pandemic simulation strategy game",
                            fontSize = 12.sp,
                            color = Color(0xFF778DA9),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Danger Zone
            item {
                SettingsSection(
                    title = "Danger Zone",
                    content = {
                        ClickableSetting(
                            label = "Reset Tutorial",
                            description = "Show tutorial again on next game",
                            textColor = Color(0xFFF77F00),
                            onClick = { preferences.resetTutorial() }
                        )

                        ClickableSetting(
                            label = "Reset All Settings",
                            description = "Restore default settings",
                            textColor = Color(0xFFE63946),
                            onClick = { showResetConfirmation = true }
                        )
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // Reset Confirmation Dialog
    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Reset Settings?") },
            text = { Text("This will reset all settings to their default values. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        preferences.resetToDefaults()
                        showResetConfirmation = false
                    }
                ) {
                    Text("Reset", color = Color(0xFFE63946))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF06FFA5),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            content()
        }
    }
}

@Composable
fun SliderSetting(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = "${(value * 100).toInt()}%",
                fontSize = 14.sp,
                color = Color(0xFF778DA9),
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFE63946),
                activeTrackColor = Color(0xFFE63946),
                inactiveTrackColor = Color(0xFF415A77)
            )
        )
    }
}

@Composable
fun SwitchSetting(
    label: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White
            )
            description?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color(0xFF778DA9),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF06FFA5),
                checkedTrackColor = Color(0xFF06FFA5).copy(alpha = 0.5f),
                uncheckedThumbColor = Color(0xFF778DA9),
                uncheckedTrackColor = Color(0xFF415A77)
            )
        )
    }
}

@Composable
fun <T> DropdownSetting(
    label: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    displayText: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                color = Color(0xFF2D3E50),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayText(selected),
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "▼",
                        fontSize = 10.sp,
                        color = Color(0xFF778DA9)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D3E50))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = displayText(option),
                                color = if (option == selected) Color(0xFF06FFA5) else Color.White
                            )
                        },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TextSetting(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF778DA9),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ClickableSetting(
    label: String,
    description: String? = null,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
            description?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color(0xFF778DA9),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
