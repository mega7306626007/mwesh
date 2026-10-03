package com.mweshimiwa.assistant

import android.app.Application
import com.mweshimiwa.assistant.ai.context.ContextManager
import com.mweshimiwa.assistant.ai.model.ModelConfig
import com.mweshimiwa.assistant.ai.model.OnnxConversationModel
import com.mweshimiwa.assistant.commands.executor.CommandExecutor
import com.mweshimiwa.assistant.conversation.chat.ConversationManager
import com.mweshimiwa.assistant.conversation.personality.MweshimiwaPersonality
import com.mweshimiwa.assistant.data.database.MweshimiwaDatabase
import com.mweshimiwa.assistant.data.preferences.MweshimiwaPreferences
import com.mweshimiwa.assistant.data.repositories.ConversationRepository
import com.mweshimiwa.assistant.data.repositories.MemoryRepository
import com.mweshimiwa.assistant.ui.conversation.ConversationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MweshimiwaApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    lateinit var preferences: MweshimiwaPreferences
        private set

    lateinit var database: MweshimiwaDatabase
        private set

    lateinit var conversationRepository: ConversationRepository
        private set

    lateinit var memoryRepository: MemoryRepository
        private set

    lateinit var personality: MweshimiwaPersonality
        private set

    lateinit var conversationManager: ConversationManager
        private set

    lateinit var commandExecutor: CommandExecutor
        private set

    override fun onCreate() {
        super.onCreate()

        preferences = MweshimiwaPreferences(this)
        database = MweshimiwaDatabase.getInstance(this)
        conversationRepository = ConversationRepository(database.conversationDao())
        memoryRepository = MemoryRepository(database.memoryDao())
        personality = MweshimiwaPersonality()
        commandExecutor = CommandExecutor()

        // Your own on-device LLM (models/model.onnx in assets).
        // Falls back to the mock brain if loading fails, so the app always boots.
        val model = OnnxConversationModel(
            appContext = this,
            config = ModelConfig(
                modelId = "mweshimiwa-custom-v1",
                modelVersion = "1.0.0",
                backend = com.mweshimiwa.assistant.ai.model.InferenceBackendType.ONNX,
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
