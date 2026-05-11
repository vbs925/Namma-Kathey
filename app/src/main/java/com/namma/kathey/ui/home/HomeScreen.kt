package com.namma.kathey.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.kathey.data.model.Hero
import com.namma.kathey.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (String) -> Unit, vm: HomeViewModel = hiltViewModel()) {
    val heroes      by vm.heroes.collectAsState()
    val useKannada  by vm.useKannada.collectAsState()
    val districts   by vm.districts.collectAsState()
    val badgeCount  by vm.badgeCount.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()
    val searchRes   by vm.searchResults.collectAsState()

    var selectedDistrict by remember { mutableStateOf("All") }

    val displayed = when {
        searchQuery.isNotBlank() -> searchRes
        selectedDistrict == "All" -> heroes
        else -> heroes.filter { it.district == selectedDistrict }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (useKannada) "ನಮ್ಮ ಕಥೆ" else "Namma Kathey",
                            fontWeight = FontWeight.ExtraBold, fontSize = 20.sp
                        )
                        Text(
                            if (useKannada) "ಕರ್ನಾಟಕದ ವೀರ ನಾಯಕರ ಕಥೆ" else "Karnataka's Regional Hero Storybook",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { onNavigate(Screen.Badge.route) }) {
                        BadgeCountIcon(badgeCount)
                    }
                    IconButton(onClick = { onNavigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = {},
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text(if (useKannada) "ಮನೆ" else "Home") })
                NavigationBarItem(selected = false,
                    onClick = { onNavigate(Screen.Badge.route) },
                    icon = { Icon(Icons.Default.EmojiEvents, null) },
                    label = { Text(if (useKannada) "ಬ್ಯಾಡ್ಜ್" else "Badges") })
                NavigationBarItem(selected = false,
                    onClick = { onNavigate(Screen.AiGuide.route) },
                    icon = { Icon(Icons.Default.SmartToy, null) },
                    label = { Text(if (useKannada) "AI ಮಾರ್ಗದರ್ಶಿ" else "AI Guide") })
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pad),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = vm::onSearch,
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    placeholder = { Text(if (useKannada) "ನಾಯಕ ಹುಡುಕಿ..." else "Search heroes...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank())
                            IconButton(onClick = { vm.onSearch("") }) { Icon(Icons.Default.Clear, null) }
                    },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
            }
            // District filter
            if (searchQuery.isBlank()) {
                item {
                    LazyRow(contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(selected = selectedDistrict == "All",
                                onClick = { selectedDistrict = "All" },
                                label = { Text(if (useKannada) "ಎಲ್ಲ" else "All") })
                        }
                        items(districts) { d ->
                            FilterChip(selected = selectedDistrict == d,
                                onClick = { selectedDistrict = d },
                                label = { Text(d, maxLines = 1) })
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            // Banner
            item { HeroBanner(useKannada, badgeCount, heroes.size) }
            // Hero cards
            if (displayed.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(8.dp))
                            Text(if (useKannada) "ಲೋಡ್ ಆಗುತ್ತಿದೆ..." else "Loading heroes...")
                        }
                    }
                }
            } else {
                items(displayed) { hero ->
                    HeroCard(hero, useKannada) {
                        onNavigate(Screen.Story.createRoute(hero.id))
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeCountIcon(count: Int) {
    Box {
        Icon(Icons.Default.EmojiEvents, null, tint = MaterialTheme.colorScheme.onPrimary)
        if (count > 0) {
            Box(
                modifier = Modifier.size(16.dp).clip(CircleShape)
                    .background(Color(0xFFFFD700)).align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text("$count", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun HeroBanner(useKannada: Boolean, badgeCount: Int, heroCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6F00))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("🏛️", fontSize = 40.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (useKannada) "ಕರ್ನಾಟಕದ $heroCount ವೀರ ನಾಯಕರ ಕಥೆ ಓದಿ!"
                    else "Read stories of $heroCount Karnataka heroes!",
                    fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp
                )
                Text(
                    if (useKannada) "$badgeCount / $heroCount ಬ್ಯಾಡ್ಜ್ ಗಳಿಸಿದ್ದೀರಿ 🏅"
                    else "$badgeCount / $heroCount Heritage Badges earned! 🏅",
                    color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun HeroCard(hero: Hero, useKannada: Boolean, onClick: () -> Unit) {
    val cardColor = try { Color(android.graphics.Color.parseColor(hero.colorHex)) }
    catch (e: Exception) { MaterialTheme.colorScheme.primary }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(
                Modifier.width(80.dp).fillMaxHeight().background(cardColor),
                contentAlignment = Alignment.Center
            ) {
                Text(hero.emojiIcon, fontSize = 36.sp, modifier = Modifier.padding(vertical = 20.dp))
            }
            Column(Modifier.weight(1f).padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (useKannada) hero.nameKn else hero.nameEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(4.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = cardColor.copy(alpha = 0.15f)) {
                        Text(
                            if (useKannada) hero.categoryKn else hero.categoryEn,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = cardColor, fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(
                        " ${if (useKannada) hero.districtKn else hero.district} · ${hero.era}",
                        style = MaterialTheme.typography.bodySmall, color = Color.Gray
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    if (useKannada) hero.shortBioKn else hero.shortBioEn,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, color = Color.DarkGray
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AssistChip(onClick = onClick,
                        label = { Text(if (useKannada) "ಕಥೆ ಓದಿ" else "Read Story", style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = { Text("📖", fontSize = 12.sp) })
                }
            }
        }
    }
}
