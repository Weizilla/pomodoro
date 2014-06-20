package com.weizilla.pomodoro;

import com.weizilla.pomodoro.cycle.Cycle;
import com.weizilla.pomodoro.cycle.CycleEndListener;
import com.weizilla.pomodoro.cycle.CycleTickListener;
import com.weizilla.pomodoro.timer.CycleTimer;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PomodoroControllerTest
{
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
    private CycleTimer mockCycleTimer;
    private PomodoroController controller;

    @Before
    public void setUp() throws Exception
    {
        mockCycleTimer = mock(CycleTimer.class);
        controller = PomodoroController.createController(mockCycleTimer, TIME_UNIT);
    }

    @Test
    public void controllerCallsTimerToStart() throws Exception
    {
        controller.startCycle(new Cycle(Cycle.Type.WORK, 10));

        verify(mockCycleTimer).startCycle(TIME_UNIT);
    }

    @Test
    public void addCycleEndListener() throws Exception
    {
        CycleEndListener listener = mock(CycleEndListener.class);

        controller.addCycleEndListener(listener);

        assertTrue(controller.cycleEndListeners.contains(listener));
    }

    @Test
    public void addCycleTickListener() throws Exception
    {
        CycleTickListener listener = mock(CycleTickListener.class);

        controller.addCycleTickListener(listener);

        assertTrue(controller.cycleTickListeners.contains(listener));
    }

    @Test
    public void controllerCallsTimerToStop() throws Exception
    {
        controller.stopCycle();

        verify(mockCycleTimer).stopCycle();
    }

    @Test
    public void tickNotifiesListenersWithRemainingTime() throws Exception
    {
        CycleTickListener listener = mock(CycleTickListener.class);
        controller.addCycleTickListener(listener);

        controller.startCycle(new Cycle(Cycle.Type.WORK, 10));

        for (int i = 9; i >= 0; i--)
        {
            controller.tick();
            verify(listener).tick(i);
        }
    }

    @Test
    public void addsControllerToCycleTimerAsTickListener() throws Exception
    {
        verify(mockCycleTimer).addTickListener(controller);
    }

    @Test
    public void countsDownAndCallsCycleEndListener() throws Exception
    {
        int numTicks = 10;
        CycleEndListener cycleEndListener = mock(CycleEndListener.class);
        controller.addCycleEndListener(cycleEndListener);
        controller.startCycle(new Cycle(Cycle.Type.WORK, numTicks));

        for (int i = 0; i < numTicks - 1; i++)
        {
            controller.tick();
        }
        verify(cycleEndListener, never()).cycleEnd();

        controller.tick();

        verify(cycleEndListener).cycleEnd();
    }
}
