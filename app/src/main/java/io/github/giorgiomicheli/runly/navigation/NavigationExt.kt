package io.github.giorgiomicheli.runly.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import timber.log.Timber

/*
    Removes every entry until 'route' is found.
    If inclusive is set to true, then the 'route' itself won't be removed, otherwise it will.
 */
fun NavBackStack<*>.popUpto(route: NavKey, inclusive: Boolean) {
    // Index of the last 'route' found in the backstack
    val index = indexOfLast { it == route }
    if (index == -1) return
    // If inclusive is false then even the route 'route' will be removed
    val cutoff = if (inclusive) index else index + 1
    while(size > cutoff) removeAt(lastIndex)
}

/*
     Turns the current back stack into 'target' while keeping the entries they have in common
     at the beginning. Those entries keep their state and their ViewModels, and no screen is duplicated.
     Example: [RunOverview] + target [RunOverview, ActiveRun] -> only ActiveRun is added.
  */
fun NavBackStack<NavKey>.mergeWith(target: List<NavKey>) {
    val commonPrefix = zip(target).takeWhile { (current, new) -> current == new }.size
    while (size > commonPrefix) removeAt(lastIndex)
    addAll(target.drop(commonPrefix))
}

fun NavBackStack<*>.printCurrentBackstack() {
    var idx = 0
    val s: StringBuilder = StringBuilder()
    s.append("[ ")
    while(size > idx) {
        s.append(get(idx).toString() + " - ")
        idx++
    }
    s.append(" ]")
    Timber.d(s.toString())
}