package com.ubb.compose.jetpackapp.quiz

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class QuestionState(
    val question: Question,
    val questionIndex: Int,
    val totalQuestionsCount: Int,
    val showPrevious: Boolean,
    val showDone: Boolean
) {
    var enableNext by mutableStateOf(false)
    var answer by mutableStateOf<Answer<*>?>(null)
}

sealed class QuizState {
    data class Questions(
        @StringRes val surveyTitle: Int,
        val questionsState: List<QuestionState>
    ) : QuizState() {
        var currentQuestionIndex by mutableStateOf(0)
    }

    data class Result(
        @StringRes val surveyTitle: Int,
        val quizResult: QuizResult
    ) : QuizState()
}
