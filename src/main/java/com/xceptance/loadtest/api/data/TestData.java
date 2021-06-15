package com.xceptance.loadtest.api.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;

import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.engine.Session;

/**
 * Common data collector for all data required during execution of a test scenario.
 * 
 * A global state supporting scenario execution.
 *
 * @author Xceptance Software Technologies
 */
public class TestData
{
    // The account used in this test case.
    private Optional<Account> account = Optional.empty();

    // Simple key-value store
    public Map<String, String> store = new HashMap<>(41);

    // Is test case expected to run with a customer that needs an existing account?
    public boolean requiresRegisteredAccount;

    // Add2cart count, number of add2cart operations for action naming
    public int totalAddToCartCount = 0;

    // Stores the current cart line item count of this very session
    public int cartLineItemCount;

    // Stores the current quantity count of the cart
    public int cartQuantityCount;

    // Identifies the site the test is targeting 
    public Site site;

    // The current page.
    private Page currentPage;
    
    /**
     * Sets the current page.
     *
     * @param page The new current page.
     */
    public void setCurrentPage(Page page)
    {
        // Call page initialization
        page.initialize();

        // Validate if the current page is the expected page
        page.validateIsExpectedPage();

        // Set as current page
        currentPage = page;
    }

    /**
     * Retrieves the current page.
     *
     * @return The current page.
     */
    public Page getCurrentPage()
    {
        return currentPage;
    }
    
    /**
     * Retrieves the current page as given type.
     *
     * @return The current page.
     */
    public <T extends Page> T getCurrentPageAs(Class<T> type)
    {
        Assert.assertTrue("Failed to retrieve current page as type " + type.getSimpleName(), type.isInstance(currentPage));
        return type.cast(currentPage);
    }
    
    /**
     * Returns the attached account as optional, so it cannot be empty/null.
     *
     * @return The current account or an optional stating we don't have one
     */
    public Optional<Account> getAccount()
    {
        return account;
    }

    /**
     * Attaches a customer account to our session/context.
     * 
     * If there is already an account attached, it will fail.
     *
     * @return Customer account
     * @throws AssertionError When an account was already attached
     */
    public Optional<Account> attachAccount()
    {
        if (!account.isPresent())
        {
            setAccount(AccountManager.aquireAccount());
        }
        else
        {
            // An account was already attached before
            Assert.fail("An account was already attached");
        }

        return account;
    }
    
    /**
     * Attaches a customer account to our session/context.
     * 
     * If there is already an account attached, it will fail.
     * 
     * @return customer account
     * @throws AssertionError When an account was already attached
     */
    public Optional<Account> attachAccountFromFile(boolean exclusive)
    {
        if (!account.isPresent())
        {
            setAccount(AccountManager.aquireAccountFromFile(exclusive));
        }
        else
        {
            // An account was already attached before
            Assert.fail("An account was already attached");
        }

        return account;
    }

    /**
     * Sets the account to use for this test.
     *
     * @param account The account to use. If set to <code>null</code> a newly generated account will assigned.
     */
    public void setAccount(final Optional<Account> account)
    {
        // Assign the account.
        this.account = account;
    }

    /**
     * Releases the used account and put it back to the pool for re-usage if possible.
     * 
     * @throws Exception 
     */
    public void releaseAccount() throws Exception
    {
        // Get the current context and account.
        final Optional<Account> account = getAccount();

        // Release the user's exclusively used account if it is a registered account. Guest user
        // account will be simply dropped.
        if (account.isPresent())
        {
            // Put account into value log
            final String siteId = Context.getSite().getId();

            final Map<String, Object> log = Session.getCurrent().getValueLog();
            log.put("account." + siteId + ".email", account.get().email);
            log.put("account." + siteId + ".password", account.get().password);
            log.put("account." + siteId + ".isRegistered", account.get().isRegistered);
            log.put("account.origin", account.get().origin);

            if (account.get().isRegistered && Session.getCurrent().hasFailed() == false)
            {
                AccountManager.returnAccount(account);
            }
        }

        // Release reference
        this.account = Optional.empty();
    }

    /**
     * Sets the site.
     *
     * @param newSite The new site
     * @return The old site if set, otherwise null.
     */
    public Site setSite(final Site newSite)
    {
        final Site oldSite = this.site;
        this.site = newSite;

        return oldSite;
    }

    /**
     * Returns the current site.
     *
     * @return The current site.
     */
    public Site getSite()
    {
        return site;
    }
}