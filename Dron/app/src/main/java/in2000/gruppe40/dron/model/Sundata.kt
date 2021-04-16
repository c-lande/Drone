package in2000.gruppe40.dron.model


data class Sundata (
    val location : Location,
    val meta : Meta1
)
data class Location(
    val height:Int,
    val time:List<Time1>,
    val longitude:Double,
    val latitude:Double
)

data class Time1(
    val sunrise:Sunrise,
    val high_moon:High_moon,
    val moonrise:Moonrise,
    val moonposition:Moonposition,
    val solarmidnight:Solarmidnight,
    val moonphase:Moonphase,
    val sunset:Sunset,
    val solarnoon:Solarnoon,
    val moonset:Moonset,
    val low_moon:Low_moon,
    val moonshadow:Moonshadow,
    val date:String
)

data class Sunrise(
    val desc:String,
    val time:String
)
data class Sunset(
    val desc:String,
    val time:String
)

data class Meta1(
    val licenseurl:String
)
data class Moonset(
    val time:String,
    val desc:String
)
data class Moonphase(
    val value:Double,
    val desc:String,
    val time:String
)
data class Solarnoon(
    val desc:String,
    val elevation:Double,
    val time:String
)
data class Low_moon(
    val desc:String,
    val time:String,
    val elevation:Double
)

data class Solarmidnight(
    val elevation:Double,
    val time:String,
    val desc:String
)
data class Moonshadow(
    val time:String,
    val elevation:Double,
    val azimuth:Double,
    val desc:String
)
data class Moonrise(
    val desc:String,
    val time:String
)
data class High_moon(
    val desc:String,
    val time:String,
    val elevation:Double
)
data class Moonposition(
    val desc:String,
    val azimuth:Double,
    val range:Double,
    val phase:Double,
    val elevation:Double,
    val time:String
)
