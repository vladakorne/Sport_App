package com.example.sportapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class InfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        val backButton = view.findViewById<ImageButton>(R.id.imageButtonBack)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}

