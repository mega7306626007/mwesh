package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.commands.handlers.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ExpressionEvaluatorV2Test {

    @Test
    fun advancedMathFunctions() {
        assertEquals(1.0, ExpressionEvaluator.evaluate("exp(0)")!!, 0.0001)
        assertEquals(0.0, ExpressionEvaluator.evaluate("log(1)")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("ln(e)")!!, 0.0001)
        assertEquals(2.0, ExpressionEvaluator.evaluate("ceil(1.1)")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("floor(1.9)")!!, 0.0001)
        assertEquals(2.0, ExpressionEvaluator.evaluate("round(1.5)")!!, 0.0001)
        assertEquals(5.0, ExpressionEvaluator.evaluate("max(3,5)")!!, 0.0001)
        assertEquals(3.0, ExpressionEvaluator.evaluate("min(3,5)")!!, 0.0001)
        assertEquals(25.0, ExpressionEvaluator.evaluate("pow(5,2)")!!, 0.0001)
    }

    @Test
    fun trigonometricFunctions() {
        assertEquals(0.0, ExpressionEvaluator.evaluate("sin(0)")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("cos(0)")!!, 0.0001)
        assertEquals(0.0, ExpressionEvaluator.evaluate("tan(0)")!!, 0.0001)
        assertEquals(1.0, abs(ExpressionEvaluator.evaluate("sin(90)^2+cos(90)^2")!!), 0.0001)
    }

    @Test
    fun hyperbolicFunctions() {
        assertEquals(1.0, ExpressionEvaluator.evaluate("cosh(0)")!!, 0.0001)
        assertEquals(0.0, ExpressionEvaluator.evaluate("sinh(0)")!!, 0.0001)
        assertEquals(0.0, ExpressionEvaluator.evaluate("tanh(0)")!!, 0.0001)
    }

    @Test
    fun statisticalFunctions() {
        assertEquals(3.0, ExpressionEvaluator.evaluate("avg(1,2,3,4,5)")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("min(1,2,3,4,5)")!!, 0.0001)
        assertEquals(5.0, ExpressionEvaluator.evaluate("max(1,2,3,4,5)")!!, 0.0001)
        assertEquals(15.0, ExpressionEvaluator.evaluate("sum(1,2,3,4,5)")!!, 0.0001)
    }

    @Test
    fun complexNestedExpressions() {
        assertEquals(10.0, ExpressionEvaluator.evaluate("sqrt(16)+pow(2,2)+3-2")!!, 0.0001)
        assertEquals(4.0, ExpressionEvaluator.evaluate("(sqrt(16)+pow(2,2))/2")!!, 0.0001)
        assertEquals(100.0, ExpressionEvaluator.evaluate("pow(sqrt(16)+6,2)")!!, 0.0001)
    }

    @Test
    fun moduloOperations() {
        assertEquals(1.0, ExpressionEvaluator.evaluate("10%3")!!, 0.0001)
        assertEquals(0.0, ExpressionEvaluator.evaluate("10%2")!!, 0.0001)
        assertEquals(2.0, ExpressionEvaluator.evaluate("17%5")!!, 0.0001)
        assertEquals(0.0, ExpressionEvaluator.evaluate("100%10")!!, 0.0001)
    }

    @Test
    fun absoluteValueOperations() {
        assertEquals(5.0, ExpressionEvaluator.evaluate("abs(-5)")!!, 0.0001)
        assertEquals(5.0, ExpressionEvaluator.evaluate("abs(5)")!!, 0.0001)
        assertEquals(0.0, ExpressionEvaluator.evaluate("abs(0)")!!, 0.0001)
        assertEquals(3.0, ExpressionEvaluator.evaluate("abs(-3)+abs(3)-3")!!, 0.0001)
    }

    @Test
    fun factorialEdgeCases() {
        assertEquals(1.0, ExpressionEvaluator.evaluate("0!")!!, 0.0001)
        assertEquals(1.0, ExpressionEvaluator.evaluate("1!")!!, 0.0001)
        assertEquals(120.0, ExpressionEvaluator.evaluate("5!")!!, 0.0001)
        assertEquals(3628800.0, ExpressionEvaluator.evaluate("10!")!!, 0.0001)
    }

    @Test
    fun variableSubstitution() {
        assertEquals(10.0, ExpressionEvaluator.evaluate("x+y+z", mapOf("x" to 2.0, "y" to 3.0, "z" to 5.0))!!, 0.0001)
        assertEquals(20.0, ExpressionEvaluator.evaluate("a*b", mapOf("a" to 4.0, "b" to 5.0))!!, 0.0001)
        assertEquals(7.0, ExpressionEvaluator.evaluate("x+5", mapOf("x" to 2.0))!!, 0.0001)
    }

    @Test
    fun divisionByZeroReturnsNull() {
        assertNull(ExpressionEvaluator.evaluate("1/0"))
        assertNull(ExpressionEvaluator.evaluate("10/(5-5)"))
        assertNull(ExpressionEvaluator.evaluate("0/0"))
        assertNull(ExpressionEvaluator.evaluate("100/(10-10)"))
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
        assertNull(ExpressionEvaluator.evaluate("(-5)!"))
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
        assertNull(ExpressionEvaluator.evaluate("max()"))
        assertNull(ExpressionEvaluator.evaluate("min()"))
    }

    @Test
    fun decimalNumbers() {
        assertEquals(7.0, ExpressionEvaluator.evaluate("3.5*2")!!, 0.0001)
        assertEquals(0.5, ExpressionEvaluator.evaluate("0.25*2")!!, 0.0001)
        assertEquals(2.5, ExpressionEvaluator.evaluate("1.25+1.25")!!, 0.0001)
        assertEquals(0.1, ExpressionEvaluator.evaluate("0.3-0.2")!!, 0.0001)
    }

    @Test
    fun whitespaceTolerant() {
        assertEquals(7.0, ExpressionEvaluator.evaluate("  3  +  4  ")!!, 0.0001)
        assertEquals(12.0, ExpressionEvaluator.evaluate("3 + 4 * 2 + 1")!!, 0.0001)
        assertEquals(5.0, ExpressionEvaluator.evaluate("\t2\t+\t3\n")!!, 0.0001)
    }

    @Test
    fun unicodeOperators() {
        assertEquals(6.0, ExpressionEvaluator.evaluate("2×3")!!, 0.0001)
        assertEquals(2.0, ExpressionEvaluator.evaluate("6÷3")!!, 0.0001)
        assertEquals(5.0, ExpressionEvaluator.evaluate("10−3")!!, 0.0001)
    }

    @Test
    fun constants() {
        assertEquals(Math.PI, ExpressionEvaluator.evaluate("pi")!!, 0.0001)
        assertEquals(Math.E, ExpressionEvaluator.evaluate("e")!!, 0.0001)
        assertEquals(2 * Math.PI, ExpressionEvaluator.evaluate("pi*2")!!, 0.0001)
        assertEquals(Math.PI / 2, ExpressionEvaluator.evaluate("pi/2")!!, 0.0001)
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
}
