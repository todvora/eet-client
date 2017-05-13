package cz.tomasdvorak.eet.client.persistence;

import cz.etrzby.xml.ObjectFactory;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.exceptions.RequestSerializationException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * EET request serializer / deserializer. May serve for persistence or logging.
 */
public class RequestSerializer {

    /**
     * Convert request to a String representation.
     */
    public static String toString(TrzbaType request) {
        try {
            final JAXBElement<TrzbaType> trzba = new ObjectFactory().createTrzba(request);
            JAXBContext context = JAXBContext.newInstance(TrzbaType.class);
            StringWriter sw = new StringWriter();
            context.createMarshaller().marshal(trzba, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new RequestSerializationException("Failed to serialize EET request to String", e);
        }
    }

    /**
     * Restore request from a String back to object.
     */
    public static TrzbaType fromString(String request) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TrzbaType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            InputStream is = new ByteArrayInputStream(request.getBytes());
            JAXBElement<TrzbaType> reqestElement = jaxbUnmarshaller.unmarshal(new StreamSource(is), TrzbaType.class);
            return reqestElement.getValue();
        } catch (JAXBException e) {
            throw new RequestSerializationException("Failed to deserialize EET request from String", e);
        }
    }
}
