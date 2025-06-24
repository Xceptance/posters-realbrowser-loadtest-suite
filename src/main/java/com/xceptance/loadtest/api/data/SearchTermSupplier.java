package com.xceptance.loadtest.api.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.text.RandomStringGenerator;
import org.apache.commons.text.WordUtils;
import org.junit.Assert;

import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Data supplier for search terms.
 * 
 * A new search data provider that is able to come up with alternative search phrases based on
 * search phrases given.
 *
 * @author Xceptance Software Technologies
 */
public class SearchTermSupplier
{
    /**
     * Keep our data loaded and shared based on the site key and they type. We set concurrency for
     * write low but still harvest good read performance
     */
    private final static ConcurrentHashMap<String, List<String>> data = new ConcurrentHashMap<>(1);

    /**
     * Get a search phrase that will not have a hit, either from a predefined list or random values
     * when the list is not set
     *
     * @return search phrase that will no produce a result
     */
    public static String getTermWithoutHits()
    {
        RandomStringGenerator randomStringGenerator = RandomStringGenerator.builder().get();
        return Context.configuration().searchNoHitsTerms.isEmpty()
                ? randomStringGenerator.generate(XltRandom.nextInt(10, 20)) + " "
                        + randomStringGenerator.generate(XltRandom.nextInt(10, 20))
                        : Context.configuration().searchNoHitsTerms.random();
    }

    /**
     * Get a random row and if set, derive a similar phrase from it
     *
     * @param derive
     *            should we derive
     * @return a search phrase that might be derived or is the original one
     */
    public static String getTermWithHit()
    {
        final String originalPhrase = getTerm(Context.get().configuration.searchHitTermsFile);

        // shall we be fancy and get us a new but otherwise same term?
        // foo bar -> Foo Bar or foo BAR or so...
        final boolean derive = Context.get().configuration.searchNewPhraseDeriveProbability.value;

        // nothing to do, shortcut our way
        if (!derive || originalPhrase == null || originalPhrase.length() == 0)
        {
            return originalPhrase;
        }

        String newPhrase = originalPhrase;

        switch (XltRandom.nextInt(5)) {
            case 0:
                newPhrase = adjustCasing(originalPhrase);
                break;
            case 1:
                newPhrase = adjustWordCasing(originalPhrase);
                break;
            case 2:
                newPhrase = changeWhiteSpaces(originalPhrase);
                break;
            case 3:
                newPhrase = changeWhiteSpaces(adjustCasing(originalPhrase));
                break;
            case 4:
                newPhrase = changeWhiteSpaces(adjustWordCasing(originalPhrase));
                break;
        }

        return newPhrase;
    }

    /**
     * Change casing completely. Either all lower case or all uppercase, depending on the input. If
     * the input is mixed case, we get always uppercase.
     *
     * @param original
     *            to be changed string
     * @return new string with fully changed casing
     */
    private static String adjustCasing(final String original)
    {
        final String changed = original.toUpperCase();
        if (changed.equals(original))
        {
            // was already lowercase
            return changed.toLowerCase();
        }
        else
        {
            return changed;
        }
    }

    /**
     * Uppercase words, make sure it is lowercase before
     *
     * @param original
     *            to be changed string
     * @return new string with uppercased words
     */
    private static String adjustWordCasing(final String original)
    {
        return WordUtils.capitalize(original.toLowerCase());
    }

    /**
     * Fancy up the white spaces by adding double whites or some front and end spaces
     *
     * @param original
     *            to be changed string
     * @return new string with different white spacing
     */
    private static String changeWhiteSpaces(final String original)
    {
        final int LOCALPROBABILITY = 66;
        boolean wasChanged = false;

        final StringBuilder sb = new StringBuilder(original.length() * 2);

        // space at the beginning?
        if (XltRandom.nextBoolean(LOCALPROBABILITY / 2))
        {
            sb.append(' ');
            wasChanged = true;
        }

        // ok, run over the characters
        for (int i = 0; i < original.length(); i++)
        {
            final char c = original.charAt(i);

            sb.append(c);

            // if we are a whitespace and we want more (33% chance), append it
            // again
            if (Character.isWhitespace(c) && XltRandom.nextBoolean(LOCALPROBABILITY))
            {
                sb.append(c);
                wasChanged = true;
            }
        }

        // if we have not touched it, just append a space at the end to make sure we always change
        // at least this
        // otherwise try random here as well
        if (!wasChanged || XltRandom.nextBoolean(LOCALPROBABILITY / 2))
        {
            sb.append(' ');
        }

        return sb.toString();
    }

    /**
     * Return a term based on the current site config
     *
     * @param filename
     *            file to open in the hierarchy
     * @return a search term with a hit from that file
     */
    private static String getTerm(final String filename)
    {
        final Site site = Context.get().data.getSite();

        // get us a key, just use
        final String key = site.toString() + File.separator + filename;

        final List<String> list = data.computeIfAbsent(key, k -> {
            // load the data otherwise break
            final Optional<File> file = DataFileProvider.dataFileBySite(site, filename);

            if (file.isPresent())
            {
                try
                {
                    return Files.readAllLines(file.get().toPath())
                                    .stream()
                                    .map(s -> s.trim())
                                    .filter(s ->
                                    {
                                        return s.length() > 0 && !s.startsWith("#");
                                    })
                                    .collect(Collectors.toList());
                }
                catch (final IOException e)
                {
                    // we will get to the assertion
                }
            }

            Assert.fail(MessageFormat.format("Unable to find search term file {0} for site {1}", filename, site));

            // for the compiler, we are not going to reach this otherwise
            return Collections.emptyList();
        });

        // determine a random value
        return list.get(XltRandom.nextInt(list.size()));
    }

    /**
     * Return all terms based on the current site config
     *
     * @param filename
     *            file to open in the hierarchy
     * @return a search term with a hit from that file
     */
    public static List<String> getAllTerms()
    {
        final Site site = Context.get().data.getSite();
        final String filename = Context.get().configuration.searchHitTermsFile;

        // get us a key, just use
        final String key = site.toString() + File.separator + filename;

        final List<String> list = data.computeIfAbsent(key, k ->
        {
            // load the data otherwise break
            final Optional<File> file = DataFileProvider.dataFileBySite(site, filename);

            if (file.isPresent())
            {
                try
                {
                    return Files.readAllLines(file.get().toPath())
                                    .stream()
                                    .map(s -> s.trim())
                                    .filter(s ->
                                    {
                                        return s.length() > 0 && !s.startsWith("#");
                                    })
                                    .collect(Collectors.toList());
                }
                catch (final IOException e)
                {
                    // we will get to the assertion
                }
            }

            Assert.fail(MessageFormat.format("Unable to find search term file {0} for site {1}", filename, site));

            // for the compiler, we are not going to reach this otherwise
            return Collections.emptyList();
        });

        // determine a random value
        return list;
    }
}
