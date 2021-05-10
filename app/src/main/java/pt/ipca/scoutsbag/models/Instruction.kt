package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Instruction {

    // "id_instrucao"
    // "texto"
    // "url_imagem"
    // "id_catalogo"

    var idInstrucao : Int?     = null
    var texto       : String?     = null
    var urlImagem   : Boolean? = null
    var idCatalogo  : Int? = null

    constructor(){

    }

    constructor(idInstrucao: Int?, texto: String?, urlImagem: Boolean?, idCatalogo: Int?) {
        this.idInstrucao = idInstrucao
        this.texto       = texto
        this.urlImagem   = urlImagem
        this.idCatalogo  = idCatalogo
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_instrucao" , idInstrucao )
        jsonObject.put("texto"        , texto       )
        jsonObject.put("url_imagem"   , urlImagem   )
        jsonObject.put("id_catalogo"  , idCatalogo  )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Instruction {
            val instruction = Instruction()
            instruction.idInstrucao = if (!jsonObject.isNull("id_instrucao" )) jsonObject.getInt     ("id_instrucao" )else null
            instruction.texto       = if (!jsonObject.isNull("texto"        )) jsonObject.getString  ("texto"        )else null
            instruction.urlImagem   = if (!jsonObject.isNull("url_imagem"   )) jsonObject.getBoolean ("url_imagem"   )else null
            instruction.idCatalogo  = if (!jsonObject.isNull("id_catalogo"  )) jsonObject.getInt     ("id_catalogo"  )else null

            return instruction
        }
    }

}
