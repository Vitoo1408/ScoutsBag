package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Section {

    // "id_section"
    // "section_name"

    var idSection   : Int?    = null
    var sectionName : String? = null

    constructor() {

    }

    constructor(idSection: Int?, sectionName: String?) {
        this.idSection   = idSection
        this.sectionName = sectionName
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_section"   , idSection   )
        jsonObject.put("section_name" , sectionName )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : Section {
            val section = Section()
            section.idSection   = if (!jsonObject.isNull("id_section"   )) jsonObject.getInt    ("id_section"   )else null
            section.sectionName = if (!jsonObject.isNull("section_name" )) jsonObject.getString ("section_name" )else null

            return section
        }
    }

}