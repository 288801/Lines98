package ru.vsu.cs.course1.game;

/**
 * Класс для хранения параметров игры
 */
public class Lines98Params {
    private int rowCount;
    private int colCount;
    private int ballsCount;
    private int miniBallsCount;

    public Lines98Params(int rowCount, int colCount, int ballsCount, int miniBallsCount) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.ballsCount = ballsCount;
        this.miniBallsCount = miniBallsCount;

    }

    public Lines98Params() {
        this(9, 9, 5,3);
    }

    /**
     * @return the colCount
     */
    public int getColCount() {
        return colCount;
    }

    /**
     * @param colCount the colCount to set
     */
    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    /**
     * @return the rowCount
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * @param rowCount the rowCount to set
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * @return the colorCount
     */
    public int getBallsCount() {
        return ballsCount;
    }

    /**
     * @param colorCount the colorCount to set
     */
    public void setBallsCount(int colorCount) {
        this.ballsCount = colorCount;
    }

    public int getMiniBallsCount() {
        return miniBallsCount;
    }

    public void setMiniBallsCount(int miniBallsCount) {
        this.miniBallsCount = miniBallsCount;
    }
}
