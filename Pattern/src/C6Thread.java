import java.lang.Runnable;
import java.util.ArrayList;

public class C6Thread implements Runnable {

    private double[][][] charges;
    private ChangeMotion motion;

    public void run() {
        motion = new ChangeMotion("Star6C");
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
                                // Determine if a point lies on a straight line
                                if (isEnableFor6C(points, newPoint)) {
                                    points.add(newPoint);
                                    ii = 0;
                                    jj = 0;
                                }
                                if (points.size() == 5) {
                                    motion.outputSolution(points, motion.getIterationFor6C(), "8");
                                    points.clear();
                                    break;
                                }
                            }
                        }
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
    private boolean isEnableFor6C(ArrayList<int[]> points, int[] newPointPos) {
        if (points.size() == 0) return false;
        // start from third Point
        if (points.size() < 2) return true;
        
        double[] newPoint = new double[] {charges[newPointPos[0]][newPointPos[1]][0], charges[newPointPos[0]][newPointPos[1]][1]};
        double[] lastPoint = charges[points.get(points.size() - 1)[0]][points.get(points.size() - 1)[1]];
        
        if (!motion.isCommunicable(lastPoint, newPoint)) {
            return false;
        }

        double[] aPos = charges[points.get(0)[0]][points.get(0)[1]];
        double[] bPos = charges[points.get(1)[0]][points.get(1)[1]];
                
        if (points.size() == 2) {
            // indicate point C (third)
            double abLen = Math.abs(Math.pow(aPos[0] - bPos[0], 2)) + Math.abs(Math.pow(aPos[1] - bPos[1], 2));
            double bcLen = Math.abs(Math.pow(bPos[0] - newPoint[0], 2)) + Math.abs(Math.pow(bPos[1] - newPoint[1], 2));
            double caLen = Math.abs(Math.pow(newPoint[0] - aPos[0], 2)) + Math.abs(Math.pow(newPoint[1] - aPos[1], 2));

            // angle ABC must be 0-90 deg
            if (abLen + bcLen >= caLen) return true;
        } else if (points.size() == 3) {
            // indicate point D (fourth)
            double[] cPos = charges[points.get(2)[0]][points.get(2)[1]];

            double[] oPos = getIntersection(new double[][] {aPos, lastPoint}, new double[][] {bPos, cPos});
            if (oPos == null) return false;

            // if angle BCD is 0-180, the segment AO must small than AD, intersection segment
            double aoLen = Math.sqrt(Math.abs(Math.pow(aPos[0] - oPos[0], 2)) + Math.abs(Math.pow(aPos[1] - oPos[1], 2)));
            double adLen = Math.sqrt(Math.abs(Math.pow(aPos[0] - lastPoint[0], 2)) + Math.abs(Math.pow(aPos[1] - lastPoint[1], 2)));
            
            // angle BCD must be 0-180
            if (adLen >= aoLen) return true;
        } else if (points.size() == 4) { 
            // indicate point E (fifth)
            double[] cPos = charges[points.get(2)[0]][points.get(2)[1]];
            double[] dPos = charges[points.get(3)[0]][points.get(3)[1]];

            double[] oPos = getIntersection(new double[][] {bPos, dPos}, new double[][] {cPos, lastPoint});
            if (oPos == null) return false;

            // if angle CDE is 0-180, the segment BO must small than BD. O is intersection point
            double boLen = Math.sqrt(Math.abs(Math.pow(bPos[0] - oPos[0], 2)) + Math.abs(Math.pow(bPos[1] - oPos[1], 2)));
            double bdLen = Math.sqrt(Math.abs(Math.pow(bPos[0] - dPos[0], 2)) + Math.abs(Math.pow(bPos[1] - dPos[1], 2)));
            
            // angle BCD must be 0-180
            if (bdLen >= boLen) return true;
        }

        return false;
    }


    /**
     *  Finding Intersection o(x, y) of Two Lines AD, BC
     *
     * @param points current four points formed straight
     * @param lastPoint the vertex of triangle
     * @return Boolean
     */
    private double[] getIntersection(double[][] line1, double[][] line2) {
        try {
            // a1 * x + b1 * y + c1 = 0
            // a2 * x + b2 * y + c2 = 0
            // y - y1 = m * (x - x1); => y - m * x - (y1 + m * x1) = 0;
            double [] pos11 = line1[0], pos12 = line1[1];
            double [] pos21 = line2[0], pos22 = line2[1];
            
            double m1 = (pos11[1] - pos12[1]) / (pos11[0] - pos12[0]);      
            double b1 = 1, b2 = 1, a1 = -1 * m1, c1 = -1 * (pos11[1] + m1 * pos11[0]);
            double m2 = (pos21[1] - pos22[1]) / (pos21[0] - pos22[0]);
            double a2 = -1 * m2, c2 = -1 * (pos21[1] + m2 * pos21[0]);

            double oY = (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1);
            double oX = (b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1);

            return new double[] {oX, oY};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
