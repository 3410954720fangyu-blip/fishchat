package me.rerere.rikkahub.data.ai.tools

import android.content.Context
import androidx.core.content.FileProvider
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import me.rerere.ai.core.InputSchema
import me.rerere.ai.core.Tool
import me.rerere.ai.ui.UIMessagePart
import me.rerere.rikkahub.data.datastore.SystemToolsSetting
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

fun createTtsTool(context: Context, settings: SystemToolsSetting): Tool = Tool(
    name = "speak",
    description = "Convert text to speech audio. Use this tool ONLY when the user explicitly asks for a voice/audio reply; otherwise respond with normal text.",
    parameters = {
        InputSchema.Obj(
            properties = buildJsonObject {
                                                     putJsonObject("text") {
                    put("type", "string")
                    put("description", "The text content to convert to speech")
                }
            },
            required = listOf("text")
        )
    },
    execute = { args ->
        val params = args.jsonObject
        val text = params["text"]?.jsonPrimitive?.content ?: ""

        if (text.isBlank()) {
            return@Tool listOf(UIMessagePart.Text(
                buildJsonObject {
                    put("success", false)
                    put("error", "Text parameter is empty")
                }.toString()
            ))
        }

        val apiUrl = settings.ttsApiUrl
        val apiKey = settings.ttsApiKey
        val model = settings.ttsModel
        val voice = settings.ttsVoice

        if (apiUrl.isBlank() || apiKey.isBlank() || model.isBlank() || voice.isBlank()) {
            return@Tool listOf(UIMessagePart.Text(
                buildJsonObject {
                    put("success", false)
                    put("error", "TTS is not configured. Please set TTS API URL, API Key, Model, and Voice in system tools settings.")
                }.toString()
            ))
        }

        try {
            // Build request body (OpenAI TTS format)
            val requestBody = buildJsonObject {
                put("model", model)
                put("input", text)
                put("voice", voice)
            }.toString()

            // Make HTTP POST request
            val url = URL(apiUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $apiKey")
                doOutput = true
                connectTimeout = 30000
                readTimeout = 60000
            }

            connection.outputStream.use { os ->
                os.write(requestBody.toByteArray(Charsets.UTF_8))
                os.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                val errorBody = try {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                } catch (_: Exception) {
                    "HTTP $responseCode"
                }
                return@Tool listOf(UIMessagePart.Text(
                    buildJsonObject {
                        put("success", false)
                        put("error", "TTS API returned HTTP $responseCode: $errorBody")
                    }.toString()
                ))
            }

            // Read audio binary response and write to cache file
            val ttsDir = File(context.cacheDir, "tts")
            if (!ttsDir.exists()) ttsDir.mkdirs()

            val audioFile = File(ttsDir, "tts_${System.currentTimeMillis()}.mp3")
            connection.inputStream.use { input ->
                audioFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Generate FileProvider URI
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                audioFile
            )

            listOf(UIMessagePart.Audio(url = uri.toString()))
        } catch (e: Exception) {
            listOf(UIMessagePart.Text(
                buildJsonObject {
                    put("success", false)
                    put("error", "TTS failed: ${e.message ?: "Unknown error"}")
                }.toString()
            ))
        }
    }
)
