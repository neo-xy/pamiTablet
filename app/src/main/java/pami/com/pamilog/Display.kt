package pami.com.pamilog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextClock
import android.widget.TextView
import java.util.*


class Display : Fragment(), View.OnClickListener {

    lateinit var btn1: Button;
    lateinit var btn2: Button;
    lateinit var btn3: Button;
    lateinit var btn4: Button;
    lateinit var btn5: Button;
    lateinit var btn6: Button;
    lateinit var btn7: Button;
    lateinit var btn8: Button;
    lateinit var btn9: Button;
    lateinit var btn0: Button;
    lateinit var btnEnter: Button;
    lateinit var btnRemove: Button;
    lateinit var socialNumberET: TextView
    var employees = mutableListOf<User>()
    var activeShifts = mutableListOf<Shift>()
    lateinit var clock: TextClock
    var isEmployeeClockedIn: Boolean = false
    lateinit var container: ViewGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.container = container!!
        val view = inflater.inflate(R.layout.fragment_display, container, false)
        activity!!.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        socialNumberET = view.findViewById(R.id.socialNr_et)

        FirebaseController.getEmployees(FirebaseController.user.companyId).subscribe {
            employees = it;
        }
        FirebaseController.getActiveShifts(FirebaseController.user.companyId).subscribe {
            activeShifts = it
        }

        clock = view.findViewById(R.id.clock)

        btn1 = view.findViewById(R.id.btn1)
        btn2 = view.findViewById(R.id.btn2)
        btn3 = view.findViewById(R.id.btn3)
        btn4 = view.findViewById(R.id.btn4)
        btn5 = view.findViewById(R.id.btn5)
        btn6 = view.findViewById(R.id.btn6)
        btn7 = view.findViewById(R.id.btn7)
        btn8 = view.findViewById(R.id.btn8)
        btn9 = view.findViewById(R.id.btn9)
        btn0 = view.findViewById(R.id.btn0)
        btnEnter = view.findViewById(R.id.btnEnter)
        btnRemove = view.findViewById(R.id.btnRemove)

        Log.d("pawell", "eeggg")
        btn1.setOnClickListener(this)
        btn2.setOnClickListener(this)
        btn3.setOnClickListener(this)
        btn4.setOnClickListener(this)
        btn5.setOnClickListener(this)
        btn6.setOnClickListener(this)
        btn7.setOnClickListener(this)
        btn8.setOnClickListener(this)
        btn9.setOnClickListener(this)
        btn0.setOnClickListener(this)
        btnEnter.setOnClickListener(this)
        btnRemove.setOnClickListener(this)

        return view
    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            btn1.id -> socialNumberET.text = StringBuilder(socialNumberET.text).append("1")
            btn2.id -> socialNumberET.text = StringBuilder(socialNumberET.text).append("2")
            btn3.id -> socialNumberET.text = StringBuilder(socialNumberET.text).append("3")
            btn4.id -> socialNumberET.text = StringBuilder(socialNumberET.text).append("4")
            btn5.id -> socialNumberET.text = StringBuilder(socialNumberET.text).append("5")
            btn6.id -> socialNumberET.text = StringBuilder(socialNumberET.text).append("6")
            btn7.id -> socialNumberET.text = StringBuilder(socialNumberET.text).append("7")
            btn8.id -> socialNumberET.text = StringBuilder(socialNumberET.text).append("8")
            btn9.id -> socialNumberET.text = StringBuilder(socialNumberET.text).append("9")
            btn0.id -> socialNumberET.text = StringBuilder(socialNumberET.text).append("0")
            btnEnter.id -> clockIn()
            btnRemove.id -> {
                if (socialNumberET.text.length > 0) socialNumberET.text = socialNumberET.text.substring(0, socialNumberET.text.length - 1)
            }
        }
    }

    fun clockIn() {
        var clockingEmployee: User? = null
        employees.forEach {
            if (socialNumberET.text.toString().toLong() == it.socialSecurityNumber) {
                clockingEmployee = it
                return@forEach
            }
        }
        if(clockingEmployee != null){

            var isEmployeeActive = false
            var activeShift = Shift()
            activeShifts.forEach {
                if (it.employeeId == clockingEmployee?.employeeId) {
                    isEmployeeActive = true
                    activeShift = it;
                    return@forEach
                }
            }
            val dialogMessage: String
            if (isEmployeeActive) {
                dialogMessage = "Stämpla ut? "
                isEmployeeClockedIn = true
            } else {
                isEmployeeClockedIn = false
                dialogMessage = "Stämpla in? "
            }

            val dialogTitle = clockingEmployee!!.firstName + " " + clockingEmployee!!.lastName + " " + clock.text

            val msgView:View = View.inflate(context,R.layout.clock_in_dialog,null)
            val alarm: AlertDialog.Builder = AlertDialog.Builder(context!!)
                    .setTitle(dialogTitle)
                    .setView(msgView)
                    .setMessage(dialogMessage)
                    .setPositiveButton("Ja", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                            val msg  =msgView.findViewById<EditText>(R.id.username).text.toString()
                            if (isEmployeeActive) {
                                activeShift.endDate = Date()
                                activeShift.messageOut =msg

                                FirebaseController.combo(activeShift, activeShift.shiftId, FirebaseController.user.companyId).subscribe {

                                }
                            } else {

                                activeShift.startDate = Date()
                                activeShift.employeeId = clockingEmployee!!.employeeId
                                activeShift.firstName = clockingEmployee!!.firstName
                                activeShift.lastName = clockingEmployee!!.lastName
                                activeShift.messageIn = msg
                                FirebaseController.addActiveShift(clockingEmployee!!.companyId, activeShift)

                            }
                            socialNumberET.setText("")
                        }
                    })
                    .setNegativeButton("Avbryt", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                        }
                    })
            alarm.create().show()
        }else{
            AlertDialog.Builder(context!!)
                    .setTitle("Fel")
                    .setMessage("Person med detta perssonnummer är inte registrerad på företaget")
                    .setNeutralButton("Ok",object :DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                        }
                    })
                    .create()
                    .show()
        }

    }

}
