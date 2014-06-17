package com.weizilla.pomodoro;

import com.weizilla.pomodoro.timer.CycleTimer;
import com.weizilla.pomodoro.timer.TickListener;

import java.util.concurrent.TimeUnit;

public class PomodoroController
{
    private CycleTimer cycleTimer;

    public PomodoroController(CycleTimer cycleTimer)
    {
        this.cycleTimer = cycleTimer;
    }

    public void startCycle(TimeUnit timeUnit, TickListener listener)
    {
        cycleTimer.addTickListener(listener);
        cycleTimer.startCycle(timeUnit);
    }
}
