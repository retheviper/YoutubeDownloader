package com.retheviper.youtube_downloader.service

object SystemService {
    fun getBrewPath(): String {
        return System.getenv("HOMEBREW_PREFIX") ?: when {
            System.getProperty("os.arch").contains("x86") -> "/usr/local"
            else -> "/opt/homebrew"
        }
    }
}