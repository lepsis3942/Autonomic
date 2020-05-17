package com.cjapps.utility.viewbinding

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by cjgonz on 4/11/20.
 */
fun <T> Fragment.viewBindingLifecycle(initialize: () -> T): ReadOnlyProperty<Fragment, T>
        = object: ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {
    private var binding: T? = null
    private var viewLifecycleOwner: LifecycleOwner? = null

    init {
        this@viewBindingLifecycle
            .viewLifecycleOwnerLiveData
            .observe(this@viewBindingLifecycle, Observer { newLifeCycleOwner ->
                viewLifecycleOwner?.lifecycle?.removeObserver(this)
                viewLifecycleOwner = newLifeCycleOwner.also { it.lifecycle.addObserver(this) }
            })
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        binding = null
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return this.binding ?: initialize().also { this.binding = it }
    }
}