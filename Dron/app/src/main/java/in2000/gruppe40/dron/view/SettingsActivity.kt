package in2000.gruppe40.dron.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import in2000.gruppe40.dron.R
import in2000.gruppe40.dron.model.SharedPreference
import kotlinx.android.synthetic.main.activity_settings.*



class SettingsActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        supportActionBar?.hide()

        val sp = SharedPreference(this)
        setContentView(R.layout.activity_settings)

        windSeekBar.setOnSeekBarChangeListener(this)
        rainSeekBar.setOnSeekBarChangeListener(this)
        fogSeekBar.setOnSeekBarChangeListener(this)

        if (sp.getFloat(sp.windTAG) == -1F) {
            windSeekBar.progress = 70
        } else {
            windSeekBar.progress = (sp.getFloat(sp.windTAG) * 10).toInt()
        }

        if (sp.getFloat(sp.rainTAG) == -1F) {
            rainSeekBar.progress = 55
        } else {
            rainSeekBar.progress = (sp.getFloat(sp.rainTAG) * 10).toInt()
        }

        if (sp.getFloat(sp.fogTAG) == -1F) {
            fogSeekBar.progress = 300
        } else {
            fogSeekBar.progress = (sp.getFloat(sp.fogTAG) * 10).toInt()
        }

        windSeekBarTextDisplay.text = (windSeekBar.progress * 0.1).format(1)
        rainSeekBarTextDisplay.text = (rainSeekBar.progress * 0.1).format(1)
        fogSeekBarTextDisplay.text = (fogSeekBar.progress * 0.1).format(1)


        val godkjenn = findViewById<Button>(R.id.SettingsGodkjenn)
        val avbryt = findViewById<Button>(R.id.SettingsAvbryt)

        godkjenn.setOnClickListener {
            sp.removeFloat(sp.windTAG)
            sp.saveFloat(sp.windTAG, (windSeekBar.progress / 10.0F))
            sp.removeFloat(sp.rainTAG)
            sp.saveFloat(sp.rainTAG, (rainSeekBar.progress / 10.0F))
            sp.removeFloat(sp.fogTAG)
            sp.saveFloat(sp.fogTAG, (fogSeekBar.progress / 10.0F))

            this.finish()
        }

        avbryt.setOnClickListener { view ->
            this.finish()
        }
    }



    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        //Her oppdaterer textView ved siden av baren
        when (seekBar) {
            windSeekBar -> {
                val vindProgress = (progress * 0.1).format(1)
                val vindSeekbarSetText = "$vindProgress m/s"
                windSeekBarTextDisplay.text = vindSeekbarSetText
            }
            rainSeekBar -> {
                val rainProgress = (progress * 0.1).format(1)
                val rainSeekbarSetText = "$rainProgress mm"
                rainSeekBarTextDisplay.text = rainSeekbarSetText
            }
            fogSeekBar -> {
                val siktProgress = (progress * 0.1).format(1)
                val siktSeekbarSetText = "$siktProgress %"
                fogSeekBarTextDisplay.text = siktSeekbarSetText
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    @SuppressLint("SetTextI18n")
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        when (seekBar) {
            windSeekBar -> {
                val vindSeekBarProgress = (windSeekBar.progress * 0.1).format(1)
                windSeekBarTextDisplay.text = "$vindSeekBarProgress m/s"

            }
            rainSeekBar -> {
                val rainSeekBarProgress = (rainSeekBar.progress * 0.1).format(1)
                rainSeekBarTextDisplay.text = "$rainSeekBarProgress mm"
            }
            fogSeekBar -> {
                val siktSeekBarProgress = (fogSeekBar.progress * 0.1).format(1)
                fogSeekBarTextDisplay.text = "$siktSeekBarProgress %"
            }
        }
    }


}

fun Double.format(digits: Int) = "%.${digits}f".format(this)