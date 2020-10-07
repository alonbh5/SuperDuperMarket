package course.java.sdm.engine;

import course.java.sdm.exceptions.*;
import course.java.sdm.generatedClasses.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBException;
import java.util.*;

public class Seller extends Person {

    private final List<String> Zones= new ArrayList<>();
    private final Map<String,SuperDuperMarketSystem> AllSuperMarket = new HashMap<>(); //by zone..
    private final Map<Long,Store> Stores = new HashMap<>();
    private final Map<Customer,FeedBack> FeedBacks = new HashMap<>();

    public Seller(long i_IDNumber, String i_Name) {
        super(i_IDNumber, i_Name);
    }

    Integer getAmountOfStore(){
        return Stores.size();
    }

    Integer getAmountOfFeedBacks(){
        return FeedBacks.size();
    }

    Collection<String> getZones() {
        return Zones;
    }

    void addStore (Store storeToAdd){
        Stores.put(storeToAdd.getStoreID(),storeToAdd);
    }

    void addFeedBack (FeedBack FeedbackToAdd) {
        FeedBacks.put(FeedbackToAdd.getCustomer(),FeedbackToAdd);
    }

    boolean isCustomerGaveFeedBack (Customer customer) {
        return FeedBacks.containsKey(customer);
    }

    public void UploadInfoFromXML (String XMLPath) throws NoValidXMLException, IllegalOfferException, DuplicateItemIDException, WrongPayingMethodException, NoOffersInDiscountException, DuplicateItemInStoreException, ItemIsNotSoldAtAllException, StoreDoesNotSellItemException, DuplicateStoreInSystemException, DuplicatePointOnGridException, NegativePriceException, PointOutOfGridException, NegativeQuantityException, StoreItemNotInSystemException {
        SuperDuperMarketDescriptor superDuperMarketDescriptor;

        try {
            superDuperMarketDescriptor = FileHandler.UploadFile(XMLPath);

        } catch (JAXBException e) {
            throw new NoValidXMLException();
        }

        String Zone = superDuperMarketDescriptor.getSDMZone().getName();
        SuperDuperMarketSystem newArea = new SuperDuperMarketSystem(Zone,this);
        LoadXml loader = new LoadXml(superDuperMarketDescriptor,this,newArea);

    }
}
