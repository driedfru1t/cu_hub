package com.nikol.ui

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.ChronoField

fun Instant.toLocalizedStyle(
    dateStyle: FormatStyle = FormatStyle.LONG,
    timeStyle: FormatStyle = FormatStyle.SHORT,
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    val activeLocale = getActiveAppLocale()

    val formatter = DateTimeFormatterBuilder()
        .appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT)
        .appendLiteral(", ")
        .appendLocalized(dateStyle, timeStyle)
        .toFormatter(activeLocale)

    return this.atZone(zoneId).format(formatter)
}