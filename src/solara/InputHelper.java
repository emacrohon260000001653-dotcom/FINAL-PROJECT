package solara;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Safe keyboard input. Keeps asking until the user types something valid. */
public class InputHelper {
    private static final Scanner SC = new Scanner(System.in);

    public static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = SC.nextLine().trim();

            if (!answer.isEmpty()) {
                return answer;
            } else {
                System.out.println("  Error: this field cannot be empty.");
            }
        }
    }

    public static String readOptional(String prompt) {
        System.out.print(prompt);
        String answer = SC.nextLine().trim();
        return answer;
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String answer = SC.nextLine().trim();

            try {
                int number = Integer.parseInt(answer);

                if (number >= min && number <= max) {
                    return number;
                } else {
                    System.out.println(
                            "  Error: enter a number from " + min + " to " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("  Error: please enter a whole number.");
            }
        }
    }

    public static BigDecimal readMoney(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = SC.nextLine().trim();

            try {
                BigDecimal amount = new BigDecimal(answer);

                if (amount.signum() > 0) {
                    return amount;
                } else {
                    System.out.println("  Error: amount must be more than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println(
                        "  Error: enter a valid amount (example: 1500.00).");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String answer = SC.nextLine().trim();

            try {
                LocalDate date = LocalDate.parse(answer);
                return date;
            } catch (DateTimeParseException e) {
                System.out.println(
                        "  Error: use the format YYYY-MM-DD, e.g. 2026-12-24.");
            }
        }
    }

    public static boolean confirm(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String answer = SC.nextLine().trim();

            if (answer.equalsIgnoreCase("Y")) {
                return true;
            } else if (answer.equalsIgnoreCase("N")) {
                return false;
            } else {
                System.out.println("  Please type Y or N.");
            }
        }
    }

    public static void pause() {
        System.out.print("\nPress ENTER to continue...");
        SC.nextLine();
    }
}
