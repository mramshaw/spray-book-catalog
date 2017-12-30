# Overview

Sample Book Catalog REST(ful) web service in __Scala__ with the __Spray__ framework.

![RESTful API](/images/Swagger_API.png)

API testing will be carried out with JUnit.

#### Spray

[Spray](http://spray.io/) is a pattern-matching Routing plug-in for Scala's Akka framework.

It seems very similiar to Python's Flask framework, or Golang's GorillaMux, or even node.js handlers.

It has its own DSL (Domain-Specific Language) for pattern-matching HTTP requests.

#### Swagger

![Swagger Logo](/images/swagger-logo.png)

[Swagger](https://swagger.io/) describes itself as the world's most popular API tooling.

The REST API will be documented with Swagger. The very useful Swagger UI rivals Postman.

As far as I can tell Swagger introspects on code annotations, albeit in a very useful way.

## To Run

Scala features the __sbt__ (Simple Build Tool). At first blush this seems to be yet another build/compile/test/run tool along the lines of __make__ or __ant__ or __maven__ or __gradle__. But really it is much more along the lines of __create-react-app__ in that it doesn't only download dependencies, it also creates a project directory. And of course it is anything but simple. On the plus side, it does at least enforce a standard project directory structure.

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

Or simply:

        $ sbt run

As usual, __Ctrl-C__ to terminate.

## Swagger UI location

        https://localhost:9001/

![Swagger UI](/images/Swagger_UI.png)

As the course material suggests, the Swagger UI is really great (and possibly the best thing about Swagger).

#### Firefox versus Chromium

Neither of these browsers will accept self-signed certificates unprompted. To use __Firefox__ it will be necessary to add a security exception (which should be removed later). As __Chromium__ will allow an easy override to proceed to the website anyway, it is probably the browser to use. This only saves a click or two but as it is one less thing to remember about, a useful saving. Also, Chromium will be very clear that the site is untrusted:

![Untrusted HTTPS](/images/Chromium_-_Untrusted_HTTPS.png)

For __expired__ certificates (surprisingly common these days) the procedure is much the same.

## Calling HTTPS endpoints

The HTTPS endpoints may also be verified with either __cURL__ or __httpie__ (first and second options below).

Although [httpie](https://github.com/jakubroztocil/httpie) is less verbose and has a somewhat more sensible call sequence, my preference is for __curl__ simply because of the __-k__ option (which allows for the ignoring of expired or self-signed security certificates - a bad practice to be sure, but acceptable for testing).

* with a certificate provided
 
        curl --cacert certificate.crt -vi https://localhost:9001/api/v1/books

        http --verify=certificate.crt https://localhost:9001/api/v1/books

* without a certificate provided

        curl -vik https://localhost:9001/api/v1/books

        http --verify=no https://localhost:9001/api/v1/books        

## Supported REST(ful) endpoints

#### Books

* Search for a book on Google Books:

        curl -vik https://localhost:9001/api/v1/search?query=scala

* Get all books:

        curl -vik https://localhost:9001/api/v1/books

* Get book by ISBN:

        curl -vik https://localhost:9001/api/v1/books/978-1935182757

* Update book by ISBN:
 
        curl -vik -X PUT https://localhost:9001/api/v1/books/978-1935182757 
             -H "Content-Type: application/json" 
             -d '{"author": "Thomas Alexandre", "title": "Scala for Java Developers", "publishingDate": "2016-12-12"}'
             -u admin:passw0rd

* Remove book from the catalog by ISBN:

        curl -vik -X DELETE https://localhost:9001/api/v1/books/978-1935182757 -u admin:passw0rd

#### Publishers

* Get all publishers:

        curl -vik https://localhost:9001/api/v1/publishers

* Add new publisher to book catalog:

        curl -vik -X POST https://localhost:9001/api/v1/publishers
             -H "Content-Type: application/json"
             -d '{"name": "Leanpub"}'
             -u admin:passw0rd

* Get publisher by publisher identifier:

        curl -vik https://localhost:9001/api/v1/publishers/1

* Update publisher by publisher identifier:

        curl -vik -X PUT https://localhost:9001/api/v1/publishers/1
             -H "Content-Type: application/json"
             -d '{"name": "Leanpub"}'
             -u admin:passw0rd

* Remove publisher by publisher identifier:

        curl -vik -X DELETE https://localhost:9001/api/v1/publishers/1 -u admin:passw0rd

* Get all books published by the publisher:

        curl -vik https://localhost:9001/api/v1/publishers/1/books

* Add new book published by the publisher:

        curl -vik -X POST https://localhost:9001/api/v1/publishers/2/books
             -H "Content-Type: application/json" 
             -d '{"isbn": "978-1935182757", "author": "Thomas Alexandre", "title": "Aaa", "publishingDate": "2016-12-12"}'
             -u admin:passw0rd

## Generating a certificate

In order to use __TLS__ we will need to create an __x509__ security certificate, along with infrastructure to house it.

By default, openssl will generate ```unable to write 'random state'``` messages if it is unable to write to the user's __.rand__ file (which is normally owned by __root__). There are a number of work-arounds but the simplest one is to tell openssl to use a different file (this avoids having to obtain __root__ permission):

        $ export RANDFILE=./.randfile

This file does not even need to be created, as openssl will create it itself if it cannot find it.

* Generate __.key__ and __.crt__ files:
 
        $ openssl req -x509         \
            -sha256                 \
            -newkey rsa:2048        \
            -keyout certificate.key \
            -out    certificate.crt \
            -days 365 -nodes

* Export our keys into a PKCS12 keystore:

        $ openssl pkcs12 -export        \
           -in       certificate.crt    \
           -inkey    certificate.key    \
           -out      server.p12         \
           -name     spray-book-catalog \
           -password pass:passw0rd

* Optionally, convert to PEM:

        $ openssl x509 -inform PEM -in certificate.crt > certificate.pem

* Import the certificate into JKS:

        $ keytool -importkeystore                       \
                  -srcstorepass  passw0rd               \
                  -destkeystore  spray-book-catalog.jks \
                  -deststorepass passw0rd               \
                  -srckeystore   server.p12             \
                  -srcstoretype  PKCS12                 \
                  -alias spray-book-catalog

PKCS12 keystores offer some improvements over JKS keystores (with [Java 9](http://openjdk.java.net/jeps/229) java keystores use PKCS12 rather than JKS however there may be interoperability problems due to Java's multi-keystore implementation. So it is probably a good idea to retain the original server.p12 keystore in case of compatibility issues).

A PKCS12 keystore usually has a file extension of p12 or pfx.

If desired, the temporary random file may be deleted:

        $ rm ./.randfile

Of course, leaving this file in the directory may be a good reminder for the next time.

## To Do

- [x] Investigate using self-signed certificates with __Firefox__ and __Chromium__
- [ ] Investigate whether both __PKCS12__ and __JKS__ keystores are really needed
- [ ] Investigate replacing __openssl__ / __keytool__ certificate process with [cfssl](https://github.com/mramshaw/cfssl)
- [ ] Investigate the latest crypto protocols with a view towards certificate generation
- [ ] Investigate the failing tests and fix them
- [ ] Investigate upgrading to HTTP2

## Credits

This was the material for the course "Learning Scala Web Development", which can be found here:

        https://www.lynda.com/Scala-tutorials/Learning-Scala-Web-Development/521233-2.html

The course was originally published here:

        https://www.packtpub.com/web-development/learning-scala-web-development-video

Although the course is only slightly more than a year old, the material has aged. For instance the [Spray framework](http://spray.io/) is now apparently deprecated and has been replaced by [Akka HTTP](http://doc.akka.io/docs/akka-http/current/scala/http/). Happily there is a migration guide:

        http://doc.akka.io/docs/akka-http/current/scala/http/migration-guide/migration-from-spray.html

The course itself was slightly frustrating - for instance, a good editor would have insisted on idiomatic English. The material itself referred in large part to this code and was more in line with an explanation of the code rather than an actual tutorial. The script was beautifully read by a professional reader, but obviously NOT a subject-matter expert - which made for a slightly jarring experience. Even so, I learned a few things and the editorial comments were interesting.
