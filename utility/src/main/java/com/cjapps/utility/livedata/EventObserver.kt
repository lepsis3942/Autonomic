package com.cjapps.utility.livedata

import androidx.lifecycle.Observer

/**
 * Created by cjgonz on 2019-09-17.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { onEventUnhandledContent(it) }
    }
}