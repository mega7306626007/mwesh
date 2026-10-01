package com.jarvis.assistant

import com.jarvis.assistant.commands.handlers.CalculatorCommandHandler
import com.jarvis.assistant.commands.handlers.DateCommandHandler
import com.jarvis.assistant.commands.handlers.JokeCommandHandler
import com.jarvis.assistant.commands.handlers.OpenAppCommandHandler
import com.jarvis.assistant.commands.handlers.ReminderCommandHandler
import com.jarvis.assistant.commands.handlers.TimerCommandHandler
import com.jarvis.assistant.commands.handlers.TimeCommandHandler
import com.jarvis.assistant.commands.router.CommandIntent
import com.jarvis.assistant.commands.router.CommandRegistry
import com.jarvis.assistant.commands.router.CommandType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandRegistryTest {

    private fun populatedRegistry(): CommandRegistry {
        val registry = CommandRegistry()
        registry.register(TimerCommandHandler())
        registry.register(CalculatorCommandHandler())
        registry.register(TimeCommandHandler())
        registry.register(ReminderCommandHandler())
        registry.register(OpenAppCommandHandler())
        registry.register(DateCommandHandler())
        registry.register(JokeCommandHandler())
        return registry
    }

    @Test
    fun registerAddsHandlers() {
        val registry = populatedRegistry()
        assertEquals(7, registry.handlerCount())
    }

    @Test
    fun duplicateRegistrationRejected() {
        val registry = CommandRegistry()
        val handler = TimerCommandHandler()
        assertTrue(registry.register(handler))
        assertTrue(!registry.register(handler))
        assertEquals(1, registry.handlerCount())
    }

    @Test
    fun unregisterRemovesHandler() {
        val registry = populatedRegistry()
        val handler = registry.findByType(CommandType.TIMER)!!
        assertTrue(registry.unregister(handler))
        assertNull(registry.findByType(CommandType.TIMER))
        assertEquals(6, registry.handlerCount())
    }

    @Test
    fun unregisterUnknownHandlerReturnsFalse() {
        val registry = populatedRegistry()
        assertTrue(!registry.unregister(TimerCommandHandler()))
    }

    @Test
    fun disabledHandlerDoesNotRoute() {
        val registry = populatedRegistry()
        val handler = registry.findByType(CommandType.TIMER)!!
        registry.disable(handler)
        assertNull(registry.route("set a timer for 5 minutes"))
        assertEquals(6, registry.enabledCount())
    }

    @Test
    fun enabledHandlerRoutesAgain() {
        val registry = populatedRegistry()
        val handler = registry.findByType(CommandType.TIMER)!!
        registry.disable(handler)
        registry.enable(handler)
        val intent = registry.route("set a timer for 5 minutes")
        assertNotNull(intent)
        assertEquals(CommandType.TIMER, intent!!.command)
    }

    @Test
    fun isEnabledReflectsState() {
        val registry = populatedRegistry()
        val handler = registry.findByType(CommandType.JOKE)!!
        assertTrue(registry.isEnabled(handler))
        registry.disable(handler)
        assertTrue(!registry.isEnabled(handler))
    }

    @Test
    fun routeReturnsCommandIntent() {
        val registry = populatedRegistry()
        val intent = registry.route("calculate 2+2")!!
        assertEquals(CommandType.CALCULATOR, intent.command)
        assertEquals(1.0f, intent.confidence, 0.0001f)
        assertEquals("calculate 2+2", intent.rawInput)
    }

    @Test
    fun routeExtractsParameters() {
        val registry = populatedRegistry()
        val intent = registry.route("set a timer for 5 minutes")!!
        assertEquals("5", intent.parameters["duration"])
        assertEquals("minutes", intent.parameters["unit"])
    }

    @Test
    fun routeUnknownInputReturnsNull() {
        val registry = populatedRegistry()
        assertNull(registry.route("hello there"))
        assertNull(registry.route("xyzzy"))
    }

    @Test
    fun routeOnlyConsidersEnabledHandlers() {
        val registry = populatedRegistry()
        registry.getEnabledHandlers().forEach { registry.disable(it) }
        assertEquals(0, registry.enabledCount())
        assertNull(registry.route("set a timer for 5 minutes"))
    }

    @Test
    fun statsTrackRouteCounts() {
        val registry = populatedRegistry()
        registry.route("set a timer for 5 minutes")
        registry.route("set a timer for 10 minutes")
        registry.route("calculate 2+2")
        assertEquals(2, registry.getRouteCount(CommandType.TIMER))
        assertEquals(1, registry.getRouteCount(CommandType.CALCULATOR))
        assertEquals(0, registry.getRouteCount(CommandType.JOKE))
    }

    @Test
    fun statsDisabledHandlerNotCounted() {
        val registry = populatedRegistry()
        val handler = registry.findByType(CommandType.TIMER)!!
        registry.disable(handler)
        registry.route("set a timer for 5 minutes")
        assertEquals(0, registry.getRouteCount(CommandType.TIMER))
    }

    @Test
    fun clearStatsResetsCounts() {
        val registry = populatedRegistry()
        registry.route("set a timer for 5 minutes")
        registry.clearStats()
        assertTrue(registry.getStats().isEmpty())
        assertEquals(0, registry.getRouteCount(CommandType.TIMER))
    }

    @Test
    fun getStatsReturnsCopy() {
        val registry = populatedRegistry()
        registry.route("set a timer for 5 minutes")
        val stats = registry.getStats()
        assertEquals(1, stats[CommandType.TIMER.name])
    }

    @Test
    fun findByTypeReturnsCorrectHandler() {
        val registry = populatedRegistry()
        assertEquals(CommandType.TIME, registry.findByType(CommandType.TIME)!!.type)
        assertEquals(CommandType.REMINDER, registry.findByType(CommandType.REMINDER)!!.type)
    }

    @Test
    fun getHandlersReturnsAllRegistered() {
        val registry = populatedRegistry()
        val types = registry.getHandlers().map { it.type }.toSet()
        assertTrue(types.contains(CommandType.TIMER))
        assertTrue(types.contains(CommandType.CALCULATOR))
        assertTrue(types.contains(CommandType.OPEN_APP))
    }

    @Test
    fun intentCarriesRawInput() {
        val registry = populatedRegistry()
        val raw = "  CALCULATE   10+5  "
        val intent: CommandIntent? = registry.route(raw)
        assertNotNull(intent)
        assertEquals(raw, intent!!.rawInput)
    }

    @Test
    fun emptyRegistryRoutesNothing() {
        val registry = CommandRegistry()
        assertNull(registry.route("anything"))
        assertEquals(0, registry.handlerCount())
        assertEquals(0, registry.enabledCount())
    }
}
