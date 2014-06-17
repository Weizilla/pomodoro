package com.weizilla.pomodoro.console;

import com.weizilla.pomodoro.PomodoroController;
import com.weizilla.pomodoro.timer.DefaultCycleTimer;
import com.weizilla.pomodoro.timer.TickListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConsoleApp
{
    public static void main(String[] args)
    {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        DefaultCycleTimer timer = new DefaultCycleTimer(executorService);
        PomodoroController controller = new PomodoroController(timer);
        controller.startCycle(TimeUnit.SECONDS, new TickListener()
        {
            @Override
            public void tick()
            {
                System.out.println("Tick");
            }
        });
    }
}
