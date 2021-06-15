package com.xceptance.loadtest.posters.pages;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selenide;
import com.xceptance.loadtest.api.util.Action;
import com.xceptance.loadtest.api.util.Context;

/**
 * Does things on a search result page
 * 
 * @author rschwietzke
 *
 */
public class SearchResultPage 
{
	/**
	 * Check that the homepage is correct. This activity has to be in the action to get the failure
	 * properly reported later on in case this verification fails.
	 * 
	 * Everything possible can be done here of course. But for a load test the balance has to be right.
	 * Every check requires cpu and memory, the more checks, the less scale you get out of a single node
	 * in terms of concurrent users. Experiment with that.
	 */
	public static void pdp()
	{
		// check header
		$("#globalNavigation").exists();
		
		// mini cart must be 0
		$(".headerCartProductCount").should(exactText("0"));
		
		// check footer
		$("#footer").exists();
	}
}
