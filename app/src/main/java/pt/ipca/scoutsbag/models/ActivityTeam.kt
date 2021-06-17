package pt.ipca.scoutsbag.models

import org.json.JSONObject

class ActivityTeam {

    // "id_activity"
    // "id_team"

    var idActivity : Int?    = null
    var idTeam     : Int? = null

    constructor(){

    }

    constructor(idActivity: Int?, idTeam: Int?) {
        this.idActivity = idActivity
        this.idTeam = idTeam
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_activity", idActivity )
        jsonObject.put("id_team"    , idTeam     )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : ActivityTeam {
            val activityTeam = ActivityTeam()
            activityTeam.idActivity = if (!jsonObject.isNull("id_activity" )) jsonObject.getInt ("id_activity" )else null
            activityTeam.idTeam     = if (!jsonObject.isNull("id_team"     )) jsonObject.getInt ("id_team"     )else null

            return activityTeam
        }
    }

}