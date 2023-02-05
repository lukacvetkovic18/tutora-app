package hr.ferit.lukacvetkovic.tutora.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.StudentSolvesQuiz

class StudentQuizResultsRecyclerAdapter(
    private val items: ArrayList<StudentSolvesQuiz>
): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StudentQuizResultsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_subject_quiz_res_student, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StudentQuizResultsViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class StudentQuizResultsViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        private val quizName = view.findViewById<TextView>(R.id.studentQuizResName)
        private val quizScore = view.findViewById<TextView>(R.id.studentQuizResResult)

        fun bind(
            quiz: StudentSolvesQuiz
        ) {
            quizName.text = quiz.quizName
            quizScore.text = quiz.correctAnswers.toString() + "/3"
        }
    }
}