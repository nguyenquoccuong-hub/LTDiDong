package com.example.firebasecrud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.firebasecrud.ui.theme.FirebaseCRUDTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FirebaseCRUDTheme {
                FirebaseScreen()
            }
        }
    }
}

// Model
data class User(
    var id: String = "",
    var name: String = "",
    var age: Int = 0
)

@Composable
fun FirebaseScreen() {
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var docId by remember { mutableStateOf("") }

    var userList by remember { mutableStateOf(listOf<User>()) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Firebase CRUD", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = docId,
            onValueChange = { docId = it },
            label = { Text("Document ID (update/delete)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            Button(onClick = {
                if (name.isNotEmpty() && age.isNotEmpty()) {
                    val user = hashMapOf(
                        "name" to name,
                        "age" to age.toInt()
                    )
                    if (docId.isNotEmpty()) {
                        db.collection("users")
                            .document(docId)
                            .set(
                                mapOf(
                                    "name" to name,
                                    "age" to age.toInt()
                                )
                            )
                    }
                }
            }) {
                Text("Add")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (docId.isNotEmpty()) {
                    db.collection("users")
                        .document(docId)
                        .update(
                            mapOf(
                                "name" to name,
                                "age" to age.toInt()
                            )
                        )
                }
            }) {
                Text("Update")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            Button(onClick = {
                if (docId.isNotEmpty()) {
                    db.collection("users")
                        .document(docId)
                        .delete()
                }
            }) {
                Text("Delete")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->
                        val list = mutableListOf<User>()
                        for (doc in result) {
                            val user = doc.toObject(User::class.java)
                            user.id = doc.id
                            list.add(user)
                        }
                        userList = list
                    }
            }) {
                Text("Load All")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {
            items(userList) { user ->
                Text("ID: ${user.id} | ${user.name} | ${user.age}")
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}