package com.pipai.dragontiles.hero

import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.relics.Transmuter
import com.pipai.dragontiles.spells.colorless.Chow
import com.pipai.dragontiles.spells.elementalist.PingHu
import com.pipai.dragontiles.spells.elementalist.Eyes
import com.pipai.dragontiles.spells.colorless.Pong
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.colorless.Blast
import com.pipai.dragontiles.spells.colorless.Invoke
import com.pipai.dragontiles.spells.colorless.Strike
import com.pipai.dragontiles.spells.elementalist.*

class Elementalist : HeroClass {
    override val strId: String = "base:hero:Elementalist"
    override val assetName: String = "elementalist.png"

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
        DualInvoke(),
        MultiInvoke(),
        StrengthRune(),
        PiercingStrike(),
        RampStrike(),
        Concentrate(),
        FeedbackLoop(),
        Singularity(),
        Spark(),
        Blast(),
        Explosion(),
        Precipitate(),
        BurnRune(),
        FrostRune(),
        Fireball(),
        IceShard(),
        ChainLightning(),
        PingHu(),
        Chow(),
        Pong(),
    )
    override val hpMax: Int = 60
    override val fluxMax: Int = 40
    override val startingGold: Int = 1
    override val handSize: Int = 17
    override val activeSpellSize: Int = 6
    override val sideboardSize: Int = 3
    override val sorceriesSize: Int = 9
    override val potionSlotSize: Int = 3
}
