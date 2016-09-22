package cz.tomasdvorak.eet.client.security;

import cz.etrzby.xml.TrzbaDataType;
import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import cz.tomasdvorak.eet.client.utils.DateUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Utility class for easier testing of PKP and BKP codes generating.
 */
class DemoRequestHolder {
    private final String clientKeyPath;
    private final String clientKeyPassword;
    private final String demoRequestPath;
    private final Document request;

    DemoRequestHolder(final String clientKeyPath, final String clientKeyPassword, final String demoRequestPath) throws IOException, SAXException, ParserConfigurationException {
        this.clientKeyPath = clientKeyPath;
        this.clientKeyPassword = clientKeyPassword;
        this.demoRequestPath = demoRequestPath;
        this.request = createDocument(demoRequestPath);
    }

    private Document createDocument(final String demoRequestPath) throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(getClass().getResourceAsStream(demoRequestPath));
    }

    SecurityCodesGenerator getCodesGenerator() throws InvalidKeystoreException {
        final ClientKey clientKey = new ClientKey(getClass().getResourceAsStream(clientKeyPath), clientKeyPassword);
        return new SecurityCodesGenerator(clientKey);
    }

    String getDemoRequestPath() {
        return demoRequestPath;
    }

    TrzbaDataType getTrzbaDataType() throws Exception {
        return new TrzbaDataType()
                .withDicPopl(getXPathValue("//Trzba/Data/@dic_popl"))
                .withIdProvoz(Integer.parseInt(getXPathValue("//Trzba/Data/@id_provoz")))
                .withIdPokl(getXPathValue("//Trzba/Data/@id_pokl"))
                .withPoradCis(getXPathValue("//Trzba/Data/@porad_cis"))
                .withDatTrzby(DateUtils.parse(getXPathValue("//Trzba/Data/@dat_trzby")))
                .withCelkTrzba(new BigDecimal(getXPathValue("//Trzba/Data/@celk_trzba")));
    }

    String getXPathValue(final String xpathQuery) throws Exception {
        final XPathFactory xPathfactory = XPathFactory.newInstance();
        final XPath xpath = xPathfactory.newXPath();
        final XPathExpression expr = xpath.compile(xpathQuery);
        final Object evaluate = expr.evaluate(this.request, XPathConstants.STRING);
        return evaluate.toString();
    }
}
