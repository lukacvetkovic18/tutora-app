package hr.ferit.lukacvetkovic.tutora.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.Student
import hr.ferit.lukacvetkovic.tutora.models.Subject
import hr.ferit.lukacvetkovic.tutora.models.Tutor

class TutorCreateSubjectFragment : Fragment() {
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutor_create_subject, container, false)
        val tutorFragment = TutorHomeFragment()
        val tutorEmail = arguments?.getString("TUTOR_EMAIL").toString()

        val createBtn = view.findViewById<AppCompatButton>(R.id.tutorCreateSubjectButton)
        val backBtn = view.findViewById<AppCompatImageButton>(R.id.tutorCreateSubjectBackBtn)

        createBtn.setOnClickListener {
            val name = view.findViewById<EditText>(R.id.tutorCreateSubjectNameInput)
            val shortDesc = view.findViewById<EditText>(R.id.tutorCreateSubjectShortDescInput)
            val desc = view.findViewById<EditText>(R.id.tutorCreateSubjectDescInput)
            val imageURL = view.findViewById<EditText>(R.id.tutorCreateSubjectImageInput)

            val nameText = name.text.toString()
            val shortDescText = shortDesc.text.toString()
            val descText = desc.text.toString()
            val imageURLText = imageURL.text.toString()

            if(nameText.isNotEmpty() && shortDescText.isNotEmpty() && descText.isNotEmpty() && imageURLText.isNotEmpty()) {
                db.collection("subjects")
                    .get()
                    .addOnSuccessListener { result ->
                        var flag = 0;
                        for (data in result.documents) {
                            val subject = data.toObject(Subject::class.java)
                            if (subject?.name == nameText) {
                                flag = 1;
                            }
                        }
                        if (flag == 0) {
                            val subject = Subject(
                                nameText,
                                shortDescText,
                                descText,
                                imageURLText,
                                tutorEmail
                            )
                            db.collection("subjects")
                                .add(subject)

                            val bundle = Bundle()
                            bundle.putString("TUTOR_EMAIL", tutorEmail)
                            tutorFragment.arguments = bundle

                            val fragmentTransaction: FragmentTransaction? =
                                activity?.supportFragmentManager?.beginTransaction()
                            fragmentTransaction?.replace(R.id.fragmentContainerView, tutorFragment)
                            fragmentTransaction?.commit()
                        } else {
                            Toast.makeText(
                                activity?.baseContext,
                                "Subject with that name already exists.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    activity?.baseContext, "You must fill all the fields.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        backBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("TUTOR_EMAIL", tutorEmail)
            tutorFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainerView, tutorFragment)
            fragmentTransaction?.commit()
        }

        return view;
    }
}