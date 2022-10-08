package main.java.model;

public abstract class AbstractBeam implements Beam {
    private long remainPhotons;

    private final long initSize;

    protected AbstractBeam(long remainPhotons) {
        this.remainPhotons = remainPhotons;
        initSize = remainPhotons;
    }

    @Override
    public boolean hasNextPhoton() {
        return remainPhotons > 0;
    }

    @Override
    public long initSize() {
        return initSize;
    }

    protected void emmitPhoton() {
        remainPhotons--;
    }
}
