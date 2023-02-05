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
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.adapters.TutorSubjectStudentsRecyclerAdapter
import hr.ferit.lukacvetkovic.tutora.models.Student
import hr.ferit.lukacvetkovic.tutora.models.StudentAttendsSubject
import hr.ferit.lukacvetkovic.tutora.models.Subject

class TutorSubjectViewStudentsFragment : Fragment(),
    TutorSubjectStudentsRecyclerAdapter.ContentListener {
    private val db = Firebase.firestore
    private lateinit var recyclerAdapter: TutorSubjectStudentsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutor_subject_view_students, container, false)
        val subjectFragment = TutorSubjectFragment()
        val tutorEmail = arguments?.getString("TUTOR_EMAIL").toString()
        val subjectName = arguments?.getString("SUBJECT_NAME").toString()

        val title = view.findViewById<TextView>(R.id.tutorSubjectViewStudentsTitle)
        val image = view.findViewById<ImageView>(R.id.tutorSubjectViewStudentsImage)
        val backBtn = view.findViewById<AppCompatImageButton>(R.id.tutorSubjectViewStudentsBackButton)
        val recyclerView = view.findViewById<RecyclerView>(R.id.tutorSubjectViewStudentsList)

        title.text = "\n\n\t\t" + subjectName
        db.collection("subjects")
            .whereEqualTo("name", subjectName)
            .get()
            .addOnSuccessListener { result ->
                for (data in result.documents) {
                    val subject = data.toObject(Subject::class.java)
                    Glide.with(view.context).load(subject?.imageURL).into(image)
                }
            }

        db.collection("studentAttendsSubject")
            .whereEqualTo("subjectName", subjectName)
            .get()
            .addOnSuccessListener { result ->
                val students = ArrayList<Student>()
                for(data in result.documents) {
                    val attend = data.toObject(StudentAttendsSubject::class.java)
                    db.collection("students")
                        .whereEqualTo("email", attend!!.studentEmail)
                        .get()
                        .addOnSuccessListener { res ->
                            for(data1 in res.documents){
                                val student = data1.toObject(Student::class.java)
                                students.add(student!!)
                            }
                            recyclerAdapter = TutorSubjectStudentsRecyclerAdapter(students, this@TutorSubjectViewStudentsFragment)
                            recyclerView.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = recyclerAdapter
                            }
                        }
                }
            }
            .addOnFailureListener {
                Log.w("ViewStudentsFragment", "Error getting documents.", it)
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

    override fun onItemButtonClick(index: Int, student: Student) {
        val subjectName = arguments?.getString("SUBJECT_NAME").toString()
        recyclerAdapter.removeItem(index)
        db.collection("studentAttendsSubject")
            .whereEqualTo("subjectName", subjectName)
            .whereEqualTo("studentEmail", student.email)
            .get()
            .addOnSuccessListener {
                for(data in it.documents){
                    db.collection("studentAttendsSubject")
                        .document(data.id)
                        .delete()
                }
            }
    }
}