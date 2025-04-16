package com.example.restaurantmanage.ui.theme.screens.assignment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PasswordScreen(navController: NavHostController, email: String) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Nhập mật khẩu",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Đăng nhập cho $email",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text(text = "Mật khẩu", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) android.R.drawable.ic_menu_view
                            else android.R.drawable.ic_secure
                        ),
                        contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiển thị mật khẩu",
                        tint = Color.Gray
                    )
                }
            },
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (password.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            val userId = authResult.user?.uid
                            if (userId != null) {
                                // Kiểm tra vai trò người dùng
                                firestore.collection("users").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        val role = document.getString("role") ?: "user"
                                        // Lưu thông tin nếu người dùng chưa có trong Firestore
                                        if (!document.exists()) {
                                            val userData = hashMapOf(
                                                "email" to email,
                                                "role" to "user"
                                            )
                                            firestore.collection("users").document(userId).set(userData)
                                        }
                                        // Điều hướng dựa trên vai trò
                                        val destination = if (role == "admin") "admin_screen" else "user_screen"
                                        navController.navigate(destination) {
                                            popUpTo("login_screen") { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        errorMessage = "Lỗi kiểm tra vai trò: ${e.message}"
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            errorMessage = when {
                                e.message?.contains("password is invalid") == true -> "Mật khẩu không đúng"
                                e.message?.contains("no user record") == true -> "Tài khoản không tồn tại"
                                else -> "Đăng nhập thất bại: ${e.message}"
                            }
                        }
                } else {
                    errorMessage = "Vui lòng nhập mật khẩu"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Đăng nhập",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text(
                text = "Quay lại",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordScreenPreview() {
    PasswordScreen(navController = NavHostController(LocalContext.current), email = "test@gmail.com")
}