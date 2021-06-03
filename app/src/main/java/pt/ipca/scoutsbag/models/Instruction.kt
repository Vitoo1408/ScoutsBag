package com.example.scoutsteste1

import org.json.JSONObject

class Instruction {

    // "id_instruction"
    // "instruction_text"
    // "image_url"
    // "id_catalog"

    var idInstruction   : Int?     = null
    var instructionText : String?  = null
    var imageUrl        : String? = null
    var idCatalog       : Int?     = null

    constructor(){

    }

    constructor(idInstruction: Int?, instructionText: String?, imageUrl: String?, idCatalog: Int?) {
        this.idInstruction   = idInstruction
        this.instructionText = instructionText
        this.imageUrl        = imageUrl
        this.idCatalog       = idCatalog
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_instruction"   , idInstruction )
        jsonObject.put("instruction_text" , instructionText       )
        jsonObject.put("image_url"        , imageUrl   )
        jsonObject.put("id_catalog"       , idCatalog  )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Instruction {
            val instruction = Instruction()
            instruction.idInstruction   = if (!jsonObject.isNull("id_instruction"   )) jsonObject.getInt     ("id_instruction"   )else null
            instruction.instructionText = if (!jsonObject.isNull("instruction_text" )) jsonObject.getString  ("instruction_text" )else null
            instruction.imageUrl        = if (!jsonObject.isNull("image_url"        )) jsonObject.getString ("image_url"        )else null
            instruction.idCatalog       = if (!jsonObject.isNull("id_catalog"       )) jsonObject.getInt     ("id_catalog"       )else null

            return instruction
        }
    }

}