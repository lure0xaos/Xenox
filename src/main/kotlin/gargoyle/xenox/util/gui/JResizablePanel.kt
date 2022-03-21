package gargoyle.xenox.util.gui

import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager
import javax.swing.JComponent
import javax.swing.JPanel

class JResizablePanel(layout: LayoutManager) : JPanel() {
    private val lastParentSize = Dimension()
    private val lastSize = Dimension()
    private val size = Dimension()
    private lateinit var lastParent: Container
    private var managed = true

    init {
        setLayout(layout)
    }

    private fun _size(): Dimension {
        synchronized(treeLock) {
            val p = parent
            var changed = false
            if (!::lastParent.isInitialized || lastParent !== p) {
                changed = true
                lastParent = p
            }
            if (p == null) {
                return if (!managed || size.width == 0 || size.height == 0) {
                    super.getSize()
                } else size
            }
            val out = p.size
            if (size.width == 0 || size.height == 0) {
                val preferredSize = super.getPreferredSize()
                return if (preferredSize.width == 0 || preferredSize.height == 0) {
                    super.getSize()
                } else preferredSize
            }
            if (!managed) {
                return out
            }
            val fit = Dimension()
            if (out.width != lastParentSize.width || out.height != lastParentSize.height) {
                changed = true
                lastParentSize.size = out
            }
            if (!changed) {
                return lastSize
            }
            if (out.width == size.width && out.height == size.height) {
                fit.size = size
                lastSize.size = fit
                return fit
            }
            if (out.width < out.height) {
                fit.width = out.width
                fit.height = (size.width / size.height.toDouble() * out.width).toInt()
            } else {
                fit.height = out.height
                fit.width = (size.width / size.height.toDouble() * out.height).toInt()
            }
            lastSize.size = fit
            return fit
        }
    }

    override fun getPreferredSize(): Dimension {
        return _size()
    }

    override fun getSize(size: Dimension): Dimension {
        size.size = size
        return size
    }

    override fun getWidth(): Int {
        return _size().width
    }

    override fun getHeight(): Int {
        return _size().height
    }

    override fun getSize(): Dimension {
        val size = Dimension()
        size.setSize(size.width, size.height)
        return size
    }

    fun reset() {
        synchronized(treeLock) { lastParentSize.setSize(0, 0) }
    }

    var component: JComponent?
        get() = super.getComponent(0) as JComponent?
        set(component) {
            synchronized(treeLock) { size.size = if (managed) component!!.preferredSize else component!!.size }
        }

    fun setManaged(managed: Boolean) {
        this.managed = managed
    }
}
