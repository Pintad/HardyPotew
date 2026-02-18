package com.example.wizardquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wizardquiz.data.QuestionRepository
import com.example.wizardquiz.ui.WizardQuizApp
import com.example.wizardquiz.ui.WizardQuizViewModel
import com.example.wizardquiz.ui.theme.WizardQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = QuestionRepository(applicationContext)

        setContent {
            WizardQuizTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val vm: WizardQuizViewModel = viewModel(
                        factory = WizardQuizViewModel.Factory(repository)
                    )
                    WizardQuizApp(viewModel = vm)
                }
            }
        }
    }
}
