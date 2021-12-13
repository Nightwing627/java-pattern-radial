import java.lang.Runnable;
import java.util.ArrayList;

public class S6Thread implements Runnable {

    private double[][][] charges;
    private ChargeMotion motion;

    public void run() {
        motion = new ChargeMotion("Star6");
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
                if (Math.abs(charges[i][j][0]) >= 10 || Math.abs(charges[i][j][1]) >= 10) {
                    points.add(new int[] {i, j});   // set begining point's position

                    for (int ii = 0; ii < motion.getCountRadial(); ii++) {
                        for (int jj = 0; jj < motion.getCountCharge(); jj++) {
                            //  all points be located within r > 10
                            if (Math.abs(charges[ii][jj][0]) >= 10 || Math.abs(charges[ii][jj][1]) >= 10) {
                                int [] newPoint = new int[] {ii, jj};
                                // start to recognize around fourth point in straight
                                if (points.size() % 4 == 0) {       
                                    if (isEnableFor6(points, newPoint)) {
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

                    if (points.size() >= 5) {
                        motion.output(points, motion.getIterationFor6(), "6");
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
    private boolean isEnableFor6(ArrayList<int[]> points, int[] lastPoint) {
        // determines whether the last point does not coincide with the four points of a straight
        for (int [] point : points) {
            if (charges[point[0]][point[1]] == charges[lastPoint[0]][lastPoint[1]]) {
                return false;
            }
        }

        int nSize = points.size();
        double[] hP = charges[points.get(nSize - 1)[0]][points.get(nSize - 1)[1]];
        double[] gP = charges[points.get(nSize - 2)[0]][points.get(nSize - 2)[1]];
        double[] iP = charges[points.get(nSize - 3)[0]][points.get(nSize - 3)[1]];
        double[] fP = charges[lastPoint[0]][lastPoint[1]];

        // judge what the all segments of triangle be within 0-1m
        if (motion.isCommunicable(gP, fP)) {
            // judge what the angle is equal or small than 90 deg
            double igLen = Math.abs(Math.pow(iP[0] - gP[0], 2)) + Math.abs(Math.pow(iP[1] - gP[1], 2));
            double gfLen = Math.abs(Math.pow(gP[0] - fP[0], 2)) + Math.abs(Math.pow(gP[1] - fP[1], 2));
            double ghLen = Math.abs(Math.pow(gP[0] - hP[0], 2)) + Math.abs(Math.pow(gP[1] - hP[1], 2));
            double fiLen = Math.abs(Math.pow(fP[0] - iP[0], 2)) + Math.abs(Math.pow(fP[1] - iP[1], 2));
            double fhLen = Math.abs(Math.pow(fP[0] - hP[0], 2)) + Math.abs(Math.pow(fP[1] - hP[1], 2));
            
            if ((igLen + gfLen) < fiLen && (gfLen + ghLen) < fhLen && fiLen > 1 && fhLen > 1) {
                return true;
            } 
        }
        return false;
    }
}
