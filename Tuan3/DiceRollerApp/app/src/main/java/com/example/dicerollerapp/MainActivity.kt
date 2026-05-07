package com.example.dicerollerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dicerollerapp.ui.theme.DiceRollerAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceRollerAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DiceRollerApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun DiceRollerApp(modifier: Modifier = Modifier) {
    val (diceResult, setDiceResult) = remember { mutableStateOf(1) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val isRolling = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hiển thị xúc xắc với animation
        Box(
            modifier = Modifier.offset(y = offsetY.value.dp),
            contentAlignment = Alignment.Center
        ) {
            DiceFaceImage(diceResult)
        }

        // Nút Roll
        Button(
            onClick = {
                if (!isRolling.value) {
                    isRolling.value = true
                    scope.launch {
                        // Animation nhảy lên rơi xuống
                        offsetY.animateTo(
                            targetValue = -100f,
                            animationSpec = tween(durationMillis = 300)
                        )
                        offsetY.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = 300)
                        )

                        // Cập nhật kết quả sau animation
                        setDiceResult((1..6).random())
                        isRolling.value = false
                    }
                }
            },
            enabled = !isRolling.value
        ) {
            Text("Roll")
        }
    }
}

@Composable
fun DiceFaceImage(number: Int) {
    val imageRes = when (number) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Dice $number",
        modifier = Modifier.size(180.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun DiceRollerAppPreview() {
    DiceRollerAppTheme {
        DiceRollerApp()
    }
}
