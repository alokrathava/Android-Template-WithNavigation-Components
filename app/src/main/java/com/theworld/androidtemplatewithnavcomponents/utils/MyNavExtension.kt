package com.theworld.androidtemplatewithnavcomponents.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import  com.theworld.androidtemplatewithnavcomponents.R


fun NavController.popBackStackAllInstances(destination: Int, inclusive: Boolean): Boolean {
    var popped: Boolean
    while (true) {
        popped = popBackStack(destination, inclusive)
        if (!popped) {
            break
        }
    }
    return popped
}




fun NavController.redirectToDestination(
    destination: Int =  R.id.login_graph,
    popUpTo: Int = R.id.main_graph_xml
) =
    this.apply {
        val navOptions = NavOptions.Builder().setPopUpTo(popUpTo, true).build()
        navigate(destination, null, navOptions)
    }

fun Fragment.getFragmentNavController(@IdRes id: Int) = activity?.let {
    return@let Navigation.findNavController(it, id)
}