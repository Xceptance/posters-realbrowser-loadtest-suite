package com.xceptance.loadtest.api.data;

/**
 * Can be used as flexible container to return two things at once if needed.
 * This is not awesome for performance due to the container object and overhead, 
 * but if applied carefully can help to lower processing time due to avoiding to 
 * do things twice.
 * 
 * Looking forward to "include classes" and "records" with Java 14 and higher!
 * 
 * @author rschwietzke
 *
 * @param <A>
 * @param <B>
 */
public class Tuple<A, B> 
{
	public final A valueA;
	public final B valueB;
	
	public Tuple(final A valueA, final B valueB)
	{
		this.valueA = valueA;
		this.valueB = valueB;
	}
}