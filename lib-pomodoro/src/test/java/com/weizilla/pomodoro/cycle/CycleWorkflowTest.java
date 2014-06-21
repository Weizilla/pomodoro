package com.weizilla.pomodoro.cycle;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CycleWorkflowTest
{
    private static final int NUM_WORK_TICKS = 25;
    private static final int NUM_BREAK_TICKS = 5;
    private static final int NUM_LONG_BREAK_TICKS = 20;
    private static final Cycle WORK = new Cycle(Cycle.Type.WORK, NUM_WORK_TICKS);
    private static final Cycle BREAK = new Cycle(Cycle.Type.BREAK, NUM_BREAK_TICKS);
    private static final Cycle LONG_BREAK = new Cycle(Cycle.Type.LONG_BREAK, NUM_LONG_BREAK_TICKS);

    private CycleWorkflow workflow;

    @Before
    public void setUp() throws Exception
    {
        workflow = new CycleWorkflow(NUM_WORK_TICKS, NUM_BREAK_TICKS, NUM_LONG_BREAK_TICKS);
    }

    @Test
    public void createsWorkCycle() throws Exception
    {
        Cycle cycle = workflow.createCycle(Cycle.Type.WORK);
        assertEquals(WORK, cycle);
    }

    @Test
    public void createsBreakCycle() throws Exception
    {
        Cycle cycle = workflow.createCycle(Cycle.Type.BREAK);
        assertEquals(BREAK, cycle);
    }

    @Test
    public void createsLongBreakCycle() throws Exception
    {
        Cycle cycle = workflow.createCycle(Cycle.Type.LONG_BREAK);
        assertEquals(LONG_BREAK, cycle);
    }

    @Test
    public void movesFromWorkToBreak() throws Exception
    {
        Cycle next = workflow.getNextCycle(WORK);
        assertEquals(Cycle.Type.BREAK, next.getType());
    }

    @Test
    public void movesFromBreakToWork() throws Exception
    {
        Cycle next = workflow.getNextCycle(BREAK);
        assertEquals(Cycle.Type.WORK, next.getType());
    }

    @Test
    public void movesFromLongBreakToWork() throws Exception
    {
        Cycle next = workflow.getNextCycle(LONG_BREAK);
        assertEquals(Cycle.Type.WORK, next.getType());
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