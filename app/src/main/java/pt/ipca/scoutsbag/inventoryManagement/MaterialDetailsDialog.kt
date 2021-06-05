package pt.ipca.scoutsbag.inventoryManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.loginAndRegister.Register

class MaterialDetailsDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.dialog_material_details, container, false)

        //

        // .text =
        rootView.findViewById<TextView>(R.id.textViewNameMaterial)
        rootView.findViewById<TextView>(R.id.textViewMaterialType)
        rootView.findViewById<TextView>(R.id.editTextMaterialQuantity)

        return rootView
    }

}