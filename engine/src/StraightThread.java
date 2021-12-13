import java.lang.Runnable;
import java.util.ArrayList;

public class StraightThread implements Runnable{

    private double[][][] charges;
    private ChargeMotion motion;

    public void run() {
        motion = new ChargeMotion("straight");
        charges = motion.getCharges();
        validation();
    }

    /**
     * whether 5 charges communicate each other, or not
     */
    private void validation() {
        ArrayList<int[]> points = new ArrayList<>();
        for (int i = 0; i < motion.getCountRadial(); i++) {
            for (int j = 0; j < motion.getCountCharge(); j++) {
                if (Math.abs(charges[i][j][0]) >= 10 && Math.abs(charges[i][j][1]) >= 10) {         // point musb located without 10m
                    int[] firstPoint = new int[2];
                    firstPoint[0] = i;
                    firstPoint[1] = j;
                    points.add(firstPoint);
                    for (int ii = 0; ii < motion.getCountRadial(); ii ++) {
                        for (int jj = 0; jj < motion.getCountCharge(); jj ++) {
                            if (Math.abs(charges[ii][jj][0]) >= 10 && Math.abs(charges[ii][jj][1]) >= 10) {         // point musb located without 10m
                                int[] point = new int[2];
                                point[0] = ii;
                                point[1] = jj;
                                if (motion.isEnableForStraight(points, point)) {
                                    points.add(point);
                                    ii = 0;
                                    jj = 0;
                                }
                            }
                        }
                    }
                    if (points.size() == 5) {
                        motion.output(points, motion.getIterationForStraight(), "Straight");
                    }
                    points.clear();
                }
            }
        }
    }
}
