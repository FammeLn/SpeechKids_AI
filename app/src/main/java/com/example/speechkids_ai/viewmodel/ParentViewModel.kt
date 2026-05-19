package com.example.speechkids_ai.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.speechkids_ai.model.Child

class ParentViewModel : ViewModel() {
    private val _children = MutableLiveData<List<Child>>(emptyList())
    val children: LiveData<List<Child>> = _children

    init {
        // Load mock data
        loadChildren()
    }

    private fun loadChildren() {
        _children.value = listOf(
            Child(
                id = "child_1",
                name = "Маша",
                age = 5,
                progress = 65,
                streak = 7
            ),
            Child(
                id = "child_2",
                name = "Ваня",
                age = 7,
                progress = 45,
                streak = 3
            )
        )
    }

    fun addChild(child: Child) {
        val current = _children.value?.toMutableList() ?: mutableListOf()
        current.add(child)
        _children.value = current
    }

    fun deleteChild(childId: String) {
        val current = _children.value?.toMutableList() ?: mutableListOf()
        current.removeAll { it.id == childId }
        _children.value = current
    }
}

