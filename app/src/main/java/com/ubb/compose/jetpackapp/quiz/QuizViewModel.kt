package com.ubb.compose.jetpackapp.quiz

import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class QuizViewModel(
    private val surveyRepository: QuizRepository,
    private val photoManager: PhotoManager
) : ViewModel() {

    private val _uiState = MutableLiveData<QuizState>()
    val uiState: LiveData<QuizState>
        get() = _uiState

    private lateinit var quizInitialState: QuizState

    // Uri used to save photos taken with the camera
    private var uri: Uri? = null

    init {
        viewModelScope.launch {
            val survey = surveyRepository.getQuiz()

            // Create the default questions state based on the survey questions
            val questions: List<QuestionState> = survey.questions.mapIndexed { index, question ->
                val showPrevious = index > 0
                val showDone = index == survey.questions.size - 1
                QuestionState(
                    question = question,
                    questionIndex = index,
                    totalQuestionsCount = survey.questions.size,
                    showPrevious = showPrevious,
                    showDone = showDone
                )
            }
            quizInitialState = QuizState.Questions(survey.title, questions)
            _uiState.value = quizInitialState
        }
    }

    fun computeResult(quizQuestions: QuizState.Questions) {
        // val answers = quizQuestions.questionsState.mapNotNull { it.answer }
        val result = surveyRepository.getQuizResult()
        _uiState.value = QuizState.Result(quizQuestions.surveyTitle, result)
    }

    fun onDatePicked(questionId: Int, date: String) {
        updateStateWithActionResult(questionId, QuizActionResult.Date(date))
    }

    fun getCurrentDate(questionId: Int): Long {
        return getSelectedDate(questionId)
    }

    fun getUriToSaveImage(): Uri? {
        uri = photoManager.buildNewUri()
        return uri
    }

    fun onImageSaved() {
        uri?.let { uri ->
            getLatestQuestionId()?.let { questionId ->
                updateStateWithActionResult(questionId, QuizActionResult.Photo(uri))
            }
        }
    }

    private fun updateStateWithActionResult(questionId: Int, result: QuizActionResult) {
        val latestState = _uiState.value
        if (latestState != null && latestState is QuizState.Questions) {
            val question =
                latestState.questionsState.first { questionState ->
                    questionState.question.id == questionId
                }

            question.answer = Answer.Action(result)
            question.enableNext = true
        }
    }

    private fun getLatestQuestionId(): Int? {
        val latestState = _uiState.value
        if (latestState != null && latestState is QuizState.Questions) {
            return latestState.questionsState[latestState.currentQuestionIndex].question.id
        }
        return null
    }

    private fun getSelectedDate(questionId: Int): Long {
        val latestState = _uiState.value
        var ret = Date().time
        if (latestState != null && latestState is QuizState.Questions) {
            val question =
                latestState.questionsState.first { questionState ->
                    questionState.question.id == questionId
                }
            val answer: Answer.Action? = question.answer as Answer.Action?
            if (answer != null && answer.result is QuizActionResult.Date) {
                val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
                val formatted = formatter.parse(answer.result.date)
                if (formatted is Date)
                    ret = formatted.time
            }
        }
        return ret
    }
}

class QuizViewModelFactory(
    private val photoManager: PhotoManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(QuizRepository, photoManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
