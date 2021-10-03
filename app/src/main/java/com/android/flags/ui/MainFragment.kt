package com.android.flags.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.android.flags.adapters.CountryAdapter
import com.android.flags.databinding.FragmentMainBinding
import com.android.flags.util.Status
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment @Inject constructor(
    private var viewModel: MainViewModel? = null,
    val glide: RequestManager,
    private val countryAdapter: CountryAdapter
) : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

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
        viewModel = viewModel ?: ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        subscribeToObservers()
        setupRecyclerView()

        countryAdapter.setOnItemClickListener { country ->
            //implement showing second screen
        }

        binding.srlFlags.setOnRefreshListener {
            viewModel?.getCountries()
        }
    }

    private fun subscribeToObservers() {
        viewModel?.countries?.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.srlFlags.isRefreshing = false
                        countryAdapter.countries = result.data ?: arrayListOf()
                    }
                    Status.LOADING -> {
                        binding.srlFlags.isRefreshing = true
                    }
                    Status.ERROR -> {
                        binding.srlFlags.isRefreshing = false
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvFlags.apply {
            adapter = countryAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}