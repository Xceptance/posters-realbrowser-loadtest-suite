package com.xceptance.loadtest.posters.pages;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selenide;
import com.xceptance.loadtest.api.util.Action;
import com.xceptance.loadtest.api.util.Context;

/**
 * Holds all relevant homepage logic. This approach is a relatively simple but 
 * works well depending on complexity.
 * 
 * @author rschwietzke
 *
 */
public class Homepage 
{
	/**
	 * Opens the homepage, if authentication is needed, will take care of that as well
	 */
	public static void open()
	{
		final String NAME = "OpenHomepage";
		
		// Clear cookies to ensure a new session, this is only needed for local debugging
		Selenide.clearBrowserCookies();

		final String siteUrl = Context.get().configuration.siteUrlHomepage;
		final String siteUsername = Context.get().configuration.credentialsUserName;
		final String sitePassword = Context.get().configuration.credentialsPassword;

		if (siteUsername != null && !siteUsername.isBlank())
		{
			// this is almost a must when you run https and authentication
			if (Context.get().configuration.preloadAuthentication)
			{
				// Open home page and authenticate
				Action.run(NAME + "_Auth", () -> Selenide.open(siteUrl, "", siteUsername, sitePassword));
				// we don't check the first load... second is enough and the page might be broken anyway,
				// hence the later reload
	
				// Reopen home page to avoid JS security issue with credentials in URL
				Action.run(NAME, () -> {
					Selenide.open(siteUrl);
					verify();
				});
			}
			else
			{
				// Simply open the home page and authenticate
				Action.run(NAME + "_Auth", () -> 
				{
					Selenide.open(siteUrl, "", siteUsername, sitePassword);
					verify();
				});
			}
		}
		else 
		{
			// !! Action run takes care of the handling of action and request for XLT, if this 
			// is not used aka the activity marked, it might not be measured at all.
			// Also note, only page loads will see page load timing such as First Paint. 
			// For any asynchronous action, make sure you wait properly to ensure that the request
			// finishes within this action block, otherwise it might not be measured or end up in the
			// next action.
			// Reopen home page to avoid JS security issue with credentials in URL
			Action.run(NAME, () -> {
				Selenide.open(siteUrl);
				verify();
			});
		}
	}	
	
	/**
	 * Check that the homepage is correct. This activity has to be in the action to get the failure
	 * properly reported later on in case this verification fails.
	 * 
	 * Everything possible can be done here of course. But for a load test the balance has to be right.
	 * Every check requires cpu and memory, the more checks, the less scale you get out of a single node
	 * in terms of concurrent users. Experiment with that.
	 */
	public static void verify()
	{
		// check header
		$("#globalNavigation").exists();
		
		// mini cart must be 0
		$(".header-cart-product-count").should(exactText("0"));
		
		// check footer
		$("#footer").exists();
	}
}
