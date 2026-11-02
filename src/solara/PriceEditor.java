import java.util.Map;

public class PriceEditor {
    private PricingService prices;

    public PriceEditor(PricingService prices) {
        this.prices = prices;
    }

    public void showPriceMenu() {
        boolean editing = true;
        while (editing) {
            System.out.println("\n--- Edit Prices Menu ---");
            System.out.println("1. Edit Room Rates");
            System.out.println("2. Edit Extra Adult Fee");
            System.out.println("3. Edit Extra Child Fee");
            System.out.println("4. Edit Extra Breakfast Fee");
            System.out.println("5. Edit Add-on Prices");
            System.out.println("6. Back to Admin Menu");
            System.out.print("Choose an option: ");

            int choice = InputHelper.readInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter new nightly room rate: ");
                    double newRate = InputHelper.readMoney();
                    break;
                case 2:
                    System.out.print("Enter new Extra Adult Fee: ");
                    prices.setExtraAdultFee(InputHelper.readMoney());
                    break;
                case 3:
                    System.out.print("Enter new Extra Child Fee: ");
                    prices.setExtraChildFee(InputHelper.readMoney());
                    break;
                case 4:
                    System.out.print("Enter new Extra Breakfast Fee: ");
                    prices.setExtraBreakfastFee(InputHelper.readMoney());
                    break;
                case 5:
                    System.out.print("Enter add-on name to update: ");
                    String addonName = InputHelper.readText();
                    System.out.print("Enter new price for " + addonName + ": ");
                    double addonPrice = InputHelper.readMoney();
                    prices.getAddOns().put(addonName, addonPrice);
                    break;
                case 6:
                    editing = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
            if (editing) {
                prices.save();
                System.out.println("Prices updated successfully.");
            }
        }
    }
}
