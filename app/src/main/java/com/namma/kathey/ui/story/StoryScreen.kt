package com.namma.kathey.ui.story

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.namma.kathey.data.model.Hero
import com.namma.kathey.ui.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StoryScreen(onNavigate: (String) -> Unit, onBack: () -> Unit, vm: StoryViewModel = hiltViewModel()) {
    val hero       by vm.hero.collectAsState()
    val useKannada by vm.useKannada.collectAsState()
    val hasBadge   by vm.hasBadge.collectAsState()

    if (hero == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val h = hero!!
    val cardColor = try { Color(android.graphics.Color.parseColor(h.colorHex)) }
    catch (e: Exception) { MaterialTheme.colorScheme.primary }

    val pages: List<String> = remember(useKannada) {
        try {
            val type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(if (useKannada) h.storyPagesKn else h.storyPagesEn, type)
        } catch (e: Exception) { listOf("Story coming soon!") }
    }

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (useKannada) h.nameKn else h.nameEn, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cardColor, titleContentColor = Color.White
                )
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            // Hero info header
            Box(Modifier.fillMaxWidth().background(cardColor).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(h.emojiIcon, fontSize = 48.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(h.era, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                        Text(
                            if (useKannada) h.categoryKn else h.categoryEn,
                            color = Color.White, fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            if (useKannada) h.districtKn else h.district,
                            color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Page indicator dots
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                pages.forEachIndexed { i, _ ->
                    Box(
                        Modifier.size(if (pagerState.currentPage == i) 10.dp else 7.dp)
                            .clip(CircleShape)
                            .background(if (pagerState.currentPage == i) cardColor else Color.LightGray)
                            .padding(2.dp)
                    )
                    if (i < pages.size - 1) Spacer(Modifier.width(4.dp))
                }
            }

            // Story pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                Card(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7))
                ) {
                    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Page ${page + 1} / ${pages.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = cardColor, fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                pages[page],
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp,
                                color = Color(0xFF3E2723)
                            )
                        }
                    }
                }
            }

            // Navigation row
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                    enabled = pagerState.currentPage > 0
                ) { Icon(Icons.Default.ArrowBack, null); Spacer(Modifier.width(4.dp)); Text(if (useKannada) "ಹಿಂದೆ" else "Back") }

                if (pagerState.currentPage == pages.size - 1) {
                    Button(
                        onClick = { onNavigate(Screen.Quiz.createRoute(h.id)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(if (useKannada) "ಕ್ವಿಜ್ ಆಡಿ!" else "Take Quiz!")
                        Spacer(Modifier.width(4.dp))
                        Text("🏅")
                    }
                } else {
                    Button(
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }
                    ) { Text(if (useKannada) "ಮುಂದೆ" else "Next"); Spacer(Modifier.width(4.dp)); Icon(Icons.Default.ArrowForward, null) }
                }
            }

            // Statue finder button
            OutlinedButton(
                onClick = {
                    val uri = "geo:${h.statueLatitude},${h.statueLongitude}?q=${h.statueLocation}"
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
                    // Will be handled via context in real app
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(6.dp))
                Text(
                    if (useKannada) "ಪ್ರತಿಮೆ ಸ್ಥಳ ನೋಡಿ" else "Find Statue Location",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
