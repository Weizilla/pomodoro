package com.weizilla.pomodoro.cycle;

import java.util.concurrent.TimeUnit;

public class CycleWorkflow
{
    private int numWorkTicks;
    private int numBreakTicks;
    private int numLongBreakTicks;
    private TimeUnit timeUnit;
    private int numBreaks;

    public CycleWorkflow(int numWorkTicks, int numBreakTicks, int numLongBreakTicks, TimeUnit timeUnit)
    {
        this.numWorkTicks = numWorkTicks;
        this.numBreakTicks = numBreakTicks;
        this.numLongBreakTicks = numLongBreakTicks;
        this.timeUnit = timeUnit;
    }

    public Cycle getNextCycle(Cycle cycle)
    {
        switch (cycle.getType())
        {
            case WORK:
                return createNextBreakCycle();
            case BREAK:
                // fall through
            case LONG_BREAK:
                return new Cycle(Cycle.Type.WORK, numWorkTicks, timeUnit);
            default:
                throw new IllegalArgumentException("Unknown cycle type: " + cycle.getType());
        }
    }

    private Cycle createNextBreakCycle()
    {
        if (numBreaks == 3)
        {
            numBreaks = 0;
            return new Cycle(Cycle.Type.LONG_BREAK, numLongBreakTicks, timeUnit);
        }
        else
        {
            numBreaks++;
            return new Cycle(Cycle.Type.BREAK, numBreakTicks, timeUnit);
        }
    }
}