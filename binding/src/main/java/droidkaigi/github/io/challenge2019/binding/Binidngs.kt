package droidkaigi.github.io.challenge2019.platform.binding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.bindRefreshing(isRefreshing: Boolean) {
    this.isRefreshing = isRefreshing
}

//@BindingAdapter("isRefreshingChanged")
//fun SwipeRefreshLayout.bindRefreshingChanged(listener: InverseBindingListener?) {
//    this.setOnRefreshListener { listener?.onChange() }
//}
//
//@InverseBindingAdapter(attribute = "isRefreshing", event = "isRefreshingChanged")
//fun SwipeRefreshLayout.bindRefreshing(): Boolean {
//    return this.isRefreshing
//}
