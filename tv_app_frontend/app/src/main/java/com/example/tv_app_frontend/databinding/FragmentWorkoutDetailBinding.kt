package com.example.tv_app_frontend.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.media3.ui.PlayerView
import com.example.tv_app_frontend.R

/**
 * ViewBinding for fragment_workout_detail.xml (generated manually for this exercise environment).
 */
class FragmentWorkoutDetailBinding private constructor(
    val root: View,
    val playerView: PlayerView,
    val title: TextView,
    val description: TextView
) {
    companion object {
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean = false): FragmentWorkoutDetailBinding {
            val view = inflater.inflate(R.layout.fragment_workout_detail, parent, attachToParent)
            return bind(view)
        }

        fun bind(view: View): FragmentWorkoutDetailBinding {
            val player = view.findViewById<PlayerView>(R.id.player_view)
            val title = view.findViewById<TextView>(R.id.title)
            val desc = view.findViewById<TextView>(R.id.description)
            return FragmentWorkoutDetailBinding(view, player, title, desc)
        }
    }
}
