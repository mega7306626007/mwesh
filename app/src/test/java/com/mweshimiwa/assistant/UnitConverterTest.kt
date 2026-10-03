package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.commands.handlers.UnitCategory
import com.mweshimiwa.assistant.commands.handlers.UnitConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class UnitConverterTest {

    @Test
    fun lengthConversions() {
        assertEquals(1000.0, UnitConverter.convert(1.0, "km", "m")!!.value, 0.0001)
        assertEquals(1.609344, UnitConverter.convert(1.0, "mi", "km")!!.value, 0.0001)
        assertEquals(1.0, UnitConverter.convert(100.0, "cm", "m")!!.value, 0.0001)
        assertEquals(1.0, UnitConverter.convert(12.0, "in", "ft")!!.value, 0.0001)
        assertEquals(3.0, UnitConverter.convert(1.0, "yd", "ft")!!.value, 0.0001)
        assertEquals(3.2808, UnitConverter.convert(1.0, "m", "ft")!!.value, 0.001)
    }

    @Test
    fun weightConversions() {
        assertEquals(2.20462262, UnitConverter.convert(1.0, "kg", "lb")!!.value, 0.0001)
        assertEquals(1.0, UnitConverter.convert(16.0, "oz", "lb")!!.value, 0.0001)
        assertEquals(6.35029318, UnitConverter.convert(1.0, "st", "kg")!!.value, 0.0001)
        assertEquals(1000.0, UnitConverter.convert(1.0, "kg", "g")!!.value, 0.0001)
        assertEquals(1000.0, UnitConverter.convert(1.0, "t", "kg")!!.value, 0.0001)
    }

    @Test
    fun temperatureConversions() {
        assertEquals(32.0, UnitConverter.convert(0.0, "c", "f")!!.value, 0.0001)
        assertEquals(212.0, UnitConverter.convert(100.0, "c", "f")!!.value, 0.0001)
        assertEquals(0.0, UnitConverter.convert(32.0, "f", "c")!!.value, 0.0001)
        assertEquals(273.15, UnitConverter.convert(0.0, "c", "k")!!.value, 0.0001)
        assertEquals(-40.0, UnitConverter.convert(-40.0, "c", "f")!!.value, 0.0001)
        assertEquals(-273.15, UnitConverter.convert(0.0, "k", "c")!!.value, 0.0001)
        assertEquals(37.0, UnitConverter.convert(98.6, "f", "c")!!.value, 0.01)
    }

    @Test
    fun volumeConversions() {
        assertEquals(3.785411784, UnitConverter.convert(1.0, "gal", "l")!!.value, 0.0001)
        assertEquals(0.5, UnitConverter.convert(500.0, "ml", "l")!!.value, 0.0001)
        assertEquals(4.0, UnitConverter.convert(1.0, "gal", "qt")!!.value, 0.0001)
        assertEquals(1000.0, UnitConverter.convert(1.0, "m3", "l")!!.value, 0.0001)
    }

    @Test
    fun speedConversions() {
        assertEquals(27.7778, UnitConverter.convert(100.0, "kmh", "mps")!!.value, 0.001)
        assertEquals(96.5606, UnitConverter.convert(60.0, "mph", "kmh")!!.value, 0.001)
        assertEquals(1.0, UnitConverter.convert(1.0, "mps", "mps")!!.value, 0.0001)
        assertEquals(0.5144444, UnitConverter.convert(1.0, "knot", "mps")!!.value, 0.0001)
    }

    @Test
    fun dataConversions() {
        assertEquals(1024.0, UnitConverter.convert(1.0, "kb", "b")!!.value, 0.0001)
        assertEquals(1024.0, UnitConverter.convert(1.0, "mb", "kb")!!.value, 0.0001)
        assertEquals(1024.0, UnitConverter.convert(1.0, "gb", "mb")!!.value, 0.0001)
        assertEquals(1073741824.0, UnitConverter.convert(1.0, "gb", "b")!!.value, 0.0001)
        assertEquals(1.0, UnitConverter.convert(1.0, "kib", "kib")!!.value, 0.0001)
    }

    @Test
    fun areaConversions() {
        assertEquals(10000.0, UnitConverter.convert(1.0, "ha", "m2")!!.value, 0.0001)
        assertEquals(4046.8564224, UnitConverter.convert(1.0, "ac", "m2")!!.value, 0.0001)
        assertEquals(10.7639, UnitConverter.convert(1.0, "m2", "ft2")!!.value, 0.001)
        assertEquals(100.0, UnitConverter.convert(1.0, "are", "m2")!!.value, 0.0001)
    }

    @Test
    fun timeConversions() {
        assertEquals(60.0, UnitConverter.convert(1.0, "h", "min")!!.value, 0.0001)
        assertEquals(1.5, UnitConverter.convert(90.0, "min", "h")!!.value, 0.0001)
        assertEquals(1000.0, UnitConverter.convert(1.0, "s", "ms")!!.value, 0.0001)
        assertEquals(7.0, UnitConverter.convert(1.0, "wk", "d")!!.value, 0.0001)
        assertEquals(3600000.0, UnitConverter.convert(1.0, "h", "ms")!!.value, 0.0001)
    }

    @Test
    fun pressureEnergyPowerConversions() {
        assertEquals(100.0, UnitConverter.convert(1.0, "bar", "kpa")!!.value, 0.0001)
        assertEquals(101325.0, UnitConverter.convert(1.0, "atm", "pa")!!.value, 0.0001)
        assertEquals(4.184, UnitConverter.convert(1.0, "kcal", "kj")!!.value, 0.0001)
        assertEquals(1000.0, UnitConverter.convert(1.0, "kw", "w")!!.value, 0.0001)
        assertEquals(745.7, UnitConverter.convert(1.0, "hp", "w")!!.value, 0.01)
    }

    @Test
    fun frequencyAndAngleConversions() {
        assertEquals(1000.0, UnitConverter.convert(1.0, "mhz", "khz")!!.value, 0.0001)
        assertEquals(57.2958, UnitConverter.convert(1.0, "rad", "deg")!!.value, 0.001)
        assertEquals(6.2832, UnitConverter.convert(1.0, "turn", "rad")!!.value, 0.001)
    }

    @Test
    fun sameUnitConvertsByFactor() {
        assertEquals(5.0, UnitConverter.convert(5.0, "m", "m")!!.value, 0.0001)
        assertEquals(42.0, UnitConverter.convert(42.0, "kg", "kg")!!.value, 0.0001)
        assertEquals(-10.0, UnitConverter.convert(-10.0, "c", "c")!!.value, 0.0001)
    }

    @Test
    fun caseInsensitiveUnits() {
        assertEquals(1000.0, UnitConverter.convert(1.0, "KM", "M")!!.value, 0.0001)
        assertEquals(2.20462262, UnitConverter.convert(1.0, "KG", "LB")!!.value, 0.0001)
        assertEquals(32.0, UnitConverter.convert(0.0, "C", "F")!!.value, 0.0001)
    }

    @Test
    fun fullUnitNamesSupported() {
        assertEquals(1000.0, UnitConverter.convert(1.0, "kilometer", "meter")!!.value, 0.0001)
        assertEquals(1.0, UnitConverter.convert(1.0, "kilogram", "kg")!!.value, 0.0001)
        assertEquals(1000.0, UnitConverter.convert(1.0, "liter", "milliliter")!!.value, 0.0001)
    }

    @Test
    fun zeroAndNegativeValues() {
        assertEquals(0.0, UnitConverter.convert(0.0, "km", "m")!!.value, 0.0001)
        assertEquals(-27.7778, UnitConverter.convert(-100.0, "kmh", "mps")!!.value, 0.001)
        assertEquals(-459.67, UnitConverter.convert(-459.67, "f", "f")!!.value, 0.0001)
    }

    @Test
    fun invalidUnitsReturnNull() {
        assertNull(UnitConverter.convert(1.0, "banana", "apple"))
        assertNull(UnitConverter.convert(1.0, "km", "banana"))
        assertNull(UnitConverter.convert(1.0, "banana", "m"))
        assertNull(UnitConverter.convert(1.0, "kg", "m"))
        assertNull(UnitConverter.convert(1.0, "c", "m"))
    }

    @Test
    fun conversionResultCarriesMetadata() {
        val result = UnitConverter.convert(5.0, "km", "m")!!
        assertEquals(5000.0, result.value, 0.0001)
        assertEquals("km", result.fromUnit)
        assertEquals("m", result.toUnit)
        assertEquals(UnitCategory.LENGTH, result.category)
    }

    @Test
    fun temperatureResultCategory() {
        val result = UnitConverter.convert(100.0, "c", "f")!!
        assertEquals(UnitCategory.TEMPERATURE, result.category)
    }

    @Test
    fun unitsForListsCategoryUnits() {
        assertTrue(UnitConverter.unitsFor(UnitCategory.LENGTH).contains("km"))
        assertTrue(UnitConverter.unitsFor(UnitCategory.WEIGHT).contains("lb"))
        assertTrue(UnitConverter.unitsFor(UnitCategory.DATA).contains("gb"))
        assertTrue(UnitConverter.unitsFor(UnitCategory.PRESSURE).contains("bar"))
        assertTrue(UnitConverter.unitsFor(UnitCategory.ANGLE).contains("rad"))
        assertTrue(UnitConverter.unitsFor(UnitCategory.TEMPERATURE).isEmpty())
    }

    @Test
    fun formatResultAppendsUnit() {
        val result = UnitConverter.convert(5.0, "km", "m")!!
        assertEquals("5000 m", UnitConverter.formatResult(result))
        val precise = UnitConverter.convert(1.0, "mi", "km")!!
        assertTrue(UnitConverter.formatResult(precise).endsWith("km"))
    }

    @Test
    fun formatResultRespectsPrecision() {
        val result = UnitConverter.convert(1.0, "mi", "km")!!
        assertEquals("1.609 km", UnitConverter.formatResult(result, 4))
    }

    @Test
    fun allCategoriesExceptTemperatureHaveUnits() {
        for (category in UnitCategory.values()) {
            if (category == UnitCategory.TEMPERATURE) continue
            assertTrue("category $category should have units", UnitConverter.unitsFor(category).isNotEmpty())
        }
    }

    @Test
    fun specialUnitsSupported() {
        assertEquals(1609.344, UnitConverter.convert(1.0, "mi", "m")!!.value, 0.0001)
        assertEquals(0.45359237, UnitConverter.convert(1.0, "lb", "kg")!!.value, 0.0001)
        assertEquals(1000.0, UnitConverter.convert(1.0, "tonne", "kg")!!.value, 0.0001)
        assertEquals(6.2832, UnitConverter.convert(1.0, "revolution", "rad")!!.value, 0.001)
    }

    @Test
    fun formatResultUsesDefaultLocale() {
        val result = UnitConverter.convert(1.5, "km", "m")!!
        assertEquals("1500 m", UnitConverter.formatResult(result))
        assertNotNull(Locale.getDefault())
    }
}
