package com.theworld.androidtemplatewithnavcomponents.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/*------------------------------- Activity Level ---------------------------*/

fun <T> AppCompatActivity.collectFlow(
        flow: Flow<T>,
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        collect: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(lifecycleState) {
            flow.collect(collect)
        }
    }
}


fun <T> AppCompatActivity.collectLatestFlow(
        flow: Flow<T>,
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        collect: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(lifecycleState) {
            flow.collectLatest(collect)
        }
    }
}

/*------------------------------- Fragment Level ---------------------------*/

fun <T> Fragment.collectFlow(
        flow: Flow<T>,
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        collect: suspend (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(lifecycleState) {
            flow.collect(collect)
        }
    }
}


fun <T> Fragment.collectLatestFlow(
        flow: Flow<T>,
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        collect: suspend (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(lifecycleState) {
            flow.collectLatest(collect)
        }
    }
}

fun <T> Fragment.collectEachFlow(
        flow: Flow<T>,
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        collect: suspend (T) -> Unit
) {

    flow.flowWithLifecycle(lifecycle, lifecycleState)
            .onEach(collect).launchIn(viewLifecycleOwner.lifecycleScope)
}
