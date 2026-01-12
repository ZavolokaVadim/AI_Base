package com.example.newsapp.presentation.screen.main.profile

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsapp.domain.entity.User
import com.example.newsapp.domain.model.NewsItem
import com.example.newsapp.presentation.ui.theme.NewsAppTheme

@Composable
fun ProfileScreen() {
    val viewModel = hiltViewModel<ProfileScreenViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.selectedNewsArticleUrl) {
        state.selectedNewsArticleUrl?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        }
    }
    ProfileScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ProfileScreenContent(
    state: ProfileScreenState,
    onEvent: (ProfileScreenEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
        )

        state.userLoadingError?.let { userLoadingError ->
            Text(
                text = userLoadingError,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        state.currentUser?.let { currentUser ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null
                )
                Text(
                    text = currentUser.username
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null
                )
                Text(
                    text = currentUser.email
                )
            }
        }

        NewsListView(
            modifier = Modifier.padding(top = 10.dp),
            favoriteNews = state.favoriteNews ?: emptyList(),
            onEvent = onEvent
        )
    }
}

@Composable
fun NewsListView(
    modifier: Modifier = Modifier,
    favoriteNews: List<NewsItem>,
    onEvent: (ProfileScreenEvent) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(0.9f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(favoriteNews) { currentNewsItem ->
            com.example.newsapp.presentation.ui.component.NewsItem(
                newsItem = currentNewsItem,
                onFavoriteClicked = {
                    onEvent(ProfileScreenEvent.NewsItemFavoriteToggleClicked(currentNewsItem))
                },
                onReadClicked = {
                    onEvent(ProfileScreenEvent.NewsItemClicked(currentNewsItem))
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    NewsAppTheme {
        ProfileScreenContent(
            state = ProfileScreenState(
                currentUser = User("", "ProgrammerC11", "testemail@gmail.com", "password")
            ),
            onEvent = {}
        )
    }
}