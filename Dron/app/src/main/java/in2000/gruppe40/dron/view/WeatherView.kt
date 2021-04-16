package in2000.gruppe40.dron.view

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import in2000.gruppe40.dron.R
import in2000.gruppe40.dron.model.SharedPreference
import in2000.gruppe40.dron.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.weather_view_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    var lat = 0.0
    var lon = 0.0
    private val sp = SharedPreference(context)
    var toastCount = 0


    fun updatePos(loc: Location?) {
        if (loc != null) {
            this.lat = loc.latitude
            this.lon = loc.longitude
            refreshData(context)

        }
    }

    init {
        initialize(context)
    }

    private fun initialize(context: Context) {
        // Inflater viewet, ihht. dokumentasjon
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.weather_view_layout, this, true)


        refreshData(context)
    }

    private fun refreshData(context: Context) {
        //Launcher en co-routine for å kalle på en metode som gjør API-kall på en seperat tråd.
        GlobalScope.launch(Dispatchers.Main) {

            //sjekker at lokasjonen til brukeren ikke er utenfor Norges ytterpunkter.
            //https://no.wikipedia.org/wiki/Liste_over_norske_geografiske_ytterpunkter
            if ((lat > 57.836741 && lat < 80.874363) && (lon > -9.077461 && lon < 33.516317)) {
                val weatherViewModel = WeatherViewModel(lat, lon)
                val weatherDataMap = weatherViewModel.getMapOfWeatherData()
                val sunDataMap = weatherViewModel.getMapOfSunData()
                val rainDataMap = weatherViewModel.getMapOfRainData()

                //Oppdaterer weatherview med data fra API-kallet
                WindValue.text =
                    context.getString(R.string.WindValueText, weatherDataMap?.get("wind"))
                TempratureValue.text = weatherDataMap?.get("temp")
                val rainCheck = context.getString(R.string.RainValue, rainDataMap?.get("rainValue"))
                if (rainCheck == "-1.0 mm/t") {
                    RainValue.text = context.getString(R.string.Ukjent)
                } else {
                    RainValue.text = rainCheck
                }
                FogValue.text = weatherDataMap?.get("fog")
                SunsetValue.text = sunDataMap?.get("sunset")
                SunriseValue.text = sunDataMap?.get("sunrise")

                if (weatherDataMap?.get("wind")!!.toFloat() >= sp.getFloat(sp.windTAG)) {
                    WindValue.setTextColor(Color.RED)
                } else {
                    WindValue.setTextColor(Color.BLACK)
                }
                if (weatherDataMap.get("fog")!!.toFloat() >= sp.getFloat(sp.fogTAG)) {
                    FogValue.setTextColor(Color.RED)
                } else {
                    FogValue.setTextColor(Color.BLACK)
                }
                if (rainDataMap?.get("rainValue")!!.toFloat() >= sp.getFloat(sp.rainTAG)) {
                    RainValue.setTextColor(Color.RED)
                } else {
                    RainValue.setTextColor(Color.BLACK)
                }

                val theWindDir = weatherDataMap.get("windDir")

                when (theWindDir) {
                    "N" -> WindDirection.setImageResource(R.mipmap.n)
                    "S" -> WindDirection.setImageResource(R.mipmap.s)
                    "E" -> WindDirection.setImageResource(R.mipmap.e)
                    "W" -> WindDirection.setImageResource(R.mipmap.w)
                    "NW" -> WindDirection.setImageResource(R.mipmap.nw)
                    "SW" -> WindDirection.setImageResource(R.mipmap.sw)
                    "NE" -> WindDirection.setImageResource(R.mipmap.ne)
                    "SE" -> WindDirection.setImageResource(R.mipmap.se)
                    else -> {
                        WindDirection.setImageResource(R.mipmap.ae)
                    }
                }
            } else {
                if (toastCount > 0){

                    Toast.makeText(
                        context,
                        "Værdata ikke tilgjengelig utenfor Norge.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                toastCount++

                WindValue.text = "n/a"
                TempratureValue.text = "n/a"
                RainValue.text = "n/a"
                FogValue.text = "n/a"
                SunsetValue.text = "n/a"
                SunriseValue.text = "n/a"
            }
        }
    }
}
