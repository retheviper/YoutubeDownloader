package com.retheviper.youtube_downloader.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Inputs(
    downloadFolder: String,
    downloadFolderChanged: (String) -> Unit,
    url: String,
    urlChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        Text("Download Path")
        TextField(
            value = downloadFolder,
            onValueChange = downloadFolderChanged,
            placeholder = { Text("None") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Column(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        Text("URL")
        TextField(
            value = url,
            onValueChange = urlChanged,
            placeholder = { Text("None") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}