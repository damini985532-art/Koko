package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.CateringCardElevated
import com.example.ui.theme.CateringTextMuted
import java.io.File

@Composable
fun CateringImage(
    imageUri: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    isItem: Boolean = false
) {
    val context = LocalContext.current

    when {
        imageUri.contains("koko_bakers_logo") -> {
            Image(
                painter = painterResource(id = R.drawable.koko_bakers_logo),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageUri.contains("pizza_van_banner") -> {
            Image(
                painter = painterResource(id = R.drawable.pizza_van_banner),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageUri.contains("sushi_van_banner") -> {
            Image(
                painter = painterResource(id = R.drawable.sushi_van_banner),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageUri.isNotBlank() -> {
            val model = if (imageUri.startsWith("/") || imageUri.startsWith("file://")) {
                File(imageUri.removePrefix("file://"))
            } else {
                imageUri
            }
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(model)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        else -> {
            Box(
                modifier = modifier.background(CateringCardElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isItem) Icons.Default.Image else Icons.Default.DinnerDining,
                    contentDescription = contentDescription,
                    tint = CateringTextMuted,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
