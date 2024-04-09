package com.mistershorr.loginandregistration

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset.UTC
import java.time.ZoneOffset.ofHours
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class SleepAdapter (var dataSet: List<Sleep>) : RecyclerView.Adapter<SleepAdapter.ViewHolder>() {

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
        val sleep = dataSet[position]
        val context = holder.layout.context


        val formatter = DateTimeFormatter.ofPattern("yyy-MM-dd")
        val sleepDate = LocalDateTime.ofEpochSecond(sleep.dateSleptMillis/1000, 0,
            ZoneId.systemDefault().rules.getOffset(Instant.now()))
        holder.textViewDate.text = formatter.format(sleepDate)

        // calculate the difference in time from bed to wake and convert to hours & minutes
        // use String.format() to display it in HH:mm format in the duration textview
        // hint: you need leading zeroes and a width of 2




        // sets the actual hours slept textview
        val bedTime = LocalDateTime.ofEpochSecond(sleep.dateSleptMillis/1000, 0,
            ZoneId.systemDefault().rules.getOffset(Instant.now()))
        val wakeTime = LocalDateTime.ofEpochSecond(sleep.timeWokenMillis/1000, 0,
            ZoneId.systemDefault().rules.getOffset(Instant.now()))
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        holder.textViewHours.text = "${timeFormatter.format(bedTime)} - ${timeFormatter.format(wakeTime)}"

        holder.ratingBarQuality.rating = sleep.sleepRating.toFloat()


        holder.layout.setOnClickListener {
            val intent = Intent(context, SleepDetailActivity::class.java).apply {
                putExtra(SleepDetailActivity.EXTRA_SLEEP, sleep)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}