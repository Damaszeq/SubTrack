package pl.lab2.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import pl.lab2.subtrack.ui.AddSubscriptionScreen
import pl.lab2.subtrack.ui.MainScreen
import pl.lab2.subtrack.ui.NotificationsScreen
import pl.lab2.subtrack.ui.SubTrackTheme
import pl.lab2.subtrack.ui.SubscriptionDetailsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubTrackTheme {
                NotificationsScreen()
            }
        }
    }
}

/* By wrocic do MainScreen, klikając run
zamien
            SubTrackTheme {
                AddSubscriptionScreen()
            } lub inny

            na

                        SubTrackTheme {
                MainScreen()
            }
            itd
 */