package com.example.plot

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.plot.ui.theme.PlotTheme
import androidx.lifecycle.lifecycleScope
import com.example.plot.repository.Axis
import com.example.plot.repository.FileRepository
import com.example.plot.repository.FileRepositoryImpl
import com.example.plot.util.ChartsTopAppBar
import com.example.plot.wavechart.WaveChart
import kotlinx.coroutines.launch

//TODO: Создать отдельный ViewModel и обрабатывать events и хранить state внутри него

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var xAxis by remember{ mutableStateOf(listOf<Float>())}
            val repo: FileRepository = remember {
                FileRepositoryImpl()
            }
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                uri?.let {
                    lifecycleScope.launch {
                        repo.loadDocument(context, it)
                        xAxis = repo.getAxisData(axis = Axis.X)
                    }
                }
            }
            PlotTheme {
                Scaffold(
                    topBar = {
                        ChartsTopAppBar(
                            pickFile = { launcher.launch(arrayOf("*/*")) }
                        )
                             },
                    modifier = Modifier.fillMaxSize()
                ){
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 70.dp)) {
                            WaveChart(
                                signal = xAxis,
                                sampleRate = 128000,
                                modifier = Modifier
                                    .height(400.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}