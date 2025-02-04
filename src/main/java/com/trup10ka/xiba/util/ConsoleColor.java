package com.trup10ka.xiba.util;

public enum ConsoleColor
{
    RED("\u001B[31m"),
    RESET("\u001B[0m");

    private final String color;

    ConsoleColor(String color)
    {
        this.color = color;
    }

    public String getColor()
    {
        return color;
    }
}
