package com.weizilla.pomodoro;

import com.weizilla.pomodoro.cycle.Cycle;
import com.weizilla.pomodoro.cycle.CycleChangeListener;
import com.weizilla.pomodoro.cycle.CycleTickListener;
import com.weizilla.pomodoro.cycle.CycleWorkflow;
import com.weizilla.pomodoro.timer.CycleTimer;
import com.weizilla.pomodoro.timer.TimerTickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PomodoroController implements TimerTickListener
{
    protected final List<CycleTickListener> cycleTickListeners = new ArrayList<>();
    protected final List<CycleChangeListener> cycleChangeListeners = new ArrayList<>();
    private final CycleTimer timer;
    private final CycleWorkflow workflow;
    private final TimeUnit timeUnit;
    private int remainingTicks;
    private Cycle currentCycle;
    private boolean started;
    private boolean paused;

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

    public void start(Cycle.Type type)
    {
        if (started)
        {
            stopWorkflow();
        }

        startWorkflow(type);
    }

    public void pause()
    {
        if (started)
        {
            if (paused)
            {
                unpauseWorkflow();
            }
            else
            {
                pauseWorkflow();
            }
        }
    }

    public void stop()
    {
        stopWorkflow();
    }

    private void startWorkflow(Cycle.Type type)
    {
        currentCycle = workflow.createCycle(type);
        remainingTicks = currentCycle.getNumTicks();
        started = true;
        paused = false;
        timer.startCycle(timeUnit);
    }

    private void pauseWorkflow()
    {
        paused = true;
        timer.stopCycle();
    }

    private void unpauseWorkflow()
    {
        paused = false;
        timer.startCycle(timeUnit);
    }

    private void stopWorkflow()
    {
        currentCycle = null;
        remainingTicks = 0;
        started = false;
        paused = false;
        timer.stopCycle();
    }

    @Override
    public void tick()
    {
        remainingTicks--;
        if (remainingTicks == 0)
        {
            currentCycle = workflow.getNextCycle(currentCycle);
            remainingTicks = currentCycle.getNumTicks();
            notifyCycleChangeListeners();
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

    public int getRemainingTicks()
    {
        return remainingTicks;
    }

    public void addCycleTickListener(CycleTickListener listener)
    {
        cycleTickListeners.add(listener);
    }

    public void addCycleChangeListener(CycleChangeListener listener)
    {
        cycleChangeListeners.add(listener);
    }

    private void notifyCycleTickListeners()
    {
        for (CycleTickListener listener : cycleTickListeners)
        {
            listener.tick(remainingTicks);
        }
    }

    private void notifyCycleChangeListeners()
    {
        for (CycleChangeListener listener : cycleChangeListeners)
        {
            listener.cycleChange(remainingTicks);
        }
    }
}
