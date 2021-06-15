package com.xceptance.loadtest.api.components;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

/**
 * Abstract base class for all components.
 *
 * @author Xceptance Software Technologies
 */
public abstract class AbstractComponent implements Component
{
    private final SelenideElement ancestor;

    public AbstractComponent()
    {
        ancestor = $("html");
    }

    public AbstractComponent(final SelenideElement ancestor)
    {
        this.ancestor = ancestor;
    }

    public SelenideElement ancestor()
    {
        return ancestor;
    }

    /**
     * Checks if a component is available.
     * <p>
     * Note:
     * Will use implementation validateComponent() but catch all AssertionErrors.
     * Since waiting (polling) is involved the method should be used with reason.
     *
     * @return true if the component is available, false otherwise.
     */
    public boolean isAvailable()
    {
        try
        {
            validateComponent();
        } catch (final AssertionError ae)
        {
            return false;
        }

        return true;
    }
}
