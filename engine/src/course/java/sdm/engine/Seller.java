package course.java.sdm.engine;

import course.java.sdm.exceptions.*;
import course.java.sdm.generatedClasses.AreaInfo;
import course.java.sdm.generatedClasses.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.*;

public class Seller extends Person {

    private final List<String> Zones= new ArrayList<>();
    private final Map<String,SuperDuperMarketSystem> AllSuperMarket = new HashMap<>(); //by zone..
    private final Map<Long,Store> Stores = new HashMap<>();
    private final Map<Customer,FeedBack> FeedBacks = new HashMap<>();
    private final Wallet wallet = new Wallet();

    public Map<String, SuperDuperMarketSystem> getAllSuperMarket() {
        return AllSuperMarket;
    }

    public Map<Long, Store> getStores() {
        return Stores;
    }

    public Map<Customer, FeedBack> getFeedBacks() {
        return FeedBacks;
    }

    public Wallet getWallet() {
        return wallet;
    }

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

    public void UploadInfoFromXML (String Zone,SuperDuperMarketDescriptor superDuperMarketDescriptor) throws NoValidXMLException, IllegalOfferException, DuplicateItemIDException, WrongPayingMethodException, NoOffersInDiscountException, DuplicateItemInStoreException, ItemIsNotSoldAtAllException, StoreDoesNotSellItemException, DuplicateStoreInSystemException, DuplicatePointOnGridException, NegativePriceException, PointOutOfGridException, NegativeQuantityException, StoreItemNotInSystemException {

        SuperDuperMarketSystem newArea = new SuperDuperMarketSystem(Zone,this);
        LoadXml loader = new LoadXml(superDuperMarketDescriptor,this,newArea);
        this.AllSuperMarket.put(Zone,newArea); //will not add if the file no good
    }

    boolean isZoneInUser (String zone) {
        return AllSuperMarket.containsKey(zone);
    }

    AreaInfo getAreaInfo(String Zone) {
        SuperDuperMarketSystem cur = AllSuperMarket.get(Zone);
        return new AreaInfo(this.getName(),cur.getZone(),cur.getAmountOfItemsInSystem(),cur.getAmountOfStoresInSystem()
        ,cur.getAmountOfOrdersInSystem(),cur.getAvgOrderPrice());
    }
}
