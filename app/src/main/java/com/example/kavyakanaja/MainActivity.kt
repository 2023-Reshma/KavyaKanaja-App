package com.example.kavyakanaja
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.*
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.media.MediaPlayer
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
data class Poem(
    val title: String,
    val poet: String,
    val kannadaText: String,
    val englishMeaning: String,
    val audioFile: String,
    val difficultWord: String,
    val wordMeaning: String
)
fun loadPoems(context: android.content.Context): List<Poem> {
    val json = context.assets.open("poems.json")
        .bufferedReader()
        .use { it.readText() }

    val type = object : TypeToken<List<Poem>>() {}.type

    return Gson().fromJson(json, type)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
                    SplashScreen(navController)
                }

                composable("home") {
                    HomeScreen(navController)
                }

                composable("poems") {
                    PoemScreen(navController)
                }

                composable("fullpoem/{poemIndex}") { backStackEntry ->
                    val poemIndex =
                        backStackEntry.arguments?.getString("poemIndex")?.toIntOrNull() ?: 0

                    FullPoemScreen(
                        navController = navController,
                        poemIndex = poemIndex
                    )
                }
                composable("poets") {
                    PoetsCornerScreen(navController)
                }
                composable("poetDetail/{poetName}") { backStackEntry ->
                    val poetName = backStackEntry.arguments?.getString("poetName") ?: ""
                    PoetDetailScreen(navController, poetName)
                }
                composable("dailyPoem") {
                    com.example.kavyakanaja.DailyPoemScreen(navController)
                }

            }
        }
    }
}
@Composable
fun HomeScreen(navController: NavController) {

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF3E5F5),
                            Color(0xFFE1BEE7)
                        )
                    )
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "K\na\nV\ny\na\nK\na\nN\na\nJ\na",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8E24AA),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Kavya Kanaja",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A1B9A)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Old Poetry, New Energy ",
                fontSize = 18.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = { navController.navigate("poems") },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E24AA)
                ),
                modifier = Modifier
                    .height(55.dp)
                    .width(180.dp)
            ) {
                Text(
                    text = "Start Reading",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("dailyPoem")
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A1B9A)
                ),
                modifier = Modifier
                    .height(55.dp)
                    .width(180.dp)
            ) {
                Text(
                    text = "Poem of the Day",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("poets")
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A148C)
                ),
                modifier = Modifier
                    .height(55.dp)
                    .width(180.dp)
            ) {
                Text(
                    text = "Poet's Corner",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Explore beautiful Kannada stories and poems",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

        }
    }
}
@Composable
fun PoemScreen(navController: NavController) {
    var searchText by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    val poems = remember {
        loadPoems(context)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Poem Collection",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A1B9A)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search Poems") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        items(
            poems.filter {
                it.title.contains(searchText, ignoreCase = true) ||
                        it.poet.contains(searchText, ignoreCase = true)
            }
        ) { poem ->

            Card(
                onClick = {
                    val index = poems.indexOf(poem)
                    navController.navigate("fullpoem/$index")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = poem.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4A148C)
                    )

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = Color.Red
                    )
                }
            }
        }
    }


}
@Composable
fun FullPoemScreen(
    navController: NavController,
    poemIndex: Int
) {
    var isPlaying by remember {
        mutableStateOf(false)
    }
    var showMeaningDialog by remember { mutableStateOf(false) }
    var selectedWord by remember { mutableStateOf("") }
    var selectedMeaning by remember { mutableStateOf("") }
    val context = LocalContext.current
    val poems = remember {
        loadPoems(context)
    }

    val poem = poems.getOrElse(poemIndex) { poems.first() }

    val audioResId = context.resources.getIdentifier(
        poem.audioFile,
        "raw",
        context.packageName
    )

    val mediaPlayer = remember(audioResId) {
        if (audioResId != 0) {
            MediaPlayer.create(context, audioResId)
        } else {
            null
        }

    }
    DisposableEffect(mediaPlayer) {
        onDispose {
            mediaPlayer?.release()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = poem.title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A148C)
        )

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "✍ ${poem.poet}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A1B9A)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = poem.kannadaText,

            fontSize = 22.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Tap difficult word: ${poem.difficultWord}",
            fontSize = 18.sp,
            color = Color(0xFF6A1B9A),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                selectedWord = poem.difficultWord
                selectedMeaning = poem.wordMeaning
                showMeaningDialog = true
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = poem.englishMeaning,
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                if (isPlaying) {
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.start()
                }
                isPlaying = !isPlaying
            },

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8E24AA)
            )
        ) {

            Text(
                text = if (isPlaying)
                    "⏸ Pause Audio"
                else
                    "▶ Play Audio",

                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8E24AA)
            )
        ) {

            Text(
                text = "Back",
                color = Color.White
            )
        }
    }
    if (showMeaningDialog) {
        AlertDialog(
            onDismissRequest = {
                showMeaningDialog = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMeaningDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            title = {
                Text(selectedWord)
            },
            text = {
                Text(selectedMeaning)
            }
        )
    }
}

@Composable
fun SplashScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFCE93D8),
                        Color(0xFFF3E5F5)
                    )
                )
            ),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text =
                """
            K
            a
            V
            y
            a
            K
            a
            N
            a
            J
            a
            """.trimIndent(),

            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "KavyaKanaja",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Beautiful Kannada Poems",
            fontSize = 18.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                navController.navigate("home")
            },

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8E24AA)
            )
        ) {

            Text(
                text = "Enter App",
                color = Color.White
            )
        }
    }
}
@Composable
fun BottomBar(navController: NavController) {

    NavigationBar(
        containerColor = Color(0xFFE1BEE7)
    ) {

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("home")
            },

            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home"
                )
            },

            label = {
                Text("Home")
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("poems")
            },

            icon = {
                Icon(
                    Icons.Default.MenuBook,
                    contentDescription = "Poems"
                )
            },

            label = {
                Text("Poems")
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },

            icon = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorites"
                )
            },

            label = {
                Text("Favorites")
            }
        )
    }
}
fun getPoetDescription(poet: String): String {
    return when (poet) {

        "Kuvempu" ->
            "Kuppali Venkatappa Puttappa, widely known as Kuvempu, is one of the tallest figures in Kannada literature and a beacon of cultural pride for Karnataka. Honored with the title Rashtrakavi and recipient of the Jnanpith Award, Kuvempu believed that literature should elevate humanity and awaken universal values. His philosophy of 'Vishwa Manava' (Universal Human) encourages people to rise above caste, religion, and social divisions and embrace compassion and equality. His masterpiece Sri Ramayana Darshanam is considered one of the greatest epics in modern Indian literature, blending spirituality, philosophy, and poetic brilliance. Other notable works include Malegalalli Madumagalu, Kanooru Heggadithi, and numerous inspiring poems. If you want to experience the grandeur of Kannada language and the depth of human thought, reading Kuvempu is an unforgettable journey."

        "Da. Ra. Bendre" ->
            "Dattatreya Ramachandra Bendre, affectionately known as Da. Ra. Bendre, is celebrated as one of the most lyrical and imaginative poets in Kannada literature. A Jnanpith Award winner, his poetry combines music, mysticism, and emotion in a way that touches the soul. His most famous collection, Naaku Tanti, is regarded as a landmark in Kannada poetry and explores life, love, and spirituality with extraordinary depth. Works such as Gari and Sakhi Geethe reveal his mastery of language and emotion. Bendre’s poetry transforms ordinary moments into profound experiences, inspiring readers to see beauty and wonder in every aspect of life."

        "K. S. Narasimhaswamy" ->
            "K. S. Narasimhaswamy is cherished for capturing the tenderness of love and family life with remarkable simplicity. His iconic work Mysore Mallige remains one of the most beloved poetry collections in Kannada, portraying newly married life with warmth, innocence, and emotional honesty. His poetry shows that everyday experiences can be deeply poetic and meaningful. Reading Narasimhaswamy fills the heart with affection and reminds us that the quiet moments of life often carry the greatest beauty."

        "Masti Venkatesha Iyengar" ->
            "Masti Venkatesha Iyengar, fondly known as Masti, was a master storyteller, poet, and thinker whose contributions earned him the Jnanpith Award. His celebrated novel Chikaveera Rajendra and many short stories explore history, morality, and the complexities of human nature. His writing is elegant yet profound, offering insights into character, society, and timeless values. Masti’s works inspire readers to reflect deeply on life while appreciating the artistry of great storytelling."

        "Gopalakrishna Adiga" ->
            "Gopalakrishna Adiga was the pioneering force behind the Navya movement, which brought modernism to Kannada poetry. His influential collection Nadedu Banda Dari challenged traditional forms and introduced bold new themes about social change, personal identity, and the realities of contemporary life. Adiga’s poetry is intellectually stimulating and emotionally resonant, proving that literature can question, provoke, and inspire new ways of thinking."

        "B. M. Srikantaiah" ->
            "B. M. Srikantaiah was a visionary scholar and poet who played a transformative role in modernizing Kannada literature. Through works such as English Geetagalu, he introduced global literary influences while preserving the richness of Kannada expression. As an educator and translator, he opened new horizons for readers and writers alike. His legacy inspires us to embrace both tradition and innovation in the pursuit of knowledge."

        "D. V. Gundappa" ->
            "D. V. Gundappa, affectionately known as DVG, was a philosopher, poet, and public intellectual whose writings continue to guide generations. His timeless masterpiece Mankuthimmana Kagga offers practical wisdom on life, ethics, humility, and inner peace. Often compared to a spiritual handbook, this work speaks to readers of all ages with its clarity and depth. DVG’s literature encourages introspection and teaches that true success lies in wisdom and balanced living."

        "Panje Mangesha Rao" ->
            "Panje Mangesha Rao was a celebrated poet and educator who introduced generations of children to the magic of Kannada language. His delightful poems, including classics such as Huttidare Kannada Nadalli Huttabeku, are filled with rhythm, imagination, and love for the motherland. His works spark joy and curiosity, proving that poetry can educate while delighting the heart. Panje’s writings continue to inspire young readers to cherish their language and culture."

        else ->
            "This distinguished Kannada poet has made lasting contributions to the literary heritage of Karnataka. Through memorable works and inspiring ideas, their writing continues to connect readers with the beauty, wisdom, and emotional power of Kannada literature."
    }
}
@Composable
fun PoetsCornerScreen(navController: NavController) {
    val context = LocalContext.current
    val poems = remember { loadPoems(context) }
    val poets = poems.map { it.poet }.distinct().sorted()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F3FF))
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Poet's Corner",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A1B9A)
        )

        Spacer(modifier = Modifier.height(24.dp))

        poets.forEach { poet ->
            PoetCard(
                name = poet,
                description = getPoetDescription(poet),
                navController = navController
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8E24AA)
            )
        ) {
            Text("Back", color = Color.White)
        }
    }
}
@Composable
fun PoetCard(
    name: String,
    description: String,
    navController: NavController
)
{
    Card(
        onClick = {
            navController.navigate("poetDetail/$name")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A148C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}
@Composable
fun PoetDetailScreen(
    navController: NavController,
    poetName: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FF))
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = poetName,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A1B9A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = getPoetDescription(poetName),
            fontSize = 18.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Justify,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8E24AA)
            )
        ) {
            Text("Back", color = Color.White)
        }
    }
}
@Composable
fun DailyPoemScreen(navController: NavController) {

    val context = LocalContext.current

    val poems = remember {
        loadPoems(context)
    }

    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    val poem = poems[dayOfYear % poems.size]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🌟 Poem of the Day",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A1B9A)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = poem.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A148C),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "✍ ${poem.poet}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6A1B9A)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = poem.kannadaText,
            fontSize = 22.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = poem.englishMeaning,
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8E24AA)
            )
        ) {
            Text(
                text = "Back",
                color = Color.White
            )
        }
    }
}
