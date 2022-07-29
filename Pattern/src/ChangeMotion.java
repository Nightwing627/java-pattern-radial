import java.io.FilePermission;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChangeMotion {
    private int countRadial = 32;
    private double radius = 50;
    private static int countCharge;

    private final long duration = 50;
    private Timer timer;
    
    
    private double[] centerPoint = {0, 0};
    private static double[][][] charges;
    private static Map<Integer, Map<String, List<String>>> totalData = new HashMap<>();
    /**
     * 
    duplicate, count of Pij (per 100k), 
    {
        total iteration: [
            solution name: [
                point: string,
                iterCount: integer,
                isDuplicate: boolean,

                "4-7, 0-6, 19-7, 31-4, 9-5, 1200, 1",
                ...
            ]
        ]
    }
    */

    private static int iterationForStar;
    private static int iterationFor1;
    private static int iterationFor2A;
    private static int iterationFor2B;
    private static int iterationFor6;
    private static int iterationFor6B;
    private static int iterationFor6C;
    private static int totalIteration;
    
    private ArrayList<Integer> validDirections;
    private static FileOperation fileOperation;

    public ChangeMotion() {
        fileOperation = new FileOperation();
        ChangeMotion.countCharge = fileOperation.getPointsOfEachRadial();
        charges = new double[countRadial][countCharge][2];
        initPosition();
    }

    public ChangeMotion(String method) {}

    /**
     * moving iteration
     */
    public void iteration() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ChangeMotion.this.motion();
            }
        }, 0, duration);
    }

    /**
     * each motion
     */
    private void motion() {
        increaseAll();
        outputThird(totalIteration);
        totalIteration ++;

        if (totalIteration % 100000 == 0) {
            checkFunc();
        }

        for (int i = 0; i < countRadial; i ++) {
            for (int j = 0; j < countCharge; j ++) {
                double [] temp = newPosition(charges[i][j]);
                BigDecimal value = new BigDecimal(temp[0]);
                charges[i][j][0] = Double.valueOf(value.setScale(5, RoundingMode.HALF_UP).toString());
                value = new BigDecimal(temp[1]);
                charges[i][j][1] = Double.valueOf(value.setScale(5, RoundingMode.HALF_UP).toString());
            }
        }
    
        Thread t1 = new Thread(new S1Thread());
        t1.start();    

        // Thread star = new Thread(new StarThread());
        // star.start();
        
        // Thread t2A = new Thread(new A2Thread());
        // t2A.start();

        // Thread t2B = new Thread(new B2Thread());
        // t2B.start();

        // Thread t6 = new Thread(new S6Thread());
        // t6.start();

        // Thread t6B = new Thread(new B6Thread());
        // t6B.start();

        // Thread t6C = new Thread(new C6Thread());
        // t6C.start();
    }

    /**
     * initial position of charges
     */
    private void initPosition() {
        for (int i = 0; i < countRadial; i++) {
            for (int j = 0; j < countCharge; j++) {
                double r = 10 + (j+1) * 40 / countCharge;
                double angle = Math.PI * 2 * (countRadial - i) / countRadial;
                BigDecimal value = new BigDecimal(r * Math.cos(angle) + centerPoint[0]);
                charges[i][j][0] = Double.valueOf(value.setScale(5, RoundingMode.HALF_UP).toString());
                value = new BigDecimal(r * Math.sin(angle) + centerPoint[1]);
                charges[i][j][1] = Double.valueOf(value.setScale(5, RoundingMode.HALF_UP).toString());
            }
        }
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
                pos[1] --;//top
                break;
            case 1:
                pos[0] ++;//right
                break;
            case 2:
                pos[1] ++;//down
                break;
            case 3:
                pos[0] --;//left
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
        double distance = Math.pow((pos1[0] - pos2[0]), 2) + Math.pow((pos1[1] - pos2[1]), 2);
        if (distance <= 1 && distance > 0) {
            return true;
        }

        return false;
    }

    private void increaseAll() {
        setIterationForStar(iterationForStar + 1);
        setiterationFor1(iterationFor1 + 1);
        setIterationFor2A(iterationFor2A + 1);
        setIterationFor2B(iterationFor2B + 1);
        setIterationFor6(iterationFor6 + 1);
        setIterationFor6B(iterationFor6B + 1);
        setIterationFor6C(iterationFor6C + 1);
    }

    public void outputSolution(ArrayList<int[]> indexs, Integer iterCnt, String type) {
        String terminal_output = "Total: " + this.getTotalIteration() + "\n"
                                + "Solution: " + type + "\n";
        String file_1_output = "Total: " + this.getTotalIteration() + "\n";
        String file_2_output = terminal_output;    
        
        String pointStr = "";
        for (int [] i : indexs) {
            pointStr += i[0] + "-" + i[1] + (indexs.get(indexs.size() - 1).equals(i) ? "" : ",");
            terminal_output += "P" + i[0] + "-" + i[1] + ", ";
            file_1_output += "P" + i[0] + "-" + i[1] + ", ";
            file_2_output += "P" + i[0] + "-" + i[1] + ", ";
        }

        terminal_output += "\n\t";
        file_1_output += "\n\t";
        file_2_output += "\n";
        for (int [] i : indexs) {
            double [] point = ChangeMotion.charges[i[0]][i[1]];
            terminal_output += point[0] + ", " + point[1] + 
                    (indexs.get(indexs.size() - 1).equals(i) ? "\n" : "\n\t");
            file_1_output += point[0] + ", " + point[1] + 
                    (indexs.get(indexs.size() - 1).equals(i) ? "\n" : "\n\t");
        }

        System.out.println(terminal_output);
        fileOperation.writeResult(file_1_output, file_2_output);
        storeData(pointStr, iterCnt, type);
        cleanIteration(type);
    }

    /**
     * Store all occured solutions in Map,
     * Check solution's points duplicate with other solutions's points in the same total iteration,
     * Count same type solution in the same total iteration
     * 
     * @param pStr point's string of new solution
     * @param interCnt iteration count of solution's type occurred current
     * @param type current solution's type
     * @return void
     */

    public void storeData(String pStr, Integer iterCnt, String solnName) {
        Integer mainKey = ChangeMotion.totalIteration;
        
        String [] realPoints = pStr.split(",");
        Boolean isDupFlag = false;

        if (totalData.get(mainKey) != null) {
            Map<String, List<String>> mainItem = totalData.get(mainKey);

            // check duplicate
            outer:
            for (String key : mainItem.keySet()) {
                if (key != solnName) {
                    List<String> data = mainItem.get(key);
                    for (int i = 0; i < realPoints.length; i ++) {
                        int count = 0;
                        for (String strItem: data) {
                            if (strItem.contains(realPoints[i])) count ++;
                        }
                        if (count == realPoints.length) {
                            isDupFlag = true;
                            break outer;
                        }
                    }
                }
            }
            
            String strItem = pStr + "," + iterCnt + "," + (isDupFlag ? 1 : 0); 

            if (mainItem.get(solnName) != null) {
                List<String> subItem = mainItem.get(solnName);
                subItem.add(strItem);
                mainItem.replace(solnName, subItem);
                totalData.replace(mainKey, mainItem);
            } else {
                List<String> subItem = new ArrayList<>();
                subItem.add(strItem);
                mainItem.put(solnName, subItem);
                totalData.replace(mainKey, mainItem);
            }
        } else {
            Map<String, List<String>> mainItem = new HashMap<>();
            List<String> data = new ArrayList<>();
            String subItem = pStr + "," + iterCnt + "," + 0;
            data.add(subItem);
            mainItem.put(solnName, data);
            totalData.put(mainKey, mainItem);
        }
    }

    private void cleanIteration(String type) {
        switch(type) {
            case "1":
                setiterationFor1(0); break;
            case "2":
                setIterationFor2A(0); break;
            case "3":
                setIterationFor2B(0); break;
            case "4":
                setIterationForStar(0); break;
            case "6":
                setIterationFor6(0); break;
            case "7":
                setIterationFor6B(0); break;
            case "8":
                setIterationFor6C(0); break;
        }
    }

    /** check the count of Pijs per 100k */
    private void checkFunc() {
        Map<String, Map<String, List<String>>> i = new HashMap<>();
        Map<String, Integer> outData = new HashMap<>();
        for (Integer key: totalData.keySet()) {
            Map<String, List<String>> item = totalData.get(key);
            for (String solnKey: item.keySet()) {

            }
        }
    }


    // output the total iteration for which unique solution occurs
    private void outputThird(Integer mainKey) {
        var result = new Object() { Boolean flag1 = false; Boolean flag2 = false; };

        if (totalData.get(mainKey) != null) {
            result.flag1 = true;
            Map<String, List<String>> solutions = totalData.get(mainKey);    
            
            solutions.entrySet().forEach(entry -> {
                List<String> item = entry.getValue();
                if (item.size() > 1) {
                    result.flag2 = true;
                    return;
                }
            });
        }

        if (result.flag1 && !result.flag2) {
            fileOperation.writeThird(mainKey);
        }
    }

    public double[][][] getCharges() {
        return ChangeMotion.charges;
    }

    public int getCountRadial() {
        return this.countRadial;
    }

    public int getCountCharge() {
        return ChangeMotion.countCharge;
    }

    public int getIterationForStar() {
        return ChangeMotion.iterationForStar;
    }

    public void setIterationForStar(int value) {
        ChangeMotion.iterationForStar = value;
    }

    public int getIterationFor2A() {
        return ChangeMotion.iterationFor2A;
    }

    public void setIterationFor2A(int value) {
        ChangeMotion.iterationFor2A = value;
    }

    public int getIterationFor2B() {
        return ChangeMotion.iterationFor2B;
    }

    public void setIterationFor2B(int value) {
        ChangeMotion.iterationFor2B = value;
    }

    public int getIterationFor6C() {
        return ChangeMotion.iterationFor6C;
    }

    public void setIterationFor6C(int value) {
        ChangeMotion.iterationFor6C = value;
    }

    public int getiterationFor1() {
        return ChangeMotion.iterationFor1;
    }

    public void setiterationFor1(int value) {
        ChangeMotion.iterationFor1 = value;
    }

    public int getIterationFor6() {
        return ChangeMotion.iterationFor6;
    }

    public void setIterationFor6(int value) {
        ChangeMotion.iterationFor6 = value;
    }

    public int getIterationFor6B() {
        return ChangeMotion.iterationFor6B;
    }

    public void setIterationFor6B(int value) {
        ChangeMotion.iterationFor6B = value;
    }

    public int getTotalIteration() {
        return ChangeMotion.totalIteration;
    }

    public void setTotalIteration(int value) {
        ChangeMotion.totalIteration = value;
    }
}
