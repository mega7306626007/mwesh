package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.ai.model.ChangelogEntry
import com.mweshimiwa.assistant.ai.model.ModelChangelog
import com.mweshimiwa.assistant.ai.model.ModelVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelVersionTest {

    @Test
    fun comparisonByMajorMinorPatch() {
        assertTrue(ModelVersion(2, 0, 0) > ModelVersion(1, 9, 9))
        assertTrue(ModelVersion(1, 2, 0) > ModelVersion(1, 1, 9))
        assertTrue(ModelVersion(1, 1, 2) > ModelVersion(1, 1, 1))
        assertEquals(0, ModelVersion(1, 2, 3).compareTo(ModelVersion(1, 2, 3)))
        assertTrue(ModelVersion(1, 0, 0) < ModelVersion(2, 0, 0))
    }

    @Test
    fun prereleaseOrdering() {
        assertTrue(ModelVersion(1, 0, 0, "alpha") < ModelVersion(1, 0, 0))
        assertTrue(ModelVersion(1, 0, 0, "alpha") < ModelVersion(1, 0, 0, "beta"))
        assertTrue(ModelVersion(1, 0, 0) > ModelVersion(1, 0, 0, "rc1"))
        assertNotEquals(0, ModelVersion(1, 0, 0, "alpha").compareTo(ModelVersion(1, 0, 0, "beta")))
    }

    @Test
    fun parseVersionStrings() {
        assertEquals(ModelVersion(1, 2, 3), ModelVersion("1.2.3"))
        assertEquals(ModelVersion(1, 2, 3), ModelVersion("v1.2.3"))
        assertEquals(ModelVersion(1, 0, 0), ModelVersion("1.0"))
        assertEquals(ModelVersion(1, 0, 0), ModelVersion("1"))
        assertEquals(ModelVersion(0, 0, 0), ModelVersion(""))
        assertEquals(ModelVersion(1, 2, 3), ModelVersion("1.2.3-alpha"))
        assertEquals(null, ModelVersion("1.2.3-alpha").prerelease)
        assertEquals(null, ModelVersion("1.2.3+build5").build)
    }

    @Test
    fun toStringFormats() {
        assertEquals("1.2.3", ModelVersion(1, 2, 3).toString())
        assertEquals("1.2.3-alpha", ModelVersion(1, 2, 3, "alpha").toString())
        assertEquals("1.2.3-alpha+build5", ModelVersion(1, 2, 3, "alpha", "build5").toString())
        assertEquals("1.2.3", ModelVersion(1, 2, 3).toSemverString())
    }

    @Test
    fun negativeComponentsRejected() {
        var thrown = false
        try {
            ModelVersion(-1, 0, 0)
        } catch (e: IllegalArgumentException) {
            thrown = true
        }
        assertTrue(thrown)
    }

    @Test
    fun compatibilityCheck() {
        assertTrue(ModelVersion(2, 1, 0).isCompatibleWith(ModelVersion(2, 5, 3)))
        assertFalse(ModelVersion(2, 1, 0).isCompatibleWith(ModelVersion(3, 0, 0)))
        assertTrue(ModelVersion(1, 0, 0).isCompatibleWith(ModelVersion(1, 9, 9), ModelVersion(1, 0, 0)))
        assertFalse(ModelVersion(0, 9, 0).isCompatibleWith(ModelVersion(1, 0, 0), ModelVersion(1, 0, 0)))
    }

    @Test
    fun orderingHelpers() {
        assertTrue(ModelVersion(2, 0, 0).isNewerThan(ModelVersion(1, 9, 9)))
        assertTrue(ModelVersion(1, 0, 0).isOlderThan(ModelVersion(1, 0, 1)))
        assertTrue(ModelVersion(2, 3, 4).isSameMajor(ModelVersion(2, 9, 9)))
        assertFalse(ModelVersion(2, 3, 4).isSameMajor(ModelVersion(3, 0, 0)))
    }

    @Test
    fun versionBumps() {
        assertEquals(ModelVersion(2, 0, 0), ModelVersion(1, 9, 9).nextMajor())
        assertEquals(ModelVersion(1, 3, 0), ModelVersion(1, 2, 5).nextMinor())
        assertEquals(ModelVersion(1, 2, 4), ModelVersion(1, 2, 3).nextPatch())
    }

    @Test
    fun fromPartsAndConstants() {
        assertEquals(ModelVersion(3, 4, 5), ModelVersion.fromParts(3, 4, 5))
        assertEquals(ModelVersion(0, 0, 0), ModelVersion.ZERO)
        assertTrue(ModelVersion.LATEST > ModelVersion(9999, 0, 0))
        assertEquals(ModelVersion(1, 2, 3), ModelVersion.parse("1.2.3"))
    }

    @Test
    fun changelogQueries() {
        val changelog = ModelChangelog(
            modelId = "mweshimiwa",
            entries = listOf(
                ChangelogEntry(ModelVersion(2, 0, 1), "2024-03-15", listOf("fix"), deprecated = true),
                ChangelogEntry(ModelVersion(2, 0, 0), "2024-03-01", listOf("rewrite"), breaking = true),
                ChangelogEntry(ModelVersion(1, 1, 0), "2024-02-01", listOf("features")),
                ChangelogEntry(ModelVersion(1, 0, 0), "2024-01-01", listOf("initial"))
            )
        )
        assertEquals(ModelVersion(2, 0, 1), changelog.latestVersion())
        assertEquals(3, changelog.entriesSince(ModelVersion(1, 0, 0)).size)
        assertEquals(1, changelog.breakingChangesSince(ModelVersion(1, 0, 0)).size)
        assertTrue(changelog.hasBreakingChangesSince(ModelVersion(1, 0, 0)))
        assertFalse(changelog.hasBreakingChangesSince(ModelVersion(2, 0, 0)))
    }
}
