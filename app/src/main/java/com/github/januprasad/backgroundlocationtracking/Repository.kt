package com.github.januprasad.backgroundlocationtracking

import android.content.Context
import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class Repository private constructor(
    context: Context,
) {
    val locationChannel = Channel<Pair<String, String>>()

    fun updateLocation(location: Location) {
        val lat = location.latitude.toString().takeLast(3)
        val long = location.longitude.toString().takeLast(3)
        CoroutineScope(Dispatchers.IO).launch {
            locationChannel.send(Pair(lat, long))
        }
    }

    companion object : SingletonHolder<Repository, Context>(::Repository)
}
