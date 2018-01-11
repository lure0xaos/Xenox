package gargoyle.xenox.game.scr.game.game;

import java.awt.Point;

public class Item {
    public static final byte I_DEATH = 6;
    public static final byte I_LEVEL = 5;
    public static final byte I_LIFE = 4;
    public static final byte I_MINUS_BALL = 2;
    public static final byte I_PLUS_BALL = 1;
    public static final byte I_SCORE = 3;
    private final Point position = new Point();
    private final byte type;

    public Item() {
        type = (byte) (1 + Math.random() * 6);
    }

    public Point getPosition() {
        return position;
    }

    void setPosition(Point position) {
        this.position.setLocation(position);
    }

    public byte getType() {
        return type;
    }
}
