import java.lang.Runnable;
import java.util.ArrayList;

public class B2Thread implements Runnable {

    private double[][][] charges;
    private ChargeMotion motion;

    public void run() {
        motion = new ChargeMotion("Star2B");
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
                //  all points be located within r > 10
                if (Math.abs(charges[i][j][0]) >= 10 && Math.abs(charges[i][j][1]) >= 10) {
                    points.add(new int[] {i, j});   // set begining point's position

                    for (int ii = 0; ii < motion.getCountRadial(); ii ++) {
                        for (int jj = 0; jj < motion.getCountCharge(); jj ++) {
                            //  all points be located within r > 10
                            if (Math.abs(charges[ii][jj][0]) >= 10 && Math.abs(charges[ii][jj][1]) >= 10) {
                                int [] newPoint = new int[] {ii, jj};
                                // start to recognize around fourth point in straight
                                if (points.size() % 4 == 0) {       
                                    if (isEnableFor2B(points, newPoint)) {
                                        points.add(newPoint);
                                    }
                                } else {
                                    // Determine if a point lies on a straight line
                                    if (motion.isEnableForStraight(points, newPoint)) {
                                        points.add(newPoint);
                                        ii = 0;
                                        jj = 0;
                                    }
                                }
                            }
                        }
                    }

                    if (points.size() == 5) {
                        motion.output(points, motion.getIterationFor2B(), "2B");
                    }
                    points.clear();
                }
                
            }
        }
    }

    /**
     *  determine triangle
     *
     * @param points current four points formed straight
     * @param lastPoint the vertex of triangle
     * @return Boolean
     */
    private boolean isEnableFor2B(ArrayList<int[]> points, int[] lastPoint) {
        // determines whether the last point does not coincide with the four points of a straight
        for (int [] point : points) {
            if (charges[point[0]][point[1]] == charges[lastPoint[0]][lastPoint[1]]) {
                return false;
            }
        }
        // All of L, M, N, O are vertexs of both triangle
        double[] lPos = charges[points.get(1)[0]][points.get(1)[1]];
        double[] mPos = charges[points.get(2)[0]][points.get(2)[1]];
        double[] nPos = charges[points.get(3)[0]][points.get(3)[1]];
        double[] oPos = charges[lastPoint[0]][lastPoint[1]];

        // judge what the all segments of triangle be within 0-1m
        if (motion.isCommunicable(lPos, oPos) && motion.isCommunicable(mPos, oPos) 
                && motion.isCommunicable(nPos, oPos)) {

            // judge what the angle is equal or small than 90 deg
            double lmLen = Math.abs(Math.pow(lPos[0] - mPos[0], 2)) + Math.abs(Math.pow(lPos[1] - mPos[1], 2));
            double mnLen = Math.abs(Math.pow(mPos[0] - nPos[0], 2)) + Math.abs(Math.pow(mPos[1] - nPos[1], 2));
            double noLen = Math.abs(Math.pow(nPos[0] - oPos[0], 2)) + Math.abs(Math.pow(nPos[1] - oPos[1], 2));
            double olLen = Math.abs(Math.pow(oPos[0] - lPos[0], 2)) + Math.abs(Math.pow(oPos[1] - lPos[1], 2));
            double omLen = Math.abs(Math.pow(oPos[0] - mPos[0], 2)) + Math.abs(Math.pow(oPos[1] - mPos[1], 2));

            if (Math.round(olLen) == Math.round(noLen) 
                    || ((omLen + lmLen) <= olLen && (omLen + mnLen) >= noLen)
                    || ((omLen + lmLen) >= olLen && (omLen + mnLen) <= noLen)) {
                return true;
            }
        }
        return false;
    }
}
