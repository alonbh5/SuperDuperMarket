package course.java.sdm.engine;

import java.util.Date;

public class FeedBack {

    private final Integer stars;
    private final Date date;
    private String feed;
    private final Customer Customer;
    private final Seller Seller;
    private final Order FromOrder;
    private final String Zone;

    public FeedBack(Integer stars, Date date, String feed, Customer customer, Seller seller, Order fromOrder, String zone) {
        this.stars = stars;
        this.date = date;
        this.feed = feed;
        Customer = customer;
        Seller = seller;
        FromOrder = fromOrder;
        Zone = zone;
    }

     Integer getStars() {
        return stars;
    }

    public String getZone() {
        return Zone;
    }

    String getFeed() {
        return feed;
    }

     course.java.sdm.engine.Customer getCustomer() {
        return Customer;
    }

     course.java.sdm.engine.Seller getSeller() {
        return Seller;
    }

     Order getFromOrder() {
        return FromOrder;
    }

    Date getDate() {
        return FromOrder.getDate();
    }

    boolean isFromZone (String Zone) {
        return this.Zone.equals(Zone);
    }
}
