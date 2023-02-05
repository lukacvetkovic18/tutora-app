package hr.ferit.lukacvetkovic.tutora.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.Quiz

class TutorQuizzesRecyclerAdapter (
    private val items: ArrayList<Quiz>,
    private val listener: ContentListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TutorQuizzesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_subject_quizzes_tutor, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TutorQuizzesViewHolder -> {
                holder.bind(listener, items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TutorQuizzesViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val quizBtn = view.findViewById<AppCompatButton>(R.id.tutorQuizzesNameButton)

        fun bind(
            listener: ContentListener,
            quiz: Quiz,
        ) {
            quizBtn.text = "\t\t" + quiz.name + "\t\t"

            quizBtn.setOnClickListener {
                listener.onItemButtonClick(quiz)
            }
        }
    }

    interface ContentListener {
        fun onItemButtonClick(quiz: Quiz)
    }
}
