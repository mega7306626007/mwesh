package com.jarvis.assistant.commands.handlers

data class Joke(
    val id: Int,
    val text: String,
    val language: String,
    val category: String
)

object JokeApi {

    private val jokes = listOf(
        Joke(1, "Why don't scientists trust atoms? Because they make up everything.", "en", "Science"),
        Joke(2, "Why did the scarecrow win an award? Because he was outstanding in his field.", "en", "General"),
        Joke(3, "Why don't skeletons fight each other? They don't have the guts.", "en", "General"),
        Joke(4, "What do you call a fish without eyes? A fsh.", "en", "Animals"),
        Joke(5, "Why did the math book look sad? Because it had too many problems.", "en", "School"),
        Joke(6, "What do you call a bear with no teeth? A gummy bear.", "en", "Animals"),
        Joke(7, "Why did the golfer bring two pairs of pants? In case he got a hole in one.", "en", "Sports"),
        Joke(8, "What did the ocean say to the beach? Nothing, it just waved.", "en", "Nature"),
        Joke(9, "Why did the bicycle fall over? Because it was two-tired.", "en", "General"),
        Joke(10, "How does a penguin build its house? Igloos it together.", "en", "Animals"),
        Joke(11, "Why don't eggs tell jokes? They'd crack each other up.", "en", "Food"),
        Joke(12, "What do you call a cheese that isn't yours? Nacho cheese.", "en", "Food"),
        Joke(13, "Why did the coffee file a police report? It got mugged.", "en", "Food"),
        Joke(14, "What do you call a belt made of watches? A waist of time.", "en", "General"),
        Joke(15, "Why did the tomato turn red? Because it saw the salad dressing.", "en", "Food"),
        Joke(16, "What did one wall say to the other wall? I'll meet you at the corner.", "en", "General"),
        Joke(17, "Why can't you give Elsa a balloon? Because she'll let it go.", "en", "Movies"),
        Joke(18, "What do you call a dinosaur that crashes his car? Tyrannosaurus Wrecks.", "en", "Animals"),
        Joke(19, "Why did the cookie go to the hospital? Because it felt crummy.", "en", "Food"),
        Joke(20, "What do you call a sleeping bull? A bulldozer.", "en", "Animals"),
        Joke(21, "Why did the stadium get hot after the game? All the fans left.", "en", "Sports"),
        Joke(22, "What do you call a pony with a sore throat? A little hoarse.", "en", "Animals"),
        Joke(23, "Why did the policeman go to the bakery? He was looking for a doughnut.", "en", "Food"),
        Joke(24, "What do you call a boomerang that won't come back? A stick.", "en", "General"),
        Joke(25, "Why did the smartphone need glasses? Because it lost all its contacts.", "en", "Technology"),
        Joke(26, "What do you call a fish that practices medicine? A sturgeon.", "en", "Animals"),
        Joke(27, "Why was the computer cold? It left its Windows open.", "en", "Technology"),
        Joke(28, "What do you call a group of unorganized cats? A cat-astrophe.", "en", "Animals"),
        Joke(29, "Why did the ghost go to the bakery? To get some boo-ty bread.", "en", "Halloween"),
        Joke(30, "What do you call a snowman with a six pack? An abdominal snowman.", "en", "Winter"),
        Joke(31, "Why do bees have sticky hair? Because they use honeycombs.", "en", "Animals"),
        Joke(32, "What do you call a fake noodle? An impasta.", "en", "Food"),
        Joke(33, "Why did the Clydesdale go to church? Because he was a little horse.", "en", "Animals"),
        Joke(34, "What do you call a deer with no eyes? No idea.", "en", "Animals"),
        Joke(35, "Why don't oysters donate to charity? Because they're shellfish.", "en", "Animals"),
        Joke(36, "What do you call a man with a rubber toe? Roberto.", "en", "General"),
        Joke(37, "Why did the invisible man turn down the job offer? He couldn't see himself doing it.", "en", "General"),
        Joke(38, "What do you call a can opener that doesn't work? A can't opener.", "en", "General"),
        Joke(39, "Why did the barber win the race? He took a short cut.", "en", "General"),
        Joke(40, "What do you call a dog magician? A labracadabrador.", "en", "Animals"),
        Joke(41, "Why did the man fall down the well? Because he couldn't see that well.", "en", "General"),
        Joke(42, "What do you call a fish that wears a crown? A king fish.", "en", "Animals"),
        Joke(43, "Why did the computer go to the doctor? Because it had a virus.", "en", "Technology"),
        Joke(44, "What do you call a cow with no legs? Ground beef.", "en", "Animals"),
        Joke(45, "Why did the music teacher go to jail? For fingering A minor.", "en", "Music"),
        Joke(46, "What do you call a sheep with no legs? A cloud.", "en", "Animals"),
        Joke(47, "Why did the skeleton go to the dance alone? He had no body to go with him.", "en", "Halloween"),
        Joke(48, "What do you call a man who can't stand? Neil.", "en", "General"),
        Joke(49, "Why did the cookie cry? Because its mom was a wafer so long.", "en", "Food"),
        Joke(50, "What do you call a fish that plays guitar? A bass player.", "en", "Music"),
        Joke(51, "Kwa nini mtu hajui kusema kiswahili vizuri? Kwa sababu anasema haraka sana.", "sw", "General"),
        Joke(52, "Mwalimu: Jina lako ni nini? Mwanafunzi: Jina langu ni rafiki yako.", "sw", "School"),
        Joke(53, "Kwa nini ndege haanyi chini? Kwa sababu ana mabawa ya shoka.", "sw", "Animals"),
        Joke(54, "Daktari: Unahitaji usingizi mzuri. Mgonjwa: Usingizi mkubwa sana kwa macho yangu.", "sw", "Health"),
        Joke(55, "Kwa nini baiskeli hawezi kwembamba pekee? Kwa sababu ina maguruduma mawili tu.", "sw", "General"),
        Joke(56, "Mwalimu: Tafuta jina la mnyama anayelala usiku. Mwanafunzi: Ni nani bwana.", "sw", "School"),
        Joke(57, "Kwa nini kuku alikataa kuvuka barabara? Alikuwa na haraka kwenda shambani.", "sw", "Animals"),
        Joke(58, "Bodi: Unakula nini leo? Mwana: Nina leo moja tu, kesho nyingine.", "sw", "Food"),
        Joke(59, "Kwa nini simu haioni? Kwa sababu ina skrini ya macho.", "sw", "Technology"),
        Joke(60, "Habari za asubuhi! Kazi yako leo ni kusikiliza neno moja tu: polepole.", "sw", "General"),
        Joke(61, "Mwalimu: Kwa nini umechelewa? Mwanafunzi: Kwa sababu nilichelewa kuamka.", "sw", "School"),
        Joke(62, "Kwa nini tembo ana mkia mfupi? Kwa sababu hakujifunza kurefusha.", "sw", "Animals"),
        Joke(63, "Daktari: Unavipi? Mgonjwa: Nina maumivu ya kichwa kila siku.", "sw", "Health"),
        Joke(64, "Kwa nini mwalimu alipiga kelele? Kwa sababu wanafunzi walikuwa wakizungumza.", "sw", "School"),
        Joke(65, "Kwa nini samaki hawezi kucheka? Kwa sababu ana maji machoni.", "sw", "Animals"),
        Joke(66, "Mwalimu: Andika sentensi yenye neno 'sababu'. Mwanafunzi: Sababu ya kuandika ni kusoma.", "sw", "School"),
        Joke(67, "Kwa nini gari halina miguu? Kwa sababu ina maguruduma.", "sw", "General"),
        Joke(68, "Kwa nini mtu alikataa kula chakula? Kwa sababu alikuwa amekula.", "sw", "Food"),
        Joke(69, "Kwa nini mti ulianguka? Kwa sababu ulikuwa umechoka kusimama.", "sw", "Nature"),
        Joke(70, "Kwa nini jua halitoki usiku? Kwa sababu linachoka kung'aa.", "sw", "Nature"),
        Joke(71, "Wakaudzwa sei? Ndichambvisa minwe dzangu mu shure.", "sh", "General"),
        Joke(72, "Kuti muchitamba, tinenge tichitamba sekuru wacho.", "sh", "General"),
        Joke(73, "Sei tsvuku ichibva mudenga? Kuti ikaibva kubva kumaodzanyemba.", "sh", "Animals"),
        Joke(74, "Vana vaviri vanotaura: wakamira ipapo? Ndinoreva mimba dzangu.", "sh", "General"),
        Joke(75, "Kuti wakafara sei nezvake? Nekuti wakawana zvake zvese.", "sh", "General"),
        Joke(76, "Murume akati: Mukadzi wangu anoda mari yese. Mukadzi akati: Hongu, kusimuka ndangariro.", "sh", "General"),
        Joke(77, "Kuti bhurukwa rakafamba sei? Neminwe yaro ya kusimuka.", "sh", "General"),
        Joke(78, "Kuti vana vachade kudya sei? Vachada kudya zvakanyanya.", "sh", "Food"),
        Joke(79, "Kuti gaka rakafanira sei? Rakanaka kugara pachigaro chake.", "sh", "General"),
        Joke(80, "Kuti tsvimbo yakasvika sei? Yakasvika nekuti yakanga isina pfuma.", "sh", "General"),
        Joke(81, "Sei munhu achitamba achiseka? Nekuti ane zvaanofara nazvo.", "sh", "General"),
        Joke(82, "Kuti imbwa yakabuda sei? Yakabuda nekuti yakanga isina musuo.", "sh", "Animals"),
        Joke(83, "Sei chikoro chisina vadzidzisi? Nekuti vakanga vachitamba.", "sh", "School"),
        Joke(84, "Kuti mvura yakanda sei? Yakanda nekuti yakanga isina nzira.", "sh", "Nature"),
        Joke(85, "Sei munhu akatenga bhuku? Nekuti akanga achida kuverenga.", "sh", "General"),
        Joke(86, "Kuti zuva rakabuda sei? Rakabuda nekuti rakanga rachoda kupenya.", "sh", "Nature"),
        Joke(87, "Sei munhu akafara? Nekuti akanga ane shamwari.", "sh", "General"),
        Joke(88, "Kuti nzou yakafamba sei? Yakafamba nekuti yakanga isina nzira yekumira.", "sh", "Animals"),
        Joke(89, "Sei munhu akataura zvizere? Nekuti akanga asina zvekutaura.", "sh", "General"),
        Joke(90, "Kuti chikafu chakabikwa sei? Chakabikwa nekuti chakanga chichida kubikwa.", "sh", "Food"),
        Joke(91, "Sei munhu akamhanya? Nekuti akanga achida kusvika.", "sh", "General"),
        Joke(92, "Kuti gore rakapera sei? Rakapera nekuti rakanga rachida kupera.", "sh", "Nature"),
        Joke(93, "Sei munhu akataura zvisina basa? Nekuti akanga asina zvekutaura.", "sh", "General"),
        Joke(94, "Kuti bhuku rakanyorwa sei? Rakanyorwa nekuti rakanga richida kunyorwa.", "sh", "General"),
        Joke(95, "Sei munhu akafara zvikuru? Nekuti akanga ane zvinofara.", "sh", "General"),
        Joke(96, "Kuti mvura yakayera sei? Yakayera nekuti yakanga ichida kuyerera.", "sh", "Nature"),
        Joke(97, "Sei munhu akataura zvakawanda? Nekuti akanga ane zvakawanda zvekutaura.", "sh", "General"),
        Joke(98, "Kuti chisikana chakabika sei? Chakabika nekuti chakanga chichida kubika.", "sh", "Food"),
        Joke(99, "Sei munhu akamhanya zvikuru? Nekuti akanga achida kusvika nekukurumidza.", "sh", "General"),
        Joke(100, "Kuti zuva rakanyika sei? Rakanyika nekuti rakanga rachoda kunyika.", "sh", "Nature"),
        Joke(101, "Sei munhu akataura zvakanaka? Nekuti akanga achida kutaura zvakanaka.", "sh", "General"),
        Joke(102, "Kuti nzou yakafara sei? Yakafara nekuti yakanga ine zvinofara.", "sh", "Animals")
    )

    fun getJoke(language: String = "en", category: String? = null): Joke {
        val filtered = jokes.filter { it.language == language }
        val pool = if (category != null) {
            filtered.filter { it.category.equals(category, ignoreCase = true) }
        } else {
            filtered
        }
        return if (pool.isNotEmpty()) pool.random() else jokes.random()
    }

    fun getRandomJoke(): Joke = jokes.random()

    fun getByCategory(category: String): List<Joke> {
        return jokes.filter { it.category.equals(category, ignoreCase = true) }
    }

    fun getCategories(): List<String> {
        return jokes.map { it.category }.distinct()
    }

    fun getLanguages(): List<String> {
        return jokes.map { it.language }.distinct()
    }

    fun getJokeCount(): Int = jokes.size
}
