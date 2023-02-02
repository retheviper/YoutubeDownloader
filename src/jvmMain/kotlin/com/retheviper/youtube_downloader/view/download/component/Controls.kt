package com.retheviper.youtube_downloader.view.download.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.retheviper.youtube_downloader.common.fileNameWithoutExtension
import com.retheviper.youtube_downloader.view.state.ApplicationState
import com.retheviper.youtube_downloader.view.state.DownloadOptionState
import com.retheviper.youtube_downloader.view.state.UserInputState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID

@Composable
fun Controls(applicationState: ApplicationState) {
    val coroutineScope = rememberCoroutineScope()
    var process: Process? = null
    var processMessage by remember { mutableStateOf("") }
    var isDownloading by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            enabled = !isDownloading && applicationState.userInput.videoUrl.isNotEmpty(),
            onClick = {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        if (fileExists(
                                downloadOption = applicationState.downloadOption,
                                userInput = applicationState.userInput
                            )
                        ) {
                            processMessage = "File already exists."
                            return@withContext
                        }

                        isDownloading = true

                        val uuid = UUID.randomUUID().toString()

                        process = download(
                            downloadOption = applicationState.downloadOption,
                            userInput = applicationState.userInput,
                            uuid = uuid
                        )

                        process?.inputStream?.bufferedReader()?.use { reader ->
                            reader.lines().forEach { stdout ->
                                processMessage = stdout
                            }
                        }

                        if (process?.errorStream?.available() != 0) {
                            try {
                                convert(
                                    downloadOption = applicationState.downloadOption,
                                    userInput = applicationState.userInput,
                                    uuid = uuid
                                )
                            } catch (e: Exception) {
                                processMessage = e.message ?: "Unknown error with Convert."

                            }
                            removeUUID(applicationState.userInput.downloadFolder, uuid)
                            processMessage = "Download completed."
                            process?.errorStream?.close()
                        } else {
                            removeUUID(applicationState.userInput.downloadFolder, uuid)
                            processMessage = "Download completed."
                        }

                        try {
                            process?.waitFor()
                            isDownloading = false
                        } catch (e: Exception) {
                            process?.destroyForcibly()
                        }
                    }
                }
            }
        ) {
            Text("Download")
        }

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            enabled = isDownloading,
            onClick = {
                process?.destroyForcibly()
                isDownloading = false
            }
        ) {
            Text("Stop")
        }
    }

    Text(
        text = processMessage,
        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
    )
}

fun getBrewPath(): String {
    return System.getenv("HOMEBREW_PREFIX") ?: when {
        System.getProperty("os.arch").contains("x86") -> "/usr/local"
        else -> "/opt/homebrew"
    }
}

fun fileExists(downloadOption: DownloadOptionState, userInput: UserInputState): Boolean {
    val command = buildList {
        add("${getBrewPath()}/bin/yt-dlp")
        add("--print")
        add("title")
        add(userInput.videoUrl)
    }

    val process = ProcessBuilder(command).start()

    val title = process.inputStream.bufferedReader().use {
        it.lines().findFirst().get()
    }

    process.waitFor()

    val fileName = if (downloadOption.audioOnly) {
        "${userInput.downloadFolder}/${title}.mp3"
    } else {
        "${userInput.downloadFolder}/${title}.mp4"
    }

    return Files.exists(Path.of(fileName))
}

fun download(downloadOption: DownloadOptionState, userInput: UserInputState, uuid: String): Process {
    val command = buildList {
        add("${getBrewPath()}/bin/yt-dlp")
        add("-o")
        add("${userInput.downloadFolder}/${uuid}%(title)s.%(ext)s")
        add("-f")
        if (downloadOption.audioOnly) {
            add("bestaudio")
            add("--extract-audio")
            add("--audio-format")
            add("mp3")
        } else {
            add("bestvideo[ext=mp4]+bestaudio[ext=m4a]")
            add("--merge-output-format")
            add("mp4")
            add("-S")
            add("vcodec:h264")
        }
        if (downloadOption.noPlaylist) add("--no-playlist")
        add(userInput.videoUrl)
    }

    return ProcessBuilder(command)
        .start()
}

fun convert(downloadOption: DownloadOptionState, userInput: UserInputState, uuid: String) {
    val files = Files.walk(Path.of(userInput.downloadFolder))
        .filter { it.fileName.toString().contains(uuid) }
        .toList()

    if (files.isEmpty()) return

    val command = buildList {
        add("${getBrewPath()}/bin/ffmpeg")
        if (downloadOption.audioOnly) {
            add("acodec")
            add("libmp3lame")
            add("${userInput.downloadFolder}/${files.first().fileNameWithoutExtension.replace(uuid, "")}.mp3")
            return@buildList
        } else {
            val video = files.find { it.fileName.toString().contains(".mp4") }
            val audio = files.find { it.fileName.toString().contains(".m4a") }
            add("-i")
            add(video.toString())
            add("-i")
            add(audio.toString())
            add("-c:v")
            add("copy")
            add("-c:a")
            add("copy")
            add("${userInput.downloadFolder}/${video?.fileNameWithoutExtension?.replace(uuid, "")}.mp4")
        }
    }

    val process = ProcessBuilder(command)
        .start()

    if (process.errorStream.available() != 0) {
        process.errorStream.close()
        throw RuntimeException("Failed to convert file.")
    }
}

fun removeUUID(downloadFolder: String, uuid: String) {
    Files.walk(Path.of(downloadFolder))
        .filter { it.fileName.toString().contains(uuid) }
        .forEach {
            Files.move(
                it,
                Path.of(
                    downloadFolder,
                    it.fileName.toString()
                        .replace(uuid, "")
                        .replace(Regex("f\\d{3}."), "")
                ),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
}