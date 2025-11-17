package com.example.tv_app_frontend.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.tv_app_frontend.databinding.FragmentWorkoutDetailBinding
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * PUBLIC_INTERFACE
 * WorkoutDetailFragment plays workout video and shows minimal metadata.
 * Adds blurred thumbnail background and TV-friendly typography.
 */
class WorkoutDetailFragment : Fragment() {

    private var _binding: FragmentWorkoutDetailBinding? = null
    private val binding get() = _binding!!
    private val vm: WorkoutViewModel by viewModels()
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = requireArguments().getString(ARG_WORKOUT_ID) ?: return
        vm.loadWorkout(id)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.detail.observe(viewLifecycleOwner) { detail ->
            if (detail != null) {
                binding.title.text = detail.title
                binding.description.text = detail.description ?: ""
                initPlayer(detail.videoUrl)
            }
        }

        // Load blurred background thumbnail when available
        vm.thumbnailUrl.observe(viewLifecycleOwner) { url ->
            if (!url.isNullOrBlank()) {
                Glide.with(requireContext())
                    .load(url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .transform(CenterCrop(), BlurTransformation(18, 4))
                    .into(binding.bgImage)
            }
        }
    }

    private fun initPlayer(url: String) {
        releasePlayer()
        player = ExoPlayer.Builder(requireContext()).build().also { exo ->
            binding.playerView.player = exo
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.prepare()
            exo.playWhenReady = true
        }
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onStop() {
        super.onStop()
        // Save progress in a minimal viable way (SharedPreferences)
        // Future: send to backend
        releasePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_WORKOUT_ID = "arg_workout_id"
    }
}
