**client-service**

A small Spring Boot project I'm building for the Distributed Information Systems course at BSUIR. It stores client records in a database and can read and write them as XML and JSON.

The plan is simple: take clients from an XML file (checked against an XSD schema), save them to PostgreSQL with Hibernate, and export them back to XML or JSON.

It's still in progress, see the status list at the bottom.

**Stack:** Java 21, Spring Boot, Spring Data JPA (Hibernate), PostgreSQL 16 (in Docker), JAXB / _xjc_ for XML, Gson for JSON, JUnit 5, Maven

**Data model:** three tables: client, city (dictionary), citizenship (dictionary)

The dictionary tables are filled automatically on the first start. A client is identified by passport series plus passport number, and that pair is unique.

**Client fields:** Last name, first name, middle name (required), Birth date (required), Passport series and number	(required), City of residence (from city)	(required), Residence address (required), Registration address (required), Citizenship (from citizenship)	(required), Mobile phone (not required), E-mail (not required), Employed (true/false) (not required), Position (not required).

The exact set of fields depends on the variant number from the assignment.

**Running it:** you need JDK 21 and Docker.
_docker compose up -d_
_./mvnw spring-boot:run_
On Windows use _mvnw.cmd_ instead of _./mvnw_.

The connection settings in _application.properties_ match _docker-compose.yml_. Tables are created by Hibernate on startup _(ddl-auto=update)_, which is fine for a study project but not something I'd use in production.

To stop the database: _docker compose stop_. To delete it together with its data: _docker compose down -v_.

**How XML is handled:** the JAXB classes are generated from the XSD by _xjc_ during _mvn compile_. They only describe the XML format. The database uses separate JPA entities, and a mapper converts between the two. This way the generated code can be rebuilt at any time without touching the entities.

**Status:**
[x] Entities and repositories
[x] Dictionary tables with initial data
XSD schema and sample XML
CRUD service
Import from XML to the database
Export from the database to XML
JSON import/export with Gson
Tests