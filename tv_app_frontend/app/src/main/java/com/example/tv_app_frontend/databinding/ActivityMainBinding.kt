package com.example.tv_app_frontend.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tv_app_frontend.R

/**
 * Simple ViewBinding for activity_main.xml container layout.
 */
class ActivityMainBinding private constructor(
    val root: View
) {
    companion object {
        fun inflate(inflater: LayoutInflater): ActivityMainBinding {
            val view = inflater.inflate(R.layout.activity_main, null, false)
            return ActivityMainBinding(view)
        }
    }
}
