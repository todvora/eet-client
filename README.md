## Client / demo application for #EET - [etrzby.cz](http://www.etrzby.cz)
[![Build Status](https://travis-ci.org/todvora/eet-client.svg?branch=master)](https://travis-ci.org/todvora/eet-client)
[![Codecov](https://img.shields.io/codecov/c/github/todvora/eet-client.svg)](https://codecov.io/github/todvora/eet-client/)
[![Jitpack](https://jitpack.io/v/todvora/eet-client.svg)](https://jitpack.io/#todvora/eet-client)
[![Gitter](https://img.shields.io/gitter/room/nwjs/nw.js.svg)](https://gitter.im/eet-client/Lobby)

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
InputStream clientKey = getClass().getResourceAsStream("/keys/CZ683555118.p12");
InputStream rootCACertificate = getClass().getResourceAsStream("/keys/rca15_rsa.der");
InputStream subordinateCACertificate = getClass().getResourceAsStream("/keys/2qca16_rsa.der");
EETClient client = EETServiceFactory.getInstance(clientKey, "eet", rootCACertificate, subordinateCACertificate);

TrzbaDataType data = new TrzbaDataType()
        .withDicPopl("CZ683555118")
        .withIdProvoz(243)
        .withIdPokl("24/A-6/Brno_2")
        .withPoradCis("#135433c/11/2016")
        .withDatTrzby(new Date())
        .withCelkTrzba(new BigDecimal("3264"));

try {
    TrzbaType request = eetService.prepareRequest(data, CommunicationMode.REAL);
    SubmitResult result = eetService.sendSync(request, EndpointType.PLAYGROUND);
    // print codes on the receipt
    System.out.println("FIK:" + result.getFik());
    System.out.println("BKP:" + result.getBKP());
} catch (final CommunicationTimeoutException e) {
    // timeout occurred, resend later again
    System.out.println("PKP:" + e.getPKP());
    System.out.println("BKP:" + e.getBKP());
    // get other data from the request
    System.out.println(e.getRequest().getData().getDatTrzby());
} catch (final CommunicationException e) {
    // resend, if fails again, print PKP on the receipt
    System.out.println("PKP:" + e.getPKP());
    System.out.println("BKP:" + e.getBKP());
    // get other data from the request
    System.out.println(e.getRequest().getData().getDatTrzby());
}
```

Asynchronous call with a callback:

```java
TrzbaType request = eetClient.prepareRequest(data, CommunicationMode.REAL);
eetClient.sendAsync(request, EndpointType.PLAYGROUND, new ResponseCallback() {
    @Override
    public void onComplete(final SubmitResult result) {
        System.out.println("FIK:" + result.getFik());
    }
    @Override
    public void onError(final CommunicationException e) {
        System.out.println("PKP:" + e.getPKP());
    }
    @Override
    public void onTimeout(final CommunicationTimeoutException cause) {
       System.out.println("PKP:" + e.getPKP());
    }
});
```

## Additional resources
- Maven project info, containing all dependencies information: https://todvora.github.io/eet-client/latest/project-info.html
- Javadoc: https://todvora.github.io/eet-client/latest/apidocs/index.html
- Releases: https://github.com/todvora/eet-client/releases
- Continuous integration test results: https://travis-ci.org/todvora/eet-client
- Code coverage: https://codecov.io/github/todvora/eet-client/

## Professional support
Support during implementation or specific features required? No problem! Write me at [todvora@gmail.com](mailto:todvora@gmail.com).
You will donate to [Médecins Sans Frontières](http://www.msf.org/en/donate) and I will help you out with the #EET.


## Request signing
Every request has to be signed with a client's key. The key will be provided by EET ([see how and where](http://www.etrzby.cz/cs/pred-zahajenim-evidence-trzeb)). For the demo application and playground environment, some [test keys](http://www.etrzby.cz/assets/cs/prilohy/CA_PG_v1.zip) have been published. Those keys are used in integration tests of this demo app.

The signing itself complies with [WS-Security](https://cxf.apache.org/docs/ws-security.html). There is a [WSS4JOutInterceptor](https://cxf.apache.org/javadoc/latest/org/apache/cxf/ws/security/wss4j/WSS4JOutInterceptor.html) configured, which handles signing, key embedding, hashing algorithms selection and so one.

## Response verification

### Response signature
SOAP responses are signed by a certificate issued for:

- Production: `O=Česká republika - Generální finanční ředitelství, C=CZ, CN=Elektronická evidence tržeb`
- Playground: `O=Česká republika - Generální finanční ředitelství, CN=Elektronická evidence tržeb - Playground, C=CZ`


To be able to validate the signature, the root certificate(s) for the I.CA has to be present. There are two sets of CA certificates.

#### For production

You need two certificates - root CA certificate and subordinate CA cert.

- [rca15_rsa.der](www.ica.cz/userfiles/files/certifikaty/HCA_root/rca15_rsa.der) (Qualified system certificate root CA (CN = I.CA Root CA/RSA,  sn:  100000000/0x5f5e100)).
- [2qca16_rsa.der](http://www.ica.cz/userfiles/files/certifikaty/HCA_kvalifikovany/2qca16_rsa.der) Qualified system certificate subordinate QCA (CN = I.CA Qualified 2 CA/RSA 02/2016 sn:  100001006/0x5f5e4ee)

You can download them directly from links above or from http://www.ica.cz/HCA-root-en and http://www.ica.cz/HCA-qualificate

#### For playground
You can download it [here](https://www.ica.cz/userfiles/files/certifikaty/SHA2/qica_root_key_20090901.der)
or go to [http://www.ica.cz/CA-pro-kvalifikovane-sluzby](http://www.ica.cz/CA-pro-kvalifikovane-sluzby) and download the SHA-2 DER variant.

Besides different certificates, everything is the same for both production and playground env.

This CA certificate(s) has to be provided as the third (and fourth) parameter in the ```EETServiceFactory#getInstance``` method call.

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
The client application should verify, that EET public certificate has not been revoked. To do that, either <a href="https://en.wikipedia.org/wiki/Revocation_list">CRL</a> or <a href="https://en.wikipedia.org/wiki/Online_Certificate_Status_Protocol">OCSP</a> should be used. <a href="http://www.ica.cz">I.CA</a> is the EET's certificate authority. They provide CRL on http://q.ica.cz/cgi-bin/crl_qpub.cgi?language=cs&snIssuer=10500000 for manual download (captcha is required). I.CA should also provide OCSP, as stated in this [news article[2011, czech]](http://www.ica.cz/Novinky?IdNews=140).

Current implementation of this client is based on CRL Distribution Points provided in the EET certificate itself.

The client reads the provided certificate (sent along with the response) downloads CRLs and checks the EET certificate validity against them. CLR has to have an update interval configured. The client caches CRL in memory and updates it when needed. See the [MerlinWithCRLDistributionPointsExtension](src/main/java/cz/tomasdvorak/eet/client/security/MerlinWithCRLDistributionPointsExtension.java) implementation for details.

## WS-Policy

> WS-Policy is a specification that allows web services to use XML to advertise their policies (on security, quality of service, etc.) and for web service consumers to specify their policy requirements.
([from Wikipedia](https://en.wikipedia.org/wiki/WS-Policy))

EET WSDL contained ws-policy with security constraints defined till EET interface version 2. This definition has been removed in version 3. Every developer is now required to take care of setting security configuration manually, following official documentation of EET.

For more details see https://github.com/todvora/eet-client/issues/1. See also [diff between versions 2. and 3. of EET WSDL](https://gist.github.com/todvora/1abbdc6291bcc5d0d2d593ea8b33443b/revisions?diff=unified).

*Note: It doesn't affect you as an user of this EET client, is important only for a green field implementations of EET webservice consumers.*

## Installation

### Maven
If you want to use this library as a dependency in your Maven based project, follow instructions provided on [jitpack.io](https://jitpack.io/#todvora/eet-client). There is currently no maven central release.

### Manually
Download latest release ```eet-client-X.Y.jar``` from [Github Releases](https://github.com/todvora/eet-client/releases). Add it to your classpath, together with
all dependencies included in ```eet-client-X.Y-dependencies.zip``` archive, located also in [Github Releases](https://github.com/todvora/eet-client/releases).
(Dependencies should be extracted from the zip archive first and then added to classpath).

Dependencies archive is generated automatically with every release and contains all dependencies required by eet-client.

## Java version
Since EET client has to deal with lots of encryption and security, up-to-date version of Java should be used.

Supported and tested are following versions:

- Oracle JDK 8
- Oracle JDK 7
- OpenJDK 7
- OpenJDK 6

Oracle Java 6 is after its end-of-life and doesn't provide required TLSv1.1 implementation for secure communication. Thus it's not possible to run this EET client on Oracle Java 6!

## Java Cryptography Extension (JCE) Unlimited Strength
The production communication requires unlimited cryptography strength. If you are Open JDK / Open JRE user, you don't have to worry about that. Oracle users have to follow these steps:

- Download Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy File from Oracle website (direct links for [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html), [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)).
- Unzip the downloaded archive.
- Copy local_policy.jar and US_export_policy.jar to the `$JAVA_HOME/jre/lib/security` (overriding existing jars).

Exact information and instalation details are also in README.txt in the downloaded archives.

### How do I know, that I need to install JCE?
You will see in logs exceptions like:

```
java.io.IOException: exception unwrapping private key - java.security.InvalidKeyException: Illegal key size
```

## Development, debugging, logging

### Application logging
This client has extended logging of both internal information and webservice communication.
Logs are persisted inside ```logs/``` directory under current working directory (usually your app or workspace dir).
Logs are rotated on date and file size basis, to be able to read and process them easily.

See
- ```logs/all.log``` - all produced logs from the app, containing also webservice requests and responses
- ```logs/webservice.log``` - only webservice communication, requests and responses

If you want to change the configuration of the logging, simply create your own [log4j2 configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html#XML) file and
provide path to it in the following system property:
```
-Dlog4j.configurationFile=log4j2-custom.xml
```
you can copy and adapt [the current configuration file](https://github.com/todvora/eet-client/blob/master/src/main/resources/log4j2.xml) to your needs for that.

### SSL and handshake logging
To print debugging information regarding SSL connection to EET servers, add following system property to your java command:
```
-ea -Djavax.net.debug=ssl,handshake
```
More on [Debugging SSL/TLS Connections](https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/ReadDebug.html)

### Certificates and keys debugging
Add following system property to your java command
```
    -Djava.security.debug=certpath
```
More on [Troubleshooting Security](https://docs.oracle.com/javase/7/docs/technotes/guides/security/troubleshooting-security.html)

### Request duration logging
Every request is measured and the library collects time logs. They are stored as ```timing.csv``` inside ```logs``` directory.
Every row contains current date and time, endpoint url, request ID (to be able to compare timing with request/response data inside webservice.log)  and finally request duration in millis.  
For example:
```
2016-11-11T15:35:22+01:00;1143;https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3;id_1
2016-11-11T15:35:23+01:00;253;https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3;id_2
2016-11-11T15:35:23+01:00;247;https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3;id_3
2016-11-11T15:35:23+01:00;244;https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3;id_4
2016-11-11T15:35:24+01:00;242;https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3;id_5
```


## News, discussions

To follow latest news about #EET, join us on [gitter.im/eet-client](https://gitter.im/eet-client/Lobby).

## Similar projects

- https://github.com/ondrejnov/eet (PHP, MIT license)
- https://github.com/novakmi/eetlite (Groovy, MIT license)
- https://github.com/l-ra/openeet (Java, C#, UNIX shell, Apache 2.0 license)
- https://github.com/mirus77/DelphiEET (Delphi, MIT license)
- https://drive.google.com/drive/folders/0B2B4_OfsI25paTB2R0NNM1hqMzg (C#, unknown license)
- https://github.com/JakubMrozek/eet (Node.js, MIT license)
- https://github.com/MewsSystems/eet (C#, MIT License)
- https://github.com/slevomat/eet-client (PHP, MIT License)
- https://github.com/charlieMonroe/SwiftEET (Swift, GPL-3.0)

## TODO and to decide

- Should be the I.CA root certificate downloaded automatically or provided by the implementer? IMHO no, not secure enough.
- Should the I.CA root be added to the default JVM truststore?
- Create demo project, using this client as a dependency
- Run integration tests on travis-ci (apparently blocked travis's IP/range to the WS by EET server itself)
- Security review - is everything as correct as possible?

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

(See a [human readable explanation of the MIT license](http://choosealicense.com/licenses/mit/)).
