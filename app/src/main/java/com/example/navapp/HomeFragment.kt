package com.example.navapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use findViewById to access the DSA card view
        val dsaCardView: CardView = view.findViewById(R.id.dsaCardView)
        val androidCardView: CardView = view.findViewById(R.id.androidCardView)
        val webDevCardView: CardView = view.findViewById(R.id.webDevCardView)
        val machineLearningCardView: CardView = view.findViewById(R.id.machineLearningCardView)
        val blockChainCardView: CardView = view.findViewById(R.id.blockChainCardView)
        val cyberCardView: CardView = view.findViewById(R.id.cyberCardView)



        // Set OnClickListener on the DSA card view
        dsaCardView.setOnClickListener {
            val intent = Intent(activity, DsaActivity::class.java)
            startActivity(intent)
        }

        androidCardView.setOnClickListener {
            val intent = Intent(activity, AndroidActivity::class.java)
            startActivity(intent)
        }

        webDevCardView.setOnClickListener {
            val intent = Intent(activity, WebDevActivity::class.java)
            startActivity(intent)
        }

        machineLearningCardView.setOnClickListener {
            val intent = Intent(activity, MachineLearningActivity::class.java)
            startActivity(intent)
        }

        blockChainCardView.setOnClickListener {
            val intent = Intent(activity, BlockChainActivity::class.java)
            startActivity(intent)
        }

        cyberCardView.setOnClickListener {
            val intent = Intent(activity, CyberSecurityActivity::class.java)
            startActivity(intent)
        }
    }

}
