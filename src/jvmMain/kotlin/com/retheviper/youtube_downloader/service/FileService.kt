package com.retheviper.youtube_downloader.service

import com.retheviper.youtube_downloader.view.state.DownloadOptionState
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

object FileService {
    fun fileExists(downloadOption: DownloadOptionState, downloadFolder: String, videoUrl: String): Boolean {
        val command = buildList {
            add("${SystemService.getBrewPath()}/bin/yt-dlp")
            add("--print")
            add("title")
            add(videoUrl)
        }

        val process = ProcessBuilder(command).start()

        val title = process.inputStream.bufferedReader().use {
            it.lines().findFirst().get()
        }

        process.waitFor()

        val fileName = if (downloadOption.audioOnly) {
            "${downloadFolder}/${title}.mp3"
        } else {
            "${downloadFolder}/${title}.mp4"
        }

        return Files.exists(Path.of(fileName))
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
}