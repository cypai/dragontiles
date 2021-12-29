package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.EntitySubscription
import com.artemis.utils.IntBag
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.EnemyClickEvent
import com.pipai.dragontiles.artemis.events.EnemyHoverEnterEvent
import com.pipai.dragontiles.artemis.events.EnemyHoverExitEvent
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.dungeon.Encounter
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.*

class CombatantStateSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
    private val encounter: Encounter
) : NoProcessingSystem() {

    private val mXy by mapper<XYComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mHoverable by mapper<HoverableComponent>()
    private val mClickable by mapper<ClickableComponent>()

    private val sCombat by system<CombatControllerSystem>()
    private val sTooltip by system<TooltipSystem>()

    private val enemyEntityMap: MutableMap<Enemy, EntityId> = mutableMapOf()
    private val combatantTables: MutableMap<EntityId, CombatantUi> = mutableMapOf()

    override fun initialize() {
        encounter.enemies.forEach { (enemy, position) ->
            val entityId = world.create()
            val cXy = mXy.create(entityId)
            cXy.setXy(position)

            val cSprite = mSprite.create(entityId)
            cSprite.sprite = Sprite(game.assets.get(enemyAssetPath(enemy.assetName), Texture::class.java))

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
    }

    fun enemyDefeated(enemy: Enemy) {
        val ui = combatantTables.remove(enemyEntityMap.remove(enemy))!!
        ui.table.remove()
    }

    private fun createUi(enemy: Enemy, entityId: EntityId) {
        val cXy = mXy.get(entityId)
        val cSprite = mSprite.get(entityId)
        val cEnemy = mEnemy.get(entityId)
        val ui = CombatantUi.create(game, enemy, cSprite.sprite.width)
        ui.nameLabel.setText(game.gameStrings.nameLocalization(cEnemy.enemy).name)

        combatantTables[entityId] = ui
        ui.table.x = cXy.x
        ui.table.y = cXy.y + cSprite.sprite.height
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
        val entityId = enemyEntityMap[enemy]!!
        val cEnemy = mEnemy.get(entityId)
        cEnemy.hp += amount
        updateEnemyStats(enemy, cEnemy.hp, cEnemy.hpMax, cEnemy.flux, cEnemy.fluxMax)
    }

    fun updateEnemyStats(enemy: Enemy, hp: Int, hpMax: Int, flux: Int, fluxMax: Int) {
        val entityId = enemyEntityMap[enemy]!!
        val cEnemy = mEnemy.get(entityId)
        val ui = combatantTables[entityId]!!
        cEnemy.hp = hp
        cEnemy.hpMax = hpMax
        cEnemy.flux = flux
        cEnemy.fluxMax = fluxMax
        ui.hpLabel.setText("$hp/$hpMax")
        ui.hpBar.value = hp.toFloat()
        if (ui.fluxLabel != null) {
            ui.fluxLabel.setText("$flux/$fluxMax")
        }
        if (ui.fluxBar != null) {
            ui.fluxBar.value = flux.toFloat()
        }
    }

    fun updateIntent(enemy: Enemy, intent: Intent?) {
        val entityId = enemyEntityMap[enemy]!!
        val cEnemy = mEnemy.get(entityId)
        val ui = combatantTables[entityId]!!
        cEnemy.intent = intent

        clearIntent(ui)
        if (intent != null) {
            when (intent) {
                is AttackIntent -> {
                    updateAttackIntent(ui, intent)
                    updateTooltip(ui, "Aggressive", "This enemy is about to attack.")
                }
                is BuffIntent -> {
                    if (intent.attackIntent == null) {
                        updateBuffIntent(ui, false)
                        updateTooltip(ui, "Buffing", "This enemy is about to buff itself.")
                    } else {
                        updateAttackIntent(ui, intent.attackIntent)
                        updateBuffIntent(ui, true)
                        updateDoubleIntent(ui)
                        updateTooltip(ui, "Aggressive", "This enemy is about to both attack and buff itself.")
                    }
                }
                is VentIntent -> {
                    if (intent.status == null) {
                        updateVentIntent(ui, intent.amount, false)
                        updateTooltip(ui, "Defensive", "This enemy is about to vent flux.")
                    } else {
                        updateVentIntent(ui, intent.amount, false)
                        updateBuffIntent(ui, true)
                        updateDoubleIntent(ui)
                        updateTooltip(ui, "Defensive", "This enemy is about to vent flux and buff itself.")
                    }
                }
                is DebuffIntent -> {
                    if (intent.attackIntent == null) {
                        updateDebuffIntent(ui, false)
                        updateTooltip(ui, "Debuffing", "This enemy is about to inflict a negative effect on you.")
                    } else {
                        updateAttackIntent(ui, intent.attackIntent)
                        updateDebuffIntent(ui, true)
                        updateDoubleIntent(ui)
                        updateTooltip(ui, "Aggressive", "This enemy is about to attack and inflict a negative effect on you.")
                    }
                }
                is StunnedIntent -> {
                    ui.intent1.drawable = SpriteDrawable(
                        Sprite(
                            game.assets.get(
                                intentAssetPath("stunned.png"),
                                Texture::class.java
                            )
                        )
                    )
                    updateTooltip(ui, "Stunned", "This enemy is stunned.")
                }
                is FumbleIntent -> {
                    when (val innerIntent = intent.intent) {
                        is AttackIntent -> {
                            updateAttackIntent(ui, innerIntent)
                            updateDebuffIntent(ui, true)
                            updateTooltip(ui, "Aggressive", "This enemy is about to attack and inflict a negative effect on you.")
                        }
                        is BuffIntent -> {
                            updateDebuffIntent(ui, false)
                            updateBuffIntent(ui, true)
                            updateTooltip(ui, "Strategic", "This enemy is about to buff and inflict a negative effect on you.")
                        }
                        else -> {
                            updateDebuffIntent(ui, false)
                            updateTooltip(ui, "Debuffing", "This enemy is about to inflict a negative effect on you.")
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
                    updateTooltip(ui, "Unknown", "This enemy is about to do something unusual.")
                }
            }
        }
    }

    private fun updateTooltip(ui: CombatantUi, header: String, text: String) {
        ui.table.clearListeners()
        ui.table.addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                sTooltip.addText(header, text, false)
                sTooltip.showTooltip()
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                sTooltip.hideTooltip()
            }
        })
    }

    private fun updateDoubleIntent(ui: CombatantUi) {
        val i1 = ui.intent1.drawable as SpriteDrawable
        val i2 = ui.intent2.drawable as SpriteDrawable
        i1.sprite.setAlpha(0.6f)
        i2.sprite.setAlpha(0.6f)
    }

    private fun updateAttackIntent(ui: CombatantUi, intent: AttackIntent) {
        val attackPower =
            sCombat.controller.api.calculateDamageOnHero(intent.enemy, intent.element, intent.attackPower)
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

    private fun updateBuffIntent(ui: CombatantUi, isSecond: Boolean) {
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

    private fun updateDebuffIntent(ui: CombatantUi, isSecond: Boolean) {
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

    private fun updateVentIntent(ui: CombatantUi, amount: Int, isSecond: Boolean) {
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

    private fun clearIntent(ui: CombatantUi) {
        ui.attackLabel.setText("")
        ui.ventLabel.setText("")
        ui.intent1.drawable = null
        ui.intent2.drawable = null
    }

    data class CombatantUi(
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
            fun create(game: DragonTilesGame, enemy: Enemy, width: Float): CombatantUi {
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
                    .prefHeight(16f)
                numbersTable.row()
                numbersTable.add(attackLabel)
                    .bottom()
                    .left()
                    .prefHeight(16f)

                val intentStack = Stack()
                val intent1 = Image()
                val intent2 = Image()
                intentStack.add(intent2)
                intentStack.add(intent1)
                intentStack.add(numbersTable)

                table.add(intentStack)
                    .size(48f)
                val nameLabel = Label("", game.skin, "whiteSmall")
                nameLabel.setAlignment(Align.center)
                table.add(nameLabel)
                    .expand()
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
                        .width(width)
                        .bottom()
                    stateTable.row()
                }
                stateTable.add(hpStack)
                    .bottom()
                    .width(width)
                    .padBottom(2f)
                stateTable.row()
                table.add(stateTable)
                    .colspan(2)
                    .width(width)
                table.width = width
                table.height = table.prefHeight
                return CombatantUi(
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
