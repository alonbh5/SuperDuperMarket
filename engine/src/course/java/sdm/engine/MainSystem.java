package course.java.sdm.engine;

import course.java.sdm.classesForUI.CustomerInfo;
import course.java.sdm.exceptions.*;
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



    public List<CustomerInfo> getListOfAllCustomerInSystem ()  {

        List<CustomerInfo> res = new ArrayList<>();

        for (Customer curCustomer : m_CustomersInSystem.values()){
            CustomerInfo newCustomer = new CustomerInfo(curCustomer.getName(),
                    curCustomer.getIdNumber()
                    ,curCustomer.getCoordinate()
                    ,curCustomer.getAvgPriceOfShipping()
                    ,curCustomer.getAvgPriceOfOrdersWithoutShipping()
                    ,curCustomer.getAmountOFOrders());
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


    public void uploadFile(InputStream inputStream, String sellerName) throws DuplicatePointOnGridException, DuplicateItemInStoreException, NoOffersInDiscountException, IllegalOfferException, PointOutOfGridException, DuplicateStoreInSystemException, ItemIsNotSoldAtAllException, StoreItemNotInSystemException, StoreDoesNotSellItemException, NegativePriceException, NoValidXMLException, NegativeQuantityException, DuplicateItemIDException, WrongPayingMethodException, DuplicateZoneException {
        Seller curSeller = m_SellersInSystem.get(sellerName);


        SuperDuperMarketDescriptor superDuperMarketDescriptor;

        try {
            superDuperMarketDescriptor = FileHandler.UploadFile(inputStream);

        } catch (JAXBException e) {
            throw new NoValidXMLException();
        }

        String Zone = superDuperMarketDescriptor.getSDMZone().getName();

        for (Seller cur : m_SellersInSystem.values())
            if (cur.isZoneInUser(Zone))
                throw new DuplicateZoneException(Zone,cur.getName());

        curSeller.UploadInfoFromXML(Zone,superDuperMarketDescriptor);

    }
}
