package in2000.gruppe40.dron.model

data class Raindata (
    val product : Product2,
    val meta : Meta2,
    val created : String
)

data class Meta2 (
    val model : Model2
)

data class Product2 (
    val time : List<Time2>
)

data class Time2 (
    val from : String,
    val location : Location2,
    val to : String,
    val datatype : String
)

data class Model2 (
    val from : String,
    val termin : String,
    val to : String,
    val name : String,
    val runended : String,
    val nextrun : String
)

data class Location2 (
    val longitude : Double,
    val latitude : Double,
    val precipitation : Precipitation2
)

data class Precipitation2 (
    val unit : String,
    val value : Double
)
