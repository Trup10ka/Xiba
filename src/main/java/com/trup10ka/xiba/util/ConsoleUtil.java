package com.trup10ka.xiba.util;

import static com.trup10ka.xiba.util.ConsoleColor.RESET;

public class ConsoleUtil
{
    public static void println(String text, ConsoleColor color)
    {
        System.out.println(color.getColor() + text + RESET.getColor());
    }

    public static void print(String text, ConsoleColor color)
    {
        System.out.print(color.getColor() + text + RESET.getColor());
    }

    public static void println(String text)
    {
        println(text, RESET);
    }

    public static void print(String text)
    {
        print(text, RESET);
    }
}
