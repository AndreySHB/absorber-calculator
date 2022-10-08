package main.java.model;

import static main.java.util.AbsorberUtil.RANDOM;

public class GaussBeam extends AbstractBeam {

    private final double meanRadius;

    public GaussBeam(long photonAmount, double meanRadius) {
        super(photonAmount);
        this.meanRadius = meanRadius;
    }

    private double getRandomR() {
        return Math.abs(RANDOM.nextGaussian(0, meanRadius / 2));
    }

    @Override
    public Photon getNextPhoton() {
        emmitPhoton();
        return new Photon(getRandomR());
    }
}
