package com.retheviper.youtube_downloader.common

import java.nio.file.Path

val Path.fileNameWithoutExtension: String
    get() = fileName.toString().substringBeforeLast(".")