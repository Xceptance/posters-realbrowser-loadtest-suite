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
			$("#search-form input[name='searchText']").sendKeys(phrase);

			// send search, this is our page load
			$("#btnSearch").click();

			// verify count
			$("#totalProductCount").should(Condition.exactText(expectedCount));
		});
	}
}
