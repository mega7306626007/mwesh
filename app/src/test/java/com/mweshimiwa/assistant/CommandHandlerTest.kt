package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.commands.handlers.AlarmCommandHandler
import com.mweshimiwa.assistant.commands.handlers.AppLaunchCommandHandler
import com.mweshimiwa.assistant.commands.handlers.BatteryCommandHandler
import com.mweshimiwa.assistant.commands.handlers.CalculatorCommandHandler
import com.mweshimiwa.assistant.commands.handlers.CallCommandHandler
import com.mweshimiwa.assistant.commands.handlers.CalendarCommandHandler
import com.mweshimiwa.assistant.commands.handlers.ContactCommandHandler
import com.mweshimiwa.assistant.commands.handlers.DateCommandHandler
import com.mweshimiwa.assistant.commands.handlers.DeviceInfoCommandHandler
import com.mweshimiwa.assistant.commands.handlers.FlashlightCommandHandler
import com.mweshimiwa.assistant.commands.handlers.JokeCommandHandler
import com.mweshimiwa.assistant.commands.handlers.LocationCommandHandler
import com.mweshimiwa.assistant.commands.handlers.MathCommandHandler
import com.mweshimiwa.assistant.commands.handlers.MusicCommandHandler
import com.mweshimiwa.assistant.commands.handlers.NetworkCommandHandler
import com.mweshimiwa.assistant.commands.handlers.NoneCommandHandler
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
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandHandlerTest {

    private fun assertMatches(handler: CommandHandler, vararg inputs: String) {
        for (input in inputs) {
            assertTrue("${handler.type} should match '$input'", handler.matches(input))
        }
    }

    private fun assertNotMatches(handler: CommandHandler, vararg inputs: String) {
        for (input in inputs) {
            assertFalse("${handler.type} should not match '$input'", handler.matches(input))
        }
    }

    @Test
    fun timerHandler() {
        val handler = TimerCommandHandler()
        assertMatches(
            handler,
            "set a timer for 5 minutes",
            "timer for 10 minutes",
            "countdown for 30 seconds",
            "set a timer for one hour"
        )
        assertNotMatches(handler, "what time is it", "calculate 2+2", "hello")
        val params = handler.extractParameters("set a timer for 5 minutes")
        assertEquals("5", params["duration"])
        assertEquals("minutes", params["unit"])
        assertEquals(CommandType.TIMER, handler.type)
    }

    @Test
    fun calculatorHandler() {
        val handler = CalculatorCommandHandler()
        assertMatches(
            handler,
            "calculate 2+2",
            "what is 10 times 5",
            "compute 100/4",
            "2+2"
        )
        assertNotMatches(handler, "set a timer", "what time is it")
        val params = handler.extractParameters("calculate 2+2")
        assertEquals("2 + 2", params["expression"])
        assertEquals(CommandType.CALCULATOR, handler.type)
    }

    @Test
    fun timeHandler() {
        val handler = TimeCommandHandler()
        assertMatches(
            handler,
            "what time is it",
            "what's the time",
            "current time",
            "time now",
            "tell me the time"
        )
        assertNotMatches(handler, "set a timer for 5 minutes", "calculate 2+2")
        assertEquals(CommandType.TIME, handler.type)
    }

    @Test
    fun reminderHandler() {
        val handler = ReminderCommandHandler()
        assertMatches(
            handler,
            "remind me to call the doctor tomorrow",
            "remind me about the meeting today",
            "set a reminder to buy groceries in 5 minutes",
            "show my reminders",
            "cancel reminder 2"
        )
        assertNotMatches(handler, "what time is it", "take a note buy milk")
        val params = handler.extractParameters("remind me to call the doctor tomorrow")
        assertEquals("set", params["action"])
        assertEquals("call the doctor", params["task"])
        assertEquals("tomorrow", params["time"])
        assertEquals(CommandType.REMINDER, handler.type)
    }

    @Test
    fun openAppHandler() {
        val handler = OpenAppCommandHandler()
        assertMatches(
            handler,
            "open spotify",
            "launch camera",
            "start messages",
            "run calculator"
        )
        assertNotMatches(handler, "what time is it", "call mom")
        val params = handler.extractParameters("open spotify")
        assertEquals("spotify", params["app"])
        assertEquals(CommandType.OPEN_APP, handler.type)
    }

    @Test
    fun weatherHandler() {
        val handler = WeatherCommandHandler()
        assertMatches(
            handler,
            "what's the weather",
            "what is the weather in nairobi",
            "weather in london",
            "how hot is it",
            "will it rain today",
            "weather forecast"
        )
        assertNotMatches(handler, "what time is it")
        val params = handler.extractParameters("weather in nairobi")
        assertEquals("nairobi", params["city"])
        assertEquals("current", params["query"])
        assertEquals(CommandType.WEATHER, handler.type)
    }

    @Test
    fun noteHandler() {
        val handler = NoteCommandHandler()
        assertMatches(
            handler,
            "take a note buy milk",
            "note the meeting is at 3",
            "remember that the keys are in the drawer",
            "show my notes"
        )
        assertNotMatches(handler, "remind me to buy milk")
        val params = handler.extractParameters("note the meeting is at 3")
        assertEquals("create", params["action"])
        assertEquals("the meeting is at 3", params["text"])
        assertEquals(CommandType.NOTE, handler.type)
    }

    @Test
    fun unitConversionHandler() {
        val handler = UnitConversionCommandHandler()
        assertMatches(
            handler,
            "convert 5 km to miles",
            "10 kg in lbs",
            "how many cups in 500 ml",
            "change 3 feet to meters"
        )
        assertNotMatches(handler, "calculate 5 km", "what is 5 km")
        val params = handler.extractParameters("convert 5 km to miles")
        assertEquals("5", params["value"])
        assertEquals("km", params["fromUnit"])
        assertEquals("mi", params["toUnit"])
        assertEquals(CommandType.UNIT_CONVERSION, handler.type)
    }

    @Test
    fun translationHandler() {
        val handler = TranslationCommandHandler()
        assertMatches(
            handler,
            "translate hello to swahili",
            "how do you say thanks in english",
            "what is goodbye in shona"
        )
        assertNotMatches(handler, "what time is it")
        val params = handler.extractParameters("translate hello to swahili")
        assertEquals("hello", params["text"])
        assertEquals("sw", params["targetLang"])
        assertEquals(CommandType.TRANSLATION, handler.type)
    }

    @Test
    fun searchHandler() {
        val handler = SearchCommandHandler()
        assertMatches(
            handler,
            "search for kotlin tutorials",
            "google the weather",
            "look up nearby restaurants",
            "find python docs online"
        )
        assertNotMatches(handler, "find contact mom")
        val params = handler.extractParameters("search for kotlin tutorials")
        assertEquals("kotlin tutorials", params["query"])
        assertEquals(CommandType.SEARCH, handler.type)
    }

    @Test
    fun contactHandler() {
        val handler = ContactCommandHandler()
        assertMatches(
            handler,
            "find contact mom",
            "search contacts for john",
            "who is alice",
            "text john saying hi"
        )
        assertNotMatches(handler, "settings menu")
        val params = handler.extractParameters("find contact mom")
        assertEquals("find", params["action"])
        assertEquals("mom", params["name"])
        assertEquals(CommandType.CONTACT, handler.type)
    }

    @Test
    fun alarmHandler() {
        val handler = AlarmCommandHandler()
        assertMatches(
            handler,
            "set alarm for 7 am",
            "alarm for 8 am",
            "wake me at 6:30",
            "wake me up in 30 minutes",
            "show my alarms"
        )
        assertNotMatches(handler, "set a timer for 5 minutes")
        val params = handler.extractParameters("set alarm for 7 am")
        assertEquals("set", params["action"])
        assertEquals("7 am", params["time"])
        assertEquals(CommandType.ALARM, handler.type)
    }

    @Test
    fun calendarHandler() {
        val handler = CalendarCommandHandler()
        assertMatches(
            handler,
            "what's on my calendar",
            "what is on my calendar today",
            "list my events",
            "schedule meeting with team",
            "check my schedule"
        )
        assertNotMatches(handler, "whats the date")
        val params = handler.extractParameters("schedule lunch with team tomorrow")
        assertEquals("add", params["action"])
        assertEquals("lunch with team", params["title"])
        assertEquals(CommandType.CALENDAR, handler.type)
    }

    @Test
    fun musicHandler() {
        val handler = MusicCommandHandler()
        assertMatches(
            handler,
            "play shape of you",
            "play music",
            "pause music",
            "next song",
            "skip track",
            "volume up"
        )
        assertNotMatches(handler, "movie night")
        val params = handler.extractParameters("play shape of you")
        assertEquals("play", params["action"])
        assertEquals("shape of you", params["track"])
        assertEquals(CommandType.MUSIC, handler.type)
    }

    @Test
    fun deviceInfoHandler() {
        val handler = DeviceInfoCommandHandler()
        assertMatches(
            handler,
            "device info",
            "tell me about this phone",
            "what phone am i using",
            "storage space"
        )
        assertNotMatches(handler, "device is slow")
        assertEquals(CommandType.DEVICE_INFO, handler.type)
    }

    @Test
    fun batteryHandler() {
        val handler = BatteryCommandHandler()
        assertMatches(
            handler,
            "battery level",
            "how much battery",
            "am i charging",
            "battery status"
        )
        assertNotMatches(handler, "battery icon looks bad")
        assertEquals(CommandType.BATTERY, handler.type)
    }

    @Test
    fun networkHandler() {
        val handler = NetworkCommandHandler()
        assertMatches(
            handler,
            "network status",
            "wifi status",
            "am i connected to wifi",
            "internet connection"
        )
        assertNotMatches(handler, "network cable is missing")
        assertEquals(CommandType.NETWORK, handler.type)
    }

    @Test
    fun flashlightHandler() {
        val handler = FlashlightCommandHandler()
        assertMatches(
            handler,
            "turn on flashlight",
            "torch on",
            "turn off flashlight",
            "flashlight off",
            "toggle the torch"
        )
        assertNotMatches(handler, "flashlight app is cool")
        assertEquals("on", handler.extractParameters("turn on the flashlight")["state"])
        assertEquals("off", handler.extractParameters("turn off the torch")["state"])
        assertEquals(CommandType.FLASHLIGHT, handler.type)
    }

    @Test
    fun screenshotHandler() {
        val handler = ScreenshotCommandHandler()
        assertMatches(
            handler,
            "take a screenshot",
            "screenshot",
            "capture the screen",
            "snap a screenshot"
        )
        assertNotMatches(handler, "screenshot folder")
        assertEquals(CommandType.SCREENSHOT, handler.type)
    }

    @Test
    fun appLaunchHandler() {
        val handler = AppLaunchCommandHandler()
        assertMatches(
            handler,
            "launch app spotify",
            "open app camera",
            "start the messages app",
            "can you open chrome"
        )
        assertNotMatches(handler, "close the app")
        val params = handler.extractParameters("open app camera")
        assertEquals("camera", params["app"])
        assertEquals(CommandType.APP_LAUNCH, handler.type)
    }

    @Test
    fun sendMessageHandler() {
        val handler = SendMessageCommandHandler()
        assertMatches(
            handler,
            "send message to mom",
            "text john saying on my way",
            "message peter saying hello",
            "send an sms to jane"
        )
        assertNotMatches(handler, "received message")
        val params = handler.extractParameters("text mom saying hi")
        assertEquals("mom", params["recipient"])
        assertEquals("hi", params["message"])
        assertEquals(CommandType.SEND_MESSAGE, handler.type)
    }

    @Test
    fun callHandler() {
        val handler = CallCommandHandler()
        assertMatches(
            handler,
            "call mom",
            "phone john",
            "dial 5551234",
            "give peter a call"
        )
        assertNotMatches(handler, "football match")
        val params = handler.extractParameters("call mom")
        assertEquals("mom", params["target"])
        assertEquals("contact", params["targetType"])
        assertEquals(CommandType.CALL, handler.type)
    }

    @Test
    fun settingsHandler() {
        val handler = SettingsCommandHandler()
        assertMatches(
            handler,
            "turn on wifi",
            "turn off bluetooth",
            "set brightness to 50",
            "increase brightness",
            "set the volume to 30"
        )
        assertNotMatches(handler, "open settings menu")
        val params = handler.extractParameters("turn on wifi")
        assertEquals("wifi", params["setting"])
        assertEquals("on", params["state"])
        assertEquals(CommandType.SETTINGS, handler.type)
    }

    @Test
    fun locationHandler() {
        val handler = LocationCommandHandler()
        assertMatches(
            handler,
            "where am i",
            "my location",
            "current location",
            "locate me"
        )
        assertNotMatches(handler, "location permission")
        assertEquals(CommandType.LOCATION, handler.type)
    }

    @Test
    fun speedTestHandler() {
        val handler = SpeedTestCommandHandler()
        assertMatches(
            handler,
            "speed test",
            "internet speed",
            "test my connection",
            "run a speed test"
        )
        assertNotMatches(handler, "speed of light")
        assertEquals(CommandType.SPEED_TEST, handler.type)
    }

    @Test
    fun randomHandler() {
        val handler = RandomCommandHandler()
        assertMatches(
            handler,
            "random number",
            "pick a random number between 1 and 100",
            "flip a coin",
            "roll a dice"
        )
        assertNotMatches(handler, "randomize the playlist")
        assertEquals("coin", handler.extractParameters("flip a coin")["kind"])
        assertEquals("dice", handler.extractParameters("roll a dice")["kind"])
        val range = handler.extractParameters("choose a random number between 1 and 100")
        assertEquals("1", range["min"])
        assertEquals("100", range["max"])
        assertEquals(CommandType.RANDOM, handler.type)
    }

    @Test
    fun dateHandler() {
        val handler = DateCommandHandler()
        assertMatches(
            handler,
            "what's the date",
            "what day is it",
            "today's date",
            "date today"
        )
        assertNotMatches(handler, "date night")
        assertEquals(CommandType.DATE, handler.type)
    }

    @Test
    fun jokeHandler() {
        val handler = JokeCommandHandler()
        assertMatches(
            handler,
            "tell me a joke",
            "say something funny",
            "make me laugh",
            "another joke"
        )
        assertNotMatches(handler, "joke is on you")
        assertEquals(CommandType.JOKE, handler.type)
    }

    @Test
    fun quoteHandler() {
        val handler = QuoteCommandHandler()
        assertMatches(
            handler,
            "give me a quote",
            "inspire me",
            "motivational quote",
            "quote of the day"
        )
        assertNotMatches(handler, "quote unquote")
        assertEquals(CommandType.QUOTE, handler.type)
    }

    @Test
    fun mathHandler() {
        val handler = MathCommandHandler()
        assertMatches(
            handler,
            "15 percent of 200",
            "what is 10 percent of 50",
            "calculate 20 percent of 80",
            "how much is 30 percent of 90"
        )
        assertNotMatches(handler, "calculate 2+2")
        val percent = handler.extractParameters("15 percent of 200")
        assertEquals("percentage", percent["kind"])
        assertEquals("15", percent["percent"])
        assertEquals("200", percent["of"])
        assertEquals(CommandType.MATH, handler.type)
    }

    @Test
    fun noneHandlerMatchesNothing() {
        val handler = NoneCommandHandler()
        assertNotMatches(
            handler,
            "hello",
            "what time is it",
            "calculate 2+2",
            "",
            "anything at all"
        )
        assertEquals(CommandType.NONE, handler.type)
    }

    @Test
    fun routerDispatchesToCorrectHandlers() {
        val registry = CommandRegistry()
        registry.register(TimerCommandHandler())
        registry.register(CalculatorCommandHandler())
        registry.register(TimeCommandHandler())
        registry.register(SettingsCommandHandler())
        registry.register(OpenAppCommandHandler())

        assertEquals(CommandType.TIMER, registry.route("set a timer for 5 minutes")!!.command)
        assertEquals(CommandType.CALCULATOR, registry.route("calculate 2+2")!!.command)
        assertEquals(CommandType.TIME, registry.route("what time is it")!!.command)
        assertEquals(CommandType.SETTINGS, registry.route("turn on wifi")!!.command)
        assertEquals(CommandType.OPEN_APP, registry.route("open camera")!!.command)
    }

    @Test
    fun routerReturnsNullForUnknownInput() {
        val registry = CommandRegistry()
        registry.register(TimerCommandHandler())
        registry.register(CalculatorCommandHandler())
        assertNotNull(registry.route("xyzzy") == null)
    }

    @Test
    fun handlerTypesAreUnique() {
        val handlers = listOf(
            TimerCommandHandler(), CalculatorCommandHandler(), TimeCommandHandler(),
            ReminderCommandHandler(), OpenAppCommandHandler(), WeatherCommandHandler(),
            NoteCommandHandler(), UnitConversionCommandHandler(), TranslationCommandHandler(),
            SearchCommandHandler(), ContactCommandHandler(), AlarmCommandHandler(),
            CalendarCommandHandler(), MusicCommandHandler(), DeviceInfoCommandHandler(),
            BatteryCommandHandler(), NetworkCommandHandler(), FlashlightCommandHandler(),
            ScreenshotCommandHandler(), AppLaunchCommandHandler(), SendMessageCommandHandler(),
            CallCommandHandler(), SettingsCommandHandler(), LocationCommandHandler(),
            SpeedTestCommandHandler(), RandomCommandHandler(), DateCommandHandler(),
            JokeCommandHandler(), QuoteCommandHandler(), MathCommandHandler(),
            NoneCommandHandler()
        )
        val types = handlers.map { it.type }
        assertEquals(types.size, types.toSet().size)
    }
}
