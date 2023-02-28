package ru.vsu.cs.course1.game;

import java.util.Locale;

/**
 * Класс с методом main
 */
public class Program {

    /**
     * Основная функция программы
     */
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        //SwingUtils.setLookAndFeelByName("Windows");
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        //SwingUtils.setDefaultFont("Microsoft Sans Serif", 18);

        java.awt.EventQueue.invokeLater(() -> new MainForm().setVisible(true));
    }
}
