package com.weizilla.pomodoro.timer;

import java.util.concurrent.TimeUnit;

public interface CycleTimer
{
    void startCycle(TimeUnit timeUnit);

    void addTickListener(TimerTickListener listener);

    void stopCycle();
}
