package com.pipai.dragontiles.misc

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

/** @author Xoppa
 */
class RadialSprite(textureRegion: TextureRegion) : Drawable {

    var texture: Texture? = null
        private set
    private val verts = FloatArray(60)
    private var x: Float = 0.toFloat()
    private var y: Float = 0.toFloat()
    private var angle: Float = 0.toFloat()
    private var width: Float = 0.toFloat()
    private var height: Float = 0.toFloat()
    private var u1: Float = 0.toFloat()
    private var u2: Float = 0.toFloat()
    private var v1: Float = 0.toFloat()
    private var v2: Float = 0.toFloat()
    private val du: Float
    private val dv: Float
    private var dirty = true
    private var draw = 0
    private val angleOffset = 270f
    private var originX: Float = 0.toFloat()
    private var originY: Float = 0.toFloat()
    private var scaleX = 1f
    private var scaleY = 1f

    private var leftWidth = 0f
    private var rightWidth = 0f
    private var topHeight = 0f
    private var bottomHeight = 0f
    private var minWidth = 0f
    private var minHeight = 0f

    init {
        this.texture = textureRegion.texture
        this.u1 = textureRegion.u
        this.v1 = textureRegion.v
        this.u2 = textureRegion.u2
        this.v2 = textureRegion.v2
        this.du = u2 - u1
        this.dv = v2 - v1
        this.width = textureRegion.regionWidth.toFloat()
        this.height = textureRegion.regionHeight.toFloat()
        setColor(Color.WHITE)
    }

    fun setColor(packedColor: Float) {
        for (i in 0..11)
            verts[i * 5 + 2] = packedColor
    }

    fun setColor(color: Color) {
        setColor(color.toFloatBits())
    }

    private fun vert(verts: FloatArray, offset: Int, x: Float, y: Float) {
        val u = u1 + du * ((x - this.x) / this.width)
        val v = v1 + dv * (1f - (y - this.y) / this.height)
        vert(verts, offset, x, y, u, v)
    }

    private fun vert(verts: FloatArray, offset: Int, x: Float, y: Float, u: Float, v: Float) {
        verts[offset] = this.x + originX + (x - this.x - originX) * scaleX
        verts[offset + 1] = this.y + originY + (y - this.y - originY) * scaleY
        verts[offset + 3] = u
        verts[offset + 4] = v
    }

    protected fun calculate(x: Float, y: Float, width: Float, height: Float, angle: Float, u0: Float, v0: Float, u1: Float, v1: Float) {
        if (!this.dirty && this.x == x && this.y == y && this.angle == angle && this.width == width && this.height == height
                && this.u1 == u0 && this.v2 == v1 && this.u2 == u1 && this.v2 == v1)
            return
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        this.angle = angle
        this.u1 = u0
        this.v1 = v0
        this.u2 = u1
        this.v2 = v1
        val centerX = width * 0.5f
        val centerY = height * 0.5f
        val x2 = x + width
        val y2 = y + height
        val xc = x + centerX
        val yc = y + centerY
        val ax = MathUtils.cosDeg(angle + angleOffset) // positive right, negative left
        val ay = MathUtils.sinDeg(angle + angleOffset) // positive top, negative bottom
        val txa = if (ax != 0f) Math.abs(centerX / ax) else 99999999f // intersection on left or right "wall"
        val tya = if (ay != 0f) Math.abs(centerY / ay) else 99999999f // intersection on top or bottom "wall"
        val t = Math.min(txa, tya)
        // tx and ty are the intersection points relative to centerX and centerY.
        val tx = t * ax
        val ty = t * ay
        vert(verts, BOTTOMRIGHT1, x + centerX, y)
        if (ax >= 0f) { // rotation on the rights half
            vert(verts, TOPLEFT1, x, y2)
            vert(verts, TOPRIGHT1, xc, y2)
            vert(verts, BOTTOMLEFT1, x, y)
            vert(verts, BOTTOMLEFT2, xc, yc)
            vert(verts, TOPLEFT2, xc, y2)
            if (txa < tya) { // rotation on the right side
                vert(verts, TOPRIGHT2, x2, y2)
                vert(verts, BOTTOMRIGHT2, x2, yc + ty)
                draw = 2
            } else if (ay > 0f) { // rotation on the top side
                vert(verts, BOTTOMRIGHT2, xc + tx, y2)
                vert(verts, TOPRIGHT2, xc + tx * 0.5f, y2)
                draw = 2
            } else { // rotation on the bottom side
                vert(verts, TOPRIGHT2, x2, y2)
                vert(verts, BOTTOMRIGHT2, x2, y)
                vert(verts, TOPLEFT3, xc, yc)
                vert(verts, TOPRIGHT3, x2, y)
                vert(verts, BOTTOMLEFT3, xc + tx, y)
                vert(verts, BOTTOMRIGHT3, xc + tx * 0.5f, y)
                draw = 3
            }
        } else { // rotation on the left half
            vert(verts, TOPRIGHT1, x + centerX, y + centerY)
            if (txa < tya) { // rotation on the left side
                vert(verts, BOTTOMLEFT1, x, y)
                vert(verts, TOPLEFT1, x, yc + ty)
                draw = 1
            } else if (ay < 0f) { // rotation on the bottom side
                vert(verts, TOPLEFT1, xc + tx, y)
                vert(verts, BOTTOMLEFT1, xc + tx * 0.5f, y)
                draw = 1
            } else { // rotation on the top side
                vert(verts, TOPLEFT1, x, y2)
                vert(verts, BOTTOMLEFT1, x, y)
                vert(verts, BOTTOMRIGHT2, xc, yc)
                vert(verts, BOTTOMLEFT2, x, y2)
                vert(verts, TOPLEFT2, xc + tx * 0.5f, y2)
                vert(verts, TOPRIGHT2, xc + tx, y2)
                draw = 2
            }
        }
        this.dirty = false
    }

    fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float, angle: Float) {
        var w = width
        var h = height
        if (w < 0) {
            scaleX = -1f
            w = -w
        }
        if (h < 0) {
            scaleY = -1f
            h = -h
        }
        calculate(x, y, w, h, angle, u1, v1, u2, v2)
        batch.draw(texture, verts, 0, 20 * draw)
    }

    fun draw(batch: Batch, x: Float, y: Float, angle: Float) {
        draw(batch, x, y, width, height, angle)
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        draw(batch, x, y, width, height, angle)
    }

    fun setOrigin(x: Float, y: Float) {
        if (originX == x && originY == y)
            return
        originX = x
        originY = y
        dirty = true
    }

    fun setScale(x: Float, y: Float) {
        if (scaleX == x && scaleY == y)
            return
        scaleX = x
        scaleY = y
        dirty = true
    }

    fun draw(batch: SpriteBatch, x: Float, y: Float, width: Float, height: Float) {
        draw(batch, x, y, width, height, this.angle)
    }

    fun getAngle(): Float {
        return this.angle
    }

    fun setAngle(angle: Float) {
        if (this.angle == angle)
            return
        this.angle = angle
        dirty = true
    }

    override fun getLeftWidth(): Float {
        return leftWidth
    }

    override fun setLeftWidth(leftWidth: Float) {
        this.leftWidth = leftWidth
    }

    override fun getRightWidth(): Float {
        return rightWidth
    }

    override fun setRightWidth(rightWidth: Float) {
        this.rightWidth = rightWidth
    }

    override fun getTopHeight(): Float {
        return topHeight
    }

    override fun setTopHeight(topHeight: Float) {
        this.topHeight = topHeight
    }

    override fun getBottomHeight(): Float {
        return bottomHeight
    }

    override fun setBottomHeight(bottomHeight: Float) {
        this.bottomHeight = bottomHeight
    }

    override fun getMinWidth(): Float {
        return minWidth
    }

    override fun setMinWidth(minWidth: Float) {
        this.minWidth = minWidth
    }

    override fun getMinHeight(): Float {
        return minHeight
    }

    override fun setMinHeight(minHeight: Float) {
        this.minHeight = minHeight
    }

    fun setTextureRegion(textureRegion: TextureRegion) {
        this.texture = textureRegion.texture
        this.u1 = textureRegion.u
        this.v1 = textureRegion.v
        this.u2 = textureRegion.u2
        this.v2 = textureRegion.v2
        this.dirty = true
    }

    fun width() = width
    fun height() = height

    companion object {
        private val TOPRIGHT1 = 0
        private val BOTTOMRIGHT1 = 5
        private val BOTTOMLEFT1 = 10
        private val TOPLEFT1 = 15
        private val TOPRIGHT2 = 20
        private val BOTTOMRIGHT2 = 25
        private val BOTTOMLEFT2 = 30
        private val TOPLEFT2 = 35
        private val TOPRIGHT3 = 40
        private val BOTTOMRIGHT3 = 45
        private val BOTTOMLEFT3 = 50
        private val TOPLEFT3 = 55
    }
}

