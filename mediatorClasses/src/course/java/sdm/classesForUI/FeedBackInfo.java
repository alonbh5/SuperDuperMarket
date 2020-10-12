package course.java.sdm.classesForUI;

import java.util.Date;

public class FeedBackInfo {

    public final Integer stars;
    public final String feed;
    public final String CustomerName;
    public final String SellerName;
    public final Date DateGiven;

    public FeedBackInfo(Integer stars, String feed, String customerName, String sellerName, Date dateGiven) {
        this.stars = stars;
        this.feed = feed;
        CustomerName = customerName;
        SellerName = sellerName;
        DateGiven = dateGiven;
    }
}
