package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.commands.handlers.AlarmCommandHandler
import com.mweshimiwa.assistant.commands.handlers.BatteryCommandHandler
import com.mweshimiwa.assistant.commands.handlers.CalculatorCommandHandler
import com.mweshimiwa.assistant.commands.handlers.CalendarCommandHandler
import com.mweshimiwa.assistant.commands.handlers.CallCommandHandler
import com.mweshimiwa.assistant.commands.handlers.ContactCommandHandler
import com.mweshimiwa.assistant.commands.handlers.DateCommandHandler
import com.mweshimiwa.assistant.commands.handlers.DeviceInfoCommandHandler
import com.mweshimiwa.assistant.commands.handlers.FlashlightCommandHandler
import com.mweshimiwa.assistant.commands.handlers.JokeCommandHandler
import com.mweshimiwa.assistant.commands.handlers.LocationCommandHandler
import com.mweshimiwa.assistant.commands.handlers.MathCommandHandler
import com.mweshimiwa.assistant.commands.handlers.MusicCommandHandler
import com.mweshimiwa.assistant.commands.handlers.NetworkCommandHandler
import com.mweshimiwa.assistant.commands.handlers.NoteCommandHandler
import com.mweshimiwa.assistant.commands.handlers.OpenAppCommandHandler
import com.mweshimiwa.assistant.commands.handlers.QuoteCommandHandler
import com.mweshimiwa.assistant.commands.handlers.RandomCommandHandler
import com.mweshimiwa.assistant.commands.handlers.ReminderCommandHandler
import com.mweshimiwa.assistant.commands.handlers.SearchCommandHandler
import com.mweshimiwa.assistant.commands.handlers.SendMessageCommandHandler
import com.mweshimiwa.assistant.commands.handlers.SettingsCommandHandler
import com.mweshimiwa.assistant.commands.handlers.SpeedTestCommandHandler
import com.mweshimiwa.assistant.commands.handlers.ScreenshotCommandHandler
import com.mweshimiwa.assistant.commands.handlers.TimeCommandHandler
import com.mweshimiwa.assistant.commands.handlers.TimerCommandHandler
import com.mweshimiwa.assistant.commands.handlers.TranslationCommandHandler
import com.mweshimiwa.assistant.commands.handlers.UnitConversionCommandHandler
import com.mweshimiwa.assistant.commands.handlers.WeatherCommandHandler
import com.mweshimiwa.assistant.commands.router.CommandHandler
import com.mweshimiwa.assistant.commands.router.CommandRegistry
import com.mweshimiwa.assistant.commands.router.CommandType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandHandlerV2Test {

    @Test
    fun allHandlerTypesAreUnique() {
        val handlers = listOf(
            AlarmCommandHandler(), BatteryCommandHandler(), CalculatorCommandHandler(),
            CalendarCommandHandler(), CallCommandHandler(), ContactCommandHandler(),
            DateCommandHandler(), DeviceInfoCommandHandler(), FlashlightCommandHandler(),
            JokeCommandHandler(), LocationCommandHandler(), MathCommandHandler(),
            MusicCommandHandler(), NetworkCommandHandler(), NoteCommandHandler(),
            OpenAppCommandHandler(), QuoteCommandHandler(), RandomCommandHandler(),
            ReminderCommandHandler(), SearchCommandHandler(), SendMessageCommandHandler(),
            SettingsCommandHandler(), SpeedTestCommandHandler(), ScreenshotCommandHandler(),
            TimeCommandHandler(), TimerCommandHandler(), TranslationCommandHandler(),
            UnitConversionCommandHandler(), WeatherCommandHandler()
        )
        val types = handlers.map { it.type }
        assertEquals(types.size, types.toSet().size)
    }

    @Test
    fun registryDispatchesToAllHandlers() {
        val registry = CommandRegistry()
        registry.register(AlarmCommandHandler())
        registry.register(CalculatorCommandHandler())
        registry.register(TimeCommandHandler())
        registry.register(SettingsCommandHandler())
        registry.register(OpenAppCommandHandler())
        registry.register(ReminderCommandHandler())
        registry.register(WeatherCommandHandler())
        registry.register(NoteCommandHandler())
        registry.register(UnitConversionCommandHandler())
        registry.register(TranslationCommandHandler())
        registry.register(SearchCommandHandler())
        registry.register(AlarmCommandHandler())
        registry.register(DateCommandHandler())
        registry.register(JokeCommandHandler())
        registry.register(QuoteCommandHandler())
        registry.register(MathCommandHandler())
        registry.register(DeviceInfoCommandHandler())

        assertEquals(CommandType.ALARM, registry.route("set alarm for 7 am")!!.command)
        assertEquals(CommandType.CALCULATOR, registry.route("calculate 2+2")!!.command)
        assertEquals(CommandType.TIME, registry.route("what time is it")!!.command)
        assertEquals(CommandType.SETTINGS, registry.route("turn on wifi")!!.command)
        assertEquals(CommandType.OPEN_APP, registry.route("open camera")!!.command)
        assertEquals(CommandType.REMINDER, registry.route("remind me to call mom")!!.command)
        assertEquals(CommandType.WEATHER, registry.route("what is the weather")!!.command)
        assertEquals(CommandType.NOTE, registry.route("take a note buy milk")!!.command)
        assertEquals(CommandType.UNIT_CONVERSION, registry.route("convert 5 km to miles")!!.command)
        assertEquals(CommandType.TRANSLATION, registry.route("translate hello to swahili")!!.command)
        assertEquals(CommandType.SEARCH, registry.route("search for kotlin")!!.command)
        assertEquals(CommandType.DATE, registry.route("what is the date")!!.command)
        assertEquals(CommandType.JOKE, registry.route("tell me a joke")!!.command)
        assertEquals(CommandType.QUOTE, registry.route("give me a quote")!!.command)
        assertEquals(CommandType.MATH, registry.route("15 percent of 200")!!.command)
        assertEquals(CommandType.DEVICE_INFO, registry.route("device info")!!.command)
    }

    @Test
    fun timerHandlerExtractsComplexParameters() {
        val handler = TimerCommandHandler()
        val params = handler.extractParameters("set a timer for 2 hours and 30 minutes")
        assertNotNull(params["duration"])
        assertTrue(handler.matches("set a timer for 5 minutes"))
        assertTrue(handler.matches("timer for 10 minutes"))
        assertTrue(handler.matches("countdown for 30 seconds"))
        assertFalse(handler.matches("what time is it"))
        assertFalse(handler.matches("calculate 2+2"))
    }

    @Test
    fun calculatorHandlerHandlesVariousExpressions() {
        val handler = CalculatorCommandHandler()
        assertTrue(handler.matches("calculate 2+2"))
        assertTrue(handler.matches("what is 10 times 5"))
        assertTrue(handler.matches("compute 100/4"))
        assertTrue(handler.matches("2+2"))
        assertFalse(handler.matches("set a timer"))
        assertFalse(handler.matches("what time is it"))
    }

    @Test
    fun weatherHandlerExtractsCityAndQuery() {
        val handler = WeatherCommandHandler()
        val params = handler.extractParameters("what is the weather in nairobi")
        assertEquals("nairobi", params["city"])
        assertTrue(handler.matches("what's the weather"))
        assertTrue(handler.matches("weather in london"))
        assertTrue(handler.matches("will it rain today"))
        assertFalse(handler.matches("what time is it"))
    }

    @Test
    fun musicHandlerMatchesAllActions() {
        val handler = MusicCommandHandler()
        assertTrue(handler.matches("play shape of you"))
        assertTrue(handler.matches("play music"))
        assertTrue(handler.matches("pause music"))
        assertTrue(handler.matches("next song"))
        assertTrue(handler.matches("skip track"))
        assertTrue(handler.matches("volume up"))
        assertFalse(handler.matches("movie night"))
    }

    @Test
    fun settingsHandlerMatchesAllSettings() {
        val handler = SettingsCommandHandler()
        assertTrue(handler.matches("turn on wifi"))
        assertTrue(handler.matches("turn off bluetooth"))
        assertTrue(handler.matches("set brightness to 50"))
        assertTrue(handler.matches("increase brightness"))
        assertTrue(handler.matches("set the volume to 30"))
        assertFalse(handler.matches("open settings menu"))
    }

    @Test
    fun flashlightHandlerMatchesOnOffPatterns() {
        val handler = FlashlightCommandHandler()
        assertTrue(handler.matches("turn on flashlight"))
        assertTrue(handler.matches("torch on"))
        assertTrue(handler.matches("turn off flashlight"))
        assertTrue(handler.matches("flashlight off"))
        assertTrue(handler.matches("toggle the torch"))
        assertEquals("on", handler.extractParameters("turn on the flashlight")["state"])
        assertEquals("off", handler.extractParameters("turn off the torch")["state"])
        assertFalse(handler.matches("flashlight app is cool"))
    }

    @Test
    fun contactHandlerMatchesContactOperations() {
        val handler = ContactCommandHandler()
        assertTrue(handler.matches("find contact mom"))
        assertTrue(handler.matches("search contacts for john"))
        assertTrue(handler.matches("who is alice"))
        assertTrue(handler.matches("text john saying hi"))
        assertFalse(handler.matches("settings menu"))
    }

    @Test
    fun calendarHandlerMatchesCalendarOperations() {
        val handler = CalendarCommandHandler()
        assertTrue(handler.matches("what's on my calendar"))
        assertTrue(handler.matches("list my events"))
        assertTrue(handler.matches("schedule meeting with team"))
        assertTrue(handler.matches("check my schedule"))
        assertFalse(handler.matches("whats the date"))
    }

    @Test
    fun alarmHandlerMatchesAlarmOperations() {
        val handler = AlarmCommandHandler()
        assertTrue(handler.matches("set alarm for 7 am"))
        assertTrue(handler.matches("alarm for 8 am"))
        assertTrue(handler.matches("wake me at 6:30"))
        assertTrue(handler.matches("show my alarms"))
        assertFalse(handler.matches("set a timer for 5 minutes"))
    }

    @Test
    fun randomHandlerMatchesRandomOperations() {
        val handler = RandomCommandHandler()
        assertTrue(handler.matches("random number"))
        assertTrue(handler.matches("pick a random number between 1 and 100"))
        assertTrue(handler.matches("flip a coin"))
        assertTrue(handler.matches("roll a dice"))
        assertEquals("coin", handler.extractParameters("flip a coin")["kind"])
        assertEquals("dice", handler.extractParameters("roll a dice")["kind"])
        assertFalse(handler.matches("randomize the playlist"))
    }

    @Test
    fun handlerNullRoutingReturnsNull() {
        val registry = CommandRegistry()
        registry.register(TimerCommandHandler())
        registry.register(CalculatorCommandHandler())
        assertNull(registry.route("xyzzy"))
        assertNull(registry.route(""))
        assertNull(registry.route("!!!"))
    }

    @Test
    fun handlerParameterExtractionEdgeCases() {
        val timer = TimerCommandHandler()
        val params = timer.extractParameters("set a timer for 1 hour")
        assertNotNull(params["duration"])
        assertNotNull(params["unit"])

        val calc = CalculatorCommandHandler()
        val calcParams = calc.extractParameters("calculate 100/4")
        assertEquals("100 / 4", calcParams["expression"])
    }

    @Test
    fun handlerMatchCaseInsensitivity() {
        val handler = TimeCommandHandler()
        assertTrue(handler.matches("What Time Is It"))
        assertTrue(handler.matches("WHAT TIME IS IT"))
        assertTrue(handler.matches("what time is it"))
    }

    @Test
    fun handlerMatchWithExtraWhitespace() {
        val handler = TimerCommandHandler()
        assertTrue(handler.matches("  set a timer for 5 minutes  "))
        assertTrue(handler.matches("set  a  timer  for  5  minutes"))
    }

    @Test
    fun registryOverwriteHandler() {
        val registry = CommandRegistry()
        registry.register(TimerCommandHandler())
        val first = registry.route("set a timer for 5 minutes")
        assertEquals(CommandType.TIMER, first!!.command)
        registry.register(TimerCommandHandler())
        val second = registry.route("set a timer for 5 minutes")
        assertEquals(CommandType.TIMER, second!!.command)
    }

    @Test
    fun allHandlersHaveNonNullType() {
        val handlers = listOf(
            AlarmCommandHandler(), BatteryCommandHandler(), CalculatorCommandHandler(),
            CalendarCommandHandler(), CallCommandHandler(), ContactCommandHandler(),
            DateCommandHandler(), DeviceInfoCommandHandler(), FlashlightCommandHandler(),
            JokeCommandHandler(), LocationCommandHandler(), MathCommandHandler(),
            MusicCommandHandler(), NetworkCommandHandler(), NoteCommandHandler(),
            OpenAppCommandHandler(), QuoteCommandHandler(), RandomCommandHandler(),
            ReminderCommandHandler(), SearchCommandHandler(), SendMessageCommandHandler(),
            SettingsCommandHandler(), SpeedTestCommandHandler(), ScreenshotCommandHandler(),
            TimeCommandHandler(), TimerCommandHandler(), TranslationCommandHandler(),
            UnitConversionCommandHandler(), WeatherCommandHandler()
        )
        for (handler in handlers) {
            assertNotNull("${handler.javaClass.simpleName} type should not be null", handler.type)
        }
    }
}
