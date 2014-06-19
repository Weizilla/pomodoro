package com.weizilla.pomodoro;

import com.weizilla.pomodoro.timer.CycleTimer;
import com.weizilla.pomodoro.timer.TickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PomodoroController implements TickListener
{
    private final CycleTimer cycleTimer;
    protected final List<TickListener> tickListeners = new ArrayList<>();
    protected final List<CycleEndListener> cycleEndListeners = new ArrayList<>();
    private int remainingTicks;

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
        remainingTicks = cycle.getNumTicks();
        cycleTimer.startCycle(timeUnit);
    }

    public void stopCycle()
    {
        cycleTimer.stopCycle();
    }

    public void addTickListener(TickListener listener)
    {
        tickListeners.add(listener);
    }

    public void addCycleEndListener(CycleEndListener listener)
    {
        cycleEndListeners.add(listener);
    }

    @Override
    public void tick()
    {
        notifyTickListeners();
        remainingTicks--;
        if (remainingTicks == 0)
        {
            notifyCycleEndListeners();
            //TODO activiate next cycle
        }
    }

    private void notifyTickListeners()
    {
        for (TickListener listener : tickListeners)
        {
            listener.tick();
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
