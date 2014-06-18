package com.weizilla.pomodoro;

import com.weizilla.pomodoro.timer.CycleTimer;
import com.weizilla.pomodoro.timer.TickListener;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PomodoroControllerTest
{
    @Test
    public void controllerCallsTimerToStart() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = PomodoroController.createController(cycleTimer);

        TimeUnit timeUnit = TimeUnit.MINUTES;
        controller.startCycle(timeUnit);

        verify(cycleTimer).startCycle(timeUnit);
    }

    @Test
    public void addTickListener() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = PomodoroController.createController(cycleTimer);

        TickListener listener = mock(TickListener.class);

        controller.addTickListener(listener);

        assertTrue(controller.listeners.contains(listener));
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
}
