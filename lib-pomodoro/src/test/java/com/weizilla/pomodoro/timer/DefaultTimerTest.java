package com.weizilla.pomodoro.timer;

import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DefaultTimerTest
{
    @Test
    public void addsListener() throws Exception
    {
        TickListener listener = mock(TickListener.class);
        DefaultCycleTimer timer = new DefaultCycleTimer(mock(ScheduledExecutorService.class));

        timer.addTickListener(listener);

        assertTrue(timer.listeners.contains(listener));
    }

    @Test
    public void updatesListenerWhenRun() throws Exception
    {
        TickListener listener = mock(TickListener.class);
        DefaultCycleTimer timer = new DefaultCycleTimer(mock(ScheduledExecutorService.class));
        timer.addTickListener(listener);

        timer.run();

        verify(listener).tick();
    }

    @Test
    public void startCycleSchedulesTimerToRun()
    {
        ScheduledExecutorService service = mock(ScheduledExecutorService.class);
        DefaultCycleTimer timer = new DefaultCycleTimer(service);

        TimeUnit timeUnit = TimeUnit.MINUTES;
        timer.startCycle(timeUnit);

        verify(service).scheduleAtFixedRate(timer, 0, 1, timeUnit);
    }

    @Test
    public void stopCycleStopsViaFuture()
    {
        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        ScheduledExecutorService service = new ScheduleExecutorServiceStub(future);

        DefaultCycleTimer timer = new DefaultCycleTimer(service);

        timer.startCycle(TimeUnit.MINUTES);
        timer.stopCycle();

        verify(future).cancel(anyBoolean());
    }
}