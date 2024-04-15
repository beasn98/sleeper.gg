package com.mistershorr.loginandregistration

import android.content.Intent
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

    companion object {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingActionButtonSleepListActivityNewSleep.setOnClickListener {
            var sleepIntent = Intent(this@SleepListActivity, SleepDetailActivity::class.java)
            this@SleepListActivity.startActivity(sleepIntent)
        }
    }

    override fun onResume() {
        super.onResume()
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
                    val sleepAdapter = SleepAdapter(foundSleep as MutableList<Sleep>)
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


}