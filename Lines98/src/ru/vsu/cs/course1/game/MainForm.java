package ru.vsu.cs.course1.game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import ru.vsu.cs.util.DrawUtils;
import ru.vsu.cs.util.JTableUtils;
import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

public class MainForm extends JFrame {
    private JPanel panelMain;
    private JTable tableGameField;
    private JLabel labelStatus;
    private JPanel panelTop;
    private JPanel panelTime;
    private JLabel labelTime;
    private JPanel panelPoints;
    private JLabel labelPoints;
    private JButton buttonNewGame;

    private static final int DEFAULT_COL_COUNT = 9;
    private static final int DEFAULT_ROW_COUNT = 9;
    private static final int DEFAULT_BALL_COUNT = 5;
    private static final int DEFAULT_MINIBALL_COUNT = 3;

    private static final int DEFAULT_GAP = 10;
    private static final int DEFAULT_CELL_SIZE = 30;
    // private static final int DEFAULT_NEW_GAME_BUTTON_SIZE = 50;

    private static final Color[] BALLS_COLORS = {
            Color.GREEN,
            Color.RED,
            Color.BLUE,
            Color.BLACK,
            Color.ORANGE,
            Color.YELLOW,
            Color.DARK_GRAY,
            Color.PINK
    };

    private Lines98Params params = new Lines98Params(DEFAULT_ROW_COUNT, DEFAULT_COL_COUNT, DEFAULT_BALL_COUNT, DEFAULT_MINIBALL_COUNT);
    private Lines98Game game = new Lines98Game();

    private static final Lines98Game.LinesCell
            NULL_CELL = new Lines98Game.LinesCell(Lines98Game.CellState.NOT_PRESSED, null);

    /* Демонстрация работы с таймером (удалить, если не нужно в вашей игре) */
    private int time = 0;
    private Timer timer = new Timer(1000, e -> {
        time++;
        this.labelTime.setText("" + time);
    });

    public MainForm() {
        this.setTitle("Lines98");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        SwingUtils.setShowMessageDefaultErrorHandler();

        labelTime.setFont(new Font("Comic Sans MS", Font.PLAIN, labelTime.getFont().getSize()));
        labelTime.setForeground(new Color(0, 0, 128));
        panelTime.setBackground(Color.LIGHT_GRAY);
        labelPoints.setFont(new Font("Comic Sans MS", Font.PLAIN, labelPoints.getFont().getSize()));
        labelPoints.setForeground(new Color(128, 0, 0));
        panelPoints.setBackground(Color.LIGHT_GRAY);

        tableGameField.setRowHeight(DEFAULT_CELL_SIZE);
        JTableUtils.initJTableForArray(tableGameField, DEFAULT_CELL_SIZE, false, false, false, false);
        tableGameField.setIntercellSpacing(new Dimension(0, 0));
        tableGameField.setEnabled(false);

        tableGameField.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final class DrawComponent extends Component {
                private int row = 0, column = 0;

                @Override
                public void paint(Graphics gr) {
                    Graphics2D g2d = (Graphics2D) gr;
                    int width = getWidth() - 2;
                    int height = getHeight() - 2;
                    paintCell(row, column, g2d, width, height);
                }
            }

            DrawComponent comp = new DrawComponent();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                comp.row = row;
                comp.column = column;
                return comp;
            }
        });

        newGame();

        updateWindowSize();
        updateView();

        buttonNewGame.addActionListener(e -> {
            newGame();
        });

        tableGameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = tableGameField.rowAtPoint(e.getPoint());
                int col = tableGameField.columnAtPoint(e.getPoint());
                if (SwingUtilities.isLeftMouseButton(e)) {
                    game.сlick(row, col);
                }
                updateView();
            }
        });
    }

    private void updateWindowSize() {
        int menuSize = this.getJMenuBar() != null ? this.getJMenuBar().getHeight() : 0;
        SwingUtils.setFixedSize(
                this,
                tableGameField.getWidth() + 2 * DEFAULT_GAP + 60,
                tableGameField.getHeight() + panelMain.getY() + panelTop.getHeight() + labelStatus.getHeight() +
                        menuSize + 1 * DEFAULT_GAP + 2 * DEFAULT_GAP + 60
        );
        this.setMaximumSize(null);
        this.setMinimumSize(null);
    }

    private void updateView() {
        labelPoints.setText("" + (game.getPoints()));
        labelTime.setText("" + time);
        tableGameField.repaint();
        if (game.getState() == Lines98Game.GameState.PLAYING) {
            labelStatus.setForeground(Color.BLACK);
            labelStatus.setText("Игра идет");
        } else {
            timer.stop();
            labelStatus.setText("");
            if (game.getState() == Lines98Game.GameState.FAIL) {
                labelStatus.setForeground(Color.RED);
                labelStatus.setText("Поражение :-(");
            }
        }
    }


    private Font font = null;

    private Font getFont(int size) {
        if (font == null || font.getSize() != size) {
            font = new Font("Comic Sans MS", Font.BOLD, size);
        }
        return font;
    }

    private void paintCell(int row, int column, Graphics2D g2d, int cellWidth, int cellHeight) {
        Lines98Game.LinesCell cell = game.getCell(row, column);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cell == null) {
            cell = NULL_CELL;
        }
        int size = Math.min(cellWidth, cellHeight);

        Color backColor = Color.LIGHT_GRAY;


        Color color = DrawUtils.getContrastColor(backColor);
        char ch = ' ';

        Lines98Game.ColorState[] colors = {Lines98Game.ColorState.GREEN, Lines98Game.ColorState.RED, Lines98Game.ColorState.BLUE, Lines98Game.ColorState.BLACK, Lines98Game.ColorState.ORANGE, Lines98Game.ColorState.YELLOW, Lines98Game.ColorState.DARK_GREY, Lines98Game.ColorState.PINK};
        Color[] colors1 = {Color.GREEN, Color.RED, Color.BLUE, Color.BLACK, Color.ORANGE, Color.YELLOW, Color.DARK_GRAY, Color.PINK};

        if (cell.getState() == Lines98Game.CellState.PRESSED) {
            backColor = Color.MAGENTA;
            g2d.setColor(backColor);
            g2d.fillRect(0, 0, size, size);
            ;
            DrawUtils.drawStringInCenter(g2d, font, "" + ch, 0, 0, cellWidth, (int) Math.round(cellHeight * 0.95));
        }


// большие шары
        if (cell.isBall()) {
            for (int i = 0; i < colors.length; i++) {
                if (cell.getColor() == colors[i]) {
                    int bound = (int) Math.round(size * 0.1);

                    g2d.setColor(colors1[i]);
                    g2d.fillOval(bound, bound, size - 2 * bound, size - 2 * bound);
                    g2d.setColor(colors1[i]);
                    g2d.drawOval(bound, bound, size - 2 * bound, size - 2 * bound);


                    g2d.setFont(getFont(size - 2 * bound));
                    g2d.setColor(DrawUtils.getContrastColor(color));
                }
            }
        }


//маленькие шары
        if (cell.isMiniBall()) {
            for (int i = 0; i < colors.length; i++) {
                if (cell.getColor() == colors[i]) {
                    int bound = (int) Math.round(size * 0.3);

                    g2d.setColor(colors1[i]);
                    g2d.fillOval(bound, bound, size - 2 * bound, size - 2 * bound);
                    g2d.setColor(colors1[i]);
                    g2d.drawOval(bound, bound, size - 2 * bound, size - 2 * bound);


                    g2d.setFont(getFont(size - 2 * bound));
                    g2d.setColor(DrawUtils.getContrastColor(color));
                }
            }
        }
        g2d.setColor(color);
        int bound = (int) Math.round(size * 0.1);
        Font font = getFont(size - 2 * bound);
        DrawUtils.drawStringInCenter(g2d, font, "" + ch, 0, 0, cellWidth, (int) Math.round(cellHeight * 0.95));

    }

//    private void paintCell(Graphics2D g2d, int cellWidth, int cellHeight) {
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        int size = Math.min(cellWidth, cellHeight);
//
//        Color backColor = Color.LIGHT_GRAY;
//
//        for (int r = 0; r < DEFAULT_ROW_COUNT; r++) {
//            for (int c = 0; c < DEFAULT_COL_COUNT; c++) {
//                Lines98Game.LinesCell cell = game.getCell(r, c);
//                if (cell == null) {
//                    cell = NULL_CELL;
//                }
//                if (cell.getColor() == Lines98Game.ColorState.RED) {
//                    backColor = Color.RED;
//                }
//                if (cell.getColor() == Lines98Game.ColorState.GREEN) {
//                    backColor = Color.GREEN;
//                }
//                if (cell.getColor() == Lines98Game.ColorState.DARK_GREY) {
//                    backColor = Color.DARK_GRAY;
//                }
//                if (cell.getColor() == Lines98Game.ColorState.YELLOW) {
//                    backColor = Color.YELLOW;
//                }
//                if (cell.getColor() == Lines98Game.ColorState.PINK) {
//                    backColor = Color.PINK;
//                }
//                if (cell.getColor() == Lines98Game.ColorState.BLUE) {
//                    backColor = Color.BLUE;
//                }
//                if (cell.getColor() == Lines98Game.ColorState.ORANGE) {
//                    backColor = Color.ORANGE;
//                }
//                if (cell.getColor() == Lines98Game.ColorState.BLACK) {
//                    backColor = Color.BLACK;
//                }
//                DrawUtils.getContrastColor(backColor);
//            }
//        }
//    }

    private void newGame() {
        game.newGame(params.getRowCount(), params.getColCount(), params.getBallsCount(), params.getMiniBallsCount());
        JTableUtils.resizeJTable(tableGameField,
                game.getRowCount(), game.getColCount(),
                tableGameField.getRowHeight(), tableGameField.getRowHeight()
        );
        time = 0;
        timer.start();
        updateView();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, 10));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelMain.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableGameField = new JTable();
        scrollPane1.setViewportView(tableGameField);
        labelStatus = new JLabel();
        labelStatus.setText("Label");
        panelMain.add(labelStatus, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelTop = new JPanel();
        panelTop.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panelTop, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelTime = new JPanel();
        panelTime.setLayout(new GridLayoutManager(1, 1, new Insets(0, 5, 0, 5), -1, -1));
        panelTop.add(panelTime, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(100, -1), null, 0, false));
        panelTime.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        labelTime = new JLabel();
        Font labelTimeFont = this.$$$getFont$$$(null, -1, 36, labelTime.getFont());
        if (labelTimeFont != null) labelTime.setFont(labelTimeFont);
        labelTime.setHorizontalTextPosition(4);
        labelTime.setText("0");
        panelTime.add(labelTime, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelPoints = new JPanel();
        panelPoints.setLayout(new GridLayoutManager(1, 1, new Insets(0, 5, 0, 5), -1, -1));
        panelTop.add(panelPoints, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(100, -1), null, 0, false));
        panelPoints.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        labelPoints = new JLabel();
        Font labelPointsFont = this.$$$getFont$$$(null, -1, 36, labelPoints.getFont());
        if (labelPointsFont != null) labelPoints.setFont(labelPointsFont);
        labelPoints.setHorizontalTextPosition(4);
        labelPoints.setText("0");
        panelPoints.add(labelPoints, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonNewGame = new JButton();
        buttonNewGame.setFocusable(false);
        buttonNewGame.setText("Новая игра");
        panelTop.add(buttonNewGame, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panelTop.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panelTop.add(spacer2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

}
