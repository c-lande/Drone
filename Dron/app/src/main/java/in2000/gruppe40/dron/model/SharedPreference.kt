package in2000.gruppe40.dron.model

import android.content.Context
import android.content.SharedPreferences

/*Denne klassen brukes for å lagre dataen lokalt på brukerens telefon
Verdiene lagres som key-values (Altså, som diccionaries i Python eller hashmaps i Java)
 Før man lagrer en verdi må man kalle på riktig metode. I første omgang har jeg laget metoder for String, Int og Float.
 Her er det også lurt å holde styr på hva man kaller KEY_NAME slik at man kan slette og gette verdier.*/

class SharedPreference(context: Context) {
    private val PREFS_NAME = "Useful_data"

    val windTAG = "WIND"
    val rainTAG = "RAIN"
    val fogTAG = "FOG"

    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME,
        Context.MODE_PRIVATE
    )


    fun saveFloat(KEY_NAME: String, value: Float){
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putFloat(KEY_NAME, value)
        editor.apply()
    }

    fun getFloat(KEY_NAME: String): Float{
        return sharedPref.getFloat(KEY_NAME, (-1.0).toFloat())
    }

    fun removeFloat(KEY_NAME: String){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }

}



