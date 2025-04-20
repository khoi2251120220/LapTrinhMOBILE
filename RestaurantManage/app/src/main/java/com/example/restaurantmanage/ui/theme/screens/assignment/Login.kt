package com.example.restaurantmanage.ui.theme.screens.assignment

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.restaurantmanage.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "GoogleSignIn"

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        FirebaseApp.initializeApp(context)
    }
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val signInClient: SignInClient = Identity.getSignInClient(context)

    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("752190148410-0k1t2seckuv01u47sdefhp69ot843g4m.apps.googleusercontent.com")
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val credential = signInClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener { authResult ->
                        Log.d(TAG, "signInWithCredential:success")
                        val userId = authResult.user?.uid
                        if (userId != null) {
                            // Kiểm tra vai trò người dùng
                            firestore.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    val role = document.getString("role") ?: "user"
                                    // Lưu thông tin nếu người dùng chưa có trong Firestore
                                    if (!document.exists()) {
                                        val userData = hashMapOf(
                                            "email" to auth.currentUser?.email,
                                            "role" to "user"
                                        )
                                        firestore.collection("users").document(userId).set(userData)
                                    }
                                    // Điều hướng dựa trên vai trò
                                    val destination = if (role == "admin") "admin_screen" else "user_screen"
                                    navController.navigate(destination) {
                                        popUpTo("login_screen") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Failed to fetch user role", e)
                                    errorMessage = "Lỗi kiểm tra vai trò: ${e.message}"
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "signInWithCredential:failure", e)
                        errorMessage = "Đăng nhập Google thất bại: ${e.message}"
                    }
            } else {
                Log.w(TAG, "No ID token!")
                errorMessage = "Không tìm thấy token Google"
            }
        } catch (e: Exception) {
            Log.w(TAG, "Sign in with Google failed", e)
            errorMessage = "Lỗi: ${e.message}"
        }
    }

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
            text = "Đăng nhập",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Nhập tài khoản gmail để đăng nhập",
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
            placeholder = { Text(text = "...@gmail.com", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    navController.navigate("password_screen/$email")
                    errorMessage = ""
                } else {
                    errorMessage = "Vui lòng nhập email hợp lệ"
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
                text = "Tiếp tục",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                color = Color.Gray
            )
            Text(
                text = "or",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            )
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        OutlinedButton(
            onClick = {
                signInClient.beginSignIn(signInRequest)
                    .addOnSuccessListener { result ->
                        Log.d(TAG, "Begin sign in successful")
                        try {
                            launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                        } catch (e: Exception) {
                            Log.w(TAG, "Couldn't start Sign In: ${e.localizedMessage}", e)
                            errorMessage = "Lỗi khởi động đăng nhập Google"
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Begin sign in failed", e)
                        errorMessage = "Đăng nhập Google thất bại: ${e.message}"
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gg),
                    contentDescription = "Google Icon",
                    modifier = Modifier
                        .size(28.dp)
                        .padding(end = 8.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = "Tiếp tục với Google",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bấm tiếp tục khi bạn đồng ý với Điều khoản dịch vụ và Chính sách bảo mật của chúng tôi",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Chưa có tài khoản? ",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            )
            Text(
                text = "Đăng ký",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable {
                    navController.navigate("register_screen")
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = NavHostController(LocalContext.current))
}