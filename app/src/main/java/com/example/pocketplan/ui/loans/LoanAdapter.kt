package com.pocketplan.ui.loans

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.models.Loan
import java.text.NumberFormat
import java.util.*

class LoanAdapter(
    private val loans: List<Loan>,
    private val onItemClick: (Loan) -> Unit,
    private val onDeleteClick: (Loan) -> Unit
) : RecyclerView.Adapter<LoanAdapter.LoanViewHolder>() {

    inner class LoanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvLoan: CardView = itemView.findViewById(R.id.cvLoan)
        val tvLoanIcon: TextView = itemView.findViewById(R.id.tvLoanIcon)
        val tvLoanName: TextView = itemView.findViewById(R.id.tvLoanName)
        val tvBalance: TextView = itemView.findViewById(R.id.tvBalance)
        val tvInterestRate: TextView = itemView.findViewById(R.id.tvInterestRate)
        val tvPayoffTime: TextView = itemView.findViewById(R.id.tvPayoffTime)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan, parent, false)
        return LoanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val loan = loans[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

        holder.tvLoanIcon.text = "💳"
        holder.tvLoanName.text = loan.loanName
        holder.tvBalance.text = "Balance: ${currencyFormat.format(loan.currentBalance)}"
        holder.tvInterestRate.text = "${String.format("%.2f", loan.interestRate)}% APR"

        // Format payoff time
        if (loan.monthsToPayoff == Int.MAX_VALUE) {
            holder.tvPayoffTime.text = "⚠️ Payment too low"
            holder.cvLoan.setCardBackgroundColor(Color.parseColor("#F44336")) // Red
        } else {
            val years = loan.monthsToPayoff / 12
            val months = loan.monthsToPayoff % 12
            holder.tvPayoffTime.text = if (years > 0) {
                "${years}y ${months}m to payoff"
            } else {
                "${months} months to payoff"
            }
            holder.cvLoan.setCardBackgroundColor(Color.parseColor("#8B7BA8")) // Purple
        }

        holder.cvLoan.setOnClickListener {
            onItemClick(loan)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(loan)
        }
    }

    override fun getItemCount(): Int = loans.size
}