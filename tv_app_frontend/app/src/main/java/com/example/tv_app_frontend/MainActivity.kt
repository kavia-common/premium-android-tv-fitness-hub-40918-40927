package com.example.tv_app_frontend

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.example.tv_app_frontend.databinding.ActivityMainBinding
import com.example.tv_app_frontend.ui.tv.HomeBrowseFragment

/**
 * PUBLIC_INTERFACE
 * MainActivity is the entry point for the Android TV app. It hosts the Leanback
 * HomeBrowseFragment in a full-screen container.
 */
class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.container, HomeBrowseFragment.newInstance())
            }
        }
    }
}
