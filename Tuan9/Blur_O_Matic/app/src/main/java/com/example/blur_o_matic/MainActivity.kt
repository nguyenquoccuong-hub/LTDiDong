package com.example.blur_o_matic

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.*
import com.example.blur_o_matic.ui.theme.Blur_O_MaticTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Blur_O_MaticTheme {
                BlurScreen()
            }
        }
    }
}

@Composable
fun BlurScreen() {
    val context = LocalContext.current
    var blurLevel by remember { mutableStateOf(0) }
    var isWorking by remember { mutableStateOf(false) }
    var resultUriString by remember { mutableStateOf<String?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val blurOptions = listOf("Mờ nhẹ", "Mờ hơn", "Mờ nhiều nhất")
    val workManager = WorkManager.getInstance(context)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    selectedImageBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                }
                // Reset result when new image is picked
                resultUriString = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ứng dụng Làm Mờ Ảnh",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        Button(onClick = {
            imagePickerLauncher.launch("image/*")
        }) {
            Text("Chọn Ảnh từ Máy")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedImageBitmap != null) {
            Image(
                bitmap = selectedImageBitmap!!.asImageBitmap(),
                contentDescription = "Ảnh đã chọn",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            )
        } else {
            Text("Chưa chọn ảnh", modifier = Modifier.padding(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedImageBitmap != null) {
            Text("Chọn Mức Độ Mờ:")
            Spacer(modifier = Modifier.height(8.dp))
            blurOptions.forEachIndexed { idx, text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = blurLevel == idx,
                            onClick = { blurLevel = idx }
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = blurLevel == idx,
                        onClick = { blurLevel = idx }
                    )
                    Text(text)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    selectedImageUri?.let { uri ->
                        isWorking = true
                        resultUriString = null
                        try {
                            val imageFile = File(context.cacheDir, "original_${System.currentTimeMillis()}.png")
                            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                imageFile.outputStream().use { outputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }

                            val cleanup = OneTimeWorkRequestBuilder<CleanupWorker>().build()
                            val blur = OneTimeWorkRequestBuilder<BlurWorker>()
                                .setInputData(workDataOf(
                                    BlurWorker.KEY_IMAGE_URI to imageFile.absolutePath,
                                    BlurWorker.KEY_BLUR_LEVEL to (blurLevel + 1)
                                ))
                                .build()
                            val save = OneTimeWorkRequestBuilder<SaveImageToFileWorker>().build()

                            workManager.beginWith(cleanup)
                                .then(blur)
                                .then(save)
                                .enqueue()

                            workManager.getWorkInfoByIdLiveData(save.id).observeForever { info ->
                                if (info != null && info.state.isFinished) {
                                    isWorking = false
                                    if (info.state == WorkInfo.State.SUCCEEDED) {
                                        resultUriString = info.outputData.getString(SaveImageToFileWorker.KEY_IMAGE_URI)
                                        Toast.makeText(context, "Đã lưu ảnh vào bộ sưu tập!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            isWorking = false
                            e.printStackTrace()
                        }
                    }
                },
                enabled = !isWorking && selectedImageBitmap != null
            ) {
                if (isWorking) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Bắt Đầu Làm Mờ")
                }
            }

            if (isWorking) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Đang xử lý mờ ảnh... Vui lòng chờ.")
            }

            resultUriString?.let { uriString ->
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.parse(uriString), "image/png")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Không tìm thấy ứng dụng xem ảnh", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }) {
                    Text("Xem Ảnh Kết Quả")
                }
            }
        }
    }
}
