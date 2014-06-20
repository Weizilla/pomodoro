package com.weizilla.pomodoro.cycle;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class CycleWorkflowTest
{
    private static final Cycle WORK = new Cycle(Cycle.Type.WORK, 10, TimeUnit.MINUTES);
    private static final Cycle BREAK = new Cycle(Cycle.Type.BREAK, 10, TimeUnit.MINUTES);
    private static final Cycle LONG_BREAK = new Cycle(Cycle.Type.LONG_BREAK, 10, TimeUnit.MINUTES);
    private static final int NUM_WORK_TICKS = 25;
    private static final int NUM_BREAK_TICKS = 5;
    private static final int NUM_LONG_BREAK_TICKS = 20;
    private static final TimeUnit TIME_UNITS = TimeUnit.MINUTES;
    
    private CycleWorkflow workflow;

    @Before
    public void setUp() throws Exception
    {
        workflow = new CycleWorkflow(NUM_WORK_TICKS, NUM_BREAK_TICKS, NUM_LONG_BREAK_TICKS, TIME_UNITS);
    }

    @Test
    public void movesFromWorkToBreak() throws Exception
    {
        Cycle next = workflow.getNextCycle(WORK);
        assertEquals(Cycle.Type.BREAK, next.getType());
        assertEquals(NUM_BREAK_TICKS, next.getNumTicks());
        assertEquals(TIME_UNITS, next.getTimeUnit());
    }

    @Test
    public void movesFromBreakToWork() throws Exception
    {
        Cycle next = workflow.getNextCycle(BREAK);
        assertEquals(Cycle.Type.WORK, next.getType());
        assertEquals(NUM_WORK_TICKS, next.getNumTicks());
        assertEquals(TIME_UNITS, next.getTimeUnit());
    }

    @Test
    public void movesFromLongBreakToWork() throws Exception
    {
        Cycle next = workflow.getNextCycle(LONG_BREAK);
        assertEquals(Cycle.Type.WORK, next.getType());
        assertEquals(NUM_WORK_TICKS, next.getNumTicks());
        assertEquals(TIME_UNITS, next.getTimeUnit());
    }

    @Test
    public void forthBreakIsLongBreak() throws Exception
    {
        Cycle next = null;
        for (int i = 0; i < 4; i++)
        {
            next = workflow.getNextCycle(WORK);
        }
        assertEquals(Cycle.Type.LONG_BREAK, next.getType());
        assertEquals(NUM_LONG_BREAK_TICKS, next.getNumTicks());
        assertEquals(TIME_UNITS, next.getTimeUnit());
    }

    @Test
    public void fullWorkflow() throws Exception
    {
        Cycle.Type[] expectedTypes =
            {
                Cycle.Type.WORK, Cycle.Type.BREAK,
                Cycle.Type.WORK, Cycle.Type.BREAK,
                Cycle.Type.WORK, Cycle.Type.BREAK,
                Cycle.Type.WORK, Cycle.Type.LONG_BREAK,
                Cycle.Type.WORK, Cycle.Type.BREAK
            };
        Cycle cycle = WORK;
        for (Cycle.Type expected : expectedTypes)
        {
            assertEquals(expected, cycle.getType());
            cycle = workflow.getNextCycle(cycle);
        }
    }
}