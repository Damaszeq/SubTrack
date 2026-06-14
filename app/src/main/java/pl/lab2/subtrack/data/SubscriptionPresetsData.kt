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
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Disney+",
            plans = listOf(
                SubscriptionPlanPreset("Standard", 37.99),
                SubscriptionPlanPreset("Premium 4K", 49.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Max",
            plans = listOf(
                SubscriptionPlanPreset("Podstawowy", 19.99),
                SubscriptionPlanPreset("Standard", 29.99),
                SubscriptionPlanPreset("Premium", 49.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "SkyShowtime",
            plans = listOf(
                SubscriptionPlanPreset("Standard z reklamami", 19.99),
                SubscriptionPlanPreset("Standard Plus (bez reklam)", 24.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Amazon Prime Video",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 10.99),
                SubscriptionPlanPreset("Roczny", 49.00, "Rok")
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Player",
            plans = listOf(
                SubscriptionPlanPreset("Z reklamami", 15.00),
                SubscriptionPlanPreset("Bez reklam", 25.00)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Polsat Box Go",
            plans = listOf(
                SubscriptionPlanPreset("Start", 30.00, "Rok"),
                SubscriptionPlanPreset("Premium", 30.00)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Canal+ Online",
            plans = listOf(
                SubscriptionPlanPreset("Filmy i Seriale", 29.00),
                SubscriptionPlanPreset("Canal+ Super Sport", 69.00)
            ),
            tags = listOf("Rozrywka", "Sport i Zdrowie")
        ),
        ServicePreset(
            serviceName = "TVP VOD",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny Plus", 9.99),
                SubscriptionPlanPreset("Roczny Plus", 44.99, "Rok")
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Viaplay",
            plans = listOf(
                SubscriptionPlanPreset("Medium", 40.00),
                SubscriptionPlanPreset("Total", 55.00)
            ),
            tags = listOf("Rozrywka", "Sport i Zdrowie")
        ),
        ServicePreset(
            serviceName = "YouTube Premium",
            plans = listOf(
                SubscriptionPlanPreset("Indywidualny", 25.99),
                SubscriptionPlanPreset("Studencki", 14.99),
                SubscriptionPlanPreset("Rodzinny", 46.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Apple TV+",
            plans = listOf(
                SubscriptionPlanPreset("Standard", 34.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Spotify",
            plans = listOf(
                SubscriptionPlanPreset("Individual", 23.99),
                SubscriptionPlanPreset("Student", 12.99),
                SubscriptionPlanPreset("Duo", 30.99),
                SubscriptionPlanPreset("Family", 37.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Tidal",
            plans = listOf(
                SubscriptionPlanPreset("HiFi", 21.99),
                SubscriptionPlanPreset("Family", 34.99),
                SubscriptionPlanPreset("Student", 10.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Apple Music",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 22.99),
                SubscriptionPlanPreset("Studencki", 11.99),
                SubscriptionPlanPreset("Rodzinny", 34.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Deezer",
            plans = listOf(
                SubscriptionPlanPreset("Premium", 24.99),
                SubscriptionPlanPreset("Family", 41.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Audioteka",
            plans = listOf(
                SubscriptionPlanPreset("Klub Audioteki", 19.90),
                SubscriptionPlanPreset("Klub Optymalny", 34.90)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Storytel",
            plans = listOf(
                SubscriptionPlanPreset("Basic", 22.90),
                SubscriptionPlanPreset("Premium", 44.90),
                SubscriptionPlanPreset("Family", 54.90)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Empik Go",
            plans = listOf(
                SubscriptionPlanPreset("Go Lektury", 14.99),
                SubscriptionPlanPreset("Go Standard", 24.99),
                SubscriptionPlanPreset("Go Max", 44.99)
            ),
            tags = listOf("Rozrywka", "Informacje")
        ),
        ServicePreset(
            serviceName = "Legimi",
            plans = listOf(
                SubscriptionPlanPreset("Bez limitu", 49.99),
                SubscriptionPlanPreset("Bez limitu + E-ink", 54.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Marvel Unlimited",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 45.00),
                SubscriptionPlanPreset("Roczny", 325.00, "Rok")
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Allegro Smart!",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 14.99),
                SubscriptionPlanPreset("Roczny", 59.90, "Rok")
            ),
            tags = listOf("Zakupy i Dostawy")
        ),
        ServicePreset(
            serviceName = "Amazon Prime",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 10.99),
                SubscriptionPlanPreset("Roczny", 49.00, "Rok")
            ),
            tags = listOf("Rozrywka", "Zakupy i Dostawy")
        ),
        ServicePreset(
            serviceName = "Empik Premium",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 12.99),
                SubscriptionPlanPreset("Roczny", 59.99, "Rok")
            ),
            tags = listOf("Zakupy i Dostawy", "Informacje")
        ),
        ServicePreset(
            serviceName = "InPost Fresh",
            plans = listOf(
                SubscriptionPlanPreset("Darmowa Dostawa", 0.00)
            ),
            tags = listOf("Zakupy i Dostawy")
        ),
        ServicePreset(
            serviceName = "Glovo Prime",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 14.99)
            ),
            tags = listOf("Zakupy i Dostawy")
        ),
        ServicePreset(
            serviceName = "Uber One",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 12.99),
                SubscriptionPlanPreset("Roczny", 129.99, "Rok")
            ),
            tags = listOf("Zakupy i Dostawy")
        ),
        ServicePreset(
            serviceName = "Xbox Game Pass",
            plans = listOf(
                SubscriptionPlanPreset("Core", 34.99),
                SubscriptionPlanPreset("Ultimate", 62.99)
            ),
            tags = listOf("Gaming", "Rozrywka")
        ),
        ServicePreset(
            serviceName = "PlayStation Plus",
            plans = listOf(
                SubscriptionPlanPreset("Essential", 37.00),
                SubscriptionPlanPreset("Extra", 58.00),
                SubscriptionPlanPreset("Premium", 70.00)
            ),
            tags = listOf("Gaming", "Rozrywka")
        ),
        ServicePreset(
            serviceName = "EA Play",
            plans = listOf(
                SubscriptionPlanPreset("Standard", 24.90),
                SubscriptionPlanPreset("Pro", 85.00)
            ),
            tags = listOf("Gaming", "Rozrywka")
        ),
        ServicePreset(
            serviceName = "Ubisoft+",
            plans = listOf(
                SubscriptionPlanPreset("Classics", 33.90),
                SubscriptionPlanPreset("Premium", 74.90)
            ),
            tags = listOf("Gaming", "Rozrywka")
        ),
        ServicePreset(
            serviceName = "Nintendo Switch Online",
            plans = listOf(
                SubscriptionPlanPreset("Indywidualny", 16.00),
                SubscriptionPlanPreset("Indywidualny + Expansion", 170.00, "Rok"),
                SubscriptionPlanPreset("Rodzinny", 120.00, "Rok")
            ),
            tags = listOf("Gaming", "Rozrywka")
        ),
        ServicePreset(
            serviceName = "GeForce NOW",
            plans = listOf(
                SubscriptionPlanPreset("Priority", 49.00),
                SubscriptionPlanPreset("Ultimate", 99.00)
            ),
            tags = listOf("Gaming", "Rozrywka")
        ),
        ServicePreset(
            serviceName = "Microsoft 365",
            plans = listOf(
                SubscriptionPlanPreset("Personal", 29.99),
                SubscriptionPlanPreset("Family", 42.99)
            ),
            tags = listOf("Produktywność")
        ),
        ServicePreset(
            serviceName = "Google One",
            plans = listOf(
                SubscriptionPlanPreset("100 GB", 8.99),
                SubscriptionPlanPreset("200 GB", 13.99),
                SubscriptionPlanPreset("2 TB Premium", 46.99)
            ),
            tags = listOf("Produktywność")
        ),
        ServicePreset(
            serviceName = "Adobe Creative Cloud",
            plans = listOf(
                SubscriptionPlanPreset("Plan fotograficzny", 53.99),
                SubscriptionPlanPreset("Pojedyncza aplikacja", 114.99),
                SubscriptionPlanPreset("Wszystkie aplikacje", 284.99)
            ),
            tags = listOf("Produktywność")
        ),
        ServicePreset(
            serviceName = "Canva Pro",
            plans = listOf(
                SubscriptionPlanPreset("Dla jednej osoby", 49.99),
                SubscriptionPlanPreset("Dla zespołu", 119.00)
            ),
            tags = listOf("Produktywność")
        ),
        ServicePreset(
            serviceName = "ChatGPT Plus",
            plans = listOf(
                SubscriptionPlanPreset("Plus", 85.00)
            ),
            tags = listOf("Produktywność")
        ),
        ServicePreset(
            serviceName = "Midjourney",
            plans = listOf(
                SubscriptionPlanPreset("Basic Plan", 42.00),
                SubscriptionPlanPreset("Standard Plan", 125.00)
            ),
            tags = listOf("Produktywność")
        ),
        ServicePreset(
            serviceName = "GitHub Copilot",
            plans = listOf(
                SubscriptionPlanPreset("Copilot Individual", 42.00)
            ),
            tags = listOf("Produktywność")
        ),
        ServicePreset(
            serviceName = "Notion",
            plans = listOf(
                SubscriptionPlanPreset("Plus", 42.00),
                SubscriptionPlanPreset("Notion AI", 34.00)
            ),
            tags = listOf("Produktywność")
        ),
        ServicePreset(
            serviceName = "iCloud+",
            plans = listOf(
                SubscriptionPlanPreset("50 GB", 4.99),
                SubscriptionPlanPreset("200 GB", 14.99),
                SubscriptionPlanPreset("2 TB", 49.99)
            ),
            tags = listOf("Produktywność")
        ),
        ServicePreset(
            serviceName = "LinkedIn Premium",
            plans = listOf(
                SubscriptionPlanPreset("Career", 137.98),
                SubscriptionPlanPreset("Business", 239.99)
            ),
            tags = listOf("Produktywność")
        ),
        ServicePreset(
            serviceName = "Duolingo Plus",
            plans = listOf(
                SubscriptionPlanPreset("Super (Indywidualny)", 34.99),
                SubscriptionPlanPreset("Super (Rodzinny)", 52.99)
            ),
            tags = listOf("Edukacja")
        ),
        ServicePreset(
            serviceName = "Wyborcza.pl",
            plans = listOf(
                SubscriptionPlanPreset("Podstawowy", 19.90),
                SubscriptionPlanPreset("Premium", 29.90)
            ),
            tags = listOf("Informacje")
        ),
        ServicePreset(
            serviceName = "Newsweek Polska",
            plans = listOf(
                SubscriptionPlanPreset("Podstawowy", 24.90),
                SubscriptionPlanPreset("Premium (Onet Premium)", 34.90)
            ),
            tags = listOf("Informacje")
        ),
        ServicePreset(
            serviceName = "Onet Premium",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 19.90),
                SubscriptionPlanPreset("Roczny", 199.00, "Rok")
            ),
            tags = listOf("Informacje")
        ),
        ServicePreset(
            serviceName = "Polityka Cyfrowa",
            plans = listOf(
                SubscriptionPlanPreset("Standard", 29.00),
                SubscriptionPlanPreset("Premium", 39.00)
            ),
            tags = listOf("Informacje")
        ),
        ServicePreset(
            serviceName = "MultiSport",
            plans = listOf(
                SubscriptionPlanPreset("Light", 80.00),
                SubscriptionPlanPreset("Plus", 120.00)
            ),
            tags = listOf("Sport i Zdrowie")
        ),
        ServicePreset(
            serviceName = "Medicover Sport",
            plans = listOf(
                SubscriptionPlanPreset("Fit & Gym", 99.00),
                SubscriptionPlanPreset("Aqua & Gym Plus", 139.00)
            ),
            tags = listOf("Sport i Zdrowie")
        ),
        ServicePreset(
            serviceName = "Strava",
            plans = listOf(
                SubscriptionPlanPreset("Premium Miesięczny", 49.99),
                SubscriptionPlanPreset("Premium Roczny", 289.99, "Rok")
            ),
            tags = listOf("Sport i Zdrowie")
        ),
        ServicePreset(
            serviceName = "Tinder Gold",
            plans = listOf(
                SubscriptionPlanPreset("Plus", 32.99),
                SubscriptionPlanPreset("Gold", 49.99),
                SubscriptionPlanPreset("Platinum", 64.99)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "Fitatu Premium",
            plans = listOf(
                SubscriptionPlanPreset("Premium Miesięczny", 24.99),
                SubscriptionPlanPreset("Premium Roczny", 149.99, "Rok")
            ),
            tags = listOf("Sport i Zdrowie")
        ),
        ServicePreset(
            serviceName = "Headspace",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny", 51.99),
                SubscriptionPlanPreset("Roczny", 289.99, "Rok")
            ),
            tags = listOf("Sport i Zdrowie")
        ),
        ServicePreset(
            serviceName = "Flo Premium",
            plans = listOf(
                SubscriptionPlanPreset("Premium Miesięczny", 19.99),
                SubscriptionPlanPreset("Premium Roczny", 129.99, "Rok")
            ),
            tags = listOf("Sport i Zdrowie")
        ),
        ServicePreset(
            serviceName = "Crunchyroll",
            plans = listOf(
                SubscriptionPlanPreset("Fan", 25.00),
                SubscriptionPlanPreset("Mega Fan", 30.00)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "CD-Action (Geek Week)",
            plans = listOf(
                SubscriptionPlanPreset("Miesięczny Premium", 15.00),
                SubscriptionPlanPreset("Roczny Premium", 149.00, "Rok")
            ),
            tags = listOf("Gaming", "Informacje")
        ),
        ServicePreset(
            serviceName = "Pyszne.pl Premium",
            plans = listOf(
                SubscriptionPlanPreset("Darmowe Dostawy", 12.99)
            ),
            tags = listOf("Zakupy i Dostawy")
        ),
        ServicePreset(
            serviceName = "Carly (Premium App)",
            plans = listOf(
                SubscriptionPlanPreset("Dla jednej marki", 39.99, "Rok"),
                SubscriptionPlanPreset("Wszystkie marki", 79.99, "Rok")
            ),
            tags = listOf("Motoryzacja")
        ),
        ServicePreset(
            serviceName = "SoundCloud Go+",
            plans = listOf(
                SubscriptionPlanPreset("Go+ Standard", 25.00),
                SubscriptionPlanPreset("Go+ Student", 12.50)
            ),
            tags = listOf("Rozrywka")
        ),
        ServicePreset(
            serviceName = "HelloChinese",
            plans = listOf(
                SubscriptionPlanPreset("Premium Miesięczny", 42.99),
                SubscriptionPlanPreset("Premium Roczny", 259.99, "Rok"),
                SubscriptionPlanPreset("Premium Plus Miesięczny", 89.99),
                SubscriptionPlanPreset("Premium Plus Roczny", 519.99, "Rok")
            ),
            tags = listOf("Edukacja")
        )
    )
}