package com.example.tv_app_frontend.ui.tv

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import com.example.tv_app_frontend.data.remote.WorkoutItem
import com.example.tv_app_frontend.ui.browse.BrowseViewModel
import com.example.tv_app_frontend.ui.workout.WorkoutDetailFragment

/**
 * PUBLIC_INTERFACE
 * WorkoutBrowseFragment displays a single row/grid of workouts for a category with filters.
 */
class WorkoutBrowseFragment : RowsSupportFragment() {

    private val vm: BrowseViewModel by viewModels()
    private lateinit var rowAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = arguments?.getString(ARG_CATEGORY)
        vm.setFilters(category, null, null, null)

        rowAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowAdapter

        vm.items.observe(this) { items ->
            rowAdapter.clear()
            val cardPresenter = WorkoutCardPresenter()
            val listAdapter = ArrayObjectAdapter(cardPresenter)
            items.forEach { listAdapter.add(it) }
            rowAdapter.add(ListRow(HeaderItem(category ?: "Workouts"), listAdapter))
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
    }

    companion object {
        const val ARG_CATEGORY = "arg_category"

        // PUBLIC_INTERFACE
        fun newInstance(category: String?): WorkoutBrowseFragment {
            /** Create instance with given category. */
            val f = WorkoutBrowseFragment()
            f.arguments = bundleOf(ARG_CATEGORY to category)
            return f
        }
    }
}
