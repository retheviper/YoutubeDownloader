package com.retheviper.youtube_downloader.service

import com.retheviper.youtube_downloader.common.fileNameWithoutExtension
import com.retheviper.youtube_downloader.view.state.DownloadOptionState
import com.retheviper.youtube_downloader.view.state.UserInputState
import java.nio.file.Files
import java.nio.file.Path

object VideoService {
    fun download(downloadOption: DownloadOptionState, downloadFolder: String, videoUrl: String, uuid: String): Process {
        val command = buildList {
            add("${SystemService.getBrewPath()}/bin/yt-dlp")
            add("-o")
            add("${downloadFolder}/${uuid}%(title)s.%(ext)s")
            if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
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
            }
            add(videoUrl)
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
            add("${SystemService.getBrewPath()}/bin/ffmpeg")
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
}