package in2000.gruppe40.dron.model

data class Weatherdata (val created : String, val meta : Meta, val product : Product1)

data class Meta (val model : Model)

data class Model (
    val from : String,
    val name : String,
    val termin : String,
    val to : String,
    val runended : String,
    val nextrun : String
)

data class Product1 (
    val time : List<Time>
)

data class Time (
    val from : String,
    val to : String,
    val location : Location1,
    val datatype : String
)

data class Location1 (
    val windDirection : WindDirection,
    val lowClouds : LowClouds,
    val temperatureProbability : TemperatureProbability,
    val windGust : WindGust,
    val dewpointTemperature : DewpointTemperature,
    val cloudiness : Cloudiness,
    val altitude : Int,
    val temperature : Temperature,
    val mediumClouds : MediumClouds,
    val humidity : Humidity,
    val pressure : Pressure,
    val latitude : Double,
    val fog : Fog,
    val longitude : Double,
    val highClouds : HighClouds,
    val windSpeed : WindSpeed,
    val windProbability : WindProbability
)

data class WindSpeed (val id : String, val beaufort : Int, val mps : Double, val name : String)
data class Cloudiness (val id : String, val percent : Double)
data class Temperature (val value : Double, val unit : String, val id : String)
data class TemperatureProbability (val value : Int, val unit : String)
data class WindDirection (val id : String, val deg : Double, val name : String)
data class WindGust (val mps : Double, val id : String)
data class WindProbability (val unit : String, val value : Int)
data class Pressure (val value : Double, val unit : String, val id : String)
data class LowClouds (val percent : Double, val id : String)
data class MediumClouds (val id : String, val percent : Double)
data class DewpointTemperature (val value : Double, val unit : String, val id : String)
data class Fog (val percent : Double, val id : String)
data class HighClouds (val percent : Double, val id : String)
data class Humidity (val unit : String, val value : Double)