package com.lawmobile.presentation.ui.onBoardingCards

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.OnBoardingCardContent

class OnBoardingCardsAdapter(private val onBoardingCardContents: List<OnBoardingCardContent>) :
    RecyclerView.Adapter<OnBoardingCardsAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_item_container, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return onBoardingCardContents.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(onBoardingCardContents[position])
    }

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textDescription = view.findViewById<TextView>(R.id.textDescription)
        private val imageIcon = view.findViewById<ImageView>(R.id.imageSlideIcon)

        fun bind(onBoardingCardContent: OnBoardingCardContent) {
            val text = Html.fromHtml(onBoardingCardContent.description, Html.FROM_HTML_MODE_LEGACY)
            textDescription.text = text
            imageIcon.setImageResource(onBoardingCardContent.icon)
        }
    }
}
