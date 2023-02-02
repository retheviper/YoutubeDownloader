package com.retheviper.youtube_downloader.view.download.component

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
import com.retheviper.youtube_downloader.view.state.ApplicationState

@Composable
fun UserInput(applicationState: ApplicationState) {
    Column(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        Text("Download Path")
        TextField(
            value = applicationState.userInput.downloadFolder,
            onValueChange = { applicationState.userInput.downloadFolder = it },
            placeholder = { Text("None") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Column(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        Text("URL of Video")
        TextField(
            value = applicationState.userInput.videoUrl,
            onValueChange = { applicationState.userInput.videoUrl = it },
            placeholder = { Text("None") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}