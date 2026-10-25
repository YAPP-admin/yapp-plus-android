package com.yapp.plus.feature.main.board

import androidx.lifecycle.SavedStateHandle
import com.yapp.plus.feature.main.board.model.BoardCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class BoardViewModelTest {
    @Test
    fun categorySelectionPersistsAndRestores() {
        val savedState = SavedStateHandle()
        val viewModel = BoardViewModel(savedState)

        assertEquals(BoardCategory.All, viewModel.category.value)

        viewModel.selectCategory(BoardCategory.Operation)
        viewModel.selectCategory(BoardCategory.Session)
        viewModel.selectCategory(BoardCategory.Session)

        assertEquals(BoardCategory.Session, viewModel.category.value)

        val restoredValues = savedState.keys().associateWith { savedState.get<Any>(it) }
        val restored = BoardViewModel(SavedStateHandle(restoredValues))

        assertEquals(BoardCategory.Session, restored.category.value)
    }
}
