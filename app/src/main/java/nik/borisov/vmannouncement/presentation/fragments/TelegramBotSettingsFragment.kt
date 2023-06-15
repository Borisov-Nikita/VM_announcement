package nik.borisov.vmannouncement.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import nik.borisov.vmannouncement.R
import nik.borisov.vmannouncement.databinding.FragmentTelegramBotSettingsBinding
import nik.borisov.vmannouncement.presentation.MainActivity
import nik.borisov.vmannouncement.presentation.viewmodels.TelegramBotSettingsViewModel
import nik.borisov.vmannouncement.presentation.viewmodels.states.Bot
import nik.borisov.vmannouncement.presentation.viewmodels.states.Error
import nik.borisov.vmannouncement.presentation.viewmodels.states.Finish

class TelegramBotSettingsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            (activity as MainActivity).viewModelFactory
        )[TelegramBotSettingsViewModel::class.java]
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
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is Bot -> {
                    binding.botTokenEditText.setText(it.bot.token)
                    binding.chatIdEditText.setText(it.bot.chatId)
                }
                is Error -> {
                    Toast.makeText(
                        requireContext(),
                        "${getString(R.string.bot_invalid)}\n${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Finish -> {
                    finishFragment()
                }
            }
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