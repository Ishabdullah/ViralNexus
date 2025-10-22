package com.viralnexus.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.viralnexus.game.engine.GameManager
import com.viralnexus.game.ui.GameScreen

class SimpleGameActivity : ComponentActivity() {
    private val gameManager = GameManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    GameScreen(
                        gameManager = gameManager,
                        onExit = { finish() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameManager.reset()
    }
}