package Servlets;

import Utils.ServletUtils;
import Utils.SessionUtils;
import course.java.sdm.engine.MainSystem;
import course.java.sdm.exceptions.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadXml extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)   throws ServletException, IOException {

        response.setContentType("text/html");

        Collection<Part> parts = request.getParts();
        MainSystem MainSDM = ServletUtils.getMainSystem(getServletContext());
        String userNameFromSession = SessionUtils.getUserName(request);
        PrintWriter out = response.getWriter();

        try {
            MainSDM.uploadFile(parts.iterator().next().getInputStream(),userNameFromSession); //only one file
            out.println("File Uploaded!");
        } catch (DuplicatePointOnGridException e) {
            out.println("Error - There is a Duplicated Point on Grid - "+ e.PointInput.x+","+e.PointInput.y);
        } catch (DuplicateItemInStoreException e) {
            out.println("Error - There is a Duplicated Item in Store #"+e.id);
        } catch (NoOffersInDiscountException e) {
            out.println("Error - Discount - "+ e.DiscountName+" Does Not Offer any Items");
        } catch (IllegalOfferException e) {
            out.println("Error - There is a Illegal Offer In Discount - "+ e.OfferName);
        } catch (PointOutOfGridException e) {
            out.println("Error - There is a Point Not On Grid - "+ e.PointReceived.x+","+e.PointReceived.y);
        } catch (DuplicateStoreInSystemException e) {
            out.println("Error - There is a Duplicated Store in Zone #"+e.Storeid);
        } catch (ItemIsNotSoldAtAllException e) {
            out.println("Error - There is a Item not Being Sold (Item #"+e.ItemID+")");
        } catch (StoreItemNotInSystemException e) {
            out.println("Error - There is a Store That sell Undefined Item (Store #"+e.StoreIdInput+" Item #"+e.ItemIdInput+")");
        } catch (StoreDoesNotSellItemException e) {
            out.println("Error - There is a Store With no Selling Items (Store #"+e.StoreID+")");
        } catch (NegativePriceException e) {
            out.println("Error - There is a Negative Price in Zone - "+e.PriceReceived);
        } catch (NoValidXMLException e) {
            out.println("Error - File is not XML form ");
        } catch (NegativeQuantityException e) {
            out.println("Error - There is a Negative Quantity in Zone - "+e.Quantity);
        } catch (DuplicateItemIDException e) {
            out.println("Error - There is a Duplicated Item in Zone ("+e.id+")");
        } catch (WrongPayingMethodException e) {
            out.println("Error - Paying Method Should be Amount or Weight (and not "+e.PayingInput+")");
        } catch (DuplicateZoneException e) {
            out.println("Error - Zone "+e.Zone+" is Already Own by "+e.SellerThatHasZone);
        } catch (Exception e) {
            out.println("Error - Unknown");
        }
    }

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            processRequest(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }
}
