package com.nikol.lms_impl.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikol.lms_impl.viewModels.CoursesRouter
import com.nikol.lms_impl.viewModels.CoursesViewModel
import com.nikol.viewmodel.daggerViewModel

@Composable
fun CoursesScreen() {
    val viewModel = daggerViewModel<CoursesViewModel, CoursesRouter> {
        object : CoursesRouter {
            override fun toCourse(id: Int) {
                TODO("Not yet implemented")
            }

            override fun toAuth() {
                TODO("Not yet implemented")
            }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = state.courses,
            key = { it.id }
        ) {
            Text(it.name)
        }
    }
}