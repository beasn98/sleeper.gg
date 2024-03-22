package com.mistershorr.loginandregistration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Sleep(
    var timeWoken: Date = Date(),
    var timeSlept: Date = Date(),
    var dateSlept: Date = Date(),
    var sleepRating: Int = 5,
    var notes: String? = null
) : Parcelable {

}