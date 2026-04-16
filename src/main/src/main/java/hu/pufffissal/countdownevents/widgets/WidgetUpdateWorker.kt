package hu.pufffissal.countdownevents.widgets

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val refresher: WidgetRefresher,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        refresher.refreshAll()
        return Result.success()
    }
}

