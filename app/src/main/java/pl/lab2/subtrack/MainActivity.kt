package pl.lab2.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import pl.lab2.subtrack.ui.AddSubscriptionScreen
import pl.lab2.subtrack.ui.MainScreen
import pl.lab2.subtrack.ui.SubTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubTrackTheme {
                AddSubscriptionScreen()
            }
        }
    }
}

/* By wrocic do MainScreen, klikając run
zamien
            SubTrackTheme {
                AddSubscriptionScreen()
            }

            na

                        SubTrackTheme {
                MainScreen()
            }
 */