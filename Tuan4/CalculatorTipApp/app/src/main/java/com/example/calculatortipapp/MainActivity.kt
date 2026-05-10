package com.example.calculatortipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatortipapp.ui.theme.CalculatorTipAppTheme
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.ceil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTipAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TipCalculatorVN(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class InputField {
    AMOUNT, PERCENT
}

@Composable
fun TipCalculatorVN(modifier: Modifier = Modifier) {
    var amount by remember { mutableStateOf("") }
    var percent by remember { mutableStateOf("") }
    var roundUp by remember { mutableStateOf(false) }
    var selectedField by remember { mutableStateOf(InputField.AMOUNT) }

    val amountValue = amount.replace(",", ".").toDoubleOrNull() ?: 0.0
    val percentValue = percent.replace(",", ".").toDoubleOrNull() ?: 0.0
    var tip = amountValue * percentValue / 100
    if (roundUp) tip = ceil(tip)
    
    // Cấu hình định dạng để hiển thị số lẻ khi không chọn làm tròn
    val formattedTip = remember(tip, roundUp) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        if (!roundUp) {
            // Đối với tiền VND, mặc định là 0 chữ số thập phân. 
            // Ta cần ghi đè để hiển thị phần lẻ nếu có.
            formatter.minimumFractionDigits = 0
            formatter.maximumFractionDigits = 2
        } else {
            // Không hiển thị phần thập phân nếu đã chọn làm tròn
            formatter.maximumFractionDigits = 0
        }
        formatter.format(tip)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Tính tiền boa",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            // Ô nhập Số tiền hóa đơn
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {},
                    label = { Text("Số tiền hóa đơn") },
                    leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    singleLine = true,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = if (selectedField == InputField.AMOUNT) Color(0xFF1976D2) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFEBEE),
                        focusedContainerColor = Color(0xFFFFEBEE),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                // Lớp phủ để bắt sự kiện click thay cho TextField
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { selectedField = InputField.AMOUNT }
                )
            }

            // Ô nhập Phần trăm boa
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = percent,
                    onValueChange = {},
                    label = { Text("Phần trăm boa") },
                    leadingIcon = { Icon(Icons.Default.Percent, contentDescription = null) },
                    singleLine = true,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = if (selectedField == InputField.PERCENT) Color(0xFF1976D2) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFEBEE),
                        focusedContainerColor = Color(0xFFFFEBEE),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                // Lớp phủ để bắt sự kiện click thay cho TextField
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { selectedField = InputField.PERCENT }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Làm tròn tiền boa?", fontSize = 16.sp)
                Spacer(Modifier.weight(1f))
                Switch(checked = roundUp, onCheckedChange = { roundUp = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tiền boa: $formattedTip",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        CustomNumberPadVN(
            onNumberClick = { digit ->
                when (selectedField) {
                    InputField.AMOUNT -> {
                        if ((digit == "." || digit == ",") && (amount.contains(".") || amount.contains(","))) return@CustomNumberPadVN
                        if ((digit == "." || digit == ",") && amount.isEmpty()) amount = "0"
                        amount += digit
                    }
                    InputField.PERCENT -> {
                        if ((digit == "." || digit == ",") && (percent.contains(".") || percent.contains(","))) return@CustomNumberPadVN
                        if ((digit == "." || digit == ",") && percent.isEmpty()) percent = "0"
                        percent += digit
                    }
                }
            },
            onDelete = {
                when (selectedField) {
                    InputField.AMOUNT -> if (amount.isNotEmpty()) amount = amount.dropLast(1)
                    InputField.PERCENT -> if (percent.isNotEmpty()) percent = percent.dropLast(1)
                }
            },
            onDone = {
                // Xử lý khi nhấn xong
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun CustomNumberPadVN(
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonRows = listOf(
        listOf("1", "2", "3", ""),
        listOf("4", "5", "6", ""),
        listOf("7", "8", "9", "\u232B"),
        listOf(",", "0", ".", "\u2713")
    )
    val specialButtonColors = mapOf(
        "\u232B" to Color(0xFFE1BEE7),
        "\u2713" to Color(0xFFE3F2FD)
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8))
            .padding(bottom = 8.dp, top = 8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        buttonRows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                row.forEach { label ->
                    if (label.isEmpty()) {
                        Spacer(modifier = Modifier.size(72.dp).padding(4.dp))
                    } else {
                        val bgColor = specialButtonColors[label] ?: Color.White
                        Button(
                            onClick = {
                                when (label) {
                                    "\u232B" -> onDelete()
                                    "\u2713" -> onDone()
                                    else -> onNumberClick(label)
                                }
                            },
                            modifier = Modifier
                                .size(72.dp)
                                .padding(4.dp),
                            shape = RoundedCornerShape(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = bgColor
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 28.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
