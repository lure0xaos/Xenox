package gargoyle.xenox.game.scr.menu;

import gargoyle.xenox.util.log.Log;

import javax.swing.JComponent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.Serializable;
import java.lang.Thread.State;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class JFlame extends JComponent implements Runnable {
    private static final int INT0x00FF0000 = 0x00ff0000;
    private static final int INT0x00FFFFFF = 0x00ffffff;
    private static final int INT0xFF000000 = 0xff000000;
    private static final int INT0xFF00FF00 = 0xff00ff00;
    private static final int INT0xFF00FFFF = 0xff00ffff;
    private static final int INT0xFFFFFF00 = 0xffffff00;
    private static final int INT130 = 130;
    private static final int INT16 = 16;
    private static final int INT210 = 210;
    private static final int INT255 = 255;
    private static final int INT0xff = INT255;
    private static final int INT256 = 256;
    private static final int INT240 = INT256 - INT16;
    private static final int INT40 = INT256 / 6;
    private static final int INT60 = INT256 / 4;
    private static final int INT65280 = INT0xff * INT256;
    private static final int INT8 = 8;
    private static final int INT90 = 90;
    private static final int SLEEP100 = 100;
    private static final int SLEEP20 = 20;
    private static final int TW_MAX_COLOR = 50;
    private static final long serialVersionUID = -2155412932761138317L;
    private final int pal[] = new int[INT256];
    private final FlameParams params;
    private final Random rnd = new Random();
    private final int twColor[] = new int[TW_MAX_COLOR];
    private volatile boolean active = true;
    private int alpha;
    private transient Image art;
    private int[] buf;
    private boolean burn = true;
    private volatile boolean canDisplay;
    private int colMask = INT0x00FF0000;
    private int colShift = INT16;
    private transient Image flameImage;
    private transient MemoryImageSource flameSource;
    private volatile boolean running;
    private transient volatile Thread thread;
    private long twTimer;
    private int[] twWords;
    private int xSize;
    private int ySize;

    public JFlame(FlameParams params) {
        this.params = new FlameParams(params);
    }

    private static int rgb(int r, int g, int b) {
        return INT0xFF000000 | b << INT16 | r << INT8 | g;
    }

    private static long time() {
        return System.currentTimeMillis();
    }

    public void destroy() {
        canDisplay = false;
        if (thread != null) {
            if (art != null) {
                art.flush();
            }
            running = false;
            thread = null;
        }
    }

    public long fade() {
        burn = false;
        return params.getFadeDelay();
    }

    public void init() {
        setBackground(Color.BLACK);
        canDisplay = false;
        if (thread == null) {
            thread = new Thread(this, JFlame.class.getName());
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        } else {
            if (thread.getState() == State.NEW) {
                thread.start();
            }
        }
    }

    public boolean isCanDisplay() {
        return canDisplay;
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    private void makePal(boolean aSolid) {
        if (aSolid) {
            for (int i = 0; i < TW_MAX_COLOR; i++) {
                twColor[i] = pal[INT210 + rnd.nextInt(INT40) & INT0xff];
            }
        }
    }

    private void makePal(int n) {
        switch (n) {
            case 2:
                makePal2();
                break;
            case 3:
                makePal3();
                break;
            case 4:
                makePal4();
                break;
            case 5:
                makePal5();
                break;
            case 6:
                makePal6();
                break;
            case 1:
            default:
                makePal1();
                break;
        }
    }

    private void makePal1() {
        colMask = INT0x00FF0000;
        colShift = INT16;
        for (int i = 0; i < INT256; i++) {
            pal[i] = INT0xFF000000;
        }
        int r = 1;
        int g = 1;
        int i1 = 3;
        int i2 = 5;
        for (int i = 1; i < INT60; i++) {
            pal[i] = rgb(r, g, i);
            if (i1 == 0) {
                i1 = 5;
                r++;
            }
            if (i2 == 0) {
                i2 = 7;
                g++;
            }
            i1--;
            i2--;
        }
        for (int i = INT60; i < INT90; i++) {
            pal[i] = rgb(r, g, i);
            if (i1 == 0) {
                i1 = 3;
                r++;
            }
            if (i2 == 0) {
                i2 = 6;
                g++;
            }
            i1--;
            i2--;
        }
        for (int i = INT90; i < INT130; i++) {
            pal[i] = rgb(r, g, i);
            if (r < INT255) {
                r++;
            }
            if (i2 == 0) {
                i2 = 6;
                g++;
            }
            i2--;
        }
        for (int i = INT130; i < INT210; i++) {
            pal[i] = rgb(r, g, i);
            r += 2;
            if (r >= INT255) {
                r = INT255;
            }
            if (i2 == 0) {
                i2 = 5;
                g++;
            }
            i2--;
        }
        for (int i = INT210; i < INT256; i++) {
            pal[i] = rgb(r, g, i);
            r += 3;
            if (r >= INT255) {
                r = INT255;
            }
            if (i2 == 0) {
                i2 = 4;
                g++;
            }
            i2--;
        }
        for (int i = 0; i < TW_MAX_COLOR; i++) {
            twColor[i] = pal[INT240 - i] & INT0xFFFFFF00 | pal[INT240 - i] >> INT16 & INT0xff;
        }
    }

    private void makePal2() {
        colMask = INT65280;
        colShift = INT8;
        for (int i = 0; i < INT256; i++) {
            pal[i] = INT0xFF000000;
        }
        int g = 1;
        int b = 1;
        int i1 = 3;
        int i2 = 5;
        for (int i = 1; i < INT60; i++) {
            pal[i] = rgb(i, g, b);
            if (i1 == 0) {
                i1 = 5;
                g++;
            }
            if (i2 == 0) {
                i2 = 7;
                b++;
            }
            i1--;
            i2--;
        }
        for (int i = INT60; i < INT90; i++) {
            pal[i] = rgb(i, g, b);
            if (i1 == 0) {
                i1 = 3;
                g++;
            }
            if (i2 == 0) {
                i2 = 5;
                b++;
            }
            i1--;
            i2--;
        }
        for (int i = INT90; i < INT130; i++) {
            pal[i] = rgb(i, g, b);
            if (g < INT255) {
                g++;
            }
            if (b < INT255) {
                b++;
            }
            i2--;
        }
        for (int i = INT130; i < INT210; i++) {
            pal[i] = rgb(i, g, b);
            g += 2;
            if (g >= INT255) {
                g = INT255;
            }
            if (b < INT255) {
                b++;
            }
        }
        for (int i = INT210; i < INT256; i++) {
            pal[i] = rgb(i, g, b);
            g += 3;
            if (g >= INT255) {
                g = INT255;
            }
            b += 2;
            if (b >= INT255) {
                b = INT255;
            }
        }
        for (int i = 0; i < TW_MAX_COLOR; i++) {
            twColor[i] = pal[INT240 - i] & INT0xFF00FF00 | INT0x00FF0000 & pal[INT240 - i] << INT8;
        }
    }

    private void makePal3() {
        colMask = INT255;
        colShift = 0;
        for (int i = 0; i < INT256; i++) {
            pal[i] = INT0xFF000000;
        }
        int r = 1;
        int b = 1;
        int i1 = 3;
        int i2 = 5;
        for (int i = 1; i < INT60; i++) {
            pal[i] = rgb(r, i, b);
            if (i1 == 0) {
                i1 = 5;
                r++;
            }
            if (i2 == 0) {
                i2 = 7;
                b++;
            }
            i1--;
            i2--;
        }
        for (int i = INT60; i < INT90; i++) {
            pal[i] = rgb(r, i, b);
            if (i1 == 0) {
                i1 = 3;
                r++;
            }
            if (i2 == 0) {
                i2 = 5;
                b++;
            }
            i1--;
            i2--;
        }
        for (int i = INT90; i < INT130; i++) {
            pal[i] = rgb(r, i, b);
            if (r < INT255) {
                r++;
            }
            if (b < INT255) {
                b++;
            }
            i2--;
        }
        for (int i = INT130; i < INT210; i++) {
            pal[i] = rgb(r, i, b);
            ++r;
            if (r >= INT255) {
                r = INT255;
            }
            if (b < INT255) {
                b++;
            }
        }
        for (int i = INT210; i < INT256; i++) {
            pal[i] = rgb(r, i, b);
            r += 3;
            if (r >= INT255) {
                r = INT255;
            }
            b += 2;
            if (b >= INT255) {
                b = INT255;
            }
        }
        for (int i = 0; i < TW_MAX_COLOR; i++) {
            twColor[i] = rgb(INT255 - i, INT255 - i, INT255 - i);
        }
    }

    private void makePal4() {
        colMask = INT0x00FF0000;
        colShift = INT16;
        for (int i = 0; i < INT256; i++) {
            pal[i] = INT0xFF000000;
        }
        int r = 1;
        int g = 1;
        int i1 = 3;
        int i2 = 5;
        for (int i = 1; i < INT60; i++) {
            pal[i] = rgb(r, g, i);
            if (i1 == 0) {
                i1 = 7;
                r++;
            }
            if (i2 == 0) {
                i2 = 3;
                g++;
            }
            i1--;
            i2--;
        }
        for (int i = INT60; i < INT90; i++) {
            pal[i] = rgb(r, g, i);
            if (i1 == 0) {
                i1 = 5;
                r++;
            }
            if (i2 == 0) {
                i2 = 2;
                g++;
            }
            i1--;
            i2--;
        }
        for (int i = INT90; i < INT130; i++) {
            pal[i] = rgb(r, g, i);
            if (i1 == 0) {
                i1 = 3;
                r++;
            }
            if (g < INT255) {
                g++;
            }
            i1--;
        }
        for (int i = INT130; i < INT210; i++) {
            pal[i] = rgb(r, g, i);
            if (r < INT255) {
                r++;
            }
            g += 2;
            if (g >= INT255) {
                g = INT255;
            }
        }
        for (int i = INT210; i < INT256; i++) {
            pal[i] = rgb(r, g, i);
            r += 2;
            if (r >= INT255) {
                r = INT255;
            }
            g += 4;
            if (g >= INT255) {
                g = INT255;
            }
        }
        for (int i = 0; i < TW_MAX_COLOR; i++) {
            twColor[i] = rgb(INT255 - i, INT255 - i, INT255 - i);
        }
    }

    private void makePal5() {
        colMask = INT65280;
        colShift = INT8;
        for (int i = 0; i < INT256; i++) {
            pal[i] = rgb(i, i, i);
        }
        for (int i = 0; i < TW_MAX_COLOR; i++) {
            twColor[i] = pal[INT240 - i] & INT0xFF00FFFF;
        }
    }

    private void makePal6() {
        colMask = INT0x00FF0000;
        colShift = INT16;
        for (int i = 0; i < INT256; i++) {
            pal[i] = INT0xFF000000;
        }
        int r = 1;
        int g = 1;
        int i1 = 3;
        int i2 = 5;
        for (int i = 1; i < INT60; i++) {
            pal[i] = rgb(r, g, i);
            if (i1 == 0) {
                i1 = 9;
                r++;
            }
            if (i2 == 0) {
                i2 = 6;
                g++;
            }
            i1--;
            i2--;
        }
        for (int i = INT60; i < INT90; i++) {
            pal[i] = rgb(r, g, i);
            if (i1 == 0) {
                i1 = INT8;
                r++;
            }
            if (i2 == 0) {
                i2 = 5;
                g++;
            }
            i1--;
            i2--;
        }
        for (int i = INT90; i < INT130; i++) {
            pal[i] = rgb(r, g, i);
            if (i1 == 0) {
                i1 = 5;
                r++;
            }
            if (i2 == 0) {
                i2 = 3;
                g++;
            }
            i1--;
            i2--;
        }
        for (int i = INT130; i < INT210; i++) {
            pal[i] = rgb(r, g, i);
            ++r;
            if (r >= INT255) {
                r = INT255;
            }
            ++g;
            if (g >= INT255) {
                g = INT255;
            }
        }
        for (int i = INT210; i < INT256; i++) {
            pal[i] = rgb(r, g, i);
            r += 3;
            if (r >= INT255) {
                r = INT255;
            }
            g += 3;
            if (g >= INT255) {
                g = INT255;
            }
        }
        for (int i = 0; i < TW_MAX_COLOR; i++) {
            twColor[i] = rgb(INT255 - i, INT255 - i, INT255 - i);
        }
    }

    @Override
    public void paint(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            Composite c = g2d.getComposite();
            if (alpha != 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / (float) 100));
            }
            paint0(g);
            g2d.setComposite(c);
        } else {
            paint0(g);
        }
    }

    private void paint0(Graphics g) {
        if (canDisplay && art != null) {
            g.drawImage(art, 0, 0, getWidth(), getHeight(), 0, 0, art.getWidth(this), art.getHeight(this), Color.BLACK, this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, xSize, ySize);
        }
    }

    private boolean paintActive() {
        for (int l = 1; l < xSize - 1 - 1; l++) {
            int bb = xSize;
            int aa = bb - xSize;
            if (l < xSize - 1) {
                for (int j = 1; j < ySize; j++) {
                    if ((buf[l + bb] & colMask) >> colShift < params.getDecay()) {
                        buf[l + aa] = INT0xFF000000;
                    } else {
                        buf[aa + l - (rnd.nextInt(3) - 1)] =
                                pal[((buf[l + bb] & colMask) >> colShift) - rnd.nextInt(params.getDecay()) & INT0xff];
                    }
                    aa += xSize;
                    bb += xSize;
                }
            }
        }
        long l = time();
        if ((burn && (l < twTimer - params.getFadeDelay() || !params.isFade()))) {
            int i = 1;
            int j = 0;
            while (i < twWords[0]) {
                j += twWords[i];
                i++;
                int cnt = twWords[i];
                i++;
                while (cnt > 0) {
                    buf[j] = twColor[rnd.nextInt(TW_MAX_COLOR)];
                    j++;
                    --cnt;
                }
            }
        }
        if (l > twTimer) {
            twTimer = time() + params.getDelay();
        }
        flameSource.newPixels(0, 0, xSize, ySize);
        art.getGraphics().drawImage(flameImage, 0, 0, null);
        try {
            Thread.sleep(SLEEP20);
        } catch (InterruptedException e) {
            return true;
        }
        repaint();
        return false;
    }

    private void prepare() {
        xSize = getWidth();
        ySize = getHeight();
        int size = xSize * ySize;
        art = createImage(xSize, ySize);
        buf = new int[size];
        flameSource = new MemoryImageSource(xSize, ySize, buf, 0, xSize);
        flameSource.setAnimated(true);
        flameImage = createImage(flameSource);
        alpha = params.getAlpha();
        int twLen = params.getFontSize() * (xSize / (params.getFontSize() / INT8));
        makePal(params.getColor());
        makePal(!params.isSolid());
        Graphics g = art.getGraphics();
        g.setFont(new Font(params.getFont(), Font.PLAIN, params.getFontSize()));
        FontMetrics fontmetrics = g.getFontMetrics();
        String word = params.getText();
        PixelGrabber pixelgrabber = new PixelGrabber(art, 0, 0, xSize, ySize, buf, 0, xSize);
        int i1 = size - 1;
        while (i1 >= 0) {
            buf[i1] = INT0xFF000000;
            i1--;
        }
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, xSize, ySize);
        g.setColor(Color.WHITE);
        int y = 0;
        FlameParams.Align align = params.getAlign();
        switch (align) {
            case CENTER:
                y = (ySize - fontmetrics.getHeight()) / 2 + fontmetrics.getAscent() + params.getPosition();
                break;
            case TOP:
                y = fontmetrics.getAscent() + params.getPosition();
                break;
            case BOTTOM:
                y = ySize - fontmetrics.getHeight() + fontmetrics.getAscent() + params.getPosition();
                break;
        }
        g.drawString(word, (xSize - fontmetrics.stringWidth(word)) / 2, y);
        try {
            canDisplay = pixelgrabber.grabPixels();
        } catch (InterruptedException e) {
            running = false;
        }
        twWords = new int[twLen];
        if (canDisplay) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, xSize, ySize);
            int j = 1;
            int l = 0;
            while (l < size - 1 && j < twLen - 2) {
                int k = 0;
                while (l < size - 1 && (buf[l] & INT0x00FFFFFF) == 0) {
                    l++;
                    k++;
                }
                twWords[j] = k;
                j++;
                int i = 0;
                while (l < size - 1 && (buf[l] & INT0x00FFFFFF) != 0) {
                    l++;
                    i++;
                }
                twWords[j] = i;
                j++;
            }
            twWords[0] = j - 2;
        }
        for (int i = 0; i < size; i++) {
            buf[i] = INT0xFF000000;
        }
        burn = true;
        twTimer = 0;
        if (params.getDelay() != 0) {
            twTimer = time() + params.getDelay() - params.getFadeDelay();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void run() {
        running = true;
        prepare();
        while (running) {
            if (active) {
                try {
                    if (paintActive()) break;
                } catch (RuntimeException ignored) {
                }
            } else {
                try {
                    Thread.sleep(SLEEP100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        running = false;
        thread = null;
    }

    public void start() {
        active = true;
    }

    public void stop() {
        active = false;
    }

    @SuppressWarnings("WeakerAccess")
    static class FlameParams implements Serializable {
        private static final Align DEFAULT_ALIGN = Align.TOP;
        private static final int DEFAULT_ALPHA = 0;
        private static final int DEFAULT_COLOR = 1;
        private static final int DEFAULT_DECAY = 20;
        private static final long DEFAULT_DELAY = 10000L;
        private static final boolean DEFAULT_FADE = false;
        private static final long DEFAULT_FADE_DELAY = 2000L;
        private static final String DEFAULT_FONT = "Times New Roman";
        private static final int DEFAULT_FONT_SIZE = 140;
        private static final int DEFAULT_POSITION = 0;
        private static final boolean DEFAULT_SOLID = false;
        private static final String DEFAULT_TEXT = "Flames|by|IoN CheN";
        private static final String FONT_FAMILY_COURIER = "Courier";
        private static final String FONT_FAMILY_DIALOG = "Dialog";
        private static final String FONT_FAMILY_DIALOG_INPUT = "DialogInput";
        private static final String FONT_FAMILY_HELVETICA = "Helvetica";
        private static final String FONT_FAMILY_SYMBOL = "Symbol";
        private static final String FONT_VALUE_COURIER = "courier";
        private static final String FONT_VALUE_DIALOG = "dialog";
        @SuppressWarnings("SpellCheckingInspection")
        private static final String FONT_VALUE_DIALOG_INPUT = "dialoginput";
        private static final String FONT_VALUE_HELVETICA = "helvetica";
        private static final String FONT_VALUE_SYMBOL = "symbol";
        private static final int MAX_COLOR = 6;
        private static final int MAX_DECAY = 20;
        private static final int MIN_COLOR = 1;
        private static final int MIN_DECAY = 5;
        private static final int MIN_FONT_SIZE = 10;
        private Align align = DEFAULT_ALIGN;
        private int alpha = DEFAULT_ALPHA;
        private int color = DEFAULT_COLOR;
        private int decay = DEFAULT_DECAY;
        private long delay = DEFAULT_DELAY;
        private boolean fade = DEFAULT_FADE;
        private long fadeDelay = DEFAULT_FADE_DELAY;
        private String font = DEFAULT_FONT;
        private int fontSize = DEFAULT_FONT_SIZE;
        private int position = DEFAULT_POSITION;
        private boolean solid = DEFAULT_SOLID;
        private String text = DEFAULT_TEXT;

        public FlameParams() {
        }

        public FlameParams(FlameParams p) {
            align = p.align;
            alpha = p.alpha;
            color = p.color;
            decay = p.decay;
            delay = p.delay;
            fade = p.fade;
            fadeDelay = p.fadeDelay;
            font = p.font;
            fontSize = p.fontSize;
            position = p.position;
            solid = p.solid;
            text = p.text;
        }

        public Align getAlign() {
            return align;
        }

        public void setAlign(Align align) {
            this.align = align == null ? Align.CENTER : align;
        }

        public int getAlpha() {
            return alpha;
        }

        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color < FlameParams.MIN_COLOR || color > FlameParams.MAX_COLOR ? FlameParams.MIN_COLOR : color;
        }

        public int getDecay() {
            return decay;
        }

        public void setDecay(int decay) {
            this.decay = decay < FlameParams.MIN_DECAY || decay > FlameParams.MAX_DECAY ? DEFAULT_DECAY : decay;
        }

        public long getDelay() {
            return delay;
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }

        public long getFadeDelay() {
            return fadeDelay;
        }

        public void setFadeDelay(long fadeDelay) {
            this.fadeDelay = fadeDelay;
        }

        public String getFont() {
            return font;
        }

        public void setFont(String font) {
            switch (font) {
                case FlameParams.FONT_VALUE_COURIER:
                    this.font = FlameParams.FONT_FAMILY_COURIER;
                    break;
                case FlameParams.FONT_VALUE_DIALOG:
                    this.font = FlameParams.FONT_FAMILY_DIALOG;
                    break;
                case FlameParams.FONT_VALUE_DIALOG_INPUT:
                    this.font = FlameParams.FONT_FAMILY_DIALOG_INPUT;
                    break;
                case FlameParams.FONT_VALUE_HELVETICA:
                    this.font = FlameParams.FONT_FAMILY_HELVETICA;
                    break;
                case FlameParams.FONT_VALUE_SYMBOL:
                    this.font = FlameParams.FONT_FAMILY_SYMBOL;
                    break;
                default:
                    List<String> list = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
                    if (!list.contains(font)) {
                        Log.warn(String.format("invalid font name %s, using %s, choose from: %s", font, DEFAULT_FONT, list));
                        this.font = FlameParams.DEFAULT_FONT;
                    }
                    break;
            }
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize < FlameParams.MIN_FONT_SIZE ? FlameParams.MIN_FONT_SIZE : fontSize;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isFade() {
            return fade;
        }

        public void setFade(boolean fade) {
            this.fade = fade;
        }

        public boolean isSolid() {
            return solid;
        }

        public void setSolid(boolean solid) {
            this.solid = solid;
        }

        enum Align {
            TOP, BOTTOM, CENTER
        }
    }
}
