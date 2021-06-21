package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Invite {

    // "id_activity"
    // "id_user"
    // "accepted_invite"

    var idActivity   : Int?     = null
    var idUser       : Int?     = null
    var acceptedInvite : Int? = null

    constructor(){

    }

    constructor(idActivity: Int?, idUser: Int?, acceptedInvite: Int?) {
        this.idActivity = idActivity
        this.idUser = idUser
        this.acceptedInvite = acceptedInvite
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_activity"  , idActivity  )
        jsonObject.put("id_user"      , idUser )
        jsonObject.put("accepted_invite" , acceptedInvite )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Invite {
            val invite = Invite()
            invite.idActivity     = if (!jsonObject.isNull("id_activity"  )) jsonObject.getInt ("id_activity"  )else null
            invite.idUser         = if (!jsonObject.isNull("id_user"      )) jsonObject.getInt ("id_user"      )else null
            invite.acceptedInvite = if (!jsonObject.isNull("accepted_invite" )) jsonObject.getInt ("accepted_invite" )else null

            return invite
        }
    }

}