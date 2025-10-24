package com.viralnexus.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import androidx.lifecycle.compose.LifecycleResumeEffect // Temporarily disabled for Step 1 (unused)
import com.viralnexus.game.engine.GameStatistics
import com.viralnexus.game.models.*
// import com.viralnexus.game.repository.SaveGameRepository // Temporarily disabled for Step 1
import com.viralnexus.game.utils.formatNumber
import com.viralnexus.game.viewmodel.GameViewModel
// import kotlinx.coroutines.launch // Temporarily disabled for Step 1 (unused)

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    // saveGameRepository: SaveGameRepository, // Temporarily disabled for Step 1
    onExit: () -> Unit
) {
    // var showSaveDialog by remember { mutableStateOf(false) } // Temporarily disabled for Step 1
    // val coroutineScope = rememberCoroutineScope() // Temporarily disabled for Step 1
    // Collect state from ViewModel
    val gameStarted by viewModel.gameStarted.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val gameStatistics by viewModel.gameStatistics.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    if (!gameStarted) {
        // Show setup screen
        GameSetupScreen(
            onStartGame = { pathogenType, pathogenName, difficulty, startingCountry ->
                viewModel.startNewGame(pathogenType, pathogenName, difficulty, startingCountry)
            }
        )
        return
    }

    // Get current game state
    if (gameState == null) {
        Text("Error: Game failed to initialize", color = Color.Red)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
            .padding(16.dp)
    ) {
        // Top stats bar - use cached stats for better performance
        GameStatsBar(gameStatistics)

        Spacer(modifier = Modifier.height(8.dp))

        // Game controls (Pause/Resume and Speed)
        GameControls(viewModel, gameState!!)

        Spacer(modifier = Modifier.height(16.dp))

        // Main content area
        when (selectedTab) {
            0 -> WorldMapTab(gameState!!)
            1 -> UpgradesTab(viewModel)
            2 -> StatisticsTab(gameStatistics)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom navigation
        BottomNavigation(
            selectedTab = selectedTab,
            onTabSelected = { viewModel.selectTab(it) },
            onSave = { /* TODO: Re-enable in Step 3 */ }, // Temporarily disabled for Step 1
            onExit = {
                viewModel.resetGame()
                onExit()
            }
        )
    }

    // Save game dialog - Temporarily disabled for Step 1, will be fixed in Step 3
    /*
    if (showSaveDialog) {
        var saveName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Game") },
            text = {
                Column {
                    Text("Enter a name for this save:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = saveName,
                        onValueChange = { saveName = it },
                        label = { Text("Save Name") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            val currentState = viewModel.getCurrentGameState()
                            if (currentState != null) {
                                saveGameRepository.saveGame(
                                    gameState = currentState,
                                    saveName = saveName.ifBlank { null } ?: "",
                                    isAutoSave = false
                                )
                            }
                            showSaveDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    */
}

@Composable
fun GameControls(viewModel: GameViewModel, gameState: GameState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B263B), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pause/Resume Button
        Button(
            onClick = {
                viewModel.togglePause()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (gameState.isPaused) Color(0xFF06FFA5) else Color(0xFFE63946)
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(if (gameState.isPaused) "▶ Resume" else "⏸ Pause", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Game Speed Selector
        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                GameSpeed.SLOW to "0.5x",
                GameSpeed.NORMAL to "1x",
                GameSpeed.FAST to "2x",
                GameSpeed.ULTRA to "4x"
            ).forEach { (speed, label) ->
                SpeedButton(
                    label = label,
                    isSelected = gameState.gameSpeed == speed,
                    onClick = {
                        viewModel.setGameSpeed(speed)
                    }
                )
            }
        }
    }
}

@Composable
fun SpeedButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp),
        color = if (isSelected) Color(0xFFE63946) else Color(0xFF415A77),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun GameStatsBar(stats: GameStatistics?) {
    if (stats == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("DNA", stats.dnaPoints.toString(), Color(0xFFE63946))
                StatItem("Infected", formatNumber(stats.totalInfected), Color(0xFFF77F00))
                StatItem("Dead", formatNumber(stats.totalDead), Color(0xFF9D4EDD))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Countries", "${stats.infectedCountries}/58", Color(0xFF06FFA5))
                StatItem("Day", stats.elapsedDays.toString(), Color(0xFF00B4D8))
                StatItem("Cure", "${(stats.cureProgress * 100).toInt()}%", Color(0xFFFFB703))
            }

            // Cure progress bar
            if (stats.cureProgress > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = stats.cureProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = Color(0xFFE63946),
                    trackColor = Color(0xFF415A77)
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF778DA9)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun WorldMapTab(gameState: GameState) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            item {
                Text(
                    text = "World Status",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(gameState.countries.filter { it.infected > 0 || it.dead > 0 }) { country ->
                CountryCard(country)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CountryCard(country: Country) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D3E50)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = country.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Infected: ${formatNumber(country.infected)}", color = Color(0xFFF77F00), fontSize = 12.sp)
                    Text("Dead: ${formatNumber(country.dead)}", color = Color(0xFF9D4EDD), fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${(country.infectionRate * 100).toInt()}% infected", color = Color(0xFF06FFA5), fontSize = 12.sp)
                    Text("Pop: ${formatNumber(country.population)}", color = Color(0xFF778DA9), fontSize = 12.sp)
                }
            }

            if (country.infectionRate > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = country.infectionRate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = Color(0xFFF77F00),
                    trackColor = Color(0xFF415A77)
                )
            }
        }
    }
}

@Composable
fun UpgradesTab(viewModel: GameViewModel) {
    var selectedCategory by remember { mutableStateOf(UpgradeCategory.TRANSMISSION) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Category tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            UpgradeCategory.values().forEach { category ->
                CategoryChip(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = { selectedCategory = category }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Upgrades list
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                val upgrades = viewModel.getUpgradesByCategory(selectedCategory)
                items(upgrades) { upgrade ->
                    UpgradeCard(upgrade, viewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: UpgradeCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) Color(0xFFE63946) else Color(0xFF415A77),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = category.displayName,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun UpgradeCard(upgrade: Upgrade, viewModel: GameViewModel) {
    val gameState = viewModel.getGameManager().getCurrentGame() ?: return
    val isOwned = gameState.pathogen.hasUpgrade(upgrade.id)
    val isAvailable = upgrade.isAvailable(gameState.pathogen)
    val canAfford = upgrade.canAfford(gameState.pathogen)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isOwned && isAvailable && canAfford) {
                viewModel.purchaseUpgrade(upgrade.id)
            },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isOwned -> Color(0xFF2D5016)
                !isAvailable -> Color(0xFF3C3C3C)
                canAfford -> Color(0xFF2D3E50)
                else -> Color(0xFF4A1F1F)
            }
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = upgrade.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOwned) Color(0xFF06FFA5) else Color.White
                    )
                    if (isOwned) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓",
                            color = Color(0xFF06FFA5),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = upgrade.description,
                    fontSize = 11.sp,
                    color = Color(0xFF778DA9),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (!isOwned) {
                Surface(
                    color = if (canAfford && isAvailable) Color(0xFFE63946) else Color(0xFF778DA9),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${upgrade.cost} DNA",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticsTab(stats: GameStatistics?) {
    // Null check for game statistics
    if (stats == null) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No game data available", color = Color(0xFF778DA9))
            }
        }
        return
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Global Statistics",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))

                StatisticRow("Total Infected", formatNumber(stats.totalInfected), Color(0xFFF77F00))
                StatisticRow("Total Deaths", formatNumber(stats.totalDead), Color(0xFF9D4EDD))
                StatisticRow("Total Healthy", formatNumber(stats.totalHealthy), Color(0xFF06FFA5))
                StatisticRow("World Population", formatNumber(stats.worldPopulation), Color(0xFF00B4D8))

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFF415A77))
                Spacer(modifier = Modifier.height(16.dp))

                StatisticRow("Infected Countries", "${stats.infectedCountries}/58", Color(0xFFF77F00))
                StatisticRow("Fully Infected", "${stats.fullyInfectedCountries}", Color(0xFFE63946))
                StatisticRow("Global Infection Rate", "${(stats.globalInfectionRate * 100).toInt()}%", Color(0xFFF77F00))
                StatisticRow("Global Death Rate", "${(stats.globalDeathRate * 100).toInt()}%", Color(0xFF9D4EDD))

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFF415A77))
                Spacer(modifier = Modifier.height(16.dp))

                StatisticRow("Cure Progress", "${(stats.cureProgress * 100).toInt()}%", Color(0xFFFFB703))
                StatisticRow("DNA Points", stats.dnaPoints.toString(), Color(0xFFE63946))
                StatisticRow("Days Elapsed", stats.elapsedDays.toString(), Color(0xFF00B4D8))

                Spacer(modifier = Modifier.height(24.dp))

                when (stats.gameStatus) {
                    GameStatus.VICTORY -> {
                        StatusCard("VICTORY!", "You have successfully infected the world!", Color(0xFF06FFA5))
                    }
                    GameStatus.DEFEAT -> {
                        StatusCard("DEFEAT", "The cure was developed before you could win.", Color(0xFFE63946))
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun StatisticRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF778DA9)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun StatusCard(title: String, message: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun BottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onSave: () -> Unit,
    onExit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B263B), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavButton("World", selectedTab == 0) { onTabSelected(0) }
        NavButton("Upgrades", selectedTab == 1) { onTabSelected(1) }
        NavButton("Stats", selectedTab == 2) { onTabSelected(2) }
        NavButton("Save", false, Color(0xFFF77F00)) { onSave() }
        NavButton("Exit", false, Color(0xFFE63946)) { onExit() }
    }
}

@Composable
fun NavButton(text: String, isSelected: Boolean, color: Color = Color(0xFF06FFA5), onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) color else Color.Transparent,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color(0xFF778DA9),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
