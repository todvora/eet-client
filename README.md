## Client / demo application for #EET - [etrzby.cz](http://www.etrzby.cz)
[![Build Status](https://travis-ci.org/todvora/eet-client.svg?branch=master)](https://travis-ci.org/todvora/eet-client)

Simple java client for submission of receipts to the central registry at eet.cz.

It solves following topics:

- Keys and certificates import
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
InputStream clientKey = getClass().getResourceAsStream("/keys/01000005.p12");
InputStream serverCertificate = getClass().getResourceAsStream("/keys/qica.der");
InputStream crl = getClass().getResourceAsStream("/keys/qica.crl");
EETClient service = EETServiceFactory.getInstance(clientKey, "eet", serverCertificate, crl);
OdpovedType response = service.submitReceipt(
    receipt, // content, receipt data
    CommunicationMode.REAL, // real or test mode
    EndpointType.PLAYGROUND,  // which endpoint should be used
    SubmissionType.FIRST_ATTEMPT // first or repeated submission
);
// process the response, repeat if contains error
```

## Request signing
Every request has to be signed with a client's key. The key will be provided by EET ([see how and where](http://www.etrzby.cz/cs/pred-zahajenim-evidence-trzeb)). For the demo application and playground environment, some [test keys](http://www.etrzby.cz/assets/cs/prilohy/CA_PG_v1.zip) have been published. Those keys are used in integration tests of this demo app.

The signing itself complies with [WS-Security](https://cxf.apache.org/docs/ws-security.html). There is a [WSS4JOutInterceptor](https://cxf.apache.org/javadoc/latest/org/apache/cxf/ws/security/wss4j/WSS4JOutInterceptor.html) configured, which handles signing, key embedding, hashing algorithms selection and so one.

## Response verification

### Response signature
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

### Validation
[WSS4JInInterceptor](https://cxf.apache.org/javadoc/latest/org/apache/cxf/ws/security/wss4j/WSS4JInInterceptor.html) handles response validation. It's configured to verify signature against I.CA root certificate, checks CRL and handles all the obscure cases, where message is deliberately unsigned (see the table above).

### Certificate revocation
The client application should verify, that EET public certificate has not been revoked. To do that, either <a href="https://en.wikipedia.org/wiki/Revocation_list">CRL</a> or <a href="https://en.wikipedia.org/wiki/Online_Certificate_Status_Protocol">OCSP</a> should be used. <a href="http://www.ica.cz">I.CA</a> is the EET's certificate authority. They provide CRL on http://q.ica.cz/cgi-bin/crl_qpub.cgi?language=cs&snIssuer=10500000. Unfortunately captcha is required, which prevents any automation. I.CA should also provide OCSP, as stated in this [news article[2011, czech]](http://www.ica.cz/Novinky?IdNews=140). I'm currently investigating, how to automate certificate status verification.

## To do and to decision

- Automate CRL or OCSP for responses certificate
- Should be the I.CA root certificate downloaded automatically or provided by the implementer?
- Should the I.CA root be added to the default JVM truststore?
- Distribute through Maven central or [JitPack](https://jitpack.io)?
- Create demo project, using this client as a dependency
- Detailed logging
- Run integration tests on travis-ci (apparently blocked travis's IP/range to the WS by EET server itself)
- Security review - is everything as correct as possible?
- Configurable logging when used as a client / connector?


## Resources

## License

```
MIT License

Copyright (c) 2016 Tomas Dvorak

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```

(See a [human readable explanation of MIT license](http://choosealicense.com/licenses/mit/)).
