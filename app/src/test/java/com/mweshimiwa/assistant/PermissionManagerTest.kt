package com.mweshimiwa.assistant

import android.Manifest
import com.mweshimiwa.assistant.security.PermissionLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionManagerTest {

    @Test
    fun dangerousPermissionsIdentified() {
        assertTrue(PermissionLogic.isDangerous(Manifest.permission.RECORD_AUDIO))
        assertTrue(PermissionLogic.isDangerous(Manifest.permission.CAMERA))
        assertTrue(PermissionLogic.isDangerous(Manifest.permission.ACCESS_FINE_LOCATION))
        assertTrue(PermissionLogic.isDangerous(Manifest.permission.READ_CONTACTS))
        assertFalse(PermissionLogic.isDangerous(Manifest.permission.INTERNET))
        assertFalse(PermissionLogic.isDangerous(Manifest.permission.VIBRATE))
    }

    @Test
    fun missingPermissionsComputed() {
        val required = setOf("a", "b", "c")
        assertEquals(listOf("b", "c"), PermissionLogic.missingPermissions(setOf("a"), required))
        assertEquals(emptyList<String>(), PermissionLogic.missingPermissions(required, required))
        assertEquals(listOf("a", "b", "c"), PermissionLogic.missingPermissions(emptySet(), required))
    }

    @Test
    fun allRequiredGrantedCheck() {
        val required = setOf("a", "b")
        assertTrue(PermissionLogic.allRequiredGranted(setOf("a", "b", "c"), required))
        assertTrue(PermissionLogic.allRequiredGranted(required, required))
        assertFalse(PermissionLogic.allRequiredGranted(setOf("a"), required))
        assertFalse(PermissionLogic.allRequiredGranted(emptySet(), required))
    }

    @Test
    fun rationaleShownForDangerousPermissionPreviouslyGranted() {
        assertTrue(
            PermissionLogic.shouldShowRationale(
                Manifest.permission.RECORD_AUDIO,
                grantedBefore = true,
                permanentlyDenied = false
            )
        )
    }

    @Test
    fun rationaleNotShownWhenNeverAsked() {
        assertFalse(
            PermissionLogic.shouldShowRationale(
                Manifest.permission.RECORD_AUDIO,
                grantedBefore = false,
                permanentlyDenied = false
            )
        )
    }

    @Test
    fun rationaleNotShownWhenPermanentlyDenied() {
        assertFalse(
            PermissionLogic.shouldShowRationale(
                Manifest.permission.CAMERA,
                grantedBefore = true,
                permanentlyDenied = true
            )
        )
    }

    @Test
    fun rationaleNotShownForNormalPermissions() {
        assertFalse(
            PermissionLogic.shouldShowRationale(
                Manifest.permission.INTERNET,
                grantedBefore = true,
                permanentlyDenied = false
            )
        )
    }

    @Test
    fun permissionGroups() {
        assertEquals("microphone", PermissionLogic.permissionGroup(Manifest.permission.RECORD_AUDIO))
        assertEquals("camera", PermissionLogic.permissionGroup(Manifest.permission.CAMERA))
        assertEquals("location", PermissionLogic.permissionGroup(Manifest.permission.ACCESS_FINE_LOCATION))
        assertEquals("location", PermissionLogic.permissionGroup(Manifest.permission.ACCESS_COARSE_LOCATION))
        assertEquals("contacts", PermissionLogic.permissionGroup(Manifest.permission.READ_CONTACTS))
        assertEquals("calendar", PermissionLogic.permissionGroup(Manifest.permission.READ_CALENDAR))
        assertEquals("storage", PermissionLogic.permissionGroup(Manifest.permission.READ_EXTERNAL_STORAGE))
        assertEquals("phone", PermissionLogic.permissionGroup(Manifest.permission.CALL_PHONE))
        assertEquals("sms", PermissionLogic.permissionGroup(Manifest.permission.SEND_SMS))
        assertEquals("notifications", PermissionLogic.permissionGroup(Manifest.permission.POST_NOTIFICATIONS))
        assertEquals("other", PermissionLogic.permissionGroup("com.custom.permission"))
    }

    @Test
    fun dangerousPermissionSetContents() {
        assertTrue(PermissionLogic.DANGEROUS_PERMISSIONS.contains(Manifest.permission.RECORD_AUDIO))
        assertTrue(PermissionLogic.DANGEROUS_PERMISSIONS.contains(Manifest.permission.BODY_SENSORS))
        assertFalse(PermissionLogic.DANGEROUS_PERMISSIONS.contains(Manifest.permission.INTERNET))
    }
}
