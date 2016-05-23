package com.example.poubelleconnetable.utilities;

import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Collection;

/**
 * Created by fabienlenoir on 22/05/2016.
 */
public class CustomReflectionAssert {
    /**
     * Check whether expected object is present in a collection
     *
     * @param msg      msg to display if fail
     * @param expected expected object
     * @param actual   actual object
     */
    public static void reflectionContains(final String msg, final Object expected, final Collection<?> actual) {

        for (final Object item : actual) {

            try {
                ReflectionAssert.assertReflectionEquals(expected, item);
                return;
            } catch (final AssertionFailedError assertionFailedError) {
            }
        }
        Assert.fail(msg);
    }

    /**
     * Check whether expected object is present in a collection
     *
     * @param exptected expected object
     * @param actual    actual object
     */
    public static void reflectionContains(final Object exptected, final Collection<?> actual) {
        reflectionContains("object not found in collection", exptected, actual);
    }

    /**
     * Check expected object is not present in a collection
     *
     * @param msg      msg to display if fail
     * @param expected expected object
     * @param actual   actual object
     */
    public static void reflectionNotContains(final String msg, final Object expected, final Collection<?> actual) {

        for (final Object item : actual) {

            try {
                ReflectionAssert.assertReflectionEquals(expected, item);
                Assert.fail(msg);
            } catch (final AssertionFailedError assertionFailedError) {
            }
        }

    }

    /**
     * Check expected object is not present in a collection
     *
     * @param exptected expected object
     * @param actual    actual object
     */
    public static void reflectionNotContains(final Object exptected, final Collection<?> actual) {
        reflectionNotContains("object found in collection", exptected, actual);
    }
}
