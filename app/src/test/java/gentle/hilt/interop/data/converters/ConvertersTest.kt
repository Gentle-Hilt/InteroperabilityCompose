package gentle.hilt.interop.data.converters

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import gentle.hilt.interop.data.room.converters.Converters
import org.junit.Test

class ConvertersTest {

    @Test
    fun `fromString() should convert string to list`() {
        val value = "item1,item2,item3"
        val list = Converters().fromString(value)

        val listOf = listOf("1", "2", "3")
        assertThat(listOf.size).isEqualTo(list.size)
        assertThat("item1").isEqualTo(list[0])
        assertThat("item2").isEqualTo(list[1])
        assertThat("item3").isEqualTo(list[2])
    }

    @Test
    fun `fromList() should convert list to string`() {
        val list = listOf("1","2","3")
        val value = Converters().fromList(list)

        assertThat("1,2,3").isEqualTo(value)
    }
}