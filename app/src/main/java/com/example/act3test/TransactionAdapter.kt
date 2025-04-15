package com.example.personalfinancetracker

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onShowDeleteDialog: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardContainer: LinearLayout = itemView.findViewById(R.id.cardContainer)
        val borderView: View = itemView.findViewById(R.id.borderView)
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val amountText: TextView = itemView.findViewById(R.id.amountText)
        val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.titleText.text = transaction.title ?: "No Title"
        // Format amount with "LKR" and two decimal places
        holder.amountText.text = "LKR ${String.format("%.2f", transaction.amount ?: 0.0)}"
        holder.categoryText.text = transaction.category ?: "No Category"
        holder.dateText.text = transaction.date ?: "No Date"

        val borderColor = if (transaction.type == "expense") {
            ContextCompat.getColor(holder.itemView.context, R.color.expense_border)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.income_border)
        }
        val cardDrawable = GradientDrawable().apply {
            setColor(ContextCompat.getColor(holder.itemView.context, android.R.color.transparent))
            setStroke(3, borderColor)
            cornerRadius = 15f
        }
        holder.cardContainer.background = cardDrawable
        (holder.itemView as? androidx.cardview.widget.CardView)?.background = null // Explicitly clear CardView background

        // Add category highlight background
        val categoryBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 30f // 30dp radius
            setPadding(10, 5, 10, 5) // Optional padding for better text spacing
        }
        when (transaction.category) {
            "Food" -> categoryBackground.setColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight_food))
            "Transport" -> categoryBackground.setColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight_transport))
            "Bills" -> categoryBackground.setColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight_bills))
            "Entertainment" -> categoryBackground.setColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight_entertainment))
            else -> categoryBackground.setColor(ContextCompat.getColor(holder.itemView.context, R.color.grey_600))
        }
        holder.categoryText.background = categoryBackground

        holder.deleteBtn.setOnClickListener {
            onShowDeleteDialog(transaction)
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}