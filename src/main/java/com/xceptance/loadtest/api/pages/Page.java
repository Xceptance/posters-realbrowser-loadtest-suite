package com.xceptance.loadtest.api.pages;

/**
 * Page interface.
 *
 * @author Xceptance Software Technologies
 */
public interface Page
{
    /**
     * Initializes the page object.
     * <p>
     * Will be executed once upon setting the page as current page and before validation of expected page.
     * Default implementation is empty. Override if initialization code is required.
     */
    void initialize();

    /**
     * Validates if the current page matches this page type.
     * <p>
     * Will be called when setting as the new current page.
     */
    void validateIsExpectedPage();
}
