package com.raywenderlich.emitron.data.progressions

import com.raywenderlich.emitron.model.Contents
import com.raywenderlich.emitron.model.ProgressionsUpdate
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * Service for API calls for Progressions
 */
interface ProgressionApi {

  /**
   * Create/Update a progression
   *
   * @param data to create progression
   *
   * @return Response<Content> response object containing response body
   */
  @POST("progressions/bulk")
  @Throws(Exception::class)
  suspend fun updateProgression(
    @Body data: ProgressionsUpdate
  ): Contents

  /**
   * Delete a progression
   *
   * @param id progression id
   *
   * @return Response<Any> response object containing response body
   */
  @DELETE("progressions/{id}")
  @Throws(Exception::class)
  suspend fun deleteProgression(
    @Path("id") id: String
  ): Response<Any>

  /**
   * Fetch all progressions
   */
  @GET("progressions")
  fun getProgressions(
    @Query("page[number]") pageNumber: Int,
    @Query("page[size]") pageSize: Int,
    @Query("filter[content_types][]") contentType: List<String> = listOf(
      "screencast",
      "collection"
    ),
    @Query("filter[completion_status]") completionStatus: String
  ): Call<Contents>

  companion object {

    /**
     * Factory function for [ProgressionApi]
     */
    fun create(retroFit: Retrofit): ProgressionApi = retroFit.create(
      ProgressionApi::class.java
    )
  }
}
