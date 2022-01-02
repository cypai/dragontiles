package com.pipai.dragontiles.artemis.components

import com.artemis.Component
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.AnimationStateData
import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.SkeletonData
import com.pipai.dragontiles.misc.RadialSprite
import net.mostlyoriginal.api.event.common.Event

class XYComponent : Component() {
    var x = 0f
    var y = 0f

    fun setXy(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setXy(vec: Vector2) {
        this.x = vec.x
        this.y = vec.y
    }

    fun toVector2() = Vector2(x, y)
}

class AnchorComponent : Component() {
    var x = 0f
    var y = 0f

    fun setXy(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setXy(vec: Vector2) {
        this.x = vec.x
        this.y = vec.y
    }

    fun toVector2() = Vector2(x, y)
}

class PathInterpolationComponent : Component() {
    lateinit var interpolation: Interpolation
    var t: Float = 0f
    var maxT: Float = 0f

    var onEndpoint: ((PathInterpolationComponent) -> Unit)? = null
    var onEnd = EndStrategy.REMOVE

    val endpoints: MutableList<Vector2> = mutableListOf()
    var endpointIndex = 0

    fun setPath(start: Vector2, end: Vector2, maxT: Float, interpolation: Interpolation, onEndStrategy: EndStrategy) {
        endpoints.clear()
        endpoints.add(start)
        endpoints.add(end)
        t = 0f
        this.maxT = maxT
        this.interpolation = interpolation
        onEnd = onEndStrategy
    }

    fun getCurrentPos(): Vector2 {
        val a = t / maxT
        val start = endpoints[endpointIndex]
        val end = endpoints[endpointIndex + 1]
        return Vector2(
            interpolation.apply(start.x, end.x, a),
            interpolation.apply(start.y, end.y, a)
        )
    }
}

enum class EndStrategy {
    REMOVE, DESTROY, RESTART, REVERSE_THEN_REMOVE
}

class TimerComponent : Component() {
    var t: Float = 0f
    var maxT: Float = 0f

    var onEnd: EndStrategy = EndStrategy.REMOVE
    var onEndCallback: (() -> Unit)? = null
}

class OrthographicCameraComponent : Component() {

    val camera: OrthographicCamera =
        OrthographicCamera(Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat())

    init {
        camera.setToOrtho(false, Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat())
    }

}

class MutualDestroyComponent : Component() {
    val ids: MutableList<Int> = mutableListOf()
}

class DepthComponent : Component() {
    var depth = 0
}

class SpriteComponent : Component() {
    lateinit var sprite: Sprite
    var width = 0f
    var height = 0f
}

class SpineComponent : Component() {
    lateinit var skeleton: Skeleton
    lateinit var stateData: AnimationStateData
    lateinit var state: AnimationState

    fun load(skeletonData: SkeletonData) {
        skeleton = Skeleton(skeletonData)
        stateData = AnimationStateData(skeletonData)
        state = AnimationState(stateData)
    }
}

class ActorComponent : Component() {
    lateinit var actor: Actor
}

class RadialSpriteComponent : Component() {
    lateinit var sprite: RadialSprite
}

class AlphaInterpolationComponent : Component() {
    lateinit var interpolation: Interpolation
    var t: Float = 0f
    var maxT: Float = 0f

    var onEndpoint: ((AlphaInterpolationComponent) -> Unit)? = null
    var onEnd = EndStrategy.REMOVE

    var startAlpha = 0f
    var targetAlpha = 0f

    fun set(startAlpha: Float, targetAlpha: Float, maxT: Float, interpolation: Interpolation, onEnd: EndStrategy) {
        this.interpolation = interpolation
        t = 0f
        this.maxT = maxT
        this.onEnd = onEnd
        this.startAlpha = startAlpha
        this.targetAlpha = targetAlpha
    }
}

class ClickableComponent : Component() {
    var eventGenerator: ((Int) -> Event)? = null
    var callback: (() -> Unit)? = null
}

class HoverableComponent : Component() {
    var hovering: Boolean = false
    var enterEvent: Event? = null
    var exitEvent: Event? = null
    var enterCallback: (() -> Unit)? = null
    var exitCallback: (() -> Unit)? = null
    var recheck = false
}

class TextLabelComponent : Component() {
    var text = ""
    var size: TextLabelSize = TextLabelSize.NORMAL
    var xOffset = 0f
    var yOffset = 0f
    var color: Color = Color.WHITE
}

enum class TextLabelSize {
    NORMAL, SMALL, TINY
}
