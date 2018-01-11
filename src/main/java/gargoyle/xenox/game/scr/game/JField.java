package gargoyle.xenox.game.scr.game;

import gargoyle.xenox.Xenox;
import gargoyle.xenox.game.scr.game.game.Ball;
import gargoyle.xenox.game.scr.game.game.Field;
import gargoyle.xenox.game.scr.game.game.Item;
import gargoyle.xenox.util.res.Resources;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URL;

class JField extends JComponent {
    private static final Color COLOR = new Color(255, 0, 0, 128);
    private static final Color COLOR_PATH = COLOR;
    private static final int FONT_SIZE = 12;
    private static final double ROUND = 1.5;
    private static transient Image imgFreeBall;
    private static transient Image imgItemBallMinus;
    private static transient Image imgItemBallPlus;
    private static transient Image imgItemDeath;
    private static transient Image imgItemLevel;
    private static transient Image imgItemLife;
    private static transient Image imgItemScore;
    private static transient Image imgPlayer;
    private static transient Image imgWallBall;
    private static transient volatile boolean init;
    private final JGame game;
    private final Resources resources;
    private transient Image cover;
    private transient Image image;

    public JField(JGame game, Resources resources) {
        this.game = game;
        this.resources = resources;
        if (!init) {
            imgFreeBall = this.resources.load(BufferedImage.class, "freeball.gif");
            imgWallBall = this.resources.load(BufferedImage.class, "wallball.gif");
            imgPlayer = this.resources.load(BufferedImage.class, "player.gif");
            imgItemBallMinus = this.resources.load(BufferedImage.class, "itemballminus.gif");
            imgItemBallPlus = this.resources.load(BufferedImage.class, "itemballplus.gif");
            imgItemLife = this.resources.load(BufferedImage.class, "itemlife.gif");
            imgItemLevel = this.resources.load(BufferedImage.class, "itemlevel.gif");
            imgItemScore = this.resources.load(BufferedImage.class, "itemscore.gif");
            imgItemDeath = this.resources.load(BufferedImage.class, "itemdeath.gif");
            init = true;
        }
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE));
    }

    private void drawBall(Graphics g, Ball ball) {
        Point position = ball.getPosition();
        drawImage(g, getRect(position),
                game.getField().is(position.x, position.y, Field.C_FREE) ? imgFreeBall : imgWallBall);
    }

    private void drawBalls(Graphics g) {
        for (Ball ball : game.getField().getBalls()) {
            drawBall(g, ball);
        }
    }

    private void drawEmpty(Graphics g, Rectangle rectangle) {
        Color c = g.getColor();
        g.setColor(Color.BLACK);
        if (cover != null) {
            drawImageCell(g, cover, rectangle);
        } else {
            g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        g.setColor(c);
    }

    private void drawImage(Graphics g, Rectangle rectangle, Image img) {
        g.drawImage(img, rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, 0, 0,
                img.getWidth(this), img.getHeight(this), this);
    }

    private void drawImageCell(Graphics g, Image img, Rectangle rectangle) {
        if (img != null) {
            double dw = img.getWidth(this) / (double) getWidth();
            double dh = img.getHeight(this) / (double) getHeight();
            g.drawImage(img, rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height,
                    (int) (rectangle.x * dw), (int) (rectangle.y * dh), (int) ((rectangle.x + rectangle.width) * dw),
                    (int) ((rectangle.y + rectangle.height) * dh), this);
        }
    }

    private void drawItem(Graphics g) {
        Field field = game.getField();
        Item item = field.getItem();
        if (item != null) {
            Color c = g.getColor();
            Point position = item.getPosition();
            Rectangle rectangle = getRect(position);
            if (field.is(position.x, position.y, Field.C_PATH)) {
                drawEmpty(g, rectangle);
            }
            if (field.is(position.x, position.y, Field.C_WALL)) {
                drawWall(g, rectangle);
            }
            switch (item.getType()) {
                case Item.I_MINUS_BALL:
                    drawImage(g, rectangle, imgItemBallMinus);
                    break;
                case Item.I_PLUS_BALL:
                    drawImage(g, rectangle, imgItemBallPlus);
                    break;
                case Item.I_LIFE:
                    drawImage(g, rectangle, imgItemLife);
                    break;
                case Item.I_LEVEL:
                    drawImage(g, rectangle, imgItemLevel);
                    break;
                case Item.I_SCORE:
                    drawImage(g, rectangle, imgItemScore);
                    break;
                case Item.I_DEATH:
                    drawImage(g, rectangle, imgItemDeath);
                    break;
                default:
                    break;
            }
            g.setColor(c);
        }
    }

    private void drawPath(Graphics g, Rectangle rectangle) {
        Color c = g.getColor();
        if (cover != null) {
            g.setColor(COLOR_PATH);
            drawImageCell(g, cover, rectangle);
        } else {
            g.setColor(Color.RED);
        }
        g.fill3DRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, true);
        g.setColor(c);
    }

    private void drawPlayer(Graphics g) {
        Color c = g.getColor();
        Field field = game.getField();
        if (field.getPlayer() == null) {
            return;
        }
        Point position = field.getPlayer().getPosition();
        Rectangle rectangle = getRect(position);
        if (field.is(position.x, position.y, Field.C_PATH)) {
            drawEmpty(g, rectangle);
        }
        if (field.is(position.x, position.y, Field.C_WALL)) {
            drawWall(g, rectangle);
        }
        drawImage(g, rectangle, imgPlayer);
        g.setColor(c);
    }

    private void drawWall(Graphics g, Rectangle rectangle) {
        Color c = g.getColor();
        g.setColor(Color.BLUE);
        if (image != null) {
            drawImageCell(g, image, rectangle);
        }
        g.draw3DRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, true);
        g.setColor(c);
    }

    private Rectangle getRect(Point position) {
        Rectangle r = new Rectangle();
        getRect(r, position);
        return r;
    }

    private void getRect(Rectangle r, Point position) {
        double cellSizeX = getWidth() / (double) game.getField().getWidth();
        double cellSizeY = getHeight() / (double) game.getField().getHeight();
        r.setBounds((int) (-ROUND + position.x * cellSizeX), (int) (-ROUND + position.y * cellSizeY),
                (int) (ROUND + cellSizeX), (int) (ROUND + cellSizeY));
    }

    @Override
    public Dimension getPreferredSize() {
        return image == null ? game.getField() == null ?
                super.getPreferredSize() :
                new Dimension(game.getField().getWidth() * Xenox.PF_CELL,
                        game.getField().getHeight() * Xenox.PF_CELL) :
                new Dimension(image.getWidth(this), image.getHeight(this));
    }

    @Override
    public void paint(Graphics g) {
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Field field = game.getField();
        if (field != null && field.getWidth() != 0 && field.getHeight() != 0) {
            Point position = new Point();
            Rectangle rectangle = new Rectangle();
            for (position.x = 0; position.x < field.getWidth(); position.x++) {
                for (position.y = 0; position.y < field.getHeight(); position.y++) {
                    getRect(rectangle, position);
                    switch (field.get(position.x, position.y)) {
                        case Field.C_FREE:
                            drawEmpty(g, rectangle);
                            break;
                        case Field.C_WALL:
                            drawWall(g, rectangle);
                            break;
                        case Field.C_PATH:
                            drawPath(g, rectangle);
                            break;
                        default:
                            break;
                    }
                }
            }
            drawPlayer(g);
            drawBalls(g);
            drawItem(g);
        } else {
            if (cover != null) {
                g.drawImage(cover, 0, 0, getWidth(), getHeight(), 0, 0, cover.getWidth(this), cover.getHeight(this), this);
            } else {
                super.paint(g);
            }
        }
    }

    public void setCover(URL cover) {
        this.cover = resources.load(BufferedImage.class, cover);
    }

    public void setImage(URL image) {
        this.image = resources.load(BufferedImage.class, image);
    }
}
