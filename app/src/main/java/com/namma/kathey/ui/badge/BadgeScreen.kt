package com.namma.kathey.ui.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeScreen(onBack: () -> Unit, vm: BadgeViewModel = hiltViewModel()) {
    val badgeItems  by vm.badgeItems.collectAsState()
    val useKannada  by vm.useKannada.collectAsState()
    val totalHeroes by vm.totalHeroes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (useKannada) "ಹೆರಿಟೇಜ್ ಬ್ಯಾಡ್ಜ್‌ಗಳು" else "Heritage Badges",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B5E20), titleContentColor = Color.White
                )
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏆", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${badgeItems.size} / $totalHeroes",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            if (useKannada) "ಹೆರಿಟೇಜ್ ಬ್ಯಾಡ್ಜ್‌ಗಳು ಗಳಿಸಿದ್ದೀರಿ" else "Heritage Badges Earned",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { if (totalHeroes > 0) badgeItems.size.toFloat() / totalHeroes else 0f },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                            color = Color(0xFFFFD700),
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            if (badgeItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Column(
                            Modifier.padding(32.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📚", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                if (useKannada) "ಇನ್ನೂ ಬ್ಯಾಡ್ಜ್ ಗಳಿಸಿಲ್ಲ!\nನಾಯಕರ ಕಥೆ ಓದಿ ಕ್ವಿಜ್ ಆಡಿ!"
                                else "No badges yet!\nRead hero stories and take quizzes to earn badges!",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        if (useKannada) "ಗಳಿಸಿದ ಬ್ಯಾಡ್ಜ್‌ಗಳು" else "Earned Badges",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }
                items(badgeItems) { item ->
                    BadgeCard(item, useKannada)
                }
            }
        }
    }
}

@Composable
fun BadgeCard(item: BadgeUiItem, useKannada: Boolean) {
    val cardColor = try { Color(android.graphics.Color.parseColor(item.hero.colorHex)) }
    catch (e: Exception) { MaterialTheme.colorScheme.primary }

    val dateStr = remember(item.badge.earnedAt) {
        SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date(item.badge.earnedAt))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(60.dp).clip(CircleShape).background(cardColor),
                contentAlignment = Alignment.Center
            ) {
                Text(item.hero.emojiIcon, fontSize = 28.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (useKannada) item.hero.nameKn else item.hero.nameEn,
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold
                )
                Text(
                    if (useKannada) item.hero.districtKn else item.hero.district,
                    style = MaterialTheme.typography.bodySmall, color = Color.Gray
                )
                Text(
                    "${if (useKannada) "ಗಳಿಸಿದ ದಿನ" else "Earned"}: $dateStr",
                    style = MaterialTheme.typography.labelSmall, color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏅", fontSize = 28.sp)
                Text(
                    if (useKannada) "ಬ್ಯಾಡ್ಜ್" else "Badge",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
