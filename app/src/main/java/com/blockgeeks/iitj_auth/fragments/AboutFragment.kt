package com.blockgeeks.iitj_auth.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.blockgeeks.iitj_auth.R

class AboutFragment : Fragment() {
    private var linkedInLinkImageView: ImageView? = null
    private var instaLinkImageView: ImageView? = null
    private var githubLinkImageView: ImageView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.about_fragment, container, false)
        findAllIds(view)
        linkedInLinkImageView!!.setOnClickListener { openLinkedInId() }
        instaLinkImageView!!.setOnClickListener { openInstaId() }
        githubLinkImageView!!.setOnClickListener { openGithubId() }
        return view
    }

    private fun openGithubId() {
        val uri = Uri.parse("https://www.github.com/pratyaksh123")
        val githubIntent = Intent(Intent.ACTION_VIEW, uri)
        githubIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        startActivity(githubIntent)
    }

    private fun openInstaId() {
        val uri = Uri.parse("instagram://user?username=pratyaksh.tyagi\n")
        val facebookIntent = Intent(Intent.ACTION_VIEW, uri)
        facebookIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(facebookIntent)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/pratyaksh.tyagi/")
                )
            )
        }
    }

    private fun openLinkedInId() {
        val uri = Uri.parse("linkedin://add/%@" + "pratyaksh5676")
        val linkedInIntent = Intent(Intent.ACTION_VIEW, uri)
        linkedInIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(linkedInIntent)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.linkedin.com/in/" + "pratyaksh5676")
                )
            )
        }
    }

    private fun findAllIds(view: View) {
        linkedInLinkImageView = view.findViewById(R.id.linkedInLinkImageView)
        instaLinkImageView = view.findViewById(R.id.instaLinkImageView)
        githubLinkImageView = view.findViewById(R.id.githubLinkImageView)
    }
}