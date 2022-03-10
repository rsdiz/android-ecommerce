package id.rsdiz.rdshop.base.utils

import org.threeten.bp.DateTimeUtils
import org.threeten.bp.OffsetDateTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * Convert the string with pattern [FormatPattern.DATE_FORMAT_PATTERN] to OffsetDateTime
 */
fun String.toOffsetDateTime(): OffsetDateTime = OffsetDateTime.parse(this)

/**
 * Convert OffsetDateTime to Date
 */
fun OffsetDateTime.toDate(): Date = DateTimeUtils.toDate(this.toInstant())

/**
 * Get Date from OffsetDateTime and then return as string
 */
fun OffsetDateTime.stringDate(): String = SimpleDateFormat(FormatPattern.LOCAL_DATE, Locale.getDefault()).format(this.toDate())

/**
 * Get Time from OffsetDateTime and then return as string
 */
fun OffsetDateTime.stringTime(): String = SimpleDateFormat(FormatPattern.LOCAL_TIME, Locale.getDefault()).format(this.toDate())

/**
 * The Format of default pattern date will use in the apps
 */
object FormatPattern {
    const val DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX"
    const val LOCAL_DATE = "dd MMMM yyyy"
    const val LOCAL_TIME = "HH:mm:ss"
}
