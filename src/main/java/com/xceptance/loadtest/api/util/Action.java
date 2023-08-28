package com.xceptance.loadtest.api.util;

import java.util.Arrays;
import java.util.function.Supplier;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.xceptance.xlt.api.engine.scripting.StaticScriptCommands;

/**
 * Convenience methods for step definitions
 *
 * @author rschwietzke
 */
public class Action
{
    public final static Runnable EMPTY_RUNNABLE = () ->
    {
    };

    public static void run(final String name, final Runnable action)
    {
        runAndScreenWait(name, false, action);
    }

    public static void runAndScreenWait(final String name, final Runnable action)
    {
        runAndScreenWait(name, true, action);
    }

    public static void runAndScreenWait(final String name, final boolean screenWait, final Runnable action)
    {
        try
        {
            StaticScriptCommands.startAction(name);
            action.run();

            // wait for screen to settle
            if (screenWait)
            {
                waitForScreenToSettle(name);
            }
        }
        finally
        {
            StaticScriptCommands.stopAction();

            // final int a = XltProperties.getInstance().getProperty("com.xceptance.xlt.thinktime.action", 0);
            // final int b = XltProperties.getInstance().getProperty("com.xceptance.xlt.thinktime.action.deviation", 0);
            // final long resultingThinkTime = Math.max(0, XltRandom.nextIntWithDeviation(a, b));
            // try
            // {
            // Thread.sleep(resultingThinkTime);
            // }
            // catch (final InterruptedException e)
            // {
            // // bad, but for now
            // e.printStackTrace();
            // }
        }
    }

    public static <T> T run(final String name, final Supplier<T> action)
    {
        return runAndScreenWait(name, false, action);
    }

    public static <T> T runAndScreenWait(final String name, final Supplier<T> action)
    {
        return runAndScreenWait(name, true, action);
    }

    public static <T> T runAndScreenWait(final String name, final boolean screenWait, final Supplier<T> action)
    {
        try
        {
            StaticScriptCommands.startAction(name);
            final T t = action.get();

            // wait for screen to settle
            if (screenWait)
            {
                waitForScreenToSettle(name);
            }
            return t;
        }
        finally
        {
            StaticScriptCommands.stopAction();
        }
    }

    private static byte[] takeScreenshot(final String name)
    {
        try
        {
            return CustomDataLogger.log(name + " Screenshot", () ->
            {
                return ((TakesScreenshot) Context.getWebDriver()).getScreenshotAs(OutputType.BYTES);
            });
        }
        catch (final Throwable e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static void waitForScreenToSettle(final String name)
    {
        CustomDataLogger.log(name + " Screen Settle Time", () ->
        {
            byte[] s1 = null;
            byte[] s2 = takeScreenshot(name);

            // final Checksum checksum = new CRC32();
            // // update the current checksum with the specified array of bytes
            // checksum.update(s2, 0, s2.length);
            // // get the current checksum value
            // final long checksumValue = checksum.getValue();

            do
            {
                s1 = s2; // swap

                // wait
                try
                {
                    Thread.sleep(200);
                }
                catch (final InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                s2 = takeScreenshot(name);
            }
            while (Arrays.equals(s1, s2) == false);
        });
    }



}