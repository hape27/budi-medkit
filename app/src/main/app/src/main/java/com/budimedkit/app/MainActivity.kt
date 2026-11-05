
package com.budimedkit.app

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

data class Transaction(
    val id: Long,
    val title: String,
    val amount: Long,
    val type: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudiMedkitApp(this)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudiMedkitApp(context: Context) {
    val transactions = remember {
        mutableStateListOf<Transaction>().apply {
            addAll(loadTransactions(context))
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("PEMASUKAN") }

    val income = transactions
        .filter { it.type == "PEMASUKAN" }
        .sumOf { it.amount }

    val expense = transactions
        .filter { it.type == "PENGELUARAN" }
        .sumOf { it.amount }

    val balance = income - expense

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFFB71C1C),
            secondary = Color(0xFF263238),
            background = Color(0xFFF4F5F7)
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "BUDI MEDKIT",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "BUDI JAYA MOTOR",
                                fontSize = 12.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFB71C1C),
                        titleContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = Color(0xFFB71C1C),
                    contentColor = Color.White
                ) {
                    Text("+", fontSize = 28.sp)
                }
            },
            containerColor = Color(0xFFF4F5F7)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        "Dashboard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF263238)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                "TOTAL SALDO",
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                rupiah(balance),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "BUDI JAYA MOTOR",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SummaryCard(
                            title = "Pemasukan",
                            amount = income,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "Pengeluaran",
                            amount = expense,
                            color = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        "Riwayat Transaksi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (transactions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Belum ada transaksi. Tekan tombol + untuk menambahkan.",
                                modifier = Modifier.padding(20.dp),
                                color = Color.Gray
                            )
                        }
                    }
                }

                items(
                    transactions.sortedByDescending { it.id },
                    key = { it.id }
                ) { transaction ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    transaction.title,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    transaction.type,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Text(
                                (if (transaction.type == "PEMASUKAN") "+ " else "- ") +
                                    rupiah(transaction.amount),
                                color = if (transaction.type == "PEMASUKAN")
                                    Color(0xFF2E7D32)
                                else Color(0xFFC62828),
                                fontWeight = FontWeight.Bold
                            )

                            TextButton(
                                onClick = {
                                    transactions.remove(transaction)
                                    saveTransactions(context, transactions)
                                }
                            ) {
                                Text("Hapus", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AddTransactionDialog(
                selectedType = selectedType,
                onTypeChange = { selectedType = it },
                onDismiss = { showDialog = false },
                onSave = { title, amount ->
                    transactions.add(
                        Transaction(
                            id = System.currentTimeMillis(),
                            title = title,
                            amount = amount,
                            type = selectedType
                        )
                    )
                    saveTransactions(context, transactions)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Long,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                rupiah(amount),
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun AddTransactionDialog(
    selectedType: String,
    onTypeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (String, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Transaksi") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == "PEMASUKAN",
                        onClick = { onTypeChange("PEMASUKAN") },
                        label = { Text("Pemasukan") }
                    )
                    FilterChip(
                        selected = selectedType == "PENGELUARAN",
                        onClick = { onTypeChange("PENGELUARAN") },
                        label = { Text("Pengeluaran") }
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Keterangan transaksi") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it.filter { char -> char.isDigit() }
                    },
                    label = { Text("Nominal (Rupiah)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = amount.toLongOrNull() ?: 0L
                    if (title.isNotBlank() && value > 0) {
                        onSave(title.trim(), value)
                    }
                },
                enabled = title.isNotBlank() &&
                    (amount.toLongOrNull() ?: 0L) > 0
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

fun rupiah(value: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    )
    formatter.maximumFractionDigits = 0
    formatter.minimumFractionDigits = 0
    return formatter.format(value)
}

fun loadTransactions(context: Context): List<Transaction> {
    return try {
        val preferences = context.getSharedPreferences(
            "budi_medkit_data",
            Context.MODE_PRIVATE
        )
        val json = preferences.getString("transactions", "[]") ?: "[]"
        val array = JSONArray(json)

        (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            Transaction(
                id = item.getLong("id"),
                title = item.getString("title"),
                amount = item.getLong("amount"),
                type = item.getString("type")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun saveTransactions(
    context: Context,
    transactions: List<Transaction>
) {
    val array = JSONArray()

    transactions.forEach { transaction ->
        val item = JSONObject()
        item.put("id", transaction.id)
        item.put("title", transaction.title)
        item.put("amount", transaction.amount)
        item.put("type", transaction.type)
        array.put(item)
    }

    context.getSharedPreferences(
        "budi_medkit_data",
        Context.MODE_PRIVATE
    ).edit()
        .putString("transactions", array.toString())
        .apply()
}
