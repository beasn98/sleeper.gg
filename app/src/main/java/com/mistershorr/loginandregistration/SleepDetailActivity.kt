package com.mistershorr.loginandregistration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.mistershorr.loginandregistration.databinding.ActivitySleepDetailBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter

class SleepDetailActivity : AppCompatActivity() {

    companion object {
        val TAG = "SleepDetailActivity"
    }

    private lateinit var binding: ActivitySleepDetailBinding
    lateinit var bedTime: LocalDateTime
    lateinit var wakeTime: LocalDateTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sleep = intent.getParcelableExtra<Sleep>(SleepAdapter.EXTRA_SLEEP)

        // these are default values that should be set when creating a new entry
        // however, if editing an existing entry, those values should be used instead

        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE MMM dd, yyyy")

        Log.d(TAG, "onCreate: $sleep")

        if (sleep != null) {
            bedTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(sleep.timeSleptMillis), UTC)
            binding.buttonSleepDetailBedTime.text = timeFormatter.format(bedTime)
            wakeTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(sleep.timeWokenMillis), UTC)
            binding.buttonSleepDetailWakeTime.text = timeFormatter.format(wakeTime)

            binding.editTextTextMultiLineSleepDetailNotes.setText(sleep.notes)
            binding.ratingBarSleepDetailQuality.rating = sleep.sleepRating.toFloat()/2

            binding.buttonSleepDetailDate.text = dateFormatter.format(bedTime)

        }
        else {
            bedTime = LocalDateTime.now()
            binding.buttonSleepDetailBedTime.text = timeFormatter.format(bedTime)
            wakeTime = bedTime.plusHours(8)
            binding.buttonSleepDetailWakeTime.text = timeFormatter.format(wakeTime)
            binding.buttonSleepDetailDate.text = dateFormatter.format(bedTime)
        }

        binding.buttonSleepDetailBedTime.setOnClickListener {
            setTime(bedTime, timeFormatter, binding.buttonSleepDetailBedTime)
        }

        binding.buttonSleepDetailWakeTime.setOnClickListener {
            setTime(wakeTime, timeFormatter, binding.buttonSleepDetailWakeTime)
        }

        binding.buttonSleepDetailDate.setOnClickListener {
            val selection = bedTime.toEpochSecond(ZoneOffset.UTC)
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(selection * 1000) // requires milliseconds
                .setTitleText("Select a Date")
                .build()

            Log.d(
                TAG,
                "onCreate: after build: ${
                    LocalDateTime.ofEpochSecond(
                        datePicker.selection ?: 0L,
                        0,
                        ZoneOffset.UTC
                    )
                }"
            )
            datePicker.addOnPositiveButtonClickListener { millis ->
                val selectedLocalDate =
                    Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDateTime()
                Toast.makeText(
                    this,
                    "Date is: ${dateFormatter.format(selectedLocalDate)}",
                    Toast.LENGTH_SHORT
                ).show()

                // make sure that waking up the next day if waketime < bedtime is preserved
                var wakeDate = selectedLocalDate

                if (wakeTime.dayOfMonth != bedTime.dayOfMonth) {
                    wakeDate = wakeDate.plusDays(1)
                }

                bedTime = LocalDateTime.of(
                    selectedLocalDate.year,
                    selectedLocalDate.month,
                    selectedLocalDate.dayOfMonth,
                    bedTime.hour,
                    bedTime.minute
                )

                wakeTime = LocalDateTime.of(
                    wakeDate.year,
                    wakeDate.month,
                    wakeDate.dayOfMonth,
                    wakeTime.hour,
                    wakeTime.minute
                )
                binding.buttonSleepDetailDate.text = dateFormatter.format(bedTime)
            }
            datePicker.show(supportFragmentManager, "datepicker")
        }

        binding.buttonSleepDetailSave.setOnClickListener {
            if (sleep == null) {
                val newSleep = Sleep(
                    wakeTime.toEpochSecond(UTC) * 1000,
                    bedTime.toEpochSecond(UTC) * 1000,
                    wakeTime.toEpochSecond(UTC) * 1000,
                    binding.ratingBarSleepDetailQuality.rating.toInt() * 2,
                    binding.editTextTextMultiLineSleepDetailNotes.text.toString(),
                    Backendless.UserService.CurrentUser().userId
                )
                saveToBackendless(newSleep)

            }
            else {
                saveToBackendless(sleep)
            }
            finish()
        }

        binding.buttonSleepDetailCancel.setOnClickListener {
            finish()
        }

    }



    fun setTime(time: LocalDateTime, timeFormatter: DateTimeFormatter, button: Button) {
        val timePickerDialog = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(time.hour)
            .setMinute(time.minute)
            .build()

        timePickerDialog.show(supportFragmentManager, "bedtime")
        timePickerDialog.addOnPositiveButtonClickListener {
            var selectedTime = LocalDateTime.of(
                time.year,
                time.month,
                time.dayOfMonth,
                timePickerDialog.hour,
                timePickerDialog.minute
            )
            button.text = timeFormatter.format(selectedTime)
            when (button.id) {
                binding.buttonSleepDetailBedTime.id -> {
                    bedTime = selectedTime
                    if (wakeTime.toEpochSecond(UTC) < selectedTime.toEpochSecond(UTC)) {
                        wakeTime = wakeTime.plusDays(1)
                    }
                }

                binding.buttonSleepDetailWakeTime.id -> {
                    if (selectedTime.toEpochSecond(UTC) < bedTime.toEpochSecond(UTC)) {
                        selectedTime = selectedTime.plusDays(1)
                    }
                    wakeTime = selectedTime
                }
            }
        }
    }

    private fun saveToBackendless(newSleep: Sleep) {
        // the real use case will be to read from all the editText
        // fields in the detail activity and then use that info
        // to make the object

        Backendless.Data.of(Sleep::class.java).save(newSleep, object : AsyncCallback<Sleep?> {
            override fun handleResponse(response: Sleep?) {
                Toast.makeText(this@SleepDetailActivity, "Object added", Toast.LENGTH_SHORT).show()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault: ${fault.message}")
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        })
    }

}