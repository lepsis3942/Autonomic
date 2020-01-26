package com.cjapps.autonomic.livedata

/**
 * Created by cjgonz on 2019-09-17.
 */
class Event<out T>(private val content: T) {
    var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent() = content
}