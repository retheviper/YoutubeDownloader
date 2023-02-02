package com.retheviper.youtube_downloader.view.download

import androidx.compose.runtime.Composable
import com.retheviper.youtube_downloader.view.download.component.Controls
import com.retheviper.youtube_downloader.view.download.component.Options
import com.retheviper.youtube_downloader.view.download.component.UserInput
import com.retheviper.youtube_downloader.view.state.ApplicationState

@Composable
fun DownloadSection(applicationState: ApplicationState) {
    Options(applicationState)
    UserInput(applicationState)
    Controls(applicationState)
}