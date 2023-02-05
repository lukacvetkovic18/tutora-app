package hr.ferit.lukacvetkovic.tutora.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.Student

class TutorSubjectStudentsRecyclerAdapter(
    private val items: ArrayList<Student>,
    private val listener: ContentListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TutorSubjectStudentsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_subject_students_tutor, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TutorSubjectStudentsViewHolder -> {
                holder.bind(position, listener, items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun removeItem(index: Int) {
        items.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, items.size)
    }

    class TutorSubjectStudentsViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val db = Firebase.firestore
        private val studentName = view.findViewById<TextView>(R.id.tutorStudentsSubjectName)
        private val studentEmail = view.findViewById<TextView>(R.id.tutorStudentsSubjectEmail)
        private val removeBtn = view.findViewById<AppCompatButton>(R.id.tutorStudentsSubjectRemoveBtn)

        fun bind(
            index: Int,
            listener: ContentListener,
            student: Student,
        ) {
            studentName.text = student.firstName + " " +student.lastName
            studentEmail.text = student.email

            removeBtn.setOnClickListener {
                listener.onItemButtonClick(index, student)
            }
        }
    }

    interface ContentListener {
        fun onItemButtonClick(index: Int, student: Student)
    }
}