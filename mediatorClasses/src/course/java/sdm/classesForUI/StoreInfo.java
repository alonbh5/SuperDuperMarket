package course.java.sdm.classesForUI;

import java.awt.*;
import java.util.List;


public class StoreInfo {

    public final String locationPoint;
    public final String Owner;
    public final Long StoreID;
    public final Double profitFromShipping;
    public final Double ProfitFromItems;
    public final List<ItemInStoreInfo> Items;
    public final List<OrderInfo> OrderHistory ;
    public final List<DiscountInfo> Discount;
    public final String Name;
    public final Integer PPK;

    public final Point locationCoordinate;

    public StoreInfo(Point locationCoordinate, Long storeID, Double profitFromShipping, List<ItemInStoreInfo> items, List<OrderInfo> orderHistory, List<DiscountInfo> discount, String name, Integer PPK,String owner,Double profitFromItems) {
        this.locationPoint = getPointString(locationCoordinate);
        this.locationCoordinate = locationCoordinate;
        StoreID = storeID;
        this.profitFromShipping = profitFromShipping;
        Items = items;
        OrderHistory = orderHistory;
        Discount = discount;
        Name = name;
        this.PPK = PPK;
        this.Owner = owner;
        this.ProfitFromItems = profitFromItems;
    }

    public boolean isItemIDinStore (long ItemID)
    {
        return Items.stream().anyMatch(t->t.serialNumber == ItemID);
    }

    public static String getPointString (Point locationCoordinate) {
        return ("("+(int)locationCoordinate.getX()+","+(int)locationCoordinate.getY()+")");
    }

    public String getDistanceFromUser (Point UserLocation) {
        return (Double.toString(locationCoordinate.distance(UserLocation)));
    }

    public String getShippingPriceFromUser (Point UserLocation) {
        return (Double.toString(PPK * locationCoordinate.distance(UserLocation)));
    }

    @Override
    public String toString() {
        return "Store #" + StoreID +
                " (" + Name +
                "), at " + locationCoordinate +
                ", PPK is " + PPK ;
    }

    public boolean isOwnerName (String sellerName) {
        return this.Owner.equals(sellerName);
    }
}
