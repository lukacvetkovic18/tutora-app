package hr.ferit.lukacvetkovic.tutora.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.Subject
import hr.ferit.lukacvetkovic.tutora.models.Tutor

class StudentSubjectRecyclerAdapter(
    private val items: ArrayList<Subject>,
    private val listener: ContentListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StudentSubjectViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_subject_student, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StudentSubjectViewHolder -> {
                holder.bind(listener, items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class StudentSubjectViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        private val db = Firebase.firestore

        private val subjectImage = view.findViewById<ImageView>(R.id.studentSubjectPhoto)
        private val subjectName = view.findViewById<TextView>(R.id.studentSubjectName)
        private val subjectDesc = view.findViewById<TextView>(R.id.studentSubjectDesc)
        private val subjectTutor = view.findViewById<TextView>(R.id.studentSubjectTutorName)

        fun bind(
            listener: ContentListener,
            subject: Subject,
        ) {
            Glide.with(view.context).load(subject.imageURL).into(subjectImage)
            subjectName.text = subject.name
            subjectDesc.text = subject.shortDescription
            db.collection("tutors")
                .whereEqualTo("email", subject.tutorEmail)
                .get()
                .addOnSuccessListener {
                    for (data in it.documents){
                        val tutor = data.toObject(Tutor::class.java)
                        subjectTutor.text = "Tutor: " + tutor?.firstName + " " + tutor?.lastName
                    }
                }

            subjectImage.setOnClickListener {
                listener.onItemButtonClick(subject)
            }
        }
    }

    interface ContentListener {
        fun onItemButtonClick(subject: Subject)
    }
}