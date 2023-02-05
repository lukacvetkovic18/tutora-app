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
import hr.ferit.lukacvetkovic.tutora.models.Subject

class StudentSubjectSolveQuizFragment : Fragment() {
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_subject_solve_quiz, container, false)
        val subjectFragment = StudentSubjectFragment()
        val studentEmail = arguments?.getString("STUDENT_EMAIL").toString()
        val subjectName = arguments?.getString("SUBJECT_NAME").toString()
        val quizName = arguments?.getString("QUIZ_NAME").toString()

        val title = view.findViewById<TextView>(R.id.studentSubjectSolveQuizTitle)
        val desc = view.findViewById<TextView>(R.id.studentSubjectSolveQuizDesc)
        val image = view.findViewById<ImageView>(R.id.studentSubjectSolveQuizImage)
        val saveBtn = view.findViewById<AppCompatButton>(R.id.studentSubjectSolveQuizSaveBtn)
        val backBtn = view.findViewById<AppCompatImageButton>(R.id.studentSubjectSolveQuizBackButton)
        val question1 = view.findViewById<TextView>(R.id.studentSubjectSolveQuizQ1Txt)
        val question2 = view.findViewById<TextView>(R.id.studentSubjectSolveQuizQ2Txt)
        val question3 = view.findViewById<TextView>(R.id.studentSubjectSolveQuizQ3Txt)

        title.text = "\n\n\t\t" + subjectName
        desc.text = quizName
        db.collection("subjects")
            .whereEqualTo("name", subjectName)
            .get()
            .addOnSuccessListener { result ->
                for (data in result.documents) {
                    val subject = data.toObject(Subject::class.java)
                    Glide.with(view.context).load(subject?.imageURL).into(image)
                }
            }
        db.collection("quizzes")
            .whereEqualTo("name", quizName)
            .get()
            .addOnSuccessListener { result ->
                for (data in result.documents) {
                    val quiz = data.toObject(Quiz::class.java)
                    question1.text = quiz?.question1
                    question2.text = quiz?.question2
                    question3.text = quiz?.question3
                }
            }

        saveBtn.setOnClickListener {

            val answer1 = view.findViewById<EditText>(R.id.studentSubjectSolveQuizA1)
            val answer2 = view.findViewById<EditText>(R.id.studentSubjectSolveQuizA2)
            val answer3 = view.findViewById<EditText>(R.id.studentSubjectSolveQuizA3)
            var counter = 0

            val answer1Text = answer1.text.toString()
            val answer2Text = answer2.text.toString()
            val answer3Text = answer3.text.toString()

            if(answer1Text.isNotEmpty() && answer2Text.isNotEmpty() && answer3Text.isNotEmpty()) {
                db.collection("quizzes")
                    .whereEqualTo("name", quizName)
                    .get()
                    .addOnSuccessListener { result ->
                        for (data in result.documents) {
                            val quiz = data.toObject(Quiz::class.java)
                            if (quiz?.answer1?.lowercase() == answer1Text.lowercase()) {
                                counter++
                            }
                            if (quiz?.answer2?.lowercase() == answer2Text.lowercase()) {
                                counter++
                            }
                            if (quiz?.answer3?.lowercase() == answer3Text.lowercase()) {
                                counter++
                            }

                            db.collection("studentSolvesQuiz")
                                .whereEqualTo("studentEmail", studentEmail)
                                .whereEqualTo("quizName", quizName)
                                .get()
                                .addOnSuccessListener { result1 ->
                                    for (data1 in result1.documents) {
                                        db.collection("studentSolvesQuiz")
                                            .document(data1.id)
                                            .update(
                                                "correctAnswers", counter,
                                                "solved", true
                                            )
                                    }

                                    val bundle = Bundle()
                                    bundle.putString("STUDENT_EMAIL", studentEmail)
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
                        }
                    }
            } else {
                Toast.makeText(
                            activity?.baseContext, "You must answer all the questions.",
                            Toast.LENGTH_SHORT
                        ).show()
            }
        }

        backBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("STUDENT_EMAIL", studentEmail)
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