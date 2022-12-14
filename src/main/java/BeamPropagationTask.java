package main.java;

import main.java.model.Beam;
import main.java.model.Grid;
import main.java.model.Material;
import main.java.model.Photon;

import java.util.concurrent.Callable;

import static main.java.util.AbsorberUtil.isAbsorbedOnDistance;

/**
 * Describes propagation task of a given beam in a given material,
 * stores absorbed photons in a grid,
 * returns the indicatrice of photons passed through
 */
public class BeamPropagationTask implements Callable<int[]> {
    private final Beam beam;

    private final Material material;

    private final Grid grid;

    private final long indicator;

    private final double tetaStep;

    private final int[] lostPhotonsArr = new int[100];

    public BeamPropagationTask(Beam beam, Grid grid, Material material) {
        {
            tetaStep = Math.PI / 100;
            indicator = beam.initSize() / 100;
        }
        this.beam = beam;
        this.material = material;
        this.grid = grid;
    }

    @Override
    public int[] call() {
        for (long i = 0; beam.hasNextPhoton(); i++) {
            Photon photon = beam.getNextPhoton();
            if (grid.isNotInBorders((int) Math.round(photon.getR()), 0)) {
                addToLostPhotons(photon);
                continue;
            }
            while (true) {
                double deltaL = material.getRandomDistance();
                double deltaTeta = material.getRandomTeta();
                photon.updatePosition(deltaL, deltaTeta);
                int r = (int) Math.round(photon.getR());
                int z = (int) Math.round(photon.getZ());
                if (grid.isNotInBorders(r, z)) {
                    addToLostPhotons(photon);
                    break;
                }
                boolean isAbsorbed = isAbsorbedOnDistance(deltaL, material.getAbsCoff());
                if (isAbsorbed) {
                    grid.accumulate(r, z);
                    break;
                }
            }
            countProgress(i);
        }
        return lostPhotonsArr;
    }

    private void addToLostPhotons(Photon photon) {
        double teta = Math.abs(photon.getTeta()) % Math.PI;
        double border = tetaStep;
        for (int i = 0; i < 100; i++) {
            if (teta < border) {
                lostPhotonsArr[i]++;
                break;
            }
            border += tetaStep;
        }
    }

    private void countProgress(long i) {
        if (i % indicator == 0) {
            String name = Thread.currentThread().getName();
            System.out.printf("progress for %s is %d%% %n", name, 100 * i / beam.initSize());
        }
    }
}
