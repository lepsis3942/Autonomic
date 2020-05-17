package com.cjapps.utility.extensions

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

/**
 * If Fragment A in a navigation graph needs information from Fragment B, Fragment B can use this to
 * save the data for A
 */
fun <T: Parcelable> Fragment.saveValueToNavBackStack(key: String, value: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)
}

/**
 * If Fragment A in a navigation graph needs information from Fragment B, Fragment A can use this to
 * observe data from B
 */
fun <T: Parcelable> Fragment.observeValueFromNavBackStack(key: String, observer: Observer<T>) {
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)?.observe(viewLifecycleOwner, observer)
}