package com.weizilla.pomodoro;

import com.weizilla.pomodoro.cycle.Cycle;
import com.weizilla.pomodoro.cycle.CycleChangeListener;
import com.weizilla.pomodoro.cycle.CycleTickListener;
import com.weizilla.pomodoro.cycle.CycleWorkflow;
import com.weizilla.pomodoro.timer.CycleTimer;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PomodoroControllerTest
{
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    private static final int TEST_NUM_TICKS = 10;
    private static final Cycle.Type TEST_TYPE = Cycle.Type.WORK;
    private static final Cycle TEST_CYCLE = new Cycle(TEST_TYPE, TEST_NUM_TICKS);

    private static final int TEST_NEXT_NUM_TICKS = 5;
    private static final Cycle TEST_NEXT_CYCLE = new Cycle(Cycle.Type.BREAK, TEST_NEXT_NUM_TICKS);

    private CycleTimer mockTimer;
    private CycleWorkflow workflow;
    private PomodoroController controller;

    @Before
    public void setUp() throws Exception
    {
        mockTimer = mock(CycleTimer.class);
        workflow = spy(new CycleWorkflowStub(TEST_CYCLE, TEST_NEXT_CYCLE));
        controller = PomodoroController.createController(mockTimer, workflow, TIME_UNIT);
    }

    @Test
    public void controllerStartCallsWorkflowForCycleAndReturnsAsCurrentCycle() throws Exception
    {
        controller.start(TEST_TYPE);

        verify(workflow).createCycle(TEST_TYPE);

        assertSame(TEST_CYCLE, controller.getCurrentCycle());
        assertSame(TEST_NUM_TICKS, controller.getRemainingTicks());
    }

    @Test
    public void startCallsTimerToStart() throws Exception
    {
        controller.start(Cycle.Type.WORK);

        verify(mockTimer).startCycle(TIME_UNIT);
    }

    @Test
    public void repeatedCallsToStartRestartsTimer() throws Exception
    {
        reset(mockTimer);
        controller.start(Cycle.Type.WORK);
        verify(mockTimer, times(1)).startCycle(TIME_UNIT);

        reset(mockTimer);
        controller.start(Cycle.Type.BREAK);
        verify(mockTimer, times(1)).stopCycle();
        verify(mockTimer, times(1)).startCycle(TIME_UNIT);

        reset(mockTimer);
        controller.start(Cycle.Type.LONG_BREAK);
        verify(mockTimer, times(1)).stopCycle();
        verify(mockTimer, times(1)).startCycle(TIME_UNIT);
    }

    @Test
    public void pauseCallsTimerToStop() throws Exception
    {
        controller.start(TEST_TYPE);

        controller.pause();

        verify(mockTimer).stopCycle();
    }

    @Test
    public void pauseCallsTimerToStartAgainIfStartedCycleBefore() throws Exception
    {
        controller.start(TEST_TYPE);

        reset(mockTimer);
        controller.pause();

        verify(mockTimer, never()).startCycle(TIME_UNIT);
        verify(mockTimer, times(1)).stopCycle();

        reset(mockTimer);
        controller.pause();

        verify(mockTimer, times(1)).startCycle(TIME_UNIT);
        verify(mockTimer, never()).stopCycle();
    }

    @Test
    public void causeDoesNotCallTimerToStartIfNoCycleStartedBefore() throws Exception
    {
        controller.pause();

        verify(mockTimer, never()).stopCycle();
        verify(mockTimer, never()).startCycle(any(TimeUnit.class));
    }

    @Test
    public void stopCallsTimerToStop() throws Exception
    {
        controller.stop();

        verify(mockTimer).stopCycle();
    }

    @Test
    public void addCycleChangeListener() throws Exception
    {
        CycleChangeListener listener = mock(CycleChangeListener.class);

        controller.addCycleChangeListener(listener);

        assertTrue(controller.cycleChangeListeners.contains(listener));
    }

    @Test
    public void addCycleTickListener() throws Exception
    {
        CycleTickListener listener = mock(CycleTickListener.class);

        controller.addCycleTickListener(listener);

        assertTrue(controller.cycleTickListeners.contains(listener));
    }

    @Test
    public void tickNotifiesListenersWithRemainingTime() throws Exception
    {
        CycleTickListener listener = mock(CycleTickListener.class);
        controller.addCycleTickListener(listener);

        controller.start(TEST_TYPE);

        for (int i = TEST_NUM_TICKS - 1; i > 0; i--)
        {
            reset(listener);
            controller.tick();
            verify(listener).tick(i);
        }
    }

    @Test
    public void doNotSendRemainingTickOfZero() throws Exception
    {
        CycleTickListener listener = mock(CycleTickListener.class);
        controller.addCycleTickListener(listener);

        controller.start(TEST_TYPE);

        for (int i = 0; i < TEST_NUM_TICKS; i++)
        {
            controller.tick();
        }

        verify(listener, never()).tick(0);
    }

    @Test
    public void addsControllerToCycleTimerAsTickListener() throws Exception
    {
        verify(mockTimer).addTickListener(controller);
    }

    @Test
    public void countsDownAndCallsCycleChangeListener() throws Exception
    {
        CycleChangeListener cycleChangeListener = mock(CycleChangeListener.class);
        controller.addCycleChangeListener(cycleChangeListener);
        controller.start(TEST_TYPE);

        for (int i = 0; i < TEST_NUM_TICKS - 1; i++)
        {
            controller.tick();
        }
        verify(cycleChangeListener, never()).cycleChange(anyInt());

        controller.tick();

        verify(cycleChangeListener).cycleChange(TEST_NEXT_NUM_TICKS);
    }

    @Test
    public void callsWorkflowForNextCycleAndSetsItWhenCycleEnds() throws Exception
    {
        Cycle nextCycle = new Cycle(Cycle.Type.BREAK, 5);
        CycleWorkflow workflow = spy(new CycleWorkflowStub(TEST_CYCLE, nextCycle));
        controller = PomodoroController.createController(mockTimer, workflow, TIME_UNIT);

        controller.start(TEST_TYPE);

        for (int i = 0; i < TEST_NUM_TICKS; i++)
        {
            controller.tick();
        }

        verify(workflow).getNextCycle(TEST_CYCLE);
        assertSame(nextCycle, controller.getCurrentCycle());
    }

    @Test
    public void resetsRemainingTicksWhenStartingNextCycle() throws Exception
    {
        CycleWorkflow workflow = spy(new CycleWorkflowStub(TEST_CYCLE, TEST_NEXT_CYCLE));
        controller = PomodoroController.createController(mockTimer, workflow, TIME_UNIT);

        controller.start(TEST_TYPE);

        for (int i = 0; i < TEST_NUM_TICKS; i++)
        {
            controller.tick();
        }

        verify(workflow).getNextCycle(TEST_CYCLE);
        assertSame(TEST_NEXT_CYCLE, controller.getCurrentCycle());
        assertSame(TEST_NEXT_NUM_TICKS, controller.getRemainingTicks());
    }

    private static class CycleWorkflowStub extends CycleWorkflow
    {
        private Cycle cycle;
        private Cycle nextCycle;

        public CycleWorkflowStub(Cycle cycle, Cycle nextCycle)
        {
            super(0, 0, 0);
            this.cycle = cycle;
            this.nextCycle = nextCycle;
        }

        @Override
        public Cycle createCycle(Cycle.Type type)
        {
            return cycle;
        }

        @Override
        public Cycle getNextCycle(Cycle cycle)
        {
            return nextCycle;
        }
    }
}
