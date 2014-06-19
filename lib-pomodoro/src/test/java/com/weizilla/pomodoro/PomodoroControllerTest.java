package com.weizilla.pomodoro;

import com.weizilla.pomodoro.timer.CycleTimer;
import com.weizilla.pomodoro.timer.TickListener;
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
    public void addTickListener() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = PomodoroController.createController(cycleTimer);

        TickListener listener = mock(TickListener.class);

        controller.addTickListener(listener);

        assertTrue(controller.tickListeners.contains(listener));
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
    public void tickNotifiesListeners() throws Exception
    {
        TickListener listener = mock(TickListener.class);

        PomodoroController controller = PomodoroController.createController(mock(CycleTimer.class));
        controller.addTickListener(listener);

        controller.tick();

        verify(listener).tick();
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
