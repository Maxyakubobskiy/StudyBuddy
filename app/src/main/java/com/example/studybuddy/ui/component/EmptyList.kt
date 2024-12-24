package com.example.studybuddy.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign

@Composable
fun EmptyList(element: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .alpha(0.4f)
                .align(Alignment.Center),
            text = "There are no $element yet. Click on '+' button to add a $element."
        )
    }
}