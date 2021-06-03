package com.ubb.compose.jetpackapp.quiz

import android.net.Uri
import androidx.annotation.StringRes

data class QuizResult(
    val library: String,
    @StringRes val result: Int,
    @StringRes val description: Int
)

data class Quiz(
    @StringRes val title: Int,
    val questions: List<Question>
)

data class Question(
    val id: Int,
    @StringRes val questionText: Int,
    val answer: PossibleAnswer,
    @StringRes val description: Int? = null
)

/**
 * Type of supported special actions for a quiz - pick date, take photo, select contact...
 */
enum class QuizCustomActionType { PICK_DATE, TAKE_PHOTO }

sealed class QuizActionResult {
    data class Date(val date: String) : QuizActionResult()
    data class Photo(val uri: Uri) : QuizActionResult()
}

sealed class PossibleAnswer {
    data class SingleChoice(val optionsStringRes: List<Int>) : PossibleAnswer()
    data class MultipleChoice(val optionsStringRes: List<Int>) : PossibleAnswer()
    data class Action(
        @StringRes val label: Int,
        val actionType: QuizCustomActionType
    ) : PossibleAnswer()

    data class Slider(
        val range: ClosedFloatingPointRange<Float>,
        val steps: Int,
        @StringRes val startText: Int,
        @StringRes val endText: Int,
        @StringRes val neutralText: Int,
        val defaultValue: Float = 5.5f
    ) : PossibleAnswer()
}

sealed class Answer<T : PossibleAnswer> {
    data class SingleChoice(@StringRes val answer: Int) : Answer<PossibleAnswer.SingleChoice>()
    data class MultipleChoice(val answersStringRes: Set<Int>) :
        Answer<PossibleAnswer.MultipleChoice>()

    data class Action(val result: QuizActionResult) : Answer<PossibleAnswer.Action>()
    data class Slider(val answerValue: Float) : Answer<PossibleAnswer.Slider>()
}

/**
 * Add or remove an answer from the list of selected answers depending on whether the answer was
 * selected or deselected.
 */
fun Answer.MultipleChoice.withAnswerSelected(
    @StringRes answer: Int,
    selected: Boolean
): Answer.MultipleChoice {
    val newStringRes = answersStringRes.toMutableSet()
    if (!selected) {
        newStringRes.remove(answer)
    } else {
        newStringRes.add(answer)
    }
    return Answer.MultipleChoice(newStringRes)
}
