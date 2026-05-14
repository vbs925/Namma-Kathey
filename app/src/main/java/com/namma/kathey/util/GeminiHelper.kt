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
        // Validation check for the API Key
        if (BuildConfig.GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE" || BuildConfig.GEMINI_API_KEY.isEmpty()) {
            return "Error: API Key is missing or not loaded. Please check your local.properties file and Rebuild the project."
        }

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
            val errorMsg = e.localizedMessage ?: "Unknown error"
            if (errorMsg.contains("API_KEY_INVALID", ignoreCase = true)) {
                "The API Key provided is invalid. Please double-check it in Google AI Studio. 🔑"
            } else {
                "Could not reach AI assistant. (${errorMsg})"
            }
        }
    }
}
