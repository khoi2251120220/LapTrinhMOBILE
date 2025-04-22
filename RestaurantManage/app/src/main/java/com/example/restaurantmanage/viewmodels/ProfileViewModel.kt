package com.example.restaurantmanage.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.dao.UserDao
import com.example.restaurantmanage.data.local.entity.UserEntity
import com.example.restaurantmanage.data.models.UserProfile
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userDao: UserDao
) : ViewModel() {
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val auth = FirebaseAuth.getInstance()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _isLoading.value = true
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val email = user.email ?: ""
            val role = if (email == "admin@gmail.com") "admin" else "user"
            
            // Tìm user trong cơ sở dữ liệu Room
            viewModelScope.launch {
                try {
                    val userEntity = userDao.getUserById(userId).first()
                    if (userEntity != null) {
                        // Nếu đã có trong DB, dùng dữ liệu từ DB
                        _userProfile.value = UserProfile(
                            name = userEntity.name,
                            email = userEntity.email,
                            phone = userEntity.phone,
                            favoriteItems = emptyList(),
                            role = userEntity.role
                        )
                    } else {
                        // Nếu chưa có, tạo mới với thông tin từ Firebase
                        _userProfile.value = UserProfile(
                            name = user.displayName ?: "",
                            email = email,
                            phone = "",
                            favoriteItems = emptyList(),
                            role = role
                        )
                        
                        // Lưu vào Room DB
                        userDao.insertUser(
                            UserEntity(
                                userId = userId,
                                name = user.displayName ?: "",
                                email = email,
                                phone = "",
                                role = role
                            )
                        )
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Lỗi khi tải thông tin người dùng: ${e.message}"
                    Log.e("ProfileViewModel", "Error loading user profile", e)
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            _errorMessage.value = "Vui lòng đăng nhập lại"
            _isLoading.value = false
        }
    }

    fun updateProfile(
        name: String = _userProfile.value.name,
        phone: String = _userProfile.value.phone
    ) {
        _isLoading.value = true
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            
            viewModelScope.launch {
                try {
                    // Cập nhật user profile trong memory
                    _userProfile.value = _userProfile.value.copy(
                        name = name,
                        phone = phone
                    )
                    
                    // Lưu vào Room Database
                    val userEntity = UserEntity(
                        userId = userId,
                        name = name,
                        email = _userProfile.value.email,
                        phone = phone,
                        role = _userProfile.value.role
                    )
                    userDao.updateUser(userEntity)
                    
                    _isSaved.value = true
                    Log.d("ProfileViewModel", "Đã lưu thông tin người dùng vào Room DB: $userEntity")
                } catch (e: Exception) {
                    _errorMessage.value = "Lỗi khi cập nhật thông tin: ${e.message}"
                    Log.e("ProfileViewModel", "Error updating user profile", e)
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            _errorMessage.value = "Vui lòng đăng nhập lại"
            _isLoading.value = false
        }
    }

    fun signOut(context: Context, onSuccess: () -> Unit) {
        _isLoading.value = true
        auth.signOut()
        val signInClient = Identity.getSignInClient(context)
        signInClient.signOut()
            .addOnSuccessListener {
                _isLoading.value = false
                onSuccess()
            }
            .addOnFailureListener { e ->
                _errorMessage.value = when {
                    e.message?.contains("ApiException: 10") == true -> "Lỗi xác thực Google, vui lòng kiểm tra cấu hình Firebase"
                    else -> "Lỗi đăng xuất: ${e.message}"
                }
                Log.e("ProfileViewModel", "Lỗi đăng xuất: ${e.message}", e)
                _isLoading.value = false
            }
    }

    fun resetSaveStatus() {
        _isSaved.value = false
    }

    fun clearError() {
        _errorMessage.value = ""
    }
    
    class Factory(private val database: RestaurantDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(database.userDao()) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}