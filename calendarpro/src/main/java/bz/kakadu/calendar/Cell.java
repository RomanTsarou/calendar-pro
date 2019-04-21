package bz.kakadu.calendar;


/**
 * Created on 24.11.2016
 *
 * @author Roman Tsarou
 */
public final class Cell {
    final Day day = new Day();
    String value;
    boolean isVisible;
    private int drawPos;

    Cell() {
    }

    public int getDrawPosition() {
        return drawPos;
    }

    public void setDrawPosition(int pos) {
        drawPos = pos;
    }

    public Day getDay() {
        return day;
    }

    public String getValue() {
        return value;
    }
}
