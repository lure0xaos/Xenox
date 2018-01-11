package gargoyle.xenox.game.sys;

import gargoyle.xenox.util.config.Config;
import gargoyle.xenox.util.log.Log;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Controls extends Config implements KeyListener {
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_DOWN = "down";
    private static final String PARAM_LEFT = "left";
    private static final String PARAM_RIGHT = "right";
    private static final String PARAM_UP = "up";
    private static final long serialVersionUID = -6095549099825656180L;
    private final List<KeyConfig> keyConfigs = new ArrayList<>();
    private transient volatile boolean action;
    private transient volatile boolean down;
    private transient volatile boolean left;
    private transient volatile boolean right;
    private transient volatile boolean up;

    public Controls() {
        reset();
    }

    public void addDefaultConfig() {
        newConfig(KeyEvent.VK_ENTER, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
    }

    public List<KeyConfig> getConfigs() {
        return Collections.unmodifiableList(keyConfigs);
    }

    public boolean isAction() {
        return action;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isUp() {
        return up;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        reset();
        for (KeyConfig keyConfig : keyConfigs) {
            if (key == keyConfig.getAction()) {
                action = true;
            }
            if (key == keyConfig.getDown()) {
                down = true;
            }
            if (key == keyConfig.getLeft()) {
                left = true;
            }
            if (key == keyConfig.getRight()) {
                right = true;
            }
            if (key == keyConfig.getUp()) {
                up = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        for (KeyConfig keyConfig : keyConfigs) {
            if (key == keyConfig.getAction()) {
                action = false;
            }
            if (key == keyConfig.getDown()) {
                down = false;
            }
            if (key == keyConfig.getLeft()) {
                left = false;
            }
            if (key == keyConfig.getRight()) {
                right = false;
            }
            if (key == keyConfig.getUp()) {
                up = false;
            }
        }
    }

    public KeyConfig newConfig(int action, int up, int down, int left, int right) {
        KeyConfig keyConfig = new KeyConfig(this, action, up, down, left, right);
        keyConfigs.add(keyConfig);
        return keyConfig;
    }

    public void reset() {
        action = false;
        down = false;
        up = false;
        right = false;
        left = false;
    }

    public static class KeyConfig implements Serializable {
        private static final long serialVersionUID = 8127782339105594052L;
        private int num;
        private Preferences preferences;

        KeyConfig(Controls controls, int action, int up, int down, int left,
                  int right) {
            num = 0;
            preferences = controls.node(String.valueOf(num));
            try {
                while (controls.nodeExists(String.valueOf(num))) {
                    if (action == getAction() && up == getUp() && down == getDown() &&
                            left == getLeft() && right == getRight()) {
                        break;
                    }
                    num++;
                    preferences = controls.node(String.valueOf(num));
                }
            } catch (BackingStoreException e) {
                Log.error(e.getLocalizedMessage(), e);
            }
            setAction(action);
            setUp(up);
            setDown(down);
            setLeft(left);
            setRight(right);
        }

        public int getAction() {
            return preferences.getInt(PARAM_ACTION, KeyEvent.VK_ENTER);
        }

        public void setAction(int action) {
            preferences.putInt(PARAM_ACTION, action);
        }

        public int getDown() {
            return preferences.getInt(PARAM_DOWN, KeyEvent.VK_DOWN);
        }

        public void setDown(int down) {
            preferences.putInt(PARAM_DOWN, down);
        }

        public int getLeft() {
            return preferences.getInt(PARAM_LEFT, KeyEvent.VK_LEFT);
        }

        public void setLeft(int left) {
            preferences.putInt(PARAM_LEFT, left);
        }

        public int getRight() {
            return preferences.getInt(PARAM_RIGHT, KeyEvent.VK_RIGHT);
        }

        public void setRight(int right) {
            preferences.putInt(PARAM_RIGHT, right);
        }

        public int getUp() {
            return preferences.getInt(PARAM_UP, KeyEvent.VK_UP);
        }

        public void setUp(int up) {
            preferences.putInt(PARAM_UP, up);
        }
    }
}
