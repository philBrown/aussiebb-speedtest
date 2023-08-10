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

## Setup

The remote webdriver and InfluxDB are provided as Docker Compose services.

```shell
docker compose up
```

The InfluxDB UI can be accessed at <http://localhost:8086> using credentials

* Username: `speedtest`
* Password: `speedtest`

_Note:_ Credentials and other configuration is done via environment variables in
[docker-compose.yml](./docker-compose.yml).

### Configuration Environment Variables

| Name            | Description                                   | Default value           |
| --------------- | ----------------------------------------      | ----------------------- |
| `BROWSER_URL`   | Location of the remote Firefox webdriver      | `http://localhost:4444` |
| `DB_URL`        | Location of the InfluxDB                      | `http://localhost:8086` |
| `DB_AUTH_TOKEN` | InfluxDB auth token                           | `speedtest-admin-token` |
| `DB_ORG`        | InfluxDB organisation                         | `speedtest`             |
| `DB_BUCKET`     | InfluxDB bucket for speedtest results         | `speedtest`             |
| `TIMEOUT`       | Max wait time (seconds) for results to appear | `120`                   |

## Running

Install dependencies...

```shell
pip install -r requirements.txt
```

and run...

```shell
python speed-test.py
```

### Docker

Alternately, run the application via Docker Compose

```shell
docker compose run app
```

[speed test]: https://speed.aussiebroadband.com.au/
