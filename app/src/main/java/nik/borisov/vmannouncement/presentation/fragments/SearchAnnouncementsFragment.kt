package nik.borisov.vmannouncement.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import nik.borisov.vmannouncement.R
import nik.borisov.vmannouncement.databinding.DisplaySettingsBottomSheetBinding
import nik.borisov.vmannouncement.databinding.FragmentSearchAnnouncementsBinding
import nik.borisov.vmannouncement.databinding.LineTextBottomSheetBinding
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.presentation.DateForAnnouncements
import nik.borisov.vmannouncement.presentation.SearchAnnouncementSettings
import nik.borisov.vmannouncement.presentation.viewmodels.SearchAnnouncementsViewModel
import nik.borisov.vmannouncement.presentation.adapters.AnnouncementsAdapter
import nik.borisov.vmannouncement.utils.DataResult

class SearchAnnouncementsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[SearchAnnouncementsViewModel::class.java]
    }

    private var _binding: FragmentSearchAnnouncementsBinding? = null
    private val binding: FragmentSearchAnnouncementsBinding
        get() = _binding
            ?: throw NullPointerException("FragmentSearchAnnouncementsBinding is null")

    private val bindingLineTextBottomSheetDialog by lazy {
        LineTextBottomSheetBinding.inflate(LayoutInflater.from(context))
    }

    private val bindingDisplaySettingsBottomSheetDialog by lazy {
        DisplaySettingsBottomSheetBinding.inflate(LayoutInflater.from(context))
    }

    private val lineTextDialog by lazy {
        setupLineTextDialog()
    }

    private val displaySettingsDialog by lazy {
        setupDisplaySettingsDialog()
    }

    private val adapter = AnnouncementsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchAnnouncementsBinding.inflate(inflater, container, false)
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
        viewModel.announcements.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.line.observe(viewLifecycleOwner) {
            when (it) {
                is DataResult.Success -> {
                    if (!it.data.isNullOrBlank()) {
                        bindingLineTextBottomSheetDialog.lineTextView.text = it.data
                        lineTextDialog.show()
                    } else {
                        Toast.makeText(context, "Line didn't found.", Toast.LENGTH_SHORT).show()
                    }
                }
                is DataResult.Error -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is DataResult.Loading -> {}
            }
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
        adapter.onAnnouncementClickListener = onAnnouncementItemClick()
        setupAnnouncementItemSwipeListener(recyclerView)
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
                viewModel.deleteAnnouncementItem(announcementItem)
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun setupClickListeners() {
        binding.topToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.display_settings_menu_item -> {
                    displaySettingsDialog.show()
                    true
                }
                R.id.save_report_menu_item -> {
                    viewModel.saveAnnouncements()
                    Toast.makeText(
                        requireContext(),
                        "Announcements report saved.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    true
                }
                R.id.send_announcements_menu_item -> {
                    viewModel.sendAnnouncementsReport()
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
        with(bindingDisplaySettingsBottomSheetDialog) {
            showButton.setOnClickListener {
                viewModel.updateSearchAnnouncementSettings(
                    SearchAnnouncementSettings(
                        date = if (todayRadioButton.isChecked) {
                            DateForAnnouncements.TODAY
                        } else {
                            DateForAnnouncements.TOMORROW
                        },
                        timeFrom = timeToDisplaySlider.values[0].toInt(),
                        timeTo = timeToDisplaySlider.values[1].toInt()
                    )
                )
                displaySettingsDialog.hide()
            }
        }
    }

    private fun onAnnouncementItemClick(): (AnnouncementItem) -> Unit = {
        viewModel.getLine(it.firstTeam, it.secondTeam, it.time)
        bindingLineTextBottomSheetDialog.announcementTextView.text = it.announcementText
    }

    private fun setupLineTextDialog(): BottomSheetDialog {
        val lineTextDialog = BottomSheetDialog(requireContext())
        lineTextDialog.setContentView(bindingLineTextBottomSheetDialog.root)
        return lineTextDialog
    }

    private fun setupDisplaySettingsDialog(): BottomSheetDialog {
        val displaySettingsDialog = BottomSheetDialog(requireContext())
        displaySettingsDialog.setContentView(bindingDisplaySettingsBottomSheetDialog.root)
        return displaySettingsDialog
    }

    companion object {

        fun newInstance() = SearchAnnouncementsFragment()
    }
}