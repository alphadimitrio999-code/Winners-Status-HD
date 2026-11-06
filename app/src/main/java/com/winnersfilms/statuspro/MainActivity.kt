package com.winnersfilms.statuspro

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
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
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.max

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
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(Blue.copy(alpha = 0.13f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) { icon() }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, color = Muted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun SmallFeature(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier) {
    Surface(modifier = modifier, color = Panel, shape = RoundedCornerShape(15.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, tint = Blue, modifier = Modifier.size(19.dp))
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------------------------------------------------------------------
// Écran Photo
// ---------------------------------------------------------------------------

private data class PhotoInfo(
    val width: Int,
    val height: Int,
    val mime: String,
    val sizeBytes: Long,
    val preview: ImageBitmap
)

@Composable
private fun PhotoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var uri by remember { mutableStateOf<Uri?>(null) }
    var info by remember { mutableStateOf<PhotoInfo?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { picked ->
        if (picked != null) uri = picked
    }

    LaunchedEffect(uri) {
        val selected = uri ?: return@LaunchedEffect
        loading = true
        error = null
        info = null
        try {
            info = withContext(Dispatchers.IO) { loadPhotoInfo(context, selected) }
        } catch (e: OutOfMemoryError) {
            error = "Image trop lourde pour la mémoire de l'appareil."
        } catch (e: Exception) {
            error = e.message ?: "Impossible de lire cette image."
        }
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(Modifier.width(6.dp))
            Text("Photo HD", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = Ink)
        ) {
            Text(
                if (uri == null) "Choisir une photo" else "Choisir une autre photo",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        if (loading) {
            Text("Lecture de la photo...", color = Muted, fontSize = 14.sp)
        }

        error?.let {
            Text(it, color = Color(0xFFFF8A80), fontSize = 14.sp)
        }

        info?.let { photo ->
            Image(
                bitmap = photo.preview,
                contentDescription = "Aperçu de la photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.FillWidth
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = Panel),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InfoRow("Dimensions", "${photo.width} × ${photo.height} px")
                    InfoRow("Format", photo.mime.substringAfter("/").uppercase())
                    InfoRow("Poids", formatSize(photo.sizeBytes))
                }
            }
            Text(
                "Aperçu réduit pour l'affichage. Le fichier original n'est jamais modifié.",
                color = Muted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted, fontSize = 14.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "inconnu"
    val mo = bytes / (1024.0 * 1024.0)
    return if (mo >= 1.0) {
        String.format(Locale.getDefault(), "%.2f Mo", mo)
    } else {
        String.format(Locale.getDefault(), "%.0f Ko", bytes / 1024.0)
    }
}

private fun loadPhotoInfo(context: Context, uri: Uri): PhotoInfo {
    val resolver = context.contentResolver

    // 1. Dimensions et format, sans charger l'image entière
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    val rawWidth = bounds.outWidth
    val rawHeight = bounds.outHeight
    if (rawWidth <= 0 || rawHeight <= 0) {
        throw IllegalStateException("Format d'image non pris en charge.")
    }
    val mime = bounds.outMimeType ?: "image/inconnu"

    // 2. Orientation EXIF
    val orientation = resolver.openInputStream(uri)?.use { stream ->
        ExifInterface(stream).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    } ?: ExifInterface.ORIENTATION_NORMAL
    val degrees = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }

    // 3. Aperçu réduit (maximum environ 1600 px) pour économiser la mémoire
    var sample = 1
    while (max(rawWidth, rawHeight) / sample > 1600) sample *= 2
    val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sample }
    var bitmap = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, decodeOptions) }
        ?: throw IllegalStateException("Impossible de lire cette image.")

    if (degrees != 0f) {
        val matrix = Matrix().apply { postRotate(degrees) }
        bitmap = android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // 4. Poids du fichier
    var size = 0L
    resolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (index >= 0 && !cursor.isNull(index)) size = cursor.getLong(index)
        }
    }

    val swapped = degrees == 90f || degrees == 270f
    return PhotoInfo(
        width = if (swapped) rawHeight else rawWidth,
        height = if (swapped) rawWidth else rawHeight,
        mime = mime,
        sizeBytes = size,
        preview = bitmap.asImageBitmap()
    )
}
