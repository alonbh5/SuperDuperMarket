package course.java.sdm.engine;

import java.util.Date;
import java.util.Objects;

public class FeedBack {

    private final Integer stars;
    private final Date date;
    private String feed;
    private final Customer Customer;
    private final Seller Seller;
    private final Order FromOrder;
    private final String Zone;
    private final Store Store;

    public FeedBack(Integer stars, Date date, String feed, Customer customer, Seller seller, Order fromOrder, String zone,Store store) {
        this.stars = stars;
        this.date = date;
        this.feed = feed;
        Customer = customer;
        Seller = seller;
        FromOrder = fromOrder;
        Zone = zone;
        Store = store;
    }

    public course.java.sdm.engine.Store getStore() {
        return Store;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedBack feedBack = (FeedBack) o;
        return FromOrder.equals(feedBack.FromOrder) && this.Store.equals(feedBack.getStore());
    }

    @Override
    public int hashCode() {
        return Objects.hash(FromOrder, Zone, Store);
    }
}
