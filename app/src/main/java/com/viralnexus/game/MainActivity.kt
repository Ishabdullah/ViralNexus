package com.viralnexus.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.viralnexus.game.data.UserPreferences
import com.viralnexus.game.database.GameDatabase
import com.viralnexus.game.repository.SaveGameRepository
import com.viralnexus.game.ui.SaveLoadScreen
import com.viralnexus.game.ui.SettingsScreen
import com.viralnexus.game.ui.theme.ViralNexusTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: UserPreferences
    private lateinit var saveGameRepository: SaveGameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPreferences = UserPreferences(this)

        // Initialize database and repository
        val database = GameDatabase.getDatabase(this)
        saveGameRepository = SaveGameRepository(database.saveGameDao())

        setContent {
            ViralNexusTheme {
                var showSettings by remember { mutableStateOf(false) }
                var showSaveLoad by remember { mutableStateOf(false) }

                when {
                    showSettings -> {
                        SettingsScreen(
                            preferences = userPreferences,
                            onBack = { showSettings = false }
                        )
                    }
                    showSaveLoad -> {
                        val savedGames by saveGameRepository.allSaves.collectAsState(initial = emptyList())

                        SaveLoadScreen(
                            savedGames = savedGames,
                            onLoadGame = { saveId ->
                                lifecycleScope.launch {
                                    val gameState = saveGameRepository.loadGame(saveId)
                                    if (gameState != null) {
                                        // Pass the loaded game state to SimpleGameActivity
                                        val intent = Intent(this@MainActivity, SimpleGameActivity::class.java)
                                        intent.putExtra("LOAD_GAME", true)
                                        intent.putExtra("SAVE_ID", saveId)
                                        startActivity(intent)
                                    }
                                    showSaveLoad = false
                                }
                            },
                            onDeleteGame = { saveId ->
                                lifecycleScope.launch {
                                    saveGameRepository.deleteSave(saveId)
                                }
                            },
                            onNewGame = {
                                showSaveLoad = false
                                startActivity(Intent(this@MainActivity, SimpleGameActivity::class.java))
                            },
                            onBack = { showSaveLoad = false }
                        )
                    }
                    else -> {
                        MainMenuScreen(
                            onOpenSettings = { showSettings = true },
                            onOpenSaveLoad = { showSaveLoad = true },
                            onNewGame = {
                                startActivity(Intent(this@MainActivity, SimpleGameActivity::class.java))
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onOpenSettings: () -> Unit = {},
    onOpenSaveLoad: () -> Unit = {},
    onNewGame: () -> Unit = {}
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Game Title
            Text(
                text = "VIRAL NEXUS",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00FF88),
                textAlign = TextAlign.Center,
                letterSpacing = 4.sp
            )
            
            Text(
                text = "Evolution of Contagion",
                fontSize = 16.sp,
                color = Color(0xFF88FFAA),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Menu Buttons
            MenuButton(
                text = "NEW PANDEMIC",
                onClick = onNewGame
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "CONTINUE OUTBREAK",
                onClick = onOpenSaveLoad
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MenuButton(
                text = "PATHOGEN RESEARCH",
                onClick = { /* TODO: Show pathogen selection */ }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MenuButton(
                text = "GLOBAL STATISTICS",
                onClick = { /* TODO: Show stats */ }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MenuButton(
                text = "SETTINGS",
                onClick = onOpenSettings
            )
        }
        
        // Version info
        Text(
            text = "v1.0.0 - Beta",
            color = Color(0xFF666666),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
fun MenuButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E3A8A).copy(alpha = 0.8f),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF1E3A8A).copy(alpha = 0.3f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
    }
}