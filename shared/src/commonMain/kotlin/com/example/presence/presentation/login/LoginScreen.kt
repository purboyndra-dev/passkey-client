package com.example.presence.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.presence.MainRoute
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navHostController: NavHostController,
    viewModel: LoginViewModel = koinViewModel()
) {

    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val uisState by viewModel.uiState.collectAsStateWithLifecycle()

    var enabled by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uisState) {
        if (uisState.success != null) {
            navHostController.navigate(
                MainRoute(
                    "${userName}@gmail.com"
                )
            )
            snackbarHostState
                .showSnackbar(
                    message = "Successfully login with passkeys!",
                )

        } else if (uisState.error != null) {
            snackbarHostState
                .showSnackbar(
                    message = uisState.error!!,
                )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sign In", style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
//                navigationIcon = {
//                    IconButton(onClick = { navHostController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).padding(
                horizontal = 12.dp,
                vertical = 16.dp
            )
        ) {
            item {
                Text(
                    "Username", style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    value = userName,
                    onValueChange = {
                        viewModel.setUserName(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth().background(
                                    color = Color.LightGray.copy(
                                        alpha = 0.3f,
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ).padding(12.dp)
                        ) {
                            if (userName.isBlank()) {
                                Text(
                                    text = "Enter username",
                                    style = TextStyle(
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.Both
                                        )
                                    )
                                )
                            }
                            innerTextField.invoke()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Signing In", style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().background(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                ) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = buildAnnotatedString {
                                    append(
                                        "A passkey is a faster and safer way to sign in than a password. Your account is created with one unless you choose another option "
                                    )
                                    append(".")
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        append(" How do passkeys work?")
                                    }
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Normal
                                )
                            )

                            TextButton(
                                onClick = {},
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    "Other ways to sign in",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier.width(64.dp).height(64.dp).background(
                                color = Color.Red.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                ElevatedButton(
                    onClick = {
                        viewModel.loginOptions()
                    },
                    enabled = !uisState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uisState.isLoading) CircularProgressIndicator() else
                        Text("Sign In")
                }
            }
        }
    }
}
