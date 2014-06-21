package com.weizilla.pomodoro;

import com.weizilla.pomodoro.cycle.Cycle;
import com.weizilla.pomodoro.cycle.CycleEndListener;
import com.weizilla.pomodoro.cycle.CycleTickListener;
import com.weizilla.pomodoro.cycle.CycleWorkflow;
import com.weizilla.pomodoro.timer.CycleTimer;
import com.weizilla.pomodoro.timer.TickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PomodoroController implements TickListener
{
    protected final List<CycleTickListener> cycleTickListeners = new ArrayList<>();
    protected final List<CycleEndListener> cycleEndListeners = new ArrayList<>();
    private final CycleTimer timer;
    private final CycleWorkflow workflow;
    private final TimeUnit timeUnit;
    private int ticksRemaining;
    private Cycle currentCycle;

    private PomodoroController(CycleTimer timer, CycleWorkflow workflow, TimeUnit timeUnit)
    {
        this.timer = timer;
        this.workflow = workflow;
        this.timeUnit = timeUnit;
    }

    public static PomodoroController createController(CycleTimer timer, CycleWorkflow workflow, TimeUnit timeUnit)
    {
        PomodoroController controller = new PomodoroController(timer, workflow, timeUnit);
        timer.addTickListener(controller);
        return controller;
    }

    public void startCycle(Cycle.Type type)
    {
        currentCycle = workflow.createCycle(type);
        ticksRemaining = currentCycle.getNumTicks();
        timer.startCycle(timeUnit);
    }

    public void stopCycle()
    {
        timer.stopCycle();
    }

    @Override
    public void tick()
    {
        ticksRemaining--;
        if (ticksRemaining == 0)
        {
            notifyCycleEndListeners();
            currentCycle = workflow.getNextCycle(currentCycle);
            ticksRemaining = currentCycle.getNumTicks();
        }
        else
        {
            notifyCycleTickListeners();
        }
    }

    public Cycle getCurrentCycle()
    {
        return currentCycle;
    }

    public int getTicksRemaining()
    {
        return ticksRemaining;
    }

    public void addCycleTickListener(CycleTickListener listener)
    {
        cycleTickListeners.add(listener);
    }

    public void addCycleEndListener(CycleEndListener listener)
    {
        cycleEndListeners.add(listener);
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
