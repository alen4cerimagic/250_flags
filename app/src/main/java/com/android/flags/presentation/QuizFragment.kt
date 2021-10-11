package com.android.flags.presentation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.flags.R
import com.android.flags.databinding.FragmentMainBinding
import com.android.flags.domain.CountryModel
import com.android.flags.util.Constants
import com.android.flags.util.Status
import com.android.flags.util.TrackingEvents.ANSWER_QUESTION
import com.android.flags.util.TrackingEvents.CHANGE_HIGH_SCORE
import com.android.flags.util.TrackingEvents.LOSE_GAME
import com.android.flags.util.TrackingEvents.START_GAME
import com.android.flags.util.TrackingEvents.WIN_GAME
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var viewModel: QuizViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private val countryAdapter = QuizAdapter()
    private var highScore = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        sharedPreferences =
            requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)

        subscribeToObservers()
        setupRecyclerView()
        setClickListeners()

        setInitialHighScore()
    }

    private fun setupRecyclerView() = binding.rvAnswers.apply {
        adapter = countryAdapter
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun setClickListeners() {
        countryAdapter.setOnItemClickListener { country ->
            logEvent(ANSWER_QUESTION, null)
            viewModel.answer(country)
        }

        binding.btnPlay.setOnClickListener {
            logEvent(START_GAME, null)
            binding.btnPlay.visibility = View.INVISIBLE
            viewModel.play()
        }
    }

    private fun setInitialHighScore() {
        highScore = sharedPreferences.getInt(Constants.PREF_HIGH_SCORE, 0)
        binding.tvHighScore.text = highScore.toString()
    }

    private fun subscribeToObservers() {
        viewModel.questions.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        initGameState()
                        setQuestions(result.data ?: arrayListOf())
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        setError()
                    }
                }
            }
        })

        viewModel.time.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                updateTimer(it)
            }
        })

        viewModel.extraTime.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandled()?.let {
                setExtraTime(it)
            }
        })

        viewModel.correctAnswersCount.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandled()?.let {
                setProgress(it)
            }
        })

        viewModel.result.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                gameLost(it)
            }
        })

        viewModel.gameWon.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandled()?.let {
                gameWon(it)
            }
        })
    }

    private fun initGameState() = binding.apply {
        tvMessage.visibility = View.GONE
        tvTitle.visibility = View.GONE
        rvAnswers.visibility = View.VISIBLE
    }

    private fun setQuestions(questions: List<CountryModel>) {
        countryAdapter.countries = questions
        questions.forEach {
            if (it.correct == true)
                Glide.with(requireContext()).load(it.flag).into(binding.ivMain)
        }
    }

    private fun setError() = binding.apply {
        btnPlay.visibility = View.VISIBLE
        tvMessage.visibility = View.VISIBLE
        tvTitle.visibility = View.VISIBLE

        tvTitle.text = getString(R.string.error_title)
        tvMessage.text = getString(R.string.error_message)
    }

    private fun setExtraTime(value: Int) {
        val textColor = if (value < 0) ContextCompat.getColor(
            requireContext(),
            R.color.red
        ) else ContextCompat.getColor(requireContext(), R.color.green)

        binding.apply {
            tvExtraTime.text = if (value < 0) "-1" else "+1"
            tvExtraTime.setTextColor(textColor)
            tvExtraTime.alpha = 1f
            tvExtraTime.animate().alpha(0f).setDuration(500).start()
        }
    }

    private fun setProgress(value: Int) = binding.apply {
        progress.progress = value
        tvProgress.text = String.format(getString(R.string.question_counter), value)
    }

    private fun updateTimer(value: Int) = binding.apply {
        tvTimer.text = value.toString()
    }

    private fun endGame(value: Pair<Int, Int>) = binding.apply {
        tvMessage.text = getString(R.string.your_result, value.first, value.second)

        btnPlay.visibility = View.VISIBLE
        tvMessage.visibility = View.VISIBLE
        tvTitle.visibility = View.VISIBLE
        ivMain.setImageResource(R.drawable.professor)
        countryAdapter.countries = arrayListOf()

        if (value.first > highScore) {
            logEvent(CHANGE_HIGH_SCORE, Bundle().apply { putInt("high_score", value.first) })
            binding.tvHighScore.text = value.first.toString()
            sharedPreferences.edit().putInt(Constants.PREF_HIGH_SCORE, value.first).apply()
        }
    }

    private fun gameWon(value: Pair<Int, Int>) = binding.apply {
        logEvent(WIN_GAME, Bundle().apply { putInt("incorrect_answers", value.second) })

        tvTitle.text = getString(R.string.game_won)
        endGame(value)
    }

    private fun gameLost(value: Pair<Int, Int>) = binding.apply {
        logEvent(LOSE_GAME, Bundle().apply {
            putInt("correct_answers", value.first)
            putInt("incorrect_answers", value.second)
        })

        tvTitle.text = getString(R.string.game_over)
        endGame(value)
    }

    private fun logEvent(event: String, data: Bundle?) {
        firebaseAnalytics.logEvent(event, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}