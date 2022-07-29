import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) {
        startMotion();
    }

    public static void startMotion() {
        ChangeMotion chargeMotion = new ChangeMotion();
        chargeMotion.iteration();
    }
}
