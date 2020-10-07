package course.java.sdm.engine;

import java.util.Date;

public class FeedBack {

    private final Integer stars;
    private String feed;
    private Customer Customer;
    private Seller Seller;
    private Order FromOrder;

    public FeedBack(Integer stars, String feed, Customer customer, Seller seller, Order fromOrder) {
        this.stars = stars;
        this.feed = feed;
        Customer = customer;
        Seller = seller;
        FromOrder = fromOrder;
    }

     Integer getStars() {
        return stars;
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
}
