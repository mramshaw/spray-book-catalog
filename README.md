# Overview

Sample Book Catalog REST(ful) web service in __Scala__ with the __Spray__ framework.

![RESTful API](/images/Swagger_API.png)

#### Spray

[Spray](http://spray.io/) is a pattern-matching Routing plug-in for Scala's Akka framework.

It seems very similiar to Python's Flask framework, or Golang's GorillaMux, or even node.js handlers.

It has its own DSL (Domain-Specific Language) for pattern-matching HTTP requests.

#### Swagger

![Swagger Logo](/images/swagger-logo.png)

[Swagger](https://swagger.io/) describes itself as the world's most popular API tooling.

The REST API will be documented with Swagger. The very useful Swagger UI rivals Postman.

![Swagger UI](/images/Swagger_UI.png)

As far as I can tell Swagger introspects on code annotations, albeit in a very useful way.

## To Run

Scala features the __sbt__ (Simple Build Tool), which seems to be yet another build/compile/test/run tool along the lines of __ant__ or __maven__ or __gradle__. And of course it is anything but simple. On the plus side, it does at least enforce a standard project directory structure.

	sbt
	....
	> compile
	....
	> test
	....
	> run
	....
	> exit
	$

## Swagger UI location

        https://localhost:9001/

As the course material suggests, the Swagger UI is really great (and possibly the best thing about Swagger).

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

#### Books

* Search for a book on Google Books

        curl -vik -X GET https://localhost:9001/api/v1/search?query=scala

* Get all books:

        curl -vik -X GET https://localhost:9001/api/v1/books

* Get book by ISBN:

        curl -vik -X GET https://localhost:9001/api/v1/books/978-1935182757

* Update book by ISBN:
 
        curl -vik -X PUT https://localhost:9001/api/v1/books/978-1935182757 
        	 -H "Content-Type: application/json" 
        	 -d '{"author": "Thomas Alexandre", "title": "Scala for Java Developers", "publishingDate": "2016-12-12"}'
        	 -u admin:passw0rd

* Remove book from the catalog by ISBN:
	
        curl -vik -X DELETE https://localhost:9001/api/v1/books/978-1935182757 -u admin:passw0rd

#### Publishers

* Get all publishers:

        curl -vik -X GET https://localhost:9001/api/v1/publishers

* Add new publisher to book catalog:

        curl -vik -X POST https://localhost:9001/api/v1/publishers
            -H "Content-Type: application/json"
            -d '{"name": "Leanpub"}'
            -u admin:passw0rd

* Get publisher by publisher identifier:

        curl -vik -X GET https://localhost:9001/api/v1/publishers/1

* Update publisher by publisher identifier:

        curl -vik -X PUT https://localhost:9001/api/v1/publishers/1
            -H "Content-Type: application/json"
            -d '{"name": "Leanpub"}'
            -u admin:passw0rd

* Remove publisher by publisher identifier:

        curl -vik -X DELETE https://localhost:9001/api/v1/publishers/1 -u admin:passw0rd

* Get all books published by the publisher:

        curl -vik -X GET https://localhost:9001/api/v1/publishers/1/books

* Add new book published by the publisher:

        curl -vik -X POST https://localhost:9001/api/v1/publishers/2/books
            -H "Content-Type: application/json" 
            -d '{"isbn": "978-1935182757", "author": "Thomas Alexandre", "title": "Aaa", "publishingDate": "2016-12-12"}'
            -u admin:passw0rd

## Generating a certificate

* Generate a self-signed certificate
 
        openssl req 
           -x509 
        	-sha256 
        	-newkey rsa:2048 
        	-keyout certificate.key 
        	-out certificate.crt 
        	-days 365 -nodes

* Export it into PKCS#12 format

        openssl pkcs12 
          	-export 
         	-in certificate.crt 
         	-inkey certificate.key  
         	-out server.p12 
         	-name spray-book-catalog 
         	-password pass:passw0rd

PKCS12 offers some improvements over JKS keystores.

A PKCS12 keystore usually has a file extension of p12 or pfx.

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

## To Do

[ ] Investigate replacing __openssl__ / __keytool__ certificate process with __cfssl__

## Credits

This was the material for the course "Learning Scala Web Development", which can be found here:

	https://www.lynda.com/Scala-tutorials/Learning-Scala-Web-Development/521233-2.html

The course was orignally published here:

	https://www.packtpub.com/web-development/learning-scala-web-development-video

Although the course is only slightly more than a year old, the material has aged badly. For instance the [Spray framework](http://spray.io/) is now apparently deprecated and has been replaced by [Akka HTTP](http://doc.akka.io/docs/akka-http/current/scala/http/). Happily there is a migration guide:

	http://doc.akka.io/docs/akka-http/current/scala/http/migration-guide/migration-from-spray.html

The course itself was slightly frustrating - for instance, a good editor would have insisted on idiomatic English. The material itself referred in large part to this code and was more in line with an explanation of the code rather than an actual tutorial. The script was beautifully read by a professional reader, but obviously NOT a subject-matter expert - which made for a slightly jarring experience. Even so, I learned a few things and the editorial comments were interesting.
