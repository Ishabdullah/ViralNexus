package com.viralnexus.game.engine

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*

class GameRenderer(private val gameEngine: GameEngine) : GLSurfaceView.Renderer {
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var earth: Earth3D? = null
    private var shaderProgram: ShaderProgram? = null
    private var isInitialized = false

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        Log.d("GameRenderer", "onSurfaceCreated started")

        try {
            // Check OpenGL version
            val version = GLES30.glGetString(GLES30.GL_VERSION)
            Log.d("GameRenderer", "OpenGL Version: $version")

            // Clear any previous OpenGL errors
            while (GLES30.glGetError() != GLES30.GL_NO_ERROR) {
                // Clear error queue
            }

            GLES30.glClearColor(0.0f, 0.0f, 0.1f, 1.0f)
            checkGLError("glClearColor")

            GLES30.glEnable(GLES30.GL_DEPTH_TEST)
            checkGLError("glEnable GL_DEPTH_TEST")

            GLES30.glEnable(GLES30.GL_CULL_FACE)
            checkGLError("glEnable GL_CULL_FACE")

            // Initialize shaders and 3D objects
            Log.d("GameRenderer", "Creating ShaderProgram")
            shaderProgram = ShaderProgram()

            Log.d("GameRenderer", "Creating Earth3D")
            earth = Earth3D()

            // Set up the camera position
            Matrix.setLookAtM(
                viewMatrix, 0,
                0f, 0f, 5f,  // Eye position
                0f, 0f, 0f,  // Look at center
                0f, 1f, 0f   // Up vector
            )

            isInitialized = true
            Log.d("GameRenderer", "onSurfaceCreated completed successfully")

        } catch (e: Exception) {
            Log.e("GameRenderer", "Error in onSurfaceCreated", e)
            isInitialized = false
        }
    }

    private fun checkGLError(operation: String) {
        val error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            val errorMsg = "OpenGL error in $operation: $error"
            Log.e("GameRenderer", errorMsg)
            throw RuntimeException(errorMsg)
        }
    }
    
    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        Log.d("GameRenderer", "onSurfaceChanged: ${width}x${height}")

        try {
            GLES30.glViewport(0, 0, width, height)
            checkGLError("glViewport")

            val ratio = width.toFloat() / height.toFloat()
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 100f)

            Log.d("GameRenderer", "Surface changed successfully")
        } catch (e: Exception) {
            Log.e("GameRenderer", "Error in onSurfaceChanged", e)
        }
    }

    override fun onDrawFrame(unused: GL10) {
        if (!isInitialized) {
            Log.w("GameRenderer", "Skipping draw frame - not initialized")
            return
        }

        try {
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
            checkGLError("glClear")

            // Update game logic
            gameEngine.update()

            // Apply camera transformations
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.rotateM(modelMatrix, 0, gameEngine.cameraRotationX, 1f, 0f, 0f)
            Matrix.rotateM(modelMatrix, 0, gameEngine.cameraRotationY, 0f, 1f, 0f)
            Matrix.scaleM(modelMatrix, 0, gameEngine.cameraZoom, gameEngine.cameraZoom, gameEngine.cameraZoom)

            // Calculate MVP matrix
            val tempMatrix = FloatArray(16)
            Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)

            // Render the Earth
            val currentEarth = earth
            val currentShaderProgram = shaderProgram

            if (currentEarth != null && currentShaderProgram != null) {
                currentEarth.draw(currentShaderProgram, mvpMatrix)
                checkGLError("draw earth")
            } else {
                Log.w("GameRenderer", "Skipping earth rendering - objects not initialized")
            }

            // Render infection visualization
            renderInfectionData()

        } catch (e: Exception) {
            Log.e("GameRenderer", "Error in onDrawFrame", e)
        }
    }

    private fun renderInfectionData() {
        try {
            // TODO: Render infection points, spread patterns, etc.
            // This will visualize the pandemic data on the 3D globe
        } catch (e: Exception) {
            Log.e("GameRenderer", "Error in renderInfectionData", e)
        }
    }
}

