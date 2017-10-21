package org.sharethemeal.hackathon.android

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    companion object {
        val SPREAD_SHEET_ID = "1BbaM4ghKe2Y6VCySpnnn7JyTr2pghRpR2VOUBoWE_SM"
    }

    @Provides
    fun provieSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences("default", 0)
    }

    @Provides
    fun provideCredentials(): GoogleAccountCredential {
        return GoogleAccountCredential.usingOAuth2(
                application, arrayListOf(SheetsScopes.SPREADSHEETS_READONLY))
                .setBackOff(ExponentialBackOff())
    }


    @Provides
    fun provideSheets(credential: GoogleAccountCredential, sharedPreferences: SharedPreferences): Sheets {
        credential.selectedAccountName = sharedPreferences.getString("userName", "")
        return Sheets.Builder(
                AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("STM Insights")
                .build()
    }

    @Singleton
    @Provides
    fun provideFetcher(sheets: Sheets): Store<List<DataPoint>, BarCode> {
        return StoreBuilder.parsedWithKey<BarCode, List<DataPoint>, List<DataPoint>>()
                .fetcher {
                    Single.create<List<Int>> {
                        val valueRange = sheets.spreadsheets()
                                .values()
                                .get(SPREAD_SHEET_ID, "Daily_track!H:J")
                                .execute()
                        val values = valueRange.getValues()
                        it.onSuccess(values.subList(11, values.size).map { it[0].toString().toInt() })
                    }
                            .map { it.map { DataPoint(0, it) } }
                }
                .open()
    }

    @Singleton
    @Provides
    fun insightService(store: Store<List<DataPoint>, BarCode>): InsightService {
        return InsightService(store, AppSchedulerProvider())
    }


}