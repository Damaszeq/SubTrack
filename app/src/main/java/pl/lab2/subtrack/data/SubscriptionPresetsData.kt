package pl.lab2.subtrack.data

import pl.lab2.subtrack.R
import pl.lab2.subtrack.model.ServicePreset
import pl.lab2.subtrack.model.SubscriptionPlanPreset

object SubscriptionPresetsData {
    val availablePresets = listOf(
        // --- STREAMING WIDEO (ENTERTAINMENT) ---
        ServicePreset(
            serviceName = "Netflix",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_basic, 29.00),
                SubscriptionPlanPreset(R.string.plan_standard, 43.00),
                SubscriptionPlanPreset(R.string.plan_premium_4k, 67.00)
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Disney+",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_standard, 37.99),
                SubscriptionPlanPreset(R.string.plan_premium_4k, 49.99)
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Max",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_basic, 19.99),
                SubscriptionPlanPreset(R.string.plan_standard, 29.99),
                SubscriptionPlanPreset(R.string.plan_premium, 49.99)
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Amazon Prime Video",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_monthly, 10.99),
                SubscriptionPlanPreset(R.string.plan_yearly, 49.00, "Rok")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "YouTube Premium",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_individual, 25.99),
                SubscriptionPlanPreset(R.string.plan_student, 14.99),
                SubscriptionPlanPreset(R.string.plan_family, 46.99)
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "SkyShowtime",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_standard, 24.99),
                SubscriptionPlanPreset(R.string.plan_premium_4k, 49.99)
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Apple TV+",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_individual, 34.99)
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),

        // --- MUZYKA / AUDIO (ENTERTAINMENT) ---
        ServicePreset(
            serviceName = "Spotify",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_individual, 23.99),
                SubscriptionPlanPreset(R.string.plan_student, 12.99),
                SubscriptionPlanPreset(R.string.plan_family, 37.99)
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Tidal",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_individual, 21.99),
                SubscriptionPlanPreset(R.string.plan_family, 34.99),
                SubscriptionPlanPreset(R.string.plan_student, 9.99)
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Apple Music",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_individual, 21.99),
                SubscriptionPlanPreset(R.string.plan_student, 11.99),
                SubscriptionPlanPreset(R.string.plan_family, 34.99)
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Audioteka",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_individual, 29.90) // Audioteka Klub
            ),
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_education)
        ),

        // --- ZAKUPY (SHOPPING) ---
        ServicePreset(
            serviceName = "Allegro Smart!",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_monthly, 14.99),
                SubscriptionPlanPreset(R.string.plan_yearly, 59.90, "Rok")
            ),
            tagsRes = listOf(R.string.tag_shopping)
        ),
        ServicePreset(
            serviceName = "Empik Premium",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_monthly, 12.99),
                SubscriptionPlanPreset(R.string.plan_yearly, 49.99, "Rok")
            ),
            tagsRes = listOf(R.string.tag_shopping)
        ),

        // --- GAMING (GAMING) ---
        ServicePreset(
            serviceName = "Xbox Game Pass",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_basic, 34.99), // Core
                SubscriptionPlanPreset(R.string.plan_premium, 62.99) // Ultimate
            ),
            tagsRes = listOf(R.string.tag_gaming, R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "PlayStation Plus",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_basic, 37.00),    // Essential
                SubscriptionPlanPreset(R.string.plan_standard, 58.00), // Extra
                SubscriptionPlanPreset(R.string.plan_premium, 70.00)   // Premium
            ),
            tagsRes = listOf(R.string.tag_gaming, R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Nintendo Switch Online",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_individual, 16.00),
                SubscriptionPlanPreset(R.string.plan_family, 140.00, "Rok")
            ),
            tagsRes = listOf(R.string.tag_gaming)
        ),
        ServicePreset(
            serviceName = "GeForce NOW",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_standard, 49.00), // Priority
                SubscriptionPlanPreset(R.string.plan_premium, 99.00)  // Ultimate
            ),
            tagsRes = listOf(R.string.tag_gaming)
        ),

        // --- PRODUKTYWNOŚĆ, NARZĘDZIA I CHMURA (PRODUCTIVITY / WORK) ---
        ServicePreset(
            serviceName = "Google One",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_basic, 8.99), // 100 GB
                SubscriptionPlanPreset(R.string.plan_standard, 13.99), // 200 GB
                SubscriptionPlanPreset(R.string.plan_premium, 46.99) // 2 TB
            ),
            tagsRes = listOf(R.string.tag_productivity)
        ),
        ServicePreset(
            serviceName = "Microsoft 365",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_individual, 29.99), // Personal
                SubscriptionPlanPreset(R.string.plan_family, 42.99)      // Family
            ),
            tagsRes = listOf(R.string.tag_productivity)
        ),
        ServicePreset(
            serviceName = "iCloud+",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_basic, 4.99),   // 50 GB
                SubscriptionPlanPreset(R.string.plan_standard, 14.99), // 200 GB
                SubscriptionPlanPreset(R.string.plan_premium, 49.99)  // 2 TB
            ),
            tagsRes = listOf(R.string.tag_productivity)
        ),
        ServicePreset(
            serviceName = "ChatGPT Plus",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_individual, 85.00) // Ok. $20 USD przekonwertowane orientacyjnie
            ),
            tagsRes = listOf(R.string.tag_productivity, R.string.tag_education)
        ),
        ServicePreset(
            serviceName = "Adobe Creative Cloud",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_student, 95.00),
                SubscriptionPlanPreset(R.string.plan_individual, 280.00)
            ),
            tagsRes = listOf(R.string.tag_productivity)
        ),

        // --- EDUKACJA (EDUCATION) ---
        ServicePreset(
            serviceName = "HelloChinese",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_monthly, 42.99),
                SubscriptionPlanPreset(R.string.plan_yearly, 259.99, "Rok")
            ),
            tagsRes = listOf(R.string.tag_education)
        ),
        ServicePreset(
            serviceName = "Duolingo Plus",
            plans = listOf(
                SubscriptionPlanPreset(R.string.plan_individual, 34.99),
                SubscriptionPlanPreset(R.string.plan_family, 52.99)
            ),
            tagsRes = listOf(R.string.tag_education)
        )
    )
}