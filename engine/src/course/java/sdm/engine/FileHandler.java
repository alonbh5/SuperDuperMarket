package course.java.sdm.engine;

import course.java.sdm.exceptions.NoValidXMLException;
import course.java.sdm.generatedClasses.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;



class FileHandler {

    private final static String JAXB_XML_SDM_PACKAGE_NAME = "course.java.sdm.generatedClasses";

     static SuperDuperMarketDescriptor UploadFile (InputStream inputStream) throws JAXBException, NoValidXMLException {

         return deserializeFrom(inputStream);


     }

    static SuperDuperMarketDescriptor deserializeFrom(InputStream inputStream) throws JAXBException {

        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_SDM_PACKAGE_NAME);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (SuperDuperMarketDescriptor) unmarshaller.unmarshal(inputStream);
    }

     private static SuperDuperMarketDescriptor deserializeFrom(File inputStream) throws JAXBException {

         JAXBContext jc = JAXBContext.newInstance(JAXB_XML_SDM_PACKAGE_NAME);
         Unmarshaller unmarshaller = jc.createUnmarshaller();
         return (SuperDuperMarketDescriptor) unmarshaller.unmarshal(inputStream);
     }

}
