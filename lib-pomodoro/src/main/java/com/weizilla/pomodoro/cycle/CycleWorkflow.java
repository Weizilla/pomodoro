package com.weizilla.pomodoro.cycle;

public class CycleWorkflow
{
    private int numWorkTicks;
    private int numBreakTicks;
    private int numLongBreakTicks;
    private int numBreaks;

    public CycleWorkflow(int numWorkTicks, int numBreakTicks, int numLongBreakTicks)
    {
        this.numWorkTicks = numWorkTicks;
        this.numBreakTicks = numBreakTicks;
        this.numLongBreakTicks = numLongBreakTicks;
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
                return new Cycle(Cycle.Type.WORK, numWorkTicks);
            default:
                throw new IllegalArgumentException("Unknown cycle type: " + cycle.getType());
        }
    }

    private Cycle createNextBreakCycle()
    {
        if (numBreaks == 3)
        {
            numBreaks = 0;
            return new Cycle(Cycle.Type.LONG_BREAK, numLongBreakTicks);
        }
        else
        {
            numBreaks++;
            return new Cycle(Cycle.Type.BREAK, numBreakTicks);
        }
    }
}
