package com.weizilla.pomodoro.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DefaultCycleTimer implements CycleTimer, Runnable
{
    protected final List<TimerTickListener> listeners = new ArrayList<>();
    private final ScheduledExecutorService service;
    protected ScheduledFuture<?> future;

    public DefaultCycleTimer(ScheduledExecutorService service)
    {
        this.service = service;
    }

    @Override
    public void startCycle(TimeUnit timeUnit)
    {
        future = service.scheduleAtFixedRate(this, 0, 1, timeUnit);
    }

    @Override
    public void run()
    {
        notifyListeners();
    }

    private void notifyListeners()
    {
        for (TimerTickListener listener : listeners)
        {
            listener.tick();
        }
    }

    @Override
    public void addTickListener(TimerTickListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void stopCycle()
    {
        if (future != null)
        {
            future.cancel(false);
            future = null;
        }
    }
}
