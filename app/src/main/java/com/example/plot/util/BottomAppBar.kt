package com.example.plot.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.plot.R
import com.example.plot.cursor.CursorMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsBottomAppBar(
    onHome: () -> Unit,
    onCursor: (startCursor: CursorMode, endCursor: CursorMode) -> Unit,
    onSync: () -> Unit,
    pickFile: () -> Unit,
) {
    var startCursorMode by remember { mutableStateOf(CursorMode.INVISIBLE) }
    var endCursorMode by remember { mutableStateOf(CursorMode.INVISIBLE) }
    BottomAppBar(
        actions = {
            IconButton(
                onClick = { onHome() }
            ) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Reset"
                )
            }
            IconButton(
                onClick = {
                    startCursorMode = if(startCursorMode == CursorMode.MOVE) CursorMode.VISIBLE else CursorMode.MOVE
                    onCursor(startCursorMode, endCursorMode)
                },
                modifier = Modifier.background(selectCursorBackgroundColor(startCursorMode))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.start_cursor),
                    contentDescription = "Start Cursor"
                )
            }
            IconButton(
                onClick = {
                    endCursorMode = if(endCursorMode == CursorMode.MOVE) CursorMode.VISIBLE else CursorMode.MOVE
                    onCursor(startCursorMode, endCursorMode)
                },
                modifier = Modifier.background(selectCursorBackgroundColor(endCursorMode))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.end_cursor),
                    contentDescription = "End Cursor"
                )
            }
            IconButton(
                onClick = {
                    startCursorMode = CursorMode.INVISIBLE
                    endCursorMode = CursorMode.INVISIBLE
                    onCursor(startCursorMode, endCursorMode)
                }
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Clear Cursor"
                )
            }
            IconButton(
                onClick = { pickFile() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.upload_file),
                    contentDescription = "Upload from file"
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onSync() },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(painter = painterResource(R.drawable.sync),
                    contentDescription = "Sync data"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

fun selectCursorBackgroundColor(cursorMode: CursorMode): Color {
    return when(cursorMode){
        CursorMode.VISIBLE -> {
           Color.Green
        }
        CursorMode.MOVE -> {
           Color.Red
        }
        CursorMode.INVISIBLE -> {
            Color.Unspecified
        }
    }
}