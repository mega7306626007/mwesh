package com.jarvis.assistant.ui.translator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val sample: String
)

data class TranslationResult(
    val id: Long = System.nanoTime(),
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

data class PhrasebookEntry(
    val id: Int,
    val category: String,
    val english: String,
    val french: String,
    val swahili: String,
    val sheng: String
) {
    fun inLanguage(code: String): String = when (code) {
        "fr" -> french
        "sw" -> swahili
        "sh" -> sheng
        else -> english
    }
}

data class TranslatorUiState(
    val sourceLanguage: Language = Language("en", "English", "English", "Hello! How can I help you?"),
    val targetLanguage: Language = Language("sw", "Swahili", "Kiswahili", "Habari! Naweza kukusaidia vipi?"),
    val sourceText: String = "",
    val translatedText: String = "",
    val isTranslating: Boolean = false,
    val languages: List<Language> = emptyList(),
    val recentTranslations: List<TranslationResult> = emptyList(),
    val favoritesOnly: Boolean = false,
    val historyQuery: String = "",
    val detectedLanguage: Language? = null,
    val detectionConfidence: Float = 0f,
    val phrasebookCategory: String = "All",
    val phrasebookCategories: List<String> = emptyList(),
    val errorMessage: String? = null,
    val characterCount: Int = 0,
    val maxCharacters: Int = 500
)

class TranslatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TranslatorUiState())
    val uiState: StateFlow<TranslatorUiState> = _uiState.asStateFlow()

    init {
        loadLanguages()
        _uiState.value = _uiState.value.copy(
            phrasebookCategories = listOf("All") + PHRASEBOOK.map { it.category }.distinct()
        )
    }

    private fun loadLanguages() {
        val languages = listOf(
            Language("en", "English", "English", "Hello! How can I help you?"),
            Language("fr", "French", "Français", "Bonjour ! Comment puis-je vous aider ?"),
            Language("sw", "Swahili", "Kiswahili", "Habari! Naweza kukusaidia vipi?"),
            Language("sh", "Sheng", "Sheng", "Niaje! Naeza kukuhelp aje?")
        )
        _uiState.value = _uiState.value.copy(languages = languages)
    }

    fun onSourceTextChanged(text: String) {
        if (text.length <= _uiState.value.maxCharacters) {
            val current = _uiState.value
            val detection = if (text.isBlank()) null else detectLanguageInternal(text)
            _uiState.value = current.copy(
                sourceText = text,
                characterCount = text.length,
                translatedText = if (text.isBlank()) "" else current.translatedText,
                detectedLanguage = detection?.first,
                detectionConfidence = detection?.second ?: 0f
            )
        }
    }

    fun useDetectedLanguage() {
        val detected = _uiState.value.detectedLanguage ?: return
        _uiState.value = _uiState.value.copy(sourceLanguage = detected)
    }

    fun selectSourceLanguage(language: Language) {
        _uiState.value = _uiState.value.copy(sourceLanguage = language)
    }

    fun selectTargetLanguage(language: Language) {
        _uiState.value = _uiState.value.copy(targetLanguage = language)
    }

    fun swapLanguages() {
        val current = _uiState.value
        _uiState.value = current.copy(
            sourceLanguage = current.targetLanguage,
            targetLanguage = current.sourceLanguage,
            sourceText = current.translatedText,
            translatedText = current.sourceText
        )
    }

    fun translate() {
        val state = _uiState.value
        if (state.sourceText.isBlank()) return
        if (state.sourceLanguage.code == state.targetLanguage.code) {
            _uiState.value = state.copy(
                translatedText = state.sourceText.trim(),
                errorMessage = null
            )
            return
        }
        _uiState.value = state.copy(isTranslating = true, errorMessage = null)
        val result = translateInternal(
            state.sourceText,
            state.sourceLanguage.code,
            state.targetLanguage.code
        )
        val entry = TranslationResult(
            sourceText = state.sourceText.trim(),
            translatedText = result,
            sourceLanguage = state.sourceLanguage.name,
            targetLanguage = state.targetLanguage.name
        )
        _uiState.value = _uiState.value.copy(
            translatedText = result,
            isTranslating = false,
            recentTranslations = (listOf(entry) + _uiState.value.recentTranslations).take(100)
        )
    }

    fun toggleFavorite(id: Long) {
        _uiState.value = _uiState.value.copy(
            recentTranslations = _uiState.value.recentTranslations.map {
                if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
            }
        )
    }

    fun deleteHistoryItem(id: Long) {
        _uiState.value = _uiState.value.copy(
            recentTranslations = _uiState.value.recentTranslations.filter { it.id != id }
        )
    }

    fun reuseHistoryItem(item: TranslationResult) {
        val languages = _uiState.value.languages
        val source = languages.find { it.name == item.sourceLanguage } ?: _uiState.value.sourceLanguage
        val target = languages.find { it.name == item.targetLanguage } ?: _uiState.value.targetLanguage
        _uiState.value = _uiState.value.copy(
            sourceLanguage = source,
            targetLanguage = target,
            sourceText = item.sourceText,
            translatedText = item.translatedText,
            characterCount = item.sourceText.length
        )
    }

    fun setFavoritesOnly(favoritesOnly: Boolean) {
        _uiState.value = _uiState.value.copy(favoritesOnly = favoritesOnly)
    }

    fun onHistoryQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(historyQuery = query)
    }

    fun selectPhrasebookCategory(category: String) {
        _uiState.value = _uiState.value.copy(phrasebookCategory = category)
    }

    fun usePhrasebookEntry(entry: PhrasebookEntry) {
        val text = entry.inLanguage(_uiState.value.sourceLanguage.code)
        onSourceTextChanged(text)
    }

    fun translatePhrasebookEntry(entry: PhrasebookEntry) {
        val source = entry.inLanguage(_uiState.value.sourceLanguage.code)
        val target = entry.inLanguage(_uiState.value.targetLanguage.code)
        val result = TranslationResult(
            sourceText = source,
            translatedText = target,
            sourceLanguage = _uiState.value.sourceLanguage.name,
            targetLanguage = _uiState.value.targetLanguage.name
        )
        _uiState.value = _uiState.value.copy(
            sourceText = source,
            translatedText = target,
            characterCount = source.length,
            recentTranslations = (listOf(result) + _uiState.value.recentTranslations).take(100)
        )
    }

    fun visibleHistory(): List<TranslationResult> {
        val state = _uiState.value
        return state.recentTranslations
            .filter { !state.favoritesOnly || it.isFavorite }
            .filter {
                if (state.historyQuery.isBlank()) true
                else it.sourceText.contains(state.historyQuery, ignoreCase = true) ||
                        it.translatedText.contains(state.historyQuery, ignoreCase = true)
            }
    }

    fun visiblePhrasebook(): List<PhrasebookEntry> {
        val category = _uiState.value.phrasebookCategory
        return if (category == "All") PHRASEBOOK else PHRASEBOOK.filter { it.category == category }
    }

    fun favoriteCount(): Int = _uiState.value.recentTranslations.count { it.isFavorite }

    fun clearText() {
        _uiState.value = _uiState.value.copy(
            sourceText = "",
            translatedText = "",
            characterCount = 0,
            detectedLanguage = null,
            detectionConfidence = 0f
        )
    }

    fun clearHistory() {
        _uiState.value = _uiState.value.copy(recentTranslations = emptyList())
    }

    private fun translateInternal(text: String, from: String, to: String): String {
        val normalized = text.trim().lowercase().replace(Regex("\\s+"), " ")
        if (normalized.isEmpty()) return ""
        val pairKey = "$from-$to"
        EXACT_PHRASES[pairKey]?.get(normalized)?.let { return matchCase(text.trim(), it) }
        val words = normalized.split(" ")
        if (words.size == 1) {
            wordTranslate(words[0], from, to)?.let { return matchCase(text.trim(), it) }
            return text.trim()
        }
        return words.joinToString(" ") { word ->
            val clean = word.trim('.', ',', '!', '?', ';', ':')
            val translated = wordTranslate(clean, from, to) ?: clean
            val prefix = word.takeWhile { !it.isLetterOrDigit() }
            val suffix = word.takeLastWhile { !it.isLetterOrDigit() }
            prefix + translated + suffix
        }
    }

    private fun wordTranslate(word: String, from: String, to: String): String? {
        if (from == to) return word
        WORD_TABLES["$from-$to"]?.get(word)?.let { return it }
        val english = toEnglish(word, from) ?: return null
        if (to == "en") return english
        return WORD_TABLES["en-$to"]?.get(english)
    }

    private fun toEnglish(word: String, from: String): String? {
        if (from == "en") return word
        val table = WORD_TABLES["en-$from"] ?: return null
        return table.entries.find { it.value == word }?.key
            ?: table.entries.find { it.value == word.trimEnd('s') }?.key
    }

    private fun matchCase(original: String, translated: String): String {
        if (original.isEmpty()) return translated
        return when {
            original[0].isUpperCase() && original.all { it.isUpperCase() || !it.isLetter() } ->
                translated.uppercase()
            original[0].isUpperCase() -> translated.replaceFirstChar { it.uppercase() }
            else -> translated
        }
    }

    private fun detectLanguageInternal(text: String): Pair<Language, Float>? {
        val tokens = text.lowercase().split(Regex("[^a-zàâäéèêëîïôöùûüç']+")).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return null
        val languages = _uiState.value.languages.ifEmpty { return null }
        var best: Language? = null
        var bestScore = 0
        for (language in languages) {
            val keywords = DETECTION_KEYWORDS[language.code] ?: continue
            val score = tokens.count { it in keywords }
            if (score > bestScore) {
                bestScore = score
                best = language
            }
        }
        if (best == null || bestScore == 0) return null
        val confidence = (bestScore.toFloat() / tokens.size).coerceIn(0f, 1f)
        return best to confidence
    }

    companion object {
        private fun w(vararg pairs: Pair<String, String>): Map<String, String> = mapOf(*pairs)

        private val EN_SW = w(
            "hello" to "habari", "good" to "nzuri", "morning" to "asubuhi",
            "evening" to "jioni", "night" to "usiku", "thank you" to "asante",
            "please" to "tafadhali", "yes" to "ndiyo", "no" to "hapana",
            "water" to "maji", "food" to "chakula", "friend" to "rafiki",
            "house" to "nyumba", "car" to "gari", "money" to "pesa",
            "time" to "wakati", "day" to "siku", "today" to "leo",
            "tomorrow" to "kesho", "yesterday" to "jana", "man" to "mwanaume",
            "woman" to "mwanamke", "child" to "mtoto", "love" to "upendo",
            "help" to "msaada", "work" to "kazi", "school" to "shule",
            "market" to "soko", "road" to "barabara", "rain" to "mvua",
            "sun" to "jua", "phone" to "simu", "book" to "kitabu",
            "door" to "mlango", "window" to "dirisha", "dog" to "mbwa",
            "cat" to "paka"
        )

        private val EN_FR = w(
            "hello" to "bonjour", "good" to "bon", "morning" to "matin",
            "evening" to "soir", "night" to "nuit", "thank you" to "merci",
            "please" to "s'il vous plaît", "yes" to "oui", "no" to "non",
            "water" to "eau", "food" to "nourriture", "friend" to "ami",
            "house" to "maison", "car" to "voiture", "money" to "argent",
            "time" to "temps", "day" to "jour", "today" to "aujourd'hui",
            "tomorrow" to "demain", "yesterday" to "hier", "man" to "homme",
            "woman" to "femme", "child" to "enfant", "love" to "amour",
            "help" to "aide", "work" to "travail", "school" to "école",
            "market" to "marché", "road" to "route", "rain" to "pluie",
            "sun" to "soleil", "phone" to "téléphone", "book" to "livre",
            "door" to "porte", "window" to "fenêtre", "dog" to "chien",
            "cat" to "chat"
        )

        private val EN_SH = w(
            "hello" to "niaje", "good" to "poa", "morning" to "asubuhi",
            "evening" to "jioni", "night" to "usiku", "thank you" to "asante",
            "please" to "tafadhali", "yes" to "ndio", "no" to "zi",
            "water" to "maji", "food" to "chakula", "friend" to "buda",
            "house" to "keja", "car" to "nganya", "money" to "ganji",
            "time" to "saa", "day" to "siku", "today" to "leo",
            "tomorrow" to "kesho", "yesterday" to "jana", "man" to "msee",
            "woman" to "dame", "child" to "mtoi", "love" to "mapenzi",
            "help" to "msaada", "work" to "kazi", "school" to "shule",
            "market" to "soko", "road" to "baro", "rain" to "mvua",
            "sun" to "jua", "phone" to "simu", "book" to "buku",
            "door" to "mlango", "window" to "dirisha", "dog" to "mbwa",
            "cat" to "paka"
        )

        private val WORD_TABLES = mapOf(
            "en-sw" to EN_SW, "en-fr" to EN_FR, "en-sh" to EN_SH
        )

        private val EXACT_PHRASES = mapOf(
            "en-sw" to w(
                "how are you" to "habari yako", "good morning" to "habari za asubuhi",
                "good evening" to "habari za jioni", "thank you very much" to "asante sana",
                "see you later" to "tutaonana baadaye", "what is your name" to "jina lako nani",
                "my name is" to "jina langu ni", "where are you from" to "unatoka wapi",
                "i love you" to "nakupenda", "good night" to "usiku mwema",
                "welcome" to "karibu", "goodbye" to "kwa heri",
                "how much" to "bei gani", "i don't understand" to "sielewi",
                "speak slowly" to "ongea polepole", "where is the market" to "soko liko wapi",
                "have a nice day" to "uwe na siku njema", "congratulations" to "hongera"
            ),
            "sw-en" to w(
                "habari yako" to "how are you", "habari za asubuhi" to "good morning",
                "habari za jioni" to "good evening", "asante sana" to "thank you very much",
                "tutaonana baadaye" to "see you later", "jina lako nani" to "what is your name",
                "jina langu ni" to "my name is", "unatoka wapi" to "where are you from",
                "nakupenda" to "i love you", "usiku mwema" to "good night",
                "karibu" to "welcome", "kwa heri" to "goodbye",
                "bei gani" to "how much", "sielewi" to "i don't understand",
                "ongea polepole" to "speak slowly", "soko liko wapi" to "where is the market",
                "uwe na siku njema" to "have a nice day", "hongera" to "congratulations"
            ),
            "en-fr" to w(
                "how are you" to "comment vas-tu", "good morning" to "bonjour",
                "good evening" to "bonsoir", "thank you very much" to "merci beaucoup",
                "see you later" to "à plus tard", "what is your name" to "comment tu t'appelles",
                "my name is" to "je m'appelle", "where are you from" to "d'où viens-tu",
                "i love you" to "je t'aime", "good night" to "bonne nuit",
                "welcome" to "bienvenue", "goodbye" to "au revoir",
                "how much" to "combien ça coûte", "i don't understand" to "je ne comprends pas",
                "speak slowly" to "parle lentement", "where is the market" to "où est le marché",
                "have a nice day" to "bonne journée", "congratulations" to "félicitations"
            ),
            "fr-en" to w(
                "comment vas-tu" to "how are you", "bonjour" to "good morning",
                "bonsoir" to "good evening", "merci beaucoup" to "thank you very much",
                "à plus tard" to "see you later", "comment tu t'appelles" to "what is your name",
                "je m'appelle" to "my name is", "d'où viens-tu" to "where are you from",
                "je t'aime" to "i love you", "bonne nuit" to "good night",
                "bienvenue" to "welcome", "au revoir" to "goodbye",
                "combien ça coûte" to "how much", "je ne comprends pas" to "i don't understand",
                "parle lentement" to "speak slowly", "où est le marché" to "where is the market",
                "bonne journée" to "have a nice day", "félicitations" to "congratulations"
            ),
            "en-sh" to w(
                "how are you" to "mambo vipi", "good morning" to "niaje asubuhi",
                "thank you" to "asante buda", "see you later" to "tutaonana baadaye",
                "what is your name" to "jina ni nani", "where are you from" to "unatoka wapi",
                "good night" to "usiku mwema", "welcome" to "karibu",
                "how much" to "ngapi", "i don't understand" to "sielewi",
                "goodbye" to "kwa heri", "my friend" to "buda yangu",
                "let's go" to "twende", "no problem" to "hakuna noma",
                "well done" to "poa sana", "come here" to "kuja hapa",
                "wait a bit" to "ngoja kidogo", "i am hungry" to "naskia njaa"
            ),
            "sh-en" to w(
                "mambo vipi" to "how are you", "niaje asubuhi" to "good morning",
                "asante buda" to "thank you", "tutaonana baadaye" to "see you later",
                "jina ni nani" to "what is your name", "unatoka wapi" to "where are you from",
                "usiku mwema" to "good night", "karibu" to "welcome",
                "ngapi" to "how much", "sielewi" to "i don't understand",
                "kwa heri" to "goodbye", "buda yangu" to "my friend",
                "twende" to "let's go", "hakuna noma" to "no problem",
                "poa sana" to "well done", "kuja hapa" to "come here",
                "ngoja kidogo" to "wait a bit", "naskia njaa" to "i am hungry"
            )
        )

        private val DETECTION_KEYWORDS = mapOf(
            "en" to setOf("the", "is", "are", "you", "what", "how", "hello", "please", "thank", "with", "for", "have", "good", "morning", "are", "my", "name"),
            "fr" to setOf("le", "la", "les", "bonjour", "merci", "oui", "non", "avec", "pour", "comment", "est", "une", "des", "vous", "je", "bonne", "merci"),
            "sw" to setOf("habari", "asante", "tafadhali", "ndiyo", "hapana", "na", "ya", "za", "ni", "wapi", "nini", "yako", "langu", "sana", "karibu"),
            "sh" to setOf("niaje", "mambo", "poa", "buda", "vipi", "sasa", "mbwe", "mnoma", "kali", "dame", "keja", "ganji", "msee", "noma", "twende")
        )

        val PHRASEBOOK = listOf(
            PhrasebookEntry(1, "Greetings", "Hello", "Bonjour", "Habari", "Niaje"),
            PhrasebookEntry(2, "Greetings", "Good morning", "Bonjour", "Habari za asubuhi", "Niaje asubuhi"),
            PhrasebookEntry(3, "Greetings", "Good evening", "Bonsoir", "Habari za jioni", "Niaje jioni"),
            PhrasebookEntry(4, "Greetings", "How are you?", "Comment vas-tu ?", "Habari yako?", "Mambo vipi?"),
            PhrasebookEntry(5, "Courtesy", "Thank you", "Merci", "Asante", "Asante"),
            PhrasebookEntry(6, "Courtesy", "Thank you very much", "Merci beaucoup", "Asante sana", "Asante sana"),
            PhrasebookEntry(7, "Courtesy", "Please", "S'il vous plaît", "Tafadhali", "Tafadhali"),
            PhrasebookEntry(8, "Courtesy", "Welcome", "Bienvenue", "Karibu", "Karibu"),
            PhrasebookEntry(9, "Courtesy", "Goodbye", "Au revoir", "Kwa heri", "Kwa heri"),
            PhrasebookEntry(10, "Courtesy", "Congratulations", "Félicitations", "Hongera", "Hongera"),
            PhrasebookEntry(11, "Travel", "Where is the market?", "Où est le marché ?", "Soko liko wapi?", "Soko iko wapi?"),
            PhrasebookEntry(12, "Travel", "How much?", "Combien ça coûte ?", "Bei gani?", "Ngapi?"),
            PhrasebookEntry(13, "Travel", "Let's go", "Allons-y", "Twende", "Twende"),
            PhrasebookEntry(14, "Travel", "Where are you from?", "D'où viens-tu ?", "Unatoka wapi?", "Unatoka wapi?"),
            PhrasebookEntry(15, "Food", "I am hungry", "J'ai faim", "Nina njaa", "Naskia njaa"),
            PhrasebookEntry(16, "Food", "Water", "Eau", "Maji", "Maji"),
            PhrasebookEntry(17, "Time", "What time is it?", "Quelle heure est-il ?", "Ni saa ngapi?", "Ni saa ngapi?"),
            PhrasebookEntry(18, "Time", "See you later", "À plus tard", "Tutaonana baadaye", "Tutaonana baadaye"),
            PhrasebookEntry(19, "Time", "Good night", "Bonne nuit", "Usiku mwema", "Usiku mwema"),
            PhrasebookEntry(20, "Emergency", "Help!", "Au secours !", "Msaada!", "Msaada!"),
            PhrasebookEntry(21, "Emergency", "I don't understand", "Je ne comprends pas", "Sielewi", "Sielewi"),
            PhrasebookEntry(22, "Emergency", "Speak slowly", "Parle lentement", "Ongea polepole", "Ongea polepole"),
            PhrasebookEntry(23, "People", "My friend", "Mon ami", "Rafiki yangu", "Buda yangu"),
            PhrasebookEntry(24, "People", "What is your name?", "Comment tu t'appelles ?", "Jina lako nani?", "Jina ni nani?")
        )
    }
}
