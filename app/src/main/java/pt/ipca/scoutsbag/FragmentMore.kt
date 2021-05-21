package pt.ipca.scoutsbag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipca.scoutsbag.activityManagement.CreateActivityActivity
import pt.ipca.scoutsbag.userManagement.ColonyActivity

class FragmentMore: Fragment() {

    // Global Variables
    lateinit var row_colony: ConstraintLayout

    /*
       This function create the view
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Create the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_more, container, false)

        println("CONA01")

        // Set data
        row_colony = rootView.findViewById(R.id.row_colony)

        println("CONA02")

        return rootView
    }

    /*
        This function configures the fragment after its creation
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("CONA1")

        // Button on click events
        row_colony.setOnClickListener {
            val intent = Intent(activity, ColonyActivity::class.java)
            println("CONA2")
            startActivity(intent)
        }
    }
}