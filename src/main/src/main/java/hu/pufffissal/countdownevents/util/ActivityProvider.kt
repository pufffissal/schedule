package hu.pufffissal.countdownevents.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Small helper to show platform dialogs from Compose screens without threading context through every composable.
 * It's safe in-app (single process) and only holds a weak reference-like pointer cleared on lifecycle events.
 */
@Singleton
class ActivityProvider @Inject constructor(
    application: Application,
) : Application.ActivityLifecycleCallbacks {
    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        current = activity
    }

    override fun onActivityStarted(activity: Activity) {
        current = activity
    }

    override fun onActivityResumed(activity: Activity) {
        current = activity
    }

    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (current === activity) current = null
    }

    companion object {
        @Volatile
        var current: Activity? = null
            internal set
    }
}

