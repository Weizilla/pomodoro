package com.weizilla.pomodoro;

import java.util.concurrent.TimeUnit;

public class Cycle
{
    private TimeUnit timeUnit;

    public Cycle(TimeUnit timeUnit)
    {
        this.timeUnit = timeUnit;
    }

    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }
}
