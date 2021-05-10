package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Activity
{
// "id_atividade"
    // "nome_atividade"
    // "id_tipo"
    // "descricao"
    // "local"
    // "data_inicio"
    // "data_fim"
    // "coordenadas_gps"
    // "local_inicio"
    // "local_fim"
    // "custo"

    var idAtividade     : Int?    = null
    var nomeAtividade   : String? = null
    var idTipo          : Int?    = null
    var descricao       : String? = null
    var local           : String? = null
    var dataInicio      : String? = null
    var dataFim         : String? = null
    var coordenadasGps  : String? = null
    var localInicio     : String? = null
    var localFim        : String? = null
    var custo           : String?  = null

    constructor(){

    }

    constructor(
        idAtividade: Int?,
        nomeAtividade: String?,
        idTipo: Int?,
        descricao: String?,
        local: String?,
        dataInicio: String?,
        dataFim: String?,
        coordenadasGps: String?,
        localInicio: String?,
        localFim: String?,
        custo: String?) {
        this.idAtividade = idAtividade
        this.nomeAtividade = nomeAtividade
        this.idTipo = idTipo
        this.descricao = descricao
        this.local = local
        this.dataInicio = dataInicio
        this.dataFim = dataFim
        this.coordenadasGps = coordenadasGps
        this.localInicio = localInicio
        this.localFim = localFim
        this.custo = custo
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_atividade"    , idAtividade    )
        jsonObject.put("nome_atividade"  , nomeAtividade  )
        jsonObject.put("id_tipo"         , idTipo         )
        jsonObject.put("descricao"       , descricao      )
        jsonObject.put("local"           , local          )
        jsonObject.put("data_inicio"     , dataInicio     )
        jsonObject.put("data_fim"        , dataFim        )
        jsonObject.put("coordenadas_gps" , coordenadasGps )
        jsonObject.put("local_inicio"    , localInicio    )
        jsonObject.put("local_fim"       , localFim       )
        jsonObject.put("custo"           , custo          )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Activity {
            val activity = Activity()
            activity.idAtividade    = if (!jsonObject.isNull("id_atividade"     )) jsonObject.getInt   ("id_atividade"    )else null
            activity.nomeAtividade  = if (!jsonObject.isNull("nome_atividade"   )) jsonObject.getString("nome_atividade"  )else null
            activity.idTipo         = if (!jsonObject.isNull("id_tipo"          )) jsonObject.getInt   ("id_tipo"         )else null
            activity.descricao      = if (!jsonObject.isNull("descricao"        )) jsonObject.getString("descricao"       )else null
            activity.local          = if (!jsonObject.isNull("local"            )) jsonObject.getString("local"           )else null
            activity.dataInicio     = if (!jsonObject.isNull("data_inicio"      )) jsonObject.getString("data_inicio"     )else null
            activity.dataFim        = if (!jsonObject.isNull("data_fim"         )) jsonObject.getString("data_fim"        )else null
            activity.coordenadasGps = if (!jsonObject.isNull("coordenadas_gps"  )) jsonObject.getString("coordenadas_gps" )else null
            activity.localInicio    = if (!jsonObject.isNull("local_inicio"     )) jsonObject.getString("local_inicio"    )else null
            activity.localFim       = if (!jsonObject.isNull("local_fim"        )) jsonObject.getString("local_fim"       )else null
            activity.custo          = if (!jsonObject.isNull("custo"            )) jsonObject.getString("custo"           )else null

            return activity
        }
    }
}