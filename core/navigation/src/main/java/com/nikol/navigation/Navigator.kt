package com.nikol.navigation

import androidx.navigation3.runtime.NavKey

class Navigator(
    val state: NavigationState
) {
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            if (state.topLevelRoute == route) {
                state.backStacks[route]?.let {
                    if (it.size > 1) {
                        it.subList(1, it.size).clear()
                    }
                }
            } else {
                state.topLevelRoute = route
            }

        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun onBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        if (currentStack.size > 1) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}