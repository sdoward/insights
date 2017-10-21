package org.sharethemeal.hackathon.android

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.graph_activity.*
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import android.content.Context
import android.content.SharedPreferences
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.sheets.v4.Sheets
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers


class GraphActivity : AppCompatActivity() {

    companion object {
        val PERMISSIONS = 200
        val ACCOUNT = 300
        val SPREAD_SHEET_ID = "1BbaM4ghKe2Y6VCySpnnn7JyTr2pghRpR2VOUBoWE_SM"
        fun start(context: Context) {
            context.startActivity(Intent(context, GraphActivity::class.java))
        }
    }

    val sheets: Sheets by lazy {
        com.google.api.services.sheets.v4.Sheets.Builder(
                AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("Google Sheets API Android Quickstart")
                .build()
    }

    val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("default", 0)
    }

    val credential: GoogleAccountCredential by lazy {
        GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), arrayListOf(SheetsScopes.SPREADSHEETS_READONLY))
                .setBackOff(ExponentialBackOff())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graph_activity)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).show()
        }

        sparkView.animateChanges = true

        credential.selectedAccountName = sharedPreferences.getString("userName", "")
        Single.create<List<Int>> {
            val valueRange = sheets.spreadsheets()
                    .values()
                    .get(SPREAD_SHEET_ID, "Daily_track!H:J")
                    .execute()
            val values = valueRange.getValues()
            it.onSuccess(values.subList(11, values.size).map { it[0].toString().toInt() })
        }
                .map { it.map { DataPoint(0, it) } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            sparkView.adapter = STMSparkAdapter(it, it.map { it.mealCount }.average().toFloat())
                        },
                        onError = {
                            it.printStackTrace()
                            if (it is UserRecoverableAuthIOException) {
                                startActivityForResult(it.intent, 400)
                            }
                        }
                )
    }

}
