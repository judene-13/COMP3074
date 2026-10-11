
package ca.gbc.comp3074.judene.labex3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MessageScreen()
            }
        }
    }
}

data class Message(
    val name: String,
    val text: String
)

@Composable
fun MessageScreen() {

    val messages = listOf(
        Message("Carla", "Hi!"),
        Message("Kim", "Hey Carla!"),
        Message("Carla", "How are you?"),
        Message("Kim", "I'm good, how about you?"),
        Message("Carla", "I'm doing good too!"),
        Message("Kim", "See you later!")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7FF))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(messages) { message ->
            MessageItem(message)
        }
    }
}

@Composable
fun MessageItem(message: Message) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {

        Surface(
            modifier = Modifier
                .size(70.dp)
                .border(2.dp, Color(0xFF008000), CircleShape),
            shape = CircleShape,
            color = Color(0xFFF3F3F3)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "●  ●",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column {

            Text(
                text = message.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF665E75)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFFF7FF),
                shadowElevation = 1.dp
            ) {
                Text(
                    text = message.text,
                    fontSize = 20.sp,
                    color = Color(0xFF29252D),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
