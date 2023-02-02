package com.retheviper.youtube_downloader.view.download.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.retheviper.youtube_downloader.view.state.ApplicationState

@Composable
fun Options(applicationState: ApplicationState) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("No playlist")
        Checkbox(
            checked = applicationState.downloadOption.noPlaylist,
            onCheckedChange = { applicationState.downloadOption.noPlaylist = it }
        )

        Text("Audio only")
        Checkbox(
            checked = applicationState.downloadOption.audioOnly,
            onCheckedChange = { applicationState.downloadOption.audioOnly = it }
        )
    }
}