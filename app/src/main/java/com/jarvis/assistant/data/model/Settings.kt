package com.jarvis.assistant.data.model

data class Settings(
    val id: String,
    val userId: String,
    val theme: Theme = Theme.SYSTEM,
    val language: Language = Language.ENGLISH,
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val autoSyncEnabled: Boolean = true,
    val analyticsEnabled: Boolean = true,
    val biometricEnabled: Boolean = false,
    val fontSize: Int = 14,
    val accentColor: String = "#6200EE",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun isDarkMode(): Boolean {
        return when (theme) {
            Theme.DARK -> true
            Theme.LIGHT -> false
            Theme.SYSTEM, Theme.AUTO -> darkModeEnabled
        }
    }

    fun isLightMode(): Boolean {
        return !isDarkMode()
    }

    fun getLanguageCode(): String {
        return language.code
    }

    fun getThemeName(): String {
        return theme.name.lowercase()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "theme" to theme.name,
            "language" to language.code,
            "notificationsEnabled" to notificationsEnabled,
            "darkModeEnabled" to darkModeEnabled,
            "autoSyncEnabled" to autoSyncEnabled,
            "analyticsEnabled" to analyticsEnabled,
            "biometricEnabled" to biometricEnabled,
            "fontSize" to fontSize,
            "accentColor" to accentColor,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Settings {
            return Settings(
                id = map["id"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                theme = try {
                    Theme.valueOf(map["theme"] as? String ?: "SYSTEM")
                } catch (e: Exception) {
                    Theme.SYSTEM
                },
                language = try {
                    Language.fromCode(map["language"] as? String ?: "en")
                } catch (e: Exception) {
                    Language.ENGLISH
                },
                notificationsEnabled = map["notificationsEnabled"] as? Boolean ?: true,
                darkModeEnabled = map["darkModeEnabled"] as? Boolean ?: false,
                autoSyncEnabled = map["autoSyncEnabled"] as? Boolean ?: true,
                analyticsEnabled = map["analyticsEnabled"] as? Boolean ?: true,
                biometricEnabled = map["biometricEnabled"] as? Boolean ?: false,
                fontSize = (map["fontSize"] as? Number)?.toInt() ?: 14,
                accentColor = map["accentColor"] as? String ?: "#6200EE",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }

        fun default(userId: String): Settings {
            return Settings(
                id = generateId(),
                userId = userId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }

        private fun generateId(): String {
            return "settings_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
        }
    }
}

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM,
    AUTO
}

enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Espanol"),
    FRENCH("fr", "Francais"),
    GERMAN("de", "Deutsch"),
    ITALIAN("it", "Italiano"),
    PORTUGUESE("pt", "Portugues"),
    RUSSIAN("ru", "Russkiy"),
    CHINESE("zh", "Zhongwen"),
    JAPANESE("ja", "Nihongo"),
    KOREAN("ko", "Hangugeo"),
    ARABIC("ar", "Arabiyyah"),
    HINDI("hi", "Hindi"),
    DUTCH("nl", "Nederlands"),
    POLISH("pl", "Polski"),
    TURKISH("tr", "Turkce"),
    SWEDISH("sv", "Svenska"),
    NORWEGIAN("no", "Norsk"),
    DANISH("da", "Dansk"),
    FINNISH("fi", "Suomi"),
    GREEK("el", "Ellinika"),
    CZECH("cs", "Cestina"),
    HUNGARIAN("hu", "Magyar"),
    ROMANIAN("ro", "Romana"),
    THAI("th", "Thai"),
    VIETNAMESE("vi", "Tieng Viet"),
    INDONESIAN("id", "Bahasa Indonesia"),
    MALAY("ms", "Bahasa Melayu"),
    FILIPINO("tl", "Filipino"),
    UKRAINIAN("uk", "Ukrainska"),
    HEBREW("iw", "Ivrit"),
    BULGARIAN("bg", "Bulgarski"),
    CROATIAN("hr", "Hrvatski"),
    SERBIAN("sr", "Srpski"),
    SLOVAK("sk", "Slovencina"),
    SLOVENE("sl", "Slovenscina"),
    LITHUANIAN("lt", "Lietuviu"),
    LATVIAN("lv", "Latviesu"),
    ESTONIAN("et", "Eesti"),
    ICELANDIC("is", "Islenska"),
    IRISH("ga", "Gaeilge"),
    WELSH("cy", "Cymraeg"),
    CATALAN("ca", "Catala"),
    BASQUE("eu", "Euskara"),
    GALICIAN("gl", "Galego"),
    ALBANIAN("sq", "Shqip"),
    MACEDONIAN("mk", "Makedonski"),
    BELARUSIAN("be", "Belaruskaya"),
    GEORGIAN("ka", "Kartuli"),
    ARMENIAN("hy", "Hayeren"),
    AZERBAIJANI("az", "Azerbaycanca"),
    KAZAKH("kk", "Qazaqsha"),
    UZBEK("uz", "Ozbekcha"),
    TURKMEN("tk", "Turkmence"),
    KYRGYZ("ky", "Kyrgyzcha"),
    TAJIK("tg", "Tojikii"),
    MONGOLIAN("mn", "Mongol"),
    NEPALI("ne", "Nepali"),
    SINHALA("si", "Sinhala"),
    TAMIL("ta", "Tamil"),
    TELUGU("te", "Telugu"),
    KANNADA("kn", "Kannada"),
    MALAYALAM("ml", "Malayalam"),
    MARATHI("mr", "Marathi"),
    GUJARATI("gu", "Gujarati"),
    PUNJABI("pa", "Punjabi"),
    BENGALI("bn", "Bengali"),
    URDU("ur", "Urdu"),
    PASHTO("ps", "Pashto"),
    KURDISH("ku", "KurdÃ®"),
    PERSIAN("fa", "Farsi"),
    SWAHILI("sw", "Kiswahili"),
    AFRIKAANS("af", "Afrikaans"),
    ZULU("zu", "isiZulu"),
    XHOSA("xh", "isiXhosa"),
    YORUBA("yo", "YorÃ¹bÃ¡"),
    IGBO("ig", "Igbo"),
    AMHARIC("am", "Amarinya"),
    HAUSA("ha", "Hausa"),
    SOMALI("so", "Soomaali"),
    TIGRINYA("ti", "Tigrinya"),
    OROMO("om", "Afaan Oromoo"),
    TWI("tw", "Twi"),
    EWE("ee", "Ewegbe"),
    FON("fon", "Fon"),
    WOLOF("wo", "Wolof"),
    BAMBARA("bm", "Bamanankan"),
    LINGALA("ln", "Lingala"),
    KINYARWANDA("rw", "Kinyarwanda"),
    KIRUNDI("rn", "Kirundi"),
    LUGANDA("lg", "Luganda"),
    TSWANA("tn", "Setswana"),
    SESOTHO("st", "Sesotho"),
    SHONA("sn", "chiShona"),
    NDEBELE("nd", "isiNdebele"),
    VENDA("ve", "Tshivenda"),
    TSONGA("ts", "Xitsonga"),
    SWAZI("ss", "siSwati"),
    CHEWA("ny", "Chichewa"),
    MALAGASY("mg", "Malagasy"),
    MAURITIAN_CREOLE("mfe", "Kreol Morisien"),
    SEYCHELLOIS_CREOLE("crs", "Kreol Seselwa"),
    HAITIAN_CREOLE("ht", "Kreyol Ayisyen"),
    JAMAICAN_PATOIS("jam", "Patwa"),
    TRINIDADIAN_CREOLE("trc", "Trini Creole"),
    BAJAN_CREOLE("bjs", "Bajan"),
    GUYANESE_CREOLE("gyn", "Guyanese Creole"),
    SURINAMESE_CREOLE("srn", "Sranan Tongo"),
    PAPIAMENTO("pap", "Papiamento"),
    PALENQUERO("plc", "Palenquero"),
    GARIFUNA("cab", "Garifuna"),
    MISKITO("miq", "Miskitu"),
    KUNA("kvn", "Kuna"),
    EMBERA("emp", "Embera"),
    WAUNANA("wna", "Waunana"),
    NASO("nso", "Naso Teribe"),
    BRIBRI("bzd", "Bribri"),
    CABECAR("cjp", "Cabecar"),
    BORUCA("brc", "Boruca"),
    TERRABA("tjo", "Teribe"),
    GUAYMI("gym", "Ngabere"),
    BUGLE("sac", "Buglere");
    companion object {
        fun fromCode(code: String): Language = values().find { it.code == code } ?: ENGLISH
    }
}
