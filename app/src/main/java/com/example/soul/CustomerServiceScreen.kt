package com.example.soul

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerServiceScreen(
    viewModel: CustomerServiceViewModel = viewModel()
) {
    var inputMessage by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    
    val messages = remember { mutableStateListOf<Pair<String, String>>() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is CustomerServiceUiState.Success -> {
                messages.add("AI 상담원" to (uiState as CustomerServiceUiState.Success).reply)
            }
            is CustomerServiceUiState.Error -> {
                messages.add("시스템" to "오류: ${(uiState as CustomerServiceUiState.Error).message}")
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("SOUL AI 고객센터") })
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("궁금한 점을 입력해 주세요...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Button(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            messages.add("나" to inputMessage)
                            viewModel.sendMessage(inputMessage)
                            inputMessage = ""
                        }
                    },
                    enabled = uiState !is CustomerServiceUiState.Loading
                ) {
                    Text("전송")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { (sender, text) ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (sender == "나") Alignment.End else Alignment.Start
                    ) {
                        Text(text = sender, style = MaterialTheme.typography.labelSmall)
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = if (sender == "나") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = text,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                if (uiState is CustomerServiceUiState.Loading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}
