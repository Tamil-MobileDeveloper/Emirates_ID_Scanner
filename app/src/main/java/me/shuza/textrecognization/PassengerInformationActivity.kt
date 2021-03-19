package me.shuza.textrecognization

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_passenger_information.*
import kotlinx.android.synthetic.main.activity_pattern_check.*
import java.util.regex.Pattern

const val ARG_ID_NUMBER = "passenger_id"
const val ARG_NAME = "passenger_name"

class PassengerInformationActivity : AppCompatActivity() {
    private var name: String? = null
    private var idNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_information)
        handleIntentValues()
        tv_name.text = name
        tv_id.text = idNumber
    }

    private fun handleIntentValues() {
        intent?.extras?.let {
            name = it.getString(ARG_NAME)
            idNumber = it.getString(ARG_ID_NUMBER)
        }
    }

    override fun onResume() {
        super.onResume()
        btnSaveAndContinue.setOnClickListener {
            validation()
        }
    }

    private fun validation() {
        if (tv_name.text.isEmpty()) {
            showToast("Please enter Name")

        } else if (tv_id.text.isEmpty()) {
            showToast("Please enter id")

        } else if (edit_ph_number.text.isEmpty() || edit_ph_number.text.length > 10 || edit_ph_number.text.length < 10) {
            showToast("Please enter valid phoneNumber")

        } else {
            showToast(" Details Saved!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        )
                .show()
    }
}