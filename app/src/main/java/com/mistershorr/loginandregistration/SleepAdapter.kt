package com.mistershorr.loginandregistration

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class SleepAdapter(var sleepList: MutableList<Sleep>) : RecyclerView.Adapter<SleepAdapter.ViewHolder>() {

    companion object {
        val TAG = "SleepAdapter"
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewDate : TextView
        val textViewHours: TextView
        val textViewDuration: TextView
        val layout : ConstraintLayout
        val ratingBarQuality : RatingBar
        init {
            textViewDate = view.findViewById(R.id.textView_itemSleep_date)
            textViewDuration = view.findViewById(R.id.textView_itemSleep_duration)
            textViewHours = view.findViewById(R.id.textView_itemSleep_hours)
            layout = view.findViewById(R.id.layout_itemSleep)
            ratingBarQuality = view.findViewById(R.id.ratingBar_itemSleep_sleepQuality)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sleep, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: SleepAdapter.ViewHolder, position: Int) {
        val sleep = sleepList[position]
        val context = holder.layout.context


        val formatter = DateTimeFormatter.ofPattern("yyy-MM-dd")
        val sleepDate = LocalDateTime.ofEpochSecond(sleep.dateSleptMillis/1000, 0,
            ZoneId.systemDefault().rules.getOffset(Instant.now()))
        holder.textViewDate.text = formatter.format(sleepDate)

        // calculate the difference in time from bed to wake and convert to hours & minutes
        // use String.format() to display it in HH:mm format in the duration textview
        // hint: you need leading zeroes and a width of 2
        val totalSeconds = (sleep.timeWokenMillis-sleep.timeSleptMillis)/1000
        val hours = totalSeconds/60/60 % 24
        val minutes = (totalSeconds/60)%60
        val duration = "%02d:%02d".format(hours,minutes)

        holder.textViewDuration.text = duration



        // sets the actual hours slept textview
        val bedTime = LocalDateTime.ofEpochSecond(sleep.timeSleptMillis/1000, 0,
            ZoneId.systemDefault().rules.getOffset(Instant.now()))
        val wakeTime = LocalDateTime.ofEpochSecond(sleep.timeWokenMillis/1000, 0,
            ZoneId.systemDefault().rules.getOffset(Instant.now()))
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        holder.textViewHours.text = "${timeFormatter.format(bedTime)} - ${timeFormatter.format(wakeTime)}"

        holder.ratingBarQuality.rating = sleep.sleepRating/2.0.toFloat()



        //popup menu
        holder.layout.isLongClickable = true
        holder.layout.setOnLongClickListener {
            val popMenu = PopupMenu(context, holder.ratingBarQuality)
            popMenu.inflate(R.menu.menu_sleep_list_context)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_sleeplist_delete -> {
                        deleteFromBackendless(position)
                        true
                    }
                    else -> true
                }
            }
            popMenu.show()
            true
        }


        holder.layout.setOnClickListener {
            val intent = Intent(context, SleepDetailActivity::class.java).apply {
                putExtra(SleepDetailActivity.EXTRA_SLEEP, sleep)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return sleepList.size
    }

    private fun deleteFromBackendless(position: Int) {
        Log.d(TAG, "deleteFromBackendless: Trying to delete ${sleepList[position]}")
        // put in the code to delete the item using the callback from Backendless
        // in the handleResponse, we'll need to also delete the item from the sleepList
        // and make sure that the recyclerview is updated

        Backendless.Data.of(Sleep::class.java).save(sleepList[position], object : AsyncCallback<Sleep?> {
            override fun handleResponse(savedContact: Sleep?) {
                Backendless.Data.of(Sleep::class.java).remove(savedContact,
                    object : AsyncCallback<Long?> {
                        override fun handleResponse(response: Long?) {
                            Log.d(TAG, "handleResponse: Object has been deleted")
                            sleepList.removeAt(position)
                            notifyDataSetChanged()

                            // Contact has been deleted. The response is the
                            // time in milliseconds when the object was deleted
                        }

                        override fun handleFault(fault: BackendlessFault) {
                            Log.d(TAG, "handleFault: ${fault.message}")
                            // an error has occurred, the error code can be 
                            // retrieved with fault.getCode()
                        }
                    })
            }

            override fun handleFault(fault: BackendlessFault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()

                Log.d(TAG, "handleFault: That's too bad")
            }
        })
    }

}