package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Statistic {

    // "id_estatistic"
    // "participation_year"
    // "participation_month"
    // "participation_total"
    // "id_user"

    var idEstatistic       : Int?    = null
    var participationYear  : String? = null
    var participationMonth : String? = null
    var participationTotal : String? = null
    var idUser             : Int?    = null

    constructor() {

    }

    constructor(
        idEstatistic: Int?,
        participationYear: String?,
        participationMonth: String?,
        participationTotal: String?,
        idUser: Int?
    ) {
        this.idEstatistic = idEstatistic
        this.participationYear = participationYear
        this.participationMonth = participationMonth
        this.participationTotal = participationTotal
        this.idUser = idUser
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_estatistic"       , idEstatistic       )
        jsonObject.put("participation_year"  , participationYear  )
        jsonObject.put("participation_month" , participationMonth )
        jsonObject.put("participation_total" , participationTotal )
        jsonObject.put("id_user"             , idUser             )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Statistic {
            val statistic = Statistic()
            statistic.idEstatistic       = if (!jsonObject.isNull("id_estatistic"       )) jsonObject.getInt    ("id_estatistic"       )else null
            statistic.participationYear  = if (!jsonObject.isNull("participation_year"  )) jsonObject.getString ("participation_year"  )else null
            statistic.participationMonth = if (!jsonObject.isNull("participation_month" )) jsonObject.getString ("participation_month" )else null
            statistic.participationTotal = if (!jsonObject.isNull("participation_total" )) jsonObject.getString ("participation_total" )else null
            statistic.idUser             = if (!jsonObject.isNull("id_user"             )) jsonObject.getInt    ("id_user"             )else null

            return statistic
        }
    }

}