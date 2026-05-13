package dev.bsprout.btweaks.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoadingWindow {
    private static JFrame frame;
    private static JProgressBar bar;
    private static Thread progressThread;

    public static void show() {
//        System.setProperty("java.awt.headless", "false");
//        SwingUtilities.invokeLater(() -> {
//            frame = new JFrame("BTweaks: Loading Minecraft");
//            frame.setUndecorated(true);
//            frame.setSize(600, 180);
//            frame.setLocationRelativeTo(null);
//            frame.setBackground(new Color(26, 26, 26));
//            frame.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
//
//            JPanel panel = new JPanel();
//            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//            panel.setBackground(new Color(26, 26, 26));
//            panel.setBorder(new EmptyBorder(20, 30, 20, 30));
//
//            JLabel title = new JLabel("BTweaks");
//            title.setForeground(Color.WHITE);
//            title.setFont(new Font("Arial", Font.BOLD, 25));
//            title.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//            JLabel subtitle = new JLabel("Loading...");
//            subtitle.setForeground(new Color(180, 180, 180));
//            subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
//            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//            bar = new JProgressBar(0, 150);
//            bar.setForeground(new Color(25, 110, 232));
//            bar.setBackground(new Color(40, 40, 40));
//            bar.setBorderPainted(false);
//            bar.setPreferredSize(new Dimension(510, 9));
//            bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9));
//            bar.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//            panel.add(title);
//            panel.add(Box.createVerticalStrut(9));
//            panel.add(subtitle);
//            panel.add(Box.createVerticalStrut(24));
//            panel.add(bar);
//
//            frame.add(panel);
//            frame.setVisible(true);
//
//            // animate progress bar
//            progressThread = new Thread(() -> {
//                int progress = 0;
//                while (progress < 90 && !Thread.currentThread().isInterrupted()) {
//                    try {
//                        Thread.sleep(30);
//                        progress = Math.min(90, progress + 1);
//                        int finalProgress = progress;
//                        SwingUtilities.invokeLater(() -> bar.setValue(finalProgress));
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                    }
//                }
//            });
//            progressThread.setDaemon(true);
//            progressThread.start();
//        });
    }

    public static void close() {
//        SwingUtilities.invokeLater(() -> {
//            if (progressThread != null) progressThread.interrupt();
//            if (bar != null) {
//                bar.setValue(100);
//            }
//            // small delay so user sees 100%
//            Timer timer = new Timer(300, e -> {
//                if (frame != null) {
//                    frame.dispose();
//                    frame = null;
//                }
//            });
//            timer.setRepeats(false);
//            timer.start();
//        });
    }
}