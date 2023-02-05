package hr.ferit.lukacvetkovic.tutora.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.adapters.StudentSubjectRecyclerAdapter
import hr.ferit.lukacvetkovic.tutora.models.Student
import hr.ferit.lukacvetkovic.tutora.models.StudentAttendsSubject
import hr.ferit.lukacvetkovic.tutora.models.Subject

class StudentHomeFragment : Fragment(),
    StudentSubjectRecyclerAdapter.ContentListener {
    private val db = Firebase.firestore
    private lateinit var recyclerAdapter: StudentSubjectRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_home, container, false)
        val loginFragment = LoginFragment()
        val studentEmail = arguments?.getString("STUDENT_EMAIL").toString()
        var student: Student?
        val title = view.findViewById<TextView>(R.id.studentHomeTitle)
        val logoutBtn = view.findViewById<AppCompatImageButton>(R.id.studentHomeLogout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.studentHomeSubjectRecyclerView)

        db.collection("students")
            .whereEqualTo("email", studentEmail)
            .get()
            .addOnSuccessListener { result ->
                for (data in result.documents) {
                    student = data.toObject(Student::class.java)
                    title.text = "Welcome " + student!!.firstName
                }
            }

        db.collection("studentAttendsSubject")
            .whereEqualTo("studentEmail", studentEmail)
            .get()
            .addOnSuccessListener { result ->
                val subjects = ArrayList<Subject>()
                for (data in result.documents) {
                    val attend = data.toObject(StudentAttendsSubject::class.java)
                    db.collection("subjects")
                        .whereEqualTo("name", attend?.subjectName)
                        .get()
                        .addOnSuccessListener { result1 ->
                            for (data1 in result1.documents) {
                                val subject = data1.toObject(Subject::class.java)
                                subjects.add(subject!!)
                                }

                            recyclerAdapter = StudentSubjectRecyclerAdapter(subjects, this@StudentHomeFragment)
                            recyclerView.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = recyclerAdapter
                            }
                            }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("StudentHomeFragment", "Error getting attends.", exception)
            }

        logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainerView, loginFragment)
            fragmentTransaction?.commit()

        }

        return view
    }

    override fun onItemButtonClick(subject: Subject) {
        val subjectFragment = StudentSubjectFragment()
        val studentEmail = arguments?.getString("STUDENT_EMAIL").toString()
        val bundle = Bundle()
        bundle.putString("STUDENT_EMAIL", studentEmail)
        bundle.putString("SUBJECT_NAME", subject.name)
        subjectFragment.arguments = bundle

        val fragmentTransaction: FragmentTransaction? =
            activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(
            R.id.fragmentContainerView,
            subjectFragment)
        fragmentTransaction?.commit()
    }
}