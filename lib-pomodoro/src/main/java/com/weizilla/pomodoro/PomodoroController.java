package com.weizilla.pomodoro;

import com.weizilla.pomodoro.timer.CycleTimer;
import com.weizilla.pomodoro.timer.TickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PomodoroController implements TickListener
{
    private final CycleTimer cycleTimer;
    protected final List<TickListener> listeners = new ArrayList<>();

    private PomodoroController(CycleTimer cycleTimer)
    {
        this.cycleTimer = cycleTimer;
    }

    public static PomodoroController createController(CycleTimer cycleTimer)
    {
        PomodoroController controller = new PomodoroController(cycleTimer);
        cycleTimer.addTickListener(controller);
        return controller;
    }

    public void startCycle(Cycle cycle)
    {
        TimeUnit timeUnit = cycle.getTimeUnit();
        cycleTimer.startCycle(timeUnit);
    }

    public void stopCycle()
    {
        cycleTimer.stopCycle();
    }

    public void addTickListener(TickListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void tick()
    {
        for (TickListener listener : listeners)
        {
            listener.tick();
        }
    }
}
