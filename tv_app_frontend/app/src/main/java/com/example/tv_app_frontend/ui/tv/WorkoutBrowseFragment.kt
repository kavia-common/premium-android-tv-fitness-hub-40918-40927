package com.example.tv_app_frontend.ui.tv

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tv_app_frontend.R
import com.example.tv_app_frontend.data.remote.WorkoutItem
import com.example.tv_app_frontend.ui.browse.BrowseViewModel
import com.example.tv_app_frontend.ui.workout.WorkoutDetailFragment

/**
 * PUBLIC_INTERFACE
 * WorkoutBrowseFragment displays workouts for a category with Ocean spacing/style.
 */
class WorkoutBrowseFragment : RowsSupportFragment() {

    private val vm: BrowseViewModel by viewModels()
    private lateinit var rowAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = arguments?.getString(ARG_CATEGORY)
        vm.setFilters(category, null, null, null)

        val listRowPresenter = ListRowPresenter().apply {
            setShadowEnabled(true)
            setKeepChildForeground(true)
        }
        rowAdapter = ArrayObjectAdapter(listRowPresenter)
        adapter = rowAdapter

        vm.items.observe(this) { items ->
            rowAdapter.clear()
            val cardPresenter = WorkoutCardPresenter()
            val listAdapter = ArrayObjectAdapter(cardPresenter)
            items.forEach { listAdapter.add(it) }
            val headerTitle = category ?: getString(R.string.section_categories)
            rowAdapter.add(ListRow(HeaderItem(headerTitle), listAdapter))
        }

        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is WorkoutItem) {
                val f = WorkoutDetailFragment()
                f.arguments = bundleOf(WorkoutDetailFragment.ARG_WORKOUT_ID to item.id)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, f)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Prefetch neighboring items and apply spacing when selection changes for smooth UX
        setOnItemViewSelectedListener { _, item, rowViewHolder, _ ->
            if (rowViewHolder is ListRowPresenter.ViewHolder) {
                val grid = rowViewHolder.gridView
                val hSpace = resources.getDimensionPixelSize(R.dimen.card_spacing_horizontal)
                val vSpace = resources.getDimensionPixelSize(R.dimen.row_spacing_vertical)
                grid.setItemSpacing(hSpace)
                val padStart = grid.paddingStart
                val padEnd = grid.paddingEnd
                grid.setPaddingRelative(padStart, vSpace / 2, padEnd, vSpace / 2)
            }

            if (item == null || rowViewHolder !is ListRowPresenter.ViewHolder) return@setOnItemViewSelectedListener
            val row = rowViewHolder.row
            if (row !is ListRow) return@setOnItemViewSelectedListener
            val objectAdapter = row.adapter as? ArrayObjectAdapter ?: return@setOnItemViewSelectedListener
            val index = (0 until objectAdapter.size()).firstOrNull { i -> objectAdapter.get(i) == item } ?: return@setOnItemViewSelectedListener
            val context = context ?: return@setOnItemViewSelectedListener

            for (offset in 1..3) {
                val next = if (index + offset < objectAdapter.size()) objectAdapter.get(index + offset) else null
                val url = (next as? WorkoutItem)?.thumbnailUrl
                if (!url.isNullOrBlank()) {
                    Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).preload()
                }
            }
        }
    }

    companion object {
        const val ARG_CATEGORY = "arg_category"

        // PUBLIC_INTERFACE
        fun newInstance(category: String? = null): WorkoutBrowseFragment {
            /** Create instance with given category. */
            val f = WorkoutBrowseFragment()
            f.arguments = bundleOf(ARG_CATEGORY to category)
            return f
        }
    }
}
