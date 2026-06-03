package pl.lab2.subtrack.data

import pl.lab2.subtrack.model.ServicePreset
import pl.lab2.subtrack.model.SubscriptionPlanPreset

object SubscriptionPresetsData {
    val availablePresets = listOf(
        ServicePreset(
            serviceName = "Netflix",
            plans = listOf(
                SubscriptionPlanPreset("Podstawowy", 29.00),
                SubscriptionPlanPreset("Standard", 43.00),
                SubscriptionPlanPreset("Premium 4K", 67.00)
            )
        ),
        ServicePreset(
            serviceName = "Disney+",
            plans = listOf(
                SubscriptionPlanPreset("Standard", 37.99),
                SubscriptionPlanPreset("Premium 4K", 49.99)
            )
        ),
        // Zmiana z HBO Max na Max zgodnie z obecnym brandingiem w Polsce
        ServicePreset(
            serviceName = "Max",
            plans = listOf(
                SubscriptionPlanPreset("Podstawowy", 19.99),
                SubscriptionPlanPreset("Standard", 29.99),
                SubscriptionPlanPreset("Premium", 49.99)
            )
        ),
        ServicePreset(
            serviceName = "SkyShowtime",
            plans = listOf(
                SubscriptionPlanPreset("Standard z reklamami", 19.99),
                SubscriptionPlanPreset("Standard Plus (bez reklam)", 24.99)
            )
        ),
        ServicePreset(
            serviceName = "Amazon Prime Video",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 10.99),
                SubscriptionPlanPreset("Roczny", 49.00, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Player",
            plans = listOf(
                SubscriptionPlanPreset("Z reklamami", 15.00),
                SubscriptionPlanPreset("Bez reklam", 25.00)
            )
        ),
        ServicePreset(
            serviceName = "Polsat Box Go",
            plans = listOf(
                SubscriptionPlanPreset("Premium", 30.00),
                SubscriptionPlanPreset("Start", 30.00, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Canal+",
            plans = listOf(
                SubscriptionPlanPreset("Filmy i Seriale", 29.00),
                SubscriptionPlanPreset("Canal+ Super Sport", 69.00)
            )
        ),
        ServicePreset(
            serviceName = "TVP VOD",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny Plus", 9.99),
                SubscriptionPlanPreset("Roczny Plus", 44.99, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "YouTube Premium",
            plans = listOf(
                SubscriptionPlanPreset("Indywidualny", 25.99),
                SubscriptionPlanPreset("Studencki", 14.99),
                SubscriptionPlanPreset("Rodzinny", 46.99)
            )
        ),
        ServicePreset(
            serviceName = "Apple TV+",
            plans = listOf(
                SubscriptionPlanPreset("Standard", 34.99)
            )
        ),
        ServicePreset(
            serviceName = "Empik Go",
            plans = listOf(
                SubscriptionPlanPreset("Go Lektury", 14.99),
                SubscriptionPlanPreset("Go Standard", 24.99),
                SubscriptionPlanPreset("Go Max", 44.99)
            )
        ),
        ServicePreset(
            serviceName = "Empik Premium",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 12.99),
                SubscriptionPlanPreset("Roczny", 59.99, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Allegro Smart",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 14.99),
                SubscriptionPlanPreset("Roczny", 59.90, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Amazon Prime",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 10.99),
                SubscriptionPlanPreset("Roczny", 49.00, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Glovo Prime",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 14.99)
            )
        ),
        ServicePreset(
            serviceName = "PlayStation Plus",
            plans = listOf(
                SubscriptionPlanPreset("Essential", 37.00),
                SubscriptionPlanPreset("Extra", 58.00),
                SubscriptionPlanPreset("Premium", 70.00)
            )
        ),
        ServicePreset(
            serviceName = "Xbox Game Pass",
            plans = listOf(
                SubscriptionPlanPreset("Core (dawny Gold)", 34.99),
                SubscriptionPlanPreset("Ultimate", 62.99)
            )
        ),
        ServicePreset(
            serviceName = "EA Play",
            plans = listOf(
                SubscriptionPlanPreset("Standard", 24.90),
                SubscriptionPlanPreset("Pro", 85.00)
            )
        ),
        ServicePreset(
            serviceName = "Ubisoft+",
            plans = listOf(
                SubscriptionPlanPreset("Classics", 33.90),
                SubscriptionPlanPreset("Premium", 74.90)
            )
        ),
        ServicePreset(
            serviceName = "Nintendo Switch Online",
            plans = listOf(
                SubscriptionPlanPreset("Indywidualny", 16.00),
                SubscriptionPlanPreset("Indywidualny + Expansion Pack", 170.00, "Rok"),
                SubscriptionPlanPreset("Rodzinny", 120.00, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Adobe Creative Cloud",
            plans = listOf(
                SubscriptionPlanPreset("Plan fotograficzny (20GB)", 53.99),
                SubscriptionPlanPreset("Pojedyncza aplikacja", 114.99),
                SubscriptionPlanPreset("Wszystkie aplikacje", 284.99)
            )
        ),
        ServicePreset(
            serviceName = "ChatGPT",
            plans = listOf(
                SubscriptionPlanPreset("Plus", 85.00)
            )
        ),
        ServicePreset(
            serviceName = "iCloud+",
            plans = listOf(
                SubscriptionPlanPreset("50 GB", 4.99),
                SubscriptionPlanPreset("200 GB", 14.99),
                SubscriptionPlanPreset("2 TB", 49.99)
            )
        ),
        ServicePreset(
            serviceName = "LinkedIn Premium",
            plans = listOf(
                SubscriptionPlanPreset("Career", 137.98),
                SubscriptionPlanPreset("Business", 239.99)
            )
        ),
        ServicePreset(
            serviceName = "Duolingo",
            plans = listOf(
                SubscriptionPlanPreset("Super Duolingo (Indywidualny)", 34.99),
                SubscriptionPlanPreset("Super Duolingo (Rodzinny)", 52.99)
            )
        ),
        ServicePreset(
            serviceName = "Newsweek Polska",
            plans = listOf(
                SubscriptionPlanPreset("Podstawowy", 24.90),
                SubscriptionPlanPreset("Premium (Onet Premium)", 34.90)
            )
        ),
        ServicePreset(
            serviceName = "Onet Premium",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 19.90),
                SubscriptionPlanPreset("Roczny", 199.00, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Polityka Cyfrowa",
            plans = listOf(
                SubscriptionPlanPreset("Standard", 29.00),
                SubscriptionPlanPreset("Premium", 39.00)
            )
        ),
        ServicePreset(
            serviceName = "MultiSport",
            plans = listOf(
                SubscriptionPlanPreset("Plus", 120.00),
                SubscriptionPlanPreset("Light", 80.00)
            )
        ),
        ServicePreset(
            serviceName = "Tinder",
            plans = listOf(
                SubscriptionPlanPreset("Plus", 32.99),
                SubscriptionPlanPreset("Gold", 49.99),
                SubscriptionPlanPreset("Platinum", 64.99)
            )
        ),
        ServicePreset(
            serviceName = "Spotify",
            plans = listOf(
                SubscriptionPlanPreset("Individual", 23.99),
                SubscriptionPlanPreset("Duo", 30.99),
                SubscriptionPlanPreset("Family", 37.99),
                SubscriptionPlanPreset("Student", 12.99)
            )
        ),
        ServicePreset(
            serviceName = "Fitatu",
            plans = listOf(
                SubscriptionPlanPreset("Premium Miesięczny", 24.99),
                SubscriptionPlanPreset("Premium Roczny", 149.99, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Flo",
            plans = listOf(
                SubscriptionPlanPreset("Premium Miesięczny", 19.99),
                SubscriptionPlanPreset("Premium Roczny", 129.99, "Rok")
            )
        )
    )
}