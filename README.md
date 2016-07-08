## Client / demo application for #EET - [etrzby.cz](http://www.etrzby.cz)
[![Build Status](https://travis-ci.org/todvora/eet-client.svg?branch=master)](https://travis-ci.org/todvora/eet-client)

Simple java client for submission of receipts to the central registry at eet.cz. 

It solves following topics:

- Certificates import
- Webservice communication
- Computation of verification codes PKP and BKP
- Signing (WS-Security) of requests

Implementer has to take care of:

- Provide InputStream to a valid pkcs12 keystore 
- Errors handling
- Resubmission, in case of failure


## Usage
```java
InputStream keystore = getClass().getResourceAsStream("/keys/01000005.p12");
EETClient eetService = EETServiceFactory.getInstance(keystore, "eet");
eetService.submitReceipt(receipt, CommunicationMode.TEST, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT)
```

## License
MIT License (see a [human readable explanation](http://choosealicense.com/licenses/mit/))
