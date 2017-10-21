package org.sharethemeal.hackathon.android

import com.robinhood.spark.SparkAdapter

class STMSparkAdapter(private val dataPoints: List<DataPoint>, private val average: Float) : SparkAdapter() {

    override fun getY(index: Int): Float {
        return dataPoints[index].mealCount.toFloat()
    }

    override fun getItem(index: Int): Any {
        return index
    }

    override fun getCount(): Int {
        return dataPoints.size
    }

    override fun hasBaseLine(): Boolean {
        return true
    }

    override fun getBaseLine(): Float {
        return average
    }


}