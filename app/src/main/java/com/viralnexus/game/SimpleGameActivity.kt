package com.viralnexus.game

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class SimpleGameActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("SimpleGameActivity", "Creating simple game view")

        try {
            // Create a simple text-based view instead of OpenGL
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val titleText = TextView(this).apply {
                text = "VIRAL NEXUS - PANDEMIC SIMULATION"
                textSize = 24f
                setPadding(20, 50, 20, 20)
            }

            val statusText = TextView(this).apply {
                text = "3D Engine Loading...\n\nThis is a simplified view while the OpenGL rendering system is being optimized.\n\nThe game features:\n- Global pandemic simulation\n- Real-time country infection tracking\n- Pathogen evolution system\n- Interactive 3D Earth visualization"
                textSize = 16f
                setPadding(20, 20, 20, 20)
            }

            layout.addView(titleText)
            layout.addView(statusText)

            setContentView(layout)

            Log.d("SimpleGameActivity", "Simple game view created successfully")

        } catch (e: Exception) {
            Log.e("SimpleGameActivity", "Error creating simple game view", e)
            finish()
        }
    }
}