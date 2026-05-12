package com.oscarjaime.gymtraker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Brand mark
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "GT",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "GymTraker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(24.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
fun LoginScreen(
    error: String?,
    onLogin: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthLayout(
        title = "GymTraker",
        subtitle = "Registra pesos, repeticiones e historico sin perder ritmo.",
        notice = error,
        isError = true
    ) {
        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Usuario o correo") },
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onLogin(identifier, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = identifier.isNotBlank() && password.isNotBlank()
        ) {
            Text(
                "Acceder",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
        Spacer(Modifier.height(4.dp))
        TextButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Crear cuenta",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun RegisterScreen(
    error: String?,
    onRegister: (String, String, String, String, String) -> Unit,
    onLoginClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthLayout(
        title = "Crear cuenta",
        subtitle = "Tu sesión se queda abierta después del alta.",
        notice = error,
        isError = true
    ) {
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre") },
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Apellido") },
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Usuario") },
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onRegister(firstName, lastName, email, username, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = listOf(firstName, lastName, email, username, password).all { it.isNotBlank() }
        ) {
            Text(
                "Registrarme",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
        Spacer(Modifier.height(4.dp))
        TextButton(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Ya tengo cuenta",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AuthLayout(
    title: String,
    subtitle: String,
    notice: String?,
    isError: Boolean,
    content: @Composable () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            // Brand mark
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "GT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(28.dp))
            if (notice != null) {
                Notice(text = notice, isError = isError)
                Spacer(Modifier.height(16.dp))
            }
            content()
        }
    }
}