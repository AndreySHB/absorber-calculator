package main.java.model;

public abstract class AbstractMaterial implements Material {

    public AbstractMaterial(double absCoff, int avgDist) {
        this.absCoff = absCoff;
        this.avgDist = avgDist;
    }

    protected final double absCoff;

    protected final int avgDist;

    public double getAbsCoff() {
        return absCoff;
    }
}
