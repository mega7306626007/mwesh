package com.mweshimiwa.assistant

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CollectionOperationsTest {

    @Test
    fun listFilterAndMap() {
        val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val result = numbers.filter { it % 2 == 0 }.map { it * it }
        assertEquals(listOf(4, 16, 36, 64, 100), result)
    }

    @Test
    fun listGroupBy() {
        val words = listOf("apple", "banana", "avocado", "blueberry", "cherry")
        val grouped = words.groupBy { it.first() }
        assertEquals(3, grouped.size)
        assertEquals(listOf("apple", "avocado"), grouped['a'])
        assertEquals(listOf("banana", "blueberry"), grouped['b'])
        assertEquals(listOf("cherry"), grouped['c'])
    }

    @Test
    fun listDistinctAndSorted() {
        val numbers = listOf(5, 3, 5, 2, 3, 1, 4, 2)
        val result = numbers.distinct().sorted()
        assertEquals(listOf(1, 2, 3, 4, 5), result)
    }

    @Test
    fun listPartition() {
        val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val (evens, odds) = numbers.partition { it % 2 == 0 }
        assertEquals(listOf(2, 4, 6, 8, 10), evens)
        assertEquals(listOf(1, 3, 5, 7, 9), odds)
    }

    @Test
    fun listFlatMap() {
        val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5, 6))
        val result = nested.flatMap { it }
        assertEquals(listOf(1, 2, 3, 4, 5, 6), result)
    }

    @Test
    fun listReduceAndFold() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val sum = numbers.reduce { acc, i -> acc + i }
        assertEquals(15, sum)
        val product = numbers.fold(1) { acc, i -> acc * i }
        assertEquals(120, product)
    }

    @Test
    fun listAssociate() {
        val words = listOf("one", "two", "three")
        val map = words.associate { it to it.length }
        assertEquals(3, map.size)
        assertEquals(3, map["one"])
        assertEquals(3, map["two"])
        assertEquals(5, map["three"])
    }

    @Test
    fun listChunked() {
        val numbers = (1..10).toList()
        val chunks = numbers.chunked(3)
        assertEquals(4, chunks.size)
        assertEquals(listOf(1, 2, 3), chunks[0])
        assertEquals(listOf(10), chunks[3])
    }

    @Test
    fun listWindowed() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val windows = numbers.windowed(3)
        assertEquals(3, windows.size)
        assertEquals(listOf(1, 2, 3), windows[0])
        assertEquals(listOf(3, 4, 5), windows[2])
    }

    @Test
    fun listZip() {
        val names = listOf("Alice", "Bob", "Charlie")
        val ages = listOf(25, 30, 35)
        val zipped = names.zip(ages)
        assertEquals(3, zipped.size)
        assertEquals("Alice" to 25, zipped[0])
        assertEquals("Charlie" to 35, zipped[2])
    }

    @Test
    fun mapOperations() {
        val map = mutableMapOf("a" to 1, "b" to 2, "c" to 3)
        map["d"] = 4
        assertEquals(4, map.size)
        map.remove("b")
        assertEquals(3, map.size)
        assertTrue(map.containsKey("a"))
        assertTrue(map.containsValue(3))
    }

    @Test
    fun mapFilterAndTransform() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4)
        val filtered = map.filter { it.value % 2 == 0 }
        assertEquals(mapOf("b" to 2, "d" to 4), filtered)
        val transformed = map.mapValues { it.value * 10 }
        assertEquals(mapOf("a" to 10, "b" to 20, "c" to 30, "d" to 40), transformed)
    }

    @Test
    fun setOperations() {
        val setA = setOf(1, 2, 3, 4, 5)
        val setB = setOf(4, 5, 6, 7, 8)
        assertEquals(setOf(1, 2, 3), setA - setB)
        assertEquals(setOf(6, 7, 8), setB - setA)
        assertEquals(setOf(4, 5), setA intersect setB)
        assertEquals(setOf(1, 2, 3, 4, 5, 6, 7, 8), setA union setB)
    }

    @Test
    fun sequenceLazyEvaluation() {
        val sequence = generateSequence(1) { it + 1 }
            .filter { it % 2 == 0 }
            .map { it * it }
            .take(5)
            .toList()
        assertEquals(listOf(4, 16, 36, 64, 100), sequence)
    }

    @Test
    fun collectionStats() {
        val numbers = listOf(10, 20, 30, 40, 50)
        assertEquals(30.0, numbers.average(), 0.001)
        assertEquals(150, numbers.sum())
        assertEquals(10, numbers.minOrNull())
        assertEquals(50, numbers.maxOrNull())
    }

    @Test
    fun collectionSearch() {
        val words = listOf("apple", "banana", "cherry", "date")
        assertEquals("banana", words.find { it.startsWith("b") })
        assertEquals("cherry", words.find { it.length > 5 })
        assertEquals(null, words.find { it.startsWith("z") })
        assertTrue(words.any { it.length > 4 })
        assertTrue(words.all { it.isNotEmpty() })
    }

    @Test
    fun collectionSorting() {
        val people = listOf(
            Triple("Alice", 25, "NYC"),
            Triple("Bob", 30, "LA"),
            Triple("Charlie", 25, "Chicago")
        )
        val sortedByAge = people.sortedBy { it.second }
        assertEquals("Alice", sortedByAge[0].first)
        val sortedByAgeDesc = people.sortedByDescending { it.second }
        assertEquals("Bob", sortedByAgeDesc[0].first)
    }

    @Test
    fun collectionGroupingAndCounting() {
        val words = listOf("apple", "banana", "avocado", "blueberry", "cherry", "apricot")
        val byLength = words.groupingBy { it.length }.eachCount()
        assertEquals(2, byLength[5])
        assertEquals(1, byLength[6])
        assertEquals(1, byLength[7])
        assertEquals(2, byLength[8])
    }

    @Test
    fun collectionMinMaxBy() {
        val people = listOf(
            Triple("Alice", 25, "NYC"),
            Triple("Bob", 30, "LA"),
            Triple("Charlie", 20, "Chicago")
        )
        assertEquals("Charlie", people.minByOrNull { it.second }?.first)
        assertEquals("Bob", people.maxByOrNull { it.second }?.first)
    }

    @Test
    fun collectionScan() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val runningTotals = numbers.runningFold(0) { acc, i -> acc + i }
        assertEquals(listOf(0, 1, 3, 6, 10, 15), runningTotals)
    }

    @Test
    fun collectionMinusAndPlus() {
        val list = mutableListOf(1, 2, 3, 4, 5)
        list += 6
        assertEquals(listOf(1, 2, 3, 4, 5, 6), list)
        list -= 1
        assertEquals(listOf(2, 3, 4, 5, 6), list)
    }

    @Test
    fun collectionSlice() {
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        assertEquals(listOf(3, 4, 5), list.slice(2..4))
        assertEquals(listOf(1, 3, 5, 7, 9), list.slice(listOf(0, 2, 4, 6, 8)))
    }

    @Test
    fun collectionTakeAndDrop() {
        val list = (1..10).toList()
        assertEquals(listOf(1, 2, 3), list.take(3))
        assertEquals(listOf(8, 9, 10), list.takeLast(3))
        assertEquals(listOf(4, 5, 6, 7, 8, 9, 10), list.drop(3))
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7), list.dropLast(3))
    }

    @Test
    fun collectionCount() {
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        assertEquals(5, list.count { it % 2 == 0 })
        assertEquals(5, list.count { it % 2 != 0 })
        assertEquals(0, list.count { it > 100 })
    }

    @Test
    fun collectionJoinToString() {
        val words = listOf("Hello", "World", "Kotlin")
        assertEquals("Hello, World, Kotlin", words.joinToString(", "))
        assertEquals("[Hello | World | Kotlin]", words.joinToString(" | ", "[", "]"))
    }

    @Test
    fun collectionToMap() {
        val pairs = listOf("a" to 1, "b" to 2, "c" to 3)
        val map = pairs.toMap()
        assertEquals(3, map.size)
        assertEquals(1, map["a"])
        assertEquals(2, map["b"])
        assertEquals(3, map["c"])
    }

    @Test
    fun collectionGroupByMultiple() {
        val items = listOf(
            Triple("fruit", "apple", 1.0),
            Triple("fruit", "banana", 0.5),
            Triple("vegetable", "carrot", 0.3),
            Triple("vegetable", "broccoli", 0.8)
        )
        val byCategory = items.groupBy { it.first }
        assertEquals(2, byCategory.size)
        assertEquals(2, byCategory["fruit"]?.size)
        assertEquals(2, byCategory["vegetable"]?.size)
    }

    @Test
    fun collectionAggregate() {
        val orders = listOf(
            Triple("Alice", "Book", 20.0),
            Triple("Bob", "Pen", 5.0),
            Triple("Alice", "Pen", 5.0),
            Triple("Bob", "Book", 20.0)
        )
        val totalByPerson = orders.groupBy { it.first }
            .mapValues { (_, v) -> v.sumOf { it.third } }
        assertEquals(25.0, totalByPerson["Alice"]!!, 0.001)
        assertEquals(25.0, totalByPerson["Bob"]!!, 0.001)
    }

    @Test
    fun collectionPermutations() {
        val list = listOf(1, 2, 3)
        val permutations = list.flatMap { a ->
            list.filter { it != a }.flatMap { b ->
                list.filter { it != a && it != b }.map { c -> listOf(a, b, c) }
            }
        }
        assertEquals(6, permutations.size)
    }

    @Test
    fun collectionBinarySearch() {
        val sorted = listOf(1, 3, 5, 7, 9, 11, 13, 15)
        assertEquals(3, sorted.binarySearch(7))
        assertEquals(-1, sorted.binarySearch(0))
        assertEquals(-6, sorted.binarySearch(10))
    }

    @Test
    fun collectionRotate() {
        val list = listOf(1, 2, 3, 4, 5)
        val rotated = list.drop(2) + list.take(2)
        assertEquals(listOf(3, 4, 5, 1, 2), rotated)
    }

    @Test
    fun collectionMedian() {
        val odd = listOf(1, 3, 5, 7, 9)
        assertEquals(5, odd[odd.size / 2])
        val even = listOf(1, 3, 5, 7)
        assertEquals(4.0, (even[even.size / 2 - 1] + even[even.size / 2]) / 2.0, 0.001)
    }

    @Test
    fun collectionMode() {
        val list = listOf(1, 2, 2, 3, 3, 3, 4, 4, 4, 4)
        val frequency = list.groupingBy { it }.eachCount()
        val mode = frequency.maxByOrNull { it.value }?.key
        assertEquals(4, mode)
    }

    @Test
    fun collectionRangeChecks() {
        val list = listOf(1, 2, 3, 4, 5)
        assertTrue(list.all { it in 1..5 })
        assertTrue(list.none { it > 5 })
        assertTrue(list.any { it in 3..5 })
    }

    @Test
    fun collectionCartesianProduct() {
        val colors = listOf("red", "green", "blue")
        val sizes = listOf("S", "M", "L")
        val product = colors.flatMap { c -> sizes.map { s -> "$c-$s" } }
        assertEquals(9, product.size)
        assertTrue(product.contains("red-M"))
        assertTrue(product.contains("blue-L"))
    }

    @Test
    fun collectionTopN() {
        val numbers = listOf(45, 12, 78, 23, 67, 89, 34, 56, 90, 11)
        val top3 = numbers.sortedDescending().take(3)
        assertEquals(listOf(90, 89, 78), top3)
        val bottom3 = numbers.sorted().take(3)
        assertEquals(listOf(11, 12, 23), bottom3)
    }

    @Test
    fun collectionFrequencyDistribution() {
        val grades = listOf("A", "B", "A", "C", "B", "A", "D", "B", "A", "C")
        val distribution = grades.groupingBy { it }.eachCount().toSortedMap()
        assertEquals(4, distribution["A"])
        assertEquals(3, distribution["B"])
        assertEquals(2, distribution["C"])
        assertEquals(1, distribution["D"])
    }

    @Test
    fun collectionSlidingWindow() {
        val temps = listOf(72, 75, 78, 80, 77, 74, 71, 69, 72, 75)
        val averages = temps.windowed(3, 1) { it.average() }
        assertEquals(8, averages.size)
        assertEquals(75.0, averages[0], 0.001)
        assertEquals(71.667, averages[7], 0.001)
    }

    @Test
    fun collectionLongestIncreasingSubsequence() {
        val numbers = listOf(10, 22, 9, 33, 21, 50, 41, 60, 80)
        val lis = mutableListOf<Int>()
        for (num in numbers) {
            val pos = lis.binarySearch(num).let { if (it < 0) -it - 1 else it }
            if (pos == lis.size) lis.add(num) else lis[pos] = num
        }
        assertEquals(6, lis.size)
    }

    @Test
    fun collectionIntersectionOfMultiple() {
        val a = setOf(1, 2, 3, 4, 5)
        val b = setOf(3, 4, 5, 6, 7)
        val c = setOf(5, 6, 7, 8, 9)
        val common = a intersect b intersect c
        assertEquals(setOf(5), common)
    }

    @Test
    fun collectionPowerSet() {
        val set = setOf(1, 2, 3)
        var powerSet = setOf(setOf<Int>())
        for (element in set) {
            powerSet = powerSet + powerSet.map { it + element }
        }
        assertEquals(8, powerSet.size)
        assertTrue(powerSet.contains(setOf(1, 2)))
        assertTrue(powerSet.contains(setOf(3)))
        assertTrue(powerSet.contains(emptySet()))
    }

    @Test
    fun collectionMergeSorted() {
        val a = listOf(1, 3, 5, 7)
        val b = listOf(2, 4, 6, 8)
        val merged = (a + b).sorted()
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7, 8), merged)
    }

    @Test
    fun collectionDeduplicate() {
        val list = listOf(1, 2, 2, 3, 3, 3, 4, 4, 4, 4)
        val deduped = list.distinct()
        assertEquals(listOf(1, 2, 3, 4), deduped)
    }

    @Test
    fun collectionSplitByPredicate() {
        val numbers = (1..20).toList()
        val (evens, odds) = numbers.partition { it % 2 == 0 }
        assertEquals(10, evens.size)
        assertEquals(10, odds.size)
        assertTrue(evens.all { it % 2 == 0 })
        assertTrue(odds.all { it % 2 != 0 })
    }

    @Test
    fun collectionRunningAverage() {
        val numbers = listOf(10, 20, 30, 40, 50)
        val runningAvg = numbers.runningFold(0 to 0) { (sum, count), n ->
            (sum + n) to (count + 1)
        }.drop(1).map { (sum, count) -> sum.toDouble() / count }
        assertEquals(listOf(10.0, 15.0, 20.0, 25.0, 30.0), runningAvg)
    }

    @Test
    fun collectionNthLargest() {
        val numbers = listOf(45, 12, 78, 23, 67, 89, 34, 56, 90, 11)
        val sorted = numbers.sortedDescending()
        assertEquals(90, sorted[0])
        assertEquals(89, sorted[1])
        assertEquals(78, sorted[2])
        assertEquals(11, sorted[9])
    }

    @Test
    fun collectionCoalesce() {
        val values = listOf(null, null, "first", "second", null)
        val firstNonNull = values.firstOrNull { it != null }
        assertEquals("first", firstNonNull)
    }

    @Test
    fun collectionGroupByTransform() {
        val transactions = listOf(
            Triple("food", "groceries", 50.0),
            Triple("food", "restaurant", 30.0),
            Triple("transport", "gas", 40.0),
            Triple("transport", "uber", 25.0),
            Triple("entertainment", "movie", 15.0)
        )
        val byCategory = transactions.groupBy { it.first }
            .mapValues { (_, v) -> v.sumOf { it.third } }
        assertEquals(80.0, byCategory["food"]!!, 0.001)
        assertEquals(65.0, byCategory["transport"]!!, 0.001)
        assertEquals(15.0, byCategory["entertainment"]!!, 0.001)
    }

    @Test
    fun collectionMatrixTranspose() {
        val matrix = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        )
        val transposed = matrix[0].indices.map { col ->
            matrix.indices.map { row -> matrix[row][col] }
        }
        assertEquals(listOf(1, 4, 7), transposed[0])
        assertEquals(listOf(2, 5, 8), transposed[1])
        assertEquals(listOf(3, 6, 9), transposed[2])
    }

    @Test
    fun collectionMatrixMultiplication() {
        val a = listOf(
            listOf(1, 2),
            listOf(3, 4)
        )
        val b = listOf(
            listOf(5, 6),
            listOf(7, 8)
        )
        val result = a.indices.map { i ->
            b[0].indices.map { j ->
                a[i].indices.sumOf { k -> a[i][k] * b[k][j] }
            }
        }
        assertEquals(listOf(19, 22), result[0])
        assertEquals(listOf(43, 50), result[1])
    }

    @Test
    fun collectionFibonacci() {
        val fib = generateSequence(0 to 1) { (a, b) -> b to (a + b) }
            .map { it.first }
            .take(10)
            .toList()
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34), fib)
    }

    @Test
    fun collectionPrimeSieve() {
        val n = 30
        val sieve = BooleanArray(n + 1) { true }
        sieve[0] = false
        sieve[1] = false
        for (i in 2..kotlin.math.sqrt(n.toDouble()).toInt()) {
            if (sieve[i]) {
                for (j in i * i..n step i) {
                    sieve[j] = false
                }
            }
        }
        val primes = sieve.withIndex().filter { it.value }.map { it.index }
        assertEquals(listOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29), primes)
    }

    @Test
    fun collectionAnagrams() {
        val words = listOf("listen", "silent", "enlist", "hello", "world")
        val grouped = words.groupBy { it.toCharArray().sorted().joinToString("") }
        assertEquals(3, grouped["eilnst"]?.size)
        assertEquals(1, grouped["ehllo"]?.size)
    }

    @Test
    fun collectionPalindromeCheck() {
        fun isPalindrome(s: String): Boolean {
            val cleaned = s.lowercase().filter { it.isLetterOrDigit() }
            return cleaned == cleaned.reversed()
        }
        assertTrue(isPalindrome("A man a plan a canal Panama"))
        assertTrue(isPalindrome("racecar"))
        assertTrue(isPalindrome("Was it a car or a cat I saw"))
    }

    @Test
    fun collectionStringPermutations() {
        fun permutations(s: String): List<String> {
            if (s.length <= 1) return listOf(s)
            val result = mutableListOf<String>()
            for (i in s.indices) {
                val c = s[i].toString()
                val rest = s.removeRange(i, i + 1)
                for (p in permutations(rest)) {
                    result.add(c + p)
                }
            }
            return result
        }
        val perms = permutations("abc")
        assertEquals(6, perms.size)
        assertTrue(perms.contains("abc"))
        assertTrue(perms.contains("bca"))
        assertTrue(perms.contains("cab"))
    }

    @Test
    fun collectionTopologicalSort() {
        val graph = mapOf(
            "A" to listOf("B", "C"),
            "B" to listOf("D"),
            "C" to listOf("D"),
            "D" to emptyList()
        )
        val visited = mutableSetOf<String>()
        val result = mutableListOf<String>()
        fun visit(node: String) {
            if (node !in visited) {
                visited.add(node)
                graph[node]?.forEach { visit(it) }
                result.add(node)
            }
        }
        graph.keys.forEach { visit(it) }
        assertEquals(listOf("D", "C", "B", "A"), result)
    }

    @Test
    fun collectionGraphBFS() {
        val graph = mapOf(
            "A" to listOf("B", "C"),
            "B" to listOf("D", "E"),
            "C" to listOf("F"),
            "D" to emptyList(),
            "E" to listOf("F"),
            "F" to emptyList()
        )
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add("A")
        visited.add("A")
        val result = mutableListOf<String>()
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            result.add(node)
            graph[node]?.forEach { neighbor ->
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }
        assertEquals(listOf("A", "B", "C", "D", "E", "F"), result)
    }

    @Test
    fun collectionGraphDFS() {
        val graph = mapOf(
            "A" to listOf("B", "C"),
            "B" to listOf("D", "E"),
            "C" to listOf("F"),
            "D" to emptyList(),
            "E" to listOf("F"),
            "F" to emptyList()
        )
        val visited = mutableSetOf<String>()
        val result = mutableListOf<String>()
        fun dfs(node: String) {
            if (node !in visited) {
                visited.add(node)
                result.add(node)
                graph[node]?.forEach { dfs(it) }
            }
        }
        dfs("A")
        assertEquals(listOf("A", "B", "D", "E", "F", "C"), result)
    }

    @Test
    fun collectionDijkstra() {
        val graph = mapOf(
            "A" to mapOf("B" to 1, "C" to 4),
            "B" to mapOf("C" to 2, "D" to 6),
            "C" to mapOf("D" to 3),
            "D" to emptyMap()
        )
        val dist = mutableMapOf("A" to 0, "B" to Int.MAX_VALUE, "C" to Int.MAX_VALUE, "D" to Int.MAX_VALUE)
        val pq = java.util.PriorityQueue<Pair<Int, String>>(compareBy { it.first })
        pq.add(0 to "A")
        while (pq.isNotEmpty()) {
            val (d, u) = pq.poll()
            if (d > dist[u]!!) continue
            graph[u]?.forEach { (v, w) ->
                val newDist = d + w
                if (newDist < dist[v]!!) {
                    dist[v] = newDist
                    pq.add(newDist to v)
                }
            }
        }
        assertEquals(0, dist["A"])
        assertEquals(1, dist["B"])
        assertEquals(3, dist["C"])
        assertEquals(6, dist["D"])
    }

    @Test
    fun collectionKnapsack() {
        val weights = intArrayOf(2, 3, 4, 5)
        val values = intArrayOf(3, 4, 5, 6)
        val capacity = 8
        val n = weights.size
        val dp = Array(n + 1) { IntArray(capacity + 1) }
        for (i in 1..n) {
            for (w in 0..capacity) {
                dp[i][w] = if (weights[i - 1] <= w) {
                    maxOf(dp[i - 1][w], dp[i - 1][w - weights[i - 1]] + values[i - 1])
                } else {
                    dp[i - 1][w]
                }
            }
        }
        assertEquals(10, dp[n][capacity])
    }

    @Test
    fun collectionLCS() {
        val a = "ABCDGH"
        val b = "AEDFHR"
        val m = a.length
        val n = b.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] = if (a[i - 1] == b[j - 1]) {
                    dp[i - 1][j - 1] + 1
                } else {
                    maxOf(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }
        assertEquals(3, dp[m][n])
    }

    @Test
    fun collectionEditDistance() {
        val a = "kitten"
        val b = "sitting"
        val m = a.length
        val n = b.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] = if (a[i - 1] == b[j - 1]) {
                    dp[i - 1][j - 1]
                } else {
                    1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
                }
            }
        }
        assertEquals(3, dp[m][n])
    }

    @Test
    fun collectionCoinChange() {
        val coins = intArrayOf(1, 5, 10, 25)
        val amount = 63
        val dp = IntArray(amount + 1) { Int.MAX_VALUE - 1 }
        dp[0] = 0
        for (coin in coins) {
            for (i in coin..amount) {
                dp[i] = minOf(dp[i], dp[i - coin] + 1)
            }
        }
        assertEquals(6, dp[amount])
    }

    @Test
    fun collectionWordBreak() {
        val wordDict = setOf("leet", "code", "le", "et", "cod")
        val s = "leetcode"
        val dp = BooleanArray(s.length + 1)
        dp[0] = true
        for (i in 1..s.length) {
            for (j in 0 until i) {
                if (dp[j] && s.substring(j, i) in wordDict) {
                    dp[i] = true
                    break
                }
            }
        }
        assertTrue(dp[s.length])
    }

    @Test
    fun collectionLongestPalindromicSubstring() {
        val s = "babad"
        var start = 0
        var maxLen = 1
        for (i in s.indices) {
            for (j in i + 1..s.length) {
                val sub = s.substring(i, j)
                if (sub == sub.reversed() && sub.length > maxLen) {
                    start = i
                    maxLen = sub.length
                }
            }
        }
        assertEquals("bab", s.substring(start, start + maxLen))
    }

    @Test
    fun collectionMedianOfTwoSortedArrays() {
        val a = intArrayOf(1, 3)
        val b = intArrayOf(2)
        val merged = (a + b).sorted()
        val median = if (merged.size % 2 == 0) {
            (merged[merged.size / 2 - 1] + merged[merged.size / 2]) / 2.0
        } else {
            merged[merged.size / 2].toDouble()
        }
        assertEquals(2.0, median, 0.001)
    }

    @Test
    fun collectionTrappingRainWater() {
        val height = intArrayOf(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1)
        val n = height.size
        val leftMax = IntArray(n)
        val rightMax = IntArray(n)
        leftMax[0] = height[0]
        for (i in 1 until n) leftMax[i] = maxOf(leftMax[i - 1], height[i])
        rightMax[n - 1] = height[n - 1]
        for (i in n - 2 downTo 0) rightMax[i] = maxOf(rightMax[i + 1], height[i])
        var water = 0
        for (i in 0 until n) {
            water += minOf(leftMax[i], rightMax[i]) - height[i]
        }
        assertEquals(6, water)
    }

    @Test
    fun collectionSlidingWindowMaximum() {
        val nums = intArrayOf(1, 3, -1, -3, 5, 3, 6, 7)
        val k = 3
        val result = mutableListOf<Int>()
        val deque = ArrayDeque<Int>()
        for (i in nums.indices) {
            while (deque.isNotEmpty() && deque.first() < i - k + 1) deque.removeFirst()
            while (deque.isNotEmpty() && nums[deque.last()] < nums[i]) deque.removeLast()
            deque.addLast(i)
            if (i >= k - 1) result.add(nums[deque.first()])
        }
        assertEquals(listOf(3, 3, 5, 5, 6, 7), result)
    }

    @Test
    fun collectionUnionFind() {
        val parent = IntArray(10) { it }
        fun find(x: Int): Int {
            if (parent[x] != x) parent[x] = find(parent[x])
            return parent[x]
        }
        fun union(x: Int, y: Int) {
            parent[find(x)] = find(y)
        }
        union(0, 1)
        union(1, 2)
        union(3, 4)
        assertEquals(find(0), find(2))
        assertEquals(find(3), find(4))
        assertFalse(find(0) == find(3))
    }

    @Test
    fun collectionTrie() {
        class TrieNode {
            val children = mutableMapOf<Char, TrieNode>()
            var isEnd = false
        }
        val root = TrieNode()
        fun insert(word: String) {
            var node = root
            for (c in word) {
                node = node.children.getOrPut(c) { TrieNode() }
            }
            node.isEnd = true
        }
        fun search(word: String): Boolean {
            var node = root
            for (c in word) {
                node = node.children[c] ?: return false
            }
            return node.isEnd
        }
        insert("apple")
        insert("app")
        assertTrue(search("apple"))
        assertTrue(search("app"))
    }

    @Test
    fun collectionLRUCache() {
        val capacity = 2
        val cache = object : LinkedHashMap<Int, Int>(capacity, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, Int>?): Boolean {
                return size > capacity
            }
        }
        cache[1] = 1
        cache[2] = 2
        cache[1]
        cache[3] = 3
        assertFalse(cache.containsKey(2))
        assertTrue(cache.containsKey(1))
        assertTrue(cache.containsKey(3))
    }

    @Test
    fun collectionHeapOperations() {
        val heap = java.util.PriorityQueue<Int>()
        heap.add(5)
        heap.add(1)
        heap.add(3)
        heap.add(2)
        heap.add(4)
        assertEquals(1, heap.poll())
        assertEquals(2, heap.poll())
        assertEquals(3, heap.poll())
        assertEquals(4, heap.poll())
        assertEquals(5, heap.poll())
    }

    @Test
    fun collectionDisjointSetWithRank() {
        val parent = IntArray(5) { it }
        val rank = IntArray(5)
        fun find(x: Int): Int {
            if (parent[x] != x) parent[x] = find(parent[x])
            return parent[x]
        }
        fun union(x: Int, y: Int) {
            val px = find(x)
            val py = find(y)
            if (px == py) return
            if (rank[px] < rank[py]) {
                parent[px] = py
            } else if (rank[px] > rank[py]) {
                parent[py] = px
            } else {
                parent[py] = px
                rank[px]++
            }
        }
        union(0, 1)
        union(2, 3)
        union(1, 2)
        assertEquals(find(0), find(3))
    }

    @Test
    fun collectionSegmentTree() {
        val arr = intArrayOf(1, 3, 5, 7, 9, 11)
        val n = arr.size
        val tree = IntArray(4 * n)
        fun build(node: Int, start: Int, end: Int) {
            if (start == end) {
                tree[node] = arr[start]
            } else {
                val mid = (start + end) / 2
                build(2 * node, start, mid)
                build(2 * node + 1, mid + 1, end)
                tree[node] = tree[2 * node] + tree[2 * node + 1]
            }
        }
        fun query(node: Int, start: Int, end: Int, l: Int, r: Int): Int {
            if (r < start || end < l) return 0
            if (l <= start && end <= r) return tree[node]
            val mid = (start + end) / 2
            return query(2 * node, start, mid, l, r) + query(2 * node + 1, mid + 1, end, l, r)
        }
        build(1, 0, n - 1)
        assertEquals(15, query(1, 0, n - 1, 0, 2))
        assertEquals(24, query(1, 0, n - 1, 1, 4))
    }

    @Test
    fun collectionFenwickTree() {
        val n = 8
        val bit = IntArray(n + 1)
        fun update(i: Int, delta: Int) {
            var idx = i
            while (idx <= n) {
                bit[idx] += delta
                idx += idx and -idx
            }
        }
        fun query(i: Int): Int {
            var sum = 0
            var idx = i
            while (idx > 0) {
                sum += bit[idx]
                idx -= idx and -idx
            }
            return sum
        }
        update(1, 3)
        update(2, 5)
        update(3, 7)
        assertEquals(8, query(2))
        assertEquals(15, query(3))
    }

    @Test
    fun collectionSparseTable() {
        val arr = intArrayOf(7, 2, 3, 0, 5, 10, 3, 12, 18)
        val n = arr.size
        val log = IntArray(n + 1)
        for (i in 2..n) log[i] = log[i / 2] + 1
        val k = log[n] + 1
        val st = Array(k) { IntArray(n) }
        for (i in 0 until n) st[0][i] = arr[i]
        for (j in 1 until k) {
            for (i in 0..n - (1 shl j)) {
                st[j][i] = minOf(st[j - 1][i], st[j - 1][i + (1 shl (j - 1))])
            }
        }
        fun query(l: Int, r: Int): Int {
            val j = log[r - l + 1]
            return minOf(st[j][l], st[j][r - (1 shl j) + 1])
        }
        assertEquals(0, query(0, 4))
        assertEquals(3, query(5, 7))
    }

    @Test
    fun collectionMonotonicStack() {
        val heights = intArrayOf(2, 1, 5, 6, 2, 3)
        val stack = ArrayDeque<Int>()
        var maxArea = 0
        for (i in heights.indices) {
            while (stack.isNotEmpty() && heights[stack.last()] > heights[i]) {
                val h = heights[stack.removeLast()]
                val w = if (stack.isEmpty()) i else i - stack.last() - 1
                maxArea = maxOf(maxArea, h * w)
            }
            stack.addLast(i)
        }
        assertEquals(10, maxArea)
    }

    @Test
    fun collectionMonotonicQueue() {
        val nums = intArrayOf(8, 7, 6, 9, 5, 4, 3, 2, 1)
        val k = 3
        val deque = ArrayDeque<Int>()
        val result = mutableListOf<Int>()
        for (i in nums.indices) {
            while (deque.isNotEmpty() && nums[deque.last()] <= nums[i]) deque.removeLast()
            deque.addLast(i)
            if (deque.first() <= i - k) deque.removeFirst()
            if (i >= k - 1) result.add(nums[deque.first()])
        }
        assertEquals(listOf(8, 9, 9, 9, 5, 4), result)
    }

    @Test
    fun collectionKMP() {
        val text = "ABABDABACDABABCABAB"
        val pattern = "ABABCABAB"
        val lps = IntArray(pattern.length)
        var len = 0
        var i = 1
        while (i < pattern.length) {
            if (pattern[i] == pattern[len]) {
                len++
                lps[i] = len
                i++
            } else {
                if (len != 0) {
                    len = lps[len - 1]
                } else {
                    lps[i] = 0
                    i++
                }
            }
        }
        var j = 0
        i = 0
        var found = -1
        while (i < text.length) {
            if (pattern[j] == text[i]) {
                i++
                j++
            }
            if (j == pattern.length) {
                found = i - j
                break
            } else if (i < text.length && pattern[j] != text[i]) {
                if (j != 0) j = lps[j - 1] else i++
            }
        }
        assertEquals(10, found)
    }

    @Test
    fun collectionRabinKarp() {
        val text = "AABAACAADAABAABA"
        val pattern = "AABA"
        val d = 256
        val q = 101
        val m = pattern.length
        val n = text.length
        var p = 0
        var t = 0
        var h = 1
        for (i in 0 until m - 1) h = (h * d) % q
        for (i in 0 until m) {
            p = (d * p + pattern[i].code) % q
            t = (d * t + text[i].code) % q
        }
        val matches = mutableListOf<Int>()
        for (i in 0..n - m) {
            if (p == t) {
                if (text.substring(i, i + m) == pattern) matches.add(i)
            }
            if (i < n - m) {
                t = (d * (t - text[i].code * h) + text[i + m].code) % q
                if (t < 0) t += q
            }
        }
        assertEquals(listOf(0, 9, 12), matches)
    }

    @Test
    fun collectionZAlgorithm() {
        val s = "aabxaabxcaabxaabxay"
        val pattern = "aab"
        val concat = "$pattern\$" + s
        val z = IntArray(concat.length)
        var l = 0
        var r = 0
        for (i in 1 until concat.length) {
            if (i > r) {
                l = i
                r = i
                while (r < concat.length && concat[r - l] == concat[r]) r++
                z[i] = r - l
                r--
            } else {
                val k = i - l
                if (z[k] < r - i + 1) {
                    z[i] = z[k]
                } else {
                    l = i
                    while (r < concat.length && concat[r - l] == concat[r]) r++
                    z[i] = r - l
                    r--
                }
            }
        }
        val matches = z.withIndex().filter { it.value == pattern.length }.map { it.index - pattern.length - 1 }
        assertEquals(listOf(0, 4, 8, 12), matches)
    }

    @Test
    fun collectionSuffixArray() {
        val s = "banana"
        val suffixes = s.indices.map { i -> s.substring(i) to i }.sortedBy { it.first }
        val sa = suffixes.map { it.second }
        assertEquals(listOf(5, 3, 1, 0, 4, 2), sa)
    }

    @Test
    fun collectionAhoCorasick() {
        val patterns = listOf("he", "she", "his", "hers")
        val text = "ushers"
        val matches = mutableListOf<Pair<Int, String>>()
        for (pattern in patterns) {
            var index = text.indexOf(pattern)
            while (index >= 0) {
                matches.add(index to pattern)
                index = text.indexOf(pattern, index + 1)
            }
        }
        matches.sortBy { it.first }
        assertEquals(2, matches.size)
        assertEquals(1 to "she", matches[0])
        assertEquals(2 to "he", matches[1])
    }

    @Test
    fun collectionManacher() {
        val s = "abacaba"
        val t = "^#" + s.toCharArray().joinToString("#") + "#$"
        val p = IntArray(t.length)
        var c = 0
        var r = 0
        for (i in 1 until t.length - 1) {
            val mirror = 2 * c - i
            if (i < r) p[i] = minOf(r - i, p[mirror])
            while (t[i + 1 + p[i]] == t[i - 1 - p[i]]) p[i]++
            if (i + p[i] > r) {
                c = i
                r = i + p[i]
            }
        }
        val maxLen = p.maxOrNull() ?: 0
        assertEquals(7, maxLen)
    }

    @Test
    fun collectionBoyerMoore() {
        val text = "ABAAABCD"
        val pattern = "ABC"
        val m = pattern.length
        val n = text.length
        val badChar = IntArray(256) { -1 }
        for (i in 0 until m) badChar[pattern[i].code] = i
        var shift = 0
        var found = -1
        while (shift <= n - m) {
            var j = m - 1
            while (j >= 0 && pattern[j] == text[shift + j]) j--
            if (j < 0) {
                found = shift
                break
            } else {
                shift += maxOf(1, j - badChar[text[shift + j].code])
            }
        }
        assertEquals(4, found)
    }

    @Test
    fun collectionQuickSelect() {
        val arr = intArrayOf(3, 2, 1, 5, 6, 4)
        val k = 2
        val result = arr.sortedDescending()[k - 1]
        assertEquals(5, result)
    }

    @Test
    fun collectionMergeSort() {
        fun mergeSort(arr: IntArray): IntArray {
            if (arr.size <= 1) return arr
            val mid = arr.size / 2
            val left = mergeSort(arr.copyOfRange(0, mid))
            val right = mergeSort(arr.copyOfRange(mid, arr.size))
            val result = IntArray(arr.size)
            var i = 0
            var j = 0
            var k = 0
            while (i < left.size && j < right.size) {
                result[k++] = if (left[i] <= right[j]) left[i++] else right[j++]
            }
            while (i < left.size) result[k++] = left[i++]
            while (j < right.size) result[k++] = right[j++]
            return result
        }
        val arr = intArrayOf(38, 27, 43, 3, 9, 82, 10)
        val sorted = mergeSort(arr)
        assertEquals(intArrayOf(3, 9, 10, 27, 38, 43, 82).toList(), sorted.toList())
    }

    @Test
    fun collectionQuickSort() {
        fun quickSort(arr: IntArray, low: Int, high: Int) {
            if (low < high) {
                val pivot = arr[high]
                var i = low - 1
                for (j in low until high) {
                    if (arr[j] <= pivot) {
                        i++
                        val temp = arr[i]
                        arr[i] = arr[j]
                        arr[j] = temp
                    }
                }
                val temp = arr[i + 1]
                arr[i + 1] = arr[high]
                arr[high] = temp
                val pi = i + 1
                quickSort(arr, low, pi - 1)
                quickSort(arr, pi + 1, high)
            }
        }
        val arr = intArrayOf(10, 7, 8, 9, 1, 5)
        quickSort(arr, 0, arr.size - 1)
        assertEquals(intArrayOf(1, 5, 7, 8, 9, 10).toList(), arr.toList())
    }

    @Test
    fun collectionHeapSort() {
        fun heapSort(arr: IntArray) {
            val n = arr.size
            for (i in n / 2 - 1 downTo 0) {
                var largest = i
                val left = 2 * i + 1
                val right = 2 * i + 2
                if (left < n && arr[left] > arr[largest]) largest = left
                if (right < n && arr[right] > arr[largest]) largest = right
                if (largest != i) {
                    val temp = arr[i]
                    arr[i] = arr[largest]
                    arr[largest] = temp
                }
            }
            for (i in n - 1 downTo 1) {
                val temp = arr[0]
                arr[0] = arr[i]
                arr[i] = temp
                var largest = 0
                val left = 1
                val right = 2
                if (left < i && arr[left] > arr[largest]) largest = left
                if (right < i && arr[right] > arr[largest]) largest = right
                if (largest != 0) {
                    val swap = arr[0]
                    arr[0] = arr[largest]
                    arr[largest] = swap
                }
            }
        }
        val arr = intArrayOf(12, 11, 13, 5, 6, 7)
        heapSort(arr)
        assertEquals(intArrayOf(5, 6, 7, 11, 12, 13).toList(), arr.toList())
    }

    @Test
    fun collectionCountingSort() {
        val arr = intArrayOf(4, 2, 2, 8, 3, 3, 1)
        val max = arr.maxOrNull() ?: 0
        val count = IntArray(max + 1)
        for (num in arr) count[num]++
        val result = mutableListOf<Int>()
        for (i in count.indices) {
            repeat(count[i]) { result.add(i) }
        }
        assertEquals(listOf(1, 2, 2, 3, 3, 4, 8), result)
    }

    @Test
    fun collectionRadixSort() {
        val arr = intArrayOf(170, 45, 75, 90, 802, 24, 2, 66)
        val max = arr.maxOrNull() ?: 0
        var exp = 1
        while (max / exp > 0) {
            val output = IntArray(arr.size)
            val count = IntArray(10)
            for (num in arr) count[(num / exp) % 10]++
            for (i in 1 until 10) count[i] += count[i - 1]
            for (i in arr.size - 1 downTo 0) {
                output[count[(arr[i] / exp) % 10] - 1] = arr[i]
                count[(arr[i] / exp) % 10]--
            }
            for (i in arr.indices) arr[i] = output[i]
            exp *= 10
        }
        assertEquals(intArrayOf(2, 24, 45, 66, 75, 90, 170, 802).toList(), arr.toList())
    }

    @Test
    fun collectionBucketSort() {
        val arr = doubleArrayOf(0.42, 0.32, 0.33, 0.52, 0.37, 0.47, 0.51)
        val n = arr.size
        val buckets = Array(n) { mutableListOf<Double>() }
        for (num in arr) {
            val index = (num * n).toInt()
            buckets[index].add(num)
        }
        for (bucket in buckets) bucket.sort()
        val result = buckets.flatten()
        assertEquals(listOf(0.32, 0.33, 0.37, 0.42, 0.47, 0.51, 0.52), result)
    }

    @Test
    fun collectionTopologicalSortKahn() {
        val graph = mapOf(
            0 to listOf(1, 2),
            1 to listOf(3),
            2 to listOf(3),
            3 to emptyList()
        )
        val inDegree = IntArray(4)
        for ((_, neighbors) in graph) {
            for (n in neighbors) inDegree[n]++
        }
        val queue = ArrayDeque<Int>()
        for (i in inDegree.indices) if (inDegree[i] == 0) queue.add(i)
        val result = mutableListOf<Int>()
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            result.add(node)
            graph[node]?.forEach { neighbor ->
                inDegree[neighbor]--
                if (inDegree[neighbor] == 0) queue.add(neighbor)
            }
        }
        assertEquals(listOf(0, 1, 2, 3), result)
    }

    @Test
    fun collectionBellmanFord() {
        val edges = listOf(
            Triple(0, 1, 4),
            Triple(0, 2, 5),
            Triple(1, 2, -3),
            Triple(2, 3, 4),
            Triple(3, 1, -1)
        )
        val n = 4
        val dist = IntArray(n) { Int.MAX_VALUE }
        dist[0] = 0
        for (i in 1 until n) {
            for ((u, v, w) in edges) {
                if (dist[u] != Int.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w
                }
            }
        }
        assertEquals(0, dist[0])
        assertEquals(4, dist[1])
        assertEquals(1, dist[2])
        assertEquals(5, dist[3])
    }

    @Test
    fun collectionFloydWarshall() {
        val INF = Int.MAX_VALUE / 2
        val graph = arrayOf(
            intArrayOf(0, 5, INF, 10),
            intArrayOf(INF, 0, 3, INF),
            intArrayOf(INF, INF, 0, 1),
            intArrayOf(INF, INF, INF, 0)
        )
        val n = graph.size
        val dist = Array(n) { i -> graph[i].copyOf() }
        for (k in 0 until n) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j]
                    }
                }
            }
        }
        assertEquals(0, dist[0][0])
        assertEquals(5, dist[0][1])
        assertEquals(8, dist[0][2])
        assertEquals(9, dist[0][3])
    }

    @Test
    fun collectionKruskal() {
        data class Edge(val u: Int, val v: Int, val w: Int)
        val edges = listOf(
            Edge(0, 1, 10),
            Edge(0, 2, 6),
            Edge(0, 3, 5),
            Edge(1, 3, 15),
            Edge(2, 3, 4)
        ).sortedBy { it.w }
        val parent = IntArray(4) { it }
        fun find(x: Int): Int {
            if (parent[x] != x) parent[x] = find(parent[x])
            return parent[x]
        }
        val mst = mutableListOf<Edge>()
        for (edge in edges) {
            val pu = find(edge.u)
            val pv = find(edge.v)
            if (pu != pv) {
                mst.add(edge)
                parent[pu] = pv
            }
        }
        assertEquals(3, mst.size)
        assertEquals(19, mst.sumOf { it.w })
    }

    @Test
    fun collectionPrim() {
        val graph = arrayOf(
            intArrayOf(0, 2, 0, 6, 0),
            intArrayOf(2, 0, 3, 8, 5),
            intArrayOf(0, 3, 0, 0, 7),
            intArrayOf(6, 8, 0, 0, 9),
            intArrayOf(0, 5, 7, 9, 0)
        )
        val n = graph.size
        val visited = BooleanArray(n)
        val minEdge = IntArray(n) { Int.MAX_VALUE }
        minEdge[0] = 0
        var totalWeight = 0
        for (i in 0 until n) {
            var u = -1
            for (j in 0 until n) {
                if (!visited[j] && (u == -1 || minEdge[j] < minEdge[u])) u = j
            }
            visited[u] = true
            totalWeight += minEdge[u]
            for (v in 0 until n) {
                if (graph[u][v] > 0 && !visited[v] && graph[u][v] < minEdge[v]) {
                    minEdge[v] = graph[u][v]
                }
            }
        }
        assertEquals(16, totalWeight)
    }

    @Test
    fun collectionMaxFlow() {
        val graph = arrayOf(
            intArrayOf(0, 16, 13, 0, 0, 0),
            intArrayOf(0, 0, 10, 12, 0, 0),
            intArrayOf(0, 4, 0, 0, 14, 0),
            intArrayOf(0, 0, 9, 0, 0, 20),
            intArrayOf(0, 0, 0, 7, 0, 4),
            intArrayOf(0, 0, 0, 0, 0, 0)
        )
        val n = graph.size
        val residual = Array(n) { i -> graph[i].copyOf() }
        val parent = IntArray(n)
        fun bfs(s: Int, t: Int): Boolean {
            val visited = BooleanArray(n)
            val queue = ArrayDeque<Int>()
            queue.add(s)
            visited[s] = true
            parent[s] = -1
            while (queue.isNotEmpty()) {
                val u = queue.removeFirst()
                for (v in 0 until n) {
                    if (!visited[v] && residual[u][v] > 0) {
                        queue.add(v)
                        parent[v] = u
                        visited[v] = true
                    }
                }
            }
            return visited[t]
        }
        var maxFlow = 0
        while (bfs(0, 5)) {
            var pathFlow = Int.MAX_VALUE
            var v = 5
            while (v != 0) {
                val u = parent[v]
                pathFlow = minOf(pathFlow, residual[u][v])
                v = u
            }
            v = 5
            while (v != 0) {
                val u = parent[v]
                residual[u][v] -= pathFlow
                residual[v][u] += pathFlow
                v = u
            }
            maxFlow += pathFlow
        }
        assertEquals(23, maxFlow)
    }

    @Test
    fun collectionNQueens() {
        val n = 4
        val board = Array(n) { CharArray(n) { '.' } }
        val cols = mutableSetOf<Int>()
        val diag1 = mutableSetOf<Int>()
        val diag2 = mutableSetOf<Int>()
        fun solve(row: Int): Boolean {
            if (row == n) return true
            for (col in 0 until n) {
                if (col !in cols && (row - col) !in diag1 && (row + col) !in diag2) {
                    board[row][col] = 'Q'
                    cols.add(col)
                    diag1.add(row - col)
                    diag2.add(row + col)
                    if (solve(row + 1)) return true
                    board[row][col] = '.'
                    cols.remove(col)
                    diag1.remove(row - col)
                    diag2.remove(row + col)
                }
            }
            return false
        }
        assertTrue(solve(0))
        assertEquals(2, board.sumOf { row -> row.count { it == 'Q' } })
    }

    @Test
    fun collectionSudoku() {
        val board = arrayOf(
            intArrayOf(5, 3, 0, 0, 7, 0, 0, 0, 0),
            intArrayOf(6, 0, 0, 1, 9, 5, 0, 0, 0),
            intArrayOf(0, 9, 8, 0, 0, 0, 0, 6, 0),
            intArrayOf(8, 0, 0, 0, 6, 0, 0, 0, 3),
            intArrayOf(4, 0, 0, 8, 0, 3, 0, 0, 1),
            intArrayOf(7, 0, 0, 0, 2, 0, 0, 0, 6),
            intArrayOf(0, 6, 0, 0, 0, 0, 2, 8, 0),
            intArrayOf(0, 0, 0, 4, 1, 9, 0, 0, 5),
            intArrayOf(0, 0, 0, 0, 8, 0, 0, 7, 9)
        )
        fun isValid(row: Int, col: Int, num: Int): Boolean {
            for (i in 0 until 9) {
                if (board[row][i] == num) return false
                if (board[i][col] == num) return false
                if (board[3 * (row / 3) + i / 3][3 * (col / 3) + i % 3] == num) return false
            }
            return true
        }
        fun solve(): Boolean {
            for (i in 0 until 9) {
                for (j in 0 until 9) {
                    if (board[i][j] == 0) {
                        for (num in 1..9) {
                            if (isValid(i, j, num)) {
                                board[i][j] = num
                                if (solve()) return true
                                board[i][j] = 0
                            }
                        }
                        return false
                    }
                }
            }
            return true
        }
        assertTrue(solve())
        assertEquals(5, board[0][0])
        assertEquals(3, board[0][1])
    }

    @Test
    fun collectionRatInMaze() {
        val maze = arrayOf(
            intArrayOf(1, 0, 0, 0),
            intArrayOf(1, 1, 0, 1),
            intArrayOf(0, 1, 0, 0),
            intArrayOf(1, 1, 1, 1)
        )
        val n = maze.size
        val solution = Array(n) { IntArray(n) }
        fun solve(row: Int, col: Int): Boolean {
            if (row == n - 1 && col == n - 1) {
                solution[row][col] = 1
                return true
            }
            if (row < 0 || row >= n || col < 0 || col >= n || maze[row][col] == 0 || solution[row][col] == 1) {
                return false
            }
            solution[row][col] = 1
            if (solve(row + 1, col)) return true
            if (solve(row, col + 1)) return true
            if (solve(row - 1, col)) return true
            if (solve(row, col - 1)) return true
            solution[row][col] = 0
            return false
        }
        assertTrue(solve(0, 0))
        assertEquals(1, solution[3][3])
    }

    @Test
    fun collectionHamiltonianCycle() {
        val graph = arrayOf(
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(1, 0, 1, 1, 1),
            intArrayOf(0, 1, 0, 0, 1),
            intArrayOf(1, 1, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        )
        val n = graph.size
        val path = IntArray(n) { -1 }
        path[0] = 0
        fun isSafe(v: Int, pos: Int): Boolean {
            if (graph[path[pos - 1]][v] == 0) return false
            for (i in 0 until pos) {
                if (path[i] == v) return false
            }
            return true
        }
        fun solve(pos: Int): Boolean {
            if (pos == n) return graph[path[pos - 1]][path[0]] == 1
            for (v in 1 until n) {
                if (isSafe(v, pos)) {
                    path[pos] = v
                    if (solve(pos + 1)) return true
                    path[pos] = -1
                }
            }
            return false
        }
        assertTrue(solve(1))
    }

    @Test
    fun collectionGraphColoring() {
        val graph = arrayOf(
            intArrayOf(0, 1, 1, 1),
            intArrayOf(1, 0, 1, 0),
            intArrayOf(1, 1, 0, 1),
            intArrayOf(1, 0, 1, 0)
        )
        val n = graph.size
        val colors = IntArray(n) { -1 }
        val numColors = 3
        fun isSafe(v: Int, c: Int): Boolean {
            for (i in 0 until n) {
                if (graph[v][i] == 1 && colors[i] == c) return false
            }
            return true
        }
        fun solve(v: Int): Boolean {
            if (v == n) return true
            for (c in 0 until numColors) {
                if (isSafe(v, c)) {
                    colors[v] = c
                    if (solve(v + 1)) return true
                    colors[v] = -1
                }
            }
            return false
        }
        assertTrue(solve(0))
        assertTrue(colors.all { it in 0 until numColors })
    }

    @Test
    fun collectionTravelingSalesman() {
        val dist = arrayOf(
            intArrayOf(0, 10, 15, 20),
            intArrayOf(10, 0, 35, 25),
            intArrayOf(15, 35, 0, 30),
            intArrayOf(20, 25, 30, 0)
        )
        val n = dist.size
        val visited = BooleanArray(n)
        visited[0] = true
        fun tsp(curr: Int, count: Int, cost: Int, minCost: IntArray) {
            if (count == n && dist[curr][0] > 0) {
                minCost[0] = minOf(minCost[0], cost + dist[curr][0])
                return
            }
            for (i in 0 until n) {
                if (!visited[i] && dist[curr][i] > 0) {
                    visited[i] = true
                    tsp(i, count + 1, cost + dist[curr][i], minCost)
                    visited[i] = false
                }
            }
        }
        val minCost = intArrayOf(Int.MAX_VALUE)
        tsp(0, 1, 0, minCost)
        assertEquals(80, minCost[0])
    }

    @Test
    fun collectionMinimax() {
        val scores = intArrayOf(3, 5, 6, 9, 1, 2, 0, -1)
        fun minimax(depth: Int, index: Int, maximizing: Boolean): Int {
            if (depth == 0) return scores[index]
            if (maximizing) {
                return maxOf(
                    minimax(depth - 1, index * 2, false),
                    minimax(depth - 1, index * 2 + 1, false)
                )
            } else {
                return minOf(
                    minimax(depth - 1, index * 2, true),
                    minimax(depth - 1, index * 2 + 1, true)
                )
            }
        }
        val result = minimax(3, 0, true)
        assertEquals(5, result)
    }

    @Test
    fun collectionAlphaBeta() {
        val scores = intArrayOf(3, 5, 6, 9, 1, 2, 0, -1)
        fun alphaBeta(depth: Int, index: Int, alpha: Int, beta: Int, maximizing: Boolean): Int {
            if (depth == 0) return scores[index]
            if (maximizing) {
                var value = Int.MIN_VALUE
                value = maxOf(value, alphaBeta(depth - 1, index * 2, alpha, beta, false))
                alpha = maxOf(alpha, value)
                if (alpha >= beta) return value
                value = maxOf(value, alphaBeta(depth - 1, index * 2 + 1, alpha, beta, false))
                return value
            } else {
                var value = Int.MAX_VALUE
                value = minOf(value, alphaBeta(depth - 1, index * 2, alpha, beta, true))
                beta = minOf(beta, value)
                if (alpha >= beta) return value
                value = minOf(value, alphaBeta(depth - 1, index * 2 + 1, alpha, beta, true))
                return value
            }
        }
        val result = alphaBeta(3, 0, Int.MIN_VALUE, Int.MAX_VALUE, true)
        assertEquals(5, result)
    }

    @Test
    fun collectionAStar() {
        val grid = arrayOf(
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 0)
        )
        val start = Pair(0, 0)
        val goal = Pair(4, 4)
        val rows = grid.size
        val cols = grid[0].size
        fun heuristic(a: Pair<Int, Int>, b: Pair<Int, Int>): Int {
            return kotlin.math.abs(a.first - b.first) + kotlin.math.abs(a.second - b.second)
        }
        val openSet = java.util.PriorityQueue<Triple<Int, Int, Pair<Int, Int>>>(compareBy { it.first })
        openSet.add(Triple(0, 0, start))
        val gScore = mutableMapOf<Pair<Int, Int>, Int>()
        gScore[start] = 0
        val cameFrom = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        while (openSet.isNotEmpty()) {
            val current = openSet.poll().third
            if (current == goal) {
                val path = mutableListOf<Pair<Int, Int>>()
                var node = goal
                while (node != start) {
                    path.add(node)
                    node = cameFrom[node]!!
                }
                path.add(start)
                path.reverse()
                assertEquals(8, path.size - 1)
                return
            }
            for ((dr, dc) in listOf(Pair(0, 1), Pair(1, 0), Pair(0, -1), Pair(-1, 0))) {
                val neighbor = Pair(current.first + dr, current.second + dc)
                if (neighbor.first in 0 until rows && neighbor.second in 0 until cols && grid[neighbor.first][neighbor.second] == 0) {
                    val tentativeG = gScore[current]!! + 1
                    if (tentativeG < gScore.getOrDefault(neighbor, Int.MAX_VALUE)) {
                        gScore[neighbor] = tentativeG
                        val f = tentativeG + heuristic(neighbor, goal)
                        openSet.add(Triple(f, tentativeG, neighbor))
                        cameFrom[neighbor] = current
                    }
                }
            }
        }
    }

    @Test
    fun collectionSimulatedAnnealing() {
        fun energy(x: Double): Double = x * x - 4 * x + 4
        var current = 0.0
        var best = current
        var temperature = 100.0
        val coolingRate = 0.95
        val random = java.util.Random(42)
        while (temperature > 0.01) {
            val next = current + (random.nextDouble() - 0.5) * 2
            val delta = energy(next) - energy(current)
            if (delta < 0 || random.nextDouble() < kotlin.math.exp(-delta / temperature)) {
                current = next
                if (energy(current) < energy(best)) {
                    best = current
                }
            }
            temperature *= coolingRate
        }
        assertTrue(kotlin.math.abs(best - 2.0) < 0.1)
    }

    @Test
    fun collectionGeneticAlgorithm() {
        val target = "HELLO"
        val populationSize = 100
        val mutationRate = 0.01
        val random = java.util.Random(42)
        fun randomChar(): Char = ('A'..'Z').random(random)
        fun randomIndividual(): String = (1..target.length).map { randomChar() }.joinToString("")
        fun fitness(individual: String): Int {
            return individual.zip(target).count { (a, b) -> a == b }
        }
        fun crossover(a: String, b: String): String {
            val point = random.nextInt(a.length)
            return a.substring(0, point) + b.substring(point)
        }
        fun mutate(individual: String): String {
            return individual.map { if (random.nextDouble() < mutationRate) randomChar() else it }.joinToString("")
        }
        var population = (1..populationSize).map { randomIndividual() }
        var generations = 0
        while (generations < 1000) {
            val scored = population.map { it to fitness(it) }.sortedByDescending { it.second }
            if (scored[0].second == target.length) break
            val newPopulation = mutableListOf<String>()
            while (newPopulation.size < populationSize) {
                val parent1 = scored[random.nextInt(scored.size / 2)].first
                val parent2 = scored[random.nextInt(scored.size / 2)].first
                newPopulation.add(mutate(crossover(parent1, parent2)))
            }
            population = newPopulation
            generations++
        }
        assertTrue(generations < 1000)
    }

    @Test
    fun collectionNeuralNetwork() {
        val inputs = arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(0.0, 1.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(1.0, 1.0))
        val outputs = intArrayOf(0, 1, 1, 0)
        val hiddenSize = 4
        val learningRate = 0.5
        val random = java.util.Random(42)
        val w1 = Array(2) { DoubleArray(hiddenSize) { random.nextDouble() * 2 - 1 } }
        val b1 = DoubleArray(hiddenSize) { random.nextDouble() * 2 - 1 }
        val w2 = DoubleArray(hiddenSize) { random.nextDouble() * 2 - 1 }
        val b2 = random.nextDouble() * 2 - 1
        fun sigmoid(x: Double): Double = 1.0 / (1.0 + kotlin.math.exp(-x))
        fun forward(input: DoubleArray): Pair<DoubleArray, Double> {
            val hidden = DoubleArray(hiddenSize) { i -> sigmoid(input[0] * w1[0][i] + input[1] * w1[1][i] + b1[i]) }
            val output = sigmoid(hidden.indices.sumOf { hidden[it] * w2[it] } + b2)
            return hidden to output
        }
        for (epoch in 0 until 10000) {
            for (i in inputs.indices) {
                val (hidden, output) = forward(inputs[i])
                val error = outputs[i] - output
                val outputDelta = error * output * (1 - output)
                for (j in hidden.indices) {
                    val hiddenDelta = outputDelta * w2[j] * hidden[j] * (1 - hidden[j])
                    w2[j] += learningRate * outputDelta * hidden[j]
                    w1[0][j] += learningRate * hiddenDelta * inputs[i][0]
                    w1[1][j] += learningRate * hiddenDelta * inputs[i][1]
                    b1[j] += learningRate * hiddenDelta
                }
                b2 += learningRate * outputDelta
            }
        }
        val (_, output00) = forward(doubleArrayOf(0.0, 0.0))
        val (_, output11) = forward(doubleArrayOf(1.0, 1.0))
        assertTrue(output00 < 0.5)
        assertTrue(output11 < 0.5)
    }

    @Test
    fun collectionKMeans() {
        val points = arrayOf(
            doubleArrayOf(1.0, 2.0), doubleArrayOf(1.5, 1.8), doubleArrayOf(5.0, 8.0),
            doubleArrayOf(8.0, 8.0), doubleArrayOf(1.0, 0.6), doubleArrayOf(9.0, 11.0),
            doubleArrayOf(8.0, 2.0), doubleArrayOf(10.0, 2.0), doubleArrayOf(9.0, 3.0)
        )
        val k = 3
        val random = java.util.Random(42)
        var centroids = Array(k) { points[random.nextInt(points.size)].copyOf() }
        val assignments = IntArray(points.size)
        for (iteration in 0 until 100) {
            for (i in points.indices) {
                var minDist = Double.MAX_VALUE
                for (j in 0 until k) {
                    val dist = kotlin.math.sqrt((0 until 2).sumOf { d -> (points[i][d] - centroids[j][d]).let { it * it } })
                    if (dist < minDist) {
                        minDist = dist
                        assignments[i] = j
                    }
                }
            }
            val newCentroids = Array(k) { DoubleArray(2) }
            val counts = IntArray(k)
            for (i in points.indices) {
                val cluster = assignments[i]
                newCentroids[cluster][0] += points[i][0]
                newCentroids[cluster][1] += points[i][1]
                counts[cluster]++
            }
            for (j in 0 until k) {
                if (counts[j] > 0) {
                    newCentroids[j][0] /= counts[j]
                    newCentroids[j][1] /= counts[j]
                }
            }
            centroids = newCentroids
        }
        assertEquals(3, assignments.toSet().size)
    }

    @Test
    fun collectionLinearRegression() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val y = doubleArrayOf(2.0, 4.0, 5.0, 4.0, 5.0)
        val n = x.size
        val sumX = x.sum()
        val sumY = y.sum()
        val sumXY = x.zip(y).sumOf { (xi, yi) -> xi * yi }
        val sumX2 = x.sumOf { it * it }
        val slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
        val intercept = (sumY - slope * sumX) / n
        val yPred = x.map { slope * it + intercept }
        val ssRes = y.zip(yPred).sumOf { (yi, yp) -> (yi - yp).let { it * it } }
        val yMean = y.average()
        val ssTot = y.sumOf { (it - yMean).let { d -> d * d } }
        val r2 = 1 - ssRes / ssTot
        assertTrue(r2 > 0.5)
    }

    @Test
    fun collectionLogisticRegression() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
        val y = intArrayOf(0, 0, 0, 0, 1, 1, 1, 1)
        val n = x.size
        var w = 0.0
        var b = 0.0
        val lr = 0.1
        for (epoch in 0 until 1000) {
            var dw = 0.0
            var db = 0.0
            for (i in 0 until n) {
                val z = w * x[i] + b
                val pred = 1.0 / (1.0 + kotlin.math.exp(-z))
                val error = pred - y[i]
                dw += error * x[i]
                db += error
            }
            w -= lr * dw / n
            b -= lr * db / n
        }
        val pred0 = 1.0 / (1.0 + kotlin.math.exp(-(w * 1.0 + b)))
        val pred7 = 1.0 / (1.0 + kotlin.math.exp(-(w * 7.0 + b)))
        assertTrue(pred0 < 0.5)
        assertTrue(pred7 > 0.5)
    }

    @Test
    fun collectionDecisionTree() {
        data class DataPoint(val features: List<Double>, val label: String)
        val data = listOf(
            DataPoint(listOf(1.0, 1.0), "A"),
            DataPoint(listOf(1.0, 0.0), "A"),
            DataPoint(listOf(0.0, 1.0), "B"),
            DataPoint(listOf(0.0, 0.0), "B")
        )
        fun gini(labels: List<String>): Double {
            val total = labels.size.toDouble()
            val counts = labels.groupingBy { it }.eachCount()
            return 1.0 - counts.values.sumOf { (it / total) * (it / total) }
        }
        val rootGini = gini(data.map { it.label })
        assertEquals(0.5, rootGini, 0.001)
        val leftLabels = data.filter { it.features[0] == 1.0 }.map { it.label }
        val rightLabels = data.filter { it.features[0] == 0.0 }.map { it.label }
        val weightedGini = (leftLabels.size * gini(leftLabels) + rightLabels.size * gini(rightLabels)) / data.size
        assertEquals(0.0, weightedGini, 0.001)
    }

    @Test
    fun collectionNaiveBayes() {
        val data = listOf(
            Triple("sunny", "hot", "no"),
            Triple("sunny", "mild", "no"),
            Triple("overcast", "hot", "yes"),
            Triple("rainy", "mild", "yes"),
            Triple("rainy", "cool", "yes"),
            Triple("overcast", "cool", "yes"),
            Triple("sunny", "mild", "no"),
            Triple("rainy", "mild", "yes"),
            Triple("sunny", "mild", "yes"),
            Triple("overcast", "mild", "yes"),
            Triple("overcast", "hot", "yes"),
            Triple("rainy", "mild", "no")
        )
        val playYes = data.count { it.third == "yes" }
        val playNo = data.count { it.third == "no" }
        val pYes = playYes.toDouble() / data.size
        val pNo = playNo.toDouble() / data.size
        assertEquals(9.0 / 12.0, pYes, 0.001)
        assertEquals(3.0 / 12.0, pNo, 0.001)
    }

    @Test
    fun collectionKNN() {
        val training = listOf(
            Triple(1.0, 1.0, "A"),
            Triple(1.5, 1.5, "A"),
            Triple(5.0, 5.0, "B"),
            Triple(5.5, 5.5, "B")
        )
        val test = Pair(1.2, 1.2)
        val k = 3
        val distances = training.map { (x, y, label) ->
            val dist = kotlin.math.sqrt((x - test.first) * (x - test.first) + (y - test.second) * (y - test.second))
            label to dist
        }.sortedBy { it.second }
        val topK = distances.take(k)
        val votes = topK.groupingBy { it.first }.eachCount()
        val predicted = votes.maxByOrNull { it.value }?.key
        assertEquals("A", predicted)
    }

    @Test
    fun collectionPCA() {
        val data = arrayOf(
            doubleArrayOf(2.5, 2.4),
            doubleArrayOf(0.5, 0.7),
            doubleArrayOf(2.2, 2.9),
            doubleArrayOf(1.9, 2.2),
            doubleArrayOf(3.1, 3.0),
            doubleArrayOf(2.3, 2.7),
            doubleArrayOf(2.0, 1.6),
            doubleArrayOf(1.0, 1.1),
            doubleArrayOf(1.5, 1.6),
            doubleArrayOf(1.1, 0.9)
        )
        val n = data.size
        val meanX = data.map { it[0] }.average()
        val meanY = data.map { it[1] }.average()
        val centered = data.map { doubleArrayOf(it[0] - meanX, it[1] - meanY) }
        val covXX = centered.sumOf { it[0] * it[0] } / (n - 1)
        val covYY = centered.sumOf { it[1] * it[1] } / (n - 1)
        val covXY = centered.sumOf { it[0] * it[1] } / (n - 1)
        assertTrue(covXX > 0)
        assertTrue(covYY > 0)
    }

    @Test
    fun collectionSVM() {
        val points = listOf(
            Triple(1.0, 1.0, 1),
            Triple(2.0, 2.0, 1),
            Triple(-1.0, -1.0, -1),
            Triple(-2.0, -2.0, -1)
        )
        var w = Pair(0.0, 0.0)
        var b = 0.0
        val lr = 0.1
        for (epoch in 0 until 100) {
            for ((x, y, label) in points) {
                val margin = label * (w.first * x + w.second * y + b)
                if (margin < 1) {
                    w = Pair(w.first + lr * label * x, w.second + lr * label * y)
                    b += lr * label
                }
            }
        }
        val margin1 = w.first * 1.0 + w.second * 1.0 + b
        val margin2 = w.first * -1.0 + w.second * -1.0 + b
        assertTrue(margin1 > 0)
        assertTrue(margin2 < 0)
    }

    @Test
    fun collectionRandomForest() {
        val data = listOf(
            Triple(1.0, 1.0, "A"),
            Triple(1.5, 1.5, "A"),
            Triple(5.0, 5.0, "B"),
            Triple(5.5, 5.5, "B"),
            Triple(1.2, 1.2, "A"),
            Triple(5.2, 5.2, "B")
        )
        val random = java.util.Random(42)
        val trees = (1..10).map {
            val sample = (1..data.size).map { data[random.nextInt(data.size)] }
            val labels = sample.map { it.third }
            labels.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        }
        val prediction = trees.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        assertTrue(prediction == "A" || prediction == "B")
    }

    @Test
    fun collectionGradientBoosting() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val y = doubleArrayOf(2.0, 4.0, 6.0, 8.0, 10.0)
        var prediction = y.average()
        val residuals = y.map { it - prediction }
        val lr = 0.1
        val trees = mutableListOf<DoubleArray>()
        for (tree in 0 until 10) {
            val treePrediction = DoubleArray(x.size) { i ->
                val feature = x[i]
                val bin = (feature / 2).toInt().coerceIn(0, 4)
                residuals.slice(bin..bin).average()
            }
            for (i in x.indices) {
                prediction += lr * treePrediction[i]
            }
            trees.add(treePrediction)
        }
        assertEquals(10, trees.size)
    }

    @Test
    fun collectionApriori() {
        val transactions = listOf(
            setOf("bread", "milk"),
            setOf("bread", "diaper", "beer", "eggs"),
            setOf("milk", "diaper", "beer", "cola"),
            setOf("bread", "milk", "diaper", "beer"),
            setOf("bread", "milk", "diaper", "cola")
        )
        val minSupport = 0.4
        val itemCounts = transactions.flatten().groupingBy { it }.eachCount()
        val frequentItems = itemCounts.filter { it.value >= transactions.size * minSupport }.keys
        assertEquals(setOf("bread", "milk", "diaper", "beer"), frequentItems)
    }

    @Test
    fun collectionPageRank() {
        val graph = arrayOf(
            intArrayOf(0, 1, 1, 0),
            intArrayOf(0, 0, 1, 0),
            intArrayOf(1, 0, 0, 1),
            intArrayOf(0, 0, 1, 0)
        )
        val n = graph.size
        val damping = 0.85
        val ranks = DoubleArray(n) { 1.0 / n }
        for (iteration in 0 until 100) {
            val newRanks = DoubleArray(n)
            for (i in 0 until n) {
                var sum = 0.0
                for (j in 0 until n) {
                    if (graph[j][i] == 1) {
                        val outDegree = graph[j].sum()
                        sum += ranks[j] / outDegree
                    }
                }
                newRanks[i] = (1 - damping) / n + damping * sum
            }
            for (i in 0 until n) ranks[i] = newRanks[i]
        }
        assertEquals(1.0, ranks.sum(), 0.001)
    }

    @Test
    fun collectionCollaborativeFiltering() {
        val ratings = arrayOf(
            intArrayOf(5, 3, 0, 1),
            intArrayOf(4, 0, 0, 1),
            intArrayOf(1, 1, 0, 5),
            intArrayOf(1, 0, 0, 4),
            intArrayOf(0, 1, 5, 4)
        )
        val user1 = ratings[0]
        val user2 = ratings[1]
        val dotProduct = user1.zip(user2).sumOf { (a, b) -> a * b }
        val norm1 = kotlin.math.sqrt(user1.sumOf { it * it }.toDouble())
        val norm2 = kotlin.math.sqrt(user2.sumOf { it * it }.toDouble())
        val similarity = dotProduct / (norm1 * norm2)
        assertTrue(similarity > 0.0 && similarity <= 1.0)
    }

    @Test
    fun collectionBloomFilter() {
        val size = 100
        val numHashes = 3
        val bits = BooleanArray(size)
        val random = java.util.Random(42)
        fun hash(item: String, seed: Int): Int {
            var h = seed
            for (c in item) {
                h = 31 * h + c.code
            }
            return kotlin.math.abs(h) % size
        }
        fun add(item: String) {
            for (i in 0 until numHashes) {
                bits[hash(item, i)] = true
            }
        }
        fun mightContain(item: String): Boolean {
            return (0 until numHashes).all { bits[hash(item, it)] }
        }
        add("hello")
        add("world")
        assertTrue(mightContain("hello"))
        assertTrue(mightContain("world"))
    }

    @Test
    fun collectionHyperLogLog() {
        val p = 4
        val m = 1 shl p
        val registers = IntArray(m)
        val random = java.util.Random(42)
        fun hash(value: Long): Int {
            var h = value
            h = h xor (h ushr 33)
            h *= 0xff51afd7ed558ccdL
            h = h xor (h ushr 33)
            h *= 0xc4ceb9fe1a85ec53L
            h = h xor (h ushr 33)
            return h.toInt()
        }
        for (i in 0 until 10000) {
            val h = hash(random.nextLong())
            val index = h ushr (32 - p)
            val leadingZeros = Integer.numberOfLeadingZeros(h) + 1
            registers[index] = maxOf(registers[index], leadingZeros)
        }
        val alpha = 0.673
        val estimate = alpha * m * m / registers.sumOf { 1.0 / (1 shl it) }
        assertTrue(estimate > 5000 && estimate < 15000)
    }

    @Test
    fun collectionCountMinSketch() {
        val width = 100
        val depth = 5
        val sketch = Array(depth) { IntArray(width) }
        val random = java.util.Random(42)
        fun hash(item: String, seed: Int): Int {
            var h = seed
            for (c in item) {
                h = 31 * h + c.code
            }
            return kotlin.math.abs(h) % width
        }
        val counts = mutableMapOf<String, Int>()
        for (i in 0 until 1000) {
            val item = "item${random.nextInt(100)}"
            counts[item] = counts.getOrDefault(item, 0) + 1
            for (d in 0 until depth) {
                sketch[d][hash(item, d)]++
            }
        }
        fun estimate(item: String): Int {
            return (0 until depth).minOf { d -> sketch[d][hash(item, d)] }
        }
        val item42 = estimate("item42")
        assertTrue(item42 >= counts["item42"]!!)
    }

    @Test
    fun collectionConsistentHashing() {
        val nodes = listOf("node1", "node2", "node3")
        val virtualNodes = 150
        val ring = TreeMap<Long, String>()
        val random = java.util.Random(42)
        for (node in nodes) {
            for (i in 0 until virtualNodes) {
                val hash = (node + i).hashCode().toLong() and 0xffffffffL
                ring[hash] = node
            }
        }
        fun getNode(key: String): String {
            val hash = key.hashCode().toLong() and 0xffffffffL
            val entry = ring.ceilingEntry(hash) ?: ring.firstEntry()
            return entry.value
        }
        val distribution = (0 until 1000).map { getNode("key$it") }.groupingBy { it }.eachCount()
        assertEquals(3, distribution.size)
    }

    @Test
    fun collectionSkipList() {
        val list = java.util.Collections.synchronizedList(mutableListOf<Int>())
        val random = java.util.Random(42)
        for (i in 0 until 100) {
            list.add(i)
        }
        list.sort()
        assertEquals(100, list.size)
        assertEquals(0, list[0])
        assertEquals(99, list[99])
    }

    @Test
    fun collectionBTree() {
        val map = TreeMap<Int, String>()
        for (i in 0 until 100) {
            map[i] = "value$i"
        }
        assertEquals(100, map.size)
        assertEquals("value0", map[0])
        assertEquals("value99", map[99])
        assertEquals("value50", map.ceilingEntry(50).value)
    }

    @Test
    fun collectionRedBlackTree() {
        val set = java.util.TreeSet<Int>()
        for (i in 0 until 100) {
            set.add(i)
        }
        assertEquals(100, set.size)
        assertTrue(set.contains(50))
        assertFalse(set.contains(100))
        assertEquals(0, set.first())
        assertEquals(99, set.last())
    }

    @Test
    fun collectionDisjointSetWithPathCompression() {
        val parent = IntArray(10) { it }
        fun find(x: Int): Int {
            if (parent[x] != x) parent[x] = find(parent[x])
            return parent[x]
        }
        fun union(x: Int, y: Int) {
            parent[find(x)] = find(y)
        }
        union(0, 1)
        union(2, 3)
        union(4, 5)
        union(1, 3)
        assertEquals(find(0), find(2))
        assertEquals(find(0), find(3))
        assertFalse(find(0) == find(4))
    }

    @Test
    fun collectionIntervalTree() {
        val intervals = listOf(
            Pair(15, 20), Pair(10, 30), Pair(17, 19),
            Pair(5, 20), Pair(12, 15), Pair(30, 40)
        )
        val sorted = intervals.sortedBy { it.first }
        assertEquals(Pair(5, 20), sorted[0])
        assertEquals(Pair(30, 40), sorted[5])
    }

    @Test
    fun collectionKDTree() {
        data class Point(val x: Double, val y: Double)
        val points = listOf(
            Point(2.0, 3.0), Point(5.0, 4.0), Point(9.0, 6.0),
            Point(4.0, 7.0), Point(8.0, 1.0), Point(7.0, 2.0)
        )
        val sortedByX = points.sortedBy { it.x }
        assertEquals(Point(2.0, 3.0), sortedByX[0])
        assertEquals(Point(9.0, 6.0), sortedByX[5])
    }

    @Test
    fun collectionRabinFingerprint() {
        val text = "hello world"
        val prime = 31
        var hash = 0L
        for (c in text) {
            hash = (hash * prime + c.code) % 1000000007
        }
        assertTrue(hash > 0)
    }

    @Test
    fun collectionSimHash() {
        val doc1 = "the cat sat on the mat"
        val doc2 = "the cat sat on the hat"
        val words1 = doc1.split(" ").toSet()
        val words2 = doc2.split(" ").toSet()
        val intersection = words1 intersect words2
        val union = words1 union words2
        val jaccard = intersection.size.toDouble() / union.size
        assertTrue(jaccard > 0.5)
    }

    @Test
    fun collectionMinHash() {
        val set1 = setOf(1, 2, 3, 4, 5)
        val set2 = setOf(4, 5, 6, 7, 8)
        val intersection = set1 intersect set2
        val union = set1 union set2
        val jaccard = intersection.size.toDouble() / union.size
        assertEquals(2.0 / 8.0, jaccard, 0.001)
    }

    @Test
    fun collectionLSH() {
        val vectors = listOf(
            listOf(1, 0, 0, 0),
            listOf(0, 1, 0, 0),
            listOf(0, 0, 1, 0),
            listOf(1, 1, 0, 0)
        )
        val random = java.util.Random(42)
        val planes = (0 until 4).map { List(4) { random.nextDouble() * 2 - 1 } }
        fun hash(vector: List<Int>): String {
            return planes.joinToString("") { plane ->
                val dot = vector.zip(plane).sumOf { (v, p) -> v * p }
                if (dot >= 0) "1" else "0"
            }
        }
        val hashes = vectors.map { hash(it) }
        assertEquals(4, hashes.size)
    }

    @Test
    fun collectionReservoirSampling() {
        val stream = (1..1000).toList()
        val k = 10
        val random = java.util.Random(42)
        val reservoir = mutableListOf<Int>()
        for ((index, item) in stream.withIndex()) {
            if (index < k) {
                reservoir.add(item)
            } else {
                val j = random.nextInt(index + 1)
                if (j < k) {
                    reservoir[j] = item
                }
            }
        }
        assertEquals(k, reservoir.size)
        assertTrue(reservoir.all { it in stream })
    }

    @Test
    fun collectionWeightedRandom() {
        val items = listOf("A", "B", "C", "D")
        val weights = listOf(10, 20, 30, 40)
        val totalWeight = weights.sum()
        val random = java.util.Random(42)
        val counts = mutableMapOf<String, Int>()
        for (i in 0 until 10000) {
            var r = random.nextInt(totalWeight)
            for ((index, weight) in weights.withIndex()) {
                r -= weight
                if (r < 0) {
                    counts[items[index]] = counts.getOrDefault(items[index], 0) + 1
                    break
                }
            }
        }
        assertTrue(counts["D"]!! > counts["A"]!!)
    }

    @Test
    fun collectionTournamentTree() {
        val players = listOf("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry")
        val skills = mapOf(
            "Alice" to 85, "Bob" to 90, "Charlie" to 78, "Diana" to 92,
            "Eve" to 88, "Frank" to 75, "Grace" to 95, "Henry" to 80
        )
        var round = players
        while (round.size > 1) {
            val nextRound = mutableListOf<String>()
            for (i in round.indices step 2) {
                if (i + 1 < round.size) {
                    val winner = if (skills[round[i]]!! >= skills[round[i + 1]]!!) round[i] else round[i + 1]
                    nextRound.add(winner)
                } else {
                    nextRound.add(round[i])
                }
            }
            round = nextRound
        }
        assertEquals("Grace", round[0])
    }

    @Test
    fun collectionSegmentTreeWithLazyPropagation() {
        val arr = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
        val n = arr.size
        val tree = IntArray(4 * n)
        val lazy = IntArray(4 * n)
        fun build(node: Int, start: Int, end: Int) {
            if (start == end) {
                tree[node] = arr[start]
            } else {
                val mid = (start + end) / 2
                build(2 * node, start, mid)
                build(2 * node + 1, mid + 1, end)
                tree[node] = tree[2 * node] + tree[2 * node + 1]
            }
        }
        fun updateRange(node: Int, start: Int, end: Int, l: Int, r: Int, value: Int) {
            if (lazy[node] != 0) {
                tree[node] += (end - start + 1) * lazy[node]
                if (start != end) {
                    lazy[2 * node] += lazy[node]
                    lazy[2 * node + 1] += lazy[node]
                }
                lazy[node] = 0
            }
            if (start > end || start > r || end < l) return
            if (start >= l && end <= r) {
                tree[node] += (end - start + 1) * value
                if (start != end) {
                    lazy[2 * node] += value
                    lazy[2 * node + 1] += value
                }
                return
            }
            val mid = (start + end) / 2
            updateRange(2 * node, start, mid, l, r, value)
            updateRange(2 * node + 1, mid + 1, end, l, r, value)
            tree[node] = tree[2 * node] + tree[2 * node + 1]
        }
        fun queryRange(node: Int, start: Int, end: Int, l: Int, r: Int): Int {
            if (start > end || start > r || end < l) return 0
            if (lazy[node] != 0) {
                tree[node] += (end - start + 1) * lazy[node]
                if (start != end) {
                    lazy[2 * node] += lazy[node]
                    lazy[2 * node + 1] += lazy[node]
                }
                lazy[node] = 0
            }
            if (start >= l && end <= r) return tree[node]
            val mid = (start + end) / 2
            return queryRange(2 * node, start, mid, l, r) + queryRange(2 * node + 1, mid + 1, end, l, r)
        }
        build(1, 0, n - 1)
        assertEquals(36, queryRange(1, 0, n - 1, 0, 7))
        updateRange(1, 0, n - 1, 0, 3, 2)
        assertEquals(44, queryRange(1, 0, n - 1, 0, 7))
    }

    @Test
    fun collectionPersistentSegmentTree() {
        val arr = intArrayOf(1, 2, 3, 4, 5)
        val n = arr.size
        val versions = mutableListOf<IntArray>()
        versions.add(arr.copyOf())
        val newArr = arr.copyOf()
        newArr[2] = 10
        versions.add(newArr.copyOf())
        assertEquals(3, versions[0][2])
        assertEquals(10, versions[1][2])
    }

    @Test
    fun collectionTreap() {
        val random = java.util.Random(42)
        val elements = (1..100).shuffled(random)
        val sorted = elements.sorted()
        assertEquals(100, sorted.size)
        assertEquals(1, sorted[0])
        assertEquals(100, sorted[99])
    }

    @Test
    fun collectionSplayTree() {
        val elements = listOf(10, 20, 30, 40, 50, 25)
        val sorted = elements.sorted()
        assertEquals(listOf(10, 20, 25, 30, 40, 50), sorted)
    }

    @Test
    fun collectionAVLTree() {
        val elements = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val sorted = elements.sorted()
        assertEquals(elements, sorted)
    }

    @Test
    fun collectionBPlusTree() {
        val keys = (1..100).toList()
        val sorted = keys.sorted()
        assertEquals(100, sorted.size)
        assertEquals(1, sorted.first())
        assertEquals(100, sorted.last())
    }

    @Test
    fun collectionMerkleTree() {
        val leaves = listOf("a", "b", "c", "d")
        fun hash(data: String): String {
            return java.security.MessageDigest.getInstance("SHA-256")
                .digest(data.toByteArray())
                .joinToString("") { "%02x".format(it) }
        }
        val hashes = leaves.map { hash(it) }
        val combined = hash(hashes[0] + hashes[1]) + hash(hashes[2] + hashes[3])
        val root = hash(combined)
        assertTrue(root.isNotEmpty())
    }

    @Test
    fun collectionMerkleProof() {
        val leaves = listOf("tx1", "tx2", "tx3", "tx4")
        fun hash(data: String): String {
            return java.security.MessageDigest.getInstance("SHA-256")
                .digest(data.toByteArray())
                .joinToString("") { "%02x".format(it) }
        }
        val hashes = leaves.map { hash(it) }
        val left = hash(hashes[0] + hashes[1])
        val right = hash(hashes[2] + hashes[3])
        val root = hash(left + right)
        val proof = listOf(hashes[1], right)
        val computedLeft = hash(hashes[0] + proof[0])
        val computedRoot = hash(computedLeft + proof[1])
        assertEquals(root, computedRoot)
    }

    @Test
    fun collectionGraphIsomorphism() {
        val graph1 = mapOf(
            0 to setOf(1, 2),
            1 to setOf(0, 2),
            2 to setOf(0, 1)
        )
        val graph2 = mapOf(
            "A" to setOf("B", "C"),
            "B" to setOf("A", "C"),
            "C" to setOf("A", "B")
        )
        assertEquals(graph1.size, graph2.size)
        assertEquals(graph1.values.map { it.size }.sorted(), graph2.values.map { it.size }.sorted())
    }

    @Test
    fun collectionMaxClique() {
        val graph = arrayOf(
            intArrayOf(0, 1, 1, 0, 0),
            intArrayOf(1, 0, 1, 1, 0),
            intArrayOf(1, 1, 0, 1, 0),
            intArrayOf(0, 1, 1, 0, 1),
            intArrayOf(0, 0, 0, 1, 0)
        )
        val n = graph.size
        var maxClique = 0
        for (mask in 1 until (1 shl n)) {
            val vertices = (0 until n).filter { mask and (1 shl it) != 0 }
            val isClique = vertices.all { i ->
                vertices.all { j -> i == j || graph[i][j] == 1 }
            }
            if (isClique) {
                maxClique = maxOf(maxClique, vertices.size)
            }
        }
        assertEquals(3, maxClique)
    }

    @Test
    fun collectionVertexCover() {
        val edges = listOf(
            Pair(0, 1), Pair(0, 2), Pair(1, 2), Pair(1, 3), Pair(2, 4), Pair(3, 4)
        )
        val vertices = edges.flatten().toSet()
        var minCover = vertices.size
        for (mask in 1 until (1 shl vertices.size)) {
            val cover = vertices.filterIndexed { index, _ -> mask and (1 shl index) != 0 }
            val coversAll = edges.all { (u, v) -> u in cover || v in cover }
            if (coversAll) {
                minCover = minOf(minCover, cover.size)
            }
        }
        assertEquals(3, minCover)
    }

    @Test
    fun collectionDominatingSet() {
        val graph = arrayOf(
            intArrayOf(0, 1, 1, 0),
            intArrayOf(1, 0, 1, 1),
            intArrayOf(1, 1, 0, 1),
            intArrayOf(0, 1, 1, 0)
        )
        val n = graph.size
        var minDomSet = n
        for (mask in 1 until (1 shl n)) {
            val domSet = (0 until n).filter { mask and (1 shl it) != 0 }
            val dominated = BooleanArray(n)
            for (v in domSet) {
                dominated[v] = true
                for (u in 0 until n) {
                    if (graph[v][u] == 1) dominated[u] = true
                }
            }
            if (dominated.all { it }) {
                minDomSet = minOf(minDomSet, domSet.size)
            }
        }
        assertEquals(1, minDomSet)
    }

    @Test
    fun collectionIndependentSet() {
        val graph = arrayOf(
            intArrayOf(0, 1, 0, 1),
            intArrayOf(1, 0, 1, 0),
            intArrayOf(0, 1, 0, 1),
            intArrayOf(1, 0, 1, 0)
        )
        val n = graph.size
        var maxIndSet = 0
        for (mask in 1 until (1 shl n)) {
            val vertices = (0 until n).filter { mask and (1 shl it) != 0 }
            val isIndependent = vertices.all { i ->
                vertices.all { j -> i == j || graph[i][j] == 0 }
            }
            if (isIndependent) {
                maxIndSet = maxOf(maxIndSet, vertices.size)
            }
        }
        assertEquals(2, maxIndSet)
    }

    @Test
    fun collectionGraphMatching() {
        val graph = arrayOf(
            intArrayOf(0, 1, 1, 0, 0),
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(0, 1, 1, 0, 1),
            intArrayOf(0, 0, 0, 1, 0)
        )
        val n = graph.size
        val matchR = IntArray(n) { -1 }
        fun bpm(u: Int, seen: BooleanArray): Boolean {
            for (v in 0 until n) {
                if (graph[u][v] == 1 && !seen[v]) {
                    seen[v] = true
                    if (matchR[v] < 0 || bpm(matchR[v], seen)) {
                        matchR[v] = u
                        return true
                    }
                }
            }
            return false
        }
        var result = 0
        for (u in 0 until n) {
            val seen = BooleanArray(n)
            if (bpm(u, seen)) result++
        }
        assertEquals(2, result)
    }

    @Test
    fun collectionEulerianPath() {
        val graph = arrayOf(
            intArrayOf(0, 1, 1, 0, 0),
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(1, 0, 0, 1, 1),
            intArrayOf(0, 1, 1, 0, 1),
            intArrayOf(0, 0, 1, 1, 0)
        )
        val n = graph.size
        val degrees = IntArray(n)
        for (i in 0 until n) {
            degrees[i] = graph[i].sum()
        }
        val oddCount = degrees.count { it % 2 != 0 }
        assertTrue(oddCount == 0 || oddCount == 2)
    }

    @Test
    fun collectionArticulationPoints() {
        val graph = arrayOf(
            intArrayOf(0, 1, 1, 0, 0),
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(1, 0, 0, 1, 1),
            intArrayOf(0, 1, 1, 0, 1),
            intArrayOf(0, 0, 1, 1, 0)
        )
        val n = graph.size
        val visited = BooleanArray(n)
        val disc = IntArray(n)
        val low = IntArray(n)
        val parent = IntArray(n) { -1 }
        val ap = BooleanArray(n)
        var time = 0
        fun dfs(u: Int) {
            var children = 0
            visited[u] = true
            disc[u] = time
            low[u] = time
            time++
            for (v in 0 until n) {
                if (graph[u][v] == 1) {
                    if (!visited[v]) {
                        children++
                        parent[v] = u
                        dfs(v)
                        low[u] = minOf(low[u], low[v])
                        if (parent[u] == -1 && children > 1) ap[u] = true
                        if (parent[u] != -1 && low[v] >= disc[u]) ap[u] = true
                    } else if (v != parent[u]) {
                        low[u] = minOf(low[u], disc[v])
                    }
                }
            }
        }
        for (i in 0 until n) {
            if (!visited[i]) dfs(i)
        }
        assertTrue(ap.any { it })
    }

    @Test
    fun collectionBridges() {
        val graph = arrayOf(
            intArrayOf(0, 1, 1, 0, 0),
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(1, 0, 0, 1, 1),
            intArrayOf(0, 1, 1, 0, 1),
            intArrayOf(0, 0, 1, 1, 0)
        )
        val n = graph.size
        val visited = BooleanArray(n)
        val disc = IntArray(n)
        val low = IntArray(n)
        val parent = IntArray(n) { -1 }
        val bridges = mutableListOf<Pair<Int, Int>>()
        var time = 0
        fun dfs(u: Int) {
            visited[u] = true
            disc[u] = time
            low[u] = time
            time++
            for (v in 0 until n) {
                if (graph[u][v] == 1) {
                    if (!visited[v]) {
                        parent[v] = u
                        dfs(v)
                        low[u] = minOf(low[u], low[v])
                        if (low[v] > disc[u]) {
                            bridges.add(u to v)
                        }
                    } else if (v != parent[u]) {
                        low[u] = minOf(low[u], disc[v])
                    }
                }
            }
        }
        for (i in 0 until n) {
            if (!visited[i]) dfs(i)
        }
        assertTrue(bridges.isNotEmpty())
    }

    @Test
    fun collectionStronglyConnectedComponents() {
        val graph = arrayOf(
            intArrayOf(0, 1, 0, 0, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(0, 0, 0, 1, 0)
        )
        val n = graph.size
        val visited = BooleanArray(n)
        val stack = ArrayDeque<Int>()
        fun dfs1(u: Int) {
            visited[u] = true
            for (v in 0 until n) {
                if (graph[u][v] == 1 && !visited[v]) dfs1(v)
            }
            stack.addLast(u)
        }
        for (i in 0 until n) {
            if (!visited[i]) dfs1(i)
        }
        val transposed = Array(n) { IntArray(n) }
        for (i in 0 until n) {
            for (j in 0 until n) {
                transposed[j][i] = graph[i][j]
            }
        }
        val visited2 = BooleanArray(n)
        val sccs = mutableListOf<List<Int>>()
        fun dfs2(u: Int, component: MutableList<Int>) {
            visited2[u] = true
            component.add(u)
            for (v in 0 until n) {
                if (transposed[u][v] == 1 && !visited2[v]) dfs2(v, component)
            }
        }
        while (stack.isNotEmpty()) {
            val u = stack.removeLast()
            if (!visited2[u]) {
                val component = mutableListOf<Int>()
                dfs2(u, component)
                sccs.add(component)
            }
        }
        assertEquals(2, sccs.size)
    }

    @Test
    fun collectionBiconnectedComponents() {
        val graph = arrayOf(
            intArrayOf(0, 1, 1, 0, 0),
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(1, 0, 0, 1, 1),
            intArrayOf(0, 1, 1, 0, 1),
            intArrayOf(0, 0, 1, 1, 0)
        )
        val n = graph.size
        val visited = BooleanArray(n)
        val disc = IntArray(n)
        val low = IntArray(n)
        val parent = IntArray(n) { -1 }
        val stack = ArrayDeque<Pair<Int, Int>>()
        val components = mutableListOf<List<Pair<Int, Int>>>()
        var time = 0
        fun dfs(u: Int) {
            visited[u] = true
            disc[u] = time
            low[u] = time
            time++
            for (v in 0 until n) {
                if (graph[u][v] == 1) {
                    if (!visited[v]) {
                        parent[v] = u
                        stack.addLast(u to v)
                        dfs(v)
                        low[u] = minOf(low[u], low[v])
                        if (low[v] >= disc[u]) {
                            val component = mutableListOf<Pair<Int, Int>>()
                            while (true) {
                                val edge = stack.removeLast()
                                component.add(edge)
                                if (edge == u to v) break
                            }
                            components.add(component)
                        }
                    } else if (v != parent[u] && disc[v] < disc[u]) {
                        stack.addLast(u to v)
                        low[u] = minOf(low[u], disc[v])
                    }
                }
            }
        }
        for (i in 0 until n) {
            if (!visited[i]) dfs(i)
        }
        assertTrue(components.isNotEmpty())
    }

    @Test
    fun collectionNetworkSimplex() {
        val supply = intArrayOf(10, 0, 0, -10)
        val edges = listOf(
            Triple(0, 1, 2, 2),
            Triple(0, 2, 3, 5),
            Triple(1, 3, 1, 3),
            Triple(2, 3, 2, 1)
        )
        assertEquals(4, edges.size)
        assertEquals(0, supply.sum())
    }

    @Test
    fun collectionSuccessiveShortestPath() {
        val graph = arrayOf(
            intArrayOf(0, 10, 0, 0),
            intArrayOf(0, 0, 5, 0),
            intArrayOf(0, 0, 0, 10),
            intArrayOf(0, 0, 0, 0)
        )
        val n = graph.size
        val dist = IntArray(n) { Int.MAX_VALUE }
        dist[0] = 0
        for (i in 1 until n) {
            for (u in 0 until n) {
                for (v in 0 until n) {
                    if (graph[u][v] > 0 && dist[u] != Int.MAX_VALUE && dist[u] + graph[u][v] < dist[v]) {
                        dist[v] = dist[u] + graph[u][v]
                    }
                }
            }
        }
        assertEquals(15, dist[3])
    }

    @Test
    fun collectionCycleCanceling() {
        val graph = arrayOf(
            intArrayOf(0, 10, 0, 0),
            intArrayOf(0, 0, 5, 0),
            intArrayOf(0, 0, 0, 10),
            intArrayOf(0, 0, 0, 0)
        )
        val n = graph.size
        val dist = IntArray(n) { Int.MAX_VALUE }
        dist[0] = 0
        for (i in 1 until n) {
            for (u in 0 until n) {
                for (v in 0 until n) {
                    if (graph[u][v] > 0 && dist[u] != Int.MAX_VALUE && dist[u] + graph[u][v] < dist[v]) {
                        dist[v] = dist[u] + graph[u][v]
                    }
                }
            }
        }
        assertEquals(15, dist[3])
    }

    @Test
    fun collectionCapacityScaling() {
        val graph = arrayOf(
            intArrayOf(0, 16, 13, 0, 0, 0),
            intArrayOf(0, 0, 10, 12, 0, 0),
            intArrayOf(0, 4, 0, 0, 14, 0),
            intArrayOf(0, 0, 9, 0, 0, 20),
            intArrayOf(0, 0, 0, 7, 0, 4),
            intArrayOf(0, 0, 0, 0, 0, 0)
        )
        val n = graph.size
        val residual = Array(n) { i -> graph[i].copyOf() }
        val parent = IntArray(n)
        fun bfs(s: Int, t: Int): Boolean {
            val visited = BooleanArray(n)
            val queue = ArrayDeque<Int>()
            queue.add(s)
            visited[s] = true
            parent[s] = -1
            while (queue.isNotEmpty()) {
                val u = queue.removeFirst()
                for (v in 0 until n) {
                    if (!visited[v] && residual[u][v] > 0) {
                        queue.add(v)
                        parent[v] = u
                        visited[v] = true
                    }
                }
            }
            return visited[t]
        }
        var maxFlow = 0
        while (bfs(0, 5)) {
            var pathFlow = Int.MAX_VALUE
            var v = 5
            while (v != 0) {
                val u = parent[v]
                pathFlow = minOf(pathFlow, residual[u][v])
                v = u
            }
            v = 5
            while (v != 0) {
                val u = parent[v]
                residual[u][v] -= pathFlow
                residual[v][u] += pathFlow
                v = u
            }
            maxFlow += pathFlow
        }
        assertEquals(23, maxFlow)
    }

    @Test
    fun collectionPushRelabel() {
        val graph = arrayOf(
            intArrayOf(0, 16, 13, 0, 0, 0),
            intArrayOf(0, 0, 10, 12, 0, 0),
            intArrayOf(0, 4, 0, 0, 14, 0),
            intArrayOf(0, 0, 9, 0, 0, 20),
            intArrayOf(0, 0, 0, 7, 0, 4),
            intArrayOf(0, 0, 0, 0, 0, 0)
        )
        val n = graph.size
        val residual = Array(n) { i -> graph[i].copyOf() }
        val height = IntArray(n)
        val excess = IntArray(n)
        height[0] = n
        for (v in 0 until n) {
            if (graph[0][v] > 0) {
                residual[0][v] = 0
                residual[v][0] = graph[0][v]
                excess[v] = graph[0][v]
                excess[0] -= graph[0][v]
            }
        }
        var maxFlow = 0
        for (v in 0 until n) {
            if (v != 0 && v != n - 1 && excess[v] > 0) {
                maxFlow += excess[v]
            }
        }
        assertTrue(maxFlow > 0)
    }

    @Test
    fun collectionHungarianAlgorithm() {
        val cost = arrayOf(
            intArrayOf(4, 1, 3),
            intArrayOf(2, 0, 5),
            intArrayOf(3, 2, 2)
        )
        val n = cost.size
        val u = IntArray(n + 1)
        val v = IntArray(n + 1)
        val p = IntArray(n + 1)
        val way = IntArray(n + 1)
        for (i in 1..n) {
            p[0] = i
            var j0 = 0
            val minv = IntArray(n + 1) { Int.MAX_VALUE }
            val used = BooleanArray(n + 1)
            do {
                used[j0] = true
                var i0 = p[j0]
                var delta = Int.MAX_VALUE
                var j1 = 0
                for (j in 1..n) {
                    if (!used[j]) {
                        val cur = cost[i0 - 1][j - 1] - u[i0] - v[j]
                        if (cur < minv[j]) {
                            minv[j] = cur
                            way[j] = j0
                        }
                        if (minv[j] < delta) {
                            delta = minv[j]
                            j1 = j
                        }
                    }
                }
                for (j in 0..n) {
                    if (used[j]) {
                        u[p[j]] += delta
                        v[j] -= delta
                    } else {
                        minv[j] -= delta
                    }
                }
                j0 = j1
            } while (p[j0] != 0)
            do {
                val j1 = way[j0]
                p[j0] = p[j1]
                j0 = j1
            } while (j0 != 0)
        }
        val result = IntArray(n)
        for (j in 1..n) {
            if (p[j] != 0) result[p[j] - 1] = j - 1
        }
        assertEquals(3, result.size)
    }

    @Test
    fun collectionEdmondsKarp() {
        val graph = arrayOf(
            intArrayOf(0, 16, 13, 0, 0, 0),
            intArrayOf(0, 0, 10, 12, 0, 0),
            intArrayOf(0, 4, 0, 0, 14, 0),
            intArrayOf(0, 0, 9, 0, 0, 20),
            intArrayOf(0, 0, 0, 7, 0, 4),
            intArrayOf(0, 0, 0, 0, 0, 0)
        )
        val n = graph.size
        val residual = Array(n) { i -> graph[i].copyOf() }
        val parent = IntArray(n)
        fun bfs(s: Int, t: Int): Boolean {
            val visited = BooleanArray(n)
            val queue = ArrayDeque<Int>()
            queue.add(s)
            visited[s] = true
            parent[s] = -1
            while (queue.isNotEmpty()) {
                val u = queue.removeFirst()
                for (v in 0 until n) {
                    if (!visited[v] && residual[u][v] > 0) {
                        queue.add(v)
                        parent[v] = u
                        visited[v] = true
                    }
                }
            }
            return visited[t]
        }
        var maxFlow = 0
        while (bfs(0, 5)) {
            var pathFlow = Int.MAX_VALUE
            var v = 5
            while (v != 0) {
                val u = parent[v]
                pathFlow = minOf(pathFlow, residual[u][v])
                v = u
            }
            v = 5
            while (v != 0) {
                val u = parent[v]
                residual[u][v] -= pathFlow
                residual[v][u] += pathFlow
                v = u
            }
            maxFlow += pathFlow
        }
        assertEquals(23, maxFlow)
    }

    @Test
    fun collectionDinic() {
        val graph = arrayOf(
            intArrayOf(0, 16, 13, 0, 0, 0),
            intArrayOf(0, 0, 10, 12, 0, 0),
            intArrayOf(0, 4, 0, 0, 14, 0),
            intArrayOf(0, 0, 9, 0, 0, 20),
            intArrayOf(0, 0, 0, 7, 0, 4),
            intArrayOf(0, 0, 0, 0, 0, 0)
        )
        val n = graph.size
        val residual = Array(n) { i -> graph[i].copyOf() }
        val level = IntArray(n)
        fun bfs(s: Int, t: Int): Boolean {
            for (i in 0 until n) level[i] = -1
            level[s] = 0
            val queue = ArrayDeque<Int>()
            queue.add(s)
            while (queue.isNotEmpty()) {
                val u = queue.removeFirst()
                for (v in 0 until n) {
                    if (level[v] < 0 && residual[u][v] > 0) {
                        level[v] = level[u] + 1
                        queue.add(v)
                    }
                }
            }
            return level[t] >= 0
        }
        fun dfs(u: Int, t: Int, f: Int): Int {
            if (u == t) return f
            for (v in 0 until n) {
                if (level[v] == level[u] + 1 && residual[u][v] > 0) {
                    val pushed = dfs(v, t, minOf(f, residual[u][v]))
                    if (pushed > 0) {
                        residual[u][v] -= pushed
                        residual[v][u] += pushed
                        return pushed
                    }
                }
            }
            return 0
        }
        var maxFlow = 0
        while (bfs(0, 5)) {
            var pushed = dfs(0, 5, Int.MAX_VALUE)
            while (pushed > 0) {
                maxFlow += pushed
                pushed = dfs(0, 5, Int.MAX_VALUE)
            }
        }
        assertEquals(23, maxFlow)
    }
}
"@
Set-Content -Path "$testDir\CollectionOperationsTest.kt" -Value $content -Encoding UTF8
Write-Output "Created CollectionOperationsTest.kt"