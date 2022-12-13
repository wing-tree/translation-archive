package com.wing.tree.bruni.inPlaceTranslate.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding> : Fragment() {
    abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): VB

    private var _viewBinding: VB? = null
    protected val viewBinding: VB get() = _viewBinding!!

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return with(inflate(inflater, container)) {
            _viewBinding = this

            root
        }
    }

    @CallSuper
    override fun onDestroyView() {
        _viewBinding = null

        super.onDestroyView()
    }

    protected fun navigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }
}