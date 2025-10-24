package com.viralnexus.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viralnexus.game.database.entities.SaveGameEntity
import com.viralnexus.game.utils.formatNumber
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveLoadScreen(
    savedGames: List<SaveGameEntity>,
    onLoadGame: (Long) -> Unit,
    onDeleteGame: (Long) -> Unit,
    onNewGame: () -> Unit,
    onBack: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf<SaveGameEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Load Game") },
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
        if (savedGames.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF778DA9)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Saved Games",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start a new pandemic to create your first save!",
                        fontSize = 14.sp,
                        color = Color(0xFF778DA9)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onNewGame,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE63946)
                        )
                    ) {
                        Icon(Icons.Default.Add, "New Game")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start New Pandemic")
                    }
                }
            }
        } else {
            // List of saved games
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // New game button at top
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNewGame),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1B263B)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF06FFA5),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Start New Pandemic",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF06FFA5)
                                )
                                Text(
                                    text = "Begin a fresh outbreak",
                                    fontSize = 12.sp,
                                    color = Color(0xFF778DA9)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Section header
                item {
                    Text(
                        text = "Saved Games",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Saved games list
                items(savedGames) { save ->
                    SaveGameCard(
                        save = save,
                        onLoad = { onLoadGame(save.id) },
                        onDelete = { showDeleteConfirmation = save }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteConfirmation?.let { save ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Delete Save?") },
            text = { Text("Are you sure you want to delete \"${save.saveName}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteGame(save.id)
                        showDeleteConfirmation = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFE63946))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SaveGameCard(
    save: SaveGameEntity,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLoad),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B263B)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Icon based on game status
            val (icon, iconColor) = when (save.gameStatus) {
                "VICTORY" -> Icons.Default.Done to Color(0xFF06FFA5)
                "DEFEAT" -> Icons.Default.Close to Color(0xFFE63946)
                else -> Icons.Default.PlayArrow to Color(0xFFF77F00)
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Save info
            Column(modifier = Modifier.weight(1f)) {
                // Save name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = save.saveName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (save.isAutoSave) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF415A77),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "AUTO",
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Pathogen info
                Text(
                    text = "${save.pathogenName} (${save.pathogenType})",
                    fontSize = 12.sp,
                    color = Color(0xFF778DA9)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats
                Row {
                    SaveStatChip(
                        label = "Day ${save.elapsedDays}",
                        color = Color(0xFF00B4D8)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SaveStatChip(
                        label = "${save.infectedCountries}/58 countries",
                        color = Color(0xFFF77F00)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    SaveStatChip(
                        label = "${formatNumber(save.totalInfected)} infected",
                        color = Color(0xFFF77F00)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SaveStatChip(
                        label = "${formatNumber(save.totalDead)} dead",
                        color = Color(0xFF9D4EDD)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Saved date
                Text(
                    text = "Saved ${formatSaveDate(save.savedAt)}",
                    fontSize = 11.sp,
                    color = Color(0xFF778DA9)
                )
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFE63946)
                )
            }
        }
    }
}

@Composable
fun SaveStatChip(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

fun formatSaveDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "just now"
        diff < 3600_000 -> "${diff / 60_000} minutes ago"
        diff < 86400_000 -> "${diff / 3600_000} hours ago"
        diff < 604800_000 -> "${diff / 86400_000} days ago"
        else -> {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
