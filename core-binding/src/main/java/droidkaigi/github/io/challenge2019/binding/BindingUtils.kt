package droidkaigi.github.io.challenge2019.binding

import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun <T : ViewDataBinding> ComponentActivity.bindLayout(layoutId: Int): T = DataBindingUtil.setContentView(this, layoutId)
