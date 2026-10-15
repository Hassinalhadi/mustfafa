package io.github.zensu357.camswap.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.zensu357.camswap.ActivationActivity
import io.github.zensu357.camswap.LicenseManager
import io.github.zensu357.camswap.R
import java.net.URLEncoder

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaManagerViewModel,
    onPermissionRequest: () -> Unit
) {
    val mainUiState by mainViewModel.uiState.collectAsState()
    val mediaUiState by mediaViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val hasMedia = mediaUiState.videos.isNotEmpty()
    val isSelected = !mediaUiState.selectedVideoName.isNullOrEmpty()
    val isWorking = mainUiState.hasPermission && hasMedia && isSelected && !mainUiState.isModuleDisabled

    val displayMediaName: String? = run {
        val selectedItem = mediaUiState.videos.find { it.name == mediaUiState.selectedVideoName }
        val raw = selectedItem?.displayName ?: mediaUiState.selectedVideoName
        if (raw == "Cam.mp4") mainUiState.originalVideoName ?: raw else raw
    }

    val selectedAudioItem = mediaUiState.audios.find { it.name == mediaUiState.selectedAudioName }
    val displayAudioName = selectedAudioItem?.displayName ?: mediaUiState.selectedAudioName

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        StatusCard(
            isXposedActive = mainUiState.isXposedActive,
            hasPermission = mainUiState.hasPermission,
            hasMedia = hasMedia,
            isSelected = isSelected,
            isWorking = isWorking,
            isRandomPlay = mainUiState.enableRandomPlay,
            mediaSourceName = displayMediaName,
            enableMicHook = mainUiState.enableMicHook,
            micHookMode = mainUiState.micHookMode,
            selectedAudioName = displayAudioName,
            playVideoSound = mainUiState.playVideoSound,
            notificationControlEnabled = mainUiState.notificationControlEnabled,
            onPermissionRequest = onPermissionRequest
        )

        VersionCard(
            currentVersion = io.github.zensu357.camswap.BuildConfig.VERSION_NAME
        )

        SupportCard()
    }
}

/**
 * Mustafa Rider Version & License Status Card (No external repository links)
 */
@Composable
fun VersionCard(
    currentVersion: String
) {
    val context = LocalContext.current
    val licInfo = LicenseManager.checkSavedLicense(context)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable {
                context.startActivity(Intent(context, ActivationActivity::class.java))
            },
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.version_current),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = currentVersion,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.license_status_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = if (licInfo.isValid) "${licInfo.tier} • ${licInfo.expiresAt}" else stringResource(R.string.version_check_github),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.developer_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = "Mustafa",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatusCard(
    isXposedActive: Boolean,
    hasPermission: Boolean,
    hasMedia: Boolean,
    isSelected: Boolean,
    isWorking: Boolean,
    isRandomPlay: Boolean,
    mediaSourceName: String?,
    enableMicHook: Boolean,
    micHookMode: String,
    selectedAudioName: String?,
    playVideoSound: Boolean,
    notificationControlEnabled: Boolean,
    isImageMode: Boolean = false,
    onPermissionRequest: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val noMediaStr = stringResource(R.string.status_no_video)
    val noSelectionStr = stringResource(R.string.status_no_selection)
    val noMediaHint = stringResource(R.string.status_add_video_hint)
    val noSelectionHint = stringResource(R.string.status_select_video_hint)
    val (backgroundColor, textColor, statusText, statusIcon) = when {
        !isXposedActive -> Quadruple(
            colorScheme.errorContainer,
            colorScheme.onErrorContainer,
            stringResource(R.string.status_module_inactive),
            Icons.Default.Error
        )
        !hasPermission -> Quadruple(
            colorScheme.errorContainer,
            colorScheme.onErrorContainer,
            stringResource(R.string.status_no_permission),
            Icons.Default.Error
        )
        !hasMedia -> Quadruple(
            colorScheme.tertiaryContainer,
            colorScheme.onTertiaryContainer,
            noMediaStr,
            Icons.Default.Warning
        )
        !isSelected -> Quadruple(
            colorScheme.tertiaryContainer,
            colorScheme.onTertiaryContainer,
            noSelectionStr,
            Icons.Default.Warning
        )
        isWorking -> Quadruple(
            colorScheme.primaryContainer,
            colorScheme.onPrimaryContainer,
            stringResource(R.string.status_working),
            Icons.Default.CheckCircle
        )
        else -> Quadruple(
            colorScheme.surfaceVariant,
            colorScheme.onSurfaceVariant,
            stringResource(R.string.status_paused),
            Icons.Default.Pause
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 16.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isXposedActive) {
                Text(
                    text = stringResource(R.string.status_activate_hint),
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.8f)
                )
            } else if (!hasPermission) {
                Button(
                    onClick = onPermissionRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.error,
                        contentColor = colorScheme.onError
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(stringResource(R.string.status_grant_permission), fontSize = 14.sp)
                }
            } else if (!hasMedia) {
                Text(
                    text = noMediaHint,
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.8f)
                )
            } else if (!isSelected) {
                Text(
                    text = noSelectionHint,
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.8f)
                )
            } else {
                @Suppress("DEPRECATION")
                Divider(
                    color = textColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                StatusRow(
                    icon = Icons.Default.Videocam,
                    label = stringResource(R.string.status_label_video),
                    value = if (isRandomPlay) stringResource(R.string.status_value_random)
                            else (mediaSourceName ?: "—"),
                    valueIcon = if (isRandomPlay) Icons.Default.Shuffle else null,
                    tint = textColor
                )

                StatusRow(
                    icon = Icons.Default.PlayArrow,
                    label = stringResource(R.string.status_label_play_mode),
                    value = if (isRandomPlay) stringResource(R.string.status_value_random_play)
                            else stringResource(R.string.status_value_sequential_play),
                    tint = textColor
                )

                val micStatusValue = if (!enableMicHook) {
                    stringResource(R.string.status_value_off)
                } else {
                    when (micHookMode) {
                        "mute" -> stringResource(R.string.status_value_mic_mute)
                        "replace" -> {
                            val audioName = selectedAudioName ?: "Mic.mp3"
                            stringResource(R.string.status_value_mic_replace) + " ($audioName)"
                        }
                        "video_sync" -> stringResource(R.string.status_value_mic_sync)
                        else -> stringResource(R.string.status_value_off)
                    }
                }
                StatusRow(
                    icon = if (enableMicHook) Icons.Default.Mic else Icons.Default.MicOff,
                    label = stringResource(R.string.status_label_mic),
                    value = micStatusValue,
                    tint = textColor
                )

                StatusRow(
                    icon = if (playVideoSound) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    label = stringResource(R.string.status_label_video_sound),
                    value = if (playVideoSound) stringResource(R.string.status_value_on)
                            else stringResource(R.string.status_value_off),
                    tint = textColor
                )

                StatusRow(
                    icon = if (notificationControlEnabled) Icons.Default.Notifications
                           else Icons.Default.NotificationsOff,
                    label = stringResource(R.string.status_label_notification),
                    value = if (notificationControlEnabled) stringResource(R.string.status_value_on)
                            else stringResource(R.string.status_value_off),
                    tint = textColor,
                    isLast = true
                )
            }
        }
    }
}

@Composable
private fun StatusRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueIcon: ImageVector? = null,
    tint: Color,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = tint.copy(alpha = 0.6f),
            modifier = Modifier.widthIn(min = 60.dp, max = 90.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        if (valueIcon != null) {
            Icon(
                imageVector = valueIcon,
                contentDescription = null,
                tint = tint.copy(alpha = 0.85f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = tint.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 200.dp)
        )
    }
    if (!isLast) {
        @Suppress("DEPRECATION")
        Divider(
            color = tint.copy(alpha = 0.06f),
            modifier = Modifier.padding(start = 28.dp)
        )
    }
}

/**
 * Mustafa Official Support & WhatsApp Card (+966557917803 with pre-filled "HI i need buy key")
 * Zero external repository links — exclusively Mustafa copyrights.
 */
@Composable
fun SupportCard() {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Mustafa Rider",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.support_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Direct WhatsApp Button (+966557917803 -> "HI i need buy key")
            Button(
                onClick = {
                    try {
                        val encoded = URLEncoder.encode(LicenseManager.WHATSAPP_DEFAULT_MSG, "UTF-8")
                        val url = "https://api.whatsapp.com/send?phone=${LicenseManager.WHATSAPP_NUMBER}&text=$encoded"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.support_whatsapp_buy),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Open Licensing & HWID Screen
            OutlinedButton(
                onClick = {
                    context.startActivity(Intent(context, ActivationActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.license_manage_btn),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            @Suppress("DEPRECATION")
            Divider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.copyright_mustafa),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
