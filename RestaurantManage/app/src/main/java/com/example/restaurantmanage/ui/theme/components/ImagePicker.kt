package com.example.restaurantmanage.ui.theme.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.restaurantmanage.util.ImageUtils

/**
 * Component for picking and displaying an image
 * @param currentImagePath the current image path, can be empty
 * @param onImageSelected callback when an image is selected, returns the path
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun ImagePicker(
    currentImagePath: String,
    onImageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var hasImage by remember { mutableStateOf(currentImagePath.isNotEmpty()) }

    // Theo dõi thay đổi của Uri để xử lý ngoài lambda
    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            val path = ImageUtils.saveImageFromUri(context, uri)
            onImageSelected(path)
        }
    }
    
    // Khai báo pickImageLauncher trước để permissionLauncher có thể tham chiếu đến nó
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            hasImage = true
        }
    }
    
    // Khai báo photoPicker cho Android 14+ (API 34+)
    val photoPickerLauncher = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                imageUri = it
                hasImage = true
            }
        }
    } else null
    
    // Khai báo permissionLauncher sau khi đã có pickImageLauncher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                photoPickerLauncher?.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                pickImageLauncher.launch("image/*")
            }
        }
    }
    
    Box(
        modifier = modifier
            .size(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                when {
                    // Android 14+ (API 34+): Sử dụng Photo Picker API
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                        val requiredPermission = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                        when {
                            ContextCompat.checkSelfPermission(
                                context, requiredPermission
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                photoPickerLauncher?.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            else -> {
                                permissionLauncher.launch(requiredPermission)
                            }
                        }
                    }
                    // Android 13 (API 33): Sử dụng READ_MEDIA_IMAGES
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        val requiredPermission = Manifest.permission.READ_MEDIA_IMAGES
                        when {
                            ContextCompat.checkSelfPermission(
                                context, requiredPermission
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                pickImageLauncher.launch("image/*")
                            }
                            else -> {
                                permissionLauncher.launch(requiredPermission)
                            }
                        }
                    }
                    // Android 12 và cũ hơn: Sử dụng READ_EXTERNAL_STORAGE
                    else -> {
                        val requiredPermission = Manifest.permission.READ_EXTERNAL_STORAGE
                        when {
                            ContextCompat.checkSelfPermission(
                                context, requiredPermission
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                pickImageLauncher.launch("image/*")
                            }
                            else -> {
                                permissionLauncher.launch(requiredPermission)
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (hasImage) {
            // Display the current image
            if (currentImagePath.isNotEmpty()) {
                AsyncImage(
                    model = currentImagePath,
                    contentDescription = "Selected image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Delete button overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f))
                        .clickable {
                            imageUri = null
                            hasImage = false
                            onImageSelected(null)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove image",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        } else {
            // Placeholder for selecting an image
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Add photo",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 