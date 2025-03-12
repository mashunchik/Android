package com.example.lab2

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ResultFragment : Fragment() {

    companion object {
        private const val ARG_TEXT = "arg_text"
        private const val ARG_COLOR = "arg_color"

        fun newInstance(text: String, color: Int): ResultFragment {
            val fragment = ResultFragment()
            val args = Bundle()
            args.putString(ARG_TEXT, text)
            args.putInt(ARG_COLOR, color)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var resultText: TextView
    private lateinit var cancelButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultText = view.findViewById(R.id.resultText)
        cancelButton = view.findViewById(R.id.cancelButton)

        val text = arguments?.getString(ARG_TEXT) ?: ""
        val color = arguments?.getInt(ARG_COLOR) ?: Color.BLACK

        resultText.text = text
        resultText.setTextColor(color)

        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()  // Повертає назад до InputFragment
        }
    }
}