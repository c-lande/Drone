package in2000.gruppe40.dron

import in2000.gruppe40.dron.model.Time
import in2000.gruppe40.dron.viewmodel.WeatherViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test

class MainActivityTest {

    @Test
    fun first() {
        assert(true)
    }


    @Test
    fun weatherViewTest() {
        val wvModel = mockk<WeatherViewModel>()
        
        every {
            GlobalScope.launch(Dispatchers.Main) { wvModel.getMapOfWeatherData() }
        }

    }

}