package course.java.sdm.engine;

import course.java.sdm.classesForUI.*;
import course.java.sdm.exceptions.*;
import javax.management.openmbean.*;
import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

class Store implements HasName, Coordinatable,Serializable {

    private final String m_Zone;
    private final Seller m_Seller;
    private final Point m_locationCoordinate;
    private final Long m_StoreID;
    private Double m_profitFromShipping = 0d;
    private final Map<Long,ProductInStore> m_items = new HashMap<>();
    private final Map<Long,Order> m_OrderHistory = new HashMap<>();
    private final Set<Discount> m_Discounts = new HashSet<>();
    private String m_Name;
    private int PPK;

    Store(Long i_serialNumber,Point i_locationCoordinate,String m_Name, int i_PPK,Seller seller,String Zone) {
        this.m_StoreID = i_serialNumber;
        this.m_locationCoordinate = i_locationCoordinate;
        this.PPK=i_PPK;
        this.m_Name = m_Name;
        this.m_Seller=seller;
        this.m_Zone=Zone;
    }

    int getPPK() {
        return PPK;
    }

    long getStoreID() {
        return m_StoreID;
    }

    int getAmountOfItems () {return m_items.size();}

    String getZone() {return m_Zone;}

    Collection getDiscounts () {return m_Discounts;};

    double getProfitFromShipping() {
        return m_profitFromShipping;
    }

    ProductInStore getProductInStoreByID (Long itemID)
    {
            return m_items.get(itemID);
    }

    public Seller getSeller() {
        return m_Seller;
    }

    void addItemToStore (ProductInStore ProductToAdd) throws NegativePriceException {
        Long itemKey = ProductToAdd.getItem().getSerialNumber();

        if (m_items.containsKey(itemKey))
            throw (new KeyAlreadyExistsException("The key for "+ProductToAdd.getItem().getName()+" #"+itemKey+"is Already in Store #"+this.m_StoreID));
        if (ProductToAdd.getPricePerUnit() <=0)
            throw (new NegativePriceException(ProductToAdd.getPricePerUnit()));
        if (ProductToAdd.getStore().getStoreID() != this.getStoreID())
            throw (new IllegalArgumentException("the Product belongs to store #"+ProductToAdd.getStore().getStoreID()+"and does not match store #"+this.getStoreID()));

        m_items.put(itemKey,ProductToAdd);
    }

    void addDiscount (Discount discount) {m_Discounts.add(discount);}

    double getPriceForItem (Long ItemID)
    {
        if (m_items.containsKey(ItemID))
            return (m_items.get(ItemID).getPricePerUnit());
        else
            throw (new InvalidKeyException("item #"+ItemID+" is not in Store"));
    }

    double getPriceForItem (ProductInStore ItemToCheck)
    {
        return getPriceForItem(ItemToCheck.getItem().getSerialNumber());
    }

    boolean isItemInStore (Item ItemToCheck)
    {
        return m_items.containsKey(ItemToCheck.getSerialNumber());
    }

    boolean isItemInStore (Long ItemID)
    {
        return m_items.containsKey(ItemID);
    }

    ProductInStore getItemInStore (long ItemID) {return m_items.get(ItemID);}

    void addOrderToStoreHistory (Order NewOrder)
    {
        if (NewOrder.isStoreInOrder(this))
            m_OrderHistory.put(NewOrder.getOrderSerialNumber(), NewOrder);
        else
            throw (new IllegalArgumentException("Order #"+NewOrder.getOrderSerialNumber()+" does not buy from store #"+this.getStoreID()));
    }

    Double getItemsProfit() {
        Double res = 0d;
        for (Order cur : m_OrderHistory.values()) {
            if (cur.isStatic())
                res+=cur.getItemsPrice();
            else {
                for (ProductInOrder item : cur.getBasket()) {
                    if (this.equals( item.getProductInStore().getStore()))
                        res += item.getPriceOfTotalItems();
                }
            }
        }
        return res;
    }

    @Override
    public String getName() {
        return m_Name;
    }

    @Override
    public void setName(String Input) {
        m_Name = Input;
    }

    @Override
    public Point getCoordinate() {
        return this.m_locationCoordinate;
    }

    void newShippingFromStore (double AmountToAdd)
    {
        m_profitFromShipping += AmountToAdd;
    }

    @Override
    public String toString() { //2.3
        return "Store #" + m_StoreID +
                "\" " + m_Name +
                "\n PPK is: " + PPK +
                " So Far Profit from Shipping is :" + m_profitFromShipping +"\n";
    }

    List<ItemInStoreInfo> getItemList ()
    {
        List<ItemInStoreInfo> res = new ArrayList<>();
            for (ProductInStore curItem : m_items.values()) {
                ItemInStoreInfo newItem = new ItemInStoreInfo(curItem.getSerialNumber(),curItem.getItem().getName(),
                        curItem.getItem().getPayBy().toString(),curItem.getPricePerUnit(),curItem.getAmountSold());
                res.add(newItem);
            }
            return res;
    }

    List<OrderInfo> getOrderHistoryList()
    {
        List<OrderInfo> res = new ArrayList<>();
        List<ItemInOrderInfo> itemsOnlyFromStore;
        List<StoreInOrderInfo> stores;
        CustomerInfo customer;

        for (Order curOrder : m_OrderHistory.values())
        {
            stores = curOrder.getStoreInfo();
            itemsOnlyFromStore = curOrder.getItemsOnlyFromStore(this.m_StoreID);
            customer =  new CustomerInfo(curOrder.getCostumer().getName(),curOrder.getCostumer().getId(),curOrder.getCostumer().getCoordinate(),curOrder.getCostumer().getAvgPriceOfShipping(),curOrder.getCostumer().getAvgPriceOfOrdersWithoutShipping(),curOrder.getCostumer().getAmountOFOrders(),curOrder.getCostumer().getWallet().getWalletInfo());
            OrderInfo newOrder = new OrderInfo(curOrder.getOrderSerialNumber(),curOrder.getDate(),stores
            ,itemsOnlyFromStore,curOrder.getTotalPrice(),curOrder.getShippingPrice()
                    ,curOrder.getItemsPrice(),curOrder.getAmountOfItems(),customer,curOrder.isStatic());
            res.add(newOrder);
        }

        return res;
    }

    List<DiscountInfo> getDiscountsList () {

        List<DiscountInfo> res = new ArrayList<>();
        List<OfferItemInfo> offeredItems;
        OfferItemInfo WhatYouBuy;
        Item Temp;

        for (Discount curDis : m_Discounts)
        {
            offeredItems = new ArrayList<>();
            for (ProductYouGet curItem: curDis.getOffersBasket() )
                offeredItems.add(new OfferItemInfo(curItem.getItem().getSerialNumber(),
                        curItem.getItem().getName(),curItem.getItem().getPayBy().toString(),
                        curItem.getAmountYouGet(),curItem.getPriceToAdd()));
            Temp = curDis.getWhatYouBuy().getItem();
            WhatYouBuy = new OfferItemInfo (Temp.getSerialNumber(),Temp.getName(),
                    Temp.getPayBy().toString(),curDis.getWhatYouBuy().getAmountToBuy(),0.0);

            DiscountInfo newDis = new DiscountInfo (curDis.getDiscountName(),curDis.getOfferType().toString(),WhatYouBuy
            ,curDis.getWhatYouBuy().getAmountToBuy(),offeredItems,this.m_StoreID);
            res.add(newDis);
        }

        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
         return (this.getStoreID() == store.getStoreID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_StoreID);
    }

    void DeleteItem(long itemID) {

        if (!m_items.containsKey(itemID))
            throw new InvalidKeyException("Item #"+itemID+" is not in the store #"+this.m_StoreID);

        List<Discount> removeDis = new ArrayList<>();

        for (Discount cur : m_Discounts) {
            if (cur.getWhatYouBuy().getItem().getSerialNumber() == itemID)
                removeDis.add(cur);
        }
        m_Discounts.removeAll(removeDis);
        m_items.remove(itemID);
    }

    void DeleteDiscount (String DiscountName) {
        Discount disToDel = null;

        for (Discount cur : m_Discounts)
            if (cur.getDiscountName().equals(DiscountName))
                disToDel = cur;

        if (disToDel != null)
            m_Discounts.remove(disToDel);
    }

    void changePrice(long itemID, double newPrice) {
        if (!m_items.containsKey(itemID))
            throw new InvalidKeyException("Item #"+itemID+" is not in the store #"+this.m_StoreID);
        m_items.get(itemID).setPrice(newPrice);
    }

    public List<DiscountInfo> getDiscountsListByItems(Set<ProductInOrder> wantedItemsInStore) {
        List<DiscountInfo> AllDiscount = getDiscountsList();
        List<DiscountInfo> res = new ArrayList<>();
        for (DiscountInfo curDiscount : AllDiscount) {
            for (ProductInOrder curItem :wantedItemsInStore)
                if (curDiscount.itemToBuy.ID.equals(curItem.getSerialNumber()))
                    if (curDiscount.AmountToBuy <= curItem.getAmount()) {
                        curDiscount.setAmountEntitled((int) (curItem.getAmount() / curDiscount.AmountToBuy));
                        res.add(curDiscount);
                    }
        }
        return ConnectDiscount(res);
    }

    public Collection<DiscountInfo> getDiscountsListFilteredByItems(Set<ProductInOrder> wantedItems) {

        List<DiscountInfo> AllDiscount = getDiscountsList();
        List<DiscountInfo> res = new ArrayList<>();
        for (DiscountInfo curDiscount : AllDiscount) {
            for (ProductInOrder curItem : wantedItems)
                if (curItem.getProductInStore().getStore().m_StoreID.equals(this.m_StoreID))
                    if (curDiscount.itemToBuy.ID.equals(curItem.getSerialNumber()))
                        if (curDiscount.AmountToBuy <= curItem.getAmount()) {
                            curDiscount.setAmountEntitled((int) (curItem.getAmount() / curDiscount.AmountToBuy));
                            res.add(curDiscount);
                        }
        }
        return ConnectDiscount(res);
    }

    private List<DiscountInfo> ConnectDiscount(List<DiscountInfo> discounts) {
        if (discounts.size() > 1) {
            for (int i = 0; i < discounts.size(); i++) {
                for (int j=i+1;j<discounts.size();j++) {
                    if (discounts.get(i).itemToBuy.ID.equals(discounts.get(j).itemToBuy.ID)) {
                        discounts.get(i).addListener(discounts.get(j));
                        discounts.get(j).addListener(discounts.get(i));
                    }

                }
            }
        }
        return discounts;
    }

     StoreInfo getStoreInfo() {
        return new StoreInfo(getCoordinate(),
                getStoreID(),
                getProfitFromShipping(),
                null,null,null
                ,getName(),getPPK(),m_Seller.getName(),getItemsProfit());
    }

    int howManyDiscount(Long itemID) {
        int counter = 0;
        if (!m_items.containsKey(itemID))
            return counter;



        for (Discount cur : m_Discounts)
            if (cur.isItemYouBuyInDiscount(itemID))
                counter++;
        return counter;
    }

    public boolean gotFeedFor(Order order) {
        boolean flag = false;
       Customer customer = order.getCostumer();
       for(FeedBack cur : customer. getFeedBacks()) {
           if (cur.getFromOrder().getOrderSerialNumber().equals(order.getOrderSerialNumber())) //feedback for order?
                if(this.m_StoreID.equals(cur.getStore().getStoreID())) //feedback for this store?
                    flag = true;
       }
       return flag;
    }
}
