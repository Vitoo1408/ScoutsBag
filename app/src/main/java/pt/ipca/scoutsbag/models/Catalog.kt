package com.example.scoutsteste1

import org.json.JSONObject

class Catalog {

    // "id_catalog"
    // "name_catalog"
    // "catalog_description"
    // "classification"
    // "instructions_time"

    var idCatalog          : Int?    = null
    var nameCatalog        : String? = null
    var catalogDescription : Int?    = null
    var classification     : String? = null
    var instructionsTime   : String? = null

    constructor(){

    }

    constructor(
        idCatalog: Int?,
        nameCatalog: String?,
        catalogDescription: Int?,
        classification: String?,
        instructionsTime: String?
    ) {
        this.idCatalog = idCatalog
        this.nameCatalog = nameCatalog
        this.catalogDescription = catalogDescription
        this.classification = classification
        this.instructionsTime = instructionsTime
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_catalog"          , idCatalog          )
        jsonObject.put("name_catalog"        , nameCatalog        )
        jsonObject.put("catalog_description" , catalogDescription )
        jsonObject.put("classification"      , classification     )
        jsonObject.put("instructions_time"   , instructionsTime   )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Catalog {
            val catalog = Catalog()
            catalog.idCatalog          = if (!jsonObject.isNull("id_catalog"          )) jsonObject.getInt   ("id_catalog"          )else null
            catalog.nameCatalog        = if (!jsonObject.isNull("name_catalog"        )) jsonObject.getString("name_catalog"        )else null
            catalog.catalogDescription = if (!jsonObject.isNull("catalog_description" )) jsonObject.getInt   ("catalog_description" )else null
            catalog.classification     = if (!jsonObject.isNull("classification"      )) jsonObject.getString("classification"      )else null
            catalog.instructionsTime   = if (!jsonObject.isNull("instructions_time"   )) jsonObject.getString("instructions_time"   )else null

            return catalog
        }
    }

}