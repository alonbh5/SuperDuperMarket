package course.java.sdm.engine;

import course.java.sdm.classesForUI.*;
import course.java.sdm.exceptions.*;
import course.java.sdm.generatedClasses.*;
import javax.management.openmbean.*;
import java.awt.*;
import java.util.*;
import java.util.List;

//todo check all throws
public class SuperDuperMarketSystem {
    //todo access modifier this is the only public!
    //todo make serializable interface?

    public static final int MAX_COORDINATE = 50;
    public static final int MIN_COORDINATE = 1;
    private static long ItemSerialGenerator = 10000;
    private static long StoreSerialGenerator = 300000;
    private static long OrdersSerialGenerator = 5000000; //todo thing about what to do - XML chooses the Serial somethinms

    private Map<Long,ProductInSystem> m_ItemsInSystem = new HashMap<>(); //todo array or map
    private Map<Point,Coordinatable> m_SystemGrid = new HashMap<>(); //all the shops
    private Map<Long,Order> m_OrderHistory = new HashMap<>(); //all the shops
    private Map<Long,Store> m_StoresInSystem = new HashMap<>(); //Todo - Merge this shit
    private boolean locked = true;


    static double CalculatePPK(Store FromStore, Point curLocation)
    {
        return (double)FromStore.getPPK() * FromStore.getCoordinate().distance(curLocation);
    }

    public static double CalculatePPK(StoreInfo FromStore, Point curLocation)
    {
        return (double)FromStore.PPK * FromStore.locationCoordinate.distance(curLocation);
    }

    public double CalculatePPK (Long FromStoreID, Point curLocation)
    {
        return CalculatePPK(getStoreByID(FromStoreID),curLocation);
    }

    public void AddNewItem (Item newItem)
    {
        if (m_ItemsInSystem.containsKey(newItem.getSerialNumber()))
            throw (new KeyAlreadyExistsException("The Item Serial Number " + newItem.getSerialNumber() + " Exist already in system "));

        ProductInSystem newProductSystem = new ProductInSystem(newItem);
        m_ItemsInSystem.put(newItem.getSerialNumber(),newProductSystem);
    }

    private void UpdateShippingProfitAfterOrder(Order newOrder) {
        Set<Store> AllStoresFromOrder = newOrder.getStoreSet();

        for (Store curStore : AllStoresFromOrder) {
            curStore.newShippingFromStore(CalculatePPK(curStore,newOrder.getCoordinate()));
        }
    }

    public void AddNewStore (Store newStore)
    {
        if (m_SystemGrid.containsKey(newStore.getCoordinate()))
            throw (new KeyAlreadyExistsException("There is a Store at Coordinate (" + newStore.getCoordinate() + ") in system "));
        if (m_StoresInSystem.containsKey(newStore.getStoreID()))
            throw (new KeyAlreadyExistsException("The Serial Number " + newStore.getStoreID() + " Already Exist in system "));
        if (!isCoordinateInRange(newStore.getCoordinate()))
            throw (new PointOutOfGridException(newStore.getCoordinate()));

        m_SystemGrid.put(newStore.getCoordinate(),newStore);
        m_StoresInSystem.put(newStore.getStoreID(),newStore);
    }

    private Store getStoreByID (Long StoreID)
    {
        if (isStoreInSystem(StoreID))
            return m_StoresInSystem.get(StoreID);
        throw  ( new InvalidKeyException("Store #"+StoreID+" is not in System"));
    }

    public boolean isItemInSystem (long i_serialNumber)
    {
        return m_ItemsInSystem.containsKey(i_serialNumber);
    }

    public boolean isStoreInSystem (long i_serialNumber)
    {
        return m_StoresInSystem.containsKey(i_serialNumber);
    }

    public boolean isOrderInSystem (long i_serialNumber)
    {
        return m_OrderHistory.containsKey(i_serialNumber);
    }

    public boolean isLocationTaken (Point pointInGrid)
    {
        return m_SystemGrid.containsKey(pointInGrid);
    }

    public int getAmountOfItemsInSystem ()
    {
        return m_ItemsInSystem.size();
    }

    public int getAmountOfOrdersInSystem ()
    {
        return m_OrderHistory.size();
    }

    public int getAmountOfStoresInSystem ()
    {
        return m_SystemGrid.size();
    }

    public static boolean isCoordinateInRange(Point Coordinate)
    {
        return (((Coordinate.x <= MAX_COORDINATE) && (Coordinate.x >= MIN_COORDINATE) && ((Coordinate.y <= MAX_COORDINATE) && (Coordinate.y >= MIN_COORDINATE))));
    }

    public Item.payByMethod getPayingMethod (Long itemID)
    {
        if (!isItemInSystem(itemID))
            throw (new RuntimeException("Item Key #"+itemID+" is not is System"));

        return (m_ItemsInSystem.get(itemID).getItem().PayBy);

    }

    public double getAvgPriceForItem (Long ItemID)
    {
        return Arrays.stream(m_StoresInSystem.values().stream()
                .filter(t -> t.isItemInStore(ItemID))
                .mapToDouble(t -> t.getPriceForItem(ItemID)).toArray())
                .average().getAsDouble();
    }

    public void addItemToStore (Long StoreID,ProductInStore productToAdd)
    {
        try {
            Store storeToAddTo = m_StoresInSystem.get(StoreID);
            storeToAddTo.addItemToStore(productToAdd);
            m_ItemsInSystem.get(productToAdd.getSerialNumber()).addSellingStore();
        }
        catch (Exception e){
            //todo catch
        }
    }

    private void addProductToOrder (Long OrderID,ProductInOrder productToAdd)
    {
        try {
            Order OrderToAddTo = m_OrderHistory.get(OrderID);
            OrderToAddTo.addProductToOrder(productToAdd);
            m_ItemsInSystem.get(productToAdd.getSerialNumber()).addTimesSold(productToAdd.getAmountByPayingMethod());
            //if 2 shampoo => +2 , if 4.5 apples => +1
        }
        catch (Exception e){
            //todo catch
        }
    }


    public List<StoreInfo> getListOfAllStoresInSystem () throws NoValidXMLException
    {
        if (locked)
            throw new NoValidXMLException();

        StringBuilder str = new StringBuilder();
        List<StoreInfo> res = new ArrayList<>();

        for (Store CurStore : m_StoresInSystem.values()){
            List<ItemInStoreInfo> items = CurStore.getItemList();
            List<OrdersInStoreInfo> orders = CurStore.getOrderHistoryList();

            StoreInfo newStore = new StoreInfo(CurStore.getCoordinate(),CurStore.getStoreID(),
                    CurStore.getName(),CurStore.getPPK(),items,orders, CurStore.getProfitFromShipping());
            res.add(newStore);
        }


        return res;
    }

    public List<ItemInfo> getListOfAllItems () throws NoValidXMLException
    {
        if (locked)
            throw new NoValidXMLException();

        StringBuilder str = new StringBuilder();
        List<ItemInfo> res = new ArrayList<>();

        for (ProductInSystem curItem : m_ItemsInSystem.values())
        {
            Item theItemObj = curItem.getItem();
            ItemInfo newItem = new ItemInfo(theItemObj.getSerialNumber(),theItemObj.getName()
                    ,theItemObj.PayBy.toString(),getAvgPriceForItem(curItem.getSerialNumber()),
                            curItem.getNumberOfSellingStores(),curItem.getAmountOfItemWasSold());
            res.add(newItem);
        }

        return res;
    }

    public List<OrderInfo> getListOfAllOrderInSystem() throws NoValidXMLException
    {
        if (locked)
            throw new NoValidXMLException();

        List<OrderInfo> res = new ArrayList<>();

        for (Order CurOrder : m_OrderHistory.values()) {

            Set<Store> stores = CurOrder.getStoreSet();
            List<String> storesList = new ArrayList<>();

            Set<ProductInOrder> Items = CurOrder.getBasket();
            List<ItemInOrderInfo> itemsInOrder = new ArrayList<>();

            for (Store curStore : stores)
                storesList.add("Store Name: "+curStore.getName()+ " #"+curStore.getStoreID());

            for (ProductInOrder curProd : Items)
                itemsInOrder.add(new ItemInOrderInfo(curProd.getSerialNumber(),curProd.getProductInStore().getItem().getName(),
                        curProd.getPayBy().toString(),curProd.getProductInStore().getStore().getStoreID()
                        ,curProd.getAmountByPayingMethod(),curProd.getPriceOfTotalItems()));

            OrderInfo newOrder = new OrderInfo(CurOrder.getOrderSerialNumber(),CurOrder.getDate(),
                    storesList,itemsInOrder,CurOrder.getTotalPrice(),CurOrder.getShippingPrice(),CurOrder.getItemsPrice(),CurOrder.getAmountOfItems());

            res.add(newOrder);
        }

        return res;
    }

    public boolean UploadInfoFromXML (String XMLPath) throws DuplicatePointOnGridException,DuplicateItemIDException,DuplicateItemInStoreException
    ,DuplicateStoreInSystemException,ItemIsNotSoldAtAllException,NegativePriceException,PointOutOfGridException,
            StoreDoesNotSellItemException,StoreItemNotInSystemException,WrongPayingMethodException,NoValidXMLException//todo throw exception from method...
    {
        SuperDuperMarketDescriptor superDuperMarketDescriptor = InfoLoader.UploadFile(XMLPath);
        CopyInfoFromXMLClasses(superDuperMarketDescriptor);
        return !locked;
    }

    private void CopyInfoFromXMLClasses(SuperDuperMarketDescriptor superDuperMarketDescriptor) {

        Map<Long,ProductInSystem> tempItemsInSystem = m_ItemsInSystem;
        Map<Point,Coordinatable> tempSystemGrid = m_SystemGrid;
        Map<Long,Store> tempStoresInSystem = m_StoresInSystem;
        Map<Long,Order> tempOrderInSystem = m_OrderHistory;

        m_ItemsInSystem = new HashMap<>();
        m_SystemGrid = new HashMap<>();
        m_StoresInSystem = new HashMap<>();
        m_OrderHistory = new HashMap<>();

        try {

            for (SDMItem Item : superDuperMarketDescriptor.getSDMItems().getSDMItem()) {
                if (!isItemInSystem(Item.getId()))
                    crateNewItemInSystem(Item);
                else throw new DuplicateItemIDException(Item.getId());
            }

            for (SDMStore Store : superDuperMarketDescriptor.getSDMStores().getSDMStore()) {
                if (!isStoreInSystem(Store.getId()))
                    crateNewStoreInSystem(Store);
                else throw new DuplicateStoreInSystemException(Store.getId());
            }
            checkMissingItem();

        } catch (RuntimeException e) //if from any reason xml was bas - restore data
        {
            m_ItemsInSystem= tempItemsInSystem;
            m_SystemGrid = tempSystemGrid;
            m_StoresInSystem=tempStoresInSystem;
            m_OrderHistory = tempOrderInSystem;
            throw e;
        }

        locked = false;
    }

    private void checkMissingItem() {
        for (ProductInSystem curItem : m_ItemsInSystem.values())
            if (curItem.getNumberOfSellingStores() == 0)
                throw new ItemIsNotSoldAtAllException(curItem.getSerialNumber(),curItem.getItem().getName());
    }

    private void crateNewStoreInSystem(SDMStore store) {
        Point StoreLocation = new Point(store.getLocation().getX(),store.getLocation().getY());
        if (!isCoordinateInRange(StoreLocation))
            throw new PointOutOfGridException(StoreLocation);
        if (isLocationTaken(StoreLocation))
            throw new DuplicatePointOnGridException(StoreLocation);

        Store newStore = new Store ((long)store.getId(),StoreLocation,store.getName(),store.getDeliveryPpk());

        for (SDMSell curItem : store.getSDMPrices().getSDMSell()){
            Long ItemID = (long)curItem.getItemId();
            double itemPrice = curItem.getPrice();

            if (itemPrice<=0)
                throw new NegativePriceException(itemPrice);
            if (!isItemInSystem(ItemID))
                throw new StoreItemNotInSystemException(ItemID);
            if (newStore.isItemInStore(ItemID))
                throw new DuplicateItemInStoreException(ItemID);

            Item BaseItem = m_ItemsInSystem.get(ItemID).getItem();
            ProductInStore newItemForStore = new ProductInStore(BaseItem,itemPrice,newStore);
            newStore.addItemToStore(newItemForStore);
            m_ItemsInSystem.get(ItemID).addSellingStore();
        }

        if (newStore.getItemList().isEmpty())
            throw new StoreDoesNotSellItemException(newStore.getStoreID());

        m_StoresInSystem.put(newStore.getStoreID(),newStore);
        m_SystemGrid.put(newStore.getCoordinate(),newStore);
    }

    private void crateNewItemInSystem(SDMItem item) {
        Item.payByMethod ePayBy;

        if (item.getPurchaseCategory().equals("Weight"))
            ePayBy = Item.payByMethod.WEIGHT;
        else
            if (item.getPurchaseCategory().equals("Quantity"))
                 ePayBy = Item.payByMethod.AMOUNT;
            else
                throw new WrongPayingMethodException(item.getPurchaseCategory());

        Item newBaseItem = new Item ((long)item.getId(),item.getName(),ePayBy);
        ProductInSystem newItem = new ProductInSystem(newBaseItem);
        m_ItemsInSystem.put(newItem.getSerialNumber(),newItem);
    }

    public boolean isItemSoldInStore(Long storeID,Long itemID) {
        Store store = getStoreByID(storeID);
        return store.isItemInStore(itemID);
    }

    public double getItemPriceInStore(long storeID, long ItemID) {
            Store store = getStoreByID(storeID);
           return store.getPriceForItem(ItemID);
    }

    public StoreInfo GetStoreInfoByID (Long storeID) throws InvalidKeyException
    {
        if (isStoreInSystem(storeID))
            {
                Store curStore = m_StoresInSystem.get(storeID);
                List<ItemInStoreInfo> items = curStore.getItemList();
                List<OrdersInStoreInfo> orders = curStore.getOrderHistoryList();

                return new StoreInfo(curStore.getCoordinate(),curStore.getStoreID(),
                        curStore.getName(),curStore.getPPK(),items,orders, curStore.getProfitFromShipping());

            }
        throw  (new InvalidKeyException("Store #"+storeID+" is not in System"));

    }

    public ItemInfo getItemInfo(long itemID) {
        if (isItemInSystem(itemID))
        {
            ProductInSystem curItem = m_ItemsInSystem.get(itemID);
            Item theItemObj = curItem.getItem();
            ItemInfo res = new ItemInfo(theItemObj.getSerialNumber(),theItemObj.getName()
                    ,theItemObj.PayBy.toString(),getAvgPriceForItem(curItem.getSerialNumber()),
                    curItem.getNumberOfSellingStores(),curItem.getAmountOfItemWasSold());
            return res;
        }
        return null;
    }

    public void addStaticOrderToSystem(Collection<ItemInOrderInfo> itemsChosen, StoreInfo storeChosen, Point curLoc, Date OrderDate) {

        if (!isCoordinateInRange(curLoc))
            throw (new PointOutOfGridException(curLoc));
        if (m_SystemGrid.containsKey(curLoc))
            throw (new KeyAlreadyExistsException("There is a store at "+ curLoc.toString()));
        if (!m_StoresInSystem.containsKey(storeChosen.StoreID))
            throw (new RuntimeException("Store ID #"+storeChosen.StoreID+" is not in System"));
        if (itemsChosen.isEmpty())
            throw (new RuntimeException("Empty List"));

        Store curStore = m_StoresInSystem.get(storeChosen.StoreID);
        while (m_OrderHistory.containsKey(OrdersSerialGenerator))
            OrdersSerialGenerator++;

        Order newOrder = new Order (curLoc,OrdersSerialGenerator++,OrderDate);

        for (ItemInOrderInfo curItem : itemsChosen) {
            if (!curStore.isItemInStore(curItem.serialNumber))
                throw (new StoreDoesNotSellItemException(curItem.serialNumber));
            if (curItem.amountBought <= 0)
                throw (new RuntimeException("Amount is not Allowed.." + curItem.amountBought));

            ProductInStore curProd = curStore.getProductInStoreByID(curItem.serialNumber);
            ProductInOrder newItem = new ProductInOrder(curProd);
            newItem.setAmountBought(curItem.amountBought);
            //todo check here also and combine if you got 2 shampoo (from the same store only!) - remember this is static order
            newOrder.addProductToOrder(newItem);
        }

        for (ProductInOrder curItem :newOrder.getBasket())
            m_ItemsInSystem.get(curItem.getSerialNumber()).addTimesSold(1);

        m_OrderHistory.put(newOrder.getOrderSerialNumber(),newOrder);
        UpdateShippingProfitAfterOrder(newOrder);
    }
}
