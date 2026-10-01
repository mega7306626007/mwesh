package com.jarvis.assistant

import com.jarvis.assistant.commands.handlers.CalculatorCommandHandler
import com.jarvis.assistant.commands.handlers.TimeCommandHandler
import com.jarvis.assistant.commands.handlers.TimerCommandHandler
import com.jarvis.assistant.commands.router.CommandRouter
import com.jarvis.assistant.commands.router.CommandType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CommandRoutingTest {

    private val router = CommandRouter(
        listOf(
            TimerCommandHandler(),
            CalculatorCommandHandler(),
            TimeCommandHandler()
        )
    )

    @Test
    fun `timer command is recognized`() {
        val intent = router.route("Set a timer for 5 minutes")
        assertNotNull(intent)
        assertEquals(CommandType.TIMER, intent!!.command)
        assertEquals("5", intent.parameters["duration"])
        assertEquals("minutes", intent.parameters["unit"])
    }

    @Test
    fun `timer command with seconds`() {
        val intent = router.route("Timer for 30 seconds")
        assertNotNull(intent)
        assertEquals(CommandType.TIMER, intent!!.command)
        assertEquals("30", intent.parameters["duration"])
        assertEquals("seconds", intent.parameters["unit"])
    }

    @Test
    fun `calculator command is recognized`() {
        val intent = router.route("Calculate 78 × 42")
        assertNotNull(intent)
        assertEquals(CommandType.CALCULATOR, intent!!.command)
    }

    @Test
    fun `calculator with word expression`() {
        val intent = router.route("What is 15 + 27")
        assertNotNull(intent)
        assertEquals(CommandType.CALCULATOR, intent!!.command)
    }

    @Test
    fun `time command is recognized`() {
        val intent = router.route("What time is it?")
        assertNotNull(intent)
        assertEquals(CommandType.TIME, intent!!.command)
    }

    @Test
    fun `casual conversation is not a command`() {
        val intent = router.route("How are you doing today?")
        assertNull(intent)
    }

    @Test
    fun `multilingual input does not break router`() {
        val intent = router.route("Habari yako, how are you?")
        assertNull(intent)
    }

    @Test
    fun `sheng input does not break router`() {
        val intent = router.route("Niaje, what's up?")
        assertNull(intent)
    }

    @Test
    fun `empty input returns null`() {
        val intent = router.route("")
        assertNull(intent)
    }
}
