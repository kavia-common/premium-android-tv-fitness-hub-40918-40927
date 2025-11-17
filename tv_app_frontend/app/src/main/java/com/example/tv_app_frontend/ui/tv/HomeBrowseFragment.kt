package com.example.tv_app_frontend.ui.tv

import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.tv_app_frontend.R
import com.example.tv_app_frontend.data.remote.CategoryItem
import com.example.tv_app_frontend.data.remote.WorkoutItem
import com.example.tv_app_frontend.ui.home.HomeState
import com.example.tv_app_frontend.ui.home.HomeViewModel

/**
 * PUBLIC_INTERFACE
 * HomeBrowseFragment shows favorites, recent, continue, and categories in Leanback rows.
 * It applies Ocean Professional theme accents, custom header styling, spacing,
 * and focus behaviors (zoom + elevation).
 */
class HomeBrowseFragment : BrowseSupportFragment() {

    private val vm: HomeViewModel by viewModels()
    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title = resources.getString(R.string.app_name)

        // Ocean Professional brand color for Leanback title/brand bar
        setBrandColor(ContextCompat.getColor(requireContext(), R.color.ocean_primary))
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // Use custom header presenter
        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(item: Any?): Presenter {
                return OceanRowHeaderPresenter()
            }
        })

        // Standard ListRowPresenter; spacing will be applied on selection to each row grid
        val listRowPresenter = ListRowPresenter().apply {
            setShadowEnabled(true)
            setKeepChildForeground(true)
        }
        rowsAdapter = ArrayObjectAdapter(listRowPresenter)
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
        // Optionally show a loading row; keep minimal for demo
    }

    private fun showError(@Suppress("UNUSED_PARAMETER") message: String) {
        // Optionally show an error row; keep minimal for demo
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

        // Apply grid spacing when a row becomes active and preload neighbor thumbnails for smoother transitions
        onItemViewSelectedListener =
            OnItemViewSelectedListener { _, item, rowViewHolder, _ ->
                if (rowViewHolder is ListRowPresenter.ViewHolder) {
                    val grid = rowViewHolder.gridView
                    val hSpace = resources.getDimensionPixelSize(R.dimen.card_spacing_horizontal)
                    val vSpace = resources.getDimensionPixelSize(R.dimen.row_spacing_vertical)
                    grid.setItemSpacing(hSpace)
                    val padStart = grid.paddingStart
                    val padEnd = grid.paddingEnd
                    grid.setPaddingRelative(padStart, vSpace / 2, padEnd, vSpace / 2)
                }

                if (item == null || rowViewHolder !is ListRowPresenter.ViewHolder) return@OnItemViewSelectedListener
                val row = rowViewHolder.row
                if (row !is ListRow) return@OnItemViewSelectedListener
                val objectAdapter = row.adapter as? ArrayObjectAdapter ?: return@OnItemViewSelectedListener
                val index = (0 until objectAdapter.size()).firstOrNull { i -> objectAdapter.get(i) == item } ?: return@OnItemViewSelectedListener
                val context = context ?: return@OnItemViewSelectedListener

                // Preload next few items
                for (offset in 1..3) {
                    val nextIndex = index + offset
                    if (nextIndex >= objectAdapter.size()) continue
                    val nextObj = objectAdapter.get(nextIndex)
                    val url: String? = when (nextObj) {
                        is WorkoutItem -> nextObj.thumbnailUrl
                        is CategoryItem -> nextObj.heroImageUrl
                        else -> null
                    }
                    if (!url.isNullOrBlank()) {
                        Glide.with(context)
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .preload()
                    }
                }
            }
    }

    private fun openWorkout(item: WorkoutItem) {
        val f = com.example.tv_app_frontend.ui.workout.WorkoutDetailFragment()
        f.arguments = bundleOf(com.example.tv_app_frontend.ui.workout.WorkoutDetailFragment.ARG_WORKOUT_ID to item.id)
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

// Card constants via dimens
private fun View.cardWidthPx(): Int = resources.getDimensionPixelSize(R.dimen.card_width)
private fun View.cardHeightPx(): Int = resources.getDimensionPixelSize(R.dimen.card_height)
private fun View.cardCornerRadiusPx(): Int = resources.getDimensionPixelSize(R.dimen.card_corner_radius)

private const val FOCUSED_SCALE = 1.08f

/**
 * Presenter for workout cards with precise dimensions,
 * rounded corners, focus zoom, and elevation animator.
 */
class WorkoutCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val context = parent.context
        val card = ImageCardView(context).apply {
            setMainImageDimensions(
                context.resources.getDimensionPixelSize(R.dimen.card_width),
                context.resources.getDimensionPixelSize(R.dimen.card_height)
            )
            infoVisibility = ImageCardView.CARD_REGION_VISIBLE_ALWAYS
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(ContextCompat.getColor(context, R.color.card_surface))

            // Card background with stroke and radius
            mainImageView.background = ContextCompat.getDrawable(context, R.drawable.card_bg)
            mainImageView.clipToOutline = true

            // Elevation animator based on focus/selection state
            stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.card_elevation_state_list)
        }

        // Scale on focus to achieve 1.08x
        card.setOnFocusChangeListener { v, hasFocus ->
            val scale = if (hasFocus) FOCUSED_SCALE else 1f
            v.animate().scaleX(scale).scaleY(scale).setDuration(120).start()
            card.isSelected = hasFocus
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val workout = item as WorkoutItem
        val card = viewHolder.view as ImageCardView

        // Ellipsize handled by internal layout; set texts
        card.titleText = workout.title
        val mins = (workout.durationSec / 60).coerceAtLeast(0)
        val level = workout.level ?: ""
        card.contentText = card.context.getString(R.string.duration_level_format, mins, level)

        // Placeholder to avoid flicker while Glide loads
        card.mainImageView.setImageResource(R.drawable.ic_launcher)

        // Load with rounded corners and crossfade
        val radius = card.cardCornerRadiusPx()
        Glide.with(card.context)
            .load(workout.thumbnailUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .transform(CenterCrop(), jp.wasabeef.glide.transformations.RoundedCornersTransformation(radius, 0))
            .placeholder(R.drawable.ic_launcher)
            .error(R.drawable.ic_launcher)
            .fallback(R.drawable.ic_launcher)
            .into(card.mainImageView)
    }

    override fun onUnbindViewHolder(@Suppress("UNUSED_PARAMETER") viewHolder: ViewHolder) {
        // no-op
    }
}

/**
 * Presenter for category cards with same sizing/behavior as workout cards.
 */
class CategoryCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val context = parent.context
        val card = ImageCardView(context).apply {
            setMainImageDimensions(
                context.resources.getDimensionPixelSize(R.dimen.card_width),
                context.resources.getDimensionPixelSize(R.dimen.card_height)
            )
            infoVisibility = ImageCardView.CARD_REGION_VISIBLE_ALWAYS
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(ContextCompat.getColor(context, R.color.card_surface))

            mainImageView.background = ContextCompat.getDrawable(context, R.drawable.card_bg)
            mainImageView.clipToOutline = true

            stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.card_elevation_state_list)
        }

        card.setOnFocusChangeListener { v, hasFocus ->
            val scale = if (hasFocus) FOCUSED_SCALE else 1f
            v.animate().scaleX(scale).scaleY(scale).setDuration(120).start()
            card.isSelected = hasFocus
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val category = item as CategoryItem
        val card = viewHolder.view as ImageCardView
        card.titleText = category.name

        card.mainImageView.setImageResource(R.drawable.ic_launcher)

        val radius = card.cardCornerRadiusPx()
        Glide.with(card.context)
            .load(category.heroImageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .transform(CenterCrop(), jp.wasabeef.glide.transformations.RoundedCornersTransformation(radius, 0))
            .placeholder(R.drawable.ic_launcher)
            .error(R.drawable.ic_launcher)
            .fallback(R.drawable.ic_launcher)
            .into(card.mainImageView)
    }

    override fun onUnbindViewHolder(@Suppress("UNUSED_PARAMETER") viewHolder: ViewHolder) {
        // no-op
    }
}
