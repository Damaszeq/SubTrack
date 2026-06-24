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
                SubscriptionPlanPreset("netflix_basic", R.string.plan_basic, 29.00, "Miesiąc"),
                SubscriptionPlanPreset("netflix_standard", R.string.plan_standard, 43.00, "Miesiąc"),
                SubscriptionPlanPreset("netflix_premium", R.string.plan_premium_4k, 67.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Disney+",
            plans = listOf(
                SubscriptionPlanPreset("disney_standard", R.string.plan_standard, 37.99, "Miesiąc"),
                SubscriptionPlanPreset("disney_premium", R.string.plan_premium_4k, 49.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "HBO Max",
            plans = listOf(
                SubscriptionPlanPreset("hbo_max_standard", R.string.plan_standard, 29.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "SkyShowtime",
            plans = listOf(
                SubscriptionPlanPreset("skyshowtime_standard", R.string.plan_standard, 24.99, "Miesiąc"),
                SubscriptionPlanPreset("skyshowtime_premium", R.string.plan_premium_4k, 49.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Amazon Prime Video",
            plans = listOf(
                SubscriptionPlanPreset("amazon_video_monthly", R.string.plan_monthly, 10.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Player",
            plans = listOf(
                SubscriptionPlanPreset("player_ads", R.string.plan_basic, 15.00, "Miesiąc"),
                SubscriptionPlanPreset("player_no_ads", R.string.plan_standard, 25.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Polsat Box Go",
            plans = listOf(
                SubscriptionPlanPreset("polsat_box_premium", R.string.plan_standard, 30.00, "Miesiąc"),
                SubscriptionPlanPreset("polsat_box_sport", R.string.plan_premium, 40.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Canal+ Online",
            plans = listOf(
                SubscriptionPlanPreset("canal_plus_standard", R.string.plan_standard, 54.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "TVP VOD",
            plans = listOf(
                SubscriptionPlanPreset("tvp_vod_strefa", R.string.plan_individual, 9.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Viaplay",
            plans = listOf(
                SubscriptionPlanPreset("viaplay_medium", R.string.plan_standard, 40.00, "Miesiąc"),
                SubscriptionPlanPreset("viaplay_total", R.string.plan_premium, 55.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "YouTube Premium",
            plans = listOf(
                SubscriptionPlanPreset("youtube_individual", R.string.plan_individual, 25.99, "Miesiąc"),
                SubscriptionPlanPreset("youtube_student", R.string.plan_student, 14.99, "Miesiąc"),
                SubscriptionPlanPreset("youtube_family", R.string.plan_family, 46.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Apple TV+",
            plans = listOf(
                SubscriptionPlanPreset("appletv_individual", R.string.plan_individual, 34.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),

        // --- MUZYKA / AUDIO / KSIĄŻKI (ENTERTAINMENT & EDUCATION) ---
        ServicePreset(
            serviceName = "Spotify",
            plans = listOf(
                SubscriptionPlanPreset("spotify_individual", R.string.plan_individual, 23.99, "Miesiąc"),
                SubscriptionPlanPreset("spotify_student", R.string.plan_student, 12.99, "Miesiąc"),
                SubscriptionPlanPreset("spotify_family", R.string.plan_family, 37.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Tidal",
            plans = listOf(
                SubscriptionPlanPreset("tidal_individual", R.string.plan_individual, 21.99, "Miesiąc"),
                SubscriptionPlanPreset("tidal_family", R.string.plan_family, 34.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Apple Music",
            plans = listOf(
                SubscriptionPlanPreset("applemusic_individual", R.string.plan_individual, 21.99, "Miesiąc"),
                SubscriptionPlanPreset("applemusic_family", R.string.plan_family, 34.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Deezer",
            plans = listOf(
                SubscriptionPlanPreset("deezer_premium", R.string.plan_individual, 24.99, "Miesiąc"),
                SubscriptionPlanPreset("deezer_family", R.string.plan_family, 41.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "Audioteka",
            plans = listOf(
                SubscriptionPlanPreset("audioteka_club", R.string.plan_individual, 29.90, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_education)
        ),
        ServicePreset(
            serviceName = "Storytel",
            plans = listOf(
                SubscriptionPlanPreset("storytel_basic", R.string.plan_basic, 22.90, "Miesiąc"),
                SubscriptionPlanPreset("storytel_unlimited", R.string.plan_individual, 44.90, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_education)
        ),
        ServicePreset(
            serviceName = "Empik Go",
            plans = listOf(
                SubscriptionPlanPreset("empik_go_audiobooks", R.string.plan_standard, 32.99, "Miesiąc"),
                SubscriptionPlanPreset("empik_go_max", R.string.plan_premium, 44.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_education)
        ),
        ServicePreset(
            serviceName = "Legimi",
            plans = listOf(
                SubscriptionPlanPreset("legimi_limit", R.string.plan_basic, 32.99, "Miesiąc"),
                SubscriptionPlanPreset("legimi_unlimited", R.string.plan_individual, 49.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_education)
        ),

        // --- ZAKUPY I DOSTAWY (SHOPPING / DELIVERY) ---
        ServicePreset(
            serviceName = "Allegro Smart!",
            plans = listOf(
                SubscriptionPlanPreset("allegro_smart_monthly", R.string.plan_monthly, 14.99, "Miesiąc"),
                SubscriptionPlanPreset("allegro_smart_yearly", R.string.plan_yearly, 59.90, "Rok")
            ),
            tagsRes = listOf(R.string.tag_shopping)
        ),
        ServicePreset(
            serviceName = "Amazon Prime (Dostawy)",
            plans = listOf(
                SubscriptionPlanPreset("amazon_prime_delivery_monthly", R.string.plan_monthly, 10.99, "Miesiąc"),
                SubscriptionPlanPreset("amazon_prime_delivery_yearly", R.string.plan_yearly, 49.00, "Rok")
            ),
            tagsRes = listOf(R.string.tag_shopping)
        ),
        ServicePreset(
            serviceName = "Empik Premium",
            plans = listOf(
                SubscriptionPlanPreset("empik_premium_monthly", R.string.plan_monthly, 12.99, "Miesiąc"),
                SubscriptionPlanPreset("empik_premium_yearly", R.string.plan_yearly, 49.99, "Rok")
            ),
            tagsRes = listOf(R.string.tag_shopping)
        ),
        ServicePreset(
            serviceName = "InPost Fresh",
            plans = listOf(
                SubscriptionPlanPreset("inpost_fresh_sub", R.string.plan_individual, 9.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_shopping)
        ),
        ServicePreset(
            serviceName = "Glovo Prime",
            plans = listOf(
                SubscriptionPlanPreset("glovo_prime_monthly", R.string.plan_monthly, 14.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_shopping)
        ),
        ServicePreset(
            serviceName = "Uber One",
            plans = listOf(
                SubscriptionPlanPreset("uber_one_monthly", R.string.plan_monthly, 12.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_shopping)
        ),

        // --- GAMING ---
        ServicePreset(
            serviceName = "Xbox Game Pass",
            plans = listOf(
                SubscriptionPlanPreset("xbox_game_pass_core", R.string.plan_basic, 34.99, "Miesiąc"),
                SubscriptionPlanPreset("xbox_game_pass_ultimate", R.string.plan_premium, 62.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_gaming, R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "PlayStation Plus",
            plans = listOf(
                SubscriptionPlanPreset("ps_plus_essential", R.string.plan_basic, 37.00, "Miesiąc"),
                SubscriptionPlanPreset("ps_plus_extra", R.string.plan_standard, 58.00, "Miesiąc"),
                SubscriptionPlanPreset("ps_plus_premium", R.string.plan_premium, 70.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_gaming, R.string.tag_entertainment)
        ),
        ServicePreset(
            serviceName = "EA Play",
            plans = listOf(
                SubscriptionPlanPreset("ea_play_standard", R.string.plan_standard, 29.90, "Miesiąc"),
                SubscriptionPlanPreset("ea_play_pro", R.string.plan_premium, 64.90, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_gaming)
        ),
        ServicePreset(
            serviceName = "Ubisoft+",
            plans = listOf(
                SubscriptionPlanPreset("ubisoft_plus_classics", R.string.plan_standard, 33.90, "Miesiąc"),
                SubscriptionPlanPreset("ubisoft_plus_premium", R.string.plan_premium, 74.90, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_gaming)
        ),
        ServicePreset(
            serviceName = "Nintendo Switch Online",
            plans = listOf(
                SubscriptionPlanPreset("nintendo_individual", R.string.plan_individual, 16.00, "Miesiąc"),
                SubscriptionPlanPreset("nintendo_family_yearly", R.string.plan_family, 140.00, "Rok")
            ),
            tagsRes = listOf(R.string.tag_gaming)
        ),
        ServicePreset(
            serviceName = "GeForce Now",
            plans = listOf(
                SubscriptionPlanPreset("geforce_now_priority", R.string.plan_standard, 49.00, "Miesiąc"),
                SubscriptionPlanPreset("geforce_now_ultimate", R.string.plan_premium, 99.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_gaming)
        ),

        // --- PRODUKTYWNOŚĆ, DESIGN I CHMURA (PRODUCTIVITY) ---
        ServicePreset(
            serviceName = "Microsoft 365",
            plans = listOf(
                SubscriptionPlanPreset("ms365_personal", R.string.plan_individual, 29.99, "Miesiąc"),
                SubscriptionPlanPreset("ms365_family", R.string.plan_family, 42.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_productivity)
        ),
        ServicePreset(
            serviceName = "Google One",
            plans = listOf(
                SubscriptionPlanPreset("google_one_100gb", R.string.plan_basic, 8.99, "Miesiąc"),
                SubscriptionPlanPreset("google_one_200gb", R.string.plan_standard, 13.99, "Miesiąc"),
                SubscriptionPlanPreset("google_one_2tb", R.string.plan_premium, 46.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_productivity)
        ),
        ServicePreset(
            serviceName = "Adobe Creative Cloud",
            plans = listOf(
                SubscriptionPlanPreset("adobe_cc_student", R.string.plan_student, 95.00, "Miesiąc"),
                SubscriptionPlanPreset("adobe_cc_individual", R.string.plan_individual, 280.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_productivity)
        ),
        ServicePreset(
            serviceName = "Canva Pro",
            plans = listOf(
                SubscriptionPlanPreset("canva_pro_individual", R.string.plan_individual, 49.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_productivity)
        ),
        ServicePreset(
            serviceName = "ChatGPT Plus",
            plans = listOf(
                SubscriptionPlanPreset("chatgpt_plus", R.string.plan_individual, 85.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_productivity, R.string.tag_education)
        ),
        ServicePreset(
            serviceName = "iCloud+",
            plans = listOf(
                SubscriptionPlanPreset("icloud_50gb", R.string.plan_basic, 4.99, "Miesiąc"),
                SubscriptionPlanPreset("icloud_200gb", R.string.plan_standard, 14.99, "Miesiąc"),
                SubscriptionPlanPreset("icloud_2tb", R.string.plan_premium, 49.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_productivity)
        ),
        ServicePreset(
            serviceName = "LinkedIn Premium",
            plans = listOf(
                SubscriptionPlanPreset("linkedin_career", R.string.plan_standard, 124.99, "Miesiąc"),
                SubscriptionPlanPreset("linkedin_business", R.string.plan_premium, 200.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_productivity)
        ),

        // --- EDUKACJA (EDUCATION) ---
        ServicePreset(
            serviceName = "Duolingo Plus",
            plans = listOf(
                SubscriptionPlanPreset("duolingo_individual", R.string.plan_individual, 34.99, "Miesiąc"),
                SubscriptionPlanPreset("duolingo_family", R.string.plan_family, 52.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_education)
        ),

        // --- PRASA I INFORMACJE (NEWS) ---
        ServicePreset(
            serviceName = "Wyborcza.pl",
            plans = listOf(
                SubscriptionPlanPreset("wyborcza_premium", R.string.plan_standard, 19.90, "Miesiąc"),
                SubscriptionPlanPreset("wyborcza_club", R.string.plan_premium, 29.90, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_news)
        ),
        ServicePreset(
            serviceName = "Newsweek Polska",
            plans = listOf(
                SubscriptionPlanPreset("newsweek_digital", R.string.plan_standard, 25.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_news)
        ),
        ServicePreset(
            serviceName = "Onet Premium",
            plans = listOf(
                SubscriptionPlanPreset("onet_premium_sub", R.string.plan_individual, 19.90, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_news)
        ),
        ServicePreset(
            serviceName = "Polityka Cyfrowa",
            plans = listOf(
                SubscriptionPlanPreset("polityka_digital", R.string.plan_standard, 29.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_news)
        ),

        // --- SPORT / ZDROWIE / ROZRYWKA (HEALTH & LIFESTYLE) ---
        ServicePreset(
            serviceName = "MultiSport",
            plans = listOf(
                SubscriptionPlanPreset("multisport_plus", R.string.plan_individual, 179.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_health)
        ),
        ServicePreset(
            serviceName = "Medicover Sport",
            plans = listOf(
                SubscriptionPlanPreset("medicover_fit", R.string.plan_standard, 120.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_health)
        ),
        ServicePreset(
            serviceName = "Tinder Gold",
            plans = listOf(
                SubscriptionPlanPreset("tinder_gold_sub", R.string.plan_individual, 65.00, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_lifestyle)
        ),
        ServicePreset(
            serviceName = "Fitatu Premium",
            plans = listOf(
                SubscriptionPlanPreset("fitatu_monthly", R.string.plan_monthly, 24.99, "Miesiąc"),
                SubscriptionPlanPreset("fitatu_yearly", R.string.plan_yearly, 89.99, "Rok")
            ),
            tagsRes = listOf(R.string.tag_health)
        ),
        ServicePreset(
            serviceName = "Headspace",
            plans = listOf(
                SubscriptionPlanPreset("headspace_monthly", R.string.plan_monthly, 49.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_health)
        ),
        ServicePreset(
            serviceName = "Flo Premium",
            plans = listOf(
                SubscriptionPlanPreset("flo_monthly", R.string.plan_monthly, 39.99, "Miesiąc")
            ),
            tagsRes = listOf(R.string.tag_health)
        )
    )
}