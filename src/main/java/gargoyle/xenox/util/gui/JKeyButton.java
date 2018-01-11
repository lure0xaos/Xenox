package gargoyle.xenox.util.gui;

import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class JKeyButton extends JToggleButton {
    private final List<ActionListener> lateActionListeners = new ArrayList<>();
    private volatile int keyCode;

    public JKeyButton(int keyCode) {
        setFocusTraversalKeysEnabled(false);
        setInputMap(JComponent.WHEN_FOCUSED, new InputMap());
        setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, new ComponentInputMap(this));
        setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new InputMap());
        this.keyCode = keyCode;
        setText(text());
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                onKey(e.getKeyCode());
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    onKey(0);
                }
            }
        });
    }

    public void addLateActionListener(ActionListener l) {
        lateActionListeners.add(l);
    }

    private void fireLateActionPerformed(ActionEvent event) {
        for (ActionListener listener : new ArrayList<>(lateActionListeners)) {
            listener.actionPerformed(event);
        }
    }

    public int getKeyCode() {
        return keyCode;
    }

    private void onKey(int key) {
        if (isSelected() || key == 0) {
            setSelected(false);
            keyCode = key;
            setText(text());
            fireLateActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        }
    }

    private String text() {
        String text = KeyEvent.getKeyText(keyCode);
        return text.startsWith(Toolkit.getProperty("AWT.unknown", "Unknown")) ? "?" : text;
    }
}
