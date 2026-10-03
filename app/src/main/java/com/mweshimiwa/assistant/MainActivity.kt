package com.mweshimiwa.assistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mweshimiwa.assistant.ui.conversation.ConversationScreen
import com.mweshimiwa.assistant.ui.conversation.ConversationViewModel
import com.mweshimiwa.assistant.ui.developer.DeveloperScreen
import com.mweshimiwa.assistant.ui.settings.SettingsScreen
import com.mweshimiwa.assistant.ui.theme.MweshimiwaTheme
import com.mweshimiwa.assistant.voice.speech.SpeechRecognizer
import com.mweshimiwa.assistant.voice.tts.MweshimiwaTts

class MainActivity : ComponentActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var mweshimiwaTts: MweshimiwaTts

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListening()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MweshimiwaApplication

        speechRecognizer = SpeechRecognizer(this)
        mweshimiwaTts = MweshimiwaTts(this)

        val viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ConversationViewModel(
                        conversationManager = app.conversationManager,
                        commandExecutor = app.commandExecutor
                    ) as T
                }
            }
        )[ConversationViewModel::class.java]

        setContent {
            val isDarkTheme by app.preferences.isDarkTheme.collectAsState(initial = true)

            MweshimiwaTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "conversation") {
                        composable("conversation") {
                            ConversationScreen(
                                viewModel = viewModel,
                                onMicRequested = { checkPermissionAndListen() }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                preferences = app.preferences,
                                onBack = { navController.popBackStack() },
                                onDeveloperClick = { navController.navigate("developer") },
                                onThemeClick = {},
                                onLanguageClick = {},
                                onVoiceClick = {}
                            )
                        }

                        composable("developer") {
                            val state by viewModel.conversationState.collectAsState()
                            DeveloperScreen(
                                modelState = state.modelState,
                                modelName = state.modelMetadata.name,
                                modelVersion = state.modelMetadata.version,
                                backend = state.modelMetadata.backend.name,
                                format = state.modelMetadata.format,
                                quantization = state.modelMetadata.quantization,
                                contextLength = state.modelMetadata.contextLength,
                                modelSize = formatSize(state.modelMetadata.modelSizeBytes),
                                loadTime = "${state.modelMetadata.loadTimeMs}ms",
                                tokensPerSecond = state.tokensPerSecond,
                                lastError = state.lastError,
                                onBack = { navController.popBackStack() },
                                onNetworkTest = {}
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkPermissionAndListen() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED -> {
                startListening()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startListening() {
        speechRecognizer.startListening(
            onResult = { text ->
                val viewModel = ViewModelProvider(this)[ConversationViewModel::class.java]
                viewModel.onVoiceResult(text)
            },
            onError = { error ->
            },
            onPartialResult = { text ->
                val viewModel = ViewModelProvider(this)[ConversationViewModel::class.java]
                viewModel.onVoicePartialResult(text)
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        mweshimiwaTts.shutdown()
    }

    private fun formatSize(bytes: Long): String {
        return when {
            bytes >= 1_073_741_824 -> "%.1f GB".format(bytes / 1_073_741_824.0)
            bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
            bytes >= 1024 -> "%.1f KB".format(bytes / 1024.0)
            else -> "$bytes B"
        }
    }
}
