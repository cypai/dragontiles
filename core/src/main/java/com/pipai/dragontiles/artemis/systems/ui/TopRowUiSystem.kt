package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.events.PotionUseUiEvent
import com.pipai.dragontiles.artemis.events.TopRowUiUpdateEvent
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.potions.PotionType
import com.pipai.dragontiles.utils.potionAssetPath
import com.pipai.dragontiles.utils.relicAssetPath
import com.pipai.dragontiles.utils.su
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
    private val resolution = game.gameConfig.resolution

    private val rootTable = Table()
    private val topRow = Table()
    private val relicRow = Table()
    private var hp = runData.hero.hp
    private var hpMax = runData.hero.hpMax
    private var flux = runData.hero.flux
    private var fluxMax = runData.hero.fluxMax
    private var tempFluxMax = runData.hero.tempFluxMax
    private var gold = runData.hero.gold
    private val hpLabel = Label("HP: $hp/$hpMax", skin)
    private val fluxLabel = Label("Flux: $flux/$fluxMax", skin)
    private val goldLabel = Label("Gold: $gold", skin)
    private val potionTable = Table()

    private val topRowLabelWidth = su(2f)
    private val spacing = su(0.5f)

    private val sTooltip by system<TooltipSystem>()
    private val sEvent by system<EventSystem>()

    private lateinit var api: GlobalApi

    override fun initialize() {
        api = GlobalApi(game.data, game.assets, runData, sEvent)
        rootTable.setFillParent(true)
        topRow.background = skin.getDrawable("frameDrawable")
        topRow.add(Label(runData.hero.name, skin))
            .padLeft(spacing / 2f)
            .space(spacing)
            .left()
        topRow.add(hpLabel)
            .space(spacing)
            .width(topRowLabelWidth)
        topRow.add(fluxLabel)
            .space(spacing)
            .width(topRowLabelWidth)
        topRow.add(goldLabel)
            .space(spacing)
            .width(topRowLabelWidth)
        updatePotions()
        topRow.add(potionTable)
            .expand()

        updateRelicRow()

        rootTable.add(topRow)
            .width(resolution.width.toFloat())
            .height(su(0.7f))
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
        runData.hero.potionSlots.forEachIndexed { i, slot ->
            if (slot.potionId == null) {
                potionTable.add(Image(game.assets.get(potionAssetPath("empty.png"), Texture::class.java)))
                    .prefWidth(48f)
                    .prefHeight(48f)
            } else {
                val potion = game.data.getPotion(slot.potionId!!)
                val potionImage = Image(game.assets.get(potionAssetPath(potion.assetName), Texture::class.java))
                potionTable.add(potionImage)
                    .prefWidth(48f)
                    .prefHeight(48f)
                potionImage.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        if (isCombat) {
                            potionImage.color = Color.GRAY
                            sEvent.dispatch(PotionUseUiEvent(i))
                        } else {
                            if (potion.type == PotionType.UNIVERSAL) {
                                api.usePotion(i)
                            }
                        }
                    }

                    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                        sTooltip.addNameDescLocalization(game.gameStrings.nameDescLocalization(potion.id))
                        sTooltip.showTooltip()
                    }

                    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                        sTooltip.hideTooltip()
                    }
                })
                potionImage.addListener(object : ClickListener(Input.Buttons.RIGHT) {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        api.removePotionAtIndex(i)
                    }
                })
            }
        }
    }

    fun updateRelicRow() {
        relicRow.clearChildren()
        runData.hero.relicIds.forEach { relicInstance ->
            val relic = game.data.getRelic(relicInstance.id)
            val counterLabel = Label(if (relic.showCounter) relicInstance.counter.toString() else "", skin, "white")
            counterLabel.setAlignment(Align.bottomRight)
            val image = Image(game.assets.get(relicAssetPath(relic.assetName), Texture::class.java))
            val stack = Stack()
            stack.add(image)
            stack.add(counterLabel)
            stack.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    sTooltip.addNameDescLocalization(game.gameStrings.nameDescLocalization(relic.id))
                    sTooltip.showTooltip()
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    sTooltip.hideTooltip()
                }
            })
            relicRow.add(stack)
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
        setFlux(min(flux + amount, tempFluxMax).coerceAtLeast(0), tempFluxMax, fluxMax)
    }

    fun setTempMaxFluxRelative(amount: Int) {
        setFlux(flux, tempFluxMax + amount, fluxMax)
    }

    fun setFlux(flux: Int, tempFluxMax: Int, fluxMax: Int) {
        this.flux = flux
        this.tempFluxMax = tempFluxMax
        this.fluxMax = fluxMax
        if (tempFluxMax == fluxMax) {
            fluxLabel.setText("Flux: $flux/$fluxMax")
        } else {
            fluxLabel.setText("Flux: $flux/$tempFluxMax ($fluxMax)")
        }
    }

    @Subscribe
    fun handleTopRowUpdate(ev: TopRowUiUpdateEvent) {
        update()
    }

    fun update() {
        goldLabel.setText("Gold: ${runData.hero.gold}")
        if (!isCombat) {
            setHp(runData.hero.hp, runData.hero.hpMax)
            setFlux(runData.hero.flux, runData.hero.tempFluxMax, runData.hero.fluxMax)
        }
        updatePotions()
        updateRelicRow()
    }
}
