package com.retheviper.youtube_downloader.view.download.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.retheviper.youtube_downloader.service.VideoService
import com.retheviper.youtube_downloader.view.state.ApplicationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Controls(applicationState: ApplicationState) {
    val coroutineScope = rememberCoroutineScope()
    var process: Process? = null
    var processMessage by remember { mutableStateOf("") }
    var isDownloading by remember { mutableStateOf(false) }

    suspend fun store() {
        withContext(Dispatchers.IO) {
            isDownloading = true

            process = VideoService.download(
                downloadOption = applicationState.downloadOption,
                downloadFolder = applicationState.userInput.downloadFolder,
                videoUrl = applicationState.userInput.videoUrl
            )

            process?.inputStream?.bufferedReader()?.use { reader ->
                reader.lines().forEach { stdout ->
                    processMessage = stdout
                }
            }

            if (process?.errorStream?.available() != 0) {
                process?.errorStream?.close()
            }

            processMessage = "Download completed."

            runCatching {
                process?.waitFor()
                isDownloading = false
            }.onFailure {
                process?.destroyForcibly()
            }
        }
    }


    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            enabled = !isDownloading && applicationState.userInput.videoUrl.isNotEmpty(),
            onClick = {
                coroutineScope.launch {
                    isDownloading = true
                    processMessage = "Preparing..."
                    store()
                    isDownloading = false
                }
            }
        ) {
            Text("Download")
        }

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            enabled = isDownloading,
            onClick = {
                process?.destroyForcibly()
                isDownloading = false
            }
        ) {
            Text("Stop")
        }
    }

    Text(
        text = processMessage,
        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
    )
}