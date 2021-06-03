package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Team {

    // "id_team"
    // "team_name"
    // "id_section"

    var idTeam    : Int?    = null
    var teamName  : String? = null
    var idSection : Int?    = null

    constructor() {

    }

    constructor(idTeam: Int?, teamName: String?, idSection: Int?) {
        this.idTeam = idTeam
        this.teamName = teamName
        this.idSection = idSection
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_team"    , idTeam    )
        jsonObject.put("team_name"  , teamName  )
        jsonObject.put("id_section" , idSection )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Team {
            val team = Team()
            team.idTeam    = if (!jsonObject.isNull("id_team"    )) jsonObject.getInt    ("id_team"    )else null
            team.teamName  = if (!jsonObject.isNull("team_name"  )) jsonObject.getString ("team_name"  )else null
            team.idSection = if (!jsonObject.isNull("id_section" )) jsonObject.getInt    ("id_section" )else null

            return team
        }
    }

}