import net.rebux.jumpandrun.utils.MessageBuilder
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MessageBuilderTests {

  @Test
  fun throwsWithoutTemplate() {
    assertThrows<IllegalStateException> {
      MessageBuilder().build()
    }
  }

  @Test
  fun buildsSimpleMessage() {
    val expected = "message";
    assertEquals(
      expected,
      MessageBuilder()
        .template("message")
        .prefix(false)
        .buildSingle()
    )
  }

  @Test
  fun buildsMultilineMessage() {
    val expected = listOf("line1", "line2")
    assertEquals(
      expected,
      MessageBuilder()
        .template("line1\nline2")
        .prefix(false)
        .build()
    )
  }

  @Test
  fun buildsSimpleErrorMessage() {
    val expected = "§4error"
    assertEquals(
      expected,
      MessageBuilder()
        .template("error")
        .error()
        .prefix(false)
        .buildSingle()
    )
  }

  @Test
  fun buildsMultilineErrorMessage() {
    val expected = listOf("§4error1", "§4error2")
    assertEquals(
      expected,
      MessageBuilder()
        .template("error1\nerror2")
        .error()
        .prefix(false)
        .build()
    )
  }

  @Test
  fun insertsTemplateValue() {
    val expected = "value=2"
    assertEquals(
      expected,
      MessageBuilder()
        .template("value={value}")
        .values(
          mapOf(
            "value" to 2
          )
        )
        .prefix(false)
        .buildSingle()
    )
  }

  @Test
  fun buildsComplexMessage() {
    val expected = listOf("§4line1=1", "§4line2=2")
    assertEquals(
      expected,
      MessageBuilder()
        .template("line1={line1}\nline2={line2}")
        .values(
          mapOf(
            "line1" to 1,
            "line2" to 2
          )
        )
        .error()
        .prefix(false)
        .build()
    )
  }
}
