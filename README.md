# Felles dokumentløsninger Java module

Felles Java moduler som løser vanlige dokumentløsninger API behov. 

## Hvordan ta i bruk

Modulene blir publisert til GitHub Package Registry. For at maven/gradel skal hente modulene fra repository

Eksamplet nedenfør viser hvordan man trekker inne **jiraapi**

Maven:
```xml
<dependency>
    <groupId>no.nav.dok-common</groupId>
    <artifactId>jiraapi</artifactId>
    <version>INSERT_LATEST_VERSION</version>
</dependency>
```

### Hvordan release ny versjon

Så lenge [build-artifact](https://github.com/navikt/dok-workflows/blob/main/.github/workflows/build-artifact.yml) genererer build tags på ikke-semver format må vi sette versjon manuelt for hver release. Dette gjøres ved å endre release tag og tittel til ønsket versjon før release publiseres (tittel er strengt talt ikke nødvendig, men ryddig å endre til samme som tag).

### Contact
Spørsmål om koda eller prosjektet kan stillast på [Slack-kanalen for \#Team Dokumentløsninger](https://nav-it.slack.com/archives/C6W9E5GPJ).
