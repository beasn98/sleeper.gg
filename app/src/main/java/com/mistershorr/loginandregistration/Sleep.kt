package com.mistershorr.loginandregistration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Sleep(
    var timeWokenMillis: Long = System.currentTimeMillis(),
    var timeSleptMillis: Long = System.currentTimeMillis(),
    var dateSleptMillis: Long = System.currentTimeMillis(),
    var sleepRating: Int = 5,
    var notes: String? = null,
    var ownerId: String? = null,
    var objectId: String? = null
) : Parcelable {

}