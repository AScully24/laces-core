package com.laces.core.security.component

import org.apache.commons.lang3.time.DateUtils.*
import java.util.*

fun expireInDays(days: Int): Date {
    var toReturn = Date(Calendar.getInstance().time.time)
    toReturn = setHours(toReturn,0)
    toReturn = setMinutes(toReturn,0)
    toReturn = setSeconds(toReturn,0)
    toReturn = setMilliseconds(toReturn,0)
    toReturn = addDays(toReturn, days)
    return toReturn
}


fun expireInMinutes(minutes: Int): Date {
    val time = Calendar.getInstance().time.time
    return addMinutes(Date(time), minutes)
}
