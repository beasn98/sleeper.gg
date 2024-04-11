package com.mistershorr.loginandregistration

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.mistershorr.loginandregistration.databinding.ActivitySleepListBinding


class SleepListActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySleepListBinding
    private lateinit var sleepAdapter: SleepAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrieve()

    }

    private fun retrieve() {
        val userId = Backendless.UserService.CurrentUser().userId
        // need the ownerId to match the objectId of the user
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        // include the queryBuilder in the find function
        Backendless.Data.of(Sleep::class.java).find(queryBuilder, object : AsyncCallback<List<Sleep>?> { //got rid of nullable on sleep (idk)
            override fun handleResponse(foundSleep: List<Sleep>?) {
                Log.d("SleepListActivity", "handleResponse: $foundSleep")
                // all Sleep instances have been found return foundSleep
                if (foundSleep != null) {
                    val sleepAdapter = SleepAdapter(foundSleep)
                    binding.recyclerViewSleepListActivitySleepList.layoutManager = LinearLayoutManager(this@SleepListActivity)
                    binding.recyclerViewSleepListActivitySleepList.adapter = sleepAdapter
                }

            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d("SleepListActivity", "handleFault: ${fault.message}")
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        })
    }

    private fun saveToBackendless() {
        // the real use case will be to read from all the editText
        // fields in the detail activity and then use that info
        // to make the object

        // here, we'll just make up an object
        val sleep = Sleep(
            1711981800000, 1711953000000, 1711868400000, 10, "finally a restful night"
        )
        sleep.ownerId = Backendless.UserService.CurrentUser().userId

        Backendless.Data.of(Sleep::class.java).save(sleep, object : AsyncCallback<Sleep?> {
            override fun handleResponse(response: Sleep?) {
                // new Contact instance has been saved
            }

            override fun handleFault(fault: BackendlessFault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        })
    }


}