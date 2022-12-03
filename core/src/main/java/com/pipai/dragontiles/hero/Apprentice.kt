package com.pipai.dragontiles.hero

import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.relics.TigerBangle
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.apprentice.Faan
import com.pipai.dragontiles.spells.apprentice.PingHu
import com.pipai.dragontiles.spells.apprentice.QiGong
import com.pipai.dragontiles.spells.colorless.Invoke
import com.pipai.dragontiles.spells.colorless.Strike
import com.pipai.dragontiles.spells.elementalist.*

class Apprentice : HeroClass {
    override val id: String = "base:hero:Apprentice"
    override val assetName: String = "elementalist.png"

    override val defaultName: String = "Mei"
    override val startingRelic: Relic = TigerBangle()
    override val classRelics: List<Relic> = listOf()
    override val starterDeck: List<Spell> = listOf(
        Invoke(),
        Strike(),
        Vent(),
        QiGong(),
        Faan(),
        PingHu(),
    )
    override val spells: List<Spell> = listOf(
        Slap(),
        XAttack(),
        RampStrike(),
        Concentrate(),
        GreatPower(),
        ReturnToSender(),
        WindUp(),
        Typhoon(),
        FluxBlast(),
    )
    override val hpMax: Int = 60
    override val fluxMax: Int = 40
    override val startingGold: Int = 3
    override val handSize: Int = 14
    override val activeSpellSize: Int = 6
    override val sideboardSize: Int = 3
    override val sorceriesSize: Int = 6
    override val potionSlotSize: Int = 3
}
