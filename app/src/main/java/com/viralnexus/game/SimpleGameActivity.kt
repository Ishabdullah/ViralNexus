package com.viralnexus.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
// import com.viralnexus.game.database.GameDatabase // Temporarily disabled for Step 1
// import com.viralnexus.game.repository.SaveGameRepository // Temporarily disabled for Step 1
import com.viralnexus.game.ui.GameScreen
import com.viralnexus.game.viewmodel.GameViewModel
import kotlinx.coroutines.launch

class SimpleGameActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()
    // private lateinit var saveGameRepository: SaveGameRepository // Temporarily disabled for Step 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and repository - Temporarily disabled for Step 1
        // val database = GameDatabase.getDatabase(this)
        // saveGameRepository = SaveGameRepository(database.saveGameDao())

        // Check if we should load a saved game - Temporarily disabled for Step 1
        /*
        val shouldLoadGame = intent.getBooleanExtra("LOAD_GAME", false)
        val saveId = intent.getLongExtra("SAVE_ID", -1L)

        if (shouldLoadGame && saveId != -1L) {
            // Load the saved game
            lifecycleScope.launch {
                val gameState = saveGameRepository.loadGame(saveId)
                if (gameState != null) {
                    viewModel.loadGame(gameState)
                }
            }
        }
        */

        setContent {
            MaterialTheme {
                Surface {
                    GameScreen(
                        viewModel = viewModel,
                        // saveGameRepository = saveGameRepository, // Temporarily disabled for Step 1
                        onExit = { finish() }
                    )
                }
            }
        }
    }
}