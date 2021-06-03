package com.ubb.compose.jetpackapp.quiz

import android.os.Build
import com.ubb.compose.jetpackapp.R


private val mockQuestions = mutableListOf(
    Question(
        id = 1,
        questionText = R.string.in_my_free_time,
        answer = PossibleAnswer.MultipleChoice(
            optionsStringRes = listOf(
                R.string.read,
                R.string.work_out,
                R.string.draw,
                R.string.play_games,
                R.string.dance,
                R.string.watch_movies
            )
        ),
        description = R.string.select_all
    ),
    Question(
        id = 2,
        questionText = R.string.movie,
        answer = PossibleAnswer.SingleChoice(
            optionsStringRes = listOf(
                R.string.thriller,
                R.string.romance,
                R.string.drama,
                R.string.action
            )
        ),
        description = R.string.select_one
    ),
    Question(
        id = 7,
        questionText = R.string.favourite_movie,
        answer = PossibleAnswer.SingleChoice(
            listOf(
                R.string.star_trek,
                R.string.social_network,
                R.string.back_to_future,
                R.string.outbreak
            )
        ),
        description = R.string.select_one
    ),
    Question(
        id = 3,
        questionText = R.string.birthday,
        answer = PossibleAnswer.Action(label = R.string.pick_date, actionType = QuizCustomActionType.PICK_DATE),
        description = R.string.select_date
    ),
    Question(
        id = 4,
        questionText = R.string.selfies,
        answer = PossibleAnswer.Slider(
            range = 1f..10f,
            steps = 3,
            startText = R.string.strongly_dislike,
            endText = R.string.strongly_like,
            neutralText = R.string.neutral
        )
    )
).apply {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        // Add the camera feature only for devices 29+
        add(
            Question(
                id = 975,
                questionText = R.string.selfie_skills,
                answer = PossibleAnswer.Action(label = R.string.add_photo, actionType = QuizCustomActionType.TAKE_PHOTO)
            )
        )
    }
}.toList()

private val jetpackQuiz = Quiz(
    title = R.string.which_jetpack_library,
    questions = mockQuestions
)

object QuizRepository {
    fun getQuiz() = jetpackQuiz

    fun getQuizResult(): QuizResult {
        return QuizResult(
            library = "Jetpack Compose",
            result = R.string.survey_result,
            description = R.string.survey_result_description
        )
    }
}
