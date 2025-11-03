package com.bondy.bondybranch.core.serialization

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.util.Date
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializes [Date] instances using ISO-8601 strings in UTC.
 */
object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BondyDate", PrimitiveKind.STRING)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(decoder: Decoder): Date {
        val value = decoder.decodeString()
        return Date.from(Instant.parse(value))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(value.toInstant().toString())
    }
}
