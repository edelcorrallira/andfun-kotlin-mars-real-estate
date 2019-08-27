/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Enum that is used to represent Network state.
 * Loading - When the data is still loading
 * Done - Successfully downloaded images, ready for display.
 * Error - A network error has prevented us from loading the images.
 */
enum class MarsApiStatus { LOADING, DONE, ERROR }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    //considering response data mainly contains response status it was refactored for clarity
    private val _status = MutableLiveData<MarsApiStatus>()

    // The external immutable LiveData for the response String
    val status: LiveData<MarsApiStatus>
        get() = _status

    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()

    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty

    /**
     * This navigation function is a setter that defines which MarsProperty object will be
     * shown in detail.
     * @param marsProperty Item that was clicked on.
     */
    fun displayPropertyDetails(marsProperty: MarsProperty){
        _navigateToSelectedProperty.value = marsProperty
    }

    /**
     * This navigation function clears MarsProperty object once navigation is completed.
     */
    fun displayPropertyDetailsComplete(){
        _navigateToSelectedProperty.value = null
    }

    // Actual property information
    private val _properties = MutableLiveData<List<MarsProperty>>()

    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    // In order to use our deferred (and hence coroutines) we create a Job
    private var viewModelJob = Job()
    // This job runs on the main (UI) thread
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        //We must be inside our co-routine scope in order to use our Deferred
        coroutineScope.launch {
            //While code is still being executed on the main thread, coroutines manage concurrency
            var getPropertiesDeferred = MarsApi.retrofitService.getProperties(filter.value)
            try {
                _status.value = MarsApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _status.value = MarsApiStatus.DONE
                _properties.value = listResult
            } catch (e: Exception) {
//              Invoked when a network exception occurred talking to the server or when an unexpected
//              exception occurred creating the request or processing the response
                _status.value = MarsApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }

    /**
     * This method is canceling any unfinished Jobs when the ViewModel is dismissed.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Used to set [MarsProperty] filter values.
     */
    fun updateFilter(filter:MarsApiFilter){
        getMarsRealEstateProperties(filter)
    }
}
