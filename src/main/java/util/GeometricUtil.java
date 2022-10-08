package main.java.util;

import java.util.Arrays;
import java.util.OptionalInt;

import static java.lang.Math.*;
import static main.java.util.AbsorberUtil.RANDOM;

public class GeometricUtil {

    /**
     * simulate photon scattering in medium with refractive index = n1 on a spherical particle with refractive index = n2,
     * the case of multiple inner reflection is considered
     * @return scattered angle
     */
    public static double getRandomTeta(double n1, double n2) {
        double teta = 0;
        boolean isIn;
        double yHit = sqrt(RANDOM.nextDouble());
        double xHit = sqrt(1 - yHit * yHit);
        double kRectang = yHit / xHit;
        double bRectang;
        double tetaIncident = atan(kRectang);
        double pReflected;
        double tetaTrans;
        double kTrans;
        double bTrans;
        double deltaTeta = 0;
        double kReflected;
        double b;
        double c;
        double x0;
        double xHit1;
        double xHit2;
        double xHitNew = 0;
        double yHitNew = 0;
        double kIncident = 0;
        double bIncident = 0;
        if ((n1 / n2 > 1) && (tetaIncident > asin(n2 / n1))) {
            pReflected = 1;
        } else {
            tetaTrans = asin((n1 / n2) * sin(tetaIncident));
            pReflected = ((sin(tetaIncident - tetaTrans) / sin(tetaIncident + tetaTrans)));
            pReflected = pReflected * pReflected;
            deltaTeta = abs(tetaTrans - tetaIncident);
        }
        if (RANDOM.nextDouble() < pReflected) {
            isIn = false;
            kReflected = tan(2 * tetaIncident);
            teta = kReflected > 0 ? PI - atan(kReflected) : -atan(kReflected);
        } else {
            isIn = true;
            kTrans = tan(tetaIncident - asin((n1 / n2) * sin(tetaIncident)));
            bTrans = yHit - xHit * kTrans;
            double tmp = kTrans * kTrans + 1;
            b = 2 * kTrans * bTrans / tmp;
            c = (bTrans * bTrans - 1) / tmp;
            tmp = sqrt(b * b - 4 * c);
            xHit1 = (-b + tmp) / 2;
            xHit2 = (-b - tmp) / 2;
            xHitNew = xHit2 * xHit1 / xHit;
            yHitNew = sqrt(1 - xHitNew * xHitNew);
            x0 = -bTrans / kTrans;
            if (abs(x0) < 1) {
                bTrans = -kTrans * x0;
            }
            kIncident = kTrans;
            bIncident = bTrans;
        }
        while (isIn) {
            xHit = xHitNew;
            yHit = yHitNew;
            double kTangent = -xHit / sqrt(1 - xHit * xHit);
            double bTangent = yHit - kTangent * xHit;
            kRectang = yHit / xHit;
            bRectang = 0;
            if (RANDOM.nextDouble() > pReflected) {
                double tetaTrans1 = atan(kIncident) + deltaTeta;
                double tetaTrans2 = atan(kIncident) - deltaTeta;
                bTangent++;
                double kTrans1 = tan(tetaTrans1);
                double kTrans2 = tan(tetaTrans2);
                double bTrans1 = yHit - kTrans1 * xHit;
                double bTrans2 = yHit - kTrans2 * xHit;
                double x1 = (bTrans1 - bTangent) / (kTangent - kTrans1);
                double x2 = (bTrans2 - bTangent) / (kTangent - kTrans2);
                double y1 = kTrans1 * x1 + bTrans1;
                double y2 = kTrans2 * x2 + bTrans2;
                double xRect = (bRectang - bTangent) / (kTangent - kRectang);
                double yRect = kRectang * xRect + bRectang;
                double tmp1 = pow(xRect - x1, 2) + pow(yRect - y1, 2);
                double tmp2 = pow(xRect - x2, 2) + pow(yRect - y2, 2);
                tetaTrans = tetaTrans1;
                if ((n2 > n1) && (tmp2 >= tmp1)) tetaTrans = tetaTrans2;
                if ((n2 <= n1) && (tmp2 < tmp1)) tetaTrans = tetaTrans2;
                kTrans = tan(tetaTrans);
                bTrans = yHit - kTrans * xHit;
                double delta = 0.001;
                teta = abs(atan(kTrans));
                if (kTrans * (xHit + delta) + bTrans > sqrt(1 - pow(xHit + delta, 2))) {
                    teta = PI - teta;
                }
                isIn = false;
            } else {
                double bInd = bTangent + 1;
                double xCross1 = (bIncident - bInd) / (kTangent - kIncident);
                double xCross2 = (bRectang - bInd) / (kTangent - kRectang);
                double xNew = 2 * xCross2 - xCross1;
                double yNew = xNew * kTangent * bInd;
                kReflected = (yNew - yHit) / (xNew - xHit);
                double bReflected = yHit - kReflected * xHit;
                double tmp = kReflected * kReflected + 1;
                b = 2 * kReflected * bReflected / tmp;
                c = (bReflected * bReflected - 1) / tmp;
                tmp = sqrt(b * b - 4 * c);
                xHit1 = (-b + tmp) / 2;
                xHit2 = (-b - tmp) / 2;
                xHitNew = xHit2 * xHit1 / xHit;
                yHitNew = sqrt(1 - xHitNew * xHitNew);
                x0 = -bReflected / kReflected;
                if (abs(x0) < 1) {
                    kReflected = tan(PI - atan(kReflected));
                    bReflected = -kReflected * x0;
                }
                kIncident = kReflected;
                bIncident = bReflected;
            }
        }
        return teta;
    }

    public static void main(String[] args) {
        int steps = 100;
        double step = PI / steps;
        int counts = 10_000_000;
        double cosSumm = 0;
        double tetaSumm = 0;
        double n1 = 1;
        double n2 = 1.33;
        int[] indicatrice = new int[steps + 1];
        for (int i = 0; i < counts; i++) {
            double teta = getRandomTeta(n1, n2);
            int t = 1;
            while (teta > step * t) {
                t++;
            }
            indicatrice[t]++;
            tetaSumm += teta;
            cosSumm += cos(teta);
        }
        System.out.println("tetaSumm= " + tetaSumm);
        System.out.println("cosSumm= " + cosSumm);
        for (int j : indicatrice) {
            System.out.println(j);
        }
        OptionalInt reduce = Arrays.stream(indicatrice).reduce(Integer::sum);
        System.out.println(reduce.getAsInt());
    }
}
