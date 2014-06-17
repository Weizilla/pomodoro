package com.weizilla.pomodoro.timer;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CycleTimerStub implements CycleTimer
{
    private final List<TickListener> listeners = new ArrayList<>();

    @Override
    public void startCycle(TimeUnit timeUnit)
    {
        //TODO Auto-generated
    }

    @Override
    public void addTickListener(TickListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void stopCycle()
    {
        //TODO Auto-generated

    }

    public void tick()
    {
        for (TickListener listener : listeners)
        {
            listener.tick();
        }
    }
}
