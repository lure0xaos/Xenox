package gargoyle.xenox.game.scr.game.game;

import java.awt.Point;
import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 2800736031194151590L;
    private final Point last = new Point();
    private final Point position = new Point();
    private int lives;
    private int score;

    public Player(int lives) {
        this.lives = lives;
        score = 0;
    }

    Point getLast() {
        return last;
    }

    public int getLives() {
        return lives;
    }

    void setLives(int lives) {
        this.lives = lives;
    }

    public Point getPosition() {
        return position;
    }

    public int getScore() {
        return score;
    }

    void setScore(int score) {
        this.score = score;
    }

    public boolean isAlive() {
        return lives > 0;
    }
}
