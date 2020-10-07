package course.java.sdm.engine;

import course.java.sdm.classesForUI.CustomerInfo;
import course.java.sdm.exceptions.NoValidXMLException;
import course.java.sdm.generatedClasses.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBException;
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
}
