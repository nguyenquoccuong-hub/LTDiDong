package com.example.businesscardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.businesscardapp.ui.theme.BusinessCardAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BusinessCardAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BusinessCard(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BusinessCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFFD2E8D4))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Android Logo
        Column(
            modifier = Modifier
                .size(120.dp)
                .background(color = Color(0xFF073042)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Android Logo",
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF3DDC84)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = "Nguyễn Quốc Cường",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFF000000)
        )

        // Title
        Text(
            text = "Cường Đô La Developer",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D6A4F)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Contact Info Section
        ContactRow(
            icon = Icons.Rounded.Call,
            text = "+84 705428529"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ContactRow(
            icon = Icons.Rounded.Share,
            text = "@CuongDoLaDeveloper"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ContactRow(
            icon = Icons.Rounded.Email,
            text = "cuongnq.24itb@vku.udn.vn"
        )
    }
}

@Composable
fun ContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,  // ← Sửa dòng này
    text: String,
    modifier: Modifier = Modifier
)
 {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 16.dp),
            tint = Color(0xFF2D6A4F)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF000000)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BusinessCardPreview() {
    BusinessCardAppTheme {
        BusinessCard()
    }
}
