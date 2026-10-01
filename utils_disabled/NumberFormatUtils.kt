package com.jarvis.assistant.utils

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

object NumberFormatUtils {

    fun format(number: Number, pattern: String = "#,##0.##"): String {
        val df = DecimalFormat(pattern)
        return df.format(number)
    }

    fun format(number: Number, locale: Locale): String {
        val nf = NumberFormat.getNumberInstance(locale)
        return nf.format(number)
    }

    fun formatInteger(number: Long, locale: Locale = Locale.getDefault()): String {
        val nf = NumberFormat.getIntegerInstance(locale)
        return nf.format(number)
    }

    fun formatCurrency(amount: Double, currencyCode: String = "USD", locale: Locale = Locale.getDefault()): String {
        val nf = NumberFormat.getCurrencyInstance(locale)
        return nf.format(amount)
    }

    fun formatPercent(value: Double, decimals: Int = 1, locale: Locale = Locale.getDefault()): String {
        val nf = NumberFormat.getPercentInstance(locale)
        nf.minimumFractionDigits = decimals
        nf.maximumFractionDigits = decimals
        return nf.format(value)
    }

    fun formatDecimal(value: Double, decimals: Int = 2): String {
        return "%.${decimals}f".format(Locale.US, value)
    }

    fun formatScientific(value: Double, decimals: Int = 2): String {
        return "%.${decimals}e".format(Locale.US, value)
    }

    fun formatEngineering(value: Double, decimals: Int = 2): String {
        if (value == 0.0) return "0"
        val exp = floor(log10(abs(value))).toInt()
        val engExp = exp - (exp % 3)
        val mantissa = value / 10.0.pow(engExp)
        return "%.${decimals}fE${engExp}".format(Locale.US, mantissa)
    }

    fun formatWithCommas(number: Long): String = "%,d".format(Locale.US, number)

    fun formatWithCommas(number: Double, decimals: Int = 2): String {
        return "%,.${decimals}f".format(Locale.US, number)
    }

    fun formatLeadingZeros(number: Int, width: Int): String = "%0${width}d".format(Locale.US, number)

    fun formatSign(number: Double): String = if (number >= 0) "+$number" else "$number"

    fun formatPlusMinus(number: Double): String = if (number >= 0) "±$number" else "∓$number"

    fun formatRange(min: Double, max: Double, separator: String = " - "): String = "$min$separator$max"

    fun formatOrdinal(number: Int): String {
        val suffixes = arrayOf("th", "st", "nd", "rd")
        val mod100 = number % 100
        val suffix = if (mod100 in 11..13) "th" else suffixes.getOrElse(number % 10) { "th" }
        return "$number$suffix"
    }

    fun formatRoman(number: Int): String {
        if (number <= 0 || number > 3999) return number.toString()
        val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val symbols = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        val result = StringBuilder()
        var num = number
        for (i in values.indices) {
            while (num >= values[i]) {
                result.append(symbols[i])
                num -= values[i]
            }
        }
        return result.toString()
    }

    fun formatBinary(number: Int): String = Integer.toBinaryString(number)

    fun formatOctal(number: Int): String = Integer.toOctalString(number)

    fun formatHex(number: Int): String = Integer.toHexString(number).uppercase(Locale.getDefault())

    fun formatHex(number: Long): String = java.lang.Long.toHexString(number).uppercase(Locale.getDefault())

    fun formatBase(number: Long, radix: Int): String = number.toString(radix)

    fun formatFixedWidth(number: Double, width: Int, decimals: Int): String {
        return "%${width}.${decimals}f".format(Locale.US, number)
    }

    fun formatAligned(numbers: List<Double>, decimals: Int = 2): List<String> {
        val maxLen = numbers.maxOfOrNull { "%.${decimals}f".format(Locale.US, it).length } ?: 0
        return numbers.map { "%${maxLen}.${decimals}f".format(Locale.US, it) }
    }

    fun formatTable(numbers: List<List<Double>>, decimals: Int = 2): List<String> {
        if (numbers.isEmpty()) return emptyList()
        val colCount = numbers[0].size
        val colWidths = IntArray(colCount)
        for (row in numbers) {
            for (i in row.indices) {
                val len = "%.${decimals}f".format(Locale.US, row[i]).length
                if (len > colWidths[i]) colWidths[i] = len
            }
        }
        return numbers.map { row ->
            row.mapIndexed { i, v -> "%${colWidths[i]}.${decimals}f".format(Locale.US, v) }.joinToString(" | ")
        }
    }

    fun formatMatrix(matrix: Array<DoubleArray>, decimals: Int = 2): String {
        if (matrix.isEmpty()) return ""
        val colCount = matrix[0].size
        val colWidths = IntArray(colCount)
        for (row in matrix) {
            for (i in row.indices) {
                val len = "%.${decimals}f".format(Locale.US, row[i]).length
                if (len > colWidths[i]) colWidths[i] = len
            }
        }
        return matrix.joinToString("\n") { row ->
            row.mapIndexed { i, v -> "%${colWidths[i]}.${decimals}f".format(Locale.US, v) }.joinToString("  ")
        }
    }

    fun formatPercentage(value: Double, decimals: Int = 1): String {
        return "%.${decimals}f%%".format(Locale.US, value)
    }

    fun formatFraction(numerator: Int, denominator: Int): String = "$numerator/$denominator"

    fun formatMixedNumber(whole: Int, numerator: Int, denominator: Int): String {
        return if (whole == 0) "$numerator/$denominator" else "$whole $numerator/$denominator"
    }

    fun formatRatio(a: Int, b: Int): String {
        val gcd = gcd(a, b)
        return "${a / gcd}:${b / gcd}"
    }

    fun formatAspectRatio(width: Int, height: Int): String = formatRatio(width, height)

    fun formatSlope(rise: Int, run: Int): String = formatRatio(rise, run)

    fun formatGradient(rise: Double, run: Double): String {
        if (run == 0.0) return "undefined"
        return formatDecimal(rise / run)
    }

    fun formatSlopeIntercept(slope: Double, intercept: Double): String {
        val sign = if (intercept >= 0) "+" else "-"
        return "y = ${formatDecimal(slope)}x $sign ${formatDecimal(abs(intercept))}"
    }

    fun formatPointSlope(slope: Double, x: Double, y: Double): String {
        val sign = if (y >= 0) "+" else "-"
        return "y $sign ${formatDecimal(abs(y))} = ${formatDecimal(slope)}(x - ${formatDecimal(x)})"
    }

    fun formatTwoPoint(x1: Double, y1: Double, x2: Double, y2: Double): String {
        val slope = (y2 - y1) / (x2 - x1)
        return formatSlopeIntercept(slope, y1 - slope * x1)
    }

    fun formatQuadratic(a: Double, b: Double, c: Double): String {
        val parts = mutableListOf<String>()
        if (a != 0.0) parts.add("${formatDecimal(a)}x²")
        if (b != 0.0) parts.add("${if (b > 0 && parts.isNotEmpty()) "+" else ""}${formatDecimal(b)}x")
        if (c != 0.0) parts.add("${if (c > 0 && parts.isNotEmpty()) "+" else ""}${formatDecimal(c)}")
        return parts.joinToString(" ")
    }

    fun formatPolynomial(coefficients: List<Double>): String {
        val parts = mutableListOf<String>()
        for (i in coefficients.indices.reversed()) {
            val c = coefficients[i]
            if (c == 0.0) continue
            val term = when (i) {
                0 -> formatDecimal(c)
                1 -> "${formatDecimal(c)}x"
                else -> "${formatDecimal(c)}x^$i"
            }
            if (c > 0 && parts.isNotEmpty()) parts.add("+ $term") else parts.add(term)
        }
        return parts.joinToString(" ")
    }

    fun formatExponential(base: Double, exponent: Double): String {
        return "${formatDecimal(base)}^${formatDecimal(exponent)}"
    }

    fun formatLogarithm(base: Double, value: Double): String {
        return "log_${formatDecimal(base)}(${formatDecimal(value)})"
    }

    fun formatTrigonometric(function: String, angle: Double): String {
        return "$function(${formatDecimal(angle)})"
    }

    fun formatComplex(real: Double, imaginary: Double): String {
        val sign = if (imaginary >= 0) "+" else "-"
        return "${formatDecimal(real)} $sign ${formatDecimal(abs(imaginary))}i"
    }

    fun formatVector(components: List<Double>): String {
        return "(${components.joinToString(", ") { formatDecimal(it) }})"
    }

    fun formatMatrixLatex(matrix: Array<DoubleArray>, decimals: Int = 2): String {
        val rows = matrix.joinToString(" \\\\ ") { row ->
            row.joinToString(" & ") { "%.${decimals}f".format(Locale.US, it) }
        }
        return "\\begin{pmatrix} $rows \\end{pmatrix}"
    }

    fun formatNumberLine(numbers: List<Double>, decimals: Int = 2): String {
        return numbers.joinToString(" → ") { "%.${decimals}f".format(Locale.US, it) }
    }

    fun formatNumberSequence(numbers: List<Double>, decimals: Int = 2): String {
        return numbers.joinToString(", ") { "%.${decimals}f".format(Locale.US, it) }
    }

    fun formatInterval(start: Double, end: Double, startInclusive: Boolean = true, endInclusive: Boolean = true): String {
        val left = if (startInclusive) "[" else "("
        val right = if (endInclusive) "]" else ")"
        return "$left${formatDecimal(start)}, ${formatDecimal(end)}$right"
    }

    fun formatOpenInterval(start: Double, end: Double): String = formatInterval(start, end, false, false)

    fun formatClosedInterval(start: Double, end: Double): String = formatInterval(start, end, true, true)

    fun formatHalfOpenInterval(start: Double, end: Double): String = formatInterval(start, end, true, false)

    fun formatHalfClosedInterval(start: Double, end: Double): String = formatInterval(start, end, false, true)

    fun formatSet(elements: List<Double>, decimals: Int = 2): String {
        return "{${elements.joinToString(", ") { "%.${decimals}f".format(Locale.US, it) }}}"
    }

    fun formatTuple(elements: List<Double>, decimals: Int = 2): String {
        return "(${elements.joinToString(", ") { "%.${decimals}f".format(Locale.US, it) }})"
    }

    fun formatCoordinate(x: Double, y: Double, decimals: Int = 2): String {
        return "(${"%.${decimals}f".format(Locale.US, x)}, ${"%.${decimals}f".format(Locale.US, y)})"
    }

    fun formatCoordinate3D(x: Double, y: Double, z: Double, decimals: Int = 2): String {
        return "(${"%.${decimals}f".format(Locale.US, x)}, ${"%.${decimals}f".format(Locale.US, y)}, ${"%.${decimals}f".format(Locale.US, z)})"
    }

    fun formatPolar(radius: Double, angle: Double, decimals: Int = 2): String {
        return "(${"%.${decimals}f".format(Locale.US, radius)}, ${"%.${decimals}f".format(Locale.US, angle)}°)"
    }

    fun formatSpherical(radius: Double, theta: Double, phi: Double, decimals: Int = 2): String {
        return "(${"%.${decimals}f".format(Locale.US, radius)}, ${"%.${decimals}f".format(Locale.US, theta)}°, ${"%.${decimals}f".format(Locale.US, phi)}°)"
    }

    fun formatCylindrical(radius: Double, theta: Double, z: Double, decimals: Int = 2): String {
        return "(${"%.${decimals}f".format(Locale.US, radius)}, ${"%.${decimals}f".format(Locale.US, theta)}°, ${"%.${decimals}f".format(Locale.US, z)})"
    }

    fun formatGeographic(latitude: Double, longitude: Double, decimals: Int = 6): String {
        val latDir = if (latitude >= 0) "N" else "S"
        val lonDir = if (longitude >= 0) "E" else "W"
        return "${"%.${decimals}f".format(Locale.US, abs(latitude))}°$latDir, ${"%.${decimals}f".format(Locale.US, abs(longitude))}°$lonDir"
    }

    fun formatDMS(decimalDegrees: Double, isLatitude: Boolean = true): String {
        val direction = if (isLatitude) {
            if (decimalDegrees >= 0) "N" else "S"
        } else {
            if (decimalDegrees >= 0) "E" else "W"
        }
        val abs = abs(decimalDegrees)
        val degrees = abs.toInt()
        val minutesFull = (abs - degrees) * 60
        val minutes = minutesFull.toInt()
        val seconds = (minutesFull - minutes) * 60
        return "$degrees°${minutes}'${formatDecimal(seconds)}\"$direction"
    }

    fun formatTime(hours: Int, minutes: Int, seconds: Int): String {
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

    fun formatTime12(hours: Int, minutes: Int, seconds: Int): String {
        val period = if (hours < 12) "AM" else "PM"
        val h = if (hours == 0) 12 else if (hours > 12) hours - 12 else hours
        return "%02d:%02d:%02d $period".format(h, minutes, seconds)
    }

    fun formatDuration(hours: Int, minutes: Int, seconds: Int): String {
        return "${hours}h ${minutes}m ${seconds}s"
    }

    fun formatDuration(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return when {
            days > 0 -> "${days}d ${hours % 24}h ${minutes % 60}m ${seconds % 60}s"
            hours > 0 -> "${hours}h ${minutes % 60}m ${seconds % 60}s"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val units = arrayOf("KB", "MB", "GB", "TB", "PB")
        var value = bytes.toDouble()
        var unitIndex = -1
        do {
            value /= 1024
            unitIndex++
        } while (value >= 1024 && unitIndex < units.size - 1)
        return "%.2f %s".format(Locale.US, value, units[unitIndex])
    }

    fun formatSpeed(bytesPerSecond: Long): String = "${formatFileSize(bytesPerSecond)}/s"

    fun formatDataRate(bitsPerSecond: Long): String {
        if (bitsPerSecond < 1000) return "$bitsPerSecond bps"
        val units = arrayOf("Kbps", "Mbps", "Gbps", "Tbps")
        var value = bitsPerSecond.toDouble()
        var unitIndex = -1
        do {
            value /= 1000
            unitIndex++
        } while (value >= 1000 && unitIndex < units.size - 1)
        return "%.2f %s".format(Locale.US, value, units[unitIndex])
    }

    fun formatFrequency(hz: Double): String {
        if (hz < 1000) return "${formatDecimal(hz)} Hz"
        val units = arrayOf("kHz", "MHz", "GHz", "THz")
        var value = hz
        var unitIndex = -1
        do {
            value /= 1000
            unitIndex++
        } while (value >= 1000 && unitIndex < units.size - 1)
        return "%.2f %s".format(Locale.US, value, units[unitIndex])
    }

    fun formatWavelength(meters: Double): String {
        if (meters >= 1) return "${formatDecimal(meters)} m"
        if (meters >= 0.001) return "${formatDecimal(meters * 1000)} mm"
        if (meters >= 0.000001) return "${formatDecimal(meters * 1000000)} μm"
        return "${formatDecimal(meters * 1000000000)} nm"
    }

    fun formatTemperature(celsius: Double): String = "${formatDecimal(celsius)}°C"

    fun formatTemperatureFahrenheit(celsius: Double): String {
        val f = celsius * 9 / 5 + 32
        return "${formatDecimal(f)}°F"
    }

    fun formatTemperatureKelvin(celsius: Double): String {
        val k = celsius + 273.15
        return "${formatDecimal(k)} K"
    }

    fun formatPressure(pascals: Double): String {
        if (pascals < 1000) return "${formatDecimal(pascals)} Pa"
        val units = arrayOf("kPa", "MPa", "GPa")
        var value = pascals
        var unitIndex = -1
        do {
            value /= 1000
            unitIndex++
        } while (value >= 1000 && unitIndex < units.size - 1)
        return "%.2f %s".format(Locale.US, value, units[unitIndex])
    }

    fun formatEnergy(joules: Double): String {
        if (joules < 1000) return "${formatDecimal(joules)} J"
        val units = arrayOf("kJ", "MJ", "GJ", "TJ")
        var value = joules
        var unitIndex = -1
        do {
            value /= 1000
            unitIndex++
        } while (value >= 1000 && unitIndex < units.size - 1)
        return "%.2f %s".format(Locale.US, value, units[unitIndex])
    }

    fun formatPower(watts: Double): String {
        if (watts < 1000) return "${formatDecimal(watts)} W"
        val units = arrayOf("kW", "MW", "GW", "TW")
        var value = watts
        var unitIndex = -1
        do {
            value /= 1000
            unitIndex++
        } while (value >= 1000 && unitIndex < units.size - 1)
        return "%.2f %s".format(Locale.US, value, units[unitIndex])
    }

    fun formatVoltage(volts: Double): String {
        if (volts < 1) return "${formatDecimal(volts * 1000)} mV"
        if (volts < 1000) return "${formatDecimal(volts)} V"
        return "${formatDecimal(volts / 1000)} kV"
    }

    fun formatCurrent(amperes: Double): String {
        if (amperes < 0.001) return "${formatDecimal(amperes * 1000000)} μA"
        if (amperes < 1) return "${formatDecimal(amperes * 1000)} mA"
        return "${formatDecimal(amperes)} A"
    }

    fun formatResistance(ohms: Double): String {
        if (ohms < 1) return "${formatDecimal(ohms * 1000)} mΩ"
        if (ohms < 1000) return "${formatDecimal(ohms)} Ω"
        if (ohms < 1000000) return "${formatDecimal(ohms / 1000)} kΩ"
        return "${formatDecimal(ohms / 1000000)} MΩ"
    }

    fun formatCapacitance(farads: Double): String {
        if (farads < 0.000001) return "${formatDecimal(farads * 1000000000)} nF"
        if (farads < 0.001) return "${formatDecimal(farads * 1000000)} μF"
        if (farads < 1) return "${formatDecimal(farads * 1000)} mF"
        return "${formatDecimal(farads)} F"
    }

    fun formatInductance(henries: Double): String {
        if (henries < 0.001) return "${formatDecimal(henries * 1000000)} μH"
        if (henries < 1) return "${formatDecimal(henries * 1000)} mH"
        return "${formatDecimal(henries)} H"
    }

    fun formatAngle(degrees: Double): String = "${formatDecimal(degrees)}°"

    fun formatAngleRadians(radians: Double): String = "${formatDecimal(radians)} rad"

    fun formatAngleGradians(gradians: Double): String = "${formatDecimal(gradians)} gon"

    fun formatAngleTurns(turns: Double): String = "${formatDecimal(turns)} turns"

    fun formatAngleDMS(degrees: Int, minutes: Int, seconds: Double): String {
        return "$degrees°${minutes}'${formatDecimal(seconds)}\""
    }

    fun formatAngleCompass(degrees: Double): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = ((degrees + 11.25) / 22.5).toInt() % 16
        return directions[index]
    }

    fun formatDistance(meters: Double): String {
        if (meters < 1) return "${formatDecimal(meters * 1000)} mm"
        if (meters < 1000) return "${formatDecimal(meters)} m"
        return "${formatDecimal(meters / 1000)} km"
    }

    fun formatDistanceMiles(miles: Double): String = "${formatDecimal(miles)} mi"

    fun formatDistanceNauticalMiles(nauticalMiles: Double): String = "${formatDecimal(nauticalMiles)} nmi"

    fun formatDistanceFeet(feet: Double): String = "${formatDecimal(feet)} ft"

    fun formatDistanceInches(inches: Double): String = "${formatDecimal(inches)} in"

    fun formatDistanceYards(yards: Double): String = "${formatDecimal(yards)} yd"

    fun formatArea(squareMeters: Double): String {
        if (squareMeters < 10000) return "${formatDecimal(squareMeters)} m²"
        if (squareMeters < 1000000) return "${formatDecimal(squareMeters / 10000)} ha"
        return "${formatDecimal(squareMeters / 1000000)} km²"
    }

    fun formatVolume(cubicMeters: Double): String {
        if (cubicMeters < 0.001) return "${formatDecimal(cubicMeters * 1000000)} mL"
        if (cubicMeters < 1) return "${formatDecimal(cubicMeters * 1000)} L"
        return "${formatDecimal(cubicMeters)} m³"
    }

    fun mass(kilograms: Double): String {
        if (kilograms < 0.001) return "${formatDecimal(kilograms * 1000000)} mg"
        if (kilograms < 1) return "${formatDecimal(kilograms * 1000)} g"
        if (kilograms < 1000) return "${formatDecimal(kilograms)} kg"
        return "${formatDecimal(kilograms / 1000)} t"
    }

    fun formatMassPounds(pounds: Double): String = "${formatDecimal(pounds)} lb"

    fun formatMassOunces(ounces: Double): String = "${formatDecimal(ounces)} oz"

    fun formatMassTons(tons: Double): String = "${formatDecimal(tons)} tons"

    fun formatMassGrains(grains: Double): String = "${formatDecimal(grains)} gr"

    fun formatMassCarats(carats: Double): String = "${formatDecimal(carats)} ct"

    fun formatVelocity(metersPerSecond: Double): String = "${formatDecimal(metersPerSecond)} m/s"

    fun formatVelocityKmh(kmh: Double): String = "${formatDecimal(kmh)} km/h"

    fun formatVelocityMph(mph: Double): String = "${formatDecimal(mph)} mph"

    fun formatVelocityKnots(knots: Double): String = "${formatDecimal(knots)} knots"

    fun formatVelocityMach(mach: Double): String = "Mach ${formatDecimal(mach)}"

    fun formatAcceleration(metersPerSecondSquared: Double): String = "${formatDecimal(metersPerSecondSquared)} m/s²"

    fun formatForce(newtons: Double): String = "${formatDecimal(newtons)} N"

    fun formatTorque(newtonMeters: Double): String = "${formatDecimal(newtonMeters)} N⋅m"

    fun formatDensity(kilogramsPerCubicMeter: Double): String = "${formatDecimal(kilogramsPerCubicMeter)} kg/m³"

    fun formatViscosity(pascalSeconds: Double): String = "${formatDecimal(pascalSeconds)} Pa⋅s"

    fun formatSurfaceTension(newtonsPerMeter: Double): String = "${formatDecimal(newtonsPerMeter)} N/m"

    fun formatHeatFlux(wattsPerSquareMeter: Double): String = "${formatDecimal(wattsPerSquareMeter)} W/m²"

    fun formatThermalConductivity(wattsPerMeterKelvin: Double): String = "${formatDecimal(wattsPerMeterKelvin)} W/(m⋅K)"

    fun formatSpecificHeat(joulesPerKilogramKelvin: Double): String = "${formatDecimal(joulesPerKilogramKelvin)} J/(kg⋅K)"

    fun formatThermalExpansion(1PerKelvin: Double): String = "${formatDecimal(1PerKelvin)} K⁻¹"

    fun formatElectricField(voltsPerMeter: Double): String = "${formatDecimal(voltsPerMeter)} V/m"

    fun formatMagneticField(teslas: Double): String = "${formatDecimal(teslas)} T"

    fun formatMagneticFlux(webers: Double): String = "${formatDecimal(webers)} Wb"

    fun formatMagneticFluxDensity(teslas: Double): String = "${formatDecimal(teslas)} T"

    fun formatLuminance(candelasPerSquareMeter: Double): String = "${formatDecimal(candelasPerSquareMeter)} cd/m²"

    fun formatIlluminance(lux: Double): String = "${formatDecimal(lux)} lx"

    fun formatLuminousFlux(lumens: Double): String = "${formatDecimal(lumens)} lm"

    fun formatLuminousIntensity(candelas: Double): String = "${formatDecimal(candelas)} cd"

    fun formatRadioactivity(becquerels: Double): String = "${formatDecimal(becquerels)} Bq"

    fun formatAbsorbedGray(grays: Double): String = "${formatDecimal(grays)} Gy"

    fun formatEquivalentDose(sieverts: Double): String = "${formatDecimal(sieverts)} Sv"

    fun formatCatalysis(katals: Double): String = "${formatDecimal(katals)} kat"

    fun formatMolarity(molesPerLiter: Double): String = "${formatDecimal(molesPerLiter)} mol/L"

    fun formatMolality(molesPerKilogram: Double): String = "${formatDecimal(molesPerKilogram)} mol/kg"

    fun formatMoleFraction(moleFraction: Double): String = "${formatDecimal(moleFraction)} mol/mol"

    fun formatConcentration(partsPerMillion: Double): String = "${formatDecimal(partsPerMillion)} ppm"

    fun formatConcentrationPartsPerBillion(ppb: Double): String = "${formatDecimal(ppb)} ppb"

    fun formatConcentrationPartsPerTrillion(ppt: Double): String = "${formatDecimal(ppt)} ppt"

    fun formatConcentrationPercent(percent: Double): String = "${formatDecimal(percent)}%"

    fun formatConcentrationPerMille(perMille: Double): String = "${formatDecimal(perMille)}‰"

    fun formatConcentrationBasisPoints(basisPoints: Double): String = "${formatDecimal(basisPoints)} bp"

    fun formatConcentrationMolar(molar: Double): String = "${formatDecimal(molar)} M"

    fun formatConcentrationMillimolar(millimolar: Double): String = "${formatDecimal(millimolar)} mM"

    fun formatConcentrationMicromolar(micromolar: Double): String = "${formatDecimal(micromolar)} μM"

    fun formatConcentrationNanomolar(nanomolar: Double): String = "${formatDecimal(nanomolar)} nM"

    fun formatConcentrationPicomolar(picomolar: Double): String = "${formatDecimal(picomolar)} pM"

    fun formatConcentrationFemtomolar(femtomolar: Double): String = "${formatDecimal(femtomolar)} fM"

    fun formatConcentrationAttomolar(attomolar: Double): String = "${formatDecimal(attomolar)} aM"

    fun formatConcentrationZeptomolar(zeptomolar: Double): String = "${formatDecimal(zeptomolar)} zM"

    fun formatConcentrationYoctomolar(yoctomolar: Double): String = "${formatDecimal(yoctomolar)} yM"

    fun formatPH(ph: Double): String = "${formatDecimal(ph)} pH"

    fun formatPOH(poh: Double): String = "${formatDecimal(poh)} pOH"

    fun formatPKa(pka: Double): String = "${formatDecimal(pka)} pKa"

    fun formatPKb(pkb: Double): String = "${formatDecimal(pkb)} pKb"

    fun formatPKw(pkw: Double): String = "${formatDecimal(pkw)} pKw"

    fun formatPKc(pkc: Double): String = "${formatDecimal(pkc)} pKc"

    fun formatPKsp(pksp: Double): String = "${formatDecimal(pksp)} pKsp"

    fun formatPKow(pkow: Double): String = "${formatDecimal(pkow)} pKow"

    fun formatPKd(pkd: Double): String = "${formatDecimal(pkd)} pKd"

    fun formatPKi(pki: Double): String = "${formatDecimal(pki)} pKi"

    fun formatPKm(pkm: Double): String = "${formatDecimal(pkm)} pKm"

    fun formatPKcat(pkcat: Double): String = "${formatDecimal(pkcat)} pKcat"

    fun formatPKmApp(pkmApp: Double): String = "${formatDecimal(pkmApp)} pKmApp"

    fun formatPKiApp(pkiApp: Double): String = "${formatDecimal(pkiApp)} pKiApp"

    fun formatPKdApp(pkdApp: Double): String = "${formatDecimal(pkdApp)} pKdApp"

    fun formatPKowApp(pkowApp: Double): String = "${formatDecimal(pkowApp)} pKowApp"

    fun formatPKspApp(pkspApp: Double): String = "${formatDecimal(pkspApp)} pKspApp"

    fun formatPKcApp(pkcApp: Double): String = "${formatDecimal(pkcApp)} pKcApp"

    fun formatPKwApp(pkwApp: Double): String = "${formatDecimal(pkwApp)} pKwApp"

    fun formatPKbApp(pkbApp: Double): String = "${formatDecimal(pkbApp)} pKbApp"

    fun formatPKaApp(pkaApp: Double): String = "${formatDecimal(pkaApp)} pKaApp"

    fun formatPKmTrue(pkmTrue: Double): String = "${formatDecimal(pkmTrue)} pKmTrue"

    fun formatPKiTrue(pkiTrue: Double): String = "${formatDecimal(pkiTrue)} pKiTrue"

    fun formatPKdTrue(pkdTrue: Double): String = "${formatDecimal(pkdTrue)} pKdTrue"

    fun formatPKowTrue(pkowTrue: Double): String = "${formatDecimal(pkowTrue)} pKowTrue"

    fun formatPKspTrue(pkspTrue: Double): String = "${formatDecimal(pkspTrue)} pKspTrue"

    fun formatPKcTrue(pkcTrue: Double): String = "${formatDecimal(pkcTrue)} pKcTrue"

    fun formatPKwTrue(pkwTrue: Double): String = "${formatDecimal(pkwTrue)} pKwTrue"

    fun formatPKbTrue(pkbTrue: Double): String = "${formatDecimal(pkbTrue)} pKbTrue"

    fun formatPKaTrue(pkaTrue: Double): String = "${formatDecimal(pkaTrue)} pKaTrue"

    fun formatPKmIntrinsic(pkmIntrinsic: Double): String = "${formatDecimal(pkmIntrinsic)} pKmIntrinsic"

    fun formatPKiIntrinsic(pkiIntrinsic: Double): String = "${formatDecimal(pkiIntrinsic)} pKiIntrinsic"

    fun formatPKdIntrinsic(pkdIntrinsic: Double): String = "${formatDecimal(pkdIntrinsic)} pKdIntrinsic"

    fun formatPKowIntrinsic(pkowIntrinsic: Double): String = "${formatDecimal(pkowIntrinsic)} pKowIntrinsic"

    fun formatPKspIntrinsic(pkspIntrinsic: Double): String = "${formatDecimal(pkspIntrinsic)} pKspIntrinsic"

    fun formatPKcIntrinsic(pkcIntrinsic: Double): String = "${formatDecimal(pkcIntrinsic)} pKcIntrinsic"

    fun formatPKwIntrinsic(pkwIntrinsic: Double): String = "${formatDecimal(pkwIntrinsic)} pKwIntrinsic"

    fun formatPKbIntrinsic(pkbIntrinsic: Double): String = "${formatDecimal(pkbIntrinsic)} pKbIntrinsic"

    fun formatPKaIntrinsic(pkaIntrinsic: Double): String = "${formatDecimal(pkaIntrinsic)} pKaIntrinsic"

    fun formatPKmApparent(pkmApparent: Double): String = "${formatDecimal(pkmApparent)} pKmApparent"

    fun formatPKiApparent(pkiApparent: Double): String = "${formatDecimal(pkiApparent)} pKiApparent"

    fun formatPKdApparent(pkdApparent: Double): String = "${formatDecimal(pkdApparent)} pKdApparent"

    fun formatPKowApparent(pkowApparent: Double): String = "${formatDecimal(pkowApparent)} pKowApparent"

    fun formatPKspApparent(pkspApparent: Double): String = "${formatDecimal(pkspApparent)} pKspApparent"

    fun formatPKcApparent(pkcApparent: Double): String = "${formatDecimal(pkcApparent)} pKcApparent"

    fun formatPKwApparent(pkwApparent: Double): String = "${formatDecimal(pkwApparent)} pKwApparent"

    fun formatPKbApparent(pkbApparent: Double): String = "${formatDecimal(pkbApparent)} pKbApparent"

    fun formatPKaApparent(pkaApparent: Double): String = "${formatDecimal(pkaApparent)} pKaApparent"

    fun formatPKmConditional(pkmConditional: Double): String = "${formatDecimal(pkmConditional)} pKmConditional"

    fun formatPKiConditional(pkiConditional: Double): String = "${formatDecimal(pkiConditional)} pKiConditional"

    fun formatPKdConditional(pkdConditional: Double): String = "${formatDecimal(pkdConditional)} pKdConditional"

    fun formatPKowConditional(pkowConditional: Double): String = "${formatDecimal(pkowConditional)} pKowConditional"

    fun formatPKspConditional(pkspConditional: Double): String = "${formatDecimal(pkspConditional)} pKspConditional"

    fun formatPKcConditional(pkcConditional: Double): String = "${formatDecimal(pkcConditional)} pKcConditional"

    fun formatPKwConditional(pkwConditional: Double): String = "${formatDecimal(pkwConditional)} pKwConditional"

    fun formatPKbConditional(pkbConditional: Double): String = "${formatDecimal(pkbConditional)} pKbConditional"

    fun formatPKaConditional(pkaConditional: Double): String = "${formatDecimal(pkaConditional)} pKaConditional"

    fun formatPKmMixed(pkmMixed: Double): String = "${formatDecimal(pkmMixed)} pKmMixed"

    fun formatPKiMixed(pkiMixed: Double): String = "${formatDecimal(pkiMixed)} pKiMixed"

    fun formatPKdMixed(pkdMixed: Double): String = "${formatDecimal(pkdMixed)} pKdMixed"

    fun formatPKowMixed(pkowMixed: Double): String = "${formatDecimal(pkowMixed)} pKowMixed"

    fun formatPKspMixed(pkspMixed: Double): String = "${formatDecimal(pkspMixed)} pKspMixed"

    fun formatPKcMixed(pkcMixed: Double): String = "${formatDecimal(pkcMixed)} pKcMixed"

    fun formatPKwMixed(pkwMixed: Double): String = "${formatDecimal(pkwMixed)} pKwMixed"

    fun formatPKbMixed(pkbMixed: Double): String = "${formatDecimal(pkbMixed)} pKbMixed"

    fun formatPKaMixed(pkaMixed: Double): String = "${formatDecimal(pkaMixed)} pKaMixed"

    fun formatPKmComplex(pkmComplex: Double): String = "${formatDecimal(pkmComplex)} pKmComplex"

    fun formatPKiComplex(pkiComplex: Double): String = "${formatDecimal(pkiComplex)} pKiComplex"

    fun formatPKdComplex(pkdComplex: Double): String = "${formatDecimal(pkdComplex)} pKdComplex"

    fun formatPKowComplex(pkowComplex: Double): String = "${formatDecimal(pkowComplex)} pKowComplex"

    fun formatPKspComplex(pkspComplex: Double): String = "${formatDecimal(pkspComplex)} pKspComplex"

    fun formatPKcComplex(pkcComplex: Double): String = "${formatDecimal(pkcComplex)} pKcComplex"

    fun formatPKwComplex(pkwComplex: Double): String = "${formatDecimal(pkwComplex)} pKwComplex"

    fun formatPKbComplex(pkbComplex: Double): String = "${formatDecimal(pkbComplex)} pKbComplex"

    fun formatPKaComplex(pkaComplex: Double): String = "${formatDecimal(pkaComplex)} pKaComplex"

    fun formatPKmOverall(pkmOverall: Double): String = "${formatDecimal(pkmOverall)} pKmOverall"

    fun formatPKiOverall(pkiOverall: Double): String = "${formatDecimal(pkiOverall)} pKiOverall"

    fun formatPKdOverall(pkdOverall: Double): String = "${formatDecimal(pkdOverall)} pKdOverall"

    fun formatPKowOverall(pkowOverall: Double): String = "${formatDecimal(pkowOverall)} pKowOverall"

    fun formatPKspOverall(pkspOverall: Double): String = "${formatDecimal(pkspOverall)} pKspOverall"

    fun formatPKcOverall(pkcOverall: Double): String = "${formatDecimal(pkcOverall)} pKcOverall"

    fun formatPKwOverall(pkwOverall: Double): String = "${formatDecimal(pkwOverall)} pKwOverall"

    fun formatPKbOverall(pkbOverall: Double): String = "${formatDecimal(pkbOverall)} pKbOverall"

    fun formatPKaOverall(pkaOverall: Double): String = "${formatDecimal(pkaOverall)} pKaOverall"

    fun formatPKmStepwise(pkmStepwise: Double): String = "${formatDecimal(pkmStepwise)} pKmStepwise"

    fun formatPKiStepwise(pkiStepwise: Double): String = "${formatDecimal(pkiStepwise)} pKiStepwise"

    fun formatPKdStepwise(pkdStepwise: Double): String = "${formatDecimal(pkdStepwise)} pKdStepwise"

    fun formatPKowStepwise(pkowStepwise: Double): String = "${formatDecimal(pkowStepwise)} pKowStepwise"

    fun formatPKspStepwise(pkspStepwise: Double): String = "${formatDecimal(pkspStepwise)} pKspStepwise"

    fun formatPKcStepwise(pkcStepwise: Double): String = "${formatDecimal(pkcStepwise)} pKcStepwise"

    fun formatPKwStepwise(pkwStepwise: Double): String = "${formatDecimal(pkwStepwise)} pKwStepwise"

    fun formatPKbStepwise(pkbStepwise: Double): String = "${formatDecimal(pkbStepwise)} pKbStepwise"

    fun formatPKaStepwise(pkaStepwise: Double): String = "${formatDecimal(pkaStepwise)} pKaStepwise"

    fun formatPKmMacroscopic(pkmMacroscopic: Double): String = "${formatDecimal(pkmMacroscopic)} pKmMacroscopic"

    fun formatPKiMacroscopic(pkiMacroscopic: Double): String = "${formatDecimal(pkiMacroscopic)} pKiMacroscopic"

    fun formatPKdMacroscopic(pkdMacroscopic: Double): String = "${formatDecimal(pkdMacroscopic)} pKdMacroscopic"

    fun formatPKowMacroscopic(pkowMacroscopic: Double): String = "${formatDecimal(pkowMacroscopic)} pKowMacroscopic"

    fun formatPKspMacroscopic(pkspMacroscopic: Double): String = "${formatDecimal(pkspMacroscopic)} pKspMacroscopic"

    fun formatPKcMacroscopic(pkcMacroscopic: Double): String = "${formatDecimal(pkcMacroscopic)} pKcMacroscopic"

    fun formatPKwMacroscopic(pkwMacroscopic: Double): String = "${formatDecimal(pkwMacroscopic)} pKwMacroscopic"

    fun formatPKbMacroscopic(pkbMacroscopic: Double): String = "${formatDecimal(pkbMacroscopic)} pKbMacroscopic"

    fun formatPKaMacroscopic(pkaMacroscopic: Double): String = "${formatDecimal(pkaMacroscopic)} pKaMacroscopic"

    fun formatPKmMicroscopic(pkmMicroscopic: Double): String = "${formatDecimal(pkmMicroscopic)} pKmMicroscopic"

    fun formatPKiMicroscopic(pkiMicroscopic: Double): String = "${formatDecimal(pkiMicroscopic)} pKiMicroscopic"

    fun formatPKdMicroscopic(pkdMicroscopic: Double): String = "${formatDecimal(pkdMicroscopic)} pKdMicroscopic"

    fun formatPKowMicroscopic(pkowMicroscopic: Double): String = "${formatDecimal(pkowMicroscopic)} pKowMicroscopic"

    fun formatPKspMicroscopic(pkspMicroscopic: Double): String = "${formatDecimal(pkspMicroscopic)} pKspMicroscopic"

    fun formatPKcMicroscopic(pkcMicroscopic: Double): String = "${formatDecimal(pkcMicroscopic)} pKcMicroscopic"

    fun formatPKwMicroscopic(pkwMicroscopic: Double): String = "${formatDecimal(pkwMicroscopic)} pKwMicroscopic"

    fun formatPKbMicroscopic(pkbMicroscopic: Double): String = "${formatDecimal(pkbMicroscopic)} pKbMicroscopic"

    fun formatPKaMicroscopic(pkaMicroscopic: Double): String = "${formatDecimal(pkaMicroscopic)} pKaMicroscopic"

    fun formatPKmThermodynamic(pkmThermodynamic: Double): String = "${formatDecimal(pkmThermodynamic)} pKmThermodynamic"

    fun formatPKiThermodynamic(pkiThermodynamic: Double): String = "${formatDecimal(pkiThermodynamic)} pKiThermodynamic"

    fun formatPKdThermodynamic(pkdThermodynamic: Double): String = "${formatDecimal(pkdThermodynamic)} pKdThermodynamic"

    fun formatPKowThermodynamic(pkowThermodynamic: Double): String = "${formatDecimal(pkowThermodynamic)} pKowThermodynamic"

    fun formatPKspThermodynamic(pkspThermodynamic: Double): String = "${formatDecimal(pkspThermodynamic)} pKspThermodynamic"

    fun formatPKcThermodynamic(pkcThermodynamic: Double): String = "${formatDecimal(pkcThermodynamic)} pKcThermodynamic"

    fun formatPKwThermodynamic(pkwThermodynamic: Double): String = "${formatDecimal(pkwThermodynamic)} pKwThermodynamic"

    fun formatPKbThermodynamic(pkbThermodynamic: Double): String = "${formatDecimal(pkbThermodynamic)} pKbThermodynamic"

    fun formatPKaThermodynamic(pkaThermodynamic: Double): String = "${formatDecimal(pkaThermodynamic)} pKaThermodynamic"

    fun formatPKmKinetic(pkmKinetic: Double): String = "${formatDecimal(pkmKinetic)} pKmKinetic"

    fun formatPKiKinetic(pkiKinetic: Double): String = "${formatDecimal(pkiKinetic)} pKiKinetic"

    fun formatPKdKinetic(pkdKinetic: Double): String = "${formatDecimal(pkdKinetic)} pKdKinetic"

    fun formatPKowKinetic(pkowKinetic: Double): String = "${formatDecimal(pkowKinetic)} pKowKinetic"

    fun formatPKspKinetic(pkspKinetic: Double): String = "${formatDecimal(pkspKinetic)} pKspKinetic"

    fun formatPKcKinetic(pkcKinetic: Double): String = "${formatDecimal(pkcKinetic)} pKcKinetic"

    fun formatPKwKinetic(pkwKinetic: Double): String = "${formatDecimal(pkwKinetic)} pKwKinetic"

    fun formatPKbKinetic(pkbKinetic: Double): String = "${formatDecimal(pkbKinetic)} pKbKinetic"

    fun formatPKaKinetic(pkaKinetic: Double): String = "${formatDecimal(pkaKinetic)} pKaKinetic"

    fun formatPKmEquilibrium(pkmEquilibrium: Double): String = "${formatDecimal(pkmEquilibrium)} pKmEquilibrium"

    fun formatPKiEquilibrium(pkiEquilibrium: Double): String = "${formatDecimal(pkiEquilibrium)} pKiEquilibrium"

    fun formatPKdEquilibrium(pkdEquilibrium: Double): String = "${formatDecimal(pkdEquilibrium)} pKdEquilibrium"

    fun formatPKowEquilibrium(pkowEquilibrium: Double): String = "${formatDecimal(pkowEquilibrium)} pKowEquilibrium"

    fun formatPKspEquilibrium(pkspEquilibrium: Double): String = "${formatDecimal(pkspEquilibrium)} pKspEquilibrium"

    fun formatPKcEquilibrium(pkcEquilibrium: Double): String = "${formatDecimal(pkcEquilibrium)} pKcEquilibrium"

    fun formatPKwEquilibrium(pkwEquilibrium: Double): String = "${formatDecimal(pkwEquilibrium)} pKwEquilibrium"

    fun formatPKbEquilibrium(pkbEquilibrium: Double): String = "${formatDecimal(pkbEquilibrium)} pKbEquilibrium"

    fun formatPKaEquilibrium(pkaEquilibrium: Double): String = "${formatDecimal(pkaEquilibrium)} pKaEquilibrium"

    fun formatPKmStandard(pkmStandard: Double): String = "${formatDecimal(pkmStandard)} pKmStandard"

    fun formatPKiStandard(pkiStandard: Double): String = "${formatDecimal(pkiStandard)} pKiStandard"

    fun formatPKdStandard(pkdStandard: Double): String = "${formatDecimal(pkdStandard)} pKdStandard"

    fun formatPKowStandard(pkowStandard: Double): String = "${formatDecimal(pkowStandard)} pKowStandard"

    fun formatPKspStandard(pkspStandard: Double): String = "${formatDecimal(pkspStandard)} pKspStandard"

    fun formatPKcStandard(pkcStandard: Double): String = "${formatDecimal(pkcStandard)} pKcStandard"

    fun formatPKwStandard(pkwStandard: Double): String = "${formatDecimal(pkwStandard)} pKwStandard"

    fun formatPKbStandard(pkbStandard: Double): String = "${formatDecimal(pkbStandard)} pKbStandard"

    fun formatPKaStandard(pkaStandard: Double): String = "${formatDecimal(pkaStandard)} pKaStandard"

    fun formatPKmFormal(pkmFormal: Double): String = "${formatDecimal(pkmFormal)} pKmFormal"

    fun formatPKiFormal(pkiFormal: Double): String = "${formatDecimal(pkiFormal)} pKiFormal"

    fun formatPKdFormal(pkdFormal: Double): String = "${formatDecimal(pkdFormal)} pKdFormal"

    fun formatPKowFormal(pkowFormal: Double): String = "${formatDecimal(pkowFormal)} pKowFormal"

    fun formatPKspFormal(pkspFormal: Double): String = "${formatDecimal(pkspFormal)} pKspFormal"

    fun formatPKcFormal(pkcFormal: Double): String = "${formatDecimal(pkcFormal)} pKcFormal"

    fun formatPKwFormal(pkwFormal: Double): String = "${formatDecimal(pkwFormal)} pKwFormal"

    fun formatPKbFormal(pkbFormal: Double): String = "${formatDecimal(pkbFormal)} pKbFormal"

    fun formatPKaFormal(pkaFormal: Double): String = "${formatDecimal(pkaFormal)} pKaFormal"

    fun formatPKmAnalytical(pkmAnalytical: Double): String = "${formatDecimal(pkmAnalytical)} pKmAnalytical"

    fun formatPKiAnalytical(pkiAnalytical: Double): String = "${formatDecimal(pkiAnalytical)} pKiAnalytical"

    fun formatPKdAnalytical(pkdAnalytical: Double): String = "${formatDecimal(pkdAnalytical)} pKdAnalytical"

    fun formatPKowAnalytical(pkowAnalytical: Double): String = "${formatDecimal(pkowAnalytical)} pKowAnalytical"

    fun formatPKspAnalytical(pkspAnalytical: Double): String = "${formatDecimal(pkspAnalytical)} pKspAnalytical"

    fun formatPKcAnalytical(pkcAnalytical: Double): String = "${formatDecimal(pkcAnalytical)} pKcAnalytical"

    fun formatPKwAnalytical(pkwAnalytical: Double): String = "${formatDecimal(pkwAnalytical)} pKwAnalytical"

    fun formatPKbAnalytical(pkbAnalytical: Double): String = "${formatDecimal(pkbAnalytical)} pKbAnalytical"

    fun formatPKaAnalytical(pkaAnalytical: Double): String = "${formatDecimal(pkaAnalytical)} pKaAnalytical"

    fun formatPKmExperimental(pkmExperimental: Double): String = "${formatDecimal(pkmExperimental)} pKmExperimental"

    fun formatPKiExperimental(pkiExperimental: Double): String = "${formatDecimal(pkiExperimental)} pKiExperimental"

    fun formatPKdExperimental(pkdExperimental: Double): String = "${formatDecimal(pkdExperimental)} pKdExperimental"

    fun formatPKowExperimental(pkowExperimental: Double): String = "${formatDecimal(pkowExperimental)} pKowExperimental"

    fun formatPKspExperimental(pkspExperimental: Double): String = "${formatDecimal(pkspExperimental)} pKspExperimental"

    fun formatPKcExperimental(pkcExperimental: Double): String = "${formatDecimal(pkcExperimental)} pKcExperimental"

    fun formatPKwExperimental(pkwExperimental: Double): String = "${formatDecimal(pkwExperimental)} pKwExperimental"

    fun formatPKbExperimental(pkbExperimental: Double): String = "${formatDecimal(pkbExperimental)} pKbExperimental"

    fun formatPKaExperimental(pkaExperimental: Double): String = "${formatDecimal(pkaExperimental)} pKaExperimental"

    fun formatPKmCalculated(pkmCalculated: Double): String = "${formatDecimal(pkmCalculated)} pKmCalculated"

    fun formatPKiCalculated(pkiCalculated: Double): String = "${formatDecimal(pkiCalculated)} pKiCalculated"

    fun formatPKdCalculated(pkdCalculated: Double): String = "${formatDecimal(pkdCalculated)} pKdCalculated"

    fun formatPKowCalculated(pkowCalculated: Double): String = "${formatDecimal(pkowCalculated)} pKowCalculated"

    fun formatPKspCalculated(pkspCalculated: Double): String = "${formatDecimal(pkspCalculated)} pKspCalculated"

    fun formatPKcCalculated(pkcCalculated: Double): String = "${formatDecimal(pkcCalculated)} pKcCalculated"

    fun formatPKwCalculated(pkwCalculated: Double): String = "${formatDecimal(pkwCalculated)} pKwCalculated"

    fun formatPKbCalculated(pkbCalculated: Double): String = "${formatDecimal(pkbCalculated)} pKbCalculated"

    fun formatPKaCalculated(pkaCalculated: Double): String = "${formatDecimal(pkaCalculated)} pKaCalculated"

    fun formatPKmEstimated(pkmEstimated: Double): String = "${formatDecimal(pkmEstimated)} pKmEstimated"

    fun formatPKiEstimated(pkiEstimated: Double): String = "${formatDecimal(pkiEstimated)} pKiEstimated"

    fun formatPKdEstimated(pkdEstimated: Double): String = "${formatDecimal(pkdEstimated)} pKdEstimated"

    fun formatPKowEstimated(pkowEstimated: Double): String = "${formatDecimal(pkowEstimated)} pKowEstimated"

    fun formatPKspEstimated(pkspEstimated: Double): String = "${formatDecimal(pkspEstimated)} pKspEstimated"

    fun formatPKcEstimated(pkcEstimated: Double): String = "${formatDecimal(pkcEstimated)} pKcEstimated"

    fun formatPKwEstimated(pkwEstimated: Double): String = "${formatDecimal(pkwEstimated)} pKwEstimated"

    fun formatPKbEstimated(pkbEstimated: Double): String = "${formatDecimal(pkbEstimated)} pKbEstimated"

    fun formatPKaEstimated(pkaEstimated: Double): String = "${formatDecimal(pkaEstimated)} pKaEstimated"

    fun formatPKmPredicted(pkmPredicted: Double): String = "${formatDecimal(pkmPredicted)} pKmPredicted"

    fun formatPKiPredicted(pkiPredicted: Double): String = "${formatDecimal(pkiPredicted)} pKiPredicted"

    fun formatPKdPredicted(pkdPredicted: Double): String = "${formatDecimal(pkdPredicted)} pKdPredicted"

    fun formatPKowPredicted(pkowPredicted: Double): String = "${formatDecimal(pkowPredicted)} pKowPredicted"

    fun formatPKspPredicted(pkspPredicted: Double): String = "${formatDecimal(pkspPredicted)} pKspPredicted"

    fun formatPKcPredicted(pkcPredicted: Double): String = "${formatDecimal(pkcPredicted)} pKcPredicted"

    fun formatPKwPredicted(pkwPredicted: Double): String = "${formatDecimal(pkwPredicted)} pKwPredicted"

    fun formatPKbPredicted(pkbPredicted: Double): String = "${formatDecimal(pkbPredicted)} pKbPredicted"

    fun formatPKaPredicted(pkaPredicted: Double): String = "${formatDecimal(pkaPredicted)} pKaPredicted"

    fun formatPKmTheoretical(pkmTheoretical: Double): String = "${formatDecimal(pkmTheoretical)} pKmTheoretical"

    fun formatPKiTheoretical(pkiTheoretical: Double): String = "${formatDecimal(pkiTheoretical)} pKiTheoretical"

    fun formatPKdTheoretical(pkdTheoretical: Double): String = "${formatDecimal(pkdTheoretical)} pKdTheoretical"

    fun formatPKowTheoretical(pkowTheoretical: Double): String = "${formatDecimal(pkowTheoretical)} pKowTheoretical"

    fun formatPKspTheoretical(pkspTheoretical: Double): String = "${formatDecimal(pkspTheoretical)} pKspTheoretical"

    fun formatPKcTheoretical(pkcTheoretical: Double): String = "${formatDecimal(pkcTheoretical)} pKcTheoretical"

    fun formatPKwTheoretical(pkwTheoretical: Double): String = "${formatDecimal(pkwTheoretical)} pKwTheoretical"

    fun formatPKbTheoretical(pkbTheoretical: Double): String = "${formatDecimal(pkbTheoretical)} pKbTheoretical"

    fun formatPKaTheoretical(pkaTheoretical: Double): String = "${formatDecimal(pkaTheoretical)} pKaTheoretical"

    fun formatPKmEmpirical(pkmEmpirical: Double): String = "${formatDecimal(pkmEmpirical)} pKmEmpirical"

    fun formatPKiEmpirical(pkiEmpirical: Double): String = "${formatDecimal(pkiEmpirical)} pKiEmpirical"

    fun formatPKdEmpirical(pkdEmpirical: Double): String = "${formatDecimal(pkdEmpirical)} pKdEmpirical"

    fun formatPKowEmpirical(pkowEmpirical: Double): String = "${formatDecimal(pkowEmpirical)} pKowEmpirical"

    fun formatPKspEmpirical(pkspEmpirical: Double): String = "${formatDecimal(pkspEmpirical)} pKspEmpirical"

    fun formatPKcEmpirical(pkcEmpirical: Double): String = "${formatDecimal(pkcEmpirical)} pKcEmpirical"

    fun formatPKwEmpirical(pkwEmpirical: Double): String = "${formatDecimal(pkwEmpirical)} pKwEmpirical"

    fun formatPKbEmpirical(pkbEmpirical: Double): String = "${formatDecimal(pkbEmpirical)} pKbEmpirical"

    fun formatPKaEmpirical(pkaEmpirical: Double): String = "${formatDecimal(pkaEmpirical)} pKaEmpirical"

    fun formatPKmSemiEmpirical(pkmSemiEmpirical: Double): String = "${formatDecimal(pkmSemiEmpirical)} pKmSemiEmpirical"

    fun formatPKiSemiEmpirical(pkiSemiEmpirical: Double): String = "${formatDecimal(pkiSemiEmpirical)} pKiSemiEmpirical"

    fun formatPKdSemiEmpirical(pkdSemiEmpirical: Double): String = "${formatDecimal(pkdSemiEmpirical)} pKdSemiEmpirical"

    fun formatPKowSemiEmpirical(pkowSemiEmpirical: Double): String = "${formatDecimal(pkowSemiEmpirical)} pKowSemiEmpirical"

    fun formatPKspSemiEmpirical(pkspSemiEmpirical: Double): String = "${formatDecimal(pkspSemiEmpirical)} pKspSemiEmpirical"

    fun formatPKcSemiEmpirical(pkcSemiEmpirical: Double): String = "${formatDecimal(pkcSemiEmpirical)} pKcSemiEmpirical"

    fun formatPKwSemiEmpirical(pkwSemiEmpirical: Double): String = "${formatDecimal(pkwSemiEmpirical)} pKwSemiEmpirical"

    fun formatPKbSemiEmpirical(pkbSemiEmpirical: Double): String = "${formatDecimal(pkbSemiEmpirical)} pKbSemiEmpirical"

    fun formatPKaSemiEmpirical(pkaSemiEmpirical: Double): String = "${formatDecimal(pkaSemiEmpirical)} pKaSemiEmpirical"

    fun formatPKmAbInitio(pkmAbInitio: Double): String = "${formatDecimal(pkmAbInitio)} pKmAbInitio"

    fun formatPKiAbInitio(pkiAbInitio: Double): String = "${formatDecimal(pkiAbInitio)} pKiAbInitio"

    fun formatPKdAbInitio(pkdAbInitio: Double): String = "${formatDecimal(pkdAbInitio)} pKdAbInitio"

    fun formatPKowAbInitio(pkowAbInitio: Double): String = "${formatDecimal(pkowAbInitio)} pKowAbInitio"

    fun formatPKspAbInitio(pkspAbInitio: Double): String = "${formatDecimal(pkspAbInitio)} pKspAbInitio"

    fun formatPKcAbInitio(pkcAbInitio: Double): String = "${formatDecimal(pkcAbInitio)} pKcAbInitio"

    fun formatPKwAbInitio(pkwAbInitio: Double): String = "${formatDecimal(pkwAbInitio)} pKwAbInitio"

    fun formatPKbAbInitio(pkbAbInitio: Double): String = "${formatDecimal(pkbAbInitio)} pKbAbInitio"

    fun formatPKaAbInitio(pkaAbInitio: Double): String = "${formatDecimal(pkaAbInitio)} pKaAbInitio"

    fun formatPKmDFT(pkmDFT: Double): String = "${formatDecimal(pkmDFT)} pKmDFT"

    fun formatPKiDFT(pkiDFT: Double): String = "${formatDecimal(pkiDFT)} pKiDFT"

    fun formatPKdDFT(pkdDFT: Double): String = "${formatDecimal(pkdDFT)} pKdDFT"

    fun formatPKowDFT(pkowDFT: Double): String = "${formatDecimal(pkowDFT)} pKowDFT"

    fun formatPKspDFT(pkspDFT: Double): String = "${formatDecimal(pkspDFT)} pKspDFT"

    fun formatPKcDFT(pkcDFT: Double): String = "${formatDecimal(pkcDFT)} pKcDFT"

    fun formatPKwDFT(pkwDFT: Double): String = "${formatDecimal(pkwDFT)} pKwDFT"

    fun formatPKbDFT(pkbDFT: Double): String = "${formatDecimal(pkbDFT)} pKbDFT"

    fun formatPKaDFT(pkaDFT: Double): String = "${formatDecimal(pkaDFT)} pKaDFT"

    fun formatPKmHF(pkmHF: Double): String = "${formatDecimal(pkmHF)} pKmHF"

    fun formatPKiHF(pkiHF: Double): String = "${formatDecimal(pkiHF)} pKiHF"

    fun formatPKdHF(pkdHF: Double): String = "${formatDecimal(pkdHF)} pKdHF"

    fun formatPKowHF(pkowHF: Double): String = "${formatDecimal(pkowHF)} pKowHF"

    fun formatPKspHF(pkspHF: Double): String = "${formatDecimal(pkspHF)} pKspHF"

    fun formatPKcHF(pkcHF: Double): String = "${formatDecimal(pkcHF)} pKcHF"

    fun formatPKwHF(pkwHF: Double): String = "${formatDecimal(pkwHF)} pKwHF"

    fun formatPKbHF(pkbHF: Double): String = "${formatDecimal(pkbHF)} pKbHF"

    fun formatPKaHF(pkaHF: Double): String = "${formatDecimal(pkaHF)} pKaHF"

    fun formatPKmMP2(pkmMP2: Double): String = "${formatDecimal(pkmMP2)} pKmMP2"

    fun formatPKiMP2(pkiMP2: Double): String = "${formatDecimal(pkiMP2)} pKiMP2"

    fun formatPKdMP2(pkdMP2: Double): String = "${formatDecimal(pkdMP2)} pKdMP2"

    fun formatPKowMP2(pkowMP2: Double): String = "${formatDecimal(pkowMP2)} pKowMP2"

    fun formatPKspMP2(pkspMP2: Double): String = "${formatDecimal(pkspMP2)} pKspMP2"

    fun formatPKcMP2(pkcMP2: Double): String = "${formatDecimal(pkcMP2)} pKcMP2"

    fun formatPKwMP2(pkwMP2: Double): String = "${formatDecimal(pkwMP2)} pKwMP2"

    fun formatPKbMP2(pkbMP2: Double): String = "${formatDecimal(pkbMP2)} pKbMP2"

    fun formatPKaMP2(pkaMP2: Double): String = "${formatDecimal(pkaMP2)} pKaMP2"

    fun formatPKmCCSD(pkmCCSD: Double): String = "${formatDecimal(pkmCCSD)} pKmCCSD"

    fun formatPKiCCSD(pkiCCSD: Double): String = "${formatDecimal(pkiCCSD)} pKiCCSD"

    fun formatPKdCCSD(pkdCCSD: Double): String = "${formatDecimal(pkdCCSD)} pKdCCSD"

    fun formatPKowCCSD(pkowCCSD: Double): String = "${formatDecimal(pkowCCSD)} pKowCCSD"

    fun formatPKspCCSD(pkspCCSD: Double): String = "${formatDecimal(pkspCCSD)} pKspCCSD"

    fun formatPKcCCSD(pkcCCSD: Double): String = "${formatDecimal(pkcCCSD)} pKcCCSD"

    fun formatPKwCCSD(pkwCCSD: Double): String = "${formatDecimal(pkwCCSD)} pKwCCSD"

    fun formatPKbCCSD(pkbCCSD: Double): String = "${formatDecimal(pkbCCSD)} pKbCCSD"

    fun formatPKaCCSD(pkaCCSD: Double): String = "${formatDecimal(pkaCCSD)} pKaCCSD"

    fun formatPKmCCSDT(pkmCCSDT: Double): String = "${formatDecimal(pkmCCSDT)} pKmCCSDT"

    fun formatPKiCCSDT(pkiCCSDT: Double): String = "${formatDecimal(pkiCCSDT)} pKiCCSDT"

    fun formatPKdCCSDT(pkdCCSDT: Double): String = "${formatDecimal(pkdCCSDT)} pKdCCSDT"

    fun formatPKowCCSDT(pkowCCSDT: Double): String = "${formatDecimal(pkowCCSDT)} pKowCCSDT"

    fun formatPKspCCSDT(pkspCCSDT: Double): String = "${formatDecimal(pkspCCSDT)} pKspCCSDT"

    fun formatPKcCCSDT(pkcCCSDT: Double): String = "${formatDecimal(pkcCCSDT)} pKcCCSDT"

    fun formatPKwCCSDT(pkwCCSDT: Double): String = "${formatDecimal(pkwCCSDT)} pKwCCSDT"

    fun formatPKbCCSDT(pkbCCSDT: Double): String = "${formatDecimal(pkbCCSDT)} pKbCCSDT"

    fun formatPKaCCSDT(pkaCCSDT: Double): String = "${formatDecimal(pkaCCSDT)} pKaCCSDT"

    fun formatPKmCASPT2(pkmCASPT2: Double): String = "${formatDecimal(pkmCASPT2)} pKmCASPT2"

    fun formatPKiCASPT2(pkiCASPT2: Double): String = "${formatDecimal(pkiCASPT2)} pKiCASPT2"

    fun formatPKdCASPT2(pkdCASPT2: Double): String = "${formatDecimal(pkdCASPT2)} pKdCASPT2"

    fun formatPKowCASPT2(pkowCASPT2: Double): String = "${formatDecimal(pkowCASPT2)} pKowCASPT2"

    fun formatPKspCASPT2(pkspCASPT2: Double): String = "${formatDecimal(pkspCASPT2)} pKspCASPT2"

    fun formatPKcCASPT2(pkcCASPT2: Double): String = "${formatDecimal(pkcCASPT2)} pKcCASPT2"

    fun formatPKwCASPT2(pkwCASPT2: Double): String = "${formatDecimal(pkwCASPT2)} pKwCASPT2"

    fun formatPKbCASPT2(pkbCASPT2: Double): String = "${formatDecimal(pkbCASPT2)} pKbCASPT2"

    fun formatPKaCASPT2(pkaCASPT2: Double): String = "${formatDecimal(pkaCASPT2)} pKaCASPT2"

    fun formatPKmNEVPT2(pkmNEVPT2: Double): String = "${formatDecimal(pkmNEVPT2)} pKmNEVPT2"

    fun formatPKiNEVPT2(pkiNEVPT2: Double): String = "${formatDecimal(pkiNEVPT2)} pKiNEVPT2"

    fun formatPKdNEVPT2(pkdNEVPT2: Double): String = "${formatDecimal(pkdNEVPT2)} pKdNEVPT2"

    fun formatPKowNEVPT2(pkowNEVPT2: Double): String = "${formatDecimal(pkowNEVPT2)} pKowNEVPT2"

    fun formatPKspNEVPT2(pkspNEVPT2: Double): String = "${formatDecimal(pkspNEVPT2)} pKspNEVPT2"

    fun formatPKcNEVPT2(pkcNEVPT2: Double): String = "${formatDecimal(pkcNEVPT2)} pKcNEVPT2"

    fun formatPKwNEVPT2(pkwNEVPT2: Double): String = "${formatDecimal(pkwNEVPT2)} pKwNEVPT2"

    fun formatPKbNEVPT2(pkbNEVPT2: Double): String = "${formatDecimal(pkbNEVPT2)} pKbNEVPT2"

    fun formatPKaNEVPT2(pkaNEVPT2: Double): String = "${formatDecimal(pkaNEVPT2)} pKaNEVPT2"

    fun formatPKmMRCI(pkmMRCI: Double): String = "${formatDecimal(pkmMRCI)} pKmMRCI"

    fun formatPKiMRCI(pkiMRCI: Double): String = "${formatDecimal(pkiMRCI)} pKiMRCI"

    fun formatPKdMRCI(pkdMRCI: Double): String = "${formatDecimal(pkdMRCI)} pKdMRCI"

    fun formatPKowMRCI(pkowMRCI: Double): String = "${formatDecimal(pkowMRCI)} pKowMRCI"

    fun formatPKspMRCI(pkspMRCI: Double): String = "${formatDecimal(pkspMRCI)} pKspMRCI"

    fun formatPKcMRCI(pkcMRCI: Double): String = "${formatDecimal(pkcMRCI)} pKcMRCI"

    fun formatPKwMRCI(pkwMRCI: Double): String = "${formatDecimal(pkwMRCI)} pKwMRCI"

    fun formatPKbMRCI(pkbMRCI: Double): String = "${formatDecimal(pkbMRCI)} pKbMRCI"

    fun formatPKaMRCI(pkaMRCI: Double): String = "${formatDecimal(pkaMRCI)} pKaMRCI"

    fun formatPKmCASSCF(pkmCASSCF: Double): String = "${formatDecimal(pkmCASSCF)} pKmCASSCF"

    fun formatPKiCASSCF(pkiCASSCF: Double): String = "${formatDecimal(pkiCASSCF)} pKiCASSCF"

    fun formatPKdCASSCF(pkdCASSCF: Double): String = "${formatDecimal(pkdCASSCF)} pKdCASSCF"

    fun formatPKowCASSCF(pkowCASSCF: Double): String = "${formatDecimal(pkowCASSCF)} pKowCASSCF"

    fun formatPKspCASSCF(pkspCASSCF: Double): String = "${formatDecimal(pkspCASSCF)} pKspCASSCF"

    fun formatPKcCASSCF(pkcCASSCF: Double): String = "${formatDecimal(pkcCASSCF)} pKcCASSCF"

    fun formatPKwCASSCF(pkwCASSCF: Double): String = "${formatDecimal(pkwCASSCF)} pKwCASSCF"

    fun formatPKbCASSCF(pkbCASSCF: Double): String = "${formatDecimal(pkbCASSCF)} pKbCASSCF"

    fun formatPKaCASSCF(pkaCASSCF: Double): String = "${formatDecimal(pkaCASSCF)} pKaCASSCF"

    fun formatPKmRASSCF(pkmRASSCF: Double): String = "${formatDecimal(pkmRASSCF)} pKmRASSCF"

    fun formatPKiRASSCF(pkiRASSCF: Double): String = "${formatDecimal(pkiRASSCF)} pKiRASSCF"

    fun formatPKdRASSCF(pkdRASSCF: Double): String = "${formatDecimal(pkdRASSCF)} pKdRASSCF"

    fun formatPKowRASSCF(pkowRASSCF: Double): String = "${formatDecimal(pkowRASSCF)} pKowRASSCF"

    fun formatPKspRASSCF(pkspRASSCF: Double): String = "${formatDecimal(pkspRASSCF)} pKspRASSCF"

    fun formatPKcRASSCF(pkcRASSCF: Double): String = "${formatDecimal(pkcRASSCF)} pKcRASSCF"

    fun formatPKwRASSCF(pkwRASSCF: Double): String = "${formatDecimal(pkwRASSCF)} pKwRASSCF"

    fun formatPKbRASSCF(pkbRASSCF: Double): String = "${formatDecimal(pkbRASSCF)} pKbRASSCF"

    fun formatPKaRASSCF(pkaRASSCF: Double): String = "${formatDecimal(pkaRASSCF)} pKaRASSCF"

    fun formatPKmGVB(pkmGVB: Double): String = "${formatDecimal(pkmGVB)} pKmGVB"

    fun formatPKiGVB(pkiGVB: Double): String = "${formatDecimal(pkiGVB)} pKiGVB"

    fun formatPKdGVB(pkdGVB: Double): String = "${formatDecimal(pkdGVB)} pKdGVB"

    fun formatPKowGVB(pkowGVB: Double): String = "${formatDecimal(pkowGVB)} pKowGVB"

    fun formatPKspGVB(pkspGVB: Double): String = "${formatDecimal(pkspGVB)} pKspGVB"

    fun formatPKcGVB(pkcGVB: Double): String = "${formatDecimal(pkcGVB)} pKcGVB"

    fun formatPKwGVB(pkwGVB: Double): String = "${formatDecimal(pkwGVB)} pKwGVB"

    fun formatPKbGVB(pkbGVB: Double): String = "${formatDecimal(pkbGVB)} pKbGVB"

    fun formatPKaGVB(pkaGVB: Double): String = "${formatDecimal(pkaGVB)} pKaGVB"

    fun formatPKmMCSCF(pkmMCSCF: Double): String = "${formatDecimal(pkmMCSCF)} pKmMCSCF"

    fun formatPKiMCSCF(pkiMCSCF: Double): String = "${formatDecimal(pkiMCSCF)} pKiMCSCF"

    fun formatPKdMCSCF(pkdMCSCF: Double): String = "${formatDecimal(pkdMCSCF)} pKdMCSCF"

    fun formatPKowMCSCF(pkowMCSCF: Double): String = "${formatDecimal(pkowMCSCF)} pKowMCSCF"

    fun formatPKspMCSCF(pkspMCSCF: Double): String = "${formatDecimal(pkspMCSCF)} pKspMCSCF"

    fun formatPKcMCSCF(pkcMCSCF: Double): String = "${formatDecimal(pkcMCSCF)} pKcMCSCF"

    fun formatPKwMCSCF(pkwMCSCF: Double): String = "${formatDecimal(pkwMCSCF)} pKwMCSCF"

    fun formatPKbMCSCF(pkbMCSCF: Double): String = "${formatDecimal(pkbMCSCF)} pKbMCSCF"

    fun formatPKaMCSCF(pkaMCSCF: Double): String = "${formatDecimal(pkaMCSCF)} pKaMCSCF"

    fun formatPKmCI(pkmCI: Double): String = "${formatDecimal(pkmCI)} pKmCI"

    fun formatPKiCI(pkiCI: Double): String = "${formatDecimal(pkiCI)} pKiCI"

    fun formatPKdCI(pkdCI: Double): String = "${formatDecimal(pkdCI)} pKdCI"

    fun formatPKowCI(pkowCI: Double): String = "${formatDecimal(pkowCI)} pKowCI"

    fun formatPKspCI(pkspCI: Double): String = "${formatDecimal(pkspCI)} pKspCI"

    fun formatPKcCI(pkcCI: Double): String = "${formatDecimal(pkcCI)} pKcCI"

    fun formatPKwCI(pkwCI: Double): String = "${formatDecimal(pkwCI)} pKwCI"

    fun formatPKbCI(pkbCI: Double): String = "${formatDecimal(pkbCI)} pKbCI"

    fun formatPKaCI(pkaCI: Double): String = "${formatDecimal(pkaCI)} pKaCI"

    fun formatPKmCISD(pkmCISD: Double): String = "${formatDecimal(pkmCISD)} pKmCISD"

    fun formatPKiCISD(pkiCISD: Double): String = "${formatDecimal(pkiCISD)} pKiCISD"

    fun formatPKdCISD(pkdCISD: Double): String = "${formatDecimal(pkdCISD)} pKdCISD"

    fun formatPKowCISD(pkowCISD: Double): String = "${formatDecimal(pkowCISD)} pKowCISD"

    fun formatPKspCISD(pkspCISD: Double): String = "${formatDecimal(pkspCISD)} pKspCISD"

    fun formatPKcCISD(pkcCISD: Double): String = "${formatDecimal(pkcCISD)} pKcCISD"

    fun formatPKwCISD(pkwCISD: Double): String = "${formatDecimal(pkwCISD)} pKwCISD"

    fun formatPKbCISD(pkbCISD: Double): String = "${formatDecimal(pkbCISD)} pKbCISD"

    fun formatPKaCISD(pkaCISD: Double): String = "${formatDecimal(pkaCISD)} pKaCISD"

    fun formatPKmCISDT(pkmCISDT: Double): String = "${formatDecimal(pkmCISDT)} pKmCISDT"

    fun formatPKiCISDT(pkiCISDT: Double): String = "${formatDecimal(pkiCISDT)} pKiCISDT"

    fun formatPKdCISDT(pkdCISDT: Double): String = "${formatDecimal(pkdCISDT)} pKdCISDT"

    fun formatPKowCISDT(pkowCISDT: Double): String = "${formatDecimal(pkowCISDT)} pKowCISDT"

    fun formatPKspCISDT(pkspCISDT: Double): String = "${formatDecimal(pkspCISDT)} pKspCISDT"

    fun formatPKcCISDT(pkcCISDT: Double): String = "${formatDecimal(pkcCISDT)} pKcCISDT"

    fun formatPKwCISDT(pkwCISDT: Double): String = "${formatDecimal(pkwCISDT)} pKwCISDT"

    fun formatPKbCISDT(pkbCISDT: Double): String = "${formatDecimal(pkbCISDT)} pKbCISDT"

    fun formatPKaCISDT(pkaCISDT: Double): String = "${formatDecimal(pkaCISDT)} pKaCISDT"

    fun formatPKmCISDTQ(pkmCISDTQ: Double): String = "${formatDecimal(pkmCISDTQ)} pKmCISDTQ"

    fun formatPKiCISDTQ(pkiCISDTQ: Double): String = "${formatDecimal(pkiCISDTQ)} pKiCISDTQ"

    fun formatPKdCISDTQ(pkdCISDTQ: Double): String = "${formatDecimal(pkdCISDTQ)} pKdCISDTQ"

    fun formatPKowCISDTQ(pkowCISDTQ: Double): String = "${formatDecimal(pkowCISDTQ)} pKowCISDTQ"

    fun formatPKspCISDTQ(pkspCISDTQ: Double): String = "${formatDecimal(pkspCISDTQ)} pKspCISDTQ"

    fun formatPKcCISDTQ(pkcCISDTQ: Double): String = "${formatDecimal(pkcCISDTQ)} pKcCISDTQ"

    fun formatPKwCISDTQ(pkwCISDTQ: Double): String = "${formatDecimal(pkwCISDTQ)} pKwCISDTQ"

    fun formatPKbCISDTQ(pkbCISDTQ: Double): String = "${formatDecimal(pkbCISDTQ)} pKbCISDTQ"

    fun formatPKaCISDTQ(pkaCISDTQ: Double): String = "${formatDecimal(pkaCISDTQ)} pKaCISDTQ"

    fun formatPKmFCI(pkmFCI: Double): String = "${formatDecimal(pkmFCI)} pKmFCI"

    fun formatPKiFCI(pkiFCI: Double): String = "${formatDecimal(pkiFCI)} pKiFCI"

    fun formatPKdFCI(pkdFCI: Double): String = "${formatDecimal(pkdFCI)} pKdFCI"

    fun formatPKowFCI(pkowFCI: Double): String = "${formatDecimal(pkowFCI)} pKowFCI"

    fun formatPKspFCI(pkspFCI: Double): String = "${formatDecimal(pkspFCI)} pKspFCI"

    fun formatPKcFCI(pkcFCI: Double): String = "${formatDecimal(pkcFCI)} pKcFCI"

    fun formatPKwFCI(pkwFCI: Double): String = "${formatDecimal(pkwFCI)} pKwFCI"

    fun formatPKbFCI(pkbFCI: Double): String = "${formatDecimal(pkbFCI)} pKbFCI"

    fun formatPKaFCI(pkaFCI: Double): String = "${formatDecimal(pkaFCI)} pKaFCI"

    fun formatPKmHCI(pkmHCI: Double): String = "${formatDecimal(pkmHCI)} pKmHCI"

    fun formatPKiHCI(pkiHCI: Double): String = "${formatDecimal(pkiHCI)} pKiHCI"

    fun formatPKdHCI(pkdHCI: Double): String = "${formatDecimal(pkdHCI)} pKdHCI"

    fun formatPKowHCI(pkowHCI: Double): String = "${formatDecimal(pkowHCI)} pKowHCI"

    fun formatPKspHCI(pkspHCI: Double): String = "${formatDecimal(pkspHCI)} pKspHCI"

    fun formatPKcHCI(pkcHCI: Double): String = "${formatDecimal(pkcHCI)} pKcHCI"

    fun formatPKwHCI(pkwHCI: Double): String = "${formatDecimal(pkwHCI)} pKwHCI"

    fun formatPKbHCI(pkbHCI: Double): String = "${formatDecimal(pkbHCI)} pKbHCI"

    fun formatPKaHCI(pkaHCI: Double): String = "${formatDecimal(pkaHCI)} pKaHCI"

    fun formatPKmSACCI(pkmSACCI: Double): String = "${formatDecimal(pkmSACCI)} pKmSACCI"

    fun formatPKiSACCI(pkiSACCI: Double): String = "${formatDecimal(pkiSACCI)} pKiSACCI"

    fun formatPKdSACCI(pkdSACCI: Double): String = "${formatDecimal(pkdSACCI)} pKdSACCI"

    fun formatPKowSACCI(pkowSACCI: Double): String = "${formatDecimal(pkowSACCI)} pKowSACCI"

    fun formatPKspSACCI(pkspSACCI: Double): String = "${formatDecimal(pkspSACCI)} pKspSACCI"

    fun formatPKcSACCI(pkcSACCI: Double): String = "${formatDecimal(pkcSACCI)} pKcSACCI"

    fun formatPKwSACCI(pkwSACCI: Double): String = "${formatDecimal(pkwSACCI)} pKwSACCI"

    fun formatPKbSACCI(pkbSACCI: Double): String = "${formatDecimal(pkbSACCI)} pKbSACCI"

    fun formatPKaSACCI(pkaSACCI: Double): String = "${formatDecimal(pkaSACCI)} pKaSACCI"

    fun formatPKmSAC(pkmSAC: Double): String = "${formatDecimal(pkmSAC)} pKmSAC"

    fun formatPKiSAC(pkiSAC: Double): String = "${formatDecimal(pkiSAC)} pKiSAC"

    fun formatPKdSAC(pkdSAC: Double): String = "${formatDecimal(pkdSAC)} pKdSAC"

    fun formatPKowSAC(pkowSAC: Double): String = "${formatDecimal(pkowSAC)} pKowSAC"

    fun formatPKspSAC(pkspSAC: Double): String = "${formatDecimal(pkspSAC)} pKspSAC"

    fun formatPKcSAC(pkcSAC: Double): String = "${formatDecimal(pkcSAC)} pKcSAC"

    fun formatPKwSAC(pkwSAC: Double): String = "${formatDecimal(pkwSAC)} pKwSAC"

    fun formatPKbSAC(pkbSAC: Double): String = "${formatDecimal(pkbSAC)} pKbSAC"

    fun formatPKaSAC(pkaSAC: Double): String = "${formatDecimal(pkaSAC)} pKaSAC"

    fun formatPKmSACCI2(pkmSACCI2: Double): String = "${formatDecimal(pkmSACCI2)} pKmSACCI2"

    fun formatPKiSACCI2(pkiSACCI2: Double): String = "${formatDecimal(pkiSACCI2)} pKiSACCI2"

    fun formatPKdSACCI2(pkdSACCI2: Double): String = "${formatDecimal(pkdSACCI2)} pKdSACCI2"

    fun formatPKowSACCI2(pkowSACCI2: Double): String = "${formatDecimal(pkowSACCI2)} pKowSACCI2"

    fun formatPKspSACCI2(pkspSACCI2: Double): String = "${formatDecimal(pkspSACCI2)} pKspSACCI2"

    fun formatPKcSACCI2(pkcSACCI2: Double): String = "${formatDecimal(pkcSACCI2)} pKcSACCI2"

    fun formatPKwSACCI2(pkwSACCI2: Double): String = "${formatDecimal(pkwSACCI2)} pKwSACCI2"

    fun formatPKbSACCI2(pkbSACCI2: Double): String = "${formatDecimal(pkbSACCI2)} pKbSACCI2"

    fun formatPKaSACCI2(pkaSACCI2: Double): String = "${formatDecimal(pkaSACCI2)} pKaSACCI2"

    fun formatPKmSACCI3(pkmSACCI3: Double): String = "${formatDecimal(pkmSACCI3)} pKmSACCI3"

    fun formatPKiSACCI3(pkiSACCI3: Double): String = "${formatDecimal(pkiSACCI3)} pKiSACCI3"

    fun formatPKdSACCI3(pkdSACCI3: Double): String = "${formatDecimal(pkdSACCI3)} pKdSACCI3"

    fun formatPKowSACCI3(pkowSACCI3: Double): String = "${formatDecimal(pkowSACCI3)} pKowSACCI3"

    fun formatPKspSACCI3(pkspSACCI3: Double): String = "${formatDecimal(pkspSACCI3)} pKspSACCI3"

    fun formatPKcSACCI3(pkcSACCI3: Double): String = "${formatDecimal(pkcSACCI3)} pKcSACCI3"

    fun formatPKwSACCI3(pkwSACCI3: Double): String = "${formatDecimal(pkwSACCI3)} pKwSACCI3"

    fun formatPKbSACCI3(pkbSACCI3: Double): String = "${formatDecimal(pkbSACCI3)} pKbSACCI3"

    fun formatPKaSACCI3(pkaSACCI3: Double): String = "${formatDecimal(pkaSACCI3)} pKaSACCI3"

    fun formatPKmSACCI4(pkmSACCI4: Double): String = "${formatDecimal(pkmSACCI4)} pKmSACCI4"

    fun formatPKiSACCI4(pkiSACCI4: Double): String = "${formatDecimal(pkiSACCI4)} pKiSACCI4"

    fun formatPKdSACCI4(pkdSACCI4: Double): String = "${formatDecimal(pkdSACCI4)} pKdSACCI4"

    fun formatPKowSACCI4(pkowSACCI4: Double): String = "${formatDecimal(pkowSACCI4)} pKowSACCI4"

    fun formatPKspSACCI4(pkspSACCI4: Double): String = "${formatDecimal(pkspSACCI4)} pKspSACCI4"

    fun formatPKcSACCI4(pkcSACCI4: Double): String = "${formatDecimal(pkcSACCI4)} pKcSACCI4"

    fun formatPKwSACCI4(pkwSACCI4: Double): String = "${formatDecimal(pkwSACCI4)} pKwSACCI4"

    fun formatPKbSACCI4(pkbSACCI4: Double): String = "${formatDecimal(pkbSACCI4)} pKbSACCI4"

    fun formatPKaSACCI4(pkaSACCI4: Double): String = "${formatDecimal(pkaSACCI4)} pKaSACCI4"

    fun formatPKmSACCI5(pkmSACCI5: Double): String = "${formatDecimal(pkmSACCI5)} pKmSACCI5"

    fun formatPKiSACCI5(pkiSACCI5: Double): String = "${formatDecimal(pkiSACCI5)} pKiSACCI5"

    fun formatPKdSACCI5(pkdSACCI5: Double): String = "${formatDecimal(pkdSACCI5)} pKdSACCI5"

    fun formatPKowSACCI5(pkowSACCI5: Double): String = "${formatDecimal(pkowSACCI5)} pKowSACCI5"

    fun formatPKspSACCI5(pkspSACCI5: Double): String = "${formatDecimal(pkspSACCI5)} pKspSACCI5"

    fun formatPKcSACCI5(pkcSACCI5: Double): String = "${formatDecimal(pkcSACCI5)} pKcSACCI5"

    fun formatPKwSACCI5(pkwSACCI5: Double): String = "${formatDecimal(pkwSACCI5)} pKwSACCI5"

    fun formatPKbSACCI5(pkbSACCI5: Double): String = "${formatDecimal(pkbSACCI5)} pKbSACCI5"

    fun formatPKaSACCI5(pkaSACCI5: Double): String = "${formatDecimal(pkaSACCI5)} pKaSACCI5"

    fun formatPKmSACCI6(pkmSACCI6: Double): String = "${formatDecimal(pkmSACCI6)} pKmSACCI6"

    fun formatPKiSACCI6(pkiSACCI6: Double): String = "${formatDecimal(pkiSACCI6)} pKiSACCI6"

    fun formatPKdSACCI6(pkdSACCI6: Double): String = "${formatDecimal(pkdSACCI6)} pKdSACCI6"

    fun formatPKowSACCI6(pkowSACCI6: Double): String = "${formatDecimal(pkowSACCI6)} pKowSACCI6"

    fun formatPKspSACCI6(pkspSACCI6: Double): String = "${formatDecimal(pkspSACCI6)} pKspSACCI6"

    fun formatPKcSACCI6(pkcSACCI6: Double): String = "${formatDecimal(pkcSACCI6)} pKcSACCI6"

    fun formatPKwSACCI6(pkwSACCI6: Double): String = "${formatDecimal(pkwSACCI6)} pKwSACCI6"

    fun formatPKbSACCI6(pkbSACCI6: Double): String = "${formatDecimal(pkbSACCI6)} pKbSACCI6"

    fun formatPKaSACCI6(pkaSACCI6: Double): String = "${formatDecimal(pkaSACCI6)} pKaSACCI6"

    fun formatPKmSACCI7(pkmSACCI7: Double): String = "${formatDecimal(pkmSACCI7)} pKmSACCI7"

    fun formatPKiSACCI7(pkiSACCI7: Double): String = "${formatDecimal(pkiSACCI7)} pKiSACCI7"

    fun formatPKdSACCI7(pkdSACCI7: Double): String = "${formatDecimal(pkdSACCI7)} pKdSACCI7"

    fun formatPKowSACCI7(pkowSACCI7: Double): String = "${formatDecimal(pkowSACCI7)} pKowSACCI7"

    fun formatPKspSACCI7(pkspSACCI7: Double): String = "${formatDecimal(pkspSACCI7)} pKspSACCI7"

    fun formatPKcSACCI7(pkcSACCI7: Double): String = "${formatDecimal(pkcSACCI7)} pKcSACCI7"

    fun formatPKwSACCI7(pkwSACCI7: Double): String = "${formatDecimal(pkwSACCI7)} pKwSACCI7"

    fun formatPKbSACCI7(pkbSACCI7: Double): String = "${formatDecimal(pkbSACCI7)} pKbSACCI7"

    fun formatPKaSACCI7(pkaSACCI7: Double): String = "${formatDecimal(pkaSACCI7)} pKaSACCI7"

    fun formatPKmSACCI8(pkmSACCI8: Double): String = "${formatDecimal(pkmSACCI8)} pKmSACCI8"

    fun formatPKiSACCI8(pkiSACCI8: Double): String = "${formatDecimal(pkiSACCI8)} pKiSACCI8"

    fun formatPKdSACCI8(pkdSACCI8: Double): String = "${formatDecimal(pkdSACCI8)} pKdSACCI8"

    fun formatPKowSACCI8(pkowSACCI8: Double): String = "${formatDecimal(pkowSACCI8)} pKowSACCI8"

    fun formatPKspSACCI8(pkspSACCI8: Double): String = "${formatDecimal(pkspSACCI8)} pKspSACCI8"

    fun formatPKcSACCI8(pkcSACCI8: Double): String = "${formatDecimal(pkcSACCI8)} pKcSACCI8"

    fun formatPKwSACCI8(pkwSACCI8: Double): String = "${formatDecimal(pkwSACCI8)} pKwSACCI8"

    fun formatPKbSACCI8(pkbSACCI8: Double): String = "${formatDecimal(pkbSACCI8)} pKbSACCI8"

    fun formatPKaSACCI8(pkaSACCI8: Double): String = "${formatDecimal(pkaSACCI8)} pKaSACCI8"

    fun formatPKmSACCI9(pkmSACCI9: Double): String = "${formatDecimal(pkmSACCI9)} pKmSACCI9"

    fun formatPKiSACCI9(pkiSACCI9: Double): String = "${formatDecimal(pkiSACCI9)} pKiSACCI9"

    fun formatPKdSACCI9(pkdSACCI9: Double): String = "${formatDecimal(pkdSACCI9)} pKdSACCI9"

    fun formatPKowSACCI9(pkowSACCI9: Double): String = "${formatDecimal(pkowSACCI9)} pKowSACCI9"

    fun formatPKspSACCI9(pkspSACCI9: Double): String = "${formatDecimal(pkspSACCI9)} pKspSACCI9"

    fun formatPKcSACCI9(pkcSACCI9: Double): String = "${formatDecimal(pkcSACCI9)} pKcSACCI9"

    fun formatPKwSACCI9(pkwSACCI9: Double): String = "${formatDecimal(pkwSACCI9)} pKwSACCI9"

    fun formatPKbSACCI9(pkbSACCI9: Double): String = "${formatDecimal(pkbSACCI9)} pKbSACCI9"

    fun formatPKaSACCI9(pkaSACCI9: Double): String = "${formatDecimal(pkaSACCI9)} pKaSACCI9"

    fun formatPKmSACCI10(pkmSACCI10: Double): String = "${formatDecimal(pkmSACCI10)} pKmSACCI10"

    fun formatPKiSACCI10(pkiSACCI10: Double): String = "${formatDecimal(pkiSACCI10)} pKiSACCI10"

    fun formatPKdSACCI10(pkdSACCI10: Double): String = "${formatDecimal(pkdSACCI10)} pKdSACCI10"

    fun formatPKowSACCI10(pkowSACCI10: Double): String = "${formatDecimal(pkowSACCI10)} pKowSACCI10"

    fun formatPKspSACCI10(pkspSACCI10: Double): String = "${formatDecimal(pkspSACCI10)} pKspSACCI10"

    fun formatPKcSACCI10(pkcSACCI10: Double): String = "${formatDecimal(pkcSACCI10)} pKcSACCI10"

    fun formatPKwSACCI10(pkwSACCI10: Double): String = "${formatDecimal(pkwSACCI10)} pKwSACCI10"

    fun formatPKbSACCI10(pkbSACCI10: Double): String = "${formatDecimal(pkbSACCI10)} pKbSACCI10"

    fun formatPKaSACCI10(pkaSACCI10: Double): String = "${formatDecimal(pkaSACCI10)} pKaSACCI10"

    fun formatPKmSACCI11(pkmSACCI11: Double): String = "${formatDecimal(pkmSACCI11)} pKmSACCI11"

    fun formatPKiSACCI11(pkiSACCI11: Double): String = "${formatDecimal(pkiSACCI11)} pKiSACCI11"

    fun formatPKdSACCI11(pkdSACCI11: Double): String = "${formatDecimal(pkdSACCI11)} pKdSACCI11"

    fun formatPKowSACCI11(pkowSACCI11: Double): String = "${formatDecimal(pkowSACCI11)} pKowSACCI11"

    fun formatPKspSACCI11(pkspSACCI11: Double): String = "${formatDecimal(pkspSACCI11)} pKspSACCI11"

    fun formatPKcSACCI11(pkcSACCI11: Double): String = "${formatDecimal(pkcSACCI11)} pKcSACCI11"

    fun formatPKwSACCI11(pkwSACCI11: Double): String = "${formatDecimal(pkwSACCI11)} pKwSACCI11"

    fun formatPKbSACCI11(pkbSACCI11: Double): String = "${formatDecimal(pkbSACCI11)} pKbSACCI11"

    fun formatPKaSACCI11(pkaSACCI11: Double): String = "${formatDecimal(pkaSACCI11)} pKaSACCI11"

    fun formatPKmSACCI12(pkmSACCI12: Double): String = "${formatDecimal(pkmSACCI12)} pKmSACCI12"

    fun formatPKiSACCI12(pkiSACCI12: Double): String = "${formatDecimal(pkiSACCI12)} pKiSACCI12"

    fun formatPKdSACCI12(pkdSACCI12: Double): String = "${formatDecimal(pkdSACCI12)} pKdSACCI12"

    fun formatPKowSACCI12(pkowSACCI12: Double): String = "${formatDecimal(pkowSACCI12)} pKowSACCI12"

    fun formatPKspSACCI12(pkspSACCI12: Double): String = "${formatDecimal(pkspSACCI12)} pKspSACCI12"

    fun formatPKcSACCI12(pkcSACCI12: Double): String = "${formatDecimal(pkcSACCI12)} pKcSACCI12"

    fun formatPKwSACCI12(pkwSACCI12: Double): String = "${formatDecimal(pkwSACCI12)} pKwSACCI12"

    fun formatPKbSACCI12(pkbSACCI12: Double): String = "${formatDecimal(pkbSACCI12)} pKbSACCI12"

    fun formatPKaSACCI12(pkaSACCI12: Double): String = "${formatDecimal(pkaSACCI12)} pKaSACCI12"

    fun formatPKmSACCI13(pkmSACCI13: Double): String = "${formatDecimal(pkmSACCI13)} pKmSACCI13"

    fun formatPKiSACCI13(pkiSACCI13: Double): String = "${formatDecimal(pkiSACCI13)} pKiSACCI13"

    fun formatPKdSACCI13(pkdSACCI13: Double): String = "${formatDecimal(pkdSACCI13)} pKdSACCI13"

    fun formatPKowSACCI13(pkowSACCI13: Double): String = "${formatDecimal(pkowSACCI13)} pKowSACCI13"

    fun formatPKspSACCI13(pkspSACCI13: Double): String = "${formatDecimal(pkspSACCI13)} pKspSACCI13"

    fun formatPKcSACCI13(pkcSACCI13: Double): String = "${formatDecimal(pkcSACCI13)} pKcSACCI13"

    fun formatPKwSACCI13(pkwSACCI13: Double): String = "${formatDecimal(pkwSACCI13)} pKwSACCI13"

    fun formatPKbSACCI13(pkbSACCI13: Double): String = "${formatDecimal(pkbSACCI13)} pKbSACCI13"

    fun formatPKaSACCI13(pkaSACCI13: Double): String = "${formatDecimal(pkaSACCI13)} pKaSACCI13"

    fun formatPKmSACCI14(pkmSACCI14: Double): String = "${formatDecimal(pkmSACCI14)} pKmSACCI14"

    fun formatPKiSACCI14(pkiSACCI14: Double): String = "${formatDecimal(pkiSACCI14)} pKiSACCI14"

    fun formatPKdSACCI14(pkdSACCI14: Double): String = "${formatDecimal(pkdSACCI14)} pKdSACCI14"

    fun formatPKowSACCI14(pkowSACCI14: Double): String = "${formatDecimal(pkowSACCI14)} pKowSACCI14"

    fun formatPKspSACCI14(pkspSACCI14: Double): String = "${formatDecimal(pkspSACCI14)} pKspSACCI14"

    fun formatPKcSACCI14(pkcSACCI14: Double): String = "${formatDecimal(pkcSACCI14)} pKcSACCI14"

    fun formatPKwSACCI14(pkwSACCI14: Double): String = "${formatDecimal(pkwSACCI14)} pKwSACCI14"

    fun formatPKbSACCI14(pkbSACCI14: Double): String = "${formatDecimal(pkbSACCI14)} pKbSACCI14"

    fun formatPKaSACCI14(pkaSACCI14: Double): String = "${formatDecimal(pkaSACCI14)} pKaSACCI14"

    fun formatPKmSACCI15(pkmSACCI15: Double): String = "${formatDecimal(pkmSACCI15)} pKmSACCI15"

    fun formatPKiSACCI15(pkiSACCI15: Double): String = "${formatDecimal(pkiSACCI15)} pKiSACCI15"

    fun formatPKdSACCI15(pkdSACCI15: Double): String = "${formatDecimal(pkdSACCI15)} pKdSACCI15"

    fun formatPKowSACCI15(pkowSACCI15: Double): String = "${formatDecimal(pkowSACCI15)} pKowSACCI15"

    fun formatPKspSACCI15(pkspSACCI15: Double): String = "${formatDecimal(pkspSACCI15)} pKspSACCI15"

    fun formatPKcSACCI15(pkcSACCI15: Double): String = "${formatDecimal(pkcSACCI15)} pKcSACCI15"

    fun formatPKwSACCI15(pkwSACCI15: Double): String = "${formatDecimal(pkwSACCI15)} pKwSACCI15"

    fun formatPKbSACCI15(pkbSACCI15: Double): String = "${formatDecimal(pkbSACCI15)} pKbSACCI15"

    fun formatPKaSACCI15(pkaSACCI15: Double): String = "${formatDecimal(pkaSACCI15)} pKaSACCI15"

    fun formatPKmSACCI16(pkmSACCI16: Double): String = "${formatDecimal(pkmSACCI16)} pKmSACCI16"

    fun formatPKiSACCI16(pkiSACCI16: Double): String = "${formatDecimal(pkiSACCI16)} pKiSACCI16"

    fun formatPKdSACCI16(pkdSACCI16: Double): String = "${formatDecimal(pkdSACCI16)} pKdSACCI16"

    fun formatPKowSACCI16(pkowSACCI16: Double): String = "${formatDecimal(pkowSACCI16)} pKowSACCI16"

    fun formatPKspSACCI16(pkspSACCI16: Double): String = "${formatDecimal(pkspSACCI16)} pKspSACCI16"

    fun formatPKcSACCI16(pkcSACCI16: Double): String = "${formatDecimal(pkcSACCI16)} pKcSACCI16"

    fun formatPKwSACCI16(pkwSACCI16: Double): String = "${formatDecimal(pkwSACCI16)} pKwSACCI16"

    fun formatPKbSACCI16(pkbSACCI16: Double): String = "${formatDecimal(pkbSACCI16)} pKbSACCI16"

    fun formatPKaSACCI16(pkaSACCI16: Double): String = "${formatDecimal(pkaSACCI16)} pKaSACCI16"

    fun formatPKmSACCI17(pkmSACCI17: Double): String = "${formatDecimal(pkmSACCI17)} pKmSACCI17"

    fun formatPKiSACCI17(pkiSACCI17: Double): String = "${formatDecimal(pkiSACCI17)} pKiSACCI17"

    fun formatPKdSACCI17(pkdSACCI17: Double): String = "${formatDecimal(pkdSACCI17)} pKdSACCI17"

    fun formatPKowSACCI17(pkowSACCI17: Double): String = "${formatDecimal(pkowSACCI17)} pKowSACCI17"

    fun formatPKspSACCI17(pkspSACCI17: Double): String = "${formatDecimal(pkspSACCI17)} pKspSACCI17"

    fun formatPKcSACCI17(pkcSACCI17: Double): String = "${formatDecimal(pkcSACCI17)} pKcSACCI17"

    fun formatPKwSACCI17(pkwSACCI17: Double): String = "${formatDecimal(pkwSACCI17)} pKwSACCI17"

    fun formatPKbSACCI17(pkbSACCI17: Double): String = "${formatDecimal(pkbSACCI17)} pKbSACCI17"

    fun formatPKaSACCI17(pkaSACCI17: Double): String = "${formatDecimal(pkaSACCI17)} pKaSACCI17"

    fun formatPKmSACCI18(pkmSACCI18: Double): String = "${formatDecimal(pkmSACCI18)} pKmSACCI18"

    fun formatPKiSACCI18(pkiSACCI18: Double): String = "${formatDecimal(pkiSACCI18)} pKiSACCI18"

    fun formatPKdSACCI18(pkdSACCI18: Double): String = "${formatDecimal(pkdSACCI18)} pKdSACCI18"

    fun formatPKowSACCI18(pkowSACCI18: Double): String = "${formatDecimal(pkowSACCI18)} pKowSACCI18"

    fun formatPKspSACCI18(pkspSACCI18: Double): String = "${formatDecimal(pkspSACCI18)} pKspSACCI18"

    fun formatPKcSACCI18(pkcSACCI18: Double): String = "${formatDecimal(pkcSACCI18)} pKcSACCI18"

    fun formatPKwSACCI18(pkwSACCI18: Double): String = "${formatDecimal(pkwSACCI18)} pKwSACCI18"

    fun formatPKbSACCI18(pkbSACCI18: Double): String = "${formatDecimal(pkbSACCI18)} pKbSACCI18"

    fun formatPKaSACCI18(pkaSACCI18: Double): String = "${formatDecimal(pkaSACCI18)} pKaSACCI18"

    fun formatPKmSACCI19(pkmSACCI19: Double): String = "${formatDecimal(pkmSACCI19)} pKmSACCI19"

    fun formatPKiSACCI19(pkiSACCI19: Double): String = "${formatDecimal(pkiSACCI19)} pKiSACCI19"

    fun formatPKdSACCI19(pkdSACCI19: Double): String = "${formatDecimal(pkdSACCI19)} pKdSACCI19"

    fun formatPKowSACCI19(pkowSACCI19: Double): String = "${formatDecimal(pkowSACCI19)} pKowSACCI19"

    fun formatPKspSACCI19(pkspSACCI19: Double): String = "${formatDecimal(pkspSACCI19)} pKspSACCI19"

    fun formatPKcSACCI19(pkcSACCI19: Double): String = "${formatDecimal(pkcSACCI19)} pKcSACCI19"

    fun formatPKwSACCI19(pkwSACCI19: Double): String = "${formatDecimal(pkwSACCI19)} pKwSACCI19"

    fun formatPKbSACCI19(pkbSACCI19: Double): String = "${formatDecimal(pkbSACCI19)} pKbSACCI19"

    fun formatPKaSACCI19(pkaSACCI19: Double): String = "${formatDecimal(pkaSACCI19)} pKaSACCI19"

    fun formatPKmSACCI20(pkmSACCI20: Double): String = "${formatDecimal(pkmSACCI20)} pKmSACCI20"

    fun formatPKiSACCI20(pkiSACCI20: Double): String = "${formatDecimal(pkiSACCI20)} pKiSACCI20"

    fun formatPKdSACCI20(pkdSACCI20: Double): String = "${formatDecimal(pkdSACCI20)} pKdSACCI20"

    fun formatPKowSACCI20(pkowSACCI20: Double): String = "${formatDecimal(pkowSACCI20)} pKowSACCI20"

    fun formatPKspSACCI20(pkspSACCI20: Double): String = "${formatDecimal(pkspSACCI20)} pKspSACCI20"

    fun formatPKcSACCI20(pkcSACCI20: Double): String = "${formatDecimal(pkcSACCI20)} pKcSACCI20"

    fun formatPKwSACCI20(pkwSACCI20: Double): String = "${formatDecimal(pkwSACCI20)} pKwSACCI20"

    fun formatPKbSACCI20(pkbSACCI20: Double): String = "${formatDecimal(pkbSACCI20)} pKbSACCI20"

    fun formatPKaSACCI20(pkaSACCI20: Double): String = "${formatDecimal(pkaSACCI20)} pKaSACCI20"

    fun formatPKmSACCI21(pkmSACCI21: Double): String = "${formatDecimal(pkmSACCI21)} pKmSACCI21"

    fun formatPKiSACCI21(pkiSACCI21: Double): String = "${formatDecimal(pkiSACCI21)} pKiSACCI21"

    fun formatPKdSACCI21(pkdSACCI21: Double): String = "${formatDecimal(pkdSACCI21)} pKdSACCI21"

    fun formatPKowSACCI21(pkowSACCI21: Double): String = "${formatDecimal(pkowSACCI21)} pKowSACCI21"

    fun formatPKspSACCI21(pkspSACCI21: Double): String = "${formatDecimal(pkspSACCI21)} pKspSACCI21"

    fun formatPKcSACCI21(pkcSACCI21: Double): String = "${formatDecimal(pkcSACCI21)} pKcSACCI21"

    fun formatPKwSACCI21(pkwSACCI21: Double): String = "${formatDecimal(pkwSACCI21)} pKwSACCI21"

    fun formatPKbSACCI21(pkbSACCI21: Double): String = "${formatDecimal(pkbSACCI21)} pKbSACCI21"

    fun formatPKaSACCI21(pkaSACCI21: Double): String = "${formatDecimal(pkaSACCI21)} pKaSACCI21"

    fun formatPKmSACCI22(pkmSACCI22: Double): String = "${formatDecimal(pkmSACCI22)} pKmSACCI22"

    fun formatPKiSACCI22(pkiSACCI22: Double): String = "${formatDecimal(pkiSACCI22)} pKiSACCI22"

    fun formatPKdSACCI22(pkdSACCI22: Double): String = "${formatDecimal(pkdSACCI22)} pKdSACCI22"

    fun formatPKowSACCI22(pkowSACCI22: Double): String = "${formatDecimal(pkowSACCI22)} pKowSACCI22"

    fun formatPKspSACCI22(pkspSACCI22: Double): String = "${formatDecimal(pkspSACCI22)} pKspSACCI22"

    fun formatPKcSACCI22(pkcSACCI22: Double): String = "${formatDecimal(pkcSACCI22)} pKcSACCI22"

    fun formatPKwSACCI22(pkwSACCI22: Double): String = "${formatDecimal(pkwSACCI22)} pKwSACCI22"

    fun formatPKbSACCI22(pkbSACCI22: Double): String = "${formatDecimal(pkbSACCI22)} pKbSACCI22"

    fun formatPKaSACCI22(pkaSACCI22: Double): String = "${formatDecimal(pkaSACCI22)} pKaSACCI22"

    fun formatPKmSACCI23(pkmSACCI23: Double): String = "${formatDecimal(pkmSACCI23)} pKmSACCI23"

    fun formatPKiSACCI23(pkiSACCI23: Double): String = "${formatDecimal(pkiSACCI23)} pKiSACCI23"

    fun formatPKdSACCI23(pkdSACCI23: Double): String = "${formatDecimal(pkdSACCI23)} pKdSACCI23"

    fun formatPKowSACCI23(pkowSACCI23: Double): String = "${formatDecimal(pkowSACCI23)} pKowSACCI23"

    fun formatPKspSACCI23(pkspSACCI23: Double): String = "${formatDecimal(pkspSACCI23)} pKspSACCI23"

    fun formatPKcSACCI23(pkcSACCI23: Double): String = "${formatDecimal(pkcSACCI23)} pKcSACCI23"

    fun formatPKwSACCI23(pkwSACCI23: Double): String = "${formatDecimal(pkwSACCI23)} pKwSACCI23"

    fun formatPKbSACCI23(pkbSACCI23: Double): String = "${formatDecimal(pkbSACCI23)} pKbSACCI23"

    fun formatPKaSACCI23(pkaSACCI23: Double): String = "${formatDecimal(pkaSACCI23)} pKaSACCI23"

    fun formatPKmSACCI24(pkmSACCI24: Double): String = "${formatDecimal(pkmSACCI24)} pKmSACCI24"

    fun formatPKiSACCI24(pkiSACCI24: Double): String = "${formatDecimal(pkiSACCI24)} pKiSACCI24"

    fun formatPKdSACCI24(pkdSACCI24: Double): String = "${formatDecimal(pkdSACCI24)} pKdSACCI24"

    fun formatPKowSACCI24(pkowSACCI24: Double): String = "${formatDecimal(pkowSACCI24)} pKowSACCI24"

    fun formatPKspSACCI24(pkspSACCI24: Double): String = "${formatDecimal(pkspSACCI24)} pKspSACCI24"

    fun formatPKcSACCI24(pkcSACCI24: Double): String = "${formatDecimal(pkcSACCI24)} pKcSACCI24"

    fun formatPKwSACCI24(pkwSACCI24: Double): String = "${formatDecimal(pkwSACCI24)} pKwSACCI24"

    fun formatPKbSACCI24(pkbSACCI24: Double): String = "${formatDecimal(pkbSACCI24)} pKbSACCI24"

    fun formatPKaSACCI24(pkaSACCI24: Double): String = "${formatDecimal(pkaSACCI24)} pKaSACCI24"

    fun formatPKmSACCI25(pkmSACCI25: Double): String = "${formatDecimal(pkmSACCI25)} pKmSACCI25"

    fun formatPKiSACCI25(pkiSACCI25: Double): String = "${formatDecimal(pkiSACCI25)} pKiSACCI25"

    fun formatPKdSACCI25(pkdSACCI25: Double): String = "${formatDecimal(pkdSACCI25)} pKdSACCI25"

    fun formatPKowSACCI25(pkowSACCI25: Double): String = "${formatDecimal(pkowSACCI25)} pKowSACCI25"

    fun formatPKspSACCI25(pkspSACCI25: Double): String = "${formatDecimal(pkspSACCI25)} pKspSACCI25"

    fun formatPKcSACCI25(pkcSACCI25: Double): String = "${formatDecimal(pkcSACCI25)} pKcSACCI25"

    fun formatPKwSACCI25(pkwSACCI25: Double): String = "${formatDecimal(pkwSACCI25)} pKwSACCI25"

    fun formatPKbSACCI25(pkbSACCI25: Double): String = "${formatDecimal(pkbSACCI25)} pKbSACCI25"

    fun formatPKaSACCI25(pkaSACCI25: Double): String = "${formatDecimal(pkaSACCI25)} pKaSACCI25"

    fun formatPKmSACCI26(pkmSACCI26: Double): String = "${formatDecimal(pkmSACCI26)} pKmSACCI26"

    fun formatPKiSACCI26(pkiSACCI26: Double): String = "${formatDecimal(pkiSACCI26)} pKiSACCI26"

    fun formatPKdSACCI26(pkdSACCI26: Double): String = "${formatDecimal(pkdSACCI26)} pKdSACCI26"

    fun formatPKowSACCI26(pkowSACCI26: Double): String = "${formatDecimal(pkowSACCI26)} pKowSACCI26"

    fun formatPKspSACCI26(pkspSACCI26: Double): String = "${formatDecimal(pkspSACCI26)} pKspSACCI26"

    fun formatPKcSACCI26(pkcSACCI26: Double): String = "${formatDecimal(pkcSACCI26)} pKcSACCI26"

    fun formatPKwSACCI26(pkwSACCI26: Double): String = "${formatDecimal(pkwSACCI26)} pKwSACCI26"

    fun formatPKbSACCI26(pkbSACCI26: Double): String = "${formatDecimal(pkbSACCI26)} pKbSACCI26"

    fun formatPKaSACCI26(pkaSACCI26: Double): String = "${formatDecimal(pkaSACCI26)} pKaSACCI26"

    fun formatPKmSACCI27(pkmSACCI27: Double): String = "${formatDecimal(pkmSACCI27)} pKmSACCI27"

    fun formatPKiSACCI27(pkiSACCI27: Double): String = "${formatDecimal(pkiSACCI27)} pKiSACCI27"

    fun formatPKdSACCI27(pkdSACCI27: Double): String = "${formatDecimal(pkdSACCI27)} pKdSACCI27"

    fun formatPKowSACCI27(pkowSACCI27: Double): String = "${formatDecimal(pkowSACCI27)} pKowSACCI27"

    fun formatPKspSACCI27(pkspSACCI27: Double): String = "${formatDecimal(pkspSACCI27)} pKspSACCI27"

    fun formatPKcSACCI27(pkcSACCI27: Double): String = "${formatDecimal(pkcSACCI27)} pKcSACCI27"

    fun formatPKwSACCI27(pkwSACCI27: Double): String = "${formatDecimal(pkwSACCI27)} pKwSACCI27"

    fun formatPKbSACCI27(pkbSACCI27: Double): String = "${formatDecimal(pkbSACCI27)} pKbSACCI27"

    fun formatPKaSACCI27(pkaSACCI27: Double): String = "${formatDecimal(pkaSACCI27)} pKaSACCI27"

    fun formatPKmSACCI28(pkmSACCI28: Double): String = "${formatDecimal(pkmSACCI28)} pKmSACCI28"

    fun formatPKiSACCI28(pkiSACCI28: Double): String = "${formatDecimal(pkiSACCI28)} pKiSACCI28"

    fun formatPKdSACCI28(pkdSACCI28: Double): String = "${formatDecimal(pkdSACCI28)} pKdSACCI28"

    fun formatPKowSACCI28(pkowSACCI28: Double): String = "${formatDecimal(pkowSACCI28)} pKowSACCI28"

    fun formatPKspSACCI28(pkspSACCI28: Double): String = "${formatDecimal(pkspSACCI28)} pKspSACCI28"

    fun formatPKcSACCI28(pkcSACCI28: Double): String = "${formatDecimal(pkcSACCI28)} pKcSACCI28"

    fun formatPKwSACCI28(pkwSACCI28: Double): String = "${formatDecimal(pkwSACCI28)} pKwSACCI28"

    fun formatPKbSACCI28(pkbSACCI28: Double): String = "${formatDecimal(pkbSACCI28)} pKbSACCI28"

    fun formatPKaSACCI28(pkaSACCI28: Double): String = "${formatDecimal(pkaSACCI28)} pKaSACCI28"

    fun formatPKmSACCI29(pkmSACCI29: Double): String = "${formatDecimal(pkmSACCI29)} pKmSACCI29"

    fun formatPKiSACCI29(pkiSACCI29: Double): String = "${formatDecimal(pkiSACCI29)} pKiSACCI29"

    fun formatPKdSACCI29(pkdSACCI29: Double): String = "${formatDecimal(pkdSACCI29)} pKdSACCI29"

    fun formatPKowSACCI29(pkowSACCI29: Double): String = "${formatDecimal(pkowSACCI29)} pKowSACCI29"

    fun formatPKspSACCI29(pkspSACCI29: Double): String = "${formatDecimal(pkspSACCI29)} pKspSACCI29"

    fun formatPKcSACCI29(pkcSACCI29: Double): String = "${formatDecimal(pkcSACCI29)} pKcSACCI29"

    fun formatPKwSACCI29(pkwSACCI29: Double): String = "${formatDecimal(pkwSACCI29)} pKwSACCI29"

    fun formatPKbSACCI29(pkbSACCI29: Double): String = "${formatDecimal(pkbSACCI29)} pKbSACCI29"

    fun formatPKaSACCI29(pkaSACCI29: Double): String = "${formatDecimal(pkaSACCI29)} pKaSACCI29"

    fun formatPKmSACCI30(pkmSACCI30: Double): String = "${formatDecimal(pkmSACCI30)} pKmSACCI30"

    fun formatPKiSACCI30(pkiSACCI30: Double): String = "${formatDecimal(pkiSACCI30)} pKiSACCI30"

    fun formatPKdSACCI30(pkdSACCI30: Double): String = "${formatDecimal(pkdSACCI30)} pKdSACCI30"

    fun formatPKowSACCI30(pkowSACCI30: Double): String = "${formatDecimal(pkowSACCI30)} pKowSACCI30"

    fun formatPKspSACCI30(pkspSACCI30: Double): String = "${formatDecimal(pkspSACCI30)} pKspSACCI30"

    fun formatPKcSACCI30(pkcSACCI30: Double): String = "${formatDecimal(pkcSACCI30)} pKcSACCI30"

    fun formatPKwSACCI30(pkwSACCI30: Double): String = "${formatDecimal(pkwSACCI30)} pKwSACCI30"

    fun formatPKbSACCI30(pkbSACCI30: Double): String = "${formatDecimal(pkbSACCI30)} pKbSACCI30"

    fun formatPKaSACCI30(pkaSACCI30: Double): String = "${formatDecimal(pkaSACCI30)} pKaSACCI30"

    fun formatPKmSACCI31(pkmSACCI31: Double): String = "${formatDecimal(pkmSACCI31)} pKmSACCI31"

    fun formatPKiSACCI31(pkiSACCI31: Double): String = "${formatDecimal(pkiSACCI31)} pKiSACCI31"

    fun formatPKdSACCI31(pkdSACCI31: Double): String = "${formatDecimal(pkdSACCI31)} pKdSACCI31"

    fun formatPKowSACCI31(pkowSACCI31: Double): String = "${formatDecimal(pkowSACCI31)} pKowSACCI31"

    fun formatPKspSACCI31(pkspSACCI31: Double): String = "${formatDecimal(pkspSACCI31)} pKspSACCI31"

    fun formatPKcSACCI31(pkcSACCI31: Double): String = "${formatDecimal(pkcSACCI31)} pKcSACCI31"

    fun formatPKwSACCI31(pkwSACCI31: Double): String = "${formatDecimal(pkwSACCI31)} pKwSACCI31"

    fun formatPKbSACCI31(pkbSACCI31: Double): String = "${formatDecimal(pkbSACCI31)} pKbSACCI31"

    fun formatPKaSACCI31(pkaSACCI31: Double): String = "${formatDecimal(pkaSACCI31)} pKaSACCI31"

    fun formatPKmSACCI32(pkmSACCI32: Double): String = "${formatDecimal(pkmSACCI32)} pKmSACCI32"

    fun formatPKiSACCI32(pkiSACCI32: Double): String = "${formatDecimal(pkiSACCI32)} pKiSACCI32"

    fun formatPKdSACCI32(pkdSACCI32: Double): String = "${formatDecimal(pkdSACCI32)} pKdSACCI32"

    fun formatPKowSACCI32(pkowSACCI32: Double): String = "${formatDecimal(pkowSACCI32)} pKowSACCI32"

    fun formatPKspSACCI32(pkspSACCI32: Double): String = "${formatDecimal(pkspSACCI32)} pKspSACCI32"

    fun formatPKcSACCI32(pkcSACCI32: Double): String = "${formatDecimal(pkcSACCI32)} pKcSACCI32"

    fun formatPKwSACCI32(pkwSACCI32: Double): String = "${formatDecimal(pkwSACCI32)} pKwSACCI32"

    fun formatPKbSACCI32(pkbSACCI32: Double): String = "${formatDecimal(pkbSACCI32)} pKbSACCI32"

    fun formatPKaSACCI32(pkaSACCI32: Double): String = "${formatDecimal(pkaSACCI32)} pKaSACCI32"

    fun formatPKmSACCI33(pkmSACCI33: Double): String = "${formatDecimal(pkmSACCI33)} pKmSACCI33"

    fun formatPKiSACCI33(pkiSACCI33: Double): String = "${formatDecimal(pkiSACCI33)} pKiSACCI33"

    fun formatPKdSACCI33(pkdSACCI33: Double): String = "${formatDecimal(pkdSACCI33)} pKdSACCI33"

    fun formatPKowSACCI33(pkowSACCI33: Double): String = "${formatDecimal(pkowSACCI33)} pKowSACCI33"

    fun formatPKspSACCI33(pkspSACCI33: Double): String = "${formatDecimal(pkspSACCI33)} pKspSACCI33"

    fun formatPKcSACCI33(pkcSACCI33: Double): String = "${formatDecimal(pkcSACCI33)} pKcSACCI33"

    fun formatPKwSACCI33(pkwSACCI33: Double): String = "${formatDecimal(pkwSACCI33)} pKwSACCI33"

    fun formatPKbSACCI33(pkbSACCI33: Double): String = "${formatDecimal(pkbSACCI33)} pKbSACCI33"

    fun formatPKaSACCI33(pkaSACCI33: Double): String = "${formatDecimal(pkaSACCI33)} pKaSACCI33"

    fun formatPKmSACCI34(pkmSACCI34: Double): String = "${formatDecimal(pkmSACCI34)} pKmSACCI34"

    fun formatPKiSACCI34(pkiSACCI34: Double): String = "${formatDecimal(pkiSACCI34)} pKiSACCI34"

    fun formatPKdSACCI34(pkdSACCI34: Double): String = "${formatDecimal(pkdSACCI34)} pKdSACCI34"

    fun formatPKowSACCI34(pkowSACCI34: Double): String = "${formatDecimal(pkowSACCI34)} pKowSACCI34"

    fun formatPKspSACCI34(pkspSACCI34: Double): String = "${formatDecimal(pkspSACCI34)} pKspSACCI34"

    fun formatPKcSACCI34(pkcSACCI34: Double): String = "${formatDecimal(pkcSACCI34)} pKcSACCI34"

    fun formatPKwSACCI34(pkwSACCI34: Double): String = "${formatDecimal(pkwSACCI34)} pKwSACCI34"

    fun formatPKbSACCI34(pkbSACCI34: Double): String = "${formatDecimal(pkbSACCI34)} pKbSACCI34"

    fun formatPKaSACCI34(pkaSACCI34: Double): String = "${formatDecimal(pkaSACCI34)} pKaSACCI34"

    fun formatPKmSACCI35(pkmSACCI35: Double): String = "${formatDecimal(pkmSACCI35)} pKmSACCI35"

    fun formatPKiSACCI35(pkiSACCI35: Double): String = "${formatDecimal(pkiSACCI35)} pKiSACCI35"

    fun formatPKdSACCI35(pkdSACCI35: Double): String = "${formatDecimal(pkdSACCI35)} pKdSACCI35"

    fun formatPKowSACCI35(pkowSACCI35: Double): String = "${formatDecimal(pkowSACCI35)} pKowSACCI35"

    fun formatPKspSACCI35(pkspSACCI35: Double): String = "${formatDecimal(pkspSACCI35)} pKspSACCI35"

    fun formatPKcSACCI35(pkcSACCI35: Double): String = "${formatDecimal(pkcSACCI35)} pKcSACCI35"

    fun formatPKwSACCI35(pkwSACCI35: Double): String = "${formatDecimal(pkwSACCI35)} pKwSACCI35"

    fun formatPKbSACCI35(pkbSACCI35: Double): String = "${formatDecimal(pkbSACCI35)} pKbSACCI35"

    fun formatPKaSACCI35(pkaSACCI35: Double): String = "${formatDecimal(pkaSACCI35)} pKaSACCI35"

    fun formatPKmSACCI36(pkmSACCI36: Double): String = "${formatDecimal(pkmSACCI36)} pKmSACCI36"

    fun formatPKiSACCI36(pkiSACCI36: Double): String = "${formatDecimal(pkiSACCI36)} pKiSACCI36"

    fun formatPKdSACCI36(pkdSACCI36: Double): String = "${formatDecimal(pkdSACCI36)} pKdSACCI36"

    fun formatPKowSACCI36(pkowSACCI36: Double): String = "${formatDecimal(pkowSACCI36)} pKowSACCI36"

    fun formatPKspSACCI36(pkspSACCI36: Double): String = "${formatDecimal(pkspSACCI36)} pKspSACCI36"

    fun formatPKcSACCI36(pkcSACCI36: Double): String = "${formatDecimal(pkcSACCI36)} pKcSACCI36"

    fun formatPKwSACCI36(pkwSACCI36: Double): String = "${formatDecimal(pkwSACCI36)} pKwSACCI36"

    fun formatPKbSACCI36(pkbSACCI36: Double): String = "${formatDecimal(pkbSACCI36)} pKbSACCI36"