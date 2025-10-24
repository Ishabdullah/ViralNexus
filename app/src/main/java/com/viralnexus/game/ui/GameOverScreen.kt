package com.viralnexus.game.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viralnexus.game.engine.GameStatistics
import com.viralnexus.game.models.GameStatus
import kotlinx.coroutines.delay
import java.text.NumberFormat

/**
 * Game Over Screen
 * Shows victory or defeat with final statistics and options
 */
@Composable
fun GameOverScreen(
    gameStatus: GameStatus,
    statistics: GameStatistics,
    pathogenName: String,
    onNewGame: () -> Unit,
    onMainMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animated visibility
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(800)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(800)
        ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (gameStatus == GameStatus.VICTORY) {
                            listOf(
                                Color(0xFF0D1B2A),
                                Color(0xFF1B263B),
                                Color(0xFF415A77)
                            )
                        } else {
                            listOf(
                                Color(0xFF2B0000),
                                Color(0xFF1A1A2E),
                                Color(0xFF16213E)
                            )
                        }
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Title
                GameOverTitle(gameStatus, pathogenName)

                Spacer(modifier = Modifier.height(48.dp))

                // Statistics Card
                StatisticsCard(statistics, gameStatus)

                Spacer(modifier = Modifier.height(48.dp))

                // Action Buttons
                ActionButtons(
                    onNewGame = onNewGame,
                    onMainMenu = onMainMenu
                )
            }
        }
    }
}

/**
 * Game Over Title with animation
 */
@Composable
private fun GameOverTitle(gameStatus: GameStatus, pathogenName: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = when (gameStatus) {
                GameStatus.VICTORY -> "HUMANITY EXTINCT"
                GameStatus.DEFEAT -> "CURE DEVELOPED"
                else -> "GAME OVER"
            },
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = when (gameStatus) {
                GameStatus.VICTORY -> Color(0xFF00FF88).copy(alpha = glowAlpha)
                GameStatus.DEFEAT -> Color(0xFFFF4444).copy(alpha = glowAlpha)
                else -> Color.Gray
            },
            textAlign = TextAlign.Center,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (gameStatus) {
                GameStatus.VICTORY -> "$pathogenName has successfully eliminated all human life"
                GameStatus.DEFEAT -> "The world has developed a cure for $pathogenName"
                else -> "Game ended"
            },
            fontSize = 14.sp,
            color = Color(0xFFCCCCCC),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Statistics Card with animated counters
 */
@Composable
private fun StatisticsCard(statistics: GameStatistics, gameStatus: GameStatus) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2D3E).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "FINAL STATISTICS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00FF88),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Statistics Rows
            AnimatedStatRow(
                label = "Total Infected",
                value = statistics.totalInfected,
                color = Color(0xFFFFAA00)
            )

            AnimatedStatRow(
                label = "Total Deaths",
                value = statistics.totalDead,
                color = Color(0xFFFF4444)
            )

            AnimatedStatRow(
                label = "Countries Infected",
                value = statistics.infectedCountries.toLong(),
                suffix = " / 56",
                color = Color(0xFF00AAFF)
            )

            AnimatedStatRow(
                label = "Days Elapsed",
                value = statistics.elapsedDays.toLong(),
                suffix = " days",
                color = Color(0xFFAA88FF)
            )

            AnimatedStatRow(
                label = "DNA Points Earned",
                value = statistics.dnaPoints.toLong(),
                color = Color(0xFF00FF88)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Infection Rate Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Global Infection Rate",
                        color = Color(0xFFCCCCCC),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${(statistics.globalInfectionRate * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedProgressBar(
                    progress = statistics.globalInfectionRate,
                    color = Color(0xFFFFAA00)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cure Progress (if defeat)
            if (gameStatus == GameStatus.DEFEAT) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cure Progress",
                            color = Color(0xFFCCCCCC),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${(statistics.cureProgress * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedProgressBar(
                        progress = statistics.cureProgress,
                        color = Color(0xFF00FF88)
                    )
                }
            }
        }
    }
}

/**
 * Animated Statistic Row with count-up animation
 */
@Composable
private fun AnimatedStatRow(
    label: String,
    value: Long,
    suffix: String = "",
    color: Color,
    modifier: Modifier = Modifier
) {
    var displayValue by remember { mutableStateOf(0L) }

    LaunchedEffect(value) {
        val duration = 1000L // 1 second animation
        val steps = 50
        val increment = value / steps
        val delayTime = duration / steps

        for (i in 1..steps) {
            delay(delayTime)
            displayValue = (increment * i).coerceAtMost(value)
        }
        displayValue = value // Ensure we end at exact value
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFFCCCCCC),
            fontSize = 14.sp
        )

        Text(
            text = NumberFormat.getInstance().format(displayValue) + suffix,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Animated Progress Bar
 */
@Composable
private fun AnimatedProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    var animatedProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(progress) {
        val duration = 1000L
        val steps = 50
        val increment = progress / steps
        val delayTime = duration / steps

        for (i in 1..steps) {
            delay(delayTime)
            animatedProgress = (increment * i).coerceAtMost(progress)
        }
        animatedProgress = progress
    }

    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp),
        color = color,
        trackColor = Color(0xFF2A2A3E)
    )
}

/**
 * Action Buttons
 */
@Composable
private fun ActionButtons(
    onNewGame: () -> Unit,
    onMainMenu: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onNewGame,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00FF88),
                contentColor = Color(0xFF0D1B2A)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "NEW PANDEMIC",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onMainMenu,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF00FF88)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "MAIN MENU",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}
