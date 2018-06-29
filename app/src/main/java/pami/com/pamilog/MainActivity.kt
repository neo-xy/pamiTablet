package pami.com.pamilog

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    lateinit var actionBar:ActionBar
    lateinit var fragmentMenager:FragmentManager
    lateinit var fragmentTransaction: FragmentTransaction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar = this.supportActionBar!!
        actionBar.title ="ffff"

        FirebaseAuth.getInstance().addAuthStateListener {
            val auth = it
            fragmentMenager = this.supportFragmentManager
            fragmentTransaction = fragmentMenager.beginTransaction()
            if (it.currentUser != null) {
                FirebaseController.getEmployee(it.currentUser!!.uid).subscribe{
                    if(it.role== "boss"){
                        fragmentTransaction.replace(R.id.display, Display()).commit()

                        FirebaseController.getCompany(FirebaseController.user.companyId).subscribe{
                            actionBar.title = it.companyName
                        }
                    }else{
                        AlertDialog.Builder(baseContext)
                                .setMessage("Du har inte behörighet att logga in på denna enhet")
                                .create().show()
                        auth.signOut()
                    }
                }
            } else {
                fragmentTransaction.replace(R.id.display, LoginFragment()).commit()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
      MenuInflater(this).inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        FirebaseAuth.getInstance().signOut()
        return true
    }
}
