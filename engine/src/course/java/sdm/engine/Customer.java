package course.java.sdm.engine;
import course.java.sdm.classesForUI.DiscountInfo;
import course.java.sdm.exceptions.OrderIsNotForThisCustomerException;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Customer extends Person implements Coordinatable {

    private Point m_currentLocation;
    private final Map<Long,Order> m_OrderHistory = new HashMap<>();
    private final List<FeedBack> FeedBacks = new ArrayList<>();
    private final Wallet wallet = new Wallet();
    Order m_tempOrder = null;
    List<DiscountInfo> m_tempDiscounts;


    Customer(Long i_IDNumber, String i_Name) {
        super(i_IDNumber, i_Name);
        this.m_currentLocation = new Point();
    }

    Long getIdNumber () {return super.getIDNumber();}

    public void setCurrentLocation (Point i_newLocation)
    {
        this.m_currentLocation = i_newLocation;
    }

    public Wallet getWallet() {
        return wallet;
    }

    void addOrderToHistory (Order newOrder) throws OrderIsNotForThisCustomerException {
        if (newOrder.getCustomerID() != this.getId() || m_OrderHistory.containsKey(newOrder.getOrderSerialNumber()))
            throw new OrderIsNotForThisCustomerException(newOrder.getOrderSerialNumber(),this.getId());

            m_OrderHistory.put(newOrder.getOrderSerialNumber(),newOrder);
    }


    public Map<Long, Order> getOrderHistory() {
        return m_OrderHistory;
    }

    @Override
    public Point getCoordinate() {
        return m_currentLocation;
    }

    double getAvgPriceOfShipping () {
        if (m_OrderHistory.isEmpty())
            return 0;
        return m_OrderHistory.values()
                .stream()
                .mapToDouble(v->v.getShippingPrice())
                .average().getAsDouble();
    }

    double getAvgPriceOfOrdersWithoutShipping () {

        if (m_OrderHistory.isEmpty())
            return 0;
        return m_OrderHistory.values()
                .stream()
                .mapToDouble(v->v.getItemsPrice())
                .average().getAsDouble();
    }

    void addFeedBack (FeedBack FeedbackToAdd) {
        FeedBacks.add(FeedbackToAdd);
    }

    List<FeedBack>  getFeedBacks () {
        return FeedBacks;
    }

    int getAmountOFOrders() {
        return m_OrderHistory.size();
    }

    void addMoney (Double money, Date date) {
        this.wallet.Charge(money,date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return this.getId().equals(customer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIDNumber());
    }

    List<DiscountInfo> getTempDiscounts () {
        return m_tempDiscounts;
    }

    void setTempDiscounts (List<DiscountInfo> discounts) {
        m_tempDiscounts = discounts;
    }
}
