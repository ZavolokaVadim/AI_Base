package com.example.newsapp.presentation.screen.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.repository.AuthRepository
import com.example.newsapp.data.repository.FavoriteNewsRepository
import com.example.newsapp.domain.entity.User
import com.example.newsapp.domain.model.NewsItem
import com.example.newsapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val favoriteNewsRepository: FavoriteNewsRepository,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _state = MutableStateFlow(ProfileScreenState())
    val state = _state.asStateFlow()

    fun onEvent(event: ProfileScreenEvent) {
        when(event) {
            is ProfileScreenEvent.NewsItemClicked -> onNewsItemClicked(event.newsItem)
            is ProfileScreenEvent.NewsItemFavoriteToggleClicked -> onNewsItemFavoriteToggleClicked(event.newsItem)
        }
    }

    private fun onNewsItemClicked(newsItem: NewsItem) {
        _state.update { it.copy(selectedNewsArticleUrl = newsItem.url) }
    }

    private fun onNewsItemFavoriteToggleClicked(newsItem: NewsItem) {
        if (newsItem.isFavorite) {
            val updatedFavoriteNews = state.value.favoriteNews?.filter { it.id != newsItem.id }
            _state.update { it.copy(favoriteNews = updatedFavoriteNews) }
        }
    }

    private fun loadCurrentUser() = viewModelScope.launch {
        val result = Dispatchers.IO {
            authRepository.getCurrentUser()
        }
        when(result) {
            is Result.Success<User> -> {
                _state.update { it.copy(currentUser = result.data) }
            }
            is Result.Failure<User> -> {
                _state.update { it.copy(userLoadingError = "Failed to load the current user") }
            }
        }
    }

    private fun loadFavoriteNews() = viewModelScope.launch {
        val favoriteNewsItemsList = Dispatchers.IO {
            favoriteNewsRepository.getAllFavoriteNews()
        }
        if (favoriteNewsItemsList.isNotEmpty()) {
            _state.update { it.copy(favoriteNews = favoriteNewsItemsList) }
        }
    }

    init {
        loadCurrentUser()
        loadFavoriteNews()
    }

}