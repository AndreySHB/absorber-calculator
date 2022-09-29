package main.java.model;

import main.java.util.GeometricUtil;

import static main.java.util.AbsorberUtil.RANDOM;

public class PorousMaterial extends AbstractMaterial {

    private final double n1;

    private final double n2;

    public PorousMaterial(double absCoff, int avgDist, double n1, double n2) {
        super(absCoff, avgDist);
        this.n1 = n1;
        this.n2 = n2;
    }

    @Override
    public double getRandomDistance() {
        double rLength = 0.75 * RANDOM.nextGaussian(avgDist, avgDist);
        while (rLength < 1) {
            rLength = 0.75 * RANDOM.nextGaussian(avgDist, avgDist);
        }
        return  rLength;
    }

    @Override
    public double getRandomTeta() {
        double randomTeta = GeometricUtil.getRandomTeta(n1, n2);
        return RANDOM.nextBoolean() ? randomTeta : -randomTeta;
    }
}
