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
import com.retheviper.youtube_downloader.view.state.DownloadOptionState
import com.retheviper.youtube_downloader.view.state.UserInputState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Controls(
    userInputState: UserInputState,
    downloadOptionState: DownloadOptionState
) {
    val coroutineScope = rememberCoroutineScope()
    var process: Process? = null
    var processMessage by remember { mutableStateOf("") }
    var isDownloading by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(if (isDownloading) "Downloading..." else "Ready")

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            enabled = !isDownloading && userInputState.videoUrl.isNotEmpty(),
            onClick = {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        isDownloading = true
                        process = download(
                            downloadFolder = userInputState.downloadFolder,
                            audioOnly = downloadOptionState.audioOnly,
                            noPlaylist = downloadOptionState.noPlaylist,
                            videoUrl = userInputState.videoUrl
                        )

                        process?.inputStream?.bufferedReader()?.use { reader ->
                            reader.lines().forEach { stdout ->
                                processMessage = stdout
                            }
                        }

                        try {
                            process?.waitFor()
                            isDownloading = false
                        } catch (e: Exception) {
                            process?.destroyForcibly()
                        }
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

        // TODO add convert button
    }

    Text(
        text = processMessage,
        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
    )
}

fun download(downloadFolder: String, audioOnly: Boolean, noPlaylist: Boolean, videoUrl: String): Process {
    val brewPath = System.getenv("HOMEBREW_PREFIX") ?: when {
        System.getProperty("os.arch").contains("x86") -> "/usr/local"
        else -> "/opt/homebrew"
    }

    val command = buildList {
        add("$brewPath/bin/yt-dlp")
        add("-o")
        add("${downloadFolder}/%(title)s.%(ext)s")
        add("-f")
        if (audioOnly) {
            add("bestaudio")
            add("--extract-audio")
            add("--audio-format")
            add("mp3")
        } else {
            add("bestvideo[ext=mp4]+bestaudio[ext=m4a]")
            add("--merge-output-format")
            add("mp4")
            add("-S")
            add("vcodec:h264")
        }
        if (noPlaylist) add("--no-playlist")
        add(videoUrl)
    }

    return ProcessBuilder(command).start()
}