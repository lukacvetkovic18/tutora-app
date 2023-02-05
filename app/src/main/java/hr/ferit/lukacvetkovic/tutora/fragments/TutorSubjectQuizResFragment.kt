package hr.ferit.lukacvetkovic.tutora.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.adapters.TutorQuizResultsRecyclerAdapter
import hr.ferit.lukacvetkovic.tutora.models.StudentSolvesQuiz
import hr.ferit.lukacvetkovic.tutora.models.Subject

class TutorSubjectQuizResFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var recyclerAdapter: TutorQuizResultsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutor_subject_quiz_res, container, false)
        val subjectFragment = TutorSubjectFragment()
        val tutorEmail = arguments?.getString("TUTOR_EMAIL").toString()
        val subjectName = arguments?.getString("SUBJECT_NAME").toString()
        val quizName = arguments?.getString("QUIZ_NAME").toString()

        val title = view.findViewById<TextView>(R.id.tutorSubjectQuizResTitle)
        val image = view.findViewById<ImageView>(R.id.tutorSubjectQuizResImage)
        val desc = view.findViewById<TextView>(R.id.tutorSubjectQuizResDesc)
        val backBtn = view.findViewById<AppCompatImageButton>(R.id.tutorSubjectVQuizResBackButton)
        val recyclerView = view.findViewById<RecyclerView>(R.id.tutorSubjectQuizResList)

        title.text = "\n\n\t\t" + subjectName
        desc.text = quizName + " results:"
        db.collection("subjects")
            .whereEqualTo("name", subjectName)
            .get()
            .addOnSuccessListener { result ->
                for (data in result.documents) {
                    val subject = data.toObject(Subject::class.java)
                    Glide.with(view.context).load(subject?.imageURL).into(image)
                }
            }

        db.collection("studentSolvesQuiz")
            .whereEqualTo("quizName", quizName)
            .whereEqualTo("solved", true)
            .get()
            .addOnSuccessListener { result ->
                val quizzes = ArrayList<StudentSolvesQuiz>()
                for(data in result.documents) {
                    val quiz = data.toObject(StudentSolvesQuiz::class.java)
                    quizzes.add(quiz!!)
                }

                recyclerAdapter = TutorQuizResultsRecyclerAdapter(quizzes)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = recyclerAdapter
                }
            }

            .addOnFailureListener {
                Log.w("QuizResultsFragment", "Error getting documents.", it)
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