package com.example.plot.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.plot.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsTopAppBar(
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.wave_chart)) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun ChartsTopAppBarPreview() {
    Surface(){
        ChartsTopAppBar()
    }
}