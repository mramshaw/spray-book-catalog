# Overview

Sample Book Catalog REST(ful) web service in Scala with the Spray framework.

![Swagger Logo](/images/swagger-logo.png)

The REST API will be documented with Swagger. The very useful Swagger UI rivals Postman.

## To Run

	sbt
	....
	> compile
	....
	> test
	....
	> run
	....
	> exit

## Swagger UI location

        https://localhost:9001/

## Calling HTTPS endpoints

The HTTPS endpoints may be verified with either __cURL__ or __httpie__ (first and second options below).

Although __httpie__ is less verbose and has a somewhat more sensible call sequence, my preference is for __curl__ simply because of the __-k__ option (which allows for the ignoring of expired security certificates - a bad practice to be sure, but acceptable for testing).

* with certificate provided
 
        curl --cacert certificate.crt -vi -X GET https://localhost:9001/api/v1/books

        http --verify=certificate.crt https://localhost:9001/api/v1/books

* without certificate provided

        curl -vik -X GET https://localhost:9001/api/v1/books

        http --verify=no https://localhost:9001/api/v1/books        

## Supported REST(ful) endpoints

* Search for a book on Google Books

        curl -vik -X GET https://localhost:9001/api/v1/search?query=scala

* Get all books: `https://localhost:9001/api/v1/books`

        curl -vik -X GET https://localhost:9001/api/v1/books

* Get book by ISBN: `https://localhost:9001/api/v1/books/{isbn}`

        curl -vik -X GET https://localhost:9001/api/v1/books/978-1935182757

* Update book by ISBN: `https://localhost:9001/api/v1/books/{isbn}`
 
        curl -vik -X PUT https://localhost:9001/api/v1/books/978-1935182757 
        	 -H "Content-Type: application/json" 
        	 -d '{"author": "Thomas Alexandre", "title": "Scala for Java Developers", "publishingDate": "2016-12-12"}'
        	 -u admin:passw0rd

* Remove book from the catalog by ISBN: `https://localhost:9001/api/v1/books/{isbn}`
	
        curl -vik -X DELETE https://localhost:9001/api/v1/books/978-1935182757 -u admin:passw0rd

* Get all publishers: `https://localhost:9001/api/v1/publishers`

        curl -vik -X GET https://localhost:9001/api/v1/publishers

* Add new publisher to book catalog: `https://localhost:9001/api/v1/publishers`

        curl -vik -X POST https://localhost:9001/api/v1/publishers
            -H "Content-Type: application/json"
            -d '{"name": "Leanpub"}'
            -u admin:passw0rd

* Get publisher by publisher identifier: `https://localhost:9001/api/v1/publishers/{id}`

        curl -vik -X GET https://localhost:9001/api/v1/publishers/1

* Update publisher by publisher identifier: `https://localhost:9001/api/v1/publishers/{id}`

        curl -vik -X PUT https://localhost:9001/api/v1/publishers/1
            -H "Content-Type: application/json"
            -d '{"name": "Leanpub"}'
            -u admin:passw0rd

* Remove publisher by publisher identifier: `https://localhost:9001/api/v1/publishers/{id}`

        curl -vik -X DELETE https://localhost:9001/api/v1/publishers/1 -u admin:passw0rd

* Get all books published by the publisher: `https://localhost:9001/api/v1/publishers/{id}/books`

        curl -vik -X GET https://localhost:9001/api/v1/publishers/1/books

* Add new book published by the publisher: `https://localhost:9001/api/v1/publishers/{id}/books`

        curl -vik -X POST https://localhost:9001/api/v1/publishers/2/books
            -H "Content-Type: application/json" 
            -d '{"isbn": "978-1935182757", "author": "Thomas Alexandre", "title": "Aaa", "publishingDate": "2016-12-12"}'
            -u admin:passw0rd

## Generating certificate
* Generate a self-signed certificate
 
        openssl req 
           -x509 
        	-sha256 
        	-newkey rsa:2048 
        	-keyout certificate.key 
        	-out certificate.crt 
        	-days 365 -nodes

* Export it into PKCS#12 format (PKCS12 offers some improvements over JKS keystores. A PKCS12 keystore usually has a file extension of p12 or pfx.)

        openssl pkcs12 
          	-export 
         	-in certificate.crt 
         	-inkey certificate.key  
         	-out server.p12 
         	-name spray-book-catalog 
         	-password pass:passw0rd

* Optionally, convert it into PEM format

        openssl x509 -inform PEM -in certificate.crt > certificate.pem

* Import the certificate into JKS

        keytool 
         	-importkeystore 
         	-srcstorepass passw0rd 
         	-destkeystore spray-book-catalog.jks 
         	-deststorepass passw0rd 
         	-srckeystore server.p12 
         	-srcstoretype PKCS12 
         	-alias spray-book-catalog
