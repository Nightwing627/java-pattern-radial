import java.io.*;
import java.util.Date;

public class FileOperation {
    private String inputFile;
    private String file_output_1;
    private String file_output_2;
    private String file_output_3;
    private String fileName;
    private BufferedWriter bwOrignal;
    private BufferedWriter bwResult;

    public FileOperation() {
        this.inputFile = "./src/input.txt";
        setFileName();
        this.file_output_1 = "./src/output/"+ this.fileName + "_1.txt";
        this.file_output_2 = "./src/output/"+ this.fileName + "_2.txt";
        this.file_output_3 = "./src/output/"+ this.fileName + "_3.txt";
    }

    /**
     * get points of a radial
     *
     * @return number of points
     */
    public int getPointsOfEachRadial() {
        int points = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            points = Integer.parseInt(br.readLine());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return points;
    }

    /**
     * append string to Result file
     *
     * @param str string to append
     */
    public void writeResult(String result_1, String result_2) {
        try {
            bwResult = new BufferedWriter(new FileWriter(file_output_1, true));
            bwResult.append(result_1);
            bwResult.close();

            bwResult = new BufferedWriter(new FileWriter(file_output_2, true));
            bwResult.append(result_2);
            bwResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeThird(Integer totalIteration) {
        try {
            bwResult = new BufferedWriter(new FileWriter(file_output_3, true));
            bwResult.append(totalIteration + "\n");
            bwResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFileName() {
        Date date = new Date();
        this.fileName = (date.getYear() + 1900) + "-" 
                    + (date.getMonth() + 1) + "-" 
                    + date.getDate() + " " 
                    + date.getHours() 
                    + date.getMinutes() 
                    + date.getSeconds();
    }

}
