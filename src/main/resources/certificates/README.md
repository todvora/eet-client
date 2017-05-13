
## qica.der
The [I.CA](http://www.ica.cz/English) certification authority root certificate.
This authority produces EET certificate used to sign EET responses. The EET certificate
should be valid and verified against this root CA certificate.

In the real communication, user is required to provide this I.CA root certificate separately.
How to obtain it is described at https://github.com/todvora/eet-client#response-signature

## 2qca16_rsa.der
The [I.CA](http://www.ica.cz/English) certification authority intermediate certificate for qualified services. This 
certificate should be the one against we verify production responses.

## rca15_rsa.der
The [I.CA](http://www.ica.cz/English) certification authority root certificate for qualified services. This 
certificate should be the one against we verify production responses.