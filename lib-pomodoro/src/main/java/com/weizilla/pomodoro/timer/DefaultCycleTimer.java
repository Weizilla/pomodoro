package com.weizilla.pomodoro.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DefaultCycleTimer implements CycleTimer, Runnable
{
    protected final List<TickListener> listeners = new ArrayList<>();
    private final ScheduledExecutorService service;

    public DefaultCycleTimer(ScheduledExecutorService service)
    {
        this.service = service;
    }

    @Override
    public void startCycle(TimeUnit timeUnit)
    {
        service.scheduleAtFixedRate(this, 0, 1, timeUnit);
    }

    @Override
    public void run()
    {
        notifyListeners();
    }

    private void notifyListeners()
    {
        for (TickListener listener : listeners)
        {
            listener.tick();
        }
    }

    @Override
    public void addTickListener(TickListener listener)
    {
        listeners.add(listener);
    }
}
