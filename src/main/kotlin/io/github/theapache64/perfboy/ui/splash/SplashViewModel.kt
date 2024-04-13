package io.github.theapache64.perfboy.ui.splash

import io.github.theapache64.perfboy.data.repo.AppRepo
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import javax.inject.Inject

class SplashViewModel @Inject constructor() {
    companion object{
        private const val VERSION = "1.0.0-alpha01"
    }

    private val _welcomeMsg = MutableLiveData<String>()
    val welcomeMsg: LiveData<String> = _welcomeMsg

    private val _goToHome = MutableLiveData<String>()
    val goToHome: LiveData<String> = _goToHome

    init {
        val splashMsg = "perf-boy ($VERSION)"
        _welcomeMsg.value = splashMsg
        _goToHome.value = splashMsg
    }


}
