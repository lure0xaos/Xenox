package gargoyle.xenox.game.scr.menu.config;

import gargoyle.xenox.Xenox;
import gargoyle.xenox.game.sys.JScreen;
import gargoyle.xenox.game.sys.Options;
import gargoyle.xenox.util.i18n.Messages;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class JOptions extends JDialog {
    public static final String STR_OPTIONS = "options";
    private static final String STR_RESIZE = "resize";
    private static final String STR_SOUND = "sound";
    private final Options options;

    public JOptions(JScreen parent) {
        super(SwingUtilities.getWindowAncestor(parent), parent.getMessages().get(STR_OPTIONS),
                ModalityType.APPLICATION_MODAL);
        options = parent.getOptions();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        init(parent.getMessages());
    }

    private void init(Messages messages) {
        setLayout(new BorderLayout());
        JPanel pnlOptions = new JPanel(new GridLayout(0, 1));
        JCheckBox chkSound = new JCheckBox(messages.get(STR_SOUND), options.isSound());
        pnlOptions.add(chkSound);
        JCheckBox chkResize = new JCheckBox(messages.get(STR_RESIZE), options.isResize());
        pnlOptions.add(chkResize);
        JPanel pnlButtons = new JPanel(new GridLayout(1, 2));
        JButton btnOk = new JButton(messages.get(Xenox.STR_OK));
        btnOk.addActionListener(e -> {
            options.setSound(chkSound.isSelected());
            options.setResize(chkResize.isSelected());
            setVisible(false);
        });
        pnlButtons.add(btnOk);
        JButton btnCancel = new JButton(messages.get(Xenox.STR_CANCEL));
        btnCancel.addActionListener(e -> {
            chkSound.setSelected(options.isSound());
            chkResize.setSelected(options.isResize());
            setVisible(false);
        });
        pnlButtons.add(btnCancel);
        add(pnlOptions, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);
    }
}
