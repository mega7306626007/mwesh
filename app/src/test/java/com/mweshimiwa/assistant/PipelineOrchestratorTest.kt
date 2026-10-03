package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.voice.PipelineOrchestrator
import com.mweshimiwa.assistant.voice.ResponseProcessor
import com.mweshimiwa.assistant.voice.SpeechOutput
import com.mweshimiwa.assistant.voice.TranscriptionProvider
import com.mweshimiwa.assistant.voice.VoicePipelineState
import com.mweshimiwa.assistant.voice.WakeWordProvider
import com.mweshimiwa.assistant.voice.speech.TranscriptionResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PipelineOrchestratorTest {

    private class FakeWakeWordProvider(var result: Boolean = true) : WakeWordProvider {
        var callCount = 0
        override suspend fun awaitWakeWord(): Boolean {
            callCount++
            return result
        }
    }

    private class FakeTranscriptionProvider(var result: TranscriptionResult?) : TranscriptionProvider {
        var callCount = 0
        override suspend fun listen(): TranscriptionResult? {
            callCount++
            return result
        }
    }

    private class FakeResponseProcessor(var response: String = "done") : ResponseProcessor {
        var received: String? = null
        override suspend fun process(transcription: String): String {
            received = transcription
            return response
        }
    }

    private class FakeSpeechOutput : SpeechOutput {
        val spoken = mutableListOf<String>()
        var stopCount = 0
        override fun speak(text: String) {
            spoken.add(text)
        }
        override fun stop() {
            stopCount++
        }
    }

    private fun transcription(text: String) = TranscriptionResult(text = text, confidence = 0.9f)

    @Test
    fun startListeningTransitionsFromIdle() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        assertEquals(VoicePipelineState.IDLE, orchestrator.state)
        assertTrue(orchestrator.startListening())
        assertEquals(VoicePipelineState.LISTENING, orchestrator.state)
    }

    @Test
    fun startListeningRejectedWhenAlreadyListening() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.startListening()
        assertFalse(orchestrator.startListening())
        assertEquals(VoicePipelineState.LISTENING, orchestrator.state)
    }

    @Test
    fun transcriptionMovesListeningToProcessing() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(transcription("hello")),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.startListening()
        assertTrue(orchestrator.onTranscriptionReceived(transcription("hello")))
        assertEquals(VoicePipelineState.PROCESSING, orchestrator.state)
    }

    @Test
    fun transcriptionRejectedFromIdle() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        assertFalse(orchestrator.onTranscriptionReceived(transcription("hello")))
        assertEquals(VoicePipelineState.IDLE, orchestrator.state)
    }

    @Test
    fun responseMovesProcessingToSpeaking() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor("answer"), FakeSpeechOutput()
        )
        orchestrator.startListening()
        orchestrator.onTranscriptionReceived(transcription("question"))
        assertTrue(orchestrator.onResponseReady("answer"))
        assertEquals(VoicePipelineState.SPEAKING, orchestrator.state)
    }

    @Test
    fun responseRejectedFromListening() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.startListening()
        assertFalse(orchestrator.onResponseReady("answer"))
        assertEquals(VoicePipelineState.LISTENING, orchestrator.state)
    }

    @Test
    fun speechCompleteMovesSpeakingToIdle() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.startListening()
        orchestrator.onTranscriptionReceived(transcription("hi"))
        orchestrator.onResponseReady("hello")
        assertTrue(orchestrator.onSpeechComplete())
        assertEquals(VoicePipelineState.IDLE, orchestrator.state)
    }

    @Test
    fun speechCompleteRejectedFromIdle() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        assertFalse(orchestrator.onSpeechComplete())
    }

    @Test
    fun failureMovesToErrorState() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.startListening()
        assertTrue(orchestrator.fail("network error"))
        assertEquals(VoicePipelineState.ERROR, orchestrator.state)
    }

    @Test
    fun failureRejectedFromIdle() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        assertFalse(orchestrator.fail("error"))
        assertEquals(VoicePipelineState.IDLE, orchestrator.state)
    }

    @Test
    fun resetMovesErrorToIdle() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.startListening()
        orchestrator.fail("error")
        assertTrue(orchestrator.reset())
        assertEquals(VoicePipelineState.IDLE, orchestrator.state)
    }

    @Test
    fun resetRejectedFromNonErrorState() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.startListening()
        assertFalse(orchestrator.reset())
    }

    @Test
    fun bargeInMovesSpeakingToListening() = runTest {
        val output = FakeSpeechOutput()
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), output
        )
        orchestrator.startListening()
        orchestrator.onTranscriptionReceived(transcription("hi"))
        orchestrator.onResponseReady("hello")
        assertTrue(orchestrator.bargeIn())
        assertEquals(VoicePipelineState.LISTENING, orchestrator.state)
        assertEquals(1, output.stopCount)
    }

    @Test
    fun bargeInRejectedFromIdle() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        assertFalse(orchestrator.bargeIn())
    }

    @Test
    fun fullCycleRunsSuccessfully() = runTest {
        val processor = FakeResponseProcessor("It is 3 pm")
        val output = FakeSpeechOutput()
        val wake = FakeWakeWordProvider(true)
        val transcriber = FakeTranscriptionProvider(transcription("what time is it"))
        val orchestrator = PipelineOrchestrator(wake, transcriber, processor, output)
        orchestrator.startListening()
        val completed = orchestrator.runCycle()
        assertTrue(completed)
        assertEquals(1, wake.callCount)
        assertEquals(1, transcriber.callCount)
        assertEquals("what time is it", processor.received)
        assertEquals(listOf("It is 3 pm"), output.spoken)
        assertEquals(VoicePipelineState.SPEAKING, orchestrator.state)
    }

    @Test
    fun cycleFailsWhenNoSpeechDetected() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(true), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.startListening()
        assertFalse(orchestrator.runCycle())
        assertEquals(VoicePipelineState.ERROR, orchestrator.state)
    }

    @Test
    fun cycleFailsOnBlankTranscription() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(true), FakeTranscriptionProvider(transcription("")),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.startListening()
        assertFalse(orchestrator.runCycle())
        assertEquals(VoicePipelineState.ERROR, orchestrator.state)
    }

    @Test
    fun cycleRejectedFromWrongState() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(true), FakeTranscriptionProvider(transcription("hi")),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        assertFalse(orchestrator.runCycle())
    }

    @Test
    fun stateChangeCallbackInvoked() = runTest {
        val transitions = mutableListOf<Pair<VoicePipelineState, VoicePipelineState>>()
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.onStateChange = { old, new -> transitions.add(old to new) }
        orchestrator.startListening()
        orchestrator.onTranscriptionReceived(transcription("hi"))
        orchestrator.onResponseReady("hello")
        orchestrator.onSpeechComplete()
        assertEquals(4, transitions.size)
        assertEquals(VoicePipelineState.IDLE to VoicePipelineState.LISTENING, transitions[0])
        assertEquals(VoicePipelineState.LISTENING to VoicePipelineState.PROCESSING, transitions[1])
        assertEquals(VoicePipelineState.PROCESSING to VoicePipelineState.SPEAKING, transitions[2])
        assertEquals(VoicePipelineState.SPEAKING to VoicePipelineState.IDLE, transitions[3])
    }

    @Test
    fun errorCallbackInvokedOnFailure() = runTest {
        var errorMessage: String? = null
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.onError = { errorMessage = it }
        orchestrator.startListening()
        orchestrator.fail("boom")
        assertEquals("boom", errorMessage)
    }

    @Test
    fun stopMovesToIdle() = runTest {
        val orchestrator = PipelineOrchestrator(
            FakeWakeWordProvider(), FakeTranscriptionProvider(null),
            FakeResponseProcessor(), FakeSpeechOutput()
        )
        orchestrator.startListening()
        orchestrator.stop()
        assertEquals(VoicePipelineState.IDLE, orchestrator.state)
    }
}
