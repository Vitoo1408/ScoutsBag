package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Participation {

    // "id_activity"
    // "id_user"
    // "participated"

    var idActivity   : Int?     = null
    var idUser       : Int?     = null
    var participated : Int? = null

    constructor(){

    }

    constructor(idActivity: Int?, idUser: Int?, participated: Int?) {
        this.idActivity = idActivity
        this.idUser = idUser
        this.participated = participated
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_activity"  , idActivity  )
        jsonObject.put("id_user"      , idUser )
        jsonObject.put("participated" , participated )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Participation {
            val participation = Participation()
            participation.idActivity   = if (!jsonObject.isNull("id_activity"  )) jsonObject.getInt ("id_activity"  )else null
            participation.idUser       = if (!jsonObject.isNull("id_user"      )) jsonObject.getInt ("id_user"      )else null
            participation.participated = if (!jsonObject.isNull("participated" )) jsonObject.getInt ("participated" )else null

            return participation
        }
    }

}