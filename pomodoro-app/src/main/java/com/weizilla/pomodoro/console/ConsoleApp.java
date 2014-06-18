package com.weizilla.pomodoro.console;

import com.weizilla.pomodoro.PomodoroController;
import com.weizilla.pomodoro.timer.DefaultCycleTimer;
import com.weizilla.pomodoro.timer.TickListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsoleApp
{
    public static void main(String[] args)
    {
        final AtomicInteger t = new AtomicInteger(0);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        DefaultCycleTimer timer = new DefaultCycleTimer(executorService);
        final PomodoroController controller = PomodoroController.createController(timer);
        controller.addTickListener(new TickListener()
        {
            @Override
            public void tick()
            {
                int i = t.incrementAndGet();
                System.out.println("Tick " + i);
                if (i == 10)
                {
                    controller.stopCycle();
                }
            }
        });
        controller.startCycle(TimeUnit.SECONDS);
    }
}
