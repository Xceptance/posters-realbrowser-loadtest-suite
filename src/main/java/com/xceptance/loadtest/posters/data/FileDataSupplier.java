package com.xceptance.loadtest.posters.data;

import org.junit.Assert;

import com.xceptance.loadtest.api.data.DataSupplier;
import com.xceptance.loadtest.api.data.Tuple;
import com.xceptance.loadtest.api.util.Context;

public class FileDataSupplier 
{
    /**
     * Get us some search data with expected counts
     */
    public static Tuple<String, String> searchPhraseWithResult()
    {
        var s = DataSupplier.randomString(DataSupplier.getSourceList(Context.configuration().dataFileSearchPhrases));
        var splitString = s.split(",");
        
        // make sure we have two things
        Assert.assertEquals(2, splitString.length);
        
        return new Tuple<String, String>(splitString[0], splitString[1]);
    }
}
