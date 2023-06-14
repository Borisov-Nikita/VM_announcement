package nik.borisov.vmannouncement.data.network.services

import nik.borisov.vmannouncement.data.network.models.AnnouncementResponse
import nik.borisov.vmannouncement.data.network.models.LineRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MarathonBetApiService {

    @POST("su/react/live/announces/list")
    suspend fun loadAnnouncement(@Body body: LineRequest): Response<AnnouncementResponse>
}