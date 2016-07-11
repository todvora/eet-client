## Client / demo application for #EET - [etrzby.cz](http://www.etrzby.cz)
[![Build Status](https://travis-ci.org/todvora/eet-client.svg?branch=master)](https://travis-ci.org/todvora/eet-client)

Simple java client for submission of receipts to the central registry at eet.cz.

It solves following topics:

- Certificates import
- Webservice communication
- Computation of verification codes PKP and BKP
- Signing of requests (WS-Security)
- Validating of responses (WS-Security)

Implementer has to take care of:

- Provide InputStream to a valid pkcs12 keystore with client keys
- Provide InputStream to a I.CA root certificate keystore (see lower)
- Errors handling
- Resubmission, in case of failure

## Usage
```java
final InputStream clientKey = getClass().getResourceAsStream("/keys/01000005.p12");
final InputStream serverCertificate = getClass().getResourceAsStream("/keys/qica.der");
EETClient eetService = EETServiceFactory.getInstance(clientKey, "eet", serverCertificate);
eetService.submitReceipt(receipt, CommunicationMode.REAL, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT)
```

## Response verification
SOAP responses are signed by a certificate issued for:

```
O=Česká republika - Generální finanční ředitelství, CN=Elektronická evidence tržeb - Playground, C=CZ
```
To be able to validate the signature, the root certificate for the I.CA has to be present.
You can download it [here](https://www.ica.cz/userfiles/files/certifikaty/SHA2/qica_root_key_20090901.der)
or go to [http://www.ica.cz/CA-pro-kvalifikovane-sluzby](http://www.ica.cz/CA-pro-kvalifikovane-sluzby) and download the SHA-2 DER variant.

This root certificate has to be provided as the third parameter in the ```submitReceipt``` method call.

There is a pretty complicated logic, which decides, when the response is signed. Following table summarizes it:

| CommunicationMode | EndpointType | Valid message? | Is response signed?  |
|---|---|---|---|
| REAL | PRODUCTION | true  | **yes** (prod.cert) |
| REAL | PRODUCTION | false | no |
| REAL | PLAYGROUND | true  | **yes** (test cert) |
| REAL | PLAYGROUND | false | no |
| TEST | PRODUCTION | true  | no |
| TEST | PRODUCTION | false | no |
| TEST | PLAYGROUND | true  | no |
| TEST | PLAYGROUND | false | no |

[see the original table from documentation](signing.png)


## License
MIT License (see a [human readable explanation](http://choosealicense.com/licenses/mit/))
