package hr.ferit.lukacvetkovic.tutora.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.Student
import hr.ferit.lukacvetkovic.tutora.models.StudentSolvesQuiz

class TutorQuizResultsRecyclerAdapter(
    private val items: ArrayList<StudentSolvesQuiz>
): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TutorQuizResultsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_subject_quiz_res_tutor, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TutorQuizResultsViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TutorQuizResultsViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        private val db = Firebase.firestore
        private val studentName = view.findViewById<TextView>(R.id.tutorQuizResStudentName)
        private val studentEmail = view.findViewById<TextView>(R.id.tutorQuizResStudentEmail)
        private val quizScore = view.findViewById<TextView>(R.id.tutorQuizResStudentResult)

        fun bind(
            quiz: StudentSolvesQuiz
        ) {
            db.collection("students")
                .whereEqualTo("email", quiz.studentEmail)
                .get()
                .addOnSuccessListener { results ->
                    for(res in results.documents) {
                        val student = res.toObject(Student::class.java)
                        studentName.text = student?.firstName + " " + student?.lastName
                        studentEmail.text = student?.email
                        quizScore.text = quiz.correctAnswers.toString() + "/3"
                    }
                }
        }
    }
}