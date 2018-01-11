package gargoyle.xenox.game.scr.menu.config;

import gargoyle.xenox.Xenox;
import gargoyle.xenox.game.sys.Controls;
import gargoyle.xenox.game.sys.Controls.KeyConfig;
import gargoyle.xenox.game.sys.JScreen;
import gargoyle.xenox.util.gui.JKeyButton;
import gargoyle.xenox.util.i18n.Messages;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JControls extends JDialog {
    public static final String STR_CONTROLS = "controls";
    private static final String STR_CONFIG_PLUS = "+";
    private static final String STR_CONFIG = "config";
    private static final String STR_CONF_ACTION = "action";
    private static final String STR_CONF_DOWN = "down";
    private static final String STR_CONF_LEFT = "left";
    private static final String STR_CONF_RIGHT = "right";
    private static final String STR_CONF_UP = "up";

    public JControls(JScreen parent) {
        super(SwingUtilities.getWindowAncestor(parent), parent.getMessages().get(JOptions.STR_OPTIONS),
                ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        init(parent.getMessages(), parent.getControls());
    }

    private static void addConfigPanel(Controls controls, Messages messages, JTabbedPane tabConfigs,
                                       int num, int c, KeyConfig keyConfig) {
        JPanel pnlConfig = new JPanel(new GridLayout(0, 2));
        {
            pnlConfig.add(new JLabel(messages.get(STR_CONF_ACTION)));
            JKeyButton btn = new JKeyButton(keyConfig.getAction());
            btn.addLateActionListener(new KeyActionListener(controls, c, btn));
            pnlConfig.add(btn);
        }
        {
            pnlConfig.add(new JLabel(messages.get(STR_CONF_LEFT)));
            JKeyButton btn = new JKeyButton(keyConfig.getLeft());
            btn.addLateActionListener(new KeyLeftListener(controls, c, btn));
            pnlConfig.add(btn);
        }
        {
            pnlConfig.add(new JLabel(messages.get(STR_CONF_RIGHT)));
            JKeyButton btn = new JKeyButton(keyConfig.getRight());
            btn.addLateActionListener(new KeyRightListener(controls, c, btn));
            pnlConfig.add(btn);
        }
        {
            pnlConfig.add(new JLabel(messages.get(STR_CONF_UP)));
            JKeyButton btn = new JKeyButton(keyConfig.getUp());
            btn.addLateActionListener(new KeyUpListener(controls, c, btn));
            pnlConfig.add(btn);
        }
        pnlConfig.add(new JLabel(messages.get(STR_CONF_DOWN)));
        JKeyButton btn = new JKeyButton(keyConfig.getDown());
        btn.addLateActionListener(new KeyDownListener(controls, c, btn));
        pnlConfig.add(btn);
        tabConfigs.insertTab(String.format("%s %d", messages.get(STR_CONFIG), num), null, pnlConfig, String.valueOf(num),
                num - 1);
    }

    private void init(Messages messages, Controls controls) {
        setLayout(new BorderLayout());
        JTabbedPane tabConfigs = new JTabbedPane();
        int num = 1;
        for (int c = 0; c < controls.getConfigs().size(); c++) {
            KeyConfig keyConfig = controls.getConfigs().get(c);
            addConfigPanel(controls, messages, tabConfigs, num, c, keyConfig);
            num++;
        }
        JPanel pnlNewConfig = new JPanel();
        tabConfigs.addTab(messages.get(STR_CONFIG_PLUS), pnlNewConfig);
        tabConfigs.addChangeListener(evt -> {
            if (tabConfigs.getSelectedComponent() == pnlNewConfig) {
                KeyConfig keyConfig = controls.newConfig(0, 0, 0, 0, 0);
                int c = controls.getConfigs().size();
                tabConfigs.setSelectedIndex(c - 1);
                controls.getConfigs().add(keyConfig);
                addConfigPanel(controls, messages, tabConfigs, c + 1, c, keyConfig);
                pack();
                tabConfigs.setSelectedIndex(c);
            }
        });
        JPanel pnlButtons = new JPanel(new GridLayout(1, 2));
        JButton btnOk = new JButton(messages.get(Xenox.STR_OK));
        btnOk.addActionListener(e -> setVisible(false));
        pnlButtons.add(btnOk);
        JButton btnCancel = new JButton(messages.get(Xenox.STR_CANCEL));
        btnCancel.addActionListener(e -> setVisible(false));
        pnlButtons.add(btnCancel);
        add(tabConfigs, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private abstract static class BaseKeyListener implements ActionListener {
        final JKeyButton btn;
        final int c;
        final Controls controls;

        BaseKeyListener(Controls controls, int c, JKeyButton btn) {
            this.controls = controls;
            this.c = c;
            this.btn = btn;
        }
    }

    private static final class KeyActionListener extends BaseKeyListener {
        KeyActionListener(Controls controls, int c, JKeyButton btn) {
            super(controls, c, btn);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controls.getConfigs().get(c).setAction(btn.getKeyCode());
        }
    }

    private static final class KeyDownListener extends BaseKeyListener {
        KeyDownListener(Controls controls, int c, JKeyButton btn) {
            super(controls, c, btn);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controls.getConfigs().get(c).setDown(btn.getKeyCode());
        }
    }

    private static final class KeyLeftListener extends BaseKeyListener {
        KeyLeftListener(Controls controls, int c, JKeyButton btn) {
            super(controls, c, btn);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controls.getConfigs().get(c).setLeft(btn.getKeyCode());
        }
    }

    private static final class KeyRightListener extends BaseKeyListener {
        KeyRightListener(Controls controls, int c, JKeyButton btn) {
            super(controls, c, btn);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controls.getConfigs().get(c).setRight(btn.getKeyCode());
        }
    }

    private static final class KeyUpListener extends BaseKeyListener {
        KeyUpListener(Controls controls, int c, JKeyButton btn) {
            super(controls, c, btn);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controls.getConfigs().get(c).setUp(btn.getKeyCode());
        }
    }
}
