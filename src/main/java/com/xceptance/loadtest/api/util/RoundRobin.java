package com.xceptance.loadtest.api.util;

/**
 * Round robin index counter.
 *
 * @autor Xceptance Software Technologies
 */
public class RoundRobin
{
    /**
     * Starting at the given index the method will add the offset in a round robin way and return
     * the resulting index.<br>
     * More technical:
     *
     * <pre>
     * (startIndex + offset) % size
     * </pre>
     *
     * It will also check that the given values are within a valid range.
     *
     * @param size
     * @param startIndex
     * @param offset
     * @return throws IndexOutOfBoundsException if size is negative, startIndex is not within size
     *         range, or offset is negative
     */
    public static int getIndex(final int size, final int startIndex, final int offset)
    {
        if (size <= 0 || startIndex < 0 || startIndex > size - 1 || offset < 0)
        {
            throw new IndexOutOfBoundsException();
        }

        return (startIndex + offset) % size;
    }
}