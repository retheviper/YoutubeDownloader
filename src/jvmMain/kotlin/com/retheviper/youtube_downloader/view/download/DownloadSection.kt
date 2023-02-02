package com.retheviper.youtube_downloader.view.download

import androidx.compose.runtime.Composable
import com.retheviper.youtube_downloader.view.download.component.Controls
import com.retheviper.youtube_downloader.view.download.component.Options
import com.retheviper.youtube_downloader.view.download.component.UserInput
import com.retheviper.youtube_downloader.view.state.DownloadOptionState
import com.retheviper.youtube_downloader.view.state.rememberUserInputState

@Composable
fun DownloadSection(downloadOptionState: DownloadOptionState) {
    // Inputs
    val userInputState = rememberUserInputState()

    Options(downloadOptionState)
    UserInput(userInputState)
    Controls(userInputState, downloadOptionState)
}