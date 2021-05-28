package pt.ipca.scoutsbag.loginAndRegister

import org.json.JSONObject

class LoginModel {
    var email: String? = null
    var pass: String? = null

    constructor(){

    }

    fun toJson() : JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("pass", pass)
        return jsonObject
    }
}