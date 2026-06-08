package pl.lab2.subtrack.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R
import java.util.UUID

// MODELE DANYCH
data class Subscription(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double,
    val currency: String = "PLN"
)

enum class NotificationType {
    PAYMENT_REMINDER,
    TRIAL_EXPIRING,
    SYSTEM_ALERT
}

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val titleResId: Int,       // Przejście na ID zasobów dla pełnej lokalizacji
    val messageResId: Int,     // Przejście na ID zasobów
    val formatArgs: Array<Any> = emptyArray(), // Opcjonalne argumenty do stringów (np. ceny)
    val timestampResId: Int,   // Przejście na ID zasobów
    val type: NotificationType,
    val isRead: Boolean = false,
    val subscription: Subscription? = null
)

// PRZYKŁADOWE DANE (Zlokalizowany Mock Data)
val mockNotifications = listOf(
    NotificationItem(
        titleResId = R.string.mock_title_payment_tomorrow,
        messageResId = R.string.mock_msg_netflix,
        formatArgs = arrayOf("43,99"),
        timestampResId = R.string.time_today,
        type = NotificationType.PAYMENT_REMINDER,
        subscription = Subscription(name = "Netflix", price = 43.99)
    ),
    NotificationItem(
        titleResId = R.string.mock_title_payment_approaching,
        messageResId = R.string.mock_msg_spotify,
        formatArgs = arrayOf("19,99"),
        timestampResId = R.string.time_yesterday,
        type = NotificationType.PAYMENT_REMINDER,
        subscription = Subscription(name = "Spotify", price = 19.99),
        isRead = false
    ),
    NotificationItem(
        titleResId = R.string.mock_title_trial_ending,
        messageResId = R.string.mock_msg_youtube,
        timestampResId = R.string.time_2_days_ago,
        type = NotificationType.TRIAL_EXPIRING,
        subscription = Subscription(name = "YouTube", price = 25.99),
        isRead = false
    ),
    NotificationItem(
        titleResId = R.string.mock_title_price_update,
        messageResId = R.string.mock_msg_system,
        timestampResId = R.string.time_3_days_ago,
        type = NotificationType.SYSTEM_ALERT,
        isRead = true
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBackClick: () -> Unit) {
    val notifications = mockNotifications
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.notifications_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (notifications.isNotEmpty()) {
                        showSystemNotification(context, notifications.first())
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.trigger_push_desc))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.notifications_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItemRow(notification = notification)
                }
            }
        }
    }
}

@Composable
fun NotificationItemRow(notification: NotificationItem) {
    val titleText = stringResource(id = notification.titleResId)
    val messageText = stringResource(id = notification.messageResId, *notification.formatArgs)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Logika ikon oparta na typie powiadomienia i flagach tekstowych
            val (icon: ImageVector, backgroundColor: Color) = when {
                messageText.contains("1 dzień", ignoreCase = true) ||
                        messageText.contains("1 day", ignoreCase = true) ||
                        titleText.contains("jutro", ignoreCase = true) ||
                        titleText.contains("tomorrow", ignoreCase = true) -> {
                    Icons.Default.NotificationsActive to Color(0xFFE50914)
                }
                messageText.contains("3 dni", ignoreCase = true) ||
                        messageText.contains("3 days", ignoreCase = true) -> {
                    Icons.Default.Notifications to Color(0xFFFF9800)
                }
                notification.type == NotificationType.TRIAL_EXPIRING -> {
                    Icons.Default.Error to Color(0xFFD32F2F)
                }
                else -> {
                    Icons.Default.Notifications to Color(0xFF2196F3)
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (!notification.isRead) backgroundColor else Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (!notification.isRead) Color.White else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = titleText,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(id = notification.timestampResId),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = messageText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                )
            }
        }
    }
}

fun showSystemNotification(context: Context, notificationItem: NotificationItem) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "subtrack_notifications"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            context.getString(R.string.system_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.system_channel_desc)
        }
        notificationManager.createNotificationChannel(channel)
    }

    // Wyciągamy teksty na podstawie ID zasobu przekazanego w obiekcie
    val titleText = context.getString(notificationItem.titleResId)
    val messageText = context.getString(notificationItem.messageResId, *notificationItem.formatArgs)

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(titleText)
        .setContentText(messageText)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    notificationManager.notify(notificationItem.id.hashCode(), builder.build())
}