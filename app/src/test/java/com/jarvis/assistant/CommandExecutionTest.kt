package com.jarvis.assistant

import com.jarvis.assistant.commands.executor.CommandExecutor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandExecutionTest {

    private val executor = CommandExecutor()

    @Test
    fun `timer execution returns success`() {
        val result = executor.execute("TIMER", mapOf("duration" to "10", "unit" to "minutes"))
        assertTrue(result.success)
        assertTrue(result.response.contains("10 minutes"))
    }

    @Test
    fun `calculator addition works`() {
        val result = executor.execute("CALCULATOR", mapOf("expression" to "5 + 3"))
        assertTrue(result.success)
        assertEquals("= 8.0", result.response)
    }

    @Test
    fun `calculator multiplication works`() {
        val result = executor.execute("CALCULATOR", mapOf("expression" to "7 × 6"))
        assertTrue(result.success)
        assertEquals("= 42.0", result.response)
    }

    @Test
    fun `calculator division works`() {
        val result = executor.execute("CALCULATOR", mapOf("expression" to "100 / 4"))
        assertTrue(result.success)
        assertEquals("= 25.0", result.response)
    }

    @Test
    fun `unknown command returns failure`() {
        val result = executor.execute("UNKNOWN", emptyMap())
        assertTrue(!result.success)
    }
}
