package nik.borisov.vmannouncement.presentation.fragments

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
import com.google.android.material.bottomsheet.BottomSheetDialog
import nik.borisov.vmannouncement.R
import nik.borisov.vmannouncement.databinding.FragmentSavedAnnouncementsBinding
import nik.borisov.vmannouncement.databinding.LineTextBottomSheetBinding
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.presentation.adapters.AnnouncementsAdapter
import nik.borisov.vmannouncement.presentation.viewmodels.SavedAnnouncementsViewModel
import nik.borisov.vmannouncement.presentation.viewmodels.states.Announcements
import nik.borisov.vmannouncement.presentation.viewmodels.states.BotError
import nik.borisov.vmannouncement.presentation.viewmodels.states.Line
import nik.borisov.vmannouncement.utils.DataResult

class SavedAnnouncementsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[SavedAnnouncementsViewModel::class.java]
    }

    private var _binding: FragmentSavedAnnouncementsBinding? = null
    private val binding: FragmentSavedAnnouncementsBinding
        get() = _binding ?: throw NullPointerException("FragmentSavedAnnouncementsBinding is null")

    private val bindingLineTextBottomSheetDialog by lazy {
        LineTextBottomSheetBinding.inflate(LayoutInflater.from(context))
    }

    private val lineTextDialog by lazy {
        setupLineTextDialog()
    }

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
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is Announcements -> {}
                is Line -> {
                    when (it.line) {
                        is DataResult.Success -> {
                            if (!it.line.data.isNullOrBlank()) {
                                bindingLineTextBottomSheetDialog.lineTextView.text = it.line.data
                                lineTextDialog.show()
                            } else {
                                Toast.makeText(
                                    context,
                                    getString(R.string.line_not_found),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is DataResult.Error -> {
                            Toast.makeText(context, it.line.message, Toast.LENGTH_SHORT).show()
                        }
                        is DataResult.Loading -> {}
                    }
                }
                is BotError -> {
                    Toast.makeText(context, getString(R.string.bot_error), Toast.LENGTH_LONG).show()
                }
            }
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
        adapter.onAnnouncementClickListener = onAnnouncementItemClick()
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
                        getString(R.string.message_sent),
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun onAnnouncementItemClick(): (AnnouncementItem) -> Unit = {
        viewModel.getLine(it.firstTeam, it.secondTeam, it.time)
        bindingLineTextBottomSheetDialog.announcementTextView.text = it.announcementText
    }

    private fun parseReportIdParam(): Long {
        val arguments = requireArguments()
        if (!arguments.containsKey(ARG_REPORT_ID)) {
            throw IllegalArgumentException("Param report id is absent.")
        }
        return arguments.getLong(ARG_REPORT_ID)
    }

    private fun setupLineTextDialog(): BottomSheetDialog {
        val lineTextDialog = BottomSheetDialog(requireContext())
        lineTextDialog.setContentView(bindingLineTextBottomSheetDialog.root)
        return lineTextDialog
    }

    private fun finishFragment() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {

        private const val ARG_REPORT_ID = "arg_report_id"

        fun newInstance(announcementsReportId: Long) =
            SavedAnnouncementsFragment().apply {
                arguments = bundleOf(
                    ARG_REPORT_ID to announcementsReportId
                )
            }
    }
}