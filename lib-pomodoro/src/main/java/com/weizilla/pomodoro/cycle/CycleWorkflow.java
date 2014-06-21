package com.weizilla.pomodoro.cycle;

import java.util.HashMap;
import java.util.Map;

public class CycleWorkflow
{
    private final Map<Cycle.Type, Integer> numTicks = new HashMap<>(Cycle.Type.values().length);
    private int numBreaks;

    public CycleWorkflow(int numWorkTicks, int numBreakTicks, int numLongBreakTicks)
    {
        numTicks.put(Cycle.Type.WORK, numWorkTicks);
        numTicks.put(Cycle.Type.BREAK, numBreakTicks);
        numTicks.put(Cycle.Type.LONG_BREAK, numLongBreakTicks);
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
                return createCycle(Cycle.Type.WORK);
            default:
                throw new IllegalArgumentException("Unknown cycle type: " + cycle.getType());
        }
    }

    private Cycle createNextBreakCycle()
    {
        if (numBreaks == 3)
        {
            numBreaks = 0;
            return createCycle(Cycle.Type.LONG_BREAK);
        }
        else
        {
            numBreaks++;
            return createCycle(Cycle.Type.BREAK);
        }
    }

    public Cycle createCycle(Cycle.Type type)
    {
        return new Cycle(type, numTicks.get(type));
    }
}
