package com.example.project_3_tcs_grupo4_dam.ui.colaborador.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_3_tcs_grupo4_dam.R

@Composable
fun HomeScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // =============================
        // HEADER: Perfil del colaborador
        // =============================
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // luego reemplazas por tu foto
                contentDescription = "Foto del colaborador",
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text("Mariana Ramírez", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Senior Frontend Developer", color = Color.Gray)
                Text("Desarrollo Web | Activo", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // =============================
        // PROGRESO GENERAL DE SKILLS
        // =============================
        Text("Tu Progreso de Skills", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = 0.80f,   // ejemplo
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text("80%", color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        // =============================
        // TARJETAS DE NIVELES
        // =============================
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LevelCard("6", "Avanzado", Color(0xFFDFF6DD))
            LevelCard("2", "Intermedio", Color(0xFFDCEFFF))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LevelCard("0", "Básico", Color(0xFFFFF6D8))
            LevelCard("0", "No iniciado", Color(0xFFF2F2F2))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =============================
        // BOTÓN VER TODOS MIS SKILLS
        // =============================
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* TODO navegar a Skills */ }
        ) {
            Text("Ver todos mis skills")
        }

        Spacer(modifier = Modifier.height(25.dp))

        // =============================
        // REQUERIMIENTOS URGENTES
        // =============================
        Text("Requerimientos Urgentes", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(10.dp))

        UrgentCard(
            title = "Debes actualizar 2 skills requeridos",
            detail = "Haz clic para ver detalles"
        )
    }
}


// ======================================================
// Componentes reutilizables
// ======================================================

@Composable
fun LevelCard(cantidad: String, label: String, color: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(160.dp)
            .height(90.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(cantidad, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.DarkGray)
        }
    }
}

@Composable
fun UrgentCard(title: String, detail: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8D8)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(detail, color = Color.DarkGray)
        }
    }
}
