package com.bit.bithub

import android.Manifest
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.*
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bit.bithub.components.UpdateBottomSheet
import com.bit.bithub.data.AppItem
import com.bit.bithub.data.UpdateViewModel
import com.bit.bithub.navigation.AppDestinations
import com.bit.bithub.screens.*
import com.bit.bithub.ui.theme.BitHubTheme
import com.bit.bithub.ui.theme.ThemeMode
import com.bit.bithub.util.isWifiConnected
import com.bit.bithub.settings.SettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id != -1L) {
                installApkFromDownloadId(id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        ContextCompat.registerReceiver(
            this,
            downloadReceiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )

        enableEdgeToEdge()
        setContent {
            val currentTheme = SettingsManager.themeMode
            val isDarkTheme = if (currentTheme == ThemeMode.SYSTEM) isSystemInDarkTheme() else currentTheme == ThemeMode.DARK

            LaunchedEffect(isDarkTheme) {
                val style = if (isDarkTheme) {
                    SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
                }
                enableEdgeToEdge(
                    statusBarStyle = style,
                    navigationBarStyle = style
                )
            }

            BitHubTheme(themeMode = currentTheme) {
                BitHubApp()
            }
        }
    }

    private fun installApkFromDownloadId(downloadId: Long) {
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(statusIdx)
            
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                val uriStringIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                val uriString = cursor.getString(uriStringIdx)
                
                if (uriString != null) {
                    val fileUri = Uri.parse(uriString)
                    val filePath = fileUri.path
                    if (filePath != null) {
                        val file = File(filePath)
                        if (file.exists()) {
                            val contentUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
                            installApkFromUri(contentUri)
                        } else {
                            // Fallback to standard URI if path extraction fails
                            val uri = downloadManager.getUriForDownloadedFile(downloadId)
                            if (uri != null) installApkFromUri(uri)
                        }
                    }
                }
            }
        }
        cursor.close()
    }

    private fun installApkFromUri(uri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = "package:$packageName".toUri()
                })
                return
            }
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        try {
            startActivity(intent)
        } catch (_: Exception) { }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(downloadReceiver)
        } catch (_: Exception) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitHubApp(
    viewModel: MainViewModel = viewModel(),
    updateViewModel: UpdateViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val stateDownloading = stringResource(R.string.state_downloading)
    
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var selectedAppId by rememberSaveable { mutableStateOf<Int?>(null) }
    var showProfileSheet by rememberSaveable { mutableStateOf(false) }
    var appToConfirmDownload by remember { mutableStateOf<AppItem?>(null) }
    
    val profileSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var pendingDownload by remember { mutableStateOf<Pair<AppItem, String>?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    DisposableEffect(lifecycleOwner) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                viewModel.refreshInstalledApps()
            }
        }
        context.registerReceiver(receiver, filter)

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshInstalledApps()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            try { context.unregisterReceiver(receiver) } catch (_: Exception) {}
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    fun vibrate() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(15)
                }
            }
        } catch (_: Exception) { }
    }

    LaunchedEffect(Unit) {
        // Initial data load
        viewModel.loadData()
        // Check for updates
        updateViewModel.checkForUpdates()

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                scope.launch {
                    delay(2000)
                    viewModel.loadData()
                    updateViewModel.checkForUpdates()
                }
            }
        }
        
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            cm.registerNetworkCallback(request, networkCallback)
        } catch (_: Exception) {}
    }

    fun installApkFromFile(file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) { }
    }

    fun startDownload(app: AppItem) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            pendingDownload = Pair(app, app.downloadUrl ?: "")
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        }
        viewModel.download(app, stateDownloading)
    }

    fun handleInstallClick(app: AppItem) {
        vibrate()
        val appId = app.id ?: return
        
        if (viewModel.downloadingProgress.containsKey(appId)) {
            viewModel.cancelDownload(appId)
            return
        }

        val apkFile = viewModel.getApkFile(app.title)
        if (apkFile.exists() && !viewModel.installedApps.containsKey(app.packageName)) {
            installApkFromFile(apkFile)
            return
        }

        if (SettingsManager.downloadWifiOnly && !isWifiConnected(context)) {
            appToConfirmDownload = app
            return
        }

        startDownload(app)
    }

    LaunchedEffect(pendingDownload) {
        val currentPending = pendingDownload
        if (currentPending != null && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            viewModel.download(currentPending.first, stateDownloading)
            pendingDownload = null
        }
    }

    if (selectedAppId != null) BackHandler { selectedAppId = null }

    Box(Modifier.fillMaxSize()) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach { dest ->
                    item(
                        icon = { Icon(dest.icon, stringResource(dest.labelRes), modifier = Modifier.size(24.dp)) },
                        label = { Text(text = stringResource(dest.labelRes), maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp) },
                        selected = dest == currentDestination && selectedAppId == null,
                        onClick = { 
                            if (currentDestination != dest || selectedAppId != null) {
                                vibrate()
                                currentDestination = dest
                                selectedAppId = null
                            }
                        }
                    )
                }
            }
        ) {
            if (viewModel.isLoading && viewModel.appsFromCloud.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val app = viewModel.appsFromCloud.find { it.id == selectedAppId }
                if (app != null) {
                    val isInstalled = viewModel.installedApps.containsKey(app.packageName)
                    val currentVersion = viewModel.installedApps[app.packageName] ?: 0
                    val isUpdate = isInstalled && app.versionNumber > currentVersion
                    val progress = viewModel.downloadingProgress[app.id]

                    AppDetailScreen(
                        app = app,
                        isFavorite = false,
                        isInstalled = isInstalled,
                        needsUpdate = isUpdate,
                        hasApk = viewModel.appsWithApk.contains(app.id),
                        isDownloading = progress != null,
                        downloadProgress = progress ?: 0f,
                        onBack = { 
                            vibrate()
                            selectedAppId = null 
                        },
                        onToggleFavorite = {},
                        onInstall = { handleInstallClick(app) }
                    )
                } else if (currentDestination == AppDestinations.HOME) {
                    HomeScreen(
                        apps = viewModel.appsFromCloud,
                        onAppClick = { appItem ->
                            vibrate()
                            selectedAppId = appItem.id
                        },
                        onSearchClick = {
                            currentDestination = AppDestinations.APPS
                        },
                        onProfileClick = {
                            vibrate()
                            showProfileSheet = true
                        }
                    )
                } else {
                    StoreScreen(
                        title = stringResource(currentDestination.labelRes),
                        apps = when (currentDestination) {
                            AppDestinations.GAMES -> viewModel.appsFromCloud.filter { it.isGame }
                            AppDestinations.APPS -> viewModel.appsFromCloud.filter { !it.isGame }
                            else -> viewModel.appsFromCloud
                        },
                        isGamesTab = currentDestination == AppDestinations.GAMES,
                        onAppClick = { appItem ->
                            vibrate()
                            selectedAppId = appItem.id
                        },
                        onInstallClick = { appItem -> handleInstallClick(appItem) },
                        installedApps = viewModel.installedApps,
                        appsWithApk = viewModel.appsWithApk.toSet(),
                        downloadingIds = viewModel.downloadingProgress,
                        onProfileClick = {
                            vibrate()
                            showProfileSheet = true
                        },
                        isRefreshing = viewModel.isLoading,
                        onRefresh = { viewModel.loadData() },
                        error = viewModel.errorMessage,
                        onRetry = { viewModel.loadData() }
                    )
                }
            }
        }
        
        if (showProfileSheet) {
            ModalBottomSheet(
                onDismissRequest = { showProfileSheet = false },
                sheetState = profileSheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                modifier = Modifier.fillMaxSize()
            ) {
                ProfileScreen(
                    currentThemeMode = SettingsManager.themeMode,
                    onThemeChange = { SettingsManager.themeMode = it },
                    downloadWifiOnly = SettingsManager.downloadWifiOnly,
                    onDownloadWifiOnlyChange = { SettingsManager.downloadWifiOnly = it },
                    installedCount = viewModel.installedApps.size,
                    onClose = { 
                        scope.launch { profileSheetState.hide() }.invokeOnCompletion {
                            if (!profileSheetState.isVisible) {
                                showProfileSheet = false
                            }
                        }
                    }
                )
            }
        }

        // Update BottomSheet
        updateViewModel.updateInfo?.let { update ->
            UpdateBottomSheet(
                updateInfo = update,
                onDismiss = { updateViewModel.dismissUpdate() },
                onUpdate = {
                    updateViewModel.startUpdate(context, update.downloadUrl, update.versionName)
                }
            )
        }

        appToConfirmDownload?.let { app ->
            AlertDialog(
                onDismissRequest = { appToConfirmDownload = null },
                title = { Text(stringResource(R.string.dialog_mobile_data_title)) },
                text = { Text(stringResource(R.string.dialog_mobile_data_desc)) },
                confirmButton = {
                    TextButton(onClick = {
                        appToConfirmDownload = null
                        startDownload(app)
                    }) {
                        Text(stringResource(R.string.btn_download))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { appToConfirmDownload = null }) {
                        Text(stringResource(R.string.btn_cancel))
                    }
                }
            )
        }
    }
}
