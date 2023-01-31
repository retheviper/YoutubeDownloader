package com.retheviper.youtube_downloader.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DownloadOptions(
    noPlaylist: Boolean,
    noPlaylistChanged: (Boolean) -> Unit,
    audioOnly: Boolean,
    audioOnlyChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("No playlist")
        Checkbox(
            checked = noPlaylist,
            onCheckedChange = noPlaylistChanged
        )

        Text("Audio only")
        Checkbox(
            checked = audioOnly,
            onCheckedChange = audioOnlyChanged
        )
    }
}