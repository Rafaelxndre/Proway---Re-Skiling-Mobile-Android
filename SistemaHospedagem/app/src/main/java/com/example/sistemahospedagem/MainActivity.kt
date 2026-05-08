package com.example.sistemahospedagem

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.example.sistemahospedagem.ui.theme.SistemaHospedagemTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.ui.draw.alpha
import com.example.sistemahospedagem.R
import androidx.compose.ui.graphics.vector.ImageVector

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaHospedagemTheme(darkTheme = true) {
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
                TelaAtracoes(navController = navController)
            }

            composable("detalhe_atracao") {
                atracaoSelecionada?.let { atracao ->
                    TelaDetalheAtracao(
                        atracao = atracao,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable("comentarios") {
                TelaComentarios()
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
            selected = currentRoute == "comentarios",
            onClick = { navController.navigate("comentarios") },
            icon = { Icon(Icons.Default.Info, null) },
            label = { Text("Comentários") },

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
fun TelaAtracoes(navController: NavController) {
    val atracoes = listOf(
        AtracaoUi(
            tipo = TipoAtracao.ATRACAO,
            badge = "Atração",
            tituloSecao = "Disponível Agora",
            titulo = "Degustação de Vinhos",
            subtitulo = "Rótulos selecionados com sommelier e harmonização especial.",
            jogo = "Adega Panorâmica",
            imagem = R.drawable.vinho
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Evento Noturno",
            tituloSecao = "Hoje às 20h",
            titulo = "Noite de Fondue & Jazz",
            subtitulo = "Menu completo com música ao vivo no lounge principal.",
            jogo = "Lounge Aurora",
            imagem = R.drawable.fundue
        ),
        AtracaoUi(
            tipo = TipoAtracao.ATRACAO,
            badge = "Atração",
            tituloSecao = "Todos os dias",
            titulo = "Passeio de Jet Ski na Neve",
            subtitulo = "Circuito guiado com segurança e paisagens incríveis.",
            jogo = "Lago Congelado",
            imagem = R.drawable.nordica
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Evento Especial",
            tituloSecao = "Acontecendo Agora",
            titulo = "Piscina Termal",
            subtitulo = "Experiência térmica com vista para as montanhas geladas.",
            jogo = "Spa Alpino",
            imagem = R.drawable.termal
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Evento Especial",
            tituloSecao = "Acontecendo Agora",
            titulo = "Massagem a dois",
            subtitulo = "Um ritual a dois de toque, silêncio e profundas sensações.",
            jogo = "Santuário Alpino",
            imagem = R.drawable.massagem
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Evento Especial",
            tituloSecao = "Acontecendo Agora",
            titulo = "Bar de whisky e charutos",
            subtitulo = "Um refúgio de silêncio, madeira, fogo e sabores intensos.",
            jogo = "Velvet Room",
            imagem = R.drawable.barwhisky
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Evento Especial",
            tituloSecao = "Acontecendo Agora",
            titulo = "Passeio de helicóptero",
            subtitulo = "Uma experiência aérea sobre paisagens que cortam a respiração.",
            jogo = "Heliponto",
            imagem = R.drawable.helicopter
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Evento Especial",
            tituloSecao = "Acontecendo Agora",
            titulo = "Snowshoe walk ao luar",
            subtitulo = "Uma travessia silenciosa sob a luz da lua, onde cada passo se transforma em memória.",
            jogo = "Trilha Lunar",
            imagem = R.drawable.snowshoe
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Evento Especial",
            tituloSecao = "Acontecendo Agora",
            titulo = "Sessão privada de yoga",
            subtitulo = "Um encontro íntimo entre respiração, silêncio e paisagens que acalmam a alma.",
            jogo = "Aurora Zen",
            imagem = R.drawable.yoga
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Baile de Máscaras Exclusivo",
            subtitulo = "Uma noite de mistério, elegância e encanto digna de um conto de fadas.",
            jogo = "Véu Real ",
            imagem = R.drawable.mask
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Teatro Musical",
            subtitulo = "Onde música, história e emoção se encontram como em um filme inesquecível.",
            jogo = "Salão do Encanto",
            imagem = R.drawable.teatro
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Café Colonial",
            subtitulo = "Comece o dia envolvido pelo frio da paisagem e o calor dos sentidos.",
            jogo = "Pavilhão Alpino",
            imagem = R.drawable.cafecolonial
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Patinação no Gelo",
            subtitulo = "Viva a magia do inverno sobre o gelo.",
            jogo = "Lago da Lua",
            imagem = R.drawable.patinacao
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Jantar em iglu",
            subtitulo = "Um jantar íntimo em meio ao charme do inverno.",
            jogo = "Vila dos Iglus",
            imagem = R.drawable.iglu
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Passeio de trenó",
            subtitulo = "Uma jornada encantadora guiada pela força e beleza dos cães do inverno.",
            jogo = "Vale Polar",
            imagem = R.drawable.treno
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Cassino",
            subtitulo = " Glamour, jogos e emoção em uma atmosfera exclusiva.",
            jogo = "Salão Royale",
            imagem = R.drawable.cassino
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Fogueira night",
            subtitulo = "Conforto, histórias e sabores ao redor da fogueira.",
            jogo = "Vale da Aurora",
            imagem = R.drawable.fogueira
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Cinema",
            subtitulo = "Um filme quentinho para noites frias.",
            jogo = "Velvet Cine",
            imagem = R.drawable.cinema
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Jantar a luz de velas",
            subtitulo = "Um jantar íntimo iluminado pelo romance.",
            jogo = "La Belle Nuit",
            imagem = R.drawable.jantarvela
        ),
        AtracaoUi(
            tipo = TipoAtracao.EVENTO,
            badge = "Em Alta",
            tituloSecao = "Acontecendo Agora",
            titulo = "Passeio a cavalo",
            subtitulo = "Uma aventura inesquecível sobre a neve.",
            jogo = "Vale gelado",
            imagem = R.drawable.horsesneve
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
                text = "Atualizações da semana",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(atracoes) { atracao ->
            CardAtracao(atracao = atracao, navController = navController)
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

private var atracaoSelecionada: AtracaoUi? = null

@Composable
private fun CardAtracao(atracao: AtracaoUi, navController: NavController) {
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
                    painter = painterResource(id = R.drawable.logo),
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
                onClick = {
                    atracaoSelecionada = atracao
                    navController.navigate("detalhe_atracao")
                },
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
private fun TelaDetalheAtracao(
    atracao: AtracaoUi,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
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
                                0f to Color.Black.copy(alpha = 0.25f),
                                0.6f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = Color(0xFF1A2535),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF39C7C8),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = atracao.badge,
                    color = Color(0xFF39C7C8),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = atracao.tituloSecao,
                color = Color(0xFFB0B0B0),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = atracao.titulo,
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = atracao.subtitulo,
                color = Color(0xFFD9D9D9),
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF39C7C8),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = atracao.jogo,
                    color = Color(0xFFE3EAF4),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(8.dp))

            HorizontalDivider(thickness = 1.dp, color = Color(0xFF262626))

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF334255),
                    contentColor = Color(0xFFEAF1FB)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = if (atracao.tipo == TipoAtracao.ATRACAO) "Confirmar Agendamento" else "Confirmar Interesse",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun TelaComentarios() {
    val comentarios = listOf(
        ComentarioUi(
            nome = "Cristiano Ronaldo",
            tempoComoHospede = "4 anos como hóspede",
            resumo = "janeiro de 2026 · Ficou uma noite",
            texto = "Experiência simplesmente impecável! O chalé é um refúgio de paz, com uma vista deslumbrante. A acomodação é perfeita: extremamente limpa, decorada com muito bom gosto e equipada com tudo o que precisávamos para uma estadia confortável. Acordar com aquele cenário foi revigorante. Cada detalhe mostra o carinho dos anfitriões. Recomendo de olhos fechados e já planejo a volta!",
            foto = R.drawable.perfil_cristiano
        ),
        ComentarioUi(
            nome = "Robert Downey Jr",
            tempoComoHospede = "3 anos como hóspede",
            resumo = "janeiro de 2026 · Ficou algumas noites",
            texto = "Local extremamente lindo, uma vista espetacular sem palavras, acomodação muito aconchegante, atendimento maravilhoso sempre pronto a nos atender. Experiência excelente, espero voltar com os amigos em breve.",
            foto = R.drawable.perfil_dois
        ),
        ComentarioUi(
            nome = "Elon Musk",
            tempoComoHospede = "3 anos como hóspede",
            resumo = "janeiro de 2026 · Ficou algumas noites",
            texto = "Local extremamente lindo, uma vista espetacular sem palavras, acomodação muito aconchegante, atendimento maravilhoso sempre pronto a nos atender. Experiência excelente, espero voltar com os amigos em breve.",
            foto = R.drawable.perfil_tres
        ),
        ComentarioUi(
            nome = "Sylvester Stallone",
            tempoComoHospede = "3 anos como hóspede",
            resumo = "janeiro de 2026 · Ficou algumas noites",
            texto = "Local extremamente lindo, uma vista espetacular sem palavras, acomodação muito aconchegante, atendimento maravilhoso sempre pronto a nos atender. Experiência excelente, espero voltar com os amigos em breve.",
            foto = R.drawable.perfil_quinto
        ),
        ComentarioUi(
            nome = "Michael B. Jordan",
            tempoComoHospede = "3 anos como hóspede",
            resumo = "janeiro de 2026 · Ficou algumas noites",
            texto = "Local extremamente lindo, uma vista espetacular sem palavras, acomodação muito aconchegante, atendimento maravilhoso sempre pronto a nos atender. Experiência excelente, espero voltar com os amigos em breve.",
            foto = R.drawable.perfi_quatro
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "66 avaliações",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .border(1.dp, Color(0xFF2A3341), RoundedCornerShape(28.dp))
                            .background(Color(0xFF111111))
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mais relevantes",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF111111))
                            .border(1.dp, Color(0xFF2A3341), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        itemsIndexed(comentarios) { index, comentario ->
            ComentarioItem(comentario = comentario)

            if (index < comentarios.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 20.dp),
                    thickness = 1.dp,
                    color = Color(0xFF262626)
                )
            }
        }
    }
}

private data class ComentarioUi(
    val nome: String,
    val tempoComoHospede: String,
    val resumo: String,
    val texto: String,
    val foto: Int
)

@Composable
private fun ComentarioItem(comentario: ComentarioUi) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = comentario.foto),
                contentDescription = comentario.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = comentario.nome,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = comentario.tempoComoHospede,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text(
            text = "★★★★★ · ${comentario.resumo}",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal
        )

        Text(
            text = comentario.texto,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun TelaFaq() {
    val secoesFaq = listOf(
        FaqSecaoUi(
            icone = Icons.Default.Info,
            titulo = "Política de cancelamento",
            detalhes = listOf(
                "Adicione as datas de viagem para obter as informações de cancelamento dessa reserva."
            )
        ),
        FaqSecaoUi(
            icone = Icons.Default.Home,
            titulo = "Regras da casa",
            detalhes = listOf(
                "Check-in: 15:00 - 02:00",
                "Checkout: 11:00",
                "Self check-in com fechadura inteligente",
                "Proibido fumar",
                "Não permite animais de estimação"
            )
        ),
        FaqSecaoUi(
            icone = Icons.Default.Star,
            titulo = "Segurança e propriedade",
            detalhes = listOf(
                "Alarme de monóxido de carbono não informado",
                "Detector de fumaça não informado",
                "Câmeras de segurança na parte externa da propriedade"
            )
        ),
        FaqSecaoUi(
            icone = Icons.Default.Info,
            titulo = "Saúde e segurança",
            detalhes = listOf(
                "Compromisso com o protocolo de higienização. Saiba mais",
                "Seguimos diretrizes de segurança sanitária e recomendações de bem-estar para todos os hóspedes",
                "Alarme de monóxido de carbono"
            )
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Text(
            text = "O que você precisa saber",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(24.dp))

        secoesFaq.forEachIndexed { index, secao ->
            FaqSecaoItem(secao = secao)

            if (index < secoesFaq.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 18.dp),
                    thickness = 1.dp,
                    color = Color(0xFF262626)
                )
            }
        }
    }
}

private data class FaqSecaoUi(
    val icone: ImageVector,
    val titulo: String,
    val detalhes: List<String>
)

@Composable
private fun FaqSecaoItem(secao: FaqSecaoUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = secao.icone,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(24.dp)
        )

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = secao.titulo,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(6.dp))

            secao.detalhes.forEach { detalhe ->
                Text(
                    text = detalhe,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(24.dp)
        )
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
    val mostrarTextoCompleto = remember { mutableStateOf(false) }
    val titleTextColor = Color.White
    val supportingTextColor = Color.LightGray
    val bodyTextColor = Color.White
    val dividerColor = Color(0xFF262626)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp)
            .background(Color.Black)
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
                    tint = Color.White,
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
                text = "Espaço inteiro · Hospedagem de luxo",
                style = MaterialTheme.typography.bodyMedium,
                color = supportingTextColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Ideal para grupos · Até 12 pessoas",
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
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("5.0", fontWeight = FontWeight.Normal, color = Color.White)
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

            HorizontalDivider(thickness = 1.4.dp, color = dividerColor)
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
            text = "O Proway Retreat é mais do que um destino é uma experiência criada para marcar a vida. " +
                    "Um refúgio onde cada detalhe convida você a desacelerar, sentir e compartilhar momentos " +
                    "que ganham significado quando vividos ao lado de quem está com você.",
            style = MaterialTheme.typography.bodyMedium,
            color = bodyTextColor,
            lineHeight = 24.sp
        )

        if (mostrarTextoCompleto.value) {
            Spacer(Modifier.height(14.dp))

            Text(
                text = "As noites se transformam em lembranças inesquecíveis ao som suave do jazz, acompanhadas por " +
                        "degustações de vinhos que prolongam conversas, olhares e conexões. Entre romance e adrenalina, " +
                        "surgem aventuras únicas, como explorar paisagens cobertas de neve sobre duas rodas, " +
                        "despertando emoções que ficam para sempre.",
                style = MaterialTheme.typography.bodyMedium,
                color = bodyTextColor,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = "E quando o silêncio pede passagem, as piscinas termais com vista para as montanhas oferecem " +
                        "paz, calor e tranquilidade absoluta. Um lugar para se reconectar, criar memórias profundas e " +
                        "viver uma experiência tão especial que você vai querer voltar e nunca esquecer.",
                style = MaterialTheme.typography.bodyMedium,
                color = bodyTextColor,
                lineHeight = 24.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { mostrarTextoCompleto.value = !mostrarTextoCompleto.value },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE6E6E6),
                contentColor = Color(0xFF202020)
            )
        ) {
            Text(
                text = if (mostrarTextoCompleto.value) "Mostrar menos" else "Saiba mais",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(thickness = 1.4.dp, color = dividerColor)

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
                    val uri = "geo:-23.5505,-46.6333?q=São+Paulo".toUri()
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

            HorizontalDivider(
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

