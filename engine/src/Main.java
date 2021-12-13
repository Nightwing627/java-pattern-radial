import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) {
        startMotion();
    }

    public static void startMotion() {
        ChargeMotion chargeMotion = new ChargeMotion();
        chargeMotion.iteration();
    }
}
