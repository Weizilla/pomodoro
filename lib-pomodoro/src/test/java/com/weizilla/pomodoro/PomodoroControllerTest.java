package com.weizilla.pomodoro;

import com.weizilla.pomodoro.timer.CycleTimer;
import com.weizilla.pomodoro.timer.CycleTimerStub;
import com.weizilla.pomodoro.timer.TickListener;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PomodoroControllerTest
{
    @Test
    public void controllerCallsTimerToStart() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = new PomodoroController(cycleTimer);

        TimeUnit timeUnit = TimeUnit.MINUTES;
        controller.startCycle(timeUnit, mock(TickListener.class));

        verify(cycleTimer).startCycle(timeUnit);
    }

    @Test
    public void controllerRegistersForTimerTicks() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = new PomodoroController(cycleTimer);

        TickListener listener = mock(TickListener.class);

        controller.startCycle(TimeUnit.MINUTES, listener);

        verify(cycleTimer).addTickListener(listener);
    }

    @Test
    public void timerTickNotifiesTickListener()
    {
        CycleTimerStub timer = new CycleTimerStub();
        PomodoroController controller = new PomodoroController(timer);

        TickListener listener = mock(TickListener.class);

        controller.startCycle(TimeUnit.MINUTES, listener);

        timer.tick();
        timer.tick();

        verify(listener, times(2)).tick();
    }

    @Test
    public void controllerCallsTimerToStop() throws Exception
    {
        CycleTimer cycleTimer = mock(CycleTimer.class);
        PomodoroController controller = new PomodoroController(cycleTimer);

        controller.stopCycle();

        verify(cycleTimer).stopCycle();
    }
}