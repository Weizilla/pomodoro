package com.weizilla.pomodoro.cycle;

public class Cycle
{
    public enum Type
    {
        WORK, BREAK, LONG_BREAK
    }

    private final Type type;
    private final int numTicks;

    public Cycle(Type type, int numTicks)
    {
        this.type = type;
        this.numTicks = numTicks;
    }

    public Type getType()
    {
        return type;
    }

    public int getNumTicks()
    {
        return numTicks;
    }
}
