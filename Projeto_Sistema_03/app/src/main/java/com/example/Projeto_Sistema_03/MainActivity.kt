package com.example.projeto_sistema_03

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.projeto_sistema_03.ui.theme.Exercicio1_navegacaoTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.ui.draw.alpha
import com.example.exercicio1_navegacao.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Exercicio1_navegacaoTheme(darkTheme = true) {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // ✅ Estado compartilhado
    val modoReservaAtivo = remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {

            // 🔹 MENU PADRÃO
            AnimatedVisibility(
                visible = !modoReservaAtivo.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                BottomMenu(navController)
            }

            // 🔹 RODAPÉ DE RESERVA (ocupa TODO o espaço do menu)
            AnimatedVisibility(
                visible = modoReservaAtivo.value,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                BottomBarReserva(
                    preco = "R$ 1.250",
                    onReservarClick = {
                        // fluxo de reserva
                    }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "lista",
            modifier = Modifier.padding(paddingValues)
        ) {

            composable("lista") {
                TelaHeroComBottomSheet(
                    modoReservaAtivo = modoReservaAtivo
                )
            }

            composable("atracoes") {
                TelaAtracoes()
            }

            composable("feed") {
                TelaFeed()
            }

            composable("faq") {
                TelaFaq()
            }
        }
    }
}


@Composable
fun BottomMenu(navController: NavController) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val colorScheme = MaterialTheme.colorScheme
    val selectedMenuColor = Color.White

    NavigationBar(
        containerColor = colorScheme.background
    ) {

        NavigationBarItem(
            selected = currentRoute == "lista",
            onClick = {
                navController.navigate("lista") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Início") },

            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedMenuColor,
                selectedTextColor = selectedMenuColor,
                unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                indicatorColor = selectedMenuColor.copy(alpha = 0.2f)
            )

        )

        NavigationBarItem(
            selected = currentRoute == "atracoes",
            onClick = { navController.navigate("atracoes") },
            icon = { Icon(Icons.Default.Star, null) },
            label = { Text("Atrações") },

            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedMenuColor,
                selectedTextColor = selectedMenuColor,
                unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                indicatorColor = selectedMenuColor.copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "feed",
            onClick = { navController.navigate("feed") },
            icon = { Icon(Icons.Default.Info, null) },
            label = { Text("Feed") },

            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedMenuColor,
                selectedTextColor = selectedMenuColor,
                unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                indicatorColor = selectedMenuColor.copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "faq",
            onClick = { navController.navigate("faq") },
            icon = { Icon(Icons.Default.Email, null) },
            label = { Text("FAQ") },

            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedMenuColor,
                selectedTextColor = selectedMenuColor,
                unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                indicatorColor = selectedMenuColor.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
fun TelaAtracoes() {
    val atracoes = listOf(
        AtracaoUi(
            tipo = TipoAtracao.ATRACAO,
            badge = "Atração",
            tituloSecao = "Disponível Agora",
            titulo = "Degustação de Vinhos",
            subtitulo = "Rótulos selecionados com sommelier e harmonização especial.",
            jogo = "Adega Panorâmica",
            imagem = R.drawable.principal
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Evento Especial",
            tituloSecao = "Acontecendo Agora",
            titulo = "Sauna na Neve",
            subtitulo = "Experiência térmica com vista para as montanhas geladas.",
            jogo = "Spa Alpino",
            imagem = R.drawable.vingadores
        ),
        AtracaoUi(
            tipo = TipoAtracao.ATRACAO,
            badge = "Atração",
            tituloSecao = "Todos os dias",
            titulo = "Passeio de Jet Ski na Neve",
            subtitulo = "Circuito guiado com segurança e paisagens incríveis.",
            jogo = "Lago Congelado",
            imagem = R.drawable.gladiador
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Evento Noturno",
            tituloSecao = "Hoje às 20h",
            titulo = "Noite de Fondue & Jazz",
            subtitulo = "Menu completo com música ao vivo no lounge principal.",
            jogo = "Lounge Aurora",
            imagem = R.drawable.dark
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Atualizações e eventos",
                color = Color(0xFF39C7C8),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Atualizações dos seus jogos",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(atracoes) { atracao ->
            CardAtracao(atracao = atracao)
        }
    }
}

private data class AtracaoUi(
    val tipo: TipoAtracao,
    val badge: String,
    val tituloSecao: String,
    val titulo: String,
    val subtitulo: String,
    val jogo: String,
    val imagem: Int
)

private enum class TipoAtracao {
    ATRACAO,
    EVENTO
}

@Composable
private fun CardAtracao(atracao: AtracaoUi) {
    val textoBotao = if (atracao.tipo == TipoAtracao.ATRACAO) "Agendar" else "Saiba mais"

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
        ) {
            Image(
                painter = painterResource(id = atracao.imagem),
                contentDescription = atracao.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Black.copy(alpha = 0.18f),
                                0.65f to Color(0xFF090909).copy(alpha = 0.72f),
                                1f to Color(0xFF111111)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = atracao.badge,
                        color = Color(0xFFEDEDED),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column {
                    Text(
                        text = atracao.tituloSecao,
                        color = Color(0xFFD6D6D6),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = atracao.titulo,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = atracao.subtitulo,
                        color = Color(0xFFD9D9D9),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF12161D))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = atracao.jogo,
                    color = Color(0xFFE3EAF4),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF334255),
                    contentColor = Color(0xFFEAF1FB)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = textoBotao,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TelaFeed() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Tela Feed")
    }
}

@Composable
fun TelaFaq() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Tela FAQ")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHeroComBottomSheet(
    modoReservaAtivo: MutableState<Boolean>
) {

    val scaffoldState = rememberBottomSheetScaffoldState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val sheetPeekHeight = screenHeight * 0.46f

    val bottomSheetState = scaffoldState.bottomSheetState

    val isFullyExpanded by remember {
        derivedStateOf {
            bottomSheetState.currentValue == SheetValue.Expanded &&
                    bottomSheetState.targetValue == SheetValue.Expanded
        }
    }

    LaunchedEffect(isFullyExpanded) {
        modoReservaAtivo.value = isFullyExpanded
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 🔥 Imagem HERO
        Image(
            painter = painterResource(id = R.drawable.principal),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.26f)
                        )
                    )
                )
        )

        // ✅ BottomSheet
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetPeekHeight,
            containerColor = Color.Transparent,
            sheetContainerColor = Color.Transparent,
            sheetShape = RoundedCornerShape(
                topStart = 28.dp,
                topEnd = 28.dp
            ),
            sheetDragHandle = null,
            sheetContent = {
                ConteudoBottomSheet()
            }
        ) {
            // conteúdo vazio
        }
    }
}

@Composable
fun ConteudoBottomSheet() {

    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val titleTextColor = colorScheme.onSurface
    val supportingTextColor = colorScheme.onSurfaceVariant
    val bodyTextColor = colorScheme.onSurface
    val dividerColor = colorScheme.outline

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp)
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to colorScheme.surfaceVariant,
                        1.0f to colorScheme.surface
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // Puxador
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .alpha(0f)
        )

        Spacer(Modifier.height(0.dp))

        // ===== HEADER =====
        Column(modifier = Modifier.fillMaxWidth()) {

            Text(
                text = "Proway Retreat",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = titleTextColor,
                maxLines = 2
            )

            Spacer(Modifier.height(1.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = colorScheme.secondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "São Paulo, São Paulo",
                    style = MaterialTheme.typography.bodySmall,
                    color = supportingTextColor
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Espaço inteiro · Hotel em São Paulo, Brasil",
                style = MaterialTheme.typography.bodyMedium,
                color = supportingTextColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "12 hóspedes · 6 quartos · 10 camas · 6,5 banheiros",
                style = MaterialTheme.typography.bodySmall,
                color = supportingTextColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("5.0", fontWeight = FontWeight.Normal, color = colorScheme.onSurface)
                Spacer(Modifier.width(6.dp))
                Text("·", color = supportingTextColor)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "1.000.000 avaliações",
                    color = supportingTextColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))

            Divider(thickness = 1.4.dp, color = dividerColor)
        }

        // ===== SOBRE ESTE ESPAÇO =====
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Sobre este espaço",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = titleTextColor
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text =
                "Hotel com decoração de luxo situado em meio à neve, suspenso em altas montanhas e cercado por paisagens deslumbrantes. " +
                        "O Proway Retreat oferece uma vista de tirar o fôlego, combinando conforto, exclusividade e contato direto com a natureza.",
            style = MaterialTheme.typography.bodyMedium,
            color = bodyTextColor,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(16.dp))
        Divider(thickness = 1.4.dp, color = dividerColor)

        // ===== LOCALIZAÇÃO NO MAPA =====
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Localização no Mapa",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = titleTextColor
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    val uri = Uri.parse("geo:-23.5505,-46.6333?q=São+Paulo")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    context.startActivity(intent)
                }
        ) {
            // Imagem do mapa
            Image(
                painter = painterResource(id = R.drawable.navigator),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Marcador em cima da imagem
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                    tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(22.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "A localização exata será compartilhada após a reserva.",
            style = MaterialTheme.typography.bodySmall,
            color = supportingTextColor
        )

    }
//    Spacer(Modifier.height(10.dp))
}

@Composable
fun BottomBarReserva(
    preco: String,
    onReservarClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0F131A),
        tonalElevation = 2.dp,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
        ) {

            Divider(
                thickness = 1.dp,
                color = Color(0xFF2A3341)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = preco,
                        color = Color(0xFFE8EEF8),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "por noite",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB3C0D3)
                    )
                }

                Button(
                    onClick = onReservarClick,
                    modifier = Modifier
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF334255),
                        contentColor = Color(0xFFEAF1FB)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        text = "Reservar",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

