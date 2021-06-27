package com.edairy.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.edairy.R
import com.edairy.databinding.FragmentMainBinding
import com.edairy.databinding.FragmentSearchBinding
import com.edairy.ui.main.MainViewModel
import com.edairy.utils.AppConstants
import com.edairy.utils.Logger


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding

    private val viewModel: SearchViewModel by viewModels()
    private val adapter by lazy { SearchAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding?.viewmodel = viewModel
        binding?.executePendingBindings()
        binding?.recyclerView?.adapter = adapter
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.searchModelResponse.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                adapter.submitList(it)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}