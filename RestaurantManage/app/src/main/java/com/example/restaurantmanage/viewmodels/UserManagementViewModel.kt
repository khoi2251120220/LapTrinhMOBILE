package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.firebase.FirebaseHelper
import com.example.restaurantmanage.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserManagementViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usersList = FirebaseHelper.getAllUsers()
                _users.value = usersList
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi tải dữ liệu: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun updateUserStatus(userId: String, newStatus: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = FirebaseHelper.updateUserStatus(userId, newStatus)
                
                if (success) {
                    // Cập nhật danh sách người dùng sau khi thay đổi
                    _users.value = _users.value.map { user ->
                        if (user.id == userId) {
                            user.copy(status = newStatus)
                        } else {
                            user
                        }
                    }
                } else {
                    _errorMessage.value = "Không thể cập nhật trạng thái người dùng"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi cập nhật trạng thái: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = FirebaseHelper.updateUserRole(userId, newRole)
                
                if (success) {
                    // Cập nhật danh sách người dùng sau khi thay đổi
                    _users.value = _users.value.map { user ->
                        if (user.id == userId) {
                            user.copy(role = newRole)
                        } else {
                            user
                        }
                    }
                } else {
                    _errorMessage.value = "Không thể cập nhật vai trò người dùng"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi cập nhật vai trò: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = FirebaseHelper.deleteUser(userId)
                
                if (success) {
                    // Cập nhật danh sách người dùng
                    _users.value = _users.value.filter { it.id != userId }
                } else {
                    _errorMessage.value = "Không thể xóa người dùng"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi xóa người dùng: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = ""
    }
}

class UserManagementViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserManagementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserManagementViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 