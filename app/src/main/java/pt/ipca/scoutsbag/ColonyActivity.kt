package pt.ipca.scoutsbag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipca.scoutsbag.activityManagement.FragmentActivity
import pt.ipca.scoutsbag.models.Activity

class ColonyActivity : AppCompatActivity() {

    // Global Variables
    lateinit var listView : ListView
    lateinit var adapter : FragmentActivity.ActivitiesAdapter
    var users : MutableList<User> = arrayListOf()

    lateinit var buttonAdd : FloatingActionButton

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Create the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_activity, container, false)

        // Set data
        buttonAdd = rootView.findViewById(R.id.buttonAddActivity)
        listView = rootView.findViewById(R.id.listViewActivities)
        adapter = ActivitiesAdapter()
        listView.adapter = adapter

        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colony)
    }
}