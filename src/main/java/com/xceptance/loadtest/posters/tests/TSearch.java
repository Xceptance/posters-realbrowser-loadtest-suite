package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.posters.data.FileDataSupplier;
import com.xceptance.loadtest.posters.pages.GeneralPage;
import com.xceptance.loadtest.posters.pages.Homepage;
import com.xceptance.loadtest.posters.pages.SearchResultPage;

public class TSearch extends LoadTestCase
{
	/**
	 * Just execute a search and check the result as well as see a 
	 * product. It uses a test data file.
	 */
    public void test()
    {
    	// loads the homepage, data needed is taking from the properties automatically
    	// using the Context as well as the attached configuration.
    	Homepage.open();
    	
    	// search, get some data first, feel free to replace the Tuple approach for the return 
    	// value if this seems to fancy or stubborn or is not needed
    	var data = FileDataSupplier.searchPhraseWithResult();
    	GeneralPage.search(data.valueA, data.valueB);
    	
    	// view a product, this logic here expects that we have a product!
    	SearchResultPage.pdp();
    }
}
