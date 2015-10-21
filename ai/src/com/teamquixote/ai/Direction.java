package com.teamquixote.ai;

public enum Direction {
    North,
    NorthWest,
    West,
    SouthWest,
    South,
    SouthEast,
    East,
    NorthEast;

    public static Direction getRelativeDirection(Direction facing, RelativeDirection relative){
        int newDir = (facing.ordinal() + relative.ordinal()) % Direction.values().length;
        return Direction.values()[newDir];
    }
}
