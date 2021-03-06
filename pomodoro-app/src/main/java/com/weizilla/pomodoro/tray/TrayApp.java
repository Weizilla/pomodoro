package com.weizilla.pomodoro.tray;

import com.weizilla.pomodoro.PomodoroController;
import com.weizilla.pomodoro.cycle.Cycle;
import com.weizilla.pomodoro.cycle.Cycle.Type;
import com.weizilla.pomodoro.cycle.CycleChangeListener;
import com.weizilla.pomodoro.cycle.CycleTickListener;
import com.weizilla.pomodoro.cycle.CycleWorkflow;
import com.weizilla.pomodoro.timer.CycleTimer;
import com.weizilla.pomodoro.timer.DefaultCycleTimer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrayApp implements CycleTickListener, CycleChangeListener, CycleEndDialog.ResultsListener
{
    private static final Map<Cycle.Type, CycleSettings> CYCLE_SETTINGS = new EnumMap<>(Type.class);
    static
    {
        CYCLE_SETTINGS.put(Type.LONG_BREAK, new CycleSettings(Color.MAGENTA, "Long break time!"));
        CYCLE_SETTINGS.put(Type.BREAK, new CycleSettings(Color.BLUE, "Break time!"));
        CYCLE_SETTINGS.put(Type.WORK, new CycleSettings(Color.WHITE, "Back to work!"));
    }

    private final PomodoroController controller;
    private final BufferedImage image;
    private final TrayIcon trayIcon;
    private CycleEndDialog dialog;
    private MenuItem cycleName;

    private TrayApp(PomodoroController controller)
    {
        this.controller = controller;
        image = new BufferedImage(20, 16, BufferedImage.TYPE_INT_ARGB);
        trayIcon = new TrayIcon(image);

        createTray();
        drawNumber(0);
    }

    private void init()
    {
        dialog = new CycleEndDialog(this);
    }

    public static void startApplication(PomodoroController controller)
    {
        TrayApp trayApp = new TrayApp(controller);
        trayApp.init();
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
        start.addActionListener(e -> start());
        menu.add(start);

        MenuItem pause = new MenuItem("Pause");
        pause.addActionListener(e -> pause());
        menu.add(pause);

        MenuItem stop = new MenuItem("Stop");
        stop.addActionListener(e -> stop());
        menu.add(stop);

        MenuItem quit = new MenuItem("Quit");
        quit.addActionListener(e -> System.exit(0));
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
    public void cycleChange(Cycle previous, Cycle next)
    {
        drawNumber(next.getNumTicks());
        cycleName.setLabel(next.getType().name());
        controller.pause();
        dialog.showMessage(MessageFormat.format("{0} cycle ended. Continue with {1}?", previous.getType(), next.getType()));
    }

    @Override
    public void dialogResult(CycleEndDialog.Result result)
    {
        if (result == CycleEndDialog.Result.YES)
        {
            controller.pause();
        }
        dialog.setVisible(false);
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
        Color result = Color.WHITE;
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

    public static void main(String[] args)
    {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        CycleTimer timer = new DefaultCycleTimer(executorService);
        CycleWorkflow workflow = new CycleWorkflow(25, 5, 15);
        PomodoroController controller = PomodoroController.createController(timer, workflow, TimeUnit.MINUTES);
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
