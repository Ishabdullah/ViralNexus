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
import com.viralnexus.game.ui.theme.ViralNexusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViralNexusTheme {
                MainMenuScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen() {
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
                onClick = {
                    try {
                        // Use SimpleGameActivity for now to avoid OpenGL crashes
                        context.startActivity(Intent(context, SimpleGameActivity::class.java))
                    } catch (e: Exception) {
                        // If that fails too, at least log it
                        println("SimpleGameActivity not available: ${e.message}")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MenuButton(
                text = "CONTINUE OUTBREAK",
                onClick = { /* TODO: Load saved game */ }
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
                onClick = { /* TODO: Settings screen */ }
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
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E3A8A).copy(alpha = 0.8f),
            contentColor = Color.White
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