package com.weizilla.pomodoro.tray;

import com.weizilla.pomodoro.PomodoroController;
import com.weizilla.pomodoro.cycle.Cycle;
import com.weizilla.pomodoro.cycle.CycleChangeListener;
import com.weizilla.pomodoro.cycle.CycleTickListener;
import com.weizilla.pomodoro.cycle.CycleWorkflow;
import com.weizilla.pomodoro.timer.DefaultCycleTimer;

import javax.swing.*;
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
    private static final Map<Cycle.Type, CycleSettings> CYCLE_SETTINGS =
        new EnumMap<Cycle.Type, CycleSettings>(Cycle.Type.class)
    {{
        put(Cycle.Type.WORK, new CycleSettings(Color.BLACK, "Back to work!"));
        put(Cycle.Type.BREAK, new CycleSettings(Color.BLUE, "Break time!"));
        put(Cycle.Type.LONG_BREAK, new CycleSettings(Color.MAGENTA, "Long break time!"));
    }};
    private final PomodoroController controller;
    private final BufferedImage image;
    private final TrayIcon trayIcon;
    private final JFrame frame;
    private final JLabel frameMessage;
    private MenuItem cycleName;

    private TrayApp(PomodoroController controller)
    {
        this.controller = controller;
        image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        trayIcon = new TrayIcon(image);

        frame = new JFrame("Pomodoro");
        frame.setSize(50, 100);
        frame.setAlwaysOnTop(true);
        frameMessage = new JLabel();
        frame.getContentPane().add(frameMessage, BorderLayout.CENTER);

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

        MenuItem pause = new MenuItem("Pause");
        pause.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                pause();
            }
        });
        menu.add(pause);

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
        controller.start(Cycle.Type.WORK);
    }

    private void pause()
    {
        controller.pause();
    }

    private void stop()
    {
        controller.stop();
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
        createMessageDialog();
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
            CycleSettings settings = CYCLE_SETTINGS.get(currentCycle.getType());
            if (settings != null)
            {
                result = settings.textColor;
            }
        }
        return result;
    }

    private void createMessageDialog()
    {
        Cycle currentCycle = controller.getCurrentCycle();
        if (currentCycle != null)
        {
            CycleSettings settings = CYCLE_SETTINGS.get(currentCycle.getType());
            if (settings != null)
            {
                frameMessage.setText(settings.dialogMessage);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.toFront();
            }
        }
    }

    public static void main(String[] args)
    {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        DefaultCycleTimer timer = new DefaultCycleTimer(executorService);
        CycleWorkflow workflow = new CycleWorkflow(3, 4, 5);
        PomodoroController controller = PomodoroController.createController(timer, workflow, TimeUnit.SECONDS);
        startApplication(controller);

    }

    private static class CycleSettings
    {
        private Color textColor;
        private String dialogMessage;

        private CycleSettings(Color textColor, String dialogMessage)
        {
            this.textColor = textColor;
            this.dialogMessage = dialogMessage;
        }
    }

}
