package com.weizilla.pomodoro;

import com.weizilla.pomodoro.timer.CycleTimer;
import com.weizilla.pomodoro.timer.TickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PomodoroController implements TickListener
{
    private final CycleTimer cycleTimer;
    protected final List<CycleTickListener> cycleTickListeners = new ArrayList<>();
    protected final List<CycleEndListener> cycleEndListeners = new ArrayList<>();
    private int ticksRemaining;

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
        ticksRemaining = cycle.getNumTicks();
        cycleTimer.startCycle(timeUnit);
    }

    public void stopCycle()
    {
        cycleTimer.stopCycle();
    }

    public void addCycleTickListener(CycleTickListener listener)
    {
        cycleTickListeners.add(listener);
    }

    public void addCycleEndListener(CycleEndListener listener)
    {
        cycleEndListeners.add(listener);
    }

    @Override
    public void tick()
    {
        ticksRemaining--;
        notifyCycleTickListeners();
        if (ticksRemaining == 0)
        {
            notifyCycleEndListeners();
            //TODO activiate next cycle
        }
    }

    private void notifyCycleTickListeners()
    {
        for (CycleTickListener listener : cycleTickListeners)
        {
            listener.tick(ticksRemaining);
        }
    }

    private void notifyCycleEndListeners()
    {
        for (CycleEndListener listener : cycleEndListeners)
        {
            listener.cycleEnd();
        }
    }
}
