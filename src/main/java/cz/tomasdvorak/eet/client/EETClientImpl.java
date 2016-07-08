package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.*;
import cz.tomasdvorak.eet.client.dto.CommunicationMode;
import cz.tomasdvorak.eet.client.dto.EndpointType;
import cz.tomasdvorak.eet.client.dto.SecurityCodesGenerator;
import cz.tomasdvorak.eet.client.dto.SubmissionType;
import cz.tomasdvorak.eet.client.exceptions.DataSigningException;
import cz.tomasdvorak.eet.client.logging.WebserviceLogging;
import cz.tomasdvorak.eet.client.security.ClientKey;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

import javax.xml.ws.BindingProvider;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


class EETClientImpl implements EETClient {

    private static final String CRYPTO_INSTANCE_KEY = "eetCryptoInstance";
    private static final String JAVAX_NET_SSL_KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
    private static final long RECEIVE_TIMEOUT = 10_000L; // 10s timeout for webservice call - TODO: should it be adjustable?

    /**
     * Service instance is thread safe and cachable, so create just one instance during initialization of the class
     */
    private static final  EETService WEBSERVICE = new EETService(EETClientImpl.class.getResource("schema/EETServiceSOAP.wsdl"));

    /**
     * Signing of data and requests
     */
    private final ClientKey clientKey;

    public EETClientImpl(final ClientKey clientKey) {
        this.clientKey = clientKey;
    }

    public OdpovedType submitReceipt(final TrzbaDataType receipt, final CommunicationMode mode, final EndpointType endpointType, final SubmissionType submissionType) throws DataSigningException {
        return getPort(endpointType).odeslaniTrzby(prepateData(receipt, mode, submissionType));
    }

    private TrzbaType prepateData(final TrzbaDataType data, final CommunicationMode mode, final SubmissionType submissionType) throws DataSigningException {
        final TrzbaHlavickaType header = getHeader(mode, submissionType);
        final TrzbaType trzbaType = new TrzbaType();
        trzbaType.setData(data);
        trzbaType.setHlavicka(header);
        trzbaType.setKontrolniKody(getCheckCodes(data));
        return trzbaType;
    }

    private TrzbaHlavickaType getHeader(final CommunicationMode mode, final SubmissionType submissionType) {
        final TrzbaHlavickaType header = new TrzbaHlavickaType();
        header.setUuidZpravy(UUID.randomUUID().toString());
        header.setDatOdesl(new Date());
        header.setPrvniZaslani(submissionType.isFirstSubmission());
        header.setOvereni(mode.isCheckOnly());
        return header;
    }

    private TrzbaKontrolniKodyType getCheckCodes(final TrzbaDataType data) throws DataSigningException {
        final SecurityCodesGenerator securityCodesGenerator = new SecurityCodesGenerator(this.clientKey);
        final byte[] pkpValue = securityCodesGenerator.getPKP(data);
        final String bkpValue = securityCodesGenerator.getBKP(data);

        final TrzbaKontrolniKodyType checkCodes = new TrzbaKontrolniKodyType();
        final PkpElementType pkp = new PkpElementType();
        checkCodes.setPkp(pkp);
        pkp.setValue(pkpValue);
        pkp.setEncoding(PkpEncodingType.BASE_64);
        pkp.setCipher(PkpCipherType.RSA_2048);
        pkp.setDigest(PkpDigestType.SHA_256);

        final BkpElementType bkp = new BkpElementType();
        checkCodes.setBkp(bkp);
        bkp.setValue(bkpValue);
        bkp.setDigest(BkpDigestType.SHA_1);
        bkp.setEncoding(BkpEncodingType.BASE_16);
        return checkCodes;
    }

    private EET getPort(final EndpointType endpointType) {
        EET port = WEBSERVICE.getEETServiceSOAP();
        final Client clientProxy = ClientProxy.getClient(port);
        ensureHTTPSKeystorePassword();
        configureEndpointUrl(port, endpointType.getWebserviceUrl());
        configureSchemaValidation(port);
        configureTimeout(clientProxy);
        configureLogging(clientProxy);
        configureSigning(clientProxy);
        return port;
    }

    private void ensureHTTPSKeystorePassword() {
        if(System.getProperty(JAVAX_NET_SSL_KEY_STORE_PASSWORD) == null) {
            // there is not set keystore password (needed for HTTPS communication handshake), set the usual default one
            // TODO: is this assumption ok?
            System.setProperty(JAVAX_NET_SSL_KEY_STORE_PASSWORD, "changeit");
        }
    }

    private void configureSigning(final Client clientProxy) {
        Map<String,Object> signingProperties = new HashMap<>();
        signingProperties.put(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE); // only sign, do not encrypt
        signingProperties.put(WSHandlerConstants.SIGNATURE_USER, this.clientKey.getAlias());
        signingProperties.put(WSHandlerConstants.PW_CALLBACK_REF, this.clientKey.getClientPasswordCallback());
        signingProperties.put(CRYPTO_INSTANCE_KEY, clientKey.getCrypto());
        signingProperties.put(WSHandlerConstants.SIG_PROP_REF_ID, CRYPTO_INSTANCE_KEY);
        signingProperties.put(WSHandlerConstants.SIG_KEY_ID, "DirectReference");
        signingProperties.put(WSHandlerConstants.SIG_ALGO, "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        signingProperties.put(WSHandlerConstants.SIG_DIGEST_ALGO, "http://www.w3.org/2001/04/xmlenc#sha256");
        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(signingProperties);
        clientProxy.getOutInterceptors().add(wssOut);
        //TODO: should be the response signed and validated too?
    }

    private void configureTimeout(final Client clientProxy) {
        final HTTPConduit conduit = (HTTPConduit)clientProxy.getConduit();
        final HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setReceiveTimeout(RECEIVE_TIMEOUT);
        conduit.setClient(policy);
    }

    private void configureEndpointUrl(final EET remote, final String webserviceUrl) {
        final Map<String, Object> requestContext = ((BindingProvider)remote).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, webserviceUrl);
    }

    private void configureSchemaValidation(final EET remote) {
        final Map<String, Object> requestContext = ((BindingProvider)remote).getRequestContext();
        requestContext.put("schema-validation-enabled", "true");
    }

    private void configureLogging(final Client clientProxy) {
        clientProxy.getInInterceptors().add(WebserviceLogging.LOGGING_IN_INTERCEPTOR);
        clientProxy.getOutInterceptors().add(WebserviceLogging.LOGGING_OUT_INTERCEPTOR);
    }
}
