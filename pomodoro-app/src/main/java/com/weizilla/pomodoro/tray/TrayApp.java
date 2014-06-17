package com.weizilla.pomodoro.tray;

import com.weizilla.pomodoro.PomodoroController;
import com.weizilla.pomodoro.timer.DefaultCycleTimer;
import com.weizilla.pomodoro.timer.TickListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrayApp implements TickListener
{
    private final PomodoroController controller;
    private BufferedImage image;
    private TrayIcon trayIcon;
    private int count;

    public TrayApp(PomodoroController controller)
    {
        this.controller = controller;

        createTray();
        start();
    }

    private void createTray()
    {
        image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        SystemTray systemTray = SystemTray.getSystemTray();
        trayIcon = new TrayIcon(image);
        trayIcon.setImageAutoSize(true);
        try
        {
            systemTray.add(trayIcon);
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }
    }

    private void start()
    {
        count = 0;
        controller.startCycle(TimeUnit.SECONDS, this);
    }

    @Override
    public void tick()
    {
        count++;
        refreshImage();
    }

    private void refreshImage()
    {
        Graphics2D g2d = image.createGraphics();
        g2d.setBackground(new Color(0, 0, 0, 0));
        g2d.clearRect(0, 0, 100, 100);
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setPaint(Color.black);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2d.drawString(String.valueOf(count), 0, 12);
        g2d.dispose();

        trayIcon.setImage(image);
    }

    public static void main(String[] args)
    {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        DefaultCycleTimer timer = new DefaultCycleTimer(executorService);
        PomodoroController controller = new PomodoroController(timer);
        new TrayApp(controller);
    }
}
