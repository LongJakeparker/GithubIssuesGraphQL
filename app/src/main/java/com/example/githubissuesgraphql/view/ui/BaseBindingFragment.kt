package com.example.githubissuesgraphql.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Inherits Fragment and supports fast generic-binding
 * @author longtran
 * @since 08/06/2021
 * @param T
 */
@ExperimentalCoroutinesApi
abstract class BaseBindingFragment<T : ViewBinding> : Fragment() {
    protected lateinit var binding: T

    abstract val inflaterMethod: (LayoutInflater, ViewGroup?, Boolean) -> T

    /**
     * Function return a dynamic title which can be override
     * @return String
     */
    open fun getDynamicTitle(): String? {
        return null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Sets the toolbar title in MainActivity
        // if the dynamic title is null then get the title is defined in nav file
        // have to do this because can not get the {dynamic} title in nav file, only the fixed title
        if (requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).setCustomTitle(
                getDynamicTitle() ?: findNavController().currentDestination?.label?.toString()
            )
        }
        binding = inflaterMethod.invoke(inflater, container, false)
        onBindingReady()
        return binding.root
    }

    /**
     * Invoke binding object after object is created, before returning binding.root.
     */
    abstract fun onBindingReady()

    abstract fun fetchData()
}