package com.retheviper.youtube_downloader.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun InfoDialog(resultCode: Int?, onClick: () -> Unit) {
    AlertDialog(
        title = { Text("Info") },
        text = { Text(if (resultCode == 0) "Download Completed" else "Stopped") },
        modifier = Modifier.width(350.dp),
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    backgroundColor = Color.Black
                ),
                onClick = onClick,
                modifier = Modifier.padding(end = 20.dp),
            ) { Text("OK") }
        },
        onDismissRequest = {}
    )
}