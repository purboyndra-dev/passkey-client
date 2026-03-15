package com.example.presence.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.presence.data.UserResponse
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navHostController: NavHostController,
    email: String,
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchUserByEmail(email)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                uiState.error != null -> {
                    ErrorContent(
                        message = uiState.error ?: "An unexpected error occurred",
                        onRetry = { viewModel.fetchUserByEmail(email) }
                    )
                }

                uiState.user != null -> {
                    UserContent(user = uiState.user!!)
                }
            }
        }
    }
}

@Composable
fun UserContent(user: UserResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Personal Information",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        InfoTile(label = "Full Name", value = user.fullName)
        InfoTile(label = "Email", value = user.email)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = 0.3f
                )
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Role: ${user.role.uppercase()}", style = MaterialTheme.typography.labelLarge)
                Text("User ID: ${user.id}", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Joined: ${user.createdAt.substringBefore("T")}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun InfoTile(label: String, value: String) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall)
                Text(
                    value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Button(onClick = onRetry) {
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}
