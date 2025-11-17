package com.example.tv_app_frontend.ui.tv

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
        // Ocean Professional brand color for Leanback title.
        setBrandColor(ContextCompat.getColor(requireContext(), R.color.ocean_primary))
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
        // Could add a spinner row if desired.
    }

    private fun showError(@Suppress("UNUSED_PARAMETER") message: String) {
        // Could add an error row.
    }

    private fun bindHome(data: HomeState.Data) {
        rowsAdapter.clear()
        addWorkoutListRow(getString(R.string.section_continue), data.home.continueWatching)
        addWorkoutListRow(getString(R.string.section_favorites), data.home.favorites)
        addWorkoutListRow(getString(R.string.section_recent), data.home.recent)
        addCategoryRow(getString(R.string.section_categories), data.home.categories)
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

private const val CARD_WIDTH = 400
private const val CARD_HEIGHT = 225
private const val FOCUSED_SCALE = 1.08f

class WorkoutCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
        val card = ImageCardView(parent.context).apply {
            setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
            infoVisibility = ImageCardView.CARD_REGION_VISIBLE_ALWAYS
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(ContextCompat.getColor(context, R.color.card_surface))
        }.also { card ->
            card.setOnFocusChangeListener { v, hasFocus ->
                val scale = if (hasFocus) FOCUSED_SCALE else 1f
                v.scaleX = scale
                v.scaleY = scale
                card.isSelected = hasFocus
            }
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val workout = item as WorkoutItem
        val card = viewHolder.view as ImageCardView
        card.titleText = workout.title

        val mins = (workout.durationSec / 60).coerceAtLeast(0)
        val level = workout.level ?: ""
        card.contentText = card.context.getString(R.string.duration_level_format, mins, level)

        // Set a placeholder immediately to avoid flicker while Glide loads
        card.mainImageView.setImageResource(R.drawable.ic_launcher)

        Glide.with(card.context)
            .load(workout.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_launcher)
            .error(R.drawable.ic_launcher)
            .fallback(R.drawable.ic_launcher)
            .into(card.mainImageView)
    }

    override fun onUnbindViewHolder(@Suppress("UNUSED_PARAMETER") viewHolder: ViewHolder) {
        // no-op
    }
}

class CategoryCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
        val card = ImageCardView(parent.context).apply {
            setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
            infoVisibility = ImageCardView.CARD_REGION_VISIBLE_ALWAYS
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(ContextCompat.getColor(context, R.color.card_surface))
        }.also { card ->
            card.setOnFocusChangeListener { v, hasFocus ->
                val scale = if (hasFocus) FOCUSED_SCALE else 1f
                v.scaleX = scale
                v.scaleY = scale
                card.isSelected = hasFocus
            }
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val category = item as CategoryItem
        val card = viewHolder.view as ImageCardView
        card.titleText = category.name

        // Set immediate placeholder
        card.mainImageView.setImageResource(R.drawable.ic_launcher)

        Glide.with(card.context)
            .load(category.heroImageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_launcher)
            .error(R.drawable.ic_launcher)
            .fallback(R.drawable.ic_launcher)
            .into(card.mainImageView)
    }

    override fun onUnbindViewHolder(@Suppress("UNUSED_PARAMETER") viewHolder: ViewHolder) {
        // no-op
    }
}
