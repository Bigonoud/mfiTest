package com.mfi.test;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static javax.swing.SwingConstants.HORIZONTAL;

/**
 * A window that contains the slider.
 */
public class MainWindow extends JFrame {
    /**
     * The minimum expected random value.
     */
    private static final int MIN_RANDOM_VALUE = 0;
    /**
     * The maximum expected random value.
     */
    private static final int MAX_RANDOM_VALUE = 4;
    /**
     * Repeat request time (in seconds).
     */
    private static final int REPEAT_DELAY = 10;
    /**
     * A slider to display the random nutriscore.
     */
    private JSlider nutriscore;

    /**
     * Constructor.
     */
    public MainWindow() {
        setTitle("MFI Nutritest");
        setSize(450, 160);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new GridLayout(1, 1));

        nutriscore = new JSlider(HORIZONTAL, MIN_RANDOM_VALUE, MAX_RANDOM_VALUE, MIN_RANDOM_VALUE);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("A"));
        labelTable.put(1, new JLabel("B"));
        labelTable.put(2, new JLabel("C"));
        labelTable.put(3, new JLabel("D"));
        labelTable.put(4, new JLabel("E"));
        nutriscore.setLabelTable(labelTable);
        nutriscore.setPaintLabels(true);

        getContentPane().add(nutriscore);

        setVisible(true);
        launchTask();
    }

    /**
     * Method to launch the background task. Request a random number to update the nutriscore.
     */
    private void launchTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int newValue = Random.getRandomInt();

                if (newValue >= MIN_RANDOM_VALUE && newValue <= MAX_RANDOM_VALUE) {
                    nutriscore.setValue(newValue);
                }
            }
        };

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(task, 0, REPEAT_DELAY, TimeUnit.SECONDS);
    }
}
