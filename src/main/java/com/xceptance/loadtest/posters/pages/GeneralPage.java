package com.xceptance.loadtest.posters.pages;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Condition;
import com.xceptance.loadtest.api.util.Action;

/**
 * Contains everything doable on all page types.
 * 
 * @author rschwietzke
 */
public class GeneralPage 
{
	public static void search(final String phrase, final String expectedCount)
	{
		Action.run("Search", () ->
		{
			// enter phrase
			$("#header-search-form .form-control").sendKeys(phrase);

			// send search, this is our page load
			$("#header-search-button").click();

			// verify count
			$("#total-product-count").should(Condition.exactText(expectedCount));
		});
	}
}
