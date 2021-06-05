package pt.ipca.scoutsbag.models

import org.json.JSONObject

class ActivityMaterial {

    // "id_activity"
    // "id_material"
    // "qnt"

    var idActivity : Int? = null
    var idMaterial : Int? = null
    var qnt        : Int? = null

    constructor(){

    }

    constructor(idActivity: Int?, idMaterial: Int?, qnt: Int?) {
        this.idActivity = idActivity
        this.idMaterial = idMaterial
        this.qnt = qnt
    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_activity" , idActivity  )
        jsonObject.put("id_material" , idMaterial )
        jsonObject.put("qnt"         , qnt )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : ActivityMaterial {
            val activityMaterial = ActivityMaterial()
            activityMaterial.idActivity = if (!jsonObject.isNull("id_activity" )) jsonObject.getInt ("id_activity" )else null
            activityMaterial.idMaterial = if (!jsonObject.isNull("id_material" )) jsonObject.getInt ("id_material" )else null
            activityMaterial.qnt        = if (!jsonObject.isNull("qnt"         )) jsonObject.getInt ("qnt"         )else null

            return activityMaterial
        }
    }

}