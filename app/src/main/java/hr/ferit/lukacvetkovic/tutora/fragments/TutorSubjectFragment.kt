package hr.ferit.lukacvetkovic.tutora.fragments

import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.adapters.TutorQuizResultsRecyclerAdapter
import hr.ferit.lukacvetkovic.tutora.adapters.TutorQuizzesRecyclerAdapter
import hr.ferit.lukacvetkovic.tutora.adapters.TutorSubjectRecyclerAdapter
import hr.ferit.lukacvetkovic.tutora.models.Quiz
import hr.ferit.lukacvetkovic.tutora.models.StudentAttendsSubject
import hr.ferit.lukacvetkovic.tutora.models.StudentSolvesQuiz
import hr.ferit.lukacvetkovic.tutora.models.Subject

class TutorSubjectFragment : Fragment(),
    TutorQuizzesRecyclerAdapter.ContentListener {
    private val db = Firebase.firestore
    private lateinit var recyclerAdapter: TutorQuizzesRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutor_subject, container, false)
        val tutorFragment = TutorHomeFragment()
        val viewStudentsFragment = TutorSubjectViewStudentsFragment()
        val createQuizFragment = TutorSubjectCreateQuizFragment()
        val tutorEmail = arguments?.getString("TUTOR_EMAIL").toString()
        val subjectName = arguments?.getString("SUBJECT_NAME").toString()

        val title = view.findViewById<TextView>(R.id.tutorSubjectTitle)
        val desc = view.findViewById<TextView>(R.id.tutorSubjectDescription)
        val image = view.findViewById<ImageView>(R.id.tutorSubjectImage)
        val studentsBtn = view.findViewById<AppCompatButton>(R.id.tutorSubjectSeeStudentsBtn)
        val createQuizBtn = view.findViewById<AppCompatButton>(R.id.tutorSubjectCreateQuitBtn)
        val addStudentBtn = view.findViewById<AppCompatButton>(R.id.tutorSubjectAddStudentBtn)
        val backBtn = view.findViewById<AppCompatImageButton>(R.id.tutorSubjectBackButton)
        val recyclerView = view.findViewById<RecyclerView>(R.id.tutorSubjectQuizResRecyclerView)

        title.text = "\n\n\t\t" + subjectName
        db.collection("subjects")
            .whereEqualTo("name", subjectName)
            .get()
            .addOnSuccessListener { result ->
                for (data in result.documents) {
                    val subject = data.toObject(Subject::class.java)
                    desc.text = subject?.description
                    Glide.with(view.context).load(subject?.imageURL).into(image)
                }
            }

        db.collection("quizzes")
            .whereEqualTo("subjectName", subjectName)
            .get()
            .addOnSuccessListener { result ->
                val quizzes = ArrayList<Quiz>()
                for (data in result.documents) {
                    val quiz = data.toObject(Quiz::class.java)
                    if (quiz != null) {
                        quizzes.add(quiz)
                    }
                }

                recyclerAdapter = TutorQuizzesRecyclerAdapter(quizzes, this@TutorSubjectFragment)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = recyclerAdapter
                }
            }.addOnFailureListener { exception ->
                Log.d("TutorSubjectFragment", "Error getting documents.", exception)
            }

        studentsBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("TUTOR_EMAIL", tutorEmail)
            bundle.putString("SUBJECT_NAME", subjectName)
            viewStudentsFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainerView, viewStudentsFragment)
            fragmentTransaction?.commit()
        }

        createQuizBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("TUTOR_EMAIL", tutorEmail)
            bundle.putString("SUBJECT_NAME", subjectName)
            createQuizFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainerView, createQuizFragment)
            fragmentTransaction?.commit()
        }

        addStudentBtn.setOnClickListener {
            val email = view.findViewById<EditText>(R.id.tutorSubjectAddStudentEdit)
            val emailText = email.text.toString()

            if(emailText.isNotEmpty()) {
                db.collection("students")
                    .whereEqualTo("email", emailText)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            Toast.makeText(
                                activity?.baseContext,
                                "Student doesn't exist.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            db.collection("studentAttendsSubject")
                                .whereEqualTo("studentEmail", emailText)
                                .whereEqualTo("subjectName", subjectName)
                                .get()
                                .addOnSuccessListener { result ->
                                    if (result.isEmpty) {
                                        val newAttend = StudentAttendsSubject(
                                            emailText,
                                            subjectName
                                        )

                                        db.collection("studentAttendsSubject")
                                            .add(newAttend)
                                        Toast.makeText(
                                            activity?.baseContext,
                                            "Student successfully added to the class.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        db.collection("quizzes")
                                            .whereEqualTo("subjectName", subjectName)
                                            .get()
                                            .addOnSuccessListener { result1 ->
                                                for (data in result1) {
                                                    val quiz = data.toObject(Quiz::class.java)
                                                    val solve = StudentSolvesQuiz(
                                                        emailText,
                                                        quiz.name,
                                                        subjectName,
                                                        0,
                                                        false
                                                    )
                                                    db.collection("studentSolvesQuiz")
                                                        .add(solve)
                                                }
                                            }
                                    } else {
                                        Toast.makeText(
                                            activity?.baseContext,
                                            "Student is already in this class.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }
            } else {
                Toast.makeText(
                            activity?.baseContext, "Email field cannot be empty.",
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

        return view
    }

    override fun onItemButtonClick(quiz: Quiz) {
        val quizResultsFragment = TutorSubjectQuizResFragment()
        val tutorEmail = arguments?.getString("TUTOR_EMAIL").toString()
        val bundle = Bundle()
        bundle.putString("TUTOR_EMAIL", tutorEmail)
        bundle.putString("SUBJECT_NAME", quiz.subjectName)
        bundle.putString("QUIZ_NAME", quiz.name)
        quizResultsFragment.arguments = bundle

        val fragmentTransaction: FragmentTransaction? =
            activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragmentContainerView, quizResultsFragment)
        fragmentTransaction?.commit()
    }
}