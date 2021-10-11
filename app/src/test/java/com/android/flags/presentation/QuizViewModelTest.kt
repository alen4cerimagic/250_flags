package com.android.flags.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.flags.MainCoroutineRule
import com.android.flags.TestQuizRepository
import com.android.flags.domain.CountryModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.tools.ant.taskdefs.Sleep
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class QuizViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: QuizViewModel

    @Before
    fun setup() {
        viewModel = QuizViewModel(TestQuizRepository())
    }

    @Test
    fun `check if correct answer exists`() {
        viewModel.play()

        val correctAnswer =
            getCorrectAnswer(viewModel.questions.value?.peekContent()?.data)

        assertThat(correctAnswer).isNotNull()
    }

    @Test
    fun `check if incorrect answer exists`() {
        viewModel.play()

        val incorrectAnswer =
            getIncorrectAnswer(viewModel.questions.value?.peekContent()?.data)

        assertThat(incorrectAnswer).isNotNull()
    }

    @Test
    fun `time run out`() {
        viewModel.play()

        val time = 1000L * 15
        Thread.sleep(time)

        assertThat(viewModel.result.value?.peekContent()).isNotNull()
    }

    @Test
    fun `game won`() {
        viewModel.play()

        for (i in 0..50) {
            val correctAnswer =
                getCorrectAnswer(viewModel.questions.value?.peekContent()?.data)
            if (correctAnswer != null)
                viewModel.answer(correctAnswer)
        }

        assertThat(viewModel.gameWon.value?.peekContent()?.first).isEqualTo(50)
    }

    @Test
    fun `game lost`() {
        viewModel.play()

        for (i in 0..2) {
            val correctAnswer =
                getCorrectAnswer(viewModel.questions.value?.peekContent()?.data)
            if (correctAnswer != null)
                viewModel.answer(correctAnswer)
        }

        for (i in 0..2) {
            val incorrectAnswer =
                getIncorrectAnswer(viewModel.questions.value?.peekContent()?.data)
            if (incorrectAnswer != null)
                viewModel.answer(incorrectAnswer)
        }

        val time = 1000L * 11
        Thread.sleep(time)

        assertThat(viewModel.result.value?.peekContent()?.first).isEqualTo(3)
        assertThat(viewModel.result.value?.peekContent()?.second).isEqualTo(3)
    }

    @Test
    fun `check timer with correct answers`() {
        viewModel.play()

        for (i in 0..10) {
            Thread.sleep(1000)

            val correctAnswer =
                getCorrectAnswer(viewModel.questions.value?.peekContent()?.data)
            if (correctAnswer != null)
                viewModel.answer(correctAnswer)
        }

        assertThat(viewModel.time.value?.peekContent()).isEqualTo(10)
    }

    @Test
    fun `check timer with incorrect answers`() {
        viewModel.play()

        for (i in 0..3) {
            Thread.sleep(1000)

            val incorrectAnswer =
                getIncorrectAnswer(viewModel.questions.value?.peekContent()?.data)
            if (incorrectAnswer != null)
                viewModel.answer(incorrectAnswer)
        }

        assertThat(viewModel.time.value?.peekContent()).isEqualTo(4)
    }

    private fun getCorrectAnswer(questions: List<CountryModel>?): CountryModel? {
        questions?.forEach {
            if (it.correct == true)
                return it
        }
        return null
    }

    private fun getIncorrectAnswer(questions: List<CountryModel>?): CountryModel? {
        questions?.forEach {
            if (it.correct == false)
                return it
        }
        return null
    }
}