/*
 * This file is a part of Telegram X
 * Copyright © 2014-2022 (tgx-android@pm.me)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.vkryl.task

import java.io.File
import java.io.FileOutputStream
import java.io.Writer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalContracts
fun writeToFile(path: String, mkdirs: Boolean = true, block: (Writer) -> Unit) {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  val file = File(path)
  if (!file.parentFile.exists()) {
    if (mkdirs) {
      if (!file.parentFile.mkdirs())
        error("Could not create folder: ${file.parentFile.absolutePath}")
    } else {
      error("Folder does not exist: ${file.parentFile.absolutePath}")
    }
  }

  if (file.exists() && !file.isFile) {
    error("Not a file: ${file.absolutePath}")
  }
  val outFile = File(file.parentFile, "${file.name}.temp")
  FileOutputStream(outFile).use { stream ->
    stream.bufferedWriter().use {
      try {
        block(it)
      } catch (t: Throwable) {
        outFile.delete()
        throw t
      }
    }
    stream.flush()
  }

  if (file.exists()) {
    if (!areFileContentsIdentical(file, outFile)) {
      copyOrReplace(outFile, file)
    }
    outFile.delete()
  } else {
    outFile.renameTo(file)
  }
}

fun copyOrReplace(fromFile: File, toFile: File) {
  FileChannel.open(fromFile.toPath(), StandardOpenOption.READ).use { inChannel ->
    FileChannel.open(toFile.toPath(), setOf(
      StandardOpenOption.WRITE,
      StandardOpenOption.TRUNCATE_EXISTING)
    ).use { outChannel ->
      inChannel.transferTo(0, inChannel.size(), outChannel)
    }
  }
}

fun editFile(path: String, block: (String) -> String) {
  val file = File(path)
  if (!file.exists()) {
    error("File does not exist: ${file.absolutePath}")
  }
  if (!file.isFile) {
    error("Not a file: ${file.absolutePath}")
  }

  val tempFile = File(file.parentFile, "${file.name}.temp")
  var hasChanges = false
  tempFile.bufferedWriter().use { writer ->
    file.bufferedReader().use { reader ->
      var first = true
      while (true) {
        val line = reader.readLine() ?: break
        if (first) {
          first = false
        } else {
          writer.append("\n")
        }
        val changedLine = block(line)
        if (!hasChanges && line != changedLine) {
          hasChanges = true
        }
        writer.append(changedLine)
      }
    }
  }

  if (hasChanges) {
    copyOrReplace(tempFile, file)
  }
  tempFile.delete()
}

fun areFileContentsIdentical(a: File, b: File): Boolean {
  FileChannel.open(a.toPath(), StandardOpenOption.READ).use { fileChannelA ->
    FileChannel.open(b.toPath(), StandardOpenOption.READ).use { fileChannelB ->
      val mapA = fileChannelA.map(FileChannel.MapMode.READ_ONLY, 0, fileChannelA.size())
      val mapB = fileChannelB.map(FileChannel.MapMode.READ_ONLY, 0, fileChannelB.size())
      return mapA == mapB
    }
  }
}

fun String.camelCaseToUpperCase(): String {
  val upperCase = StringBuilder()
  var i = 0
  while (i < this.length) {
    val codePoint = this.codePointAt(i)
    if (Character.isUpperCase(codePoint)) {
      if (i > 0)
        upperCase.append('_')
      upperCase.appendCodePoint(codePoint)
    } else {
      upperCase.appendCodePoint(Character.toUpperCase(codePoint))
    }
    i += Character.charCount(codePoint)
  }
  return upperCase.toString()
}

fun String.stripUnderscoresWithCamelCase (): String {
  val upperCase = StringBuilder(this.length)
  var nextUpperCase = false
  for (c in this) {
    when {
      c == '_' -> nextUpperCase = true
      nextUpperCase -> {
        upperCase.append(c.toUpperCase())
        nextUpperCase = false
      }
      else -> upperCase.append(c)
    }
  }
  return upperCase.toString()
}

fun String.normalizeArgbHex(): String {
  if (!this.startsWith("#"))
    error("Invalid color: $this")
  val hex = this.substring(1)
  when (hex.length) {
    3 -> {
      val b = StringBuilder(8).append("ff")
      for (c in hex) {
        val l = c.toLowerCase()
        b.append(l).append(l)
      }
      return b.toString()
    }
    4 -> {
      val r = hex[0].toLowerCase()
      val g = hex[1].toLowerCase()
      val b = hex[2].toLowerCase()
      val a = hex[3].toLowerCase()
      return StringBuilder(8)
        .append(a).append(a)
        .append(r).append(r)
        .append(g).append(g)
        .append(b).append(b).toString()
    }
    6 -> {
      return "ff${hex.toLowerCase(Locale.US)}"
    }
    8 -> {
      return hex.substring(6, 8).toLowerCase(Locale.US) + hex.substring(0, 6).toLowerCase(Locale.US)
    }
    else -> error("Invalid color: $this")
  }
}

fun String.parseArgbColor(): Int {
  val hex = this.normalizeArgbHex()
  val colors = mutableListOf<Int>()
  for (i in 0 .. (hex.length / 2)) {
    val x = hex.substring(i * 2, i * 2 + 1)
    colors.add(x.toInt(16))
  }
  return if (colors.size == 3) {
    rgb(colors[0], colors[1], colors[2])
  } else {
    argb(colors[0], colors[1], colors[2], colors[3])
  }
}

fun rgb(red: Int, green: Int, blue: Int): Int {
  return -0x1000000 or (red shl 16) or (green shl 8) or blue
}

fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int {
  return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
}

fun String.unwrapDoubleQuotes(): String {
  if (!this.startsWith("\"") || !this.endsWith("\""))
    error("Not wrapped: \"${this}\"")
  return this.substring(1, this.length - 1).replace("\\\"", "\"")
}