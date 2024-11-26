package com.retheviper.youtube_downloader.service

import kotlin.io.path.absolutePathString

object SystemService {
    private const val PATH = "/binaries"

    val ffmpegPath: String by lazy {
        BinaryBundleService.getBinaryBundle("$PATH/ffmpeg").absolutePathString()
    }

    val ytDlpPath: String by lazy {
        BinaryBundleService.getBinaryBundle("$PATH/yt-dlp").absolutePathString()
    }
}