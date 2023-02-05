package hr.ferit.lukacvetkovic.tutora.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.adapters.QuizAvailableRecyclerAdapter
import hr.ferit.lukacvetkovic.tutora.adapters.StudentQuizResultsRecyclerAdapter
import hr.ferit.lukacvetkovic.tutora.models.Student
import hr.ferit.lukacvetkovic.tutora.models.StudentSolvesQuiz
import hr.ferit.lukacvetkovic.tutora.models.Subject

class StudentSubjectFragment : Fragment(),
QuizAvailableRecyclerAdapter.ContentListener {
    private val db = Firebase.firestore
    private lateinit var solveRecyclerAdapter: QuizAvailableRecyclerAdapter
    private lateinit var resultsRecyclerAdapter: StudentQuizResultsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_subject, container, false)
        val studentFragment = StudentHomeFragment()
        val studentEmail = arguments?.getString("STUDENT_EMAIL").toString()
        val subjectName = arguments?.getString("SUBJECT_NAME").toString()

        val title = view.findViewById<TextView>(R.id.studentSubjectTitle)
        val desc = view.findViewById<TextView>(R.id.studentSubjectDescription)
        val image = view.findViewById<ImageView>(R.id.studentSubjectImage)
        val backBtn = view.findViewById<AppCompatImageButton>(R.id.studentSubjectBackButton)
        val leaveBtn = view.findViewById<AppCompatButton>(R.id.studentSubjectAddStudentBtn)
        val solveRecyclerView =
            view.findViewById<RecyclerView>(R.id.studentSubjectQuizAvailableRecyclerView)
        val resultsRecyclerView =
            view.findViewById<RecyclerView>(R.id.studentSubjectQuizResRecyclerView)

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

        db.collection("studentSolvesQuiz")
            .whereEqualTo("studentEmail", studentEmail)
            .whereEqualTo("subjectName", subjectName)
            .whereEqualTo("solved", false)
            .get()
            .addOnSuccessListener { result ->
                val quizzes1 = ArrayList<StudentSolvesQuiz>()
                for (data in result.documents) {
                    val quiz = data.toObject(StudentSolvesQuiz::class.java)
                    if (quiz != null) {
                        quizzes1.add(quiz)
                    }
                }
                solveRecyclerAdapter =
                    QuizAvailableRecyclerAdapter(quizzes1, this@StudentSubjectFragment)
                solveRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = solveRecyclerAdapter
                }
            }.addOnFailureListener { exception ->
                Log.d("StudentSubjectFragment", "Error getting documents.", exception)
            }

        db.collection("studentSolvesQuiz")
            .whereEqualTo("studentEmail", studentEmail)
            .whereEqualTo("subjectName", subjectName)
            .whereEqualTo("solved", true)
            .get()
            .addOnSuccessListener { result ->
                val quizzes2 = ArrayList<StudentSolvesQuiz>()
                for (data in result.documents) {
                    val quiz = data.toObject(StudentSolvesQuiz::class.java)
                    if (quiz != null) {
                        quizzes2.add(quiz)
                    }
                }
                resultsRecyclerAdapter = StudentQuizResultsRecyclerAdapter(quizzes2)
                resultsRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = resultsRecyclerAdapter
                }
            }.addOnFailureListener { exception ->
                Log.d("StudentSubjectFragment", "Error getting documents.", exception)
            }

        leaveBtn.setOnClickListener {
            db.collection("studentAttendsSubject")
                .whereEqualTo("studentEmail", studentEmail)
                .whereEqualTo("subjectName", subjectName)
                .get()
                .addOnSuccessListener {
                    for (data in it.documents) {
                        db.collection("studentAttendsSubject")
                            .document(data.id)
                            .delete()
                    }

                    val bundle = Bundle()
                    bundle.putString("STUDENT_EMAIL", studentEmail)
                    studentFragment.arguments = bundle

                    val fragmentTransaction: FragmentTransaction? =
                        activity?.supportFragmentManager?.beginTransaction()
                    fragmentTransaction?.replace(R.id.fragmentContainerView, studentFragment)
                    fragmentTransaction?.commit()
                }
        }

        backBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("STUDENT_EMAIL", studentEmail)
            studentFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainerView, studentFragment)
            fragmentTransaction?.commit()
        }

        return view
    }

    override fun onItemButtonClick(quiz: StudentSolvesQuiz) {
        val solveQuizFragment = StudentSubjectSolveQuizFragment()
        val subjectName = arguments?.getString("SUBJECT_NAME").toString()
        val bundle = Bundle()
        bundle.putString("STUDENT_EMAIL", quiz.studentEmail)
        bundle.putString("SUBJECT_NAME", subjectName)
        bundle.putString("QUIZ_NAME", quiz.quizName)
        solveQuizFragment.arguments = bundle

        val fragmentTransaction: FragmentTransaction? =
            activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragmentContainerView, solveQuizFragment)
        fragmentTransaction?.commit()
    }
}