package com.namma.kathey.util

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.namma.kathey.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiHelper @Inject constructor() {
    private val model by lazy {
        GenerativeModel(modelName = "gemini-1.5-flash", apiKey = BuildConfig.GEMINI_API_KEY)
    }

    private val sysPrompt = """
        You are Namma Kathey AI — a storyteller guide for children about Karnataka's historical heroes.
        Rules:
        1. Keep answers short, child-friendly (age 8–14), engaging and fun.
        2. Use simple English. If user writes in Kannada, reply in Kannada too.
        3. Add 1-2 relevant emojis per response.
        4. Always end with an encouraging line about learning from heroes.
        5. Do NOT discuss anything unrelated to history, culture or education.
    """.trimIndent()

    suspend fun ask(question: String): String {
        return try {
            val chat = model.startChat(
                history = listOf(
                    content("user") { text(sysPrompt) },
                    content("model") { text("Namaskara! I am your Karnataka hero guide. Ask me anything! 🙏") }
                )
            )
            chat.sendMessage(question).text
                ?: "Hmm, I couldn't find an answer. Try asking differently!"
        } catch (e: Exception) {
            "Could not reach AI assistant. Check your API key and internet. (${e.localizedMessage})"
        }
    }
}
