package com.android.flags.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.flags.R
import com.android.flags.databinding.FragmentMainBinding
import com.android.flags.util.Status
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: QuizViewModel
    private val countryAdapter = QuizAdapter()

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
        viewModel =  ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)

        subscribeToObservers()
        setupRecyclerView()

        countryAdapter.setOnItemClickListener { country ->
            viewModel.answer(country)
        }

        binding.btnPlay.setOnClickListener {
            binding.btnPlay.visibility = View.INVISIBLE
            binding.tvInstructions.visibility = View.GONE
            binding.tvWelcome.visibility = View.GONE
            binding.rvAnswers.visibility = View.VISIBLE
            viewModel.play()
        }
    }

    private fun subscribeToObservers() {
        viewModel.countries.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        val questions = result.data ?: arrayListOf()
                        countryAdapter.countries = questions
                        questions.forEach {
                            if (it.correct == true)
                                Glide.with(requireContext()).load(it.flag).into(binding.ivMain)
                        }
                    }
                    Status.LOADING -> {
                    }
                    Status.SERVER_ERROR, Status.INTERNAL_ERROR -> {
                        binding.btnPlay.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "Error happen", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        viewModel.time.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                binding.tvTimer.text = it.toString()
            }
        })

        viewModel.answers.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandled()?.let {
                binding.progress.progress = it
                binding.tvProgress.text = String.format(getString(R.string.question_counter), it)
            }
        })

        viewModel.result.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                binding.btnPlay.visibility = View.VISIBLE
                binding.tvInstructions.visibility = View.VISIBLE
                binding.tvWelcome.visibility = View.VISIBLE
                binding.ivMain.setImageResource(R.drawable.proffesor)
                binding.rvAnswers.visibility = View.GONE
                countryAdapter.countries = arrayListOf()

                binding.tvWelcome.text = "Game Over!"
                binding.tvInstructions.text = "Your score is:${it.first} correct answers and ${it.second} incorrect answers!"
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvAnswers.apply {
            adapter = countryAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}