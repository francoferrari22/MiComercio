package com.micomercio.app.data

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

/**
 * Backup portable de Mi Comercio.
 *
 * Android borra el almacenamiento privado de la aplicación al desinstalarla.
 * Este archivo se guarda fuera del sandbox, en una carpeta visible del usuario:
 * Download/MiComercio/mi_comercio_backup.json
 *
 * Esto permite conservar una copia independiente de la instalación. La
 * restauración automática completa queda sujeta a las reglas de almacenamiento
 * de Android; para instalaciones nuevas se puede importar esta copia desde el
 * selector de archivos sin perder los datos.
 */
object MiComercioBackup {
    private const val FILE_NAME = "mi_comercio_backup.json"
    private const val RELATIVE_PATH = "Download/MiComercio/"

    fun write(context: Context, json: String): Boolean = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val existing = resolver.query(
                collection,
                arrayOf(MediaStore.MediaColumns._ID),
                "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?",
                arrayOf(FILE_NAME, RELATIVE_PATH),
                null
            )?.use { c -> if (c.moveToFirst()) c.getLong(0) else null }
            if (existing != null) {
                resolver.openOutputStream(android.content.ContentUris.withAppendedId(collection, existing), "wt")!!.use {
                    it.write(json.toByteArray(Charsets.UTF_8))
                }
            } else {
                val values = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, FILE_NAME)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, RELATIVE_PATH)
                }
                val uri = resolver.insert(collection, values) ?: return false
                resolver.openOutputStream(uri, "wt")!!.use { it.write(json.toByteArray(Charsets.UTF_8)) }
            }
        } else {
            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MiComercio")
            dir.mkdirs()
            File(dir, FILE_NAME).writeText(json, Charsets.UTF_8)
        }
        true
    }.getOrDefault(false)

    fun delete(context: Context): Boolean = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val uri = resolver.query(collection, arrayOf(MediaStore.MediaColumns._ID),
                "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?",
                arrayOf(FILE_NAME, RELATIVE_PATH), null)?.use { c ->
                if (c.moveToFirst()) android.content.ContentUris.withAppendedId(collection, c.getLong(0)) else null
            }
            uri != null && resolver.delete(uri, null, null) > 0
        } else {
            val f = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MiComercio/$FILE_NAME")
            if (f.exists()) f.delete() else false
        }
    }.getOrDefault(false)

    fun writeRaw(context: Context, json: String): Boolean = write(context, json)

    fun read(context: Context): String? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val uri = resolver.query(
                collection,
                arrayOf(MediaStore.MediaColumns._ID),
                "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?",
                arrayOf(FILE_NAME, RELATIVE_PATH),
                null
            )?.use { c -> if (c.moveToFirst()) android.content.ContentUris.withAppendedId(collection, c.getLong(0)) else null }
            uri?.let { resolver.openInputStream(it)?.bufferedReader(Charsets.UTF_8)?.use { r -> r.readText() } }
        } else {
            val f = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MiComercio/$FILE_NAME")
            if (f.exists()) f.readText(Charsets.UTF_8) else null
        }
    }.getOrNull()
}
