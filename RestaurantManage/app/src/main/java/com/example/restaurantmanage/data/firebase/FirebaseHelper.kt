package com.example.restaurantmanage.data.firebase

import android.util.Log
import com.example.restaurantmanage.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper class cho Firebase operations. Sử dụng non-static methods thay vì static fields
 * để tránh memory leaks liên quan đến Context.
 */
class FirebaseHelper {
    // Sử dụng lazy initialization cho các Firebase instances
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val usersCollection by lazy { firestore.collection("users") }
    
    // Lấy danh sách user từ Firestore
    suspend fun getAllUsers(): List<User> {
        return try {
            val querySnapshot = usersCollection.get().await()
            querySnapshot.documents.mapNotNull { document ->
                val id = document.id
                val name = document.getString("name") ?: ""
                val email = document.getString("email") ?: ""
                val phone = document.getString("phone") ?: ""
                val role = document.getString("role") ?: "CUSTOMER"
                val status = document.getString("status") ?: "ACTIVE"
                val createdAt = document.getString("createdAt") ?: ""

                User(
                    id = id,
                    name = name,
                    email = email,
                    phone = phone,
                    role = role,
                    status = status,
                    createdAt = createdAt
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi lấy danh sách người dùng", e)
            emptyList()
        }
    }
    
    // Cập nhật trạng thái người dùng
    suspend fun updateUserStatus(userId: String, newStatus: String): Boolean {
        return try {
            usersCollection.document(userId)
                .update("status", newStatus)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi cập nhật trạng thái người dùng", e)
            false
        }
    }
    
    // Cập nhật vai trò người dùng
    suspend fun updateUserRole(userId: String, newRole: String): Boolean {
        return try {
            usersCollection.document(userId)
                .update("role", newRole)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi cập nhật vai trò người dùng", e)
            false
        }
    }
    
    // Cập nhật số điện thoại người dùng
    suspend fun updateUserPhone(userId: String, phone: String): Boolean {
        return try {
            usersCollection.document(userId)
                .update("phone", phone)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi cập nhật số điện thoại người dùng", e)
            false
        }
    }
    
    // Xóa người dùng (cả Firestore và Authentication)
    suspend fun deleteUser(userId: String): Boolean {
        return try {
            // Xóa dữ liệu từ Firestore trước
            usersCollection.document(userId).delete().await()
            
            // Lưu ý: Xóa người dùng từ Firebase Authentication cần quyền Admin
            // Đây chỉ là xóa từ Firestore
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi xóa người dùng", e)
            false
        }
    }
    
    // Thiết lập dữ liệu người dùng mới khi đăng ký
    suspend fun setupNewUser(firebaseUser: FirebaseUser, role: String = "USER"): Boolean {
        return try {
            val userData = hashMapOf(
                "name" to (firebaseUser.displayName ?: ""),
                "email" to (firebaseUser.email ?: ""),
                "phone" to (firebaseUser.phoneNumber ?: ""),
                "role" to role,
                "status" to "ACTIVE",
                "createdAt" to getCurrentDateTime()
            )
            
            usersCollection.document(firebaseUser.uid)
                .set(userData)
                .await()
                
            true
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi thiết lập người dùng mới", e)
            false
        }
    }
    
    // Kiểm tra vai trò của người dùng hiện tại
    suspend fun getCurrentUserRole(): String {
        val currentUser = auth.currentUser ?: return ""
        
        return try {
            val document = usersCollection.document(currentUser.uid).get().await()
            document.getString("role") ?: "CUSTOMER"
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi lấy vai trò người dùng", e)
            "CUSTOMER"
        }
    }
    
    // Lấy định dạng ngày giờ hiện tại
    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
    
    companion object {
        private const val TAG = "FirebaseHelper"
        
        // Instance duy nhất của FirebaseHelper (Singleton pattern)
        @Volatile
        private var instance: FirebaseHelper? = null
        
        fun getInstance(): FirebaseHelper {
            return instance ?: synchronized(this) {
                instance ?: FirebaseHelper().also { instance = it }
            }
        }
    }
} 