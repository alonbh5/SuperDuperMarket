package course.java.sdm.engine;

import com.sun.org.apache.xpath.internal.operations.Or;
import course.java.sdm.classesForUI.*;
import course.java.sdm.exceptions.*;
import course.java.sdm.generatedClasses.AreaInfo;
import course.java.sdm.generatedClasses.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainSystem {

    private static long UsersSerialGenerator = 1;
    private static long ItemSerialGenerator = 10;
    private static long StoreSerialGenerator = 100;
    private static long OrdersSerialGenerator = 1000;

    private Map<String,Customer> m_CustomersInSystem = new HashMap<>();
    private Map<String,Seller> m_SellersInSystem = new HashMap<>();

    private List<AreaInfo> Areas = new ArrayList<>();


    public int getAreaVersion () {return Areas.size();}

    public List<CustomerInfo> getListOfAllCustomerInSystem ()  {

        List<CustomerInfo> res = new ArrayList<>();

        for (Customer curCustomer : m_CustomersInSystem.values()){
            CustomerInfo newCustomer = new CustomerInfo(curCustomer.getName(),
                    curCustomer.getIdNumber()
                    ,curCustomer.getCoordinate()
                    ,curCustomer.getAvgPriceOfShipping()
                    ,curCustomer.getAvgPriceOfOrdersWithoutShipping()
                    ,curCustomer.getAmountOFOrders(),
                    curCustomer.getWallet().getWalletInfo());
            res.add(newCustomer);
        }

        return res;
    }

    public synchronized static Long getOrderSerial() {
        return OrdersSerialGenerator++;
    }

    public synchronized static Long getStoreSerial() {
        return StoreSerialGenerator++;
    }

    public synchronized Person addNewSeller(String name) { //todo this is sync
        Seller newSeller = new Seller(UsersSerialGenerator++,name);
        m_SellersInSystem.put(name,newSeller);
        return newSeller;
    }

    public synchronized Person addNewBuyer(String name) { //todo this is sync
        Customer newCustomer = new Customer(UsersSerialGenerator++,name);
        m_CustomersInSystem.put(name,newCustomer);
        return newCustomer;
    }

    public synchronized Collection<Person> getAllUsers() {
        Collection<Person> res = new HashSet<>();
        res.addAll(m_CustomersInSystem.values());
        res.addAll(m_SellersInSystem.values());
        return res;
    }

    public boolean isUserExists(String name) {
        return m_CustomersInSystem.containsKey(name) || m_SellersInSystem.containsKey(name);
    }


    public Set<String> getAllUsersForList() {
        Set<String> res = new HashSet<>();
        for (Customer cur : m_CustomersInSystem.values())
            res.add(cur.getName()+" (Customer)");
        for (Seller cur : m_SellersInSystem.values())
            res.add(cur.getName()+" (Seller)");
        return res;
    }

    final Object key = new Object();
    public void uploadFile(InputStream inputStream, String sellerName) throws DuplicatePointOnGridException, DuplicateItemInStoreException, NoOffersInDiscountException, IllegalOfferException, PointOutOfGridException, DuplicateStoreInSystemException, ItemIsNotSoldAtAllException, StoreItemNotInSystemException, StoreDoesNotSellItemException, NegativePriceException, NoValidXMLException, NegativeQuantityException, DuplicateItemIDException, WrongPayingMethodException, DuplicateZoneException {
        Seller curSeller = m_SellersInSystem.get(sellerName);

        synchronized (key) {
            SuperDuperMarketDescriptor superDuperMarketDescriptor;

            try {
                superDuperMarketDescriptor = FileHandler.UploadFile(inputStream);

            } catch (JAXBException e) {
                throw new NoValidXMLException();
            }

            String Zone = superDuperMarketDescriptor.getSDMZone().getName().trim();

            for (Seller cur : m_SellersInSystem.values())
                if (cur.isZoneInUser(Zone))
                    throw new DuplicateZoneException(cur.getName(), Zone);

            curSeller.UploadInfoFromXML(Zone, superDuperMarketDescriptor);
            Areas.add(curSeller.getAreaInfo(Zone));
        }
    }

    private CustomerInfo createCustomerInfo (Customer user) {
        return new CustomerInfo(user.getName(),user.getId(),user.getCoordinate(),user.getAvgPriceOfShipping(),user.getAvgPriceOfOrdersWithoutShipping(),user.getAmountOFOrders(),user.getWallet().getWalletInfo());
    }


    public synchronized List<AreaInfo> getAreaEntries(int fromIndex) {
        if (fromIndex < 0 || fromIndex > Areas.size()) {
            fromIndex = 0;
        }
        return Areas.subList(fromIndex, Areas.size());
    }

    public synchronized List<AreaInfo> getAllAreaEntries() {
        List<AreaInfo> res = new ArrayList<>();
        for (Seller cur : m_SellersInSystem.values())
            for (String Zone :cur.getZones())
                 res.add(cur.getAreaInfo(Zone));

        return res;
    }

    public synchronized SuperDuperMarketSystem getSDMByZone (String Zone) {
        for (Seller cur : m_SellersInSystem.values())
            if (cur.isZoneInUser(Zone))
                return cur.getAllSuperMarket().get(Zone);

            return null;
    }


    public Collection<FeedBackInfo> getSellerFeedbackByZone(String zone, String curUserName) {
        List<FeedBackInfo> res = new ArrayList<>();
        if (!m_SellersInSystem.containsKey(curUserName))
            return res;
        Seller curSeller = m_SellersInSystem.get(curUserName);
        for (FeedBack curFeed : curSeller.getFeedBacks())
            if (curFeed.isFromZone(zone))
                res.add(new FeedBackInfo(
                        curFeed.getStars(), curFeed.getFeed(),
                        curFeed.getCustomer().getName(),
                        curFeed.getSeller().getName(), curFeed.getDate(),curFeed.getStore().getStoreInfo()));
        return res;
    }

    public WalletInfo getWalletByUser(String name) {

        Wallet UserWallet;
        if (m_SellersInSystem.containsKey(name)) {
            Seller curSeller = m_SellersInSystem.get(name);
            UserWallet = curSeller.getWallet();
        } else {
            if (m_CustomersInSystem.containsKey(name)) {
                Customer curSeller = m_CustomersInSystem.get(name);
                UserWallet = curSeller.getWallet();
            }
            else
                return null;
        }

        return UserWallet.getWalletInfo();
    }

    public Customer getCustomer(String usernameFromParameter) {
        return m_CustomersInSystem.get(usernameFromParameter);
    }

    public List<StoreInOrderInfo> addFeedback (String customerName,FeedBackInfo feedback, String Zone,Long OrderId,Long StoreId) {

        Customer customer = m_CustomersInSystem.get(customerName);
        Order order = customer.getOrderHistory().get(OrderId);
        Store store = getSDMByZone(Zone).getStore(StoreId);
        Date date = order.getDate();
        Seller seller = store.getSeller();
        FeedBack newFeed = new FeedBack(feedback.stars, date, feedback.feed, customer, seller, order, Zone, store);
        seller.addFeedBack(newFeed);
        customer.addFeedBack(newFeed);

        //todo notifaction


        List<StoreInOrderInfo> res = new ArrayList<>();
        for (Store cur : order.getStoreSet())
            if (!cur.gotFeedFor(order))
                res.add(new StoreInOrderInfo(cur.getStoreInfo(), 0d, 0d, 0d, 0));

        return res;
    }

    public void AddStore (StoreInfo storeToAdd,String Zone) throws DuplicatePointOnGridException, StoreDoesNotSellItemException, NegativePriceException, StoreItemNotInSystemException, DuplicateItemInStoreException {

        SuperDuperMarketSystem sdm = getSDMByZone(Zone);
        if (sdm.isLocationTaken(storeToAdd.locationCoordinate))
            throw new DuplicatePointOnGridException(storeToAdd.locationCoordinate);
        if (storeToAdd.Items.isEmpty())
            throw new StoreDoesNotSellItemException(storeToAdd.StoreID);
        Seller seller = m_SellersInSystem.get(storeToAdd.Owner);

        Store newStore = new Store (getStoreSerial(),storeToAdd.locationCoordinate,storeToAdd.Name,
                storeToAdd.PPK,seller,Zone);

        for (ItemInStoreInfo cur : storeToAdd.Items) {

            if (cur.PriceInStore <= 0) {
                throw new NegativePriceException(cur.PriceInStore);
            }
            if (!sdm.isItemInSystem(cur.serialNumber)) {
                throw new StoreItemNotInSystemException(cur.serialNumber, newStore.getStoreID());
            }
            if (newStore.isItemInStore(cur.serialNumber)) {
                throw new DuplicateItemInStoreException(cur.serialNumber);
            }

            Item BaseItem = sdm.getItem(cur.serialNumber);
            ProductInStore newItemForStore = new ProductInStore(BaseItem, cur.PriceInStore, newStore);
            newStore.addItemToStore(newItemForStore);
            ProductInSystem sysItem = sdm.getSysItem(cur.serialNumber);
            sysItem.addSellingStore();
            if (sysItem.getMinSellingStore() == null || cur.PriceInStore < sysItem.getMinSellingStore().getPriceForItem(BaseItem.getSerialNumber()))
                sysItem.setMinSellingStore(newStore);
        }

        seller.addStore(newStore);
        sdm.addStore(newStore);
        //todo notify..
    }


    public void AddMoney(String usernameFromParameter, Double amountToAdd, Date date) {
        Customer customer = m_CustomersInSystem.get(usernameFromParameter);
        customer.addMoney(amountToAdd,date);
    }

    public List<DiscountInfo> addDiscount(String usernameFromParameter, Integer indexOfItemWanted, Integer indexInArray) {

        Customer customer = m_CustomersInSystem.get(usernameFromParameter);
        List<DiscountInfo> discounts = customer.getTempDiscounts();
        discounts.get(indexInArray).setIndexOfWantedItem(indexOfItemWanted);
        discounts.get(indexInArray).addAmountWanted();
        customer.setTempDiscounts(discounts);
        return discounts;
    }
}
