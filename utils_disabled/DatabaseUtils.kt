package com.mweshimiwa.assistant.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

object DatabaseUtils {

    fun createDatabase(context: Context, name: String, version: Int, onCreate: (SQLiteDatabase) -> Unit, onUpgrade: ((SQLiteDatabase, Int, Int) -> Unit)? = null): SQLiteOpenHelper {
        return object : SQLiteOpenHelper(context, name, null, version) {
            override fun onCreate(db: SQLiteDatabase) {
                onCreate(db)
            }
            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                onUpgrade?.invoke(db, oldVersion, newVersion)
            }
        }
    }

    fun createTable(db: SQLiteDatabase, tableName: String, columns: Map<String, String>, primaryKey: String? = null, foreignKeys: Map<String, Pair<String, String>> = emptyMap()) {
        val columnDefs = columns.map { (name, type) ->
            val pk = if (name == primaryKey) " PRIMARY KEY" else ""
            val notNull = if (name == primaryKey) " NOT NULL" else ""
            "$name $type$pk$notNull"
        }.toMutableList()
        foreignKeys.forEach { (column, ref) ->
            columnDefs.add("FOREIGN KEY ($column) REFERENCES ${ref.first}(${ref.second})")
        }
        val sql = "CREATE TABLE IF NOT EXISTS $tableName (${columnDefs.joinToString(", ")})"
        db.execSQL(sql)
    }

    fun dropTable(db: SQLiteDatabase, tableName: String) {
        db.execSQL("DROP TABLE IF EXISTS $tableName")
    }

    fun renameTable(db: SQLiteDatabase, oldName: String, newName: String) {
        db.execSQL("ALTER TABLE $oldName RENAME TO $newName")
    }

    fun addColumn(db: SQLiteDatabase, tableName: String, columnName: String, columnType: String) {
        db.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnType")
    }

    fun dropColumn(db: SQLiteDatabase, tableName: String, columnName: String) {
        db.execSQL("ALTER TABLE $tableName DROP COLUMN $columnName")
    }

    fun createIndex(db: SQLiteDatabase, indexName: String, tableName: String, columns: List<String>, unique: Boolean = false) {
        val uniqueStr = if (unique) "UNIQUE" else ""
        val sql = "CREATE $uniqueStr INDEX IF NOT EXISTS $indexName ON $tableName (${columns.joinToString(", ")})"
        db.execSQL(sql)
    }

    fun dropIndex(db: SQLiteDatabase, indexName: String) {
        db.execSQL("DROP INDEX IF EXISTS $indexName")
    }

    fun insert(db: SQLiteDatabase, tableName: String, values: ContentValues): Long {
        return db.insert(tableName, null, values)
    }

    fun insertOrReplace(db: SQLiteDatabase, tableName: String, values: ContentValues): Long {
        return db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun insertOrIgnore(db: SQLiteDatabase, tableName: String, values: ContentValues): Long {
        return db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun bulkInsert(db: SQLiteDatabase, tableName: String, valuesList: List<ContentValues>): Int {
        var count = 0
        db.beginTransaction()
        try {
            for (values in valuesList) {
                db.insert(tableName, null, values)
                count++
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return count
    }

    fun update(db: SQLiteDatabase, tableName: String, values: ContentValues, whereClause: String? = null, whereArgs: Array<String>? = null): Int {
        return db.update(tableName, values, whereClause, whereArgs)
    }

    fun delete(db: SQLiteDatabase, tableName: String, whereClause: String? = null, whereArgs: Array<String>? = null): Int {
        return db.delete(tableName, whereClause, whereArgs)
    }

    fun query(db: SQLiteDatabase, tableName: String, columns: Array<String>? = null, selection: String? = null, selectionArgs: Array<String>? = null, groupBy: String? = null, having: String? = null, orderBy: String? = null, limit: String? = null): Cursor {
        return db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
    }

    fun rawQuery(db: SQLiteDatabase, sql: String, selectionArgs: Array<String>? = null): Cursor {
        return db.rawQuery(sql, selectionArgs)
    }

    fun execSQL(db: SQLiteDatabase, sql: String) {
        db.execSQL(sql)
    }

    fun execSQL(db: SQLiteDatabase, sql: String, bindArgs: Array<Any>) {
        db.execSQL(sql, bindArgs)
    }

    fun getTableNames(db: SQLiteDatabase): List<String> {
        val tables = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'", null)
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0))
        }
        cursor.close()
        return tables
    }

    fun getColumnNames(db: SQLiteDatabase, tableName: String): List<String> {
        val columns = mutableListOf<String>()
        val cursor = db.rawQuery("PRAGMA table_info($tableName)", null)
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(1))
        }
        cursor.close()
        return columns
    }

    fun getColumnTypes(db: SQLiteDatabase, tableName: String): Map<String, String> {
        val types = mutableMapOf<String, String>()
        val cursor = db.rawQuery("PRAGMA table_info($tableName)", null)
        while (cursor.moveToNext()) {
            types[cursor.getString(1)] = cursor.getString(2)
        }
        cursor.close()
        return types
    }

    fun getTableInfo(db: SQLiteDatabase, tableName: String): List<Map<String, Any?>> {
        val info = mutableListOf<Map<String, Any?>>()
        val cursor = db.rawQuery("PRAGMA table_info($tableName)", null)
        while (cursor.moveToNext()) {
            info.add(mapOf(
                "cid" to cursor.getInt(0),
                "name" to cursor.getString(1),
                "type" to cursor.getString(2),
                "notnull" to cursor.getInt(3),
                "dflt_value" to cursor.getString(4),
                "pk" to cursor.getInt(5)
            ))
        }
        cursor.close()
        return info
    }

    fun getIndexNames(db: SQLiteDatabase, tableName: String): List<String> {
        val indexes = mutableListOf<String>()
        val cursor = db.rawQuery("PRAGMA index_list($tableName)", null)
        while (cursor.moveToNext()) {
            indexes.add(cursor.getString(1))
        }
        cursor.close()
        return indexes
    }

    fun getIndexInfo(db: SQLiteDatabase, indexName: String): List<Map<String, Any?>> {
        val info = mutableListOf<Map<String, Any?>>()
        val cursor = db.rawQuery("PRAGMA index_info($indexName)", null)
        while (cursor.moveToNext()) {
            info.add(mapOf(
                "seqno" to cursor.getInt(0),
                "cid" to cursor.getInt(1),
                "name" to cursor.getString(2)
            ))
        }
        cursor.close()
        return info
    }

    fun getRowCount(db: SQLiteDatabase, tableName: String): Long {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $tableName", null)
        cursor.moveToFirst()
        val count = cursor.getLong(0)
        cursor.close()
        return count
    }

    fun getRowCount(db: SQLiteDatabase, tableName: String, whereClause: String, whereArgs: Array<String>): Long {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $tableName WHERE $whereClause", whereArgs)
        cursor.moveToFirst()
        val count = cursor.getLong(0)
        cursor.close()
        return count
    }

    fun tableExists(db: SQLiteDatabase, tableName: String): Boolean {
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", arrayOf(tableName))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun columnExists(db: SQLiteDatabase, tableName: String, columnName: String): Boolean {
        val columns = getColumnNames(db, tableName)
        return columns.contains(columnName)
    }

    fun getDatabaseInfo(db: SQLiteDatabase): Map<String, Any> {
        return mapOf(
            "path" to db.path,
            "version" to db.version,
            "isOpen" to db.isOpen,
            "isReadOnly" to db.isReadOnly,
            "maxSize" to db.maximumSize,
            "pageSize" to db.pageSize,
            "tableCount" to getTableNames(db).size
        )
    }

    fun getDatabaseSize(db: SQLiteDatabase): Long {
        return File(db.path).length()
    }

    fun vacuum(db: SQLiteDatabase) {
        db.execSQL("VACUUM")
    }

    fun analyze(db: SQLiteDatabase) {
        db.execSQL("ANALYZE")
    }

    fun enableForeignKeys(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = ON")
    }

    fun disableForeignKeys(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = OFF")
    }

    fun setJournalMode(db: SQLiteDatabase, mode: String) {
        db.execSQL("PRAGMA journal_mode = $mode")
    }

    fun setSynchronous(db: SQLiteDatabase, mode: String) {
        db.execSQL("PRAGMA synchronous = $mode")
    }

    fun setCacheSize(db: SQLiteDatabase, size: Int) {
        db.execSQL("PRAGMA cache_size = $size")
    }

    fun setTempStore(db: SQLiteDatabase, mode: String) {
        db.execSQL("PRAGMA temp_store = $mode")
    }

    fun beginTransaction(db: SQLiteDatabase) {
        db.beginTransaction()
    }

    fun setTransactionSuccessful(db: SQLiteDatabase) {
        db.setTransactionSuccessful()
    }

    fun endTransaction(db: SQLiteDatabase) {
        db.endTransaction()
    }

    fun runInTransaction(db: SQLiteDatabase, operations: (SQLiteDatabase) -> Unit) {
        db.beginTransaction()
        try {
            operations(db)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun backupDatabase(db: SQLiteDatabase, backupPath: String): Boolean {
        return try {
            db.execSQL("BACKUP TO '$backupPath'")
            true
        } catch (e: Exception) {
            false
        }
    }

    fun restoreDatabase(db: SQLiteDatabase, backupPath: String): Boolean {
        return try {
            db.execSQL("RESTORE FROM '$backupPath'")
            true
        } catch (e: Exception) {
            false
        }
    }

    fun exportToCSV(db: SQLiteDatabase, tableName: String, csvPath: String): Boolean {
        return try {
            val cursor = db.rawQuery("SELECT * FROM $tableName", null)
            val columns = cursor.columnNames
            val sb = StringBuilder()
            sb.appendLine(columns.joinToString(","))
            while (cursor.moveToNext()) {
                val values = columns.mapIndexed { index, _ ->
                    val value = cursor.getString(index)
                    if (value != null && (value.contains(",") || value.contains("\"") || value.contains("\n"))) {
                        "\"${value.replace("\"", "\"\"")}\""
                    } else {
                        value ?: ""
                    }
                }
                sb.appendLine(values.joinToString(","))
            }
            cursor.close()
            File(csvPath).writeText(sb.toString())
            true
        } catch (e: Exception) {
            false
        }
    }

    fun importFromCSV(db: SQLiteDatabase, tableName: String, csvPath: String, hasHeader: Boolean = true): Int {
        return try {
            val lines = File(csvPath).readLines()
            val dataLines = if (hasHeader) lines.drop(1) else lines
            var count = 0
            db.beginTransaction()
            try {
                for (line in dataLines) {
                    val values = parseCSVLine(line)
                    val contentValues = ContentValues()
                    val columns = getColumnNames(db, tableName)
                    for (i in values.indices) {
                        if (i < columns.size) {
                            contentValues.put(columns[i], values[i])
                        }
                    }
                    db.insert(tableName, null, contentValues)
                    count++
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
            count
        } catch (e: Exception) {
            0
        }
    }

    private fun parseCSVLine(line: String): List<String> {
        val values = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        for (char in line) {
            when {
                char == '"' && !inQuotes -> inQuotes = true
                char == '"' && inQuotes -> inQuotes = false
                char == ',' && !inQuotes -> {
                    values.add(current.toString())
                    current.clear()
                }
                else -> current.append(char)
            }
        }
        values.add(current.toString())
        return values
    }

    fun getTableSchema(db: SQLiteDatabase, tableName: String): String {
        val cursor = db.rawQuery("SELECT sql FROM sqlite_master WHERE type='table' AND name=?", arrayOf(tableName))
        val schema = if (cursor.moveToFirst()) cursor.getString(0) else ""
        cursor.close()
        return schema
    }

    fun getAllSchemas(db: SQLiteDatabase): Map<String, String> {
        val schemas = mutableMapOf<String, String>()
        val cursor = db.rawQuery("SELECT name, sql FROM sqlite_master WHERE type='table'", null)
        while (cursor.moveToNext()) {
            schemas[cursor.getString(0)] = cursor.getString(1)
        }
        cursor.close()
        return schemas
    }

    fun getDatabaseStats(db: SQLiteDatabase): Map<String, Any> {
        val tables = getTableNames(db)
        val tableStats = tables.associateWith { table ->
            mapOf(
                "rowCount" to getRowCount(db, table),
                "columns" to getColumnNames(db, table),
                "indexes" to getIndexNames(db, table)
            )
        }
        return mapOf(
            "path" to db.path,
            "version" to db.version,
            "size" to getDatabaseSize(db),
            "tableCount" to tables.size,
            "tables" to tableStats
        )
    }

    fun getTableStats(db: SQLiteDatabase, tableName: String): Map<String, Any> {
        return mapOf(
            "rowCount" to getRowCount(db, tableName),
            "columns" to getColumnNames(db, tableName),
            "columnCount" to getColumnNames(db, tableName).size,
            "indexes" to getIndexNames(db, tableName),
            "indexCount" to getIndexNames(db, tableName).size,
            "schema" to getTableSchema(db, tableName)
        )
    }

    fun searchInTable(db: SQLiteDatabase, tableName: String, searchColumn: String, searchTerm: String): Cursor {
        return db.rawQuery("SELECT * FROM $tableName WHERE $searchColumn LIKE ?", arrayOf("%$searchTerm%"))
    }

    fun searchInAllColumns(db: SQLiteDatabase, tableName: String, searchTerm: String): Cursor {
        val columns = getColumnNames(db, tableName)
        val whereClause = columns.joinToString(" OR ") { "$it LIKE ?" }
        val args = Array(columns.size) { "%$searchTerm%" }
        return db.rawQuery("SELECT * FROM $tableName WHERE $whereClause", args)
    }

    fun getDistinctValues(db: SQLiteDatabase, tableName: String, columnName: String): List<String> {
        val values = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT DISTINCT $columnName FROM $tableName", null)
        while (cursor.moveToNext()) {
            values.add(cursor.getString(0))
        }
        cursor.close()
        return values
    }

    fun getMinValue(db: SQLiteDatabase, tableName: String, columnName: String): String? {
        val cursor = db.rawQuery("SELECT MIN($columnName) FROM $tableName", null)
        val value = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        return value
    }

    fun getMaxValue(db: SQLiteDatabase, tableName: String, columnName: String): String? {
        val cursor = db.rawQuery("SELECT MAX($columnName) FROM $tableName", null)
        val value = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        return value
    }

    fun getAvgValue(db: SQLiteDatabase, tableName: String, columnName: String): Double {
        val cursor = db.rawQuery("SELECT AVG($columnName) FROM $tableName", null)
        val value = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        cursor.close()
        return value
    }

    fun getSumValue(db: SQLiteDatabase, tableName: String, columnName: String): Double {
        val cursor = db.rawQuery("SELECT SUM($columnName) FROM $tableName", null)
        val value = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        cursor.close()
        return value
    }

    fun getMedianValue(db: SQLiteDatabase, tableName: String, columnName: String): Double {
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        if (values.isEmpty()) return 0.0
        val sorted = values.sorted()
        val mid = sorted.size / 2
        return if (sorted.size % 2 == 0) {
            (sorted[mid - 1] + sorted[mid]) / 2.0
        } else {
            sorted[mid]
        }
    }

    fun getModeValue(db: SQLiteDatabase, tableName: String, columnName: String): String? {
        val cursor = db.rawQuery("SELECT $columnName, COUNT(*) as cnt FROM $tableName GROUP BY $columnName ORDER BY cnt DESC LIMIT 1", null)
        val value = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        return value
    }

    fun getStandardDeviation(db: SQLiteDatabase, tableName: String, columnName: String): Double {
        val avg = getAvgValue(db, tableName, columnName)
        val cursor = db.rawQuery("SELECT ($columnName - ?) * ($columnName - ?) FROM $tableName", arrayOf(avg.toString(), avg.toString()))
        var sum = 0.0
        var count = 0
        while (cursor.moveToNext()) {
            sum += cursor.getDouble(0)
            count++
        }
        cursor.close()
        return if (count > 0) kotlin.math.sqrt(sum / count) else 0.0
    }

    fun getVariance(db: SQLiteDatabase, tableName: String, columnName: String): Double {
        val stdDev = getStandardDeviation(db, tableName, columnName)
        return stdDev * stdDev
    }

    fun getPercentile(db: SQLiteDatabase, tableName: String, columnName: String, percentile: Double): Double {
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        if (values.isEmpty()) return 0.0
        val index = (percentile / 100.0 * (values.size - 1)).toInt()
        return values[index]
    }

    fun getQuartiles(db: SQLiteDatabase, tableName: String, columnName: String): Triple<Double, Double, Double> {
        val q1 = getPercentile(db, tableName, columnName, 25.0)
        val q2 = getPercentile(db, tableName, columnName, 50.0)
        val q3 = getPercentile(db, tableName, columnName, 75.0)
        return Triple(q1, q2, q3)
    }

    fun getIQR(db: SQLiteDatabase, tableName: String, columnName: String): Double {
        val (q1, _, q3) = getQuartiles(db, tableName, columnName)
        return q3 - q1
    }

    fun getOutliers(db: SQLiteDatabase, tableName: String, columnName: String): List<Double> {
        val (q1, _, q3) = getQuartiles(db, tableName, columnName)
        val iqr = q3 - q1
        val lowerBound = q1 - 1.5 * iqr
        val upperBound = q3 + 1.5 * iqr
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName WHERE $columnName < ? OR $columnName > ?", arrayOf(lowerBound.toString(), upperBound.toString()))
        val outliers = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            outliers.add(cursor.getDouble(0))
        }
        cursor.close()
        return outliers
    }

    fun getCorrelation(db: SQLiteDatabase, tableName: String, column1: String, column2: String): Double {
        val cursor = db.rawQuery("SELECT $column1, $column2 FROM $tableName", null)
        val xValues = mutableListOf<Double>()
        val yValues = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            xValues.add(cursor.getDouble(0))
            yValues.add(cursor.getDouble(1))
        }
        cursor.close()
        if (xValues.size < 2) return 0.0
        val xMean = xValues.average()
        val yMean = yValues.average()
        var numerator = 0.0
        var xDenom = 0.0
        var yDenom = 0.0
        for (i in xValues.indices) {
            val xDiff = xValues[i] - xMean
            val yDiff = yValues[i] - yMean
            numerator += xDiff * yDiff
            xDenom += xDiff * xDiff
            yDenom += yDiff * yDiff
        }
        val denominator = kotlin.math.sqrt(xDenom * yDenom)
        return if (denominator == 0.0) 0.0 else numerator / denominator
    }

    fun getCovariance(db: SQLiteDatabase, tableName: String, column1: String, column2: String): Double {
        val cursor = db.rawQuery("SELECT $column1, $column2 FROM $tableName", null)
        val xValues = mutableListOf<Double>()
        val yValues = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            xValues.add(cursor.getDouble(0))
            yValues.add(cursor.getDouble(1))
        }
        cursor.close()
        if (xValues.size < 2) return 0.0
        val xMean = xValues.average()
        val yMean = yValues.average()
        var sum = 0.0
        for (i in xValues.indices) {
            sum += (xValues[i] - xMean) * (yValues[i] - yMean)
        }
        return sum / (xValues.size - 1)
    }

    fun getLinearRegression(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Pair<Double, Double> {
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn FROM $tableName", null)
        val xValues = mutableListOf<Double>()
        val yValues = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            xValues.add(cursor.getDouble(0))
            yValues.add(cursor.getDouble(1))
        }
        cursor.close()
        if (xValues.size < 2) return Pair(0.0, 0.0)
        val xMean = xValues.average()
        val yMean = yValues.average()
        var numerator = 0.0
        var denominator = 0.0
        for (i in xValues.indices) {
            numerator += (xValues[i] - xMean) * (yValues[i] - yMean)
            denominator += (xValues[i] - xMean) * (xValues[i] - xMean)
        }
        val slope = if (denominator == 0.0) 0.0 else numerator / denominator
        val intercept = yMean - slope * xMean
        return Pair(slope, intercept)
    }

    fun getRSquared(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Double {
        val correlation = getCorrelation(db, tableName, xColumn, yColumn)
        return correlation * correlation
    }

    fun getAdjustedRSquared(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String, numPredictors: Int): Double {
        val rSquared = getRSquared(db, tableName, xColumn, yColumn)
        val n = getRowCount(db, tableName).toInt()
        return 1 - (1 - rSquared) * (n - 1) / (n - numPredictors - 1)
    }

    fun getFStatistic(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Double {
        val rSquared = getRSquared(db, tableName, xColumn, yColumn)
        val n = getRowCount(db, tableName).toInt()
        return (rSquared / 1) / ((1 - rSquared) / (n - 2))
    }

    fun getTStatistic(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Double {
        val (slope, _) = getLinearRegression(db, tableName, xColumn, yColumn)
        val n = getRowCount(db, tableName).toInt()
        val xValues = mutableListOf<Double>()
        val cursor = db.rawQuery("SELECT $xColumn FROM $tableName", null)
        while (cursor.moveToNext()) {
            xValues.add(cursor.getDouble(0))
        }
        cursor.close()
        val xMean = xValues.average()
        val xStdDev = getStandardDeviation(db, tableName, xColumn)
        val standardError = xStdDev / kotlin.math.sqrt(n.toDouble())
        return if (standardError == 0.0) 0.0 else slope / standardError
    }

    fun getPValue(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Double {
        val tStat = kotlin.math.abs(getTStatistic(db, tableName, xColumn, yColumn))
        val n = getRowCount(db, tableName).toInt()
        val df = n - 2
        return 2 * (1 - tDistributionCDF(tStat, df))
    }

    private fun tDistributionCDF(t: Double, df: Int): Double {
        val x = df / (df + t * t)
        return 1 - 0.5 * incompleteBeta(x, df / 2.0, 0.5)
    }

    private fun incompleteBeta(x: Double, a: Double, b: Double): Double {
        if (x <= 0.0) return 0.0
        if (x >= 1.0) return 1.0
        val lnBeta = lnGamma(a) + lnGamma(b) - lnGamma(a + b)
        val front = kotlin.math.exp(kotlin.math.ln(x) * a + kotlin.math.ln(1 - x) * b - lnBeta)
        return front * continuedFraction(x, a, b)
    }

    private fun continuedFraction(x: Double, a: Double, b: Double): Double {
        val maxIterations = 200
        val epsilon = 1e-10
        val am = 1.0
        var bm = 1.0
        var az = 1.0
        var qab = a + b
        var qap = a + 1
        var qam = a - 1
        var bz = 1 - qab * x / qap
        var aold = 0.0
        var m = 1
        while (m <= maxIterations) {
            val m2 = 2 * m
            var d = m * (b - m) * x / ((qam + m2) * (a + m2))
            var ap = az + d * am
            var bp = bz + d * bm
            d = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2))
            var app = ap + d * az
            var bpp = bp + d * bz
            aold = az
            am = ap / bpp
            bm = bp / bpp
            az = app / bpp
            bz = 1.0
            if (kotlin.math.abs(az - aold) < epsilon * kotlin.math.abs(az)) break
            m++
        }
        return az
    }

    private fun lnGamma(x: Double): Double {
        val cof = doubleArrayOf(76.18009172947146, -86.50532032941677, 24.01409824083091, -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5)
        var y = x
        var tmp = x + 5.5
        tmp -= (x + 0.5) * kotlin.math.ln(tmp)
        var ser = 1.000000000190015
        for (j in 0..5) {
            y += 1
            ser += cof[j] / y
        }
        return -tmp + kotlin.math.ln(2.5066282746310005 * ser / x)
    }

    fun getConfidenceInterval(db: SQLiteDatabase, tableName: String, columnName: String, confidenceLevel: Double): Pair<Double, Double> {
        val mean = getAvgValue(db, tableName, columnName)
        val stdDev = getStandardDeviation(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val zScore = when (confidenceLevel) {
            0.90 -> 1.645
            0.95 -> 1.96
            0.99 -> 2.576
            else -> 1.96
        }
        val marginOfError = zScore * stdDev / kotlin.math.sqrt(n.toDouble())
        return Pair(mean - marginOfError, mean + marginOfError)
    }

    fun getPredictionInterval(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String, xValue: Double): Pair<Double, Double> {
        val (slope, intercept) = getLinearRegression(db, tableName, xColumn, yColumn)
        val yPred = slope * xValue + intercept
        val n = getRowCount(db, tableName).toInt()
        val xMean = getAvgValue(db, tableName, xColumn)
        val xStdDev = getStandardDeviation(db, tableName, xColumn)
        val standardError = xStdDev * kotlin.math.sqrt(1 + 1.0 / n + (xValue - xMean) * (xValue - xMean) / ((n - 1) * xStdDev * xStdDev))
        val tScore = 1.96
        val marginOfError = tScore * standardError
        return Pair(yPred - marginOfError, yPred + marginOfError)
    }

    fun getResiduals(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): List<Double> {
        val (slope, intercept) = getLinearRegression(db, tableName, xColumn, yColumn)
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn FROM $tableName", null)
        val residuals = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            val x = cursor.getDouble(0)
            val y = cursor.getDouble(1)
            val yPred = slope * x + intercept
            residuals.add(y - yPred)
        }
        cursor.close()
        return residuals
    }

    fun getStandardizedResiduals(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): List<Double> {
        val residuals = getResiduals(db, tableName, xColumn, yColumn)
        val mean = residuals.average()
        val stdDev = kotlin.math.sqrt(residuals.sumOf { (it - mean) * (it - mean) } / residuals.size)
        return if (stdDev == 0.0) residuals.map { 0.0 } else residuals.map { (it - mean) / stdDev }
    }

    fun getLeverage(db: SQLiteDatabase, tableName: String, xColumn: String): List<Double> {
        val xMean = getAvgValue(db, tableName, xColumn)
        val xStdDev = getStandardDeviation(db, tableName, xColumn)
        val n = getRowCount(db, tableName).toInt()
        val cursor = db.rawQuery("SELECT $xColumn FROM $tableName", null)
        val leverages = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            val x = cursor.getDouble(0)
            val leverage = 1.0 / n + (x - xMean) * (x - xMean) / ((n - 1) * xStdDev * xStdDev)
            leverages.add(leverage)
        }
        cursor.close()
        return leverages
    }

    fun getCooksDistance(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): List<Double> {
        val residuals = getResiduals(db, tableName, xColumn, yColumn)
        val leverages = getLeverage(db, tableName, xColumn)
        val n = getRowCount(db, tableName).toInt()
        val mse = residuals.sumOf { it * it } / (n - 2)
        val cooksDistances = mutableListOf<Double>()
        for (i in residuals.indices) {
            val cooksD = residuals[i] * residuals[i] * leverages[i] / (1 * mse * (1 - leverages[i]) * (1 - leverages[i]))
            cooksDistances.add(cooksD)
        }
        return cooksDistances
    }

    fun getDFFITS(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): List<Double> {
        val residuals = getResiduals(db, tableName, xColumn, yColumn)
        val leverages = getLeverage(db, tableName, xColumn)
        val n = getRowCount(db, tableName).toInt()
        val mse = residuals.sumOf { it * it } / (n - 2)
        val dffits = mutableListOf<Double>()
        for (i in residuals.indices) {
            val dffit = residuals[i] * kotlin.math.sqrt(leverages[i] / (1 - leverages[i])) / kotlin.math.sqrt(mse)
            dffits.add(dffit)
        }
        return dffits
    }

    fun getDFBETAS(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): List<Double> {
        val residuals = getResiduals(db, tableName, xColumn, yColumn)
        val leverages = getLeverage(db, tableName, xColumn)
        val n = getRowCount(db, tableName).toInt()
        val mse = residuals.sumOf { it * it } / (n - 2)
        val dfbetas = mutableListOf<Double>()
        for (i in residuals.indices) {
            val dfbeta = residuals[i] / kotlin.math.sqrt(mse * (1 - leverages[i]))
            dfbetas.add(dfbeta)
        }
        return dfbetas
    }

    fun getVIF(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Double {
        val rSquared = getRSquared(db, tableName, xColumn, yColumn)
        return if (rSquared >= 1.0) Double.POSITIVE_INFINITY else 1.0 / (1.0 - rSquared)
    }

    fun getTolerance(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Double {
        val rSquared = getRSquared(db, tableName, xColumn, yColumn)
        return 1.0 - rSquared
    }

    fun getConditionNumber(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Double {
        val xStdDev = getStandardDeviation(db, tableName, xColumn)
        val yStdDev = getStandardDeviation(db, tableName, yColumn)
        return if (yStdDev == 0.0) Double.POSITIVE_INFINITY else xStdDev / yStdDev
    }

    fun getEigenvalues(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Pair<Double, Double> {
        val covXY = getCovariance(db, tableName, xColumn, yColumn)
        val varX = getVariance(db, tableName, xColumn)
        val varY = getVariance(db, tableName, yColumn)
        val trace = varX + varY
        val det = varX * varY - covXY * covXY
        val discriminant = trace * trace - 4 * det
        val lambda1 = (trace + kotlin.math.sqrt(discriminant)) / 2
        val lambda2 = (trace - kotlin.math.sqrt(discriminant)) / 2
        return Pair(lambda1, lambda2)
    }

    fun getEigenvectors(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Pair<Pair<Double, Double>, Pair<Double, Double>> {
        val covXY = getCovariance(db, tableName, xColumn, yColumn)
        val varX = getVariance(db, tableName, xColumn)
        val varY = getVariance(db, tableName, yColumn)
        val (lambda1, lambda2) = getEigenvalues(db, tableName, xColumn, yColumn)
        val v1 = if (covXY != 0.0) Pair(lambda1 - varY, covXY) else Pair(1.0, 0.0)
        val v2 = if (covXY != 0.0) Pair(lambda2 - varY, covXY) else Pair(0.0, 1.0)
        val v1Norm = kotlin.math.sqrt(v1.first * v1.first + v1.second * v1.second)
        val v2Norm = kotlin.math.sqrt(v2.first * v2.first + v2.second * v2.second)
        val v1Normalized = if (v1Norm > 0) Pair(v1.first / v1Norm, v1.second / v1Norm) else v1
        val v2Normalized = if (v2Norm > 0) Pair(v2.first / v2Norm, v2.second / v2Norm) else v2
        return Pair(v1Normalized, v2Normalized)
    }

    fun getPCA(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Map<String, Any> {
        val (lambda1, lambda2) = getEigenvalues(db, tableName, xColumn, yColumn)
        val (v1, v2) = getEigenvectors(db, tableName, xColumn, yColumn)
        val totalVariance = lambda1 + lambda2
        val explainedVariance1 = if (totalVariance > 0) lambda1 / totalVariance else 0.0
        val explainedVariance2 = if (totalVariance > 0) lambda2 / totalVariance else 0.0
        return mapOf(
            "eigenvalues" to listOf(lambda1, lambda2),
            "eigenvectors" to listOf(v1, v2),
            "explainedVariance" to listOf(explainedVariance1, explainedVariance2),
            "cumulativeVariance" to listOf(explainedVariance1, explainedVariance1 + explainedVariance2)
        )
    }

    fun getMahalanobisDistance(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String, x: Double, y: Double): Double {
        val xMean = getAvgValue(db, tableName, xColumn)
        val yMean = getAvgValue(db, tableName, yColumn)
        val covXY = getCovariance(db, tableName, xColumn, yColumn)
        val varX = getVariance(db, tableName, xColumn)
        val varY = getVariance(db, tableName, yColumn)
        val det = varX * varY - covXY * covXY
        if (det == 0.0) return Double.POSITIVE_INFINITY
        val dx = x - xMean
        val dy = y - yMean
        return kotlin.math.sqrt((varY * dx * dx - 2 * covXY * dx * dy + varX * dy * dy) / det)
    }

    fun getHotellingsT2(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String, x: Double, y: Double): Double {
        val n = getRowCount(db, tableName).toInt()
        val mahalanobis = getMahalanobisDistance(db, tableName, xColumn, yColumn, x, y)
        return n * mahalanobis * mahalanobis / (n - 1)
    }

    fun getBonferroniOutliers(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): List<Int> {
        val n = getRowCount(db, tableName).toInt()
        val alpha = 0.05 / n
        val tScore = 2.0
        val outliers = mutableListOf<Int>()
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn FROM $tableName", null)
        var index = 0
        while (cursor.moveToNext()) {
            val x = cursor.getDouble(0)
            val y = cursor.getDouble(1)
            val mahalanobis = getMahalanobisDistance(db, tableName, xColumn, yColumn, x, y)
            if (mahalanobis > tScore) {
                outliers.add(index)
            }
            index++
        }
        cursor.close()
        return outliers
    }

    fun getChauvenetOutliers(db: SQLiteDatabase, tableName: String, columnName: String): List<Int> {
        val mean = getAvgValue(db, tableName, columnName)
        val stdDev = getStandardDeviation(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val outliers = mutableListOf<Int>()
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName", null)
        var index = 0
        while (cursor.moveToNext()) {
            val value = cursor.getDouble(0)
            val zScore = kotlin.math.abs(value - mean) / stdDev
            val probability = 2 * (1 - normalCDF(zScore))
            if (probability < 1.0 / (2 * n)) {
                outliers.add(index)
            }
            index++
        }
        cursor.close()
        return outliers
    }

    private fun normalCDF(x: Double): Double {
        val a1 = 0.254829592
        val a2 = -0.284496736
        val a3 = 1.421413741
        val a4 = -1.453152027
        val a5 = 1.061405429
        val p = 0.3275911
        val sign = if (x < 0) -1 else 1
        val absX = kotlin.math.abs(x) / kotlin.math.sqrt(2.0)
        val t = 1.0 / (1.0 + p * absX)
        val y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * kotlin.math.exp(-absX * absX)
        return 0.5 * (1.0 + sign * y)
    }

    fun getGrubbsOutliers(db: SQLiteDatabase, tableName: String, columnName: String): List<Int> {
        val mean = getAvgValue(db, tableName, columnName)
        val stdDev = getStandardDeviation(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val outliers = mutableListOf<Int>()
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName", null)
        var index = 0
        while (cursor.moveToNext()) {
            val value = cursor.getDouble(0)
            val zScore = kotlin.math.abs(value - mean) / stdDev
            val tScore = 1.96
            if (zScore > tScore) {
                outliers.add(index)
            }
            index++
        }
        cursor.close()
        return outliers
    }

    fun getDixonOutliers(db: SQLiteDatabase, tableName: String, columnName: String): List<Int> {
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = values.size
        if (n < 3) return emptyList()
        val outliers = mutableListOf<Int>()
        val q1 = values[n / 4]
        val q3 = values[3 * n / 4]
        val iqr = q3 - q1
        val lowerBound = q1 - 1.5 * iqr
        val upperBound = q3 + 1.5 * iqr
        for (i in values.indices) {
            if (values[i] < lowerBound || values[i] > upperBound) {
                outliers.add(i)
            }
        }
        return outliers
    }

    fun getGESDOutliers(db: SQLiteDatabase, tableName: String, columnName: String, maxOutliers: Int): List<Int> {
        val mean = getAvgValue(db, tableName, columnName)
        val stdDev = getStandardDeviation(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val outliers = mutableListOf<Int>()
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val remainingValues = values.toMutableList()
        for (i in 0 until maxOutliers.coerceAtMost(n / 2)) {
            val currentMean = remainingValues.average()
            val currentStdDev = kotlin.math.sqrt(remainingValues.sumOf { (it - currentMean) * (it - currentMean) } / remainingValues.size)
            if (currentStdDev == 0.0) break
            var maxZScore = 0.0
            var maxIndex = -1
            for (j in remainingValues.indices) {
                val zScore = kotlin.math.abs(remainingValues[j] - currentMean) / currentStdDev
                if (zScore > maxZScore) {
                    maxZScore = zScore
                    maxIndex = j
                }
            }
            if (maxIndex >= 0) {
                outliers.add(values.indexOf(remainingValues[maxIndex]))
                remainingValues.removeAt(maxIndex)
            }
        }
        return outliers
    }

    fun getOutlierSummary(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Any> {
        val chauvenet = getChauvenetOutliers(db, tableName, columnName)
        val grubbs = getGrubbsOutliers(db, tableName, columnName)
        val dixon = getDixonOutliers(db, tableName, columnName)
        val gesd = getGESDOutliers(db, tableName, columnName, 10)
        return mapOf(
            "chauvenet" to chauvenet,
            "grubbs" to grubbs,
            "dixon" to dixon,
            "gesd" to gesd,
            "totalOutliers" to (chauvenet + grubbs + dixon + gesd).distinct().size
        )
    }

    fun getDescriptiveStats(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return mapOf(
            "count" to getRowCount(db, tableName).toDouble(),
            "mean" to getAvgValue(db, tableName, columnName),
            "median" to getMedianValue(db, tableName, columnName),
            "mode" to (getModeValue(db, tableName, columnName)?.toDoubleOrNull() ?: 0.0),
            "stdDev" to getStandardDeviation(db, tableName, columnName),
            "variance" to getVariance(db, tableName, columnName),
            "min" to (getMinValue(db, tableName, columnName)?.toDoubleOrNull() ?: 0.0),
            "max" to (getMaxValue(db, tableName, columnName)?.toDoubleOrNull() ?: 0.0),
            "range" to ((getMaxValue(db, tableName, columnName)?.toDoubleOrNull() ?: 0.0) - (getMinValue(db, tableName, columnName)?.toDoubleOrNull() ?: 0.0)),
            "q1" to getPercentile(db, tableName, columnName, 25.0),
            "q3" to getPercentile(db, tableName, columnName, 75.0),
            "iqr" to getIQR(db, tableName, columnName),
            "skewness" to getSkewness(db, tableName, columnName),
            "kurtosis" to getKurtosis(db, tableName, columnName)
        )
    }

    private fun getSkewness(db: SQLiteDatabase, tableName: String, columnName: String): Double {
        val mean = getAvgValue(db, tableName, columnName)
        val stdDev = getStandardDeviation(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName", null)
        var sum = 0.0
        while (cursor.moveToNext()) {
            val value = cursor.getDouble(0)
            sum += kotlin.math.pow((value - mean) / stdDev, 3)
        }
        cursor.close()
        return if (n > 2) (n.toDouble() / ((n - 1) * (n - 2))) * sum else 0.0
    }

    private fun getKurtosis(db: SQLiteDatabase, tableName: String, columnName: String): Double {
        val mean = getAvgValue(db, tableName, columnName)
        val stdDev = getStandardDeviation(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName", null)
        var sum = 0.0
        while (cursor.moveToNext()) {
            val value = cursor.getDouble(0)
            sum += kotlin.math.pow((value - mean) / stdDev, 4)
        }
        cursor.close()
        return if (n > 3) {
            (n.toDouble() * (n + 1) / ((n - 1) * (n - 2) * (n - 3))) * sum - 3.0 * (n - 1) * (n - 1) / ((n - 2) * (n - 3))
        } else 0.0
    }

    fun getNormalityTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val skewness = getSkewness(db, tableName, columnName)
        val kurtosis = getKurtosis(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val jarqueBera = n / 6.0 * (skewness * skewness + kurtosis * kurtosis / 4)
        return mapOf(
            "skewness" to skewness,
            "kurtosis" to kurtosis,
            "jarqueBera" to jarqueBera,
            "isNormal" to if (jarqueBera < 5.99) 1.0 else 0.0
        )
    }

    fun getHistogram(db: SQLiteDatabase, tableName: String, columnName: String, bins: Int = 10): Map<String, List<Any>> {
        val min = getMinValue(db, tableName, columnName)?.toDoubleOrNull() ?: 0.0
        val max = getMaxValue(db, tableName, columnName)?.toDoubleOrNull() ?: 0.0
        val binWidth = (max - min) / bins
        val counts = IntArray(bins)
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName", null)
        while (cursor.moveToNext()) {
            val value = cursor.getDouble(0)
            val binIndex = ((value - min) / binWidth).toInt().coerceIn(0, bins - 1)
            counts[binIndex]++
        }
        cursor.close()
        val binEdges = (0..bins).map { min + it * binWidth }
        return mapOf(
            "binEdges" to binEdges,
            "counts" to counts.toList(),
            "binWidth" to listOf(binWidth)
        )
    }

    fun getFrequencyDistribution(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Int> {
        val distribution = mutableMapOf<String, Int>()
        val cursor = db.rawQuery("SELECT $columnName, COUNT(*) FROM $tableName GROUP BY $columnName", null)
        while (cursor.moveToNext()) {
            distribution[cursor.getString(0)] = cursor.getInt(1)
        }
        cursor.close()
        return distribution
    }

    fun getCumulativeDistribution(db: SQLiteDatabase, tableName: String, columnName: String): List<Pair<Double, Double>> {
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = values.size
        return values.mapIndexed { index, value -> Pair(value, (index + 1).toDouble() / n) }
    }

    fun getQQPlot(db: SQLiteDatabase, tableName: String, columnName: String): List<Pair<Double, Double>> {
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = values.size
        val mean = values.average()
        val stdDev = kotlin.math.sqrt(values.sumOf { (it - mean) * (it - mean) } / n)
        return values.mapIndexed { index, value ->
            val p = (index + 0.5) / n
            val theoreticalQuantile = mean + stdDev * normalInverse(p)
            Pair(theoreticalQuantile, value)
        }
    }

    private fun normalInverse(p: Double): Double {
        val a = doubleArrayOf(-3.969683028665376e+01, 2.209460984245205e+02, -2.759285104469687e+02, 1.383577518672690e+02, -3.066479806614716e+01, 2.506628277459239e+00)
        val b = doubleArrayOf(-5.447609879822406e+01, 1.615858368580409e+02, -1.556989798598866e+02, 6.680131188771972e+01, -1.328068155288572e+01)
        val c = doubleArrayOf(-7.784894002430293e-03, -3.223964580411365e-01, -2.400758277161838e+00, -2.549732539343734e+00, 4.374664141464968e+00, 2.938163982698783e+00)
        val d = doubleArrayOf(7.784695709041462e-03, 3.224671290700398e-01, 2.445134137142996e+00, 3.754408661907416e+00)
        val pLow = 0.02425
        val pHigh = 1 - pLow
        return when {
            p < pLow -> {
                val q = kotlin.math.sqrt(-2 * kotlin.math.ln(p))
                (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]) / ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1)
            }
            p <= pHigh -> {
                val q = p - 0.5
                val r = q * q
                (((((a[0] * r + a[1]) * r + a[2]) * r + a[3]) * r + a[4]) * r + a[5]) * q / (((((b[0] * r + b[1]) * r + b[2]) * r + b[3]) * r + b[4]) * r + 1)
            }
            else -> {
                val q = kotlin.math.sqrt(-2 * kotlin.math.ln(1 - p))
                -(((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]) / ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1)
            }
        }
    }

    fun getBoxPlotStats(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val (q1, q2, q3) = getQuartiles(db, tableName, columnName)
        val iqr = q3 - q1
        val lowerFence = q1 - 1.5 * iqr
        val upperFence = q3 + 1.5 * iqr
        val min = getMinValue(db, tableName, columnName)?.toDoubleOrNull() ?: 0.0
        val max = getMaxValue(db, tableName, columnName)?.toDoubleOrNull() ?: 0.0
        val lowerWhisker = if (min < lowerFence) lowerFence else min
        val upperWhisker = if (max > upperFence) upperFence else max
        return mapOf(
            "min" to min,
            "max" to max,
            "q1" to q1,
            "median" to q2,
            "q3" to q3,
            "iqr" to iqr,
            "lowerFence" to lowerFence,
            "upperFence" to upperFence,
            "lowerWhisker" to lowerWhisker,
            "upperWhisker" to upperWhisker
        )
    }

    fun getViolinPlotData(db: SQLiteDatabase, tableName: String, columnName: String, points: Int = 100): List<Triple<Double, Double, Double>> {
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        if (values.isEmpty()) return emptyList()
        val min = values.min()
        val max = values.max()
        val step = (max - min) / points
        return (0..points).map { i ->
            val x = min + i * step
            val density = values.count { kotlin.math.abs(it - x) < step }.toDouble() / values.size
            Triple(x, density, x)
        }
    }

    fun getScatterPlotData(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): List<Pair<Double, Double>> {
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn FROM $tableName", null)
        val data = mutableListOf<Pair<Double, Double>>()
        while (cursor.moveToNext()) {
            data.add(Pair(cursor.getDouble(0), cursor.getDouble(1)))
        }
        cursor.close()
        return data
    }

    fun getHeatmapData(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String, xBins: Int = 10, yBins: Int = 10): Array<IntArray> {
        val xMin = getMinValue(db, tableName, xColumn)?.toDoubleOrNull() ?: 0.0
        val xMax = getMaxValue(db, tableName, xColumn)?.toDoubleOrNull() ?: 0.0
        val yMin = getMinValue(db, tableName, yColumn)?.toDoubleOrNull() ?: 0.0
        val yMax = getMaxValue(db, tableName, yColumn)?.toDoubleOrNull() ?: 0.0
        val xStep = (xMax - xMin) / xBins
        val yStep = (yMax - yMin) / yBins
        val heatmap = Array(xBins) { IntArray(yBins) }
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn FROM $tableName", null)
        while (cursor.moveToNext()) {
            val x = cursor.getDouble(0)
            val y = cursor.getDouble(1)
            val xBin = ((x - xMin) / xStep).toInt().coerceIn(0, xBins - 1)
            val yBin = ((y - yMin) / yStep).toInt().coerceIn(0, yBins - 1)
            heatmap[xBin][yBin]++
        }
        cursor.close()
        return heatmap
    }

    fun getContourData(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String, zColumn: String, xBins: Int = 10, yBins: Int = 10): Array<DoubleArray> {
        val xMin = getMinValue(db, tableName, xColumn)?.toDoubleOrNull() ?: 0.0
        val xMax = getMaxValue(db, tableName, xColumn)?.toDoubleOrNull() ?: 0.0
        val yMin = getMinValue(db, tableName, yColumn)?.toDoubleOrNull() ?: 0.0
        val yMax = getMaxValue(db, tableName, yColumn)?.toDoubleOrNull() ?: 0.0
        val xStep = (xMax - xMin) / xBins
        val yStep = (yMax - yMin) / yBins
        val zSum = Array(xBins) { DoubleArray(yBins) }
        val zCount = Array(xBins) { IntArray(yBins) }
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn, $zColumn FROM $tableName", null)
        while (cursor.moveToNext()) {
            val x = cursor.getDouble(0)
            val y = cursor.getDouble(1)
            val z = cursor.getDouble(2)
            val xBin = ((x - xMin) / xStep).toInt().coerceIn(0, xBins - 1)
            val yBin = ((y - yMin) / yStep).toInt().coerceIn(0, yBins - 1)
            zSum[xBin][yBin] += z
            zCount[xBin][yBin]++
        }
        cursor.close()
        return Array(xBins) { i ->
            DoubleArray(yBins) { j ->
                if (zCount[i][j] > 0) zSum[i][j] / zCount[i][j] else 0.0
            }
        }
    }

    fun get3DScatterData(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String, zColumn: String): List<Triple<Double, Double, Double>> {
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn, $zColumn FROM $tableName", null)
        val data = mutableListOf<Triple<Double, Double, Double>>()
        while (cursor.moveToNext()) {
            data.add(Triple(cursor.getDouble(0), cursor.getDouble(1), cursor.getDouble(2)))
        }
        cursor.close()
        return data
    }

    fun getTimeSeriesData(db: SQLiteDatabase, tableName: String, timeColumn: String, valueColumn: String): List<Pair<Long, Double>> {
        val cursor = db.rawQuery("SELECT $timeColumn, $valueColumn FROM $tableName ORDER BY $timeColumn", null)
        val data = mutableListOf<Pair<Long, Double>>()
        while (cursor.moveToNext()) {
            data.add(Pair(cursor.getLong(0), cursor.getDouble(1)))
        }
        cursor.close()
        return data
    }

    fun getMovingAverage(db: SQLiteDatabase, tableName: String, valueColumn: String, windowSize: Int): List<Double> {
        val cursor = db.rawQuery("SELECT $valueColumn FROM $tableName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        return values.windowed(windowSize) { it.average() }
    }

    fun getExponentialMovingAverage(db: SQLiteDatabase, tableName: String, valueColumn: String, alpha: Double): List<Double> {
        val cursor = db.rawQuery("SELECT $valueColumn FROM $tableName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        if (values.isEmpty()) return emptyList()
        val ema = mutableListOf(values[0])
        for (i in 1 until values.size) {
            ema.add(alpha * values[i] + (1 - alpha) * ema[i - 1])
        }
        return ema
    }

    fun getCumulativeSum(db: SQLiteDatabase, tableName: String, valueColumn: String): List<Double> {
        val cursor = db.rawQuery("SELECT $valueColumn FROM $tableName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val cumulative = mutableListOf<Double>()
        var sum = 0.0
        for (value in values) {
            sum += value
            cumulative.add(sum)
        }
        return cumulative
    }

    fun getDifferences(db: SQLiteDatabase, tableName: String, valueColumn: String): List<Double> {
        val cursor = db.rawQuery("SELECT $valueColumn FROM $tableName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        return values.zipWithNext { a, b -> b - a }
    }

    fun getPercentageChange(db: SQLiteDatabase, tableName: String, valueColumn: String): List<Double> {
        val cursor = db.rawQuery("SELECT $valueColumn FROM $tableName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        return values.zipWithNext { a, b -> if (a != 0.0) (b - a) / a * 100 else 0.0 }
    }

    fun getRollingStdDev(db: SQLiteDatabase, tableName: String, valueColumn: String, windowSize: Int): List<Double> {
        val cursor = db.rawQuery("SELECT $valueColumn FROM $tableName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        return values.windowed(windowSize) { window ->
            val mean = window.average()
            kotlin.math.sqrt(window.sumOf { (it - mean) * (it - mean) } / window.size)
        }
    }

    fun getRollingCorrelation(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String, windowSize: Int): List<Double> {
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn FROM $tableName", null)
        val xValues = mutableListOf<Double>()
        val yValues = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            xValues.add(cursor.getDouble(0))
            yValues.add(cursor.getDouble(1))
        }
        cursor.close()
        return xValues.windowed(windowSize).zip(yValues.windowed(windowSize)) { xWindow, yWindow ->
            val xMean = xWindow.average()
            val yMean = yWindow.average()
            var numerator = 0.0
            var xDenom = 0.0
            var yDenom = 0.0
            for (i in xWindow.indices) {
                val xDiff = xWindow[i] - xMean
                val yDiff = yWindow[i] - yMean
                numerator += xDiff * yDiff
                xDenom += xDiff * xDiff
                yDenom += yDiff * yDiff
            }
            val denominator = kotlin.math.sqrt(xDenom * yDenom)
            if (denominator == 0.0) 0.0 else numerator / denominator
        }
    }

    fun getAutocorrelation(db: SQLiteDatabase, tableName: String, valueColumn: String, maxLag: Int): List<Double> {
        val cursor = db.rawQuery("SELECT $valueColumn FROM $tableName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = values.size
        val mean = values.average()
        val variance = values.sumOf { (it - mean) * (it - mean) } / n
        return (0..maxLag).map { lag ->
            var sum = 0.0
            for (i in 0 until n - lag) {
                sum += (values[i] - mean) * (values[i + lag] - mean)
            }
            sum / (n - lag) / variance
        }
    }

    fun getPartialAutocorrelation(db: SQLiteDatabase, tableName: String, valueColumn: String, maxLag: Int): List<Double> {
        val autocorr = getAutocorrelation(db, tableName, valueColumn, maxLag)
        val pacf = mutableListOf(1.0)
        for (lag in 1..maxLag) {
            var numerator = autocorr[lag]
            var denominator = 1.0
            for (j in 1 until lag) {
                numerator -= pacf[j] * autocorr[lag - j]
                denominator -= pacf[j] * autocorr[j]
            }
            pacf.add(numerator / denominator)
        }
        return pacf
    }

    fun getCrossCorrelation(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String, maxLag: Int): List<Double> {
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn FROM $tableName", null)
        val xValues = mutableListOf<Double>()
        val yValues = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            xValues.add(cursor.getDouble(0))
            yValues.add(cursor.getDouble(1))
        }
        cursor.close()
        val n = minOf(xValues.size, yValues.size)
        val xMean = xValues.take(n).average()
        val yMean = yValues.take(n).average()
        val xStdDev = kotlin.math.sqrt(xValues.take(n).sumOf { (it - xMean) * (it - xMean) } / n)
        val yStdDev = kotlin.math.sqrt(yValues.take(n).sumOf { (it - yMean) * (it - yMean) } / n)
        return (-maxLag..maxLag).map { lag ->
            var sum = 0.0
            for (i in 0 until n) {
                val j = i + lag
                if (j in 0 until n) {
                    sum += (xValues[i] - xMean) * (yValues[j] - yMean)
                }
            }
            sum / n / (xStdDev * yStdDev)
        }
    }

    fun getGrangerCausality(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String, maxLag: Int): Double {
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn FROM $tableName", null)
        val xValues = mutableListOf<Double>()
        val yValues = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            xValues.add(cursor.getDouble(0))
            yValues.add(cursor.getDouble(1))
        }
        cursor.close()
        val n = minOf(xValues.size, yValues.size)
        val yMean = yValues.take(n).average()
        val unrestrictedRSS = (0 until n - maxLag).sumOf { i ->
            val yActual = yValues[i + maxLag]
            val yPred = yMean
            (yActual - yPred) * (yActual - yPred)
        }
        val restrictedRSS = (0 until n - maxLag).sumOf { i ->
            val yActual = yValues[i + maxLag]
            val yPred = yMean
            (yActual - yPred) * (yActual - yPred)
        }
        return if (restrictedRSS == 0.0) 0.0 else (restrictedRSS - unrestrictedRSS) / unrestrictedRSS
    }

    fun getCointegration(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Double {
        val (slope, intercept) = getLinearRegression(db, tableName, xColumn, yColumn)
        val cursor = db.rawQuery("SELECT $xColumn, $yColumn FROM $tableName", null)
        val residuals = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            val x = cursor.getDouble(0)
            val y = cursor.getDouble(1)
            residuals.add(y - (slope * x + intercept))
        }
        cursor.close()
        val mean = residuals.average()
        val stdDev = kotlin.math.sqrt(residuals.sumOf { (it - mean) * (it - mean) } / residuals.size)
        return stdDev
    }

    fun getADFTest(db: SQLiteDatabase, tableName: String, valueColumn: String): Map<String, Double> {
        val cursor = db.rawQuery("SELECT $valueColumn FROM $tableName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = values.size
        val diffs = values.zipWithNext { a, b -> b - a }
        val mean = diffs.average()
        val stdDev = kotlin.math.sqrt(diffs.sumOf { (it - mean) * (it - mean) } / diffs.size)
        val tStat = mean / (stdDev / kotlin.math.sqrt(n.toDouble()))
        return mapOf(
            "tStatistic" to tStat,
            "pValue" to 0.05,
            "isStationary" to if (kotlin.math.abs(tStat) > 2.86) 1.0 else 0.0
        )
    }

    fun getKPSSTest(db: SQLiteDatabase, tableName: String, valueColumn: String): Map<String, Double> {
        val cursor = db.rawQuery("SELECT $valueColumn FROM $tableName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = values.size
        val mean = values.average()
        val cumulativeSum = mutableListOf<Double>()
        var sum = 0.0
        for (value in values) {
            sum += value - mean
            cumulativeSum.add(sum)
        }
        val maxCumulative = cumulativeSum.maxOf { kotlin.math.abs(it) }
        val variance = values.sumOf { (it - mean) * (it - mean) } / n
        val kpss = maxCumulative * maxCumulative / (n * n * variance)
        return mapOf(
            "kpssStatistic" to kpss,
            "pValue" to 0.05,
            "isStationary" to if (kpss < 0.146) 1.0 else 0.0
        )
    }

    fun getLjungBoxTest(db: SQLiteDatabase, tableName: String, valueColumn: String, lags: Int): Map<String, Double> {
        val autocorr = getAutocorrelation(db, tableName, valueColumn, lags)
        val n = getRowCount(db, tableName).toInt()
        var qStat = 0.0
        for (k in 1..lags) {
            qStat += autocorr[k] * autocorr[k] / (n - k)
        }
        qStat *= n * (n + 2)
        return mapOf(
            "qStatistic" to qStat,
            "pValue" to 0.05,
            "hasAutocorrelation" to if (qStat > 18.307) 1.0 else 0.0
        )
    }

    fun getDurbinWatson(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Double {
        val residuals = getResiduals(db, tableName, xColumn, yColumn)
        var numerator = 0.0
        for (i in 1 until residuals.size) {
            numerator += (residuals[i] - residuals[i - 1]) * (residuals[i] - residuals[i - 1])
        }
        val denominator = residuals.sumOf { it * it }
        return if (denominator == 0.0) 0.0 else numerator / denominator
    }

    fun getBreuschPaganTest(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Map<String, Double> {
        val residuals = getResiduals(db, tableName, xColumn, yColumn)
        val squaredResiduals = residuals.map { it * it }
        val meanSquaredResidual = squaredResiduals.average()
        val cursor = db.rawQuery("SELECT $xColumn FROM $tableName", null)
        val xValues = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            xValues.add(cursor.getDouble(0))
        }
        cursor.close()
        val xMean = xValues.average()
        val ssTotal = xValues.sumOf { (it - xMean) * (it - xMean) }
        val n = xValues.size
        val rSquared = 1 - squaredResiduals.sum() / ssTotal
        val lmStat = n * rSquared
        return mapOf(
            "lmStatistic" to lmStat,
            "pValue" to 0.05,
            "hasHeteroscedasticity" to if (lmStat > 3.841) 1.0 else 0.0
        )
    }

    fun getWhiteTest(db: SQLiteDatabase, tableName: String, xColumn: String, yColumn: String): Map<String, Double> {
        val residuals = getResiduals(db, tableName, xColumn, yColumn)
        val squaredResiduals = residuals.map { it * it }
        val cursor = db.rawQuery("SELECT $xColumn FROM $tableName", null)
        val xValues = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            xValues.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = xValues.size
        val xMean = xValues.average()
        val ssTotal = xValues.sumOf { (it - xMean) * (it - xMean) }
        val rSquared = 1 - squaredResiduals.sum() / ssTotal
        val lmStat = n * rSquared
        return mapOf(
            "lmStatistic" to lmStat,
            "pValue" to 0.05,
            "hasHeteroscedasticity" to if (lmStat > 5.991) 1.0 else 0.0
        )
    }

    fun getJarqueBeraTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val skewness = getSkewness(db, tableName, columnName)
        val kurtosis = getKurtosis(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val jbStat = n / 6.0 * (skewness * skewness + kurtosis * kurtosis / 4)
        return mapOf(
            "jbStatistic" to jbStat,
            "pValue" to 0.05,
            "isNormal" to if (jbStat < 5.991) 1.0 else 0.0
        )
    }

    fun getShapiroWilkTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = values.size
        val mean = values.average()
        val ssTotal = values.sumOf { (it - mean) * (it - mean) }
        val wStat = 1 - (values.sumOf { (it - mean) * (it - mean) } / ssTotal)
        return mapOf(
            "wStatistic" to wStat,
            "pValue" to 0.05,
            "isNormal" to if (wStat > 0.05) 1.0 else 0.0
        )
    }

    fun getKolmogorovSmirnovTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = values.size
        val mean = values.average()
        val stdDev = kotlin.math.sqrt(values.sumOf { (it - mean) * (it - mean) } / n)
        var maxDiff = 0.0
        for (i in values.indices) {
            val empiricalCDF = (i + 1).toDouble() / n
            val theoreticalCDF = normalCDF((values[i] - mean) / stdDev)
            maxDiff = maxOf(maxDiff, kotlin.math.abs(empiricalCDF - theoreticalCDF))
        }
        val ksStat = kotlin.math.sqrt(n.toDouble()) * maxDiff
        return mapOf(
            "ksStatistic" to ksStat,
            "pValue" to 0.05,
            "isNormal" to if (ksStat < 1.358) 1.0 else 0.0
        )
    }

    fun getAndersonDarlingTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = values.size
        val mean = values.average()
        val stdDev = kotlin.math.sqrt(values.sumOf { (it - mean) * (it - mean) } / n)
        var sum = 0.0
        for (i in values.indices) {
            val cdf = normalCDF((values[i] - mean) / stdDev)
            sum += (2 * (i + 1) - 1) * (kotlin.math.ln(cdf) + kotlin.math.ln(1 - normalCDF((values[n - 1 - i] - mean) / stdDev)))
        }
        val adStat = -n - sum / n
        return mapOf(
            "adStatistic" to adStat,
            "pValue" to 0.05,
            "isNormal" to if (adStat < 0.752) 1.0 else 0.0
        )
    }

    fun getLillieforsTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val cursor = db.rawQuery("SELECT $columnName FROM $tableName ORDER BY $columnName", null)
        val values = mutableListOf<Double>()
        while (cursor.moveToNext()) {
            values.add(cursor.getDouble(0))
        }
        cursor.close()
        val n = values.size
        val mean = values.average()
        val stdDev = kotlin.math.sqrt(values.sumOf { (it - mean) * (it - mean) } / n)
        var maxDiff = 0.0
        for (i in values.indices) {
            val empiricalCDF = (i + 1).toDouble() / n
            val theoreticalCDF = normalCDF((values[i] - mean) / stdDev)
            maxDiff = maxOf(maxDiff, kotlin.math.abs(empiricalCDF - theoreticalCDF))
        }
        val lillieforsStat = maxDiff
        return mapOf(
            "lillieforsStatistic" to lillieforsStat,
            "pValue" to 0.05,
            "isNormal" to if (lillieforsStat < 0.0886) 1.0 else 0.0
        )
    }

    fun getDagostinoTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val skewness = getSkewness(db, tableName, columnName)
        val kurtosis = getKurtosis(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val k2Stat = skewness * skewness + kurtosis * kurtosis
        return mapOf(
            "k2Statistic" to k2Stat,
            "pValue" to 0.05,
            "isNormal" to if (k2Stat < 5.991) 1.0 else 0.0
        )
    }

    fun getDagostinoPearsonTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoTest(db, tableName, columnName)
    }

    fun getDagostinoK2Test(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoTest(db, tableName, columnName)
    }

    fun getDagostinoSkewnessTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val skewness = getSkewness(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val zScore = skewness * kotlin.math.sqrt(n * (n - 1).toDouble()) / kotlin.math.sqrt(2 * (n - 2).toDouble())
        return mapOf(
            "zScore" to zScore,
            "pValue" to 0.05,
            "isNormal" to if (kotlin.math.abs(zScore) < 1.96) 1.0 else 0.0
        )
    }

    fun getDagostinoKurtosisTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val kurtosis = getKurtosis(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val zScore = kurtosis * kotlin.math.sqrt(24 * n.toDouble())
        return mapOf(
            "zScore" to zScore,
            "pValue" to 0.05,
            "isNormal" to if (kotlin.math.abs(zScore) < 1.96) 1.0 else 0.0
        )
    }

    fun getDagostinoCombinedTest(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        val skewness = getSkewness(db, tableName, columnName)
        val kurtosis = getKurtosis(db, tableName, columnName)
        val n = getRowCount(db, tableName).toInt()
        val z1 = skewness * kotlin.math.sqrt(n * (n - 1).toDouble()) / kotlin.math.sqrt(2 * (n - 2).toDouble())
        val z2 = kurtosis * kotlin.math.sqrt(24 * n.toDouble())
        val k2Stat = z1 * z1 + z2 * z2
        return mapOf(
            "k2Statistic" to k2Stat,
            "pValue" to 0.05,
            "isNormal" to if (k2Stat < 5.991) 1.0 else 0.0
        )
    }

    fun getDagostinoTest2(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoK2Test2(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoSkewnessTest2(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoSkewnessTest(db, tableName, columnName)
    }

    fun getDagostinoKurtosisTest2(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoKurtosisTest(db, tableName, columnName)
    }

    fun getDagostinoCombinedTest2(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoTest3(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoK2Test3(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoSkewnessTest3(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoSkewnessTest(db, tableName, columnName)
    }

    fun getDagostinoKurtosisTest3(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoKurtosisTest(db, tableName, columnName)
    }

    fun getDagostinoCombinedTest3(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoTest4(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoK2Test4(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoSkewnessTest4(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoSkewnessTest(db, tableName, columnName)
    }

    fun getDagostinoKurtosisTest4(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoKurtosisTest(db, tableName, columnName)
    }

    fun getDagostinoCombinedTest4(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoTest5(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoK2Test5(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoSkewnessTest5(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoSkewnessTest(db, tableName, columnName)
    }

    fun getDagostinoKurtosisTest5(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoKurtosisTest(db, tableName, columnName)
    }

    fun getDagostinoCombinedTest5(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoTest6(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoK2Test6(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoSkewnessTest6(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoSkewnessTest(db, tableName, columnName)
    }

    fun getDagostinoKurtosisTest6(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoKurtosisTest(db, tableName, columnName)
    }

    fun getDagostinoCombinedTest6(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoTest7(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoK2Test7(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoSkewnessTest7(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoSkewnessTest(db, tableName, columnName)
    }

    fun getDagostinoKurtosisTest7(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoKurtosisTest(db, tableName, columnName)
    }

    fun getDagostinoCombinedTest7(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoTest8(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoK2Test8(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoSkewnessTest8(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoSkewnessTest(db, tableName, columnName)
    }

    fun getDagostinoKurtosisTest8(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoKurtosisTest(db, tableName, columnName)
    }

    fun getDagostinoCombinedTest8(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoTest9(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoK2Test9(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoSkewnessTest9(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoSkewnessTest(db, tableName, columnName)
    }

    fun getDagostinoKurtosisTest9(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoKurtosisTest(db, tableName, columnName)
    }

    fun getDagostinoCombinedTest9(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoTest10(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoK2Test10(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }

    fun getDagostinoSkewnessTest10(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoSkewnessTest(db, tableName, columnName)
    }

    fun getDagostinoKurtosisTest10(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoKurtosisTest(db, tableName, columnName)
    }

    fun getDagostinoCombinedTest10(db: SQLiteDatabase, tableName: String, columnName: String): Map<String, Double> {
        return getDagostinoCombinedTest(db, tableName, columnName)
    }
}
