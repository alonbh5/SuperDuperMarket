package course.java.sdm.engine;
import course.java.sdm.exceptions.*;
import course.java.sdm.generatedClasses.*;
import javafx.concurrent.Task;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LoadXml {

    private final SuperDuperMarketDescriptor superDuperMarketDescriptor;
    private final SuperDuperMarketSystem MainSys;
    private final Seller MainUploader;

    Map<Long,ProductInSystem> ItemsInSystem = new HashMap<>();
    Map<Point,Coordinatable> SystemGrid = new HashMap<>();
    Map<Long,Store> StoresInSystem = new HashMap<>();
    Map<Long,Order> OrderHistory = new HashMap<>();

     LoadXml(SuperDuperMarketDescriptor superDuperMarketDescriptor, Seller Uploader,SuperDuperMarketSystem sys) throws IllegalOfferException, DuplicateItemInStoreException, WrongPayingMethodException, DuplicateItemIDException, ItemIsNotSoldAtAllException, StoreDoesNotSellItemException, DuplicateStoreInSystemException, DuplicatePointOnGridException, NegativePriceException, PointOutOfGridException, NegativeQuantityException, NoOffersInDiscountException, StoreItemNotInSystemException {
        this.superDuperMarketDescriptor = superDuperMarketDescriptor;
         MainUploader = Uploader;
         MainSys = sys;
         copyInfoFromXMLClasses(superDuperMarketDescriptor);
    }


    private boolean copyInfoFromXMLClasses(SuperDuperMarketDescriptor superDuperMarketDescriptor) throws DuplicateItemIDException, WrongPayingMethodException, IllegalOfferException, NoOffersInDiscountException, DuplicateItemInStoreException, StoreDoesNotSellItemException, DuplicatePointOnGridException, NegativePriceException, PointOutOfGridException, NegativeQuantityException, StoreItemNotInSystemException, DuplicateStoreInSystemException, ItemIsNotSoldAtAllException {

            String Zone = superDuperMarketDescriptor.getSDMZone().getName();

            for (SDMItem Item : superDuperMarketDescriptor.getSDMItems().getSDMItem()) {
                if (!ItemsInSystem.containsKey((long)Item.getId()))
                    crateNewItemInSystem(Item);
                else {
                    throw new DuplicateItemIDException(Item.getId());
                }
            }


            for (SDMStore Store : superDuperMarketDescriptor.getSDMStores().getSDMStore()) {
                if (!StoresInSystem.containsKey((long)Store.getId()))
                    crateNewStoreInSystem(Store,Zone);
                else {
                    throw new DuplicateStoreInSystemException(Store.getId());
                }
            }

            checkMissingItem();
            MainSys.setNewSystemFromFile(ItemsInSystem,SystemGrid,StoresInSystem,OrderHistory);
            return true;
    }

    private void crateNewItemInSystem(SDMItem item) throws WrongPayingMethodException {
            Item.payByMethod ePayBy;

            if (item.getPurchaseCategory().equals("Weight"))
                ePayBy = Item.payByMethod.WEIGHT;
            else
            if (item.getPurchaseCategory().equals("Quantity"))
                ePayBy = Item.payByMethod.AMOUNT;
            else {
                throw new WrongPayingMethodException(item.getPurchaseCategory());
            }

            Item newBaseItem = new Item ((long)item.getId(),item.getName().trim(),ePayBy);
            ProductInSystem newItem = new ProductInSystem(newBaseItem);
            ItemsInSystem.put(newItem.getSerialNumber(),newItem);
        }


    private void crateNewStoreInSystem(SDMStore store,String Zone) throws PointOutOfGridException, DuplicatePointOnGridException, NegativePriceException, StoreItemNotInSystemException, DuplicateItemInStoreException, StoreDoesNotSellItemException, IllegalOfferException, NegativeQuantityException, NoOffersInDiscountException {
        Point StoreLocation = new Point(store.getLocation().getX(), store.getLocation().getY());
        if (!SuperDuperMarketSystem.isCoordinateInRange(StoreLocation)) {
            throw new PointOutOfGridException(StoreLocation);}
        if (SystemGrid.containsKey(StoreLocation)) {
            throw new DuplicatePointOnGridException(StoreLocation);}

        Store newStore = new Store((long) store.getId(), StoreLocation, store.getName().trim(), store.getDeliveryPpk(),MainUploader,Zone);
        ProductInSystem sysItem;

        for (SDMSell curItem : store.getSDMPrices().getSDMSell()) {
            Long ItemID = (long) curItem.getItemId();
            double itemPrice = curItem.getPrice();

            if (itemPrice <= 0) {
                throw new NegativePriceException(itemPrice);
            }
            if (!ItemsInSystem.containsKey(ItemID)) {
                throw new StoreItemNotInSystemException(ItemID, newStore.getStoreID());
            }
            if (newStore.isItemInStore(ItemID)) {
                throw new DuplicateItemInStoreException(ItemID);
            }

            Item BaseItem = ItemsInSystem.get(ItemID).getItem();
            ProductInStore newItemForStore = new ProductInStore(BaseItem, itemPrice, newStore);
            newStore.addItemToStore(newItemForStore);
            sysItem = ItemsInSystem.get(ItemID);
            sysItem.addSellingStore();
            if (sysItem.getMinSellingStore() == null || itemPrice < sysItem.getMinSellingStore().getPriceForItem(BaseItem.getSerialNumber()))
                sysItem.setMinSellingStore(newStore);

        }

        if (newStore.getItemList().isEmpty()) {
            throw new StoreDoesNotSellItemException(newStore.getStoreID());
        }

        if (store.getSDMDiscounts() != null)
            for (SDMDiscount curDis : store.getSDMDiscounts().getSDMDiscount()) {
                if (!newStore.isItemInStore((long) curDis.getIfYouBuy().getItemId())) {
                    throw new StoreDoesNotSellItemException("Item of Discount is not sold at store", curDis.getIfYouBuy().getItemId());
                }
                if (curDis.getIfYouBuy().getQuantity() < 0) {
                    throw new NegativeQuantityException((int)curDis.getIfYouBuy().getQuantity());
                }
                if (curDis.getThenYouGet().getSDMOffer().isEmpty()) {
                    throw new NoOffersInDiscountException(curDis.getName());
                }

                String DisOp = curDis.getThenYouGet().getOperator().trim().toUpperCase();

                if (!DisOp.equals("ONE-OF") && !DisOp.equals("ALL-OR-NOTHING") && !DisOp.equals("IRRELEVANT")){
                    throw new IllegalOfferException(curDis.getName());
                }



                Discount.OfferType newOp = Discount.OfferType.IRRELEVANT;
                if (curDis.getThenYouGet().getOperator().trim().toUpperCase().equals("ONE-OF"))
                    newOp = Discount.OfferType.ONE_OF;
                if (curDis.getThenYouGet().getOperator().trim().toUpperCase().equals("ALL-OR-NOTHING"))
                    newOp = Discount.OfferType.ALL_OR_NOTHING;
                if (newOp.equals(Discount.OfferType.IRRELEVANT) && curDis.getThenYouGet().getSDMOffer().size() != 1) {
                    throw new IllegalOfferException(curDis.getName());}

                Item curItem = ItemsInSystem.get((long) curDis.getIfYouBuy().getItemId()).getItem();
                ProductYouBuy whatYouBuy = new ProductYouBuy(curItem, curDis.getIfYouBuy().getQuantity());
                Discount newDis = new Discount(newOp, curDis.getName().trim(), whatYouBuy);

                for (SDMOffer offer : curDis.getThenYouGet().getSDMOffer()) {
                    if (offer.getForAdditional() < 0) {
                        throw new NegativePriceException(offer.getForAdditional());}
                    if (!newStore.isItemInStore((long) offer.getItemId())) {
                        throw new StoreDoesNotSellItemException("Item of Discount is not sold at store", curDis.getIfYouBuy().getItemId());}
                    if (offer.getQuantity() < 0) {
                        throw new NegativeQuantityException((int)offer.getQuantity());}

                    Item itemForCtor = ItemsInSystem.get((long) offer.getItemId()).getItem();

                    newDis.AddProductYouGet(new ProductYouGet(itemForCtor, offer.getQuantity(), (double) offer.getForAdditional()));
                }
                newStore.addDiscount(newDis);
            }

        StoresInSystem.put(newStore.getStoreID(), newStore);
        SystemGrid.put(newStore.getCoordinate(), newStore);
    }


    private void checkMissingItem() throws ItemIsNotSoldAtAllException {
        for (ProductInSystem curItem : ItemsInSystem.values())
            if (curItem.getNumberOfSellingStores() == 0) {
                throw new ItemIsNotSoldAtAllException(curItem.getSerialNumber(), curItem.getItem().getName());
            }
    }

}
