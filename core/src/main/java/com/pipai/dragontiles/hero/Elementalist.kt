package com.pipai.dragontiles.hero

import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.relics.Transmuter
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.colorless.Invoke
import com.pipai.dragontiles.spells.colorless.Strike
import com.pipai.dragontiles.spells.elementalist.*

class Elementalist : HeroClass {
    override val id: String = "base:hero:Elementalist"
    override val assetName: String = "elementalist.png"

    override val defaultName: String = "Lien"
    override val startingRelic: Relic = Transmuter()
    override val classRelics: List<Relic> = listOf()
    override val starterDeck: List<Spell> = listOf(
        Invoke(),
        Strike(),
        Vent(),
        ElementalRune(),
        Break(),
        Eyes(),
    )
    override val spells: List<Spell> = listOf(
        Slap(),
//        Burn(),
        Flare(),
        ColdHands(),
        Thundershock(),
        DualStrike(),
//        XAttack(), // Keep for next character
//        MultiInvoke(),
        Cyclone(),
//        Typhoon(), // Keep for Dragonian?
        SecondWind(),
        ElementX(),
        Enpowerment(),
//        StrengthRune(),
        FetchingStrike(),
        TransmutingFetch(),
        TransmutingStrike(),
//        RampStrike(),
//        Superconduct(),
        CircularBreathing(),
        ElementalSight(),
        Battery(),
        Discharge(),
        FluxCapacitor(),
//        InflictReactant(),
//        DenseReactants(),
//        ReactantSmoke(),
//        ReactantFumes(),
//        ReactantStrike(),
        ElementalBreathing(),
        EnpowerElement(),
        FullAutoInvoke(),
//        Concentrate(),
//        FeedbackLoop(),
//        Spark(), // Keep for 4th character
        Splash(),
        Blast(),
        BigBang(),
        BreakingBlast(),
        Explosion(),
        WindChill(),
        Precipitate(),
        BurnRune(),
        FrostRune(),
        ThunderRune(),
        Fireball(),
        IceShard(),
        ChainLightning(),
//        ReactantGenesis(),
//        GreatPower(),
        Expansion(),
        Transmutation(),
        ExpandAnomaly(),
        Ventilation(),
        Riffle(),
        ReturnToSender(),
        WindUp(),
        AnomalousSpeed(),
        Chillsink(),
        Breakwave(),
        MassTransmute(),
//        ReactionMastery(),
//        WindSwirl(),
//        SummonPhoenix(),
        Fusion(),
        UnlimitedPower(),
//        PhoenixFire(),
//        PhoenixTears(),
        DumpFlux(),
        TrialByFire(),
        SelfAffliction(),
        Inflation(),
//        Megaxplosion(),
//        FluxBlast(),
        Denormalize(),
        FluxMastery(),
        Barrier(),
        Masochism(),
        Abnormality(),
        SorcerousPower(),
        VermilionBird(),
        Catharsis(),
        Spray(),
        BirdCall(),
        ChainTarget(),
        AnomalousStrike(),
        StaticElectricity(),
        PhoenixFire(),
        Blizzard(),
        RapidFire(),
    )
    override val hpMax: Int = 60
    override val fluxMax: Int = 40
    override val startingGold: Int = 3
    override val handSize: Int = 17
    override val activeSpellSize: Int = 6
    override val sideboardSize: Int = 3
    override val sorceriesSize: Int = 3
    override val potionSlotSize: Int = 3
}
