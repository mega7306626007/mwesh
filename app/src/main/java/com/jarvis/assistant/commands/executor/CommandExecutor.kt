package com.jarvis.assistant.commands.executor

import com.jarvis.assistant.commands.router.CommandResult
import com.jarvis.assistant.core.logging.JarvisLogger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommandExecutor {

    fun execute(commandType: String, parameters: Map<String, String>): CommandResult {
        JarvisLogger.i("Executing command: $commandType with params: $parameters")

        return when (commandType) {
            "TIMER" -> executeTimer(parameters)
            "CALCULATOR" -> executeCalculator(parameters)
            "TIME" -> executeTime(parameters)
            else -> CommandResult(false, "I'm not sure how to handle that command yet.")
        }
    }

    private fun executeTimer(params: Map<String, String>): CommandResult {
        val duration = params["duration"]?.toIntOrNull() ?: return CommandResult(false, "I didn't catch the timer duration.")
        val unit = params["unit"] ?: "minutes"

        val seconds = when {
            unit.startsWith("second") -> duration
            unit.startsWith("minute") -> duration * 60
            unit.startsWith("hour") -> duration * 3600
            else -> duration * 60
        }

        return CommandResult(
            success = true,
            response = "Timer set for $duration $unit. I'll let you know when it's up."
        )
    }

    private fun executeCalculator(params: Map<String, String>): CommandResult {
        val expression = params["expression"] ?: return CommandResult(false, "No expression found.")

        val result = evaluateExpression(expression)
        return if (result != null) {
            CommandResult(true, "= $result")
        } else {
            CommandResult(false, "I couldn't calculate that.")
        }
    }

    private fun executeTime(params: Map<String, String>): CommandResult {
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        return CommandResult(true, "It's $time.")
    }

    private fun evaluateExpression(expression: String): Double? {
        val cleaned = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("plus", "+")
            .replace("minus", "-")
            .replace("times", "*")
            .replace("divided by", "/")
            .trim()

        val regex = Regex("(-?\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(-?\\d+(?:\\.\\d+)?)")
        val match = regex.find(cleaned) ?: return null

        val a = match.groupValues[1].toDoubleOrNull() ?: return null
        val op = match.groupValues[2]
        val b = match.groupValues[3].toDoubleOrNull() ?: return null

        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b != 0.0) a / b else null
            else -> null
        }
    }
}
