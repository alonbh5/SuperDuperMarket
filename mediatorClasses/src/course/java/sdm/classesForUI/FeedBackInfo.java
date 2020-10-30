package course.java.sdm.classesForUI;

import java.util.Date;

public class FeedBackInfo {

    public final Integer stars;
    public final String feed;
    public final String CustomerName;
    public final String SellerName;
    public final Date DateGiven;
    public final StoreInfo Store;

    public FeedBackInfo(Integer stars, String feed, String customerName, String sellerName, Date dateGiven,StoreInfo store) {
        this.stars = stars;
        this.feed = feed;
        CustomerName = customerName;
        SellerName = sellerName;
        DateGiven = dateGiven;
        Store = store;
    }

    public FeedBackInfo(Integer stars, String feed) {
        this.stars = stars;
        this.feed = feed;
        CustomerName = null;
        SellerName = null;
        DateGiven = null;
        Store = null;
    }
}
