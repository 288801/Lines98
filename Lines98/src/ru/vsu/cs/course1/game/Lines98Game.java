package ru.vsu.cs.course1.game;

import java.util.ArrayList;
import java.util.Random;

/**
 * Класс, реализующий логику игры
 */
public class Lines98Game {

    public enum GameState {
        NOT_STARTED,
        PLAYING,
        FAIL
    }


    public enum CellState {
        PRESSED,
        NOT_PRESSED
    }

    public enum ColorState {
        GREEN,
        RED,
        BLUE,
        BLACK,
        ORANGE,
        YELLOW,
        DARK_GREY,
        PINK

    }

    public static class LinesCell {
        private CellState state;
        private boolean ball;
        private boolean miniBall;
        private ColorState color;

        public LinesCell(CellState state, ColorState color) {
            this.state = state;
            this.color = color;
        }

        public CellState getState() {
            return state;
        }

        public boolean isBall() {
            return ball;
        }

        public boolean isMiniBall() {
            return miniBall;
        }

        public ColorState getColor() {
            return color;
        }

    }

    /**
     * объект Random для генерации случайных чисел
     * (можно было бы объявить как static)
     */
    private final Random rnd = new Random();

    /**
     * Состояние игры
     */
    GameState state = GameState.NOT_STARTED;
    /**
     * двумерный массив для хранения игрового поля
     * (в данном случае цветов, 0 - пусто; создается / пересоздается при старте игры)
     */
    private LinesCell[][] field = null;
    /**
     * Кол-во мин
     */
    private int ballsCount;
    private int miniBallsCount = 0;
    public int points = 0;

    public void newGame(int rowCount, int colCount, int ballsCount, int miniBallsCount) {
        // создаем пустое поле
        points = 0;
        field = new LinesCell[rowCount][colCount];
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                field[r][c] = new LinesCell(CellState.NOT_PRESSED, null);
            }
        }
        this.ballsCount = ballsCount;
        this.miniBallsCount = miniBallsCount;

        // расставляем шары
        for (int i = 0; i < ballsCount; i++) {
            int pos = rnd.nextInt(rowCount * colCount - i);
            one:
            for (int r = 0; r < rowCount; r++) {
                for (int c = 0; c < colCount; c++) {
                    if (!field[r][c].ball) {
                        if (pos == 0) {
                            LinesCell cell = field[r][c];
                            ColorState[] colors = {ColorState.GREEN, ColorState.RED, ColorState.BLUE, ColorState.BLACK, ColorState.ORANGE, ColorState.YELLOW, ColorState.DARK_GREY, ColorState.PINK};
                            int k = rnd.nextInt(colors.length);
                            cell.color = colors[k];
                            field[r][c].ball = true;
                            break one;
                        }
                        pos--;
                    }
                }
            }
        }

        // расставляем маленькие шары
        generateNewMiniBalls(miniBallsCount);

        // начинаем игру
        state = GameState.PLAYING;
    }

    public int getEmptyCellsCount() {
        int counter = 0;
        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColCount(); c++) {
                if (!field[r][c].ball && !field[r][c].miniBall) {
                    counter++;
                }
            }
        }
        return counter;
    }


    public void generateNewMiniBalls(int miniBallsCount) {
        this.miniBallsCount = miniBallsCount;
        int i = 0;
        while (i < Math.min(miniBallsCount, getEmptyCellsCount())) {
            int pos = rnd.nextInt(getRowCount() * getColCount() - i);
            one:
            for (int r = 0; r < getRowCount(); r++) {
                for (int c = 0; c < getColCount(); c++) {
                    if (!field[r][c].miniBall && !field[r][c].ball) {
                        if (pos == 0) {
                            LinesCell cell = field[r][c];
                            ColorState[] colors = {ColorState.GREEN, ColorState.RED, ColorState.BLUE, ColorState.BLACK, ColorState.ORANGE, ColorState.YELLOW, ColorState.DARK_GREY, ColorState.PINK};
                            int k = rnd.nextInt(colors.length);
                            cell.color = colors[k];
                            field[r][c].miniBall = true;
                            i++;

                            break one;
                        }
                        pos--;
                    }
                }
            }
        }


    }

    public boolean check(int startRow, int startCol, int finishRow, int finishCol) {
        boolean[][] possibilityOfMoving = new boolean[getRowCount()][getColCount()];
        possibilityOfMoving[startRow][startCol] = true;
        for (int i = 0; i < 15; i++) {
            for (int r = 0; r < getRowCount(); r++) {
                for (int c = 0; c < getColCount(); c++) {
                    if (!field[r][c].ball) {
                        if ((c + 1 < getColCount() && possibilityOfMoving[r][c + 1]) ||
                                (r + 1 < getRowCount() && possibilityOfMoving[r + 1][c]) ||
                                (c - 1 >= 0 && possibilityOfMoving[r][c - 1]) || (r - 1 >= 0 && possibilityOfMoving[r - 1][c])) {
                            possibilityOfMoving[r][c] = true;
                        }
                    }
                }
            }
        }
        return possibilityOfMoving[finishRow][finishCol];
    }

    public void makeMove(int startRow, int startCol, int finishRow, int finishCol) {
        if (check(startRow, startCol, finishRow, finishCol)) {
            if (field[startRow][startCol].isBall() && !field[finishRow][finishCol].isBall()) {

                field[startRow][startCol].ball = false;
                field[finishRow][finishCol].ball = true;
                field[finishRow][finishCol].miniBall = false;
                LinesCell cell1 = field[startRow][startCol];
                LinesCell cell2 = field[finishRow][finishCol];
                cell2.color = cell1.color;
                cell1.color = null;
                for (int r = 0; r < getRowCount(); r++) {
                    for (int c = 0; c < getColCount(); c++) {
                        if (field[r][c].miniBall) {
                            field[r][c].miniBall = false;
                            field[r][c].ball = true;
                        }
                    }
                }
//                deleteFiveInCol();
//                deleteFiveInDiagonal();
//                deleteFiveInRow();
                deleteFive();
                generateNewMiniBalls(miniBallsCount);
                gameEndingChecking();
            }
        } else if (field[finishRow][finishCol].ball) {
            LinesCell cell1 = field[startRow][startCol];
            LinesCell cell2 = field[finishRow][finishCol];
            cell1.state = CellState.NOT_PRESSED;
            cell2.state = CellState.PRESSED;
        }
    }

    public void deleteFiveInRow() {
        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColCount() - 4; c++) {
                int index = c;
                int countIdenticalInRow = 0;
                while (index < getColCount() && field[r][c].color != null && field[r][c].color == field[r][index].color) {
                    countIdenticalInRow++;
                    index++;
                }
                if (countIdenticalInRow >= 5) {
                    for (int i = c; i < c + countIdenticalInRow; i++) {
                        if (i < getColCount() && i >= 0) {
                            LinesCell cell = field[r][i];
                            field[r][i].ball = false;
                            cell.color = null;
                        }
                    }
                    points += countIdenticalInRow;
                }
            }
        }
    }

    public void deleteFiveInCol() {
        for (int r = 0; r < getRowCount() - 4; r++) {
            for (int c = 0; c < getColCount(); c++) {
                int index = r;
                int countIdenticalInCol = 0;
                while (index < getRowCount() && field[r][c].color != null && field[r][c].color == field[index][c].color) {
                    countIdenticalInCol++;
                    index++;
                }
                if (countIdenticalInCol >= 5) {
                    for (int i = 0; i < countIdenticalInCol; i++) {
                        if (i + r < getRowCount() && i + r >= 0) {
                            LinesCell cell = field[i + r][c];
                            field[i + r][c].ball = false;
                            cell.color = null;
                        }
                    }
                    points += countIdenticalInCol;
                }
            }
        }
    }

    public void deleteFiveInDiagonal() {

        for (int r = 0; r < getRowCount() - 4; r++) {
            for (int c = 0; c < getColCount() - 4; c++) {
                int rowIndex = r;
                int colIndex = c;
                int countIdenticalInDiagonal1 = 0;
                while (field[r][c].color != null && colIndex < getColCount() && rowIndex < getRowCount() &&
                        field[r][c].color == field[rowIndex][colIndex].color) {
                    countIdenticalInDiagonal1++;
                    rowIndex++;
                    colIndex++;
                }

                if (countIdenticalInDiagonal1 >= 5) {
                    for (int i = 0; i < countIdenticalInDiagonal1; i++) {
                        if (r + i < getRowCount() && c + i < getColCount()) {
                            LinesCell cell = field[r + i][c + i];
                            field[r + i][c + i].ball = false;
                            cell.color = null;
                        }
                    }
                    points += countIdenticalInDiagonal1;
                }
            }
        }


        for (int r = 0; r < getRowCount() - 4; r++) {
            for (int c = 3; c < getColCount(); c++) {
                int rowIndex = r;
                int colIndex = c;
                int countIdenticalInDiagonal2 = 0;
                while (field[r][c].color != null && colIndex >= 0 && rowIndex < getRowCount() &&
                        field[r][c].color == field[rowIndex][colIndex].color) {
                    countIdenticalInDiagonal2++;
                    rowIndex++;
                    colIndex--;
                }

                if (countIdenticalInDiagonal2 >= 5) {
                    for (int i = 0; i < countIdenticalInDiagonal2; i++) {
                        if (r + i < getRowCount() && c - i >= 0) {
                            LinesCell cell = field[r + i][c - i];
                            field[r + i][c - i].ball = false;
                            cell.color = null;
                        }
                    }
                    points += countIdenticalInDiagonal2;
                }
            }
        }
    }

    public void deleteFive() {
        ArrayList<Integer> coordinatesOfDeleteBalls = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 1}, {1, 0}, {1, -1}};
        boolean[][] didPointsScore = new boolean[getRowCount()][getColCount()];
        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColCount(); c++) {
                for (int i = 0; i < directions.length; i++) {
                    int dr = directions[i][0];
                    int dc = directions[i][1];
                    int k = 1;
                    if ((r + dr) < getRowCount() && (c + dc) < getColCount() && (c + dc) >= 0) {
                        while (field[r][c].color == field[r + dr * k][c + dc * k].color && field[r][c].color != null) {
                            k++;
                            if ((r + dr * k) >= getRowCount() || (c + dc * k) >= getColCount() || (c + dc * k) < 0) {
                                break;
                            }
                        }
                        if (k >= 5) {
                            for (int j = 0; j < k; j++) {
                                coordinatesOfDeleteBalls.add(r + dr * j);
                                coordinatesOfDeleteBalls.add(c + dc * j);
                                if (!didPointsScore[r + dr * j][c + dc * j]) {
                                    points++;
                                }
                                didPointsScore[r + dr * j][c + dc * j] = true;
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < coordinatesOfDeleteBalls.size() - 1; i = i + 2) {
            field[coordinatesOfDeleteBalls.get(i)][coordinatesOfDeleteBalls.get(i + 1)].ball = false;
            field[coordinatesOfDeleteBalls.get(i)][coordinatesOfDeleteBalls.get(i + 1)].color = null;
        }
    }

    public void gameEndingChecking() {
        if (getEmptyCellsCount() == 0) {
            state = GameState.FAIL;
        }
    }

    public void сlick(int row, int col) {
        LinesCell cell = field[row][col];
        cell.state = CellState.PRESSED;
        int countOfPressedCells = 0;
        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColCount(); c++) {
                LinesCell cell1 = field[r][c];
                if (cell1.state == CellState.PRESSED) {
                    countOfPressedCells++;
                }
            }
        }
        if (countOfPressedCells == 2) {
            cell.state = CellState.NOT_PRESSED;
            for (int r = 0; r < getRowCount(); r++) {
                for (int c = 0; c < getColCount(); c++) {
                    LinesCell cell1 = field[r][c];
                    if (cell1.state == CellState.PRESSED) {
                        cell1.state = CellState.NOT_PRESSED;
                        makeMove(r, c, row, col);
                    }
                }
            }
        } else if (!field[row][col].miniBall && !field[row][col].ball) {
            cell.state = CellState.NOT_PRESSED;
        }
    }

    public GameState getState() {
        return state;
    }

    public int getRowCount() {
        return field == null ? 0 : field.length;
    }

    public int getColCount() {
        return field == null ? 0 : field[0].length;
    }

    public LinesCell getCell(int row, int col) {
        return (row < 0 || row >= getRowCount() || col < 0 || col >= getColCount()) ? null : field[row][col];
    }

    public int getPoints() {
        return points;
    }
}


