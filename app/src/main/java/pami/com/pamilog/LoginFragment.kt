package pami.com.pamilog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class LoginFragment: android.support.v4.app.Fragment() {

    lateinit var loginBtn: Button;
    lateinit var userET:EditText
    lateinit var password:EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_login,container,false)
        loginBtn = view.findViewById(R.id.loginBtn)
        userET = view.findViewById(R.id.user_et)
        password = view.findViewById(R.id.password_et)

        loginBtn.setOnClickListener {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(userET.text.toString(), password.text.toString()).addOnCompleteListener(OnCompleteListener {
              if(!it.isSuccessful){
                  Toast.makeText(context, "Fel e-post elelr lösenord",Toast.LENGTH_SHORT).show()
              }
            })
        }
        return view
    }
}