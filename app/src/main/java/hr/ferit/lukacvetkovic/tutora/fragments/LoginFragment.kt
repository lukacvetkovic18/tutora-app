package hr.ferit.lukacvetkovic.tutora.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.Student
import hr.ferit.lukacvetkovic.tutora.models.Tutor

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val tutorFragment = TutorHomeFragment()
        val studentFragment = StudentHomeFragment()
        val registerFragment = RegisterFragment()

        auth = Firebase.auth

        val loginBtn = view.findViewById<AppCompatButton>(R.id.loginButton)
        val registerTxt = view.findViewById<TextView>(R.id.loginRegClickText)

        loginBtn.setOnClickListener {
            val email = view.findViewById<EditText>(R.id.loginEmailInput)
            val password = view.findViewById<EditText>(R.id.loginPassInput)

            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if(emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                auth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var student: Student? = null;
                            db.collection("students")
                                .whereEqualTo("email", emailText)
                                .get()
                                .addOnSuccessListener { results ->
                                    for (result in results.documents) {
                                        student = result.toObject(Student::class.java)
                                    }
                                    if (student != null) {
                                        val bundle = Bundle()
                                        bundle.putString("STUDENT_EMAIL", student?.email)
                                        studentFragment.arguments = bundle

                                        val fragmentTransaction: FragmentTransaction? =
                                            activity?.supportFragmentManager?.beginTransaction()
                                        fragmentTransaction?.replace(
                                            R.id.fragmentContainerView,
                                            studentFragment
                                        )
                                        fragmentTransaction?.commit()
                                    }
                                }


                            var tutor: Tutor? = null;
                            db.collection("tutors")
                                .whereEqualTo("email", emailText)
                                .get()
                                .addOnSuccessListener { results ->
                                    for (result in results.documents) {
                                        tutor = result.toObject(Tutor::class.java)
                                    }
                                    if (tutor != null) {
                                        val bundle = Bundle()
                                        bundle.putString("TUTOR_EMAIL", tutor?.email)
                                        tutorFragment.arguments = bundle

                                        val fragmentTransaction: FragmentTransaction? =
                                            activity?.supportFragmentManager?.beginTransaction()
                                        fragmentTransaction?.replace(
                                            R.id.fragmentContainerView,
                                            tutorFragment
                                        )
                                        fragmentTransaction?.commit()
                                    }
                                }

                        } else {
                            Toast.makeText(
                                activity?.baseContext,
                                "Login failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            activity?.baseContext, "Login failed. ${it.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(
                            activity?.baseContext, "You must fill all the fields.",
                            Toast.LENGTH_SHORT
                        ).show()
            }
        }

        registerTxt.setOnClickListener {
            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainerView, registerFragment)
            fragmentTransaction?.commit()
        }


        return view
    }
}