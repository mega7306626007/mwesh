package com.jarvis.assistant

import com.jarvis.assistant.commands.handlers.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ExpressionEvaluatorTest {

    @Test
    fun simpleArithmetic() {
        assertEquals(5.0, ExpressionEvaluator.evaluate("2+3")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("5-4")!!, 0.0001)
        assertEquals(6.0, ExpressionEvaluator.evaluate("2*3")!!, 0.0001)
        assertEquals(2.5, ExpressionEvaluator.evaluate("5/2")!!, 0.0001)
    }

    @Test
    fun operatorPrecedence() {
        assertEquals(14.0, ExpressionEvaluator.evaluate("2+3*4")!!, 0.0001)
        assertEquals(10.0, ExpressionEvaluator.evaluate("2*3+4")!!, 0.0001)
        assertEquals(2.0, ExpressionEvaluator.evaluate("10-2*4")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("8/4/2")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("10%3")!!, 0.0001)
    }

    @Test
    fun parentheses() {
        assertEquals(20.0, ExpressionEvaluator.evaluate("(2+3)*4")!!, 0.0001)
        assertEquals(5.0, ExpressionEvaluator.evaluate("((2+3))")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("(10-5)/(2+3)")!!, 0.0001)
        assertEquals(50.0, ExpressionEvaluator.evaluate("(1+2+3+4)*5")!!, 0.0001)
    }

    @Test
    fun powerIsRightAssociative() {
        assertEquals(512.0, ExpressionEvaluator.evaluate("2^3^2")!!, 0.0001)
        assertEquals(16.0, ExpressionEvaluator.evaluate("2^(1+3)")!!, 0.0001)
        assertEquals(27.0, ExpressionEvaluator.evaluate("3^3")!!, 0.0001)
    }

    @Test
    fun unaryOperators() {
        assertEquals(-5.0, ExpressionEvaluator.evaluate("-5")!!, 0.0001)
        assertEquals(5.0, ExpressionEvaluator.evaluate("+5")!!, 0.0001)
        assertEquals(-3.0, ExpressionEvaluator.evaluate("2*-1.5")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("--1")!!, 0.0001)
    }

    @Test
    fun mathFunctions() {
        assertEquals(4.0, ExpressionEvaluator.evaluate("sqrt(16)")!!, 0.0001)
        assertEquals(0.5, ExpressionEvaluator.evaluate("sin(30)")!!, 0.0001)
        assertEquals(0.5, ExpressionEvaluator.evaluate("cos(60)")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("tan(45)")!!, 0.0001)
        assertEquals(2.0, ExpressionEvaluator.evaluate("log(100)")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("ln(e)")!!, 0.0001)
        assertEquals(7.0, ExpressionEvaluator.evaluate("abs(-7)")!!, 0.0001)
        assertEquals(1024.0, ExpressionEvaluator.evaluate("pow(2,10)")!!, 0.0001)
        assertEquals(10.0, ExpressionEvaluator.evaluate("sqrt(81)+sqrt(9)-sqrt(4)")!!, 0.0001)
    }

    @Test
    fun constants() {
        assertEquals(Math.PI, ExpressionEvaluator.evaluate("pi")!!, 0.0001)
        assertEquals(Math.E, ExpressionEvaluator.evaluate("e")!!, 0.0001)
        assertEquals(2 * Math.PI, ExpressionEvaluator.evaluate("pi*2")!!, 0.0001)
    }

    @Test
    fun factorial() {
        assertEquals(120.0, ExpressionEvaluator.evaluate("5!")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("0!")!!, 0.0001)
        assertEquals(24.0, ExpressionEvaluator.evaluate("4!")!!, 0.0001)
        assertEquals(480.0, ExpressionEvaluator.evaluate("5!+5!+5!+5!")!!, 0.0001)
    }

    @Test
    fun variables() {
        assertEquals(5.0, ExpressionEvaluator.evaluate("x+y", mapOf("x" to 2.0, "y" to 3.0))!!, 0.0001)
        assertEquals(10.0, ExpressionEvaluator.evaluate("a*b", mapOf("a" to 2.0, "b" to 5.0))!!, 0.0001)
        assertEquals(7.0, ExpressionEvaluator.evaluate("x+5", mapOf("x" to 2.0))!!, 0.0001)
    }

    @Test
    fun unicodeOperators() {
        assertEquals(6.0, ExpressionEvaluator.evaluate("2×3")!!, 0.0001)
        assertEquals(2.0, ExpressionEvaluator.evaluate("6÷3")!!, 0.0001)
    }

    @Test
    fun whitespaceTolerant() {
        assertEquals(7.0, ExpressionEvaluator.evaluate("  3  +  4  ")!!, 0.0001)
        assertEquals(12.0, ExpressionEvaluator.evaluate("3 + 4 * 2 + 1")!!, 0.0001)
    }

    @Test
    fun divisionByZeroReturnsNull() {
        assertNull(ExpressionEvaluator.evaluate("1/0"))
        assertNull(ExpressionEvaluator.evaluate("10/(5-5)"))
        assertNull(ExpressionEvaluator.evaluate("0/0"))
    }

    @Test
    fun malformedExpressionsReturnNull() {
        assertNull(ExpressionEvaluator.evaluate(""))
        assertNull(ExpressionEvaluator.evaluate("   "))
        assertNull(ExpressionEvaluator.evaluate("2+"))
        assertNull(ExpressionEvaluator.evaluate("+"))
        assertNull(ExpressionEvaluator.evaluate("((2+3)"))
        assertNull(ExpressionEvaluator.evaluate("2+3)"))
        assertNull(ExpressionEvaluator.evaluate("2..3"))
        assertNull(ExpressionEvaluator.evaluate("foo(2)"))
        assertNull(ExpressionEvaluator.evaluate("unknownvar"))
        assertNull(ExpressionEvaluator.evaluate("sqrt"))
        assertNull(ExpressionEvaluator.evaluate("!5"))
    }

    @Test
    fun negativeFactorialReturnsNull() {
        assertNull(ExpressionEvaluator.evaluate("(-1)!"))
        assertNull(ExpressionEvaluator.evaluate("2.5!"))
    }

    @Test
    fun sqrtOfNegativeReturnsNaN() {
        val result = ExpressionEvaluator.evaluate("sqrt(-1)")
        assertTrue(result != null && result.isNaN())
    }

    @Test
    fun functionArgumentCountEnforced() {
        assertNull(ExpressionEvaluator.evaluate("sqrt(4,5)"))
        assertNull(ExpressionEvaluator.evaluate("pow(2)"))
        assertNull(ExpressionEvaluator.evaluate("pow(2,3,4)"))
    }

    @Test
    fun decimalNumbers() {
        assertEquals(7.0, ExpressionEvaluator.evaluate("3.5*2")!!, 0.0001)
        assertEquals(0.5, ExpressionEvaluator.evaluate("0.25*2")!!, 0.0001)
        assertEquals(2.5, ExpressionEvaluator.evaluate("1.25+1.25")!!, 0.0001)
    }

    @Test
    fun complexExpressions() {
        assertEquals(11.0, ExpressionEvaluator.evaluate("sqrt(16)+pow(2,2)+3")!!, 0.0001)
        assertEquals(4.0, ExpressionEvaluator.evaluate("(sqrt(16)+pow(2,2))/2")!!, 0.0001)
        assertEquals(1.0, abs(ExpressionEvaluator.evaluate("sin(90)^2+cos(90)^2")!!), 0.0001)
    }
}
