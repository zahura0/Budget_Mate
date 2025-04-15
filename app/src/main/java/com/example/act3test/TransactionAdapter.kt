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
    private val onShowEdit: (Transaction) -> Unit,
    private val onShowDeleteDialog: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardContainer: LinearLayout = itemView.findViewById(R.id.cardContainer)
        val borderView: View = itemView.findViewById(R.id.borderView)
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val amountText: TextView = itemView.findViewById(R.id.amountText)
        val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val editBtn: ImageButton = itemView.findViewById(R.id.editBtn)
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
        holder.amountText.text = "LKR ${String.format("%.2f", transaction.amount ?: 0.0)}"
        holder.categoryText.text = transaction.category ?: "No Category"
        holder.dateText.text = transaction.date ?: "No Date"

        val borderColor = if (transaction.type == "expense") {
            ContextCompat.getColor(holder.itemView.context, R.color.expense_border)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.income_border)
        }
        val cardDrawable = GradientDrawable().apply {
            setColor(ContextCompat.getColor(holder.itemView.context, R.color.dark_grey)) // Matches #1E1E1E
            setStroke(3, borderColor)
            cornerRadius = 15f
        }
        holder.cardContainer.background = cardDrawable
        (holder.itemView as? androidx.cardview.widget.CardView)?.background = null

        val categoryBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 30f
            setPadding(10, 5, 10, 5)
        }
        when (transaction.category) {
            "Food" -> categoryBackground.setColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight_food))
            "Transport" -> categoryBackground.setColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight_transport))
            "Bills" -> categoryBackground.setColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight_bills))
            "Entertainment" -> categoryBackground.setColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight_entertainment))
            else -> categoryBackground.setColor(ContextCompat.getColor(holder.itemView.context, R.color.grey_600))
        }
        holder.categoryText.background = categoryBackground

        holder.editBtn.setOnClickListener {
            onShowEdit(transaction)
        }

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