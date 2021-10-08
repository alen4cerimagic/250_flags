package com.android.flags.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.flags.databinding.FragmentMainBinding
import com.android.flags.util.ItemOffsetDecoration
import com.android.flags.util.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
            binding.btnPlay.visibility = View.GONE
            viewModel.play()
        }

        viewModel.greetUser()
    }

    private fun subscribeToObservers() {
        viewModel.countries.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        countryAdapter.countries = result.data ?: arrayListOf()
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnPlay.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        viewModel.message.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.INITIAL -> {
                        binding.tvMessage.text = result.data
                    }
                    Status.SUCCESS -> {
                        binding.btnPlay.apply {
                            visibility = View.VISIBLE
                            text = "Next question"
                        }
                        binding.tvMessage.text = result.data
                    }
                    Status.ERROR -> {
                        binding.btnPlay.apply {
                            visibility = View.VISIBLE
                            text = "Start again"
                        }
                        binding.tvMessage.text = result.message
                    }
                }
            }
        })

        viewModel.answersCount.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandled()?.let {
                binding.lpiProgress.progress = it
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvFlags.apply {
            adapter = countryAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}