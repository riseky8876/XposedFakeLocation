package com.noobexon.xposedfakelocation.manager.ui.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.noobexon.xposedfakelocation.R
import com.noobexon.xposedfakelocation.data.model.FavoriteLocation
import com.noobexon.xposedfakelocation.manager.ui.map.MapViewModel
import com.noobexon.xposedfakelocation.manager.ui.navigation.BottomNavBar
import com.noobexon.xposedfakelocation.manager.ui.navigation.Screen
import org.osmdroid.util.GeoPoint

// Liquid glass colors
private val GlassWhite = Color(0x33FFFFFF)
private val GlassBorder = Color(0x55FFFFFF)
private val GlassText = Color.White
private val GlassSubText = Color(0xCCFFFFFF)

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x55FFFFFF),
                        Color(0x22FFFFFF)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x99FFFFFF),
                        Color(0x33FFFFFF)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        content()
    }
}

@Composable
fun FavoritesScreen(
    navController: NavController,
    mapViewModel: MapViewModel,
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val favorites by favoritesViewModel.favorites.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image fullscreen
        Image(
            painter = painterResource(id = R.drawable.app_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x44000000))
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Favorites",
                color = GlassText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            // Import / Export buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Import button - liquid glass style
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0x66FFFFFF), Color(0x33FFFFFF))
                            )
                        )
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                        .clickable { /* TODO: import */ },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = GlassText, modifier = Modifier.size(18.dp))
                        Text("Import", color = GlassText, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Export button - liquid glass style
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0x44FFFFFF), Color(0x22FFFFFF))
                            )
                        )
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                        .clickable { /* TODO: export */ },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = GlassText, modifier = Modifier.size(18.dp))
                        Text("Export", color = GlassText, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Favorites list or empty state
            if (favorites.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LiquidGlassCard(modifier = Modifier.padding(32.dp)) {
                        Text(
                            "No favorite locations.",
                            color = GlassSubText,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(favorites) { favorite ->
                        FavoriteItem(
                            favorite = favorite,
                            onClick = {
                                mapViewModel.updateClickedLocation(
                                    GeoPoint(favorite.latitude, favorite.longitude)
                                )
                                navController.navigate(Screen.Map.route) {
                                    popUpTo(Screen.Map.route) { inclusive = true }
                                }
                            },
                            onDelete = { favoritesViewModel.removeFavorite(favorite) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Bottom Navigation Bar - liquid glass style
        BottomNavBar(
            navController = navController,
            currentRoute = Screen.Favorites.route,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun FavoriteItem(
    favorite: FavoriteLocation,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = favorite.name,
                    color = GlassText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Lat: ${favorite.latitude}, Lon: ${favorite.longitude}",
                    color = GlassSubText,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFFF6B6B)
                )
            }
        }
    }
}
