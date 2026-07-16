package com.example.find.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.find.ui.components.ErrorText
import com.example.find.ui.components.FindTopBar
import com.example.find.ui.theme.Forest
import com.example.find.ui.theme.Leaf
import com.example.find.ui.theme.Moss

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit
) {
    AuthFormScreen(
        title = "Entrar",
        subtitle = "Acede à tua conta Find",
        heroTitle = "Find",
        heroText = "Descobre e avalia bancos de jardim.",
        primaryLabel = "Entrar",
        secondaryLabel = "Criar conta",
        authViewModel = authViewModel,
        onSuccess = onLoginSuccess,
        onPrimary = { email, password -> authViewModel.login(email, password) },
        onSecondary = onNavigateToRegister,
        onNavigateBack = onNavigateBack,
        requireMinPassword = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit
) {
    AuthFormScreen(
        title = "Registo",
        subtitle = "Cria a tua conta",
        heroTitle = "Bem-vindo",
        heroText = "Reporta bancos e ajuda a comunidade.",
        primaryLabel = "Registar",
        secondaryLabel = "Já tenho conta",
        authViewModel = authViewModel,
        onSuccess = onRegisterSuccess,
        onPrimary = { email, password -> authViewModel.register(email, password) },
        onSecondary = onNavigateToLogin,
        onNavigateBack = onNavigateBack,
        requireMinPassword = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthFormScreen(
    title: String,
    subtitle: String,
    heroTitle: String,
    heroText: String,
    primaryLabel: String,
    secondaryLabel: String,
    authViewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onPrimary: (String, String) -> Unit,
    onSecondary: () -> Unit,
    onNavigateBack: () -> Unit,
    requireMinPassword: Boolean
) {
    val uiState by authViewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.session) {
        if (uiState.session != null) onSuccess()
    }

    val canSubmit = email.isNotBlank() &&
        if (requireMinPassword) password.length >= 6 else password.isNotBlank()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FindTopBar(
                title = title,
                subtitle = subtitle,
                onBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Forest, Moss, Leaf)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = heroTitle,
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = heroText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(if (requireMinPassword) "Password (mín. 6)" else "Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            ErrorText(uiState.error)

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(
                    onClick = { onPrimary(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = canSubmit,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(primaryLabel, style = MaterialTheme.typography.labelLarge)
                }
            }

            TextButton(
                onClick = onSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(secondaryLabel)
            }
        }
    }
}
