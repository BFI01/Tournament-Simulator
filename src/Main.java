import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] myArr = new int[32];
        for (int i = 0; i < myArr.length; i++) {
            myArr[i] = i;
        }
        System.out.println(Arrays.toString(myArr));
        System.out.println(myArr.length);

        for (int i : myArr) {
            if (i % 5 == 0) {
                System.out.println(i + " is divisible by 5");
            }
        }
    }
}
