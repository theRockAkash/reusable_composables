package com.solutions.billnest.ui.composables.pullToRefresh

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * @Created by akash on 08-04-2025.
 * Know more about author at https://akash.cloudemy.in
 */
@Composable
fun PullToRefreshContainer(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val state = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = onRefresh
    )
    Box(modifier = modifier.pullRefresh(state)) {
        content.invoke(this)
        PullRefreshIndicator(
            modifier = Modifier.align(alignment = Alignment.TopCenter),
            refreshing = isLoading,
            state = state,
        )
    }
}
