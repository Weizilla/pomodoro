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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Cycle cycle = (Cycle) o;

        if (numTicks != cycle.numTicks)
        {
            return false;
        }
        if (type != cycle.type)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + numTicks;
        return result;
    }
    
    @Override
    public String toString()
    {
        return "Cycle{" +
            "type=" + type +
            ", numTicks=" + numTicks +
            '}';
    }

}
