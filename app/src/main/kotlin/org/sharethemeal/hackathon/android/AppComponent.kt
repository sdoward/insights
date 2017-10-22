package org.sharethemeal.hackathon.android

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(graphActivity: DonatedMealsView)

}