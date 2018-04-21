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
    private static JAXBContext JAXB_CONTEXT = JAXBContext.newInstance(TrzbaType.class);

    /**
     * Convert request to a String representation.
     */
    public static String toString(TrzbaType request) {
        try {
            final JAXBElement<TrzbaType> trzba = new ObjectFactory().createTrzba(request);
            StringWriter sw = new StringWriter();
            JAXB_CONTEXT.createMarshaller().marshal(trzba, sw);
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
            Unmarshaller jaxbUnmarshaller = JAXB_CONTEXT.createUnmarshaller();
            InputStream is = new ByteArrayInputStream(request.getBytes());
            JAXBElement<TrzbaType> reqestElement = jaxbUnmarshaller.unmarshal(new StreamSource(is), TrzbaType.class);
            return reqestElement.getValue();
        } catch (JAXBException e) {
            throw new RequestSerializationException("Failed to deserialize EET request from String", e);
        }
    }
}
