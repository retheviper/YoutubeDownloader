package com.retheviper.youtube_downloader.view.download.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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

        Text("Parallel download")
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            value = applicationState.downloadOption.parallelDownload.toString(),
            onValueChange = { applicationState.downloadOption.parallelDownload = it.toInt() },
            singleLine = true,
            modifier = Modifier.size(50.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}