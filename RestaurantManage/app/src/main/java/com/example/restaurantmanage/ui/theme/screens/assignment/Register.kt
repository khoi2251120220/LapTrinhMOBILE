package com.example.restaurantmanage.ui.theme.screens.assignment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
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
            text = "Quản lý nhà hàng\nBooking",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Đăng Ký",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Vui lòng nhập đầy đủ thông tin!!!",
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
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Nhập tài khoản gmail") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(8.dp),
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Nhập mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = { Text("Xác nhận mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = ""
                when {
                    email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        errorMessage = "Vui lòng nhập email hợp lệ"
                    }
                    password.isEmpty() || password.length < 6 -> {
                        errorMessage = "Mật khẩu phải có ít nhất 6 ký tự"
                    }
                    password != confirmPassword -> {
                        errorMessage = "Mật khẩu xác nhận không khớp"
                    }
                    else -> {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { authResult ->
                                val userId = authResult.user?.uid
                                if (userId != null) {
                                    val userData = hashMapOf(
                                        "email" to email,
                                        "role" to "user"
                                    )
                                    firestore.collection("users").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            auth.currentUser?.sendEmailVerification()
                                            errorMessage = "Vui lòng kiểm tra email để xác nhận tài khoản"
                                            navController.navigate("login_screen") {
                                                popUpTo("register_screen") { inclusive = true }
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            errorMessage = "Lỗi lưu thông tin người dùng: ${e.message}"
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                errorMessage = when {
                                    e.message?.contains("email address is already in use") == true -> "Email đã được sử dụng"
                                    else -> "Đăng ký thất bại: ${e.message}"
                                }
                            }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Tiếp Tục",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = NavHostController(LocalContext.current))
}