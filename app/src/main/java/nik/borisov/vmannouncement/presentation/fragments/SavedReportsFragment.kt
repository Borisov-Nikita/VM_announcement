package nik.borisov.vmannouncement.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nik.borisov.vmannouncement.R
import nik.borisov.vmannouncement.databinding.FragmentSavedReportsBinding
import nik.borisov.vmannouncement.presentation.adapters.ReportsAdapter
import nik.borisov.vmannouncement.presentation.viewmodels.SavedReportsViewModel

class SavedReportsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[SavedReportsViewModel::class.java]
    }

    private var _binding: FragmentSavedReportsBinding? = null
    private val binding: FragmentSavedReportsBinding
        get() = _binding
            ?: throw NullPointerException("FragmentSavedReportsBinding is null")

    private val adapter = ReportsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.getAnnouncementReports().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.reportsRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        adapter.onReportClickListener = onAnnouncementReportClick()
        setupAnnouncementReportSwipeListener(recyclerView)
    }

    private fun onAnnouncementReportClick(): (Long) -> Unit = {
        val instance = SavedAnnouncementsFragment.newInstance(it)
        showFragment(instance)
    }

    private fun setupAnnouncementReportSwipeListener(recyclerView: RecyclerView) {
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
                val announcementReport = adapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteAnnouncementReport(announcementReport.id)
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun showFragment(instance: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.add(R.id.fragment_container, instance)
            ?.addToBackStack(null)
            ?.commit()
    }


    companion object {

        fun newInstance() = SavedReportsFragment()
    }
}