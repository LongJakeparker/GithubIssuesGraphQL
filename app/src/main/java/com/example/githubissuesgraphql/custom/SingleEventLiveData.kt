package com.example.githubissuesgraphql.custom

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Inherits MutableLiveData to prevents push current data whenever there is a new observer
 * @author longtran
 * @since 06/06/2021
 */
class SingleEventLiveData<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(
            owner,
            {
                if (pending.compareAndSet(true, false)) observer.onChanged(it)
            }
        )
    }

    override fun setValue(value: T) {
        pending.set(true)
        super.setValue(value)
    }
}