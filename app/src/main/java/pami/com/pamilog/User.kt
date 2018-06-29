package pami.com.pamilog

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class User {
    val firstName: String = ""
    val lastName: String = ""
    val employeeId: String = ""
    val companyId: String = ""
    var role = ""
    var socialSecurityNumber: Long = 0
}





