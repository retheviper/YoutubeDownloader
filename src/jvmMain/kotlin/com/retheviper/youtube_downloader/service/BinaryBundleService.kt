package com.retheviper.youtube_downloader.service

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.absolutePathString

object BinaryBundleService {
    fun getBinaryBundle(resourcePath: String): Path {
        val tempFile = Files.createTempFile("bundle", null).also { paths -> paths.toFile().deleteOnExit() }

        this::class.java.getResourceAsStream(resourcePath)?.use { input ->
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING)
        }

        if (!Files.isExecutable(tempFile)) {
            Files.setPosixFilePermissions(
                tempFile,
                setOf(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE
                )
            )

            val command = listOf("xattr", "-d", "com.apple.quarantine", tempFile.absolutePathString())

            ProcessBuilder(command)
                .start()
                .waitFor()
        }

        return tempFile
    }
}