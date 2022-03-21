package gargoyle.xenox.game.scr.menu

import gargoyle.xenox.game.scr.menu.JFlame.FlameParams.Align
import gargoyle.xenox.util.log.Log
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.awt.image.MemoryImageSource
import java.awt.image.PixelGrabber
import java.io.Serializable
import java.util.Random
import javax.swing.JComponent

class JFlame(params: FlameParams) : JComponent(), Runnable {
    private val pal = IntArray(INT256)
    private val params: FlameParams
    private val rnd = Random()
    private val twColor = IntArray(TW_MAX_COLOR)

    private var active = true
    private var alpha = 0

    private var art: Image? = null
    private lateinit var buf: IntArray
    private var burn = true

    var isCanDisplay = false
        private set
    private var colMask = INT0x00FF0000
    private var colShift = INT16

    private var flameImage: Image? = null

    private var flameSource: MemoryImageSource? = null

    var isRunning = false
        private set

    private var thread: Thread? = null
    private var twTimer: Long = 0
    private lateinit var twWords: IntArray
    private var xSize = 0
    private var ySize = 0

    init {
        this.params = FlameParams(params)
    }

    fun destroy() {
        isCanDisplay = false
        if (thread != null) {
            if (art != null) {
                art!!.flush()
            }
            isRunning = false
            thread = null
        }
    }

    fun fade(): Long {
        burn = false
        return params.fadeDelay
    }

    fun init() {
        background = Color.BLACK
        isCanDisplay = false
        if (thread == null) {
            thread = Thread(this, JFlame::class.java.name)
            thread!!.priority = Thread.MIN_PRIORITY
            thread!!.start()
        } else {
            if (thread!!.state == Thread.State.NEW) {
                thread!!.start()
            }
        }
    }

    private fun makePal(aSolid: Boolean) {
        if (aSolid) {
            for (i in 0 until TW_MAX_COLOR) {
                twColor[i] = pal[INT210 + rnd.nextInt(INT40) and INT0xff]
            }
        }
    }

    private fun makePal(n: Int) {
        when (n) {
            2 -> makePal2()
            3 -> makePal3()
            4 -> makePal4()
            5 -> makePal5()
            6 -> makePal6()
            1 -> makePal1()
            else -> makePal1()
        }
    }

    private fun makePal1() {
        colMask = INT0x00FF0000
        colShift = INT16
        for (i in 0 until INT256) {
            pal[i] = INT0xFF000000
        }
        var r = 1
        var g = 1
        var i1 = 3
        var i2 = 5
        for (i in 1 until INT60) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 5
                r++
            }
            if (i2 == 0) {
                i2 = 7
                g++
            }
            i1--
            i2--
        }
        for (i in INT60 until INT90) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 3
                r++
            }
            if (i2 == 0) {
                i2 = 6
                g++
            }
            i1--
            i2--
        }
        for (i in INT90 until INT130) {
            pal[i] = rgb(r, g, i)
            if (r < INT255) {
                r++
            }
            if (i2 == 0) {
                i2 = 6
                g++
            }
            i2--
        }
        for (i in INT130 until INT210) {
            pal[i] = rgb(r, g, i)
            r += 2
            if (r >= INT255) {
                r = INT255
            }
            if (i2 == 0) {
                i2 = 5
                g++
            }
            i2--
        }
        for (i in INT210 until INT256) {
            pal[i] = rgb(r, g, i)
            r += 3
            if (r >= INT255) {
                r = INT255
            }
            if (i2 == 0) {
                i2 = 4
                g++
            }
            i2--
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = pal[INT240 - i] and INT0xFFFFFF00 or pal[INT240 - i] shr INT16 and INT0xff
        }
    }

    private fun makePal2() {
        colMask = INT65280
        colShift = INT8
        for (i in 0 until INT256) {
            pal[i] = INT0xFF000000
        }
        var g = 1
        var b = 1
        var i1 = 3
        var i2 = 5
        for (i in 1 until INT60) {
            pal[i] = rgb(i, g, b)
            if (i1 == 0) {
                i1 = 5
                g++
            }
            if (i2 == 0) {
                i2 = 7
                b++
            }
            i1--
            i2--
        }
        for (i in INT60 until INT90) {
            pal[i] = rgb(i, g, b)
            if (i1 == 0) {
                i1 = 3
                g++
            }
            if (i2 == 0) {
                i2 = 5
                b++
            }
            i1--
            i2--
        }
        for (i in INT90 until INT130) {
            pal[i] = rgb(i, g, b)
            if (g < INT255) {
                g++
            }
            if (b < INT255) {
                b++
            }
            i2--
        }
        for (i in INT130 until INT210) {
            pal[i] = rgb(i, g, b)
            g += 2
            if (g >= INT255) {
                g = INT255
            }
            if (b < INT255) {
                b++
            }
        }
        for (i in INT210 until INT256) {
            pal[i] = rgb(i, g, b)
            g += 3
            if (g >= INT255) {
                g = INT255
            }
            b += 2
            if (b >= INT255) {
                b = INT255
            }
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = pal[INT240 - i] and INT0xFF00FF00 or INT0x00FF0000 and pal[INT240 - i] shl INT8
        }
    }

    private fun makePal3() {
        colMask = INT255
        colShift = 0
        for (i in 0 until INT256) {
            pal[i] = INT0xFF000000
        }
        var r = 1
        var b = 1
        var i1 = 3
        var i2 = 5
        for (i in 1 until INT60) {
            pal[i] = rgb(r, i, b)
            if (i1 == 0) {
                i1 = 5
                r++
            }
            if (i2 == 0) {
                i2 = 7
                b++
            }
            i1--
            i2--
        }
        for (i in INT60 until INT90) {
            pal[i] = rgb(r, i, b)
            if (i1 == 0) {
                i1 = 3
                r++
            }
            if (i2 == 0) {
                i2 = 5
                b++
            }
            i1--
            i2--
        }
        for (i in INT90 until INT130) {
            pal[i] = rgb(r, i, b)
            if (r < INT255) {
                r++
            }
            if (b < INT255) {
                b++
            }
            i2--
        }
        for (i in INT130 until INT210) {
            pal[i] = rgb(r, i, b)
            ++r
            if (r >= INT255) {
                r = INT255
            }
            if (b < INT255) {
                b++
            }
        }
        for (i in INT210 until INT256) {
            pal[i] = rgb(r, i, b)
            r += 3
            if (r >= INT255) {
                r = INT255
            }
            b += 2
            if (b >= INT255) {
                b = INT255
            }
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = rgb(INT255 - i, INT255 - i, INT255 - i)
        }
    }

    private fun makePal4() {
        colMask = INT0x00FF0000
        colShift = INT16
        for (i in 0 until INT256) {
            pal[i] = INT0xFF000000
        }
        var r = 1
        var g = 1
        var i1 = 3
        var i2 = 5
        for (i in 1 until INT60) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 7
                r++
            }
            if (i2 == 0) {
                i2 = 3
                g++
            }
            i1--
            i2--
        }
        for (i in INT60 until INT90) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 5
                r++
            }
            if (i2 == 0) {
                i2 = 2
                g++
            }
            i1--
            i2--
        }
        for (i in INT90 until INT130) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 3
                r++
            }
            if (g < INT255) {
                g++
            }
            i1--
        }
        for (i in INT130 until INT210) {
            pal[i] = rgb(r, g, i)
            if (r < INT255) {
                r++
            }
            g += 2
            if (g >= INT255) {
                g = INT255
            }
        }
        for (i in INT210 until INT256) {
            pal[i] = rgb(r, g, i)
            r += 2
            if (r >= INT255) {
                r = INT255
            }
            g += 4
            if (g >= INT255) {
                g = INT255
            }
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = rgb(INT255 - i, INT255 - i, INT255 - i)
        }
    }

    private fun makePal5() {
        colMask = INT65280
        colShift = INT8
        for (i in 0 until INT256) {
            pal[i] = rgb(i, i, i)
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = pal[INT240 - i] and INT0xFF00FFFF
        }
    }

    private fun makePal6() {
        colMask = INT0x00FF0000
        colShift = INT16
        for (i in 0 until INT256) {
            pal[i] = INT0xFF000000
        }
        var r = 1
        var g = 1
        var i1 = 3
        var i2 = 5
        for (i in 1 until INT60) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 9
                r++
            }
            if (i2 == 0) {
                i2 = 6
                g++
            }
            i1--
            i2--
        }
        for (i in INT60 until INT90) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = INT8
                r++
            }
            if (i2 == 0) {
                i2 = 5
                g++
            }
            i1--
            i2--
        }
        for (i in INT90 until INT130) {
            pal[i] = rgb(r, g, i)
            if (i1 == 0) {
                i1 = 5
                r++
            }
            if (i2 == 0) {
                i2 = 3
                g++
            }
            i1--
            i2--
        }
        for (i in INT130 until INT210) {
            pal[i] = rgb(r, g, i)
            ++r
            if (r >= INT255) {
                r = INT255
            }
            ++g
            if (g >= INT255) {
                g = INT255
            }
        }
        for (i in INT210 until INT256) {
            pal[i] = rgb(r, g, i)
            r += 3
            if (r >= INT255) {
                r = INT255
            }
            g += 3
            if (g >= INT255) {
                g = INT255
            }
        }
        for (i in 0 until TW_MAX_COLOR) {
            twColor[i] = rgb(INT255 - i, INT255 - i, INT255 - i)
        }
    }

    override fun paint(g: Graphics) {
        if (g is Graphics2D) {
            val c = g.composite
            if (alpha != 0) {
                g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 100f)
            }
            paint0(g)
            g.composite = c
        } else {
            paint0(g)
        }
    }

    private fun paint0(g: Graphics) {
        if (isCanDisplay && art != null) {
            g.drawImage(art, 0, 0, width, height, 0, 0, art!!.getWidth(this), art!!.getHeight(this), Color.BLACK, this)
        } else {
            g.color = Color.BLACK
            g.fillRect(0, 0, xSize, ySize)
        }
    }

    private fun paintActive(): Boolean {
        for (l in 1 until xSize - 1 - 1) {
            var bb = xSize
            var aa = bb - xSize
            if (l < xSize - 1) {
                for (j in 1 until ySize) {
                    if (buf[l + bb] and colMask shr colShift < params.getDecay()) {
                        buf[l + aa] = INT0xFF000000
                    } else {
                        buf[aa + l - (rnd.nextInt(3) - 1)] =
                            pal[(buf[l + bb] and colMask shr colShift) - rnd.nextInt(params.getDecay()) and INT0xff]
                    }
                    aa += xSize
                    bb += xSize
                }
            }
        }
        val l = time()
        if (burn && (l < twTimer - params.fadeDelay || !params.isFade)) {
            var i = 1
            var j = 0
            while (i < twWords[0]) {
                j += twWords[i]
                i++
                var cnt = twWords[i]
                i++
                while (cnt > 0) {
                    buf[j] = twColor[rnd.nextInt(TW_MAX_COLOR)]
                    j++
                    --cnt
                }
            }
        }
        if (l > twTimer) {
            twTimer = time() + params.delay
        }
        flameSource!!.newPixels(0, 0, xSize, ySize)
        art!!.graphics.drawImage(flameImage, 0, 0, null)
        try {
            Thread.sleep(SLEEP20.toLong())
        } catch (e: InterruptedException) {
            return true
        }
        repaint()
        return false
    }

    private fun prepare() {
        xSize = width
        ySize = height
        val size = xSize * ySize
        art = createImage(xSize, ySize)
        buf = IntArray(size)
        flameSource = MemoryImageSource(xSize, ySize, buf, 0, xSize)
        flameSource!!.setAnimated(true)
        flameImage = createImage(flameSource)
        alpha = params.alpha
        val twLen = params.getFontSize() * (xSize / (params.getFontSize() / INT8))
        makePal(params.getColor())
        makePal(!params.isSolid)
        val g = art!!.graphics
        g.font = Font(params.getFont(), Font.PLAIN, params.getFontSize())
        val fontmetrics = g.fontMetrics
        val word = params.text
        val pixelgrabber = PixelGrabber(art, 0, 0, xSize, ySize, buf, 0, xSize)
        var i1 = size - 1
        while (i1 >= 0) {
            buf[i1] = INT0xFF000000
            i1--
        }
        g.color = Color.BLACK
        g.fillRect(0, 0, xSize, ySize)
        g.color = Color.WHITE
        val y: Int
        val align = params.getAlign()
        y = when (align) {
            Align.CENTER -> (ySize - fontmetrics.height) / 2 + fontmetrics.ascent + params.position
            Align.TOP -> fontmetrics.ascent + params.position
            Align.BOTTOM -> ySize - fontmetrics.height + fontmetrics.ascent + params.position
        }
        g.drawString(word, (xSize - fontmetrics.stringWidth(word)) / 2, y)
        try {
            isCanDisplay = pixelgrabber.grabPixels()
        } catch (e: InterruptedException) {
            isRunning = false
        }
        twWords = IntArray(twLen)
        if (isCanDisplay) {
            g.color = Color.BLACK
            g.fillRect(0, 0, xSize, ySize)
            var j = 1
            var l = 0
            while (l < size - 1 && j < twLen - 2) {
                var k = 0
                while (l < size - 1 && buf[l] and INT0x00FFFFFF == 0) {
                    l++
                    k++
                }
                twWords[j] = k
                j++
                var i = 0
                while (l < size - 1 && buf[l] and INT0x00FFFFFF != 0) {
                    l++
                    i++
                }
                twWords[j] = i
                j++
            }
            twWords[0] = j - 2
        }
        for (i in 0 until size) {
            buf[i] = INT0xFF000000
        }
        burn = true
        twTimer = 0
        if (params.delay != 0L) {
            twTimer = time() + params.delay - params.fadeDelay
        }
    }

    override fun run() {
        isRunning = true
        prepare()
        while (isRunning) {
            if (active) {
                try {
                    if (paintActive()) break
                } catch (ignored: RuntimeException) {
                }
            } else {
                try {
                    Thread.sleep(SLEEP100.toLong())
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
        isRunning = false
        thread = null
    }

    fun start() {
        active = true
    }

    fun stop() {
        active = false
    }

    class FlameParams : Serializable {
        private var align = DEFAULT_ALIGN
        var alpha = DEFAULT_ALPHA
        private var color = DEFAULT_COLOR
        private var decay = DEFAULT_DECAY
        var delay = DEFAULT_DELAY
        var isFade = DEFAULT_FADE
        var fadeDelay = DEFAULT_FADE_DELAY
        private var font = DEFAULT_FONT
        private var fontSize = DEFAULT_FONT_SIZE
        var position = DEFAULT_POSITION
        var isSolid = DEFAULT_SOLID
        var text: String = DEFAULT_TEXT

        constructor()
        constructor(p: FlameParams) {
            align = p.align
            alpha = p.alpha
            color = p.color
            decay = p.decay
            delay = p.delay
            isFade = p.isFade
            fadeDelay = p.fadeDelay
            font = p.font
            fontSize = p.fontSize
            position = p.position
            isSolid = p.isSolid
            text = p.text
        }

        fun getAlign(): Align {
            return align
        }

        fun setAlign(align: Align?) {
            this.align = align ?: Align.CENTER
        }

        fun getColor(): Int {
            return color
        }

        fun setColor(color: Int) {
            this.color = if (color < MIN_COLOR || color > MAX_COLOR) MIN_COLOR else color
        }

        fun getDecay(): Int {
            return decay
        }

        fun setDecay(decay: Int) {
            this.decay = if (decay < MIN_DECAY || decay > MAX_DECAY) DEFAULT_DECAY else decay
        }

        fun getFont(): String {
            return font
        }

        fun setFont(font: String) {
            when (font) {
                FONT_VALUE_COURIER -> this.font = FONT_FAMILY_COURIER
                FONT_VALUE_DIALOG -> this.font = FONT_FAMILY_DIALOG
                FONT_VALUE_DIALOG_INPUT -> this.font = FONT_FAMILY_DIALOG_INPUT
                FONT_VALUE_HELVETICA -> this.font = FONT_FAMILY_HELVETICA
                FONT_VALUE_SYMBOL -> this.font = FONT_FAMILY_SYMBOL
                else -> {
                    val list =
                        listOf(*GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames)
                    if (!list.contains(font)) {
                        Log.warn(
                            String.format(
                                "invalid font name %s, using %s, choose from: %s",
                                font,
                                DEFAULT_FONT,
                                list
                            )
                        )
                        this.font = DEFAULT_FONT
                    }
                }
            }
        }

        fun getFontSize(): Int {
            return fontSize
        }

        fun setFontSize(fontSize: Int) {
            this.fontSize = if (fontSize < MIN_FONT_SIZE) MIN_FONT_SIZE else fontSize
        }

        enum class Align {
            TOP, BOTTOM, CENTER
        }

        companion object {
            private val DEFAULT_ALIGN = Align.TOP
            private const val DEFAULT_ALPHA = 0
            private const val DEFAULT_COLOR = 1
            private const val DEFAULT_DECAY = 20
            private const val DEFAULT_DELAY = 10000L
            private const val DEFAULT_FADE = false
            private const val DEFAULT_FADE_DELAY = 2000L
            private const val DEFAULT_FONT = "Times New Roman"
            private const val DEFAULT_FONT_SIZE = 140
            private const val DEFAULT_POSITION = 0
            private const val DEFAULT_SOLID = false
            private const val DEFAULT_TEXT = "Flames|by|IoN CheN"
            private const val FONT_FAMILY_COURIER = "Courier"
            private const val FONT_FAMILY_DIALOG = "Dialog"
            private const val FONT_FAMILY_DIALOG_INPUT = "DialogInput"
            private const val FONT_FAMILY_HELVETICA = "Helvetica"
            private const val FONT_FAMILY_SYMBOL = "Symbol"
            private const val FONT_VALUE_COURIER = "courier"
            private const val FONT_VALUE_DIALOG = "dialog"
            private const val FONT_VALUE_DIALOG_INPUT = "dialoginput"
            private const val FONT_VALUE_HELVETICA = "helvetica"
            private const val FONT_VALUE_SYMBOL = "symbol"
            private const val MAX_COLOR = 6
            private const val MAX_DECAY = 20
            private const val MIN_COLOR = 1
            private const val MIN_DECAY = 5
            private const val MIN_FONT_SIZE = 10
        }
    }

    companion object {
        private const val INT0x00FF0000 = 0x00ff0000
        private const val INT0x00FFFFFF = 0x00ffffff
        private const val INT0xFF000000 = -0x1000000
        private const val INT0xFF00FF00 = -0xff0100
        private const val INT0xFF00FFFF = -0xff0001
        private const val INT0xFFFFFF00 = -0x100
        private const val INT130 = 130
        private const val INT16 = 16
        private const val INT210 = 210
        private const val INT255 = 255
        private const val INT0xff = INT255
        private const val INT256 = 256
        private const val INT240 = INT256 - INT16
        private const val INT40 = INT256 / 6
        private const val INT60 = INT256 / 4
        private const val INT65280 = INT0xff * INT256
        private const val INT8 = 8
        private const val INT90 = 90
        private const val SLEEP100 = 100
        private const val SLEEP20 = 20
        private const val TW_MAX_COLOR = 50
        private const val serialVersionUID = -2155412932761138317L
        private fun rgb(r: Int, g: Int, b: Int): Int {
            return INT0xFF000000 or (b shl INT16) or (r shl INT8) or g
        }

        private fun time(): Long {
            return System.currentTimeMillis()
        }
    }
}
