package com.example.restaurantmanage.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

/**
 * Utility class for managing images in the application
 */
object ImageUtils {

    private const val TAG = "ImageUtils"
    private const val IMAGE_DIRECTORY = "menu_images"
    
    /**
     * Save image from Uri to app's internal storage and return the path
     */
    suspend fun saveImageFromUri(context: Context, imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val imageId = UUID.randomUUID().toString()
            val directory = File(context.filesDir, IMAGE_DIRECTORY).apply {
                if (!exists()) mkdirs()
            }
            
            val file = File(directory, "$imageId.jpg")
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val outputStream = FileOutputStream(file)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            // Return the file path that will be stored in the database
            file.absolutePath
        } catch (e: IOException) {
            Log.e(TAG, "Error saving image", e)
            null
        }
    }
    
    /**
     * Load image bitmap from a path
     */
    suspend fun loadImageFromPath(path: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                Log.e(TAG, "Image file not found: $path")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image", e)
            null
        }
    }
    
    /**
     * Delete image from storage
     */
    suspend fun deleteImage(path: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            } else {
                Log.e(TAG, "Image file not found: $path")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image", e)
            false
        }
    }
    
    /**
     * Get relative path from absolute path for display purposes
     */
    fun getRelativePathForDisplay(absolutePath: String): String {
        return absolutePath.substringAfterLast(IMAGE_DIRECTORY)
    }
} 