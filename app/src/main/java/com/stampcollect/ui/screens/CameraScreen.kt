package com.stampcollect.ui.screens

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.stampcollect.R
import com.stampcollect.ui.components.camera.*
import com.stampcollect.ui.components.atomic.ConfirmationDialog
import com.stampcollect.ui.theme.*
import com.stampcollect.ui.viewmodel.CollectionViewModel
import com.stampcollect.util.DateFormat
import com.stampcollect.util.LocationHelper
import com.stampcollect.util.StampImageHelper
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(viewModel: CollectionViewModel) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    if (cameraPermissionState.status.isGranted) {
        CameraContent(viewModel = viewModel, locationPermissionState = locationPermissionState)
    } else {
        Box(modifier = Modifier.fillMaxSize().background(BgPrimary), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Text(stringResource(R.string.camera_access), style = MaterialTheme.typography.displayMedium, color = TextPrimary, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.allow_camera_message), style = MaterialTheme.typography.bodyLarge, color = TextSecondary, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { cameraPermissionState.launchPermissionRequest() },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(stringResource(R.string.allow_camera).uppercase(), color = Color.White, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraContent(viewModel: CollectionViewModel, locationPermissionState: com.google.accompanist.permissions.PermissionState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { ContextCompat.getMainExecutor(context) }

    // Data State
    val collectionsRes by viewModel.collections.collectAsState()
    val collections = collectionsRes.data() ?: emptyList()
    val selectedCollection by viewModel.selectedCollection.collectAsState()

    // UI State
    var expanded by remember { mutableStateOf(false) }
    var isFlashing by remember { mutableStateOf(false) }
    var previewPhotoUri by remember { mutableStateOf<String?>(null) }
    var stampName by remember { mutableStateOf("") }
    var stampLat by remember { mutableStateOf<Double?>(null) }
    var stampLng by remember { mutableStateOf<Double?>(null) }
    var startSaveAnimation by remember { mutableStateOf(false) }
    var showConfirmSave by remember { mutableStateOf(false) }
    var showConfirmDiscard by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var frameType by remember { mutableStateOf("Classic") }
    var frameExpanded by remember { mutableStateOf(false) }

    // Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val file = File(context.filesDir, DateFormat.RELIABLE_FS.format(System.currentTimeMillis()) + ".png")
                previewPhotoUri = StampImageHelper.processAndSaveFromUri(context, uri, file, frameType)
                stampName = ""
                scope.launch {
                    val loc = LocationHelper.getCurrentLocation(context)
                    stampLat = loc?.latitude
                    stampLng = loc?.longitude
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Animations
    val flashAlpha by animateFloatAsState(
        targetValue = if (isFlashing) 1f else 0f,
        animationSpec = tween(durationMillis = 120),
        finishedListener = { if (isFlashing) isFlashing = false }
    )
    val transX by animateFloatAsState(targetValue = if (startSaveAnimation) 400f else 0f, animationSpec = tween(500, easing = FastOutSlowInEasing))
    val transY by animateFloatAsState(targetValue = if (startSaveAnimation) 700f else 0f, animationSpec = tween(500, easing = FastOutSlowInEasing))
    val previewScale by animateFloatAsState(
        targetValue = if (startSaveAnimation) 0f else 0.85f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        finishedListener = { if (startSaveAnimation) { previewPhotoUri = null; startSaveAnimation = false } }
    )

    // --- Camera Binding ---
    Box(modifier = Modifier.fillMaxSize()) {
        val previewView = remember { PreviewView(context) }

        LaunchedEffect(lensFacing) {
            val future = ProcessCameraProvider.getInstance(context)
            val provider = future.get()
            val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.Builder().requireLensFacing(lensFacing).build(),
                    preview,
                    imageCapture
                )
            } catch (e: Exception) { Log.e("Camera", "Bind failed", e) }
        }

        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Frame Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            val frameW = w * 0.75f; val frameH = frameW * 1.3f
            val frameL = (w - frameW) / 2f; val frameT = (h - frameH) / 2f
            drawRect(color = Color.Black.copy(alpha = 0.6f), size = size)
            when (frameType) {
                "Scalloped" -> drawScallopedFrame(frameL, frameT, frameW, frameH)
                "Modern" -> drawModernFrame(frameL, frameT, frameW, frameH)
                else -> drawClassicFrame(frameL, frameT, frameW, frameH, frameW * 0.028f)
            }
        }

        // Top Controls
        CameraTopControls(
            selectedCollectionName = selectedCollection?.name,
            frameType = frameType,
            expanded = expanded,
            frameExpanded = frameExpanded,
            collections = collections,
            onCollectionExpandToggle = { expanded = true },
            onFrameExpandToggle = { frameExpanded = true },
            onCollectionSelect = { viewModel.selectCollection(it); expanded = false },
            onFrameSelect = { frameType = it; frameExpanded = false },
            onDismissCollection = { expanded = false },
            onDismissFrame = { frameExpanded = false },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Bottom Controls
        CameraActionCenter(
            onShutterClick = {
                isFlashing = true
                takePhoto(context, imageCapture, cameraExecutor, frameType) { path ->
                    previewPhotoUri = path
                    stampName = ""
                    scope.launch {
                        val loc = LocationHelper.getCurrentLocation(context)
                        stampLat = loc?.latitude
                        stampLng = loc?.longitude
                    }
                }
            },
            onFlipCamera = {
                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
            },
            onOpenGallery = {
                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 120.dp)
        )

        // Flash Effect
        if (flashAlpha > 0f) Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = flashAlpha)))

        // Photo Preview
        if (previewPhotoUri != null) {
            PhotoPreviewOverlay(
                photoUri = previewPhotoUri!!,
                stampName = stampName,
                onStampNameChange = { stampName = it },
                startSaveAnimation = startSaveAnimation,
                transX = transX,
                transY = transY,
                previewScale = previewScale,
                onDiscard = { showConfirmDiscard = true },
                onSave = { showConfirmSave = true },
                canSave = selectedCollection != null
            )
        }

        // Dialogs
        if (showConfirmSave) {
            ConfirmationDialog(
                title = stringResource(R.string.save_changes),
                message = stringResource(R.string.save_changes_confirm),
                confirmText = stringResource(R.string.confirm),
                dismissText = stringResource(R.string.cancel),
                onConfirm = {
                    selectedCollection?.let { viewModel.addStamp(it.id, previewPhotoUri!!, stampName, stampLat, stampLng); startSaveAnimation = true }
                    showConfirmSave = false
                },
                onDismiss = { showConfirmSave = false },
                confirmColor = Primary
            )
        }

        if (showConfirmDiscard) {
            ConfirmationDialog(
                title = stringResource(R.string.discard),
                message = stringResource(R.string.discard_confirm),
                confirmText = stringResource(R.string.confirm),
                dismissText = stringResource(R.string.cancel),
                onConfirm = {
                    previewPhotoUri?.let { File(it).delete() }
                    previewPhotoUri = null
                    showConfirmDiscard = false
                },
                onDismiss = { showConfirmDiscard = false }
            )
        }
    }
}

private fun takePhoto(context: Context, imageCapture: ImageCapture, executor: Executor, frameType: String, onDone: (String) -> Unit) {
    val file = File(context.filesDir, DateFormat.RELIABLE_FS.format(System.currentTimeMillis()) + ".png")
    imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            try { onDone(StampImageHelper.processAndSaveStamp(image, file, frameType)) }
            catch (e: Exception) { Log.e("Camera", "Process failed", e); image.close() }
        }
        override fun onError(exc: ImageCaptureException) { Log.e("Camera", "Capture failed", exc) }
    })
}
