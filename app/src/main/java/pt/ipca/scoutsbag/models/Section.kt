package pt.ipca.scoutsbag.models

import org.json.JSONObject

class Section {

    // "id_section"
    // "section_name"

    var idSection : Int?     = null
    var active    : Boolean? = null

    constructor(idSection: Int?, active: Boolean?) {
        this.idSection   = idSection
        this.active = active
    }

}