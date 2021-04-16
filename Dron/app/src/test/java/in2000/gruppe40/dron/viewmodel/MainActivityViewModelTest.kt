package in2000.gruppe40.dron.viewmodel

import in2000.gruppe40.dron.model.Time
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class MainActivityViewModelTest {

    @Test
    fun getWindSpeed() {

        val timeResponse = mockk<Time>()
            every { timeResponse.location.windSpeed.mps } returns 10.0
            //every { timeResponse.location.precipitation.value } returns 5.0
            every { timeResponse.location.temperature.value } returns 27.0
            every { timeResponse.to } returns "10.0"


        assertEquals(timeResponse.location.windSpeed.mps, 10.0, 0.01)
        //assertEquals(timeResponse.location.precipitation.value, 5.0, 0.01)
        assertEquals(timeResponse.location.temperature.value, 27.0, 0.01)
        assertEquals("10.0", timeResponse.to )
    }



}

