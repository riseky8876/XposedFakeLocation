package com.noobexon.xposedfakelocation.manager.ui.map

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.noobexon.xposedfakelocation.data.model.FavoriteLocation
import com.noobexon.xposedfakelocation.manager.ui.map.components.AddToFavoritesDialog
import com.noobexon.xposedfakelocation.manager.ui.map.components.GoToPointDialog
import com.noobexon.xposedfakelocation.manager.ui.map.components.MapViewContainer
import com.noobexon.xposedfakelocation.manager.ui.navigation.BottomNavBar
import com.noobexon.xposedfakelocation.manager.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    navController: NavController,
    mapViewModel: MapViewModel
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    val isPlaying = uiState.isPlaying
    val isFabClickable = uiState.isFabClickable

    val showGoToPointDialog = uiState.goToPointDialogState == DialogState.Visible
    val showAddToFavoritesDialog = uiState.addToFavoritesDialogState == DialogState.Visible

    var coordInput by remember { mutableStateOf("") }

    // Parse and go to coordinate from search bar
    fun handleCoordInput() {
        val parts = coordInput.trim().split(",")
        if (parts.size == 2) {
            val lat = parts[0].trim().toDoubleOrNull()
            val lng = parts[1].trim().toDoubleOrNull()
            if (lat != null && lng != null) {
                mapViewModel.goToPoint(lat, lng)
                keyboardController?.hide()
            } else {
                Toast.makeText(context, "Invalid coordinates", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Format: lat, lng", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map as background
        MapViewContainer(mapViewModel)

        // Search bar at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter)
        ) {
            OutlinedTextField(
                value = coordInput,
                onValueChange = { coordInput = it },
                placeholder = { Text("Enter coordinates (lat, lng)") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { handleCoordInput() }),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Right side FAB buttons
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Add to Favorites button
            FloatingActionButton(
                onClick = { mapViewModel.showAddToFavoritesDialog() },
                containerColor = Color(0xFF333333),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Add to Favorites")
            }

            // Center/My Location button
            FloatingActionButton(
                onClick = { mapViewModel.triggerCenterMapEvent() },
                containerColor = Color(0xFF111111),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }

            // Play/Stop button
            FloatingActionButton(
                onClick = {
                    if (isFabClickable) {
                        val wasPlaying = isPlaying
                        mapViewModel.togglePlaying()
                        if (!wasPlaying) {
                            Toast.makeText(context, "Fake Location Set", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Unset Fake Location", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                containerColor = if (isFabClickable) Color(0xFFEEEEEE) else Color(0xFFCCCCCC),
                contentColor = if (isFabClickable) Color(0xFF333333) else Color(0xFF999999),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = if (isFabClickable) 6.dp else 0.dp
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Stop" else "Play"
                )
            }
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            BottomNavBar(navController = navController, currentRoute = Screen.Map.route)
        }

        // Active status snackbar
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFF222222),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Fake Location Is Active!",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showGoToPointDialog) {
        GoToPointDialog(
            onDismissRequest = { mapViewModel.hideGoToPointDialog() },
            onGoToPoint = { latitude, longitude ->
                mapViewModel.goToPoint(latitude, longitude)
                mapViewModel.hideGoToPointDialog()
            },
            mapViewModel = mapViewModel
        )
    }

    if (showAddToFavoritesDialog) {
        val lastClickedLocation = uiState.lastClickedLocation
        LaunchedEffect(lastClickedLocation) {
            mapViewModel.prefillCoordinatesFromMarker(
                lastClickedLocation?.latitude,
                lastClickedLocation?.longitude
            )
        }
        AddToFavoritesDialog(
            mapViewModel = mapViewModel,
            onDismissRequest = { mapViewModel.hideAddToFavoritesDialog() },
            onAddFavorite = { name, latitude, longitude ->
                val favorite = FavoriteLocation(name, latitude, longitude)
                mapViewModel.addFavoriteLocation(favorite)
                mapViewModel.hideAddToFavoritesDialog()
            }
        )
    }
}
