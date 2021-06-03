package pt.ipca.scoutsbag.models

import org.json.JSONObject

class ActivityType {

    // "id_type"
    // "designation"
    // "image_url"

    var idType     : Int?    = null
    var designation : String? = null
    var imageUrl   : String? = null

    constructor(){

    }

    constructor(idTipo: Int?, designacao: String?, urlImagem: String?) {
        this.idType = idTipo
        this.designation = designacao
        this.imageUrl = urlImagem
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_type"    , idType     )
        jsonObject.put("designation" , designation )
        jsonObject.put("image_url" , imageUrl   )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : ActivityType {
            val activityType = ActivityType()
            activityType.idType     = if (!jsonObject.isNull("id_type"    )) jsonObject.getInt    ("id_type"    )else null
            activityType.designation = if (!jsonObject.isNull("designation" )) jsonObject.getString ("designation" )else null
            activityType.imageUrl   = if (!jsonObject.isNull("image_url"  )) jsonObject.getString ("image_url"  )else null

            return activityType
        }
    }

}