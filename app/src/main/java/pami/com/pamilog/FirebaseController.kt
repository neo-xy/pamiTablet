package pami.com.pamilog


import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import io.reactivex.Observable

object FirebaseController {
    lateinit var user: User


    fun getEmployee(uid: String): io.reactivex.Observable<User> {
        return io.reactivex.Observable.create {
            FirebaseFirestore.getInstance().collection("users").document(uid).addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    Log.d("pawell", "fdllll")
                    if (p0 != null) {

                        Log.d("pawell", "ffdf" + p0)
                        user = p0.toObject(User::class.java)!!
                        it.onNext(p0.toObject(User::class.java)!!)
                    }
                }
            })
        }
    }

    fun getCompany(companyId: String): Observable<Company> {
        Log.d("pawell", "er companyId tt" + companyId)
        return Observable.create {
            FirebaseFirestore.getInstance().collection("companies").document(companyId).addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0 != null) {

                        it.onNext(p0.toObject(Company::class.java)!!)
                    }
                }
            })
        }
    }

    fun getEmployees(companyId: String): Observable<MutableList<User>> {
        return Observable.create {
            FirebaseFirestore.getInstance().collection("companies").document(companyId).collection("employees").addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0 != null) {
                        it.onNext(p0.toObjects(User::class.java))
                    }
                }
            })
        }
    }

    fun getActiveShifts(companyId: String): Observable<MutableList<Shift>> {
        return Observable.create {
            FirebaseFirestore.getInstance().collection("companies").document(companyId).collection("activeShifts").addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    var activeShifts = mutableListOf<Shift>()

                    if (p0 != null) {

                        p0.map {
                            val shift: Shift
                            shift = it.toObject(Shift::class.java)
                            shift.shiftId = it.id
                            activeShifts.add(shift)
                        }
                        it.onNext(activeShifts)
                    }
                }
            })
        }
    }

    fun addActiveShift(companyId: String, shift: Shift) {
        FirebaseFirestore.getInstance().collection("companies").document(companyId).collection("activeShifts").add(shift)
    }

    fun removeActiveShift(companyId: String, shiftId: String): Observable<Boolean> {

        return Observable.create {
            var obs = it;
            FirebaseFirestore.getInstance().collection("companies").document(companyId).collection("activeShifts").document(shiftId).delete().addOnCompleteListener(OnCompleteListener {
                obs.onNext(it.isSuccessful)
            })
        }
    }

    fun addshiftToAccept(companyId: String, shift: Shift, shiftId: String): Observable<Boolean> {

        return Observable.create {

            FirebaseFirestore.getInstance().collection("companies").document(companyId).collection("shiftsToAccept").add(shift).addOnCompleteListener(object : OnCompleteListener<DocumentReference> {
                override fun onComplete(p0: Task<DocumentReference>) {
                    it.onNext(p0.isSuccessful)
                }
            })
        }
    }
    fun combo(shift:Shift,shiftId:String,companyId: String):Observable<Boolean>{
      return  Observable.merge(addshiftToAccept(companyId,shift,shiftId), removeActiveShift(companyId,shiftId))
    }
}