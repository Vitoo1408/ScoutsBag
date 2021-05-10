package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Invite
{
    // "id_atividade"
    // "id_equipa"
    // "convocado"

    var idAtividade : Int?     = null
    var idEquipa    : Int?  = null
    var convocado   : Boolean? = null

    constructor(){

    }

    constructor(
        idAtividade: Int?,
        idEquipa: Int?,
        convocado: Boolean?) {
        this.idAtividade = idAtividade
        this.idEquipa = idEquipa
        this.convocado = convocado
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_atividade" , idAtividade )
        jsonObject.put("id_equipa"    , idEquipa    )
        jsonObject.put("convocado"    , convocado   )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Invite {
            val invite = Invite()
            invite.idAtividade = if (!jsonObject.isNull("id_atividade" )) jsonObject.getInt     ("id_atividade" )else null
            invite.idEquipa    = if (!jsonObject.isNull("id_equipa"    )) jsonObject.getInt     ("id_equipa"    )else null
            invite.convocado   = if (!jsonObject.isNull("convocado"    )) jsonObject.getBoolean ("convocado"    )else null

            return invite
        }
    }
}