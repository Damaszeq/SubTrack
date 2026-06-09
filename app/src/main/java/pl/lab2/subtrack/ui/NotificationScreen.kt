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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.lab2.subtrack.R
import pl.lab2.subtrack.models.NotificationItem
import pl.lab2.subtrack.models.NotificationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val notifications by notificationViewModel.notifications.collectAsState()
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
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { notificationViewModel.triggerTestNotification(context) },
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    NotificationItemRow(
                        notification = notification,
                        onRowClick = { notificationViewModel.markAsRead(notification.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItemRow(
    notification: NotificationItem,
    onRowClick: () -> Unit
) {
    // 1. Dynamiczne mapowanie tekstów na podstawie właściwości surowego modelu danych
    val titleText = when (notification.type) {
        NotificationType.PAYMENT_REMINDER -> if (notification.daysLeft == 1)
            stringResource(R.string.mock_title_payment_tomorrow)
        else
            stringResource(R.string.mock_title_payment_approaching)
        NotificationType.TRIAL_EXPIRING -> stringResource(R.string.mock_title_trial_ending)
        NotificationType.SYSTEM_ALERT -> stringResource(R.string.mock_title_price_update)
    }

    val messageText = when (notification.type) {
        NotificationType.PAYMENT_REMINDER -> stringResource(R.string.mock_msg_netflix, notification.priceTriggered ?: 0.0)
        NotificationType.TRIAL_EXPIRING -> stringResource(R.string.mock_msg_youtube)
        NotificationType.SYSTEM_ALERT -> stringResource(R.string.mock_msg_system)
    }

    // Prosta symulacja etykiet czasu (docelowo sformatowana data z system.currentTimeMillis)
    val timestampText = when {
        System.currentTimeMillis() - notification.timestamp < 60 * 60 * 1000 -> stringResource(R.string.time_today)
        System.currentTimeMillis() - notification.timestamp < 30 * 60 * 60 * 1000 -> stringResource(R.string.time_yesterday)
        else -> stringResource(R.string.time_3_days_ago)
    }

    Card(
        onClick = onRowClick, // Kliknięcie oznacza jako przeczytane
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 0.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp) // Zachowane kompaktowe wymiary
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val (icon: ImageVector, backgroundColor: Color) = when (notification.type) {
                NotificationType.PAYMENT_REMINDER -> {
                    if (notification.daysLeft == 1) {
                        Icons.Default.NotificationsActive to MaterialTheme.colorScheme.critical
                    } else {
                        Icons.Default.Notifications to MaterialTheme.colorScheme.warning
                    }
                }
                NotificationType.TRIAL_EXPIRING -> Icons.Default.Error to MaterialTheme.colorScheme.error
                NotificationType.SYSTEM_ALERT -> Icons.Default.Notifications to MaterialTheme.colorScheme.info
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (!notification.isRead) backgroundColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (!notification.isRead) {
                        if (backgroundColor == MaterialTheme.colorScheme.error) MaterialTheme.colorScheme.onError else Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

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
                        color = if (!notification.isRead) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = timestampText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
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

    // Logika wyciągania tekstów dla powiadomienia systemowego
    val titleText = context.getString(R.string.mock_title_payment_tomorrow)
    val messageText = context.getString(
        R.string.mock_msg_netflix,
        (notificationItem.priceTriggered ?: 0.0).toString()
    )

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(titleText)
        .setContentText(messageText)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    notificationManager.notify(notificationItem.id.hashCode(), builder.build())
}