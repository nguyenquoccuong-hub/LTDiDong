package com.example.calculatortipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatortipapp.ui.theme.CalculatorTipAppTheme
import java.text.NumberFormat
import java.util.Locale
import kotlin.collections.plusAssign
import kotlin.text.format

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTipAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TipCalculator(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TipCalculator(modifier: Modifier = Modifier) {
    var amount by remember { mutableStateOf("") }
    val amountValue = amount.replace(",", ".").toDoubleOrNull() ?: 0.0
    val tip = amountValue * 0.15
    val formattedTip = NumberFormat.getCurrencyInstance(Locale.US).format(tip)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp), // Thêm padding top
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp) // Khoảng cách đều giữa các thành phần
        ) {
            Text(
                text = "Calculate Tip",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.Start)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = {},
                label = { Text("Bill Amount") },
                singleLine = true,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFFEBEE),
                    focusedContainerColor = Color(0xFFFFEBEE)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Text(
                text = "Tip Amount: $formattedTip",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f)) // Đẩy phần nội dung lên trên, tạo khoảng trống phía dưới
        }

        CustomNumberPad(
            onNumberClick = { digit ->
                if (digit == "." || digit == ",") {
                    if (amount.contains(".") || amount.contains(",")) return@CustomNumberPad
                    if (amount.isEmpty()) amount = "0"
                }
                amount += digit
            },
            onDelete = {
                if (amount.isNotEmpty()) amount = amount.dropLast(1)
            },
            onDone = {
                // Xử lý khi nhấn xác nhận nếu muốn
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun CustomNumberPad(
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonRows = listOf(
        listOf("1", "2", "3", "-"),
        listOf("4", "5", "6", "\u23CE"),
        listOf("7", "8", "9", "\u232B"),
        listOf(",", "0", ".", "\u2713")
    )
    val specialButtonColors = mapOf(
        "-" to Color(0xFFE3F2FD),
        "\u23CE" to Color(0xFFE3F2FD),
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
