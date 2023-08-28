package com.xceptance.loadtest.api.util;

import java.util.function.Supplier;

import com.xceptance.xlt.api.engine.CustomData;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.engine.util.TimerUtils;

/**
 * Logger for custom data.
 * 
 * @author Xceptance Software Technologies
 */
public class CustomDataLogger
{
    // The instance that has a running timer attached
    private final CustomData customData;
	private long startTime;

    /**
     * Don't make it accessible from the outside
     *
     * @param name
     *            the name to use later
     */
    private CustomDataLogger(final String name)
    {
        this.customData = new CustomData(name);
        startTime = TimerUtils.get().getStartTime();
    }

    /**
     * Start a new logger
     *
     * @param name
     *            the name to use
     * @return this logger with a ticking clock
     */
    public static CustomDataLogger start(final String name)
    {
        return new CustomDataLogger(name);
    }

    /**
     * Stop this logger
     *
     * @return the runtime
     */
    public CustomDataLogger stop()
    {
    	this.customData.setRunTime(TimerUtils.get().getElapsedTime(startTime));

        return this;
    }

    /**
     * Stop this logger and return the runtime
     *
     * @return the runtime
     */
    public long stopAndGet()
    {
        stop();

        return this.customData.getRunTime();
    }

    /**
     * Stop this logger and log the time as not failed
     *
     * @return the runtime
     */
    public long stopAndLog()
    {
        return stop().log(false);
    }

    /**
     * Stop this logger and report the runtime
     *
     * @return the runtime
     */
    public long log(final boolean failed)
    {
        this.customData.setFailed(failed);

        Session.getCurrent().getDataManager().logDataRecord(this.customData);

        return this.customData.getRunTime();
    }

    /**
     * Log custom data
     *
     * @param name
     *            the name to log
     * @param runtime
     *            self measured runtime
     * @param failed
     *            was that a failed measurement
     */
    public static void log(final String name, final long runtime, final boolean failed)
    {
        final CustomData data = new CustomData();
        data.setName(name);
        data.setRunTime(runtime);
        data.setFailed(false);

        Session.getCurrent().getDataManager().logDataRecord(data);
    }

    /**
     * Log custom data that was successful
     *
     * @param name
     *            the name to log
     * @param runtime
     *            self measured runtime
     */
    public static void log(final String name, final long runtime)
    {
        log(name, runtime, false);
    }

    /**
     * Functional interface for logging custom data runtimes. Just more elegant in the code but not
     * suitable for everything due to the scope of the code block.
     *
     * @param name
     *            the name of the custom data
     * @param task
     *            the task to measure
     */
    public static void log(final String name, final Runnable task)
    {
    	final long startTime = TimerUtils.get().getStartTime();

        final CustomData cd = new CustomData(name);
    	cd.setTime(startTime);

        try
        {
            task.run();
        }
        catch (final Error e)
        {
            cd.setFailed(true);
            throw e;
        }
        finally
        {
            cd.setRunTime(TimerUtils.get().getElapsedTime(startTime));
            Session.getCurrent().getDataManager().logDataRecord(cd);
        }
    }
   
    /**
     * Functional interface for logging custom data runtimes. Just more elegant in the code but not suitable for everything due to the scope of the code block.
     *
     * @param name
     *            the name of the custom data
     * @param task
     *            the task to measure
     * @return the result of the task, because it is a supplier
     */
    public static <R> R log(final String name, final Supplier<R> task) throws Throwable
    {
    	final long startTime = TimerUtils.get().getStartTime();

    	final CustomData cd = new CustomData(name);

        try
        {
            return task.get();
        }
        catch (final Error e)
        {
            cd.setFailed(true);
            throw e;
        }
        finally
        {
            cd.setRunTime(TimerUtils.get().getElapsedTime(startTime));
            Session.getCurrent().getDataManager().logDataRecord(cd);
        }
    }
}
