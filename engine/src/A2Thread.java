import java.lang.Runnable;
import java.util.ArrayList;

public class A2Thread implements Runnable {

    private double[][][] charges;
    private ChargeMotion motion;

    public void run() {
        motion = new ChargeMotion("Star2A");
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

                    for (int ii = 0; ii < motion.getCountRadial(); ii++) {
                        for (int jj = 0; jj < motion.getCountCharge(); jj++) {
                            //  all points be located within r > 10
                            if (Math.abs(charges[ii][jj][0]) >= 10 && Math.abs(charges[ii][jj][1]) >= 10) {
                                int [] newPoint = new int[] {ii, jj};
                                // start to recognize around fourth point in straight
                                if (points.size() % 4 == 0) {       
                                    if (isEnableFor2A(points, newPoint)) {
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
                        motion.output(points, motion.getIterationFor2A(), "2A");
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
    private boolean isEnableFor2A(ArrayList<int[]> points, int[] lastPoint) {
        // determines whether the last point does not coincide with the four points of a straight
        for (int [] point : points) {
            if (charges[point[0]][point[1]] == charges[lastPoint[0]][lastPoint[1]]) {
                return false;
            }
        }
        // All of R, S, T are vertexs of triangle
        int nSize = points.size();
        double[] rPoint = charges[points.get(nSize - 2)[0]][points.get(nSize - 2)[1]];
        double[] sPoint = charges[points.get(nSize - 1)[0]][points.get(nSize - 1)[1]];
        double[] tPoint = charges[lastPoint[0]][lastPoint[1]];

        // judge what the all segments of triangle be within 0-1m
        if (motion.isCommunicable(rPoint, tPoint) && motion.isCommunicable(sPoint, tPoint) ) {
            // judge what the angle is equal or small than 90 deg
            double rsLen = Math.abs(Math.pow(rPoint[0] - sPoint[0], 2)) + Math.abs(Math.pow(rPoint[1] - sPoint[1], 2));
            double stLen = Math.abs(Math.pow(sPoint[0] - tPoint[0], 2)) + Math.abs(Math.pow(sPoint[1] - tPoint[1], 2));
            double trLen = Math.abs(Math.pow(tPoint[0] - rPoint[0], 2)) + Math.abs(Math.pow(tPoint[1] - rPoint[1], 2));
            
            // a^2 + b^2 - c^2 = 2 * a * b * cos(abc);
            double angleTSR = Math.acos((stLen + rsLen - trLen) / (2 * Math.sqrt(stLen) * Math.sqrt(rsLen)));
            if ( (Math.sqrt(rsLen) <= 1 || Math.sqrt(stLen) <= 1 || Math.sqrt(trLen) <= 1) 
                                || (0 <= angleTSR && angleTSR <= 60) ) {
                return true;
            }
        }
        return false;
    }
}
