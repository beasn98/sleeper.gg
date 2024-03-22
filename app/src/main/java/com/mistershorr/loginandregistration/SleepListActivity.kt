package com.mistershorr.loginandregistration

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.mistershorr.loginandregistration.databinding.ActivitySleepListBinding


class SleepListActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySleepListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepListBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }

    fun retrieve() {
        Backendless.Data.of(Sleep::class.java).find(object : AsyncCallback<List<Sleep?>?> {
            override fun handleResponse(foundSleep: List<Sleep?>?) {
                Log.d("SleepListActivity", "handleResponse: $foundSleep")
                // all Contact instances have been found
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d("SleepListActivity", "handleFault: ${fault.message}")
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        })
    }


}