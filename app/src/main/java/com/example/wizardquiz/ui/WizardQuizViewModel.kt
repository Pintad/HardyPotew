package com.example.wizardquiz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wizardquiz.data.QuestionRepository
import com.example.wizardquiz.domain.PreparedQuestion
import com.example.wizardquiz.domain.Question
import com.example.wizardquiz.domain.QuizMode
import com.example.wizardquiz.domain.ScoringPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeSettings(
    val mode: QuizMode = QuizMode.QUIZ,
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList()
)

data class QuizUiState(
    val isLoading: Boolean = true,
    val screen: Screen = Screen.Home,
    val settings: HomeSettings = HomeSettings(),
    val questions: List<Question> = emptyList(),
    val index: Int = 0,
    val currentChoiceCount: Int? = null,
    val preparedQuestion: PreparedQuestion? = null,
    val selectedAnswer: String? = null,
    val answerSubmitted: Boolean = false,
    val score: Int = 0,
    val downgradeMessage: String? = null,
    val lastAwardedPoints: Int = 0
) {
    enum class Screen { Home, Question, Result }

    val currentQuestionNumber: Int get() = index + 1
    val totalQuestions: Int get() = questions.size
    val isLastQuestion: Boolean get() = index >= questions.lastIndex
    val progressText: String get() = "Question $currentQuestionNumber/$totalQuestions"
}

class WizardQuizViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val categories = repository.categories()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    settings = it.settings.copy(categories = categories)
                )
            }
        }
    }

    fun setMode(mode: QuizMode) {
        _uiState.update { current ->
            current.copy(settings = current.settings.copy(mode = mode))
        }
    }

    fun setTrainingCategory(category: String?) {
        _uiState.update { current ->
            current.copy(settings = current.settings.copy(selectedCategory = category))
        }
    }

    fun startGame() {
        val state = _uiState.value
        val questions = when (state.settings.mode) {
            QuizMode.QUIZ -> repository.selectQuizQuestions(10)
            QuizMode.TRAINING -> repository.selectTrainingQuestions(state.settings.selectedCategory).take(10)
        }

        if (questions.isEmpty()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    screen = QuizUiState.Screen.Result,
                    questions = emptyList(),
                    score = 0
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                screen = QuizUiState.Screen.Question,
                questions = questions,
                index = 0,
                currentChoiceCount = null,
                preparedQuestion = null,
                selectedAnswer = null,
                answerSubmitted = false,
                score = 0,
                lastAwardedPoints = 0,
                downgradeMessage = null
            )
        }
    }

    fun setChoiceCountForCurrentQuestion(count: Int) {
        val state = _uiState.value
        if (state.answerSubmitted) return
        val currentQuestion = state.questions.getOrNull(state.index) ?: return

        val prepared = repository.prepareOptions(currentQuestion, count)
        _uiState.update {
            it.copy(
                currentChoiceCount = count,
                preparedQuestion = prepared,
                selectedAnswer = null,
                downgradeMessage = if (prepared.wasDowngraded) {
                    "Choix réduits à ${prepared.effectiveChoiceCount} pour cette question."
                } else null
            )
        }
    }

    fun chooseAnswer(answer: String) {
        if (_uiState.value.answerSubmitted) return
        _uiState.update { it.copy(selectedAnswer = answer) }
    }

    fun submitAnswer() {
        val state = _uiState.value
        val selected = state.selectedAnswer ?: return
        val prepared = state.preparedQuestion ?: return
        if (state.answerSubmitted) return

        val isCorrect = selected == prepared.question.correctAnswer
        val earnedPoints = if (isCorrect) {
            ScoringPolicy.pointsForCorrectAnswer(
                difficulty = prepared.question.difficulty,
                effectiveChoiceCount = prepared.effectiveChoiceCount
            )
        } else {
            0
        }
        val updatedScore = state.score + earnedPoints

        _uiState.update {
            it.copy(
                answerSubmitted = true,
                score = updatedScore,
                lastAwardedPoints = earnedPoints
            )
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (!state.answerSubmitted) return

        if (state.isLastQuestion) {
            _uiState.update { it.copy(screen = QuizUiState.Screen.Result, downgradeMessage = null) }
            return
        }

        val nextIndex = state.index + 1
        _uiState.update {
            it.copy(
                index = nextIndex,
                currentChoiceCount = null,
                preparedQuestion = null,
                selectedAnswer = null,
                answerSubmitted = false,
                lastAwardedPoints = 0,
                downgradeMessage = null
            )
        }
    }

    fun restart() {
        _uiState.update {
            it.copy(
                screen = QuizUiState.Screen.Home,
                questions = emptyList(),
                index = 0,
                currentChoiceCount = null,
                preparedQuestion = null,
                selectedAnswer = null,
                answerSubmitted = false,
                score = 0,
                lastAwardedPoints = 0,
                downgradeMessage = null
            )
        }
    }

    class Factory(private val repository: QuestionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WizardQuizViewModel(repository) as T
        }
    }
}
