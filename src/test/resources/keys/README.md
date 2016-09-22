# Keys folder README

This folder contains test keys and certificates using during unit/integration testing. 
None of them is used during real communication and they are not a part of jar package
produced by maven. 
 
In real communication, the client key has to be provided by an user of this library. 
The same holds for root certificate of I.CA. For details see lower.  

## 01000003.p12 - 01000005.p12
[PKCS12](https://en.wikipedia.org/wiki/PKCS_12) private/public key pairs provided by EET playground certification authority. 

Every key pair is produced for different DIČ (Tax identification number):
01000003.p12 - CZ1212121218
01000004.p12 - CZ00000019
01000005.p12 - CZ683555118

all of them are secured by a password, which is currently set to ```eet```.

In the real communication, user keys has to be requested in person or through authorized communication platform 
called [datové schranky](https://www.mojedatovaschranka.cz/). They will be produced by EET certification authority.

More details on http://www.etrzby.cz/cs/certifikacni-autorita-EET


## qica.der
The [I.CA](http://www.ica.cz/English) certification authority root certificate.
This authority produces EET certificate used to sign EET responses. The EET certificate
should be valid and verified against this root CA certificate.

In the real communication, user is required to provide this I.CA root certificate separately.
How to obtain it is described at https://github.com/todvora/eet-client#response-signature

## crls-demo-cert.pem
The EET server certificate contains also some [certificate revocation lists](https://en.wikipedia.org/wiki/Revocation_list) (CRL).
Every received and signed EET response should contain also attached EET certificate.
 
From this certificate, revocation lists are read, downloaded and used for check of EET certificate validity. 

To be able to unit-test this CRLs extraction (which is quite complicated in java), 
the EET server certificate has been extracted from one of the responses and saved 
in ```crls-demo-cert.pem```. This certificate serves only for unit-testing purposes
and isn't used during normal communication (see CRLUtilsTest.java).

## Other playground certificates and CRLs
There are some other certificates and CRLs provided by playground EET, which are not used anyhow. This contains:

* ca.crl.pem
* ca.crl
* ca.cer.pem
* ca.cer

Files ```ca.crl``` and ```ca.crl.pem``` are root certificates of EET playground CA. 
Files ```ca.cer.pem``` and ```ca.cer.pem``` are certificate revocation lists by EET playground CA.

If you want to validate one of ```01000003.p12```, ```01000004.p12``` or ```01000005.p12``` against 
them, feel free to do so. But I don't see any reason. In the real communication, this validation
has to be done on EET server side, not in your library. They has to make sure, that your keys are
valid and not revoked. 
