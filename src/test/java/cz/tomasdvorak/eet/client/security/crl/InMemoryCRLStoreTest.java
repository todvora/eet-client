package cz.tomasdvorak.eet.client.security.crl;

import cz.tomasdvorak.eet.client.security.ServerKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.cert.*;
import java.util.*;

public class InMemoryCRLStoreTest {

    private X509Certificate[] certificates;

    @Before
    public void setUp() throws Exception {
        certificates = getFromClasspath("/keys/rca15_rsa.der", "/keys/2qca16_rsa.der", "/keys/crls-prod-cert.pem");
    }

    @Test
    public void getCRLStore() throws Exception {
        CertStore crlStore = InMemoryCRLStore.INSTANCE.getCRLStore(certificates);
        final Collection<?> collection = ((CollectionCertStoreParameters) crlStore.getCertStoreParameters()).getCollection();
        final int size = collection.size();
        Assert.assertEquals(6, size);

        final Set<String> expectedDns = new HashSet<String>(Arrays.asList(
                "SERIALNUMBER=NTRCZ-26439395, CN=I.CA Root CA/RSA, O=\"První certifikační autorita, a.s.\", C=CZ",
                "SERIALNUMBER=NTRCZ-26439395, O=\"První certifikační autorita, a.s.\", CN=I.CA Qualified 2 CA/RSA 02/2016, C=CZ"
        ));

        final Set<String> foundDns = new HashSet<String>();

        for (Object o : collection) {
            X509CRL crl = (X509CRL) o;
            final String name = crl.getIssuerDN().getName();
            foundDns.add(name);
        }

        Assert.assertEquals(expectedDns, foundDns);
    }

    private X509Certificate[] getFromClasspath(final String... paths) throws CertificateException {
        final List<X509Certificate> certificates = new LinkedList<X509Certificate>();
        for (String path : paths) {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final X509Certificate certificate = (X509Certificate) cf.generateCertificate(getClass().getResourceAsStream(path));
            certificates.add(certificate);
        }
        return certificates.toArray(new X509Certificate[certificates.size()]);
    }

}