package com.xceptance.loadtest.api.tests;

import java.text.MessageFormat;

import org.junit.Before;
import org.junit.Test;

import com.xceptance.loadtest.api.data.Site;
import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.FlowStoppedException;
import com.xceptance.loadtest.api.util.SelenideUtil;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Base class for all tests.
 *
 * @author Xceptance Software Technologies
 */
public abstract class LoadTestCase extends com.xceptance.xlt.api.tests.AbstractWebDriverTestCase implements SiteByMarketShare
{
    /**
     * The determined site or an empty optional if not yet done
     */
    private Site site;

    /**
     * Constructor
     */
    public LoadTestCase()
    {
        super();

        super.__setup();

        // Set test name depending if we have sites or not
        if ("default".equals(getSite().id) == false)
        {
            // we have something non default
            setTestName(MessageFormat.format("{0}_{1}", getTestName(), getSite().id));
        }

        // this moved here to make sure we see the exceptions
        Context.createContext(
                        XltProperties.getInstance(),
                        Session.getCurrent().getUserName(),
                        getClass().getName(),
                        getSite());

        // Setup Selenide
        SelenideUtil.initializeSelenide(Context.get().configuration, getWebDriver(), Session.getCurrent());
    }

    /**
     * Returns a random site
     *
     * @return
     */
    public Site getSite()
    {
        return site == null ? site = supplySite() : site;
    }

    /**
     * Test preparation. Nothing to do here by default. Feel free to override.
     *
     * @throws Throwable
     *             thrown on error
     */
    @Before
    public void init() throws Throwable
    {
    	// Empty
    }

    /**
     * Run the test scenario.
     *
     * @throws Throwable
     */
    @Test
    public void run() throws Throwable
    {
        try
        {
            // Execute the main test method.
            test();
        }
        catch (final FlowStoppedException e)
        {
            // If there's a flow stopper, log it. You'll find these entries in the report's 'Events' section.
            EventLogger.DEFAULT.error("FlowStopped - " + Session.getCurrent().getUserName(), e.getMessage());

            // Break in development mode to notice problems but continue in load test mode to not break the complete
            // test.
            if (!Context.isLoadTest)
            {
                throw e;
            }
        }
    }

    /**
     * Main test method.
     *
     * @throws Throwable
     */
    protected abstract void test() throws Throwable;

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDown()
    {
        super.tearDown();

        // release context
        Context.releaseContext();
    }
}