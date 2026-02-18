package com.example.wizardquiz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizardquiz.domain.QuizMode

@Composable
fun WizardQuizApp(viewModel: WizardQuizViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> LoadingScreen()
        state.screen == QuizUiState.Screen.Home -> HomeScreen(
            state = state,
            onChoiceCountSelected = viewModel::setChoiceCount,
            onModeSelected = viewModel::setMode,
            onCategorySelected = viewModel::setTrainingCategory,
            onStart = viewModel::startGame
        )

        state.screen == QuizUiState.Screen.Question -> QuestionScreen(
            state = state,
            onAnswerSelected = viewModel::chooseAnswer,
            onSubmit = viewModel::submitAnswer,
            onNext = viewModel::nextQuestion
        )

        else -> ResultScreen(state = state, onReplay = viewModel::startGame, onBackHome = viewModel::restart)
    }
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Chargement...")
    }
}

@Composable
private fun HomeScreen(
    state: QuizUiState,
    onChoiceCountSelected: (Int) -> Unit,
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
        Text("Wizard Quiz", style = MaterialTheme.typography.headlineMedium)
        Text("Choisissez le nombre de propositions", fontWeight = FontWeight.Bold)

        listOf(2, 4, 8, 12).forEach { count ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = state.settings.choiceCount == count,
                        onClick = { onChoiceCountSelected(count) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = state.settings.choiceCount == count, onClick = null)
                Text("$count réponses")
            }
        }

        Text("Mode", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onModeSelected(QuizMode.QUIZ) }) { Text("Quiz") }
            Button(onClick = { onModeSelected(QuizMode.TRAINING) }) { Text("Entraînement") }
        }
        Text("Mode actuel: ${if (state.settings.mode == QuizMode.QUIZ) "Quiz" else "Entraînement"}")

        if (state.settings.mode == QuizMode.TRAINING) {
            Text("Catégorie (optionnelle)", fontWeight = FontWeight.Bold)
            Button(onClick = { onCategorySelected(null) }) { Text("Toutes") }
            state.settings.categories.forEach { category ->
                Button(onClick = { onCategorySelected(category) }) { Text(category) }
            }
            Text("Sélection: ${state.settings.selectedCategory ?: "Toutes"}")
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
            Text("Démarrer")
        }
    }
}

@Composable
private fun QuestionScreen(
    state: QuizUiState,
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
        Text("Score: ${state.score}")
        Text(state.progressText)
        state.downgradeMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(question.category, style = MaterialTheme.typography.labelLarge)
                Text(question.text, style = MaterialTheme.typography.titleMedium)
            }
        }

        prepared.options.forEach { option ->
            val selected = state.selectedAnswer == option
            val answerColor = when {
                !state.answerSubmitted -> MaterialTheme.colorScheme.onSurface
                option == question.correctAnswer -> MaterialTheme.colorScheme.primary
                selected -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            }
            Button(
                onClick = { onAnswerSelected(option) },
                enabled = !state.answerSubmitted,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(option, color = answerColor)
            }
        }

        if (!state.answerSubmitted) {
            Button(
                onClick = onSubmit,
                enabled = state.selectedAnswer != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Valider")
            }
        } else {
            val isCorrect = state.selectedAnswer == question.correctAnswer
            Text(if (isCorrect) "Correct !" else "Incorrect")
            question.explanation?.takeIf { it.isNotBlank() }?.let {
                Text("Explication: $it")
            }
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
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
    val maxScore = state.questions.sumOf { it.difficulty.bonus }.coerceAtLeast(1)
    val percent = (state.score * 100) / maxScore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Résultats", style = MaterialTheme.typography.headlineMedium)
        Text("Score: ${state.score}/$maxScore")
        Text("$percent%")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onReplay, modifier = Modifier.fillMaxWidth()) { Text("Rejouer") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onBackHome, modifier = Modifier.fillMaxWidth()) { Text("Retour accueil") }
    }
}
