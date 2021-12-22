package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.events.PotionUseEvent
import com.pipai.dragontiles.artemis.events.TopRowUiUpdateEvent
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.potions.PotionType
import com.pipai.dragontiles.utils.potionAssetPath
import com.pipai.dragontiles.utils.relicAssetPath
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe
import java.lang.Integer.min

class TopRowUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val stage: Stage,
    var isCombat: Boolean,
) : NoProcessingSystem() {

    private val skin = game.skin
    private val config = game.gameConfig

    private val rootTable = Table()
    private val topRow = Table()
    private val relicRow = Table()
    private var hp = runData.hero.hp
    private var hpMax = runData.hero.hpMax
    private var flux = runData.hero.flux
    private var fluxMax = runData.hero.fluxMax
    private var gold = runData.hero.gold
    private val hpLabel = Label("HP: $hp/$hpMax", skin)
    private val fluxLabel = Label("Flux: $flux/$fluxMax", skin)
    private val goldLabel = Label("Gold: $gold", skin)
    private val potionTable = Table()

    private val sTooltip by system<TooltipSystem>()
    private val sEvent by system<EventSystem>()

    private lateinit var api: GlobalApi

    override fun initialize() {
        api = GlobalApi(runData, sEvent)
        rootTable.setFillParent(true)
        topRow.background = skin.getDrawable("frameDrawable")
        topRow.add(Label(runData.hero.name, skin))
            .width(260f)
            .pad(8f)
            .padLeft(16f)
            .left()
            .top()
        topRow.add(hpLabel)
            .width(160f)
        topRow.add(fluxLabel)
            .width(160f)
        topRow.add(goldLabel)
            .width(120f)
        updatePotions()
        topRow.add(potionTable)
            .expand()

        updateRelicRow()

        rootTable.add(topRow)
            .width(config.resolution.width.toFloat())
            .top()
            .left()
        rootTable.row()
        rootTable.add(relicRow)
            .left()
        rootTable.row()
        rootTable.add()
            .expand()
        rootTable.row()
        stage.addActor(rootTable)
    }

    fun updatePotions() {
        potionTable.clearChildren()
        runData.hero.potionSlots.forEach { slot ->
            if (slot.potion == null) {
                potionTable.add(Image(game.assets.get(potionAssetPath("empty.png"), Texture::class.java)))
                    .prefWidth(48f)
                    .prefHeight(48f)
            } else {
                val potion = slot.potion!!
                val potionImage = Image(game.assets.get(potionAssetPath(potion.assetName), Texture::class.java))
                potionTable.add(potionImage)
                    .prefWidth(48f)
                    .prefHeight(48f)
                potionImage.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        if (isCombat) {
                            sEvent.dispatch(PotionUseEvent(potion))
                        } else {
                            if (potion.type == PotionType.UNIVERSAL) {
                                potion.useOutsideCombat(api)
                            }
                        }
                    }

                    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                        sTooltip.addNameDescLocalization(game.gameStrings.nameDescLocalization(potion.strId))
                        sTooltip.showTooltip()
                    }

                    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                        sTooltip.hideTooltip()
                    }
                })
                potionImage.addListener(object : ClickListener(Input.Buttons.RIGHT) {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        api.removePotion(slot.potion!!)
                    }
                })
            }
        }
    }

    fun updateRelicRow() {
        relicRow.clearChildren()
        runData.hero.relics.forEach { relic ->
            val image = Image(game.assets.get(relicAssetPath(relic.assetName), Texture::class.java))
            image.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    sTooltip.addNameDescLocalization(game.gameStrings.nameDescLocalization(relic.strId))
                    sTooltip.showTooltip()
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    sTooltip.hideTooltip()
                }
            })
            relicRow.add(image)
                .left()
                .prefHeight(64f)
                .prefWidth(64f)
        }
        relicRow.row()
    }

    fun setHpRelative(amount: Int) {
        setHp(hp + amount, hpMax)
    }

    fun setHp(hp: Int, hpMax: Int) {
        this.hp = hp
        this.hpMax = hpMax
        hpLabel.setText("HP: $hp/$hpMax")
    }

    fun setFluxRelative(amount: Int) {
        setFlux(min(flux + amount, fluxMax), fluxMax)
    }

    fun setFlux(flux: Int, fluxMax: Int) {
        this.flux = flux
        this.fluxMax = fluxMax
        fluxLabel.setText("Flux: $flux/$fluxMax")
    }

    @Subscribe
    fun handleTopRowUpdate(ev: TopRowUiUpdateEvent) {
        update()
    }

    fun update() {
        goldLabel.setText("Gold: ${runData.hero.gold}")
        setHp(runData.hero.hp, runData.hero.hpMax)
        setFlux(runData.hero.flux, runData.hero.fluxMax)
        updatePotions()
        updateRelicRow()
    }
}
