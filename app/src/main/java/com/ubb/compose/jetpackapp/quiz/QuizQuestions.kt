package com.ubb.compose.jetpackapp.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.ubb.compose.jetpackapp.R
import com.ubb.compose.jetpackapp.theme.JetpackCustomTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Question(
    question: Question,
    answer: Answer<*>?,
    onAnswer: (Answer<*>) -> Unit,
    onAction: (Int, QuizCustomActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            val backgroundColor = if (MaterialTheme.colors.isLight) {
                MaterialTheme.colors.onSurface.copy(alpha = 0.04f)
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = backgroundColor,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Text(
                    text = stringResource(id = question.questionText),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (question.description != null) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = stringResource(id = question.description),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(bottom = 18.dp, start = 8.dp, end = 8.dp)
                    )
                }
            }
            when (question.answer) {
                is PossibleAnswer.SingleChoice -> SingleChoiceQuestion(
                    possibleAnswer = question.answer,
                    answer = answer as Answer.SingleChoice?,
                    onAnswerSelected = { answer -> onAnswer(Answer.SingleChoice(answer)) },
                    modifier = Modifier.fillParentMaxWidth()
                )
                is PossibleAnswer.MultipleChoice -> MultipleChoiceQuestion(
                    possibleAnswer = question.answer,
                    answer = answer as Answer.MultipleChoice?,
                    onAnswerSelected = { newAnswer, selected ->
                        // create the answer if it doesn't exist or
                        // update it based on the user's selection
                        if (answer == null) {
                            onAnswer(Answer.MultipleChoice(setOf(newAnswer)))
                        } else {
                            onAnswer(answer.withAnswerSelected(newAnswer, selected))
                        }
                    },
                    modifier = Modifier.fillParentMaxWidth()
                )
                is PossibleAnswer.Action -> ActionQuestion(
                    questionId = question.id,
                    possibleAnswer = question.answer,
                    answer = answer as Answer.Action?,
                    onAction = onAction,
                    modifier = Modifier.fillParentMaxWidth()
                )
                is PossibleAnswer.Slider -> SliderQuestion(
                    possibleAnswer = question.answer,
                    answer = answer as Answer.Slider?,
                    onAnswerSelected = { onAnswer(Answer.Slider(it)) },
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SingleChoiceQuestion(
    possibleAnswer: PossibleAnswer.SingleChoice,
    answer: Answer.SingleChoice?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringRes.associateBy { stringResource(id = it) }

    val radioOptions = options.keys.toList()

    val selected = if (answer != null) {
        stringResource(id = answer.answer)
    } else {
        null
    }

    val (selectedOption, onOptionSelected) = remember(answer) { mutableStateOf(selected) }

    Column(modifier = modifier) {
        radioOptions.forEach { text ->
            val onClickHandle = {
                onOptionSelected(text)
                options[text]?.let { onAnswerSelected(it) }
                Unit
            }
            val optionSelected = text == selectedOption

            val answerBorderColor = if (optionSelected) {
                MaterialTheme.colors.primary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (optionSelected) {
                MaterialTheme.colors.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colors.background
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = optionSelected,
                            onClick = onClickHandle
                        )
                        .background(answerBackgroundColor)
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = text
                    )

                    RadioButton(
                        selected = optionSelected,
                        onClick = onClickHandle,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colors.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MultipleChoiceQuestion(
    possibleAnswer: PossibleAnswer.MultipleChoice,
    answer: Answer.MultipleChoice?,
    onAnswerSelected: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringRes.associateBy { stringResource(id = it) }
    Column(modifier = modifier) {
        for (option in options) {
            var checkedState by remember(answer) {
                val selectedOption = answer?.answersStringRes?.contains(option.value)
                mutableStateOf(selectedOption ?: false)
            }
            val answerBorderColor = if (checkedState) {
                MaterialTheme.colors.primary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (checkedState) {
                MaterialTheme.colors.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colors.background
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(answerBackgroundColor)
                        .clickable(
                            onClick = {
                                checkedState = !checkedState
                                onAnswerSelected(option.value, checkedState)
                            }
                        )
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = option.key)

                    Checkbox(
                        checked = checkedState,
                        onCheckedChange = { selected ->
                            checkedState = selected
                            onAnswerSelected(option.value, selected)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colors.primary
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionQuestion(
    questionId: Int,
    possibleAnswer: PossibleAnswer.Action,
    answer: Answer.Action?,
    onAction: (Int, QuizCustomActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    when (possibleAnswer.actionType) {
        QuizCustomActionType.PICK_DATE -> {
            DateQuestion(
                questionId = questionId,
                answer = answer,
                onAction = onAction,
                modifier = modifier
            )
        }
        QuizCustomActionType.TAKE_PHOTO -> {
            PhotoQuestion(
                questionId = questionId,
                answer = answer,
                onAction = onAction,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun PhotoQuestion(
    questionId: Int,
    answer: Answer.Action?,
    onAction: (Int, QuizCustomActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val resource = if (answer != null) {
        Icons.Filled.SwapHoriz
    } else {
        Icons.Filled.AddAPhoto
    }
    OutlinedButton(
        onClick = { onAction(questionId, QuizCustomActionType.TAKE_PHOTO) },
        modifier = modifier,
        contentPadding = PaddingValues()
    ) {
        Column {
            if (answer != null && answer.result is QuizActionResult.Photo) {
                Image(
                    painter = rememberCoilPainter(answer.result.uri, fadeIn = true),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(96.dp)
                        .aspectRatio(4 / 3f)
                )
            } else {
                PhotoDefaultImage(modifier = Modifier.padding(horizontal = 86.dp, vertical = 74.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.BottomCenter)
                    .padding(vertical = 26.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = resource, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        id = if (answer != null) {
                            R.string.retake_photo
                        } else {
                            R.string.add_photo
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun DateQuestion(
    questionId: Int,
    answer: Answer.Action?,
    onAction: (Int, QuizCustomActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val date = if (answer != null && answer.result is QuizActionResult.Date) {
        answer.result.date
    } else {
        SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
    }
    Button(
        onClick = { onAction(questionId, QuizCustomActionType.PICK_DATE) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.onSecondary
        ),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .padding(vertical = 20.dp)
            .height(54.dp),
        elevation = ButtonDefaults.elevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f))

    ) {
        Text(
            text = date,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
        )
    }
}

@Composable
private fun PhotoDefaultImage(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = MaterialTheme.colors.isLight
) {
    val assetId = if (lightTheme) {
        R.drawable.ic_selfie_light
    } else {
        R.drawable.ic_selfie_dark
    }
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier,
        contentDescription = null
    )
}

@Composable
private fun SliderQuestion(
    possibleAnswer: PossibleAnswer.Slider,
    answer: Answer.Slider?,
    onAnswerSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember {
        mutableStateOf(answer?.answerValue ?: possibleAnswer.defaultValue)
    }
    Row(modifier = modifier) {

        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onAnswerSelected(it)
            },
            valueRange = possibleAnswer.range,
            steps = possibleAnswer.steps,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )
    }
    Row {
        Text(
            text = stringResource(id = possibleAnswer.startText),
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Text(
            text = stringResource(id = possibleAnswer.neutralText),
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Text(
            text = stringResource(id = possibleAnswer.endText),
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
    }
}

@Preview
@Composable
fun QuestionPreview() {
    val question = Question(
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
    )
    JetpackCustomTheme {
        Question(question = question, answer = null, onAnswer = {}, onAction = { _, _ -> })
    }
}
