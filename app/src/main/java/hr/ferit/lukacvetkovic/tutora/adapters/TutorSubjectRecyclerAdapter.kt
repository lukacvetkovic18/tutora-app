package hr.ferit.lukacvetkovic.tutora.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.models.Subject

class TutorSubjectRecyclerAdapter(
    private val items: ArrayList<Subject>,
    private val listener: ContentListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TutorSubjectViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_subject_tutor, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TutorSubjectViewHolder -> {
                holder.bind(listener, items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TutorSubjectViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        private val subjectImage = view.findViewById<ImageView>(R.id.tutorSubjectPhoto)
        private val subjectName = view.findViewById<TextView>(R.id.tutorSubjectName)
        private val subjectDesc = view.findViewById<TextView>(R.id.tutorSubjectDesc)

        fun bind(
            listener: ContentListener,
            subject: Subject,
        ) {
            Glide.with(view.context).load(subject.imageURL).into(subjectImage)
            subjectName.text = subject.name
            subjectDesc.text = subject.shortDescription

            subjectImage.setOnClickListener {
                listener.onItemButtonClick(subject)
            }
        }
    }

    interface ContentListener {
        fun onItemButtonClick(subject: Subject)
    }
}