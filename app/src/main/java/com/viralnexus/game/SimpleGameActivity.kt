package com.viralnexus.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.viralnexus.game.ui.GameScreen
import com.viralnexus.game.viewmodel.GameViewModel

class SimpleGameActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    GameScreen(
                        viewModel = viewModel,
                        onExit = { finish() }
                    )
                }
            }
        }
    }
}