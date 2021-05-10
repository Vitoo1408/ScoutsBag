package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Catalog
{
    // "id_catalogo"
    // "nome_catalogo"
    // "descricao"
    // "classificacao"
    // "tempo"

    var idCatalogo        : Int?    = null
    var nomeCatalogo      : String? = null
    var descricaoCatalogo : Int?    = null
    var classificacao     : String? = null
    var tempo             : String? = null

    constructor(){

    }

    constructor(idCatalogo: Int?,
                nomeCatalogo: String?,
                descricaoCatalogo: Int?,
                classificacao: String?,
                tempo: String?) {
        this.idCatalogo = idCatalogo
        this.nomeCatalogo = nomeCatalogo
        this.descricaoCatalogo = descricaoCatalogo
        this.classificacao = classificacao
        this.tempo = tempo
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_catalogo"   , idCatalogo        )
        jsonObject.put("nome_catalogo" , nomeCatalogo      )
        jsonObject.put("descricao"     , descricaoCatalogo )
        jsonObject.put("classificacao" , classificacao     )
        jsonObject.put("tempo"         , tempo             )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Catalog {
            val catalog = Catalog()
            catalog.idCatalogo        = if (!jsonObject.isNull("id_catalogo"   )) jsonObject.getInt   ("id_catalogo"   )else null
            catalog.nomeCatalogo      = if (!jsonObject.isNull("nome_catalogo" )) jsonObject.getString("nome_catalogo" )else null
            catalog.descricaoCatalogo = if (!jsonObject.isNull("descricao"     )) jsonObject.getInt   ("descricao"     )else null
            catalog.classificacao     = if (!jsonObject.isNull("classificacao" )) jsonObject.getString("classificacao" )else null
            catalog.tempo             = if (!jsonObject.isNull("tempo"         )) jsonObject.getString("tempo"         )else null

            return catalog
        }
    }

}