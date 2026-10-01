package com.jarvis.assistant.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.URL
import java.net.UnknownHostException

object NetworkUtils {

    private const val DEFAULT_TIMEOUT = 5000
    private const val DEFAULT_PORT = 80

    fun isConnected(host: String = "8.8.8.8", port: Int = DEFAULT_PORT, timeout: Int = DEFAULT_TIMEOUT): Boolean {
        return try {
            Socket().use { it.connect(java.net.InetSocketAddress(host, port), timeout) }
            true
        } catch (e: IOException) {
            false
        }
    }

    fun isHostReachable(host: String, timeout: Int = DEFAULT_TIMEOUT): Boolean {
        return try {
            InetAddress.getByName(host).isReachable(timeout)
        } catch (e: IOException) {
            false
        }
    }

    fun isPortOpen(host: String, port: Int, timeout: Int = DEFAULT_TIMEOUT): Boolean {
        return try {
            Socket().use { it.connect(java.net.InetSocketAddress(host, port), timeout) }
            true
        } catch (e: IOException) {
            false
        }
    }

    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is java.net.Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
        }
        return null
    }

    fun getLocalIpv6Address(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is java.net.Inet6Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
        }
        return null
    }

    fun getPublicIpAddress(): String? {
        return try {
            val url = URL("https://api.ipify.org")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = DEFAULT_TIMEOUT
            connection.readTimeout = DEFAULT_TIMEOUT
            BufferedReader(InputStreamReader(connection.inputStream)).readLine()
        } catch (e: Exception) {
            null
        }
    }

    fun getPublicIpAddressV6(): String? {
        return try {
            val url = URL("https://api6.ipify.org")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = DEFAULT_TIMEOUT
            connection.readTimeout = DEFAULT_TIMEOUT
            BufferedReader(InputStreamReader(connection.inputStream)).readLine()
        } catch (e: Exception) {
            null
        }
    }

    fun getHostname(): String? {
        return try {
            InetAddress.getLocalHost().hostName
        } catch (e: UnknownHostException) {
            null
        }
    }

    fun getHostAddress(): String? {
        return try {
            InetAddress.getLocalHost().hostAddress
        } catch (e: UnknownHostException) {
            null
        }
    }

    fun getCanonicalHostname(): String? {
        return try {
            InetAddress.getLocalHost().canonicalHostName
        } catch (e: UnknownHostException) {
            null
        }
    }

    fun resolveHostname(hostname: String): String? {
        return try {
            InetAddress.getByName(hostname).hostAddress
        } catch (e: UnknownHostException) {
            null
        }
    }

    fun resolveAddress(address: String): String? {
        return try {
            InetAddress.getByName(address).hostName
        } catch (e: UnknownHostException) {
            null
        }
    }

    fun getAllAddresses(hostname: String): List<String> {
        return try {
            InetAddress.getAllByName(hostname).map { it.hostAddress }
        } catch (e: UnknownHostException) {
            emptyList()
        }
    }

    fun isIpAddress(host: String): Boolean {
        return host.matches(Regex("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$"))
    }

    fun isIpv6Address(host: String): Boolean {
        return host.contains(":")
    }

    fun isLocalAddress(host: String): Boolean {
        return try {
            InetAddress.getByName(host).isLoopbackAddress
        } catch (e: UnknownHostException) {
            false
        }
    }

    fun isPrivateAddress(host: String): Boolean {
        return try {
            InetAddress.getByName(host).isSiteLocalAddress
        } catch (e: UnknownHostException) {
            false
        }
    }

    fun isMulticastAddress(host: String): Boolean {
        return try {
            InetAddress.getByName(host).isMulticastAddress
        } catch (e: UnknownHostException) {
            false
        }
    }

    fun isValidPort(port: Int): Boolean {
        return port in 1..65535
    }

    fun isValidPort(port: String): Boolean {
        val p = port.toIntOrNull() ?: return false
        return isValidPort(p)
    }

    fun getDefaultHttpPort(): Int = 80
    fun getDefaultHttpsPort(): Int = 443
    fun getDefaultFtpPort(): Int = 21
    fun getDefaultSshPort(): Int = 22
    fun getDefaultSmtpPort(): Int = 25
    fun getDefaultDnsPort(): Int = 53
    fun getDefaultPop3Port(): Int = 110
    fun getDefaultImapPort(): Int = 143

    fun httpGet(url: String, timeout: Int = DEFAULT_TIMEOUT): String? {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream)).readText()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun httpPost(url: String, body: String, contentType: String = "application/json", timeout: Int = DEFAULT_TIMEOUT): String? {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", contentType)
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.outputStream.use { it.write(body.toByteArray()) }
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream)).readText()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun httpHead(url: String, timeout: Int = DEFAULT_TIMEOUT): Map<String, String> {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.responseCode
            val headers = mutableMapOf<String, String>()
            for (i in 0 until connection.headerFields.size) {
                val key = connection.headerFields.keys.elementAt(i) ?: continue
                headers[key] = connection.headerFields[key]?.joinToString(", ") ?: ""
            }
            headers
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun httpStatus(url: String, timeout: Int = DEFAULT_TIMEOUT): Int {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.responseCode
        } catch (e: Exception) {
            -1
        }
    }

    fun isUrlReachable(url: String, timeout: Int = DEFAULT_TIMEOUT): Boolean {
        return httpStatus(url, timeout) == HttpURLConnection.HTTP_OK
    }

    fun getMacAddress(): String? {
        try {
            val ip = getLocalIpAddress() ?: return null
            val networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(ip))
            val mac = networkInterface.hardwareAddress ?: return null
            return mac.joinToString(":") { "%02X".format(it) }
        } catch (e: Exception) {
            return null
        }
    }

    fun getNetworkInterfaces(): List<String> {
        return try {
            NetworkInterface.getNetworkInterfaces().toList().map { it.name }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getActiveNetworkInterface(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isUp && !networkInterface.isLoopback) {
                    return networkInterface.name
                }
            }
        } catch (e: Exception) {
        }
        return null
    }

    fun getNetworkInterfaceInfo(name: String): Map<String, Any>? {
        return try {
            val networkInterface = NetworkInterface.getByName(name) ?: return null
            mapOf(
                "name" to networkInterface.name,
                "displayName" to networkInterface.displayName,
                "isUp" to networkInterface.isUp,
                "isLoopback" to networkInterface.isLoopback,
                "isVirtual" to networkInterface.isVirtual,
                "supportsMulticast" to networkInterface.supportsMulticast,
                "mtu" to networkInterface.mtu,
                "hardwareAddress" to (networkInterface.hardwareAddress?.joinToString(":") { "%02X".format(it) } ?: "N/A"),
                "inetAddresses" to networkInterface.inetAddresses.toList().map { it.hostAddress }
            )
        } catch (e: Exception) {
            null
        }
    }

    fun ping(host: String, count: Int = 4): List<Long> {
        val results = mutableListOf<Long>()
        for (i in 0 until count) {
            val start = System.currentTimeMillis()
            val reachable = isHostReachable(host, 2000)
            val end = System.currentTimeMillis()
            if (reachable) {
                results.add(end - start)
            }
        }
        return results
    }

    fun pingAverage(host: String, count: Int = 4): Double {
        val results = ping(host, count)
        return if (results.isEmpty()) -1.0 else results.average()
    }

    fun pingMin(host: String, count: Int = 4): Long {
        val results = ping(host, count)
        return results.minOrNull() ?: -1L
    }

    fun pingMax(host: String, count: Int = 4): Long {
        val results = ping(host, count)
        return results.maxOrNull() ?: -1L
    }

    fun pingPacketLoss(host: String, count: Int = 4): Double {
        val results = ping(host, count)
        return if (count == 0) 0.0 else (count - results.size).toDouble() / count * 100.0
    }

    fun traceroute(host: String, maxHops: Int = 30): List<String> {
        val results = mutableListOf<String>()
        for (ttl in 1..maxHops) {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("tracert", "-h", ttl.toString(), "-w", "1000", host))
                val output = process.inputStream.bufferedReader().readText()
                results.add(output.trim())
                if (output.contains(host)) break
            } catch (e: Exception) {
                results.add("Hop $ttl: Error")
            }
        }
        return results
    }

    fun scanPort(host: String, port: Int, timeout: Int = 1000): Boolean {
        return isPortOpen(host, port, timeout)
    }

    fun scanPorts(host: String, ports: List<Int>, timeout: Int = 1000): Map<Int, Boolean> {
        return ports.associateWith { isPortOpen(host, it, timeout) }
    }

    fun scanCommonPorts(host: String, timeout: Int = 1000): Map<Int, Boolean> {
        val commonPorts = listOf(21, 22, 23, 25, 53, 80, 110, 143, 443, 3306, 3389, 5432, 8080)
        return scanPorts(host, commonPorts, timeout)
    }

    fun scanPortRange(host: String, startPort: Int, endPort: Int, timeout: Int = 1000): Map<Int, Boolean> {
        return (startPort..endPort).associateWith { isPortOpen(host, it, timeout) }
    }

    fun getOpenPorts(host: String, ports: List<Int>, timeout: Int = 1000): List<Int> {
        return ports.filter { isPortOpen(host, it, timeout) }
    }

    fun getOpenPortsInRange(host: String, startPort: Int, endPort: Int, timeout: Int = 1000): List<Int> {
        return (startPort..endPort).filter { isPortOpen(host, it, timeout) }
    }

    fun getHttpHeaders(url: String, timeout: Int = DEFAULT_TIMEOUT): Map<String, List<String>> {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.responseCode
            connection.headerFields.mapValues { it.value }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getHttpHeader(url: String, headerName: String, timeout: Int = DEFAULT_TIMEOUT): String? {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.responseCode
            connection.getHeaderField(headerName)
        } catch (e: Exception) {
            null
        }
    }

    fun getServerHeader(url: String, timeout: Int = DEFAULT_TIMEOUT): String? {
        return getHttpHeader(url, "Server", timeout)
    }

    fun getContentType(url: String, timeout: Int = DEFAULT_TIMEOUT): String? {
        return getHttpHeader(url, "Content-Type", timeout)
    }

    fun getContentLength(url: String, timeout: Int = DEFAULT_TIMEOUT): Long {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.contentLengthLong
        } catch (e: Exception) {
            -1L
        }
    }

    fun getLastModified(url: String, timeout: Int = DEFAULT_TIMEOUT): Long {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.lastModified
        } catch (e: Exception) {
            0L
        }
    }

    fun getEtag(url: String, timeout: Int = DEFAULT_TIMEOUT): String? {
        return getHttpHeader(url, "ETag", timeout)
    }

    fun getLocation(url: String, timeout: Int = DEFAULT_TIMEOUT): String? {
        return getHttpHeader(url, "Location", timeout)
    }

    fun getRedirects(url: String, timeout: Int = DEFAULT_TIMEOUT): List<String> {
        val redirects = mutableListOf<String>()
        var currentUrl = url
        for (i in 0 until 10) {
            try {
                val connection = URL(currentUrl).openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = false
                connection.requestMethod = "GET"
                connection.connectTimeout = timeout
                connection.readTimeout = timeout
                val code = connection.responseCode
                if (code in 300..399) {
                    val location = connection.getHeaderField("Location") ?: break
                    redirects.add(location)
                    currentUrl = location
                } else {
                    break
                }
            } catch (e: Exception) {
                break
            }
        }
        return redirects
    }

    fun downloadFile(url: String, dest: java.io.File, timeout: Int = DEFAULT_TIMEOUT): Boolean {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.inputStream.use { input ->
                dest.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun downloadFileWithProgress(url: String, dest: java.io.File, timeout: Int = DEFAULT_TIMEOUT, onProgress: (Long, Long) -> Unit): Boolean {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            val totalBytes = connection.contentLengthLong
            var downloadedBytes = 0L
            connection.inputStream.use { input ->
                dest.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        onProgress(downloadedBytes, totalBytes)
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun uploadFile(url: String, file: java.io.File, timeout: Int = DEFAULT_TIMEOUT): Boolean {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.setRequestProperty("Content-Type", "application/octet-stream")
            file.inputStream().use { input ->
                connection.outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            connection.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            false
        }
    }

    fun getLatency(host: String, port: Int = DEFAULT_PORT, samples: Int = 10): List<Long> {
        val results = mutableListOf<Long>()
        for (i in 0 until samples) {
            val start = System.currentTimeMillis()
            if (isPortOpen(host, port, 2000)) {
                results.add(System.currentTimeMillis() - start)
            }
        }
        return results
    }

    fun getAverageLatency(host: String, port: Int = DEFAULT_PORT, samples: Int = 10): Double {
        val results = getLatency(host, port, samples)
        return if (results.isEmpty()) -1.0 else results.average()
    }

    fun getMinLatency(host: String, port: Int = DEFAULT_PORT, samples: Int = 10): Long {
        val results = getLatency(host, port, samples)
        return results.minOrNull() ?: -1L
    }

    fun getMaxLatency(host: String, port: Int = DEFAULT_PORT, samples: Int = 10): Long {
        val results = getLatency(host, port, samples)
        return results.maxOrNull() ?: -1L
    }

    fun getJitter(host: String, port: Int = DEFAULT_PORT, samples: Int = 10): Double {
        val results = getLatency(host, port, samples)
        if (results.size < 2) return 0.0
        val avg = results.average()
        return results.sumOf { (it - avg) * (it - avg) } / results.size
    }

    fun getStandardDeviation(host: String, port: Int = DEFAULT_PORT, samples: Int = 10): Double {
        val results = getLatency(host, port, samples)
        if (results.size < 2) return 0.0
        val avg = results.average()
        val variance = results.sumOf { (it - avg) * (it - avg) } / results.size
        return kotlin.math.sqrt(variance)
    }

    fun getPacketLoss(host: String, port: Int = DEFAULT_PORT, samples: Int = 10): Double {
        val results = getLatency(host, port, samples)
        return if (samples == 0) 0.0 else (samples - results.size).toDouble() / samples * 100.0
    }

    fun getBandwidth(url: String, durationMs: Long = 5000): Double {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = durationMs.toInt()
            val start = System.currentTimeMillis()
            var totalBytes = 0L
            connection.inputStream.use { input ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    totalBytes += bytesRead
                    if (System.currentTimeMillis() - start >= durationMs) break
                }
            }
            val elapsed = (System.currentTimeMillis() - start).toDouble() / 1000.0
            if (elapsed > 0) totalBytes.toDouble() / elapsed else 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun getDownloadSpeed(url: String, durationMs: Long = 5000): Double = getBandwidth(url, durationMs)

    fun getUploadSpeed(url: String, file: java.io.File, durationMs: Long = 5000): Double {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connectTimeout = 5000
            connection.readTimeout = durationMs.toInt()
            val start = System.currentTimeMillis()
            var totalBytes = 0L
            file.inputStream().use { input ->
                connection.outputStream.use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytes += bytesRead
                        if (System.currentTimeMillis() - start >= durationMs) break
                    }
                }
            }
            val elapsed = (System.currentTimeMillis() - start).toDouble() / 1000.0
            if (elapsed > 0) totalBytes.toDouble() / elapsed else 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun getDnsServers(): List<String> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("cat", "/etc/resolv.conf"))
            val output = process.inputStream.bufferedReader().readText()
            output.lines().filter { it.startsWith("nameserver") }.map { it.split("\\s+")[1] }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getDnsServer(): String? {
        return getDnsServers().firstOrNull()
    }

    fun flushDnsCache(): Boolean {
        return try {
            val os = System.getProperty("os.name").lowercase()
            val command = when {
                os.contains("win") -> arrayOf("ipconfig", "/flushdns")
                os.contains("mac") -> arrayOf("dscacheutil", "-flushcache")
                else -> arrayOf("systemd-resolve", "--flush-caches")
            }
            val process = Runtime.getRuntime().exec(command)
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    fun getArpTable(): Map<String, String> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("arp", "-a"))
            val output = process.inputStream.bufferedReader().readText()
            val result = mutableMapOf<String, String>()
            output.lines().forEach { line ->
                val match = Regex("\\(([^)]+)\\)\\s+at\\s+([0-9a-fA-F:]+)").find(line)
                if (match != null) {
                    result[match.groupValues[1]] = match.groupValues[2]
                }
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getRoutingTable(): List<Map<String, String>> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("netstat", "-rn"))
            val output = process.inputStream.bufferedReader().readText()
            output.lines().drop(2).mapNotNull { line ->
                val parts = line.trim().split(Regex("\\s+"))
                if (parts.size >= 4) {
                    mapOf(
                        "destination" to parts[0],
                        "gateway" to parts[1],
                        "flags" to parts[2],
                        "interface" to parts[3]
                    )
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getActiveConnections(): List<Map<String, String>> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("netstat", "-an"))
            val output = process.inputStream.bufferedReader().readText()
            output.lines().drop(2).mapNotNull { line ->
                val parts = line.trim().split(Regex("\\s+"))
                if (parts.size >= 4) {
                    mapOf(
                        "protocol" to parts[0],
                        "localAddress" to parts[3],
                        "foreignAddress" to parts.getOrElse(4) { "" },
                        "state" to parts.getOrElse(5) { "" }
                    )
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getListeningPorts(): List<Int> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("netstat", "-tlnp"))
            val output = process.inputStream.bufferedReader().readText()
            output.lines().drop(2).mapNotNull { line ->
                val parts = line.trim().split(Regex("\\s+"))
                if (parts.size >= 4) {
                    val localAddress = parts[3]
                    val port = localAddress.substringAfterLast(":").toIntOrNull()
                    port
                } else null
            }.distinct().sorted()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getTcpConnections(): List<Map<String, String>> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("ss", "-t"))
            val output = process.inputStream.bufferedReader().readText()
            output.lines().drop(1).mapNotNull { line ->
                val parts = line.trim().split(Regex("\\s+"))
                if (parts.size >= 5) {
                    mapOf(
                        "state" to parts[0],
                        "recvQ" to parts[1],
                        "sendQ" to parts[2],
                        "localAddress" to parts[3],
                        "peerAddress" to parts[4]
                    )
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getUdpConnections(): List<Map<String, String>> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("ss", "-u"))
            val output = process.inputStream.bufferedReader().readText()
            output.lines().drop(1).mapNotNull { line ->
                val parts = line.trim().split(Regex("\\s+"))
                if (parts.size >= 5) {
                    mapOf(
                        "state" to parts[0],
                        "recvQ" to parts[1],
                        "sendQ" to parts[2],
                        "localAddress" to parts[3],
                        "peerAddress" to parts[4]
                    )
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getSocketStatistics(): Map<String, Int> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("ss", "-s"))
            val output = process.inputStream.bufferedReader().readText()
            val result = mutableMapOf<String, Int>()
            output.lines().forEach { line ->
                val parts = line.trim().split(Regex("\\s+"))
                if (parts.size >= 2) {
                    val key = parts[0]
                    val value = parts[1].toIntOrNull()
                    if (value != null) {
                        result[key] = value
                    }
                }
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getNetworkStatistics(): Map<String, Long> {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            val result = mutableMapOf<String, Long>()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isUp) {
                    result["${networkInterface.name}_rx_bytes"] = networkInterface.interfaceAddresses.sumOf { 0L }
                    result["${networkInterface.name}_tx_bytes"] = 0L
                }
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getInterfaceStatistics(name: String): Map<String, Long>? {
        return try {
            val networkInterface = NetworkInterface.getByName(name) ?: return null
            mapOf(
                "rx_bytes" to 0L,
                "tx_bytes" to 0L,
                "rx_packets" to 0L,
                "tx_packets" to 0L,
                "rx_errors" to 0L,
                "tx_errors" to 0L,
                "rx_dropped" to 0L,
                "tx_dropped" to 0L
            )
        } catch (e: Exception) {
            null
        }
    }

    fun getNetworkUsage(): Map<String, Map<String, Long>> {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            val result = mutableMapOf<String, Map<String, Long>>()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isUp) {
                    result[networkInterface.name] = mapOf(
                        "rx_bytes" to 0L,
                        "tx_bytes" to 0L
                    )
                }
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getNetworkSpeed(): Map<String, Double> {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            val result = mutableMapOf<String, Double>()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isUp) {
                    result[networkInterface.name] = 0.0
                }
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getNetworkType(): String {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isUp && !networkInterface.isLoopback) {
                    return when {
                        networkInterface.name.startsWith("wlan") || networkInterface.name.startsWith("wifi") -> "WiFi"
                        networkInterface.name.startsWith("eth") -> "Ethernet"
                        networkInterface.name.startsWith("rmnet") -> "Mobile"
                        networkInterface.name.startsWith("tun") -> "VPN"
                        networkInterface.name.startsWith("lo") -> "Loopback"
                        else -> "Unknown"
                    }
                }
            }
            "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun isWifiConnected(): Boolean = getNetworkType() == "WiFi"
    fun isEthernetConnected(): Boolean = getNetworkType() == "Ethernet"
    fun isMobileConnected(): Boolean = getNetworkType() == "Mobile"
    fun isVpnConnected(): Boolean = getNetworkType() == "VPN"

    fun getNetworkTypeName(): String = getNetworkType()

    fun getNetworkSubtype(): String = "Unknown"

    fun getNetworkOperator(): String = "Unknown"

    fun getSimOperator(): String = "Unknown"

    fun getNetworkCountry(): String = "Unknown"

    fun getSimCountry(): String = "Unknown"

    fun getNetworkMcc(): Int = 0
    fun getNetworkMnc(): Int = 0
    fun getSimMcc(): Int = 0
    fun getSimMnc(): Int = 0

    fun getNetworkIso(): String = "Unknown"
    fun getSimIso(): String = "Unknown"

    fun getNetworkOperatorName(): String = "Unknown"
    fun getSimOperatorName(): String = "Unknown"

    fun getPhoneType(): String = "Unknown"
    fun getDeviceType(): String = "Unknown"

    fun getNetworkCapabilities(): Map<String, Boolean> {
        return mapOf(
            "internet" to true,
            "validated" to true,
            "captive_portal" to false,
            "not_metered" to true,
            "not_roaming" to true,
            "not_suspended" to true
        )
    }

    fun getLinkProperties(): Map<String, Any> {
        return mapOf(
            "interface_name" to (getActiveNetworkInterface() ?: "Unknown"),
            "addresses" to emptyList<String>(),
            "dns_servers" to getDnsServers(),
            "routes" to emptyList<String>(),
            "mtu" to 1500
        )
    }

    fun getNetworkInfo(): Map<String, Any> {
        return mapOf(
            "type" to getNetworkType(),
            "subtype" to getNetworkSubtype(),
            "operator" to getNetworkOperator(),
            "sim_operator" to getSimOperator(),
            "country" to getNetworkCountry(),
            "sim_country" to getSimCountry(),
            "mcc" to getNetworkMcc(),
            "mnc" to getNetworkMnc(),
            "sim_mcc" to getSimMcc(),
            "sim_mnc" to getSimMnc(),
            "iso" to getNetworkIso(),
            "sim_iso" to getSimIso(),
            "operator_name" to getNetworkOperatorName(),
            "sim_operator_name" to getSimOperatorName(),
            "phone_type" to getPhoneType(),
            "device_type" to getDeviceType(),
            "is_wifi" to isWifiConnected(),
            "is_ethernet" to isEthernetConnected(),
            "is_mobile" to isMobileConnected(),
            "is_vpn" to isVpnConnected(),
            "local_ip" to (getLocalIpAddress() ?: "Unknown"),
            "local_ipv6" to (getLocalIpv6Address() ?: "Unknown"),
            "public_ip" to (getPublicIpAddress() ?: "Unknown"),
            "public_ipv6" to (getPublicIpAddressV6() ?: "Unknown"),
            "mac_address" to (getMacAddress() ?: "Unknown"),
            "hostname" to (getHostname() ?: "Unknown"),
            "host_address" to (getHostAddress() ?: "Unknown"),
            "canonical_hostname" to (getCanonicalHostname() ?: "Unknown"),
            "dns_servers" to getDnsServers(),
            "network_interfaces" to getNetworkInterfaces(),
            "active_interface" to (getActiveNetworkInterface() ?: "Unknown"),
            "capabilities" to getNetworkCapabilities(),
            "link_properties" to getLinkProperties()
        )
    }

    fun getNetworkSummary(): String {
        val info = getNetworkInfo()
        return buildString {
            appendLine("Network Summary")
            appendLine("=" .repeat(50))
            appendLine("Type: ${info["type"]}")
            appendLine("Subtype: ${info["subtype"]}")
            appendLine("Operator: ${info["operator"]}")
            appendLine("SIM Operator: ${info["sim_operator"]}")
            appendLine("Country: ${info["country"]}")
            appendLine("SIM Country: ${info["sim_country"]}")
            appendLine("MCC: ${info["mcc"]}")
            appendLine("MNC: ${info["mnc"]}")
            appendLine("SIM MCC: ${info["sim_mcc"]}")
            appendLine("SIM MNC: ${info["sim_mnc"]}")
            appendLine("ISO: ${info["iso"]}")
            appendLine("SIM ISO: ${info["sim_iso"]}")
            appendLine("Operator Name: ${info["operator_name"]}")
            appendLine("SIM Operator Name: ${info["sim_operator_name"]}")
            appendLine("Phone Type: ${info["phone_type"]}")
            appendLine("Device Type: ${info["device_type"]}")
            appendLine("Is WiFi: ${info["is_wifi"]}")
            appendLine("Is Ethernet: ${info["is_ethernet"]}")
            appendLine("Is Mobile: ${info["is_mobile"]}")
            appendLine("Is VPN: ${info["is_vpn"]}")
            appendLine("Local IP: ${info["local_ip"]}")
            appendLine("Local IPv6: ${info["local_ipv6"]}")
            appendLine("Public IP: ${info["public_ip"]}")
            appendLine("Public IPv6: ${info["public_ipv6"]}")
            appendLine("MAC Address: ${info["mac_address"]}")
            appendLine("Hostname: ${info["hostname"]}")
            appendLine("Host Address: ${info["host_address"]}")
            appendLine("Canonical Hostname: ${info["canonical_hostname"]}")
            appendLine("DNS Servers: ${info["dns_servers"]}")
            appendLine("Network Interfaces: ${info["network_interfaces"]}")
            appendLine("Active Interface: ${info["active_interface"]}")
        }
    }

    fun getNetworkReport(): String {
        val summary = getNetworkSummary()
        val interfaces = getNetworkInterfaces().joinToString("\n  ")
        val dnsServers = getDnsServers().joinToString("\n  ")
        val arpTable = getArpTable().entries.joinToString("\n  ") { "${it.key} -> ${it.value}" }
        val routingTable = getRoutingTable().joinToString("\n  ") { "${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}" }
        val activeConnections = getActiveConnections().joinToString("\n  ") { "${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})" }
        val listeningPorts = getListeningPorts().joinToString(", ")
        val tcpConnections = getTcpConnections().joinToString("\n  ") { "${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}" }
        val udpConnections = getUdpConnections().joinToString("\n  ") { "${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}" }
        val socketStats = getSocketStatistics().entries.joinToString("\n  ") { "${it.key}: ${it.value}" }
        val networkUsage = getNetworkUsage().entries.joinToString("\n  ") { "${it.key}: rx=${it.value["rx_bytes"]}, tx=${it.value["tx_bytes"]}" }
        val networkSpeed = getNetworkSpeed().entries.joinToString("\n  ") { "${it.key}: ${it.value} bytes/s" }
        return buildString {
            appendLine(summary)
            appendLine()
            appendLine("Network Interfaces:")
            appendLine("  $interfaces")
            appendLine()
            appendLine("DNS Servers:")
            appendLine("  $dnsServers")
            appendLine()
            appendLine("ARP Table:")
            appendLine("  $arpTable")
            appendLine()
            appendLine("Routing Table:")
            appendLine("  $routingTable")
            appendLine()
            appendLine("Active Connections:")
            appendLine("  $activeConnections")
            appendLine()
            appendLine("Listening Ports:")
            appendLine("  $listeningPorts")
            appendLine()
            appendLine("TCP Connections:")
            appendLine("  $tcpConnections")
            appendLine()
            appendLine("UDP Connections:")
            appendLine("  $udpConnections")
            appendLine()
            appendLine("Socket Statistics:")
            appendLine("  $socketStats")
            appendLine()
            appendLine("Network Usage:")
            appendLine("  $networkUsage")
            appendLine()
            appendLine("Network Speed:")
            appendLine("  $networkSpeed")
        }
    }

    fun getNetworkDiagnostics(): Map<String, Any> {
        return mapOf(
            "info" to getNetworkInfo(),
            "summary" to getNetworkSummary(),
            "report" to getNetworkReport(),
            "interfaces" to getNetworkInterfaces(),
            "dns_servers" to getDnsServers(),
            "arp_table" to getArpTable(),
            "routing_table" to getRoutingTable(),
            "active_connections" to getActiveConnections(),
            "listening_ports" to getListeningPorts(),
            "tcp_connections" to getTcpConnections(),
            "udp_connections" to getUdpConnections(),
            "socket_statistics" to getSocketStatistics(),
            "network_usage" to getNetworkUsage(),
            "network_speed" to getNetworkSpeed(),
            "capabilities" to getNetworkCapabilities(),
            "link_properties" to getLinkProperties()
        )
    }

    fun getNetworkDiagnosticsReport(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("Network Diagnostics Report")
            appendLine("=" .repeat(50))
            appendLine()
            appendLine("Network Info:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["info"] as? Map<String, Any>)?.forEach { (key, value) ->
                appendLine("  $key: $value")
            }
            appendLine()
            appendLine("Network Summary:")
            appendLine(diagnostics["summary"])
            appendLine()
            appendLine("Network Report:")
            appendLine(diagnostics["report"])
            appendLine()
            appendLine("Network Interfaces:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["interfaces"] as? List<String>)?.forEach {
                appendLine("  $it")
            }
            appendLine()
            appendLine("DNS Servers:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["dns_servers"] as? List<String>)?.forEach {
                appendLine("  $it")
            }
            appendLine()
            appendLine("ARP Table:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["arp_table"] as? Map<String, String>)?.forEach { (key, value) ->
                appendLine("  $key -> $value")
            }
            appendLine()
            appendLine("Routing Table:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["routing_table"] as? List<Map<String, String>>)?.forEach {
                appendLine("  ${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}")
            }
            appendLine()
            appendLine("Active Connections:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["active_connections"] as? List<Map<String, String>>)?.forEach {
                appendLine("  ${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})")
            }
            appendLine()
            appendLine("Listening Ports:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["listening_ports"] as? List<Int>)?.forEach {
                appendLine("  $it")
            }
            appendLine()
            appendLine("TCP Connections:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["tcp_connections"] as? List<Map<String, String>>)?.forEach {
                appendLine("  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}")
            }
            appendLine()
            appendLine("UDP Connections:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["udp_connections"] as? List<Map<String, String>>)?.forEach {
                appendLine("  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}")
            }
            appendLine()
            appendLine("Socket Statistics:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["socket_statistics"] as? Map<String, Int>)?.forEach { (key, value) ->
                appendLine("  $key: $value")
            }
            appendLine()
            appendLine("Network Usage:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["network_usage"] as? Map<String, Map<String, Long>>)?.forEach { (key, value) ->
                appendLine("  $key: rx=${value["rx_bytes"]}, tx=${value["tx_bytes"]}")
            }
            appendLine()
            appendLine("Network Speed:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["network_speed"] as? Map<String, Double>)?.forEach { (key, value) ->
                appendLine("  $key: $value bytes/s")
            }
            appendLine()
            appendLine("Network Capabilities:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["capabilities"] as? Map<String, Boolean>)?.forEach { (key, value) ->
                appendLine("  $key: $value")
            }
            appendLine()
            appendLine("Link Properties:")
            @Suppress("UNCHECKED_CAST")
            (diagnostics["link_properties"] as? Map<String, Any>)?.forEach { (key, value) ->
                appendLine("  $key: $value")
            }
        }
    }

    fun getNetworkDiagnosticsSummary(): String {
        val info = getNetworkInfo()
        return buildString {
            appendLine("Network Diagnostics Summary")
            appendLine("=" .repeat(50))
            appendLine("Type: ${info["type"]}")
            appendLine("Subtype: ${info["subtype"]}")
            appendLine("Operator: ${info["operator"]}")
            appendLine("SIM Operator: ${info["sim_operator"]}")
            appendLine("Country: ${info["country"]}")
            appendLine("SIM Country: ${info["sim_country"]}")
            appendLine("MCC: ${info["mcc"]}")
            appendLine("MNC: ${info["mnc"]}")
            appendLine("SIM MCC: ${info["sim_mcc"]}")
            appendLine("SIM MNC: ${info["sim_mnc"]}")
            appendLine("ISO: ${info["iso"]}")
            appendLine("SIM ISO: ${info["sim_iso"]}")
            appendLine("Operator Name: ${info["operator_name"]}")
            appendLine("SIM Operator Name: ${info["sim_operator_name"]}")
            appendLine("Phone Type: ${info["phone_type"]}")
            appendLine("Device Type: ${info["device_type"]}")
            appendLine("Is WiFi: ${info["is_wifi"]}")
            appendLine("Is Ethernet: ${info["is_ethernet"]}")
            appendLine("Is Mobile: ${info["is_mobile"]}")
            appendLine("Is VPN: ${info["is_vpn"]}")
            appendLine("Local IP: ${info["local_ip"]}")
            appendLine("Local IPv6: ${info["local_ipv6"]}")
            appendLine("Public IP: ${info["public_ip"]}")
            appendLine("Public IPv6: ${info["public_ipv6"]}")
            appendLine("MAC Address: ${info["mac_address"]}")
            appendLine("Hostname: ${info["hostname"]}")
            appendLine("Host Address: ${info["host_address"]}")
            appendLine("Canonical Hostname: ${info["canonical_hostname"]}")
            appendLine("DNS Servers: ${info["dns_servers"]}")
            appendLine("Network Interfaces: ${info["network_interfaces"]}")
            appendLine("Active Interface: ${info["active_interface"]}")
        }
    }

    fun getNetworkDiagnosticsJson(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("{")
            appendLine("  \"info\": {")
            @Suppress("UNCHECKED_CAST")
            val info = diagnostics["info"] as? Map<String, Any> ?: emptyMap()
            info.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < info.size - 1) "," else ""
                appendLine("    \"$key\": \"$value\"$comma")
            }
            appendLine("  },")
            appendLine("  \"summary\": {")
            @Suppress("UNCHECKED_CAST")
            val summary = diagnostics["summary"] as? String ?: ""
            appendLine("    \"text\": \"$summary\"")
            appendLine("  },")
            appendLine("  \"report\": {")
            @Suppress("UNCHECKED_CAST")
            val report = diagnostics["report"] as? String ?: ""
            appendLine("    \"text\": \"$report\"")
            appendLine("  },")
            appendLine("  \"interfaces\": {")
            @Suppress("UNCHECKED_CAST")
            val interfaces = diagnostics["interfaces"] as? List<String> ?: emptyList()
            interfaces.forEachIndexed { index, value ->
                val comma = if (index < interfaces.size - 1) "," else ""
                appendLine("    \"$index\": \"$value\"$comma")
            }
            appendLine("  },")
            appendLine("  \"dns_servers\": {")
            @Suppress("UNCHECKED_CAST")
            val dnsServers = diagnostics["dns_servers"] as? List<String> ?: emptyList()
            dnsServers.forEachIndexed { index, value ->
                val comma = if (index < dnsServers.size - 1) "," else ""
                appendLine("    \"$index\": \"$value\"$comma")
            }
            appendLine("  },")
            appendLine("  \"arp_table\": {")
            @Suppress("UNCHECKED_CAST")
            val arpTable = diagnostics["arp_table"] as? Map<String, String> ?: emptyMap()
            arpTable.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < arpTable.size - 1) "," else ""
                appendLine("    \"$key\": \"$value\"$comma")
            }
            appendLine("  },")
            appendLine("  \"routing_table\": {")
            @Suppress("UNCHECKED_CAST")
            val routingTable = diagnostics["routing_table"] as? List<Map<String, String>> ?: emptyList()
            routingTable.forEachIndexed { index, value ->
                val comma = if (index < routingTable.size - 1) "," else ""
                appendLine("    \"$index\": {")
                appendLine("      \"destination\": \"${value["destination"]}\",")
                appendLine("      \"gateway\": \"${value["gateway"]}\",")
                appendLine("      \"interface\": \"${value["interface"]}\"")
                appendLine("    }$comma")
            }
            appendLine("  },")
            appendLine("  \"active_connections\": {")
            @Suppress("UNCHECKED_CAST")
            val activeConnections = diagnostics["active_connections"] as? List<Map<String, String>> ?: emptyList()
            activeConnections.forEachIndexed { index, value ->
                val comma = if (index < activeConnections.size - 1) "," else ""
                appendLine("    \"$index\": {")
                appendLine("      \"protocol\": \"${value["protocol"]}\",")
                appendLine("      \"localAddress\": \"${value["localAddress"]}\",")
                appendLine("      \"foreignAddress\": \"${value["foreignAddress"]}\",")
                appendLine("      \"state\": \"${value["state"]}\"")
                appendLine("    }$comma")
            }
            appendLine("  },")
            appendLine("  \"listening_ports\": {")
            @Suppress("UNCHECKED_CAST")
            val listeningPorts = diagnostics["listening_ports"] as? List<Int> ?: emptyList()
            listeningPorts.forEachIndexed { index, value ->
                val comma = if (index < listeningPorts.size - 1) "," else ""
                appendLine("    \"$index\": $value$comma")
            }
            appendLine("  },")
            appendLine("  \"tcp_connections\": {")
            @Suppress("UNCHECKED_CAST")
            val tcpConnections = diagnostics["tcp_connections"] as? List<Map<String, String>> ?: emptyList()
            tcpConnections.forEachIndexed { index, value ->
                val comma = if (index < tcpConnections.size - 1) "," else ""
                appendLine("    \"$index\": {")
                appendLine("      \"state\": \"${value["state"]}\",")
                appendLine("      \"localAddress\": \"${value["localAddress"]}\",")
                appendLine("      \"peerAddress\": \"${value["peerAddress"]}\"")
                appendLine("    }$comma")
            }
            appendLine("  },")
            appendLine("  \"udp_connections\": {")
            @Suppress("UNCHECKED_CAST")
            val udpConnections = diagnostics["udp_connections"] as? List<Map<String, String>> ?: emptyList()
            udpConnections.forEachIndexed { index, value ->
                val comma = if (index < udpConnections.size - 1) "," else ""
                appendLine("    \"$index\": {")
                appendLine("      \"state\": \"${value["state"]}\",")
                appendLine("      \"localAddress\": \"${value["localAddress"]}\",")
                appendLine("      \"peerAddress\": \"${value["peerAddress"]}\"")
                appendLine("    }$comma")
            }
            appendLine("  },")
            appendLine("  \"socket_statistics\": {")
            @Suppress("UNCHECKED_CAST")
            val socketStats = diagnostics["socket_statistics"] as? Map<String, Int> ?: emptyMap()
            socketStats.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < socketStats.size - 1) "," else ""
                appendLine("    \"$key\": $value$comma")
            }
            appendLine("  },")
            appendLine("  \"network_usage\": {")
            @Suppress("UNCHECKED_CAST")
            val networkUsage = diagnostics["network_usage"] as? Map<String, Map<String, Long>> ?: emptyMap()
            networkUsage.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < networkUsage.size - 1) "," else ""
                appendLine("    \"$key\": {")
                appendLine("      \"rx_bytes\": ${value["rx_bytes"]},")
                appendLine("      \"tx_bytes\": ${value["tx_bytes"]}")
                appendLine("    }$comma")
            }
            appendLine("  },")
            appendLine("  \"network_speed\": {")
            @Suppress("UNCHECKED_CAST")
            val networkSpeed = diagnostics["network_speed"] as? Map<String, Double> ?: emptyMap()
            networkSpeed.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < networkSpeed.size - 1) "," else ""
                appendLine("    \"$key\": $value$comma")
            }
            appendLine("  },")
            appendLine("  \"capabilities\": {")
            @Suppress("UNCHECKED_CAST")
            val capabilities = diagnostics["capabilities"] as? Map<String, Boolean> ?: emptyMap()
            capabilities.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < capabilities.size - 1) "," else ""
                appendLine("    \"$key\": $value$comma")
            }
            appendLine("  },")
            appendLine("  \"link_properties\": {")
            @Suppress("UNCHECKED_CAST")
            val linkProperties = diagnostics["link_properties"] as? Map<String, Any> ?: emptyMap()
            linkProperties.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < linkProperties.size - 1) "," else ""
                appendLine("    \"$key\": \"$value\"$comma")
            }
            appendLine("  }")
            appendLine("}")
        }
    }

    fun getNetworkDiagnosticsCsv(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("Category,Key,Value")
            @Suppress("UNCHECKED_CAST")
            val info = diagnostics["info"] as? Map<String, Any> ?: emptyMap()
            info.forEach { (key, value) ->
                appendLine("info,$key,$value")
            }
            @Suppress("UNCHECKED_CAST")
            val interfaces = diagnostics["interfaces"] as? List<String> ?: emptyList()
            interfaces.forEachIndexed { index, value ->
                appendLine("interfaces,$index,$value")
            }
            @Suppress("UNCHECKED_CAST")
            val dnsServers = diagnostics["dns_servers"] as? List<String> ?: emptyList()
            dnsServers.forEachIndexed { index, value ->
                appendLine("dns_servers,$index,$value")
            }
            @Suppress("UNCHECKED_CAST")
            val arpTable = diagnostics["arp_table"] as? Map<String, String> ?: emptyMap()
            arpTable.forEach { (key, value) ->
                appendLine("arp_table,$key,$value")
            }
            @Suppress("UNCHECKED_CAST")
            val routingTable = diagnostics["routing_table"] as? List<Map<String, String>> ?: emptyList()
            routingTable.forEachIndexed { index, value ->
                appendLine("routing_table,$index,${value["destination"]},${value["gateway"]},${value["interface"]}")
            }
            @Suppress("UNCHECKED_CAST")
            val activeConnections = diagnostics["active_connections"] as? List<Map<String, String>> ?: emptyList()
            activeConnections.forEachIndexed { index, value ->
                appendLine("active_connections,$index,${value["protocol"]},${value["localAddress"]},${value["foreignAddress"]},${value["state"]}")
            }
            @Suppress("UNCHECKED_CAST")
            val listeningPorts = diagnostics["listening_ports"] as? List<Int> ?: emptyList()
            listeningPorts.forEachIndexed { index, value ->
                appendLine("listening_ports,$index,$value")
            }
            @Suppress("UNCHECKED_CAST")
            val tcpConnections = diagnostics["tcp_connections"] as? List<Map<String, String>> ?: emptyList()
            tcpConnections.forEachIndexed { index, value ->
                appendLine("tcp_connections,$index,${value["state"]},${value["localAddress"]},${value["peerAddress"]}")
            }
            @Suppress("UNCHECKED_CAST")
            val udpConnections = diagnostics["udp_connections"] as? List<Map<String, String>> ?: emptyList()
            udpConnections.forEachIndexed { index, value ->
                appendLine("udp_connections,$index,${value["state"]},${value["localAddress"]},${value["peerAddress"]}")
            }
            @Suppress("UNCHECKED_CAST")
            val socketStats = diagnostics["socket_statistics"] as? Map<String, Int> ?: emptyMap()
            socketStats.forEach { (key, value) ->
                appendLine("socket_statistics,$key,$value")
            }
            @Suppress("UNCHECKED_CAST")
            val networkUsage = diagnostics["network_usage"] as? Map<String, Map<String, Long>> ?: emptyMap()
            networkUsage.forEach { (key, value) ->
                appendLine("network_usage,$key,rx=${value["rx_bytes"]},tx=${value["tx_bytes"]}")
            }
            @Suppress("UNCHECKED_CAST")
            val networkSpeed = diagnostics["network_speed"] as? Map<String, Double> ?: emptyMap()
            networkSpeed.forEach { (key, value) ->
                appendLine("network_speed,$key,$value")
            }
            @Suppress("UNCHECKED_CAST")
            val capabilities = diagnostics["capabilities"] as? Map<String, Boolean> ?: emptyMap()
            capabilities.forEach { (key, value) ->
                appendLine("capabilities,$key,$value")
            }
            @Suppress("UNCHECKED_CAST")
            val linkProperties = diagnostics["link_properties"] as? Map<String, Any> ?: emptyMap()
            linkProperties.forEach { (key, value) ->
                appendLine("link_properties,$key,$value")
            }
        }
    }

    fun getNetworkDiagnosticsTsv(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("Category\tKey\tValue")
            @Suppress("UNCHECKED_CAST")
            val info = diagnostics["info"] as? Map<String, Any> ?: emptyMap()
            info.forEach { (key, value) ->
                appendLine("info\t$key\t$value")
            }
            @Suppress("UNCHECKED_CAST")
            val interfaces = diagnostics["interfaces"] as? List<String> ?: emptyList()
            interfaces.forEachIndexed { index, value ->
                appendLine("interfaces\t$index\t$value")
            }
            @Suppress("UNCHECKED_CAST")
            val dnsServers = diagnostics["dns_servers"] as? List<String> ?: emptyList()
            dnsServers.forEachIndexed { index, value ->
                appendLine("dns_servers\t$index\t$value")
            }
            @Suppress("UNCHECKED_CAST")
            val arpTable = diagnostics["arp_table"] as? Map<String, String> ?: emptyMap()
            arpTable.forEach { (key, value) ->
                appendLine("arp_table\t$key\t$value")
            }
            @Suppress("UNCHECKED_CAST")
            val routingTable = diagnostics["routing_table"] as? List<Map<String, String>> ?: emptyList()
            routingTable.forEachIndexed { index, value ->
                appendLine("routing_table\t$index\t${value["destination"]}\t${value["gateway"]}\t${value["interface"]}")
            }
            @Suppress("UNCHECKED_CAST")
            val activeConnections = diagnostics["active_connections"] as? List<Map<String, String>> ?: emptyList()
            activeConnections.forEachIndexed { index, value ->
                appendLine("active_connections\t$index\t${value["protocol"]}\t${value["localAddress"]}\t${value["foreignAddress"]}\t${value["state"]}")
            }
            @Suppress("UNCHECKED_CAST")
            val listeningPorts = diagnostics["listening_ports"] as? List<Int> ?: emptyList()
            listeningPorts.forEachIndexed { index, value ->
                appendLine("listening_ports\t$index\t$value")
            }
            @Suppress("UNCHECKED_CAST")
            val tcpConnections = diagnostics["tcp_connections"] as? List<Map<String, String>> ?: emptyList()
            tcpConnections.forEachIndexed { index, value ->
                appendLine("tcp_connections\t$index\t${value["state"]}\t${value["localAddress"]}\t${value["peerAddress"]}")
            }
            @Suppress("UNCHECKED_CAST")
            val udpConnections = diagnostics["udp_connections"] as? List<Map<String, String>> ?: emptyList()
            udpConnections.forEachIndexed { index, value ->
                appendLine("udp_connections\t$index\t${value["state"]}\t${value["localAddress"]}\t${value["peerAddress"]}")
            }
            @Suppress("UNCHECKED_CAST")
            val socketStats = diagnostics["socket_statistics"] as? Map<String, Int> ?: emptyMap()
            socketStats.forEach { (key, value) ->
                appendLine("socket_statistics\t$key\t$value")
            }
            @Suppress("UNCHECKED_CAST")
            val networkUsage = diagnostics["network_usage"] as? Map<String, Map<String, Long>> ?: emptyMap()
            networkUsage.forEach { (key, value) ->
                appendLine("network_usage\t$key\trx=${value["rx_bytes"]}\ttx=${value["tx_bytes"]}")
            }
            @Suppress("UNCHECKED_CAST")
            val networkSpeed = diagnostics["network_speed"] as? Map<String, Double> ?: emptyMap()
            networkSpeed.forEach { (key, value) ->
                appendLine("network_speed\t$key\t$value")
            }
            @Suppress("UNCHECKED_CAST")
            val capabilities = diagnostics["capabilities"] as? Map<String, Boolean> ?: emptyMap()
            capabilities.forEach { (key, value) ->
                appendLine("capabilities\t$key\t$value")
            }
            @Suppress("UNCHECKED_CAST")
            val linkProperties = diagnostics["link_properties"] as? Map<String, Any> ?: emptyMap()
            linkProperties.forEach { (key, value) ->
                appendLine("link_properties\t$key\t$value")
            }
        }
    }

    fun getNetworkDiagnosticsXml(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            appendLine("<network_diagnostics>")
            appendLine("  <info>")
            @Suppress("UNCHECKED_CAST")
            val info = diagnostics["info"] as? Map<String, Any> ?: emptyMap()
            info.forEach { (key, value) ->
                appendLine("    <$key>$value</$key>")
            }
            appendLine("  </info>")
            appendLine("  <summary>")
            @Suppress("UNCHECKED_CAST")
            val summary = diagnostics["summary"] as? String ?: ""
            appendLine("    <text>$summary</text>")
            appendLine("  </summary>")
            appendLine("  <report>")
            @Suppress("UNCHECKED_CAST")
            val report = diagnostics["report"] as? String ?: ""
            appendLine("    <text>$report</text>")
            appendLine("  </report>")
            appendLine("  <interfaces>")
            @Suppress("UNCHECKED_CAST")
            val interfaces = diagnostics["interfaces"] as? List<String> ?: emptyList()
            interfaces.forEachIndexed { index, value ->
                appendLine("    <interface index=\"$index\">$value</interface>")
            }
            appendLine("  </interfaces>")
            appendLine("  <dns_servers>")
            @Suppress("UNCHECKED_CAST")
            val dnsServers = diagnostics["dns_servers"] as? List<String> ?: emptyList()
            dnsServers.forEachIndexed { index, value ->
                appendLine("    <dns_server index=\"$index\">$value</dns_server>")
            }
            appendLine("  </dns_servers>")
            appendLine("  <arp_table>")
            @Suppress("UNCHECKED_CAST")
            val arpTable = diagnostics["arp_table"] as? Map<String, String> ?: emptyMap()
            arpTable.forEach { (key, value) ->
                appendLine("    <entry ip=\"$key\" mac=\"$value\"/>")
            }
            appendLine("  </arp_table>")
            appendLine("  <routing_table>")
            @Suppress("UNCHECKED_CAST")
            val routingTable = diagnostics["routing_table"] as? List<Map<String, String>> ?: emptyList()
            routingTable.forEachIndexed { index, value ->
                appendLine("    <route index=\"$index\" destination=\"${value["destination"]}\" gateway=\"${value["gateway"]}\" interface=\"${value["interface"]}\"/>")
            }
            appendLine("  </routing_table>")
            appendLine("  <active_connections>")
            @Suppress("UNCHECKED_CAST")
            val activeConnections = diagnostics["active_connections"] as? List<Map<String, String>> ?: emptyList()
            activeConnections.forEachIndexed { index, value ->
                appendLine("    <connection index=\"$index\" protocol=\"${value["protocol"]}\" local=\"${value["localAddress"]}\" remote=\"${value["foreignAddress"]}\" state=\"${value["state"]}\"/>")
            }
            appendLine("  </active_connections>")
            appendLine("  <listening_ports>")
            @Suppress("UNCHECKED_CAST")
            val listeningPorts = diagnostics["listening_ports"] as? List<Int> ?: emptyList()
            listeningPorts.forEachIndexed { index, value ->
                appendLine("    <port index=\"$index\">$value</port>")
            }
            appendLine("  </listening_ports>")
            appendLine("  <tcp_connections>")
            @Suppress("UNCHECKED_CAST")
            val tcpConnections = diagnostics["tcp_connections"] as? List<Map<String, String>> ?: emptyList()
            tcpConnections.forEachIndexed { index, value ->
                appendLine("    <connection index=\"$index\" state=\"${value["state"]}\" local=\"${value["localAddress"]}\" remote=\"${value["peerAddress"]}\"/>")
            }
            appendLine("  </tcp_connections>")
            appendLine("  <udp_connections>")
            @Suppress("UNCHECKED_CAST")
            val udpConnections = diagnostics["udp_connections"] as? List<Map<String, String>> ?: emptyList()
            udpConnections.forEachIndexed { index, value ->
                appendLine("    <connection index=\"$index\" state=\"${value["state"]}\" local=\"${value["localAddress"]}\" remote=\"${value["peerAddress"]}\"/>")
            }
            appendLine("  </udp_connections>")
            appendLine("  <socket_statistics>")
            @Suppress("UNCHECKED_CAST")
            val socketStats = diagnostics["socket_statistics"] as? Map<String, Int> ?: emptyMap()
            socketStats.forEach { (key, value) ->
                appendLine("    <stat name=\"$key\">$value</stat>")
            }
            appendLine("  </socket_statistics>")
            appendLine("  <network_usage>")
            @Suppress("UNCHECKED_CAST")
            val networkUsage = diagnostics["network_usage"] as? Map<String, Map<String, Long>> ?: emptyMap()
            networkUsage.forEach { (key, value) ->
                appendLine("    <interface name=\"$key\" rx_bytes=\"${value["rx_bytes"]}\" tx_bytes=\"${value["tx_bytes"]}\"/>")
            }
            appendLine("  </network_usage>")
            appendLine("  <network_speed>")
            @Suppress("UNCHECKED_CAST")
            val networkSpeed = diagnostics["network_speed"] as? Map<String, Double> ?: emptyMap()
            networkSpeed.forEach { (key, value) ->
                appendLine("    <interface name=\"$key\" speed=\"$value\"/>")
            }
            appendLine("  </network_speed>")
            appendLine("  <capabilities>")
            @Suppress("UNCHECKED_CAST")
            val capabilities = diagnostics["capabilities"] as? Map<String, Boolean> ?: emptyMap()
            capabilities.forEach { (key, value) ->
                appendLine("    <capability name=\"$key\">$value</capability>")
            }
            appendLine("  </capabilities>")
            appendLine("  <link_properties>")
            @Suppress("UNCHECKED_CAST")
            val linkProperties = diagnostics["link_properties"] as? Map<String, Any> ?: emptyMap()
            linkProperties.forEach { (key, value) ->
                appendLine("    <property name=\"$key\">$value</property>")
            }
            appendLine("  </link_properties>")
            appendLine("</network_diagnostics>")
        }
    }

    fun getNetworkDiagnosticsYaml(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("network_diagnostics:")
            appendLine("  info:")
            @Suppress("UNCHECKED_CAST")
            val info = diagnostics["info"] as? Map<String, Any> ?: emptyMap()
            info.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
            appendLine("  summary:")
            @Suppress("UNCHECKED_CAST")
            val summary = diagnostics["summary"] as? String ?: ""
            appendLine("    text: $summary")
            appendLine("  report:")
            @Suppress("UNCHECKED_CAST")
            val report = diagnostics["report"] as? String ?: ""
            appendLine("    text: $report")
            appendLine("  interfaces:")
            @Suppress("UNCHECKED_CAST")
            val interfaces = diagnostics["interfaces"] as? List<String> ?: emptyList()
            interfaces.forEachIndexed { index, value ->
                appendLine("    - $value")
            }
            appendLine("  dns_servers:")
            @Suppress("UNCHECKED_CAST")
            val dnsServers = diagnostics["dns_servers"] as? List<String> ?: emptyList()
            dnsServers.forEachIndexed { index, value ->
                appendLine("    - $value")
            }
            appendLine("  arp_table:")
            @Suppress("UNCHECKED_CAST")
            val arpTable = diagnostics["arp_table"] as? Map<String, String> ?: emptyMap()
            arpTable.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
            appendLine("  routing_table:")
            @Suppress("UNCHECKED_CAST")
            val routingTable = diagnostics["routing_table"] as? List<Map<String, String>> ?: emptyList()
            routingTable.forEachIndexed { index, value ->
                appendLine("    - destination: ${value["destination"]}")
                appendLine("      gateway: ${value["gateway"]}")
                appendLine("      interface: ${value["interface"]}")
            }
            appendLine("  active_connections:")
            @Suppress("UNCHECKED_CAST")
            val activeConnections = diagnostics["active_connections"] as? List<Map<String, String>> ?: emptyList()
            activeConnections.forEachIndexed { index, value ->
                appendLine("    - protocol: ${value["protocol"]}")
                appendLine("      localAddress: ${value["localAddress"]}")
                appendLine("      foreignAddress: ${value["foreignAddress"]}")
                appendLine("      state: ${value["state"]}")
            }
            appendLine("  listening_ports:")
            @Suppress("UNCHECKED_CAST")
            val listeningPorts = diagnostics["listening_ports"] as? List<Int> ?: emptyList()
            listeningPorts.forEachIndexed { index, value ->
                appendLine("    - $value")
            }
            appendLine("  tcp_connections:")
            @Suppress("UNCHECKED_CAST")
            val tcpConnections = diagnostics["tcp_connections"] as? List<Map<String, String>> ?: emptyList()
            tcpConnections.forEachIndexed { index, value ->
                appendLine("    - state: ${value["state"]}")
                appendLine("      localAddress: ${value["localAddress"]}")
                appendLine("      peerAddress: ${value["peerAddress"]}")
            }
            appendLine("  udp_connections:")
            @Suppress("UNCHECKED_CAST")
            val udpConnections = diagnostics["udp_connections"] as? List<Map<String, String>> ?: emptyList()
            udpConnections.forEachIndexed { index, value ->
                appendLine("    - state: ${value["state"]}")
                appendLine("      localAddress: ${value["localAddress"]}")
                appendLine("      peerAddress: ${value["peerAddress"]}")
            }
            appendLine("  socket_statistics:")
            @Suppress("UNCHECKED_CAST")
            val socketStats = diagnostics["socket_statistics"] as? Map<String, Int> ?: emptyMap()
            socketStats.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
            appendLine("  network_usage:")
            @Suppress("UNCHECKED_CAST")
            val networkUsage = diagnostics["network_usage"] as? Map<String, Map<String, Long>> ?: emptyMap()
            networkUsage.forEach { (key, value) ->
                appendLine("    $key:")
                appendLine("      rx_bytes: ${value["rx_bytes"]}")
                appendLine("      tx_bytes: ${value["tx_bytes"]}")
            }
            appendLine("  network_speed:")
            @Suppress("UNCHECKED_CAST")
            val networkSpeed = diagnostics["network_speed"] as? Map<String, Double> ?: emptyMap()
            networkSpeed.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
            appendLine("  capabilities:")
            @Suppress("UNCHECKED_CAST")
            val capabilities = diagnostics["capabilities"] as? Map<String, Boolean> ?: emptyMap()
            capabilities.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
            appendLine("  link_properties:")
            @Suppress("UNCHECKED_CAST")
            val linkProperties = diagnostics["link_properties"] as? Map<String, Any> ?: emptyMap()
            linkProperties.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
        }
    }

    fun getNetworkDiagnosticsToml(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("[network_diagnostics]")
            appendLine("[network_diagnostics.info]")
            @Suppress("UNCHECKED_CAST")
            val info = diagnostics["info"] as? Map<String, Any> ?: emptyMap()
            info.forEach { (key, value) ->
                appendLine("$key = \"$value\"")
            }
            appendLine("[network_diagnostics.summary]")
            @Suppress("UNCHECKED_CAST")
            val summary = diagnostics["summary"] as? String ?: ""
            appendLine("text = \"$summary\"")
            appendLine("[network_diagnostics.report]")
            @Suppress("UNCHECKED_CAST")
            val report = diagnostics["report"] as? String ?: ""
            appendLine("text = \"$report\"")
            appendLine("[network_diagnostics.interfaces]")
            @Suppress("UNCHECKED_CAST")
            val interfaces = diagnostics["interfaces"] as? List<String> ?: emptyList()
            interfaces.forEachIndexed { index, value ->
                appendLine("interface_$index = \"$value\"")
            }
            appendLine("[network_diagnostics.dns_servers]")
            @Suppress("UNCHECKED_CAST")
            val dnsServers = diagnostics["dns_servers"] as? List<String> ?: emptyList()
            dnsServers.forEachIndexed { index, value ->
                appendLine("dns_server_$index = \"$value\"")
            }
            appendLine("[network_diagnostics.arp_table]")
            @Suppress("UNCHECKED_CAST")
            val arpTable = diagnostics["arp_table"] as? Map<String, String> ?: emptyMap()
            arpTable.forEach { (key, value) ->
                appendLine("\"$key\" = \"$value\"")
            }
            appendLine("[network_diagnostics.routing_table]")
            @Suppress("UNCHECKED_CAST")
            val routingTable = diagnostics["routing_table"] as? List<Map<String, String>> ?: emptyList()
            routingTable.forEachIndexed { index, value ->
                appendLine("[network_diagnostics.routing_table.route_$index]")
                appendLine("destination = \"${value["destination"]}\"")
                appendLine("gateway = \"${value["gateway"]}\"")
                appendLine("interface = \"${value["interface"]}\"")
            }
            appendLine("[network_diagnostics.active_connections]")
            @Suppress("UNCHECKED_CAST")
            val activeConnections = diagnostics["active_connections"] as? List<Map<String, String>> ?: emptyList()
            activeConnections.forEachIndexed { index, value ->
                appendLine("[network_diagnostics.active_connections.connection_$index]")
                appendLine("protocol = \"${value["protocol"]}\"")
                appendLine("localAddress = \"${value["localAddress"]}\"")
                appendLine("foreignAddress = \"${value["foreignAddress"]}\"")
                appendLine("state = \"${value["state"]}\"")
            }
            appendLine("[network_diagnostics.listening_ports]")
            @Suppress("UNCHECKED_CAST")
            val listeningPorts = diagnostics["listening_ports"] as? List<Int> ?: emptyList()
            listeningPorts.forEachIndexed { index, value ->
                appendLine("port_$index = $value")
            }
            appendLine("[network_diagnostics.tcp_connections]")
            @Suppress("UNCHECKED_CAST")
            val tcpConnections = diagnostics["tcp_connections"] as? List<Map<String, String>> ?: emptyList()
            tcpConnections.forEachIndexed { index, value ->
                appendLine("[network_diagnostics.tcp_connections.connection_$index]")
                appendLine("state = \"${value["state"]}\"")
                appendLine("localAddress = \"${value["localAddress"]}\"")
                appendLine("peerAddress = \"${value["peerAddress"]}\"")
            }
            appendLine("[network_diagnostics.udp_connections]")
            @Suppress("UNCHECKED_CAST")
            val udpConnections = diagnostics["udp_connections"] as? List<Map<String, String>> ?: emptyList()
            udpConnections.forEachIndexed { index, value ->
                appendLine("[network_diagnostics.udp_connections.connection_$index]")
                appendLine("state = \"${value["state"]}\"")
                appendLine("localAddress = \"${value["localAddress"]}\"")
                appendLine("peerAddress = \"${value["peerAddress"]}\"")
            }
            appendLine("[network_diagnostics.socket_statistics]")
            @Suppress("UNCHECKED_CAST")
            val socketStats = diagnostics["socket_statistics"] as? Map<String, Int> ?: emptyMap()
            socketStats.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[network_diagnostics.network_usage]")
            @Suppress("UNCHECKED_CAST")
            val networkUsage = diagnostics["network_usage"] as? Map<String, Map<String, Long>> ?: emptyMap()
            networkUsage.forEach { (key, value) ->
                appendLine("[network_diagnostics.network_usage.$key]")
                appendLine("rx_bytes = ${value["rx_bytes"]}")
                appendLine("tx_bytes = ${value["tx_bytes"]}")
            }
            appendLine("[network_diagnostics.network_speed]")
            @Suppress("UNCHECKED_CAST")
            val networkSpeed = diagnostics["network_speed"] as? Map<String, Double> ?: emptyMap()
            networkSpeed.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[network_diagnostics.capabilities]")
            @Suppress("UNCHECKED_CAST")
            val capabilities = diagnostics["capabilities"] as? Map<String, Boolean> ?: emptyMap()
            capabilities.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[network_diagnostics.link_properties]")
            @Suppress("UNCHECKED_CAST")
            val linkProperties = diagnostics["link_properties"] as? Map<String, Any> ?: emptyMap()
            linkProperties.forEach { (key, value) ->
                appendLine("$key = \"$value\"")
            }
        }
    }

    fun getNetworkDiagnosticsIni(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("[network_diagnostics]")
            appendLine("[info]")
            @Suppress("UNCHECKED_CAST")
            val info = diagnostics["info"] as? Map<String, Any> ?: emptyMap()
            info.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[summary]")
            @Suppress("UNCHECKED_CAST")
            val summary = diagnostics["summary"] as? String ?: ""
            appendLine("text = $summary")
            appendLine("[report]")
            @Suppress("UNCHECKED_CAST")
            val report = diagnostics["report"] as? String ?: ""
            appendLine("text = $report")
            appendLine("[interfaces]")
            @Suppress("UNCHECKED_CAST")
            val interfaces = diagnostics["interfaces"] as? List<String> ?: emptyList()
            interfaces.forEachIndexed { index, value ->
                appendLine("interface_$index = $value")
            }
            appendLine("[dns_servers]")
            @Suppress("UNCHECKED_CAST")
            val dnsServers = diagnostics["dns_servers"] as? List<String> ?: emptyList()
            dnsServers.forEachIndexed { index, value ->
                appendLine("dns_server_$index = $value")
            }
            appendLine("[arp_table]")
            @Suppress("UNCHECKED_CAST")
            val arpTable = diagnostics["arp_table"] as? Map<String, String> ?: emptyMap()
            arpTable.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[routing_table]")
            @Suppress("UNCHECKED_CAST")
            val routingTable = diagnostics["routing_table"] as? List<Map<String, String>> ?: emptyList()
            routingTable.forEachIndexed { index, value ->
                appendLine("route_$index = ${value["destination"]},${value["gateway"]},${value["interface"]}")
            }
            appendLine("[active_connections]")
            @Suppress("UNCHECKED_CAST")
            val activeConnections = diagnostics["active_connections"] as? List<Map<String, String>> ?: emptyList()
            activeConnections.forEachIndexed { index, value ->
                appendLine("connection_$index = ${value["protocol"]},${value["localAddress"]},${value["foreignAddress"]},${value["state"]}")
            }
            appendLine("[listening_ports]")
            @Suppress("UNCHECKED_CAST")
            val listeningPorts = diagnostics["listening_ports"] as? List<Int> ?: emptyList()
            listeningPorts.forEachIndexed { index, value ->
                appendLine("port_$index = $value")
            }
            appendLine("[tcp_connections]")
            @Suppress("UNCHECKED_CAST")
            val tcpConnections = diagnostics["tcp_connections"] as? List<Map<String, String>> ?: emptyList()
            tcpConnections.forEachIndexed { index, value ->
                appendLine("connection_$index = ${value["state"]},${value["localAddress"]},${value["peerAddress"]}")
            }
            appendLine("[udp_connections]")
            @Suppress("UNCHECKED_CAST")
            val udpConnections = diagnostics["udp_connections"] as? List<Map<String, String>> ?: emptyList()
            udpConnections.forEachIndexed { index, value ->
                appendLine("connection_$index = ${value["state"]},${value["localAddress"]},${value["peerAddress"]}")
            }
            appendLine("[socket_statistics]")
            @Suppress("UNCHECKED_CAST")
            val socketStats = diagnostics["socket_statistics"] as? Map<String, Int> ?: emptyMap()
            socketStats.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[network_usage]")
            @Suppress("UNCHECKED_CAST")
            val networkUsage = diagnostics["network_usage"] as? Map<String, Map<String, Long>> ?: emptyMap()
            networkUsage.forEach { (key, value) ->
                appendLine("$key = rx=${value["rx_bytes"]},tx=${value["tx_bytes"]}")
            }
            appendLine("[network_speed]")
            @Suppress("UNCHECKED_CAST")
            val networkSpeed = diagnostics["network_speed"] as? Map<String, Double> ?: emptyMap()
            networkSpeed.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[capabilities]")
            @Suppress("UNCHECKED_CAST")
            val capabilities = diagnostics["capabilities"] as? Map<String, Boolean> ?: emptyMap()
            capabilities.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[link_properties]")
            @Suppress("UNCHECKED_CAST")
            val linkProperties = diagnostics["link_properties"] as? Map<String, Any> ?: emptyMap()
            linkProperties.forEach { (key, value) ->
                appendLine("$key = $value")
            }
        }
    }

    fun getNetworkDiagnosticsProperties(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("# Network Diagnostics Properties")
            appendLine("[info]")
            @Suppress("UNCHECKED_CAST")
            val info = diagnostics["info"] as? Map<String, Any> ?: emptyMap()
            info.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[summary]")
            @Suppress("UNCHECKED_CAST")
            val summary = diagnostics["summary"] as? String ?: ""
            appendLine("text = $summary")
            appendLine("[report]")
            @Suppress("UNCHECKED_CAST")
            val report = diagnostics["report"] as? String ?: ""
            appendLine("text = $report")
            appendLine("[interfaces]")
            @Suppress("UNCHECKED_CAST")
            val interfaces = diagnostics["interfaces"] as? List<String> ?: emptyList()
            interfaces.forEachIndexed { index, value ->
                appendLine("interface_$index = $value")
            }
            appendLine("[dns_servers]")
            @Suppress("UNCHECKED_CAST")
            val dnsServers = diagnostics["dns_servers"] as? List<String> ?: emptyList()
            dnsServers.forEachIndexed { index, value ->
                appendLine("dns_server_$index = $value")
            }
            appendLine("[arp_table]")
            @Suppress("UNCHECKED_CAST")
            val arpTable = diagnostics["arp_table"] as? Map<String, String> ?: emptyMap()
            arpTable.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[routing_table]")
            @Suppress("UNCHECKED_CAST")
            val routingTable = diagnostics["routing_table"] as? List<Map<String, String>> ?: emptyList()
            routingTable.forEachIndexed { index, value ->
                appendLine("route_$index = ${value["destination"]},${value["gateway"]},${value["interface"]}")
            }
            appendLine("[active_connections]")
            @Suppress("UNCHECKED_CAST")
            val activeConnections = diagnostics["active_connections"] as? List<Map<String, String>> ?: emptyList()
            activeConnections.forEachIndexed { index, value ->
                appendLine("connection_$index = ${value["protocol"]},${value["localAddress"]},${value["foreignAddress"]},${value["state"]}")
            }
            appendLine("[listening_ports]")
            @Suppress("UNCHECKED_CAST")
            val listeningPorts = diagnostics["listening_ports"] as? List<Int> ?: emptyList()
            listeningPorts.forEachIndexed { index, value ->
                appendLine("port_$index = $value")
            }
            appendLine("[tcp_connections]")
            @Suppress("UNCHECKED_CAST")
            val tcpConnections = diagnostics["tcp_connections"] as? List<Map<String, String>> ?: emptyList()
            tcpConnections.forEachIndexed { index, value ->
                appendLine("connection_$index = ${value["state"]},${value["localAddress"]},${value["peerAddress"]}")
            }
            appendLine("[udp_connections]")
            @Suppress("UNCHECKED_CAST")
            val udpConnections = diagnostics["udp_connections"] as? List<Map<String, String>> ?: emptyList()
            udpConnections.forEachIndexed { index, value ->
                appendLine("connection_$index = ${value["state"]},${value["localAddress"]},${value["peerAddress"]}")
            }
            appendLine("[socket_statistics]")
            @Suppress("UNCHECKED_CAST")
            val socketStats = diagnostics["socket_statistics"] as? Map<String, Int> ?: emptyMap()
            socketStats.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[network_usage]")
            @Suppress("UNCHECKED_CAST")
            val networkUsage = diagnostics["network_usage"] as? Map<String, Map<String, Long>> ?: emptyMap()
            networkUsage.forEach { (key, value) ->
                appendLine("$key = rx=${value["rx_bytes"]},tx=${value["tx_bytes"]}")
            }
            appendLine("[network_speed]")
            @Suppress("UNCHECKED_CAST")
            val networkSpeed = diagnostics["network_speed"] as? Map<String, Double> ?: emptyMap()
            networkSpeed.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[capabilities]")
            @Suppress("UNCHECKED_CAST")
            val capabilities = diagnostics["capabilities"] as? Map<String, Boolean> ?: emptyMap()
            capabilities.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[link_properties]")
            @Suppress("UNCHECKED_CAST")
            val linkProperties = diagnostics["link_properties"] as? Map<String, Any> ?: emptyMap()
            linkProperties.forEach { (key, value) ->
                appendLine("$key = $value")
            }
        }
    }

    fun getNetworkDiagnosticsEnv(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("# Network Diagnostics Environment Variables")
            appendLine("export NETWORK_TYPE=\"${getNetworkType()}\"")
            appendLine("export NETWORK_SUBTYPE=\"${getNetworkSubtype()}\"")
            appendLine("export NETWORK_OPERATOR=\"${getNetworkOperator()}\"")
            appendLine("export NETWORK_SIM_OPERATOR=\"${getSimOperator()}\"")
            appendLine("export NETWORK_COUNTRY=\"${getNetworkCountry()}\"")
            appendLine("export NETWORK_SIM_COUNTRY=\"${getSimCountry()}\"")
            appendLine("export NETWORK_MCC=\"${getNetworkMcc()}\"")
            appendLine("export NETWORK_MNC=\"${getNetworkMnc()}\"")
            appendLine("export NETWORK_SIM_MCC=\"${getSimMcc()}\"")
            appendLine("export NETWORK_SIM_MNC=\"${getSimMnc()}\"")
            appendLine("export NETWORK_ISO=\"${getNetworkIso()}\"")
            appendLine("export NETWORK_SIM_ISO=\"${getSimIso()}\"")
            appendLine("export NETWORK_OPERATOR_NAME=\"${getNetworkOperatorName()}\"")
            appendLine("export NETWORK_SIM_OPERATOR_NAME=\"${getSimOperatorName()}\"")
            appendLine("export NETWORK_PHONE_TYPE=\"${getPhoneType()}\"")
            appendLine("export NETWORK_DEVICE_TYPE=\"${getDeviceType()}\"")
            appendLine("export NETWORK_IS_WIFI=\"${isWifiConnected()}\"")
            appendLine("export NETWORK_IS_ETHERNET=\"${isEthernetConnected()}\"")
            appendLine("export NETWORK_IS_MOBILE=\"${isMobileConnected()}\"")
            appendLine("export NETWORK_IS_VPN=\"${isVpnConnected()}\"")
            appendLine("export NETWORK_LOCAL_IP=\"${getLocalIpAddress() ?: ""}\"")
            appendLine("export NETWORK_LOCAL_IPV6=\"${getLocalIpv6Address() ?: ""}\"")
            appendLine("export NETWORK_PUBLIC_IP=\"${getPublicIpAddress() ?: ""}\"")
            appendLine("export NETWORK_PUBLIC_IPV6=\"${getPublicIpAddressV6() ?: ""}\"")
            appendLine("export NETWORK_MAC_ADDRESS=\"${getMacAddress() ?: ""}\"")
            appendLine("export NETWORK_HOSTNAME=\"${getHostname() ?: ""}\"")
            appendLine("export NETWORK_HOST_ADDRESS=\"${getHostAddress() ?: ""}\"")
            appendLine("export NETWORK_CANONICAL_HOSTNAME=\"${getCanonicalHostname() ?: ""}\"")
            appendLine("export NETWORK_DNS_SERVER=\"${getDnsServer() ?: ""}\"")
            appendLine("export NETWORK_ACTIVE_INTERFACE=\"${getActiveNetworkInterface() ?: ""}\"")
        }
    }

    fun getNetworkDiagnosticsShell(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("#!/bin/bash")
            appendLine("# Network Diagnostics Shell Script")
            appendLine("echo \"Network Diagnostics\"")
            appendLine("echo \"===================\"")
            appendLine("echo \"Type: ${getNetworkType()}\"")
            appendLine("echo \"Subtype: ${getNetworkSubtype()}\"")
            appendLine("echo \"Operator: ${getNetworkOperator()}\"")
            appendLine("echo \"SIM Operator: ${getSimOperator()}\"")
            appendLine("echo \"Country: ${getNetworkCountry()}\"")
            appendLine("echo \"SIM Country: ${getSimCountry()}\"")
            appendLine("echo \"MCC: ${getNetworkMcc()}\"")
            appendLine("echo \"MNC: ${getNetworkMnc()}\"")
            appendLine("echo \"SIM MCC: ${getSimMcc()}\"")
            appendLine("echo \"SIM MNC: ${getSimMnc()}\"")
            appendLine("echo \"ISO: ${getNetworkIso()}\"")
            appendLine("echo \"SIM ISO: ${getSimIso()}\"")
            appendLine("echo \"Operator Name: ${getNetworkOperatorName()}\"")
            appendLine("echo \"SIM Operator Name: ${getSimOperatorName()}\"")
            appendLine("echo \"Phone Type: ${getPhoneType()}\"")
            appendLine("echo \"Device Type: ${getDeviceType()}\"")
            appendLine("echo \"Is WiFi: ${isWifiConnected()}\"")
            appendLine("echo \"Is Ethernet: ${isEthernetConnected()}\"")
            appendLine("echo \"Is Mobile: ${isMobileConnected()}\"")
            appendLine("echo \"Is VPN: ${isVpnConnected()}\"")
            appendLine("echo \"Local IP: ${getLocalIpAddress() ?: ""}\"")
            appendLine("echo \"Local IPv6: ${getLocalIpv6Address() ?: ""}\"")
            appendLine("echo \"Public IP: ${getPublicIpAddress() ?: ""}\"")
            appendLine("echo \"Public IPv6: ${getPublicIpAddressV6() ?: ""}\"")
            appendLine("echo \"MAC Address: ${getMacAddress() ?: ""}\"")
            appendLine("echo \"Hostname: ${getHostname() ?: ""}\"")
            appendLine("echo \"Host Address: ${getHostAddress() ?: ""}\"")
            appendLine("echo \"Canonical Hostname: ${getCanonicalHostname() ?: ""}\"")
            appendLine("echo \"DNS Server: ${getDnsServer() ?: ""}\"")
            appendLine("echo \"Active Interface: ${getActiveNetworkInterface() ?: ""}\"")
            appendLine("echo \"\"")
            appendLine("echo \"Network Interfaces:\"")
            getNetworkInterfaces().forEach {
                appendLine("echo \"  $it\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"DNS Servers:\"")
            getDnsServers().forEach {
                appendLine("echo \"  $it\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"ARP Table:\"")
            getArpTable().forEach { (key, value) ->
                appendLine("echo \"  $key -> $value\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Routing Table:\"")
            getRoutingTable().forEach {
                appendLine("echo \"  ${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Active Connections:\"")
            getActiveConnections().forEach {
                appendLine("echo \"  ${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Listening Ports:\"")
            appendLine("echo \"  ${getListeningPorts().joinToString(", ")}\"")
            appendLine("echo \"\"")
            appendLine("echo \"TCP Connections:\"")
            getTcpConnections().forEach {
                appendLine("echo \"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"UDP Connections:\"")
            getUdpConnections().forEach {
                appendLine("echo \"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Socket Statistics:\"")
            getSocketStatistics().forEach { (key, value) ->
                appendLine("echo \"  $key: $value\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Network Usage:\"")
            getNetworkUsage().forEach { (key, value) ->
                appendLine("echo \"  $key: rx=${value["rx_bytes"]}, tx=${value["tx_bytes"]}\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Network Speed:\"")
            getNetworkSpeed().forEach { (key, value) ->
                appendLine("echo \"  $key: $value bytes/s\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Network Capabilities:\"")
            getNetworkCapabilities().forEach { (key, value) ->
                appendLine("echo \"  $key: $value\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Link Properties:\"")
            getLinkProperties().forEach { (key, value) ->
                appendLine("echo \"  $key: $value\"")
            }
        }
    }

    fun getNetworkDiagnosticsBatch(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("@echo off")
            appendLine("REM Network Diagnostics Batch Script")
            appendLine("echo Network Diagnostics")
            appendLine("echo ===================")
            appendLine("echo Type: ${getNetworkType()}")
            appendLine("echo Subtype: ${getNetworkSubtype()}")
            appendLine("echo Operator: ${getNetworkOperator()}")
            appendLine("echo SIM Operator: ${getSimOperator()}")
            appendLine("echo Country: ${getNetworkCountry()}")
            appendLine("echo SIM Country: ${getSimCountry()}")
            appendLine("echo MCC: ${getNetworkMcc()}")
            appendLine("echo MNC: ${getNetworkMnc()}")
            appendLine("echo SIM MCC: ${getSimMcc()}")
            appendLine("echo SIM MNC: ${getSimMnc()}")
            appendLine("echo ISO: ${getNetworkIso()}")
            appendLine("echo SIM ISO: ${getSimIso()}")
            appendLine("echo Operator Name: ${getNetworkOperatorName()}")
            appendLine("echo SIM Operator Name: ${getSimOperatorName()}")
            appendLine("echo Phone Type: ${getPhoneType()}")
            appendLine("echo Device Type: ${getDeviceType()}")
            appendLine("echo Is WiFi: ${isWifiConnected()}")
            appendLine("echo Is Ethernet: ${isEthernetConnected()}")
            appendLine("echo Is Mobile: ${isMobileConnected()}")
            appendLine("echo Is VPN: ${isVpnConnected()}")
            appendLine("echo Local IP: ${getLocalIpAddress() ?: ""}")
            appendLine("echo Local IPv6: ${getLocalIpv6Address() ?: ""}")
            appendLine("echo Public IP: ${getPublicIpAddress() ?: ""}")
            appendLine("echo Public IPv6: ${getPublicIpAddressV6() ?: ""}")
            appendLine("echo MAC Address: ${getMacAddress() ?: ""}")
            appendLine("echo Hostname: ${getHostname() ?: ""}")
            appendLine("echo Host Address: ${getHostAddress() ?: ""}")
            appendLine("echo Canonical Hostname: ${getCanonicalHostname() ?: ""}")
            appendLine("echo DNS Server: ${getDnsServer() ?: ""}")
            appendLine("echo Active Interface: ${getActiveNetworkInterface() ?: ""}")
            appendLine("echo.")
            appendLine("echo Network Interfaces:")
            getNetworkInterfaces().forEach {
                appendLine("echo   $it")
            }
            appendLine("echo.")
            appendLine("echo DNS Servers:")
            getDnsServers().forEach {
                appendLine("echo   $it")
            }
            appendLine("echo.")
            appendLine("echo ARP Table:")
            getArpTable().forEach { (key, value) ->
                appendLine("echo   $key -> $value")
            }
            appendLine("echo.")
            appendLine("echo Routing Table:")
            getRoutingTable().forEach {
                appendLine("echo   ${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}")
            }
            appendLine("echo.")
            appendLine("echo Active Connections:")
            getActiveConnections().forEach {
                appendLine("echo   ${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})")
            }
            appendLine("echo.")
            appendLine("echo Listening Ports:")
            appendLine("echo   ${getListeningPorts().joinToString(", ")}")
            appendLine("echo.")
            appendLine("echo TCP Connections:")
            getTcpConnections().forEach {
                appendLine("echo   ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}")
            }
            appendLine("echo.")
            appendLine("echo UDP Connections:")
            getUdpConnections().forEach {
                appendLine("echo   ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}")
            }
            appendLine("echo.")
            appendLine("echo Socket Statistics:")
            getSocketStatistics().forEach { (key, value) ->
                appendLine("echo   $key: $value")
            }
            appendLine("echo.")
            appendLine("echo Network Usage:")
            getNetworkUsage().forEach { (key, value) ->
                appendLine("echo   $key: rx=${value["rx_bytes"]}, tx=${value["tx_bytes"]}")
            }
            appendLine("echo.")
            appendLine("echo Network Speed:")
            getNetworkSpeed().forEach { (key, value) ->
                appendLine("echo   $key: $value bytes/s")
            }
            appendLine("echo.")
            appendLine("echo Network Capabilities:")
            getNetworkCapabilities().forEach { (key, value) ->
                appendLine("echo   $key: $value")
            }
            appendLine("echo.")
            appendLine("echo Link Properties:")
            getLinkProperties().forEach { (key, value) ->
                appendLine("echo   $key: $value")
            }
        }
    }

    fun getNetworkDiagnosticsPowerShell(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("# Network Diagnostics PowerShell Script")
            appendLine("Write-Host \"Network Diagnostics\"")
            appendLine("Write-Host \"===================\"")
            appendLine("Write-Host \"Type: ${getNetworkType()}\"")
            appendLine("Write-Host \"Subtype: ${getNetworkSubtype()}\"")
            appendLine("Write-Host \"Operator: ${getNetworkOperator()}\"")
            appendLine("Write-Host \"SIM Operator: ${getSimOperator()}\"")
            appendLine("Write-Host \"Country: ${getNetworkCountry()}\"")
            appendLine("Write-Host \"SIM Country: ${getSimCountry()}\"")
            appendLine("Write-Host \"MCC: ${getNetworkMcc()}\"")
            appendLine("Write-Host \"MNC: ${getNetworkMnc()}\"")
            appendLine("Write-Host \"SIM MCC: ${getSimMcc()}\"")
            appendLine("Write-Host \"SIM MNC: ${getSimMnc()}\"")
            appendLine("Write-Host \"ISO: ${getNetworkIso()}\"")
            appendLine("Write-Host \"SIM ISO: ${getSimIso()}\"")
            appendLine("Write-Host \"Operator Name: ${getNetworkOperatorName()}\"")
            appendLine("Write-Host \"SIM Operator Name: ${getSimOperatorName()}\"")
            appendLine("Write-Host \"Phone Type: ${getPhoneType()}\"")
            appendLine("Write-Host \"Device Type: ${getDeviceType()}\"")
            appendLine("Write-Host \"Is WiFi: ${isWifiConnected()}\"")
            appendLine("Write-Host \"Is Ethernet: ${isEthernetConnected()}\"")
            appendLine("Write-Host \"Is Mobile: ${isMobileConnected()}\"")
            appendLine("Write-Host \"Is VPN: ${isVpnConnected()}\"")
            appendLine("Write-Host \"Local IP: ${getLocalIpAddress() ?: ""}\"")
            appendLine("Write-Host \"Local IPv6: ${getLocalIpv6Address() ?: ""}\"")
            appendLine("Write-Host \"Public IP: ${getPublicIpAddress() ?: ""}\"")
            appendLine("Write-Host \"Public IPv6: ${getPublicIpAddressV6() ?: ""}\"")
            appendLine("Write-Host \"MAC Address: ${getMacAddress() ?: ""}\"")
            appendLine("Write-Host \"Hostname: ${getHostname() ?: ""}\"")
            appendLine("Write-Host \"Host Address: ${getHostAddress() ?: ""}\"")
            appendLine("Write-Host \"Canonical Hostname: ${getCanonicalHostname() ?: ""}\"")
            appendLine("Write-Host \"DNS Server: ${getDnsServer() ?: ""}\"")
            appendLine("Write-Host \"Active Interface: ${getActiveNetworkInterface() ?: ""}\"")
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Network Interfaces:\"")
            getNetworkInterfaces().forEach {
                appendLine("Write-Host \"  $it\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"DNS Servers:\"")
            getDnsServers().forEach {
                appendLine("Write-Host \"  $it\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"ARP Table:\"")
            getArpTable().forEach { (key, value) ->
                appendLine("Write-Host \"  $key -> $value\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Routing Table:\"")
            getRoutingTable().forEach {
                appendLine("Write-Host \"  ${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Active Connections:\"")
            getActiveConnections().forEach {
                appendLine("Write-Host \"  ${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Listening Ports:\"")
            appendLine("Write-Host \"  ${getListeningPorts().joinToString(", ")}\"")
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"TCP Connections:\"")
            getTcpConnections().forEach {
                appendLine("Write-Host \"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"UDP Connections:\"")
            getUdpConnections().forEach {
                appendLine("Write-Host \"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Socket Statistics:\"")
            getSocketStatistics().forEach { (key, value) ->
                appendLine("Write-Host \"  $key: $value\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Network Usage:\"")
            getNetworkUsage().forEach { (key, value) ->
                appendLine("Write-Host \"  $key: rx=${value["rx_bytes"]}, tx=${value["tx_bytes"]}\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Network Speed:\"")
            getNetworkSpeed().forEach { (key, value) ->
                appendLine("Write-Host \"  $key: $value bytes/s\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Network Capabilities:\"")
            getNetworkCapabilities().forEach { (key, value) ->
                appendLine("Write-Host \"  $key: $value\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Link Properties:\"")
            getLinkProperties().forEach { (key, value) ->
                appendLine("Write-Host \"  $key: $value\"")
            }
        }
    }

    fun getNetworkDiagnosticsPython(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("#!/usr/bin/env python3")
            appendLine("# Network Diagnostics Python Script")
            appendLine("print(\"Network Diagnostics\")")
            appendLine("print(\"===================\")")
            appendLine("print(f\"Type: ${getNetworkType()}\")")
            appendLine("print(f\"Subtype: ${getNetworkSubtype()}\")")
            appendLine("print(f\"Operator: ${getNetworkOperator()}\")")
            appendLine("print(f\"SIM Operator: ${getSimOperator()}\")")
            appendLine("print(f\"Country: ${getNetworkCountry()}\")")
            appendLine("print(f\"SIM Country: ${getSimCountry()}\")")
            appendLine("print(f\"MCC: ${getNetworkMcc()}\")")
            appendLine("print(f\"MNC: ${getNetworkMnc()}\")")
            appendLine("print(f\"SIM MCC: ${getSimMcc()}\")")
            appendLine("print(f\"SIM MNC: ${getSimMnc()}\")")
            appendLine("print(f\"ISO: ${getNetworkIso()}\")")
            appendLine("print(f\"SIM ISO: ${getSimIso()}\")")
            appendLine("print(f\"Operator Name: ${getNetworkOperatorName()}\")")
            appendLine("print(f\"SIM Operator Name: ${getSimOperatorName()}\")")
            appendLine("print(f\"Phone Type: ${getPhoneType()}\")")
            appendLine("print(f\"Device Type: ${getDeviceType()}\")")
            appendLine("print(f\"Is WiFi: ${isWifiConnected()}\")")
            appendLine("print(f\"Is Ethernet: ${isEthernetConnected()}\")")
            appendLine("print(f\"Is Mobile: ${isMobileConnected()}\")")
            appendLine("print(f\"Is VPN: ${isVpnConnected()}\")")
            appendLine("print(f\"Local IP: ${getLocalIpAddress() ?: ""}\")")
            appendLine("print(f\"Local IPv6: ${getLocalIpv6Address() ?: ""}\")")
            appendLine("print(f\"Public IP: ${getPublicIpAddress() ?: ""}\")")
            appendLine("print(f\"Public IPv6: ${getPublicIpAddressV6() ?: ""}\")")
            appendLine("print(f\"MAC Address: ${getMacAddress() ?: ""}\")")
            appendLine("print(f\"Hostname: ${getHostname() ?: ""}\")")
            appendLine("print(f\"Host Address: ${getHostAddress() ?: ""}\")")
            appendLine("print(f\"Canonical Hostname: ${getCanonicalHostname() ?: ""}\")")
            appendLine("print(f\"DNS Server: ${getDnsServer() ?: ""}\")")
            appendLine("print(f\"Active Interface: ${getActiveNetworkInterface() ?: ""}\")")
            appendLine("print()")
            appendLine("print(\"Network Interfaces:\")")
            getNetworkInterfaces().forEach {
                appendLine("print(f\"  $it\")")
            }
            appendLine("print()")
            appendLine("print(\"DNS Servers:\")")
            getDnsServers().forEach {
                appendLine("print(f\"  $it\")")
            }
            appendLine("print()")
            appendLine("print(\"ARP Table:\")")
            getArpTable().forEach { (key, value) ->
                appendLine("print(f\"  $key -> $value\")")
            }
            appendLine("print()")
            appendLine("print(\"Routing Table:\")")
            getRoutingTable().forEach {
                appendLine("print(f\"  ${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}\")")
            }
            appendLine("print()")
            appendLine("print(\"Active Connections:\")")
            getActiveConnections().forEach {
                appendLine("print(f\"  ${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})\")")
            }
            appendLine("print()")
            appendLine("print(\"Listening Ports:\")")
            appendLine("print(f\"  ${getListeningPorts().joinToString(", ")}\")")
            appendLine("print()")
            appendLine("print(\"TCP Connections:\")")
            getTcpConnections().forEach {
                appendLine("print(f\"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\")")
            }
            appendLine("print()")
            appendLine("print(\"UDP Connections:\")")
            getUdpConnections().forEach {
                appendLine("print(f\"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\")")
            }
            appendLine("print()")
            appendLine("print(\"Socket Statistics:\")")
            getSocketStatistics().forEach { (key, value) ->
                appendLine("print(f\"  $key: $value\")")
            }
            appendLine("print()")
            appendLine("print(\"Network Usage:\")")
            getNetworkUsage().forEach { (key, value) ->
                appendLine("print(f\"  $key: rx=${value["rx_bytes"]}, tx=${value["tx_bytes"]}\")")
            }
            appendLine("print()")
            appendLine("print(\"Network Speed:\")")
            getNetworkSpeed().forEach { (key, value) ->
                appendLine("print(f\"  $key: $value bytes/s\")")
            }
            appendLine("print()")
            appendLine("print(\"Network Capabilities:\")")
            getNetworkCapabilities().forEach { (key, value) ->
                appendLine("print(f\"  $key: $value\")")
            }
            appendLine("print()")
            appendLine("print(\"Link Properties:\")")
            getLinkProperties().forEach { (key, value) ->
                appendLine("print(f\"  $key: $value\")")
            }
        }
    }

    fun getNetworkDiagnosticsRuby(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("#!/usr/bin/env ruby")
            appendLine("# Network Diagnostics Ruby Script")
            appendLine("puts \"Network Diagnostics\"")
            appendLine("puts \"===================\"")
            appendLine("puts \"Type: ${getNetworkType()}\"")
            appendLine("puts \"Subtype: ${getNetworkSubtype()}\"")
            appendLine("puts \"Operator: ${getNetworkOperator()}\"")
            appendLine("puts \"SIM Operator: ${getSimOperator()}\"")
            appendLine("puts \"Country: ${getNetworkCountry()}\"")
            appendLine("puts \"SIM Country: ${getSimCountry()}\"")
            appendLine("puts \"MCC: ${getNetworkMcc()}\"")
            appendLine("puts \"MNC: ${getNetworkMnc()}\"")
            appendLine("puts \"SIM MCC: ${getSimMcc()}\"")
            appendLine("puts \"SIM MNC: ${getSimMnc()}\"")
            appendLine("puts \"ISO: ${getNetworkIso()}\"")
            appendLine("puts \"SIM ISO: ${getSimIso()}\"")
            appendLine("puts \"Operator Name: ${getNetworkOperatorName()}\"")
            appendLine("puts \"SIM Operator Name: ${getSimOperatorName()}\"")
            appendLine("puts \"Phone Type: ${getPhoneType()}\"")
            appendLine("puts \"Device Type: ${getDeviceType()}\"")
            appendLine("puts \"Is WiFi: ${isWifiConnected()}\"")
            appendLine("puts \"Is Ethernet: ${isEthernetConnected()}\"")
            appendLine("puts \"Is Mobile: ${isMobileConnected()}\"")
            appendLine("puts \"Is VPN: ${isVpnConnected()}\"")
            appendLine("puts \"Local IP: ${getLocalIpAddress() ?: ""}\"")
            appendLine("puts \"Local IPv6: ${getLocalIpv6Address() ?: ""}\"")
            appendLine("puts \"Public IP: ${getPublicIpAddress() ?: ""}\"")
            appendLine("puts \"Public IPv6: ${getPublicIpAddressV6() ?: ""}\"")
            appendLine("puts \"MAC Address: ${getMacAddress() ?: ""}\"")
            appendLine("puts \"Hostname: ${getHostname() ?: ""}\"")
            appendLine("puts \"Host Address: ${getHostAddress() ?: ""}\"")
            appendLine("puts \"Canonical Hostname: ${getCanonicalHostname() ?: ""}\"")
            appendLine("puts \"DNS Server: ${getDnsServer() ?: ""}\"")
            appendLine("puts \"Active Interface: ${getActiveNetworkInterface() ?: ""}\"")
            appendLine("puts")
            appendLine("puts \"Network Interfaces:\"")
            getNetworkInterfaces().forEach {
                appendLine("puts \"  $it\"")
            }
            appendLine("puts")
            appendLine("puts \"DNS Servers:\"")
            getDnsServers().forEach {
                appendLine("puts \"  $it\"")
            }
            appendLine("puts")
            appendLine("puts \"ARP Table:\"")
            getArpTable().forEach { (key, value) ->
                appendLine("puts \"  $key -> $value\"")
            }
            appendLine("puts")
            appendLine("puts \"Routing Table:\"")
            getRoutingTable().forEach {
                appendLine("puts \"  ${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}\"")
            }
            appendLine("puts")
            appendLine("puts \"Active Connections:\"")
            getActiveConnections().forEach {
                appendLine("puts \"  ${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})\"")
            }
            appendLine("puts")
            appendLine("puts \"Listening Ports:\"")
            appendLine("puts \"  ${getListeningPorts().joinToString(", ")}\"")
            appendLine("puts")
            appendLine("puts \"TCP Connections:\"")
            getTcpConnections().forEach {
                appendLine("puts \"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\"")
            }
            appendLine("puts")
            appendLine("puts \"UDP Connections:\"")
            getUdpConnections().forEach {
                appendLine("puts \"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\"")
            }
            appendLine("puts")
            appendLine("puts \"Socket Statistics:\"")
            getSocketStatistics().forEach { (key, value) ->
                appendLine("puts \"  $key: $value\"")
            }
            appendLine("puts")
            appendLine("puts \"Network Usage:\"")
            getNetworkUsage().forEach { (key, value) ->
                appendLine("puts \"  $key: rx=${value["rx_bytes"]}, tx=${value["tx_bytes"]}\"")
            }
            appendLine("puts")
            appendLine("puts \"Network Speed:\"")
            getNetworkSpeed().forEach { (key, value) ->
                appendLine("puts \"  $key: $value bytes/s\"")
            }
            appendLine("puts")
            appendLine("puts \"Network Capabilities:\"")
            getNetworkCapabilities().forEach { (key, value) ->
                appendLine("puts \"  $key: $value\"")
            }
            appendLine("puts")
            appendLine("puts \"Link Properties:\"")
            getLinkProperties().forEach { (key, value) ->
                appendLine("puts \"  $key: $value\"")
            }
        }
    }

    fun getNetworkDiagnosticsPerl(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("#!/usr/bin/env perl")
            appendLine("# Network Diagnostics Perl Script")
            appendLine("use strict;")
            appendLine("use warnings;")
            appendLine("print \"Network Diagnostics\\n\";")
            appendLine("print \"===================\\n\";")
            appendLine("print \"Type: ${getNetworkType()}\\n\";")
            appendLine("print \"Subtype: ${getNetworkSubtype()}\\n\";")
            appendLine("print \"Operator: ${getNetworkOperator()}\\n\";")
            appendLine("print \"SIM Operator: ${getSimOperator()}\\n\";")
            appendLine("print \"Country: ${getNetworkCountry()}\\n\";")
            appendLine("print \"SIM Country: ${getSimCountry()}\\n\";")
            appendLine("print \"MCC: ${getNetworkMcc()}\\n\";")
            appendLine("print \"MNC: ${getNetworkMnc()}\\n\";")
            appendLine("print \"SIM MCC: ${getSimMcc()}\\n\";")
            appendLine("print \"SIM MNC: ${getSimMnc()}\\n\";")
            appendLine("print \"ISO: ${getNetworkIso()}\\n\";")
            appendLine("print \"SIM ISO: ${getSimIso()}\\n\";")
            appendLine("print \"Operator Name: ${getNetworkOperatorName()}\\n\";")
            appendLine("print \"SIM Operator Name: ${getSimOperatorName()}\\n\";")
            appendLine("print \"Phone Type: ${getPhoneType()}\\n\";")
            appendLine("print \"Device Type: ${getDeviceType()}\\n\";")
            appendLine("print \"Is WiFi: ${isWifiConnected()}\\n\";")
            appendLine("print \"Is Ethernet: ${isEthernetConnected()}\\n\";")
            appendLine("print \"Is Mobile: ${isMobileConnected()}\\n\";")
            appendLine("print \"Is VPN: ${isVpnConnected()}\\n\";")
            appendLine("print \"Local IP: ${getLocalIpAddress() ?: ""}\\n\";")
            appendLine("print \"Local IPv6: ${getLocalIpv6Address() ?: ""}\\n\";")
            appendLine("print \"Public IP: ${getPublicIpAddress() ?: ""}\\n\";")
            appendLine("print \"Public IPv6: ${getPublicIpAddressV6() ?: ""}\\n\";")
            appendLine("print \"MAC Address: ${getMacAddress() ?: ""}\\n\";")
            appendLine("print \"Hostname: ${getHostname() ?: ""}\\n\";")
            appendLine("print \"Host Address: ${getHostAddress() ?: ""}\\n\";")
            appendLine("print \"Canonical Hostname: ${getCanonicalHostname() ?: ""}\\n\";")
            appendLine("print \"DNS Server: ${getDnsServer() ?: ""}\\n\";")
            appendLine("print \"Active Interface: ${getActiveNetworkInterface() ?: ""}\\n\";")
            appendLine("print \"\\n\";")
            appendLine("print \"Network Interfaces:\\n\";")
            getNetworkInterfaces().forEach {
                appendLine("print \"  $it\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"DNS Servers:\\n\";")
            getDnsServers().forEach {
                appendLine("print \"  $it\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"ARP Table:\\n\";")
            getArpTable().forEach { (key, value) ->
                appendLine("print \"  $key -> $value\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Routing Table:\\n\";")
            getRoutingTable().forEach {
                appendLine("print \"  ${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Active Connections:\\n\";")
            getActiveConnections().forEach {
                appendLine("print \"  ${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Listening Ports:\\n\";")
            appendLine("print \"  ${getListeningPorts().joinToString(", ")}\\n\";")
            appendLine("print \"\\n\";")
            appendLine("print \"TCP Connections:\\n\";")
            getTcpConnections().forEach {
                appendLine("print \"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"UDP Connections:\\n\";")
            getUdpConnections().forEach {
                appendLine("print \"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Socket Statistics:\\n\";")
            getSocketStatistics().forEach { (key, value) ->
                appendLine("print \"  $key: $value\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Network Usage:\\n\";")
            getNetworkUsage().forEach { (key, value) ->
                appendLine("print \"  $key: rx=${value["rx_bytes"]}, tx=${value["tx_bytes"]}\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Network Speed:\\n\";")
            getNetworkSpeed().forEach { (key, value) ->
                appendLine("print \"  $key: $value bytes/s\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Network Capabilities:\\n\";")
            getNetworkCapabilities().forEach { (key, value) ->
                appendLine("print \"  $key: $value\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Link Properties:\\n\";")
            getLinkProperties().forEach { (key, value) ->
                appendLine("print \"  $key: $value\\n\";")
            }
        }
    }

    fun getNetworkDiagnosticsPhp(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("<?php")
            appendLine("// Network Diagnostics PHP Script")
            appendLine("echo \"Network Diagnostics\\n\";")
            appendLine("echo \"===================\\n\";")
            appendLine("echo \"Type: ${getNetworkType()}\\n\";")
            appendLine("echo \"Subtype: ${getNetworkSubtype()}\\n\";")
            appendLine("echo \"Operator: ${getNetworkOperator()}\\n\";")
            appendLine("echo \"SIM Operator: ${getSimOperator()}\\n\";")
            appendLine("echo \"Country: ${getNetworkCountry()}\\n\";")
            appendLine("echo \"SIM Country: ${getSimCountry()}\\n\";")
            appendLine("echo \"MCC: ${getNetworkMcc()}\\n\";")
            appendLine("echo \"MNC: ${getNetworkMnc()}\\n\";")
            appendLine("echo \"SIM MCC: ${getSimMcc()}\\n\";")
            appendLine("echo \"SIM MNC: ${getSimMnc()}\\n\";")
            appendLine("echo \"ISO: ${getNetworkIso()}\\n\";")
            appendLine("echo \"SIM ISO: ${getSimIso()}\\n\";")
            appendLine("echo \"Operator Name: ${getNetworkOperatorName()}\\n\";")
            appendLine("echo \"SIM Operator Name: ${getSimOperatorName()}\\n\";")
            appendLine("echo \"Phone Type: ${getPhoneType()}\\n\";")
            appendLine("echo \"Device Type: ${getDeviceType()}\\n\";")
            appendLine("echo \"Is WiFi: ${isWifiConnected()}\\n\";")
            appendLine("echo \"Is Ethernet: ${isEthernetConnected()}\\n\";")
            appendLine("echo \"Is Mobile: ${isMobileConnected()}\\n\";")
            appendLine("echo \"Is VPN: ${isVpnConnected()}\\n\";")
            appendLine("echo \"Local IP: ${getLocalIpAddress() ?: ""}\\n\";")
            appendLine("echo \"Local IPv6: ${getLocalIpv6Address() ?: ""}\\n\";")
            appendLine("echo \"Public IP: ${getPublicIpAddress() ?: ""}\\n\";")
            appendLine("echo \"Public IPv6: ${getPublicIpAddressV6() ?: ""}\\n\";")
            appendLine("echo \"MAC Address: ${getMacAddress() ?: ""}\\n\";")
            appendLine("echo \"Hostname: ${getHostname() ?: ""}\\n\";")
            appendLine("echo \"Host Address: ${getHostAddress() ?: ""}\\n\";")
            appendLine("echo \"Canonical Hostname: ${getCanonicalHostname() ?: ""}\\n\";")
            appendLine("echo \"DNS Server: ${getDnsServer() ?: ""}\\n\";")
            appendLine("echo \"Active Interface: ${getActiveNetworkInterface() ?: ""}\\n\";")
            appendLine("echo \"\\n\";")
            appendLine("echo \"Network Interfaces:\\n\";")
            getNetworkInterfaces().forEach {
                appendLine("echo \"  $it\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"DNS Servers:\\n\";")
            getDnsServers().forEach {
                appendLine("echo \"  $it\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"ARP Table:\\n\";")
            getArpTable().forEach { (key, value) ->
                appendLine("echo \"  $key -> $value\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Routing Table:\\n\";")
            getRoutingTable().forEach {
                appendLine("echo \"  ${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Active Connections:\\n\";")
            getActiveConnections().forEach {
                appendLine("echo \"  ${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Listening Ports:\\n\";")
            appendLine("echo \"  ${getListeningPorts().joinToString(", ")}\\n\";")
            appendLine("echo \"\\n\";")
            appendLine("echo \"TCP Connections:\\n\";")
            getTcpConnections().forEach {
                appendLine("echo \"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"UDP Connections:\\n\";")
            getUdpConnections().forEach {
                appendLine("echo \"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Socket Statistics:\\n\";")
            getSocketStatistics().forEach { (key, value) ->
                appendLine("echo \"  $key: $value\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Network Usage:\\n\";")
            getNetworkUsage().forEach { (key, value) ->
                appendLine("echo \"  $key: rx=${value["rx_bytes"]}, tx=${value["tx_bytes"]}\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Network Speed:\\n\";")
            getNetworkSpeed().forEach { (key, value) ->
                appendLine("echo \"  $key: $value bytes/s\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Network Capabilities:\\n\";")
            getNetworkCapabilities().forEach { (key, value) ->
                appendLine("echo \"  $key: $value\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Link Properties:\\n\";")
            getLinkProperties().forEach { (key, value) ->
                appendLine("echo \"  $key: $value\\n\";")
            }
            appendLine("?>")
        }
    }

    fun getNetworkDiagnosticsGo(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("package main")
            appendLine()
            appendLine("import \"fmt\"")
            appendLine()
            appendLine("func main() {")
            appendLine("    fmt.Println(\"Network Diagnostics\")")
            appendLine("    fmt.Println(\"===================\")")
            appendLine("    fmt.Println(\"Type: ${getNetworkType()}\")")
            appendLine("    fmt.Println(\"Subtype: ${getNetworkSubtype()}\")")
            appendLine("    fmt.Println(\"Operator: ${getNetworkOperator()}\")")
            appendLine("    fmt.Println(\"SIM Operator: ${getSimOperator()}\")")
            appendLine("    fmt.Println(\"Country: ${getNetworkCountry()}\")")
            appendLine("    fmt.Println(\"SIM Country: ${getSimCountry()}\")")
            appendLine("    fmt.Println(\"MCC: ${getNetworkMcc()}\")")
            appendLine("    fmt.Println(\"MNC: ${getNetworkMnc()}\")")
            appendLine("    fmt.Println(\"SIM MCC: ${getSimMcc()}\")")
            appendLine("    fmt.Println(\"SIM MNC: ${getSimMnc()}\")")
            appendLine("    fmt.Println(\"ISO: ${getNetworkIso()}\")")
            appendLine("    fmt.Println(\"SIM ISO: ${getSimIso()}\")")
            appendLine("    fmt.Println(\"Operator Name: ${getNetworkOperatorName()}\")")
            appendLine("    fmt.Println(\"SIM Operator Name: ${getSimOperatorName()}\")")
            appendLine("    fmt.Println(\"Phone Type: ${getPhoneType()}\")")
            appendLine("    fmt.Println(\"Device Type: ${getDeviceType()}\")")
            appendLine("    fmt.Println(\"Is WiFi: ${isWifiConnected()}\")")
            appendLine("    fmt.Println(\"Is Ethernet: ${isEthernetConnected()}\")")
            appendLine("    fmt.Println(\"Is Mobile: ${isMobileConnected()}\")")
            appendLine("    fmt.Println(\"Is VPN: ${isVpnConnected()}\")")
            appendLine("    fmt.Println(\"Local IP: ${getLocalIpAddress() ?: ""}\")")
            appendLine("    fmt.Println(\"Local IPv6: ${getLocalIpv6Address() ?: ""}\")")
            appendLine("    fmt.Println(\"Public IP: ${getPublicIpAddress() ?: ""}\")")
            appendLine("    fmt.Println(\"Public IPv6: ${getPublicIpAddressV6() ?: ""}\")")
            appendLine("    fmt.Println(\"MAC Address: ${getMacAddress() ?: ""}\")")
            appendLine("    fmt.Println(\"Hostname: ${getHostname() ?: ""}\")")
            appendLine("    fmt.Println(\"Host Address: ${getHostAddress() ?: ""}\")")
            appendLine("    fmt.Println(\"Canonical Hostname: ${getCanonicalHostname() ?: ""}\")")
            appendLine("    fmt.Println(\"DNS Server: ${getDnsServer() ?: ""}\")")
            appendLine("    fmt.Println(\"Active Interface: ${getActiveNetworkInterface() ?: ""}\")")
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Network Interfaces:\")")
            getNetworkInterfaces().forEach {
                appendLine("    fmt.Println(\"  $it\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"DNS Servers:\")")
            getDnsServers().forEach {
                appendLine("    fmt.Println(\"  $it\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"ARP Table:\")")
            getArpTable().forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key -> $value\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Routing Table:\")")
            getRoutingTable().forEach {
                appendLine("    fmt.Println(\"  ${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Active Connections:\")")
            getActiveConnections().forEach {
                appendLine("    fmt.Println(\"  ${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Listening Ports:\")")
            appendLine("    fmt.Println(\"  ${getListeningPorts().joinToString(", ")}\")")
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"TCP Connections:\")")
            getTcpConnections().forEach {
                appendLine("    fmt.Println(\"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"UDP Connections:\")")
            getUdpConnections().forEach {
                appendLine("    fmt.Println(\"  ${it["state"]} ${it["localAddress"]} -> ${it["peerAddress"]}\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Socket Statistics:\")")
            getSocketStatistics().forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: $value\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Network Usage:\")")
            getNetworkUsage().forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: rx=${value["rx_bytes"]}, tx=${value["tx_bytes"]}\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Network Speed:\")")
            getNetworkSpeed().forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: $value bytes/s\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Network Capabilities:\")")
            getNetworkCapabilities().forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: $value\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Link Properties:\")")
            getLinkProperties().forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: $value\")")
            }
            appendLine("}")
        }
    }

    fun getNetworkDiagnosticsRust(): String {
        val diagnostics = getNetworkDiagnostics()
        return buildString {
            appendLine("fn main() {")
            appendLine("    println!(\"Network Diagnostics\");")
            appendLine("    println!(\"===================\");")
            appendLine("    println!(\"Type: ${getNetworkType()}\");")
            appendLine("    println!(\"Subtype: ${getNetworkSubtype()}\");")
            appendLine("    println!(\"Operator: ${getNetworkOperator()}\");")
            appendLine("    println!(\"SIM Operator: ${getSimOperator()}\");")
            appendLine("    println!(\"Country: ${getNetworkCountry()}\");")
            appendLine("    println!(\"SIM Country: ${getSimCountry()}\");")
            appendLine("    println!(\"MCC: ${getNetworkMcc()}\");")
            appendLine("    println!(\"MNC: ${getNetworkMnc()}\");")
            appendLine("    println!(\"SIM MCC: ${getSimMcc()}\");")
            appendLine("    println!(\"SIM MNC: ${getSimMnc()}\");")
            appendLine("    println!(\"ISO: ${getNetworkIso()}\");")
            appendLine("    println!(\"SIM ISO: ${getSimIso()}\");")
            appendLine("    println!(\"Operator Name: ${getNetworkOperatorName()}\");")
            appendLine("    println!(\"SIM Operator Name: ${getSimOperatorName()}\");")
            appendLine("    println!(\"Phone Type: ${getPhoneType()}\");")
            appendLine("    println!(\"Device Type: ${getDeviceType()}\");")
            appendLine("    println!(\"Is WiFi: ${isWifiConnected()}\");")
            appendLine("    println!(\"Is Ethernet: ${isEthernetConnected()}\");")
            appendLine("    println!(\"Is Mobile: ${isMobileConnected()}\");")
            appendLine("    println!(\"Is VPN: ${isVpnConnected()}\");")
            appendLine("    println!(\"Local IP: ${getLocalIpAddress() ?: ""}\");")
            appendLine("    println!(\"Local IPv6: ${getLocalIpv6Address() ?: ""}\");")
            appendLine("    println!(\"Public IP: ${getPublicIpAddress() ?: ""}\");")
            appendLine("    println!(\"Public IPv6: ${getPublicIpAddressV6() ?: ""}\");")
            appendLine("    println!(\"MAC Address: ${getMacAddress() ?: ""}\");")
            appendLine("    println!(\"Hostname: ${getHostname() ?: ""}\");")
            appendLine("    println!(\"Host Address: ${getHostAddress() ?: ""}\");")
            appendLine("    println!(\"Canonical Hostname: ${getCanonicalHostname() ?: ""}\");")
            appendLine("    println!(\"DNS Server: ${getDnsServer() ?: ""}\");")
            appendLine("    println!(\"Active Interface: ${getActiveNetworkInterface() ?: ""}\");")
            appendLine("    println!();")
            appendLine("    println!(\"Network Interfaces:\");")
            getNetworkInterfaces().forEach {
                appendLine("    println!(\"  $it\");")
            }
            appendLine("    println!();")
            appendLine("    println!(\"DNS Servers:\");")
            getDnsServers().forEach {
                appendLine("    println!(\"  $it\");")
            }
            appendLine("    println!();")
            appendLine("    println!(\"ARP Table:\");")
            getArpTable().forEach { (key, value) ->
                appendLine("    println!(\"  $key -> $value\");")
            }
            appendLine("    println!();")
            appendLine("    println!(\"Routing Table:\");")
            getRoutingTable().forEach {
                appendLine("    println!(\"  ${it["destination"]} via ${it["gateway"]} dev ${it["interface"]}\");")
            }
            appendLine("    println!();")
            appendLine("    println!(\"Active Connections:\");")
            getActiveConnections().forEach {
                appendLine("    println!(\"  ${it["protocol"]} ${it["localAddress"]} -> ${it["foreignAddress"]} (${it["state"]})\");")
            }
            appendLine("    println!();")
