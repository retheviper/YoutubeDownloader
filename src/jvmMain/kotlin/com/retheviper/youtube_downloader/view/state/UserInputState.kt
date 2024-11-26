package com.retheviper.youtube_downloader.view.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class UserInputState(
    private val _downloadFolder: MutableState<String>,
    private val _videoUrl: MutableState<String>
) {
    var downloadFolder: String
        get() = _downloadFolder.value
        set(value) {
            _downloadFolder.value = value
        }

    var videoUrl: String
        get() = _videoUrl.value
        set(value) {
            _videoUrl.value = value
        }
}

@Composable
fun rememberUserInputState(
    downloadFolder: MutableState<String> = mutableStateOf("${System.getProperty("user.home")}/Downloads"),
    videoUrl: MutableState<String> = mutableStateOf("")
): UserInputState =
    remember(downloadFolder, videoUrl) { UserInputState(downloadFolder, videoUrl) }