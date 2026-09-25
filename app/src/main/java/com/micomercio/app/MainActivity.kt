@file:OptIn(ExperimentalFoundationApi::class)
package com.micomercio.app

import android.content.Intent
import kotlin.math.roundToInt
import kotlin.math.min
import kotlin.math.max
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.gestures.transformable
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Base64
import android.media.AudioManager
import android.media.ToneGenerator
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.micomercio.app.data.*
import com.micomercio.app.scanner.BarcodeScannerScreen
import com.micomercio.app.scanner.BarcodeMassScannerScreen
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.Calendar

data class MassScanPreviewLine(val productId:Int,val description:String,val quantity:Int,val unitPrice:Double)

data class MiComercioPalette(val bg:Color,val panel:Color,val panel2:Color,val accent:Color,val secondary:Color,val highlight:Color,val muted:Color)

private val DarkPro=MiComercioPalette(Color(0xFF050608),Color(0xE60D0F12),Color(0xD91A1D22),Color(0xFFFF163D),Color(0xFFFF3B5C),Color(0xFFFFB300),Color(0xFF9FA7B3))
private val ExecutiveBlue=MiComercioPalette(Color(0xFF07111F),Color(0xD90E1B2E),Color(0xC9142640),Color(0xFF42A5F5),Color(0xFF69E7FF),Color(0xFF90CAF9),Color(0xFFA8B7C9))
private val Emerald=MiComercioPalette(Color(0xFF06130F),Color(0xD90D211A),Color(0xC9123026),Color(0xFF00C98D),Color(0xFF8CFFB0),Color(0xFF4DD0A3),Color(0xFFA7C2B5))
private val MiComercioRed=MiComercioPalette(Color(0xFF080203),Color(0xE614080B),Color(0xD9230D13),Color(0xFFFF1744),Color(0xFFFF4D6D),Color(0xFFFFB300),Color(0xFFD2AAB3))
private val Titanium=MiComercioPalette(Color(0xFF0B0D0F),Color(0xD9171A1E),Color(0xC922272D),Color(0xFFE0E0E0),Color(0xFF9E9E9E),Color(0xFFFFC107),Color(0xFFADB5BD))
private val Black=MiComercioPalette(Color(0xFF000000),Color(0xE60A0A0A),Color(0xD9161616),Color(0xFFFF3B30),Color(0xFF55FFB0),Color(0xFFFFB020),Color(0xFF9FA6B2))
private val Light=MiComercioPalette(Color(0xFFF4F7FB),Color(0xF2FFFFFF),Color(0xFFFFFFFF),Color(0xFF1976D2),Color(0xFF00A6D6),Color(0xFFFF7A00),Color(0xFF5D6875))
private val Sky=MiComercioPalette(Color(0xFFEAF9FF),Color(0xEEFFFFFF),Color(0xFFFFFFFF),Color(0xFF0288D1),Color(0xFF00BCD4),Color(0xFF1565C0),Color(0xFF557080))
private val Cloud=MiComercioPalette(Color(0xFFF3F8FF),Color(0xF7FFFFFF),Color(0xFFFFFFFF),Color(0xFF3B82F6),Color(0xFF38BDF8),Color(0xFF2563EB),Color(0xFF64748B))
private val CyberNeon=MiComercioPalette(Color(0xFF05010B),Color(0xE611071D),Color(0xD91D0D2B),Color(0xFFFF2D75),Color(0xFFB000FF),Color(0xFF00F5FF),Color(0xFFB9A7C9))
private val Aurora=MiComercioPalette(Color(0xFF03100F),Color(0xE6091B18),Color(0xD9112925),Color(0xFF00E5A0),Color(0xFF00C8FF),Color(0xFFB6FF00),Color(0xFFA8C7C0))
private val Graphite=MiComercioPalette(Color(0xFF090B0E),Color(0xE616191D),Color(0xD923282E),Color(0xFFFF6B35),Color(0xFFE0E0E0),Color(0xFFFFC107),Color(0xFFB0B7C1))
private val Quantum=MiComercioPalette(Color(0xFF080412),Color(0xE6120A24),Color(0xD91E1235),Color(0xFF8A5CFF),Color(0xFFFF4FD8),Color(0xFF48E0FF),Color(0xFFB9B0D0))
private val NeonBlack=MiComercioPalette(Color(0xFF020305),Color(0xE60A0D12),Color(0xD9151B24),Color(0xFF39FF88),Color(0xFF35D7FF),Color(0xFFFFC857),Color(0xFFC9D0D8))
private val NeonWhitePalette=MiComercioPalette(Color(0xFFF7F9FC),Color(0xF2FFFFFF),Color(0xFFFFFFFF),Color(0xFF00A8A8),Color(0xFF7B2CFF),Color(0xFFFF6B00),Color(0xFF46515E))
private val Plateado=MiComercioPalette(Color(0xFF0B0E12),Color(0xE61B2026),Color(0xD9282F36),Color(0xFFE8EDF2),Color(0xFFB8C0CA),Color(0xFFF5C451),Color(0xFF8F9AA7))
private val MonoNeon=MiComercioPalette(Color(0xFF000000),Color(0xE60A0A0D),Color(0xD9181B20),Color(0xFFF5F7FA),Color(0xFF35E7FF),Color(0xFFFF2454),Color(0xFFB9C0C9))
private val OceanGlass=MiComercioPalette(Color(0xFF06131B),Color(0xE6122630),Color(0xD91A3440),Color(0xFF25D0C6),Color(0xFF55E8FF),Color(0xFFFFC857),Color(0xFFA6BAC5))
private val MidnightGold=MiComercioPalette(Color(0xFF0A0907),Color(0xE61B1710),Color(0xD92B2417),Color(0xFFE8B84A),Color(0xFFFFD978),Color(0xFF7DE2D1),Color(0xFFB9B0A0))
private val CloudPro=MiComercioPalette(Color(0xFFF1F6FA),Color(0xF7FFFFFF),Color(0xFFFFFFFF),Color(0xFF355CFF),Color(0xFF00A6A6),Color(0xFFFF8A00),Color(0xFF536273))

private fun palette(name:String)=when(name){
    "Ocean Glass"->OceanGlass
    "Midnight Gold"->MidnightGold
    "Cloud Pro"->CloudPro
    "Ejecutivo Azul"->ExecutiveBlue
    "Esmeralda"->Emerald
    "Rojo MiComercio"->MiComercioRed
    "Titanium"->Titanium
    "Black"->Black
    "Claro"->Light
    "Celeste"->Sky
    "Cloud"->Cloud
    "Cyber Neon"->CyberNeon
    "Aurora"->Aurora
    "Graphite"->Graphite
    "Quantum"->Quantum
    "Negro Neón"->NeonBlack
    "Blanco Neón"->NeonWhitePalette
    "Plateado"->Plateado
    "Negro & Blanco Neón"->MonoNeon
    "Oscuro"->DarkPro
    else->DarkPro
}

private var ActivePalette=DarkPro
private val Bg get()=ActivePalette.bg
private val Panel get()=ActivePalette.panel
private val Red get()=ActivePalette.accent
private val Green get()=ActivePalette.secondary
private val Orange get()=ActivePalette.highlight
private val Muted get()=ActivePalette.muted

private object MiComercioSounds {
    var enabled:Boolean = true
    private var tone: ToneGenerator? = null
    private var player: MediaPlayer? = null

    private fun play(type:Int,duration:Int=70){
        try{if(tone==null)tone=ToneGenerator(AudioManager.STREAM_NOTIFICATION,80);tone?.startTone(type,duration)}catch(_:Exception){}
    }

    private fun playMp3(context: android.content.Context, resourceId: Int){
        if(!enabled) return
        try{
            player?.release()
            player = MediaPlayer.create(context.applicationContext, resourceId)
            player?.setOnCompletionListener { it.release(); if(player===it) player=null }
            player?.start()
        }catch(_:Exception){ player=null }
    }

    fun tap(requested:Boolean=true)=Unit
    fun scan(){ play(ToneGenerator.TONE_PROP_ACK,90) }
    fun error(){ play(ToneGenerator.TONE_PROP_NACK,150) }
    fun startup(context: android.content.Context){ playMp3(context, R.raw.mi_comercio_start) }
    fun income(context: android.content.Context){ playMp3(context, R.raw.mi_comercio_income) }
    fun expense(context: android.content.Context){ playMp3(context, R.raw.mi_comercio_expense) }
    fun saleRegistered(context: android.content.Context){ playMp3(context,R.raw.mi_comercio_cobrar) }
    fun payment(context: android.content.Context){ playMp3(context,R.raw.mi_comercio_cobrar) }
    fun cashOpen(context: android.content.Context){ playMp3(context,R.raw.mi_comercio_cash_open) }
    fun cashClose(context: android.content.Context){ playMp3(context,R.raw.mi_comercio_cash_close) }
}

private fun repeatBottomNotification(context: android.content.Context, text: String) {
    val handler = android.os.Handler(android.os.Looper.getMainLooper())
    repeat(6) { i ->
        handler.postDelayed({
            try { android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show() } catch (_: Exception) {}
        }, i * 650L)
    }
}

@Composable
private fun ScanActionButton(onTap: () -> Unit, onLongPress: () -> Unit, modifier: Modifier = Modifier) {
    val source = remember { MutableInteractionSource() }
    val pressed = source.collectIsPressedAsState().value
    val transition = rememberInfiniteTransition(label = "scanTrail")
    val sweep by transition.animateFloat(
        initialValue = -1.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "scanSweep"
    )
    val pulse by transition.animateFloat(
        initialValue = .55f,
        targetValue = .95f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scanPulse"
    )
    val scale by animateFloatAsState(if (pressed) 1.06f else 1f, animationSpec = tween(180), label = "scanScale")
    Box(
        modifier = modifier.scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Estela roja: dos capas de luz detrás del botón, con un barrido continuo.
        Box(
            Modifier.fillMaxSize()
                .shadow(28.dp, RoundedCornerShape(16.dp), ambientColor = MiComercioNeonRed.copy(alpha = .72f), spotColor = MiComercioNeonRed.copy(alpha = .88f))
                .background(MiComercioNeonRed.copy(alpha = .10f * pulse), RoundedCornerShape(16.dp))
        )
        Canvas(Modifier.fillMaxSize().padding(horizontal = 2.dp, vertical = 1.dp)) {
            val y = size.height * (.50f + sweep * .34f)
            drawLine(MiComercioNeonRed.copy(alpha = .68f), Offset(0f, y), Offset(size.width, y + size.height * .12f), strokeWidth = 5f)
            drawLine(Color.White.copy(alpha = .18f), Offset(size.width * .12f, y + 8f), Offset(size.width * .88f, y + 8f), strokeWidth = 1.5f)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    interactionSource = source,
                    indication = null,
                    onClick = onTap,
                    onLongClick = onLongPress
                )
                .shadow(16.dp, RoundedCornerShape(16.dp), ambientColor = MiComercioNeonRed.copy(alpha = .85f), spotColor = MiComercioNeonRed.copy(alpha = .95f))
                .background(MiComercioNeonRed.copy(alpha = .96f), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCodeScanner, null, tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("ESCANEAR", color = Color.White, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun MiComercioTheme(themeName:String, content: @Composable () -> Unit){
    val p=palette(themeName)
    ActivePalette=p
    val light=themeName=="Claro" || themeName=="Celeste" || themeName=="Cloud" || themeName=="Cloud Pro" || themeName=="Blanco Neón"
    val scheme=if(light) lightColorScheme(primary=p.accent,secondary=p.secondary,background=p.bg,surface=p.panel,onSurface=Color(0xFF15202B)) else darkColorScheme(primary=p.accent,secondary=p.secondary,background=p.bg,surface=p.panel,onSurface=Color.White)
    MaterialTheme(colorScheme=scheme,content=content)
}

private val NeonViolet = Color(0xFFB000FF)
private val NeonWhite = Color(0xFFFFFFFF)
private val NeonGreen = Color(0xFF39FF88)
private val NeonRed = Color(0xFFFF1744)
private val MiComercioNeonRed = Color(0xFFFF123F)

private fun neonModifier(modifier:Modifier, shape:androidx.compose.ui.graphics.Shape, pressed:Boolean):Modifier {
    val base = when {
        ActivePalette === MiComercioRed -> NeonRed
        ActivePalette === Emerald -> NeonGreen
        ActivePalette === Titanium -> NeonWhite
        ActivePalette === ExecutiveBlue || ActivePalette === Sky -> Color(0xFF00D9FF)
        else -> NeonViolet
    }
    val secondary = when {
        base == NeonRed -> NeonViolet
        base == NeonGreen -> NeonWhite
        base == NeonWhite -> NeonViolet
        else -> NeonGreen
    }
    return modifier
        .scale(if(pressed) .97f else 1f)
        .shadow(if(pressed) 8.dp else 18.dp, shape, ambientColor=base.copy(alpha=.72f), spotColor=base.copy(alpha=.88f))
        .shadow(if(pressed) 4.dp else 9.dp, shape, ambientColor=secondary.copy(alpha=.48f), spotColor=secondary.copy(alpha=.58f))
}

@Composable
fun FButton(enabled:Boolean=true,onClick:()->Unit,modifier:Modifier=Modifier,shape:androidx.compose.ui.graphics.Shape=RoundedCornerShape(14.dp),content:@Composable RowScope.()->Unit){
    val source=remember{MutableInteractionSource()}
    val pressed=source.collectIsPressedAsState().value
    Button(enabled=enabled,onClick={MiComercioSounds.tap(true);onClick()},modifier=neonModifier(modifier,shape,pressed),shape=shape,interactionSource=source,colors=ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.primary.copy(alpha=.82f),contentColor=Color.White,disabledContainerColor=MaterialTheme.colorScheme.surface.copy(alpha=.45f),disabledContentColor=Muted),elevation=ButtonDefaults.buttonElevation(defaultElevation=4.dp,pressedElevation=1.dp),border=BorderStroke(1.dp,MaterialTheme.colorScheme.primary.copy(alpha=.72f)),content=content)
}

@Composable
fun FOutlinedButton(enabled:Boolean=true,onClick:()->Unit,modifier:Modifier=Modifier,content:@Composable RowScope.()->Unit){
    val source=remember{MutableInteractionSource()}
    val pressed=source.collectIsPressedAsState().value
    OutlinedButton(enabled=enabled,onClick={MiComercioSounds.tap(true);onClick()},modifier=neonModifier(modifier,RoundedCornerShape(14.dp),pressed),interactionSource=source,colors=ButtonDefaults.outlinedButtonColors(containerColor=Color.Transparent,contentColor=MaterialTheme.colorScheme.secondary),border=BorderStroke(1.dp,MaterialTheme.colorScheme.secondary.copy(alpha=.82f)),content=content)
}

@Composable
fun TextFButton(enabled:Boolean=true,onClick:()->Unit,modifier:Modifier=Modifier,content:@Composable ()->Unit){
    OutlinedButton(enabled=enabled,onClick={MiComercioSounds.tap(true);onClick()},modifier=modifier,colors=ButtonDefaults.outlinedButtonColors(containerColor=Color.Transparent,contentColor=MaterialTheme.colorScheme.onSurface),border=BorderStroke(1.dp,MaterialTheme.colorScheme.primary.copy(alpha=.30f)),content={content()})
}

@Composable
fun MiComercioSplash(){
    // Inicio premium: fondo negro profundo + halos y destellos neón animados.
    // El logo sigue siendo el recurso exacto Mi Comercio ya incorporado al proyecto.
    val infinite = rememberInfiniteTransition(label = "splash_neon")
    val pulse by infinite.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val drift by infinite.animateFloat(
        initialValue = -24f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )
    val sweep by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF02040A))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Halos grandes, muy suaves, para dar profundidad sin aclarar el fondo.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF123F).copy(alpha = 0.16f * pulse),
                        Color(0xFFFF123F).copy(alpha = 0.045f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.20f + drift, h * 0.30f),
                    radius = w * 0.58f
                ),
                radius = w * 0.58f,
                center = Offset(w * 0.20f + drift, h * 0.30f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFB000FF).copy(alpha = 0.13f * pulse),
                        Color(0xFF00D9FF).copy(alpha = 0.035f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.82f - drift, h * 0.66f),
                    radius = w * 0.62f
                ),
                radius = w * 0.62f,
                center = Offset(w * 0.82f - drift, h * 0.66f)
            )

            // Líneas diagonales tipo "light trails".
            val trailX = w * sweep
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color(0xFFFF1744).copy(alpha = 0.18f), Color.Transparent)
                ),
                start = Offset(trailX - w * 0.30f, h * 0.10f),
                end = Offset(trailX + w * 0.12f, h * 0.92f),
                strokeWidth = 2.5f
            )
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color(0xFF00D9FF).copy(alpha = 0.10f), Color.Transparent)
                ),
                start = Offset(w - trailX + w * 0.10f, h * 0.12f),
                end = Offset(w - trailX - w * 0.18f, h * 0.90f),
                strokeWidth = 1.5f
            )

            // Destellos deterministas: titilan sin crear ni cargar imágenes adicionales.
            val stars = arrayOf(
                floatArrayOf(.09f,.16f,.9f), floatArrayOf(.19f,.73f,1.3f), floatArrayOf(.31f,.12f,1.7f),
                floatArrayOf(.42f,.86f,2.1f), floatArrayOf(.58f,.18f,2.6f), floatArrayOf(.69f,.79f,3.0f),
                floatArrayOf(.82f,.24f,3.4f), floatArrayOf(.92f,.60f,3.8f), floatArrayOf(.12f,.48f,4.2f),
                floatArrayOf(.76f,.46f,4.7f), floatArrayOf(.37f,.34f,5.1f), floatArrayOf(.56f,.65f,5.6f)
            )
            stars.forEach { star ->
                val phase = (sweep * 6.28318f + star[2])
                val alpha = (0.10f + ((kotlin.math.sin(phase.toDouble()) + 1.0) * 0.5f * 0.62f)).toFloat()
                val r = 1.2f + 1.8f * alpha
                val c = if (star[2].toInt() % 2 == 0) Color(0xFFFF3157) else Color(0xFF45E7FF)
                drawCircle(c.copy(alpha = alpha), r, Offset(w * star[0], h * star[1]))
                if (alpha > 0.56f) {
                    drawLine(c.copy(alpha = alpha * .55f), Offset(w * star[0] - 5f, h * star[1]), Offset(w * star[0] + 5f, h * star[1]), 1f)
                    drawLine(c.copy(alpha = alpha * .55f), Offset(w * star[0], h * star[1] - 5f), Offset(w * star[0], h * star[1] + 5f), 1f)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 42.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(com.micomercio.app.R.drawable.mi_comercio_splash_logo),
                    contentDescription = "MI COMERCIO",
                    modifier = Modifier.size(292.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(18.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(58.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "MI COMERCIO",
                    color = Color.Black.copy(alpha = .88f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.1.sp,
                    modifier = Modifier.offset(x = 2.dp, y = 4.dp)
                )
                Text(
                    "MI COMERCIO",
                    color = MiComercioNeonRed.copy(alpha = .95f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.1.sp,
                    modifier = Modifier.offset(x = 1.dp, y = 2.dp)
                )
                Text(
                    "MI COMERCIO",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.1.sp
                )
            }
            Spacer(Modifier.height(26.dp))

            // Indicador de arranque más moderno que el spinner estándar.
            Box(
                modifier = Modifier.width(190.dp).height(3.dp)
                    .background(Color(0xFF1A1E27), RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(.42f)
                        .offset(x = (190f * sweep).dp - 40.dp)
                        .background(
                            Brush.horizontalGradient(listOf(Color.Transparent, MiComercioNeonRed, Color.Transparent)),
                            RoundedCornerShape(50)
                        )
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "INICIANDO SISTEMA",
                color = Color(0xFF8E98A8).copy(alpha = .65f + .30f * pulse),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.4.sp
            )
        }
    }
}


private enum class Screen {
    HOME, SALES, PRODUCTS, CLIENTS, INVENTORY, CASH, ANALYTICS, PROMOTIONS,
    TABLES, SUPPLIERS, PURCHASES, REPORTS, USERS, STOCK_HISTORY, CASH_HISTORY,
    AUDIT, GENERAL_Z, CONFIG
}

@Composable
private fun NeonAppTitle(haloName:String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.mi_comercio_splash_logo),
            contentDescription = "Mi Comercio",
            modifier = Modifier.size(34.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(8.dp))
        Text("MI COMERCIO", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color.White)
    }
}

@Composable
private fun Field(label:String, value:String, modifier:Modifier=Modifier, onChange:(String)->Unit) {
    OutlinedTextField(
        value=value,
        onValueChange=onChange,
        label={Text(label)},
        singleLine=true,
        modifier=modifier.fillMaxWidth().padding(vertical=4.dp),
        colors=OutlinedTextFieldDefaults.colors(
            focusedBorderColor=MaterialTheme.colorScheme.primary,
            unfocusedBorderColor=MaterialTheme.colorScheme.onSurface.copy(alpha=.25f),
            focusedLabelColor=MaterialTheme.colorScheme.primary,
            cursorColor=MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun CommonProductDialog(onClose:()->Unit,onAdd:(String,Double)->Unit) {
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest=onClose,
        title={Text("Producto común")},
        text={Column { Text("Agregá un artículo sin modificar el catálogo.",color=Muted); Field("Descripción",description,onChange={description=it}); Field("Precio",price,onChange={price=it}) }},
        confirmButton={FButton(onClick={ if(description.isNotBlank()) onAdd(description.trim(),num(price)) }){Text("AGREGAR")}},
        dismissButton={TextFButton(onClick=onClose){Text("CANCELAR")}}
    )
}

@Composable
private fun BulkSaleDialog(product:Product,onClose:()->Unit,onAdd:(Double,Double)->Unit) {
    var kg by remember { mutableStateOf("") }
    val amount = num(kg) * product.salePrice
    AlertDialog(
        onDismissRequest=onClose,
        title={Text("Venta por granel")},
        text={Column { Text(product.description,fontWeight=FontWeight.Bold); Text("Precio ${money(product.salePrice)} / kg",color=Green); Field("Cantidad en kg",kg,onChange={kg=it}); Text("Total ${money(amount)}",fontWeight=FontWeight.Black,fontSize=20.sp) }},
        confirmButton={FButton(onClick={val q=num(kg); if(q>0) onAdd(q,q*product.salePrice)}){Text("AGREGAR")}},
        dismissButton={TextFButton(onClick=onClose){Text("CANCELAR")}}
    )
}

@Composable
private fun SaleDetailDialog(api:MiComercioApi,sale:Sale,onClose:()->Unit,onDone:()->Unit) {
    var returnLine by remember{mutableStateOf<SaleItem?>(null)}
    if(returnLine!=null){ReturnDialog(api,sale,returnLine!!,onClose={returnLine=null},onDone={returnLine=null;onDone()});return}
    AlertDialog(
        onDismissRequest=onClose,
        title={Text("Ticket #${sale.ticket}")},
        text={LazyColumn { item { Text("Total ${money(sale.total)}",fontSize=22.sp,fontWeight=FontWeight.Black); Text("${sale.fecha} · ${sale.medio}",color=Muted); Spacer(Modifier.height(8.dp));Text("Tocá DEVOLVER en el producto correspondiente.",color=Muted,fontSize=12.sp) }; items(sale.articulos){line -> val disponible=(line.cantidad-line.devuelta).coerceAtLeast(0.0);Card(Modifier.fillMaxWidth().padding(vertical=4.dp),colors=CardDefaults.cardColors(containerColor=Panel)){Row(Modifier.padding(10.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(line.producto,fontWeight=FontWeight.Bold);Text("Vendido ${fmt(line.cantidad)} · Devuelto ${fmt(line.devuelta)} · Disponible ${fmt(disponible)} · ${money(line.subtotal)}",fontSize=11.sp,color=Muted)};FOutlinedButton(enabled=disponible>0.0001,onClick={returnLine=line}){Text("DEVOLVER")}}} } }},
        confirmButton={TextFButton(onClick=onDone){Text("CERRAR")}}
    )
}

@Composable
private fun ReturnDialog(api:MiComercioApi,sale:Sale,line:SaleItem,onClose:()->Unit,onDone:()->Unit){
    var mode by remember{mutableStateOf(if(line.cantidad%1.0==0.0)"UNIDAD" else "PESO")};var value by remember{mutableStateOf("")};var reason by remember{mutableStateOf("")};var msg by remember{mutableStateOf("")};var busy by remember{mutableStateOf(false)};val scope=rememberCoroutineScope()
    val available=(line.cantidad-line.devuelta).coerceAtLeast(0.0)
    val requested=when(mode){
        "DINERO" -> if(line.precioUnitario>0) num(value)/line.precioUnitario else 0.0
        else -> num(value)
    }
    AlertDialog(onDismissRequest=onClose,title={Text("Devolución · ${line.producto}",fontWeight=FontWeight.Black)},text={Column(Modifier.heightIn(max=480.dp)){Text("Disponible para devolver: ${fmt(available)}",color=Green,fontWeight=FontWeight.Bold);Row(Modifier.horizontalScroll(rememberScrollState())){listOf("UNIDAD","PESO","DINERO").forEach{m->FilterChip(selected=mode==m,onClick={mode=m},label={Text(m)},modifier=Modifier.padding(end=6.dp))}};Field(if(mode=="DINERO")"Importe a devolver" else if(mode=="PESO")"Peso / cantidad" else "Unidades",value,onChange={value=it});Field("Motivo obligatorio",reason,onChange={reason=it});Text(if(mode=="DINERO")"Se calcula automáticamente la cantidad a devolver según el precio unitario ${money(line.precioUnitario)}." else "La devolución reintegra stock y queda registrada en el historial.",fontSize=11.sp,color=Muted);if(msg.isNotBlank())Text(msg,color=Red)}},confirmButton={FButton(enabled=!busy,onClick={val q=requested;if(q<=0||q>available+0.0001){msg="La cantidad/importe supera lo disponible.";return@FButton};if(mode=="UNIDAD"&&q%1.0!=0.0){msg="Para devolución por unidad usá un número entero.";return@FButton};if(reason.isBlank()){msg="Indicá el motivo.";return@FButton};busy=true;scope.launch{try{val r=api.returnItem(sale.id*1_000_000L+line.id,ReturnWrite(q,reason.trim(),mode,if(mode=="DINERO")num(value) else 0.0));if(r.isSuccessful&&r.body()?.ok==true){onDone()}else{msg="No se pudo registrar la devolución";busy=false}}catch(e:Exception){msg=e.message.orEmpty();busy=false}}}){Text("CONFIRMAR DEVOLUCIÓN")}},dismissButton={TextFButton(onClick=onClose){Text("CANCELAR")}})
}

@Composable
private fun CustomerAccountDialog(api:MiComercioApi,account:CustomerAccountResponse,onClose:()->Unit,onPaid:()->Unit,context:android.content.Context) {
    var amount by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    val scope=rememberCoroutineScope()
    AlertDialog(
        onDismissRequest=onClose,
        title={Text("Cuenta de ${account.customerName}")},
        text={Column {
            Text("Deuda actual",color=Muted)
            Text(money(account.balance),fontSize=28.sp,fontWeight=FontWeight.Black,color=if(account.balance>0)Red else Green)
            Spacer(Modifier.height(8.dp))
            Field("Importe del abono",amount){amount=it}
            account.details.takeLast(8).forEach { d -> Text("${d.dateTime} · ${d.entryType} · ${money(d.amount)}",fontSize=12.sp,color=Muted,modifier=Modifier.padding(vertical=3.dp)) }
            if(msg.isNotBlank()) Text(msg,color=Red)
        }},
        confirmButton={FButton(onClick={
            val value=num(amount)
            if(value<=0) { msg="Ingresá un importe válido"; return@FButton }
            scope.launch { try { val r=api.customerPayment(account.customerId,CustomerPayment(value)); if(r.isSuccessful){ MiComercioSounds.income(context); onPaid(); onClose() } else msg="No se pudo registrar el abono" } catch(e:Exception){msg=e.message.orEmpty()} }
        }){Text("REGISTRAR ABONO")}},
        dismissButton={TextFButton(onClick=onClose){Text("CERRAR")}}
    )
}

@Composable
fun ManagerApp(){
    val context=LocalContext.current
    val prefs=remember{Prefs(context)}
    val localApi=remember{LocalMiComercioApi(context)}
    var showSplash by remember{mutableStateOf(true)}
    var loggedIn by remember{mutableStateOf(false)}
    var themeName by remember{mutableStateOf("Ocean Glass")}
    var sounds by remember{mutableStateOf(true)}
    LaunchedEffect(Unit){
        themeName=prefs.theme().ifBlank { "Ocean Glass" }
        sounds=prefs.sounds()
        MiComercioSounds.enabled=sounds
        if(sounds) MiComercioSounds.startup(context)
        delay(1200)
        showSplash=false
    }
    if(showSplash){ MiComercioSplash(); return }
    MiComercioTheme(themeName){
        if(!loggedIn){
            LoginScreen(localApi, themeName, onLoggedIn={loggedIn=true})
        }else{
            HomeScreen(prefs,onUnpair={loggedIn=false})
        }
    }
}

@Composable
private fun LoginScreen(api:MiComercioApi, themeName:String, onLoggedIn:()->Unit){
    var username by remember{mutableStateOf("")}
    var password by remember{mutableStateOf("")}
    var msg by remember{mutableStateOf("")}
    var busy by remember{mutableStateOf(false)}
    val scope=rememberCoroutineScope()
    Box(Modifier.fillMaxSize().background(Bg).padding(24.dp),contentAlignment=Alignment.Center){
        Card(Modifier.fillMaxWidth().widthIn(max=460.dp),colors=CardDefaults.cardColors(containerColor=Panel),border=BorderStroke(1.dp,MaterialTheme.colorScheme.primary.copy(alpha=.25f)),shape=RoundedCornerShape(28.dp)){
            Column(Modifier.padding(26.dp),horizontalAlignment=Alignment.CenterHorizontally){
                NeonAppTitle("Verde Flúor")
                Text("ACCESO A MI COMERCIO",fontSize=22.sp,fontWeight=FontWeight.Black,modifier=Modifier.padding(top=10.dp))
                Text("Ingresá con tu usuario y contraseña",color=Muted,fontSize=12.sp,modifier=Modifier.padding(bottom=16.dp))
                Field("Usuario",username,onChange={username=it},modifier=Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value=password,onValueChange={password=it},label={Text("Contraseña")},singleLine=true,modifier=Modifier.fillMaxWidth(),visualTransformation=androidx.compose.ui.text.input.PasswordVisualTransformation())
                if(msg.isNotBlank()) Text(msg,color=Red,fontSize=12.sp,modifier=Modifier.padding(top=10.dp))
                FButton(enabled=!busy && username.isNotBlank() && password.isNotBlank(),onClick={scope.launch{busy=true;try{val r=api.authenticateUser(username,password);if(r.isSuccessful&&r.body()?.ok==true){onLoggedIn()}else msg="Usuario o contraseña incorrectos."}catch(e:Exception){msg=e.message.orEmpty()}finally{busy=false}}},modifier=Modifier.fillMaxWidth().height(52.dp).padding(top=12.dp)){Text(if(busy)"INGRESANDO…" else "INGRESAR")}
                Text("Usuario inicial: admin · contraseña: 123456",color=Muted,fontSize=11.sp,modifier=Modifier.padding(top=12.dp))
            }
        }
    }
}

@Composable fun Loading(){Box(Modifier.fillMaxSize().background(Bg),contentAlignment=Alignment.Center){CircularProgressIndicator(color=Red)}}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeScreen(prefs:Prefs,onUnpair:()->Unit){
    val context=LocalContext.current
    val scope=rememberCoroutineScope()
    var api by remember{mutableStateOf<MiComercioApi?>(null)}
    var screen by remember{mutableStateOf(Screen.SALES)}
    // Historial interno de navegación. La flecha Atrás del sistema siempre vuelve
    // a la pantalla anterior del Manager y nunca cierra la aplicación.
    val screenHistory = remember { mutableStateListOf<Screen>() }
    fun navigate(to: Screen) {
        if (to == screen) return
        screenHistory.add(screen)
        screen = to
    }
    var themeName by remember{mutableStateOf("Ocean Glass")}
    var sounds by remember{mutableStateOf(true)}
    var haloName by remember{mutableStateOf("Verde Flúor")}
    // El carrito de venta vive en HomeScreen para que cambiar de sección no lo borre.
    // Solo se vacía explícitamente al limpiar o completar/enviar la venta.
    var salesCart by remember{mutableStateOf<List<SaleLineWrite>>(emptyList())}
    var connection by remember{mutableStateOf("CONECTANDO…")}
    LaunchedEffect(Unit){
        api=LocalMiComercioApi(context)
        themeName=prefs.theme().ifBlank { "Ocean Glass" }
        sounds=prefs.sounds()
        haloName=prefs.halo()
        MiComercioSounds.enabled=sounds
        if(sounds){delay(180);MiComercioSounds.startup(context)}
    }
    LaunchedEffect(api){ connection="● DATOS LOCALES EN EL TELÉFONO" }
    if(api==null){Loading();return}
    // La flecha Atrás del sistema navega internamente. Nunca cierra el Manager.
    // Si existe historial, vuelve exactamente a la pantalla anterior; en la raíz
    // simplemente permanece dentro de la aplicación.
    BackHandler(enabled=true){
        if (screenHistory.isNotEmpty()) {
            screen = screenHistory.removeAt(screenHistory.lastIndex)
        }
    }
    val a=api!!
    MiComercioTheme(themeName){Scaffold(containerColor=Bg,topBar={TopAppBar(colors=TopAppBarDefaults.topAppBarColors(containerColor=Color.Transparent),title={Column{Row(verticalAlignment=Alignment.CenterVertically){NeonAppTitle(haloName)};Text(connection,fontSize=10.sp,color=if(connection.contains("CONECTADO"))Green else Orange)}},actions={IconButton(onClick={navigate(Screen.CONFIG)}){Icon(Icons.Default.Settings,"Configuración")};})},bottomBar={NavigationBar(containerColor=Panel){Nav(screen,Screen.HOME,"Inicio",Icons.Default.Dashboard){navigate(it)};Nav(screen,Screen.SALES,"Ventas",Icons.Default.PointOfSale){navigate(it)};Nav(screen,Screen.PRODUCTS,"Productos",Icons.Default.Inventory2){navigate(it)};Nav(screen,Screen.CLIENTS,"Clientes",Icons.Default.People){navigate(it)};Nav(screen,Screen.CASH,"Caja",Icons.Default.AccountBalanceWallet){navigate(it)};Nav(screen,Screen.ANALYTICS,"Análisis",Icons.Default.Insights){navigate(it)}}}){pad->Box(Modifier.padding(pad).fillMaxSize().background(Brush.radialGradient(listOf(Bg, Panel, Bg)))){when(screen){Screen.HOME->Dashboard(a){navigate(it)};Screen.SALES->SalesTerminal(a,salesCart,{salesCart=it});Screen.PRODUCTS->Products(a);Screen.CLIENTS->Customers(a);Screen.INVENTORY->Inventory(a);Screen.CASH->Cash(a);Screen.ANALYTICS->Analytics(a);Screen.PROMOTIONS->Promotions(a);Screen.TABLES->Tables(a);Screen.SUPPLIERS->Suppliers(a);Screen.PURCHASES->Purchases(a);Screen.REPORTS->Reports(a);Screen.USERS->Users(a);Screen.STOCK_HISTORY->StockHistory(a);Screen.CASH_HISTORY->CashHistory(a);Screen.AUDIT->Audit(a);Screen.GENERAL_Z->GeneralZCard(a);Screen.CONFIG->Config(a,prefs,themeName,sounds,haloName,{themeName=it},{sounds=it;MiComercioSounds.enabled=it},{haloName=it})}}}}}
@Composable private fun RowScope.Nav(current:Screen,target:Screen,label:String,icon:androidx.compose.ui.graphics.vector.ImageVector,onClick:(Screen)->Unit){Column(Modifier.weight(1f).clickable{onClick(target)}.padding(vertical=6.dp),horizontalAlignment=Alignment.CenterHorizontally){Icon(imageVector=icon,contentDescription=label,tint=if(current==target)Red else Muted);Text(label,fontSize=10.sp,color=if(current==target)Red else Muted)}}

@Composable private fun Dashboard(api:MiComercioApi,onOpen:(Screen)->Unit){var d by remember{mutableStateOf<Summary?>(null)};var msg by remember{mutableStateOf("")};val scope=rememberCoroutineScope();fun load(){scope.launch{try{val r=api.summary();if(r.isSuccessful)d=r.body()else msg="Error ${r.code()}"}catch(e:Exception){msg=e.message.orEmpty()}}};LaunchedEffect(Unit){load()};LazyColumn(Modifier.fillMaxSize().padding(16.dp)){item{Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Panel operativo",fontSize=28.sp,fontWeight=FontWeight.Black);Text("Todo Mi Comercio desde el teléfono",color=Muted)};IconButton(onClick={load()}){Icon(Icons.Default.Refresh,null)}};Spacer(Modifier.height(16.dp))};d?.let{x->item{MetricGrid(x)};item{Spacer(Modifier.height(12.dp));Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=Panel)){Column(Modifier.padding(16.dp)){Text("ACCESOS RÁPIDOS",fontWeight=FontWeight.Bold,color=Muted);Quick("Nueva venta","Cobrar con todos los medios"){onOpen(Screen.SALES)};Quick("Productos","Alta · edición · código · stock"){onOpen(Screen.PRODUCTS)};Quick("Clientes","Deudas · abonos · historial"){onOpen(Screen.CLIENTS)};Quick("Mesas","Tickets abiertos para cobrar después"){onOpen(Screen.TABLES)};Quick("Promociones","Crear y sincronizar con este teléfono"){onOpen(Screen.PROMOTIONS)};Quick("Compras","Proveedores y órdenes de compra"){onOpen(Screen.PURCHASES)};Quick("Movimientos de stock","Historial de entradas, salidas y conteos"){onOpen(Screen.STOCK_HISTORY)};Quick("Movimientos de caja","Historial de ingresos y egresos de la caja principal"){onOpen(Screen.CASH_HISTORY)};Quick("Auditoría","Operaciones y motivos registrados"){onOpen(Screen.AUDIT)};Quick("Reportes detallados","Hora · categorías · medios de pago"){onOpen(Screen.REPORTS)};Quick("Corte Z general","Corte total del día · no cierra ninguna caja"){onOpen(Screen.GENERAL_Z)}}}}};if(msg.isNotBlank())item{Text(msg,color=Color(0xFFFF8A80))}}}
@Composable fun MetricGrid(x:Summary){Column{Row(horizontalArrangement=Arrangement.spacedBy(10.dp),modifier=Modifier.fillMaxWidth()){Metric("VENTAS",money(x.ventas),Red,Modifier.weight(1f));Metric("TICKETS",x.tickets.toString(),ExecutiveBlue.accent,Modifier.weight(1f))};Spacer(Modifier.height(10.dp));Row(horizontalArrangement=Arrangement.spacedBy(10.dp),modifier=Modifier.fillMaxWidth()){Metric("STOCK",fmt(x.unidadesStock),Green,Modifier.weight(1f));Metric("STOCK BAJO",x.stockBajo.toString(),Orange,Modifier.weight(1f))};Spacer(Modifier.height(10.dp));Metric("DEUDA CLIENTES",money(x.deudaClientes),MiComercioRed.accent,Modifier.fillMaxWidth())}}
@Composable fun Metric(title:String,value:String,accent:Color,modifier:Modifier){Card(modifier.shadow(10.dp,RoundedCornerShape(18.dp)),colors=CardDefaults.cardColors(containerColor=Panel)){Column(Modifier.padding(16.dp)){Text(title,color=accent,fontSize=11.sp,fontWeight=FontWeight.Bold);Text(value,fontSize=23.sp,fontWeight=FontWeight.Black)}}}
@Composable fun Quick(title:String,sub:String,onClick:()->Unit){
    val icon = when {
        title.contains("venta",true) -> Icons.Default.PointOfSale
        title.contains("producto",true) -> Icons.Default.Inventory2
        title.contains("cliente",true) -> Icons.Default.People
        title.contains("mesa",true) -> Icons.Default.TableRestaurant
        title.contains("windows",true) -> Icons.Default.ReceiptLong
        title.contains("promoc",true) -> Icons.Default.LocalOffer
        title.contains("compra",true) -> Icons.Default.ShoppingCart
        title.contains("stock",true) -> Icons.Default.Warehouse
        title.contains("caja",true) -> Icons.Default.AccountBalanceWallet
        title.contains("auditor",true) -> Icons.Default.FactCheck
        title.contains("reporte",true) -> Icons.Default.Assessment
        else -> Icons.Default.Bolt
    }
    Card(Modifier.fillMaxWidth().padding(vertical=5.dp).clickable{MiComercioSounds.tap(true);onClick()},colors=CardDefaults.cardColors(containerColor=Panel),border=BorderStroke(1.dp,Red.copy(alpha=.22f)),shape=RoundedCornerShape(18.dp)){
        Row(Modifier.padding(13.dp),verticalAlignment=Alignment.CenterVertically){
            Box(Modifier.size(48.dp).shadow(10.dp,RoundedCornerShape(14.dp),ambientColor=Red.copy(alpha=.45f),spotColor=Red.copy(alpha=.55f)).background(Red.copy(alpha=.10f),RoundedCornerShape(14.dp)),contentAlignment=Alignment.Center){Icon(icon,null,tint=Red,modifier=Modifier.size(25.dp))}
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Black);Text(sub,color=Muted,fontSize=12.sp,maxLines=2)}
            Icon(Icons.Default.ArrowForward,null,tint=Green)
        }
    }
}

@Composable
fun Products(api: MiComercioApi) {
    var q by remember { mutableStateOf("") }
    var list by remember { mutableStateOf<List<Product>>(emptyList()) }
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var suppliers by remember { mutableStateOf<List<Supplier>>(emptyList()) }
    var editor by remember { mutableStateOf<Product?>(null) }
    var create by remember { mutableStateOf(false) }
    var stock by remember { mutableStateOf<Product?>(null) }
    var scan by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf("") }
    var remove by remember { mutableStateOf<Product?>(null) }
    var reason by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    fun load(s: String) { scope.launch { try { val r = api.products(s); if (r.isSuccessful) list = r.body().orEmpty() else msg = "Error ${r.code()}" } catch(e: Exception) { msg = e.message.orEmpty() } } }
    fun loadCategories() { scope.launch { try { val r=api.categories(); if(r.isSuccessful) categories=r.body().orEmpty() } catch(e:Exception){ msg=e.message.orEmpty() } } }
    fun loadSuppliers() { scope.launch { try { val r=api.suppliers(); if(r.isSuccessful) suppliers=r.body().orEmpty() } catch(e:Exception){ msg=e.message.orEmpty() } } }
    LaunchedEffect(Unit) { load(""); loadCategories(); loadSuppliers() }
    if (scan) { BarcodeScannerScreen("ESCANEAR PRODUCTO", { code -> MiComercioSounds.scan(); q = code; scan = false; load(code) }) { scan = false }; return }
    if (create) { ProductForm(api, null, categories, suppliers, { loadCategories() }, { loadSuppliers() }) { create = false; load(q); loadCategories() }; return }
    if (editor != null) { ProductForm(api, editor!!, categories, suppliers, { loadCategories() }, { loadSuppliers() }) { editor = null; load(q); loadCategories() }; return }
    if (stock != null) { ProductStockDialog(api, stock!!) { stock = null; load(q) }; return }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Productos", fontSize = 27.sp, fontWeight = FontWeight.Black); Text("F3 · Alta, edición, precios, IVA y stock", color = Muted) }
            IconButton(onClick = { scan = true }) { Icon(Icons.Default.QrCodeScanner, null) }
            IconButton(onClick = { create = true }) { Icon(Icons.Default.AddBox, null) }
        }
        OutlinedTextField(value = q, onValueChange = { q = it; load(it) }, label = { Text("Buscar producto") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        LazyColumn(Modifier.fillMaxSize()) {
            items(list) { p ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).combinedClickable(onClick = { editor = p }, onLongClick = { remove = p; reason = "" }), colors = CardDefaults.cardColors(containerColor = Panel)) {
                    Column(Modifier.padding(14.dp)) {
                        Row { Text(p.description, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Text(money(p.salePrice), color = Red, fontWeight = FontWeight.Black) }
                        Text("${p.barcode} · ${p.category.ifBlank { "Sin categoría" }}", color = Muted, fontSize = 12.sp)
                        Text(if(p.iva21) "IVA 21% · aplicado al costo" else "IVA 21% · no aplicado", color = if(p.iva21) Orange else Muted, fontSize = 11.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Stock ${fmt(p.stock)} ${p.unit}", color = if (p.stock <= p.minStock) Orange else Green, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            TextButton(onClick = { stock = p }) { Text("AJUSTAR") }
                        }
                    }
                }
            }
        }
        if (msg.isNotBlank()) Text(msg, color = Color(0xFFFF8A80))
    }
    if(remove!=null){
        AlertDialog(onDismissRequest={remove=null},title={Text("Eliminar producto")},text={Column{Text("Se desactivará el producto y se conservará su último stock.",color=Muted);Text("Stock anterior: ${fmt(remove!!.stock)}",fontWeight=FontWeight.Bold,color=Orange);Field("Motivo obligatorio",reason,onChange={reason=it})}},confirmButton={FButton(enabled=reason.isNotBlank(),onClick={val id=remove!!.id;scope.launch{try{val r=api.deleteProduct(id,mapOf("reason" to reason.trim()));if(r.isSuccessful){remove=null;reason="";load(q)}else msg="No se pudo eliminar (${r.code()}): ${r.errorBody()?.string().orEmpty()}"}catch(e:Exception){msg=e.message.orEmpty()}}}){Text("CONFIRMAR")}},dismissButton={TextFButton(onClick={remove=null}){Text("CANCELAR")}})}
}

@Composable
fun ProductForm(api:MiComercioApi,existing:Product?,savedCategories:List<String>,savedSuppliers:List<Supplier>,onCategoryChanged:()->Unit,onSupplierChanged:()->Unit,onDone:()->Unit){
    var barcode by remember{mutableStateOf(existing?.barcode.orEmpty())}; var description by remember{mutableStateOf(existing?.description.orEmpty())}; var category by remember{mutableStateOf(existing?.category.orEmpty())}; var unit by remember{mutableStateOf(existing?.unit?:"UN")};
    var sale by remember{mutableStateOf(existing?.salePrice?.toString()?.replace('.',',').orEmpty())}; var wholesale by remember{mutableStateOf(existing?.wholesalePrice?.toString()?.replace('.',',').orEmpty())}; var cost by remember{mutableStateOf(existing?.costPrice?.toString()?.replace('.',',').orEmpty())}; var stock by remember{mutableStateOf(existing?.stock?.toString()?.replace('.',',').orEmpty())}; var min by remember{mutableStateOf(existing?.minStock?.toString()?.replace('.',',').orEmpty())};
    var inv by remember{mutableStateOf(existing?.usesInventory?:true)}; var bulk by remember{mutableStateOf(existing?.bulk?:false)}; var iva by remember{mutableStateOf(existing?.iva21?:false)}; var scan by remember{mutableStateOf(false)}; var msg by remember{mutableStateOf("")}; var showCategories by remember{mutableStateOf(false)}; var newCategory by remember{mutableStateOf(false)}; var newCategoryText by remember{mutableStateOf("")};
    var suppliers by remember(savedSuppliers){mutableStateOf(savedSuppliers)}; var supplierId by remember{mutableStateOf(existing?.supplierId?:0)}; var showSuppliers by remember{mutableStateOf(false)}; var newSupplier by remember{mutableStateOf(false)}; var newSupplierName by remember{mutableStateOf("")};
    var showStockReductionReason by remember{mutableStateOf(false)}; var stockReductionReason by remember{mutableStateOf("")}; val scope=rememberCoroutineScope()
    LaunchedEffect(savedSuppliers){suppliers=savedSuppliers; if(existing!=null && supplierId==0)supplierId=existing.supplierId}
    if(scan){BarcodeScannerScreen("ESCANEAR CÓDIGO",{code->MiComercioSounds.scan();barcode=code;scan=false}){scan=false};return}
    if(newCategory){
        AlertDialog(onDismissRequest={newCategory=false},title={Text("Nueva categoría",fontWeight=FontWeight.Black)},text={Field("Nombre de la categoría",newCategoryText,onChange={newCategoryText=it})},confirmButton={FButton(enabled=newCategoryText.trim().isNotBlank(),onClick={scope.launch{try{val r=api.createCategory(CategoryWrite(newCategoryText.trim()));if(r.isSuccessful){category=newCategoryText.trim();newCategoryText="";newCategory=false;onCategoryChanged()}else msg="No se pudo crear la categoría (${r.code()})"}catch(e:Exception){msg=e.message.orEmpty()}}}){Text("CREAR Y USAR")}},dismissButton={TextFButton(onClick={newCategory=false}){Text("CANCELAR")}});return
    }
    if(newSupplier){
        AlertDialog(onDismissRequest={newSupplier=false},title={Text("Nuevo proveedor",fontWeight=FontWeight.Black)},text={Column{Text("Crealo y quedará seleccionado para este producto.",color=Muted);Field("Nombre / razón social",newSupplierName,onChange={newSupplierName=it})}},confirmButton={FButton(enabled=newSupplierName.trim().isNotBlank(),onClick={scope.launch{try{val r=api.createSupplier(SupplierWrite(newSupplierName.trim()));if(r.isSuccessful){val id=r.body()?.id?:0;val fresh=api.suppliers();if(fresh.isSuccessful)suppliers=fresh.body().orEmpty();supplierId=id;newSupplierName="";newSupplier=false;onSupplierChanged()}else msg="No se pudo crear el proveedor (${r.code()})"}catch(e:Exception){msg=e.message.orEmpty()}}}){Text("CREAR Y USAR")}},dismissButton={TextFButton(onClick={newSupplier=false}){Text("CANCELAR")}});return
    }
    fun saveProduct(reason:String="") {
        scope.launch{try{if(description.isBlank())throw Exception("La descripción es obligatoria.");if(barcode.isBlank())throw Exception("El código de barras es obligatorio.");if(category.isBlank())throw Exception("Seleccioná o creá una categoría.");val newStock=num(stock);if(num(sale)<0||num(wholesale)<0||num(cost)<0||newStock<0||num(min)<0)throw Exception("Los importes y stock no pueden ser negativos.");if(existing!=null&&newStock<existing.stock&&reason.isBlank()){showStockReductionReason=true;return@launch};val body=ProductWrite(barcode.trim(),description.trim(),num(sale),num(wholesale),num(cost),newStock,num(min),category.trim(),unit.trim().ifBlank{"UN"},bulk,inv,iva,reason.trim());val r=if(existing==null)api.createProduct(body)else api.updateProduct(existing.id,body);if(r.isSuccessful){val savedId=if(existing==null)(r.body()?.id?:0)else existing.id;if(savedId>0){val sr=api.assignSupplier(savedId,SupplierAssignWrite(supplierId,num(cost)));if(!sr.isSuccessful)throw Exception("El producto se guardó, pero no se pudo asignar el proveedor (${sr.code()}).")};showStockReductionReason=false;stockReductionReason="";MiComercioSounds.tap(true);onDone()}else msg="No se pudo guardar (${r.code()}): ${r.errorBody()?.string().orEmpty()}"}catch(e:Exception){msg=e.message.orEmpty()}}
    }
    if(showStockReductionReason){
        AlertDialog(onDismissRequest={showStockReductionReason=false},title={Text("Reducción de stock",fontWeight=FontWeight.Black)},text={Column{Text("El stock se está reduciendo de ${fmt(existing?.stock?:0.0)} a ${fmt(num(stock))}.",color=Muted);Text("Indicá el motivo para registrarlo en el reporte final enviado por email.",color=Muted,modifier=Modifier.padding(top=6.dp));Field("Motivo obligatorio",stockReductionReason,onChange={stockReductionReason=it})}},confirmButton={FButton(enabled=stockReductionReason.trim().isNotBlank(),onClick={saveProduct(stockReductionReason)}){Text("CONFIRMAR Y GUARDAR")}},dismissButton={TextFButton(onClick={showStockReductionReason=false}){Text("CANCELAR")}});return
    }
    val contentModifier=Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    Column(Modifier.fillMaxSize().padding(horizontal=18.dp)) {
        Column(contentModifier.weight(1f)) {
            Text(if(existing==null)"Nuevo producto" else "Editar producto",fontSize=27.sp,fontWeight=FontWeight.Black)
            Row(verticalAlignment=Alignment.CenterVertically){OutlinedTextField(value=barcode,onValueChange={barcode=it},label={Text("Código de barras")},singleLine=true,modifier=Modifier.weight(1f));IconButton(onClick={scan=true}){Icon(Icons.Default.QrCodeScanner,null)}}
            Field("Descripción",description,onChange={description=it})
            Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.weight(1f)){OutlinedTextField(value=category,onValueChange={},label={Text("Categoría / departamento")},singleLine=true,readOnly=true,modifier=Modifier.fillMaxWidth().clickable{showCategories=true})};IconButton(onClick={newCategory=true},modifier=Modifier.padding(start=4.dp)){Icon(Icons.Default.AddCircle,"Crear categoría",tint=Green)};IconButton(onClick={showCategories=true}){Icon(Icons.Default.ExpandMore,"Ver categorías",tint=Muted)}}
            if(showCategories){AlertDialog(onDismissRequest={showCategories=false},title={Text("Categorías creadas",fontWeight=FontWeight.Black)},text={LazyColumn(Modifier.heightIn(max=420.dp)){if(savedCategories.isEmpty())item{Text("Todavía no hay categorías creadas.",color=Muted)};items(savedCategories){c->ListRow(c,if(c.equals(category,true))"Seleccionada" else "Tocar para usar",onClick={category=c;showCategories=false})}}},confirmButton={FButton(onClick={newCategory=true;showCategories=false}){Icon(Icons.Default.AddCircle,null);Spacer(Modifier.width(5.dp));Text("NUEVA CATEGORÍA")}},dismissButton={TextFButton(onClick={showCategories=false}){Text("CERRAR")}});return}
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Field("Venta",sale,modifier=Modifier.weight(1f),onChange={sale=it});Field("Costo",cost,modifier=Modifier.weight(1f),onChange={cost=it})}
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Field("Mayorista",wholesale,modifier=Modifier.weight(1f),onChange={wholesale=it});Field("Stock",stock,modifier=Modifier.weight(1f),onChange={stock=it})}
            Field("Stock mínimo",min,onChange={min=it});Field("Unidad",unit,onChange={unit=it})
            Row(verticalAlignment=Alignment.CenterVertically){Checkbox(checked=inv,onCheckedChange={inv=it});Text("Usa inventario");Spacer(Modifier.width(18.dp));Checkbox(checked=bulk,onCheckedChange={bulk=it});Text("A granel")}
            Row(verticalAlignment=Alignment.CenterVertically){Checkbox(checked=iva,onCheckedChange={iva=it});Text("IVA 21%",fontWeight=FontWeight.Bold,color=if(iva) Orange else Muted);Text("  · se guarda en el producto",fontSize=11.sp,color=Muted)}
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment=Alignment.CenterVertically){
                Box(Modifier.weight(1f)){OutlinedTextField(value=suppliers.firstOrNull{it.id==supplierId}?.name?:(if(supplierId==0)"Sin proveedor asignado" else "Proveedor no disponible"),onValueChange={},label={Text("Proveedor")},singleLine=true,readOnly=true,modifier=Modifier.fillMaxWidth().clickable{showSuppliers=true})}
                IconButton(onClick={newSupplier=true},modifier=Modifier.padding(start=4.dp)){Icon(Icons.Default.AddCircle,"Crear proveedor",tint=Green)}
                IconButton(onClick={showSuppliers=true}){Icon(Icons.Default.ExpandMore,"Ver proveedores",tint=Muted)}
            }
            if(showSuppliers){AlertDialog(onDismissRequest={showSuppliers=false},title={Text("Proveedores disponibles",fontWeight=FontWeight.Black)},text={LazyColumn(Modifier.heightIn(max=420.dp)){item{ListRow("SIN PROVEEDOR","Quitar asignación",onClick={supplierId=0;showSuppliers=false})};if(suppliers.isEmpty())item{Text("Todavía no hay proveedores creados.",color=Muted)};items(suppliers){sp->ListRow(sp.name,if(sp.id==supplierId)"Seleccionado" else "Tocar para usar",onClick={supplierId=sp.id;showSuppliers=false})}}},confirmButton={FButton(onClick={newSupplier=true;showSuppliers=false}){Icon(Icons.Default.AddCircle,null);Spacer(Modifier.width(5.dp));Text("NUEVO PROVEEDOR")}},dismissButton={TextFButton(onClick={showSuppliers=false}){Text("CERRAR")}});return}
            if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80),modifier=Modifier.padding(bottom=8.dp))
        }
        Column(Modifier.fillMaxWidth().padding(top=8.dp,bottom=8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
            FButton(onClick={val newStock=num(stock);if(existing!=null&&newStock<existing.stock){showStockReductionReason=true}else{saveProduct()}},modifier=Modifier.fillMaxWidth().height(58.dp)){Text("✓  GUARDAR PRODUCTO",fontSize=16.sp,fontWeight=FontWeight.Black)}
            FOutlinedButton(onClick=onDone,modifier=Modifier.fillMaxWidth().height(46.dp)){Text("CANCELAR")}
        }
    }
}
@Composable fun ProductStockDialog(api:MiComercioApi,p:Product,onClose:()->Unit){var qty by remember{mutableStateOf("")};var type by remember{mutableStateOf("ENTRADA")};var ref by remember{mutableStateOf("")};var msg by remember{mutableStateOf("")};val scope=rememberCoroutineScope();AlertDialog(onDismissRequest=onClose,title={Text("Ajustar stock")},text={Column{Text(p.description,fontWeight=FontWeight.Bold);Field("Cantidad",qty,onChange={qty=it});Field(if(type=="SALIDA")"Motivo de salida (obligatorio)" else "Motivo",ref,onChange={ref=it});Row{FilterChip(selected=type=="ENTRADA",onClick={type="ENTRADA";msg=""},label={Text("ENTRADA")});Spacer(Modifier.width(8.dp));FilterChip(selected=type=="SALIDA",onClick={type="SALIDA";msg=""},label={Text("SALIDA")})};if(msg.isNotBlank())Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.padding(top=8.dp)){Icon(Icons.Default.ErrorOutline,null,tint=Red);Spacer(Modifier.width(6.dp));Text(msg,color=Red,fontWeight=FontWeight.Bold)} }},confirmButton={FButton(onClick={if(type=="SALIDA"&&ref.isBlank()){msg="El motivo de salida es obligatorio.";return@FButton};scope.launch{try{val r=api.stock(p.id,StockUpdate(num(qty),type,ref.trim()));if(r.isSuccessful)onClose()else msg="Error ${r.code()}"}catch(e:Exception){msg=e.message.orEmpty()}}}){Text("GUARDAR")}},dismissButton={TextFButton(onClick=onClose){Text("CANCELAR")}})}

@Composable
fun SalesTerminal(api:MiComercioApi,cart:List<SaleLineWrite>,onCartChange:(List<SaleLineWrite>)->Unit){
    var products by remember{mutableStateOf<List<Product>>(emptyList())}
    var catalog by remember{mutableStateOf<List<Product>>(emptyList())}
    var promotions by remember{mutableStateOf<List<Promotion>>(emptyList())}
    var search by remember{mutableStateOf("")}; var scan by remember{mutableStateOf(false)}; var massScan by remember{mutableStateOf(false)}; var massPreview by remember{mutableStateOf<List<MassScanPreviewLine>>(emptyList())}; var massEditing by remember{mutableStateOf<Int?>(null)}
    var selected by remember{mutableStateOf<Product?>(null)}; var bulkProduct by remember{mutableStateOf<Product?>(null)}; var editing by remember{mutableStateOf<Pair<Int,SaleLineWrite>?>(null)}
    var sales by remember{mutableStateOf<List<Sale>>(emptyList())}; var detailSale by remember{mutableStateOf<Sale?>(null)}
    var customerId by remember{mutableStateOf(1)}; var customerName by remember{mutableStateOf("Consumidor final")}
    var customers by remember{mutableStateOf<List<Customer>>(emptyList())}; var chooseCustomer by remember{mutableStateOf(false)}
    var choosePromotion by remember{mutableStateOf(false)}; var pay by remember{mutableStateOf(false)}; var table by remember{mutableStateOf(false)}; var msg by remember{mutableStateOf("")}; var common by remember{mutableStateOf(false)}
    var cartDeletePromotion by remember{mutableStateOf<Int?>(null)}; var cartDeleteReason by remember{mutableStateOf("")}; var discountReason by remember{mutableStateOf("")}
    val scope=rememberCoroutineScope()
    fun load(){scope.launch{try{val r=api.products(search);if(r.isSuccessful)products=r.body().orEmpty()}catch(e:Exception){msg=e.message.orEmpty()}}}
    fun loadCatalog(){scope.launch{try{val r=api.products("");if(r.isSuccessful)catalog=r.body().orEmpty()}catch(_:Exception){}}}
    fun loadPromos(){scope.launch{try{val r=api.promotions();if(r.isSuccessful)promotions=r.body().orEmpty()}catch(e:Exception){msg=e.message.orEmpty()}}}
    fun addPromotion(p:Promotion){
        val normal=p.items.sumOf{item->(products.firstOrNull{x->x.id==item.productId}?.salePrice?:0.0)*item.quantity}
        if(normal<=0){msg="No se pudo calcular el precio normal de la promoción.";return}
        val additions=p.items.mapNotNull{item->
            val prod=products.firstOrNull{x->x.id==item.productId} ?: return@mapNotNull null
            val gross=prod.salePrice*item.quantity
            val discount=Math.max(0.0, gross-(gross/normal)*p.price)
            SaleLineWrite(prod.id,item.quantity,prod.salePrice,discount,"PROMO · ${p.name} · ${prod.description}",false)
        }
        onCartChange(cart+additions)
        choosePromotion=false
        msg="Promoción '${p.name}' agregada al carrito."
    }
    LaunchedEffect(Unit){load();loadCatalog();loadPromos();try{val r=api.sales();if(r.isSuccessful)sales=r.body().orEmpty();val c=api.customers();if(c.isSuccessful)customers=c.body().orEmpty()}catch(_:Exception){}}
    LaunchedEffect(massScan){
        if(massScan) MiComercioSounds.scan()
    }
    if(massScan){
    if(massEditing!=null){
        val index=massEditing!!
        val item=massPreview.getOrNull(index)
        if(item!=null){
            var qty by remember(item.productId, item.quantity){mutableStateOf(item.quantity.toString())}
            AlertDialog(
                onDismissRequest={massEditing=null},
                title={Text(item.description,fontWeight=FontWeight.Black)},
                text={Column{
                    Text("Producto detectado en el escaneo masivo",color=Muted)
                    Spacer(Modifier.height(8.dp))
                    Field("Cantidad",qty,onChange={qty=it})
                    Text("Precio ${money(item.unitPrice)} · Total ${money(item.unitPrice*item.quantity)}",color=Muted,fontSize=12.sp)
                }},
                confirmButton={Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                    FButton(onClick={
                        val q=num(qty).toInt()
                        if(q>0){
                            massPreview=massPreview.toMutableList().also{it[index]=item.copy(quantity=q)}
                            massEditing=null
                        }
                    }){Text("GUARDAR")};
                    TextFButton(onClick={
                        massPreview=massPreview.filterIndexed{i,_->i!=index}
                        massEditing=null
                    }){Text("ELIMINAR")}
                }},
                dismissButton={TextFButton(onClick={massEditing=null}){Text("CANCELAR")}}
            )
        } else massEditing=null
    }
    BarcodeMassScannerScreen(
        title="ESCANEO MASIVO",
        previewItems=massPreview.map { line -> "${line.description}  ×${line.quantity}" },
        onPreviewClick={index->massEditing=index},
        onResult={code->
            MiComercioSounds.scan()
            scope.launch{
                try{
                    val response=api.products(code)
                    val p=response.body()?.firstOrNull{it.barcode.equals(code,true)} ?: response.body()?.firstOrNull()
                    if(p!=null){
                        val existingIndex=massPreview.indexOfFirst{it.productId==p.id}
                        massPreview=if(existingIndex<0){
                            massPreview + MassScanPreviewLine(p.id,p.description,1,p.salePrice)
                        }else{
                            massPreview.toMutableList().also{it[existingIndex]=it[existingIndex].copy(quantity=it[existingIndex].quantity+1)}
                        }
                    }
                }catch(_:Exception){ }
            }
        },
        onStop={
            massScan=false
            val preview=massPreview
            massPreview=emptyList()
            scope.launch{
                var updated=cart
                var added=0
                for(item in preview){
                    updated=updated+SaleLineWrite(item.productId,item.quantity.toDouble(),item.unitPrice,0.0,item.description,false)
                    added+=item.quantity
                }
                onCartChange(updated)
                msg="Escaneo finalizado · $added producto(s) agregado(s) al carrito"
            }
        }
    )
    return
}
if(scan){BarcodeScannerScreen("ESCANEAR PARA VENTA",{code->MiComercioSounds.scan();scope.launch{try{val p=api.products(code).body()?.firstOrNull{it.barcode.equals(code,true)} ?: api.products(code).body()?.firstOrNull();if(p!=null){if(p.bulk){bulkProduct=p}else{onCartChange(cart+SaleLineWrite(p.id,1.0,p.salePrice,0.0,p.description,false));msg="${p.description} agregado · cantidad 1"}}else{msg="Producto no encontrado";MiComercioSounds.error()}}catch(e:Exception){msg=e.message.orEmpty();MiComercioSounds.error()}};scan=false}){scan=false};return}
    if(chooseCustomer){CustomerPickerDialog(customers,{c->customerId=c.id;customerName=c.name;chooseCustomer=false},{chooseCustomer=false});return}
    if(choosePromotion){PromotionPickerDialog(promotions,products,{addPromotion(it)},{choosePromotion=false});return}
    if(common){CommonProductDialog(onClose={common=false},onAdd={desc,price->
        common=false
        scope.launch{
            try{
                // PRODUCTO EN COMÚN es una línea libre: no necesita existir en el catálogo.
                // Se usa un identificador interno reservado y el reporte conserva descripción y precio.
                val commonId = -1
                onCartChange(cart+SaleLineWrite(commonId,1.0,price,0.0,desc,true))
                msg="Producto en común agregado al ticket y quedará registrado en el reporte."
                MiComercioSounds.tap(true)
            }catch(e:Exception){
                msg=e.message.orEmpty()
                MiComercioSounds.error()
            }
        }
    });return}
    if(bulkProduct!=null){val bp=bulkProduct!!;BulkSaleDialog(product=bp,onClose={bulkProduct=null},onAdd={kg,amount->onCartChange(cart+SaleLineWrite(bp.id,kg,bp.salePrice,0.0,bp.description,false));msg="${bp.description} agregado · ${fmt(kg)} kg · ${money(amount)}";bulkProduct=null});return}
    if(selected!=null){val sp=selected!!;var qty by remember(sp.id){mutableStateOf("1")};if(sp.bulk){BulkSaleDialog(product=sp,onClose={selected=null},onAdd={kg,amount->onCartChange(cart+SaleLineWrite(sp.id,kg,sp.salePrice,0.0,sp.description,false));msg="${sp.description} agregado · ${fmt(kg)} kg · ${money(amount)}";selected=null});return};AlertDialog(onDismissRequest={selected=null},title={Text(sp.description)},text={Column{Text("Stock ${fmt(sp.stock)}");Text("Precio ${money(sp.salePrice)}",color=Green);Field("Cantidad",qty,onChange={qty=it})}},confirmButton={FButton(onClick={val q=num(qty);if(q>0){onCartChange(cart+SaleLineWrite(sp.id,q,sp.salePrice,0.0,sp.description,false));selected=null}else msg="Cantidad inválida"}){Text("AGREGAR")}},dismissButton={TextFButton(onClick={selected=null}){Text("CANCELAR")}});return}
    editing?.let{(index,line)->val ep=catalog.firstOrNull{it.id==line.productId};if(ep?.bulk==true){BulkSaleDialog(product=ep,onClose={editing=null},onAdd={kg,_->onCartChange(cart.toMutableList().also{it[index]=line.copy(quantity=kg,discount=0.0)});editing=null})}else{LineEditDialog(line,onDone={editing=null},onSave={updated,reason->{if(reason.isNotBlank())discountReason=reason;onCartChange(cart.toMutableList().also{it[index]=updated});editing=null}})}}
    if(cartDeletePromotion!=null){
        AlertDialog(onDismissRequest={cartDeletePromotion=null;cartDeleteReason=""},title={Text("Eliminar promoción")},text={Column{Text("Esta línea pertenece a una promoción. Indicá por qué se elimina la promoción del carrito.",color=Muted);Field("Motivo obligatorio",cartDeleteReason,onChange={cartDeleteReason=it})}},confirmButton={FButton(enabled=cartDeleteReason.isNotBlank(),onClick={val idx=cartDeletePromotion!!;onCartChange(cart.toMutableList().also{it.removeAt(idx)});cartDeletePromotion=null;cartDeleteReason=""}){Text("ELIMINAR PROMOCIÓN")}},dismissButton={TextFButton(onClick={cartDeletePromotion=null;cartDeleteReason=""}){Text("CANCELAR")}})
        return
    }
    if(detailSale!=null){SaleDetailDialog(api,detailSale!!,onClose={detailSale=null},onDone={detailSale=null;scope.launch{try{sales=api.sales().body().orEmpty()}catch(_:Exception){}}});return}
    if(pay){PaymentDialog(api,cart,customerId,customerName,onClose={pay=false},onMessage={msg=it},onPaid={onCartChange(emptyList());pay=false;scope.launch{try{sales=api.sales().body().orEmpty();customers=api.customers().body().orEmpty()}catch(_:Exception){}}});return}
    if(table){TablePicker(api,cart,customerId,customerName){table=false;onCartChange(emptyList())};return}
    Column(
        Modifier.fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement=Arrangement.Top
    ){
        Row(verticalAlignment=Alignment.CenterVertically){
            Column(Modifier.weight(1f)){
                Text("Ventas",fontSize=27.sp,fontWeight=FontWeight.Black,color=Orange)
                Text("Venta directa · escáner · tickets",color=Muted)
            }
        }
        Spacer(Modifier.height(8.dp))
        ScanActionButton(
            onTap={scan=true},
            onLongPress={massScan=true},
            modifier=Modifier.fillMaxWidth().height(52.dp)
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment=Alignment.CenterVertically){
            OutlinedTextField(value=search,onValueChange={search=it;load()},label={Text("Buscar producto")},singleLine=true,modifier=Modifier.weight(1f))
            IconButton(onClick={load()}){Icon(Icons.Default.Search,null)}
        }
        if(products.isNotEmpty()&&search.isNotBlank())Column{products.take(8).forEach{p->ListRow(p.description,"${money(p.salePrice)} · stock ${fmt(p.stock)}",onClick={selected=p;search="";products=emptyList()})}}
        Card(Modifier.fillMaxWidth().padding(vertical=8.dp),colors=CardDefaults.cardColors(containerColor=Panel)){Column(Modifier.padding(12.dp)){
            Row(verticalAlignment=Alignment.CenterVertically){TextFButton(onClick={chooseCustomer=true},modifier=Modifier.weight(1f)){Column(horizontalAlignment=Alignment.Start){Text("CLIENTE",fontSize=10.sp,color=Muted);Text(customerName,fontWeight=FontWeight.Bold)}};TextFButton(onClick={onCartChange(emptyList());customerName="Consumidor final";customerId=1}){Text("LIMPIAR")}}
            Row(Modifier.fillMaxWidth().padding(top=6.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){FOutlinedButton(onClick={loadPromos();choosePromotion=true},modifier=Modifier.weight(1f).height(52.dp)){Icon(Icons.Default.LocalOffer,null);Spacer(Modifier.width(5.dp));Text("PROMOCIONES",maxLines=1)}
            FOutlinedButton(onClick={common=true},modifier=Modifier.weight(1f).height(52.dp)){Icon(Icons.Default.EditNote,null);Spacer(Modifier.width(5.dp));Text("PRODUCTO EN COMÚN",maxLines=1)} }
            if(cart.isEmpty())Text("Todavía no hay artículos en el ticket.",color=Muted,modifier=Modifier.padding(vertical=12.dp))
            cart.forEachIndexed{index,line->val cp=catalog.firstOrNull{it.id==line.productId};val isBulk=cp?.bulk==true;val lineTotal=line.quantity*line.unitPrice-line.discount;Row(Modifier.fillMaxWidth().padding(vertical=6.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(line.description,fontWeight=FontWeight.Bold);Text(if(isBulk)"${fmt(line.quantity)} kg × ${money(line.unitPrice)}/kg = ${money(lineTotal)}" else "${fmt(line.quantity)} × ${money(line.unitPrice)} · Descuento ${money(line.discount)}",color=Muted,fontSize=12.sp)};if(isBulk){TextButton(onClick={editing=index to line}){Text("${fmt(line.quantity)} kg",fontWeight=FontWeight.Black)}}else{TextButton(onClick={onCartChange(cart.toMutableList().also{it[index]=line.copy(quantity=line.quantity+1)})}){Text("+")};TextButton(onClick={editing=index to line}){Text("${fmt(line.quantity)}",fontWeight=FontWeight.Black)};TextButton(onClick={if(line.quantity>1)onCartChange(cart.toMutableList().also{it[index]=line.copy(quantity=line.quantity-1)})else onCartChange(cart.toMutableList().also{it.removeAt(index)})}){Text("−")}};IconButton(onClick={editing=index to line}){Icon(Icons.Default.Edit,"Editar")};IconButton(onClick={if(line.description.startsWith("PROMO ·",true)){cartDeletePromotion=index;cartDeleteReason=""}else{onCartChange(cart.toMutableList().also{it.removeAt(index)})}}){Icon(Icons.Default.Delete,"Eliminar")}}}
        }}
        val total=cart.sumOf{it.quantity*it.unitPrice-it.discount};Text("TOTAL ${money(total)}",fontSize=30.sp,fontWeight=FontWeight.Black)
        Column(verticalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()){
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()){BlinkingPayButton(enabled=cart.isNotEmpty(),onClick={pay=true},modifier=Modifier.weight(1f).height(52.dp));OutlinedButton(enabled=cart.isNotEmpty(),onClick={table=true},modifier=Modifier.weight(1f).height(52.dp)){Text("GUARDAR EN MESA")}}
            FOutlinedButton(enabled=cart.isNotEmpty(),onClick={scope.launch{try{val r=api.sendPendingSale(SalePendingWrite(customerId,customerName,cart,notes=discountReason));if(r.isSuccessful){msg="Venta guardada localmente · pendiente de cobro";onCartChange(emptyList())}else msg="No se pudo enviar a este teléfono (${r.code()}): ${r.errorBody()?.string()}"}catch(e:Exception){msg=e.message.orEmpty()}}},modifier=Modifier.fillMaxWidth().height(48.dp)){Text("GUARDAR VENTA PENDIENTE")}
        }
        if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80),modifier=Modifier.padding(top=8.dp))
        Spacer(Modifier.height(8.dp));Text("ÚLTIMAS VENTAS",fontWeight=FontWeight.Bold);Column{sales.take(15).forEach{s->ListRow("Ticket #${s.ticket} · ${money(s.total)}","${s.medio} · ${s.cliente} · ${s.canal}",onClick={detailSale=s})}}
    }
}

@Composable
fun Promotions(api:MiComercioApi){
    var list by remember{mutableStateOf<List<Promotion>>(emptyList())}
    var create by remember{mutableStateOf(false)}
    var edit by remember{mutableStateOf<Promotion?>(null)}
    var remove by remember{mutableStateOf<Promotion?>(null)}
    var deleteReason by remember{mutableStateOf("")}
    var msg by remember{mutableStateOf("")}
    val scope=rememberCoroutineScope()
    fun load(){scope.launch{try{val r=api.promotions();if(r.isSuccessful)list=r.body().orEmpty() else msg="Error ${r.code()}"}catch(e:Exception){msg=e.message.orEmpty()}}}
    LaunchedEffect(Unit){load()}
    if(create){PromotionForm(api,null){create=false;load()};return}
    if(edit!=null){PromotionForm(api,edit!!){edit=null;load()};return}
    Column(Modifier.fillMaxSize().padding(16.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Promociones",fontSize=27.sp,fontWeight=FontWeight.Black);Text("Alta · eliminación · sincronización este teléfono",color=Green)};IconButton(onClick={create=true}){Icon(Icons.Default.AddCircle,null)}}
        LazyColumn{
            items(list){p->
                Card(Modifier.fillMaxWidth().padding(vertical=5.dp).combinedClickable(onClick={edit=p},onLongClick={remove=p}),colors=CardDefaults.cardColors(containerColor=Panel)){
                    Column(Modifier.padding(14.dp)){
                        Row{Text(p.name,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));Text(money(p.price),color=Orange,fontWeight=FontWeight.Black)}
                        Text(if(p.active)"ACTIVA" else "INACTIVA",color=if(p.active)Green else Muted,fontSize=11.sp)
                        Text(p.description,color=Muted)
                        Text(p.items.joinToString(" · "){item->"${item.description} x${fmt(item.quantity)}"},fontSize=12.sp)
                    }
                }
            }
        }
        if(msg.isNotBlank())Text(msg,color=Orange)
    }
    if(remove!=null){
        AlertDialog(onDismissRequest={remove=null;deleteReason=""},title={Text("Eliminar promoción")},text={Column{
            Text("¿Eliminar '${remove!!.name}' de Mi Comercio? La eliminación se sincroniza con este teléfono.")
            Spacer(Modifier.height(8.dp))
            Field("Motivo obligatorio",deleteReason){deleteReason=it}
            Text("El motivo quedará registrado en el reporte final.",color=Muted,fontSize=11.sp)
        }},
            confirmButton={FButton(enabled=deleteReason.isNotBlank(),onClick={val id=remove!!.id;val reason=deleteReason.trim();scope.launch{try{val r=api.deletePromotion(id,mapOf("reason" to reason));if(r.isSuccessful){remove=null;deleteReason="";load()}else msg="Error ${r.code()}: ${r.errorBody()?.string().orEmpty()}"}catch(e:Exception){msg=e.message.orEmpty()}}}){Text("ELIMINAR")}},
            dismissButton={TextFButton(onClick={remove=null;deleteReason=""}){Text("CANCELAR")}})
    }
}

@Composable
fun PromotionForm(api:MiComercioApi,existing:Promotion?=null,onDone:()->Unit){
    var name by remember{mutableStateOf(existing?.name.orEmpty())}
    var desc by remember{mutableStateOf(existing?.description.orEmpty())}
    var price by remember{mutableStateOf(existing?.price?.toString()?.replace('.',',') ?: "")}
    var products by remember{mutableStateOf<List<Product>>(emptyList())}
    var q by remember{mutableStateOf("")}
    var selected by remember{mutableStateOf<Map<Int,Double>>(existing?.items?.associate{it.productId to it.quantity} ?: emptyMap())}
    var msg by remember{mutableStateOf("")}
    val scope=rememberCoroutineScope()
    LaunchedEffect(Unit){try{val r=api.products("");if(r.isSuccessful)products=r.body().orEmpty()}catch(e:Exception){msg=e.message.orEmpty()}}
    val filtered=products.filter{q.isBlank()||it.description.contains(q,true)||it.barcode.contains(q,true)}
    val normal=selected.entries.sumOf{(id,qty)->(products.firstOrNull{it.id==id}?.salePrice?:0.0)*qty}
    Column(Modifier.fillMaxSize().padding(18.dp)){
        Text(if(existing==null)"Nueva promoción" else "Editar promoción",fontSize=27.sp,fontWeight=FontWeight.Black)
        Field("Nombre",name){name=it}
        Field("Descripción",desc){desc=it}
        Field("Buscar producto",q){q=it}
        Text("Productos seleccionados: ${selected.values.sumOf{it}} · Precio normal ${money(normal)}",fontWeight=FontWeight.Bold,color=Green)
        Field("Precio promocional",price){price=it}
        LazyColumn(Modifier.weight(1f)){
            items(filtered){p->
                val qty=selected[p.id]?:0.0
                Card(Modifier.fillMaxWidth().padding(vertical=4.dp),colors=CardDefaults.cardColors(containerColor=Panel)){
                    Row(Modifier.padding(8.dp),verticalAlignment=Alignment.CenterVertically){
                        Column(Modifier.weight(1f)){
                            Text(p.description,fontWeight=FontWeight.Bold)
                            Text("Normal ${money(p.salePrice)} · Stock ${fmt(p.stock)}",color=Muted,fontSize=12.sp)
                        }
                        IconButton(onClick={
                            if(qty>0) selected=selected.toMutableMap().also{m->
                                val n=qty-1
                                if(n<=0)m.remove(p.id) else m[p.id]=n
                            }
                        }){Icon(Icons.Default.RemoveCircleOutline,null)}
                        Text(fmt(qty),fontWeight=FontWeight.Black,modifier=Modifier.width(35.dp))
                        IconButton(onClick={selected=selected.toMutableMap().also{it[p.id]=qty+1}}){Icon(Icons.Default.AddCircleOutline,null)}
                    }
                }
            }
        }
        Text("Precio normal ${money(normal)} · Descuento ${money(maxOf(0.0,normal-num(price)))}",fontWeight=FontWeight.Bold,color=Orange)
        FButton(enabled=name.isNotBlank()&&num(price)>0&&selected.isNotEmpty(),onClick={scope.launch{try{val r=if(existing==null) api.createPromotion(PromotionWrite(name=name,description=desc,price=num(price),items=selected.map{PromotionItemWrite(it.key,it.value)})) else api.updatePromotion(PromotionWrite(id=existing.id,name=name,description=desc,price=num(price),active=existing.active,startAt=existing.startAt,endAt=existing.endAt,items=selected.map{PromotionItemWrite(it.key,it.value)}));if(r.isSuccessful)onDone()else msg="Error ${r.code()}: ${r.errorBody()?.string()}"}catch(e:Exception){msg=e.message.orEmpty()}}},modifier=Modifier.fillMaxWidth()){Text("GUARDAR Y SINCRONIZAR CON WINDOWS")}
        FOutlinedButton(onClick=onDone,modifier=Modifier.fillMaxWidth()){Text("CANCELAR")}
        if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80))
    }
}

@Composable fun CustomerPickerDialog(customers:List<Customer>,onSelect:(Customer)->Unit,onClose:()->Unit){
    AlertDialog(onDismissRequest=onClose,title={Text("Elegir cliente para la venta")},text={Column{Text("Seleccioná el cliente al que se imputará el crédito.",color=Muted);LazyColumn(Modifier.heightIn(max=420.dp)){items(customers){c->Card(Modifier.fillMaxWidth().padding(vertical=4.dp).clickable{onSelect(c)},colors=CardDefaults.cardColors(containerColor=Panel)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(c.name,fontWeight=FontWeight.Bold);Text("Deuda ${money(c.deuda)} · Límite ${money(c.creditLimit)}",color=Muted,fontSize=12.sp)};Icon(imageVector=Icons.Default.ChevronRight,contentDescription=null,tint=Green)}}}}}},confirmButton={TextFButton(onClick=onClose){Text("CANCELAR")}})
}

@Composable fun PromotionPickerDialog(promotions:List<Promotion>,products:List<Product>,onSelect:(Promotion)->Unit,onClose:()->Unit){
    AlertDialog(onDismissRequest=onClose,title={Text("Promociones disponibles")},text={LazyColumn(Modifier.heightIn(max=500.dp)){items(promotions.filter{it.active}){p->Card(Modifier.fillMaxWidth().padding(vertical=5.dp).clickable{onSelect(p)},colors=CardDefaults.cardColors(containerColor=Panel)){Column(Modifier.padding(14.dp)){Row{Text(p.name,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));Text(money(p.price),color=Orange,fontWeight=FontWeight.Black)};Text(p.description,color=Muted);Text(p.items.joinToString(" · "){i->"${i.description} x${fmt(i.quantity)}"},fontSize=12.sp,color=Green)}}}}},confirmButton={TextFButton(onClick=onClose){Text("CANCELAR")}})
}

@Composable fun LineEditDialog(line:SaleLineWrite,onDone:()->Unit,onSave:(SaleLineWrite,String)->Unit){var qty by remember{mutableStateOf(fmt(line.quantity))};var discount by remember{mutableStateOf(if(line.quantity*line.unitPrice>0)(line.discount/(line.quantity*line.unitPrice)*100).toString().replace('.',',') else "0")};var reason by remember{mutableStateOf("")};var msg by remember{mutableStateOf("")};AlertDialog(onDismissRequest=onDone,title={Text("Editar artículo")},text={Column{Text(line.description,fontWeight=FontWeight.Bold);Field("Cantidad",qty,onChange={qty=it});Field("Descuento %",discount,onChange={discount=it});if(num(discount)>0&&!line.description.startsWith("PROMO ·",true))Field("Motivo del descuento (obligatorio)",reason,onChange={reason=it});Text("Precio unitario ${money(line.unitPrice)}",color=Muted);if(msg.isNotBlank())Row(verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.ErrorOutline,null,tint=Red);Spacer(Modifier.width(6.dp));Text(msg,color=Red,fontWeight=FontWeight.Bold)} }},confirmButton={FButton(onClick={val q=num(qty);val pct=num(discount);if(q<=0||pct<0||pct>100)msg="Ingresá valores válidos" else if(pct>0&&!line.description.startsWith("PROMO ·",true)&&reason.isBlank())msg="El motivo del descuento es obligatorio." else onSave(line.copy(quantity=q,discount=q*line.unitPrice*pct/100.0),reason.trim())}){Text("GUARDAR")}},dismissButton={TextFButton(onClick=onDone){Text("CANCELAR")}})}
@Composable
private fun BlinkingPayButton(enabled:Boolean,onClick:()->Unit,modifier:Modifier=Modifier){
    val infinite=rememberInfiniteTransition(label="cobrarNeon")
    val pulse by infinite.animateFloat(initialValue=.12f,targetValue=1f,animationSpec=infiniteRepeatable(animation=tween(360),repeatMode=RepeatMode.Reverse),label="pulse")
    val scalePulse by infinite.animateFloat(initialValue=.98f,targetValue=1.03f,animationSpec=infiniteRepeatable(animation=tween(420),repeatMode=RepeatMode.Reverse),label="scale")
    // COBRAR siempre usa verde neón para que el estado de acción sea inmediato y visible.
    val lightColor=NeonGreen
    val shape=RoundedCornerShape(16.dp)
    Button(enabled=enabled,onClick={MiComercioSounds.tap(true);onClick()},modifier=modifier.scale(scalePulse).shadow((24f+42f*pulse).dp,shape,ambientColor=lightColor.copy(alpha=.78f*pulse),spotColor=lightColor.copy(alpha=1f*pulse)).shadow((12f+20f*pulse).dp,shape,ambientColor=lightColor.copy(alpha=.42f*pulse),spotColor=lightColor.copy(alpha=.82f*pulse)),shape=shape,colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF07130D).copy(alpha=.97f),contentColor=lightColor,disabledContainerColor=MaterialTheme.colorScheme.surface.copy(alpha=.35f)),border=BorderStroke((3f+3.5f*pulse).dp,lightColor.copy(alpha=.72f+.28f*pulse))){
        Text("COBRAR",fontWeight=FontWeight.Black,fontSize=24.sp,letterSpacing=2.2.sp,color=lightColor.copy(alpha=.82f+.18f*pulse))
    }
}

@Composable
fun PaymentDialog(
    api: MiComercioApi,
    cart: List<SaleLineWrite>,
    customerId: Int,
    customerName: String,
    onClose: () -> Unit,
    onMessage: (String) -> Unit,
    onPaid: () -> Unit = {},
    tableId: Int? = null,
    windowsTicketId: String? = null
) {
    val total = cart.sumOf { it.quantity * it.unitPrice - it.discount }
    var methods by remember { mutableStateOf(listOf("EFECTIVO", "TRANSFERENCIA", "CRÉDITO", "DÓLARES", "TARJETA")) }
    // Si hay un cliente real seleccionado, la venta arranca directamente en CRÉDITO.
    // El usuario puede cambiar a MIXTO y distribuir el total entre crédito y otros medios.
    var selected by remember(customerId, total) { mutableStateOf(if (customerId > 1) "CRÉDITO" else "EFECTIVO") }
    var amount by remember(customerId, total) { mutableStateOf(if (customerId > 1) total.toString().replace('.', ',') else "") }
    var received by remember { mutableStateOf("") }
    var mixed: Map<String, Double> by remember(customerId, total) { mutableStateOf<Map<String, Double>>(if (customerId > 1) mapOf("CRÉDITO" to total) else emptyMap()) }
    val context = LocalContext.current
    var cash by remember { mutableStateOf<CashStatus?>(null) }
    var loading by remember { mutableStateOf(true) }
    var processing by remember { mutableStateOf(false) }
    var creditLimit by remember(customerId) { mutableStateOf(0.0) }
    var creditBalance by remember(customerId) { mutableStateOf(0.0) }
    var creditAlert by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val response = api.cash()
            if (response.isSuccessful) {
                cash = response.body()
                val pm = try { api.paymentMethods().body().orEmpty() } catch (_: Exception) { emptyList() }
                val allowed = listOf("EFECTIVO","TRANSFERENCIA","CRÉDITO","DÓLARES","TARJETA")
                methods = allowed.filter { pm.isEmpty() || pm.contains(it) }.toMutableList().also { list ->
                    if (!list.contains("EFECTIVO")) list.add(0,"EFECTIVO")
                    if (!list.contains("CRÉDITO")) list.add("CRÉDITO")
                    if (cash?.mercadoPagoEnabled == true) list.add("MERCADO PAGO")
                }
            }
            if (customerId > 1) {
                try {
                    val customersResponse = api.customers()
                    val customer = customersResponse.body().orEmpty().firstOrNull { it.id == customerId }
                    if (customer != null) {
                        creditLimit = customer.creditLimit
                        creditBalance = customer.deuda
                    }
                } catch (_: Exception) { }
            }
        } catch (e: Exception) {
            onMessage(e.message.orEmpty())
        } finally {
            loading = false
        }
    }

    val cajaAbierta = cash?.estado?.let { it.equals("OPEN",true) || it.equals("ABIERTA",true) }==true
    val mercadoPagoDisponible = cajaAbierta && cash?.mercadoPagoEnabled == true
    val requiereCaja = if (selected == "MIXTO") {
        mixed.any { (method, value) -> value > 0.009 && method.equals("EFECTIVO", true) }
    } else selected.equals("EFECTIVO", true)
    val medioValido = selected != "MERCADO PAGO" || mercadoPagoDisponible
    val clienteConCuenta = customerId > 1
    val creditoDisponible = if (creditLimit <= 0.0) Double.POSITIVE_INFINITY else maxOf(0.0, creditLimit - creditBalance)

    if (creditAlert.isNotBlank()) {
        Dialog(onDismissRequest = { creditAlert = "" }) {
            Card(
                Modifier.fillMaxWidth().padding(18.dp).shadow(22.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0B0E)),
                border = BorderStroke(2.dp, NeonRed.copy(alpha = .95f))
            ) {
                Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LÍMITE DE CRÉDITO", color = NeonRed, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(10.dp))
                    Text(creditAlert, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Disponible: ${if (creditoDisponible.isInfinite()) "SIN LÍMITE" else money(creditoDisponible)}", color = NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(16.dp))
                    FButton(onClick = { creditAlert = "" }, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("ENTENDIDO") }
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Cobrar · ${money(total)}") },
        text = {
            Column(Modifier.heightIn(max = 520.dp)) {
                Text("Cliente: $customerName", color = Muted)
                if (clienteConCuenta) {
                    Text(
                        if (selected == "MIXTO") "Elegí cuánto queda a crédito y cuánto se cobra con otros medios."
                        else "Esta venta se cargará automáticamente a la cuenta corriente del cliente.",
                        color = if (selected == "MIXTO") Orange else Green,
                        fontWeight = FontWeight.Bold
                    )
                } else if (selected == "CRÉDITO") {
                    Text("El crédito requiere seleccionar un cliente guardado.", color = Orange, fontWeight = FontWeight.Bold)
                }
                if (loading) {
                    Row(Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Red, strokeWidth = 2.dp)
                        Spacer(Modifier.width(10.dp))
                        Text("Consultando estado de caja…", color = Muted)
                    }
                }
                if (requiereCaja && !cajaAbierta) {
                    Text("⚠ Esta combinación incluye efectivo. Primero abrí la caja desde Caja.", color = Orange, fontWeight = FontWeight.Bold)
                }
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    methods.forEach { method ->
                        FilterChip(
                            selected = selected == method,
                            onClick = { selected = method },
                            label = { Text(method) },
                            enabled = (method != "MERCADO PAGO" || mercadoPagoDisponible) && (method != "CRÉDITO" || customerId > 1),
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                    FilterChip(
                        selected = selected == "MIXTO",
                        onClick = {
                            selected = "MIXTO"
                            if (mixed.isEmpty()) mixed = mapOf("CRÉDITO" to total)
                        },
                        label = { Text("MIXTO") },
                        enabled = clienteConCuenta
                    )
                }
                if (selected == "MIXTO") {
                    methods.forEach { method ->
                        if (method != "MERCADO PAGO" || mercadoPagoDisponible) {
                            Field(
                                method,
                                (mixed[method] ?: 0.0).toString().replace('.', ','),
                                onChange = { value ->
                                    mixed = mixed.toMutableMap().apply { put(method, num(value)) }
                                }
                            )
                        }
                    }
                    Text(
                        "Aplicado ${money(mixed.values.sum())} · Restante ${money(total - mixed.values.sum())}",
                        fontWeight = FontWeight.Bold
                    )
                } else if (selected == "CRÉDITO" && clienteConCuenta) {
                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10261A))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("TODO A CRÉDITO", color = Green, fontWeight = FontWeight.Black)
                            Text("Se agregará a la cuenta corriente de $customerName", color = Muted, fontSize = 12.sp)
                            Text(money(total), color = Green, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        }
                    }
                } else {
                    Field("Importe", amount, onChange = { amount = it })
                    if (selected == "EFECTIVO") {
                        Field("Recibido", received, onChange = { received = it })
                    }
                }
                if (selected == "MERCADO PAGO" && !mercadoPagoDisponible) {
                    Text(
                        "Mercado Pago está bloqueado porque no está habilitado en la caja.",
                        color = Orange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !processing && cart.isNotEmpty() && medioValido && (!requiereCaja || cajaAbierta) && (selected != "CRÉDITO" || clienteConCuenta),
                onClick = {
                    processing = true
                    scope.launch {
                        try {
                            val payments = if (selected == "MIXTO") {
                                mixed.filterValues { it > 0.009 }.map { PaymentWrite(it.key, it.value, "") }
                            } else if (selected == "CRÉDITO") {
                                listOf(PaymentWrite("CRÉDITO", total, ""))
                            } else {
                                listOf(PaymentWrite(selected, if (num(amount) > 0) num(amount) else total, ""))
                            }
                            val creditRequested = payments.filter { it.method.equals("CRÉDITO", true) || it.method.equals("CREDITO", true) }.sumOf { it.amount }
                            if (creditRequested > 0.009 && creditLimit > 0.0 && creditBalance + creditRequested > creditLimit + 0.01) {
                                processing = false
                                creditAlert = "El cliente $customerName no tiene suficiente límite disponible para esta operación. No se cargará la venta a crédito."
                                return@launch
                            }
                            val paid = payments.sumOf { it.amount }
                            if (selected == "MIXTO" && kotlin.math.abs(paid - total) > 0.009) {
                                processing = false
                                onMessage("El cobro mixto debe completar exactamente ${money(total)}. Falta ${money(total - paid)}.")
                                return@launch
                            }
                            if (selected != "MIXTO" && paid + 0.009 < total) {
                                processing = false
                                onMessage("Falta cobrar ${money(total - paid)}")
                                return@launch
                            }
                            if (selected == "MERCADO PAGO" && !mercadoPagoDisponible) {
                                processing = false
                                onMessage("Mercado Pago no está habilitado en la caja.")
                                return@launch
                            }
                            val effectiveReceived = if (selected == "EFECTIVO") num(received) else paid
                            val result = if (windowsTicketId != null) {
                                api.createSale(SaleWrite(customerId, cart, payments, effectiveReceived))
                            } else if (tableId != null) {
                                api.chargeTable(tableId, TableChargeWrite(effectiveReceived, payments))
                            } else {
                                api.createSale(
                                    SaleWrite(
                                        customerId = customerId,
                                        items = cart,
                                        payments = payments,
                                        received = effectiveReceived
                                    )
                                )
                            }
                            if (result.isSuccessful) {
                                // En tickets de este teléfono el aviso/sonido de cobro se muestra en este teléfono.
                                // En Android no se lanza Toast ni se desmonta el diálogo en medio del callback:
                                // el cierre de estado se difiere al siguiente ciclo del hilo principal para
                                // evitar que Compose destruya PaymentDialog mientras todavía está ejecutando.
                                processing = false
                                if (windowsTicketId == null) {
                                    try { MiComercioSounds.saleRegistered(context) } catch (_: Exception) {}
                                    val notification = if (tableId != null) "Mesa cobrada correctamente" else "Venta cobrada correctamente"
                                    try { repeatBottomNotification(context, notification) } catch (_: Exception) {}
                                    onMessage(if (tableId != null) "Mesa cobrada correctamente" else "Venta registrada correctamente")
                                }
                                // Estamos dentro de rememberCoroutineScope(), que ejecuta en el
                                // contexto principal de Compose. Ejecutamos el callback una sola vez,
                                // sin publicar otro Runnable sobre el mismo diálogo.
                                try { onPaid() } catch (_: Exception) {}
                            } else {
                                processing = false
                                MiComercioSounds.error()
                                val serverMessage = try { result.errorBody()?.string().orEmpty() } catch (_: Exception) { "" }
                                if (serverMessage.contains("crédito disponible", true) || serverMessage.contains("límite", true) || serverMessage.contains("credito", true)) {
                                    creditAlert = "El cliente $customerName no tiene suficiente límite disponible para esta operación. La venta no se cargó a crédito."
                                }
                                onMessage("Error ${result.code()}: $serverMessage")
                            }
                        } catch (e: Exception) {
                            processing = false
                            MiComercioSounds.error()
                            val errorText = e.message.orEmpty()
                            if (errorText.contains("crédito", true) || errorText.contains("límite", true) || errorText.contains("credito", true)) {
                                creditAlert = "El cliente $customerName no tiene suficiente límite disponible para esta operación. La venta no se cargó a crédito."
                            }
                            onMessage(errorText)
                        }
                    }
                }
            ) { Text("CONFIRMAR COBRO") }
        },
        dismissButton = { TextButton(onClick = onClose) { Text("CANCELAR") } }
    )
}

@Composable
fun Customers(api:MiComercioApi){
    var list by remember{mutableStateOf<List<Customer>>(emptyList())}
    var add by remember{mutableStateOf(false)}
    var edit by remember{mutableStateOf<Customer?>(null)}
    var selected by remember{mutableStateOf<Customer?>(null)}
    var payCustomer by remember{mutableStateOf<Customer?>(null)}
    var account by remember{mutableStateOf<CustomerAccountResponse?>(null)}
    var deleteDialog by remember{mutableStateOf<Customer?>(null)}
    var deleteReason by remember{mutableStateOf("")}
    val scope=rememberCoroutineScope(); val context=LocalContext.current
    fun load(){scope.launch{try{val r=api.customers();if(r.isSuccessful)list=r.body().orEmpty()}catch(_:Exception){}}}
    LaunchedEffect(Unit){load()}
    if(add){CustomerForm(api,null){add=false;load()};return}
    if(edit!=null){CustomerForm(api,edit!!){edit=null;load()};return}
    if(payCustomer!=null){
        PaymentForm(api,payCustomer!!){payCustomer=null;load()}
        return
    }
    if(account!=null){CustomerAccountDialog(api,account!!,onClose={account=null},onPaid={load()},context=context);return}
    Column(Modifier.fillMaxSize().padding(16.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Clientes",fontSize=27.sp,fontWeight=FontWeight.Black,color=Orange);Text("F2 · deuda, detalle, abonos, edición y comprobante",color=Muted)};IconButton(onClick={add=true}){Icon(Icons.Default.PersonAdd,null)}}
        LazyColumn{
            items(list){c->
                ListRow(c.name,"Deuda ${money(c.deuda)} · ${c.phone}",
                    onClick={scope.launch{try{val r=api.customerAccount(c.id);account=if(r.isSuccessful)r.body() else CustomerAccountResponse(c.id,c.name,c.deuda,emptyList())}catch(_:Exception){account=CustomerAccountResponse(c.id,c.name,c.deuda,emptyList())}}},
                    onLongClick={if(c.id!=1)selected=c})
            }
        }
    }
    if(selected!=null){
        val customer=selected!!
        AlertDialog(
            onDismissRequest={selected=null},
            title={Text("CLIENTE · ${customer.name}",fontWeight=FontWeight.Black)},
            text={Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(7.dp)){
                Text("Elegí una de las cinco acciones disponibles para este cliente.",color=Muted,fontSize=12.sp)
                FButton(onClick={edit=customer;selected=null},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.Edit,null);Spacer(Modifier.width(8.dp));Text("1 · EDITAR CLIENTE")}
                FOutlinedButton(onClick={scope.launch{try{val r=api.customerAccount(customer.id);account=if(r.isSuccessful)r.body() else CustomerAccountResponse(customer.id,customer.name,customer.deuda,emptyList());selected=null}catch(_:Exception){account=CustomerAccountResponse(customer.id,customer.name,customer.deuda,emptyList());selected=null}}},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.ReceiptLong,null);Spacer(Modifier.width(8.dp));Text("2 · ESTADO Y DETALLE")}
                FOutlinedButton(enabled=customer.deuda>0.005,onClick={payCustomer=customer;selected=null},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.Payments,null);Spacer(Modifier.width(8.dp));Text("3 · ABONAR DEUDA")}
                FOutlinedButton(onClick={scope.launch{try{val r=api.customerAccount(customer.id);val a=if(r.isSuccessful)r.body()?:CustomerAccountResponse(customer.id,customer.name,customer.deuda,emptyList()) else CustomerAccountResponse(customer.id,customer.name,customer.deuda,emptyList());val shareText=buildString{appendLine("MI COMERCIO POS · COMPROBANTE DE DEUDA");appendLine("Cliente: ${customer.name}");appendLine("Deuda actual: ${money(a.balance)}");appendLine();if(a.details.isEmpty())appendLine("No hay movimientos de cuenta corriente registrados.") else a.details.forEach{d->appendLine("${d.dateTime} · ${if(d.entryType.equals("SALE",true))"DEUDA" else "ABONO"} · ${money(d.amount)} · ${d.paymentMethod} · ${d.concept}");if(d.ticketNumber>0)appendLine("Ticket #${d.ticketNumber}");if(d.products.isNotBlank())appendLine(d.products)}};context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_SUBJECT,"Comprobante de deuda · ${customer.name}");putExtra(Intent.EXTRA_TEXT,shareText)},"Enviar comprobante de deuda"));selected=null}catch(_:Exception){}}},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.Send,null);Spacer(Modifier.width(8.dp));Text("4 · ENVIAR COMPROBANTE")}
                FOutlinedButton(enabled=customer.id!=1,onClick={deleteDialog=customer;selected=null},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.Delete,null);Spacer(Modifier.width(8.dp));Text("5 · ELIMINAR CLIENTE")}
            }},
            confirmButton={},
            dismissButton={TextFButton(onClick={selected=null},modifier=Modifier.fillMaxWidth()){Text("CERRAR")}}
        )
    }
    if(deleteDialog!=null){
        AlertDialog(onDismissRequest={deleteDialog=null},title={Text("Eliminar cliente")},text={Column{Text("Esta acción desactiva el cliente y conserva su historial.",color=Muted);Field("Motivo",deleteReason,onChange={deleteReason=it})}},
            confirmButton={FButton(enabled=deleteReason.isNotBlank(),onClick={val id=deleteDialog!!.id;scope.launch{try{val r=api.deleteCustomer(id,mapOf("reason" to deleteReason.trim()));if(r.isSuccessful){deleteDialog=null;deleteReason="";load()}else deleteReason="Error ${r.code()}"}catch(e:Exception){deleteReason=e.message.orEmpty()}}}){Text("CONFIRMAR ELIMINACIÓN")}},
            dismissButton={TextFButton(onClick={deleteDialog=null}){Text("CANCELAR")}})
    }
}

@Composable fun CustomerForm(api:MiComercioApi,existing:Customer?,onDone:()->Unit){var name by remember{mutableStateOf(existing?.name.orEmpty())};var doc by remember{mutableStateOf(existing?.document.orEmpty())};var phone by remember{mutableStateOf(existing?.phone.orEmpty())};var email by remember{mutableStateOf(existing?.email.orEmpty())};var limit by remember{mutableStateOf(existing?.creditLimit?.toString()?.replace('.',',')?:"0")};var msg by remember{mutableStateOf("")};val scope=rememberCoroutineScope();SimpleForm(if(existing==null)"Nuevo cliente" else "Editar cliente",listOf("Nombre" to name,"Documento" to doc,"Teléfono" to phone,"Email" to email,"Límite" to limit),{i,v->when(i){0->name=v;1->doc=v;2->phone=v;3->email=v;4->limit=v}},{scope.launch{try{val body=CustomerWrite(name,doc,phone,email,"",num(limit));val r=if(existing==null)api.createCustomer(body)else api.updateCustomer(existing.id,body);if(r.isSuccessful)onDone()else msg="Error ${r.code()}: ${r.errorBody()?.string().orEmpty()}"}catch(e:Exception){msg=e.message.orEmpty()}}},onDone,msg)}
@Composable
fun PaymentForm(api: MiComercioApi, c: Customer, onDone: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("EFECTIVO") }
    var mixed by remember { mutableStateOf(mapOf<String,Double>()) }
    var methods by remember { mutableStateOf(listOf("EFECTIVO","TRANSFERENCIA","TARJETA","DÓLARES")) }
    var msg by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { try { val pm=api.paymentMethods(); if(pm.isSuccessful && !pm.body().isNullOrEmpty()) methods=pm.body()!!.filter{it!="MIXTO"}; if(methods.isEmpty()) methods=listOf("EFECTIVO") } catch(_:Exception){} }
    AlertDialog(
        onDismissRequest=onDone,
        title={Text("Abonar cuenta")},
        text={Column(Modifier.heightIn(max=500.dp)){
            Text(c.name,fontWeight=FontWeight.Bold);Text("Deuda actual: ${money(c.deuda)}",color=Muted)
            Row(Modifier.horizontalScroll(rememberScrollState())){methods.forEach{m->FilterChip(selected=method==m,onClick={method=m},label={Text(m)},modifier=Modifier.padding(end=5.dp))};FilterChip(selected=method=="MIXTO",onClick={method="MIXTO"},label={Text("MIXTO")})}
            if(method=="MIXTO"){
                methods.forEach{m->Field(m,(mixed[m]?:0.0).toString().replace('.',','),onChange={v->mixed=mixed.toMutableMap().apply{put(m,num(v))}})}
                Text("Total abonado ${money(mixed.values.sum())}",fontWeight=FontWeight.Bold)
            } else Field("Importe",amount,onChange={amount=it})
            if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80))
        }},
        confirmButton={FButton(onClick={scope.launch{try{
            val payments=if(method=="MIXTO")mixed.filterValues{it>0}.map{PaymentWrite(it.key,it.value,"")}else listOf(PaymentWrite(method,num(amount),""))
            val total=payments.sumOf{it.amount};if(total<=0){msg="Ingresá un importe";return@launch};if(total>c.deuda+0.009){msg="El abono supera la deuda actual";return@launch}
            val r=api.customerPayment(c.id,CustomerPayment(total,method,payments=payments));if(r.isSuccessful){MiComercioSounds.payment(context);onDone()}else msg="Error ${r.code()}: ${r.errorBody()?.string()}"
        }catch(e:Exception){msg=e.message.orEmpty()}}}){Text("REGISTRAR ABONO")}},
        dismissButton={TextFButton(onClick=onDone){Text("CANCELAR")}}
    )
}

@Composable fun Inventory(api:MiComercioApi){Products(api)}
@Composable
fun Cash(api: MiComercioApi) {
    var cash by remember { mutableStateOf<CashStatus?>(null) }
    var arqueo by remember { mutableStateOf<CashArqueo?>(null) }
    var open by remember { mutableStateOf(false) }
    var movement by remember { mutableStateOf(false) }
    var close by remember { mutableStateOf(false) }
    var switchUser by remember { mutableStateOf(false) }
    val scope=rememberCoroutineScope()
    val context=LocalContext.current
    val prefs=remember{Prefs(context)}
    fun load(){scope.launch{try{val r=api.cash();if(r.isSuccessful)cash=r.body();val a=api.arqueo();if(a.isSuccessful)arqueo=a.body()}catch(_:Exception){}}}
    LaunchedEffect(Unit){load()}
    Box(Modifier.fillMaxSize()){
        LazyColumn(
            modifier=Modifier.fillMaxSize().padding(horizontal=16.dp),
            contentPadding=PaddingValues(top=16.dp,bottom=120.dp),
            verticalArrangement=Arrangement.spacedBy(8.dp)
        ){
            item{Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Caja",fontSize=27.sp,fontWeight=FontWeight.Black);Text("F5 · CAJA PRINCIPAL DE WINDOWS · misma caja, mismos movimientos y mismo arqueo",color=Muted)};IconButton(onClick={load()}){Icon(Icons.Default.Refresh,null)}}}
            arqueo?.let{a->item{Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=Panel),border=BorderStroke(1.dp,MaterialTheme.colorScheme.primary.copy(alpha=.18f))){Column(Modifier.padding(16.dp)){
                Text(a.estado,fontWeight=FontWeight.Black,color=if(a.hayTurno)Green else Orange);Text("Sesión #${a.sesion} · Cajero: ${a.cajero}",color=Muted);Text("Apertura: ${a.aperturaAt}",color=Muted)
                Spacer(Modifier.height(8.dp));Text("EFECTIVO",fontWeight=FontWeight.Black,color=Orange);Text("Inicial ${money(a.efectivoInicial)}");Text("Ventas ${money(a.efectivoVentas)}");Text("Ingresos ${money(a.efectivoIngresos)}");Text("Egresos ${money(a.efectivoEgresos)}");Text("Esperado ${money(a.efectivoEsperado)}",fontWeight=FontWeight.Bold,color=Orange)
                if(a.mercadoPagoHabilitado){Spacer(Modifier.height(8.dp));Text("MERCADO PAGO",fontWeight=FontWeight.Black,color=Green);Text("Inicial ${money(a.mercadoPagoInicial)}");Text("Ventas ${money(a.mercadoPagoVentas)}");Text("Ingresos ${money(a.mercadoPagoIngresos)}");Text("Egresos ${money(a.mercadoPagoEgresos)}");Text("Retención ${money(a.mercadoPagoRetencion)} (${a.mercadoPagoRetencionPorcentaje}%)");Text("Esperado ${money(a.mercadoPagoEsperado)}",fontWeight=FontWeight.Bold,color=Green)}
                Spacer(Modifier.height(8.dp));Text("TOTAL ESPERADO ${money(a.totalEsperado)}",fontSize=21.sp,fontWeight=FontWeight.Black)
            }}}}
            cash?.let{c->item{Text("Estado: ${c.estado} · Fondo inicial ${money(c.apertura)}",color=Muted,modifier=Modifier.padding(vertical=4.dp))}}
            item{FButton(enabled=!(arqueo?.hayTurno==true),onClick={open=true},modifier=Modifier.fillMaxWidth()){Text("ABRIR CAJA")}}
            item{FOutlinedButton(onClick={movement=true},modifier=Modifier.fillMaxWidth()){Text("INGRESO / EGRESO")}}
            item{FOutlinedButton(enabled=arqueo?.hayTurno==true,onClick={close=true},modifier=Modifier.fillMaxWidth()){Text("CERRAR CAJA · ENVIAR REPORTE")}}
            item{
                FOutlinedButton(enabled=!(arqueo?.hayTurno==true),onClick={switchUser=true},modifier=Modifier.fillMaxWidth()){
                    Icon(Icons.Default.SwitchAccount,null);Spacer(Modifier.width(8.dp));Text("CAMBIAR USUARIO")
                }
                if(arqueo?.hayTurno==true) Text("Para cambiar de usuario primero debe estar cerrada la caja.",color=Orange,fontSize=11.sp,modifier=Modifier.padding(horizontal=8.dp))
            }
        }
        if(open)CashOpenForm(api,prefs){open=false;load()}
        if(movement)CashMovementForm(api){movement=false;load()}
        if(close)CashCloseForm(api,prefs){close=false;load()}
        if(switchUser)UserSwitchDialog(api,onDone={switchUser=false;load()})
    }
}
@Composable
fun UserSwitchDialog(api:MiComercioApi,onDone:()->Unit){
    var users by remember{mutableStateOf<List<MobileUser>>(emptyList())}
    var selected by remember{mutableStateOf<MobileUser?>(null)}
    var password by remember{mutableStateOf("")}
    var msg by remember{mutableStateOf("")}
    var loading by remember{mutableStateOf(true)}
    var cashOpen by remember{mutableStateOf(false)}
    var expanded by remember{mutableStateOf(false)}
    val scope=rememberCoroutineScope()

    LaunchedEffect(Unit){
        try{
            val usersResponse=api.mobileUsers()
            if(usersResponse.isSuccessful) users=usersResponse.body().orEmpty()
            else msg="No se pudieron cargar los usuarios (${usersResponse.code()})"
            val cashResponse=api.cash()
            if(cashResponse.isSuccessful) cashOpen=cashResponse.body()?.estado?.let { it.equals("OPEN",true) || it.equals("ABIERTA",true) }==true
        }catch(e:Exception){msg=e.message.orEmpty()}
        finally{loading=false}
    }

    AlertDialog(
        onDismissRequest=onDone,
        title={Text("CAMBIAR USUARIO",fontWeight=FontWeight.Black)},
        text={Column{
            Text("La caja debe estar completamente cerrada para cambiar de usuario.",color=Muted,fontSize=12.sp)
            Spacer(Modifier.height(10.dp))
            if(loading){
                Row(verticalAlignment=Alignment.CenterVertically){CircularProgressIndicator(modifier=Modifier.size(22.dp),color=Red);Spacer(Modifier.width(10.dp));Text("Cargando usuarios…",color=Muted)}
            }else{
                Box(Modifier.fillMaxWidth()){
                    FOutlinedButton(onClick={expanded=true},modifier=Modifier.fillMaxWidth().height(52.dp)){
                        Icon(Icons.Default.Person,null)
                        Spacer(Modifier.width(8.dp))
                        Text(selected?.let{if(it.fullName.isBlank())it.username else "${it.fullName} · ${it.username}"} ?: "SELECCIONAR USUARIO",modifier=Modifier.weight(1f))
                        Icon(Icons.Default.ExpandMore,null)
                    }
                    DropdownMenu(expanded=expanded,onDismissRequest={expanded=false},modifier=Modifier.fillMaxWidth(.90f)){
                        users.forEach{u->DropdownMenuItem(
                            text={Text(if(u.fullName.isBlank())u.username else "${u.fullName} · ${u.username}")},
                            onClick={selected=u;expanded=false}
                        )}
                    }
                }
                if(selected!=null){
                    Spacer(Modifier.height(8.dp))
                    Field("Contraseña del usuario o contraseña maestra de este teléfono",password,onChange={password=it})
                }
                if(cashOpen) Text("La caja sigue abierta. Cerrá la caja desde Caja y volvé a intentar.",color=Orange,fontWeight=FontWeight.Bold,fontSize=12.sp,modifier=Modifier.padding(top=6.dp))
            }
            if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80),fontSize=12.sp,modifier=Modifier.padding(top=6.dp))
        }},
        confirmButton={FButton(
            enabled=selected!=null&&password.isNotBlank()&&!loading&&!cashOpen,
            onClick={scope.launch{
                try{
                    val r=api.switchMobileUser(MobileUserSwitch(selected!!.id,password.trim()))
                    if(r.isSuccessful){
                        val switchedName = selected?.let { if(it.fullName.isBlank()) it.username else "${it.fullName} · ${it.username}" } ?: "usuario"
                        msg="✓ Usuario activo: $switchedName"
                        // La confirmación queda visible antes de cerrar el diálogo.
                        kotlinx.coroutines.delay(650)
                        onDone()
                    }else{
                        msg=try{r.errorBody()?.string().orEmpty().ifBlank{"No se pudo cambiar el usuario"}}catch(_:Exception){"No se pudo cambiar el usuario"}
                    }
                }catch(e:Exception){msg=e.message.orEmpty()}
            }}
        ){Text("INGRESAR")}},
        dismissButton={TextFButton(onClick=onDone){Text("CANCELAR")}}
    )
}

@Composable
fun CashOpenForm(api: MiComercioApi, prefs: Prefs, onDone: () -> Unit) {
    val context = LocalContext.current
    var amount by remember { mutableStateOf("") }
    var mp by remember { mutableStateOf(false) }
    var mpOpening by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    var askEmail by remember { mutableStateOf(false) }
    var reportText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    if (askEmail) {
        val email by produceState(initialValue = "", key1 = askEmail) { value = prefs.email() }
        AlertDialog(
            onDismissRequest = { askEmail = false; onDone() },
            title = { Text("Caja abierta correctamente") },
            text = {
                Column {
                    Text("La caja ya fue abierta. ¿Querés enviar ahora el detalle de apertura por email?", color = Muted)
                    Spacer(Modifier.height(10.dp))
                    Text("Destino: ${email.ifBlank { "No configurado" }}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text(reportText, fontSize = 12.sp, color = Muted)
                }
            },
            confirmButton = {
                FButton(
                    enabled = email.isNotBlank(),
                    onClick = {
                        sendReportEmail(context, email, "Apertura de caja · Mi Comercio", reportText)
                        askEmail = false
                        onDone()
                    }
                ) { Text("SÍ, ENVIAR") }
            },
            dismissButton = {
                TextFButton(onClick = { askEmail = false; onDone() }) { Text("NO ENVIAR") }
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDone,
        title = { Text("Abrir caja") },
        text = {
            Column {
                Field("Fondo inicial en efectivo", amount, onChange = { amount = it })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = mp, onCheckedChange = { mp = it })
                    Text("Habilitar Mercado Pago")
                }
                if (mp) Field("Saldo inicial Mercado Pago", mpOpening, onChange = { mpOpening = it })
                Spacer(Modifier.height(6.dp))
                Text("Primero cargá los montos. Después de abrir la caja te voy a preguntar si querés mandar el comprobante por email.", color = Muted, fontSize = 12.sp)
                if (msg.isNotBlank()) Text(msg, color = Color(0xFFFF8A80))
            }
        },
        confirmButton = {
            FButton(onClick = {
                scope.launch {
                    try {
                        val r = api.openCash(CashOpen(num(amount), mp, num(mpOpening)))
                        if (r.isSuccessful) {
                            MiComercioSounds.cashOpen(context)
                            val a = api.arqueo().body()
                            reportText = buildString {
                                appendLine("APERTURA DE CAJA · MI COMERCIO")
                                appendLine("Fecha: ${a?.aperturaAt.orEmpty()}")
                                appendLine("Cajero: ${a?.cajero.orEmpty()}")
                                appendLine("Efectivo inicial: ${money(a?.efectivoInicial ?: num(amount))}")
                                appendLine("Mercado Pago inicial: ${money(a?.mercadoPagoInicial ?: if (mp) num(mpOpening) else 0.0)}")
                                appendLine()
                                appendLine("La caja quedó abierta correctamente.")
                            }
                            askEmail = true
                        } else {
                            MiComercioSounds.error()
                            msg = "Error ${r.code()}: ${r.errorBody()?.string().orEmpty()}"
                        }
                    } catch (e: Exception) {
                        MiComercioSounds.error()
                        msg = e.message.orEmpty()
                    }
                }
            }) { Text("ABRIR CAJA") }
        },
        dismissButton = { TextFButton(onClick = onDone) { Text("CANCELAR") } }
    )
}

@Composable fun CashMovementForm(api:MiComercioApi,onDone:()->Unit){val context=LocalContext.current;var amount by remember{mutableStateOf("")};var concept by remember{mutableStateOf("")};var type by remember{mutableStateOf("INCOME")};var method by remember{mutableStateOf("EFECTIVO")};var methods by remember{mutableStateOf(listOf("EFECTIVO","TRANSFERENCIA","TARJETA","DÓLARES"))};var expanded by remember{mutableStateOf(false)};var msg by remember{mutableStateOf("")};val scope=rememberCoroutineScope();LaunchedEffect(Unit){try{val r=api.paymentMethods();if(r.isSuccessful)methods=r.body().orEmpty().ifEmpty{methods}}catch(_:Exception){}};AlertDialog(onDismissRequest=onDone,title={Text("Movimiento de caja",fontWeight=FontWeight.Black)},text={Column{Row{FilterChip(selected=type=="INCOME",onClick={type="INCOME"},label={Text("INGRESO")});Spacer(Modifier.width(8.dp));FilterChip(selected=type=="EXPENSE",onClick={type="EXPENSE"},label={Text("EGRESO")})};Field("Concepto",concept,onChange={concept=it});Field("Importe",amount,onChange={amount=it});Box{FOutlinedButton(onClick={expanded=true},modifier=Modifier.fillMaxWidth()){Text("MEDIO DE PAGO · $method")};DropdownMenu(expanded=expanded,onDismissRequest={expanded=false}){methods.distinct().forEach{m->DropdownMenuItem(text={Text(m)},onClick={method=m;expanded=false})}}};if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80))}},confirmButton={FButton(onClick={scope.launch{try{val r=api.cashMovement(CashMovement(type,concept,num(amount),method));if(r.isSuccessful){if(type=="INCOME") MiComercioSounds.income(context) else MiComercioSounds.expense(context);onDone()}else msg="Error ${r.code()}: ${r.errorBody()?.string()}"}catch(e:Exception){msg=e.message.orEmpty()}}}){Text("GUARDAR")}},dismissButton={TextFButton(onClick=onDone){Text("CANCELAR")}})}
@Composable fun CashCloseForm(api:MiComercioApi,prefs:Prefs,onDone:()->Unit){
    val context=LocalContext.current
    var amount by remember{mutableStateOf("")};var mpAmount by remember{mutableStateOf("")};var cash by remember{mutableStateOf<CashStatus?>(null)};var configuredEmail by remember{mutableStateOf("")};var msg by remember{mutableStateOf("")};var askEmail by remember{mutableStateOf(false)};var reportText by remember{mutableStateOf("")};val scope=rememberCoroutineScope()
    LaunchedEffect(Unit){try{val r=api.cash();if(r.isSuccessful)cash=r.body()}catch(_:Exception){};try{configuredEmail=prefs.email()}catch(_:Exception){}}
    if(askEmail){AlertDialog(onDismissRequest={askEmail=false;onDone()},title={Text("Enviar reporte por email")},text={Text("¿Querés enviar el reporte final de cierre al correo ${configuredEmail.ifBlank{"configurado"}}?",color=Muted)},confirmButton={FButton(onClick={scope.launch{try{if(configuredEmail.isNotBlank())sendReportEmail(context,configuredEmail,"Cierre de caja · Mi Comercio",reportText)}catch(e:Exception){msg=e.message.orEmpty()};askEmail=false;onDone()}}){Text("SÍ, ENVIAR")}},dismissButton={TextFButton(onClick={askEmail=false;onDone()}){Text("NO ENVIAR")}});return}
    AlertDialog(onDismissRequest=onDone,title={Text("Cerrar caja")},text={Column{Field("Efectivo contado",amount,onChange={amount=it});if(cash?.mercadoPagoEnabled==true)Field("Mercado Pago contado",mpAmount,onChange={mpAmount=it});Text("Al cerrar se te preguntará si querés enviar el reporte final al email configurado.",color=Muted);if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80))}},confirmButton={FButton(onClick={scope.launch{try{val before=api.arqueo().body();val r=api.closeCash(CashClose(num(amount),countedMercadoPago=if(cash?.mercadoPagoEnabled==true)num(mpAmount) else null));if(r.isSuccessful){MiComercioSounds.cashClose(context);reportText=buildString{appendLine("CIERRE DE CAJA · MI COMERCIO");appendLine();appendLine("Cajero: ${before?.cajero.orEmpty()}");appendLine("Apertura: ${before?.aperturaAt.orEmpty()}");appendLine("Efectivo inicial: ${money(before?.efectivoInicial?:0.0)}");appendLine("Ventas en efectivo: ${money(before?.efectivoVentas?:0.0)}");appendLine("Ingresos: ${money(before?.efectivoIngresos?:0.0)}");appendLine("Egresos: ${money(before?.efectivoEgresos?:0.0)}");appendLine("Efectivo esperado: ${money(before?.efectivoEsperado?:0.0)}");appendLine("Efectivo contado: ${money(num(amount))}");if(cash?.mercadoPagoEnabled==true){appendLine();appendLine("Mercado Pago inicial: ${money(before?.mercadoPagoInicial?:0.0)}");appendLine("Mercado Pago ventas: ${money(before?.mercadoPagoVentas?:0.0)}");appendLine("Mercado Pago esperado: ${money(before?.mercadoPagoEsperado?:0.0)}");appendLine("Mercado Pago contado: ${money(num(mpAmount))}")};appendLine();appendLine("La caja fue cerrada correctamente.")};askEmail=true}else{MiComercioSounds.error();msg="Error ${r.code()}: ${r.errorBody()?.string()}"}}catch(e:Exception){MiComercioSounds.error();msg=e.message.orEmpty()}}}){Text("CERRAR CAJA")}},dismissButton={TextFButton(onClick=onDone){Text("CANCELAR")}})
}
@Composable
fun TablePicker(api: MiComercioApi, cart: List<SaleLineWrite>, customerId: Int, customerName: String, onSaved: () -> Unit) {
    var tables by remember { mutableStateOf<List<TableInfo>>(emptyList()) }
    var openTickets by remember { mutableStateOf<List<OpenTicket>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var msg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            loading = true
            try {
                val r = api.tables()
                val o = api.openTickets()
                if (r.isSuccessful) tables = r.body().orEmpty()
                else msg = "No se pudieron cargar las mesas (${r.code()})"
                if (o.isSuccessful) openTickets = o.body().orEmpty()
            } catch (e: Exception) {
                msg = e.message.orEmpty()
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    AlertDialog(
        onDismissRequest = onSaved,
        title = { Text("Guardar venta en mesa") },
        text = {
            Column {
                Text("Seleccioná una mesa. Si ya está ocupada, se agregan los nuevos productos al ticket existente y queda guardada localmente.", color = Muted)
                Spacer(Modifier.height(10.dp))
                if (loading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Red)
                        Spacer(Modifier.width(10.dp))
                        Text("Cargando mesas…", color = Muted)
                    }
                } else if (tables.isEmpty()) {
                    Text("No hay mesas disponibles.", color = Orange, fontWeight = FontWeight.Bold)
                } else {
                    LazyColumn(Modifier.heightIn(max = 430.dp)) {
                        items(tables) { t ->
                            val occupied = t.occupied
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        scope.launch {
                                            try {
                                                var ticket = openTickets.find { it.tableId == t.id }
                                                if (occupied && ticket == null) {
                                                    // La mesa puede estar ocupada por este teléfono. Refrescamos antes de rechazarla.
                                                    val fresh = api.openTickets()
                                                    ticket = fresh.body().orEmpty().find { it.tableId == t.id }
                                                    if (ticket == null) {
                                                        msg = "La mesa está ocupada, pero todavía no llegó su ticket sincronizado. Actualizá e intentá nuevamente."
                                                        return@launch
                                                    }
                                                }
                                                val base = ticket ?: OpenTicket(
                                                    tableId = t.id,
                                                    tableName = t.name,
                                                    customerId = customerId,
                                                    customerName = customerName,
                                                    items = emptyList()
                                                )
                                                if (cart.isEmpty()) {
                                                    msg = "No hay productos para agregar a la mesa."
                                                    return@launch
                                                }
                                                // No reemplazamos el ticket existente: acumulamos los nuevos productos.
                                                val combined = base.items + cart
                                                val body = base.copy(
                                                    tableId = t.id,
                                                    tableName = t.name,
                                                    items = combined
                                                )
                                                val r = if (ticket != null) api.appendOpenTicket(t.id, body.copy(items = cart)) else api.saveOpenTicket(body)
                                                if (r.isSuccessful) {
                                                    MiComercioSounds.tap(true)
                                                    onSaved()
                                                } else {
                                                    msg = "No se pudo actualizar la mesa (${r.code()}): ${r.errorBody()?.string().orEmpty()}"
                                                }
                                            } catch (e: Exception) {
                                                msg = e.message.orEmpty()
                                            }
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (occupied) Color(0xFF332019) else Panel
                                )
                            ) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TableBar,
                                        contentDescription = null,
                                        tint = if (occupied) Orange else Green
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(t.name, fontWeight = FontWeight.Bold)
                                        Text(
                                            if (occupied) "OCUPADA · TOCAR PARA AGREGAR" else "LIBRE · ${t.capacity} personas",
                                            color = if (occupied) Orange else Green,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (msg.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(msg, color = Color(0xFFFF8A80))
                }
            }
        },
        confirmButton = { TextFButton(onClick = onSaved) { Text("CANCELAR") } }
    )
}

@Composable fun Tables(api:MiComercioApi){
    var salons by remember{mutableStateOf<List<SalonInfo>>(emptyList())}
    var selectedSalonId by remember{mutableStateOf<Int?>(null)}
    var tables by remember{mutableStateOf<List<TableInfo>>(emptyList())}
    var open by remember{mutableStateOf<List<OpenTicket>>(emptyList())}
    var charge by remember{mutableStateOf<Pair<TableInfo,OpenTicket>?>(null)}
    var salonMenu by remember{mutableStateOf(false)}
    var loading by remember{mutableStateOf(true)}
    var msg by remember{mutableStateOf("")}
    val scope=rememberCoroutineScope()

    fun loadTables(salonId:Int?=selectedSalonId){
        scope.launch{
            loading=true
            try{
                // El Manager no depende del plano de este teléfono. Pedimos directamente
                // las mesas del salón activo y las mostramos como listado.
                val a=api.tables(salonId)
                if(a.isSuccessful){
                    tables=a.body().orEmpty()
                    msg=""
                }else{
                    msg="No se pudieron cargar las mesas (${a.code()})"
                }
                val b=api.openTickets()
                if(b.isSuccessful) open=b.body().orEmpty()
                else if(msg.isBlank()) msg="No se pudieron cargar los tickets (${b.code()})"
            }catch(e:Exception){
                msg="No se pudieron cargar las mesas: ${e.message.orEmpty()}"
            }finally{loading=false}
        }
    }

    LaunchedEffect(Unit){
        scope.launch{
            try{
                val sr=api.salons()
                if(sr.isSuccessful) salons=sr.body().orEmpty()
            }catch(_:Exception){}
            // IMPORTANTE: primero consultamos sin forzar un salón. este teléfono devuelve
            // su salón activo en ese caso. Así no queda la pantalla negra si el
            // selector todavía no terminó de cargar.
            loadTables(null)
        }
    }
    LaunchedEffect(selectedSalonId){
        if(selectedSalonId!=null) loadTables(selectedSalonId)
    }
    LaunchedEffect(selectedSalonId){
        while(true){delay(4000);loadTables(selectedSalonId)}
    }

    if(charge!=null){
        TableChargeDialog(api,charge!!.first,charge!!.second,onDone={charge=null;loadTables(selectedSalonId)})
        return
    }

    // Vista deliberadamente simple: listado vertical, ordenado por número de mesa.
    // Conservamos solamente el dibujo 3D de cada mesa, no el plano/espaciado de este teléfono.
    val orderedTables=remember(tables){
        tables.sortedWith(compareBy<TableInfo>{
            Regex("\\d+").find(it.name)?.value?.toIntOrNull() ?: Int.MAX_VALUE
        }.thenBy{it.name.lowercase(Locale.getDefault())})
    }

    Column(Modifier.fillMaxSize().padding(horizontal=12.dp,vertical=10.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            Column(Modifier.weight(1f)){
                Text("Mesas y tickets",fontSize=27.sp,fontWeight=FontWeight.Black)
                Text("Ver y cobrar mesas · ordenadas por número",color=Muted,fontSize=12.sp)
            }
            if(salons.size>1){
                Box{
                    FOutlinedButton(onClick={salonMenu=true}){
                        Text(salons.firstOrNull{it.id==selectedSalonId}?.name ?: "Salón activo")
                    }
                    DropdownMenu(expanded=salonMenu,onDismissRequest={salonMenu=false}){
                        salons.forEach{ss->
                            DropdownMenuItem(
                                text={Text(ss.name)},
                                onClick={selectedSalonId=ss.id;salonMenu=false}
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        if(loading && orderedTables.isEmpty()){
            Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){
                Column(horizontalAlignment=Alignment.CenterHorizontally){
                    CircularProgressIndicator(modifier=Modifier.size(28.dp),strokeWidth=3.dp,color=Red)
                    Spacer(Modifier.height(10.dp))
                    Text("Cargando mesas…",color=Muted)
                }
            }
        }else if(orderedTables.isEmpty()){
            Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){
                Column(horizontalAlignment=Alignment.CenterHorizontally){
                    Text("No hay mesas para mostrar",fontSize=18.sp,fontWeight=FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("No hay mesas activas en el salón seleccionado.",color=Muted,textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                    if(msg.isNotBlank()){
                        Spacer(Modifier.height(8.dp))
                        Text(msg,color=Color(0xFFFF8A80),textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                    }
                    Spacer(Modifier.height(12.dp))
                    FOutlinedButton(onClick={loadTables(selectedSalonId)}){Text("ACTUALIZAR")}
                }
            }
        }else{
            LazyColumn(
                modifier=Modifier.fillMaxSize(),
                verticalArrangement=Arrangement.spacedBy(10.dp),
                contentPadding=PaddingValues(bottom=20.dp)
            ){
                items(orderedTables,key={it.id}){t->
                    val ticket=open.find{it.tableId==t.id}
                    val occupied=t.occupied && ticket!=null
                    Card(
                        modifier=Modifier
                            .fillMaxWidth()
                            .clickable(enabled=occupied){charge=t to ticket!!},
                        shape=RoundedCornerShape(18.dp),
                        colors=CardDefaults.cardColors(
                            containerColor=if(occupied) Color(0xFF241518) else Color(0xFF0E1217)
                        ),
                        elevation=CardDefaults.cardElevation(defaultElevation=3.dp)
                    ){
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal=10.dp,vertical=8.dp),
                            verticalAlignment=Alignment.CenterVertically
                        ){
                            // Imagen 3D independiente del plano. Nunca queda en un
                            // canvas negro gigante ni depende de X/Y de este teléfono.
                            Box(Modifier.width(150.dp).height(112.dp),contentAlignment=Alignment.Center){
                                Table3DVisual(t,Modifier.fillMaxSize())
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)){
                                Text(t.name,fontSize=21.sp,fontWeight=FontWeight.Black,color=Color.White)
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    if(occupied) "TICKET ABIERTO · TOCAR PARA COBRAR" else "MESA LIBRE · ${t.capacity} PERSONAS",
                                    fontSize=11.sp,fontWeight=FontWeight.Bold,
                                    color=if(occupied) Color(0xFFFF365A) else Color(0xFF39FFFF)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    if(occupied) "Cliente: ${ticket?.customerName.orEmpty()}" else "Disponible para agregar una venta desde Ventas",
                                    fontSize=11.sp,color=Muted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
private fun table3dResource(shape: String, occupied: Boolean): Int = when (shape.uppercase(Locale.getDefault())) {
    "ROUND", "SQUARE" -> if (occupied) R.drawable.mesa_3d_cuadrada_roja else R.drawable.mesa_3d_cuadrada
    "OVAL" -> if (occupied) R.drawable.mesa_3d_ovalada_roja else R.drawable.mesa_3d_ovalada
    else -> if (occupied) R.drawable.mesa_3d_rectangular_roja else R.drawable.mesa_3d_rectangular
}

@Composable
private fun Table3DVisual(t:TableInfo, modifier:Modifier=Modifier){
    val occupied=t.occupied
    Box(modifier, contentAlignment=Alignment.Center){
        Image(
            painter=painterResource(table3dResource(t.shape,occupied)),
            contentDescription=t.name,
            modifier=Modifier.fillMaxWidth().aspectRatio(1.4f),
            contentScale=ContentScale.Fit
        )
        Column(horizontalAlignment=Alignment.CenterHorizontally, modifier=Modifier.padding(horizontal=28.dp)){
            Text(t.name,fontSize=17.sp,fontWeight=FontWeight.Black,color=Color.White)
            Text(
                if(occupied) "OCUPADA" else "LIBRE · ${t.capacity} personas",
                fontSize=10.sp,fontWeight=FontWeight.Bold,
                color=if(occupied) Color(0xFFFF365A) else Color(0xFF39FFFF)
            )
        }
    }
}

@Composable fun TableChargeDialog(api:MiComercioApi,t:TableInfo,ticket:OpenTicket,onDone:()->Unit){var pay by remember{mutableStateOf(false)};var msg by remember{mutableStateOf("")};val scope=rememberCoroutineScope();if(pay){PaymentDialog(api,ticket.items,ticket.customerId,ticket.customerName,onClose={pay=false},onMessage={msg=it},onPaid={onDone()},tableId=t.id);return};AlertDialog(onDismissRequest=onDone,title={Text("Cobrar ${t.name}")},text={Column{Text("Cliente: ${ticket.customerName}",fontWeight=FontWeight.Bold);ticket.items.forEach{Text("${fmt(it.quantity)} × ${it.description} · ${money(it.quantity*it.unitPrice-it.discount)}")};Spacer(Modifier.height(8.dp));Text("TOTAL ${money(ticket.items.sumOf{it.quantity*it.unitPrice-it.discount})}",fontSize=21.sp,fontWeight=FontWeight.Black);if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80))}},confirmButton={FButton(onClick={pay=true}){Text("COBRAR MESA")}},dismissButton={TextFButton(onClick=onDone){Text("CERRAR")}})}

@Composable fun Suppliers(api:MiComercioApi){
    var list by remember{mutableStateOf<List<Supplier>>(emptyList())};var add by remember{mutableStateOf(false)};var edit by remember{mutableStateOf<Supplier?>(null)};var remove by remember{mutableStateOf<Supplier?>(null)};var reason by remember{mutableStateOf("")};val scope=rememberCoroutineScope();var msg by remember{mutableStateOf("")}
    fun load(){scope.launch{try{val r=api.suppliers();if(r.isSuccessful)list=r.body().orEmpty() else msg="Error ${r.code()}"}catch(e:Exception){msg=e.message.orEmpty()}}};LaunchedEffect(Unit){load()}
    if(add){SimpleSupplierForm(api,null){add=false;load()};return};if(edit!=null){SimpleSupplierForm(api,edit!!){edit=null;load()};return}
    Column(Modifier.fillMaxSize().padding(16.dp)){Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Proveedores",fontSize=27.sp,fontWeight=FontWeight.Black);Text("Alta · edición · consulta y baja sincronizada",color=Muted)};IconButton(onClick={add=true}){Icon(Icons.Default.AddBusiness,null)}};LazyColumn{items(list){s->ListRow(s.name,"${s.phone} · ${s.email} · ${s.address}",onClick={edit=s},onLongClick={remove=s})}};if(msg.isNotBlank())Text(msg,color=Orange)}
    if(remove!=null){AlertDialog(onDismissRequest={remove=null},title={Text("Eliminar proveedor")},text={Column{Text("Se desactivará y se conservará el historial de compras.",color=Muted);Field("Motivo",reason,onChange={reason=it})}},confirmButton={FButton(enabled=reason.isNotBlank(),onClick={val id=remove!!.id;scope.launch{try{val r=api.deleteSupplier(id,mapOf("reason" to reason.trim()));if(r.isSuccessful){remove=null;reason="";load()}else msg="Error ${r.code()}"}catch(e:Exception){msg=e.message.orEmpty()}}}){Text("CONFIRMAR")}},dismissButton={TextFButton(onClick={remove=null}){Text("CANCELAR")}})}
}
@Composable fun SimpleSupplierForm(api:MiComercioApi,existing:Supplier?=null,onDone:()->Unit){var name by remember{mutableStateOf(existing?.name.orEmpty())};var doc by remember{mutableStateOf(existing?.document.orEmpty())};var phone by remember{mutableStateOf(existing?.phone.orEmpty())};var email by remember{mutableStateOf(existing?.email.orEmpty())};var address by remember{mutableStateOf(existing?.address.orEmpty())};var msg by remember{mutableStateOf("")};val scope=rememberCoroutineScope();SimpleForm(if(existing==null)"Nuevo proveedor" else "Editar proveedor",listOf("Nombre" to name,"Documento" to doc,"Teléfono" to phone,"Email" to email,"Dirección" to address),{i,v->when(i){0->name=v;1->doc=v;2->phone=v;3->email=v;4->address=v}},{scope.launch{try{val body=SupplierWrite(name,doc,phone,email,address);val r=if(existing==null)api.createSupplier(body)else api.updateSupplier(existing.id,body);if(r.isSuccessful)onDone()else msg="Error ${r.code()}: ${r.errorBody()?.string().orEmpty()}"}catch(e:Exception){msg=e.message.orEmpty()}}},onDone,msg)}

@Composable fun SelectorDialog(title:String,values:List<String>,onSelect:(Int)->Unit,onClose:()->Unit){
    var query by remember { mutableStateOf("") }
    val filtered=remember(query,values){
        val q=query.trim().lowercase(Locale.getDefault())
        values.mapIndexed{index,value->index to value}.filter{q.isBlank() || it.second.lowercase(Locale.getDefault()).contains(q)}
    }
    AlertDialog(
        onDismissRequest=onClose,
        title={
            Row(verticalAlignment=Alignment.CenterVertically){
                Column(Modifier.weight(1f)){
                    Text(title,fontSize=24.sp,fontWeight=FontWeight.Black)
                    Text("Seleccioná un proveedor",fontSize=12.sp,color=Muted)
                }
                IconButton(onClick=onClose){Icon(Icons.Default.Close,"Cerrar")}
            }
        },
        text={
            Column(Modifier.fillMaxWidth()){
                OutlinedTextField(
                    value=query,
                    onValueChange={query=it},
                    singleLine=true,
                    modifier=Modifier.fillMaxWidth(),
                    label={Text("Buscar proveedor")},
                    leadingIcon={Icon(Icons.Default.Search,null)}
                )
                Spacer(Modifier.height(10.dp))
                if(values.isEmpty()){
                    Text("No hay proveedores guardados.",color=Orange,modifier=Modifier.padding(vertical=12.dp))
                }else if(filtered.isEmpty()){
                    Text("No se encontró un proveedor con ese nombre.",color=Muted,modifier=Modifier.padding(vertical=12.dp))
                }else{
                    LazyColumn(
                        Modifier.fillMaxWidth().heightIn(max=420.dp),
                        verticalArrangement=Arrangement.spacedBy(8.dp)
                    ){
                        items(filtered,key={it.first}){item->
                            val index=item.first
                            val name=item.second
                            Button(
                                onClick={onSelect(index)},
                                modifier=Modifier.fillMaxWidth().height(62.dp),
                                shape=RoundedCornerShape(16.dp),
                                colors=ButtonDefaults.buttonColors(
                                    containerColor=MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor=MaterialTheme.colorScheme.onSurface
                                )
                            ){
                                Icon(Icons.Default.Business,null,tint=MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(12.dp))
                                Text(name,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f))
                                Icon(Icons.Default.ChevronRight,null,tint=MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton={TextButton(onClick=onClose){Text("CANCELAR")}}
    )
}

data class PurchaseDraftLine(val product:Product,val quantity:Double,val unitCost:Double)

@Composable fun PurchaseProductDialog(api:MiComercioApi,supplier:Supplier,onSelected:(Product)->Unit,onClose:()->Unit){
    var q by remember{mutableStateOf("")}
    var list by remember{mutableStateOf<List<SupplierProduct>>(emptyList())}
    var msg by remember{mutableStateOf("")}
    val scope=rememberCoroutineScope()
    fun load(text:String){
        scope.launch{
            try{
                val r=api.supplierProducts(supplier.id,text)
                if(r.isSuccessful)list=r.body().orEmpty() else msg="Error ${r.code()}"
            }catch(e:Exception){msg=e.message.orEmpty()}
        }
    }
    LaunchedEffect(Unit){load("")}
    AlertDialog(
        onDismissRequest=onClose,
        title={
            Column {
                Text("Elegir producto",fontSize=24.sp,fontWeight=FontWeight.Black)
                Text("Proveedor: ${supplier.name}",fontSize=12.sp,color=Muted)
            }
        },
        text={
            Column {
                OutlinedTextField(
                    value=q,
                    onValueChange={q=it;load(it)},
                    singleLine=true,
                    modifier=Modifier.fillMaxWidth(),
                    label={Text("Buscar producto o código")},
                    leadingIcon={Icon(Icons.Default.Search,null)}
                )
                Spacer(Modifier.height(10.dp))
                if(list.isEmpty()) {
                    Text(
                        if(q.isBlank())"No hay productos disponibles para este proveedor." else "No se encontró ese producto.",
                        color=Muted,
                        modifier=Modifier.padding(8.dp)
                    )
                } else {
                    LazyColumn(
                        Modifier.fillMaxWidth().heightIn(max=500.dp),
                        verticalArrangement=Arrangement.spacedBy(8.dp)
                    ) {
                        items(list,key={it.id}){x->
                            Button(
                                onClick={
                                    onSelected(Product(id=x.id,description=x.description,barcode=x.barcode,stock=x.stock,costPrice=x.costPrice))
                                },
                                modifier=Modifier.fillMaxWidth().heightIn(min=64.dp),
                                shape=RoundedCornerShape(14.dp),
                                colors=ButtonDefaults.buttonColors(
                                    containerColor=MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor=MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Column(Modifier.weight(1f)){
                                    Text(x.description,fontWeight=FontWeight.Bold)
                                    Text("Código ${x.barcode} · Stock ${fmt(x.stock)} · Costo ${money(x.costPrice)}",color=Muted,fontSize=11.sp)
                                }
                                Icon(Icons.Default.ChevronRight,null,tint=MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
                if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80),modifier=Modifier.padding(top=6.dp))
            }
        },
        confirmButton={TextButton(onClick=onClose){Text("CANCELAR")}}
    )
}

@Composable fun PurchaseOrderActions(order:Purchase,onModify:()->Unit,onReceive:()->Unit,onDifference:()->Unit,onDelete:()->Unit,onClose:()->Unit){
    AlertDialog(onDismissRequest=onClose,title={Text(order.orderNo,fontWeight=FontWeight.Black)},text={Column{Text("Proveedor: ${order.supplier}",fontWeight=FontWeight.Bold);Text("Estado: ${order.status}",color=if(order.status.contains("RECEIVED"))Green else Orange);Spacer(Modifier.height(6.dp));Text("Total pedido: ${money(order.total)}");Text("Recibido: ${money(order.receivedTotal)}")}},confirmButton={Column(Modifier.padding(horizontal=8.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){FButton(onClick=onModify,modifier=Modifier.fillMaxWidth()){Text("MODIFICAR ORDEN")};FOutlinedButton(onClick=onReceive,modifier=Modifier.fillMaxWidth()){Text("RECIBIR MERCADERÍA")};FOutlinedButton(onClick=onDifference,modifier=Modifier.fillMaxWidth()){Text("RECIBIR CON DIFERENCIA")};FOutlinedButton(onClick=onDelete,modifier=Modifier.fillMaxWidth()){Text("ELIMINAR ORDEN")};TextFButton(onClick=onClose,modifier=Modifier.fillMaxWidth()){Text("CANCELAR")}}})
}

@Composable fun ReceivePurchaseDialog(api:MiComercioApi,detail:PurchaseDetail,differenceMode:Boolean,onDone:()->Unit,onClose:()->Unit){
    // IMPORTANTE: mientras el usuario escribe no convertimos/reformateamos el texto.
    // La versión anterior hacía num(it) en cada tecla y luego volvía a mostrar fmt(value),
    // provocando que el cursor saltara y que el valor se reseteara. Ahora el campo conserva
    // exactamente lo escrito y recién al confirmar se convierte a número.
    var lines by remember{mutableStateOf(detail.items.associate{item ->
        val initial=if(item.remainingQuantity>0.0) item.remainingQuantity else (item.quantity-item.receivedQuantity).coerceAtLeast(0.0)
        item.productId to if(initial==0.0) "" else fmt(initial)
    })};var note by remember{mutableStateOf("")};var msg by remember{mutableStateOf("")};var busy by remember{mutableStateOf(false)};val scope=rememberCoroutineScope()
    val requested=detail.items
    Dialog(onDismissRequest={if(!busy)onClose()}){
        Surface(shape=RoundedCornerShape(24.dp),color=Panel,tonalElevation=8.dp,modifier=Modifier.fillMaxWidth().padding(6.dp)){
            Column(Modifier.padding(16.dp)){
                Text(if(differenceMode)"Recepción con diferencia" else "Recibir mercadería",fontSize=24.sp,fontWeight=FontWeight.Black)
                Text("${detail.orderNo} · ${detail.supplier}",fontSize=12.sp,color=Muted)
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.weight(1f,false).heightIn(max=430.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){items(requested){line->
                    val value=lines[line.productId].orEmpty()
                    Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceVariant.copy(alpha=.35f)),modifier=Modifier.fillMaxWidth()){Column(Modifier.padding(10.dp)){
                        Text(line.description,fontWeight=FontWeight.Bold);Text("Pedido: ${fmt(line.quantity)} · Ya recibido: ${fmt(line.receivedQuantity)} · Pendiente: ${fmt(if(line.remainingQuantity>0.0) line.remainingQuantity else (line.quantity-line.receivedQuantity).coerceAtLeast(0.0))}",fontSize=11.sp,color=Muted)
                        OutlinedTextField(value=value,onValueChange={newValue->lines=lines.toMutableMap().also{m->m[line.productId]=newValue}},singleLine=true,modifier=Modifier.fillMaxWidth(),label={Text(if(differenceMode)"Cantidad recibida (admite diferencia)" else "Cantidad recibida")})
                    }}
                }}
                OutlinedTextField(value=note,onValueChange={note=it},modifier=Modifier.fillMaxWidth(),label={Text(if(differenceMode)"Motivo / nota de diferencia" else "Nota de recepción")},minLines=2)
                if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80),modifier=Modifier.padding(top=6.dp))
                Spacer(Modifier.height(8.dp))
                FButton(enabled=!busy,onClick={
                    val items=lines.mapNotNull{(pid,text)->val q=num(text);if(q<=0.0)null else {val d=requested.firstOrNull{it.productId==pid}?:return@mapNotNull null;PurchaseReceiveItemWrite(pid,q,d.unitCost)}}
                    if(items.isEmpty()){msg="Indicá al menos una cantidad recibida.";return@FButton}
                    if(differenceMode && note.isBlank()){msg="Indicá el motivo de la diferencia.";return@FButton}
                    busy=true;scope.launch{try{val r=api.receivePurchase(detail.id,PurchaseReceiveWrite(items,differenceMode,false,note));if(r.isSuccessful){msg="Recepción registrada correctamente";onDone()}else{msg="Error ${r.code()}: ${r.errorBody()?.string().orEmpty()}";busy=false}}catch(e:Exception){msg=e.message.orEmpty();busy=false}}
                },modifier=Modifier.fillMaxWidth()){Text(if(differenceMode)"CONFIRMAR RECEPCIÓN CON DIFERENCIA" else "CONFIRMAR RECEPCIÓN")}
                TextFButton(enabled=!busy,onClick=onClose,modifier=Modifier.fillMaxWidth()){Text("CANCELAR")}
            }
        }
    }
}

@Composable
private fun PurchaseStatusBadge(status:String){
    val normalized=status.trim().uppercase(Locale.ROOT)
    val isDifference=normalized.contains("DIFFER") || normalized.contains("PARTIAL") || normalized.contains("PARCIAL")
    val isComplete=normalized.contains("RECEIVED") || normalized.contains("RECIBID") || normalized.contains("COMPLETE") || normalized.contains("COMPLET")
    val isRejected=normalized.contains("REJECT") || normalized.contains("RECHAZ")
    val isProcess=normalized.contains("PENDING") || normalized.contains("PROCESS") || normalized.contains("PROCES") || normalized.contains("OPEN") || normalized.contains("CREATED") || normalized.contains("CREADA")
    val color=when{isDifference->Color(0xFFB66DFF);isComplete->Color(0xFF39FF88);isRejected->Color(0xFFFF1744);isProcess->Color(0xFFFFD740);else->Color(0xFFFFD740)}
    val label=when{isDifference->"RECIBIDO CON DIFERENCIA";isComplete->"RECIBIDO COMPLETO";isRejected->"RECHAZADO";else->"EN PROCESO"}
    Surface(color=color.copy(alpha=.13f),shape=RoundedCornerShape(10.dp),border=BorderStroke(1.5.dp,color.copy(alpha=.85f))){
        Text(label,color=color,fontWeight=FontWeight.Black,fontSize=11.sp,modifier=Modifier.padding(horizontal=10.dp,vertical=6.dp))
    }
}

@Composable
private fun PurchaseOrderCard(order:Purchase,onClick:()->Unit,onLongClick:()->Unit){
    val normalized=order.status.trim().uppercase(Locale.ROOT)
    val isDifference=normalized.contains("DIFFER") || normalized.contains("PARTIAL") || normalized.contains("PARCIAL")
    val isComplete=normalized.contains("RECEIVED") || normalized.contains("RECIBID") || normalized.contains("COMPLETE") || normalized.contains("COMPLET")
    val isRejected=normalized.contains("REJECT") || normalized.contains("RECHAZ")
    val statusColor=when{isDifference->Color(0xFFB66DFF);isComplete->Color(0xFF39FF88);isRejected->Color(0xFFFF1744);else->Color(0xFFFFD740)}
    Card(Modifier.fillMaxWidth().padding(vertical=4.dp).combinedClickable(onClick=onClick,onLongClick=onLongClick),colors=CardDefaults.cardColors(containerColor=Color(0xFF050505)),border=BorderStroke(1.5.dp,statusColor.copy(alpha=.72f))){
        Box(Modifier.background(Brush.horizontalGradient(listOf(statusColor.copy(alpha=.12f),Color.Transparent)))){
            Column(Modifier.padding(14.dp)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    Column(Modifier.weight(1f)){Text(order.orderNo,fontWeight=FontWeight.Black,fontSize=17.sp,color=Color.White);Text(order.supplier,color=Muted,fontSize=12.sp)}
                    PurchaseStatusBadge(order.status)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(14.dp)){Text("PEDIDO ${money(order.total)}",fontWeight=FontWeight.Bold);Text("RECIBIDO ${money(order.receivedTotal)}",color=statusColor,fontWeight=FontWeight.Bold)}
                Text("Llega ${if(order.expectedDate.isBlank())order.date.take(10) else order.expectedDate}",color=Muted,fontSize=11.sp,modifier=Modifier.padding(top=4.dp))
            }
        }
    }
}

@Composable
private fun PurchaseDetailDialog(detail:PurchaseDetail,onClose:()->Unit){
    val context=LocalContext.current
    val normalized=detail.status.trim().uppercase(Locale.ROOT)
    val completed=normalized.contains("RECEIVED") || normalized.contains("RECIBID") || normalized.contains("COMPLETE") || normalized.contains("COMPLET")
    val difference=normalized.contains("DIFFER") || normalized.contains("PARTIAL") || normalized.contains("PARCIAL")
    val statusLabel=when{difference->"RECIBIDO CON DIFERENCIA";completed->"RECIBIDO COMPLETO";else->"EN PROCESO"}
    val normalizedNotes=detail.notes.replace(Regex("Producto\\s+(\\d+):")){m->
        val productId=m.groupValues.getOrNull(1)?.toIntOrNull()
        detail.items.firstOrNull{it.productId==productId}?.description?.takeIf{it.isNotBlank()} ?: m.value.removeSuffix(":")
    }
    val shareText=buildString{
        appendLine("MI COMERCIOPOS · COMPROBANTE DE ORDEN DE COMPRA")
        appendLine("Orden: ${detail.orderNo}")
        appendLine("Proveedor: ${detail.supplier}")
        appendLine("Estado: $statusLabel")
        if(detail.expectedDate.isNotBlank())appendLine("Fecha de entrega: ${detail.expectedDate}")
        appendLine()
        detail.items.forEach{line->
            appendLine("${line.description}")
            appendLine("  Pedido: ${fmt(line.quantity)} · Recibido: ${fmt(line.receivedQuantity)} · Pendiente: ${fmt(line.remainingQuantity)}")
            if(line.quantity<=0.0 && line.receivedQuantity>0.0)appendLine("  PRODUCTO ADICIONAL RECIBIDO")
            if(line.notes.isNotBlank())appendLine("  Nota: ${line.notes}")
        }
        if(normalizedNotes.isNotBlank()){appendLine();appendLine("NOTAS / MOTIVO:");appendLine(normalizedNotes)}
    }
    AlertDialog(
        onDismissRequest=onClose,
        title={Column{Text("Detalle ${detail.orderNo}",fontSize=23.sp,fontWeight=FontWeight.Black);Text("$statusLabel",color=if(completed)Green else Orange,fontSize=12.sp,fontWeight=FontWeight.Bold)}},
        text={
            LazyColumn(Modifier.fillMaxWidth().heightIn(max=560.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                item{Text("Proveedor: ${detail.supplier}",fontWeight=FontWeight.Bold);Text("Productos de la orden",color=Muted,fontSize=12.sp)}
                items(detail.items){line->
                    Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=Color(0xFF101116)),border=BorderStroke(1.dp,Color(0xFF30333B))){
                        Column(Modifier.padding(10.dp)){
                            Text(line.description,fontWeight=FontWeight.Bold)
                            if(completed || difference){
                                Text("Pedido: ${fmt(line.quantity)} · Recibido: ${fmt(line.receivedQuantity)} · Pendiente: ${fmt(line.remainingQuantity)}",fontSize=12.sp,color=Muted)
                            }else{
                                Text("Pedido: ${fmt(line.quantity)} · Pendiente: ${fmt(line.remainingQuantity)}",fontSize=12.sp,color=Muted)
                            }
                            if(line.quantity<=0.0 && line.receivedQuantity>0.0)Text("PRODUCTO ADICIONAL RECIBIDO",fontSize=11.sp,color=Orange,fontWeight=FontWeight.Bold)
                            if(line.notes.isNotBlank())Text("Nota: ${line.notes}",fontSize=11.sp,color=Muted)
                        }
                    }
                }
                if(normalizedNotes.isNotBlank())item{Text("NOTAS / MOTIVO",fontWeight=FontWeight.Bold,color=Orange);Text(normalizedNotes,color=Muted)}
            }
        },
        confirmButton={
            Row(Modifier.fillMaxWidth().padding(horizontal=8.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                FButton(onClick={context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_SUBJECT,"Comprobante orden ${detail.orderNo}");putExtra(Intent.EXTRA_TEXT,shareText)},"Enviar comprobante"))},modifier=Modifier.weight(1f).height(52.dp)){Icon(Icons.Default.Send,null);Spacer(Modifier.width(5.dp));Text("ENVIAR COMPROBANTE",maxLines=1)}
                TextFButton(onClick=onClose,modifier=Modifier.weight(1f).height(52.dp)){Text("CERRAR",maxLines=1)}
            }
        }
    )
}

@Composable fun Purchases(api:MiComercioApi){
    var list by remember{mutableStateOf<List<Purchase>>(emptyList())};var suppliers by remember{mutableStateOf<List<Supplier>>(emptyList())};var supplier by remember{mutableStateOf<Supplier?>(null)};var product by remember{mutableStateOf<Product?>(null)};var qty by remember{mutableStateOf("1")};var cost by remember{mutableStateOf("")};var cart by remember{mutableStateOf<List<PurchaseDraftLine>>(emptyList())};var msg by remember{mutableStateOf("")};var chooseSupplier by remember{mutableStateOf(false)};var chooseProduct by remember{mutableStateOf(false)};var actionOrder by remember{mutableStateOf<Purchase?>(null)};var detailOrder by remember{mutableStateOf<PurchaseDetail?>(null)};var deleteOrder by remember{mutableStateOf<Purchase?>(null)};var receiveOrder by remember{mutableStateOf<Pair<PurchaseDetail,Boolean>?>(null)};var editingId by remember{mutableStateOf<Long?>(null)};var editingNo by remember{mutableStateOf("")};var expectedDate by remember{mutableStateOf(java.text.SimpleDateFormat("yyyy-MM-dd",Locale.US).format(java.util.Date()))};val context=LocalContext.current;val scope=rememberCoroutineScope()
    fun load(){scope.launch{try{val a=api.purchases();if(a.isSuccessful)list=a.body().orEmpty();val b=api.suppliers();if(b.isSuccessful)suppliers=b.body().orEmpty()}catch(e:Exception){msg=e.message.orEmpty()}}};LaunchedEffect(Unit){load()}
    fun prettyDate(s:String):String{val p=s.split("-");return if(p.size==3)"${p[2]}/${p[1]}/${p[0]}" else s}
    fun openDatePicker(){val p=expectedDate.split("-");val cal=Calendar.getInstance();val y=p.getOrNull(0)?.toIntOrNull()?:cal.get(Calendar.YEAR);val m=(p.getOrNull(1)?.toIntOrNull()?:cal.get(Calendar.MONTH)+1)-1;val d=p.getOrNull(2)?.toIntOrNull()?:cal.get(Calendar.DAY_OF_MONTH);DatePickerDialog(context,{_,yy,mm,dd->expectedDate=String.format(Locale.US,"%04d-%02d-%02d",yy,mm+1,dd)},y,m,d).show()}
    if(chooseSupplier){SelectorDialog("Elegir proveedor",suppliers.map{it.name},{i -> supplier=suppliers.getOrNull(i);product=null;chooseSupplier=false},{chooseSupplier=false});return}
    if(chooseProduct){val s=supplier;if(s==null){chooseProduct=false;msg="Primero elegí el proveedor."}else{PurchaseProductDialog(api,s,{p->product=p;cost=p.costPrice.toString().replace('.',',');chooseProduct=false},{chooseProduct=false});return}}
    if(detailOrder!=null){PurchaseDetailDialog(detailOrder!!,onClose={detailOrder=null});return}
    if(receiveOrder!=null){val x=receiveOrder!!;ReceivePurchaseDialog(api,x.first,x.second,onDone={receiveOrder=null;load()},onClose={receiveOrder=null});return}
    if(actionOrder!=null)return PurchaseOrderActions(actionOrder!!,
        onModify={val o=actionOrder!!;actionOrder=null;scope.launch{try{val r=api.purchaseDetail(o.id);if(!r.isSuccessful){msg="No se pudo abrir la orden (${r.code()})";return@launch};val d=r.body()?:throw Exception("Orden vacía");supplier=suppliers.firstOrNull{it.id==d.supplierId};editingId=d.id;editingNo=d.orderNo;expectedDate=d.expectedDate.ifBlank{expectedDate};cart=d.items.map{PurchaseDraftLine(Product(id=it.productId,description=it.description,costPrice=it.unitCost),it.quantity,it.unitCost)};msg="Modificando ${d.orderNo}"}catch(e:Exception){msg=e.message.orEmpty()}}},
        onReceive={val o=actionOrder!!;actionOrder=null;scope.launch{try{val r=api.purchaseDetail(o.id);if(r.isSuccessful)receiveOrder=r.body()!! to false else msg="No se pudo abrir la recepción (${r.code()})"}catch(e:Exception){msg=e.message.orEmpty()}}},
        onDifference={val o=actionOrder!!;actionOrder=null;scope.launch{try{val r=api.purchaseDetail(o.id);if(r.isSuccessful)receiveOrder=r.body()!! to true else msg="No se pudo abrir la recepción (${r.code()})"}catch(e:Exception){msg=e.message.orEmpty()}}},
        onDelete={deleteOrder=actionOrder;actionOrder=null},onClose={actionOrder=null})
    if(deleteOrder!=null)return AlertDialog(onDismissRequest={deleteOrder=null},title={Text("Eliminar orden")},text={Text("¿Eliminar ${deleteOrder!!.orderNo}? Si todavía no fue recibida, se eliminará la orden y sus líneas.")},confirmButton={FButton(onClick={val o=deleteOrder!!;scope.launch{try{val r=api.deletePurchase(o.id);if(r.isSuccessful){msg="Orden eliminada correctamente";deleteOrder=null;load()}else{msg="No se pudo eliminar (${r.code()}): ${r.errorBody()?.string().orEmpty()}";deleteOrder=null}}catch(e:Exception){msg=e.message.orEmpty();deleteOrder=null}}}){Text("ELIMINAR")}},dismissButton={TextFButton(onClick={deleteOrder=null}){Text("CANCELAR")}})
    LazyColumn(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
        item{Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Compras a proveedores",fontSize=27.sp,fontWeight=FontWeight.Black,color=Orange);Text("Proveedores · productos · carrito · recepción",color=Muted)};IconButton(onClick=::load){Icon(Icons.Default.Refresh,null)}}}
        item{Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=Panel)){Column(Modifier.padding(14.dp)){
            Text("NUEVA ORDEN DE COMPRA",fontWeight=FontWeight.Black,color=Green);Spacer(Modifier.height(6.dp));Text("1 · PROVEEDOR",fontWeight=FontWeight.Black,color=Muted);FButton(onClick={chooseSupplier=true},modifier=Modifier.fillMaxWidth().height(54.dp)){Icon(Icons.Default.Business,null);Spacer(Modifier.width(8.dp));Text(supplier?.name?:"ELEGIR PROVEEDOR",modifier=Modifier.weight(1f));Icon(Icons.Default.ChevronRight,null)}
            Text("2 · PRODUCTO",fontWeight=FontWeight.Black,color=Muted,modifier=Modifier.padding(top=8.dp));FOutlinedButton(enabled=supplier!=null,onClick={chooseProduct=true},modifier=Modifier.fillMaxWidth().height(54.dp)){Icon(Icons.Default.Inventory2,null);Spacer(Modifier.width(8.dp));Text(product?.description?:if(supplier==null)"ELEGÍ UN PROVEEDOR PRIMERO" else "BUSCAR / ELEGIR PRODUCTO",modifier=Modifier.weight(1f));Icon(Icons.Default.ChevronRight,null)}
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(value=qty,onValueChange={qty=it},label={Text("Cantidad")},singleLine=true,modifier=Modifier.weight(1f));OutlinedTextField(value=cost,onValueChange={cost=it},label={Text("Costo unitario")},singleLine=true,modifier=Modifier.weight(1f))}
            FButton(enabled=supplier!=null&&product!=null&&num(qty)>0,onClick={val p=product!!;val q=num(qty);val c=if(cost.isBlank())p.costPrice else num(cost);val old=cart.firstOrNull{it.product.id==p.id};cart=if(old==null)cart+PurchaseDraftLine(p,q,c)else cart.map{if(it.product.id==p.id)it.copy(quantity=it.quantity+q,unitCost=c)else it};product=null;qty="1";cost=""},modifier=Modifier.fillMaxWidth().padding(top=8.dp)){Text("AGREGAR AL CARRITO")}
            OutlinedButton(onClick={openDatePicker()},modifier=Modifier.fillMaxWidth().padding(top=8.dp),colors=ButtonDefaults.outlinedButtonColors(contentColor=Green)){Icon(Icons.Default.CalendarMonth,null);Spacer(Modifier.width(8.dp));Text("FECHA DE LLEGADA: ${prettyDate(expectedDate)}")}
            if(cart.isNotEmpty()){Text("CARRITO ${if(editingId!=null)"· EDITANDO $editingNo" else ""}",fontWeight=FontWeight.Black,modifier=Modifier.padding(top=12.dp));cart.forEachIndexed{i,line->Row(Modifier.fillMaxWidth().padding(vertical=4.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(line.product.description,fontWeight=FontWeight.Bold);Text("${fmt(line.quantity)} × ${money(line.unitCost)} = ${money(line.quantity*line.unitCost)}",color=Muted,fontSize=12.sp)};IconButton(onClick={cart=cart.toMutableList().also{it.removeAt(i)}}){Icon(Icons.Default.Delete,null)}}};Text("TOTAL ${money(cart.sumOf{it.quantity*it.unitCost})}",fontWeight=FontWeight.Black,color=Green)}
            FButton(enabled=supplier!=null&&cart.isNotEmpty(),onClick={scope.launch{try{val body=PurchaseWrite(supplier!!.id,expectedDate=expectedDate,items=cart.map{PurchaseItemWrite(it.product.id,it.quantity,it.unitCost,"")});val r=if(editingId==null)api.createPurchase(body)else api.updatePurchase(editingId!!,body);if(r.isSuccessful){msg=if(editingId==null)"Orden creada correctamente" else "Orden modificada correctamente";cart=emptyList();editingId=null;editingNo="";product=null;load()}else msg="Error ${r.code()}: ${r.errorBody()?.string().orEmpty()}"}catch(e:Exception){msg=e.message.orEmpty()}}},modifier=Modifier.fillMaxWidth().padding(top=8.dp)){Text(if(editingId==null)"CREAR ORDEN DE COMPRA" else "GUARDAR MODIFICACIÓN")}
        }}}
        if(msg.isNotBlank())item{Text(msg,color=if(msg.contains("correctamente"))Green else Color(0xFFFF8A80),modifier=Modifier.padding(vertical=4.dp))}
        item{Text("ÚLTIMAS ÓRDENES",fontWeight=FontWeight.Bold)}
        items(list,key={it.id}){p->PurchaseOrderCard(p,onClick={scope.launch{try{val r=api.purchaseDetail(p.id);if(r.isSuccessful)detailOrder=r.body()?:PurchaseDetail(id=p.id,orderNo=p.orderNo,supplier=p.supplier,status=p.status)else msg="No se pudo abrir el detalle (${r.code()})"}catch(e:Exception){msg=e.message.orEmpty()}}},onLongClick={actionOrder=p})}
    }
}

@Composable
fun Analytics(api: MiComercioApi) {
    var d by remember { mutableStateOf<AnalyticsData?>(null) }
    var msg by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        try {
            val r = api.analytics()
            if (r.isSuccessful) d = r.body() else msg = "Error ${r.code()}"
        } catch (e: Exception) { msg = e.message.orEmpty() }
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("CENTRO DE ANÁLISIS", fontSize = 27.sp, fontWeight = FontWeight.Black, color = Red)
            Text("Ventas · stock · compras · créditos · medios de pago", color = Muted)
        }
        d?.let { x ->
            val paymentNormalized = x.paymentMix
                .groupBy { row -> row.label.trim().uppercase(Locale.getDefault()).replace("CRÉDITO", "CREDITO") }
                .map { (key, rows) ->
                    val label = if (key == "CREDITO") "CRÉDITO (CLIENTES)" else rows.first().label
                    AnalyticsRow(label, rows.sumOf { row -> row.value })
                }
                .sortedByDescending { row -> row.value }
            item { InsightCard("PRODUCTOS MÁS VENDIDOS", x.topProducts, Green) }
            item { InsightCard("PRODUCTOS QUE HAY QUE PEDIR", x.lowStock, Orange) }
            item { InsightCard("CRÉDITOS / DEUDAS", x.customers, Red) }
            item { InsightCard("PROVEEDORES", x.suppliers, Color(0xFF9C6CFF)) }
            item { InsightCard("MEDIOS DE PAGO", paymentNormalized, Color(0xFF35D7FF)) }
            item { PaymentPie(paymentNormalized) }
        }
        if (msg.isNotBlank()) item { Text(msg, color = Color(0xFFFF8A80)) }
    }
}

@Composable
fun PaymentPie(rows: List<AnalyticsRow>) {
    val values = rows.filter { it.value > 0 }.take(8)
    val total = values.sumOf { it.value }
    val sliceColors = listOf(
        Color(0xFF39FF88), Color(0xFF35D7FF), Color(0xFFFFC857), Color(0xFFFF1744),
        Color(0xFFB000FF), Color(0xFFFF7A59), Color(0xFF7CDBFF), Color(0xFFE0E0E0)
    )
    Card(
        Modifier.fillMaxWidth().shadow(18.dp, RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = Panel)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("DISTRIBUCIÓN DE COBROS", fontWeight = FontWeight.Black, color = Color(0xFF35D7FF))
            if (total <= 0) {
                Text("Sin movimientos suficientes.", color = Muted)
            } else {
                Box(Modifier.fillMaxWidth().height(245.dp)) {
                    Canvas(Modifier.fillMaxSize()) {
                        val diameter = minOf(size.width * .78f, size.height * .78f)
                        val left = (size.width - diameter) / 2f
                        val top = (size.height - diameter) / 2f - 5.dp.toPx()
                        val depth = 13.dp.toPx()
                        val arcSize = Size(diameter, diameter)
                        var start = 0f

                        // Faux 3D depth: draw the lower rim first, using the same
                        // slice color so every payment method keeps its own identity.
                        values.forEachIndexed { i, r ->
                            val sweep = (r.value / total * 360.0).toFloat()
                            val c = sliceColors[i % sliceColors.size]
                            drawArc(c.copy(alpha = .55f), startAngle = start, sweepAngle = sweep,
                                useCenter = true, topLeft = Offset(left, top + depth), size = arcSize)
                            start += sweep
                        }

                        // Main colored face.
                        start = 0f
                        values.forEachIndexed { i, r ->
                            val sweep = (r.value / total * 360.0).toFloat()
                            val c = sliceColors[i % sliceColors.size]
                            drawArc(c, startAngle = start, sweepAngle = sweep,
                                useCenter = true, topLeft = Offset(left, top), size = arcSize)
                            // Small highlight on each sector makes the face look raised.
                            drawArc(c.copy(alpha = .22f), startAngle = start + 1.5f,
                                sweepAngle = maxOf(0f, minOf(18f, sweep - 3f)), useCenter = true,
                                topLeft = Offset(left, top), size = arcSize)
                            start += sweep
                        }

                        // Crisp separators prevent adjacent sectors from visually merging.
                        start = 0f
                        values.forEachIndexed { i, r ->
                            val sweep = (r.value / total * 360.0).toFloat()
                            if (sweep > 2f) {
                                val angle = Math.toRadians(start.toDouble())
                                val cx = left + diameter / 2f
                                val cy = top + diameter / 2f
                                val radius = diameter / 2f
                                val x = cx + kotlin.math.cos(angle).toFloat() * radius
                                val y = cy + kotlin.math.sin(angle).toFloat() * radius
                                drawLine(Color.Black.copy(alpha = .65f), Offset(cx, cy), Offset(x, y), 2.dp.toPx())
                            }
                            start += sweep
                        }
                    }
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("TOTAL", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(money(total), color = Green, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("cobros", color = Muted, fontSize = 11.sp)
                    }
                }
                values.forEachIndexed { i, r ->
                    val c = sliceColors[i % sliceColors.size]
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(13.dp).shadow(4.dp, RoundedCornerShape(4.dp)).background(c, RoundedCornerShape(4.dp)))
                        Spacer(Modifier.width(9.dp))
                        Text(r.label, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("${money(r.value)} · ${((r.value / total) * 100).toInt()}%", color = c, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun InsightCard(title: String, rows: List<AnalyticsRow>, accent: Color) {
    Card(
        Modifier.fillMaxWidth().shadow(10.dp, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = Panel)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Black, color = accent)
            val max = rows.maxOfOrNull { it.value } ?: 1.0
            rows.take(8).forEach { r ->
                Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(r.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Box(Modifier.fillMaxWidth().height(9.dp).background(Color.White.copy(alpha = .08f), RoundedCornerShape(9.dp))) {
                            Box(Modifier.fillMaxWidth((r.value / max).toFloat().coerceIn(0f, 1f)).fillMaxHeight().background(accent.copy(alpha = .85f), RoundedCornerShape(9.dp)))
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(fmt(r.value), fontWeight = FontWeight.Black, color = accent)
                }
            }
            if (rows.isEmpty()) Text("Sin datos suficientes todavía.", color = Muted)
        }
    }
}

@Composable
fun StockHistory(api: MiComercioApi) {
    var rows by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var msg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    fun load() { scope.launch { try { val r = api.stockMovements(); if (r.isSuccessful) rows = r.body().orEmpty() else msg = "Error ${r.code()}" } catch (e: Exception) { msg = e.message.orEmpty() } } }
    LaunchedEffect(Unit) { load() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Movimientos de stock", fontSize = 27.sp, fontWeight = FontWeight.Black); Text("Historial local de este teléfono", color = Muted) }
            IconButton(onClick = { load() }) { Icon(Icons.Default.Refresh, null) }
        }
        LazyColumn { items(rows) { r -> ListRow((r["producto"] ?: r["description"] ?: "Producto").toString(), "${r["tipo"] ?: r["movementType"] ?: "MOVIMIENTO"} · ${r["cantidad"] ?: r["quantity"] ?: ""} · ${r["fecha"] ?: r["createdAt"] ?: ""} · ${r["usuario"] ?: ""}", {}) } }
        if (msg.isNotBlank()) Text(msg, color = Orange)
    }
}

@Composable
fun CashHistory(api: MiComercioApi) {
    var rows by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var selected by remember { mutableStateOf<Map<String, Any>?>(null) }
    var reason by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    fun load() { scope.launch { try { val r = api.cashMovements(); if (r.isSuccessful) rows = r.body().orEmpty() else msg = "Error ${r.code()}" } catch (e: Exception) { msg = e.message.orEmpty() } } }
    LaunchedEffect(Unit) { load() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Movimientos de caja", fontSize = 27.sp, fontWeight = FontWeight.Black); Text("Caja local · anulaciones con motivo", color = Muted) }
            IconButton(onClick = { load() }) { Icon(Icons.Default.Refresh, null) }
        }
        LazyColumn {
            items(rows) { r ->
                val voided = (r["anulado"] as? Boolean) == true || r["anulado"].toString().equals("true", true)
                ListRow("${r["tipo"] ?: "MOVIMIENTO"} · ${money((r["importe"] ?: 0).toString().toDoubleOrNull() ?: 0.0)}", "${r["concepto"] ?: ""} · ${r["medio"] ?: ""} · ${if (voided) "ANULADO" else r["fecha"] ?: ""}", onClick = { if (!voided) selected = r })
            }
        }
        if (msg.isNotBlank()) Text(msg, color = Orange)
    }
    if (selected != null) {
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text("Anular movimiento") },
            text = { Column { Text("Esta operación dejará de afectar la caja y quedará auditada.", color = Muted); Field("Motivo", reason, onChange = { reason = it }) } },
            confirmButton = {
                FButton(enabled = reason.isNotBlank(), onClick = {
                    val id = (selected!!["id"] ?: 0).toString().toLongOrNull() ?: 0
                    scope.launch { try { val r = api.voidCashMovement(id, mapOf("reason" to reason.trim())); if (r.isSuccessful) { selected = null; reason = ""; load() } else msg = "Error ${r.code()}" } catch (e: Exception) { msg = e.message.orEmpty() } }
                }) { Text("CONFIRMAR ANULACIÓN") }
            },
            dismissButton = { TextFButton(onClick = { selected = null }) { Text("CANCELAR") } }
        )
    }
}

@Composable
fun Reports(api: MiComercioApi) {
    var d by remember { mutableStateOf<DetailedReportData?>(null) }
    var msg by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { try { val r = api.detailedReports(); if (r.isSuccessful) d = r.body() else msg = "Error ${r.code()}" } catch (e: Exception) { msg = e.message.orEmpty() } }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("REPORTES DETALLADOS", fontSize = 27.sp, fontWeight = FontWeight.Black); Text("Día actual · hora local del teléfono · caja local", color = Muted) }
        d?.let { x ->
            item { Metric("VENTAS DEL DÍA", money(x.sales), Red, Modifier.fillMaxWidth()) }
            item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Metric("TICKETS", x.tickets.toString(), Green, Modifier.weight(1f)); Metric("PROMEDIO", money(x.averageTicket), Orange, Modifier.weight(1f)) } }
            item { ReportBars("VENTAS POR HORA", x.hourly, Red) }
            item { ReportBars("VENTAS POR CATEGORÍA", x.categories, Green) }
            item { ReportBars("MEDIOS DE PAGO", x.payments, Color(0xFF35D7FF)) }
        }
        if (msg.isNotBlank()) item { Text(msg, color = Orange) }
    }
}

@Composable
fun ReportBars(title: String, rows: List<ReportRow>, accent: Color) {
    Card(Modifier.fillMaxWidth().shadow(10.dp, RoundedCornerShape(18.dp)), colors = CardDefaults.cardColors(containerColor = Panel)) {
        Column(Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Black, color = accent)
            val max = rows.maxOfOrNull { it.value } ?: 1.0
            rows.take(12).forEach { r ->
                Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(r.label, fontSize = 11.sp, modifier = Modifier.width(78.dp))
                    Box(Modifier.weight(1f).height(10.dp).background(Color.White.copy(alpha = .07f), RoundedCornerShape(8.dp))) {
                        Box(Modifier.fillMaxWidth((r.value / max).toFloat().coerceIn(0f, 1f)).fillMaxHeight().background(accent.copy(alpha = .82f), RoundedCornerShape(8.dp)))
                    }
                }
            }
        }
    }
}

@Composable
fun Audit(api: MiComercioApi) {
    var rows by remember { mutableStateOf<List<AuditRow>>(emptyList()) }
    var msg by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { try { val r = api.auditLog(); if (r.isSuccessful) rows = r.body().orEmpty() else msg = "Error ${r.code()}" } catch (e: Exception) { msg = e.message.orEmpty() } }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("AUDITORÍA", fontSize = 27.sp, fontWeight = FontWeight.Black)
        Text("Últimas operaciones registradas en este teléfono", color = Muted)
        LazyColumn(Modifier.padding(top = 10.dp)) { items(rows) { r -> ListRow("${r.module} · ${r.action}", "${r.dateTime} · ${r.user} · ${r.details}", {}) } }
        if (msg.isNotBlank()) Text(msg, color = Orange)
    }
}

@Composable
private fun BlinkingGeneralZSendButton(onClick:()->Unit,modifier:Modifier=Modifier){
    val infinite=rememberInfiniteTransition(label="generalZSendNeon")
    val pulse by infinite.animateFloat(initialValue=.25f,targetValue=1f,animationSpec=infiniteRepeatable(animation=tween(650),repeatMode=RepeatMode.Reverse),label="sendPulse")
    val light=Color(0xFF24D9FF)
    val shape=RoundedCornerShape(15.dp)
    Button(onClick=onClick,modifier=modifier.height(52.dp).scale(1f+0.008f*pulse).shadow((12f+24f*pulse).dp,shape,ambientColor=light.copy(alpha=.70f*pulse),spotColor=light.copy(alpha=.90f*pulse)),shape=shape,colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF06151B),contentColor=light),border=BorderStroke((2.2f+1.8f*pulse).dp,light.copy(alpha=.65f+.35f*pulse))){
        Text("ENVIAR POR WHATSAPP / MENSAJE",fontWeight=FontWeight.Black,fontSize=13.sp,letterSpacing=.6.sp,color=light.copy(alpha=.82f+.18f*pulse),maxLines=1)
    }
}

@Composable
fun GeneralZCard(api:MiComercioApi){
    val context=LocalContext.current
    var report by remember{mutableStateOf<GeneralZReport?>(null)}
    var msg by remember{mutableStateOf("")}
    val scope=rememberCoroutineScope()
    fun load(){scope.launch{try{val r=api.generalZ();if(r.isSuccessful)report=r.body() else msg="Error ${r.code()}"}catch(e:Exception){msg=e.message.orEmpty()}}}
    LaunchedEffect(Unit){load()}
    Column(Modifier.fillMaxSize().padding(16.dp)){
        Text("CORTE Z GENERAL",fontSize=27.sp,fontWeight=FontWeight.Black,color=Red)
        Text("Corte total del día · todos los cajeros · no cierra ninguna caja",color=Muted)
        Spacer(Modifier.height(10.dp))
        if(report!=null){
            val r=report!!
            Card(Modifier.fillMaxWidth().weight(1f),colors=CardDefaults.cardColors(containerColor=Panel)){
                Column(Modifier.fillMaxSize().padding(14.dp)){
                    Text("${r.date} · ${r.cashiers} cajeros · ${r.tickets} tickets",fontWeight=FontWeight.Bold)
                    Text("VENTAS TOTALES ${money(r.totalSales)}",fontSize=22.sp,fontWeight=FontWeight.Black,color=Orange)
                    Spacer(Modifier.height(8.dp))
                    Text(r.text,fontSize=11.sp,color=Muted,modifier=Modifier.weight(1f).verticalScroll(rememberScrollState()))
                }
            }
            Spacer(Modifier.height(8.dp))
            BlinkingGeneralZSendButton(onClick={
                val send=Intent(Intent.ACTION_SEND).apply{
                    type="text/plain"
                    putExtra(Intent.EXTRA_SUBJECT,r.title.ifBlank{"Corte Z general"})
                    putExtra(Intent.EXTRA_TEXT,"${r.title.ifBlank{"CORTE Z GENERAL"}}\n\n${r.text}")
                }
                context.startActivity(Intent.createChooser(send,"Enviar Corte Z por WhatsApp / mensaje / otras apps"))
            },modifier=Modifier.fillMaxWidth())
            FOutlinedButton(onClick={load()},modifier=Modifier.fillMaxWidth().padding(top=7.dp)){Text("ACTUALIZAR CORTE")}
        } else {
            Box(Modifier.fillMaxWidth().weight(1f),contentAlignment=Alignment.Center){Text(if(msg.isBlank())"Cargando Corte Z general…" else msg,color=Muted)}
            FOutlinedButton(onClick={load()},modifier=Modifier.fillMaxWidth().padding(top=7.dp)){Text("ACTUALIZAR CORTE")}
        }
        if(msg.isNotBlank() && report!=null)Text(msg,color=Orange,modifier=Modifier.padding(top=4.dp))
    }
}
@Composable fun Users(api:MiComercioApi){
    val scope=rememberCoroutineScope()
    var users by remember{mutableStateOf<List<MobileUser>>(emptyList())}
    var current by remember{mutableStateOf<MobileUser?>(null)}
    var showNew by remember{mutableStateOf(false)}
    var switcher by remember{mutableStateOf(false)}
    var message by remember{mutableStateOf("")}
    fun load(){scope.launch{try{users=api.mobileUsers().body().orEmpty();current=api.currentMobileUser().body()}catch(e:Exception){message=e.message.orEmpty()}}}
    LaunchedEffect(Unit){load()}
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())){
        Text("Usuarios / Empleados",fontSize=27.sp,fontWeight=FontWeight.Black)
        Text("Cada cajero tiene usuario, contraseña, permisos y datos de contacto propios.",color=Muted,fontSize=12.sp)
        Card(Modifier.fillMaxWidth().padding(top=12.dp),colors=CardDefaults.cardColors(containerColor=Panel)){
            Column(Modifier.padding(16.dp)){
                Text("USUARIO ACTIVO",fontWeight=FontWeight.Black,color=Green)
                Text(current?.let{"${it.fullName.ifBlank{it.username}} · ${it.username}"}.orEmpty(),fontSize=19.sp,fontWeight=FontWeight.Bold)
                Text("Rol: ${current?.role.orEmpty()}",color=Muted)
                FButton(onClick={switcher=true},modifier=Modifier.fillMaxWidth().padding(top=10.dp)){Text("CAMBIAR CAJERO / USUARIO")}
            }
        }
        FButton(onClick={showNew=true},modifier=Modifier.fillMaxWidth().padding(top=12.dp).height(50.dp)){Text("+ NUEVO USUARIO / CAJERO")}
        users.forEach{u->
            Card(Modifier.fillMaxWidth().padding(top=8.dp),colors=CardDefaults.cardColors(containerColor=Panel),border=BorderStroke(1.dp,MaterialTheme.colorScheme.primary.copy(alpha=.12f))){
                Column(Modifier.padding(14.dp)){
                    Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(u.fullName.ifBlank{u.username},fontWeight=FontWeight.Bold,fontSize=17.sp);Text("${u.username} · ${u.role}",color=Muted,fontSize=12.sp)};if(u.id!=current?.id){TextButton(onClick={scope.launch{if(api.deleteMobileUser(u.id).body()?.ok==true)load()}}){Text("ELIMINAR",color=Red)}}}
                    if(u.phone.isNotBlank())Text("Tel: ${u.phone}",color=Muted,fontSize=12.sp)
                    if(u.email.isNotBlank())Text("Email: ${u.email}",color=Muted,fontSize=12.sp)
                    Text("Permisos: ${if(u.permissions.contains("ALL"))"Todos" else u.permissions.joinToString(", ").ifBlank{"Sin permisos"}}",color=Muted,fontSize=12.sp,modifier=Modifier.padding(top=4.dp))
                }
            }
        }
        if(message.isNotBlank())Text(message,color=Red,fontSize=12.sp,modifier=Modifier.padding(top=8.dp))
    }
    if(showNew) NewUserDialog(api,onDone={showNew=false;load()})
    if(switcher) UserSwitchDialog(api,onDone={switcher=false;load()})
}

@Composable private fun NewUserDialog(api:MiComercioApi,onDone:()->Unit){
    val scope=rememberCoroutineScope()
    var username by remember{mutableStateOf("")};var name by remember{mutableStateOf("")};var password by remember{mutableStateOf("")};var phone by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var role by remember{mutableStateOf("CAJERO")};var msg by remember{mutableStateOf("")}
    val allPermissions=listOf("VENTAS","PRODUCTOS","CLIENTES","CAJA","COMPRAS","PROVEEDORES","MESAS","PROMOCIONES","REPORTES","CONFIGURACION")
    var selected by remember{mutableStateOf(setOf("VENTAS","CLIENTES"))}
    AlertDialog(onDismissRequest=onDone,title={Text("Nuevo usuario / empleado",fontWeight=FontWeight.Black)},text={Column(Modifier.heightIn(max=560.dp).verticalScroll(rememberScrollState())){
        Field("Usuario",username,onChange={username=it});Field("Nombre",name,onChange={name=it});Field("Contraseña",password,onChange={password=it});Field("Teléfono (opcional)",phone,onChange={phone=it});Field("Email (opcional)",email,onChange={email=it})
        Spacer(Modifier.height(8.dp));Text("ROL",fontWeight=FontWeight.Bold);Row{FilterChip(selected=role=="CAJERO",onClick={role="CAJERO"},label={Text("CAJERO")});Spacer(Modifier.width(8.dp));FilterChip(selected=role=="ADMIN",onClick={role="ADMIN"},label={Text("ADMIN")})}
        Text("PERMISOS",fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=10.dp));Row(verticalAlignment=Alignment.CenterVertically){Checkbox(checked=selected.contains("ALL"),onCheckedChange={if(it)selected=setOf("ALL") else selected=setOf("VENTAS")});Text("Todos")}
        if(!selected.contains("ALL"))allPermissions.forEach{perm->Row(verticalAlignment=Alignment.CenterVertically){Checkbox(checked=selected.contains(perm),onCheckedChange={checked->selected=if(checked)selected+perm else selected-perm});Text(perm)}}
        if(msg.isNotBlank())Text(msg,color=Red,fontSize=12.sp)
    }},confirmButton={FButton(onClick={scope.launch{val r=api.createMobileUser(MobileUserWrite(username,name,password,phone,email,role,selected.toList()));if(r.isSuccessful&&r.body()?.ok==true)onDone() else msg="No se pudo crear el usuario. Verificá usuario y contraseña."}}){Text("CREAR USUARIO")}},dismissButton={TextFButton(onClick=onDone){Text("CANCELAR")}})
}
@Composable fun TableSettingsCard(api:MiComercioApi,themeName:String){
    var tables by remember{mutableStateOf<List<TableInfo>>(emptyList())};var msg by remember{mutableStateOf("")};var countText by remember{mutableStateOf("")};var editing by remember{mutableStateOf<TableInfo?>(null)};val scope=rememberCoroutineScope()
    fun load(){scope.launch{try{val r=api.tables(null);if(r.isSuccessful){tables=r.body().orEmpty().sortedBy{it.id};countText=tables.size.toString()}else msg="Error ${r.code()}"}catch(e:Exception){msg=e.message.orEmpty()}}}
    LaunchedEffect(Unit){load()}
    Card(Modifier.fillMaxWidth().padding(top=12.dp),colors=CardDefaults.cardColors(containerColor=palette(themeName).panel)){Column(Modifier.padding(16.dp)){
        Text("🍽️ MESAS",fontWeight=FontWeight.Black,color=MaterialTheme.colorScheme.primary);Text("Configurá cuántas mesas querés y cambiá sus nombres.",color=Muted,fontSize=12.sp)
        Row(verticalAlignment=Alignment.CenterVertically){Field("Cantidad de mesas",countText,onChange={countText=it},modifier=Modifier.weight(1f));FButton(onClick={scope.launch{val target=num(countText).toInt().coerceIn(1,99);while(tables.size<target){val r=api.createTable(TableWrite("Mesa ${tables.size+1}"));if(!r.isSuccessful)break;tables=api.tables(null).body().orEmpty().sortedBy{it.id}};while(tables.size>target){val t=tables.last();val r=api.deleteTable(t.id,mapOf("reason" to "Ajuste de cantidad de mesas"));if(!r.isSuccessful)break;tables=api.tables(null).body().orEmpty().sortedBy{it.id}};countText=tables.size.toString()}},modifier=Modifier.padding(start=8.dp)){Text("APLICAR")}}
        tables.forEach{t->ListRow(t.name,"${t.capacity} personas · ${t.shape}",onClick={editing=t})};if(msg.isNotBlank())Text(msg,color=Orange,fontSize=12.sp)
    }}
    editing?.let{t->var name by remember(t.id){mutableStateOf(t.name)};var capacity by remember(t.id){mutableStateOf(t.capacity.toString())};AlertDialog(onDismissRequest={editing=null},title={Text("Configurar ${t.name}")},text={Column{Field("Nombre de la mesa",name,onChange={name=it});Field("Capacidad",capacity,onChange={capacity=it})}},confirmButton={FButton(onClick={scope.launch{val body=TableWrite(name.trim().ifBlank{t.name},num(capacity).toInt().coerceAtLeast(1),t.shape,t.salonId,t.x,t.y,t.width,t.height,t.rotation,t.color);val r=api.updateTable(t.id,body);if(r.isSuccessful){editing=null;load()}else msg="No se pudo guardar la mesa"}}){Text("GUARDAR")}},dismissButton={TextFButton(onClick={editing=null}){Text("CANCELAR")}})}
}
@Composable
private fun BackupSettingsCard(api: MiComercioApi, themeName: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf("") }
    var showPath by remember { mutableStateOf(false) }
    val backupPath = "Almacenamiento interno compartido → Download → MiComercio → mi_comercio_backup.json"

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            scope.launch {
                runCatching {
                    val json = api.exportBackupJson()
                    context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray(Charsets.UTF_8)) }
                    message = "Respaldo exportado correctamente."
                }.onFailure { message = "No se pudo exportar: ${it.message.orEmpty()}" }
            }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                runCatching {
                    val json = context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: error("Archivo vacío")
                    if (api.importBackupJson(json)) message = "Respaldo importado. Los datos actuales fueron reemplazados por el respaldo." else error("Formato de respaldo no válido")
                }.onFailure { message = "No se pudo importar: ${it.message.orEmpty()}" }
            }
        }
    }

    if (showPath) {
        AlertDialog(onDismissRequest = { showPath = false }, title = { Text("Dónde está el respaldo") }, text = { Text(backupPath, color = Muted) }, confirmButton = { FButton(onClick = { showPath = false }) { Text("ENTENDIDO") } })
    }

    Card(Modifier.fillMaxWidth().padding(top = 12.dp), colors = CardDefaults.cardColors(containerColor = palette(themeName).panel)) {
        Column(Modifier.padding(16.dp)) {
            Text("💾 RESPALDO Y RECUPERACIÓN", fontWeight = FontWeight.Black, color = Green)
            Text("El respaldo se actualiza automáticamente al guardar cambios. Queda fuera de la instalación para sobrevivir a una desinstalación.", color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            Text("Ubicación automática", fontWeight = FontWeight.Bold)
            Text(backupPath, color = Green, fontSize = 12.sp)
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FButton(onClick = { showPath = true }, modifier = Modifier.weight(1f)) { Text("VER UBICACIÓN") }
                FOutlinedButton(onClick = { exportLauncher.launch("mi_comercio_backup.json") }, modifier = Modifier.weight(1f)) { Text("EXPORTAR") }
            }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FOutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/plain")) }, modifier = Modifier.weight(1f)) { Text("IMPORTAR") }
                FButton(onClick = { message = if (MiComercioBackup.delete(context)) "Respaldo externo eliminado. La base actual NO fue borrada." else "No hay respaldo externo para eliminar." }, modifier = Modifier.weight(1f)) { Text("BORRAR RESPALDO") }
            }
            if (message.isNotBlank()) Text(message, color = Orange, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            Text("Si querés borrar absolutamente todo, eliminá también este archivo desde Download/MiComercio y después desinstalá la aplicación.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Composable fun Config(api:MiComercioApi,prefs:Prefs,themeName:String,sounds:Boolean,haloName:String,onTheme:(String)->Unit,onSounds:(Boolean)->Unit,onHalo:(String)->Unit){
    val context=LocalContext.current
    val scope=rememberCoroutineScope()
    var email by remember{mutableStateOf("")}
    var showUsers by remember{mutableStateOf(false)}
    LaunchedEffect(Unit){ email=prefs.email() }
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())){
        Text("Configuración",fontSize=27.sp,fontWeight=FontWeight.Black,color=MiComercioNeonRed)
        Text("Mi Comercio · aplicación 100% Android · todos los datos se guardan en este teléfono.",color=Muted)
        Card(Modifier.fillMaxWidth().padding(top=14.dp),colors=CardDefaults.cardColors(containerColor=palette(themeName).panel)){Column(Modifier.padding(16.dp)){
            Text("✉️ EMAIL",fontWeight=FontWeight.Black,color=MiComercioNeonRed)
            Text("Configurá el email al que querés preparar los reportes y comprobantes.",color=Muted,fontSize=12.sp)
            Field("Email de destino",email,onChange={email=it})
            FButton(onClick={scope.launch{prefs.saveEmail(email)}},modifier=Modifier.fillMaxWidth().padding(top=8.dp)){Text("GUARDAR EMAIL")}
            FOutlinedButton(onClick={scope.launch{prefs.saveEmail(email);val i=Intent(Intent.ACTION_SENDTO).apply{data=android.net.Uri.parse("mailto:${email.trim()}");putExtra(Intent.EXTRA_SUBJECT,"Prueba · Mi Comercio");putExtra(Intent.EXTRA_TEXT,"Este es un mensaje de prueba de Mi Comercio.")};context.startActivity(i)}},modifier=Modifier.fillMaxWidth().padding(top=6.dp)){Text("PROBAR ENVÍO POR EMAIL")}
        }}
        Card(Modifier.fillMaxWidth().padding(top=12.dp),colors=CardDefaults.cardColors(containerColor=palette(themeName).panel)){Column(Modifier.padding(16.dp)){
            Text("🎨 APARIENCIA",fontWeight=FontWeight.Black)
            val themes=listOf("Ocean Glass","Midnight Gold","Cloud Pro")
            themes.forEach{t->FOutlinedButton(onClick={onTheme(t);scope.launch{prefs.saveTheme(t)}},modifier=Modifier.fillMaxWidth().padding(vertical=3.dp)){Text(if(themeName==t)"✓ $t" else t)}}
        }}
        Card(Modifier.fillMaxWidth().padding(top=12.dp),colors=CardDefaults.cardColors(containerColor=palette(themeName).panel)){Column(Modifier.padding(16.dp)){
            Text("✨ HALO DEL TÍTULO",fontWeight=FontWeight.Black)
            listOf("Verde Flúor","Naranja Flúor","Azul Neón","Rosa Neón","Morado Neón","Cian Neón","Blanco Neón").forEach{h->FOutlinedButton(onClick={onHalo(h);scope.launch{prefs.saveHalo(h)}},modifier=Modifier.fillMaxWidth().padding(vertical=2.dp)){Text(if(haloName==h)"✓ $h" else h)}}
        }}
        Card(Modifier.fillMaxWidth().padding(top=12.dp),colors=CardDefaults.cardColors(containerColor=palette(themeName).panel)){Column(Modifier.padding(16.dp)){
            Text("🔊 SONIDO",fontWeight=FontWeight.Black)
            Row(Modifier.fillMaxWidth().padding(top=8.dp),verticalAlignment=Alignment.CenterVertically){Text(if(sounds)"SONIDO ACTIVO" else "SONIDO DESACTIVADO",fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));Switch(checked=sounds,onCheckedChange={onSounds(it);scope.launch{prefs.saveSounds(it)}})}
        }}
        BackupSettingsCard(api, themeName)
        TableSettingsCard(api,themeName)
        Card(Modifier.fillMaxWidth().padding(top=12.dp),colors=CardDefaults.cardColors(containerColor=palette(themeName).panel)){Column(Modifier.padding(16.dp)){
            Text("👥 USUARIOS / EMPLEADOS",fontWeight=FontWeight.Black,color=Green)
            Text("Creá cajeros con usuario, contraseña, permisos, teléfono y email. También podés cambiar el cajero activo.",color=Muted,fontSize=12.sp)
            FButton(onClick={showUsers=true},modifier=Modifier.fillMaxWidth().padding(top=8.dp)){Text("ADMINISTRAR USUARIOS") }
        }}
        Card(Modifier.fillMaxWidth().padding(top=12.dp),colors=CardDefaults.cardColors(containerColor=palette(themeName).panel)){Column(Modifier.padding(16.dp)){
            Text("📱 ALMACENAMIENTO LOCAL",fontWeight=FontWeight.Black,color=Green)
            Text("Clientes, productos, stock, ventas, deudas, caja, compras, proveedores, promociones, mesas, usuarios, reportes y movimientos se almacenan localmente dentro de la aplicación Android.",color=Muted,fontSize=12.sp)
        }}
    }
    if(showUsers){
        Dialog(onDismissRequest={showUsers=false}){
            Surface(modifier=Modifier.fillMaxSize(.96f),shape=RoundedCornerShape(24.dp),color=Bg){
                Column(Modifier.fillMaxSize()){
                    Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically){Text("USUARIOS / EMPLEADOS",fontWeight=FontWeight.Black,fontSize=20.sp,modifier=Modifier.weight(1f));IconButton(onClick={showUsers=false}){Icon(Icons.Default.Close,"Cerrar")}}
                    Users(api)
                }
            }
        }
    }
}
@Composable fun SimpleForm(title:String,fields:List<Pair<String,String>>,set:(Int,String)->Unit,onSave:()->Unit,onCancel:()->Unit,msg:String){Column(Modifier.fillMaxSize().padding(18.dp)){Text(title,fontSize=27.sp,fontWeight=FontWeight.Black);fields.forEachIndexed{i,p->Field(p.first,p.second,onChange={set(i,it)})};FButton(onClick=onSave,modifier=Modifier.fillMaxWidth().height(50.dp)){Text("GUARDAR")};OutlinedButton(onClick=onCancel,modifier=Modifier.fillMaxWidth()){Text("CANCELAR")};if(msg.isNotBlank())Text(msg,color=Color(0xFFFF8A80))}}
@OptIn(ExperimentalFoundationApi::class)
@Composable fun ListRow(title:String,sub:String,onClick:()->Unit,onLongClick:()->Unit={}){val p=MaterialTheme.colorScheme;Card(Modifier.fillMaxWidth().padding(vertical=4.dp).shadow(7.dp,RoundedCornerShape(16.dp)).combinedClickable(onClick={MiComercioSounds.tap(true);onClick()},onLongClick=onLongClick),colors=CardDefaults.cardColors(containerColor=p.surface),border=BorderStroke(1.dp,p.primary.copy(alpha=.12f))){Box(Modifier.background(Brush.linearGradient(listOf(p.surface,p.surface.copy(alpha=.82f),p.primary.copy(alpha=.08f))))){Column(Modifier.padding(14.dp)){Text(title,fontWeight=FontWeight.Bold);Text(sub,color=Muted,fontSize=12.sp)}}}}
fun sendReportEmail(context:android.content.Context,email:String,subject:String,body:String){if(email.isBlank())return;val i=Intent(Intent.ACTION_SENDTO).apply{data=android.net.Uri.parse("mailto:${android.net.Uri.encode(email.trim())}");putExtra(Intent.EXTRA_EMAIL,arrayOf(email.trim()));putExtra(Intent.EXTRA_SUBJECT,subject);putExtra(Intent.EXTRA_TEXT,body)};runCatching{context.startActivity(i)}}
fun num(s:String):Double{val t=s.trim().replace(" ","");return if(t.contains(","))t.replace(".","").replace(",",".").toDoubleOrNull()?:0.0 else t.toDoubleOrNull()?:0.0}
fun money(v:Double)="${String.format(Locale.US,"$%,.2f",v)}"
fun fmt(v:Double)=String.format(Locale.US,"%.2f",v)


/**
 * Launcher Activity for Mi Comercio.
 *
 * The previous source contained all of the Compose UI but did not declare
 * the Activity referenced by AndroidManifest.xml. Android could therefore
 * install the APK successfully, but the launcher crashed immediately with
 * ActivityNotFound/ClassNotFound when opening the icon.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManagerApp()
        }
    }
}
