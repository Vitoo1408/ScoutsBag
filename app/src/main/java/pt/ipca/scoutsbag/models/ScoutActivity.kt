package com.example.scoutsteste1

import org.json.JSONObject

class ScoutActivity {

    // "id_activity"
    // "name_activity"
    // "id_type"
    // "activity_description"
    // "activity_site"
    // "start_date"
    // "finish_date"
    // "gps_coordinates"
    // "start_site"
    // "finish_site"
    // "price"

    var idActivity          : Int?    = null
    var nameActivity        : String? = null
    var idType              : Int?    = null
    var activityDescription : String? = null
    var activitySite        : String? = null
    var startDate           : String? = null
    var finishDate          : String? = null
    var gpsCoordinates      : String? = null
    var startSite           : String? = null
    var finishSite          : String? = null
    var price               : Float? = null

    constructor(){

    }

    constructor(
        idActivity: Int?,
        nameActivity: String?,
        idType: Int?,
        activityDescription: String?,
        activitySite: String?,
        startDate: String?,
        finishDate: String?,
        gpsCoordinates: String?,
        startSite: String?,
        finishSite: String?,
        price: Float?
    ) {
        this.idActivity = idActivity
        this.nameActivity = nameActivity
        this.idType = idType
        this.activityDescription = activityDescription
        this.activitySite = activitySite
        this.startDate = startDate
        this.finishDate = finishDate
        this.gpsCoordinates = gpsCoordinates
        this.startSite = startSite
        this.finishSite = finishSite
        this.price = price
    }


    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_activity"          , idActivity          )
        jsonObject.put("name_activity"        , nameActivity        )
        jsonObject.put("id_type"              , idType              )
        jsonObject.put("activity_description" , activityDescription )
        jsonObject.put("activity_site"        , activitySite        )
        jsonObject.put("start_date"           , startDate           )
        jsonObject.put("finish_date"          , finishDate          )
        jsonObject.put("gps_coordinates"      , gpsCoordinates      )
        jsonObject.put("start_site"           , startSite           )
        jsonObject.put("finish_site"          , finishSite          )
        jsonObject.put("price"                , price               )

        return jsonObject
    }


    companion object {
        fun fromJson(jsonObject: JSONObject) : ScoutActivity {
            val activity = ScoutActivity()
            activity.idActivity          = if (!jsonObject.isNull("id_activity"          )) jsonObject.getInt   ("id_activity"          )else null
            activity.nameActivity        = if (!jsonObject.isNull("name_activity"        )) jsonObject.getString("name_activity"        )else null
            activity.idType              = if (!jsonObject.isNull("id_type"              )) jsonObject.getInt   ("id_type"              )else null
            activity.activityDescription = if (!jsonObject.isNull("activity_description" )) jsonObject.getString("activity_description" )else null
            activity.activitySite        = if (!jsonObject.isNull("activity_site"        )) jsonObject.getString("activity_site"        )else null
            activity.startDate           = if (!jsonObject.isNull("start_date"           )) jsonObject.getString("start_date"           )else null
            activity.finishDate          = if (!jsonObject.isNull("finish_date"          )) jsonObject.getString("finish_date"          )else null
            activity.gpsCoordinates      = if (!jsonObject.isNull("gps_coordinates"      )) jsonObject.getString("gps_coordinates"      )else null
            activity.startSite           = if (!jsonObject.isNull("start_site"           )) jsonObject.getString("start_site"           )else null
            activity.finishSite          = if (!jsonObject.isNull("finish_site"          )) jsonObject.getString("finish_site"          )else null
            activity.price               = if (!jsonObject.isNull("price"                )) jsonObject.getString("price"                ).toFloat() else null

            return activity
        }
    }

}