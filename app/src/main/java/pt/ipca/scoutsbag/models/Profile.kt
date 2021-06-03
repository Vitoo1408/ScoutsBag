package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Profile {

    // "cod_type"
    // "designation"

    var codType     : Int?    = null
    var designation : String? = null

    constructor() {

    }

    constructor(codType: Int?, designation: String?) {
        this.codType = codType
        this.designation = designation
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("cod_type"    , codType    )
        jsonObject.put("designation" , designation )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Profile {
            val profile = Profile()
            profile.codType     = if (!jsonObject.isNull("cod_type"    )) jsonObject.getInt    ("cod_type"    )else null
            profile.designation = if (!jsonObject.isNull("designation" )) jsonObject.getString ("designation" )else null

            return profile
        }
    }

}