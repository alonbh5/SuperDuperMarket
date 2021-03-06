package course.java.sdm.classesForUI;
import java.awt.*;

public class CustomerInfo {

    public final String name;
    public final Long ID;
    public final String Location;
    public final Double AvgPriceForShipping;
    public final Double AvgPriceForOrderWithoutShipping;
    public final Integer AmountOfOrders;
    public final WalletInfo Wallet;
//todo add feedback..
    public CustomerInfo(String name, long ID, Point location, double avgPriceForShipping, double avgPriceForOrderWithoutShipping, int amountOfOrders,WalletInfo wallet) {
        this.name = name;
        this.ID = ID;
        Location = getLocationString(location);
        AvgPriceForShipping = Double.parseDouble(String.format("%.2f", avgPriceForShipping));
        AvgPriceForOrderWithoutShipping = Double.parseDouble(String.format("%.2f", avgPriceForOrderWithoutShipping));
        AmountOfOrders = amountOfOrders;
        this.Wallet = wallet;
    }

    public String getLocationString (Point Location){
        return ("("+(int)Location.getX()+","+(int)Location.getY()+")");
    }

    public String getName() {
        return name;
    }

    public Long getID() {
        return ID;
    }



    public Double getAvgPriceForShipping() {
        return Double.parseDouble(String.format("%.2f", AvgPriceForShipping));
    }

    public Double getAvgPriceForOrderWithoutShipping() {
        return Double.parseDouble(String.format("%.2f", AvgPriceForOrderWithoutShipping));
    }

    public Integer getAmountOfOrders() {
        return AmountOfOrders;
    }

    @Override
    public String toString() {
        return "Customer #"+ID+
                " (" + name +
                ") , Location at " + Location ;}

}
