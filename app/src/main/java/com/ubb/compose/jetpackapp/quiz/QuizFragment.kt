package com.ubb.compose.jetpackapp.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.ubb.compose.jetpackapp.theme.JetpackCustomTheme

class QuizFragment : Fragment() {

    private val viewModel: QuizViewModel by viewModels {
        QuizViewModelFactory(PhotoManager(requireContext().applicationContext))
    }

    private val takePicture = registerForActivityResult(TakePicture()) { photoSaved ->
        if (photoSaved) {
            viewModel.onImageSaved()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                JetpackCustomTheme {
                    viewModel.uiState.observeAsState().value?.let { quizState ->
                        when (quizState) {
                            is QuizState.Questions -> QuizQuestionsScreen(
                                questions = quizState,
                                onAction = { id, action -> handleSurveyAction(id, action) },
                                onDonePressed = { viewModel.computeResult(quizState) },
                                onBackPressed = {
                                    activity?.onBackPressedDispatcher?.onBackPressed()
                                }
                            )
                            is QuizState.Result -> QuizResultScreen(
                                result = quizState,
                                onDonePressed = {
                                    activity?.onBackPressedDispatcher?.onBackPressed()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleSurveyAction(questionId: Int, actionType: QuizCustomActionType) {
        when (actionType) {
            QuizCustomActionType.PICK_DATE -> showDatePicker(questionId)
            QuizCustomActionType.TAKE_PHOTO -> takeAPhoto()
        }
    }

    private fun showDatePicker(questionId: Int) {
        val date = viewModel.getCurrentDate(questionId = questionId)
        val picker = MaterialDatePicker.Builder.datePicker()
            .setSelection(date)
            .build()
        activity?.let {
            picker.show(it.supportFragmentManager, picker.toString())
            picker.addOnPositiveButtonClickListener {
                viewModel.onDatePicked(questionId, picker.headerText)
            }
        }
    }

    private fun takeAPhoto() {
        takePicture.launch(viewModel.getUriToSaveImage())
    }
}
