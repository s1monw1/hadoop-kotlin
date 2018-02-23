import au.com.bytecode.opencsv.CSVParser
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals


class CSVProcessorTest {

    private val LINE =
        """2018-02-23T11:15:28.870Z,33.2383333,-117.1448333,3.12,0.83,ml,10,302,0.2614,0.14,ci,ci38114232,2018-02-23T11:19:09.464Z,10km"""

    @Test
    fun testReadingOneLine() {
        val lines = CSVParser().parseLine(LINE)
        assertEquals(33.2383333.toString(), lines[1])
        val dtf = DateTimeFormatter.ISO_DATE_TIME
        val ta = dtf.parse(lines[0])
        assertEquals("2018-02-23", LocalDate.from(ta).toString())

    }

}