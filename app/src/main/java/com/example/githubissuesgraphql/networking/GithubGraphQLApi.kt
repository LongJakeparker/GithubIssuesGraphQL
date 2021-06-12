package com.example.githubissuesgraphql.networking

import android.os.Looper
import com.apollographql.apollo.ApolloClient
import com.example.githubissuesgraphql.SharedPreferencesManager
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * @author longtran
 * @since 06/06/2021
 */
class GithubGraphQLApi @Inject constructor(val sharedPreferencesManager: SharedPreferencesManager) {

    fun getApolloClient(): ApolloClient {
        check(Looper.myLooper() == Looper.getMainLooper()) {
            "Only the main thread can get the apolloClient instance"
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer ${sharedPreferencesManager.getAccessToken()}"
                    )
                    .build()

                chain.proceed(request)
            }
            .build()
        return ApolloClient.builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }
}