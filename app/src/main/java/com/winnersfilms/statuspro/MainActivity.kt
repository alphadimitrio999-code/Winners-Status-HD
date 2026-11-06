package com.winnersfilms.statuspro

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

private val Ink = Color(0xFF080D18)
private val Panel = Color(0xFF121C2D)
private val Blue = Color(0xFF28B8F2)
private val Muted = Color(0xFF9AA9BC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WinnersStatusApp() }
    }
}

@Composable
fun WinnersStatusApp() {
    var screen by rememberSaveable { mutableStateOf("home") }

    BackHandler(enabled = screen != "home") { screen = "home" }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Blue,
            background = Ink,
            surface = Panel,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF101E34), Ink, Ink)
                        )
                    )
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                if (screen == "photo") {
                    PhotoScreen(onBack = { screen = "home" })
                } else {
                    HomeScreen(onPhoto = { screen = "photo" })
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(onPhoto: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Header()
        HeroCard()
        Text(
            "Choisir un média",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MediaTile(
                modifier = Modifier.weight(1f),
                title = "Photo HD",
                subtitle = "Optimiser une image",
                icon = { Icon(Icons.Rounded.Photo, null, tint = Blue, modifier = Modifier.size(30.dp)) },
                onClick = onPhoto
            )
            MediaTile(
                modifier = Modifier.weight(1f),
                title = "Vidéo HD",
                subtitle = "Bientôt disponible",
                icon = { Icon(Icons.Rounded.Movie, null, tint = Blue, modifier = Modifier.size(30.dp)) },
                onClick = { }
            )
        }
        Text(
            "Votre espace de travail",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmallFeature(Icons.Rounded.Collections, "Mes exports", Modifier.weight(1f))
            SmallFeature(Icons.Rounded.Tune, "Réglages", Modifier.weight(1f))
        }
        Spacer(Modifier.weight(1f))
        Text(
            "Winners FILMS  •  La qualité avant tout",
            color = Muted,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Blue.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("W", color = Blue, fontSize = 25.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text("WINNERS", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text("STATUS HD", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
        }
        Spacer(Modifier.weight(1f))
        Surface(color = Color(0xFF12334A), shape = RoundedCornerShape(50)) {
            Text(
                "GRATUIT", color = Color(0xFF72D5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun HeroCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(Color(0xFF183B5C), Panel)))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("VOTRE CONTENU.\nVOTRE QUALITÉ.", color = Color.White, fontSize = 25.sp, lineHeight = 31.sp,
                fontWeight = FontWeight.ExtraBold)
            Text(
                "Préparez vos photos et vidéos pour le partage, avec un contrôle précis de la qualité.",
                color = Muted, fontSize = 13.sp, lineHeight = 19.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Surface(color = Blue, shape = RoundedCornerShape(50)) {
                    Text(
                        "OPTIMISATION LOCALE", color = Ink, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                    )
                }
                Text("Sans abonnement", color = Muted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun MediaTile(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(145.dp),
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween
