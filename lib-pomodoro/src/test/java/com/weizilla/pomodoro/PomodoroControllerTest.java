package com.weizilla.pomodoro;

import com.weizilla.pomodoro.cycle.Cycle;
import com.weizilla.pomodoro.cycle.CycleEndListener;
import com.weizilla.pomodoro.cycle.CycleTickListener;
import com.weizilla.pomodoro.timer.CycleTimer;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PomodoroControllerTest
{
    @Test
    public void controllerCallsTimerToStart() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = PomodoroController.createController(cycleTimer);

        TimeUnit timeUnit = TimeUnit.MINUTES;
        Cycle cycle = new Cycle(0, timeUnit);
        controller.startCycle(cycle);

        verify(cycleTimer).startCycle(timeUnit);
    }

    @Test
    public void addCycleEndListener() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = PomodoroController.createController(cycleTimer);

        CycleEndListener listener = mock(CycleEndListener.class);

        controller.addCycleEndListener(listener);

        assertTrue(controller.cycleEndListeners.contains(listener));
    }

    @Test
    public void addCycleTickListener() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = PomodoroController.createController(cycleTimer);

        CycleTickListener listener = mock(CycleTickListener.class);

        controller.addCycleTickListener(listener);

        assertTrue(controller.cycleTickListeners.contains(listener));
    }

    @Test
    public void controllerCallsTimerToStop() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = PomodoroController.createController(cycleTimer);

        controller.stopCycle();

        verify(cycleTimer).stopCycle();
    }

    @Test
    public void tickNotifiesListenersWithRemainingTime() throws Exception
    {
        CycleTickListener listener = mock(CycleTickListener.class);

        PomodoroController controller = PomodoroController.createController(mock(CycleTimer.class));
        controller.addCycleTickListener(listener);

        controller.startCycle(new Cycle(10, TimeUnit.MINUTES));

        for (int i = 9; i >= 0; i--)
        {
            controller.tick();
            verify(listener).tick(i);
        }
    }

    @Test
    public void addsControllerToCycleTimerAsTickListener() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = PomodoroController.createController(cycleTimer);

        verify(cycleTimer).addTickListener(controller);
    }

    @Test
    public void countsDownAndCallsCycleEndListener() throws Exception
    {
        int numTicks = 10;
        PomodoroController controller = PomodoroController.createController(mock(CycleTimer.class));
        CycleEndListener cycleEndListener = mock(CycleEndListener.class);
        controller.addCycleEndListener(cycleEndListener);
        controller.startCycle(new Cycle(numTicks, TimeUnit.SECONDS));

        for (int i = 0; i < numTicks - 1; i++)
        {
            controller.tick();
        }
        verify(cycleEndListener, never()).cycleEnd();

        controller.tick();

        verify(cycleEndListener).cycleEnd();
    }
}
