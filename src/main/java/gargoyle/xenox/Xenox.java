package gargoyle.xenox;

import gargoyle.xenox.game.scr.game.JGame;
import gargoyle.xenox.game.scr.game.game.Campaign;
import gargoyle.xenox.game.scr.game.game.Level;
import gargoyle.xenox.game.scr.hi.JHi;
import gargoyle.xenox.game.scr.menu.JIntro;
import gargoyle.xenox.game.scr.status.JStatus;
import gargoyle.xenox.game.sys.Controls;
import gargoyle.xenox.game.sys.HiScore;
import gargoyle.xenox.game.sys.JScreen;
import gargoyle.xenox.game.sys.Options;
import gargoyle.xenox.i.AudioManager;
import gargoyle.xenox.i.ScreenManager;
import gargoyle.xenox.util.applet.GApplet;
import gargoyle.xenox.util.gui.JResizablePanel;
import gargoyle.xenox.util.i18n.Messages;
import gargoyle.xenox.util.res.Res;
import gargoyle.xenox.util.res.Resources;
import gargoyle.xenox.util.res.audio.AudioClip;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Xenox extends GApplet implements Runnable, AudioManager, ScreenManager {
    public static final int ITEM_HIDE = 100;
    public static final int ITEM_SHOW = 100;
    public static final int LIVES = 5;
    public static final int PF_CELL = 8;
    public static final int PF_HEIGHT = 100;
    public static final int PF_WIDTH = 100;
    public static final String SND_GOT = "got.wav";
    public static final String STR_CANCEL = "cancel";
    public static final String STR_OK = "ok";
    private static final String SND_GAME_OVER = "gameover.wav";
    private static final String STR_EXIT = "exitq";
    private static final String STR_NAME = "name";
    private final Controls controls;
    private final CardLayout layout;
    private final Messages messages;
    private final Options options;
    private final JResizablePanel pnlCards;
    private final Map<String, JScreen> screens = new HashMap<>();
    private transient volatile AudioClip clip;
    private JScreen currentScreen;
    private JGame game;
    private JHi hi;
    private final HiScore hiScore;
    private JIntro intro;
    private volatile boolean running;
    private JStatus status;
    private transient Thread thread;

    public Xenox() {
        super(new Resources(true, Res.nearClassUrl(Xenox.class, "res")));
        messages = new Messages(String.format("%s/i18n/%s", Xenox.class.getPackage().getName(), Xenox.class.getSimpleName()), Locale.getDefault(), StandardCharsets.UTF_8);
        hiScore = new HiScore();
        options = new Options();
        controls = new Controls();
        addKeyListener(controls);
        controls.addDefaultConfig();
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        add(pnlCards = new JResizablePanel(layout = new CardLayout()));
    }

    public static void main(String[] args) {
        Xenox.run(Xenox.class, args);
    }

    @Override
    public boolean canExit() {
        if (getMessages() == null) {
            return super.canExit();
        }
        return isApplication() && ask(getMessages().get(STR_EXIT));
    }

    @Override
    protected void doDestroy() {
        if (intro != null) {
            intro.destroy();
        }
        if (thread != null) {
            running = false;
            thread.interrupt();
            thread = null;
        }
        if (status != null) {
            status.destroy();
        }
        game.destroy();
        musicStop();
    }

    @Override
    protected void doInit() {
        resources.addRoot(getDocumentBase());
        resources.addRoot(getCodeBase());
        setBackground(Color.BLACK);
        Container p = getParent();
        if (p != null) {
            p.setBackground(Color.BLACK);
        }
        construct();
        if (thread == null) {
            thread = new Thread(this, Xenox.class.getName());
            thread.start();
        }
    }

    @Override
    protected void doStart() {
        if (currentScreen != null) {
            currentScreen.setActive(true);
        }
    }

    @Override
    protected void doStop() {
        if (currentScreen != null) {
            currentScreen.setActive(false);
        }
    }

    private void construct() {
        screenAdd(game = new JGame(this, resources, messages, this.getControls(), this, this, options));
        screenAdd(status = new JStatus(this, resources, messages, this, this, options, this.getControls(), getTitle()));
        status.setTimer(5000);
        screenAdd(intro = new JIntro(this, resources, messages, this, this, options, this.getControls(), this.getTitle(), this.isApplication()));
        screenAdd(hi = new JHi(this, resources, messages, this.getHiScore(), this, this, options, this.getControls(), getTitle()));
    }

    private void game() throws InterruptedException {
        URL urlLevels = getUrlLevels();
        if (urlLevels == null) {
            status.process(null, messages.get(JStatus.STR_GAME_OVER), null,
                    getOptions().isSound() ? resources.url(true, SND_GAME_OVER) : null);
            return;
        }
        Campaign campaign = new Campaign(urlLevels, Charset.defaultCharset());
        status.process(null, messages.get(JIntro.STR_START_GAME), null);
        campaign.init();
        do {
            Level level = campaign.getCurrentLevel();
            if (level == null) {
                break;
            }
            status.process(level.getTitle(), messages.get(JStatus.STR_GET_READY),
                    level.getCover(), level.getMusic());
            game.loadLevel(campaign.getCurrentLevelNumber(), level);
            while (null == game.process()) {
                musicStop();
                if (!running || !game.isPlayerAlive()) {
                    break;
                }
                status.process(null, messages.get(JStatus.STR_LIFE_LOST), null);
            }
            musicStop();
            status.process(null, messages.get(JStatus.STR_LEVEL_FINISHED), level.getImage());
            if (!campaign.next()) {
                break;
            }
        } while (game.isPlayerAlive());
        musicStop();
        if (game.isPlayerAlive()) {
            status.process(null, messages.get(JStatus.STR_GAME_OVER), null,
                    resources.url(true, SND_GAME_OVER));
        } else {
            status.process(null, messages.get(JStatus.STR_GAME_OVER), null,
                    resources.url(true, SND_GAME_OVER));
        }
    }

    public Controls getControls() {
        return controls;
    }

    public HiScore getHiScore() {
        return hiScore;
    }

    public Messages getMessages() {
        return messages;
    }

    public Options getOptions() {
        return options;
    }

    private URL getUrlLevels() {
        URL urlLevels = null;
        {
            URL file = resources.url(true, "campaign.zip");
            if (file != null) {
                URL zip = Res.toURL(String.format("jar:%s!/campaign.properties", file.toExternalForm()));
                if (Res.isUrlOk(zip)) {
                    urlLevels = zip;
                }
            }
        }
        if (urlLevels == null) {
            urlLevels = resources.url(true, "levels/campaign.properties");
        }
        return urlLevels;
    }

    private String getUserName() {
        if (getHiScore().getUserName() != null && getHiScore().getUserName().isEmpty()) {
            try {
                getHiScore().setUserName(System.getProperty("user.name"));
            } catch (SecurityException e) {
                getHiScore().setUserName(prompt(getMessages().get(STR_NAME)));
            }
        }
        return getHiScore().getUserName();
    }

    @Override
    public synchronized void musicLoop() {
        if (clip != null) {
            clip.stop();
            if (options.isSound()) {
                clip.loop();
            }
        }
    }

    @Override
    public synchronized void musicPlay() {
        if (clip != null) {
            clip.stop();
            if (options.isSound()) {
                clip.play();
            }
        }
    }

    @Override
    public synchronized void musicSet(AudioClip clip) {
        clip.stop();
        this.clip = clip;
    }

    @Override
    public void musicSet(URL... clip) {
        musicSet(resources.load(AudioClip.class, clip));
    }

    @Override
    public synchronized void musicStop() {
        if (clip != null) {
            clip.stop();
        }
    }

    @Override
    public void screenShow(String name) {
        screenShow(screenGet(name));
    }

    private void screenShow(JScreen screen) {
        pnlCards.setComponent(screen);
        layout.show(pnlCards, screen.getId());
        setSize(screen.getPreferredSize());
        pnlCards.reset();
        currentScreen = screen;
    }

    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                try {
                    String action = intro.process();
                    if (action == null) {
                        return;
                    }
                    if (JGame.SCR_GAME.equals(action)) {
                        game();
                        getHiScore().score(getUserName(), game.getPlayerScore());
                    }
                    if (JStatus.SCR_STATUS.equals(action)) {
                        running = false;
                    }
                    if (JHi.SCR_HI.equals(action)) {
                        hi.process();
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        } finally {
            doDestroy();
            exit();
        }
    }

    private void screenAdd(JScreen screen) {
        screens.put(screen.getId(), screen);
        pnlCards.add(screen, screen.getId());
    }

    @Override
    public JScreen screenGet(String name) {
        return screens.get(name);
    }

    @Override
    public void soundPlay(AudioClip clip) {
        clip.play();
    }

    @Override
    public void soundPlay(URL... clip) {
        soundPlay(resources.load(AudioClip.class, clip));
    }

    @Override
    public void setSize(int width, int height) {
        if (options.isResize()) {
            pnlCards.setManaged(false);
            super.setSize(width, height);
        } else {
            pnlCards.setManaged(true);
        }
    }
}
