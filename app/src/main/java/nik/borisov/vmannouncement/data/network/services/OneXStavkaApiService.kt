package nik.borisov.vmannouncement.data.network.services

import nik.borisov.vmannouncement.data.network.models.LineResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OneXStavkaApiService {

    @GET("LineFeed/Web_SearchZip?partner=51&country=1&mode=4&antisports=188&gr=44")
    suspend fun checkLine(@Query("text") teamName: String): Response<LineResponse>
}