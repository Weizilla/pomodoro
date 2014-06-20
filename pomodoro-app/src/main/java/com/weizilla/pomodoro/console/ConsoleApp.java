package com.weizilla.pomodoro.console;

import com.weizilla.pomodoro.Cycle;
import com.weizilla.pomodoro.CycleTickListener;
import com.weizilla.pomodoro.PomodoroController;
import com.weizilla.pomodoro.timer.DefaultCycleTimer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConsoleApp
{
    public static void main(String[] args)
    {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        DefaultCycleTimer timer = new DefaultCycleTimer(executorService);
        final PomodoroController controller = PomodoroController.createController(timer);
        controller.addCycleTickListener(new CycleTickListener()
        {
            @Override
            public void tick(int ticksRemaining)
            {
                System.out.println("Tick " + ticksRemaining);
                if (ticksRemaining == 10)
                {
                    controller.stopCycle();
                }
            }
        });
        controller.startCycle(new Cycle(25, TimeUnit.SECONDS));
    }
}
