package com.namma.kathey.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiGuideScreen(onBack: () -> Unit, vm: AiGuideViewModel = hiltViewModel()) {
    val messages   by vm.messages.collectAsState()
    val loading    by vm.loading.collectAsState()
    val useKannada by vm.useKannada.collectAsState()
    var input      by remember { mutableStateOf("") }
    val listState  = rememberLazyListState()
    val scope      = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(if (useKannada) "AI ಮಾರ್ಗದರ್ಶಿ" else "AI Story Guide", fontWeight = FontWeight.Bold)
                        Text("Powered by Gemini", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                },
                actions = {
                    if (messages.isNotEmpty()) {
                        IconButton(onClick = vm::clear) {
                            Icon(Icons.Default.DeleteSweep, null, tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E), titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = input, onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                if (useKannada) "ಕರ್ನಾಟಕ ನಾಯಕರ ಬಗ್ಗೆ ಕೇಳಿ..."
                                else "Ask about Karnataka heroes...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        shape = RoundedCornerShape(24.dp), maxLines = 3, enabled = !loading
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            if (input.isNotBlank() && !loading) {
                                vm.send(input); input = ""
                            }
                        },
                        enabled = input.isNotBlank() && !loading,
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF1A237E))
                    ) { Icon(Icons.Default.Send, null) }
                }
            }
        }
    ) { pad ->
        if (messages.isEmpty() && !loading) {
            AiWelcome(useKannada) { vm.send(it) }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(pad).padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg -> AiBubble(msg) }
                if (loading) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (useKannada) "ಉತ್ತರ ಸಿದ್ಧವಾಗುತ್ತಿದೆ..." else "Thinking...",
                                style = MaterialTheme.typography.bodySmall, color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiBubble(msg: AiMessage) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!msg.isUser) {
            Text("🤖", fontSize = 22.sp, modifier = Modifier.padding(end = 4.dp, top = 4.dp))
        }
        Surface(
            shape = RoundedCornerShape(
                topStart = if (msg.isUser) 16.dp else 4.dp,
                topEnd = if (msg.isUser) 4.dp else 16.dp,
                bottomStart = 16.dp, bottomEnd = 16.dp
            ),
            color = if (msg.isUser) Color(0xFF1A237E) else Color(0xFFF3E5F5),
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                msg.text, modifier = Modifier.padding(12.dp),
                color = if (msg.isUser) Color.White else Color(0xFF1A237E),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AiWelcome(useKannada: Boolean, onSuggest: (String) -> Unit) {
    val suggestions = if (useKannada) listOf(
        "ಕಿತ್ತೂರು ಚೆನ್ನಮ್ಮ ಯಾರು?",
        "ಬಸವಣ್ಣ ಏನು ಮಾಡಿದರು?",
        "KRS ಅಣೆಕಟ್ಟು ಯಾರು ಕಟ್ಟಿದರು?",
        "ಕರ್ನಾಟಕದ ವೀರ ರಾಣಿ ಯಾರು?"
    ) else listOf(
        "Who was Kittur Chennamma?",
        "Tell me about Tippu Sultan's rockets",
        "Who founded Bengaluru city?",
        "What did Basavanna teach?"
    )

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🤖", fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            if (useKannada) "AI ಕಥಾ ಮಾರ್ಗದರ್ಶಿ" else "AI Story Guide",
            style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (useKannada) "ಕರ್ನಾಟಕದ ನಾಯಕರ ಬಗ್ಗೆ ಕನ್ನಡ ಅಥವಾ ಇಂಗ್ಲಿಷ್‌ನಲ್ಲಿ ಕೇಳಿ"
            else "Ask about Karnataka heroes in English or Kannada",
            style = MaterialTheme.typography.bodyMedium, color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        suggestions.forEach { q ->
            SuggestionChip(
                onClick = { onSuggest(q) },
                label = { Text(q, style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                icon = { Text("💡", fontSize = 14.sp) }
            )
        }
    }
}
