package com.retheviper.youtube_downloader.view.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class UserInputState(
    private val _downloadFolder: MutableState<String>,
    private val _videoUrl: MutableState<String>,
    private val _videoQueue: MutableState<ArrayDeque<String>>
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

    val videoQueue: ArrayDeque<String>
        get() = _videoQueue.value

    fun addVideoQueue(video: String) {
        _videoQueue.value.add(video)
    }

    fun getFirstVideoQueue(): String {
        return _videoQueue.value.first()
    }

    fun removeFirstVideoQueue() {
        _videoQueue.value.removeFirst()
    }
}

@Composable
fun rememberUserInputState(
    downloadFolder: MutableState<String> = mutableStateOf("${System.getProperty("user.home")}/Downloads"),
    videoUrl: MutableState<String> = mutableStateOf(""),
    videoQueue: MutableState<ArrayDeque<String>> = mutableStateOf(ArrayDeque())
): UserInputState =
    remember(downloadFolder, videoUrl, videoQueue) { UserInputState(downloadFolder, videoUrl, videoQueue) }