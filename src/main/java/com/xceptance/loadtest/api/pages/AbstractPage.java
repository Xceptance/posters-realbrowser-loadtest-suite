package com.xceptance.loadtest.api.pages;

/**
 * Abstract base class for all pages.
 *
 * @author Xceptance Software Technologies
 */
public abstract class AbstractPage implements Page
{
    public void initialize()
    {
        // Intentionally left empty, subclasses can override if page specific initialization code is required
    }
}
