package com.nagarro.kotlinfundamentals.bindings

import android.view.View
import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout

@BindingAdapter("visibility")
fun showHideView(view: View, isShow: Boolean) {
    view.visibility = if (isShow) {
        View.VISIBLE
    } else View.GONE
}

@BindingAdapter("shimmer")
fun setShimmer(view: ShimmerFrameLayout, isShimmer: Boolean) {
    if (isShimmer) view.startShimmer()
    else view.stopShimmer()
}