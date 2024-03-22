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
import com.retheviper.youtube_downloader.service.FileService
import com.retheviper.youtube_downloader.service.VideoService
import com.retheviper.youtube_downloader.view.state.ApplicationState
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

@Composable
fun Controls(applicationState: ApplicationState) {
    val coroutineScope = rememberCoroutineScope()
    var process: Process? = null
    var processMessage by remember { mutableStateOf("") }
    var isDownloading by remember { mutableStateOf(false) }

    suspend fun store(videoUrl: String, uuid: String) {
        withContext(Dispatchers.IO) {
            if (FileService.fileExists(
                    applicationState.downloadOption,
                    applicationState.userInput.downloadFolder,
                    videoUrl
                )
            ) {
                processMessage = "File already exists."
                return@withContext
            }

            isDownloading = true

            process = VideoService.download(
                downloadOption = applicationState.downloadOption,
                downloadFolder = applicationState.userInput.downloadFolder,
                videoUrl = applicationState.userInput.videoUrl,
                uuid = uuid
            )

            process?.inputStream?.bufferedReader()?.use { reader ->
                reader.lines().forEach { stdout ->
                    processMessage = stdout
                }
            }

            if (process?.errorStream?.available() != 0) {
                try {
                    VideoService.convert(
                        downloadOption = applicationState.downloadOption,
                        userInput = applicationState.userInput,
                        uuid = uuid
                    )
                } catch (e: Exception) {
                    processMessage = e.message ?: "Unknown error with Convert."

                }
                FileService.removeUUID(applicationState.userInput.downloadFolder, uuid)
                processMessage = "Download completed."
                process?.errorStream?.close()
            } else {
                FileService.removeUUID(applicationState.userInput.downloadFolder, uuid)
                processMessage = "Download completed."
            }

            try {
                process?.waitFor()
                isDownloading = false
            } catch (e: Exception) {
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
            enabled = applicationState.userInput.videoUrl.isNotEmpty(),
            onClick = {
                applicationState.userInput.addVideoQueue(applicationState.userInput.videoUrl)
                applicationState.userInput.videoUrl = ""
            }
        ) {
            Text("Add to queue")
        }

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            enabled = !isDownloading && (applicationState.userInput.videoUrl.isNotEmpty() || applicationState.userInput.videoQueue.isNotEmpty()),
            onClick = {
                coroutineScope.launch {
                    if (applicationState.userInput.videoQueue.isEmpty()) {
                        isDownloading = true
                        store(applicationState.userInput.videoUrl, UUID.randomUUID().toString())
                        isDownloading = false
                    } else {
                        isDownloading = true
                        val semaphore = Semaphore(applicationState.downloadOption.parallelDownload)
                        val mutex = Mutex()

                        withContext(Dispatchers.IO) {
                            while (applicationState.userInput.videoQueue.isNotEmpty()) {
                                val videoUrl = applicationState.userInput.getFirstVideoQueue()

                                semaphore.withPermit {
                                    launch {
                                        val uuid = UUID.randomUUID().toString()
                                        process = VideoService.download(
                                            downloadOption = applicationState.downloadOption,
                                            downloadFolder = applicationState.userInput.downloadFolder,
                                            videoUrl = videoUrl,
                                            uuid = uuid
                                        )

                                        process?.let {
                                            try {
                                                it.waitFor()
                                            } catch (e: Exception) {
                                                it.destroyForcibly()
                                            }
                                        }
                                        store(videoUrl, uuid)
                                    }

                                    mutex.withLock {
                                        applicationState.userInput.removeFirstVideoQueue()
                                    }
                                }
                            }
                        }
                        isDownloading = false
                    }
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
        text = "Current queue: ${applicationState.userInput.videoQueue.size}",
        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
    )

    if (applicationState.userInput.videoQueue.isEmpty()) {
        Text(
            text = processMessage,
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
        )
    }
}