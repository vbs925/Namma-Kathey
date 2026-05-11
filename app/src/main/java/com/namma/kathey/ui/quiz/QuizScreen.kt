package com.namma.kathey.ui.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.namma.kathey.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(onBack: () -> Unit, onNavigate: (String) -> Unit, vm: QuizViewModel = hiltViewModel()) {
    val state      by vm.state.collectAsState()
    val useKannada by vm.useKannada.collectAsState()

    val hero = state.hero ?: return
    val cardColor = try { Color(android.graphics.Color.parseColor(hero.colorHex)) }
    catch (e: Exception) { MaterialTheme.colorScheme.primary }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (useKannada) "${hero.nameKn} — ಕ್ವಿಜ್" else "${hero.nameEn} — Quiz",
                        fontWeight = FontWeight.Bold
                    )
                },
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
        Box(Modifier.fillMaxSize().padding(pad)) {
            if (state.finished) {
                ResultScreen(state, useKannada, hero.emojiIcon, cardColor, onBack, onNavigate, hero.id)
            } else if (state.questions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                QuizContent(state, useKannada, cardColor, vm)
            }
        }
    }
}

@Composable
fun QuizContent(state: QuizState, useKannada: Boolean, cardColor: Color, vm: QuizViewModel) {
    val q = state.questions.getOrNull(state.currentIndex) ?: return

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = { (state.currentIndex + 1).toFloat() / state.questions.size },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = cardColor,
            trackColor = cardColor.copy(alpha = 0.2f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "${if (useKannada) "ಪ್ರಶ್ನೆ" else "Question"} ${state.currentIndex + 1} / ${state.questions.size}",
            style = MaterialTheme.typography.labelLarge,
            color = cardColor, fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(20.dp))

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("❓", fontSize = 32.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    if (useKannada) q.questionKn else q.questionEn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF3E2723)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Options
        val optionEmojis = listOf("🅐", "🅑", "🅒", "🅓")
        q.options.forEachIndexed { idx, option ->
            val isSelected  = state.selectedOption == idx
            val isCorrect   = idx == q.correctIndex
            val showResult  = state.answered

            val bgColor = when {
                showResult && isCorrect  -> Color(0xFF4CAF50)
                showResult && isSelected && !isCorrect -> Color(0xFFF44336)
                isSelected -> cardColor.copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.surface
            }
            val borderColor = when {
                showResult && isCorrect  -> Color(0xFF4CAF50)
                showResult && isSelected && !isCorrect -> Color(0xFFF44336)
                isSelected -> cardColor
                else -> Color.LightGray
            }
            val textColor = when {
                showResult && (isCorrect || (isSelected && !isCorrect)) -> Color.White
                else -> MaterialTheme.colorScheme.onSurface
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(bgColor)
                    .border(2.dp, borderColor, RoundedCornerShape(14.dp))
                    .clickable(enabled = !state.answered) { vm.selectOption(idx) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(optionEmojis.getOrElse(idx) { "•" }, fontSize = 20.sp)
                Spacer(Modifier.width(10.dp))
                Text(option, style = MaterialTheme.typography.bodyLarge, color = textColor, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                if (showResult && isCorrect) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                if (showResult && isSelected && !isCorrect) {
                    Icon(Icons.Default.Cancel, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }

        Spacer(Modifier.weight(1f))

        if (state.answered) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.selectedOption == state.questions[state.currentIndex].correctIndex)
                        Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (state.selectedOption == state.questions[state.currentIndex].correctIndex)
                            "🎉" else "💡",
                        fontSize = 24.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (state.selectedOption == state.questions[state.currentIndex].correctIndex)
                            if (useKannada) "ಅದ್ಭುತ! ಸರಿಯಾದ ಉತ್ತರ!" else "Excellent! Correct answer!"
                        else
                            if (useKannada) "ಸರಿಯಾದ ಉತ್ತರ: ${q.options[q.correctIndex]}" else "Correct: ${q.options[q.correctIndex]}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (state.selectedOption == state.questions[state.currentIndex].correctIndex)
                            Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = vm::next,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = cardColor)
            ) {
                Text(
                    if (state.currentIndex + 1 >= state.questions.size)
                        (if (useKannada) "ಫಲಿತಾಂಶ ನೋಡಿ" else "See Result")
                    else
                        (if (useKannada) "ಮುಂದಿನ ಪ್ರಶ್ನೆ" else "Next Question"),
                    fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, null)
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun ResultScreen(
    state: QuizState, useKannada: Boolean, emoji: String, cardColor: Color,
    onBack: () -> Unit, onNavigate: (String) -> Unit, heroId: Int
) {
    val total   = state.questions.size
    val score   = state.score
    val passed  = score == total
    val percent = if (total > 0) (score * 100) / total else 0

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (passed) "🏆" else "📚", fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            if (passed)
                if (useKannada) "ಅಭಿನಂದನೆ! ಬ್ಯಾಡ್ಜ್ ಗಳಿಸಿದಿರಿ!" else "Congratulations! Badge Earned!"
            else
                if (useKannada) "ಚೆನ್ನಾಗಿ ಪ್ರಯತ್ನ ಮಾಡಿದಿರಿ!" else "Good Try! Keep Learning!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (passed) Color(0xFF2E7D32) else Color(0xFFE65100)
        )
        Spacer(Modifier.height(8.dp))
        Text("$emoji", fontSize = 48.sp)
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = if (passed) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$score / $total",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (passed) Color(0xFF2E7D32) else cardColor
                )
                Text(
                    if (useKannada) "ಸರಿಯಾದ ಉತ್ತರ" else "Correct Answers",
                    style = MaterialTheme.typography.bodyMedium, color = Color.Gray
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { percent / 100f },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = if (passed) Color(0xFF4CAF50) else cardColor,
                    trackColor = Color.LightGray
                )
                Spacer(Modifier.height(6.dp))
                Text("$percent%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = if (passed) Color(0xFF2E7D32) else cardColor)
            }
        }

        if (passed && state.badgeAwarded) {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🏅", fontSize = 28.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (useKannada) "ಹೆರಿಟೇಜ್ ಬ್ಯಾಡ್ಜ್ ನಿಮ್ಮ ಪ್ರೊಫೈಲ್‌ಗೆ ಸೇರಿಸಲಾಗಿದೆ!"
                        else "Heritage Badge added to your profile!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5D4037)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onNavigate(Screen.Badge.route) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
        ) {
            Icon(Icons.Default.EmojiEvents, null)
            Spacer(Modifier.width(8.dp))
            Text(if (useKannada) "ನನ್ನ ಬ್ಯಾಡ್ಜ್‌ಗಳು" else "My Badges", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Home, null)
            Spacer(Modifier.width(8.dp))
            Text(if (useKannada) "ಮುಖ್ಯ ಪುಟಕ್ಕೆ" else "Back to Home", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
