package course.java.sdm.engine;

import course.java.sdm.classesForUI.CustomerInfo;
import course.java.sdm.classesForUI.FeedBackInfo;
import course.java.sdm.classesForUI.OrderInfo;
import course.java.sdm.classesForUI.WalletInfo;
import course.java.sdm.exceptions.*;
import course.java.sdm.generatedClasses.AreaInfo;
import course.java.sdm.generatedClasses.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
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

    Object key = new Object();
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
        for (FeedBack curFeed : curSeller.getFeedBacks().values())
            if (curFeed.isFromZone(zone))
                res.add(new FeedBackInfo(
                        curFeed.getStars(), curFeed.getFeed(),
                        curFeed.getCustomer().getName(),
                        curFeed.getSeller().getName(), curFeed.getDate()));
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
}
