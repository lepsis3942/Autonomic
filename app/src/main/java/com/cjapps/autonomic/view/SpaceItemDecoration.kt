package com.cjapps.autonomic.view

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(context: Context, spaceHeight: Float) : RecyclerView.ItemDecoration() {
    private val spaceHeightPx: Int

    init {
        val res = context.resources
        spaceHeightPx =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spaceHeight, res.displayMetrics).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = spaceHeightPx
        }

        outRect.bottom = spaceHeightPx
    }
}