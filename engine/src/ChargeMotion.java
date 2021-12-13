import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChargeMotion {
    private int countRadial = 32;
    private double radius = 50;
    private static int countCharge;

    private final long duration = 50;
    private Timer timer;
    
    
    private double[] centerPoint = {0, 0};
    private static double[][][] charges;
    private Map<String, Integer> counterForPoint = new HashMap<>();

    private static int iterationForStar;
    private static int iterationForStraight;
    private static int iterationFor2A;
    private static int iterationFor2B;
    private static int iterationFor6;
    private static int iterationFor6B;
    private static int iterationFor6C;
    private static int totalIteration;
    
    private ArrayList<Integer> validDirections;
    private static FileOperation fileOperation;
    

    public ChargeMotion() {
        fileOperation = new FileOperation();
        ChargeMotion.countCharge = fileOperation.getPointsOfEachRadial();
        charges = new double[countRadial][countCharge][2];
        initPosition();
    }

    public ChargeMotion(String method) {}

    /**
     * moving iteration
     */
    public void iteration() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ChargeMotion.this.motion();
            }
        }, 0, duration);
    }

    /**
     * each motion
     */
    private void motion() {
        increaseAll();
        totalIteration ++;

        for (int i = 0; i < countRadial; i++) {
            for (int j = 0; j < countCharge; j++) {
                charges[i][j] = newPosition(charges[i][j]);
            }
        }

        Thread straight = new Thread(new StraightThread());
        straight.start();

        Thread star = new Thread(new StarThread());
        star.start();
        
        Thread t2A = new Thread(new A2Thread());
        t2A.start();

        Thread t2B = new Thread(new B2Thread());
        t2B.start();

        Thread t6 = new Thread(new S6Thread());
        t6.start();

        Thread t6B = new Thread(new B6Thread());
        t6B.start();

        Thread t6C = new Thread(new C6Thread());
        t6C.start();
    }

    /**
     * initial position of charges
     */
    private void initPosition() {
        for (int i = 0; i < countRadial; i++) {
            for (int j = 0; j < countCharge; j++) {
                double r = 10 + (j+1) * 40 / countCharge;
                double angle = Math.PI * 2 * (countRadial - i) / countRadial;
                charges[i][j][0] = r * Math.cos(angle) + centerPoint[0];
                charges[i][j][1] = r * Math.sin(angle) + centerPoint[1];
            }
        }
        //  print();
    }

    /**
     * get new position
     *
     * @param pos current position
     * @return new position
     */
    private double[] newPosition(double[] pos) {
        validDirections = getValidDirections(pos);
        int direction = validDirections.get((int) (Math.random() * validDirections.size()));
        switch (direction) {
            case 0:
                pos[1]--;//top
                break;
            case 1:
                pos[0]++;//right
                break;
            case 2:
                pos[1]++;//down
                break;
            case 3:
                pos[0]--;//left
                break;
        }
        return pos;
    }

    /**
     * get validation directions
     *
     * @param pos current position
     * @return array list of validation direction
     */
    private ArrayList<Integer> getValidDirections(double[] pos) {
        ArrayList<Integer> directions = new ArrayList<>();
        double[] upPos = new double[2];
        double[] rightPos = new double[2];
        double[] downPos = new double[2];
        double[] leftPos = new double[2];
        upPos[0] = pos[0];
        upPos[1] = pos[1] - 1;
        rightPos[0] = pos[0] + 1;
        rightPos[1] = pos[1];
        downPos[0] = pos[0];
        downPos[1] = pos[1] + 1;
        leftPos[0] = pos[0] - 1;
        leftPos[1] = pos[1];
        if (validationPosition(upPos)) {
            directions.add(0);
        }
        if (validationPosition(rightPos)) {
            directions.add(1);
        }
        if (validationPosition(downPos)) {
            directions.add(2);
        }
        if (validationPosition(leftPos)) {
            directions.add(3);
        }

        return directions;
    }

    /**
     * position is in certain area or not
     *
     * @param pos charge position
     * @return if position is certain, true
     */
    private boolean validationPosition(double[] pos) {
        if (Math.pow((pos[0] - centerPoint[0]), 2) + Math.pow((pos[1] - centerPoint[1]), 2) < Math.pow(radius, 2)) {
            return true;
        }
        return false;
    }

    /**
     * for straight line, current point is enable or not
     *
     * @param points   already existing group of points enable straight)
     * @param newPointPos point to add into group
     * @return if enable , true
     */
    public boolean isEnableForStraight(ArrayList<int[]> points, int[] newPointPos) {
        if (points.size() == 0) {
            return false;
        }

        double[] newPoint = new double[] {charges[newPointPos[0]][newPointPos[1]][0], charges[newPointPos[0]][newPointPos[1]][1]};
        double[] lastPoint = charges[points.get(points.size() - 1)[0]][points.get(points.size() - 1)[1]];
        if (!isCommunicable(lastPoint, newPoint)) {
            return false;
        }

        if (points.size() < 2) {
            return true;
        }

        for (int[] item : points) {
            if (item[0] == newPointPos[0] && item[1] == newPointPos[1]) return false;
        }
        
        double[] firstP = charges[points.get(points.size() - 2)[0]][points.get(points.size() - 2)[1]];
        double[] lastP = charges[points.get(points.size() - 1)[0]][points.get(points.size() - 1)[1]];
        
        // get all triangle's segment's length
        double segmentLen1 = Math.abs(Math.pow(firstP[0] - lastP[0], 2)) + Math.abs(Math.pow(firstP[1] - lastP[1], 2));
        double segmentLen2 = Math.abs(Math.pow(lastP[0] - newPoint[0], 2)) + Math.abs(Math.pow(lastP[1] - newPoint[1], 2));
        double segmentLen3 = Math.abs(Math.pow(newPoint[0] - firstP[0], 2)) + Math.abs(Math.pow(newPoint[1] - firstP[1], 2));

        // when interior angles >= 90, recognized as a straight. ex: angle ABC, BCD, CDE
        // double angle = 0;
        // try {
        //     angle = Math.acos((segmentLen1 + segmentLen2 - segmentLen3) / (2 * Math.sqrt(segmentLen1) * Math.sqrt(segmentLen2))) ;    
        // } catch (ArithmeticException e) {
        //     e.printStackTrace();
        // }
        // 180 >= angle && angle >= 120
        if (segmentLen3 > segmentLen1 + segmentLen2) {
            return true;
        }
        return false;
    }

    /**
     * whether distance between two point is less than 1, or not
     *
     * @param pos1 point1
     * @param pos2 point2
     * @return if less than 1, true
     */
    public boolean isCommunicable(double[] pos1, double[] pos2) {
        if (pos1[0] == pos2[0] && pos1[1] == pos2[1]) {
            return false;
        }
        if (Math.pow((pos1[0] - pos2[0]), 2) + Math.pow((pos1[1] - pos2[1]), 2) <= 1) {
            return true;
        }
        return false;
    }

    private void increaseAll() {
        setIterationForStar(iterationForStar + 1);
        setIterationForStraight(iterationForStraight + 1);
        setIterationFor2A(iterationFor2A + 1);
        setIterationFor2B(iterationFor2B + 1);
        setIterationFor6(iterationFor6 + 1);
        setIterationFor6B(iterationFor6B + 1);
        setIterationFor6C(iterationFor6C + 1);
    }

    public void output(ArrayList<int[]> indexs, Integer iterCnt, String type) {
        // counterForPoint
        try {
            String outStr = "";
            // if (ChargeMotion.totalIteration > 100000) {
            //     outStr += "------------- Counter Report -------------- \n";
            //     for (var entry : this.counterForPoint.entrySet()) {
            //         outStr += "\t" + entry.getKey() + " = " + entry.getValue() + "\n";
            //     }
            //     outStr += "------------- Counter Clear -------------- \n";
            //     this.counterForPoint.clear();
            // }

            if (iterCnt != 0) {
                outStr = type + " Iteration : " + iterCnt + "\n"
                        + "Total Iteration : " + ChargeMotion.totalIteration + "\n";
            }

            for (int[] i : indexs) {
                double [] point = charges[i[0]][i[1]];
                String key = point[0] + "-" + point[1];
                Integer cnt = this.counterForPoint.get(key);
                if (cnt != null) {
                    this.counterForPoint.replace(key, cnt + 1);
                } else {
                    this.counterForPoint.put(key, 1);
                }

                outStr += "P" + i[0] + "-" + i[1];
                outStr += indexs.get(indexs.size() - 1).equals(i) ? "\n" : ", ";
            }

            for (int[] i : indexs) {
                double [] point = charges[i[0]][i[1]];
                outStr += "\t" + point[0] + "," + point[1] + "\n";
            }
            writeFile(outStr, iterCnt);
            cleanIteration(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeFile(String str, Integer iterationCnt) {
        System.out.print(str);
        fileOperation.write(str);
        if (iterationCnt != 0) {
            fileOperation.writeResult(iterationCnt + "\n");
        }
        
    }

    private void cleanIteration(String type) {
        switch(type) {
            case "Star":
                setIterationForStar(0); break;
            case "Straight":
                setIterationForStraight(0); break;
            case "2A":
                setIterationFor2A(0); break;
            case "2B":
                setIterationFor2B(0); break;
            case "6":
                setIterationFor6(0); break;
            case "6B":
                setIterationFor6B(0); break;
            case "6C":
                setIterationFor6C(0); break;
        }
    }

    public double[][][] getCharges() {
        return ChargeMotion.charges;
    }

    public int getCountRadial() {
        return this.countRadial;
    }

    public int getCountCharge() {
        return ChargeMotion.countCharge;
    }

    public int getIterationForStar() {
        return ChargeMotion.iterationForStar;
    }

    public void setIterationForStar(int value) {
        ChargeMotion.iterationForStar = value;
    }

    public int getIterationFor2A() {
        return ChargeMotion.iterationFor2A;
    }

    public void setIterationFor2A(int value) {
        ChargeMotion.iterationFor2A = value;
    }

    public int getIterationFor2B() {
        return ChargeMotion.iterationFor2B;
    }

    public void setIterationFor2B(int value) {
        ChargeMotion.iterationFor2B = value;
    }

    public int getIterationFor6C() {
        return ChargeMotion.iterationFor6C;
    }

    public void setIterationFor6C(int value) {
        ChargeMotion.iterationFor6C = value;
    }

    public int getIterationForStraight() {
        return ChargeMotion.iterationForStraight;
    }

    public void setIterationForStraight(int value) {
        ChargeMotion.iterationForStraight = value;
    }

    public int getIterationFor6() {
        return ChargeMotion.iterationFor6;
    }

    public void setIterationFor6(int value) {
        ChargeMotion.iterationFor6 = value;
    }

    public int getIterationFor6B() {
        return ChargeMotion.iterationFor6B;
    }

    public void setIterationFor6B(int value) {
        ChargeMotion.iterationFor6B = value;
    }

    public int getTotalIteration() {
        return ChargeMotion.totalIteration;
    }

    public void setTotalIteration(int value) {
        ChargeMotion.totalIteration = value;
    }
}
