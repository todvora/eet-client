## Client / demo application for #EET - [etrzby.cz](http://www.etrzby.cz)
[![Build Status](https://travis-ci.org/todvora/eet-client.svg?branch=master)](https://travis-ci.org/todvora/eet-client)

## Usage
```java
InputStream keystore = getClass().getResourceAsStream("/keys/01000005.p12");
EETClient eetService = EETServiceFactory.getInstance(keystore, "eet");
eetService.submitReceipt(receipt, CommunicationMode.TEST, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT)
```

## License
MIT License (see a [human readable explanation](http://choosealicense.com/licenses/mit/))
