package com.jarvis.assistant

import android.app.Application
import com.jarvis.assistant.ai.context.ContextManager
import com.jarvis.assistant.ai.model.ModelConfig
import com.jarvis.assistant.ai.model.OnnxConversationModel
import com.jarvis.assistant.commands.executor.CommandExecutor
import com.jarvis.assistant.conversation.chat.ConversationManager
import com.jarvis.assistant.conversation.personality.JarvisPersonality
import com.jarvis.assistant.data.database.JarvisDatabase
import com.jarvis.assistant.data.preferences.JarvisPreferences
import com.jarvis.assistant.data.repositories.ConversationRepository
import com.jarvis.assistant.data.repositories.MemoryRepository
import com.jarvis.assistant.ui.conversation.ConversationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class JarvisApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    lateinit var preferences: JarvisPreferences
        private set

    lateinit var database: JarvisDatabase
        private set

    lateinit var conversationRepository: ConversationRepository
        private set

    lateinit var memoryRepository: MemoryRepository
        private set

    lateinit var personality: JarvisPersonality
        private set

    lateinit var conversationManager: ConversationManager
        private set

    lateinit var commandExecutor: CommandExecutor
        private set

    override fun onCreate() {
        super.onCreate()

        preferences = JarvisPreferences(this)
        database = JarvisDatabase.getInstance(this)
        conversationRepository = ConversationRepository(database.conversationDao())
        memoryRepository = MemoryRepository(database.memoryDao())
        personality = JarvisPersonality()
        commandExecutor = CommandExecutor()

        // Your own on-device LLM (models/model.onnx in assets).
        // Falls back to the mock brain if loading fails, so the app always boots.
        val model = OnnxConversationModel(
            appContext = this,
            config = ModelConfig(
                modelId = "jarvis-custom-v1",
                modelVersion = "1.0.0",
                backend = com.jarvis.assistant.ai.model.InferenceBackendType.ONNX,
                modelPath = "models/model.onnx",
                contextLength = 4096
            )
        )
        val contextManager = ContextManager(personality, 4096)

        conversationManager = ConversationManager(
            model = model,
            contextManager = contextManager,
            scope = applicationScope
        )

        applicationScope.launch {
            model.initialize()
        }
    }
}
