package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.ai.model.MockConversationModel
import com.mweshimiwa.assistant.ai.model.ModelState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelLifecycleTest {

    @Test
    fun `mock model initializes to READY state`() = runTest {
        val model = MockConversationModel()
        model.initialize()
        assertTrue(model.isReady())
        assertEquals(ModelState.READY, model.getState())
    }

    @Test
    fun `mock model generates response`() = runTest {
        val model = MockConversationModel()
        model.initialize()

        val request = com.mweshimiwa.assistant.ai.model.ConversationRequest(
            systemPrompt = "test",
            messages = emptyList(),
            userMessage = "Hello"
        )

        val response = model.generate(request)
        assertTrue(response.text.isNotEmpty())
        assertFalse(response.wasCancelled)
    }

    @Test
    fun `mock model can be cancelled`() = runTest {
        val model = MockConversationModel()
        model.initialize()

        model.cancel()
        assertEquals(ModelState.READY, model.getState())
    }

    @Test
    fun `mock model releases correctly`() = runTest {
        val model = MockConversationModel()
        model.initialize()
        model.release()
        assertFalse(model.isReady())
        assertEquals(ModelState.UNINITIALIZED, model.getState())
    }

    @Test
    fun `mock model metadata is dev-labeled`() {
        val model = MockConversationModel()
        val metadata = model.getMetadata()
        assertTrue(metadata.name.contains("Mock") || metadata.name.contains("DEV"))
        assertEquals("mock", metadata.format)
    }
}
