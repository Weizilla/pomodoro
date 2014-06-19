package com.weizilla.pomodoro;

import java.util.concurrent.TimeUnit;

public class Cycle
{
    private final int numTicks;
    private final TimeUnit timeUnit;

    public Cycle(int numTicks, TimeUnit timeUnit)
    {
        this.numTicks = numTicks;
        this.timeUnit = timeUnit;
    }

    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    public int getNumTicks()
    {
        return numTicks;
    }
}
