package com.weizilla.pomodoro.tray;

import com.weizilla.pomodoro.PomodoroController;
import com.weizilla.pomodoro.cycle.Cycle;
import com.weizilla.pomodoro.cycle.CycleChangeListener;
import com.weizilla.pomodoro.cycle.CycleTickListener;
import com.weizilla.pomodoro.cycle.CycleWorkflow;
import com.weizilla.pomodoro.timer.DefaultCycleTimer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrayApp implements CycleTickListener, CycleChangeListener
{
    private static final Map<Cycle.Type, Color> COLORS = new EnumMap<Cycle.Type, Color>(Cycle.Type.class)
    {{
        put(Cycle.Type.WORK, Color.BLACK);
        put(Cycle.Type.BREAK, Color.BLUE);
        put(Cycle.Type.LONG_BREAK, Color.MAGENTA);
    }};
    private final PomodoroController controller;
    private final BufferedImage image;
    private final TrayIcon trayIcon;
    private MenuItem cycleName;

    private TrayApp(PomodoroController controller)
    {
        this.controller = controller;
        image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        trayIcon = new TrayIcon(image);

        createTray();
        drawNumber(0);
    }

    public static void startApplication(PomodoroController controller)
    {
        TrayApp trayApp = new TrayApp(controller);
        controller.addCycleTickListener(trayApp);
        controller.addCycleChangeListener(trayApp);
    }

    private void createTray()
    {
        SystemTray systemTray = SystemTray.getSystemTray();
        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(createMenu());
        try
        {
            systemTray.add(trayIcon);
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }
    }

    private PopupMenu createMenu()
    {
        PopupMenu menu = new PopupMenu();

        cycleName = new MenuItem("Pomodoro");
        menu.add(cycleName);

        MenuItem start = new MenuItem("Start");
        start.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                start();
            }
        });
        menu.add(start);

        MenuItem stop = new MenuItem("Stop");
        stop.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                stop();
            }
        });
        menu.add(stop);

        MenuItem quit = new MenuItem("Quit");
        quit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        menu.add(quit);


        return menu;
    }

    private void start()
    {
        controller.startCycle(Cycle.Type.WORK);
    }

    private void stop()
    {
        controller.stopCycle();
    }

    @Override
    public void tick(int remainingTicks)
    {
        drawNumber(remainingTicks);
        cycleName.setLabel(controller.getCurrentCycle().getType().name());
    }

    @Override
    public void cycleChange(int remainingTicks)
    {
        drawNumber(remainingTicks);
        cycleName.setLabel(controller.getCurrentCycle().getType().name());
    }

    private void drawNumber(int num)
    {
        Graphics2D g2d = image.createGraphics();
        g2d.setBackground(new Color(0, 0, 0, 0));
        g2d.clearRect(0, 0, 100, 100);
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setPaint(getColor());
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2d.drawString(String.valueOf(num), 0, 12);
        g2d.dispose();

        trayIcon.setImage(image);
    }

    private Color getColor()
    {
        Color result = Color.BLACK;
        Cycle currentCycle = controller.getCurrentCycle();
        if (currentCycle != null)
        {
            Color color = COLORS.get(currentCycle.getType());
            if (color != null)
            {
                result = color;
            }
        }
        return result;
    }

    public static void main(String[] args)
    {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        DefaultCycleTimer timer = new DefaultCycleTimer(executorService);
        CycleWorkflow workflow = new CycleWorkflow(3, 4, 5);
        PomodoroController controller = PomodoroController.createController(timer, workflow, TimeUnit.SECONDS);
        startApplication(controller);

    }
}
