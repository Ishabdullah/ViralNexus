package com.viralnexus.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viralnexus.game.models.Difficulty
import com.viralnexus.game.models.PathogenType

@Composable
fun GameSetupScreen(
    onStartGame: (PathogenType, String, Difficulty, String) -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedPathogenType by remember { mutableStateOf<PathogenType?>(null) }
    var pathogenName by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    var selectedCountry by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "New Pandemic",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Progress indicator
        LinearProgressIndicator(
            progress = (currentStep + 1) / 4f,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = Color(0xFFE63946),
            trackColor = Color(0xFF415A77)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Content based on current step
        when (currentStep) {
            0 -> PathogenTypeStep(
                selectedType = selectedPathogenType,
                onTypeSelected = { selectedPathogenType = it }
            )
            1 -> PathogenNameStep(
                name = pathogenName,
                onNameChanged = { pathogenName = it }
            )
            2 -> DifficultyStep(
                selectedDifficulty = selectedDifficulty,
                onDifficultySelected = { selectedDifficulty = it }
            )
            3 -> StartingCountryStep(
                selectedCountry = selectedCountry,
                onCountrySelected = { selectedCountry = it }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                Button(
                    onClick = { currentStep-- },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF415A77))
                ) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            val canProceed = when (currentStep) {
                0 -> selectedPathogenType != null
                1 -> pathogenName.isNotBlank()
                2 -> selectedDifficulty != null
                3 -> selectedCountry != null
                else -> false
            }

            Button(
                onClick = {
                    if (currentStep < 3) {
                        currentStep++
                    } else {
                        // Start game
                        if (selectedPathogenType != null && selectedDifficulty != null && selectedCountry != null) {
                            onStartGame(
                                selectedPathogenType!!,
                                pathogenName.ifBlank { selectedPathogenType!!.displayName },
                                selectedDifficulty!!,
                                selectedCountry!!
                            )
                        }
                    }
                },
                enabled = canProceed,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE63946))
            ) {
                Text(if (currentStep < 3) "Next" else "Start Pandemic")
            }
        }
    }
}

@Composable
fun PathogenTypeStep(
    selectedType: PathogenType?,
    onTypeSelected: (PathogenType) -> Unit
) {
    Column {
        Text(
            text = "Choose Your Pathogen",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        PathogenType.values().forEach { type ->
            PathogenTypeCard(
                type = type,
                isSelected = type == selectedType,
                onClick = { onTypeSelected(type) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun PathogenTypeCard(
    type: PathogenType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE63946).copy(alpha = 0.3f) else Color(0xFF1B263B)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = type.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFFE63946) else Color.White
                )
                if (isSelected) {
                    Text(
                        text = "✓",
                        fontSize = 24.sp,
                        color = Color(0xFFE63946)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = type.description,
                fontSize = 14.sp,
                color = Color(0xFF778DA9)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatChip("Starting DNA: ${type.startingDNA}", Color(0xFFE63946))
                StatChip("Mutation: ${(type.mutationRate * 100).toInt()}%", Color(0xFF06FFA5))
            }
        }
    }
}

@Composable
fun StatChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PathogenNameStep(
    name: String,
    onNameChanged: (String) -> Unit
) {
    Column {
        Text(
            text = "Name Your Pathogen",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Choose a name that strikes fear into the hearts of humanity",
            fontSize = 14.sp,
            color = Color(0xFF778DA9)
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            label = { Text("Pathogen Name") },
            placeholder = { Text("e.g., COVID-99, Black Death II") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE63946),
                unfocusedBorderColor = Color(0xFF415A77),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color(0xFFE63946),
                unfocusedLabelColor = Color(0xFF778DA9)
            ),
            singleLine = true
        )
    }
}

@Composable
fun DifficultyStep(
    selectedDifficulty: Difficulty?,
    onDifficultySelected: (Difficulty) -> Unit
) {
    Column {
        Text(
            text = "Select Difficulty",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        Difficulty.values().forEach { difficulty ->
            DifficultyCard(
                difficulty = difficulty,
                isSelected = difficulty == selectedDifficulty,
                onClick = { onDifficultySelected(difficulty) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DifficultyCard(
    difficulty: Difficulty,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE63946).copy(alpha = 0.3f) else Color(0xFF1B263B)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = difficulty.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFFE63946) else Color.White
                )
                if (isSelected) {
                    Text(
                        text = "✓",
                        fontSize = 24.sp,
                        color = Color(0xFFE63946)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = difficulty.description,
                fontSize = 14.sp,
                color = Color(0xFF778DA9)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatChip("Starting DNA: ${difficulty.startingDNA}", Color(0xFFE63946))
                StatChip("Cure Speed: ${(difficulty.cureSpeedMultiplier * 100).toInt()}%", Color(0xFFFFB703))
            }
        }
    }
}

@Composable
fun StartingCountryStep(
    selectedCountry: String?,
    onCountrySelected: (String) -> Unit
) {
    val popularCountries = listOf(
        "china" to "China",
        "india" to "India",
        "usa" to "United States",
        "brazil" to "Brazil",
        "russia" to "Russia",
        "japan" to "Japan",
        "uk" to "United Kingdom",
        "germany" to "Germany",
        "france" to "France",
        "italy" to "Italy",
        "canada" to "Canada",
        "australia" to "Australia",
        "south_africa" to "South Africa",
        "egypt" to "Egypt",
        "saudi_arabia" to "Saudi Arabia"
    )

    Column {
        Text(
            text = "Starting Country",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Where will patient zero be infected?",
            fontSize = 14.sp,
            color = Color(0xFF778DA9)
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {
            items(popularCountries) { (id, name) ->
                CountryOption(
                    countryId = id,
                    countryName = name,
                    isSelected = id == selectedCountry,
                    onClick = { onCountrySelected(id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CountryOption(
    countryId: String,
    countryName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE63946).copy(alpha = 0.3f) else Color(0xFF1B263B)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = countryName,
                fontSize = 16.sp,
                color = if (isSelected) Color(0xFFE63946) else Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Text(
                    text = "✓",
                    fontSize = 20.sp,
                    color = Color(0xFFE63946)
                )
            }
        }
    }
}
