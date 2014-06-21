package com.weizilla.pomodoro.console;

import com.weizilla.pomodoro.cycle.Cycle;
import com.weizilla.pomodoro.cycle.CycleTickListener;
import com.weizilla.pomodoro.PomodoroController;
import com.weizilla.pomodoro.cycle.CycleWorkflow;
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
        CycleWorkflow workflow = new CycleWorkflow(10, 10, 10);
        final PomodoroController controller = PomodoroController.createController(timer, workflow, TimeUnit.SECONDS);
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
        controller.startCycle(Cycle.Type.WORK);
    }
}
