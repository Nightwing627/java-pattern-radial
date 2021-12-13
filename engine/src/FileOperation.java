import java.io.*;
import java.util.Date;

public class FileOperation {
    private String inputFile;
    private String outputFile;
    private String outputFileResult;
    private String fileName;
    private BufferedWriter bwOrignal;
    private BufferedWriter bwResult;

    public FileOperation() {
        this.inputFile = "./src/input.txt";
        setFileName();
        this.outputFile = "./src/output/"+ fileName + ".txt";;
        this.outputFileResult = "./src/output/"+ fileName + "-result.txt";
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
     * append string to output file
     *
     * @param str string to append
     */
    public void write(String str) {
        try {
            bwOrignal = new BufferedWriter(new FileWriter(outputFile, true));
            bwOrignal.append(str);
            bwOrignal.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * append string to Result file
     *
     * @param str string to append
     */
    public void writeResult(String str) {
        try {
            bwResult = new BufferedWriter(new FileWriter(outputFileResult, true));
            bwResult.append(str);
            bwResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFileName() {
        Date date = new Date();
        fileName = (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " " + date.getHours()  + date.getMinutes()+ date.getSeconds();
    }

}
