package org.sharethemeal.hackathon.android;

public class DataPointJava implements Cloneable {

    private final int mealCount;

    private final int timeStamp;

    public DataPointJava(int mealCount, int timeStamp) {
        this.mealCount = mealCount;
        this.timeStamp = timeStamp;
    }

    public int getMealCount() {
        return mealCount;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataPointJava that = (DataPointJava) o;

        if (mealCount != that.mealCount) return false;
        return timeStamp == that.timeStamp;
    }

    @Override
    public int hashCode() {
        int result = mealCount;
        result = 31 * result + timeStamp;
        return result;
    }

    @Override
    public String toString() {
        return "DataPointJava{" +
                "mealCount=" + mealCount +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
