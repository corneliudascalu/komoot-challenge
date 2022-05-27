package com.corneliudascalu.challenge

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * https://www.flickr.com/services/rest/?method=flickr.photos.search&lat=46.7566693&lon=23.6075734&radius=0.1&per_page=5&format=json&nojsoncallback=1
 *
 */
//{
//   "photos":{
//      "page":1,
//      "pages":1,
//      "perpage":5,
//      "total":3,
//      "photo":[
//         {
//            "id":"11286825213",
//            "owner":"20342758@N00",
//            "secret":"49c5727275",
//            "server":"2864",
//            "farm":3,
//            "title":"Romania December 2013 308",
//            "ispublic":1,
//            "isfriend":0,
//            "isfamily":0
//         },
//         {
//            "id":"11286769024",
//            "owner":"20342758@N00",
//            "secret":"0f062fa925",
//            "server":"5511",
//            "farm":6,
//            "title":"Romania December 2013 304",
//            "ispublic":1,
//            "isfriend":0,
//            "isfamily":0
//         },
//         {
//            "id":"7376095028",
//            "owner":"90407718@N00",
//            "secret":"f45a568025",
//            "server":"5450",
//            "farm":6,
//            "title":"",
//            "ispublic":1,
//            "isfriend":0,
//            "isfamily":0
//         }
//      ]
//   },
//   "stat":"ok"
//}
interface FlickrApi {
    @GET("services/rest/?method=flickr.photos.search&radius=0.1&per_page=5&format=json&nojsoncallback=1")
    suspend fun search(
        @Query("api_key") apiKey: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): FlickrSearchResultsWrapper
}

// TODO Create a type adapter to strip the unnecessary data
data class FlickrSearchResultsWrapper(val photos: FlickrSearchResultsPage)
data class FlickrSearchResultsPage(val page: Int, val photo: List<FlickrPhoto>)
data class FlickrPhoto(val id: String, val owner: String, val secret: String, val server: String, val title: String) {
    val url
        get() = "https://live.staticflickr.com/{server-id}/{id}_{secret}_w.jpg"
            .replace("{server-id}", server)
            .replace("{id}", id)
            .replace("{secret}", secret)

}