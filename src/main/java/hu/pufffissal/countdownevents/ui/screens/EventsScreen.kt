package hu.pufffissal.countdownevents.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun EventsScreen(
    onAdd: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
                containerColor = Color.White,
                contentColor = Color.Black,
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_input_add),
                    contentDescription = "Hozzáadás"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Skeleton kész. Következő: Room + UI.",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

