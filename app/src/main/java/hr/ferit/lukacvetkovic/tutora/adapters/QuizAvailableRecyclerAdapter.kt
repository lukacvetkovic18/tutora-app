package hr.ferit.lukacvetkovic.tutora.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.StudentSolvesQuiz

class QuizAvailableRecyclerAdapter(
    private val items: ArrayList<StudentSolvesQuiz>,
    private val listener: ContentListener
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return QuizAvailableViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_subject_quiz_available_student, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is QuizAvailableViewHolder -> {
                holder.bind(listener, items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class QuizAvailableViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        private val quizName = view.findViewById<TextView>(R.id.studentQuizAvailableName)
        private val solveBtn = view.findViewById<Button>(R.id.studentQuizAvailableSolveBtn)

        fun bind(
            listener: ContentListener,
            quiz: StudentSolvesQuiz
        ) {
            quizName.text = quiz.quizName

            solveBtn.setOnClickListener {
                listener.onItemButtonClick(quiz)
            }
        }
    }

    interface ContentListener {
        fun onItemButtonClick(quiz: StudentSolvesQuiz)
    }
}
