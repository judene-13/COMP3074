package ca.gbc.comp3074.judene.labex2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.gbc.comp3074.judene.labex2.ui.theme.LabEx2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabEx2Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ActionButtons(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@Composable
fun ActionButtons(
    modifier: Modifier = Modifier
) {

    val cnt = remember {
        mutableIntStateOf(0)
    }
    val step = remember {
        mutableIntStateOf(1)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = colorResource(id = R.color.app_background)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Application Logo",
                modifier = Modifier
                    .width(180.dp)
                    .height(120.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "${cnt.intValue}",
                fontSize = 40.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Button(
                onClick = {
                    cnt.intValue -= step.intValue
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        id = R.color.subtract_button
                    )
                )
            ) {
                Text(
                    text = "-",
                    fontSize = 24.sp
                )
            }

            Button(
                onClick = {
                    cnt.intValue += step.intValue
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        id = R.color.add_button
                    )
                )
            ) {
                Text(
                    text = "+",
                    fontSize = 24.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Button(
                onClick = {
                    cnt.intValue = 0
                    step.intValue = 1
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        id = R.color.reset_button
                    )
                )
            ) {
                Text("Reset")
            }

            Button(
                onClick = {
                    if (step.intValue < 10) {
                        step.intValue++
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        id = R.color.step_button
                    )
                )
            ) {
                Text("Step")
            }
        }
        Text(
            text = "Current Step: ${step.intValue}",
            fontSize = 16.sp
        )
    }
}
@Preview(showBackground = true)
@Composable
fun ActionButtonsPreview() {
    LabEx2Theme {
        ActionButtons()
    }
}