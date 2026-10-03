package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.commands.handlers.TimeParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.TimeUnit

class TimeParserTest {

    private fun fixedNow(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 10)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    @Test
    fun simpleRelativeMinutes() {
        val now = fixedNow()
        val result = TimeParser.parse("in 5 minutes", now)!!
        assertEquals(now + TimeUnit.MINUTES.toMillis(5), result.timestamp)
        assertEquals("in 5 minutes", result.expression)
        assertFalse(result.isPast)
    }

    @Test
    fun simpleRelativeSeconds() {
        val now = fixedNow()
        val result = TimeParser.parse("in 30 seconds", now)!!
        assertEquals(now + 30_000L, result.timestamp)
    }

    @Test
    fun simpleRelativeHours() {
        val now = fixedNow()
        val result = TimeParser.parse("in 2 hours", now)!!
        assertEquals(now + TimeUnit.HOURS.toMillis(2), result.timestamp)
    }

    @Test
    fun compositeRelativeTime() {
        val now = fixedNow()
        val result = TimeParser.parse("in 1 hour and 30 minutes", now)!!
        assertEquals(now + TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(30), result.timestamp)
    }

    @Test
    fun wordNumberRelative() {
        val now = fixedNow()
        val result = TimeParser.parse("in twenty minutes", now)!!
        assertEquals(now + 20 * 60_000L, result.timestamp)
    }

    @Test
    fun relativeDaysWeeksMonths() {
        val now = fixedNow()
        assertEquals(now + TimeUnit.DAYS.toMillis(2), TimeParser.parse("in 2 days", now)!!.timestamp)
        assertEquals(now + TimeUnit.DAYS.toMillis(7), TimeParser.parse("in 1 week", now)!!.timestamp)
        assertEquals(now + TimeUnit.DAYS.toMillis(90), TimeParser.parse("in 3 months", now)!!.timestamp)
    }

    @Test
    fun clockTimePm() {
        val now = fixedNow()
        val result = TimeParser.parse("at 3 pm", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(15, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
        assertTrue(result.timestamp > now)
    }

    @Test
    fun clockTimeAmRollsToNextDay() {
        val now = fixedNow()
        val result = TimeParser.parse("at 9:30 am", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(9, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, cal.get(Calendar.MINUTE))
        assertTrue(result.timestamp > now)
    }

    @Test
    fun tomorrowAtTime() {
        val now = fixedNow()
        val result = TimeParser.parse("tomorrow at 5pm", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(17, cal.get(Calendar.HOUR_OF_DAY))
        val expectedDay = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.DAY_OF_YEAR, 1)
        }
        assertEquals(expectedDay.get(Calendar.DAY_OF_YEAR), cal.get(Calendar.DAY_OF_YEAR))
    }

    @Test
    fun tomorrowInMorning() {
        val now = fixedNow()
        val result = TimeParser.parse("tomorrow in the morning", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
    }

    @Test
    fun tonight() {
        val now = fixedNow()
        val result = TimeParser.parse("tonight", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(20, cal.get(Calendar.HOUR_OF_DAY))
        assertTrue(result.timestamp > now)
    }

    @Test
    fun todayInAfternoon() {
        val now = fixedNow()
        val result = TimeParser.parse("today in the afternoon", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(14, cal.get(Calendar.HOUR_OF_DAY))
        assertTrue(result.timestamp > now)
    }

    @Test
    fun nextWeekday() {
        val now = fixedNow()
        val result = TimeParser.parse("next friday", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(Calendar.FRIDAY, cal.get(Calendar.DAY_OF_WEEK))
        assertTrue(result.timestamp > now)
    }

    @Test
    fun weekdayWithoutNext() {
        val now = fixedNow()
        val result = TimeParser.parse("monday", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK))
        assertEquals(18, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun recurringDaily() {
        val now = fixedNow()
        val result = TimeParser.parse("every day", now)!!
        assertEquals("daily", result.recurrence)
        assertTrue(result.timestamp >= now)
    }

    @Test
    fun recurringWeeklyAtTime() {
        val now = fixedNow()
        val result = TimeParser.parse("every week at 9 am", now)!!
        assertEquals("weekly", result.recurrence)
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(9, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun recurringMonthly() {
        val now = fixedNow()
        val result = TimeParser.parse("every month", now)!!
        assertEquals("monthly", result.recurrence)
    }

    @Test
    fun explicitDate() {
        val now = fixedNow()
        val result = TimeParser.parse("on 25 december", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH))
        assertEquals(25, cal.get(Calendar.DAY_OF_MONTH))
        assertEquals(9, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun explicitDateWithYear() {
        val now = fixedNow()
        val result = TimeParser.parse("on 25 december 2030", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(2030, cal.get(Calendar.YEAR))
        assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH))
        assertEquals(25, cal.get(Calendar.DAY_OF_MONTH))
        assertEquals(9, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun explicitDateAtTimeFallsBackToClockTime() {
        val now = fixedNow()
        val result = TimeParser.parse("on 25 december 2030 at 3pm", now)!!
        val cal = Calendar.getInstance()
        cal.timeInMillis = result.timestamp
        assertEquals(15, cal.get(Calendar.HOUR_OF_DAY))
        assertTrue(result.timestamp > now)
    }

    @Test
    fun unparseableInputReturnsNull() {
        val now = fixedNow()
        assertNull(TimeParser.parse("hello world", now))
        assertNull(TimeParser.parse("", now))
        assertNull(TimeParser.parse("the quick brown fox", now))
        assertNull(TimeParser.parse("   ", now))
    }

    @Test
    fun formatRelativeFuture() {
        val now = fixedNow()
        assertEquals("in 5 minutes", TimeParser.formatRelative(now + TimeUnit.MINUTES.toMillis(5), now))
        assertEquals("in 2 hours", TimeParser.formatRelative(now + TimeUnit.HOURS.toMillis(2), now))
        assertEquals("in 1 second", TimeParser.formatRelative(now + 1000L, now))
    }

    @Test
    fun formatRelativePast() {
        val now = fixedNow()
        assertEquals("2 days ago", TimeParser.formatRelative(now - TimeUnit.DAYS.toMillis(2), now))
        assertEquals("5 minutes ago", TimeParser.formatRelative(now - TimeUnit.MINUTES.toMillis(5), now))
        assertEquals("1 hour ago", TimeParser.formatRelative(now - TimeUnit.HOURS.toMillis(1), now))
    }

    @Test
    fun formatRelativeSingularUnits() {
        val now = fixedNow()
        assertEquals("in 1 minute", TimeParser.formatRelative(now + TimeUnit.MINUTES.toMillis(1), now))
        assertEquals("1 second ago", TimeParser.formatRelative(now - 1000L, now))
    }

    @Test
    fun resultCarriesExpression() {
        val now = fixedNow()
        val result = TimeParser.parse("in 10 minutes", now)!!
        assertNotNull(result.expression)
        assertTrue(result.expression.contains("10"))
    }
}
