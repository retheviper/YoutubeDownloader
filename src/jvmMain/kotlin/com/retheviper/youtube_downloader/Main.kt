package com.retheviper.youtube_downloader

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.retheviper.youtube_downloader.view.Controls
import com.retheviper.youtube_downloader.view.DownloadOptions
import com.retheviper.youtube_downloader.view.InfoDialog
import com.retheviper.youtube_downloader.view.Inputs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
@Preview
fun App() {
    // Options
    var noPlaylist by remember { mutableStateOf(true) }
    var audioOnly by remember { mutableStateOf(false) }

    // Inputs
    var downloadFolder by remember { mutableStateOf("${System.getProperty("user.home")}/Downloads") }
    var url by remember { mutableStateOf("") }

    // Process
    var downloading by remember { mutableStateOf(false) }
    var process: Process? = null
    var processResult by remember { mutableStateOf<Int?>(null) }

    val coroutineScope = rememberCoroutineScope()

    if (processResult != null) {
        InfoDialog(processResult) {
            processResult = null
            downloading = false
        }
    }

    MaterialTheme {
        Column {
            DownloadOptions(
                noPlaylist = noPlaylist,
                noPlaylistChanged = { noPlaylist = it },
                audioOnly = audioOnly,
                audioOnlyChanged = { audioOnly = it }
            )

            Divider(thickness = 0.5.dp)

            Inputs(
                downloadFolder = downloadFolder,
                downloadFolderChanged = { downloadFolder = it },
                url = url,
                urlChanged = { url = it }
            )

            Controls(
                downloading = downloading,
                onDownload = {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            process = runProcess(
                                downloadFolder = downloadFolder,
                                audioOnly = audioOnly,
                                noPlaylist = noPlaylist,
                                url = url
                            )
                            downloading = true
                            try {
                                processResult = process?.waitFor()
                            } catch (e: Exception) {
                                process?.destroyForcibly()
                            }
                        }
                    }
                },
                onStop = {
                    process?.destroyForcibly()
                    downloading = false
                }
            )
        }
    }
}

fun runProcess(downloadFolder: String, audioOnly: Boolean, noPlaylist: Boolean, url: String): Process {
    val command = buildList {
        add("${System.getenv("HOMEBREW_PREFIX")}/bin/yt-dlp")
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
        add(url)
    }

    return ProcessBuilder(command)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "YouTube Downloader",
        resizable = false,
        state = rememberWindowState(width = 400.dp, height = 300.dp)
    ) {
        App()
    }
}
