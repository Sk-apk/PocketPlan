package com.pocketplan.ui.loans

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.models.PaymentScenario
import java.text.NumberFormat
import java.util.*

class PaymentScenarioAdapter(
    private val scenarios: List<PaymentScenario>
) : RecyclerView.Adapter<PaymentScenarioAdapter.ScenarioViewHolder>() {

    inner class ScenarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvScenario: CardView = itemView.findViewById(R.id.cvScenario)
        val tvScenarioName: TextView = itemView.findViewById(R.id.tvScenarioName)
        val tvMonthlyPayment: TextView = itemView.findViewById(R.id.tvMonthlyPayment)
        val tvPayoffTime: TextView = itemView.findViewById(R.id.tvPayoffTime)
        val tvTotalInterest: TextView = itemView.findViewById(R.id.tvTotalInterest)
        val tvSavings: TextView = itemView.findViewById(R.id.tvSavings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment_scenario, parent, false)
        return ScenarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        val scenario = scenarios[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

        holder.tvScenarioName.text = scenario.scenarioName
        holder.tvMonthlyPayment.text = "Payment: ${currencyFormat.format(scenario.monthlyPayment)}"

        // Format payoff time
        val years = scenario.monthsToPayoff / 12
        val months = scenario.monthsToPayoff % 12
        holder.tvPayoffTime.text = if (years > 0) {
            "Payoff: ${years}y ${months}m"
        } else {
            "Payoff: ${months} months"
        }

        holder.tvTotalInterest.text = "Interest: ${currencyFormat.format(scenario.totalInterestPaid)}"

        // Show savings compared to minimum
        if (scenario.monthlySavings > 0) {
            holder.tvSavings.visibility = View.VISIBLE
            holder.tvSavings.text = "Save ${currencyFormat.format(scenario.monthlySavings)}"
        } else {
            holder.tvSavings.visibility = View.GONE
        }

        // Color code by efficiency
        val color = when (position) {
            0 -> Color.parseColor("#F44336") // Red - Minimum (worst)
            scenarios.size - 1 -> Color.parseColor("#4CAF50") // Green - Best
            else -> Color.parseColor("#8B7BA8") // Purple - Middle
        }
        holder.cvScenario.setCardBackgroundColor(color)
    }

    override fun getItemCount(): Int = scenarios.size
}