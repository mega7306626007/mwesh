package com.jarvis.assistant

import com.jarvis.assistant.commands.handlers.UnitConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitConverterV2Test {

    @Test
    fun convertLengthUnits() {
        val result1 = UnitConverter.convert(1.0, "km", "mi")
        assertNotNull(result1)
        assertTrue(result1!! > 0.62 && result1 < 0.63)

        val result2 = UnitConverter.convert(1.0, "m", "ft")
        assertNotNull(result2)
        assertTrue(result2!! > 3.28 && result2 < 3.29)

        val result3 = UnitConverter.convert(1.0, "cm", "in")
        assertNotNull(result3)
        assertTrue(result3!! > 0.39 && result3 < 0.40)
    }

    @Test
    fun convertWeightUnits() {
        val result1 = UnitConverter.convert(1.0, "kg", "lb")
        assertNotNull(result1)
        assertTrue(result1!! > 2.20 && result1 < 2.21)

        val result2 = UnitConverter.convert(1.0, "g", "oz")
        assertNotNull(result2)
        assertTrue(result2!! > 0.035 && result2 < 0.036)

        val result3 = UnitConverter.convert(1.0, "lb", "kg")
        assertNotNull(result3)
        assertTrue(result3!! > 0.45 && result3 < 0.46)
    }

    @Test
    fun convertTemperatureUnits() {
        val result1 = UnitConverter.convert(0.0, "c", "f")
        assertEquals(32.0, result1!!, 0.01)

        val result2 = UnitConverter.convert(100.0, "c", "f")
        assertEquals(212.0, result2!!, 0.01)

        val result3 = UnitConverter.convert(32.0, "f", "c")
        assertEquals(0.0, result3!!, 0.01)

        val result4 = UnitConverter.convert(0.0, "c", "k")
        assertEquals(273.15, result4!!, 0.01)
    }

    @Test
    fun convertVolumeUnits() {
        val result1 = UnitConverter.convert(1.0, "l", "gal")
        assertNotNull(result1)
        assertTrue(result1!! > 0.26 && result1 < 0.27)

        val result2 = UnitConverter.convert(1.0, "ml", "oz")
        assertNotNull(result2)
        assertTrue(result2!! > 0.033 && result2 < 0.034)
    }

    @Test
    fun convertSpeedUnits() {
        val result1 = UnitConverter.convert(1.0, "kmh", "mph")
        assertNotNull(result1)
        assertTrue(result1!! > 0.62 && result1 < 0.63)

        val result2 = UnitConverter.convert(1.0, "mps", "kmh")
        assertNotNull(result2)
        assertTrue(result2!! > 3.59 && result2 < 3.61)
    }

    @Test
    fun convertAreaUnits() {
        val result1 = UnitConverter.convert(1.0, "sqm", "sqft")
        assertNotNull(result1)
        assertTrue(result1!! > 10.76 && result1 < 10.77)

        val result2 = UnitConverter.convert(1.0, "acre", "sqm")
        assertNotNull(result2)
        assertTrue(result2!! > 4046 && result2 < 4047)
    }

    @Test
    fun convertTimeUnits() {
        val result1 = UnitConverter.convert(1.0, "hour", "min")
        assertEquals(60.0, result1!!, 0.01)

        val result2 = UnitConverter.convert(1.0, "day", "hour")
        assertEquals(24.0, result2!!, 0.01)

        val result3 = UnitConverter.convert(1.0, "week", "day")
        assertEquals(7.0, result3!!, 0.01)
    }

    @Test
    fun convertDataUnits() {
        val result1 = UnitConverter.convert(1.0, "gb", "mb")
        assertEquals(1024.0, result1!!, 0.01)

        val result2 = UnitConverter.convert(1.0, "mb", "kb")
        assertEquals(1024.0, result2!!, 0.01)

        val result3 = UnitConverter.convert(1.0, "tb", "gb")
        assertEquals(1024.0, result3!!, 0.01)
    }

    @Test
    fun convertSameUnit() {
        val result = UnitConverter.convert(5.0, "km", "km")
        assertEquals(5.0, result!!, 0.0001)
    }

    @Test
    fun convertWithZeroValue() {
        val result = UnitConverter.convert(0.0, "km", "mi")
        assertEquals(0.0, result!!, 0.0001)
    }

    @Test
    fun convertWithNegativeValue() {
        val result = UnitConverter.convert(-10.0, "c", "f")
        assertEquals(14.0, result!!, 0.01)
    }

    @Test
    fun convertWithLargeValue() {
        val result = UnitConverter.convert(1000000.0, "m", "km")
        assertEquals(1000.0, result!!, 0.0001)
    }

    @Test
    fun convertWithSmallValue() {
        val result = UnitConverter.convert(0.001, "km", "m")
        assertEquals(1.0, result!!, 0.0001)
    }

    @Test
    fun getSupportedCategories() {
        val categories = UnitConverter.getSupportedCategories()
        assertNotNull(categories)
        assertTrue(categories.isNotEmpty())
    }

    @Test
    fun getSupportedUnits() {
        val units = UnitConverter.getSupportedUnits("length")
        assertNotNull(units)
        assertTrue(units.isNotEmpty())
    }

    @Test
    fun isConversionSupported() {
        assertTrue(UnitConverter.isConversionSupported("km", "mi"))
        assertTrue(UnitConverter.isConversionSupported("kg", "lb"))
        assertTrue(UnitConverter.isConversionSupported("c", "f"))
    }

    @Test
    fun getConversionFactor() {
        val factor = UnitConverter.getConversionFactor("km", "m")
        assertEquals(1000.0, factor!!, 0.0001)
    }

    @Test
    fun getUnitCategory() {
        val category = UnitConverter.getUnitCategory("km")
        assertNotNull(category)
    }

    @Test
    fun getUnitSymbol() {
        val symbol = UnitConverter.getUnitSymbol("kilometer")
        assertNotNull(symbol)
    }

    @Test
    fun getUnitName() {
        val name = UnitConverter.getUnitName("km")
        assertNotNull(name)
    }

    @Test
    fun getAvailableConversions() {
        val conversions = UnitConverter.getAvailableConversions("km")
        assertNotNull(conversions)
        assertTrue(conversions.isNotEmpty())
    }

    @Test
    fun getConversionFormula() {
        val formula = UnitConverter.getConversionFormula("c", "f")
        assertNotNull(formula)
    }

    @Test
    fun getConversionDescription() {
        val description = UnitConverter.getConversionDescription("km", "mi")
        assertNotNull(description)
    }

    @Test
    fun getConversionPrecision() {
        val precision = UnitConverter.getConversionPrecision("km", "mi")
        assertTrue(precision > 0)
    }

    @Test
    fun getConversionOffset() {
        val offset = UnitConverter.getConversionOffset("c", "f")
        assertEquals(32.0, offset!!, 0.0001)
    }

    @Test
    fun getConversionMultiplier() {
        val multiplier = UnitConverter.getConversionMultiplier("km", "m")
        assertEquals(1000.0, multiplier!!, 0.0001)
    }

    @Test
    fun getConversionDivisor() {
        val divisor = UnitConverter.getConversionDivisor("m", "km")
        assertEquals(1000.0, divisor!!, 0.0001)
    }

    @Test
    fun getConversionBase() {
        val base = UnitConverter.getConversionBase("c", "f")
        assertEquals(0.0, base!!, 0.0001)
    }

    @Test
    fun getConversionScale() {
        val scale = UnitConverter.getConversionScale("c", "f")
        assertEquals(1.8, scale!!, 0.0001)
    }

    @Test
    fun getConversionShift() {
        val shift = UnitConverter.getConversionShift("c", "f")
        assertEquals(32.0, shift!!, 0.0001)
    }

    @Test
    fun getConversionInverse() {
        val inverse = UnitConverter.getConversionInverse("c", "f")
        assertNotNull(inverse)
    }

    @Test
    fun getConversionType() {
        val type = UnitConverter.getConversionType("km", "mi")
        assertNotNull(type)
    }

    @Test
    fun getConversionSystem() {
        val system = UnitConverter.getConversionSystem("km")
        assertNotNull(system)
    }

    @Test
    fun getConversionDimension() {
        val dimension = UnitConverter.getConversionDimension("km")
        assertNotNull(dimension)
    }

    @Test
    fun getConversionQuantity() {
        val quantity = UnitConverter.getConversionQuantity("km")
        assertNotNull(quantity)
    }

    @Test
    fun getConversionUnit() {
        val unit = UnitConverter.getConversionUnit("km")
        assertNotNull(unit)
    }

    @Test
    fun getConversionStandard() {
        val standard = UnitConverter.getConversionStandard("km")
        assertNotNull(standard)
    }

    @Test
    fun getConversionAuthority() {
        val authority = UnitConverter.getConversionAuthority("km")
        assertNotNull(authority)
    }

    @Test
    fun getConversionYear() {
        val year = UnitConverter.getConversionYear("km")
        assertNotNull(year)
    }

    @Test
    fun getConversionStatus() {
        val status = UnitConverter.getConversionStatus("km")
        assertNotNull(status)
    }

    @Test
    fun getConversionNote() {
        val note = UnitConverter.getConversionNote("km")
        assertNotNull(note)
    }

    @Test
    fun getConversionReference() {
        val reference = UnitConverter.getConversionReference("km")
        assertNotNull(reference)
    }

    @Test
    fun getConversionSource() {
        val source = UnitConverter.getConversionSource("km")
        assertNotNull(source)
    }

    @Test
    fun getConversionTarget() {
        val target = UnitConverter.getConversionTarget("km")
        assertNotNull(target)
    }

    @Test
    fun getConversionFactorType() {
        val factorType = UnitConverter.getConversionFactorType("km", "mi")
        assertNotNull(factorType)
    }

    @Test
    fun getConversionFactorValue() {
        val factorValue = UnitConverter.getConversionFactorValue("km", "mi")
        assertNotNull(factorValue)
    }

    @Test
    fun getConversionFactorUnit() {
        val factorUnit = UnitConverter.getConversionFactorUnit("km", "mi")
        assertNotNull(factorUnit)
    }

    @Test
    fun getConversionFactorSymbol() {
        val factorSymbol = UnitConverter.getConversionFactorSymbol("km", "mi")
        assertNotNull(factorSymbol)
    }

    @Test
    fun getConversionFactorName() {
        val factorName = UnitConverter.getConversionFactorName("km", "mi")
        assertNotNull(factorName)
    }

    @Test
    fun getConversionFactorDescription() {
        val factorDescription = UnitConverter.getConversionFactorDescription("km", "mi")
        assertNotNull(factorDescription)
    }

    @Test
    fun getConversionFactorFormula() {
        val factorFormula = UnitConverter.getConversionFactorFormula("km", "mi")
        assertNotNull(factorFormula)
    }

    @Test
    fun getConversionFactorPrecision() {
        val factorPrecision = UnitConverter.getConversionFactorPrecision("km", "mi")
        assertTrue(factorPrecision > 0)
    }

    @Test
    fun getConversionFactorOffset() {
        val factorOffset = UnitConverter.getConversionFactorOffset("km", "mi")
        assertEquals(0.0, factorOffset!!, 0.0001)
    }

    @Test
    fun getConversionFactorMultiplier() {
        val factorMultiplier = UnitConverter.getConversionFactorMultiplier("km", "mi")
        assertEquals(0.621371, factorMultiplier!!, 0.0001)
    }

    @Test
    fun getConversionFactorDivisor() {
        val factorDivisor = UnitConverter.getConversionFactorDivisor("km", "mi")
        assertEquals(1.0, factorDivisor!!, 0.0001)
    }

    @Test
    fun getConversionFactorBase() {
        val factorBase = UnitConverter.getConversionFactorBase("km", "mi")
        assertEquals(0.0, factorBase!!, 0.0001)
    }

    @Test
    fun getConversionFactorScale() {
        val factorScale = UnitConverter.getConversionFactorScale("km", "mi")
        assertEquals(0.621371, factorScale!!, 0.0001)
    }

    @Test
    fun getConversionFactorShift() {
        val factorShift = UnitConverter.getConversionFactorShift("km", "mi")
        assertEquals(0.0, factorShift!!, 0.0001)
    }

    @Test
    fun getConversionFactorInverse() {
        val factorInverse = UnitConverter.getConversionFactorInverse("km", "mi")
        assertNotNull(factorInverse)
    }

    @Test
    fun getConversionFactorType2() {
        val factorType = UnitConverter.getConversionFactorType2("km", "mi")
        assertNotNull(factorType)
    }

    @Test
    fun getConversionFactorValue2() {
        val factorValue = UnitConverter.getConversionFactorValue2("km", "mi")
        assertNotNull(factorValue)
    }

    @Test
    fun getConversionFactorUnit2() {
        val factorUnit = UnitConverter.getConversionFactorUnit2("km", "mi")
        assertNotNull(factorUnit)
    }

    @Test
    fun getConversionFactorSymbol2() {
        val factorSymbol = UnitConverter.getConversionFactorSymbol2("km", "mi")
        assertNotNull(factorSymbol)
    }

    @Test
    fun getConversionFactorName2() {
        val factorName = UnitConverter.getConversionFactorName2("km", "mi")
        assertNotNull(factorName)
    }

    @Test
    fun getConversionFactorDescription2() {
        val factorDescription = UnitConverter.getConversionFactorDescription2("km", "mi")
        assertNotNull(factorDescription)
    }

    @Test
    fun getConversionFactorFormula2() {
        val factorFormula = UnitConverter.getConversionFactorFormula2("km", "mi")
        assertNotNull(factorFormula)
    }

    @Test
    fun getConversionFactorPrecision2() {
        val factorPrecision = UnitConverter.getConversionFactorPrecision2("km", "mi")
        assertTrue(factorPrecision > 0)
    }

    @Test
    fun getConversionFactorOffset2() {
        val factorOffset = UnitConverter.getConversionFactorOffset2("km", "mi")
        assertEquals(0.0, factorOffset!!, 0.0001)
    }

    @Test
    fun getConversionFactorMultiplier2() {
        val factorMultiplier = UnitConverter.getConversionFactorMultiplier2("km", "mi")
        assertEquals(0.621371, factorMultiplier!!, 0.0001)
    }

    @Test
    fun getConversionFactorDivisor2() {
        val factorDivisor = UnitConverter.getConversionFactorDivisor2("km", "mi")
        assertEquals(1.0, factorDivisor!!, 0.0001)
    }

    @Test
    fun getConversionFactorBase2() {
        val factorBase = UnitConverter.getConversionFactorBase2("km", "mi")
        assertEquals(0.0, factorBase!!, 0.0001)
    }

    @Test
    fun getConversionFactorScale2() {
        val factorScale = UnitConverter.getConversionFactorScale2("km", "mi")
        assertEquals(0.621371, factorScale!!, 0.0001)
    }

    @Test
    fun getConversionFactorShift2() {
        val factorShift = UnitConverter.getConversionFactorShift2("km", "mi")
        assertEquals(0.0, factorShift!!, 0.0001)
    }

    @Test
    fun getConversionFactorInverse2() {
        val factorInverse = UnitConverter.getConversionFactorInverse2("km", "mi")
        assertNotNull(factorInverse)
    }
}
