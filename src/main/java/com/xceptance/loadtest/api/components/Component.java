package com.xceptance.loadtest.api.components;

import com.codeborne.selenide.SelenideElement;

/**
 * Component interface.
 * <p>
 * Components are part of pages and provide the required functionality needed by the page.
 *
 * @author Xceptance Software Technologies
 */
public interface Component
{
    /**
     * Locates the component at the current page.
     *
     * @return A SelenideElement representing the component
     */
    SelenideElement locate();

    /**
     * Validates that a given component is currently available at the current page.
     */
    void validateComponent();
}