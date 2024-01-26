package com.example.plot.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.plot.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsTopAppBar(
    pickFile: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.wave_chart)) },
        actions = {
            IconButton(onClick = pickFile) {
                Icon(
                    painter = painterResource(R.drawable.upload_file),
                    contentDescription = "Upload File"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun ChartsTopAppBarPreview() {
    Surface(){
        ChartsTopAppBar({})
    }
}