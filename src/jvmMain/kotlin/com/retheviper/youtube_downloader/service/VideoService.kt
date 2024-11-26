package com.retheviper.youtube_downloader.service

import com.retheviper.youtube_downloader.view.state.DownloadOptionState

object VideoService {
    fun download(downloadOption: DownloadOptionState, downloadFolder: String, videoUrl: String): Process {
        val command = buildList {
            add(SystemService.ytDlpPath)
            add("--ffmpeg-location")
            add(SystemService.ffmpegPath)
            add("-o")
            add("${downloadFolder}/%(title)s.%(ext)s")
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
}