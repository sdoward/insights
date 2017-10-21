package org.sharethemeal.hackathon.android

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import io.reactivex.Single
import javax.inject.Singleton

@Singleton
class InsightService(private val store: Store<List<DataPoint>, BarCode>, private val schedulerProvider: SchedulerProvider) {

    fun getDonatedMeals(): Single<List<DataPoint>> {
        return store.get(BarCode("key", "value"))
                .subscribeOn(schedulerProvider.io())
    }
}