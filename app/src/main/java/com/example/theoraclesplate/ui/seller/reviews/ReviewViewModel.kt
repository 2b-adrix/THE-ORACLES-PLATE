package com.example.theoraclesplate.ui.seller.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.data.repository.seller.ReviewRepositoryImpl
import com.example.theoraclesplate.model.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor() : ViewModel() {

    private val repository = ReviewRepositoryImpl()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchReviews()
    }

    private fun fetchReviews() {
        viewModelScope.launch {
            repository.getReviews()
                .catch { e -> _error.value = e.message ?: "An unknown error occurred" }
                .collect { fetchedReviews ->
                    _reviews.value = fetchedReviews
                }
        }
    }
}
