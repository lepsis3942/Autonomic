package com.cjapps.utility.livedata

/**
 * Created by cjgonz on 2019-09-17.
 */
class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

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