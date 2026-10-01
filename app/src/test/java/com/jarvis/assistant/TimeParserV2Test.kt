package com.jarvis.assistant

import com.jarvis.assistant.commands.handlers.TimeParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeParserV2Test {

    @Test
    fun parseStandardTimeFormats() {
        val result1 = TimeParser.parse("3:30 PM")
        assertNotNull(result1)
        assertEquals(15, result1!!.hour)
        assertEquals(30, result1.minute)

        val result2 = TimeParser.parse("12:00 AM")
        assertNotNull(result2)
        assertEquals(0, result2!!.hour)
        assertEquals(0, result2.minute)

        val result3 = TimeParser.parse("11:59 PM")
        assertNotNull(result3)
        assertEquals(23, result3!!.hour)
        assertEquals(59, result3.minute)
    }

    @Test
    fun parseMilitaryTime() {
        val result1 = TimeParser.parse("14:30")
        assertNotNull(result1)
        assertEquals(14, result1!!.hour)
        assertEquals(30, result1.minute)

        val result2 = TimeParser.parse("00:00")
        assertNotNull(result2)
        assertEquals(0, result2!!.hour)
        assertEquals(0, result2.minute)

        val result3 = TimeParser.parse("23:59")
        assertNotNull(result3)
        assertEquals(23, result3!!.hour)
        assertEquals(59, result3.minute)
    }

    @Test
    fun parseNaturalLanguageTime() {
        val result1 = TimeParser.parse("noon")
        assertNotNull(result1)
        assertEquals(12, result1!!.hour)
        assertEquals(0, result1.minute)

        val result2 = TimeParser.parse("midnight")
        assertNotNull(result2)
        assertEquals(0, result2!!.hour)
        assertEquals(0, result2.minute)
    }

    @Test
    fun parseRelativeTime() {
        val result1 = TimeParser.parse("in 5 minutes")
        assertNotNull(result1)

        val result2 = TimeParser.parse("in 1 hour")
        assertNotNull(result2)

        val result3 = TimeParser.parse("in 30 seconds")
        assertNotNull(result3)
    }

    @Test
    fun parseInvalidTimeReturnsNull() {
        assertNull(TimeParser.parse(""))
        assertNull(TimeParser.parse("   "))
        assertNull(TimeParser.parse("abc"))
        assertNull(TimeParser.parse("25:00"))
        assertNull(TimeParser.parse("12:60"))
    }

    @Test
    fun parseTimeWithSeconds() {
        val result = TimeParser.parse("14:30:45")
        assertNotNull(result)
        assertEquals(14, result!!.hour)
        assertEquals(30, result.minute)
        assertEquals(45, result.second)
    }

    @Test
    fun parseTimeCaseInsensitive() {
        val result1 = TimeParser.parse("3:30 pm")
        assertNotNull(result1)
        assertEquals(15, result1!!.hour)

        val result2 = TimeParser.parse("3:30 PM")
        assertNotNull(result2)
        assertEquals(15, result2!!.hour)

        val result3 = TimeParser.parse("3:30 Pm")
        assertNotNull(result3)
        assertEquals(15, result3!!.hour)
    }

    @Test
    fun parseTimeWithExtraWhitespace() {
        val result = TimeParser.parse("  3:30 PM  ")
        assertNotNull(result)
        assertEquals(15, result!!.hour)
        assertEquals(30, result.minute)
    }

    @Test
    fun parseTimeEdgeCases() {
        val result1 = TimeParser.parse("12:00 PM")
        assertNotNull(result1)
        assertEquals(12, result1!!.hour)

        val result2 = TimeParser.parse("12:00 AM")
        assertNotNull(result2)
        assertEquals(0, result2!!.hour)

        val result3 = TimeParser.parse("1:00 AM")
        assertNotNull(result3)
        assertEquals(1, result3!!.hour)
    }

    @Test
    fun parseTimeWithPeriods() {
        val result1 = TimeParser.parse("3:30 a.m.")
        assertNotNull(result1)
        assertEquals(3, result1!!.hour)

        val result2 = TimeParser.parse("3:30 p.m.")
        assertNotNull(result2)
        assertEquals(15, result2!!.hour)
    }

    @Test
    fun parseTimeRange() {
        val result = TimeParser.parse("from 3:00 PM to 5:00 PM")
        assertNotNull(result)
    }

    @Test
    fun parseTimeWithDay() {
        val result = TimeParser.parse("tomorrow at 3:00 PM")
        assertNotNull(result)
    }

    @Test
    fun parseTimeWithDate() {
        val result = TimeParser.parse("on 2024-01-15 at 3:00 PM")
        assertNotNull(result)
    }

    @Test
    fun parseTimeWithTimezone() {
        val result = TimeParser.parse("3:00 PM EST")
        assertNotNull(result)
    }

    @Test
    fun parseTimeWithOffset() {
        val result = TimeParser.parse("3:00 PM +0300")
        assertNotNull(result)
    }

    @Test
    fun parseTimeWithNamedTimezone() {
        val result = TimeParser.parse("3:00 PM UTC")
        assertNotNull(result)
    }

    @Test
    fun parseTimeWithMultipleFormats() {
        val times = listOf(
            "3:30 PM",
            "15:30",
            "3:30pm",
            "15:30:00",
            "3:30 p.m."
        )
        for (time in times) {
            val result = TimeParser.parse(time)
            assertNotNull("Should parse: $time", result)
            assertEquals("Hour for $time", 15, result!!.hour)
            assertEquals("Minute for $time", 30, result.minute)
        }
    }

    @Test
    fun parseTimeValidation() {
        assertTrue(TimeParser.isValidTime("14:30"))
        assertTrue(TimeParser.isValidTime("3:30 PM"))
        assertTrue(TimeParser.isValidTime("00:00"))
        assertTrue(TimeParser.isValidTime("23:59"))
    }
}
