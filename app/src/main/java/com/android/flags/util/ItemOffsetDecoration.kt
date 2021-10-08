package com.android.flags.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemOffsetDecoration(private val itemOffset: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        val column = position % 2

        outRect.left = column * itemOffset / 2
        outRect.right = itemOffset - (column + 1) * itemOffset / 2
        if (position >= 2)
            outRect.top = itemOffset
    }
}