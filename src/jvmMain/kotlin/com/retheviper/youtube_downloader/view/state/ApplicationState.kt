package com.retheviper.youtube_downloader.view.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class ApplicationState(
    val downloadOption: DownloadOptionState,
    val userInput: UserInputState,
)

@Composable
fun rememberApplicationState(
    downloadOptionState: DownloadOptionState = rememberDownloadOptionState(),
    userInputState: UserInputState = rememberUserInputState()
): ApplicationState =
    remember(downloadOptionState, userInputState) {
        ApplicationState(downloadOptionState, userInputState)
    }