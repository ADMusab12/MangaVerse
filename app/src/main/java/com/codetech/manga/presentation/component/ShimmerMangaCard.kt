package com.codetech.manga.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerMangaCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.background(shimmerBrush()))
    }
}

fun shimmerBrush(): Brush {
    return Brush.linearGradient(listOf(Color.LightGray, Color.Gray, Color.LightGray))
}