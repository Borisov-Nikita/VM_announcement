package nik.borisov.vmannouncement.presentation

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import nik.borisov.vmannouncement.R
import nik.borisov.vmannouncement.databinding.ActivityMainBinding
import nik.borisov.vmannouncement.presentation.fragments.SavedReportsFragment
import nik.borisov.vmannouncement.presentation.fragments.SearchAnnouncementsFragment
import nik.borisov.vmannouncement.presentation.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            showFragment(SearchAnnouncementsFragment.newInstance(), "Search")
        }
        setContentView(binding.root)
        setupBottomMenu()
    }

    private fun setupBottomMenu() {
        binding.bottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_search -> {
                    showFragment(SearchAnnouncementsFragment.newInstance(), "Search")
                    true
                }
                R.id.menu_item_saved -> {
                    showFragment(SavedReportsFragment.newInstance(), "Saved")
                    true
                }
                R.id.menu_item_settings -> {
                    showFragment(SettingsFragment.newInstance(), "Settings")
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(instance: Fragment, tag: String) {
        val currentInstance = supportFragmentManager.findFragmentByTag(tag)
        if (currentInstance == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, instance, tag)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, currentInstance, tag)
                .commit()
        }
    }
}