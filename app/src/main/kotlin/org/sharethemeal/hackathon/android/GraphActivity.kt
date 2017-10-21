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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


class GraphActivity : AppCompatActivity() {

    companion object {
        val PERMISSIONS = 200
        val ACCOUNT = 300
        fun start(context: Context) {
            context.startActivity(Intent(context, GraphActivity::class.java))
        }
    }

    @Inject
    lateinit var insightService: InsightService

    var disposable = Disposables.empty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graph_activity)
        setSupportActionBar(toolbar)
        (application as InsightsApplication).appComponent.inject(this)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).show()
        }

        sparkView.animateChanges = true
        disposable = insightService.getDonatedMeals()
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

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
