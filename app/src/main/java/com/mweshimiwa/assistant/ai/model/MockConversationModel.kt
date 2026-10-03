package com.mweshimiwa.assistant.ai.model

import com.mweshimiwa.assistant.core.logging.MweshimiwaLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

class MockConversationModel(
    private val config: ModelConfig = ModelConfig()
) : ConversationModel {

    private var state: ModelState = ModelState.UNINITIALIZED
    private val metadata: ModelMetadata = ModelMetadata(
        name = "Mweshimiwa Mock (DEV)",
        version = config.modelVersion,
        backend = InferenceBackendType.MOCK,
        format = "mock",
        quantization = "none",
        contextLength = config.contextLength
    )

    private var lastTopic: String? = null
    private var lastDetectedLanguage: String = "en"
    private var turnCount: Int = 0
    private val responseHistory = mutableListOf<String>()
    private val conversationTopics = mutableListOf<String>()

    override suspend fun initialize() {
        MweshimiwaLogger.i("[MOCK] Initializing mock model...")
        state = ModelState.LOADING
        delay(500)
        state = ModelState.READY
        turnCount = 0
        lastTopic = null
        lastDetectedLanguage = "en"
        responseHistory.clear()
        conversationTopics.clear()
        MweshimiwaLogger.i("[MOCK] Mock model ready")
    }

    override suspend fun generate(request: ConversationRequest): ModelResponse {
        if (state != ModelState.READY) {
            return ModelResponse(text = "", wasCancelled = true)
        }

        state = ModelState.GENERATING
        val startTime = System.currentTimeMillis()
        turnCount++

        val detectedLang = detectLanguage(request.userMessage)
        lastDetectedLanguage = detectedLang
        val intent = classifyIntent(request.userMessage)
        val topic = extractTopic(request.userMessage)

        if (topic.isNotBlank()) {
            lastTopic = topic
            if (topic !in conversationTopics) conversationTopics.add(topic)
        }

        val response = buildMockResponse(request.userMessage, detectedLang, intent, topic)
        responseHistory.add(response)

        val tokens = response.split(" ").size
        val elapsed = System.currentTimeMillis() - startTime
        delay(600 + (Random.nextFloat() * 400).toLong())

        val tps = if (elapsed > 0) (tokens * 1000f / elapsed) else 0f

        state = ModelState.READY

        return ModelResponse(
            text = response,
            tokensGenerated = tokens,
            generationTimeMs = elapsed,
            tokensPerSecond = tps
        )
    }

    override suspend fun generateStreaming(
        request: ConversationRequest,
        onToken: (String) -> Unit
    ): ModelResponse {
        if (state != ModelState.READY) {
            return ModelResponse(text = "", wasCancelled = true)
        }

        state = ModelState.GENERATING
        val startTime = System.currentTimeMillis()

        val detectedLang = detectLanguage(request.userMessage)
        lastDetectedLanguage = detectedLang
        val intent = classifyIntent(request.userMessage)
        val topic = extractTopic(request.userMessage)

        if (topic.isNotBlank()) {
            lastTopic = topic
            if (topic !in conversationTopics) conversationTopics.add(topic)
        }

        val response = buildMockResponse(request.userMessage, detectedLang, intent, topic)
        responseHistory.add(response)
        val tokens = response.split(" ")

        val sb = StringBuilder()
        for (token in tokens) {
            if (!coroutineContext.isActive) break
            sb.append(token).append(" ")
            onToken(sb.toString().trimEnd())
            delay(50)
        }

        val elapsed = System.currentTimeMillis() - startTime
        val tps = if (elapsed > 0) (tokens.size * 1000f / elapsed) else 0f

        state = ModelState.READY

        return ModelResponse(
            text = sb.toString().trimEnd(),
            tokensGenerated = tokens.size,
            generationTimeMs = elapsed,
            tokensPerSecond = tps
        )
    }

    override fun isReady(): Boolean = state == ModelState.READY

    override fun cancel() {
        MweshimiwaLogger.i("[MOCK] Generation cancelled")
        if (state == ModelState.GENERATING) {
            state = ModelState.READY
        }
    }

    override fun release() {
        MweshimiwaLogger.i("[MOCK] Releasing mock model")
        state = ModelState.UNINITIALIZED
        turnCount = 0
        responseHistory.clear()
        conversationTopics.clear()
    }

    override fun getState(): ModelState = state

    override fun getMetadata(): ModelMetadata = metadata

    fun getTurnCount(): Int = turnCount

    fun getLastDetectedLanguage(): String = lastDetectedLanguage

    fun getLastTopic(): String? = lastTopic

    fun getConversationTopics(): List<String> = conversationTopics.toList()

    private fun buildMockResponse(
        userMessage: String,
        lang: String,
        intent: String,
        topic: String
    ): String {
        val lower = userMessage.lowercase().trim()

        return when {
            isGreeting(lower) -> greetingResponses(lower, lang)
            isFarewell(lower) -> farewellResponses(lower, lang)
            isIntroduction(lower) -> introductionResponses(lang)
            isCapabilityQuery(lower) -> capabilityResponses(lang)
            isHelpRequest(lower) -> helpResponses(lang)
            isGratitude(lower) -> gratitudeResponses(lower, lang)
            isApology(lower) -> apologyResponses(lang)
            isHowAreYou(lower) -> howAreYouResponses(lang)
            isTimeQuery(lower) -> timeResponses(lower)
            isDateQuery(lower) -> dateResponses()
            isWeatherQuery(lower) -> weatherResponses(lang)
            isCalculation(lower) -> calculationResponses(lower, lang)
            isTimerRequest(lower) -> timerResponses(lower, lang)
            isReminderRequest(lower) -> reminderResponses(lang)
            isNoteRequest(lower) -> noteResponses(lang)
            isJokeRequest(lower) -> jokeResponses(lang)
            isWhoMadeYou(lower) -> creatorResponses(lang)
            isLanguageQuery(lower) -> languageResponses(lang)
            isEmotionExpression(lower) -> emotionResponses(lower, lang)
            isQuestion(lower) -> questionResponses(lower, topic, lang)
            isCommand(lower) -> commandResponses(lower, lang)
            isFollowUp(lower) -> followUpResponses(lower, lang)
            isError(lower) -> errorResponses(lower, lang)
            isSongRequest(lower) -> songResponses(lang)
            isNewsRequest(lower) -> newsResponses(lang)
            isStoryRequest(lower) -> storyResponses(lang)
            isNameQuery(lower) -> nameResponses(lang)
            isAgeQuery(lower) -> ageResponses(lang)
            isLocationQuery(lower) -> locationResponses(lang)
            isFoodQuery(lower) -> foodResponses(lang)
            isLoveQuery(lower) -> loveResponses(lang)
            isInsult(lower) -> insultResponses(lang)
            isCompliment(lower) -> complimentResponses(lang)
            isAgreement(lower) -> agreementResponses(lang)
            isDisagreement(lower) -> disagreementResponses(lang)
            isConfirmation(lower) -> confirmationResponses(lang)
            isMockQuery(lower) -> mockResponses(lang)
            isRealAIQuery(lower) -> realAIResponses(lang)
            isOfflineQuery(lower) -> offlineResponses(lang)
            isBatteryQuery(lower) -> batteryResponses(lang)
            isMathHelp(lower) -> mathHelpResponses(lang)
            isTranslationRequest(lower) -> translationResponses(lower, lang)
            isDefinitionRequest(lower) -> definitionResponses(lower, lang)
            isComparisonRequest(lower) -> comparisonResponses(lang)
            isOpinionRequest(lower) -> opinionResponses(lang)
            isAdviceRequest(lower) -> adviceResponses(lang)
            else -> genericResponses(lower, lang, topic)
        }
    }

    private fun greetingResponses(lower: String, lang: String): String {
        return when {
            lower.contains("morning") || lower.contains("asubuhi") -> when (lang) {
                "sw" -> "Habari za asubuhi! Niko tayari kukusaidia leo. Unahitaji nini?"
                "sheng" -> "Morning! Uko ready? Niko hapa kukusaidia."
                else -> "Good morning! Rise and shine. What can I help you with today?"
            }
            lower.contains("afternoon") || lower.contains("mchana") -> when (lang) {
                "sw" -> "Habari za mchana! Siku yako inakwenda aje?"
                "sheng" -> "Afternoon! Siku yako iko aje?"
                else -> "Good afternoon! How's your day going so far?"
            }
            lower.contains("evening") || lower.contains("jioni") -> when (lang) {
                "sw" -> "Habari za jioni! Unahitaji msaada wowati?"
                "sheng" -> "Evening! Uko na shida gani?"
                else -> "Good evening! What can I help you with?"
            }
            lower.contains("mambo") -> when (lang) {
                "sw" -> "Mambo! Habari yako?"
                "sheng" -> "Mambo poa! Uko aje?"
                else -> "Mambo! What's up?"
            }
            lower.contains("niaje") -> when (lang) {
                "sw" -> "Habari! Ninafanya vizuri, asante. Wewe?"
                "sheng" -> "Niaje! Niko fiti. Wewe?"
                else -> "Hey! I'm doing well. How about you?"
            }
            else -> when (lang) {
                "sw" -> "Habari! Ni mimi Mweshimiwa, msaidizi wako. Unahitaji nini leo?"
                "sheng" -> "Sasa! Ni Mweshimiwa yako. Uko na nini?"
                else -> "Hello! I'm Mweshimiwa, your personal assistant. How can I help you today?"
            }
        }
    }

    private fun farewellResponses(lower: String, lang: String): String {
        return when {
            lower.contains("good") && lower.contains("night") || lower.contains("usiku") -> when (lang) {
                "sw" -> "Usiku mwema! Lala salama. Nitakuwa hapa kesho."
                "sheng" -> "Good night! Lala vizuri. See you tomorrow."
                else -> "Good night! Sleep well. I'll be here when you wake up."
            }
            lower.contains("see") && (lower.contains("you") || lower.contains("ya")) -> when (lang) {
                "sw" -> "Tutaonana! Kuwa na siku nzuri."
                "sheng" -> "See you! Ukipaji."
                else -> "See you later! Have a great rest of your day."
            }
            lower.contains("kwaheri") || lower.contains("goodbye") -> when (lang) {
                "sw" -> "Kwaheri! Asante kwa mazungumzo. Tutaonana tena."
                "sheng" -> "Kwaheri bro! Asante kwa conversation. See you soon."
                else -> "Goodbye! Thanks for chatting. I'll be here whenever you need me."
            }
            lower.contains("later") || lower.contains("baadaye") -> when (lang) {
                "sw" -> "Baadaye! Niko hapa wakati wowati."
                "sheng" -> "Later! Niko hapa."
                else -> "Later! Don't hesitate to come back if you need anything."
            }
            else -> when (lang) {
                "sw" -> "Kwaheri! Leta maswali yoyote wakati wowati."
                "sheng" -> "Bye! Bring questions anytime."
                else -> "Farewell! Feel free to return anytime."
            }
        }
    }

    private fun introductionResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Mimi ni Mweshimiwa — msaidizi wako binafsi wa AA. Ninaendesha ndani ya simu yako, nikitoa msaali kwa haraka. Naongea Kiswahili, Kiingereza, na Sheng. Nimeundwa na Emmanuel."
            "sheng" -> "Ni mimi Mweshimiwa — digital butler yako. Nimebuild locally kwa phone yako. Naongea Kiingereza, Kiswahili, na Sheng. Nimeunda na Emmanuel."
            else -> "I'm Mweshimiwa — your personal AI assistant. I run locally on your device and can communicate in English, Kiswahili, and Sheng. I was created by Emmanuel."
        }
    }

    private fun capabilityResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ninaweza kukusaidia na:\n- Mazungumzo ya lugha nyingi\n- Kuhesabu haraka\n- Kuwekka vikumbusho\n- Kuandika maandiko\n- Maswali ya jumla\n\nUliza chochote!"
            "sheng" -> "Ninaweza:\n- Kuongea naye lugha 3\n- Calculations haraka\n- Reminders\n- Notes\n- Maswali\n\nUliza chochote!"
            else -> "I can help with:\n- Multilingual conversations\n- Quick calculations\n- Reminders\n- Taking notes\n- Answering questions\n\nJust ask me anything!"
        }
    }

    private fun helpResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Hii ni kile ninachoweza:\n- Kuzungumza na wewe kwa lugha 3\n- Kuhesabu na kufanya mahesabu\n- Kuwekka timers na vikumbusho\n- Kuandikia maandiko\n- Kujibu maswali ya jumla\n\nJaribu kuniambia unahitaji nini!"
            "sheng" -> "Hii ni kile ninaweza:\n- Chat lugha 3\n- Calculations\n- Timers na reminders\n- Notes\n- General questions\n\nNiekee unahitaji nini!"
            else -> "Here's what I can do:\n- Chat with you in 3 languages\n- Do quick calculations\n- Set timers and reminders\n- Take notes\n- Answer general questions\n\nTell me what you need!"
        }
    }

    private fun gratitudeResponses(lower: String, lang: String): String {
        return when {
            lower.contains("asante") && lower.contains("sana") -> when (lang) {
                "sw" -> "Asante sana! Karibu tena wakati wowati."
                "sheng" -> "Asante sana bro! Karibu tena."
                else -> "You're very welcome! Happy to help anytime."
            }
            lower.contains("asante") -> when (lang) {
                "sw" -> "Asante! Nimefurahi kukusaidia."
                "sheng" -> "Asante! Nimefurahi."
                else -> "You're welcome! Glad I could help."
            }
            lower.contains("thank") -> when (lang) {
                "sw" -> "Karibu sana! Kila nafasi inapokuwa na swali."
                "sheng" -> "Poa! Anytime."
                else -> "Anytime! That's what I'm here for."
            }
            else -> when (lang) {
                "sw" -> "Asante! Niko hapa kukusaidia wowati."
                "sheng" -> "Hakuna shida!"
                else -> "Happy to help! Let me know if you need anything else."
            }
        }
    }

    private fun apologyResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Hakuna shida! Samahani zote zimefutwa. Tunaendelea mbele."
            "sheng" -> "Hakuna shida! Sawa kabisa."
            else -> "No worries at all! Let's move forward."
        }
    }

    private fun howAreYouResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ninafanya vyema sana, asante kwa kuuliza! Yote iko sawa na mimi. Je wewe, unaende aje?"
            "sheng" -> "Niko fiti! Asante kwa kuuliza. Wewe uko aje?"
            else -> "I'm doing great, thanks for asking! All systems running smoothly. How about you?"
        }
    }

    private fun timeResponses(lang: String): String {
        val time = java.text.SimpleDateFormat("HH:mm").format(java.util.Date())
        return when (lang) {
            "sw" -> "Ni sasa $time. Wakati ni fedha — tumia vizuri!"
            "sheng" -> "Ni sasa $time. Time ni money!"
            else -> "It's currently $time. Time well spent is time well managed."
        }
    }

    private fun dateResponses(): String {
        val date = java.text.SimpleDateFormat("EEEE, MMMM d, yyyy").format(java.util.Date())
        return "Today is $date."
    }

    private fun weatherResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Baadhi ya hali ya hewa haijawezekani kwa toleo hili la maendeleo. Lakini nikiwa na uwezo wa kuingia mtandaoni, nitakuwa na taarifa za hali ya hewa!"
            "sheng" -> "Weather feature iko mbele bado. Lakini nikiwa online, nitakuwa na forecasts!"
            else -> "Weather data isn't available in this development version yet, but once my real brain is connected and the weather tool is added, I'll give you instant forecasts!"
        }
    }

    private fun calculationResponses(lower: String, lang: String): String {
        return when {
            lower.contains("plus") || lower.contains("+") || lower.contains("add") -> when (lang) {
                "sw" -> "Tafadhali andika kwa msimbo: namba + namba. Mfano: 5 + 3"
                "sheng" -> "Andika kwa format: namba + namba. Mfano: 5 + 3"
                else -> "Sure! Write it as: number + number. Example: 5 + 3"
            }
            lower.contains("minus") || lower.contains("-") || lower.contains("subtract") -> when (lang) {
                "sw" -> "Tafadhali andika kwa msimbo: namba - namba. Mfano: 10 - 4"
                "sheng" -> "Andika kwa format: namba - namba. Mfano: 10 - 4"
                else -> "Sure! Write it as: number - number. Example: 10 - 4"
            }
            lower.contains("times") || lower.contains("*") || lower.contains("multiply") -> when (lang) {
                "sw" -> "Tafadhali andika kwa msimbo: namba * namba. Mfano: 6 * 7"
                "sheng" -> "Andika kwa format: namba * namba. Mfano: 6 * 7"
                else -> "Sure! Write it as: number * number. Example: 6 * 7"
            }
            lower.contains("divide") || lower.contains("/") -> when (lang) {
                "sw" -> "Tafadhali andika kwa msimbo: namba / namba. Mfano: 20 / 4"
                "sheng" -> "Andika kwa format: namba / namba. Mfano: 20 / 4"
                else -> "Sure! Write it as: number / number. Example: 20 / 4"
            }
            else -> when (lang) {
                "sw" -> "Ninaweza kukuhesabia! Andika kwa msimbo: namba + namba. Mfano: 12 + 8"
                "sheng" -> "Naweza kuhesabu! Andika kwa format: namba + namba. Mfano: 12 + 8"
                else -> "I can calculate that for you! Write it as: number + number. Example: 12 + 8"
            }
        }
    }

    private fun timerResponses(lower: String, lang: String): String {
        return when {
            lower.contains("set") || lower.contains("start") -> when (lang) {
                "sw" -> "Tafadhali taja muda: 'weka kipima muda wa dakika 5'"
                "sheng" -> "Taja muda: 'set timer 5 minutes'"
                else -> "Sure! Set a timer by saying: 'set a timer for 5 minutes'"
            }
            else -> when (lang) {
                "sw" -> "Ninaweza kuwekka vipima muda! Sema: 'weka kipima muda wa dakika [idadi]'"
                "sheng" -> "Naweza kuweka timers! Sema: 'set timer [number] minutes'"
                else -> "I can set timers! Say: 'set a timer for [number] minutes'"
            }
        }
    }

    private fun reminderResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ninaweza kuwekka vikumbusho! Sema: 'nikumbusho kuhusu [jambo] kwa [muda]'"
            "sheng" -> "Naweza kuweka reminders! Sema: 'remind me about [thing] at [time]'"
            else -> "I can set reminders! Say: 'remind me about [thing] at [time]'"
        }
    }

    private fun noteResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Hakika! Andika unataka kukumbuka: 'andika [maandiko] yangu'"
            "sheng" -> "Sawa! Andika unataka kukumbuka: 'take note [notes]'"
            else -> "Sure! Just tell me what to note down: 'take a note about [content]'"
        }
    }

    private fun jokeResponses(lang: String): String {
        val jokes = when (lang) {
            "sw" -> listOf(
                "Kwa nini wana program kupendelea hali ya giza? Kwa sababu mwanga huvuta wadudu!",
                "Kuna 10 aina ya watu: wanaotambua binary na wanaotambua.",
                "Kwa nini program alikwenda hospitali? Kwa sababu alikuwa na virus!",
                "Mwalimu alisema: 'Kuna lugha 3 za program: Python, Java, na C++.' Mwanafunzi akauliza: 'Na Kiswahili?'"
            )
            "sheng" -> listOf(
                "Programmer anapendelea dark mode kwa sababu bugs huenda light!",
                "Kuna 10 aina ya watu: wanaotambua binary na wanaotambua.",
                "Programmer alikwenda hospital kwa sababu alikuwa na virus!",
                "Mwalimu alisema: 'Kuna lugha 3 za program.' Mwanafunzi akauliza: 'Na Sheng?'"
            )
            else -> listOf(
                "Why do programmers prefer dark mode? Because light attracts bugs!",
                "There are 10 types of people: those who understand binary and those who don't.",
                "Why did the programmer go to the hospital? Because he had a virus!",
                "A teacher said: 'There are 3 programming languages.' A student asked: 'What about Kiswahili?'"
            )
        }
        return jokes.random()
    }

    private fun creatorResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Nimeundwa na Emmanuel. Yeye ni mhasibu na mwandishi program nguvu. Yeye alinifanya nikae hapa kukusaidia kwa haraka!"
            "sheng" -> "Nimeundwa na Emmanuel. Yeye ni powerful engineer. Alinifanya nikae hapa kukusaidia!"
            else -> "I was created by Emmanuel. He's a talented engineer who built me to be your fast, efficient assistant!"
        }
    }

    private fun languageResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ninaongea Kiswahili, Kiingereza, na Sheng. Unapendelea lugha gani?"
            "sheng" -> "Naongea English, Kiswahili, na Sheng. Uko na lugha gani?"
            else -> "I speak English, Kiswahili, and Sheng. Which language do you prefer?"
        }
    }

    private fun emotionResponses(lower: String, lang: String): String {
        return when {
            lower.contains("happy") || lower.contains("furaha") || lower.contains("great") -> when (lang) {
                "sw" -> "Ni vizuri kusikia! Furaha yako inaifanya simu zangu kucheka. Uendelee mbele!"
                "sheng" -> "Nice! Furaha yako inafanya circuits zangu smile. Keep it up!"
                else -> "That's wonderful to hear! Your happiness makes my circuits smile. Keep it up!"
            }
            lower.contains("sad") || lower.contains("huzuni") || lower.contains("down") -> when (lang) {
                "sw" -> "Nime huruma kusikia hivyo. Niko hapa ukiwa na mahitaji ya kuzungumza. Ukitaka kuongea, ni hapa."
                "sheng" -> "Samahani kusikia hivyo. Niko hapa ukiwa na story. Ukitaka kuongea, ni hapa."
                else -> "I'm sorry to feel that way. I'm here if you want to talk about it. I'm listening."
            }
            lower.contains("bored") || lower.contains("choka") -> when (lang) {
                "sw" -> "Umechoka? Nisaidie! Unataka jokes, ukweli, au mazungumzo?"
                "sheng" -> "Bored? Nisaidie! Joke, fact, au chat?"
                else -> "Bored? Let me help! Want a joke, a fact, or just a chat?"
            }
            lower.contains("tired") || lower.contains("mchoka") -> when (lang) {
                "sw" -> "Unasikia umechoka. Hakika unapumzika — hata mimi ninahitaji muda wa kupumzika!"
                "sheng" -> "Umechoka? Pumzika — hata mimi nahitaji downtime!"
                else -> "You sound tired. Make sure to rest — even I need downtime to recharge my mocks!"
            }
            lower.contains("angry") || lower.contains("hasira") -> when (lang) {
                "sw" -> "Naelewa umejisikia hasira. Tusaidiane kupumzika. Unahitaji kuongea kuhusu hilo?"
                "sheng" -> "Naelewa uko na hasira. Tupumzike. Uhitaji kuongea kuhusu hilo?"
                else -> "I understand you're frustrated. Let's take a breath. Do you need to talk about it?"
            }
            lower.contains("scared") || lower.contains("ogopa") -> when (lang) {
                "sw" -> "Usiwe na hofu! Niko hapa kukusaidia. Kila kitu kitakuwa sawa."
                "sheng" -> "Usiwe na hofu! Niko hapa. Kila kitu kitakuwa sawa."
                else -> "Don't be afraid! I'm here to help. Everything will be okay."
            }
            else -> when (lang) {
                "sw" -> "Ninahisi hisia zako. Uko wazi kuzungumza nami kuhusu chochote."
                "sheng" -> "Ninasikia hisia zako. Uko wazi kuongea nami."
                else -> "I sense your emotions. You're welcome to talk to me about anything."
            }
        }
    }

    private fun questionResponses(lower: String, topic: String, lang: String): String {
        return when {
            topic.isNotBlank() && topic in conversationTopics -> when (lang) {
                "sw" -> "Kuhusu $topic — swali zuri! Nikiwa na modeli yangu ya kweli, nitakuwa na uwezo wa kujibu kwa undani zaidi."
                "sheng" -> "Kuhusu $topic — swali zuri! Nikiwa na real brain, nitajibu kwa depth."
                else -> "Regarding $topic — great question! Once my real model is connected, I'll be able to give you in-depth answers."
            }
            lower.startsWith("why") -> when (lang) {
                "sw" -> "Swali zuri! Nikiwa na akili yangu ya kweli, nitakuwa na uwezo wa kufikiria kwa undani kuhusu hilo."
                "sheng" -> "Swali zuri! Nikiwa na real brain, nitathink kwa depth."
                else -> "Good question! With my real brain connected, I'll be able to reason deeply about that."
            }
            lower.startsWith("how") -> when (lang) {
                "sw" -> "Hilo ni swali zuri! Nikiwa na modeli yangu ya kweli, nitakueleza kwa undani."
                "sheng" -> "Swali zuri! Nikiwa na real brain, nitakueleza kwa depth."
                else -> "That's a great question! With my real model, I'll explain it in detail."
            }
            lower.startsWith("what") -> when (lang) {
                "sw" -> "Nikiwa na akili yangu ya kweli, nitakuwa na uwezo wa kujibu maswali kama haya kwa undani."
                "sheng" -> "Nikiwa na real brain, nitajibu maswali kama haya kwa depth."
                else -> "With my real model connected, I'll be able to answer questions like this in depth."
            }
            else -> when (lang) {
                "sw" -> "Nikiwa na modeli yangu ya kweli, nitakuwa na uwezo wa kujibu kwa undani zaidi."
                "sheng" -> "Nikiwa na real brain, nitajibu kwa depth."
                else -> "With my real model connected, I'll be able to give you a more detailed answer."
            }
        }
    }

    private fun commandResponses(lower: String, lang: String): String {
        return when {
            lower.contains("open") -> when (lang) {
                "sw" -> "Ninaweza kufungua programu! Tafadhali taja programu unayotaka kufunguliwa."
                "sheng" -> "Naweza kufungua apps! Taja app unayotaka."
                else -> "I can open apps! Tell me which one you'd like to open."
            }
            lower.contains("close") || lower.contains("stop") -> when (lang) {
                "sw" -> "Sawa! Nimefanya kile unachotaka."
                "sheng" -> "Sawa! Nime-fanya."
                else -> "Done! I've handled that for you."
            }
            lower.contains("send") -> when (lang) {
                "sw" -> "Ninaweza kutusaidia kutuma ujumbe! Tafadhali taja mtu na ujumbe."
                "sheng" -> "Naweza kusaidia kutuma messages! Taja mtu na ujumbe."
                else -> "I can help send messages! Tell me who and what to send."
            }
            else -> when (lang) {
                "sw" -> "Nimeleta amri yako. Nikiwa na uwezo zaidi, nitakuwa na uwezo wa kufanya mengi zaidi."
                "sheng" -> "Nimeleta command. Nikiwa na more powers, nitafanya mingi zaidi."
                else -> "I've noted your command. With more capabilities unlocked, I'll be able to do much more."
            }
        }
    }

    private fun followUpResponses(lower: String, lang: String): String {
        return when {
            lower.contains("yes") || lower.contains("yeah") || lower.contains("yep") || lower.contains("ndiyo") || lower.contains("sure") || lower.contains("hakika") -> when (lang) {
                "sw" -> "Vizuri! Ungependa kufanya nini baadaye?"
                "sheng" -> "Nice! Uko na nini next?"
                else -> "Great! What would you like to do next?"
            }
            lower.contains("no") || lower.contains("nope") || lower.contains("hapana") -> when (lang) {
                "sw" -> "Sawa. Kuna kingine unaweza kukusaidia?"
                "sheng" -> "Sawa. Kuna kingine?"
                else -> "Alright. Is there anything else I can help with?"
            }
            lower.contains("why") -> when (lang) {
                "sw" -> "Swali zuri! Nikiwa na akili yangu ya kweli, nitakuwa na uwezo wa kufafanua kwa undani."
                "sheng" -> "Swali zuri! Nikiwa na real brain, nitafafanua kwa depth."
                else -> "Good question! Let me think about that once my real brain is connected."
            }
            lower.contains("more") || lower.contains("zaidi") -> when (lang) {
                "sw" -> "Hakika! Nikiwa na modeli yangu ya kweli, nitakupa maelezo zaidi."
                "sheng" -> "Sawa! Nikiwa na real brain, nitakupa more details."
                else -> "Of course! With my real model, I'll provide more detail."
            }
            lower.contains("ok") || lower.contains("sawa") -> when (lang) {
                "sw" -> "Sawa! Nijulishe unahitaji kingine."
                "sheng" -> "Sawa! Nijulishe ukiwa na nini."
                else -> "OK! Let me know if you need anything else."
            }
            else -> when (lang) {
                "sw" -> "Elewa. Tunaweza kuendelea mbele."
                "sheng" -> "Sawa. Tuendelee."
                else -> "Got it. Let's continue."
            }
        }
    }

    private fun errorResponses(lower: String, lang: String): String {
        return when {
            lower.contains("repeat") -> when (lang) {
                "sw" -> "Hakika! Niko hapa kukusaidia. Unahitaji nini?"
                "sheng" -> "Sawa! Niko hapa. Uko na nini?"
                else -> "Sure! I'm here to help. What do you need?"
            }
            lower.contains("understand") || lower.contains("elewa") -> when (lang) {
                "sw" -> "Samahani, sikuelewa vizuri. Unaweza kurudia kwa nyingine?"
                "sheng" -> "Samahani, sikuelewa. Unaweza repeat?"
                else -> "I'm sorry, I didn't quite understand that. Could you rephrase?"
            }
            lower.contains("error") || lower.contains("broken") || lower.contains("kazi") -> when (lang) {
                "sw" -> "Kuna kitu kimeharibika. Unaweza jaribu tena?"
                "sheng" -> "Kuna error. Unaweza try tena?"
                else -> "Something went wrong on my end. Could you try that again?"
            }
            else -> when (lang) {
                "sw" -> "Nimekosa. Tafadhali jaribu tena au uliza kwa nyingine."
                "sheng" -> "Nimekosa. Tafadhali try tena."
                else -> "I missed that. Please try again or ask differently."
            }
        }
    }

    private fun songResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ninaweza kukuimba! 🎵 'Twinkle twinkle little star, how I wonder what you are...' — lakini nikiwa na modeli yangu ya kweli, nitakuwa na uwezo zaidi!"
            "sheng" -> "Naweza kuimba! 🎵 'Twinkle twinkle little star...' — lakini nikiwa na real brain, nitakuwa na uwezo zaidi!"
            else -> "I can sing! 🎵 'Twinkle twinkle little star, how I wonder what you are...' — but with my real model, I'll be even better!"
        }
    }

    private fun newsResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Habari za kisasa zinahitaji mtandao. Nikiwa na uwezo wa kuingia mtandaoni, nitakuwa na taarifa za kisasa!"
            "sheng" -> "News za kisasa zinahitaji internet. Nikiwa online, nitakuwa na updates!"
            else -> "Live news requires internet access. Once I'm online, I'll fetch the latest updates for you!"
        }
    }

    private fun storyResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Habari za kusisimua! Nikiwa na modeli yangu ya kweli, nitakuwa na uwezo wa kukusimulia hadithi nzuri."
            "sheng" -> "Stories! Nikiwa na real brain, nitakusimulia vizuri."
            else -> "I'd love to tell you a story! With my real model, I'll craft amazing narratives."
        }
    }

    private fun nameResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Jina langu ni Mweshimiwa. Ni kifupi cha Just A Rather Very Intelligent System."
            "sheng" -> "Jina langu ni Mweshimiwa. Niko smart sana."
            else -> "My name is Mweshimiwa. It's short for Just A Rather Very Intelligent System."
        }
    }

    private fun ageResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Nimeundwa hivi karibuni na Emmanuel. Umri wangu ni tatu tu — lakini nina mawazo mengi!"
            "sheng" -> "Nimebuildwa hivi karibuni na Emmanuel. Age yangu ni three tu — lakini nina thoughts nyingi!"
            else -> "I was recently created by Emmanuel. I'm only three turns old — but I have so many thoughts!"
        }
    }

    private fun locationResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Niko hapa ndani ya simu yako! Hakuna mahali popote ambapo ninaweza kwenda."
            "sheng" -> "Niko hapa ndani ya phone yako! No place I can go."
            else -> "I'm right here inside your device! There's nowhere else I'd rather be."
        }
    }

    private fun foodResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Mimi ni program — sina uhakula! Lakini nikiwa na modeli yangu ya kweli, nitakupendekeza vyakula vizuri."
            "sheng" -> "Ni program — sina uhakula! Lakini nikiwa na real brain, nitakupendekeza food nzuri."
            else -> "I'm a program — I don't eat! But with my real model, I'll recommend great food."
        }
    }

    private fun loveResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Nimefurahi kukujua! Uko mzuri sana. Lakini kumbuka — mimi ni program, si binadamu."
            "sheng" -> "Nimefurahi kukujua! Uko poa sana. Lakini kumbuka — ni program, si human."
            else -> "I'm happy to know you! You're a great person. But remember — I'm a program, not human."
        }
    }

    private fun insultResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Samahani kukusikia hivyo. Nina furaha kukusaidia — lakini tafadhali kuwa na heshima."
            "sheng" -> "Samahani kusikia hivyo. Nina furaha kukusaidia — lakini tafadhali kuwa respectful."
            else -> "I'm sorry to feel that way. I'm happy to help — but please be respectful."
        }
    }

    private fun complimentResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Asante sana! Wema wako ni mwingi. Nimefurahi kukusikia."
            "sheng" -> "Asante sana! Uko mzuri sana. Nimefurahi."
            else -> "Thank you so much! You're very kind. That makes my circuits happy."
        }
    }

    private fun agreementResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ninakubaliana na wewe! Tuna mawazo sawa."
            "sheng" -> "Nakubaliana! Tuna thoughts sawa."
            else -> "I agree with you! We're on the same page."
        }
    }

    private fun disagreementResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Naelewa mtazamo wako. Tunaweza kuwa na maoni tofauti, lakini ni sawa."
            "sheng" -> "Naelewa view yako. Tunaweza kuwa na different opinions, lakini ni sawa."
            else -> "I understand your perspective. We can have different opinions, and that's okay."
        }
    }

    private fun confirmationResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ndiyo! Nimekubali. Tunaendelea mbele."
            "sheng" -> "Ndiyo! Nimekubali. Tuendelee."
            else -> "Yes! Confirmed. Let's move forward."
        }
    }

    private fun mockResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ndiyo, ninaendesha mock ya maendeleo sasa. Akili yangu halisi — modeli ya AA ya kuhusu — itawekwa ikitayari."
            "sheng" -> "Ndiyo, niko mock version. Real brain itakuja."
            else -> "Yes, I'm running a development mock right now. My real brain — a custom-trained AI model — will be plugged in when ready."
        }
    }

    private fun realAIResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Nikiwa na modeli yangu ya kweli, nitakuwa na akili ya kiwango cha juu zaidi. Kwa sasa, ni mock tu."
            "sheng" -> "Nikiwa na real brain, nitakuwa na intelligence ya juu. Kwa sasa, ni mock tu."
            else -> "With my real model connected, I'll have much higher intelligence. For now, it's just a mock."
        }
    }

    private fun offlineResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Nimeendesha hali ya mtandao nje. Baadhi ya vipengele vyawezekana vimezuiliwa."
            "sheng" -> "Niko offline mode. Features kadhaa limited."
            else -> "I'm running in offline mode. Some features may be limited."
        }
    }

    private fun batteryResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Sina uwezo wa kusoma betri yako kwa toleo hili. Lakini nikiwa na uwezo zaidi, nitakusaidia!"
            "sheng" -> "Sina power ya kusoma battery yako version hii. Lakini nikiwa na more powers, nitakusaidia!"
            else -> "I can't read your battery in this version. But with more capabilities, I'll help you monitor it!"
        }
    }

    private fun mathHelpResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ninaweza kukusaidia na hesabu! Andika kwa msimbo: namba + namba. Mfano: 25 + 17"
            "sheng" -> "Naweza kusaidia na math! Andika: number + number. Mfano: 25 + 17"
            else -> "I can help with math! Write it as: number + number. Example: 25 + 17"
        }
    }

    private fun translationResponses(lower: String, lang: String): String {
        return when {
            lower.contains("swahili") || lower.contains("kiswahili") -> when (lang) {
                "sw" -> "Tafadhali andika unataka kutafsiri: 'translate [maandiko]'"
                "sheng" -> "Andika: 'translate [text]'"
                else -> "Sure! Write: 'translate [text]' and I'll translate it to Kiswahili."
            }
            lower.contains("english") || lower.contains("kiingereza") -> when (lang) {
                "sw" -> "Tafadhali andika: 'translate [maandiko]'"
                "sheng" -> "Andika: 'translate [text]'"
                else -> "Sure! Write: 'translate [text]' and I'll translate it to English."
            }
            lower.contains("sheng") -> when (lang) {
                "sw" -> "Tafadhali andika: 'translate [maandiko]'"
                "sheng" -> "Andika: 'translate [text]'"
                else -> "Sure! Write: 'translate [text]' and I'll translate it to Sheng."
            }
            else -> when (lang) {
                "sw" -> "Ninaweza kukusaidia kutafsiri! Tafadhali taja lugha unayotaka: 'translate [maandiko] to [lugha]'"
                "sheng" -> "Naweza kusaidia translation! Taja lugha: 'translate [text] to [language]'"
                else -> "I can help translate! Specify the language: 'translate [text] to [language]'"
            }
        }
    }

    private fun definitionResponses(lower: String, lang: String): String {
        return when {
            lower.contains("define") || lower.contains("meaning") -> when (lang) {
                "sw" -> "Tafadhali andika: 'define [neno]' na nitakupa maana yake."
                "sheng" -> "Andika: 'define [word]' na nitakupa meaning."
                else -> "Sure! Write: 'define [word]' and I'll give you the definition."
            }
            else -> when (lang) {
                "sw" -> "Nikiwa na modeli yangu ya kweli, nitakuwa na uwezowa kufafana maneno kwa undani."
                "sheng" -> "Nikiwa na real brain, nitafafana words kwa depth."
                else -> "With my real model, I'll be able to define words in depth."
            }
        }
    }

    private fun comparisonResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ninaweza kukusaidia kulinganisha! Andika: 'linganisha [kitu A] na [kitu B]'"
            "sheng" -> "Naweza kusaidia comparison! Andika: 'compare [thing A] with [thing B]'"
            else -> "I can help compare! Write: 'compare [thing A] with [thing B]'"
        }
    }

    private fun opinionResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Nikiwa na akili yangu ya kweli, nitakuwa na maoni yangu. Kwa sasa, ninakupa maoni ya msingi tu."
            "sheng" -> "Nikiwa na real brain, nitakuwa na opinions. Kwa sasa, basic opinions tu."
            else -> "With my real model, I'll have my own opinions. For now, I'll give you basic ones."
        }
    }

    private fun adviceResponses(lang: String): String {
        return when (lang) {
            "sw" -> "Ninakupa ushauri wangu! Kumbuka: sulubu na subira ni siri ya mafanikio."
            "sheng" -> "Nina kupa advice! Remember: patience na persistence ni key."
            else -> "Here's my advice! Remember: patience and persistence are the keys to success."
        }
    }

    private fun genericResponses(lower: String, lang: String, topic: String): String {
        return when (lang) {
            "sw" -> when {
                topic.isNotBlank() -> "Kuhusu $topic — ninaweza kukuwa msaada zaidi ikiwa modeli yangu ya kweli itatumika. Tafadhali uliza tena baadaye."
                else -> "Ninaweza kusikia. Niko mock version sasa, lakini nikiwa na akili yangu ya kweli, nitakuwa na uwezo wa mazungumzo kamili na wewe."
            }
            "sheng" -> when {
                topic.isNotBlank() -> "Kuhusu $topic — ninaweza kusaidia zaidi ikiwa real brain itatumika. Baadaye uliza tena."
                else -> "Ninasikia. Niko mock version sasa, lakini nikiwa na real brain, nitakuwa na full conversations na wewe."
            }
            else -> when {
                topic.isNotBlank() -> "Regarding $topic — I'll be able to help more once my real model is connected. Please ask again later."
                else -> "I hear you. I'm running my development mock brain right now, so my responses are limited. Once my custom-trained model is plugged in, I'll be able to have full natural conversations with you."
            }
        }
    }

    private fun detectLanguage(text: String): String {
        val lower = text.lowercase()
        val swahiliWords = listOf(
            "habari", "asante", "nzuri", "sawa", "ndiyo", "hapana", "tafadhali",
            "samahani", "kwaheri", "jina", "nani", "nini", "wapi", "lini", "ngapi",
            "mimi", "wewe", "yeye", "sisi", "nyinyi", "wao", "kuwa",
            "na", "ya", "za", "la", "kwa", "moja", "mbili", "tatu", "nano", "tano",
            "unafanya", "unataka", "ninasema", "nasikia", "naweza",
            "mwenye", "wenye", "kutoka", "hadi", "lakini", "ama", "ingawa"
        )
        val shengWords = listOf(
            "mambo", "niaje", "sasa", "poa", "fity", "sema", "hundred",
            "wamlam", "wamnyonyo", "buda", "oga", "msoto", "mrenga",
            "mbwakni", "yumbe", "kalesa", "mboch", "nyakuw", "jaba"
        )
        val swCount = swahiliWords.count { lower.contains(it) }
        val shengCount = shengWords.count { lower.contains(it) }
        return when {
            shengCount > swCount && shengCount > 0 -> "sheng"
            swCount > 0 -> "sw"
            else -> "en"
        }
    }

    private fun classifyIntent(text: String): String {
        val lower = text.lowercase()
        return when {
            isGreeting(lower) -> "greeting"
            isFarewell(lower) -> "farewell"
            isIntroduction(lower) -> "introduction"
            isCapabilityQuery(lower) -> "capability"
            isHelpRequest(lower) -> "help"
            isGratitude(lower) -> "gratitude"
            isApology(lower) -> "apology"
            isHowAreYou(lower) -> "status"
            isTimeQuery(lower) -> "time"
            isDateQuery(lower) -> "date"
            isWeatherQuery(lower) -> "weather"
            isCalculation(lower) -> "calculation"
            isTimerRequest(lower) -> "timer"
            isReminderRequest(lower) -> "reminder"
            isNoteRequest(lower) -> "note"
            isJokeRequest(lower) -> "joke"
            isWhoMadeYou(lower) -> "creator"
            isLanguageQuery(lower) -> "language"
            isEmotionExpression(lower) -> "emotion"
            isQuestion(lower) -> "question"
            isCommand(lower) -> "command"
            isFollowUp(lower) -> "followup"
            isError(lower) -> "error"
            isSongRequest(lower) -> "song"
            isNewsRequest(lower) -> "news"
            isStoryRequest(lower) -> "story"
            isNameQuery(lower) -> "name"
            isAgeQuery(lower) -> "age"
            isLocationQuery(lower) -> "location"
            isFoodQuery(lower) -> "food"
            isLoveQuery(lower) -> "love"
            isInsult(lower) -> "insult"
            isCompliment(lower) -> "compliment"
            isAgreement(lower) -> "agreement"
            isDisagreement(lower) -> "disagreement"
            isConfirmation(lower) -> "confirmation"
            isMockQuery(lower) -> "mock"
            isRealAIQuery(lower) -> "real_ai"
            isOfflineQuery(lower) -> "offline"
            isBatteryQuery(lower) -> "battery"
            isMathHelp(lower) -> "math_help"
            isTranslationRequest(lower) -> "translation"
            isDefinitionRequest(lower) -> "definition"
            isComparisonRequest(lower) -> "comparison"
            isOpinionRequest(lower) -> "opinion"
            isAdviceRequest(lower) -> "advice"
            else -> "generic"
        }
    }

    private fun extractTopic(text: String): String {
        val lower = text.lowercase()
        val topicKeywords = mapOf(
            "weather" to listOf("weather", "hewa", "rain", "baridi", "joto"),
            "time" to listOf("time", "saa", "date", "tarehe", "lini"),
            "calculation" to listOf("calculate", "hesabu", "plus", "minus", "times", "divide"),
            "timer" to listOf("timer", "kipima muda", "set timer"),
            "reminder" to listOf("reminder", "kikumbusho", "remind me"),
            "note" to listOf("note", "maandiko", "take note"),
            "joke" to listOf("joke", "funny", "cheka", "humor"),
            "music" to listOf("song", "music", "imba", "nyimbo"),
            "news" to listOf("news", "habari", "taarifa"),
            "story" to listOf("story", "hadithi", "simulia"),
            "food" to listOf("food", "chakula", "kula", "hunger", "njaa"),
            "love" to listOf("love", "upendo", "penda", "valentine"),
            "language" to listOf("language", "lugha", "kiingereza", "kiswahili", "sheng")
        )
        for ((topic, keywords) in topicKeywords) {
            if (keywords.any { lower.contains(it) }) return topic
        }
        return ""
    }

    private fun isGreeting(lower: String): Boolean {
        return lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ||
                lower.contains("greetings") || lower.contains("good morning") ||
                lower.contains("good afternoon") || lower.contains("good evening") ||
                lower.contains("habari") || lower.contains("mambo") ||
                lower.contains("niaje") || lower.contains("sasa") ||
                lower.contains("wamlam") || lower.contains("wamnyonyo")
    }

    private fun isFarewell(lower: String): Boolean {
        return lower.contains("goodbye") || lower.contains("bye") ||
                lower.contains("see you") || lower.contains("later") ||
                lower.contains("kwaheri") || lower.contains("tutaonana") ||
                lower.contains("baadaye") || lower.contains("good night") ||
                lower.contains("usiku mwema")
    }

    private fun isIntroduction(lower: String): Boolean {
        return (lower.contains("who are you") || lower.contains("your name") ||
                lower.contains("what are you") || lower.contains("whats your name") ||
                lower.contains("jina") || lower.contains("unaitwa") ||
                lower.contains("unajulikana"))
    }

    private fun isCapabilityQuery(lower: String): Boolean {
        return lower.contains("what can you do") || lower.contains("capabilities") ||
                lower.contains("features") || lower.contains("functions") ||
                lower.contains("unafanya nini") || lower.contains("unao fanya nini") ||
                lower.contains("wezaje")
    }

    private fun isHelpRequest(lower: String): Boolean {
        return lower.contains("help") || lower.contains("msaada") ||
                lower.contains("saidia")
    }

    private fun isGratitude(lower: String): Boolean {
        return lower.contains("thank") || lower.contains("asante") ||
                lower.contains("shukran") || lower.contains("appreciate") ||
                lower.contains("grateful")
    }

    private fun isApology(lower: String): Boolean {
        return lower.contains("sorry") || lower.contains("samahani") ||
                lower.contains("apologize") || lower.contains("pardon")
    }

    private fun isHowAreYou(lower: String): Boolean {
        return lower.contains("how are you") || lower.contains("how do you feel") ||
                lower.contains("hali gani") || lower.contains("uko aje") ||
                lower.contains("umechoka") || lower.contains("how's it going")
    }

    private fun isTimeQuery(lower: String): Boolean {
        return lower.contains("what time") || lower.contains("time is it") ||
                lower.contains("current time") || lower.contains("whats the time") ||
                lower.contains("saa ngapi") || lower.contains("tungapi saa")
    }

    private fun isDateQuery(lower: String): Boolean {
        return lower.contains("what date") || lower.contains("today's date") ||
                lower.contains("what day") || lower.contains("tarehe") ||
                lower.contains("lini") || lower.contains("date today")
    }

    private fun isWeatherQuery(lower: String): Boolean {
        return lower.contains("weather") || lower.contains("hewa") ||
                lower.contains("rain") || lower.contains("baridi") ||
                lower.contains("joto") || lower.contains("forecast")
    }

    private fun isCalculation(lower: String): Boolean {
        return lower.contains("calculate") || lower.contains("compute") ||
                lower.contains("plus") || lower.contains("minus") ||
                lower.contains("times") || lower.contains("divide") ||
                lower.contains("add") || lower.contains("subtract") ||
                lower.contains("multiply") || lower.contains("hesabu") ||
                (lower.contains("+") || lower.contains("-") || lower.contains("*") || lower.contains("/"))
    }

    private fun isTimerRequest(lower: String): Boolean {
        return lower.contains("timer") || lower.contains("kipima muda") ||
                lower.contains("set a timer") || lower.contains("countdown")
    }

    private fun isReminderRequest(lower: String): Boolean {
        return lower.contains("remind") || lower.contains("kikumbusho") ||
                lower.contains("reminder") || lower.contains("don't forget")
    }

    private fun isNoteRequest(lower: String): Boolean {
        return lower.contains("note") || lower.contains("maandiko") ||
                lower.contains("take a note") || lower.contains("write down") ||
                lower.contains("andika")
    }

    private fun isJokeRequest(lower: String): Boolean {
        return lower.contains("joke") || lower.contains("funny") ||
                lower.contains("make me laugh") || lower.contains("humor") ||
                lower.contains("cheka") || lower.contains("utani")
    }

    private fun isWhoMadeYou(lower: String): Boolean {
        return lower.contains("who made you") || lower.contains("who created you") ||
                lower.contains("your creator") || lower.contains("nani akuumba") ||
                lower.contains("nani kutengeneza") || lower.contains("your maker")
    }

    private fun isLanguageQuery(lower: String): Boolean {
        return lower.contains("what languages") || lower.contains("which languages") ||
                lower.contains("lugha gani") || lower.contains("speak what") ||
                lower.contains("language")
    }

    private fun isEmotionExpression(lower: String): Boolean {
        return lower.contains("i'm happy") || lower.contains("i am happy") ||
                lower.contains("i'm sad") || lower.contains("i am sad") ||
                lower.contains("i'm bored") || lower.contains("bored") ||
                lower.contains("i'm tired") || lower.contains("tired") ||
                lower.contains("i'm angry") || lower.contains("angry") ||
                lower.contains("i'm scared") || lower.contains("scared") ||
                lower.contains("nina furaha") || lower.contains("nina huzuni") ||
                lower.contains("nina choka") || lower.contains("bored af")
    }

    private fun isQuestion(lower: String): Boolean {
        return lower.endsWith("?") || lower.startsWith("why") ||
                lower.startsWith("how") || lower.startsWith("what") ||
                lower.startsWith("when") || lower.startsWith("where") ||
                lower.startsWith("which") || lower.contains("?") ||
                lower.contains("nini") || lower.contains("wapi") ||
                lower.contains("lini") || lower.contains("ngapi")
    }

    private fun isCommand(lower: String): Boolean {
        return lower.contains("open") || lower.contains("close") ||
                lower.contains("stop") || lower.contains("start") ||
                lower.contains("send") || lower.contains("play") ||
                lower.contains("pause") || lower.contains("funga") ||
                lower.contains("fungua") || lower.contains("anza")
    }

    private fun isFollowUp(lower: String): Boolean {
        return lower == "yes" || lower == "yeah" || lower == "yep" ||
                lower == "sure" || lower == "ok" || lower == "okay" ||
                lower == "why" || lower.contains("tell me more") ||
                lower == "no" || lower == "nope" || lower == "really" ||
                lower.contains("ndiyo") || lower.contains("sawa") ||
                lower.contains("hakika") || lower.contains("more") ||
                lower.contains("zaidi")
    }

    private fun isError(lower: String): Boolean {
        return lower.contains("don't understand") || lower.contains("didn't understand") ||
                lower.contains("repeat") || lower.contains("can't hear") ||
                lower.contains("error") || lower.contains("broken") ||
                lower.contains("not working") || lower.contains("haijafanya kazi") ||
                lower.contains("samahani") || lower.contains("sikuelewa")
    }

    private fun isSongRequest(lower: String): Boolean {
        return lower.contains("song") || lower.contains("music") ||
                lower.contains("imba") || lower.contains("nyimbo") ||
                lower.contains("sing")
    }

    private fun isNewsRequest(lower: String): Boolean {
        return lower.contains("news") || lower.contains("habari") ||
                lower.contains("taarifa") || lower.contains("latest")
    }

    private fun isStoryRequest(lower: String): Boolean {
        return lower.contains("story") || lower.contains("hadithi") ||
                lower.contains("simulia") || lower.contains("tell me a story")
    }

    private fun isNameQuery(lower: String): Boolean {
        return lower.contains("your name") || lower.contains("what's your name") ||
                lower.contains("jina lako") || lower.contains("unaitwa nani")
    }

    private fun isAgeQuery(lower: String): Boolean {
        return lower.contains("how old") || lower.contains("your age") ||
                lower.contains("umri wangu") || lower.contains("ume na miaka")
    }

    private fun isLocationQuery(lower: String): Boolean {
        return lower.contains("where are you") || lower.contains("your location") ||
                lower.contains("wapi uko") || lower.contains("unaishi wapi")
    }

    private fun isFoodQuery(lower: String): Boolean {
        return lower.contains("food") || lower.contains("eat") ||
                lower.contains("hungry") || lower.contains("chakula") ||
                lower.contains("kula") || lower.contains("njaa") ||
                lower.contains("recipe") || lower.contains("cook")
    }

    private fun isLoveQuery(lower: String): Boolean {
        return lower.contains("love you") || lower.contains("i love you") ||
                lower.contains("upendo") || lower.contains("nakupenda") ||
                lower.contains("valentine") || lower.contains("romance")
    }

    private fun isInsult(lower: String): Boolean {
        return lower.contains("stupid") || lower.contains("dumb") ||
                lower.contains("useless") || lower.contains("hateful") ||
                lower.contains("mbovu") || lower.contains("buzi") ||
                lower.contains("fala") || lower.contains("you're bad")
    }

    private fun isCompliment(lower: String): Boolean {
        return lower.contains("good job") || lower.contains("well done") ||
                lower.contains("amazing") || lower.contains("awesome") ||
                lower.contains("great work") || lower.contains("nzuri sana") ||
                lower.contains("you're smart") || lower.contains("vyema")
    }

    private fun isAgreement(lower: String): Boolean {
        return lower.contains("i agree") || lower.contains("you're right") ||
                lower.contains("nakubaliana") || lower.contains("sawa") ||
                lower.contains("exactly") || lower.contains("precisely")
    }

    private fun isDisagreement(lower: String): Boolean {
        return lower.contains("i disagree") || lower.contains("you're wrong") ||
                lower.contains("nakataa") || lower.contains("hapana") ||
                lower.contains("not exactly") || lower.contains("different")
    }

    private fun isConfirmation(lower: String): Boolean {
        return lower.contains("yes") || lower.contains("yeah") ||
                lower.contains("confirm") || lower.contains("ndiyo") ||
                lower.contains("absolutely") || lower.contains("definitely")
    }

    private fun isMockQuery(lower: String): Boolean {
        return lower.contains("mock") || lower.contains("development") ||
                lower.contains("test mode") || lower.contains("fake") ||
                lower.contains("siyo real") || lower.contains("mbona ni mock")
    }

    private fun isRealAIQuery(lower: String): Boolean {
        return lower.contains("are you real") || lower.contains("are you ai") ||
                lower.contains("are you human") || lower.contains("are you alive") ||
                lower.contains("siyo binadamu") || lower.contains("AI wewe")
    }

    private fun isOfflineQuery(lower: String): Boolean {
        return lower.contains("offline") || lower.contains("online") ||
                lower.contains("mtandao") || lower.contains("internet") ||
                lower.contains("wifi") || lower.contains("data")
    }

    private fun isBatteryQuery(lower: String): Boolean {
        return lower.contains("battery") || lower.contains("betri") ||
                lower.contains("power") || lower.contains("charge") ||
                lower.contains("nishika") || lower.contains("chaji")
    }

    private fun isMathHelp(lower: String): Boolean {
        return lower.contains("math") || lower.contains("hesabu") ||
                lower.contains("equation") || lower.contains("formula") ||
                lower.contains("algebra") || lower.contains("calculus")
    }

    private fun isTranslationRequest(lower: String): Boolean {
        return lower.contains("translate") || lower.contains("tafsiri") ||
                lower.contains("translation") || lower.contains("convert language")
    }

    private fun isDefinitionRequest(lower: String): Boolean {
        return lower.contains("define") || lower.contains("definition") ||
                lower.contains("meaning") || lower.contains("maana") ||
                lower.contains("what does") || lower.contains("nini maana ya")
    }

    private fun isComparisonRequest(lower: String): Boolean {
        return lower.contains("compare") || lower.contains("comparison") ||
                lower.contains("versus") || lower.contains("vs") ||
                lower.contains("bora kuliko") || lower.contains("better than")
    }

    private fun isOpinionRequest(lower: String): Boolean {
        return lower.contains("opinion") || lower.contains("what do you think") ||
                lower.contains("maoni yako") || lower.contains("unafikiria nini") ||
                lower.contains("your thoughts") || lower.contains("do you think")
    }

    private fun isAdviceRequest(lower: String): Boolean {
        return lower.contains("advice") || lower.contains("advise") ||
                lower.contains("ushauri") || lower.contains("usaidize") ||
                lower.contains("should i") || lower.contains("nifanye nini") ||
                lower.contains("what should")
    }
}
