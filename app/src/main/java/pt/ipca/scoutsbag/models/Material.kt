package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Material {

    // "id_material"
    // "name_material"
    // "qnt_stock"
    // "image_url"
    // "material_type"

    var idMaterial   : Int?    = null
    var nameMaterial : String? = null
    var qntStock     : Int?    = null
    var imageUrl     : String? = null
    var materialType : String? = null

    constructor() {

    }

    constructor(
        idMaterial: Int?,
        nameMaterial: String?,
        qntStock: Int?,
        imageUrl: String?,
        materialType: String?
    ) {
        this.idMaterial = idMaterial
        this.nameMaterial = nameMaterial
        this.qntStock = qntStock
        this.imageUrl = imageUrl
        this.materialType = materialType
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_material"  , idMaterial  )
        jsonObject.put("name_material", nameMaterial )
        jsonObject.put("qnt_stock"    , qntStock )
        jsonObject.put("image_url"    , imageUrl )
        jsonObject.put("material_type", materialType )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Material {
            val material = Material()
            material.idMaterial   = if (!jsonObject.isNull("id_material"   )) jsonObject.getInt    ("id_material"   )else null
            material.nameMaterial = if (!jsonObject.isNull("name_material" )) jsonObject.getString ("name_material" )else null
            material.qntStock     = if (!jsonObject.isNull("qnt_stock"     )) jsonObject.getInt    ("qnt_stock"     )else null
            material.imageUrl     = if (!jsonObject.isNull("image_url"     )) jsonObject.getString ("image_url"     )else null
            material.materialType = if (!jsonObject.isNull("material_type" )) jsonObject.getString ("material_type" )else null

            return material
        }
    }

}