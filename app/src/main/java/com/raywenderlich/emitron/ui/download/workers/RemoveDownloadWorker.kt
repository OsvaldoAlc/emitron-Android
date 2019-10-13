package com.raywenderlich.emitron.ui.download.workers

import android.content.Context
import androidx.work.*
import com.raywenderlich.emitron.data.download.DownloadRepository
import com.raywenderlich.emitron.di.modules.worker.ChildWorkerFactory
import com.raywenderlich.emitron.ui.download.DownloadService
import com.raywenderlich.emitron.ui.download.workers.StartDownloadWorker.Companion.DOWNLOAD_EPISODE_ID
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * Worker for stopping downloads
 */
class RemoveDownloadWorker @AssistedInject constructor(
  @Assisted private val appContext: Context,
  @Assisted private val workerParameters: WorkerParameters,
  private val downloadRepository: DownloadRepository
) : CoroutineWorker(appContext, workerParameters) {

  /**
   * See [Worker.doWork]
   */
  override suspend fun doWork(): Result {
    val contentId = inputData.getString(DOWNLOAD_CONTENT_ID)

    val episodeId = inputData.getString(DOWNLOAD_EPISODE_ID)

    val downloadId = episodeId ?: contentId

    if (downloadId.isNullOrBlank()) {
      downloadRepository.removeAllDownloads()
      DownloadService.removeAllDownloads(appContext)
    } else {
      // Remove download from db
      downloadRepository.removeDownload(downloadId)
      // Remove download from storage
      DownloadService.removeDownload(appContext, downloadId)
    }


    return Result.success()
  }

  /**
   * Factory for [RemoveDownloadWorker]
   */
  @AssistedInject.Factory
  interface Factory : ChildWorkerFactory

  companion object {

    /**
     * Content id to be downloaded
     */
    const val DOWNLOAD_CONTENT_ID: String = "download_content_id"


    private const val DOWNLOAD_WORKER_TAG: String = "downloads"

    /**
     * Stop content download
     *
     * @param workManager WorkManager
     * @param contentId
     * @param episodeId
     */
    fun enqueue(
      workManager: WorkManager,
      contentId: String? = null,
      episodeId: String? = null
    ) {
      val downloadData =
        workDataOf(
          DOWNLOAD_CONTENT_ID to contentId,
          DOWNLOAD_EPISODE_ID to episodeId
        )

      val workRequest = OneTimeWorkRequestBuilder<RemoveDownloadWorker>()
        .setInputData(downloadData)
        .addTag(DOWNLOAD_WORKER_TAG)
        .build()

      val downloadId = episodeId ?: contentId

      if (downloadId.isNullOrBlank()) {
        workManager.enqueue(workRequest)
      } else {
        workManager
          .enqueueUniqueWork(
            downloadId,
            ExistingWorkPolicy.REPLACE,
            workRequest
          )
      }
    }
  }
}
