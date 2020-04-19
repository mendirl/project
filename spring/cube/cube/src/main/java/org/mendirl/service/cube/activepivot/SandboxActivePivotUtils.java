/*
 * (C) Quartet FS 2007-2009
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot;

import java.util.Random;

/**
 * <b>SandboxActivePivotUtils</b>
 * <p>
 * This class provides some functions (rounding ...)
 *
 * @author Quartet Financial Systems
 */
public class SandboxActivePivotUtils {

    /**
     * round a double
     *
     * @param what    the double to round
     * @param howmuch rounding precision
     * @return double the rounded double
     */
    public static double round(double what, int howmuch) {
        return ((int) (what * Math.pow(10, howmuch) + .5)) / Math.pow(10, howmuch);
    }

    /**
     * nextDouble used to generate a random number in a specific interval
     *
     * @param min  lower boundary
     * @param max  upper boundary
     * @param rand the random to use
     * @return double
     */
    public static double nextDouble(double min, double max, Random rand) {
        double deltaRandom = rand.nextDouble() * (max - min);    // should be in [0, 1) * difference = [0, difference)
        return min + deltaRandom;    // should be in [min, min + difference) = [min, max)
    }

}
