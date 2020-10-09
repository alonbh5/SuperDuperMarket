package course.java.sdm.exceptions;

public class DuplicateZoneException extends  Exception {

    public final String SellerThatHasZone;
    public final String Zone;

    public DuplicateZoneException(String sellerThatHasZone, String zone) {
        SellerThatHasZone = sellerThatHasZone;
        Zone = zone;
    }

    public DuplicateZoneException(String message, String sellerThatHasZone, String zone) {
        super(message);
        SellerThatHasZone = sellerThatHasZone;
        Zone = zone;
    }
}
