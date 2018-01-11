package gargoyle.xenox.game.scr.menu;

import gargoyle.xenox.game.scr.game.JGame;
import gargoyle.xenox.game.scr.hi.JHi;
import gargoyle.xenox.game.scr.menu.config.JControls;
import gargoyle.xenox.game.scr.menu.config.JOptions;
import gargoyle.xenox.game.scr.status.JStatus;
import gargoyle.xenox.game.sys.Controls;
import gargoyle.xenox.game.sys.JScreen;
import gargoyle.xenox.game.sys.Options;
import gargoyle.xenox.i.AudioManager;
import gargoyle.xenox.i.ScreenManager;
import gargoyle.xenox.util.i18n.Messages;
import gargoyle.xenox.util.res.Resources;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import gargoyle.xenox.util.res.audio.AudioClip;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class JIntro extends JScreen {
    public static final String INTRO_IMAGE = "intro.jpg";
    public static final String SCR_INTRO = "Intro";
    public static final String STR_START_GAME = "start_game";
    private static final int ALPHA = 40;
    private static final float FONT_SIZE = 20.0f;
    private static final String INTRO_MUSIC = "intro.au";
    private static final String STR_EXIT = "exit";
    private static final String STR_HISCORE = "hi_score";
    private final boolean application;
    private final JButton btnExit;
    private final JFlame flame;
    private final transient Image image;
    private final JControls jControls;
    private final JOptions jOptions;
    private final JPanel pnlControls;
    private transient volatile String action;
    private volatile boolean toInit = true;

    public JIntro(Component applet, Resources resources, Messages messages, AudioManager audio, ScreenManager screens, Options options, Controls controls, String title, boolean application) {
        super(JIntro.SCR_INTRO, applet, resources, messages, controls, audio, screens, options);
        jControls = new JControls(this);
        jOptions = new JOptions(this);
        setLayout(new GridLayout(0, 1));
        JFlame.FlameParams params = new JFlame.FlameParams();
        params.setText(title);
        params.setAlign(JFlame.FlameParams.Align.TOP);
        params.setFade(false);
        params.setAlpha(ALPHA);
        flame = new JFlame(params);
        flame.setVisible(false);
        add(flame);
        pnlControls = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                if (g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    Composite c = g2d.getComposite();
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA / 100.0f));
                    super.paintComponent(g);
                    g2d.setComposite(c);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        pnlControls.setVisible(false);
        pnlControls.setBackground(Color.BLACK);
        setBackground(Color.BLACK);
        GridLayout mgr = new GridLayout(0, 1);
        pnlControls.setLayout(mgr);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = getSize();
                if (size.width == 0 && size.height == 0) {
                    return;
                }
                synchronized (flame) {
                    int w = getWidth() / 4;
                    @SuppressWarnings("MagicNumber") int h = getHeight() / 64;
                    if (mgr.getHgap() != w || mgr.getVgap() != h) {
                        mgr.setHgap(w);
                        mgr.setVgap(h);
                        pnlControls.setBorder(BorderFactory.createEmptyBorder(h, w, h, w));
                    }
                    Font font = scaleFont(params.getText(), getSize(), flame.getGraphics());
                    params.setFontSize(font.getSize());
                    if (isVisible() && flame.getParent() != null) {
                        if (flame.isRunning()) {
                            flame.stop();
                            flame.destroy();
                        }
                        flame.init();
                        flame.start();
                        repaint();
                    }
                }
            }

            @Override
            public void componentShown(ComponentEvent e) {
                componentResized(e);
            }
        });
        JButton btnStart = new JButton(messages.get(STR_START_GAME));
        btnStart.addActionListener(e -> action = JGame.SCR_GAME);
        pnlControls.add(btnStart);
        JButton btnHi = new JButton(messages.get(STR_HISCORE));
        btnHi.addActionListener(e -> action = JHi.SCR_HI);
        pnlControls.add(btnHi);
        JButton btnOptions = new JButton(messages.get(JOptions.STR_OPTIONS));
        btnOptions.addActionListener(e -> {
            jOptions.pack();
            jOptions.setLocationRelativeTo(this);
            jOptions.setVisible(true);
            audio.musicStop();
            audio.musicPlay();
        });
        pnlControls.add(btnOptions);
        JButton btnControls = new JButton(messages.get(JControls.STR_CONTROLS));
        btnControls.addActionListener(e -> {
            jControls.pack();
            jControls.setLocationRelativeTo(this);
            jControls.setVisible(true);
        });
        pnlControls.add(btnControls);
        btnExit = new JButton(messages.get(STR_EXIT));
        btnExit.addActionListener(e -> action = JStatus.SCR_STATUS);
        pnlControls.add(btnExit);
        add(pnlControls);
        image = this.resources.load(BufferedImage.class, INTRO_IMAGE);
        this.application = application;
    }

    private static Font scaleFont(String text, Dimension rect, Graphics g) {
        float fontSize = FONT_SIZE;
        Font font = g.getFont().deriveFont(fontSize);
        int width = g.getFontMetrics(font).stringWidth(text);
        fontSize = (rect.width / (float) width) * fontSize;
        return g.getFont().deriveFont(fontSize);
    }

    @Override
    protected String _process() throws InterruptedException {
        musicSet(resources.load(AudioClip.class, INTRO_MUSIC));
        btnExit.setEnabled(application);
        action = null;
        flame.start();
        if (toInit) {
            Thread.sleep(1000);
            flame.setVisible(isVisible());
            pnlControls.setVisible(isVisible());
            toInit = false;
        }
        musicLoop();
        repaint();
        while (action == null && !Thread.currentThread().isInterrupted()) {
            Thread.sleep(10);
        }
        long fadeDelay = flame.fade();
        if (flame.isCanDisplay()) {
            try {
                Thread.sleep(fadeDelay);
            } catch (InterruptedException e) {
                flame.stop();
                musicStop();
                flame.destroy();
                return action;
            }
        }
        flame.stop();
        musicStop();
        return action;
    }

    @Override
    public void destroy() {
        flame.stop();
        flame.destroy();
        SwingUtilities.invokeLater(() -> {
            jOptions.dispose();
            jControls.dispose();
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return image == null ?
                super.getPreferredSize() :
                new Dimension(image.getWidth(this), image.getHeight(this));
    }

    @Override
    public void paintComponent(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), 0, 0, image.getWidth(this), image.getHeight(this), this);
        }
    }
}
