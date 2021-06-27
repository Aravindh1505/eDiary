package com.edairy.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edairy.R
import com.edairy.databinding.FragmentMainBinding
import com.edairy.utils.AppConstants
import com.edairy.utils.AppConstants.getFormattedValue
import com.edairy.utils.Logger
import com.edairy.utils.internet.Connectivity
import com.edairy.utils.internet.InternetSpeedBuilder
import com.edairy.utils.internet.OnEventInternetSpeedListener
import com.edairy.utils.internet.ProgressionModel
import com.edairy.utils.showToast


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding?.viewModel = viewModel
        binding?.executePendingBindings()

        observeViewModel()
        checkConnectivity()

        binding?.fabSearch?.setOnClickListener {
            AppConstants.MOBILE_NUMBER = viewModel.mobileNumber.get().toString()
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }
    }

    private fun checkConnectivity() {
        val isConnected = Connectivity.isConnected(requireContext())
        val connectionStatus = if (isConnected) "Yes" else "No"
        val status = "Network connected : $connectionStatus"
        viewModel.networkConnected.set(status)

        if (isConnected) {
            checkInternetSpeed()
        }
    }

    private fun checkInternetSpeed() {
        viewModel.timeStamp.set(AppConstants.getCurrentDate())

        val builder = InternetSpeedBuilder(requireActivity())
        builder.setOnEventInternetSpeedListener(object : OnEventInternetSpeedListener {
            override fun onSpeedTest(progressModel: ProgressionModel) {
                val speed = getFormattedValue(progressModel.speed)
                viewModel.speed.set("Internet Speed : $speed")
                viewModel.networkSpeed.set(speed)
            }

            override fun onTestComplete() {
                activity?.runOnUiThread {
                    binding?.imageViewRefresh?.clearAnimation()
                    viewModel.resetRefresh()
                }
            }
        })
        builder.start(AppConstants.URL, 1)
    }

    private fun observeViewModel() {
        viewModel.message.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                Logger.d(it)
                showToast(it)
                viewModel.resetMessage()
            }
        })

        viewModel.refresh.observe(viewLifecycleOwner, {
            if (it) {
                checkInternetSpeed()
                AppConstants.startAnimation(binding?.imageViewRefresh, requireContext())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}