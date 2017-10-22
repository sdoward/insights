package org.sharethemeal.hackathon.android

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.robinhood.spark.SparkView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.donated_meal_view.view.*
import javax.inject.Inject

class DonatedMealsView : FrameLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        (context.applicationContext as InsightsApplication).appComponent.inject(this)
        inflate(context, R.layout.donated_meal_view, this);
    }

    @Inject
    lateinit var insightService: InsightService

    var disposable = Disposables.empty()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        disposable = insightService.getDonatedMeals()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            sparkView.adapter = STMSparkAdapter(it, it.map { it.mealCount }.average().toFloat())
                        },
                        onError = {
                            it.printStackTrace()
                            /*                         if (it is UserRecoverableAuthIOException) {
                                                         context.startActivityForResult(it.intent, 400)
                                                     }*/
                        }
                )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposable.dispose()
    }
}