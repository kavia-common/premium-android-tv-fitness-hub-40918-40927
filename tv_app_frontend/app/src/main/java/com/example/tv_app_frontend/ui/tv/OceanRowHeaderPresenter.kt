package com.example.tv_app_frontend.ui.tv

import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import com.example.tv_app_frontend.R

/**
 * PUBLIC_INTERFACE
 * OceanRowHeaderPresenter customizes Leanback row headers:
 * - Uppercase text
 * - Increased letter spacing
 * - Ocean secondary underline indicator for focus
 */
class OceanRowHeaderPresenter : RowHeaderPresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val ctx = parent.context
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                resources.getDimensionPixelSize(R.dimen.header_margin),
                resources.getDimensionPixelSize(R.dimen.header_margin) / 2,
                resources.getDimensionPixelSize(R.dimen.header_margin),
                resources.getDimensionPixelSize(R.dimen.header_margin) / 2
            )
        }

        val title = TextView(ctx).apply {
            id = android.R.id.title
            setTextColor(ContextCompat.getColor(ctx, R.color.ocean_text_dark))
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.header_text_size))
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.06f
            isAllCaps = true
        }

        val underline = TextView(ctx).apply {
            setBackgroundColor(ContextCompat.getColor(ctx, R.color.ocean_secondary))
            val params = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.header_underline_width),
                resources.getDimensionPixelSize(R.dimen.header_underline_height)
            )
            params.topMargin = resources.getDimensionPixelSize(R.dimen.header_underline_top_margin)
            layoutParams = params
            alpha = 0.0f
        }

        container.addView(title)
        container.addView(underline)
        return ViewHolder(container)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        super.onBindViewHolder(viewHolder, item)
        val container = viewHolder.view as LinearLayout
        val title = container.findViewById<TextView>(android.R.id.title)
        val text = item.toString()
        title.text = text.uppercase()
    }

    override fun onSelectLevelChanged(holder: ViewHolder) {
        super.onSelectLevelChanged(holder)
        val container = holder.view as LinearLayout
        val underline = container.getChildAt(1)
        // Emphasize underline on selection
        underline.animate().alpha(if (holder.selectLevel > 0.5f) 1.0f else 0.0f).setDuration(120).start()
    }
}
