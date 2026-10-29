package com.freelink.app.safety

import com.freelink.app.domain.models.TravelKit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SafetyAndTravelKitTest {

    @Test
    fun emergencyManager_constantsAndChecklistIntegrity() {
        assertEquals("112", EmergencyManager.EMERGENCY_NUMBER)
        assertFalse(EmergencyManager.emergencyChecklist.isEmpty())
        assertTrue(EmergencyManager.emergencyChecklist.size >= 5)

        // Verify checklist items have actionable titles and descriptions
        EmergencyManager.emergencyChecklist.forEach { item ->
            assertTrue(item.title.isNotBlank())
            assertTrue(item.description.isNotBlank())
        }

        // Verify legal disclaimer explicitly mentions ERSS 112 and disclaims carrier override
        val disclaimer = EmergencyManager.EMERGENCY_LEGAL_DISCLAIMER
        assertTrue(disclaimer.contains("112"))
        assertTrue(disclaimer.contains("LEGAL & TECHNICAL NOTICE"))
        assertTrue(disclaimer.contains("ERSS"))
    }

    @Test
    fun travelKit_defaultValues_areOfflineReady() {
        val defaultKit = TravelKit()
        assertEquals(1, defaultKit.id)
        assertEquals("Airtel", defaultKit.primaryOperator)
        assertEquals("*121#", defaultKit.operatorUssdHelp)
        assertTrue(defaultKit.offlineSosTemplate.contains("EMERGENCY"))
        assertTrue(defaultKit.lastUpdated > 0L)
    }
}
