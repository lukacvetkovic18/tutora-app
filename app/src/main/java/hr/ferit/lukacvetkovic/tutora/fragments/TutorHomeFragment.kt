package hr.ferit.lukacvetkovic.tutora.fragments

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.lukacvetkovic.tutora.R
import hr.ferit.lukacvetkovic.tutora.adapters.TutorSubjectRecyclerAdapter
import hr.ferit.lukacvetkovic.tutora.models.Subject
import hr.ferit.lukacvetkovic.tutora.models.Tutor

class TutorHomeFragment : Fragment(),
    TutorSubjectRecyclerAdapter.ContentListener {
    private val db = Firebase.firestore
    private lateinit var recyclerAdapter: TutorSubjectRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutor_home, container, false)
        val addSubjectFragment = TutorCreateSubjectFragment()
        val loginFragment = LoginFragment()
        val tutorEmail = arguments?.getString("TUTOR_EMAIL").toString()
        var tutor: Tutor?
        val title = view.findViewById<TextView>(R.id.tutorHomeTitle)
        val addSubjectBtn = view.findViewById<AppCompatImageButton>(R.id.tutorHomeAddSubject)
        val logoutBtn = view.findViewById<AppCompatImageButton>(R.id.tutorHomeLogout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.tutorHomeSubjectRecyclerView)

        db.collection("tutors")
            .whereEqualTo("email", tutorEmail)
            .get()
            .addOnSuccessListener { result ->
                for (data in result.documents) {
                    tutor = data.toObject(Tutor::class.java)
                    title.text = "Welcome " + tutor!!.firstName
                }
                db.collection("subjects")
                    .whereEqualTo("tutorEmail", tutorEmail)
                    .get()
                    .addOnSuccessListener { result1 ->
                        val subjects = ArrayList<Subject>()
                        for (data1 in result1.documents) {
                            val subject = data1.toObject(Subject::class.java)
                            if(subject != null) {
                                subjects.add(subject)
                            }
                        }
                        recyclerAdapter = TutorSubjectRecyclerAdapter(subjects, this@TutorHomeFragment)
                        recyclerView.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = recyclerAdapter
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TutorHomeFragment", "Error getting documents.", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("TutorHomeFragment", "Error getting tutor.", exception)
            }



        addSubjectBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("TUTOR_EMAIL", tutorEmail)
            addSubjectFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainerView, addSubjectFragment)
            fragmentTransaction?.commit()
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
        val subjectFragment = TutorSubjectFragment()
        val tutorEmail = arguments?.getString("TUTOR_EMAIL").toString()
        val bundle = Bundle()
        bundle.putString("TUTOR_EMAIL", tutorEmail)
        bundle.putString("SUBJECT_NAME", subject.name)
        subjectFragment.arguments = bundle

        val fragmentTransaction: FragmentTransaction? =
            activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragmentContainerView, subjectFragment)
        fragmentTransaction?.commit()
    }
}