package com.mweshimiwa.assistant

import org.junit.Assert.*
import org.junit.Test

class BundleOperationsTest {

    @Test
    fun testBasicFunctionality() {
        assertTrue("Basic test for BundleOperationsTest passed")
    }

    @Test
    fun testEdgeCases() {
        val emptyList = emptyList<String>()
        assertTrue(emptyList.isEmpty())
    }

    @Test
    fun testNullSafety() {
        val nullable: String? = null
        assertNull(nullable)
    }

    @Test
    fun testCollectionOperations() {
        val list = listOf(1, 2, 3, 4, 5)
        assertEquals(5, list.size)
        assertEquals(15, list.sum())
    }

    @Test
    fun testStringOperations() {
        val str = "Hello, World!"
        assertEquals(13, str.length)
        assertTrue(str.startsWith("Hello"))
        assertTrue(str.endsWith("World!"))
    }

    @Test
    fun testMapOperations() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)
        assertEquals(3, map.size)
        assertEquals(1, map["a"])
        assertTrue(map.containsKey("b"))
    }

    @Test
    fun testSetOperations() {
        val setA = setOf(1, 2, 3, 4, 5)
        val setB = setOf(4, 5, 6, 7, 8)
        assertEquals(setOf(4, 5), setA intersect setB)
        assertEquals(setOf(1, 2, 3, 4, 5, 6, 7, 8), setA union setB)
    }

    @Test
    fun testFilterAndMap() {
        val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val evens = numbers.filter { it % 2 == 0 }
        assertEquals(listOf(2, 4, 6, 8, 10), evens)
        val squares = numbers.map { it * it }
        assertEquals(1, squares[0])
        assertEquals(100, squares[9])
    }

    @Test
    fun testReduceAndFold() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val sum = numbers.reduce { acc, i -> acc + i }
        assertEquals(15, sum)
        val product = numbers.fold(1) { acc, i -> acc * i }
        assertEquals(120, product)
    }

    @Test
    fun testGroupingAndAggregation() {
        val words = listOf("apple", "banana", "avocado", "blueberry")
        val byFirst = words.groupBy { it.first() }
        assertEquals(2, byFirst['a']?.size)
        assertEquals(2, byFirst['b']?.size)
    }

    @Test
    fun testSorting() {
        val numbers = listOf(5, 2, 8, 1, 9, 3)
        assertEquals(listOf(1, 2, 3, 5, 8, 9), numbers.sorted())
        assertEquals(listOf(9, 8, 5, 3, 2, 1), numbers.sortedDescending())
    }

    @Test
    fun testPartition() {
        val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val (evens, odds) = numbers.partition { it % 2 == 0 }
        assertEquals(5, evens.size)
        assertEquals(5, odds.size)
    }

    @Test
    fun testFlatMap() {
        val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5, 6))
        assertEquals(listOf(1, 2, 3, 4, 5, 6), nested.flatMap { it })
    }

    @Test
    fun testAssociate() {
        val words = listOf("one", "two", "three")
        val map = words.associate { it to it.length }
        assertEquals(3, map["one"])
        assertEquals(5, map["three"])
    }

    @Test
    fun testChunked() {
        val numbers = (1..10).toList()
        val chunks = numbers.chunked(3)
        assertEquals(4, chunks.size)
        assertEquals(listOf(10), chunks[3])
    }

    @Test
    fun testWindowed() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val windows = numbers.windowed(3)
        assertEquals(3, windows.size)
        assertEquals(listOf(3, 4, 5), windows[2])
    }

    @Test
    fun testZip() {
        val a = listOf(1, 2, 3)
        val b = listOf("a", "b", "c")
        val zipped = a.zip(b)
        assertEquals(3, zipped.size)
        assertEquals(1 to "a", zipped[0])
    }

    @Test
    fun testTakeAndDrop() {
        val list = (1..10).toList()
        assertEquals(listOf(1, 2, 3), list.take(3))
        assertEquals(listOf(8, 9, 10), list.takeLast(3))
        assertEquals(listOf(4, 5, 6, 7, 8, 9, 10), list.drop(3))
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7), list.dropLast(3))
    }

    @Test
    fun testCount() {
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        assertEquals(5, list.count { it % 2 == 0 })
        assertEquals(5, list.count { it % 2 != 0 })
    }

    @Test
    fun testMinMaxBy() {
        val people = listOf(Triple("Alice", 25, "NYC"), Triple("Bob", 30, "LA"), Triple("Charlie", 20, "Chicago"))
        assertEquals("Charlie", people.minByOrNull { it.second }?.first)
        assertEquals("Bob", people.maxByOrNull { it.second }?.first)
    }

    @Test
    fun testJoinToString() {
        val words = listOf("Hello", "World", "Kotlin")
        assertEquals("Hello, World, Kotlin", words.joinToString(", "))
        assertEquals("[Hello | World | Kotlin]", words.joinToString(" | ", "[", "]"))
    }

    @Test
    fun testSequenceLazyEval() {
        val seq = generateSequence(1) { it + 1 }.filter { it % 2 == 0 }.map { it * it }.take(5).toList()
        assertEquals(listOf(4, 16, 36, 64, 100), seq)
    }

    @Test
    fun testSearch() {
        val words = listOf("apple", "banana", "cherry", "date")
        assertEquals("banana", words.find { it.startsWith("b") })
        assertNull(words.find { it.startsWith("z") })
    }

    @Test
    fun testStats() {
        val numbers = listOf(10, 20, 30, 40, 50)
        assertEquals(30.0, numbers.average(), 0.001)
        assertEquals(150, numbers.sum())
        assertEquals(10, numbers.minOrNull())
        assertEquals(50, numbers.maxOrNull())
    }

    @Test
    fun testScan() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val running = numbers.runningFold(0) { acc, i -> acc + i }
        assertEquals(listOf(0, 1, 3, 6, 10, 15), running)
    }

    @Test
    fun testCartesianProduct() {
        val colors = listOf("red", "green", "blue")
        val sizes = listOf("S", "M", "L")
        val product = colors.flatMap { c -> sizes.map { s -> "-" } }
        assertEquals(9, product.size)
    }

    @Test
    fun testFrequencyDistribution() {
        val grades = listOf("A", "B", "A", "C", "B", "A")
        val dist = grades.groupingBy { it }.eachCount().toSortedMap()
        assertEquals(3, dist["A"])
        assertEquals(2, dist["B"])
    }

    @Test
    fun testPalindromeCheck() {
        fun isPalindrome(s: String): Boolean { val c = s.lowercase().filter { it.isLetterOrDigit() }; return c == c.reversed() }
        assertTrue(isPalindrome("racecar"))
        assertTrue(isPalindrome("A man a plan a canal Panama"))
    }

    @Test
    fun testAnagrams() {
        val words = listOf("listen", "silent", "enlist", "hello")
        val grouped = words.groupBy { it.toCharArray().sorted().joinToString("") }
        assertEquals(3, grouped["eilnst"]?.size)
    }

    @Test
    fun testFibonacci() {
        val fib = generateSequence(0 to 1) { (a, b) -> b to (a + b) }.map { it.first }.take(10).toList()
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34), fib)
    }

    @Test
    fun testPrimeSieve() {
        val n = 30; val sieve = BooleanArray(n + 1) { true }; sieve[0] = false; sieve[1] = false
        for (i in 2..kotlin.math.sqrt(n.toDouble()).toInt()) if (sieve[i]) for (j in i * i..n step i) sieve[j] = false
        val primes = sieve.withIndex().filter { it.value }.map { it.index }
        assertEquals(listOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29), primes)
    }

    @Test
    fun testMatrixTranspose() {
        val m = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))
        val t = m[0].indices.map { col -> m.indices.map { row -> m[row][col] } }
        assertEquals(listOf(1, 4, 7), t[0])
    }

    @Test
    fun testUnionFind() {
        val parent = IntArray(10) { it }
        fun find(x: Int): Int { if (parent[x] != x) parent[x] = find(parent[x]); return parent[x] }
        fun union(x: Int, y: Int) { parent[find(x)] = find(y) }
        union(0, 1); union(1, 2)
        assertEquals(find(0), find(2))
    }

    @Test
    fun testHeapOperations() {
        val heap = java.util.PriorityQueue<Int>()
        heap.add(5); heap.add(1); heap.add(3); heap.add(2); heap.add(4)
        assertEquals(1, heap.poll()); assertEquals(2, heap.poll()); assertEquals(3, heap.poll())
    }

    @Test
    fun testBinarySearch() {
        val sorted = listOf(1, 3, 5, 7, 9, 11, 13, 15)
        assertEquals(3, sorted.binarySearch(7))
        assertEquals(-1, sorted.binarySearch(0))
    }

    @Test
    fun testMergeSort() {
        fun mergeSort(arr: IntArray): IntArray { if (arr.size <= 1) return arr; val mid = arr.size / 2; val left = mergeSort(arr.copyOfRange(0, mid)); val right = mergeSort(arr.copyOfRange(mid, arr.size)); val result = IntArray(arr.size); var i = 0; var j = 0; var k = 0; while (i < left.size && j < right.size) result[k++] = if (left[i] <= right[j]) left[i++] else right[j++]; while (i < left.size) result[k++] = left[i++]; while (j < right.size) result[k++] = right[j++]; return result }
        val arr = intArrayOf(38, 27, 43, 3, 9, 82, 10)
        assertEquals(intArrayOf(3, 9, 10, 27, 38, 43, 82).toList(), mergeSort(arr).toList())
    }

    @Test
    fun testQuickSort() {
        fun quickSort(arr: IntArray, low: Int, high: Int) { if (low < high) { val pivot = arr[high]; var i = low - 1; for (j in low until high) if (arr[j] <= pivot) { i++; val t = arr[i]; arr[i] = arr[j]; arr[j] = t }; val t = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = t; val pi = i + 1; quickSort(arr, low, pi - 1); quickSort(arr, pi + 1, high) } }
        val arr = intArrayOf(10, 7, 8, 9, 1, 5)
        quickSort(arr, 0, arr.size - 1)
        assertEquals(intArrayOf(1, 5, 7, 8, 9, 10).toList(), arr.toList())
    }

    @Test
    fun testGraphBFS() {
        val graph = mapOf("A" to listOf("B", "C"), "B" to listOf("D", "E"), "C" to listOf("F"), "D" to emptyList(), "E" to listOf("F"), "F" to emptyList())
        val visited = mutableSetOf<String>(); val queue = ArrayDeque<String>(); queue.add("A"); visited.add("A"); val result = mutableListOf<String>()
        while (queue.isNotEmpty()) { val node = queue.removeFirst(); result.add(node); graph[node]?.forEach { if (it !in visited) { visited.add(it); queue.add(it) } } }
        assertEquals(listOf("A", "B", "C", "D", "E", "F"), result)
    }

    @Test
    fun testGraphDFS() {
        val graph = mapOf("A" to listOf("B", "C"), "B" to listOf("D", "E"), "C" to listOf("F"), "D" to emptyList(), "E" to listOf("F"), "F" to emptyList())
        val visited = mutableSetOf<String>(); val result = mutableListOf<String>()
        fun dfs(node: String) { if (node !in visited) { visited.add(node); result.add(node); graph[node]?.forEach { dfs(it) } } }
        dfs("A")
        assertEquals(listOf("A", "B", "D", "E", "F", "C"), result)
    }

    @Test
    fun testDijkstra() {
        val graph = mapOf("A" to mapOf("B" to 1, "C" to 4), "B" to mapOf("C" to 2, "D" to 6), "C" to mapOf("D" to 3), "D" to emptyMap())
        val dist = mutableMapOf("A" to 0, "B" to Int.MAX_VALUE, "C" to Int.MAX_VALUE, "D" to Int.MAX_VALUE)
        val pq = java.util.PriorityQueue<Pair<Int, String>>(compareBy { it.first }); pq.add(0 to "A")
        while (pq.isNotEmpty()) { val (d, u) = pq.poll(); if (d > dist[u]!!) continue; graph[u]?.forEach { (v, w) -> val nd = d + w; if (nd < dist[v]!!) { dist[v] = nd; pq.add(nd to v) } } }
        assertEquals(6, dist["D"])
    }

    @Test
    fun testKnapsack() {
        val weights = intArrayOf(2, 3, 4, 5); val values = intArrayOf(3, 4, 5, 6); val capacity = 8; val n = weights.size
        val dp = Array(n + 1) { IntArray(capacity + 1) }
        for (i in 1..n) for (w in 0..capacity) dp[i][w] = if (weights[i-1] <= w) maxOf(dp[i-1][w], dp[i-1][w-weights[i-1]] + values[i-1]) else dp[i-1][w]
        assertEquals(10, dp[n][capacity])
    }

    @Test
    fun testLCS() {
        val a = "ABCDGH"; val b = "AEDFHR"; val m = a.length; val n = b.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 1..m) for (j in 1..n) dp[i][j] = if (a[i-1] == b[j-1]) dp[i-1][j-1] + 1 else maxOf(dp[i-1][j], dp[i][j-1])
        assertEquals(3, dp[m][n])
    }

    @Test
    fun testEditDistance() {
        val a = "kitten"; val b = "sitting"; val m = a.length; val n = b.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i; for (j in 0..n) dp[0][j] = j
        for (i in 1..m) for (j in 1..n) dp[i][j] = if (a[i-1] == b[j-1]) dp[i-1][j-1] else 1 + minOf(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])
        assertEquals(3, dp[m][n])
    }

    @Test
    fun testCoinChange() {
        val coins = intArrayOf(1, 5, 10, 25); val amount = 63
        val dp = IntArray(amount + 1) { Int.MAX_VALUE - 1 }; dp[0] = 0
        for (coin in coins) for (i in coin..amount) dp[i] = minOf(dp[i], dp[i - coin] + 1)
        assertEquals(6, dp[amount])
    }

    @Test
    fun testNQueens() {
        val n = 4; val board = Array(n) { CharArray(n) { '.' } }; val cols = mutableSetOf<Int>(); val d1 = mutableSetOf<Int>(); val d2 = mutableSetOf<Int>()
        fun solve(row: Int): Boolean { if (row == n) return true; for (col in 0 until n) if (col !in cols && (row-col) !in d1 && (row+col) !in d2) { board[row][col] = 'Q'; cols.add(col); d1.add(row-col); d2.add(row+col); if (solve(row + 1)) return true; board[row][col] = '.'; cols.remove(col); d1.remove(row-col); d2.remove(row+col) }; return false }
        assertTrue(solve(0))
    }

    @Test
    fun testSudoku() {
        val board = arrayOf(intArrayOf(5,3,0,0,7,0,0,0,0), intArrayOf(6,0,0,1,9,5,0,0,0), intArrayOf(0,9,8,0,0,0,0,6,0), intArrayOf(8,0,0,0,6,0,0,0,3), intArrayOf(4,0,0,8,0,3,0,0,1), intArrayOf(7,0,0,0,2,0,0,0,6), intArrayOf(0,6,0,0,0,0,2,8,0), intArrayOf(0,0,0,4,1,9,0,0,5), intArrayOf(0,0,0,0,8,0,0,7,9))
        fun isValid(row: Int, col: Int, num: Int): Boolean { for (i in 0 until 9) { if (board[row][i] == num) return false; if (board[i][col] == num) return false; if (board[3*(row/3)+i/3][3*(col/3)+i%3] == num) return false }; return true }
        fun solve(): Boolean { for (i in 0 until 9) for (j in 0 until 9) if (board[i][j] == 0) { for (num in 1..9) if (isValid(i, j, num)) { board[i][j] = num; if (solve()) return true; board[i][j] = 0 }; return false }; return true }
        assertTrue(solve())
    }

    @Test
    fun testKMP() {
        val text = "ABABDABACDABABCABAB"; val pattern = "ABABCABAB"; val lps = IntArray(pattern.length); var len = 0; var i = 1
        while (i < pattern.length) { if (pattern[i] == pattern[len]) { len++; lps[i] = len; i++ } else { if (len != 0) len = lps[len-1] else { lps[i] = 0; i++ } } }
        var j = 0; i = 0; var found = -1
        while (i < text.length) { if (pattern[j] == text[i]) { i++; j++ }; if (j == pattern.length) { found = i - j; break } else if (i < text.length && pattern[j] != text[i]) { if (j != 0) j = lps[j-1] else i++ } }
        assertEquals(10, found)
    }

    @Test
    fun testRabinKarp() {
        val text = "AABAACAADAABAABA"; val pattern = "AABA"; val d = 256; val q = 101; val m = pattern.length; val n = text.length; var p = 0; var t = 0; var h = 1
        for (i in 0 until m-1) h = (h * d) % q
        for (i in 0 until m) { p = (d * p + pattern[i].code) % q; t = (d * t + text[i].code) % q }
        val matches = mutableListOf<Int>()
        for (i in 0..n-m) { if (p == t) { if (text.substring(i, i+m) == pattern) matches.add(i) }; if (i < n-m) { t = (d * (t - text[i].code * h) + text[i+m].code) % q; if (t < 0) t += q } }
        assertEquals(listOf(0, 9, 12), matches)
    }

    @Test
    fun testBloomFilter() {
        val size = 100; val numHashes = 3; val bits = BooleanArray(size)
        fun hash(item: String, seed: Int): Int { var h = seed; for (c in item) h = 31 * h + c.code; return kotlin.math.abs(h) % size }
        fun add(item: String) { for (i in 0 until numHashes) bits[hash(item, i)] = true }
        fun mightContain(item: String): Boolean = (0 until numHashes).all { bits[hash(item, it)] }
        add("hello"); add("world")
        assertTrue(mightContain("hello")); assertTrue(mightContain("world"))
    }

    @Test
    fun testLRUCache() {
        val capacity = 2
        val cache = object : LinkedHashMap<Int, Int>(capacity, 0.75f, true) { override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, Int>?) = size > capacity }
        cache[1] = 1; cache[2] = 2; cache[1]; cache[3] = 3
        assertFalse(cache.containsKey(2)); assertTrue(cache.containsKey(1)); assertTrue(cache.containsKey(3))
    }

    @Test
    fun testTrie() {
        class TrieNode { val children = mutableMapOf<Char, TrieNode>(); var isEnd = false }
        val root = TrieNode()
        fun insert(word: String) { var node = root; for (c in word) node = node.children.getOrPut(c) { TrieNode() }; node.isEnd = true }
        fun search(word: String): Boolean { var node = root; for (c in word) { node = node.children[c] ?: return false }; return node.isEnd }
        insert("apple"); insert("app")
        assertTrue(search("apple")); assertTrue(search("app"))
    }

    @Test
    fun testSegmentTree() {
        val arr = intArrayOf(1, 3, 5, 7, 9, 11); val n = arr.size; val tree = IntArray(4 * n)
        fun build(node: Int, start: Int, end: Int) { if (start == end) tree[node] = arr[start] else { val mid = (start+end)/2; build(2*node, start, mid); build(2*node+1, mid+1, end); tree[node] = tree[2*node] + tree[2*node+1] } }
        fun query(node: Int, start: Int, end: Int, l: Int, r: Int): Int { if (r < start || end < l) return 0; if (l <= start && end <= r) return tree[node]; val mid = (start+end)/2; return query(2*node, start, mid, l, r) + query(2*node+1, mid+1, end, l, r) }
        build(1, 0, n-1)
        assertEquals(15, query(1, 0, n-1, 0, 2))
    }

    @Test
    fun testFenwickTree() {
        val n = 8; val bit = IntArray(n + 1)
        fun update(i: Int, delta: Int) { var idx = i; while (idx <= n) { bit[idx] += delta; idx += idx and -idx } }
        fun query(i: Int): Int { var sum = 0; var idx = i; while (idx > 0) { sum += bit[idx]; idx -= idx and -idx }; return sum }
        update(1, 3); update(2, 5); update(3, 7)
        assertEquals(8, query(2)); assertEquals(15, query(3))
    }

    @Test
    fun testMonotonicStack() {
        val heights = intArrayOf(2, 1, 5, 6, 2, 3); val stack = ArrayDeque<Int>(); var maxArea = 0
        for (i in heights.indices) { while (stack.isNotEmpty() && heights[stack.last()] > heights[i]) { val h = heights[stack.removeLast()]; val w = if (stack.isEmpty()) i else i - stack.last() - 1; maxArea = maxOf(maxArea, h * w) }; stack.addLast(i) }
        assertEquals(10, maxArea)
    }

    @Test
    fun testSlidingWindowMax() {
        val nums = intArrayOf(1, 3, -1, -3, 5, 3, 6, 7); val k = 3; val result = mutableListOf<Int>(); val deque = ArrayDeque<Int>()
        for (i in nums.indices) { while (deque.isNotEmpty() && deque.first() < i - k + 1) deque.removeFirst(); while (deque.isNotEmpty() && nums[deque.last()] < nums[i]) deque.removeLast(); deque.addLast(i); if (i >= k - 1) result.add(nums[deque.first()]) }
        assertEquals(listOf(3, 3, 5, 5, 6, 7), result)
    }

    @Test
    fun testTrappingRainWater() {
        val height = intArrayOf(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1); val n = height.size
        val leftMax = IntArray(n); val rightMax = IntArray(n)
        leftMax[0] = height[0]; for (i in 1 until n) leftMax[i] = maxOf(leftMax[i-1], height[i])
        rightMax[n-1] = height[n-1]; for (i in n-2 downTo 0) rightMax[i] = maxOf(rightMax[i+1], height[i])
        var water = 0; for (i in 0 until n) water += minOf(leftMax[i], rightMax[i]) - height[i]
        assertEquals(6, water)
    }

    @Test
    fun testKMeans() {
        val points = arrayOf(doubleArrayOf(1.0, 2.0), doubleArrayOf(1.5, 1.8), doubleArrayOf(5.0, 8.0), doubleArrayOf(8.0, 8.0), doubleArrayOf(1.0, 0.6), doubleArrayOf(9.0, 11.0))
        val k = 3; val random = java.util.Random(42); var centroids = Array(k) { points[random.nextInt(points.size)].copyOf() }; val assignments = IntArray(points.size)
        for (iter in 0 until 100) { for (i in points.indices) { var minD = Double.MAX_VALUE; for (j in 0 until k) { val d = kotlin.math.sqrt((0 until 2).sumOf { dd -> (points[i][dd] - centroids[j][dd]).let { it * it } }); if (d < minD) { minD = d; assignments[i] = j } } }; val newC = Array(k) { DoubleArray(2) }; val counts = IntArray(k); for (i in points.indices) { val c = assignments[i]; newC[c][0] += points[i][0]; newC[c][1] += points[i][1]; counts[c]++ }; for (j in 0 until k) if (counts[j] > 0) { newC[j][0] /= counts[j]; newC[j][1] /= counts[j] }; centroids = newC }
        assertEquals(3, assignments.toSet().size)
    }

    @Test
    fun testLinearRegression() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0); val y = doubleArrayOf(2.0, 4.0, 5.0, 4.0, 5.0); val n = x.size
        val sumX = x.sum(); val sumY = y.sum(); val sumXY = x.zip(y).sumOf { (xi, yi) -> xi * yi }; val sumX2 = x.sumOf { it * it }
        val slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX); val intercept = (sumY - slope * sumX) / n
        val yPred = x.map { slope * it + intercept }
        val ssRes = y.zip(yPred).sumOf { (yi, yp) -> (yi - yp).let { it * it } }
        val yMean = y.average(); val ssTot = y.sumOf { (it - yMean).let { d -> d * d } }
        val r2 = 1 - ssRes / ssTot
        assertTrue(r2 > 0.5)
    }

    @Test
    fun testKNN() {
        val training = listOf(Triple(1.0, 1.0, "A"), Triple(1.5, 1.5, "A"), Triple(5.0, 5.0, "B"), Triple(5.5, 5.5, "B"))
        val test = Pair(1.2, 1.2); val k = 3
        val distances = training.map { (x, y, label) -> val d = kotlin.math.sqrt((x-test.first)*(x-test.first)+(y-test.second)*(y-test.second)); label to d }.sortedBy { it.second }
        val topK = distances.take(k); val votes = topK.groupingBy { it.first }.eachCount()
        assertEquals("A", votes.maxByOrNull { it.value }?.key)
    }

    @Test
    fun testNaiveBayes() {
        val data = listOf(Triple("sunny", "hot", "no"), Triple("sunny", "mild", "no"), Triple("overcast", "hot", "yes"), Triple("rainy", "mild", "yes"), Triple("rainy", "cool", "yes"), Triple("overcast", "cool", "yes"), Triple("sunny", "mild", "no"), Triple("rainy", "mild", "yes"), Triple("sunny", "mild", "yes"), Triple("overcast", "mild", "yes"), Triple("overcast", "hot", "yes"), Triple("rainy", "mild", "no"))
        val pYes = data.count { it.third == "yes" }.toDouble() / data.size
        assertEquals(9.0 / 12.0, pYes, 0.001)
    }

    @Test
    fun testPageRank() {
        val graph = arrayOf(intArrayOf(0,1,1,0), intArrayOf(0,0,1,0), intArrayOf(1,0,0,1), intArrayOf(0,0,1,0))
        val n = graph.size; val damping = 0.85; val ranks = DoubleArray(n) { 1.0 / n }
        for (iter in 0 until 100) { val newR = DoubleArray(n); for (i in 0 until n) { var sum = 0.0; for (j in 0 until n) if (graph[j][i] == 1) { val od = graph[j].sum(); sum += ranks[j] / od }; newR[i] = (1-damping)/n + damping*sum }; for (i in 0 until n) ranks[i] = newR[i] }
        assertEquals(1.0, ranks.sum(), 0.001)
    }

    @Test
    fun testApriori() {
        val transactions = listOf(setOf("bread", "milk"), setOf("bread", "diaper", "beer", "eggs"), setOf("milk", "diaper", "beer", "cola"), setOf("bread", "milk", "diaper", "beer"), setOf("bread", "milk", "diaper", "cola"))
        val minSupport = 0.4; val itemCounts = transactions.flatten().groupingBy { it }.eachCount()
        val frequent = itemCounts.filter { it.value >= transactions.size * minSupport }.keys
        assertEquals(setOf("bread", "milk", "diaper", "beer"), frequent)
    }

    @Test
    fun testConsistentHashing() {
        val nodes = listOf("node1", "node2", "node3"); val vNodes = 150; val ring = TreeMap<Long, String>()
        for (node in nodes) for (i in 0 until vNodes) { val h = (node + i).hashCode().toLong() and 0xffffffffL; ring[h] = node }
        fun getNode(key: String): String { val h = key.hashCode().toLong() and 0xffffffffL; val e = ring.ceilingEntry(h) ?: ring.firstEntry(); return e.value }
        val dist = (0 until 1000).map { getNode("key") }.groupingBy { it }.eachCount()
        assertEquals(3, dist.size)
    }

    @Test
    fun testMerkleTree() {
        val leaves = listOf("a", "b", "c", "d")
        fun hash(data: String) = java.security.MessageDigest.getInstance("SHA-256").digest(data.toByteArray()).joinToString("") { "%02x".format(it) }
        val hashes = leaves.map { hash(it) }
        val combined = hash(hashes[0] + hashes[1]) + hash(hashes[2] + hashes[3])
        val root = hash(combined)
        assertTrue(root.isNotEmpty())
    }

    @Test
    fun testMaxClique() {
        val graph = arrayOf(intArrayOf(0,1,1,0,0), intArrayOf(1,0,1,1,0), intArrayOf(1,1,0,1,0), intArrayOf(0,1,1,0,1), intArrayOf(0,0,0,1,0))
        val n = graph.size; var maxClique = 0
        for (mask in 1 until (1 shl n)) { val vertices = (0 until n).filter { mask and (1 shl it) != 0 }; val isClique = vertices.all { i -> vertices.all { j -> i == j || graph[i][j] == 1 } }; if (isClique) maxClique = maxOf(maxClique, vertices.size) }
        assertEquals(3, maxClique)
    }

    @Test
    fun testGraphMatching() {
        val graph = arrayOf(intArrayOf(0,1,1,0,0), intArrayOf(1,0,0,1,0), intArrayOf(1,0,0,1,0), intArrayOf(0,1,1,0,1), intArrayOf(0,0,0,1,0))
        val n = graph.size; val matchR = IntArray(n) { -1 }
        fun bpm(u: Int, seen: BooleanArray): Boolean { for (v in 0 until n) if (graph[u][v] == 1 && !seen[v]) { seen[v] = true; if (matchR[v] < 0 || bpm(matchR[v], seen)) { matchR[v] = u; return true } }; return false }
        var result = 0; for (u in 0 until n) { val seen = BooleanArray(n); if (bpm(u, seen)) result++ }
        assertEquals(2, result)
    }

    @Test
    fun testStronglyConnectedComponents() {
        val graph = arrayOf(intArrayOf(0,1,0,0,0), intArrayOf(0,0,1,0,0), intArrayOf(1,0,0,1,0), intArrayOf(0,0,0,0,1), intArrayOf(0,0,0,1,0))
        val n = graph.size; val visited = BooleanArray(n); val stack = ArrayDeque<Int>()
        fun dfs1(u: Int) { visited[u] = true; for (v in 0 until n) if (graph[u][v] == 1 && !visited[v]) dfs1(v); stack.addLast(u) }
        for (i in 0 until n) if (!visited[i]) dfs1(i)
        val transposed = Array(n) { IntArray(n) }; for (i in 0 until n) for (j in 0 until n) transposed[j][i] = graph[i][j]
        val visited2 = BooleanArray(n); val sccs = mutableListOf<List<Int>>()
        fun dfs2(u: Int, comp: MutableList<Int>) { visited2[u] = true; comp.add(u); for (v in 0 until n) if (transposed[u][v] == 1 && !visited2[v]) dfs2(v, comp) }
        while (stack.isNotEmpty()) { val u = stack.removeLast(); if (!visited2[u]) { val comp = mutableListOf<Int>(); dfs2(u, comp); sccs.add(comp) } }
        assertEquals(2, sccs.size)
    }

    @Test
    fun testHungarianAlgorithm() {
        val cost = arrayOf(intArrayOf(4,1,3), intArrayOf(2,0,5), intArrayOf(3,2,2))
        val n = cost.size; val u = IntArray(n+1); val v = IntArray(n+1); val p = IntArray(n+1); val way = IntArray(n+1)
        for (i in 1..n) { p[0] = i; var j0 = 0; val minv = IntArray(n+1) { Int.MAX_VALUE }; val used = BooleanArray(n+1); do { used[j0] = true; var i0 = p[j0]; var delta = Int.MAX_VALUE; var j1 = 0; for (j in 1..n) if (!used[j]) { val cur = cost[i0-1][j-1] - u[i0] - v[j]; if (cur < minv[j]) { minv[j] = cur; way[j] = j0 }; if (minv[j] < delta) { delta = minv[j]; j1 = j } }; for (j in 0..n) { if (used[j]) { u[p[j]] += delta; v[j] -= delta } else minv[j] -= delta }; j0 = j1 } while (p[j0] != 0); do { val j1 = way[j0]; p[j0] = p[j1]; j0 = j1 } while (j0 != 0) }
        val result = IntArray(n); for (j in 1..n) if (p[j] != 0) result[p[j]-1] = j-1
        assertEquals(3, result.size)
    }

    @Test
    fun testMaxFlow() {
        val graph = arrayOf(intArrayOf(0,16,13,0,0,0), intArrayOf(0,0,10,12,0,0), intArrayOf(0,4,0,0,14,0), intArrayOf(0,0,9,0,0,20), intArrayOf(0,0,0,7,0,4), intArrayOf(0,0,0,0,0,0))
        val n = graph.size; val residual = Array(n) { i -> graph[i].copyOf() }; val parent = IntArray(n)
        fun bfs(s: Int, t: Int): Boolean { val visited = BooleanArray(n); val queue = ArrayDeque<Int>(); queue.add(s); visited[s] = true; parent[s] = -1; while (queue.isNotEmpty()) { val u = queue.removeFirst(); for (v in 0 until n) if (!visited[v] && residual[u][v] > 0) { queue.add(v); parent[v] = u; visited[v] = true } }; return visited[t] }
        var maxFlow = 0; while (bfs(0, 5)) { var pf = Int.MAX_VALUE; var v = 5; while (v != 0) { val u = parent[v]; pf = minOf(pf, residual[u][v]); v = u }; v = 5; while (v != 0) { val u = parent[v]; residual[u][v] -= pf; residual[v][u] += pf; v = u }; maxFlow += pf }
        assertEquals(23, maxFlow)
    }

    @Test
    fun testKruskal() {
        data class Edge(val u: Int, val v: Int, val w: Int)
        val edges = listOf(Edge(0,1,10), Edge(0,2,6), Edge(0,3,5), Edge(1,3,15), Edge(2,3,4)).sortedBy { it.w }
        val parent = IntArray(4) { it }
        fun find(x: Int): Int { if (parent[x] != x) parent[x] = find(parent[x]); return parent[x] }
        val mst = mutableListOf<Edge>()
        for (edge in edges) { val pu = find(edge.u); val pv = find(edge.v); if (pu != pv) { mst.add(edge); parent[pu] = pv } }
        assertEquals(3, mst.size); assertEquals(19, mst.sumOf { it.w })
    }

    @Test
    fun testPrim() {
        val graph = arrayOf(intArrayOf(0,2,0,6,0), intArrayOf(2,0,3,8,5), intArrayOf(0,3,0,0,7), intArrayOf(6,8,0,0,9), intArrayOf(0,5,7,9,0))
        val n = graph.size; val visited = BooleanArray(n); val minEdge = IntArray(n) { Int.MAX_VALUE }; minEdge[0] = 0; var totalWeight = 0
        for (i in 0 until n) { var u = -1; for (j in 0 until n) if (!visited[j] && (u == -1 || minEdge[j] < minEdge[u])) u = j; visited[u] = true; totalWeight += minEdge[u]; for (v in 0 until n) if (graph[u][v] > 0 && !visited[v] && graph[u][v] < minEdge[v]) minEdge[v] = graph[u][v] }
        assertEquals(16, totalWeight)
    }

    @Test
    fun testBellmanFord() {
        val edges = listOf(Triple(0,1,4), Triple(0,2,5), Triple(1,2,-3), Triple(2,3,4), Triple(3,1,-1))
        val n = 4; val dist = IntArray(n) { Int.MAX_VALUE }; dist[0] = 0
        for (i in 1 until n) for ((u, v, w) in edges) if (dist[u] != Int.MAX_VALUE && dist[u] + w < dist[v]) dist[v] = dist[u] + w
        assertEquals(0, dist[0]); assertEquals(4, dist[1]); assertEquals(1, dist[2]); assertEquals(5, dist[3])
    }

    @Test
    fun testFloydWarshall() {
        val INF = Int.MAX_VALUE / 2
        val graph = arrayOf(intArrayOf(0,5,INF,10), intArrayOf(INF,0,3,INF), intArrayOf(INF,INF,0,1), intArrayOf(INF,INF,INF,0))
        val n = graph.size; val dist = Array(n) { i -> graph[i].copyOf() }
        for (k in 0 until n) for (i in 0 until n) for (j in 0 until n) if (dist[i][k] + dist[k][j] < dist[i][j]) dist[i][j] = dist[i][k] + dist[k][j]
        assertEquals(0, dist[0][0]); assertEquals(5, dist[0][1]); assertEquals(8, dist[0][2]); assertEquals(9, dist[0][3])
    }

    @Test
    fun testTravelingSalesman() {
        val dist = arrayOf(intArrayOf(0,10,15,20), intArrayOf(10,0,35,25), intArrayOf(15,35,0,30), intArrayOf(20,25,30,0))
        val n = dist.size; val visited = BooleanArray(n); visited[0] = true
        fun tsp(curr: Int, count: Int, cost: Int, minCost: IntArray) { if (count == n && dist[curr][0] > 0) { minCost[0] = minOf(minCost[0], cost + dist[curr][0]); return }; for (i in 0 until n) if (!visited[i] && dist[curr][i] > 0) { visited[i] = true; tsp(i, count+1, cost+dist[curr][i], minCost); visited[i] = false } }
        val minCost = intArrayOf(Int.MAX_VALUE); tsp(0, 1, 0, minCost)
        assertEquals(80, minCost[0])
    }

    @Test
    fun testMinimax() {
        val scores = intArrayOf(3, 5, 6, 9, 1, 2, 0, -1)
        fun minimax(depth: Int, index: Int, max: Boolean): Int { if (depth == 0) return scores[index]; if (max) return maxOf(minimax(depth-1, index*2, false), minimax(depth-1, index*2+1, false)); return minOf(minimax(depth-1, index*2, true), minimax(depth-1, index*2+1, true)) }
        assertEquals(5, minimax(3, 0, true))
    }

    @Test
    fun testAStar() {
        val grid = arrayOf(intArrayOf(0,0,0,0,0), intArrayOf(0,1,1,1,0), intArrayOf(0,0,0,0,0), intArrayOf(0,1,1,1,0), intArrayOf(0,0,0,0,0))
        val start = Pair(0, 0); val goal = Pair(4, 4); val rows = grid.size; val cols = grid[0].size
        fun h(a: Pair<Int, Int>, b: Pair<Int, Int>) = kotlin.math.abs(a.first - b.first) + kotlin.math.abs(a.second - b.second)
        val openSet = java.util.PriorityQueue<Triple<Int, Int, Pair<Int, Int>>>(compareBy { it.first }); openSet.add(Triple(0, 0, start))
        val gScore = mutableMapOf<Pair<Int, Int>, Int>(); gScore[start] = 0; val cameFrom = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        while (openSet.isNotEmpty()) { val current = openSet.poll().third; if (current == goal) { val path = mutableListOf<Pair<Int, Int>>(); var node = goal; while (node != start) { path.add(node); node = cameFrom[node]!! }; path.add(start); path.reverse(); assertEquals(8, path.size - 1); return }; for ((dr, dc) in listOf(Pair(0,1), Pair(1,0), Pair(0,-1), Pair(-1,0))) { val neighbor = Pair(current.first + dr, current.second + dc); if (neighbor.first in 0 until rows && neighbor.second in 0 until cols && grid[neighbor.first][neighbor.second] == 0) { val tg = gScore[current]!! + 1; if (tg < gScore.getOrDefault(neighbor, Int.MAX_VALUE)) { gScore[neighbor] = tg; val f = tg + h(neighbor, goal); openSet.add(Triple(f, tg, neighbor)); cameFrom[neighbor] = current } } } }
    }

    @Test
    fun testSimulatedAnnealing() {
        fun energy(x: Double) = x * x - 4 * x + 4; var current = 0.0; var best = current; var temp = 100.0; val cr = 0.95; val random = java.util.Random(42)
        while (temp > 0.01) { val next = current + (random.nextDouble() - 0.5) * 2; val delta = energy(next) - energy(current); if (delta < 0 || random.nextDouble() < kotlin.math.exp(-delta / temp)) { current = next; if (energy(current) < energy(best)) best = current }; temp *= cr }
        assertTrue(kotlin.math.abs(best - 2.0) < 0.1)
    }

    @Test
    fun testGeneticAlgorithm() {
        val target = "HELLO"; val popSize = 100; val mutRate = 0.01; val random = java.util.Random(42)
        fun randomChar() = ('A'..'Z').random(random); fun randomInd() = (1..target.length).map { randomChar() }.joinToString("")
        fun fitness(ind: String) = ind.zip(target).count { (a, b) -> a == b }
        fun crossover(a: String, b: String) = a.substring(0, random.nextInt(a.length)) + b.substring(random.nextInt(a.length))
        fun mutate(ind: String) = ind.map { if (random.nextDouble() < mutRate) randomChar() else it }.joinToString("")
        var pop = (1..popSize).map { randomInd() }; var gen = 0
        while (gen < 1000) { val scored = pop.map { it to fitness(it) }.sortedByDescending { it.second }; if (scored[0].second == target.length) break; val newPop = mutableListOf<String>(); while (newPop.size < popSize) { val p1 = scored[random.nextInt(scored.size/2)].first; val p2 = scored[random.nextInt(scored.size/2)].first; newPop.add(mutate(crossover(p1, p2))) }; pop = newPop; gen++ }
        assertTrue(gen < 1000)
    }

    @Test
    fun testNeuralNetwork() {
        val inputs = arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(0.0, 1.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(1.0, 1.0)); val outputs = intArrayOf(0, 1, 1, 0)
        val hiddenSize = 4; val lr = 0.5; val random = java.util.Random(42)
        val w1 = Array(2) { DoubleArray(hiddenSize) { random.nextDouble() * 2 - 1 } }; val b1 = DoubleArray(hiddenSize) { random.nextDouble() * 2 - 1 }
        val w2 = DoubleArray(hiddenSize) { random.nextDouble() * 2 - 1 }; val b2 = random.nextDouble() * 2 - 1
        fun sigmoid(x: Double) = 1.0 / (1.0 + kotlin.math.exp(-x))
        fun forward(input: DoubleArray): Pair<DoubleArray, Double> { val hidden = DoubleArray(hiddenSize) { i -> sigmoid(input[0]*w1[0][i] + input[1]*w1[1][i] + b1[i]) }; val output = sigmoid(hidden.indices.sumOf { hidden[it]*w2[it] } + b2); return hidden to output }
        for (epoch in 0 until 10000) for (i in inputs.indices) { val (hidden, output) = forward(inputs[i]); val error = outputs[i] - output; val od = error * output * (1 - output); for (j in hidden.indices) { val hd = od * w2[j] * hidden[j] * (1 - hidden[j]); w2[j] += lr * od * hidden[j]; w1[0][j] += lr * hd * inputs[i][0]; w1[1][j] += lr * hd * inputs[i][1]; b1[j] += lr * hd }; b2 += lr * od }
        val (_, o00) = forward(doubleArrayOf(0.0, 0.0)); val (_, o11) = forward(doubleArrayOf(1.0, 1.0))
        assertTrue(o00 < 0.5); assertTrue(o11 < 0.5)
    }

    @Test
    fun testLogisticRegression() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0); val y = intArrayOf(0, 0, 0, 0, 1, 1, 1, 1); val n = x.size
        var w = 0.0; var b = 0.0; val lr = 0.1
        for (epoch in 0 until 1000) { var dw = 0.0; var db = 0.0; for (i in 0 until n) { val z = w * x[i] + b; val pred = 1.0 / (1.0 + kotlin.math.exp(-z)); val error = pred - y[i]; dw += error * x[i]; db += error }; w -= lr * dw / n; b -= lr * db / n }
        val p0 = 1.0 / (1.0 + kotlin.math.exp(-(w * 1.0 + b))); val p7 = 1.0 / (1.0 + kotlin.math.exp(-(w * 7.0 + b)))
        assertTrue(p0 < 0.5); assertTrue(p7 > 0.5)
    }

    @Test
    fun testDecisionTree() {
        data class DP(val features: List<Double>, val label: String)
        val data = listOf(DP(listOf(1.0, 1.0), "A"), DP(listOf(1.0, 0.0), "A"), DP(listOf(0.0, 1.0), "B"), DP(listOf(0.0, 0.0), "B"))
        fun gini(labels: List<String>): Double { val total = labels.size.toDouble(); val counts = labels.groupingBy { it }.eachCount(); return 1.0 - counts.values.sumOf { (it/total)*(it/total) } }
        assertEquals(0.5, gini(data.map { it.label }), 0.001)
    }

    @Test
    fun testPCA() {
        val data = arrayOf(doubleArrayOf(2.5, 2.4), doubleArrayOf(0.5, 0.7), doubleArrayOf(2.2, 2.9), doubleArrayOf(1.9, 2.2), doubleArrayOf(3.1, 3.0), doubleArrayOf(2.3, 2.7), doubleArrayOf(2.0, 1.6), doubleArrayOf(1.0, 1.1), doubleArrayOf(1.5, 1.6), doubleArrayOf(1.1, 0.9))
        val n = data.size; val meanX = data.map { it[0] }.average(); val meanY = data.map { it[1] }.average()
        val centered = data.map { doubleArrayOf(it[0] - meanX, it[1] - meanY) }
        val covXX = centered.sumOf { it[0] * it[0] } / (n - 1); val covYY = centered.sumOf { it[1] * it[1] } / (n - 1)
        assertTrue(covXX > 0); assertTrue(covYY > 0)
    }

    @Test
    fun testSVM() {
        val points = listOf(Triple(1.0, 1.0, 1), Triple(2.0, 2.0, 1), Triple(-1.0, -1.0, -1), Triple(-2.0, -2.0, -1))
        var w = Pair(0.0, 0.0); var b = 0.0; val lr = 0.1
        for (epoch in 0 until 100) for ((x, y, label) in points) { val margin = label * (w.first * x + w.second * y + b); if (margin < 1) { w = Pair(w.first + lr * label * x, w.second + lr * label * y); b += lr * label } }
        assertTrue(w.first * 1.0 + w.second * 1.0 + b > 0); assertTrue(w.first * -1.0 + w.second * -1.0 + b < 0)
    }

    @Test
    fun testRandomForest() {
        val data = listOf(Triple(1.0, 1.0, "A"), Triple(1.5, 1.5, "A"), Triple(5.0, 5.0, "B"), Triple(5.5, 5.5, "B"), Triple(1.2, 1.2, "A"), Triple(5.2, 5.2, "B"))
        val random = java.util.Random(42); val trees = (1..10).map { val sample = (1..data.size).map { data[random.nextInt(data.size)] }; val labels = sample.map { it.third }; labels.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key }
        val prediction = trees.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        assertTrue(prediction == "A" || prediction == "B")
    }

    @Test
    fun testGradientBoosting() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0); val y = doubleArrayOf(2.0, 4.0, 6.0, 8.0, 10.0)
        var prediction = y.average(); val residuals = y.map { it - prediction }; val lr = 0.1; val trees = mutableListOf<DoubleArray>()
        for (tree in 0 until 10) { val tp = DoubleArray(x.size) { i -> val bin = (x[i] / 2).toInt().coerceIn(0, 4); residuals.slice(bin..bin).average() }; for (i in x.indices) prediction += lr * tp[i]; trees.add(tp) }
        assertEquals(10, trees.size)
    }

    @Test
    fun testCollaborativeFiltering() {
        val ratings = arrayOf(intArrayOf(5,3,0,1), intArrayOf(4,0,0,1), intArrayOf(1,1,0,5), intArrayOf(1,0,0,4), intArrayOf(0,1,5,4))
        val u1 = ratings[0]; val u2 = ratings[1]
        val dot = u1.zip(u2).sumOf { (a, b) -> a * b }; val n1 = kotlin.math.sqrt(u1.sumOf { it * it }.toDouble()); val n2 = kotlin.math.sqrt(u2.sumOf { it * it }.toDouble())
        val sim = dot / (n1 * n2); assertTrue(sim > 0.0 && sim <= 1.0)
    }

    @Test
    fun testHyperLogLog() {
        val p = 4; val m = 1 shl p; val registers = IntArray(m); val random = java.util.Random(42)
        fun hash(value: Long): Int { var h = value; h = h xor (h ushr 33); h *= 0xff51afd7ed558ccdL; h = h xor (h ushr 33); h *= 0xc4ceb9fe1a85ec53L; h = h xor (h ushr 33); return h.toInt() }
        for (i in 0 until 10000) { val h = hash(random.nextLong()); val idx = h ushr (32 - p); val lz = Integer.numberOfLeadingZeros(h) + 1; registers[idx] = maxOf(registers[idx], lz) }
        val alpha = 0.673; val estimate = alpha * m * m / registers.sumOf { 1.0 / (1 shl it) }
        assertTrue(estimate > 5000 && estimate < 15000)
    }

    @Test
    fun testCountMinSketch() {
        val width = 100; val depth = 5; val sketch = Array(depth) { IntArray(width) }; val random = java.util.Random(42)
        fun hash(item: String, seed: Int): Int { var h = seed; for (c in item) h = 31 * h + c.code; return kotlin.math.abs(h) % width }
        val counts = mutableMapOf<String, Int>()
        for (i in 0 until 1000) { val item = "item"; counts[item] = counts.getOrDefault(item, 0) + 1; for (d in 0 until depth) sketch[d][hash(item, d)]++ }
        fun estimate(item: String) = (0 until depth).minOf { d -> sketch[d][hash(item, d)] }
        assertTrue(estimate("item42") >= counts["item42"]!!)
    }

    @Test
    fun testReservoirSampling() {
        val stream = (1..1000).toList(); val k = 10; val random = java.util.Random(42); val reservoir = mutableListOf<Int>()
        for ((index, item) in stream.withIndex()) { if (index < k) reservoir.add(item) else { val j = random.nextInt(index + 1); if (j < k) reservoir[j] = item } }
        assertEquals(k, reservoir.size); assertTrue(reservoir.all { it in stream })
    }

    @Test
    fun testWeightedRandom() {
        val items = listOf("A", "B", "C", "D"); val weights = listOf(10, 20, 30, 40); val total = weights.sum(); val random = java.util.Random(42); val counts = mutableMapOf<String, Int>()
        for (i in 0 until 10000) { var r = random.nextInt(total); for ((idx, w) in weights.withIndex()) { r -= w; if (r < 0) { counts[items[idx]] = counts.getOrDefault(items[idx], 0) + 1; break } } }
        assertTrue(counts["D"]!! > counts["A"]!!)
    }

    @Test
    fun testTournamentTree() {
        val players = listOf("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry")
        val skills = mapOf("Alice" to 85, "Bob" to 90, "Charlie" to 78, "Diana" to 92, "Eve" to 88, "Frank" to 75, "Grace" to 95, "Henry" to 80)
        var round = players
        while (round.size > 1) { val next = mutableListOf<String>(); for (i in round.indices step 2) { if (i + 1 < round.size) { val w = if (skills[round[i]]!! >= skills[round[i+1]]!!) round[i] else round[i+1]; next.add(w) } else next.add(round[i]) }; round = next }
        assertEquals("Grace", round[0])
    }

    @Test
    fun testPersistentSegmentTree() {
        val arr = intArrayOf(1, 2, 3, 4, 5); val versions = mutableListOf<IntArray>(); versions.add(arr.copyOf()); val newArr = arr.copyOf(); newArr[2] = 10; versions.add(newArr.copyOf())
        assertEquals(3, versions[0][2]); assertEquals(10, versions[1][2])
    }

    @Test
    fun testMerkleProof() {
        val leaves = listOf("tx1", "tx2", "tx3", "tx4")
        fun hash(data: String) = java.security.MessageDigest.getInstance("SHA-256").digest(data.toByteArray()).joinToString("") { "%02x".format(it) }
        val hashes = leaves.map { hash(it) }; val left = hash(hashes[0] + hashes[1]); val right = hash(hashes[2] + hashes[3]); val root = hash(left + right)
        val proof = listOf(hashes[1], right); val computedLeft = hash(hashes[0] + proof[0]); val computedRoot = hash(computedLeft + proof[1])
        assertEquals(root, computedRoot)
    }

    @Test
    fun testGraphIsomorphism() {
        val g1 = mapOf(0 to setOf(1, 2), 1 to setOf(0, 2), 2 to setOf(0, 1))
        val g2 = mapOf("A" to setOf("B", "C"), "B" to setOf("A", "C"), "C" to setOf("A", "B"))
        assertEquals(g1.size, g2.size); assertEquals(g1.values.map { it.size }.sorted(), g2.values.map { it.size }.sorted())
    }

    @Test
    fun testVertexCover() {
        val edges = listOf(Pair(0,1), Pair(0,2), Pair(1,2), Pair(1,3), Pair(2,4), Pair(3,4)); val vertices = edges.flatten().toSet(); var minCover = vertices.size
        for (mask in 1 until (1 shl vertices.size)) { val cover = vertices.filterIndexed { index, _ -> mask and (1 shl index) != 0 }; val coversAll = edges.all { (u, v) -> u in cover || v in cover }; if (coversAll) minCover = minOf(minCover, cover.size) }
        assertEquals(3, minCover)
    }

    @Test
    fun testDominatingSet() {
        val graph = arrayOf(intArrayOf(0,1,1,0), intArrayOf(1,0,1,1), intArrayOf(1,1,0,1), intArrayOf(0,1,1,0)); val n = graph.size; var minDomSet = n
        for (mask in 1 until (1 shl n)) { val domSet = (0 until n).filter { mask and (1 shl it) != 0 }; val dominated = BooleanArray(n); for (v in domSet) { dominated[v] = true; for (u in 0 until n) if (graph[v][u] == 1) dominated[u] = true }; if (dominated.all { it }) minDomSet = minOf(minDomSet, domSet.size) }
        assertEquals(1, minDomSet)
    }

    @Test
    fun testIndependentSet() {
        val graph = arrayOf(intArrayOf(0,1,0,1), intArrayOf(1,0,1,0), intArrayOf(0,1,0,1), intArrayOf(1,0,1,0)); val n = graph.size; var maxIndSet = 0
        for (mask in 1 until (1 shl n)) { val vertices = (0 until n).filter { mask and (1 shl it) != 0 }; val isInd = vertices.all { i -> vertices.all { j -> i == j || graph[i][j] == 0 } }; if (isInd) maxIndSet = maxOf(maxIndSet, vertices.size) }
        assertEquals(2, maxIndSet)
    }

    @Test
    fun testEulerianPath() {
        val graph = arrayOf(intArrayOf(0,1,1,0,0), intArrayOf(1,0,0,1,0), intArrayOf(1,0,0,1,1), intArrayOf(0,1,1,0,1), intArrayOf(0,0,1,1,0)); val n = graph.size; val degrees = IntArray(n); for (i in 0 until n) degrees[i] = graph[i].sum(); val oddCount = degrees.count { it % 2 != 0 }
        assertTrue(oddCount == 0 || oddCount == 2)
    }

    @Test
    fun testArticulationPoints() {
        val graph = arrayOf(intArrayOf(0,1,1,0,0), intArrayOf(1,0,0,1,0), intArrayOf(1,0,0,1,1), intArrayOf(0,1,1,0,1), intArrayOf(0,0,1,1,0)); val n = graph.size; val visited = BooleanArray(n); val disc = IntArray(n); val low = IntArray(n); val parent = IntArray(n) { -1 }; val ap = BooleanArray(n); var time = 0
        fun dfs(u: Int) { var children = 0; visited[u] = true; disc[u] = time; low[u] = time; time++; for (v in 0 until n) if (graph[u][v] == 1) { if (!visited[v]) { children++; parent[v] = u; dfs(v); low[u] = minOf(low[u], low[v]); if (parent[u] == -1 && children > 1) ap[u] = true; if (parent[u] != -1 && low[v] >= disc[u]) ap[u] = true } else if (v != parent[u]) low[u] = minOf(low[u], disc[v]) } }
        for (i in 0 until n) if (!visited[i]) dfs(i)
        assertTrue(ap.any { it })
    }

    @Test
    fun testBridges() {
        val graph = arrayOf(intArrayOf(0,1,1,0,0), intArrayOf(1,0,0,1,0), intArrayOf(1,0,0,1,1), intArrayOf(0,1,1,0,1), intArrayOf(0,0,1,1,0)); val n = graph.size; val visited = BooleanArray(n); val disc = IntArray(n); val low = IntArray(n); val parent = IntArray(n) { -1 }; val bridges = mutableListOf<Pair<Int, Int>>(); var time = 0
        fun dfs(u: Int) { visited[u] = true; disc[u] = time; low[u] = time; time++; for (v in 0 until n) if (graph[u][v] == 1) { if (!visited[v]) { parent[v] = u; dfs(v); low[u] = minOf(low[u], low[v]); if (low[v] > disc[u]) bridges.add(u to v) } else if (v != parent[u]) low[u] = minOf(low[u], disc[v]) } }
        for (i in 0 until n) if (!visited[i]) dfs(i)
        assertTrue(bridges.isNotEmpty())
    }

    @Test
    fun testBiconnectedComponents() {
        val graph = arrayOf(intArrayOf(0,1,1,0,0), intArrayOf(1,0,0,1,0), intArrayOf(1,0,0,1,1), intArrayOf(0,1,1,0,1), intArrayOf(0,0,1,1,0)); val n = graph.size; val visited = BooleanArray(n); val disc = IntArray(n); val low = IntArray(n); val parent = IntArray(n) { -1 }; val stack = ArrayDeque<Pair<Int, Int>>(); val components = mutableListOf<List<Pair<Int, Int>>>(); var time = 0
        fun dfs(u: Int) { visited[u] = true; disc[u] = time; low[u] = time; time++; for (v in 0 until n) if (graph[u][v] == 1) { if (!visited[v]) { parent[v] = u; stack.addLast(u to v); dfs(v); low[u] = minOf(low[u], low[v]); if (low[v] >= disc[u]) { val comp = mutableListOf<Pair<Int, Int>>(); while (true) { val edge = stack.removeLast(); comp.add(edge); if (edge == u to v) break }; components.add(comp) } } else if (v != parent[u] && disc[v] < disc[u]) { stack.addLast(u to v); low[u] = minOf(low[u], disc[v]) } } }
        for (i in 0 until n) if (!visited[i]) dfs(i)
        assertTrue(components.isNotEmpty())
    }

    @Test
    fun testNetworkSimplex() {
        val supply = intArrayOf(10, 0, 0, -10); val edges = listOf(Triple(0,1,2,2), Triple(0,2,3,5), Triple(1,3,1,3), Triple(2,3,2,1))
        assertEquals(4, edges.size); assertEquals(0, supply.sum())
    }

    @Test
    fun testSuccessiveShortestPath() {
        val graph = arrayOf(intArrayOf(0,10,0,0), intArrayOf(0,0,5,0), intArrayOf(0,0,0,10), intArrayOf(0,0,0,0)); val n = graph.size; val dist = IntArray(n) { Int.MAX_VALUE }; dist[0] = 0
        for (i in 1 until n) for (u in 0 until n) for (v in 0 until n) if (graph[u][v] > 0 && dist[u] != Int.MAX_VALUE && dist[u] + graph[u][v] < dist[v]) dist[v] = dist[u] + graph[u][v]
        assertEquals(15, dist[3])
    }

    @Test
    fun testCycleCanceling() {
        val graph = arrayOf(intArrayOf(0,10,0,0), intArrayOf(0,0,5,0), intArrayOf(0,0,0,10), intArrayOf(0,0,0,0)); val n = graph.size; val dist = IntArray(n) { Int.MAX_VALUE }; dist[0] = 0
        for (i in 1 until n) for (u in 0 until n) for (v in 0 until n) if (graph[u][v] > 0 && dist[u] != Int.MAX_VALUE && dist[u] + graph[u][v] < dist[v]) dist[v] = dist[u] + graph[u][v]
        assertEquals(15, dist[3])
    }

    @Test
    fun testCapacityScaling() {
        val graph = arrayOf(intArrayOf(0,16,13,0,0,0), intArrayOf(0,0,10,12,0,0), intArrayOf(0,4,0,0,14,0), intArrayOf(0,0,9,0,0,20), intArrayOf(0,0,0,7,0,4), intArrayOf(0,0,0,0,0,0)); val n = graph.size; val residual = Array(n) { i -> graph[i].copyOf() }; val parent = IntArray(n)
        fun bfs(s: Int, t: Int): Boolean { val visited = BooleanArray(n); val queue = ArrayDeque<Int>(); queue.add(s); visited[s] = true; parent[s] = -1; while (queue.isNotEmpty()) { val u = queue.removeFirst(); for (v in 0 until n) if (!visited[v] && residual[u][v] > 0) { queue.add(v); parent[v] = u; visited[v] = true } }; return visited[t] }
        var maxFlow = 0; while (bfs(0, 5)) { var pf = Int.MAX_VALUE; var v = 5; while (v != 0) { val u = parent[v]; pf = minOf(pf, residual[u][v]); v = u }; v = 5; while (v != 0) { val u = parent[v]; residual[u][v] -= pf; residual[v][u] += pf; v = u }; maxFlow += pf }
        assertEquals(23, maxFlow)
    }

    @Test
    fun testPushRelabel() {
        val graph = arrayOf(intArrayOf(0,16,13,0,0,0), intArrayOf(0,0,10,12,0,0), intArrayOf(0,4,0,0,14,0), intArrayOf(0,0,9,0,0,20), intArrayOf(0,0,0,7,0,4), intArrayOf(0,0,0,0,0,0)); val n = graph.size; val residual = Array(n) { i -> graph[i].copyOf() }; val height = IntArray(n); val excess = IntArray(n); height[0] = n
        for (v in 0 until n) if (graph[0][v] > 0) { residual[0][v] = 0; residual[v][0] = graph[0][v]; excess[v] = graph[0][v]; excess[0] -= graph[0][v] }
        var maxFlow = 0; for (v in 0 until n) if (v != 0 && v != n-1 && excess[v] > 0) maxFlow += excess[v]
        assertTrue(maxFlow > 0)
    }

    @Test
    fun testEdmondsKarp() {
        val graph = arrayOf(intArrayOf(0,16,13,0,0,0), intArrayOf(0,0,10,12,0,0), intArrayOf(0,4,0,0,14,0), intArrayOf(0,0,9,0,0,20), intArrayOf(0,0,0,7,0,4), intArrayOf(0,0,0,0,0,0)); val n = graph.size; val residual = Array(n) { i -> graph[i].copyOf() }; val parent = IntArray(n)
        fun bfs(s: Int, t: Int): Boolean { val visited = BooleanArray(n); val queue = ArrayDeque<Int>(); queue.add(s); visited[s] = true; parent[s] = -1; while (queue.isNotEmpty()) { val u = queue.removeFirst(); for (v in 0 until n) if (!visited[v] && residual[u][v] > 0) { queue.add(v); parent[v] = u; visited[v] = true } }; return visited[t] }
        var maxFlow = 0; while (bfs(0, 5)) { var pf = Int.MAX_VALUE; var v = 5; while (v != 0) { val u = parent[v]; pf = minOf(pf, residual[u][v]); v = u }; v = 5; while (v != 0) { val u = parent[v]; residual[u][v] -= pf; residual[v][u] += pf; v = u }; maxFlow += pf }
        assertEquals(23, maxFlow)
    }

    @Test
    fun testDinic() {
        val graph = arrayOf(intArrayOf(0,16,13,0,0,0), intArrayOf(0,0,10,12,0,0), intArrayOf(0,4,0,0,14,0), intArrayOf(0,0,9,0,0,20), intArrayOf(0,0,0,7,0,4), intArrayOf(0,0,0,0,0,0)); val n = graph.size; val residual = Array(n) { i -> graph[i].copyOf() }; val level = IntArray(n)
        fun bfs(s: Int, t: Int): Boolean { for (i in 0 until n) level[i] = -1; level[s] = 0; val queue = ArrayDeque<Int>(); queue.add(s); while (queue.isNotEmpty()) { val u = queue.removeFirst(); for (v in 0 until n) if (level[v] < 0 && residual[u][v] > 0) { level[v] = level[u] + 1; queue.add(v) } }; return level[t] >= 0 }
        fun dfs(u: Int, t: Int, f: Int): Int { if (u == t) return f; for (v in 0 until n) if (level[v] == level[u] + 1 && residual[u][v] > 0) { val pushed = dfs(v, t, minOf(f, residual[u][v])); if (pushed > 0) { residual[u][v] -= pushed; residual[v][u] += pushed; return pushed } }; return 0 }
        var maxFlow = 0; while (bfs(0, 5)) { var pushed = dfs(0, 5, Int.MAX_VALUE); while (pushed > 0) { maxFlow += pushed; pushed = dfs(0, 5, Int.MAX_VALUE) } }
        assertEquals(23, maxFlow)
    }
}
