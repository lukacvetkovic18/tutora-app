package hr.ferit.lukacvetkovic.tutora.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.Student
import hr.ferit.lukacvetkovic.tutora.models.Tutor

class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        auth = Firebase.auth
        val tutorFragment = TutorHomeFragment()
        val studentFragment = StudentHomeFragment()
        val loginFragment = LoginFragment()

        val regBtn = view.findViewById<AppCompatButton>(R.id.regButton)
        val regBackBtn = view.findViewById<AppCompatImageButton>(R.id.regBackBtn)

        regBtn.setOnClickListener {
            val firstName = view.findViewById<EditText>(R.id.regFirstNameInput)
            val lastName = view.findViewById<EditText>(R.id.regLastNameInput)
            val age = view.findViewById<EditText>(R.id.regAgeInput)
            val email = view.findViewById<EditText>(R.id.regEmailInput)
            val password = view.findViewById<EditText>(R.id.regPassInput)
            val radioGroup = view.findViewById<RadioGroup>(R.id.regRoleGroup)
            val radioButton = view.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)

            val firstNameText = firstName.text.toString()
            val lastNameText = lastName.text.toString()
            val ageText = age.text.toString().toInt()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val roleText = radioButton.text.toString()

            if(firstNameText.isNotEmpty() &&
                lastNameText.isNotEmpty() &&
                ageText.toString().isNotEmpty() &&
                emailText.isNotEmpty() &&
                passwordText.isNotEmpty() &&
                roleText.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (roleText == "Student") {
                                val student = Student(
                                    firstNameText,
                                    lastNameText,
                                    ageText,
                                    emailText,
                                    passwordText
                                )
                                db.collection("students")
                                    .add(student)

                                val bundle = Bundle()
                                bundle.putString("STUDENT_EMAIL", emailText)
                                studentFragment.arguments = bundle

                                val fragmentTransaction: FragmentTransaction? =
                                    activity?.supportFragmentManager?.beginTransaction()
                                fragmentTransaction?.replace(
                                    R.id.fragmentContainerView,
                                    studentFragment
                                )
                                fragmentTransaction?.commit()

                            } else if (roleText == "Tutor") {
                                val tutor = Tutor(
                                    firstNameText,
                                    lastNameText,
                                    ageText,
                                    emailText,
                                    passwordText
                                )
                                db.collection("tutors")
                                    .add(tutor)

                                val bundle = Bundle()
                                bundle.putString("TUTOR_EMAIL", emailText)
                                tutorFragment.arguments = bundle

                                val fragmentTransaction: FragmentTransaction? =
                                    activity?.supportFragmentManager?.beginTransaction()
                                fragmentTransaction?.replace(
                                    R.id.fragmentContainerView,
                                    tutorFragment
                                )
                                fragmentTransaction?.commit()
                            }
                        } else {
                            Toast.makeText(
                                activity?.baseContext,
                                "Registration failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            activity?.baseContext, "Registration failed. ${it.localizedMessage}",
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

        regBackBtn.setOnClickListener {
            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainerView, loginFragment)
            fragmentTransaction?.commit()
        }

        return view
    }
}