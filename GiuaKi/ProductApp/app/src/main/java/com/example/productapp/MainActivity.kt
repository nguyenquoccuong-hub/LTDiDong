package com.example.productapp

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productapp.login.LoginActivity
import com.example.productapp.model.Product
import com.example.productapp.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductAdminScreen(
                onLogout = {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAdminScreen(onLogout: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var encodedImage by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    var currentProductId by remember { mutableStateOf<String?>(null) }
    
    var productList by remember { mutableStateOf(listOf<Product>()) }
    var filteredList by remember { mutableStateOf(listOf<Product>()) }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.collection("products").addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                val list = it.documents.mapNotNull { doc ->
                    val p = doc.toObject(Product::class.java)
                    p?.id = doc.id
                    p
                }
                productList = list
                if (!isSearching) filteredList = list
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            encodedImage = ImageUtils.encodeImage(bitmap)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý sản phẩm", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.LightGray)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (encodedImage.isNotEmpty()) {
                        ImageUtils.decodeImage(encodedImage)?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Text("Chọn ảnh", fontSize = 12.sp)
                    }
                }
                
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.padding(start = 16.dp).weight(1f)
                ) {
                    Text("CHỌN ẢNH")
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên sản phẩm") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Loại sản phẩm") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Giá sản phẩm") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val p = price.toLongOrNull()
                    if (name.isEmpty() || type.isEmpty() || p == null || p < 0) {
                        Toast.makeText(context, "Vui lòng nhập đúng thông tin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    val productData = hashMapOf(
                        "name" to name,
                        "type" to type,
                        "price" to p,
                        "image" to encodedImage
                    )

                    if (isEditMode && currentProductId != null) {
                        db.collection("products").document(currentProductId!!).update(productData as Map<String, Any>)
                        isEditMode = false
                        currentProductId = null
                    } else {
                        db.collection("products").add(productData)
                    }
                    name = ""; type = ""; price = ""; encodedImage = ""
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(if (isEditMode) "CẬP NHẬT SẢN PHẨM" else "THÊM SẢN PHẨM")
            }

            Button(
                onClick = {
                    isSearching = true
                    filteredList = productList.filter {
                        it.name.contains(name, ignoreCase = true) &&
                        it.type.contains(type, ignoreCase = true) &&
                        (price.isEmpty() || it.price.toString() == price)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("TÌM KIẾM SẢN PHẨM")
            }

            if (isSearching) {
                TextButton(onClick = { 
                    isSearching = false
                    filteredList = productList
                    name = ""; type = ""; price = ""
                }) {
                    Text("Hiện tất cả")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filteredList) { product ->
                    ProductItem(
                        product = product,
                        onEdit = {
                            name = it.name
                            type = it.type
                            price = it.price.toString()
                            encodedImage = it.image
                            isEditMode = true
                            currentProductId = it.id
                        },
                        onDelete = { db.collection("products").document(it).delete() }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onEdit: (Product) -> Unit, onDelete: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (product.image.isNotEmpty()) {
                ImageUtils.decodeImage(product.image)?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Box(Modifier.size(70.dp).background(Color.Gray))
            }

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text("Loại: ${product.type}")
                Text("Giá: ${product.price}", color = Color.Blue)
            }

            IconButton(onClick = { onEdit(product) }) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Magenta)
            }
            IconButton(onClick = { onDelete(product.id) }) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}
