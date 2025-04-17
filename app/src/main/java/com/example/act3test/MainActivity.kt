package com.example.personalfinancetracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.act3test.UpdateTransactionActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar
import android.app.NotificationChannel
import android.app.NotificationManager
import android.view.WindowManager
import android.widget.AdapterView

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: TransactionAdapter
    private lateinit var viewModel: FinanceViewModel
    private lateinit var yearSpinner: Spinner
    private lateinit var monthSpinner: Spinner
    private val months = listOf(
        "All",
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            sendBudgetNotification()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(FinanceViewModel::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.transactionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter(
            transactions = emptyList(),
            onShowEdit = { transaction ->
                val intent = Intent(this, UpdateTransactionActivity::class.java)
                intent.putExtra("edit_transaction", transaction)
                startActivityForResult(intent, EDIT_TRANSACTION_REQUEST)
            },
            onShowDeleteDialog = { transaction ->
                showDeleteConfirmationDialog(transaction)
            }
        )
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.addTransactionBtn).setOnClickListener {
            startActivityForResult(Intent(this, AddTransactionActivity::class.java), ADD_TRANSACTION_REQUEST)
        }

        // Initialize spinners
        yearSpinner = findViewById(R.id.yearSpinner)
        monthSpinner = findViewById(R.id.monthSpinner)
        setupSpinners()

        viewModel.transactions.observe(this) { transactions ->
            adapter.updateTransactions(transactions ?: emptyList())
            checkBudget()
        }

        createNotificationChannel()
        requestPermissions()
    }

    private fun setupSpinners() {
        // Year spinner
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = listOf("All") + (currentYear - 10..currentYear).map { it.toString() }
        val yearAdapter = ArrayAdapter(this, R.layout.spinner_item, years)
        yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        // Month spinner
        val monthAdapter = ArrayAdapter(this, R.layout.spinner_item, months)
        monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter

        // Spinner listeners
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedYear = if (years[position] == "All") null else years[position].toInt()
                updateFilter(selectedYear, getSelectedMonth())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedMonth = if (months[position] == "All") null else position - 1 // 0-based month
                updateFilter(getSelectedYear(), selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getSelectedYear(): Int? {
        val year = yearSpinner.selectedItem?.toString()
        return if (year == "All") null else year?.toInt()
    }

    private fun getSelectedMonth(): Int? {
        val month = monthSpinner.selectedItem?.toString()
        return if (month == "All") null else months.indexOf(month) - 1 // 0-based month
    }

    private fun updateFilter(year: Int?, month: Int?) {
        viewModel.setFilter(year, month)
    }

    private fun showDeleteConfirmationDialog(transaction: Transaction) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirmation, null)

        val titleText = dialogView.findViewById<TextView>(R.id.dialogTitle)
        titleText.text = "Delete ${transaction.title}?"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setDimAmount(0.5f)
        dialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val deleteButton = dialogView.findViewById<Button>(R.id.deleteButton)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        deleteButton.setOnClickListener {
            viewModel.deleteTransaction(transaction)
            dialog.dismiss()
            Toast.makeText(this, "${transaction.title} deleted", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        if (menu.javaClass.simpleName == "MenuBuilder") {
            try {
                val method = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.javaPrimitiveType)
                method.isAccessible = true
                method.invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_analysis -> {
                startActivity(Intent(this, AnalysisActivity::class.java))
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.menu_export -> {
                exportData()
                true
            }
            R.id.menu_import -> {
                importData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ADD_TRANSACTION_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    data?.getSerializableExtra("transaction")?.let { transaction ->
                        val castedTransaction = transaction as Transaction
                        viewModel.addTransaction(castedTransaction)
                        val toastMessage = if (castedTransaction.type == "expense") {
                            "Expense added successfully"
                        } else {
                            "Income added successfully"
                        }
                        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            EDIT_TRANSACTION_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    data?.getSerializableExtra("transaction")?.let { transaction ->
                        val updatedTransaction = transaction as Transaction
                        viewModel.updateTransaction(updatedTransaction)
                        Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun checkBudget() {
        val totalSpent = viewModel.transactions.value?.filter { it.type == "expense" }?.sumOf { it.amount } ?: 0.0
        val budget = viewModel.getBudget().toDouble()
        if (budget > 0) {
            if (totalSpent >= budget * 0.9) {
                Toast.makeText(this, "Warning: Nearing budget limit ($totalSpent/$budget)", Toast.LENGTH_SHORT).show()
            }
            if (totalSpent >= budget) {
                sendBudgetNotification()
            }
        }
    }

    private fun exportData() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Storage permission required to export data", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
            return
        }

        val transactions = viewModel.transactions.value ?: emptyList()
        if (transactions.isEmpty()) {
            Toast.makeText(this, "No transactions to export", Toast.LENGTH_SHORT).show()
            return
        }

        val json = Gson().toJson(transactions)
        val file = File(getExternalFilesDir(null), "finance_backup.json")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                file.writeText(json)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Data exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun importData() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Storage permission required to import data", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 102)
            return
        }

        val file = File(getExternalFilesDir(null), "finance_backup.json")
        CoroutineScope(Dispatchers.IO).launch {
            if (!file.exists()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "No backup file found", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            try {
                val json = file.readText()
                val type = object : TypeToken<List<Transaction>>() {}.type
                val transactions: List<Transaction> = Gson().fromJson(json, type)
                if (transactions.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Imported file contains no transactions", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                val currentTransactions = viewModel.transactions.value ?: emptyList()
                val newTransactions = transactions.filter { newTransaction ->
                    currentTransactions.none { it.id == newTransaction.id }
                }
                if (newTransactions.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "No new transactions to import", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                viewModel.addTransactions(newTransactions)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Data imported successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "budget_channel",
                "Budget Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for budget alerts"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun sendBudgetNotification() {
        val notification = NotificationCompat.Builder(this, "budget_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Budget Alert")
            .setContentText("You’ve exceeded your monthly budget!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            NotificationManagerCompat.from(this).notify(1, notification)
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>().apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val permissionsToRequest = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportData()
                } else {
                    Toast.makeText(this, "Storage permission denied, cannot export data", Toast.LENGTH_SHORT).show()
                }
            }
            102 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    importData()
                } else {
                    Toast.makeText(this, "Storage permission denied, cannot import data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val ADD_TRANSACTION_REQUEST = 1
        private const val EDIT_TRANSACTION_REQUEST = 2
    }
}