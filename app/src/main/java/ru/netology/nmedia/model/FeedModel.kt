package ru.netology.nmedia.model

import ru.netology.nmedia.dto.PostDto

data class FeedModel (
    val posts: List<PostDto> = emptyList(),
    val empty: Boolean = false,
)

data class FeedModelState(
    val idle: Boolean = false,
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)