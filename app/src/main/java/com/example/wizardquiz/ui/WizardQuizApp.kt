package com.example.wizardquiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizardquiz.domain.QuizMode

@Composable
fun WizardQuizApp(viewModel: WizardQuizViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.22f)
                    )
                )
            )
    ) {
        when {
            state.isLoading -> LoadingScreen()
            state.screen == QuizUiState.Screen.Home -> HomeScreen(
                state = state,
                onModeSelected = viewModel::setMode,
                onCategorySelected = viewModel::setTrainingCategory,
                onStart = viewModel::startGame
            )

            state.screen == QuizUiState.Screen.Question -> QuestionScreen(
                state = state,
                onChoiceCountSelected = viewModel::setChoiceCountForCurrentQuestion,
                onAnswerSelected = viewModel::chooseAnswer,
                onSubmit = viewModel::submitAnswer,
                onNext = viewModel::nextQuestion
            )

            else -> ResultScreen(state = state, onReplay = viewModel::startGame, onBackHome = viewModel::restart)
        }
    }
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Chargement du grimoire...")
    }
}

@Composable
private fun HomeScreen(
    state: QuizUiState,
    onModeSelected: (QuizMode) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text("Wizard Quiz", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                Text(
                    "Un style d’académie magique modernisé : réponds à 10 questions offline.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text("Mode", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.settings.mode == QuizMode.QUIZ,
                onClick = { onModeSelected(QuizMode.QUIZ) },
                label = { Text("Quiz") }
            )
            FilterChip(
                selected = state.settings.mode == QuizMode.TRAINING,
                onClick = { onModeSelected(QuizMode.TRAINING) },
                label = { Text("Entraînement") }
            )
        }

        if (state.settings.mode == QuizMode.TRAINING) {
            Text("Catégorie", fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.settings.selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("Toutes") }
                )
            }
            state.settings.categories.forEach { category ->
                FilterChip(
                    selected = state.settings.selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) }
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Nouveau scoring", fontWeight = FontWeight.SemiBold)
                Text("Tu choisis le nombre de propositions à chaque question (2 / 4 / 8 / 12).")
                Text("Moins d’options = plus de points potentiels.")
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Démarrer l’épreuve")
        }
    }
}

@Composable
private fun QuestionScreen(
    state: QuizUiState,
    onChoiceCountSelected: (Int) -> Unit,
    onAnswerSelected: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit
) {
    val prepared = state.preparedQuestion ?: return
    val question = prepared.question

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Score: ${state.score}", fontWeight = FontWeight.Bold)
            Text(state.progressText)
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(question.category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
                Text(question.text, style = MaterialTheme.typography.titleMedium)
                HorizontalDivider()
                Text("Niveau: ${question.difficulty.name.lowercase()} • Bonus de base: +${question.difficulty.bonus}")
            }
        }

        Text("Nombre de propositions (choisi avant validation)", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(2, 4, 8, 12).forEach { count ->
                FilterChip(
                    selected = state.currentChoiceCount == count,
                    onClick = { onChoiceCountSelected(count) },
                    enabled = !state.answerSubmitted,
                    label = { Text("$count") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                    )
                )
            }
        }
        state.downgradeMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

        prepared.options.forEach { option ->
            val selected = state.selectedAnswer == option
            val backgroundColor = when {
                !state.answerSubmitted && selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                state.answerSubmitted && option == question.correctAnswer -> Color(0xFF2E7D32).copy(alpha = 0.22f)
                state.answerSubmitted && selected -> Color(0xFFB71C1C).copy(alpha = 0.22f)
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            }

            Button(
                onClick = { onAnswerSelected(option) },
                enabled = !state.answerSubmitted,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(option)
            }
        }

        if (!state.answerSubmitted) {
            Button(
                onClick = onSubmit,
                enabled = state.selectedAnswer != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Valider")
            }
        } else {
            val isCorrect = state.selectedAnswer == question.correctAnswer
            Text(if (isCorrect) "Correct ! +${state.lastAwardedPoints} points" else "Incorrect")
            question.explanation?.takeIf { it.isNotBlank() }?.let {
                Text("Explication: $it")
            }
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Text(if (state.isLastQuestion) "Voir résultats" else "Suivant")
            }
        }
    }
}

@Composable
private fun ResultScreen(
    state: QuizUiState,
    onReplay: () -> Unit,
    onBackHome: () -> Unit
) {
    val maxScore = state.questions.sumOf { question ->
        val maxMultiplier = 4
        question.difficulty.bonus * maxMultiplier
    }.coerceAtLeast(1)
    val percent = (state.score * 100) / maxScore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(RoundedCornerShape(46.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text("$percent%", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Text("Résultats", style = MaterialTheme.typography.headlineMedium)
        Text("Score: ${state.score}/$maxScore")
        Text("Astuce: moins de choix possibles rapporte davantage de points.")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onReplay, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Rejouer") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onBackHome, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Retour accueil") }
    }
}
