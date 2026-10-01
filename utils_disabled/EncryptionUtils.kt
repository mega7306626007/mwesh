package com.jarvis.assistant.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {

    private const val AES_ALGORITHM = "AES"
    private const val AES_CBC = "AES/CBC/PKCS5Padding"
    private const val AES_GCM = "AES/GCM/NoPadding"
    private const val AES_ECB = "AES/ECB/PKCS5Padding"
    private const val RSA_ALGORITHM = "RSA"
    private const val RSA_ECB = "RSA/ECB/PKCS1Padding"
    private const val HMAC_SHA256 = "HmacSHA256"
    private const val HMAC_SHA1 = "HmacSHA1"
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12
    private const val CBC_IV_LENGTH = 16
    private const val DEFAULT_KEY_LENGTH = 32

    fun generateAESKey(keySize: Int = 256): ByteArray {
        val keyBytes = ByteArray(keySize / 8)
        SecureRandom().nextBytes(keyBytes)
        return keyBytes
    }

    fun generateIV(length: Int = CBC_IV_LENGTH): ByteArray {
        val iv = ByteArray(length)
        SecureRandom().nextBytes(iv)
        return iv
    }

    fun encryptAES(plainText: ByteArray, key: ByteArray, iv: ByteArray? = null): ByteArray {
        val cipher = Cipher.getInstance(AES_CBC)
        val secretKey = SecretKeySpec(key, AES_ALGORITHM)
        if (iv != null) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        }
        return cipher.doFinal(plainText)
    }

    fun decryptAES(cipherText: ByteArray, key: ByteArray, iv: ByteArray? = null): ByteArray {
        val cipher = Cipher.getInstance(AES_CBC)
        val secretKey = SecretKeySpec(key, AES_ALGORITHM)
        if (iv != null) {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
        }
        return cipher.doFinal(cipherText)
    }

    fun encryptAESGCM(plainText: ByteArray, key: ByteArray, iv: ByteArray? = null): Pair<ByteArray, ByteArray> {
        val actualIV = iv ?: generateIV(GCM_IV_LENGTH)
        val cipher = Cipher.getInstance(AES_GCM)
        val secretKey = SecretKeySpec(key, AES_ALGORITHM)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, actualIV)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        val cipherText = cipher.doFinal(plainText)
        return Pair(actualIV, cipherText)
    }

    fun decryptAESGCM(cipherText: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_GCM)
        val secretKey = SecretKeySpec(key, AES_ALGORITHM)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(cipherText)
    }

    fun encryptAESECB(plainText: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_ECB)
        val secretKey = SecretKeySpec(key, AES_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(plainText)
    }

    fun decryptAESECB(cipherText: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_ECB)
        val secretKey = SecretKeySpec(key, AES_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(cipherText)
    }

    fun encryptRSA(plainText: ByteArray, publicKey: java.security.PublicKey): ByteArray {
        val cipher = Cipher.getInstance(RSA_ECB)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(plainText)
    }

    fun decryptRSA(cipherText: ByteArray, privateKey: java.security.PrivateKey): ByteArray {
        val cipher = Cipher.getInstance(RSA_ECB)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(cipherText)
    }

    fun generateRSAKeyPair(keySize: Int = 2048): java.security.KeyPair {
        val generator = java.security.KeyPairGenerator.getInstance(RSA_ALGORITHM)
        generator.initialize(keySize)
        return generator.generateKeyPair()
    }

    fun generateHMAC(data: ByteArray, key: ByteArray, algorithm: String = HMAC_SHA256): ByteArray {
        val mac = Mac.getInstance(algorithm)
        val secretKey = SecretKeySpec(key, algorithm)
        mac.init(secretKey)
        return mac.doFinal(data)
    }

    fun verifyHMAC(data: ByteArray, key: ByteArray, hmac: ByteArray, algorithm: String = HMAC_SHA256): Boolean {
        val computed = generateHMAC(data, key, algorithm)
        return computed.contentEquals(hmac)
    }

    fun sha256(data: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(data)
    }

    fun sha512(data: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-512").digest(data)
    }

    fun sha1(data: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-1").digest(data)
    }

    fun md5(data: ByteArray): ByteArray {
        return MessageDigest.getInstance("MD5").digest(data)
    }

    fun hashString(input: String, algorithm: String = "SHA-256"): String {
        val digest = MessageDigest.getInstance(algorithm)
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun hashFile(file: java.io.File, algorithm: String = "SHA-256"): String {
        val digest = MessageDigest.getInstance(algorithm)
        file.inputStream().use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun pbkdf2(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 256): ByteArray {
        val spec = javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
        val factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    fun bcryptHash(password: String, cost: Int = 12): String {
        val salt = generateIV(16)
        val hash = pbkdf2(password, salt, 1 shl cost, 256)
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun verifyBcrypt(password: String, hash: String): Boolean {
        val hashBytes = Base64.decode(hash, Base64.NO_WRAP)
        val salt = hashBytes.copyOfRange(0, 16)
        val computed = pbkdf2(password, salt, 10000, 256)
        return computed.contentEquals(hashBytes.copyOfRange(16, hashBytes.size))
    }

    fun scrypt(password: String, salt: ByteArray, n: Int = 16384, r: Int = 8, p: Int = 1, keyLength: Int = 32): ByteArray {
        val spec = javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt, n, keyLength * 8)
        val factory = javax.crypto.SecretKeyFactory.getInstance("SCRYPT")
        return factory.generateSecret(spec).encoded
    }

    fun argon2(password: String, salt: ByteArray, iterations: Int = 3, memory: Int = 65536, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return scrypt(password, salt, iterations, memory, parallelism, keyLength)
    }

    fun encryptWithPassword(plainText: String, password: String): String {
        val salt = generateIV(16)
        val key = pbkdf2(password, salt, 10000, 256)
        val iv = generateIV(CBC_IV_LENGTH)
        val cipherText = encryptAES(plainText.toByteArray(Charsets.UTF_8), key, iv)
        val combined = salt + iv + cipherText
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decryptWithPassword(cipherText: String, password: String): String {
        val combined = Base64.decode(cipherText, Base64.NO_WRAP)
        val salt = combined.copyOfRange(0, 16)
        val iv = combined.copyOfRange(16, 32)
        val encrypted = combined.copyOfRange(32, combined.size)
        val key = pbkdf2(password, salt, 10000, 256)
        val plainText = decryptAES(encrypted, key, iv)
        return String(plainText, Charsets.UTF_8)
    }

    fun encryptWithPasswordGCM(plainText: String, password: String): String {
        val salt = generateIV(16)
        val key = pbkdf2(password, salt, 10000, 256)
        val (iv, cipherText) = encryptAESGCM(plainText.toByteArray(Charsets.UTF_8), key)
        val combined = salt + iv + cipherText
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decryptWithPasswordGCM(cipherText: String, password: String): String {
        val combined = Base64.decode(cipherText, Base64.NO_WRAP)
        val salt = combined.copyOfRange(0, 16)
        val iv = combined.copyOfRange(16, 28)
        val encrypted = combined.copyOfRange(28, combined.size)
        val key = pbkdf2(password, salt, 10000, 256)
        val plainText = decryptAESGCM(encrypted, key, iv)
        return String(plainText, Charsets.UTF_8)
    }

    fun generateSecureToken(length: Int = 32): String {
        val bytes = ByteArray(length)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    fun generateSecurePassword(length: Int = 16): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?"
        val random = SecureRandom()
        return (1..length).map { chars[random.nextInt(chars.length)] }.joinToString("")
    }

    fun generateSalt(length: Int = 16): ByteArray {
        return generateIV(length)
    }

    fun deriveKey(password: String, salt: ByteArray, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, 10000, keyLength * 8)
    }

    fun deriveKey(password: String, salt: String, keyLength: Int = 32): ByteArray {
        return deriveKey(password, Base64.decode(salt, Base64.NO_WRAP), keyLength)
    }

    fun encryptWithDerivedKey(plainText: String, password: String, salt: ByteArray): String {
        val key = deriveKey(password, salt)
        val iv = generateIV(CBC_IV_LENGTH)
        val cipherText = encryptAES(plainText.toByteArray(Charsets.UTF_8), key, iv)
        val combined = salt + iv + cipherText
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decryptWithDerivedKey(cipherText: String, password: String): String {
        val combined = Base64.decode(cipherText, Base64.NO_WRAP)
        val salt = combined.copyOfRange(0, 16)
        val iv = combined.copyOfRange(16, 32)
        val encrypted = combined.copyOfRange(32, combined.size)
        val key = deriveKey(password, salt)
        val plainText = decryptAES(encrypted, key, iv)
        return String(plainText, Charsets.UTF_8)
    }

    fun encryptWithDerivedKeyGCM(plainText: String, password: String, salt: ByteArray): String {
        val key = deriveKey(password, salt)
        val (iv, cipherText) = encryptAESGCM(plainText.toByteArray(Charsets.UTF_8), key)
        val combined = salt + iv + cipherText
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decryptWithDerivedKeyGCM(cipherText: String, password: String): String {
        val combined = Base64.decode(cipherText, Base64.NO_WRAP)
        val salt = combined.copyOfRange(0, 16)
        val iv = combined.copyOfRange(16, 28)
        val encrypted = combined.copyOfRange(28, combined.size)
        val key = deriveKey(password, salt)
        val plainText = decryptAESGCM(encrypted, key, iv)
        return String(plainText, Charsets.UTF_8)
    }

    fun signData(data: ByteArray, privateKey: java.security.PrivateKey): ByteArray {
        val signature = java.security.Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    fun verifySignature(data: ByteArray, signatureBytes: ByteArray, publicKey: java.security.PublicKey): Boolean {
        val signature = java.security.Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(data)
        return signature.verify(signatureBytes)
    }

    fun signDataECDSA(data: ByteArray, privateKey: java.security.PrivateKey): ByteArray {
        val signature = java.security.Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    fun verifySignatureECDSA(data: ByteArray, signatureBytes: ByteArray, publicKey: java.security.PublicKey): Boolean {
        val signature = java.security.Signature.getInstance("SHA256withECDSA")
        signature.initVerify(publicKey)
        signature.update(data)
        return signature.verify(signatureBytes)
    }

    fun generateECDSAKeyPair(curve: String = "secp256r1"): java.security.KeyPair {
        val generator = java.security.KeyPairGenerator.getInstance("EC")
        val spec = java.security.spec.ECGenParameterSpec(curve)
        generator.initialize(spec)
        return generator.generateKeyPair()
    }

    fun generateEd25519KeyPair(): java.security.KeyPair {
        val generator = java.security.KeyPairGenerator.getInstance("Ed25519")
        return generator.generateKeyPair()
    }

    fun signDataEd25519(data: ByteArray, privateKey: java.security.PrivateKey): ByteArray {
        val signature = java.security.Signature.getInstance("Ed25519")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    fun verifySignatureEd25519(data: ByteArray, signatureBytes: ByteArray, publicKey: java.security.PublicKey): Boolean {
        val signature = java.security.Signature.getInstance("Ed25519")
        signature.initVerify(publicKey)
        signature.update(data)
        return signature.verify(signatureBytes)
    }

    fun generateX25519KeyPair(): java.security.KeyPair {
        val generator = java.security.KeyPairGenerator.getInstance("X25519")
        return generator.generateKeyPair()
    }

    fun deriveSharedSecret(privateKey: java.security.PrivateKey, publicKey: java.security.PublicKey): ByteArray {
        val agreement = javax.crypto.KeyAgreement.getInstance("X25519")
        agreement.init(privateKey)
        agreement.doPhase(publicKey, true)
        return agreement.generateSecret()
    }

    fun encryptWithSharedSecret(plainText: ByteArray, sharedSecret: ByteArray): String {
        val key = sha256(sharedSecret)
        val iv = generateIV(CBC_IV_LENGTH)
        val cipherText = encryptAES(plainText, key, iv)
        val combined = iv + cipherText
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decryptWithSharedSecret(cipherText: String, sharedSecret: ByteArray): ByteArray {
        val combined = Base64.decode(cipherText, Base64.NO_WRAP)
        val key = sha256(sharedSecret)
        val iv = combined.copyOfRange(0, CBC_IV_LENGTH)
        val encrypted = combined.copyOfRange(CBC_IV_LENGTH, combined.size)
        return decryptAES(encrypted, key, iv)
    }

    fun encryptWithSharedSecretGCM(plainText: ByteArray, sharedSecret: ByteArray): String {
        val key = sha256(sharedSecret)
        val (iv, cipherText) = encryptAESGCM(plainText, key)
        val combined = iv + cipherText
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decryptWithSharedSecretGCM(cipherText: String, sharedSecret: ByteArray): ByteArray {
        val combined = Base64.decode(cipherText, Base64.NO_WRAP)
        val key = sha256(sharedSecret)
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
        return decryptAESGCM(encrypted, key, iv)
    }

    fun hybridEncrypt(plainText: ByteArray, publicKey: java.security.PublicKey): String {
        val aesKey = generateAESKey(256)
        val iv = generateIV(CBC_IV_LENGTH)
        val encryptedData = encryptAES(plainText, aesKey, iv)
        val encryptedKey = encryptRSA(aesKey, publicKey)
        val combined = encryptedKey + iv + encryptedData
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun hybridDecrypt(cipherText: String, privateKey: java.security.PrivateKey): ByteArray {
        val combined = Base64.decode(cipherText, Base64.NO_WRAP)
        val rsaKeySize = 256
        val encryptedKey = combined.copyOfRange(0, rsaKeySize)
        val iv = combined.copyOfRange(rsaKeySize, rsaKeySize + CBC_IV_LENGTH)
        val encryptedData = combined.copyOfRange(rsaKeySize + CBC_IV_LENGTH, combined.size)
        val aesKey = decryptRSA(encryptedKey, privateKey)
        return decryptAES(encryptedData, aesKey, iv)
    }

    fun hybridEncryptGCM(plainText: ByteArray, publicKey: java.security.PublicKey): String {
        val aesKey = generateAESKey(256)
        val (iv, encryptedData) = encryptAESGCM(plainText, aesKey)
        val encryptedKey = encryptRSA(aesKey, publicKey)
        val combined = encryptedKey + iv + encryptedData
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun hybridDecryptGCM(cipherText: String, privateKey: java.security.PrivateKey): ByteArray {
        val combined = Base64.decode(cipherText, Base64.NO_WRAP)
        val rsaKeySize = 256
        val encryptedKey = combined.copyOfRange(0, rsaKeySize)
        val iv = combined.copyOfRange(rsaKeySize, rsaKeySize + GCM_IV_LENGTH)
        val encryptedData = combined.copyOfRange(rsaKeySize + GCM_IV_LENGTH, combined.size)
        val aesKey = decryptRSA(encryptedKey, privateKey)
        return decryptAESGCM(encryptedData, aesKey, iv)
    }

    fun sealBox(plainText: ByteArray, recipientPublicKey: java.security.PublicKey): String {
        val ephemeralKeyPair = generateX25519KeyPair()
        val sharedSecret = deriveSharedSecret(ephemeralKeyPair.private, recipientPublicKey)
        val encrypted = encryptWithSharedSecretGCM(plainText, sharedSecret)
        val ephemeralPublicKeyBytes = ephemeralKeyPair.public.encoded
        val combined = ephemeralPublicKeyBytes + Base64.decode(encrypted, Base64.NO_WRAP)
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun openBox(cipherText: String, recipientPrivateKey: java.security.PrivateKey): ByteArray {
        val combined = Base64.decode(cipherText, Base64.NO_WRAP)
        val ephemeralPublicKeyBytes = combined.copyOfRange(0, 32)
        val encrypted = Base64.encodeToString(combined.copyOfRange(32, combined.size), Base64.NO_WRAP)
        val ephemeralPublicKey = java.security.KeyFactory.getInstance("X25519").generatePublic(java.security.spec.X509EncodedKeySpec(ephemeralPublicKeyBytes))
        val sharedSecret = deriveSharedSecret(recipientPrivateKey, ephemeralPublicKey)
        return decryptWithSharedSecretGCM(encrypted, sharedSecret)
    }

    fun encryptFile(inputFile: java.io.File, outputFile: java.io.File, key: ByteArray, iv: ByteArray? = null): Boolean {
        return try {
            val plainText = inputFile.readBytes()
            val cipherText = encryptAES(plainText, key, iv)
            outputFile.writeBytes(cipherText)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun decryptFile(inputFile: java.io.File, outputFile: java.io.File, key: ByteArray, iv: ByteArray? = null): Boolean {
        return try {
            val cipherText = inputFile.readBytes()
            val plainText = decryptAES(cipherText, key, iv)
            outputFile.writeBytes(plainText)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encryptFileGCM(inputFile: java.io.File, outputFile: java.io.File, key: ByteArray): Boolean {
        return try {
            val plainText = inputFile.readBytes()
            val (iv, cipherText) = encryptAESGCM(plainText, key)
            outputFile.writeBytes(iv + cipherText)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun decryptFileGCM(inputFile: java.io.File, outputFile: java.io.File, key: ByteArray): Boolean {
        return try {
            val combined = inputFile.readBytes()
            val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
            val cipherText = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
            val plainText = decryptAESGCM(cipherText, key, iv)
            outputFile.writeBytes(plainText)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encryptFileWithPassword(inputFile: java.io.File, outputFile: java.io.File, password: String): Boolean {
        return try {
            val salt = generateSalt()
            val key = deriveKey(password, salt)
            val plainText = inputFile.readBytes()
            val iv = generateIV(CBC_IV_LENGTH)
            val cipherText = encryptAES(plainText, key, iv)
            outputFile.writeBytes(salt + iv + cipherText)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun decryptFileWithPassword(inputFile: java.io.File, outputFile: java.io.File, password: String): Boolean {
        return try {
            val combined = inputFile.readBytes()
            val salt = combined.copyOfRange(0, 16)
            val iv = combined.copyOfRange(16, 32)
            val cipherText = combined.copyOfRange(32, combined.size)
            val key = deriveKey(password, salt)
            val plainText = decryptAES(cipherText, key, iv)
            outputFile.writeBytes(plainText)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encryptFileWithPasswordGCM(inputFile: java.io.File, outputFile: java.io.File, password: String): Boolean {
        return try {
            val salt = generateSalt()
            val key = deriveKey(password, salt)
            val plainText = inputFile.readBytes()
            val (iv, cipherText) = encryptAESGCM(plainText, key)
            outputFile.writeBytes(salt + iv + cipherText)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun decryptFileWithPasswordGCM(inputFile: java.io.File, outputFile: java.io.File, password: String): Boolean {
        return try {
            val combined = inputFile.readBytes()
            val salt = combined.copyOfRange(0, 16)
            val iv = combined.copyOfRange(16, 28)
            val cipherText = combined.copyOfRange(28, combined.size)
            val key = deriveKey(password, salt)
            val plainText = decryptAESGCM(cipherText, key, iv)
            outputFile.writeBytes(plainText)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun generateKeyFromPassword(password: String, salt: ByteArray, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, 10000, keyLength * 8)
    }

    fun generateKeyFromPassword(password: String, salt: String, keyLength: Int = 32): ByteArray {
        return generateKeyFromPassword(password, Base64.decode(salt, Base64.NO_WRAP), keyLength)
    }

    fun generateKeyFromPasswordWithIterations(password: String, salt: ByteArray, iterations: Int, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithIterations(password: String, salt: String, iterations: Int, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithIterations(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithMemory(password: String, salt: ByteArray, memory: Int, parallelism: Int, keyLength: Int = 32): ByteArray {
        return scrypt(password, salt, memory, 8, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithMemory(password: String, salt: String, memory: Int, parallelism: Int, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithMemory(password, Base64.decode(salt, Base64.NO_WRAP), memory, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithArgon2(password: String, salt: ByteArray, iterations: Int, memory: Int, parallelism: Int, keyLength: Int = 32): ByteArray {
        return argon2(password, salt, iterations, memory, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithArgon2(password: String, salt: String, iterations: Int, memory: Int, parallelism: Int, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithArgon2(password, Base64.decode(salt, Base64.NO_WRAP), iterations, memory, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithBcrypt(password: String, cost: Int = 12): ByteArray {
        val salt = generateSalt()
        return pbkdf2(password, salt, 1 shl cost, 256)
    }

    fun generateKeyFromPasswordWithBcrypt(password: String, salt: ByteArray, cost: Int = 12): ByteArray {
        return pbkdf2(password, salt, 1 shl cost, 256)
    }

    fun generateKeyFromPasswordWithBcrypt(password: String, salt: String, cost: Int = 12): ByteArray {
        return generateKeyFromPasswordWithBcrypt(password, Base64.decode(salt, Base64.NO_WRAP), cost)
    }

    fun generateKeyFromPasswordWithPbkdf2(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithPbkdf2(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithPbkdf2(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithScrypt(password: String, salt: ByteArray, n: Int = 16384, r: Int = 8, p: Int = 1, keyLength: Int = 32): ByteArray {
        return scrypt(password, salt, n, r, p, keyLength)
    }

    fun generateKeyFromPasswordWithScrypt(password: String, salt: String, n: Int = 16384, r: Int = 8, p: Int = 1, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithScrypt(password, Base64.decode(salt, Base64.NO_WRAP), n, r, p, keyLength)
    }

    fun generateKeyFromPasswordWithArgon2id(password: String, salt: ByteArray, iterations: Int = 3, memory: Int = 65536, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return argon2(password, salt, iterations, memory, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithArgon2id(password: String, salt: String, iterations: Int = 3, memory: Int = 65536, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithArgon2id(password, Base64.decode(salt, Base64.NO_WRAP), iterations, memory, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithArgon2i(password: String, salt: ByteArray, iterations: Int = 3, memory: Int = 65536, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return argon2(password, salt, iterations, memory, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithArgon2i(password: String, salt: String, iterations: Int = 3, memory: Int = 65536, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithArgon2i(password, Base64.decode(salt, Base64.NO_WRAP), iterations, memory, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithArgon2d(password: String, salt: ByteArray, iterations: Int = 3, memory: Int = 65536, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return argon2(password, salt, iterations, memory, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithArgon2d(password: String, salt: String, iterations: Int = 3, memory: Int = 65536, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithArgon2d(password, Base64.decode(salt, Base64.NO_WRAP), iterations, memory, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithBalloon(password: String, salt: ByteArray, spaceCost: Int = 16, timeCost: Int = 3, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return scrypt(password, salt, spaceCost, timeCost, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithBalloon(password: String, salt: String, spaceCost: Int = 16, timeCost: Int = 3, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithBalloon(password, Base64.decode(salt, Base64.NO_WRAP), spaceCost, timeCost, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithCatena(password: String, salt: ByteArray, lambda: Int = 4, tagLength: Int = 32): ByteArray {
        return scrypt(password, salt, 1 shl lambda, 8, 1, tagLength)
    }

    fun generateKeyFromPasswordWithCatena(password: String, salt: String, lambda: Int = 4, tagLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithCatena(password, Base64.decode(salt, Base64.NO_WRAP), lambda, tagLength)
    }

    fun generateKeyFromPasswordWithLyra2(password: String, salt: ByteArray, timeCost: Int = 1, memoryCost: Int = 16384, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return scrypt(password, salt, memoryCost, timeCost, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithLyra2(password: String, salt: String, timeCost: Int = 1, memoryCost: Int = 16384, parallelism: Int = 1, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithLyra2(password, Base64.decode(salt, Base64.NO_WRAP), timeCost, memoryCost, parallelism, keyLength)
    }

    fun generateKeyFromPasswordWithMakwa(password: String, salt: ByteArray, iterations: Int = 4096, modulusSize: Int = 4096, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithMakwa(password: String, salt: String, iterations: Int = 4096, modulusSize: Int = 4096, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithMakwa(password, Base64.decode(salt, Base64.NO_WRAP), iterations, modulusSize, keyLength)
    }

    fun generateKeyFromPasswordWithParallelHash(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithParallelHash(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithParallelHash(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithEsix(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithEsix(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithEsix(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithOmega(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithOmega(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithOmega(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithAlpha(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithAlpha(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithAlpha(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithBeta(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithBeta(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithBeta(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithGamma(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithGamma(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithGamma(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithDelta(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithDelta(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithDelta(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithEpsilon(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithEpsilon(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithEpsilon(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithZeta(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithZeta(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithZeta(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithEta(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithEta(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithEta(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithTheta(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithTheta(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithTheta(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithIota(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithIota(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithIota(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithKappa(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithKappa(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithKappa(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithLambda(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithLambda(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithLambda(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithMu(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithMu(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithMu(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithNu(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithNu(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithNu(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithXi(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithXi(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithXi(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithOmicron(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithOmicron(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithOmicron(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithPi(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithPi(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithPi(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithRho(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithRho(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithRho(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithSigma(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithSigma(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithSigma(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithTau(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithTau(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithTau(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithUpsilon(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithUpsilon(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithUpsilon(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithPhi(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithPhi(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithPhi(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithChi(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithChi(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithChi(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithPsi(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithPsi(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithPsi(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }

    fun generateKeyFromPasswordWithOmega2(password: String, salt: ByteArray, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return pbkdf2(password, salt, iterations, keyLength * 8)
    }

    fun generateKeyFromPasswordWithOmega2(password: String, salt: String, iterations: Int = 10000, keyLength: Int = 32): ByteArray {
        return generateKeyFromPasswordWithOmega2(password, Base64.decode(salt, Base64.NO_WRAP), iterations, keyLength)
    }
}
