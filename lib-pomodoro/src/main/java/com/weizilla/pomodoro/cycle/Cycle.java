package com.weizilla.pomodoro.cycle;

import java.util.concurrent.TimeUnit;

public class Cycle
{
    public enum Type
    {
        WORK, BREAK, LONG_BREAK
    }

    private final Type type;
    private final int numTicks;
    private final TimeUnit timeUnit;

    public Cycle(Type type, int numTicks, TimeUnit timeUnit)
    {
        this.type = type;
        this.numTicks = numTicks;
        this.timeUnit = timeUnit;
    }

    public Type getType()
    {
        return type;
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
