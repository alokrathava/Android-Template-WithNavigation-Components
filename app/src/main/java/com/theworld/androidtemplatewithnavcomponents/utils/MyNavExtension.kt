package com.theworld.androidtemplatewithnavcomponents.utils

import androidx.navigation.NavController
import androidx.navigation.NavOptions
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


fun NavController.redirectToDestination(destination: Int) = this.apply {
    val navOptions = NavOptions.Builder().setPopUpTo(R.id.main_graph_xml, true).build()
    navigate(destination, null, navOptions)
}