# Automated Aussie Broadband speed test

Automated check using Aussie Broadband's _[Speed Test]_ utility.

The primary purpose of this _very_ simple script is to run and record speed test results on a schedule using any old
spare computer or Raspberry Pi.

## Browser automation

A remote Selenium Firefox webdriver is used to run the speed test and collect results.

## Data collection

Results are collected into an InfluxDB instance. Data collected includes:

* Download speed in `kbps`
* Upload speed in `kbps`
* Ping time in `ms`

## Running

The remote webdriver and InfluxDB are provided as Docker Compose services.

```shell
docker compose up
```

The InfluxDB UI can be accessed at <http://localhost:8086> using credentials

* Username: `speedtest`
* Password: `speedtest`

_Note:_ Credentials and other configuration is done via environment variables in
[docker-compose.yml](./docker-compose.yml).

### Gradle

You can build and run the application locally using any valid JDK 17.

```shell
./gradlew run
```

### Docker

Alternately, run the application via Docker Compose

```shell
docker compose run app
```

[speed test]: https://speed.aussiebroadband.com.au/
