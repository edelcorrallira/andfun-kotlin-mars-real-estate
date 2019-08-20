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

package com.example.android.marsrealestate.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://mars.udacity.com/"


//Moshi (JSON interpreter used with Retrofit)
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

//Factory API builder call, generates API
private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()


/**
 * Defines the API Retrofit will create
 */
interface MarsApiService{
    /**
     * Retrieve property items, the annotation allows Retrofit to build the functionality
     */
    @GET("realestate")//Endpoint
    fun getProperties():
            //The call object is used to start the request
            Call<List<MarsProperty>>
}

//Since Retrofit services are expensive and we only use one in this app, we expose it to the whole
// app
object MarsApi {
    val retrofitService: MarsApiService by lazy {
        retrofit.create(MarsApiService::class.java)
    }
}