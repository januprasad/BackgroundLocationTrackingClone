package com.github.januprasad.backgroundlocationtracking

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    val mutableSharedFlow = MutableSharedFlow<Pair<String, String>>()

    fun startLocationUpdates(context: Context) {
        val repo = Repository.getInstance(context)
        viewModelScope.launch(Dispatchers.IO) {
            repo.locationChannel.consumeAsFlow().collectLatest { data ->
                Log.v("Lat & Long", "${data.first} ${data.second}")
                mutableSharedFlow.emit(data)
            }
        }
    }
}
