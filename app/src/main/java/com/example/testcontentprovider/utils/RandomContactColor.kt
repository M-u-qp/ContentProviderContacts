package com.example.testcontentprovider.utils

import androidx.compose.ui.graphics.Color
import com.example.testcontentprovider.ui.theme.pastelAquamarine
import com.example.testcontentprovider.ui.theme.pastelLavender
import com.example.testcontentprovider.ui.theme.pastelMint
import com.example.testcontentprovider.ui.theme.pastelOrange
import com.example.testcontentprovider.ui.theme.pastelPeach
import com.example.testcontentprovider.ui.theme.pastelPink
import com.example.testcontentprovider.ui.theme.pastelSalat
import com.example.testcontentprovider.ui.theme.pastelSalmon
import kotlin.math.abs

object RandomContactColor {
    val pastelColors = listOf(
        pastelPink,
        pastelMint,
        pastelPeach,
        pastelLavender,
        pastelSalat,
        pastelOrange,
        pastelAquamarine,
        pastelSalmon
    )

    fun getRandomColorForContact(name: String): Color {
        val hash = name.hashCode()
        return pastelColors[abs(hash) % pastelColors.size]
    }
}