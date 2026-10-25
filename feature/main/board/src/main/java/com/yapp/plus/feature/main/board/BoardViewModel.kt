package com.yapp.plus.feature.main.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.yapp.plus.feature.main.board.model.BoardCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class BoardViewModel
    @Inject
    constructor(
        private val savedState: SavedStateHandle,
    ) : ViewModel() {
        private val mutableCategory =
            MutableStateFlow(
                BoardCategory.entries.find { it.name == savedState.get<String>(CATEGORY_KEY) }
                    ?: BoardCategory.All,
            )
        val category = mutableCategory.asStateFlow()

        fun selectCategory(value: BoardCategory) {
            if (category.value == value) return
            mutableCategory.value = value
            savedState[CATEGORY_KEY] = value.name
        }

        companion object {
            const val CATEGORY_KEY = "boardCategory"
        }
    }
