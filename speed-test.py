from collections import namedtuple
from dataclasses import dataclass
from datetime import datetime
from os import environ as env

from influxdb_client import InfluxDBClient
from influxdb_client.client.write_api import SYNCHRONOUS
from selenium import webdriver
from selenium.webdriver.common.by import By

firefox_options = webdriver.FirefoxOptions()
firefox_options.add_argument('--headless')

"""
Configuration
"""
browser_url = env.get('BROWSER_URL', 'http://localhost:4444')
db_url = env.get('DB_URL', 'http://localhost:8086')
db_token = env.get('DB_AUTH_TOKEN', 'speedtest-admin-token')
db_org = env.get('DB_ORG', 'speedtest')
db_bucket = env.get('DB_BUCKET', 'speedtest')
timeout = env.get('TIMEOUT', '120')

class Speed(namedtuple('Speed', ['download', 'upload', 'ping', 'timestamp'])):
    pass

def main():
    with webdriver.Remote(command_executor=browser_url, options=firefox_options) as driver:
        driver.implicitly_wait(float(timeout))

        print("Opening Aussie Broadband Speed Test")
        driver.get('https://speed.aussiebroadband.com.au/')

        frame = driver.find_element(By.TAG_NAME, 'iframe')
        driver.switch_to.frame(frame)

        go_button = driver.find_element(By.TAG_NAME, 'button')

        print("Starting test")
        go_button.click()

        driver.switch_to.default_content()

        print("Waiting for results...")
        results = driver.find_elements(By.CSS_SELECTOR, '#results > strong:nth-child(n+2)')
        assert len(results) >= 3, f'expected at least 3 stats, got {len(results)}'

        [download, upload, ping] = list(map(lambda el: int(el.text) if el.text.isdecimal() else None, results))

        print(f'Download: {download}kbps, Upload: {upload}kbps, Ping: {ping}ms')

    speed = Speed(
        download=download,
        upload=upload,
        ping=ping,
        timestamp=datetime.utcnow()
    )

    with InfluxDBClient(url=db_url, token=db_token, org=db_org) as client:
        write_api = client.write_api(write_options=SYNCHRONOUS)
        write_api.write(
            bucket=db_bucket,
            record=speed,
            record_field_keys=['download', 'upload', 'ping'],
            record_measurement_name='speed',
            record_time_key='timestamp'
        )

if __name__ == "__main__":
    main()