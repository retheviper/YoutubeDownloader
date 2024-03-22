package com.retheviper.youtube_downloader

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.retheviper.youtube_downloader.view.download.DownloadSection
import com.retheviper.youtube_downloader.view.state.rememberApplicationState

@Composable
@Preview
fun App() {
    val applicationState = rememberApplicationState()

    MaterialTheme {
        Column {
            DownloadSection(applicationState)
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "YouTube Downloader",
        resizable = true,
        state = rememberWindowState(width = 600.dp, height = 380.dp)
    ) {
        window.minimumSize = window.size
        App()
    }
}
