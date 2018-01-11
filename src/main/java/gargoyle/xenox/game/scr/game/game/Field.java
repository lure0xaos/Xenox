package gargoyle.xenox.game.scr.game.game;

import gargoyle.xenox.Xenox;
import gargoyle.xenox.game.scr.game.JGame;
import gargoyle.xenox.game.sys.Controls;
import gargoyle.xenox.util.res.Resources;
import gargoyle.xenox.util.res.audio.AudioClip;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Stack;

public class Field {
    public static final byte C_FREE = 0;
    public static final byte C_PATH = 2;
    public static final byte C_WALL = 1;
    private static final byte C_NO = 3;
    private static final Random rnd = new Random();
    private final Resources resources;
    private List<Ball> balls;
    private byte f[][];
    private Item gotItem;
    private int height;
    private Item item;
    private Level level;
    private int levelNum;
    private int percent;
    private Player player;
    private int width;

    public Field(Resources resources) {
        this.resources = resources;
    }

    private void cancelPath() {
        Point position = player.getPosition();
        position.setLocation(player.getLast());
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (is(x, y, C_PATH)) {
                    set(x, y, C_FREE);
                }
            }
        }
    }

    @SuppressWarnings("LawOfDemeter")
    public boolean gameStep(JGame game) {
        Controls controls = game.getControls();
        Point position = player.getPosition();
        Point old = new Point(position);
        if (is(position.x, position.y, C_PATH) && controls.isAction()) {
            cancelPath();
        } else if (controls.isUp()) {
            if (position.y > 0) {
                position.y--;
            }
        } else if (controls.isDown()) {
            if (position.y < height - 1) {
                position.y++;
            }
        } else if (controls.isLeft()) {
            if (position.x > 0) {
                position.x--;
            }
        } else if (controls.isRight()) {
            if (position.x < width - 1) {
                position.x++;
            }
        }
        if (is(position.x, position.y, C_PATH)) {
            position.setLocation(old);
        }
        boolean lostLife = false;
        for (Ball ball : balls) {
            if (Objects.equals(ball.getPosition(), position)) {
                looseLife();
                lostLife = true;
                break;
            }
        }
        if (!lostLife) {
            if (is(position.x, position.y, C_WALL)) {
                player.getLast().setLocation(position);
            }
            if (is(position.x, position.y, C_FREE)) {
                set(position.x, position.y, C_PATH);
            }
            boolean got = false;
            if (item != null && item.getPosition().equals(player.getPosition())) {
                gotItem = item;
                item = null;
                if (gotItem(Item.I_MINUS_BALL, Item.I_PLUS_BALL, Item.I_SCORE, Item.I_LIFE)) {
                    got = true;
                }
            }
            if (is(position.x, position.y, C_WALL) && is(old.x, old.y, C_PATH)) {
                got = lineFinished();
            }
            if (got) {
                if (game.getOptions().isSound()) {
                    game.soundPlay(resources.load(AudioClip.class, resources.urls(true, Xenox.SND_GOT)));
                }
            }
        }
        Point newPos = new Point();
        Point position1 = new Point();
        for (Ball ball : balls) {
            position1.setLocation(ball.getPosition());
            byte free = get(position1.x, position1.y);
            Point delta = ball.getDelta();
            newPos.setLocation(position1.x + delta.x, position1.y + delta.y);
            Point newPos2 = null;
            for (Ball ball2 : balls) {
                Point position2 = ball2.getPosition();
                Point delta2 = ball2.getDelta();
                newPos2 = new Point(position2.x + delta2.x, position2.y + delta2.y);
                if (ball == ball2 || !newPos.equals(newPos2)) {
                    newPos2 = null;
                }
            }
            ball.setNoX(false);
            ball.setNoY(false);
            if (!isIn(newPos.x, newPos.y) || !is(newPos.x, newPos.y, free) || newPos2 != null) {
                if (isIn(newPos.x, position1.y) && is(newPos.x, position1.y, free) && isIn(position1.x, newPos.y) && is(position1.x, newPos.y, free) || newPos2 != null &&
                        (newPos.x != newPos2.x || position1.y != newPos2.y) &&
                        (position1.x != newPos2.x || newPos.y != newPos2.y)) {
                    delta.x = -delta.x;
                    delta.y = -delta.y;
                    ball.setNoX(true);
                    ball.setNoY(true);
                } else {
                    if (!isIn(newPos.x, position1.y) || !is(newPos.x, position1.y, free) ||
                            newPos2 != null && newPos.x == newPos2.x && position1.y == newPos2.y) {
                        delta.x = -delta.x;
                        ball.setNoX(true);
                    }
                    if (free == C_FREE && is(newPos.x, position1.y, C_PATH)) {
                        looseLife();
                        lostLife = true;
                    } else {
                        if (!isIn(position1.x, newPos.y) || !is(position1.x, newPos.y, free) ||
                                newPos2 != null && position1.x == newPos2.x && newPos.y == newPos2.y) {
                            delta.y = -delta.y;
                            ball.setNoY(true);
                        }
                        if (free == C_FREE && is(position1.x, newPos.y, C_PATH)) {
                            looseLife();
                            lostLife = true;
                        }
                    }
                }
                if (free == C_FREE && is(newPos.x, newPos.y, C_PATH)) {
                    looseLife();
                    lostLife = true;
                }
                if (ball.isNoX() && !ball.isNoY()) {
                    position1.y += delta.y;
                }
                if (ball.isNoY() && !ball.isNoX()) {
                    position1.x += delta.x;
                }
                ball.getNewPosition().setLocation(position1);
            } else {
                position1.setLocation(newPos);
                ball.getNewPosition().setLocation(newPos);
            }
        }
        for (Ball ball : balls) {
            ball.getPosition().setLocation(ball.getNewPosition());
        }
        if (!lostLife) {
            if (item == null) {
                if (rnd.nextInt(Xenox.ITEM_SHOW) == 0) {
                    item = new Item();
                    Point pos = new Point();
                    do {
                        pos.x = rnd.nextInt(width);
                        pos.y = rnd.nextInt(height);
                    } while (!is(pos.x, pos.y, C_FREE));
                    item.setPosition(pos);
                }
            } else {
                if (rnd.nextInt(Xenox.ITEM_HIDE) == 0) {
                    item = null;
                }
            }
        }
        return lostLife;
    }

    public byte get(int x, int y) {
        return isIn(x, y) ? f[x][y] : C_WALL;
    }

    public Collection<Ball> getBalls() {
        return Collections.unmodifiableList(balls);
    }

    public int getHeight() {
        return height;
    }

    public Item getItem() {
        return item;
    }

    public Level getLevel() {
        return level;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public int getPercent() {
        return percent;
    }

    public Player getPlayer() {
        return player;
    }

    public int getWidth() {
        return width;
    }

    private boolean gotItem(byte... types) {
        if (gotItem != null) {
            for (byte type : types) {
                if (type == gotItem.getType()) {
                    switch (type) {
                        case Item.I_LIFE:
                            player.setLives(player.getLives() + 1);
                            break;
                        case Item.I_SCORE:
                            player.setScore(player.getScore() + 100);
                            break;
                        case Item.I_MINUS_BALL:
                            balls.remove(rnd.nextInt(balls.size()));
                            break;
                        case Item.I_PLUS_BALL:
                            Point pos = new Point();
                            if (rnd.nextBoolean()) {
                                do {
                                    pos.setLocation(rnd.nextInt(width - 2) + 1,
                                            rnd.nextInt(height - 2) + 1);
                                } while (get(pos.x, pos.y) != C_FREE);
                                Ball
                                        ball =
                                        new Ball(pos.x, pos.y, (rnd.nextInt() & 1) == 0 ? 1 : -1,
                                                (rnd.nextInt() & 1) == 0 ? 1 : -1);
                                balls.add(ball);
                            } else {
                                do {
                                    pos.setLocation(rnd.nextInt(width - 2) + 1, height - 1);
                                } while (get(pos.x, pos.y) != C_WALL);
                                Ball
                                        ball =
                                        new Ball(pos.x, pos.y, (rnd.nextInt() & 1) == 0 ? 1 : -1,
                                                (rnd.nextInt() & 1) == 0 ? 1 : -1);
                                balls.add(ball);
                            }
                            break;
                        default:
                            break;
                    }
                    gotItem = null;
                    item = null;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasGotItem(boolean last, byte... types) {
        if (gotItem != null) {
            for (byte type : types) {
                if (type == gotItem.getType()) {
                    if (last) {
                        gotItem = null;
                        item = null;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void init() {
        int width = level.getWidth();
        int height = level.getHeight();
        this.width = width;
        this.height = height;
        f = new byte[width][height];
        balls = new ArrayList<>(level.getBalls());
        player = new Player(level.getLives());
        percent = 0;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (x == 0 || y == 0 || x == this.width - 1 || y == this.height - 1) {
                    set(x, y, C_WALL);
                } else {
                    set(x, y, C_FREE);
                }
            }
        }
        reset();
    }

    public void init(int levelNum, Level level) {
        this.levelNum = levelNum;
        this.level = level;
        init();
    }

    private void initPlayerLocation() {
        player.getPosition().setLocation((width - 2) / 2, 0);
    }

    public boolean is(int x, int y, byte c) {
        return isIn(x, y) ? f[x][y] == c : c == C_WALL;
    }

    private boolean isIn(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    private boolean lineFinished() {
        Stack<Point> stk = new Stack<>();
        for (Ball ball : balls) {
            Point position = ball.getPosition();
            if (C_FREE != get(position.x, position.y)) {
                continue;
            }
            set(position.x, position.y, C_NO);
            stk.push(new Point(position));
            while (!stk.isEmpty()) {
                Point p = stk.pop();
                if (is(p.x - 1, p.y, C_FREE)) {
                    set(p.x - 1, p.y, C_NO);
                    stk.push(new Point(p.x - 1, p.y));
                }
                if (is(p.x + 1, p.y, C_FREE)) {
                    set(p.x + 1, p.y, C_NO);
                    stk.push(new Point(p.x + 1, p.y));
                }
                if (is(p.x, p.y - 1, C_FREE)) {
                    set(p.x, p.y - 1, C_NO);
                    stk.push(new Point(p.x, p.y - 1));
                }
                if (is(p.x, p.y + 1, C_FREE)) {
                    set(p.x, p.y + 1, C_NO);
                    stk.push(new Point(p.x, p.y + 1));
                }
            }
        }
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (is(x, y, C_FREE) || is(x, y, C_PATH)) {
                    player.setScore(player.getScore() + 1);
                    set(x, y, C_WALL);
                }
            }
        }
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (is(x, y, C_NO)) {
                    set(x, y, C_FREE);
                }
            }
        }
        int occ = 0;
        boolean ret = false;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (is(x, y, C_WALL)) {
                    occ++;
                    if (item != null) {
                        Point pos = item.getPosition();
                        if (pos.x == x && pos.y == y) {
                            gotItem = item;
                            item = null;
                            if (gotItem(Item.I_MINUS_BALL, Item.I_PLUS_BALL, Item.I_SCORE, Item.I_LIFE)) {
                                ret = true;
                            }
                        }
                    }
                }
            }
        }
        percent = (int) (occ * 100 / (double) (width * height));
        return ret;
    }

    private void looseLife() {
        Point position = player.getPosition();
        if (is(position.x, position.y, C_WALL)) {
            initPlayerLocation();
        } else {
            position.setLocation(player.getLast());
        }
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (is(x, y, C_PATH)) {
                    set(x, y, C_FREE);
                }
            }
        }
        player.setLives(player.getLives() - 1);
    }

    private void reset() {
        initPlayerLocation();
        setBalls();
        gotItem = null;
        item = null;
    }

    public void resetBalls() {
        Point pos = new Point();
        for (Ball ball : balls) {
            Point position = ball.getPosition();
            if (is(position.x, position.y, C_WALL)) {
                do {
                    position.setLocation(rnd.nextInt(width - 2) + 1, height - 1);
                } while (get(pos.x, pos.y) != C_WALL);
            }
        }
        for (Ball ball : balls) {
            ball.getDelta()
                    .setLocation((rnd.nextInt() & 1) == 0 ? 1 : -1, (rnd.nextInt() & 1) == 0 ? 1 : -1);
        }
    }

    private void set(int x, int y, byte c) {
        if (isIn(x, y)) {
            f[x][y] = c;
        }
    }

    private void setBalls() {
        balls.clear();
        int b2 = level.getBalls() / 2;
        int b1 = level.getBalls() - b2;
        Point pos = new Point();
        for (int b = 0; b < b1; b++) {
            do {
                pos.setLocation(rnd.nextInt(width - 2) + 1, rnd.nextInt(height - 2) + 1);
            } while (get(pos.x, pos.y) != C_FREE);
            Ball
                    ball =
                    new Ball(pos.x, pos.y, (rnd.nextInt() & 1) == 0 ? 1 : -1,
                            (rnd.nextInt() & 1) == 0 ? 1 : -1);
            balls.add(ball);
        }
        for (int b = 0; b < b2; b++) {
            do {
                pos.setLocation(rnd.nextInt(width - 2) + 1, height - 1);
            } while (get(pos.x, pos.y) != C_WALL);
            Ball
                    ball =
                    new Ball(pos.x, pos.y, (rnd.nextInt() & 1) == 0 ? 1 : -1,
                            (rnd.nextInt() & 1) == 0 ? 1 : -1);
            balls.add(ball);
        }
    }
}
