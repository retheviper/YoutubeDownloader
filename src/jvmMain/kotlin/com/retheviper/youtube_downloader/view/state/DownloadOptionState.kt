package com.retheviper.youtube_downloader.view.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class DownloadOptionState(
    private val _noPlaylist: MutableState<Boolean>,
    private val _audioOnly: MutableState<Boolean>
) {
    var noPlaylist: Boolean
        get() = _noPlaylist.value
        set(value) {
            _noPlaylist.value = value
        }

    var audioOnly: Boolean
        get() = _audioOnly.value
        set(value) {
            _audioOnly.value = value
        }
}

@Composable
fun rememberDownloadOptionState(
    noPlaylist: MutableState<Boolean> = mutableStateOf(true),
    audioOnly: MutableState<Boolean> = mutableStateOf(false)
): DownloadOptionState =
    remember(noPlaylist, audioOnly) { DownloadOptionState(noPlaylist, audioOnly) }