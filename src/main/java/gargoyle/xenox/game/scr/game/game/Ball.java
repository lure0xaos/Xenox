package gargoyle.xenox.game.scr.game.game;

import java.awt.Point;

public class Ball {
    private final Point delta = new Point();
    private final Point position = new Point();
    private final Point newPosition = new Point();
    private boolean noX;
    private boolean noY;

    public Ball(int posX, int posY, int dX, int dY) {
        position.setLocation(posX, posY);
        delta.setLocation(dX, dY);
    }

    public Point getDelta() {
        return delta;
    }

    public Point getNewPosition() {
        return newPosition;
    }

    public Point getPosition() {
        return position;
    }

    public boolean isNoX() {
        return noX;
    }

    public void setNoX(boolean noX) {
        this.noX = noX;
    }

    public boolean isNoY() {
        return noY;
    }

    public void setNoY(boolean noY) {
        this.noY = noY;
    }
}
