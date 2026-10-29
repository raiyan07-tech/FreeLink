package com.freelink.app.safety

import android.content.Context
import android.content.Intent
import android.net.Uri

data class EmergencyChecklistItem(
    val title: String,
    val description: String,
    val iconName: String
)

object EmergencyManager {

    const val EMERGENCY_NUMBER = "112"

    /**
     * Builds a safe ACTION_DIAL intent with 112 prefilled.
     * Does NOT place an automatic call or bypass the user's explicit dialler action.
     */
    fun createEmergencyDialIntent(): Intent {
        return Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$EMERGENCY_NUMBER")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Secondary offline emergency checklist based on India travel safety recommendations.
     */
    val emergencyChecklist = listOf(
        EmergencyChecklistItem(
            title = "Move to a Safe Public Place",
            description = "Proceed toward railway station inquiry counters, metro booths, CISF checkpoints, or well-lit commercial shops.",
            iconName = "place"
        ),
        EmergencyChecklistItem(
            title = "Request Assistance from On-Duty Staff",
            description = "Ask station masters, bus conductors, or traffic personnel to place a critical call or hotspot on your behalf.",
            iconName = "support"
        ),
        EmergencyChecklistItem(
            title = "Identify Visible Landmarks",
            description = "Note down the nearest platform number, highway milestone, intersection board, or shop signboard.",
            iconName = "landmark"
        ),
        EmergencyChecklistItem(
            title = "Preserve Handset Battery",
            description = "Turn on Extreme Battery Saver, lower screen brightness, and disable unnecessary radio scanning when not in use.",
            iconName = "battery"
        ),
        EmergencyChecklistItem(
            title = "Use Phone Hardware SOS",
            description = "Most modern Android phones trigger Emergency SOS when pressing the Power button 5 times rapidly.",
            iconName = "power"
        )
    )

    const val EMERGENCY_LEGAL_DISCLAIMER =
        "LEGAL & TECHNICAL NOTICE: FREELINK is not an emergency response service or telecom carrier. " +
        "Tapping the emergency button opens the native Android phone dialler with 112 prefilled. " +
        "Emergency calls in India (ERSS 112) are carried over mobile cellular networks and may connect even without active data or prepaid validity, " +
        "subject to handset radio hardware and tower coverage. FREELINK does not proxy, record, or guarantee call routing."
}
