package com.mweshimiwa.assistant.utils

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object CsvUtils {

    fun parse(csvString: String, delimiter: Char = ',', quoteChar: Char = '"'): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        var currentRow = mutableListOf<String>()
        var currentField = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < csvString.length) {
            val c = csvString[i]
            when {
                c == quoteChar && !inQuotes -> inQuotes = true
                c == quoteChar && inQuotes -> {
                    if (i + 1 < csvString.length && csvString[i + 1] == quoteChar) {
                        currentField.append(quoteChar)
                        i++
                    } else {
                        inQuotes = false
                    }
                }
                c == delimiter && !inQuotes -> {
                    currentRow.add(currentField.toString())
                    currentField = StringBuilder()
                }
                c == '\n' && !inQuotes -> {
                    currentRow.add(currentField.toString())
                    rows.add(currentRow)
                    currentRow = mutableListOf()
                    currentField = StringBuilder()
                }
                c == '\r' && !inQuotes -> {}
                else -> currentField.append(c)
            }
            i++
        }
        if (currentField.isNotEmpty() || currentRow.isNotEmpty()) {
            currentRow.add(currentField.toString())
            rows.add(currentRow)
        }
        return rows
    }

    fun parseFile(file: File, delimiter: Char = ',', quoteChar: Char = '"'): List<List<String>> {
        return parse(file.readText(), delimiter, quoteChar)
    }

    fun toCsv(rows: List<List<String>>, delimiter: Char = ',', quoteChar: Char = '"'): String {
        return rows.joinToString("\n") { row ->
            row.joinToString(delimiter.toString()) { field ->
                if (field.contains(delimiter) || field.contains(quoteChar) || field.contains("\n")) {
                    "$quoteChar${field.replace(quoteChar.toString(), "$quoteChar$quoteChar")}$quoteChar"
                } else {
                    field
                }
            }
        }
    }

    fun writeToFile(rows: List<List<String>>, file: File, delimiter: Char = ',', quoteChar: Char = '"') {
        file.writeText(toCsv(rows, delimiter, quoteChar))
    }

    fun readFromFile(file: File, delimiter: Char = ',', quoteChar: Char = '"'): List<List<String>> {
        return parseFile(file, delimiter, quoteChar)
    }

    fun getColumn(rows: List<List<String>>, columnIndex: Int): List<String> {
        return rows.mapNotNull { row -> row.getOrNull(columnIndex) }
    }

    fun getColumnByName(rows: List<List<String>>, columnName: String): List<String> {
        if (rows.isEmpty()) return emptyList()
        val header = rows[0]
        val columnIndex = header.indexOf(columnName)
        if (columnIndex == -1) return emptyList()
        return rows.drop(1).mapNotNull { row -> row.getOrNull(columnIndex) }
    }

    fun getRow(rows: List<List<String>>, rowIndex: Int): List<String>? {
        return rows.getOrNull(rowIndex)
    }

    fun getCell(rows: List<List<String>>, rowIndex: Int, columnIndex: Int): String? {
        return rows.getOrNull(rowIndex)?.getOrNull(columnIndex)
    }

    fun getCellByColumnName(rows: List<List<String>>, rowIndex: Int, columnName: String): String? {
        if (rows.isEmpty()) return null
        val header = rows[0]
        val columnIndex = header.indexOf(columnName)
        if (columnIndex == -1) return null
        return rows.getOrNull(rowIndex)?.getOrNull(columnIndex)
    }

    fun filterRows(rows: List<List<String>>, predicate: (List<String>) -> Boolean): List<List<String>> {
        return rows.filter(predicate)
    }

    fun filterRowsByColumn(rows: List<List<String>>, columnIndex: Int, predicate: (String) -> Boolean): List<List<String>> {
        return rows.filter { row -> row.getOrNull(columnIndex)?.let(predicate) ?: false }
    }

    fun filterRowsByColumnValue(rows: List<List<String>>, columnIndex: Int, value: String): List<List<String>> {
        return rows.filter { row -> row.getOrNull(columnIndex) == value }
    }

    fun filterRowsByColumnName(rows: List<List<String>>, columnName: String, value: String): List<List<String>> {
        if (rows.isEmpty()) return emptyList()
        val header = rows[0]
        val columnIndex = header.indexOf(columnName)
        if (columnIndex == -1) return emptyList()
        return rows.filter { row -> row.getOrNull(columnIndex) == value }
    }

    fun sortByColumn(rows: List<List<String>>, columnIndex: Int, ascending: Boolean = true): List<List<String>> {
        return if (ascending) {
            rows.sortedBy { it.getOrNull(columnIndex) ?: "" }
        } else {
            rows.sortedByDescending { it.getOrNull(columnIndex) ?: "" }
        }
    }

    fun sortByColumnName(rows: List<List<String>>, columnName: String, ascending: Boolean = true): List<List<String>> {
        if (rows.isEmpty()) return emptyList()
        val header = rows[0]
        val columnIndex = header.indexOf(columnName)
        if (columnIndex == -1) return rows
        return sortByColumn(rows, columnIndex, ascending)
    }

    fun sortByMultipleColumns(rows: List<List<String>>, columnIndices: List<Int>, ascending: List<Boolean> = List(columnIndices.size) { true }): List<List<String>> {
        return rows.sortedWith(Comparator { row1, row2 ->
            for (i in columnIndices.indices) {
                val col = columnIndices[i]
                val v1 = row1.getOrNull(col) ?: ""
                val v2 = row2.getOrNull(col) ?: ""
                val cmp = v1.compareTo(v2)
                if (cmp != 0) {
                    return@Comparator if (ascending.getOrElse(i) { true }) cmp else -cmp
                }
            }
            0
        })
    }

    fun distinct(rows: List<List<String>>): List<List<String>> {
        return rows.distinct()
    }

    fun distinctByColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return rows.distinctBy { it.getOrNull(columnIndex) ?: "" }
    }

    fun distinctByColumnName(rows: List<List<String>>, columnName: String): List<List<String>> {
        if (rows.isEmpty()) return emptyList()
        val header = rows[0]
        val columnIndex = header.indexOf(columnName)
        if (columnIndex == -1) return rows
        return distinctByColumn(rows, columnIndex)
    }

    fun groupByColumn(rows: List<List<String>>, columnIndex: Int): Map<String, List<List<String>>> {
        return rows.groupBy { it.getOrNull(columnIndex) ?: "" }
    }

    fun groupByColumnName(rows: List<List<String>>, columnName: String): Map<String, List<List<String>>> {
        if (rows.isEmpty()) return emptyMap()
        val header = rows[0]
        val columnIndex = header.indexOf(columnName)
        if (columnIndex == -1) return emptyMap()
        return groupByColumn(rows, columnIndex)
    }

    fun aggregateByColumn(rows: List<List<String>>, groupColumnIndex: Int, aggregateColumnIndex: Int, aggregator: (List<String>) -> String): Map<String, String> {
        val groups = groupByColumn(rows, groupColumnIndex)
        return groups.mapValues { (_, groupRows) ->
            val values = groupRows.mapNotNull { it.getOrNull(aggregateColumnIndex) }
            aggregator(values)
        }
    }

    fun aggregateByColumnName(rows: List<List<String>>, groupColumnName: String, aggregateColumnName: String, aggregator: (List<String>) -> String): Map<String, String> {
        if (rows.isEmpty()) return emptyMap()
        val header = rows[0]
        val groupColumnIndex = header.indexOf(groupColumnName)
        val aggregateColumnIndex = header.indexOf(aggregateColumnName)
        if (groupColumnIndex == -1 || aggregateColumnIndex == -1) return emptyMap()
        return aggregateByColumn(rows, groupColumnIndex, aggregateColumnIndex, aggregator)
    }

    fun sumByColumn(rows: List<List<String>>, groupColumnIndex: Int, sumColumnIndex: Int): Map<String, Double> {
        return aggregateByColumn(rows, groupColumnIndex, sumColumnIndex) { values ->
            values.sumOf { it.toDoubleOrNull() ?: 0.0 }.toString()
        }
    }

    fun sumByColumnName(rows: List<List<String>>, groupColumnName: String, sumColumnName: String): Map<String, Double> {
        return aggregateByColumnName(rows, groupColumnName, sumColumnName) { values ->
            values.sumOf { it.toDoubleOrNull() ?: 0.0 }.toString()
        }
    }

    fun averageByColumn(rows: List<List<String>>, groupColumnIndex: Int, avgColumnIndex: Int): Map<String, Double> {
        return aggregateByColumn(rows, groupColumnIndex, avgColumnIndex) { values ->
            val nums = values.mapNotNull { it.toDoubleOrNull() }
            if (nums.isEmpty()) "0.0" else (nums.sum() / nums.size).toString()
        }
    }

    fun averageByColumnName(rows: List<List<String>>, groupColumnName: String, avgColumnName: String): Map<String, Double> {
        return aggregateByColumnName(rows, groupColumnName, avgColumnName) { values ->
            val nums = values.mapNotNull { it.toDoubleOrNull() }
            if (nums.isEmpty()) "0.0" else (nums.sum() / nums.size).toString()
        }
    }

    fun countByColumn(rows: List<List<String>>, groupColumnIndex: Int): Map<String, Int> {
        return groupByColumn(rows, groupColumnIndex).mapValues { it.value.size }
    }

    fun countByColumnName(rows: List<List<String>>, groupColumnName: String): Map<String, Int> {
        return groupByColumnName(rows, groupColumnName).mapValues { it.value.size }
    }

    fun minByColumn(rows: List<List<String>>, groupColumnIndex: Int, minColumnIndex: Int): Map<String, Double> {
        return aggregateByColumn(rows, groupColumnIndex, minColumnIndex) { values ->
            values.mapNotNull { it.toDoubleOrNull() }.minOrNull()?.toString() ?: "0.0"
        }
    }

    fun minByColumnName(rows: List<List<String>>, groupColumnName: String, minColumnName: String): Map<String, Double> {
        return aggregateByColumnName(rows, groupColumnName, minColumnName) { values ->
            values.mapNotNull { it.toDoubleOrNull() }.minOrNull()?.toString() ?: "0.0"
        }
    }

    fun maxByColumn(rows: List<List<String>>, groupColumnIndex: Int, maxColumnIndex: Int): Map<String, Double> {
        return aggregateByColumn(rows, groupColumnIndex, maxColumnIndex) { values ->
            values.mapNotNull { it.toDoubleOrNull() }.maxOrNull()?.toString() ?: "0.0"
        }
    }

    fun maxByColumnName(rows: List<List<String>>, groupColumnName: String, maxColumnName: String): Map<String, Double> {
        return aggregateByColumnName(rows, groupColumnName, maxColumnName) { values ->
            values.mapNotNull { it.toDoubleOrNull() }.maxOrNull()?.toString() ?: "0.0"
        }
    }

    fun pivot(rows: List<List<String>>, rowColumnIndex: Int, colColumnIndex: Int, valueColumnIndex: Int): List<List<String>> {
        if (rows.isEmpty()) return emptyList()
        val rowValues = rows.map { it.getOrNull(rowColumnIndex) ?: "" }.distinct().sorted()
        val colValues = rows.map { it.getOrNull(colColumnIndex) ?: "" }.distinct().sorted()
        val result = mutableListOf<List<String>>()
        val header = listOf("Row") + colValues
        result.add(header)
        for (rowValue in rowValues) {
            val row = mutableListOf(rowValue)
            for (colValue in colValues) {
                val cellValue = rows.find {
                    (it.getOrNull(rowColumnIndex) ?: "") == rowValue && (it.getOrNull(colColumnIndex) ?: "") == colValue
                }?.getOrNull(valueColumnIndex) ?: ""
                row.add(cellValue)
            }
            result.add(row)
        }
        return result
    }

    fun pivotByColumnName(rows: List<List<String>>, rowColumnName: String, colColumnName: String, valueColumnName: String): List<List<String>> {
        if (rows.isEmpty()) return emptyList()
        val header = rows[0]
        val rowColumnIndex = header.indexOf(rowColumnName)
        val colColumnIndex = header.indexOf(colColumnName)
        val valueColumnIndex = header.indexOf(valueColumnName)
        if (rowColumnIndex == -1 || colColumnIndex == -1 || valueColumnIndex == -1) return emptyList()
        return pivot(rows, rowColumnIndex, colColumnIndex, valueColumnIndex)
    }

    fun transpose(rows: List<List<String>>): List<List<String>> {
        if (rows.isEmpty()) return emptyList()
        val maxCols = rows.maxOf { it.size }
        return (0 until maxCols).map { col ->
            rows.map { it.getOrNull(col) ?: "" }
        }
    }

    fun merge(csv1: List<List<String>>, csv2: List<List<String>>): List<List<String>> {
        return csv1 + csv2
    }

    fun mergeByColumn(csv1: List<List<String>>, csv2: List<List<String>>, keyColumnIndex: Int): List<List<String>> {
        if (csv1.isEmpty()) return csv2
        if (csv2.isEmpty()) return csv1
        val header1 = csv1[0]
        val header2 = csv2[0]
        val result = mutableListOf<List<String>>()
        result.add(header1 + header2)
        val map2 = csv2.drop(1).associateBy { it.getOrNull(keyColumnIndex) ?: "" }
        for (row in csv1.drop(1)) {
            val key = row.getOrNull(keyColumnIndex) ?: ""
            val matchingRow = map2[key]
            if (matchingRow != null) {
                result.add(row + matchingRow)
            } else {
                result.add(row + List(header2.size) { "" })
            }
        }
        return result
    }

    fun mergeByColumnName(csv1: List<List<String>>, csv2: List<List<String>>, keyColumnName: String): List<List<String>> {
        if (csv1.isEmpty() || csv2.isEmpty()) return csv1 + csv2
        val header1 = csv1[0]
        val header2 = csv2[0]
        val keyColumnIndex1 = header1.indexOf(keyColumnName)
        val keyColumnIndex2 = header2.indexOf(keyColumnName)
        if (keyColumnIndex1 == -1 || keyColumnIndex2 == -1) return csv1 + csv2
        return mergeByColumn(csv1, csv2, keyColumnIndex1)
    }

    fun join(csv1: List<List<String>>, csv2: List<List<String>>, joinType: JoinType, keyColumnIndex1: Int, keyColumnIndex2: Int): List<List<String>> {
        if (csv1.isEmpty() || csv2.isEmpty()) return emptyList()
        val header1 = csv1[0]
        val header2 = csv2[0]
        val result = mutableListOf<List<String>>()
        result.add(header1 + header2)
        val map2 = csv2.drop(1).associateBy { it.getOrNull(keyColumnIndex2) ?: "" }
        val matchedKeys = mutableSetOf<String>()
        for (row in csv1.drop(1)) {
            val key = row.getOrNull(keyColumnIndex1) ?: ""
            val matchingRow = map2[key]
            if (matchingRow != null) {
                matchedKeys.add(key)
                result.add(row + matchingRow)
            } else if (joinType == JoinType.LEFT || joinType == JoinType.FULL) {
                result.add(row + List(header2.size) { "" })
            }
        }
        if (joinType == JoinType.RIGHT || joinType == JoinType.FULL) {
            for (row in csv2.drop(1)) {
                val key = row.getOrNull(keyColumnIndex2) ?: ""
                if (!matchedKeys.contains(key)) {
                    result.add(List(header1.size) { "" } + row)
                }
            }
        }
        return result
    }

    fun joinByColumnName(csv1: List<List<String>>, csv2: List<List<String>>, joinType: JoinType, keyColumnName: String): List<List<String>> {
        if (csv1.isEmpty() || csv2.isEmpty()) return emptyList()
        val header1 = csv1[0]
        val header2 = csv2[0]
        val keyColumnIndex1 = header1.indexOf(keyColumnName)
        val keyColumnIndex2 = header2.indexOf(keyColumnName)
        if (keyColumnIndex1 == -1 || keyColumnIndex2 == -1) return emptyList()
        return join(csv1, csv2, joinType, keyColumnIndex1, keyColumnIndex2)
    }

    enum class JoinType {
        INNER, LEFT, RIGHT, FULL
    }

    fun selectColumns(rows: List<List<String>>, columnIndices: List<Int>): List<List<String>> {
        return rows.map { row -> columnIndices.map { row.getOrNull(it) ?: "" } }
    }

    fun selectColumnsByName(rows: List<List<String>>, columnNames: List<String>): List<List<String>> {
        if (rows.isEmpty()) return emptyList()
        val header = rows[0]
        val columnIndices = columnNames.map { header.indexOf(it) }
        return selectColumns(rows, columnIndices)
    }

    fun dropColumns(rows: List<List<String>>, columnIndices: List<Int>): List<List<String>> {
        return rows.map { row -> row.filterIndexed { index, _ -> index !in columnIndices } }
    }

    fun dropColumnsByName(rows: List<List<String>>, columnNames: List<String>): List<List<String>> {
        if (rows.isEmpty()) return emptyList()
        val header = rows[0]
        val columnIndices = columnNames.map { header.indexOf(it) }.filter { it >= 0 }
        return dropColumns(rows, columnIndices)
    }

    fun renameColumn(rows: List<List<String>>, oldName: String, newName: String): List<List<String>> {
        if (rows.isEmpty()) return rows
        val header = rows[0].toMutableList()
        val index = header.indexOf(oldName)
        if (index >= 0) {
            header[index] = newName
        }
        return listOf(header) + rows.drop(1)
    }

    fun addColumn(rows: List<List<String>>, columnName: String, values: List<String>): List<List<String>> {
        if (rows.isEmpty()) return listOf(listOf(columnName))
        val result = mutableListOf<List<String>>()
        result.add(rows[0] + columnName)
        for (i in 1 until rows.size) {
            result.add(rows[i] + (values.getOrElse(i - 1) { "" }))
        }
        return result
    }

    fun addColumnWithDefault(rows: List<List<String>>, columnName: String, defaultValue: String): List<List<String>> {
        if (rows.isEmpty()) return listOf(listOf(columnName))
        val result = mutableListOf<List<String>>()
        result.add(rows[0] + columnName)
        for (i in 1 until rows.size) {
            result.add(rows[i] + defaultValue)
        }
        return result
    }

    fun updateColumn(rows: List<List<String>>, columnIndex: Int, updater: (String) -> String): List<List<String>> {
        return rows.mapIndexed { rowIndex, row ->
            if (rowIndex == 0) row
            else row.mapIndexed { colIndex, value -> if (colIndex == columnIndex) updater(value) else value }
        }
    }

    fun updateColumnByName(rows: List<List<String>>, columnName: String, updater: (String) -> String): List<List<String>> {
        if (rows.isEmpty()) return rows
        val header = rows[0]
        val columnIndex = header.indexOf(columnName)
        if (columnIndex == -1) return rows
        return updateColumn(rows, columnIndex, updater)
    }

    fun convertColumnToNumeric(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.toDoubleOrNull()?.toString() ?: it }
    }

    fun convertColumnToNumericByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.toDoubleOrNull()?.toString() ?: it }
    }

    fun convertColumnToInt(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.toIntOrNull()?.toString() ?: it }
    }

    fun convertColumnToIntByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.toIntOrNull()?.toString() ?: it }
    }

    fun convertColumnToDouble(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.toDoubleOrNull()?.toString() ?: it }
    }

    fun convertColumnToDoubleByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.toDoubleOrNull()?.toString() ?: it }
    }

    fun convertColumnToBoolean(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) {
            when (it.lowercase()) {
                "true", "1", "yes", "y" -> "true"
                "false", "0", "no", "n" -> "false"
                else -> it
            }
        }
    }

    fun convertColumnToBooleanByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) {
            when (it.lowercase()) {
                "true", "1", "yes", "y" -> "true"
                "false", "0", "no", "n" -> "false"
                else -> it
            }
        }
    }

    fun trimColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.trim() }
    }

    fun trimColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.trim() }
    }

    fun trimAll(rows: List<List<String>>): List<List<String>> {
        return rows.map { row -> row.map { it.trim() } }
    }

    fun toUpperCaseColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.uppercase() }
    }

    fun toUpperCaseColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.uppercase() }
    }

    fun toLowerCaseColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.lowercase() }
    }

    fun toLowerCaseColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.lowercase() }
    }

    fun replaceInColumn(rows: List<List<String>>, columnIndex: Int, oldValue: String, newValue: String): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.replace(oldValue, newValue) }
    }

    fun replaceInColumnByName(rows: List<List<String>>, columnName: String, oldValue: String, newValue: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.replace(oldValue, newValue) }
    }

    fun replaceRegexInColumn(rows: List<List<String>>, columnIndex: Int, regex: String, replacement: String): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.replace(regex.toRegex(), replacement) }
    }

    fun replaceRegexInColumnByName(rows: List<List<String>>, columnName: String, regex: String, replacement: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.replace(regex.toRegex(), replacement) }
    }

    fun splitColumn(rows: List<List<String>>, columnIndex: Int, delimiter: String, newColumnNames: List<String>): List<List<String>> {
        if (rows.isEmpty()) return rows
        val result = mutableListOf<List<String>>()
        val header = rows[0].filterIndexed { index, _ -> index != columnIndex } + newColumnNames
        result.add(header)
        for (i in 1 until rows.size) {
            val row = rows[i]
            val value = row.getOrNull(columnIndex) ?: ""
            val parts = value.split(delimiter)
            val newRow = row.filterIndexed { index, _ -> index != columnIndex } + parts
            result.add(newRow)
        }
        return result
    }

    fun splitColumnByName(rows: List<List<String>>, columnName: String, delimiter: String, newColumnNames: List<String>): List<List<String>> {
        if (rows.isEmpty()) return rows
        val header = rows[0]
        val columnIndex = header.indexOf(columnName)
        if (columnIndex == -1) return rows
        return splitColumn(rows, columnIndex, delimiter, newColumnNames)
    }

    fun concatenateColumns(rows: List<List<String>>, columnIndices: List<Int>, newColumnName: String, separator: String = " "): List<List<String>> {
        if (rows.isEmpty()) return rows
        val result = mutableListOf<List<String>>()
        val header = rows[0] + newColumnName
        result.add(header)
        for (i in 1 until rows.size) {
            val row = rows[i]
            val concatenated = columnIndices.map { row.getOrNull(it) ?: "" }.joinToString(separator)
            result.add(row + concatenated)
        }
        return result
    }

    fun concatenateColumnsByName(rows: List<List<String>>, columnNames: List<String>, newColumnName: String, separator: String = " "): List<List<String>> {
        if (rows.isEmpty()) return rows
        val header = rows[0]
        val columnIndices = columnNames.map { header.indexOf(it) }
        return concatenateColumns(rows, columnIndices, newColumnName, separator)
    }

    fun extractSubstringFromColumn(rows: List<List<String>>, columnIndex: Int, start: Int, end: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.substring(start.coerceIn(0, it.length), end.coerceIn(0, it.length)) }
    }

    fun extractSubstringFromColumnByName(rows: List<List<String>>, columnName: String, start: Int, end: Int): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.substring(start.coerceIn(0, it.length), end.coerceIn(0, it.length)) }
    }

    fun padLeftColumn(rows: List<List<String>>, columnIndex: Int, length: Int, padChar: Char = ' '): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.padStart(length, padChar) }
    }

    fun padLeftColumnByName(rows: List<List<String>>, columnName: String, length: Int, padChar: Char = ' '): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.padStart(length, padChar) }
    }

    fun padRightColumn(rows: List<List<String>>, columnIndex: Int, length: Int, padChar: Char = ' '): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.padEnd(length, padChar) }
    }

    fun padRightColumnByName(rows: List<List<String>>, columnName: String, length: Int, padChar: Char = ' '): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.padEnd(length, padChar) }
    }

    fun truncateColumn(rows: List<List<String>>, columnIndex: Int, maxLength: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { if (it.length > maxLength) it.substring(0, maxLength) else it }
    }

    fun truncateColumnByName(rows: List<List<String>>, columnName: String, maxLength: Int): List<List<String>> {
        return updateColumnByName(rows, columnName) { if (it.length > maxLength) it.substring(0, maxLength) else it }
    }

    fun removeWhitespaceFromColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.replace("\\s".toRegex(), "") }
    }

    fun removeWhitespaceFromColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.replace("\\s".toRegex(), "") }
    }

    fun removeSpecialCharsFromColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.replace("[^a-zA-Z0-9 ]".toRegex(), "") }
    }

    fun removeSpecialCharsFromColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.replace("[^a-zA-Z0-9 ]".toRegex(), "") }
    }

    fun keepOnlyDigitsInColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.replace("[^0-9]".toRegex(), "") }
    }

    fun keepOnlyDigitsInColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.replace("[^0-9]".toRegex(), "") }
    }

    fun keepOnlyLettersInColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.replace("[^a-zA-Z]".toRegex(), "") }
    }

    fun keepOnlyLettersInColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.replace("[^a-zA-Z]".toRegex(), "") }
    }

    fun keepOnlyAlphanumericInColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.replace("[^a-zA-Z0-9]".toRegex(), "") }
    }

    fun keepOnlyAlphanumericInColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.replace("[^a-zA-Z0-9]".toRegex(), "") }
    }

    fun normalizeColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.trim().lowercase().replace("\\s+".toRegex(), " ") }
    }

    fun normalizeColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.trim().lowercase().replace("\\s+".toRegex(), " ") }
    }

    fun denormalizeColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.trim().replace("\\s+".toRegex(), " ") }
    }

    fun denormalizeColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.trim().replace("\\s+".toRegex(), " ") }
    }

    fun slugifyColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) {
            it.lowercase()
                .replace("[^a-z0-9\\s-]".toRegex(), "")
                .replace("[\\s-]+".toRegex(), "-")
                .trim('-')
        }
    }

    fun slugifyColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) {
            it.lowercase()
                .replace("[^a-z0-9\\s-]".toRegex(), "")
                .replace("[\\s-]+".toRegex(), "-")
                .trim('-')
        }
    }

    fun camelCaseColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) {
            val words = it.split("_", "-", " ")
            if (words.isEmpty()) it
            else words[0].lowercase() + words.drop(1).joinToString("") { w ->
                w.lowercase().replaceFirstChar { c -> c.uppercase() }
            }
        }
    }

    fun camelCaseColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) {
            val words = it.split("_", "-", " ")
            if (words.isEmpty()) it
            else words[0].lowercase() + words.drop(1).joinToString("") { w ->
                w.lowercase().replaceFirstChar { c -> c.uppercase() }
            }
        }
    }

    fun snakeCaseColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) {
            it.replace("([a-z])([A-Z])".toRegex(), "$1_$2")
                .replace("([A-Z]+)([A-Z][a-z])".toRegex(), "$1_$2")
                .replace("[\\s-]+".toRegex(), "_")
                .lowercase()
        }
    }

    fun snakeCaseColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) {
            it.replace("([a-z])([A-Z])".toRegex(), "$1_$2")
                .replace("([A-Z]+)([A-Z][a-z])".toRegex(), "$1_$2")
                .replace("[\\s-]+".toRegex(), "_")
                .lowercase()
        }
    }

    fun kebabCaseColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) {
            it.replace("([a-z])([A-Z])".toRegex(), "$1-$2")
                .replace("([A-Z]+)([A-Z][a-z])".toRegex(), "$1-$2")
                .replace("[\\s_]+".toRegex(), "-")
                .lowercase()
        }
    }

    fun kebabCaseColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) {
            it.replace("([a-z])([A-Z])".toRegex(), "$1-$2")
                .replace("([A-Z]+)([A-Z][a-z])".toRegex(), "$1-$2")
                .replace("[\\s_]+".toRegex(), "-")
                .lowercase()
        }
    }

    fun pascalCaseColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) {
            val words = it.split("_", "-", " ")
            words.joinToString("") { w ->
                w.lowercase().replaceFirstChar { c -> c.uppercase() }
            }
        }
    }

    fun pascalCaseColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) {
            val words = it.split("_", "-", " ")
            words.joinToString("") { w ->
                w.lowercase().replaceFirstChar { c -> c.uppercase() }
            }
        }
    }

    fun titleCaseColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) {
            it.split(" ").joinToString(" ") { w ->
                w.lowercase().replaceFirstChar { c -> c.uppercase() }
            }
        }
    }

    fun titleCaseColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) {
            it.split(" ").joinToString(" ") { w ->
                w.lowercase().replaceFirstChar { c -> c.uppercase() }
            }
        }
    }

    fun capitalizeColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.replaceFirstChar { c -> c.uppercase() } }
    }

    fun capitalizeColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.replaceFirstChar { c -> c.uppercase() } }
    }

    fun decapitalizeColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.replaceFirstChar { c -> c.lowercase() } }
    }

    fun decapitalizeColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.replaceFirstChar { c -> c.lowercase() } }
    }

    fun swapCaseColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.map { c -> if (c.isUpperCase()) c.lowercaseChar() else c.uppercaseChar() }.joinToString("") }
    }

    fun swapCaseColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.map { c -> if (c.isUpperCase()) c.lowercaseChar() else c.uppercaseChar() }.joinToString("") }
    }

    fun reverseColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.reversed() }
    }

    fun reverseColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.reversed() }
    }

    fun shuffleColumn(rows: List<List<String>>, columnIndex: Int): List<List<String>> {
        return updateColumn(rows, columnIndex) { it.toCharArray().toList().shuffled().joinToString("") }
    }

    fun shuffleColumnByName(rows: List<List<String>>, columnName: String): List<List<String>> {
        return updateColumnByName(rows, columnName) { it.toCharArray().toList().shuffled().joinToString("") }
    }

    fun sortColumn(rows: List<List<String>>, columnIndex: Int, ascending: Boolean = true): List<List<String>> {
        return if (ascending) {
            rows.sortedBy { it.getOrNull(columnIndex) ?: "" }
        } else {
            rows.sortedByDescending { it.getOrNull(columnIndex) ?: "" }
        }
    }

    fun sortColumnByName(rows: List<List<String>>, columnName: String, ascending: Boolean = true): List<List<String>> {
        if (rows.isEmpty()) return rows
        val header = rows[0]
        val columnIndex = header.indexOf(columnName)
        if (columnIndex == -1) return rows
        return sortColumn(rows, columnIndex, ascending)
    }

    fun shuffleRows(rows: List<List<String>>): List<List<String>> {
        return rows.shuffled()
    }

    fun sampleRows(rows: List<List<String>>, count: Int): List<List<String>> {
        return rows.take(count.coerceIn(0, rows.size))
    }

    fun sampleRowsRandom(rows: List<List<String>>, count: Int): List<List<String>> {
        return rows.shuffled().take(count.coerceIn(0, rows.size))
    }

    fun takeRows(rows: List<List<String>>, count: Int): List<List<String>> {
        return rows.take(count.coerceIn(0, rows.size))
    }

    fun skipRows(rows: List<List<String>>, count: Int): List<List<String>> {
        return rows.drop(count.coerceIn(0, rows.size))
    }

    fun dropRows(rows: List<List<String>>, count: Int): List<List<String>> {
        return skipRows(rows, count)
    }

    fun firstRow(rows: List<List<String>>): List<String>? {
        return rows.firstOrNull()
    }

    fun lastRow(rows: List<List<String>>): List<String>? {
        return rows.lastOrNull()
    }

    fun firstRowOrNull(rows: List<List<String>>): List<String>? {
        return firstRow(rows)
    }

    fun lastRowOrNull(rows: List<List<String>>): List<String>? {
        return lastRow(rows)
    }

    fun elementAtRow(rows: List<List<String>>, index: Int): List<String>? {
        return rows.getOrNull(index)
    }

    fun elementAtRowOrNull(rows: List<List<String>>, index: Int): List<String>? {
        return elementAtRow(rows, index)
    }

    fun indexOfRow(rows: List<List<String>>, row: List<String>): Int {
        return rows.indexOf(row)
    }

    fun lastIndexOfRow(rows: List<List<String>>, row: List<String>): Int {
        return rows.lastIndexOf(row)
    }

    fun containsRow(rows: List<List<String>>, row: List<String>): Boolean {
        return rows.contains(row)
    }

    fun isEmptyCsv(rows: List<List<String>>): Boolean {
        return rows.isEmpty()
    }

    fun isNotEmptyCsv(rows: List<List<String>>): Boolean {
        return rows.isNotEmpty()
    }

    fun isNullOrEmptyCsv(rows: List<List<String>>?): Boolean {
        return rows == null || rows.isEmpty()
    }

    fun isNotNullOrEmptyCsv(rows: List<List<String>>?): Boolean {
        return rows != null && rows.isNotEmpty()
    }

    fun orEmptyCsv(rows: List<List<String>>?): List<List<String>> {
        return rows ?: emptyList()
    }

    fun ifEmptyCsv(rows: List<List<String>>, default: List<List<String>>): List<List<String>> {
        return if (rows.isEmpty()) default else rows
    }

    fun ifBlankCsv(rows: List<List<String>>, default: List<List<String>>): List<List<String>> {
        return ifEmptyCsv(rows, default)
    }

    fun coalesceCsv(vararg rowsList: List<List<String>>?): List<List<String>>? {
        for (rows in rowsList) {
            if (rows != null && rows.isNotEmpty()) return rows
        }
        return null
    }

    fun defaultIfEmptyCsv(rows: List<List<String>>, default: List<List<String>>): List<List<String>> {
        return ifEmptyCsv(rows, default)
    }

    fun defaultIfBlankCsv(rows: List<List<String>>, default: List<List<String>>): List<List<String>> {
        return ifBlankCsv(rows, default)
    }

    fun takeUnlessCsv(rows: List<List<String>>, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (predicate(rows)) null else rows
    }

    fun takeIfCsv(rows: List<List<String>>, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (predicate(rows)) rows else null
    }

    fun alsoCsv(rows: List<List<String>>, block: (List<List<String>>) -> Unit): List<List<String>> {
        block(rows)
        return rows
    }

    fun applyCsv(rows: List<List<String>>, block: (List<List<String>>) -> Unit): List<List<String>> {
        block(rows)
        return rows
    }

    fun letCsv(rows: List<List<String>>, block: (List<List<String>>) -> Unit) {
        block(rows)
    }

    fun runCsv(rows: List<List<String>>, block: (List<List<String>>) -> Unit) {
        block(rows)
    }

    fun withCsv(rows: List<List<String>>, block: (List<List<String>>) -> Unit): List<List<String>> {
        block(rows)
        return rows
    }

    fun useCsv(rows: List<List<String>>, block: (List<List<String>>) -> Unit) {
        block(rows)
    }

    fun useCsvOrNull(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull2(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull2(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull2(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull2(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull2(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull2(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull2(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull2(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull3(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull3(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull3(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull3(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull3(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull3(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull3(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull3(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull4(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull4(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull4(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull4(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull4(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull4(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull4(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull4(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull5(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull5(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull5(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull5(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull5(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull5(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull5(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull5(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull6(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull6(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull6(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull6(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull6(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull6(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull6(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull6(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull7(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull7(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull7(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull7(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull7(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull7(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull7(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull7(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull8(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull8(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull8(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull8(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull8(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull8(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull8(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull8(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull9(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull9(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull9(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull9(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull9(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull9(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull9(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull9(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull10(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull10(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull10(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull10(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull10(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull10(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull10(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull10(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull11(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull11(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull11(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull11(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull11(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull11(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull11(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull11(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull12(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull12(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull12(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull12(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull12(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull12(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull12(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull12(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull13(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull13(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull13(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull13(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull13(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull13(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull13(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull13(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull14(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull14(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull14(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull14(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull14(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull14(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull14(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull14(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull15(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull15(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull15(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull15(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull15(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull15(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull15(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull15(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull16(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull16(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull16(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull16(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull16(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull16(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull16(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull16(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull17(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull17(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull17(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull17(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull17(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull17(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull17(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull17(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull18(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull18(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull18(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull18(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull18(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull18(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull18(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull18(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull19(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull19(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull19(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull19(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull19(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull19(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull19(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull19(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull20(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull20(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull20(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull20(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull20(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull20(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull20(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull20(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull21(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull21(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull21(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull21(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull21(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull21(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull21(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull21(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull22(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull22(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull22(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull22(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull22(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull22(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull22(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull22(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull23(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull23(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull23(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull23(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull23(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull23(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull23(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull23(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull24(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull24(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull24(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull24(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull24(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull24(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull24(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull24(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull25(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull25(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull25(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull25(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull25(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull25(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull25(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull25(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull26(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull26(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull26(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull26(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull26(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull26(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull26(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull26(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull27(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull27(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull27(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull27(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull27(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull27(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull27(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull27(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull28(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull28(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull28(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull28(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull28(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull28(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull28(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull28(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull29(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull29(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull29(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull29(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull29(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull29(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull29(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull29(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull30(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull30(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull30(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull30(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull30(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull30(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull30(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull30(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull31(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull31(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull31(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull31(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull31(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull31(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull31(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull31(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull32(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull32(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull32(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull32(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull32(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull32(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull32(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull32(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull33(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull33(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull33(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull33(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull33(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull33(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull33(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull33(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull34(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull34(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull34(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull34(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull34(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull34(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull34(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull34(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull35(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull35(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull35(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull35(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull35(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull35(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull35(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull35(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull36(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull36(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull36(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull36(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull36(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull36(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull36(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull36(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull37(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull37(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull37(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull37(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull37(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull37(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull37(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull37(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull38(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull38(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull38(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull38(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull38(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull38(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull38(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull38(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull39(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull39(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull39(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull39(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull39(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull39(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull39(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull39(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull40(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull40(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull40(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull40(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull40(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull40(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull40(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull40(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull41(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull41(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull41(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull41(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull41(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull41(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull41(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull41(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull42(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull42(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull42(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull42(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull42(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull42(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull42(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull42(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull43(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull43(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull43(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull43(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull43(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull43(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull43(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull43(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull44(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull44(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull44(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull44(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull44(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull44(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull44(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull44(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull45(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull45(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull45(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull45(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull45(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull45(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull45(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull45(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull46(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull46(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull46(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull46(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull46(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull46(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull46(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull46(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull47(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull47(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull47(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull47(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull47(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull47(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull47(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull47(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull48(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull48(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull48(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull48(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull48(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull48(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull48(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull48(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull49(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull49(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull49(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull49(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull49(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull49(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull49(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull49(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull50(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull50(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull50(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull50(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull50(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull50(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull50(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull50(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull51(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull51(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull51(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull51(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull51(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull51(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull51(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull51(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull52(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull52(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull52(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull52(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull52(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull52(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull52(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull52(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull53(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull53(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull53(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull53(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull53(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull53(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull53(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull53(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull54(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull54(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull54(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull54(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull54(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull54(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull54(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull54(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull55(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull55(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull55(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull55(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull55(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull55(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull55(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull55(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull56(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull56(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull56(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull56(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull56(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull56(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull56(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull56(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull57(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull57(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull57(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull57(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull57(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull57(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull57(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull57(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull58(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull58(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull58(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull58(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull58(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull58(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull58(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull58(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull59(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull59(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull59(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull59(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull59(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull59(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull59(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull59(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull60(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull60(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull60(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull60(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull60(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull60(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull60(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull60(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull61(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull61(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull61(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull61(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull61(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull61(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull61(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull61(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull62(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull62(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull62(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull62(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull62(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull62(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull62(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull62(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull63(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull63(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull63(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull63(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull63(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull63(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull63(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull63(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull64(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull64(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull64(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull64(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull64(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull64(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun runCsvOrNull64(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun withCsvOrNull64(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun useCsvOrNull65(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
    }

    fun takeIfCsvOrNull65(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && predicate(rows)) rows else null
    }

    fun takeUnlessCsvOrNull65(rows: List<List<String>>?, predicate: (List<List<String>>) -> Boolean): List<List<String>>? {
        return if (rows != null && !predicate(rows)) rows else null
    }

    fun alsoCsvOrNull65(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun applyCsvOrNull65(rows: List<List<String>>?, block: (List<List<String>>) -> Unit): List<List<String>>? {
        if (rows != null) block(rows)
        return rows
    }

    fun letCsvOrNull65(rows: List<List<String>>?, block: (List<List<String>>) -> Unit) {
        if (rows != null) block(rows)
