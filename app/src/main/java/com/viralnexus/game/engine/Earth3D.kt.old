package com.viralnexus.game.engine

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.*

class Earth3D {
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private var vertexCount: Int = 0
    private var indexCount: Int = 0

    // OpenGL handles
    private var vaoId: Int = 0
    private var vboId: Int = 0
    private var eboId: Int = 0

    init {
        try {
            Log.d("Earth3D", "Initializing Earth3D")
            createSphere()
            setupBuffers()
            Log.d("Earth3D", "Earth3D initialized successfully")
        } catch (e: Exception) {
            Log.e("Earth3D", "Error initializing Earth3D", e)
            throw e
        }
    }

    private fun createSphere() {
        val radius = 1.0f
        val latitudeBands = 30
        val longitudeBands = 30

        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        // Generate vertices
        for (lat in 0..latitudeBands) {
            val theta = lat * PI / latitudeBands
            val sinTheta = sin(theta).toFloat()
            val cosTheta = cos(theta).toFloat()

            for (lng in 0..longitudeBands) {
                val phi = lng * 2 * PI / longitudeBands
                val sinPhi = sin(phi).toFloat()
                val cosPhi = cos(phi).toFloat()

                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                // Position
                vertices.add(radius * x)
                vertices.add(radius * y)
                vertices.add(radius * z)

                // Texture coordinates
                val u = lng.toFloat() / longitudeBands
                val v = lat.toFloat() / latitudeBands
                vertices.add(u)
                vertices.add(v)
            }
        }

        // Generate indices
        for (lat in 0 until latitudeBands) {
            for (lng in 0 until longitudeBands) {
                val first = (lat * (longitudeBands + 1) + lng).toShort()
                val second = (first + longitudeBands + 1).toShort()

                // First triangle
                indices.add(first)
                indices.add(second)
                indices.add((first + 1).toShort())

                // Second triangle
                indices.add(second)
                indices.add((second + 1).toShort())
                indices.add((first + 1).toShort())
            }
        }

        vertexCount = vertices.size / 5 // 3 position + 2 texture coords
        indexCount = indices.size

        // Convert to buffers
        val vertexArray = vertices.toFloatArray()
        vertexBuffer = ByteBuffer.allocateDirect(vertexArray.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertexArray)
        vertexBuffer.position(0)

        val indexArray = indices.toShortArray()
        indexBuffer = ByteBuffer.allocateDirect(indexArray.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexBuffer.put(indexArray)
        indexBuffer.position(0)
    }

    private fun setupBuffers() {
        Log.d("Earth3D", "Setting up OpenGL buffers")

        try {
            // Generate VAO
            val vaos = IntArray(1)
            GLES30.glGenVertexArrays(1, vaos, 0)
            vaoId = vaos[0]
            if (vaoId == 0) {
                throw RuntimeException("Failed to generate VAO")
            }

            // Generate VBO
            val vbos = IntArray(1)
            GLES30.glGenBuffers(1, vbos, 0)
            vboId = vbos[0]
            if (vboId == 0) {
                throw RuntimeException("Failed to generate VBO")
            }

            // Generate EBO
            val ebos = IntArray(1)
            GLES30.glGenBuffers(1, ebos, 0)
            eboId = ebos[0]
            if (eboId == 0) {
                throw RuntimeException("Failed to generate EBO")
            }

            // Bind VAO
            GLES30.glBindVertexArray(vaoId)

            // Bind and fill VBO
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId)
            GLES30.glBufferData(
                GLES30.GL_ARRAY_BUFFER,
                vertexBuffer.capacity() * 4,
                vertexBuffer,
                GLES30.GL_STATIC_DRAW
            )

            // Bind and fill EBO
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboId)
            GLES30.glBufferData(
                GLES30.GL_ELEMENT_ARRAY_BUFFER,
                indexBuffer.capacity() * 2,
                indexBuffer,
                GLES30.GL_STATIC_DRAW
            )

            // Position attribute (location = 0)
            GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 5 * 4, 0)
            GLES30.glEnableVertexAttribArray(0)

            // Texture coordinate attribute (location = 1)
            GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 5 * 4, 3 * 4)
            GLES30.glEnableVertexAttribArray(1)

            // Unbind VAO
            GLES30.glBindVertexArray(0)

            Log.d("Earth3D", "Buffers set up successfully: VAO=$vaoId, VBO=$vboId, EBO=$eboId")

        } catch (e: Exception) {
            Log.e("Earth3D", "Error setting up buffers", e)
            cleanup()
            throw e
        }
    }

    fun draw(shaderProgram: ShaderProgram, mvpMatrix: FloatArray) {
        shaderProgram.use()
        shaderProgram.setMatrix4("uMVPMatrix", mvpMatrix)

        GLES30.glBindVertexArray(vaoId)
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexCount, GLES30.GL_UNSIGNED_SHORT, 0)
        GLES30.glBindVertexArray(0)
    }

    fun cleanup() {
        if (vaoId != 0) {
            GLES30.glDeleteVertexArrays(1, intArrayOf(vaoId), 0)
            vaoId = 0
        }
        if (vboId != 0) {
            GLES30.glDeleteBuffers(1, intArrayOf(vboId), 0)
            vboId = 0
        }
        if (eboId != 0) {
            GLES30.glDeleteBuffers(1, intArrayOf(eboId), 0)
            eboId = 0
        }
    }
}