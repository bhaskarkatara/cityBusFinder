import android.content.ContentValues.TAG
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.citybusfinder.R
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(navController: NavController
,photoPickerLauncher : ActivityResultLauncher<Intent>
                   ) {
    val feedbackText = remember { mutableStateOf("") }
    val database = Firebase.database
    val userRef = database.reference.child("feedback")
   val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Feedback😊") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)) {
                OutlinedTextField(
                    value = feedbackText.value,
                    onValueChange = { feedbackText.value = it },
                    label = { Text("Enter your feedback") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val feedback = feedbackText.value
                        if (feedback.isNotEmpty()) {
                            userRef.push().setValue(feedback)
                            Log.d(TAG, "FeedbackScreen: $userRef")
                            feedbackText.value = "" // Clear the feedback text after submission
                            Toast.makeText(context, "thank you for your feedback:)", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF6200EE), // Purple color
                        contentColor = Color.White // White text color
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "Submit")
                }

                IconButton(
                    onClick = {
                     val intent =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    photoPickerLauncher.launch(intent)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Icon(
                       imageVector = Icons.Default.ShoppingCart, contentDescription = "null"
                    )
                }
            }
        }
    )
}
