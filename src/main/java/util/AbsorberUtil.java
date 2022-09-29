package main.java.util;

import java.util.SplittableRandom;

public class AbsorberUtil {
    public static final SplittableRandom RANDOM = new SplittableRandom();

    public static boolean isAbsorbedOnDistance(double length, double absCoff) {
        return 1 - Math.exp(-length * absCoff) > RANDOM.nextDouble();
    }
}
