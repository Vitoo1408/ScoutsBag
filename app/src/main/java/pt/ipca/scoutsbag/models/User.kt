package pt.ipca.scoutsbag.models

import org.json.JSONObject

class User {
    var idUser     : Int?     = null
    var userName   : String?  = null
    var birthDate   : String?  = null
    var codType    : String?     = null
    var email      : String?  = null
    var pass       : String?  = null
    var contact    : String?  = null
    var gender     : String?  = null
    var address     : String?  = null
    var nin        : String?  = null
    var imageUrl   : String?  = null
    var postalCode : String?  = null
    var userActive : Int? = null
    var accepted   : Int? = null
    var idTeam     : Int? = null

    constructor(){

    }

    constructor(
        idUser: Int?,
        userName: String?,
        birthDate: String?,
        codType: String?,
        email: String?,
        pass: String?,
        contact: String?,
        gender: String?,
        address: String?,
        nin: String?,
        imageUrl: String?,
        postalCode: String?,
        userActive: Int?,
        accepted: Int?,
        idTeam: Int?
    ) {
        this.idUser = idUser
        this.userName = userName
        this.birthDate = birthDate
        this.codType = codType
        this.email = email
        this.pass = pass
        this.contact = contact
        this.gender = gender
        this.address = address
        this.nin = nin
        this.imageUrl = imageUrl
        this.postalCode = postalCode
        this.userActive = userActive
        this.accepted = accepted
        this.idTeam = idTeam
    }


    fun toJson() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("id_user"     , idUser     )
        jsonObject.put("user_name"   , userName   )
        jsonObject.put("birth_date"   , birthDate   )
        jsonObject.put("cod_type"    , codType    )
        jsonObject.put("pass"        , pass       )
        jsonObject.put("email"       , email      )
        jsonObject.put("contact"     , contact    )
        jsonObject.put("gender"      , gender     )
        jsonObject.put("address"      , address     )
        jsonObject.put("nin"         , nin        )
        jsonObject.put("image_url"   , imageUrl   )
        jsonObject.put("postal_code" , postalCode )
        jsonObject.put("user_active" , userActive )
        jsonObject.put("accepted"    , accepted   )
        jsonObject.put("id_team"    , idTeam   )

        return jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JSONObject) : User {
            val user = User()
            user.idUser     = if (!jsonObject.isNull("id_user"     )) jsonObject.getInt    ("id_user"     )else null
            user.userName   = if (!jsonObject.isNull("user_name"   )) jsonObject.getString ("user_name"   )else null
            user.birthDate   = if (!jsonObject.isNull("birth_date"   )) jsonObject.getString ("birth_date"   )else null
            user.pass       = if (!jsonObject.isNull("pass"        )) jsonObject.getString ("pass"        )else null
            user.email      = if (!jsonObject.isNull("email"       )) jsonObject.getString ("email"       )else null
            user.codType    = if (!jsonObject.isNull("cod_type"    )) jsonObject.getString ("cod_type"    )else null
            user.contact    = if (!jsonObject.isNull("contact"     )) jsonObject.getString ("contact"     )else null
            user.gender     = if (!jsonObject.isNull("gender"      )) jsonObject.getString ("gender"      )else null
            user.address     = if (!jsonObject.isNull("address"      )) jsonObject.getString ("address"      )else null
            user.nin        = if (!jsonObject.isNull("nin"         )) jsonObject.getString ("nin"         )else null
            user.imageUrl   = if (!jsonObject.isNull("image_url"   )) jsonObject.getString ("image_url"   )else null
            user.postalCode = if (!jsonObject.isNull("postal_code" )) jsonObject.getString ("postal_code" )else null
            user.userActive = if (!jsonObject.isNull("user_active" )) jsonObject.getInt("user_active" )else null
            user.accepted   = if (!jsonObject.isNull("accepted"    )) jsonObject.getInt("accepted"    )else null
            user.idTeam     = if (!jsonObject.isNull("id_team"    ))  jsonObject.getInt("id_team"    )else null

            return user
        }
    }
}