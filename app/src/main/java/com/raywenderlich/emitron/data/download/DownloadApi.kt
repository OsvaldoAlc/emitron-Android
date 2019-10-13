package com.raywenderlich.emitron.data.download

import com.raywenderlich.emitron.model.Content
import com.raywenderlich.emitron.model.Contents
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Service to make API calls for videos
 */
interface DownloadApi {

  /**
   * Fetch a content by id
   *
   * @param id Id for content
   *
   * @return [Content]
   */
  @GET("contents/{id}")
  suspend fun getContent(
    @Path("id") id: String
  ): Content

  /**
   * Get download url
   */
  @GET("videos/{id}/download")
  suspend fun getDownloadUrl(
    @Path("id") id: String
  ): Contents

  companion object {

    /**
     * Factory function for [DownloadApi]
     */
    fun create(retroFit: Retrofit): DownloadApi = retroFit.create(DownloadApi::class.java)
  }
}
