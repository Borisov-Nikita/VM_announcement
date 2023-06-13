package nik.borisov.vmannouncement.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import nik.borisov.vmannouncement.R
import nik.borisov.vmannouncement.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding
            ?: throw NullPointerException("FragmentSettingsBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.setupTelegramBotButton.setOnClickListener {
            val instance = TelegramBotSettingsFragment.newInstance()
            showFragment(instance)
        }
    }

    private fun showFragment(instance: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.add(R.id.fragment_container, instance)
            ?.addToBackStack(null)
            ?.commit()
    }

    companion object {

        fun newInstance() = SettingsFragment()
    }
}