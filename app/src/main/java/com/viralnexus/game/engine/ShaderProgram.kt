package com.viralnexus.game.engine

import android.opengl.GLES30
import android.util.Log

class ShaderProgram {
    private var programId: Int = 0
    private var vertexShaderId: Int = 0
    private var fragmentShaderId: Int = 0

    init {
        createShaderProgram()
    }

    private fun createShaderProgram() {
        Log.d("ShaderProgram", "Creating shader program")

        try {
            // Simple vertex shader
            val vertexShaderSource = """
                #version 300 es
                layout (location = 0) in vec3 aPosition;
                layout (location = 1) in vec2 aTexCoord;

                uniform mat4 uMVPMatrix;

                out vec2 vTexCoord;

                void main() {
                    gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
                    vTexCoord = aTexCoord;
                }
            """.trimIndent()

            // Simple fragment shader
            val fragmentShaderSource = """
                #version 300 es
                precision mediump float;

                in vec2 vTexCoord;
                out vec4 fragColor;

                void main() {
                    // Simple earth-like coloring based on texture coordinates
                    float lat = vTexCoord.y;
                    float lng = vTexCoord.x;

                    // Create a simple earth pattern
                    vec3 oceanColor = vec3(0.1, 0.3, 0.8);
                    vec3 landColor = vec3(0.2, 0.6, 0.2);

                    // Simple noise-like pattern for continents
                    float continent = sin(lng * 3.14159 * 4.0) * cos(lat * 3.14159 * 2.0);
                    continent = smoothstep(-0.2, 0.2, continent);

                    vec3 color = mix(oceanColor, landColor, continent);
                    fragColor = vec4(color, 1.0);
                }
            """.trimIndent()

            Log.d("ShaderProgram", "Loading vertex shader")
            vertexShaderId = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderSource)
            if (vertexShaderId == 0) {
                throw RuntimeException("Failed to load vertex shader")
            }

            Log.d("ShaderProgram", "Loading fragment shader")
            fragmentShaderId = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderSource)
            if (fragmentShaderId == 0) {
                throw RuntimeException("Failed to load fragment shader")
            }

            Log.d("ShaderProgram", "Creating and linking program")
            programId = GLES30.glCreateProgram()
            if (programId == 0) {
                throw RuntimeException("Failed to create OpenGL program")
            }

            GLES30.glAttachShader(programId, vertexShaderId)
            GLES30.glAttachShader(programId, fragmentShaderId)
            GLES30.glLinkProgram(programId)

            // Check for linking errors
            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                val errorLog = GLES30.glGetProgramInfoLog(programId)
                Log.e("ShaderProgram", "Error linking program: $errorLog")
                GLES30.glDeleteProgram(programId)
                programId = 0
                throw RuntimeException("Failed to link shader program: $errorLog")
            }

            Log.d("ShaderProgram", "Shader program created successfully")

        } catch (e: Exception) {
            Log.e("ShaderProgram", "Error creating shader program", e)
            cleanup()
            throw e
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val typeName = if (type == GLES30.GL_VERTEX_SHADER) "vertex" else "fragment"
        Log.d("ShaderProgram", "Loading $typeName shader")

        val shader = GLES30.glCreateShader(type)
        if (shader == 0) {
            Log.e("ShaderProgram", "Failed to create $typeName shader")
            return 0
        }

        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)

        // Check for compilation errors
        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val errorLog = GLES30.glGetShaderInfoLog(shader)
            Log.e("ShaderProgram", "Error compiling $typeName shader: $errorLog")
            GLES30.glDeleteShader(shader)
            return 0
        }

        Log.d("ShaderProgram", "$typeName shader compiled successfully")
        return shader
    }

    fun use() {
        GLES30.glUseProgram(programId)
    }

    fun getUniformLocation(name: String): Int {
        return GLES30.glGetUniformLocation(programId, name)
    }

    fun setMatrix4(name: String, matrix: FloatArray) {
        val location = getUniformLocation(name)
        GLES30.glUniformMatrix4fv(location, 1, false, matrix, 0)
    }

    fun cleanup() {
        if (programId != 0) {
            GLES30.glDeleteProgram(programId)
            programId = 0
        }
        if (vertexShaderId != 0) {
            GLES30.glDeleteShader(vertexShaderId)
            vertexShaderId = 0
        }
        if (fragmentShaderId != 0) {
            GLES30.glDeleteShader(fragmentShaderId)
            fragmentShaderId = 0
        }
    }
}