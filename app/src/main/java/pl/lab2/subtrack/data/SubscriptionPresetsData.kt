package pl.lab2.subtrack.data

import pl.lab2.subtrack.R
import pl.lab2.subtrack.model.ServicePreset
import pl.lab2.subtrack.model.SubscriptionPlanPreset

object SubscriptionPresetsData {
    val availablePresets = listOf(
        // === ABSOLUTNY TOP POPULARNOŚCI (WAGA 90 - 100) ===
        ServicePreset(
            serviceName = "Netflix",
            popularityWeight = 100,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("netflix_basic", R.string.plan_basic, 29.00, "Miesiąc"),
                SubscriptionPlanPreset("netflix_standard", R.string.plan_standard, 43.00, "Miesiąc"),
                SubscriptionPlanPreset("netflix_premium", R.string.plan_premium_4k, 67.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Spotify",
            popularityWeight = 98,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("spotify_individual", R.string.plan_individual, 23.99, "Miesiąc"),
                SubscriptionPlanPreset("spotify_student", R.string.plan_student, 12.99, "Miesiąc"),
                SubscriptionPlanPreset("spotify_family", R.string.plan_family, 37.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "YouTube Premium",
            popularityWeight = 96,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("youtube_individual", R.string.plan_individual, 25.99, "Miesiąc"),
                SubscriptionPlanPreset("youtube_student", R.string.plan_student, 14.99, "Miesiąc"),
                SubscriptionPlanPreset("youtube_family", R.string.plan_family, 46.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Allegro Smart!",
            popularityWeight = 95,
            tagsRes = listOf(R.string.tag_shopping),
            plans = listOf(
                SubscriptionPlanPreset("allegro_smart_monthly", R.string.plan_monthly, 14.99, "Miesiąc"),
                SubscriptionPlanPreset("allegro_smart_yearly", R.string.plan_yearly, 59.90, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Disney+",
            popularityWeight = 92,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("disney_standard", R.string.plan_standard, 37.99, "Miesiąc"),
                SubscriptionPlanPreset("disney_premium", R.string.plan_premium_4k, 49.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "HBO Max",
            popularityWeight = 90,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("hbo_max_standard", R.string.plan_standard, 29.99, "Miesiąc")
            )
        ),

        // === BARDZO POPULARNE (WAGA 75 - 89) ===
        ServicePreset(
            serviceName = "iCloud+",
            popularityWeight = 88,
            tagsRes = listOf(R.string.tag_productivity),
            plans = listOf(
                SubscriptionPlanPreset("icloud_50gb", R.string.plan_basic, 4.99, "Miesiąc"),
                SubscriptionPlanPreset("icloud_200gb", R.string.plan_standard, 14.99, "Miesiąc"),
                SubscriptionPlanPreset("icloud_2tb", R.string.plan_premium, 49.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Google One",
            popularityWeight = 86,
            tagsRes = listOf(R.string.tag_productivity),
            plans = listOf(
                SubscriptionPlanPreset("google_one_100gb", R.string.plan_basic, 8.99, "Miesiąc"),
                SubscriptionPlanPreset("google_one_200gb", R.string.plan_standard, 13.99, "Miesiąc"),
                SubscriptionPlanPreset("google_one_2tb", R.string.plan_premium, 46.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Xbox Game Pass",
            popularityWeight = 85,
            tagsRes = listOf(R.string.tag_gaming, R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("xbox_game_pass_core", R.string.plan_basic, 34.99, "Miesiąc"),
                SubscriptionPlanPreset("xbox_game_pass_ultimate", R.string.plan_premium, 62.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Canal+ Online",
            popularityWeight = 84,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("canal_plus_standard", R.string.plan_standard, 54.00, "Miesiąc"),
                SubscriptionPlanPreset("canal_plus_sport", R.string.plan_premium, 69.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Amazon Prime (Dostawy)",
            popularityWeight = 82,
            tagsRes = listOf(R.string.tag_shopping),
            plans = listOf(
                SubscriptionPlanPreset("amazon_prime_delivery_monthly", R.string.plan_monthly, 10.99, "Miesiąc"),
                SubscriptionPlanPreset("amazon_prime_delivery_yearly", R.string.plan_yearly, 49.00, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "PlayStation Plus",
            popularityWeight = 80,
            tagsRes = listOf(R.string.tag_gaming, R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("ps_plus_essential", R.string.plan_basic, 37.00, "Miesiąc"),
                SubscriptionPlanPreset("ps_plus_extra", R.string.plan_standard, 58.00, "Miesiąc"),
                SubscriptionPlanPreset("ps_plus_premium", R.string.plan_premium, 70.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "ChatGPT Plus",
            popularityWeight = 78,
            tagsRes = listOf(R.string.tag_productivity, R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("chatgpt_plus", R.string.plan_individual, 85.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Duolingo Plus",
            popularityWeight = 76,
            tagsRes = listOf(R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("duolingo_individual", R.string.plan_individual, 34.99, "Miesiąc"),
                SubscriptionPlanPreset("duolingo_family", R.string.plan_family, 52.99, "Miesiąc")
            )
        ),

        // === ŚREDNIA POPULARNOŚĆ (WAGA 50 - 74) ===
        ServicePreset(
            serviceName = "Discord Nitro",
            popularityWeight = 74,
            tagsRes = listOf(R.string.tag_gaming, R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("discord_nitro_classic", R.string.plan_basic, 19.99, "Miesiąc"),
                SubscriptionPlanPreset("discord_nitro_full", R.string.plan_standard, 47.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Microsoft 365",
            popularityWeight = 72,
            tagsRes = listOf(R.string.tag_productivity),
            plans = listOf(
                SubscriptionPlanPreset("ms365_personal", R.string.plan_individual, 29.99, "Miesiąc"),
                SubscriptionPlanPreset("ms365_family", R.string.plan_family, 42.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "GeForce Now",
            popularityWeight = 70,
            tagsRes = listOf(R.string.tag_gaming),
            plans = listOf(
                SubscriptionPlanPreset("geforce_now_priority", R.string.plan_standard, 49.00, "Miesiąc"),
                SubscriptionPlanPreset("geforce_now_ultimate", R.string.plan_premium, 99.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "MultiSport",
            popularityWeight = 68,
            tagsRes = listOf(R.string.tag_health),
            plans = listOf(
                SubscriptionPlanPreset("multisport_plus", R.string.plan_individual, 179.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "SkyShowtime",
            popularityWeight = 66,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("skyshowtime_standard", R.string.plan_standard, 24.99, "Miesiąc"),
                SubscriptionPlanPreset("skyshowtime_premium", R.string.plan_premium_4k, 49.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Amazon Prime Video",
            popularityWeight = 65,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("amazon_video_monthly", R.string.plan_monthly, 10.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Uber One",
            popularityWeight = 64,
            tagsRes = listOf(R.string.tag_shopping),
            plans = listOf(
                SubscriptionPlanPreset("uber_one_monthly", R.string.plan_monthly, 12.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Pyszne.pl Premium",
            popularityWeight = 62,
            tagsRes = listOf(R.string.tag_shopping),
            plans = listOf(
                SubscriptionPlanPreset("pyszne_premium_monthly", R.string.plan_monthly, 12.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Glovo Prime",
            popularityWeight = 60,
            tagsRes = listOf(R.string.tag_shopping),
            plans = listOf(
                SubscriptionPlanPreset("glovo_prime_monthly", R.string.plan_monthly, 14.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Wolt+",
            popularityWeight = 58,
            tagsRes = listOf(R.string.tag_shopping),
            plans = listOf(
                SubscriptionPlanPreset("wolt_plus_monthly", R.string.plan_monthly, 14.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "GitHub Copilot",
            popularityWeight = 56,
            tagsRes = listOf(R.string.tag_productivity, R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("github_copilot_individual", R.string.plan_individual, 40.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Claude Pro",
            popularityWeight = 55,
            tagsRes = listOf(R.string.tag_productivity, R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("claude_pro", R.string.plan_individual, 85.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "YouTube Music",
            popularityWeight = 54,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("yt_music_individual", R.string.plan_individual, 21.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Apple Music",
            popularityWeight = 52,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("applemusic_individual", R.string.plan_individual, 21.99, "Miesiąc"),
                SubscriptionPlanPreset("applemusic_family", R.string.plan_family, 34.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Tidal",
            popularityWeight = 50,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("tidal_individual", R.string.plan_individual, 21.99, "Miesiąc"),
                SubscriptionPlanPreset("tidal_family", R.string.plan_family, 34.99, "Miesiąc")
            )
        ),

        // === NIŻSZA POPULARNOŚĆ / NISZOWE (WAGA < 50) ===
        ServicePreset(
            serviceName = "Player",
            popularityWeight = 48,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("player_ads", R.string.plan_basic, 15.00, "Miesiąc"),
                SubscriptionPlanPreset("player_no_ads", R.string.plan_standard, 25.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Polsat Box Go",
            popularityWeight = 46,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("polsat_box_premium", R.string.plan_standard, 30.00, "Miesiąc"),
                SubscriptionPlanPreset("polsat_box_sport", R.string.plan_premium, 40.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Viaplay",
            popularityWeight = 44,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("viaplay_medium", R.string.plan_standard, 40.00, "Miesiąc"),
                SubscriptionPlanPreset("viaplay_total", R.string.plan_premium, 55.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "F1 TV Pro",
            popularityWeight = 42,
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_health),
            plans = listOf(
                SubscriptionPlanPreset("f1tv_pro_monthly", R.string.plan_monthly, 35.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Apple TV+",
            popularityWeight = 40,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("appletv_individual", R.string.plan_individual, 34.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "CDA Premium",
            popularityWeight = 38,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("cda_premium_standard", R.string.plan_standard, 23.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Marvel Unlimited",
            popularityWeight = 36,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("marvel_unlimited_monthly", R.string.plan_monthly, 45.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Fitatu Premium",
            popularityWeight = 35,
            tagsRes = listOf(R.string.tag_health),
            plans = listOf(
                SubscriptionPlanPreset("fitatu_monthly", R.string.plan_monthly, 24.99, "Miesiąc"),
                SubscriptionPlanPreset("fitatu_yearly", R.string.plan_yearly, 89.99, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Strava",
            popularityWeight = 34,
            tagsRes = listOf(R.string.tag_health),
            plans = listOf(
                SubscriptionPlanPreset("strava_premium_monthly", R.string.plan_monthly, 32.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Crunchyroll",
            popularityWeight = 32,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("crunchyroll_fan", R.string.plan_standard, 25.00, "Miesiąc"),
                SubscriptionPlanPreset("crunchyroll_mega_fan", R.string.plan_premium, 30.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Empik Premium",
            popularityWeight = 30,
            tagsRes = listOf(R.string.tag_shopping),
            plans = listOf(
                SubscriptionPlanPreset("empik_premium_monthly", R.string.plan_monthly, 12.99, "Miesiąc"),
                SubscriptionPlanPreset("empik_premium_yearly", R.string.plan_yearly, 49.99, "Rok")
            )
        ),
        ServicePreset(
            serviceName = "Legimi",
            popularityWeight = 28,
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("legimi_limit", R.string.plan_basic, 32.99, "Miesiąc"),
                SubscriptionPlanPreset("legimi_unlimited", R.string.plan_individual, 49.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Audioteka",
            popularityWeight = 26,
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("audioteka_club", R.string.plan_individual, 29.90, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Storytel",
            popularityWeight = 25,
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("storytel_basic", R.string.plan_basic, 22.90, "Miesiąc"),
                SubscriptionPlanPreset("storytel_unlimited", R.string.plan_individual, 44.90, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Empik Go",
            popularityWeight = 24,
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("empik_go_audiobooks", R.string.plan_standard, 32.99, "Miesiąc"),
                SubscriptionPlanPreset("empik_go_max", R.string.plan_premium, 44.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "BookBeat",
            popularityWeight = 22,
            tagsRes = listOf(R.string.tag_entertainment, R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("bookbeat_basic", R.string.plan_basic, 19.99, "Miesiąc"),
                SubscriptionPlanPreset("bookbeat_standard", R.string.plan_standard, 29.99, "Miesiąc"),
                SubscriptionPlanPreset("bookbeat_premium", R.string.plan_premium, 49.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Adobe Creative Cloud",
            popularityWeight = 20,
            tagsRes = listOf(R.string.tag_productivity),
            plans = listOf(
                SubscriptionPlanPreset("adobe_cc_student", R.string.plan_student, 95.00, "Miesiąc"),
                SubscriptionPlanPreset("adobe_cc_individual", R.string.plan_individual, 280.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Canva Pro",
            popularityWeight = 19,
            tagsRes = listOf(R.string.tag_productivity),
            plans = listOf(
                SubscriptionPlanPreset("canva_pro_individual", R.string.plan_individual, 49.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "NordVPN",
            popularityWeight = 18,
            tagsRes = listOf(R.string.tag_productivity),
            plans = listOf(
                SubscriptionPlanPreset("nordvpn_monthly", R.string.plan_monthly, 49.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Notion Plus",
            popularityWeight = 17,
            tagsRes = listOf(R.string.tag_productivity),
            plans = listOf(
                SubscriptionPlanPreset("notion_plus_monthly", R.string.plan_standard, 40.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Dropbox",
            popularityWeight = 16,
            tagsRes = listOf(R.string.tag_productivity),
            plans = listOf(
                SubscriptionPlanPreset("dropbox_plus", R.string.plan_standard, 45.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Coursera Plus",
            popularityWeight = 15,
            tagsRes = listOf(R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("coursera_plus_monthly", R.string.plan_standard, 235.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Brilliant.org",
            popularityWeight = 14,
            tagsRes = listOf(R.string.tag_education),
            plans = listOf(
                SubscriptionPlanPreset("brilliant_premium", R.string.plan_standard, 59.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Medicover Sport",
            popularityWeight = 12,
            tagsRes = listOf(R.string.tag_health),
            plans = listOf(
                SubscriptionPlanPreset("medicover_fit", R.string.plan_standard, 120.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Gymsteer",
            popularityWeight = 10,
            tagsRes = listOf(R.string.tag_health),
            plans = listOf(
                SubscriptionPlanPreset("gymsteer_standard", R.string.plan_standard, 50.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Tinder Gold",
            popularityWeight = 9,
            tagsRes = listOf(R.string.tag_lifestyle),
            plans = listOf(
                SubscriptionPlanPreset("tinder_gold_sub", R.string.plan_individual, 65.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Bumble Premium",
            popularityWeight = 8,
            tagsRes = listOf(R.string.tag_lifestyle),
            plans = listOf(
                SubscriptionPlanPreset("bumble_premium_monthly", R.string.plan_monthly, 59.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Wyborcza.pl",
            popularityWeight = 7,
            tagsRes = listOf(R.string.tag_news),
            plans = listOf(
                SubscriptionPlanPreset("wyborcza_premium", R.string.plan_standard, 19.90, "Miesiąc"),
                SubscriptionPlanPreset("wyborcza_club", R.string.plan_premium, 29.90, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Onet Premium",
            popularityWeight = 6,
            tagsRes = listOf(R.string.tag_news),
            plans = listOf(
                SubscriptionPlanPreset("onet_premium_sub", R.string.plan_individual, 19.90, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Newsweek Polska",
            popularityWeight = 5,
            tagsRes = listOf(R.string.tag_news),
            plans = listOf(
                SubscriptionPlanPreset("newsweek_digital", R.string.plan_standard, 25.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Polityka Cyfrowa",
            popularityWeight = 4,
            tagsRes = listOf(R.string.tag_news),
            plans = listOf(
                SubscriptionPlanPreset("polityka_digital", R.string.plan_standard, 29.00, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "TVP VOD",
            popularityWeight = 3,
            tagsRes = listOf(R.string.tag_entertainment),
            plans = listOf(
                SubscriptionPlanPreset("tvp_vod_strefa", R.string.plan_individual, 9.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "InPost Fresh",
            popularityWeight = 2,
            tagsRes = listOf(R.string.tag_shopping),
            plans = listOf(
                SubscriptionPlanPreset("inpost_fresh_sub", R.string.plan_individual, 9.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Headspace",
            popularityWeight = 1,
            tagsRes = listOf(R.string.tag_health),
            plans = listOf(
                SubscriptionPlanPreset("headspace_monthly", R.string.plan_monthly, 49.99, "Miesiąc")
            )
        ),
        ServicePreset(
            serviceName = "Flo Premium",
            popularityWeight = 0,
            tagsRes = listOf(R.string.tag_health),
            plans = listOf(
                SubscriptionPlanPreset("flo_monthly", R.string.plan_monthly, 39.99, "Miesiąc")
            )
        )
    )
}