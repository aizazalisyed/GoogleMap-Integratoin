package java.com.googlemapsandroid

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

val TAG = "MainActivity"
val Error_DIALOGUE_REQUEST = 9001
lateinit var button: Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.btnMap)

        if (isServicesOK()) {
            init()
        }

    }

    fun init() {
        button.setOnClickListener {
            intent = Intent(this@MainActivity, MapActivity::class.java)
            startActivity(intent)
        }
    }

    fun isServicesOK(): Boolean {
        Log.d(TAG, "isServiceOK : Checking google services")
        var available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and user can make map request
            Log.d(TAG, "Google Play Service Is Working")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but we can resolve it (version issue)
            Log.d(TAG, "error occurred but we can resolve it")
            var dialogue = GoogleApiAvailability.getInstance()
                .getErrorDialog(this, available, Error_DIALOGUE_REQUEST)
            dialogue?.show()
        } else {
            Log.d(TAG, "you can't make map request")
        }
        return false
    }

}