package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.EnemyClickEvent
import com.pipai.dragontiles.artemis.events.EnemyHoverEnterEvent
import com.pipai.dragontiles.artemis.events.EnemyHoverExitEvent
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.AssetType
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.dungeon.Encounter
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.*

class CombatantStateSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
    private val runData: RunData,
    private val encounter: Encounter
) : BaseSystem() {

    private val mXy by mapper<XYComponent>()
    private val mHero by mapper<HeroComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mSpine by mapper<SpineComponent>()
    private val mHoverable by mapper<HoverableComponent>()
    private val mClickable by mapper<ClickableComponent>()

    private val sCombat by system<CombatControllerSystem>()
    private val sTooltip by system<TooltipSystem>()

    private val heroHpBar = ProgressBar(0f, 100f, 1f, false, game.dtskin, "hpbar")
    private val heroHpLabel = Label("", game.skin, "whiteTiny")
    private val heroFluxBar = ProgressBar(0f, 100f, 1f, false, game.dtskin, "fluxbar")
    private val heroFluxLabel = Label("", game.skin, "whiteTiny")
    private val enemyEntityMap: MutableMap<Enemy, EntityId> = mutableMapOf()
    private val enemyTables: MutableMap<EntityId, EnemyUi> = mutableMapOf()

    private var t = 0f

    override fun initialize() {
        initHero()
        encounter.enemies.forEach { (enemy, position) ->
            initEnemy(enemy, position)
        }
    }

    fun initEnemy(enemy: Enemy, position: Vector2) {
        val entityId = world.create()
        val cXy = mXy.create(entityId)
        cXy.setXy(position)

        when (enemy.assetConfig.type) {
            AssetType.SPRITE -> {
                val cSprite = mSprite.create(entityId)
                cSprite.sprite = Sprite(game.assets.get(enemyAssetPath(enemy.assetName), Texture::class.java))
                cSprite.width = enemy.assetConfig.width
                cSprite.height = enemy.assetConfig.width / cSprite.sprite.width * cSprite.sprite.height
            }
            AssetType.SPINE -> {
                val cSpine = mSpine.create(entityId)
                cSpine.load(game.assets.get(spineAssetPath(enemy.assetName)))
                val scale = enemy.assetConfig.width / cSpine.skeleton.data.width
                cSpine.skeleton.setScale(scale, scale)
                cSpine.state.setAnimation(0, "Idle", true)
            }
        }

        val cEnemy = mEnemy.create(entityId)
        cEnemy.setByEnemy(enemy)

        val cHover = mHoverable.create(entityId)
        cHover.enterEvent = EnemyHoverEnterEvent(cEnemy)
        cHover.exitEvent = EnemyHoverExitEvent()

        mClickable.create(entityId).eventGenerator = { EnemyClickEvent(entityId, it) }

        enemyEntityMap[enemy] = entityId
        createUi(enemy, entityId)
        updateEnemyStats(enemy, enemy.hpMax, enemy.hpMax, enemy.flux, enemy.fluxMax)
    }

    private fun initHero() {
        val entityId = world.create()
        val cXy = mXy.create(entityId)
        cXy.setXy(13f, 4.5f)
        val cHero = mHero.create(entityId)
        cHero.setByRunData(runData)
        val cSprite = mSprite.create(entityId)
        cSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/heros/elementalist.png", Texture::class.java))
        cSprite.width = 2f
        cSprite.height = 2.5f

        val stateTable = Table()
        stateTable.background = game.skin.getDrawable("disabled")
        heroHpBar.setAnimateDuration(0.25f)
        heroHpLabel.setAlignment(Align.center)
        val hpStack = Stack()
        hpStack.add(heroHpBar)
        hpStack.add(heroHpLabel)
        heroFluxBar.setAnimateDuration(0.25f)
        heroFluxLabel.setAlignment(Align.center)
        val fluxStack = Stack()
        fluxStack.add(heroFluxBar)
        fluxStack.add(heroFluxLabel)
        stateTable.add(fluxStack)
            .width(cSprite.sprite.width)
            .bottom()
        stateTable.row()
        stateTable.add(hpStack)
            .bottom()
            .width(cSprite.sprite.width)
            .padBottom(2f)
        stateTable.row()
        updateHero(runData.hero.hp, runData.hero.hpMax, runData.hero.flux, runData.hero.fluxMax)
        stateTable.x = su(cXy.x)
        stateTable.y = su(cXy.y + 2.5f)
        stateTable.width = cSprite.sprite.width
        stateTable.height = stateTable.prefHeight
        stage.addActor(stateTable)
    }

    fun changeHeroHp(amount: Int) {
        val cHero = mHero.get(world.fetch(allOf(HeroComponent::class)).first())
        updateHero(cHero.hp + amount, cHero.hpMax, cHero.flux, cHero.fluxMax)
    }

    fun changeHeroFlux(amount: Int) {
        val cHero = mHero.get(world.fetch(allOf(HeroComponent::class)).first())
        updateHero(cHero.hp, cHero.hpMax, cHero.flux + amount, cHero.fluxMax)
    }

    fun changeHeroTempMaxFlux(amount: Int) {
        val cHero = mHero.get(world.fetch(allOf(HeroComponent::class)).first())
        updateHero(cHero.hp, cHero.hpMax, cHero.flux, cHero.fluxMax + amount)
    }

    fun updateHero(hp: Int, hpMax: Int, flux: Int, fluxMax: Int) {
        val cHero = mHero.get(world.fetch(allOf(HeroComponent::class)).first())
        cHero.hp = hp.coerceAtLeast(0).coerceAtMost(cHero.hpMax)
        cHero.hpMax = hpMax
        cHero.flux = flux.coerceAtLeast(0).coerceAtMost(cHero.fluxMax)
        cHero.fluxMax = fluxMax
        heroHpLabel.setText("${cHero.hp}/$hpMax")
        heroHpBar.value = cHero.hp.toFloat() / hpMax.toFloat() * 100f
        heroFluxLabel.setText("${cHero.flux}/$fluxMax")
        heroFluxBar.value = cHero.flux.toFloat() / fluxMax.toFloat() * 100f
    }

    fun enemyDefeated(enemy: Enemy) {
        val ui = enemyTables.remove(enemyEntityMap.remove(enemy))!!
        ui.table.remove()
    }

    override fun processSystem() {
        t += world.delta
        if (t > 1f) {
            t = 0f
            flipIntents()
        }
    }

    private fun flipIntents() {
        enemyTables.values.forEach { ui ->
            if (ui.intent2.drawable == null) {
                if (ui.intent1.drawable != null) {
                    (ui.intent1.drawable as SpriteDrawable).sprite.setAlpha(1f)
                }
            } else {
                val i1 = ui.intent1.drawable as SpriteDrawable
                val i2 = ui.intent2.drawable as SpriteDrawable
                if (i1.sprite.color.a > 0f) {
                    i1.sprite.setAlpha(0f)
                    i2.sprite.setAlpha(1f)
                } else {
                    i1.sprite.setAlpha(1f)
                    i2.sprite.setAlpha(0f)
                }
            }
        }
    }

    private fun createUi(enemy: Enemy, entityId: EntityId) {
        val cXy = mXy.get(entityId)
        val screenXy = game.viewport.project(cXy.toVector2())
        val cEnemy = mEnemy.get(entityId)
        val ui = when (enemy.assetConfig.type) {
            AssetType.SPRITE -> {
                val cSprite = mSprite.get(entityId)
                val ui = EnemyUi.create(game, enemy, cSprite.width)
                ui.table.x = screenXy.x
                ui.table.y = screenXy.y + su(cSprite.height)
                ui
            }
            AssetType.SPINE -> {
                val cSpine = mSpine.get(entityId)
                val skeleton = cSpine.skeleton
                val scale = enemy.assetConfig.width / cSpine.skeleton.data.width
                val ui = EnemyUi.create(game, enemy, enemy.assetConfig.width)
                ui.table.x = screenXy.x - su(enemy.assetConfig.width) / 2f
                ui.table.y = screenXy.y + su(skeleton.data.height * scale)
                ui
            }
        }
        ui.nameLabel.setText(game.gameStrings.nameLocalization(cEnemy.enemy))

        enemyTables[entityId] = ui
        stage.addActor(ui.table)
    }

    fun changeEnemyFlux(enemy: Enemy, amount: Int) {
        val entityId = enemyEntityMap[enemy]!!
        val cEnemy = mEnemy.get(entityId)
        cEnemy.flux += amount
        if (cEnemy.flux > cEnemy.fluxMax) {
            cEnemy.flux = cEnemy.fluxMax
        }
        updateEnemyStats(enemy, cEnemy.hp, cEnemy.hpMax, cEnemy.flux, cEnemy.fluxMax)
    }

    fun changeEnemyHp(enemy: Enemy, amount: Int) {
        val entityId = enemyEntityMap[enemy]
        if (entityId != null) {
            val cEnemy = mEnemy.get(entityId)
            cEnemy.hp += amount
            updateEnemyStats(enemy, cEnemy.hp, cEnemy.hpMax, cEnemy.flux, cEnemy.fluxMax)
        }
    }

    fun updateEnemyStats(enemy: Enemy, hp: Int, hpMax: Int, flux: Int, fluxMax: Int) {
        val entityId = enemyEntityMap[enemy]!!
        val cEnemy = mEnemy.get(entityId)
        val ui = enemyTables[entityId]!!
        cEnemy.hp = hp
        cEnemy.hpMax = hpMax
        cEnemy.flux = flux.coerceAtLeast(0).coerceAtMost(fluxMax)
        cEnemy.fluxMax = fluxMax
        ui.hpLabel.setText("${cEnemy.hp}/$hpMax")
        ui.hpBar.value = hp.toFloat()
        if (ui.fluxLabel != null) {
            ui.fluxLabel.setText("${cEnemy.flux}/$fluxMax")
        }
        if (ui.fluxBar != null) {
            ui.fluxBar.value = flux.toFloat()
        }
    }

    fun updateAllIntents() {
        world.fetch(allOf(EnemyComponent::class))
            .map { mEnemy.get(it).enemy }
            .filter { it.hp > 0 }
            .forEach { updateIntent(it, sCombat.controller.api.getEnemyIntent(it)) }
    }

    fun updateIntent(enemy: Enemy, intent: Intent?) {
        val entityId = enemyEntityMap[enemy]!!
        val cEnemy = mEnemy.get(entityId)
        val ui = enemyTables[entityId]!!
        cEnemy.intent = intent

        clearIntent(ui)
        if (intent != null) {
            when (intent) {
                is AttackIntent -> {
                    updateAttackIntent(ui, intent)
                    updateTooltip(ui, enemy, "Aggressive", "This enemy is about to attack.")
                }
                is BuffIntent -> {
                    if (intent.attackIntent == null) {
                        updateBuffIntent(ui, false)
                        updateTooltip(ui, enemy, "Buffing", "This enemy is about to buff itself.")
                    } else {
                        updateAttackIntent(ui, intent.attackIntent)
                        updateBuffIntent(ui, true)
                        updateTooltip(ui, enemy, "Aggressive", "This enemy is about to both attack and buff itself.")
                    }
                }
                is VentIntent -> {
                    if (intent.status == null) {
                        updateVentIntent(ui, intent.amount, false)
                        updateTooltip(ui, enemy, "Defensive", "This enemy is about to vent flux.")
                    } else {
                        updateVentIntent(ui, intent.amount, false)
                        updateBuffIntent(ui, true)
                        updateTooltip(ui, enemy, "Defensive", "This enemy is about to vent flux and buff itself.")
                    }
                }
                is DebuffIntent -> {
                    if (intent.attackIntent == null) {
                        updateDebuffIntent(ui, false)
                        updateTooltip(
                            ui,
                            enemy,
                            "Debuffing",
                            "This enemy is about to inflict a negative effect on you."
                        )
                    } else {
                        updateAttackIntent(ui, intent.attackIntent)
                        updateDebuffIntent(ui, true)
                        updateTooltip(
                            ui,
                            enemy,
                            "Aggressive",
                            "This enemy is about to attack and inflict a negative effect on you."
                        )
                    }
                }
                is StrategicIntent -> {
                    val buffs = intent.buffs.isNotEmpty()
                    val debuffs = intent.debuffs.isNotEmpty() || intent.inflictTileStatuses.isNotEmpty()
                    when {
                        buffs && debuffs -> {
                            updateBuffIntent(ui, false)
                            updateDebuffIntent(ui, true)
                            updateTooltip(
                                ui,
                                enemy,
                                "Strategic",
                                "This enemy is about to buff itself and inflict a negative effect on you."
                            )
                        }
                        buffs && !debuffs -> {
                            updateBuffIntent(ui, false)
                            updateTooltip(ui, enemy, "Buffing", "This enemy is about to buff itself.")
                        }
                        !buffs && debuffs -> {
                            updateDebuffIntent(ui, false)
                            updateTooltip(
                                ui,
                                enemy,
                                "Debuffing",
                                "This enemy is about to inflict a negative effect on you."
                            )
                        }
                        else -> {
                            updateUnknownIntent(ui)
                            updateTooltip(
                                ui,
                                enemy,
                                "Unknown",
                                "This enemy is about to do something strange."
                            )
                        }
                    }
                }
                is DoNothingIntent -> {
                    val assetName = when (intent.type) {
                        DoNothingType.STUNNED -> "stunned.png"
                        DoNothingType.SLEEPING -> "sleeping.png"
                        DoNothingType.WAITING -> "waiting.png"
                    }
                    val name = when (intent.type) {
                        DoNothingType.STUNNED -> "Stunned"
                        DoNothingType.SLEEPING -> "Sleeping"
                        DoNothingType.WAITING -> "Waiting"
                    }
                    val description = when (intent.type) {
                        DoNothingType.STUNNED -> "This enemy is stunned."
                        DoNothingType.SLEEPING -> "This enemy is sleeping."
                        DoNothingType.WAITING -> "This enemy is not doing anything."
                    }
                    ui.intent1.drawable = SpriteDrawable(
                        Sprite(
                            game.assets.get(
                                intentAssetPath(assetName),
                                Texture::class.java
                            )
                        )
                    )
                    updateTooltip(ui, enemy, name, description)
                }
                is FumbleIntent -> {
                    when (val innerIntent = intent.intent) {
                        is AttackIntent -> {
                            updateAttackIntent(ui, innerIntent)
                            updateDebuffIntent(ui, true)
                            updateTooltip(
                                ui,
                                enemy,
                                "Aggressive",
                                "This enemy is about to attack and inflict a negative effect on you."
                            )
                        }
                        is BuffIntent -> {
                            updateDebuffIntent(ui, false)
                            updateBuffIntent(ui, true)
                            updateTooltip(
                                ui,
                                enemy,
                                "Strategic",
                                "This enemy is about to buff and inflict a negative effect on you."
                            )
                        }
                        is VentIntent -> {
                            updateVentIntent(ui, innerIntent.amount, false)
                            updateDebuffIntent(ui, true)
                            updateTooltip(
                                ui,
                                enemy,
                                "Strategic",
                                "This enemy is about to vent flux and inflict a negative effect on you."
                            )
                        }
                        else -> {
                            updateDebuffIntent(ui, false)
                            updateTooltip(
                                ui,
                                enemy,
                                "Debuffing",
                                "This enemy is about to inflict a negative effect on you."
                            )
                        }
                    }
                }
                else -> {
                    ui.intent1.drawable = SpriteDrawable(
                        Sprite(
                            game.assets.get(
                                intentAssetPath("unknown.png"),
                                Texture::class.java
                            )
                        )
                    )
                    updateTooltip(ui, enemy, "Unknown", "This enemy is about to do something unusual.")
                }
            }
        }
        if (ui.intent2.drawable != null) {
            (ui.intent2.drawable as SpriteDrawable).sprite.setAlpha(0f)
        }
    }

    private fun updateTooltip(ui: EnemyUi, enemy: Enemy, header: String, text: String) {
        ui.table.clearListeners()
        ui.table.addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                sTooltip.addLocalized(enemy)
                sTooltip.addText(header, text, false)
                sTooltip.showTooltip()
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                sTooltip.hideTooltip()
            }
        })
    }

    private fun updateAttackIntent(ui: EnemyUi, intent: AttackIntent) {
        val attackPower =
            sCombat.controller.api.calculateDamageOnHero(
                intent.enemy,
                intent.element,
                intent.attackPower,
                intent.flags()
            )
        if (intent.multistrike == 1) {
            ui.attackLabel.setText(attackPower)
        } else {
            ui.attackLabel.setText("${intent.multistrike}x $attackPower")
        }
        ui.intent1.drawable = when (intent.element) {
            Element.FIRE -> {
                SpriteDrawable(
                    Sprite(
                        game.assets.get(
                            intentAssetPath("fire.png"),
                            Texture::class.java
                        )
                    )
                )
            }
            Element.ICE -> SpriteDrawable(
                Sprite(
                    game.assets.get(
                        intentAssetPath("ice.png"),
                        Texture::class.java
                    )
                )
            )
            Element.LIGHTNING -> SpriteDrawable(
                Sprite(
                    game.assets.get(
                        intentAssetPath("lightning.png"),
                        Texture::class.java
                    )
                )
            )
            Element.NONE -> SpriteDrawable(
                Sprite(
                    game.assets.get(
                        intentAssetPath("non_element.png"),
                        Texture::class.java
                    )
                )
            )
        }
    }

    private fun updateBuffIntent(ui: EnemyUi, isSecond: Boolean) {
        val image = if (isSecond) {
            ui.intent2
        } else {
            ui.intent1
        }
        image.drawable = SpriteDrawable(
            Sprite(
                game.assets.get(
                    intentAssetPath("buff.png"),
                    Texture::class.java
                )
            )
        )
    }

    private fun updateDebuffIntent(ui: EnemyUi, isSecond: Boolean) {
        val image = if (isSecond) {
            ui.intent2
        } else {
            ui.intent1
        }
        image.drawable = SpriteDrawable(
            Sprite(
                game.assets.get(
                    intentAssetPath("debuff.png"),
                    Texture::class.java
                )
            )
        )
    }

    private fun updateVentIntent(ui: EnemyUi, amount: Int, isSecond: Boolean) {
        ui.ventLabel.setText("-$amount")
        val image = if (isSecond) {
            ui.intent2
        } else {
            ui.intent1
        }
        image.drawable = SpriteDrawable(
            Sprite(
                game.assets.get(
                    intentAssetPath("vent.png"),
                    Texture::class.java
                )
            )
        )
    }

    private fun updateUnknownIntent(ui: EnemyUi) {
        ui.intent1.drawable = SpriteDrawable(
            Sprite(
                game.assets.get(
                    intentAssetPath("unknown.png"),
                    Texture::class.java
                )
            )
        )
    }

    private fun clearIntent(ui: EnemyUi) {
        ui.attackLabel.setText("")
        ui.ventLabel.setText("")
        ui.intent1.drawable = null
        ui.intent2.drawable = null
    }

    data class EnemyUi(
        val table: Table,
        val attackLabel: Label,
        val ventLabel: Label,
        val intent1: Image,
        val intent2: Image,
        val nameLabel: Label,
        val hpBar: ProgressBar,
        val hpLabel: Label,
        val fluxBar: ProgressBar?,
        val fluxLabel: Label?,
    ) {
        companion object {
            fun create(game: DragonTilesGame, enemy: Enemy, width: Float): EnemyUi {
                val table = Table()
                table.background = game.skin.getDrawable("disabled")
                table.touchable = Touchable.enabled

                val numbersTable = Table()
                val ventLabel = Label("", game.skin, "whiteSmall")
                val attackLabel = Label("", game.skin, "whiteSmall")
                ventLabel.setAlignment(Align.right)
                attackLabel.setAlignment(Align.right)
                numbersTable.add(ventLabel)
                    .top()
                    .left()
                    .prefHeight(su(0.2f))
                numbersTable.row()
                numbersTable.add(attackLabel)
                    .bottom()
                    .left()
                    .prefHeight(su(0.2f))

                val intentStack = Stack()
                val intent1 = Image()
                val intent2 = Image()
                intentStack.add(intent2)
                intentStack.add(intent1)
                intentStack.add(numbersTable)

                table.add(intentStack)
                    .size(su(0.5f))

                val nameLabel = Label("", game.skin, "whiteTiny")
                nameLabel.setAlignment(Align.center)
                nameLabel.wrap = true
                table.add(nameLabel)
                    .width(su(width - 0.5f))
                    .height(nameLabel.prefHeight)
                table.row()

                val stateTable = Table()
                stateTable.background = game.skin.getDrawable("disabled")
                val hpBar = ProgressBar(0f, enemy.hpMax.toFloat(), 1f, false, game.dtskin, "hpbar")
                hpBar.setAnimateDuration(0.25f)
                val hpLabel = Label("", game.skin, "whiteTiny")
                hpLabel.setAlignment(Align.center)
                val hpStack = Stack()
                hpStack.add(hpBar)
                hpStack.add(hpLabel)
                var fluxBar: ProgressBar? = null
                var fluxLabel: Label? = null
                if (enemy.fluxMax > 0) {
                    fluxBar = ProgressBar(0f, enemy.fluxMax.toFloat(), 1f, false, game.dtskin, "fluxbar")
                    fluxBar.setAnimateDuration(0.25f)
                    fluxLabel = Label("", game.skin, "whiteTiny")
                    fluxLabel.setAlignment(Align.center)
                    val fluxStack = Stack()
                    fluxStack.add(fluxBar)
                    fluxStack.add(fluxLabel)
                    stateTable.add(fluxStack)
                        .width(su(width))
                        .bottom()
                    stateTable.row()
                }
                stateTable.add(hpStack)
                    .bottom()
                    .width(su(width))
                    .padBottom(2f)
                stateTable.row()
                table.add(stateTable)
                    .colspan(2)
                    .width(su(width))
                table.width = su(width)
                table.height = table.prefHeight
                return EnemyUi(
                    table,
                    attackLabel,
                    ventLabel,
                    intent1,
                    intent2,
                    nameLabel,
                    hpBar,
                    hpLabel,
                    fluxBar,
                    fluxLabel,
                )
            }
        }
    }
}
