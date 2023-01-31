package com.retheviper.youtube_downloader.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Controls(
    downloading: Boolean,
    onDownload: () -> Unit,
    onStop: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(if (downloading) "Downloading..." else "Ready")

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            enabled = !downloading,
            onClick = onDownload
        ) {
            Text("Download")
        }

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            enabled = downloading,
            onClick = onStop
        ) {
            Text("Stop")
        }
    }
}