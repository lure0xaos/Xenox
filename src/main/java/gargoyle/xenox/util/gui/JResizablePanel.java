package gargoyle.xenox.util.gui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class JResizablePanel extends JPanel {
    private final Dimension lastParentSize = new Dimension();
    private final Dimension lastSize = new Dimension();
    private final Dimension size = new Dimension();
    private Container lastParent;
    private boolean managed = true;

    public JResizablePanel(LayoutManager layout) {
        setLayout(layout);
    }

    private Dimension _size() {
        synchronized (getTreeLock()) {
            Container p = getParent();
            boolean changed = false;
            if (lastParent != p) {
                changed = true;
                lastParent = p;
            }
            if (p == null) {
                if (!managed || size.width == 0 || size.height == 0) {
                    return super.getSize();
                }
                return size;
            }
            Dimension out = p.getSize();
            if (size.width == 0 || size.height == 0) {
                Dimension preferredSize = super.getPreferredSize();
                if (preferredSize.width == 0 || preferredSize.height == 0) {
                    return super.getSize();
                }
                return preferredSize;
            }
            if (!managed) {
                return out;
            }
            Dimension fit = new Dimension();
            if (out.width != lastParentSize.width || out.height != lastParentSize.height) {
                changed = true;
                lastParentSize.setSize(out);
            }
            if (!changed) {
                return lastSize;
            }
            if (out.width == size.width && out.height == size.height) {
                fit.setSize(size);
                lastSize.setSize(fit);
                return fit;
            }
            if (out.width < out.height) {
                fit.width = out.width;
                fit.height = (int) (size.width / (double) size.height * out.width);
            } else {
                fit.height = out.height;
                fit.width = (int) (size.width / (double) size.height * out.height);
            }
            lastSize.setSize(fit);
            return fit;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return _size();
    }

    @Override
    public Dimension getSize(Dimension size) {
        size.setSize(size);
        return size;
    }

    @Override
    public int getWidth() {
        return _size().width;
    }

    @Override
    public int getHeight() {
        return _size().height;
    }

    @Override
    public Dimension getSize() {
        Dimension size = new Dimension();
        size.setSize(size.width, size.height);
        return size;
    }

    public void reset() {
        synchronized (getTreeLock()) {
            lastParentSize.setSize(0, 0);
        }
    }

    public void setComponent(JComponent component) {
        synchronized (getTreeLock()) {
            size.setSize(managed ? component.getPreferredSize() : component.getSize());
        }
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }
}
