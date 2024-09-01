import net.rebux.jumpandrun.utils.TickFormatter
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TickFormatterTests {

    @Test
    fun testSingleTick() {
        val expected = Pair("00.050", SECONDS)
        assertEquals(expected, TickFormatter.format(1))
    }

    @Test
    fun testMultipleTicks() {
        val expected = Pair("03.250", SECONDS)
        assertEquals(expected, TickFormatter.format(65))
    }

    @Test
    fun testZeroTicks() {
        val expected = Pair("00.000", SECONDS)
        assertEquals(expected, TickFormatter.format(0))
    }

    @Test
    fun testNegativeTicks() {
        val expected = Pair("-00.050", SECONDS)
        assertEquals(expected, TickFormatter.format(-1))
    }

    @Test
    fun testLargeNumberOfTicks() {
        val expected = Pair("01.00.000", MINUTES)
        assertEquals(expected, TickFormatter.format(1200))
    }

    @Test
    fun testLargeNumberOfNegativeTicks() {
        val expected = Pair("-01.00.000", MINUTES)
        assertEquals(expected, TickFormatter.format(-1200))
    }
}
