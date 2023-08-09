import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import org.openqa.selenium.By
import org.openqa.selenium.Capabilities
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.lang.System.getenv
import java.net.URL
import java.time.Duration
import java.time.Instant

const val URL = "https://speed.aussiebroadband.com.au/"

val BROWSER_URL = getenv("BROWSER_URL") ?: "http://localhost:4444"
val DB_URL = getenv("DB_URL") ?: "http://localhost:8086"
val DB_AUTH_TOKEN = getenv("DB_AUTH_TOKEN") ?: "speedtest-admin-token"
val DB_ORG = getenv("DB_ORG") ?: "speedtest"
val DB_BUCKET = getenv("DB_BUCKET") ?: "speedtest"

val BROWSER_OPTIONS: Capabilities = FirefoxOptions()
    .addArguments("--headless")

@Measurement(name = "speed")
data class Speed(
    @Column val download: Int,
    @Column val upload: Int,
    @Column val ping: Int,
    @Column(timestamp = true) val time: Instant
)

suspend fun main() {
    val driver = RemoteWebDriver(URL(BROWSER_URL), BROWSER_OPTIONS)
    driver.manage().timeouts().implicitlyWait(Duration.ofMinutes(1))

    driver.get(URL)
    val frame = driver.findElement(By.tagName("iframe"))
    driver.switchTo().frame(frame)

    val goButton = driver.findElement(By.tagName("button"))
    goButton.click()

    driver.switchTo().defaultContent()

    val (download, upload, ping) = driver.findElements(By.cssSelector("#results > strong:nth-child(n+2)"))
        .map { it.text.toInt() }
    println("Download: $download kbps, Upload: $upload kbps, Ping: $ping ms")

    val speed = Speed(download, upload, ping, Instant.now())

    val dbClient = InfluxDBClientKotlinFactory.create(DB_URL, DB_AUTH_TOKEN.toCharArray(), DB_ORG, DB_BUCKET)
    dbClient.getWriteKotlinApi().writeMeasurement(speed, WritePrecision.S)
    dbClient.close()
}