package com.example.restaurantmanage.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.restaurantmanage.data.models.UserProfile
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
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

    fun loadUserProfile() {
        _isLoading.value = true
        val user = auth.currentUser
        if (user != null) {
            val email = user.email ?: ""
            val role = if (email == "admin@gmail.com") "admin" else "user"
            _userProfile.value = UserProfile(
                name = user.displayName ?: "",
                email = email,
                phone = _userProfile.value.phone, // Giữ dữ liệu tạm thời
                address = _userProfile.value.address,
                favoriteItems = _userProfile.value.favoriteItems,
                role = role
            )
            _isLoading.value = false
        } else {
            _errorMessage.value = "Vui lòng đăng nhập lại"
            _isLoading.value = false
        }
    }

    fun updateProfile(
        name: String = _userProfile.value.name,
        email: String = _userProfile.value.email,
        phone: String = _userProfile.value.phone,
        address: String = _userProfile.value.address,
        favoriteItems: List<String> = _userProfile.value.favoriteItems,
        role: String = _userProfile.value.role
    ) {
        _userProfile.value = UserProfile(name, email, phone, address, favoriteItems, role)
        _isSaved.value = false
    }

    fun saveProfile() {
        _isLoading.value = true
        val user = auth.currentUser
        if (user != null) {
            // Không lưu vào Firestore, chỉ cập nhật tạm thời
            _isSaved.value = true
            _isLoading.value = false
            Log.d("ProfileViewModel", "Đã lưu tạm thời: ${_userProfile.value}")
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
}