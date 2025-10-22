package com.viralnexus.game

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.viralnexus.game.engine.GameRenderer
import com.viralnexus.game.engine.GameEngine

class GameActivity : Activity() {
    private lateinit var glSurfaceView: GameGLSurfaceView
    private lateinit var gameEngine: GameEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("GameActivity", "onCreate started")

        try {
            // Set fullscreen
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

            Log.d("GameActivity", "Creating GameEngine")
            gameEngine = GameEngine()

            Log.d("GameActivity", "Creating GLSurfaceView")
            glSurfaceView = GameGLSurfaceView(this, gameEngine)

            Log.d("GameActivity", "Setting content view")
            setContentView(glSurfaceView)

            Log.d("GameActivity", "onCreate completed successfully")
        } catch (e: Exception) {
            Log.e("GameActivity", "Error in onCreate", e)
            finish()
        }
    }
    
    override fun onResume() {
        super.onResume()
        try {
            Log.d("GameActivity", "onResume")
            glSurfaceView.onResume()
        } catch (e: Exception) {
            Log.e("GameActivity", "Error in onResume", e)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            Log.d("GameActivity", "onPause")
            glSurfaceView.onPause()
        } catch (e: Exception) {
            Log.e("GameActivity", "Error in onPause", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("GameActivity", "onDestroy")
    }
}

class GameGLSurfaceView(context: Context, private val gameEngine: GameEngine) : GLSurfaceView(context) {
    private val renderer: GameRenderer

    init {
        try {
            Log.d("GameGLSurfaceView", "Initializing OpenGL ES 3.0")
            setEGLContextClientVersion(3) // OpenGL ES 3.0

            Log.d("GameGLSurfaceView", "Creating GameRenderer")
            renderer = GameRenderer(gameEngine)
            setRenderer(renderer)
            renderMode = RENDERMODE_CONTINUOUSLY

            Log.d("GameGLSurfaceView", "GLSurfaceView initialized successfully")
        } catch (e: Exception) {
            Log.e("GameGLSurfaceView", "Error initializing GLSurfaceView", e)
            throw e
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return try {
            gameEngine.handleTouch(event)
            true
        } catch (e: Exception) {
            Log.e("GameGLSurfaceView", "Error handling touch event", e)
            false
        }
    }
}