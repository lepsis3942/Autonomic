package com.cjapps.utility.livedata

import androidx.lifecycle.Observer

/**
 * Created by cjgonz on 2019-09-17.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(value: Event<T>) {
        value.getContentIfNotHandled()?.let { onEventUnhandledContent(it) }
    }
}