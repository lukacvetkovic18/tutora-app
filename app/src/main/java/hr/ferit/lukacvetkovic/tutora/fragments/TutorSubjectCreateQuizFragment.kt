package hr.ferit.lukacvetkovic.tutora.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.Quiz
import hr.ferit.lukacvetkovic.tutora.models.StudentAttendsSubject
import hr.ferit.lukacvetkovic.tutora.models.StudentSolvesQuiz
import hr.ferit.lukacvetkovic.tutora.models.Subject

class TutorSubjectCreateQuizFragment : Fragment() {
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutor_subject_create_quiz, container, false)
        val subjectFragment = TutorSubjectFragment()
        val subjectName = arguments?.getString("SUBJECT_NAME").toString()
        val tutorEmail = arguments?.getString("TUTOR_EMAIL").toString()

        val title = view.findViewById<TextView>(R.id.tutorSubjectCreateQuizTitle)
        val image = view.findViewById<ImageView>(R.id.tutorSubjectCreateQuizImage)
        val saveBtn = view.findViewById<AppCompatButton>(R.id.tutorSubjectCreateQuizSaveBtn)
        val backBtn = view.findViewById<AppCompatImageButton>(R.id.tutorSubjectCreateQuizBackButton)

        title.text = "\n\n\t\t" + subjectName
        db.collection("subjects")
            .whereEqualTo("name", subjectName)
            .get()
            .addOnSuccessListener { result ->
                for (data in result.documents) {
                    val subject = data.toObject(Subject::class.java)
                    Glide.with(view.context).load(subject?.imageURL).into(image)
                }
            }

        saveBtn.setOnClickListener {
            val name = view.findViewById<EditText>(R.id.tutorSubjectCreateQuizNameEdit)
            val question1 = view.findViewById<EditText>(R.id.tutorSubjectCreateQuizQ1Edit)
            val answer1 = view.findViewById<EditText>(R.id.tutorSubjectCreateQuizA1)
            val question2 = view.findViewById<EditText>(R.id.tutorSubjectCreateQuizQ2Edit)
            val answer2 = view.findViewById<EditText>(R.id.tutorSubjectCreateQuizA2)
            val question3 = view.findViewById<EditText>(R.id.tutorSubjectCreateQuizQ3Edit)
            val answer3 = view.findViewById<EditText>(R.id.tutorSubjectCreateQuizA3)

            val nameText = name.text.toString()
            val question1Text = question1.text.toString()
            val answer1Text = answer1.text.toString()
            val question2Text = question2.text.toString()
            val answer2Text = answer2.text.toString()
            val question3Text = question3.text.toString()
            val answer3Text = answer3.text.toString()

            if(nameText.isNotEmpty() &&
                question1Text.isNotEmpty() &&
                answer1Text.isNotEmpty() &&
                question2Text.isNotEmpty() &&
                answer2Text.isNotEmpty() &&
                question3Text.isNotEmpty() &&
                answer3Text.isNotEmpty()) {
                db.collection("quizzes")
                    .whereEqualTo("name", nameText)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            val quiz = Quiz(
                                nameText,
                                question1Text,
                                answer1Text,
                                question2Text,
                                answer2Text,
                                question3Text,
                                answer3Text,
                                subjectName
                            )
                            db.collection("quizzes")
                                .add(quiz)

                            db.collection("studentAttendsSubject")
                                .whereEqualTo("subjectName", subjectName)
                                .get()
                                .addOnSuccessListener { result ->
                                    for (data in result.documents) {
                                        val attend =
                                            data.toObject(StudentAttendsSubject::class.java)
                                        val solve = StudentSolvesQuiz(
                                            attend!!.studentEmail,
                                            nameText,
                                            subjectName,
                                            0,
                                            false
                                        )
                                        db.collection("studentSolvesQuiz")
                                            .add(solve)
                                    }

                                    val bundle = Bundle()
                                    bundle.putString("TUTOR_EMAIL", tutorEmail)
                                    bundle.putString("SUBJECT_NAME", subjectName)
                                    subjectFragment.arguments = bundle

                                    val fragmentTransaction: FragmentTransaction? =
                                        activity?.supportFragmentManager?.beginTransaction()
                                    fragmentTransaction?.replace(
                                        R.id.fragmentContainerView,
                                        subjectFragment
                                    )
                                    fragmentTransaction?.commit()
                                }
                        } else {
                            Toast.makeText(
                                activity?.baseContext,
                                "Quiz with this name already exist.",
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
            bundle.putString("SUBJECT_NAME", subjectName)
            subjectFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainerView, subjectFragment)
            fragmentTransaction?.commit()
        }

        return view
    }
}