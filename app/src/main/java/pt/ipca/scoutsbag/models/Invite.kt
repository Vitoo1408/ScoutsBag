package com.example.scoutsteste1

import org.json.JSONObject

class Invite {

    // "id_activity"
    // "id_team"
    // "invited"

    var idActivity : Int? = null
    var idTeam     : Int? = null
    var invited    : Int? = null

    constructor(){

    }

    constructor(
        idActivity: Int?,
        idTeam: Int?,
        invited: Int?
    ) {
        this.idActivity = idActivity
        this.idTeam = idTeam
        this.invited = invited
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_activity" , idActivity )
        jsonObject.put("id_team"     , idTeam     )
        jsonObject.put("invited"     , invited    )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Invite {
            val invite = Invite()
            invite.idActivity = if (!jsonObject.isNull("id_activity" )) jsonObject.getInt ("id_activity" )else null
            invite.idTeam     = if (!jsonObject.isNull("id_team"     )) jsonObject.getInt ("id_team"     )else null
            invite.invited    = if (!jsonObject.isNull("invited"     )) jsonObject.getInt ("invited"     )else null

            return invite
        }
    }

}