package nik.borisov.vmannouncement.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import nik.borisov.vmannouncement.databinding.FragmentTelegramBotSettingsBinding
import nik.borisov.vmannouncement.presentation.viewmodels.TelegramBotSettingsViewModel

class TelegramBotSettingsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[TelegramBotSettingsViewModel::class.java]
    }

    private var _binding: FragmentTelegramBotSettingsBinding? = null
    private val binding: FragmentTelegramBotSettingsBinding
        get() = _binding
            ?: throw NullPointerException("FragmentTelegramBotSettingsBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelegramBotSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.bot.observe(viewLifecycleOwner) {
            binding.botTokenEditText.setText(it.token)
            binding.chatIdEditText.setText(it.chatId)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            finishFragment()
        }
    }

    private fun setupClickListeners() {
        binding.topToolbar.setNavigationOnClickListener {
            finishFragment()
        }
        binding.saveTelegramBotButton.setOnClickListener {
            viewModel.addTelegramBot(
                binding.botTokenEditText.text.toString(),
                binding.chatIdEditText.text.toString()
            )
        }
    }

    private fun finishFragment() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {

        fun newInstance() = TelegramBotSettingsFragment()
    }
}