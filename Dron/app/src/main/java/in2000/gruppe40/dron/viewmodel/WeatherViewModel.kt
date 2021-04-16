package in2000.gruppe40.dron.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import in2000.gruppe40.dron.model.Raindata
import in2000.gruppe40.dron.model.Sundata
import in2000.gruppe40.dron.model.Weatherdata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class WeatherViewModel(private val lat: Double, private val lon:Double) : ViewModel() {


    suspend fun getMapOfWeatherData(): MutableMap<String, String>? {
        val weatherDataMap: MutableMap<String, String> = mutableMapOf()
        try {
            withContext(Dispatchers.IO) {

                val weatherUrl = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/1.9/.json?lat=$lat&lon=$lon"
                val currentTimeAsText = getCurrentTime()

                val weatherData =
                    Gson().fromJson(Fuel.get(weatherUrl).awaitString(), Weatherdata::class.java)

                val currentWeatherdataTime =
                    weatherData.product.time.find { time -> time.from == currentTimeAsText }

                val cTime = currentWeatherdataTime?.to
                val cWind = currentWeatherdataTime?.location?.windSpeed?.mps
                val cTemp = currentWeatherdataTime?.location?.temperature?.value
                val cWindDir = currentWeatherdataTime?.location?.windDirection?.name
                val cFog = currentWeatherdataTime?.location?.fog

                if (cTime == null) {
                    weatherDataMap["wind"] = "n/a"
                    weatherDataMap["temp"] = "n/a"
                    weatherDataMap["windDir"] = "n/a"
                    weatherDataMap["fog"] = "n/a"
                } else {
                    weatherDataMap["wind"] = cWind.toString()
                    weatherDataMap["temp"] = cTemp.toString()
                    weatherDataMap["windDir"] = cWindDir.toString()
                    weatherDataMap["fog"] = cFog?.percent.toString()
                }
            }
            return weatherDataMap
        }catch (e: Exception){
            weatherDataMap["wind"] = "n/a"
            weatherDataMap["temp"] = "n/a"
            weatherDataMap["windDir"] = "n/a"
            weatherDataMap["fog"] = "n/a"
            return weatherDataMap
        }
    }

    @SuppressLint("SimpleDateFormat")
    suspend fun getMapOfSunData(): MutableMap<String, String>? {
        val sunDataMap: MutableMap<String, String> = mutableMapOf()
        try {
            withContext(Dispatchers.IO) {
                val calendar:Calendar=Calendar.getInstance()
                val timeFormat = SimpleDateFormat("yyyy-MM-dd")
                val cDate= timeFormat.format(calendar.time)

                val sunsetUrl="https://in2000-apiproxy.ifi.uio.no/weatherapi/sunrise/2.0/.json?lat=$lat&lon=$lon&date=$cDate&offset=+01:00"

                val sunsetData=
                    Gson().fromJson(Fuel.get(sunsetUrl).awaitString(), Sundata::class.java)

                var sunrise = sunsetData?.location?.time?.get(0)?.sunrise?.time
                var sunset = sunsetData?.location?.time?.get(0)?.sunset?.time
                sunrise = sunrise?.substring(11, 16)
                sunset = sunset?.substring(11, 16)

                if (sunrise == null || sunset == null) {
                    sunDataMap["sunset"] = "Ukjent"
                    sunDataMap["sunrise"] = "Ukjent"
                } else {
                    sunDataMap["sunset"] = sunset
                    sunDataMap["sunrise"] = sunrise
                }
            }
            return sunDataMap
        }catch (e:Exception){
            sunDataMap["sunset"] = "n/a"
            sunDataMap["sunrise"] = "n/a"
            return sunDataMap
        }
    }

    suspend fun getMapOfRainData(): MutableMap<String, String>? {
        val rainDataMap: MutableMap<String, String> = mutableMapOf()
        try{
            withContext(Dispatchers.IO) {
                val rainUrl =
                    "https://in2000-apiproxy.ifi.uio.no/weatherapi/nowcast/0.9/.json?lat=$lat&lon=$lon"

                val rainData =
                    Gson().fromJson(Fuel.get(rainUrl).awaitString(), Raindata::class.java)

                val currentTimeAsText = getPrevHour()
                val currentRaindataTime =
                    rainData.product.time.find { time -> time.from == currentTimeAsText }

                val rain = currentRaindataTime?.location?.precipitation?.value

                if (rain == null) {
                    rainDataMap["rainValue"] = "n/a"
                } else {
                    rainDataMap["rainValue"] = rain.toString()
                }
            }
            return rainDataMap
        }catch (e:Exception){
            rainDataMap["rainValue"] = "-1.0"
            return rainDataMap
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentTime(): String{
        val calendar: Calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH':00:00Z'")
        return timeFormat.format(calendar.time)

    }
    @SuppressLint("SimpleDateFormat")
    private fun getPrevHour(): String{
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, -1)

        val timeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH':00:00Z'")
        return timeFormat.format(calendar.time)
    }
}

