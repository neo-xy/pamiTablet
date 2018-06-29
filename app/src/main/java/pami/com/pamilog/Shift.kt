package pami.com.pamilog

import com.google.firebase.firestore.IgnoreExtraProperties

import java.util.*


@IgnoreExtraProperties
class Shift {
    var employeeId: String = ""
    var message: String? =null;
    var shiftId: String = ""
    var firstName = ""
    var lastName = ""
    var messageIn = ""
    var messageOut = ""
    var startDate:Date = Date()
    var endDate:Date = Date()
}