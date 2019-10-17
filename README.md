# Captcha_Service

The web-service that generates an image with question and processing text
answers. Just a generic Java EE application with custom server auth
module (SAM) implementation.

Java EE techs — JASPIC, JPA 2.0, JSF 2.2, JAX-WS, JAX-RS.

Tested on TomEE 7.1, WildFly 10.1 and GlassFish 4.

## Build requirements

- application.${config.type}.properties — application properties for selected
  build profile. Defined profiles are "Development" and "Production".
  See pom.xml for details.

## Run requirements

- Java EE 7 compilant application server.
- Database with already created tables as specified in the
  META-INF/sql/create.sql

### Notes
- Has creepy design :-)
- Designed for maximum portability (in simple meaning — does not use of the
  vendor-specific features). However the persistence.xml lacks portability
  due to JPA providers imperfections.
- Default administrator account, which creates from META-INF/sql/create.sql
  has login "admin" and password "nimda".
