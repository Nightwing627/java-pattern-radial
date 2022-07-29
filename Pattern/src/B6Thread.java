import java.lang.Runnable;
import java.util.ArrayList;

public class B6Thread implements Runnable {

    private double[][][] charges;
    private ChangeMotion motion;

    public void run() {
        motion = new ChangeMotion("Star6B");
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
                                if (points.size() == 3) {       
                                    if (isEnableFor6B(points, newPoint)) {
                                        points.add(newPoint);
                                    }
                                } else {
                                    if (motion.isEnableForStraight(points, newPoint)) {
                                        points.add(newPoint);
                                        ii = 0;
                                        jj = 0;
                                    }
                                }
                                if (points.size() == 5) {
                                    motion.outputSolution(points, motion.getIterationFor6B(), "7");
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
    private boolean isEnableFor6B(ArrayList<int[]> points, int[] lastPoint) {
        // determines whether the last point does not coincide with the four points of a straight
        for (int [] point : points) {
            if (charges[point[0]][point[1]] == charges[lastPoint[0]][lastPoint[1]]) {
                return false;
            }
        }
        int nSize = points.size();
        double[] lastP = charges[lastPoint[0]][lastPoint[1]];
        double[] firstP = charges[points.get(nSize - 1)[0]][points.get(nSize - 1)[1]];
        if (!motion.isCommunicable(firstP, lastP)) {
            return false;
        }
        double[] secondP = charges[points.get(nSize - 2)[0]][points.get(nSize - 2)[1]];
        
        double lastSeg = Math.abs(Math.pow(lastP[0] - firstP[0], 2)) + Math.abs(Math.pow(lastP[1] - firstP[1], 2));
        double firstSeg = Math.abs(Math.pow(firstP[0] - secondP[0], 2)) + Math.abs(Math.pow(firstP[1] - secondP[1], 2));
        
        if (points.size() == 3) {
            if (!motion.isCommunicable(lastP, secondP)) return false;
            double secondSeg = Math.abs(Math.pow(lastP[0] - secondP[0], 2)) + Math.abs(Math.pow(lastP[1] - secondP[1], 2));
            // double angle = 0;
            // try {
            //     angle = Math.acos((lastSeg + secondSeg - firstSeg) / (2 * Math.sqrt(lastSeg) * Math.sqrt(secondSeg))) ;    
            // } catch (ArithmeticException e) {
            //     e.printStackTrace();
            // }
            // if (angle <= 60 && angle > 0) return true;
            if (lastSeg + secondSeg > firstSeg) return true;
        } else if (points.size() == 4) {
            double[] thirdP = charges[points.get(nSize - 3)[0]][points.get(nSize - 3)[1]];
            double thirdSeg = Math.abs(Math.pow(firstP[0] - thirdP[0], 2)) + Math.abs(Math.pow(firstP[1] - thirdP[1], 2));
            double lastSecondSeg = Math.abs(Math.pow(lastP[0] - secondP[0], 2)) + Math.abs(Math.pow(lastP[1] - secondP[1], 2));
            double lasThirdSeg = Math.abs(Math.pow(lastP[0] - thirdP[0], 2)) + Math.abs(Math.pow(lastP[1] - thirdP[1], 2));

            if (lastSeg + firstSeg < lastSecondSeg && lastSecondSeg > 1 
                    && lastSeg + thirdSeg < lasThirdSeg && lasThirdSeg > 1) {
                return true;
            }
        }
        return false;
    }
}
