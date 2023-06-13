package nik.borisov.vmannouncement.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nik.borisov.vmannouncement.R
import nik.borisov.vmannouncement.databinding.FragmentSavedAnnouncementsBinding

class SavedAnnouncementsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[SavedAnnouncementsViewModel::class.java]
    }

    private var _binding: FragmentSavedAnnouncementsBinding? = null
    private val binding: FragmentSavedAnnouncementsBinding
        get() = _binding ?: throw NullPointerException("FragmentSavedAnnouncementsBinding is null")

    private val adapter = AnnouncementsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedAnnouncementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupRecyclerView()
        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.getAnnouncements(parseReportIdParam()).observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.telegramBotError.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.announcementsRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        setupAnnouncementItemSwipeListener(recyclerView)
    }

    private fun setupClickListeners() {
        binding.topToolbar.setNavigationOnClickListener {
            finishFragment()
        }
        binding.topToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.send_announcements_menu_item -> {
                    viewModel.sendAnnouncementsReport(adapter.currentList)
                    Toast.makeText(
                        requireContext(),
                        "Announcements report message sent.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupAnnouncementItemSwipeListener(recyclerView: RecyclerView) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val announcementItem = adapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteAnnouncement(announcementItem.id)
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun parseReportIdParam(): Long {
        val arguments = requireArguments()
        if (!arguments.containsKey(ARG_REPORT_ID)) {
            throw IllegalArgumentException("Param report id is absent.")
        }
        return arguments.getLong(ARG_REPORT_ID)
    }

    private fun finishFragment() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {

        private const val ARG_REPORT_ID = "arf_report_id"

        fun newInstance(announcementsReportId: Long) =
            SavedAnnouncementsFragment().apply {
                arguments = bundleOf(
                    ARG_REPORT_ID to announcementsReportId
                )
            }
    }
}