package com.example.tv_app_frontend.ui.tv

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.tv_app_frontend.R
import com.example.tv_app_frontend.data.remote.CategoryItem
import com.example.tv_app_frontend.data.remote.WorkoutItem
import com.example.tv_app_frontend.ui.home.HomeState
import com.example.tv_app_frontend.ui.home.HomeViewModel
import com.example.tv_app_frontend.ui.workout.WorkoutDetailFragment

/**
 * PUBLIC_INTERFACE
 * HomeBrowseFragment shows favorites, recent, continue, and categories in Leanback rows.
 */
class HomeBrowseFragment : BrowseSupportFragment() {

    private val vm: HomeViewModel by viewModels()
    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title = resources.getString(R.string.app_name)
        brandColor = ContextCompat.getColor(requireContext(), R.color.ocean_primary)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter

        setupListeners()
        vm.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeState.Loading -> showLoading()
                is HomeState.Error -> showError(state.message)
                is HomeState.Data -> bindHome(state)
            }
        }
        vm.load()
    }

    private fun showLoading() {
        // Simple placeholder, could add spinner row
    }

    private fun showError(message: String) {
        // Could add an error row
    }

    private fun bindHome(data: HomeState.Data) {
        rowsAdapter.clear()
        addWorkoutListRow("Continue Watching", data.home.continueWatching)
        addWorkoutListRow("Favorites", data.home.favorites)
        addWorkoutListRow("Recent", data.home.recent)
        addCategoryRow("Categories", data.home.categories)
    }

    private fun addWorkoutListRow(header: String, items: List<WorkoutItem>) {
        if (items.isEmpty()) return
        val presenter = WorkoutCardPresenter()
        val adapter = ArrayObjectAdapter(presenter)
        items.forEach { adapter.add(it) }
        rowsAdapter.add(ListRow(HeaderItem(header), adapter))
    }

    private fun addCategoryRow(header: String, categories: List<CategoryItem>) {
        if (categories.isEmpty()) return
        val presenter = CategoryCardPresenter()
        val adapter = ArrayObjectAdapter(presenter)
        categories.forEach { adapter.add(it) }
        rowsAdapter.add(ListRow(HeaderItem(header), adapter))
    }

    private fun setupListeners() {
        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                when (item) {
                    is WorkoutItem -> openWorkout(item)
                    is CategoryItem -> openCategory(item)
                }
            }
    }

    private fun openWorkout(item: WorkoutItem) {
        val f = WorkoutDetailFragment()
        f.arguments = bundleOf(WorkoutDetailFragment.ARG_WORKOUT_ID to item.id)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, f)
            .addToBackStack(null)
            .commit()
    }

    private fun openCategory(item: CategoryItem) {
        val f = WorkoutBrowseFragment.newInstance(category = item.name)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, f)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        // PUBLIC_INTERFACE
        fun newInstance(): HomeBrowseFragment {
            /** Create a new instance of HomeBrowseFragment. */
            return HomeBrowseFragment()
        }
    }
}

class WorkoutCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
        val card = ImageCardView(parent.context).apply {
            setMainImageDimensions(320, 180)
            infoVisibility = ImageCardView.CARD_REGION_VISIBLE_ALWAYS
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val workout = item as WorkoutItem
        val card = viewHolder.view as ImageCardView
        card.titleText = workout.title
        card.contentText = "${(workout.durationSec / 60)} min • ${workout.level ?: ""}"
        Glide.with(card.context)
            .load(workout.thumbnailUrl)
            .fallback(R.drawable.ic_launcher)
            .into(card.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}

class CategoryCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
        val card = ImageCardView(parent.context).apply {
            setMainImageDimensions(320, 180)
            infoVisibility = ImageCardView.CARD_REGION_VISIBLE_ALWAYS
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val category = item as CategoryItem
        val card = viewHolder.view as ImageCardView
        card.titleText = category.name
        Glide.with(card.context)
            .load(category.heroImageUrl)
            .fallback(R.drawable.ic_launcher)
            .into(card.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
