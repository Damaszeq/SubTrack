package pl.lab2.subtrack.data

import android.content.Context

fun resolvePlanName(planValue: String, context: Context): String {
    val foundPresetPlan = SubscriptionPresetsData.availablePresets
        .flatMap { it.plans }
        .find { it.planKey == planValue }

    return if (foundPresetPlan != null) {
        context.getString(foundPresetPlan.planNameRes)
    } else {
        planValue
    }
}