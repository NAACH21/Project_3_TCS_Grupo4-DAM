package com.example.project_3_tcs_grupo4_dam.presentation.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_3_tcs_grupo4_dam.R
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDashboard
import com.example.project_3_tcs_grupo4_dam.data.repository.AlertasRepository
import com.example.project_3_tcs_grupo4_dam.presentation.notificaciones.NotificacionesAdapter
import com.example.project_3_tcs_grupo4_dam.presentation.dashboard.adapters.BrechasAdapter
import com.example.project_3_tcs_grupo4_dam.presentation.dashboard.adapters.TopSkillsAdapter
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch

class DashboardAdminFragment : Fragment() {

    companion object {
        private const val TAG = "DashboardAdminFragment"
    }

    // Inicialización directa del ViewModel para evitar dependencias de fábrica en este paso.
    private val viewModel: DashboardViewModel = DashboardViewModel()

    private lateinit var tvVacantesAbiertas: TextView
    private lateinit var tvTotalVacantes: TextView
    private lateinit var tvKpiMatchValue: TextView
    private lateinit var progressMatch: ProgressBar
    private lateinit var rvTopSkills: RecyclerView
    private lateinit var rvBrechas: RecyclerView
    private lateinit var progressLoading: ProgressBar
    private lateinit var btnNotifications: ImageButton

    private val alertasRepository = AlertasRepository()

    private lateinit var topSkillsAdapter: TopSkillsAdapter
    private lateinit var brechasAdapter: BrechasAdapter

    // NUEVO: Gráficos
    private lateinit var pieChartAreas: PieChart
    private lateinit var barChartSkills: HorizontalBarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated - Inicializando vistas")

        tvVacantesAbiertas = view.findViewById(R.id.tvVacantesAbiertas)
        tvTotalVacantes = view.findViewById(R.id.tvTotalVacantes)
        tvKpiMatchValue = view.findViewById(R.id.tvKpiMatchValue)
        progressMatch = view.findViewById(R.id.progressMatch)
        rvTopSkills = view.findViewById(R.id.rvTopSkills)
        rvBrechas = view.findViewById(R.id.rvBrechas)
        progressLoading = view.findViewById(R.id.progressLoading)
        btnNotifications = view.findViewById(R.id.btnNotifications)

        // NUEVO: Inicializar gráficos
        pieChartAreas = view.findViewById(R.id.pieChartAreas)
        barChartSkills = view.findViewById(R.id.barChartSkills)

        topSkillsAdapter = TopSkillsAdapter(emptyList())
        brechasAdapter = BrechasAdapter(emptyList())

        context?.let { ctx ->
            rvTopSkills.layoutManager = LinearLayoutManager(ctx)
            rvBrechas.layoutManager = LinearLayoutManager(ctx)
        }
        rvTopSkills.adapter = topSkillsAdapter
        rvBrechas.adapter = brechasAdapter

        Log.d(TAG, "RecyclerViews configurados")

        // NUEVO: Configurar estilos de los gráficos
        setupPieChart()
        setupBarChart()

        observarViewModel()

        // Cargar métricas al mostrar el fragment
        Log.d(TAG, "Cargando métricas...")
        viewModel.cargarMetricas()

        // Manejar clic en campana de notificaciones
        btnNotifications.setOnClickListener {
            btnNotifications.isEnabled = false
            lifecycleScope.launch {
                val result = alertasRepository.obtenerDashboardAdmin()
                btnNotifications.isEnabled = true
                if (result.isSuccess) {
                    val lista = result.getOrNull() ?: emptyList()
                    mostrarDialogAlertas(lista)
                } else {
                    val ex = result.exceptionOrNull()
                    Toast.makeText(requireContext(), "Error al obtener alertas: ${ex?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun mostrarDialogAlertas(alertas: List<AlertaDashboard>) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_alertas_list, null)
        val rv = dialogView.findViewById<RecyclerView>(R.id.rvAlertasDialog)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = NotificacionesAdapter { alerta ->
            // Al hacer click en una alerta: por ahora mostrar Toast
            Toast.makeText(requireContext(), alerta.titulo, Toast.LENGTH_SHORT).show()
        }
        rv.adapter = adapter
        // NotificacionesAdapter es ListAdapter, usamos submitList
        adapter.submitList(alertas)

        AlertDialog.Builder(requireContext())
            .setTitle("Alertas pendientes")
            .setView(dialogView)
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun observarViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            Log.d(TAG, "Estado del ViewModel: ${state.javaClass.simpleName}")

            when (state) {
                is DashboardState.Loading -> {
                    Log.d(TAG, "Mostrando loading...")
                    progressLoading.visibility = View.VISIBLE
                }
                is DashboardState.Success -> {
                    Log.d(TAG, "Datos recibidos exitosamente")
                    progressLoading.visibility = View.GONE
                    bindData(state.data)
                }
                is DashboardState.Error -> {
                    Log.e(TAG, "Error al cargar dashboard: ${state.message}")
                    progressLoading.visibility = View.GONE
                    tvKpiMatchValue.text = "Error"
                    Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun bindData(data: com.example.project_3_tcs_grupo4_dam.data.model.dashboard.DashboardData) {
        Log.d(TAG, "bindData - Vinculando datos...")

        data.metricasVacantes?.let { mv ->
            Log.d(TAG, "Vacantes - Total: ${mv.totalVacantes}, Abiertas: ${mv.vacantesAbiertas}")
            tvTotalVacantes.text = mv.totalVacantes.toString()
            tvVacantesAbiertas.text = mv.vacantesAbiertas.toString()

            // NUEVO: Cargar gráfico de áreas si hay datos disponibles
            mv.vacantesPorArea?.let { areas ->
                if (areas.isNotEmpty()) {
                    loadPieChartData(areas)
                }
            }
        } ?: Log.w(TAG, "metricasVacantes es null")

        data.metricasMatching?.let { mm ->
            val porcentaje = (mm.porcentajeMatchPromedio * 100).toInt()
            Log.d(TAG, "Match promedio: $porcentaje%")
            tvKpiMatchValue.text = "$porcentaje%"
            progressMatch.progress = porcentaje
        } ?: Log.w(TAG, "metricasMatching es null")

        data.skillsMasDemandados?.let { skills ->
            Log.d(TAG, "Skills recibidos: ${skills.size} items")
            if (skills.isEmpty()) {
                Log.w(TAG, "Lista de skills está vacía")
            } else {
                skills.forEachIndexed { index, skill ->
                    Log.d(TAG, "Skill $index: ${skill.nombreSkill} - ${skill.cantidadVacantes}")
                }
                // NUEVO: Cargar gráfico de barras
                loadBarChartData(skills)
            }
            topSkillsAdapter.update(skills)
            rvTopSkills.visibility = if (skills.isEmpty()) View.GONE else View.VISIBLE
        } ?: run {
            Log.w(TAG, "skillsMasDemandados es null")
            rvTopSkills.visibility = View.GONE
        }

        data.brechasPrioritarias?.let { brechas ->
            Log.d(TAG, "Brechas recibidas: ${brechas.size} items")
            if (brechas.isEmpty()) {
                Log.w(TAG, "Lista de brechas está vacía")
            } else {
                brechas.forEachIndexed { index, brecha ->
                    Log.d(TAG, "Brecha $index: ${brecha.nombreSkill} - ${brecha.brechaPromedio}")
                }
            }
            brechasAdapter.update(brechas)
            rvBrechas.visibility = if (brechas.isEmpty()) View.GONE else View.VISIBLE
        } ?: run {
            Log.w(TAG, "brechasPrioritarias es null")
            rvBrechas.visibility = View.GONE
        }

        Log.d(TAG, "bindData completado")
    }

    // ==================== NUEVO: Configuración de Gráficos ====================

    /**
     * Configura el estilo y comportamiento del PieChart
     */
    private fun setupPieChart() {
        pieChartAreas.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawHoleEnabled(true)
            setHoleColor(android.graphics.Color.WHITE)
            holeRadius = 40f
            transparentCircleRadius = 45f
            setDrawCenterText(true)
            centerText = "Áreas"
            setEntryLabelColor(android.graphics.Color.BLACK)
            setEntryLabelTextSize(12f)

            // Configurar leyenda
            legend.apply {
                isEnabled = true
                textSize = 12f
                form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE
                horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            }

            // Deshabilitar rotación
            isRotationEnabled = false
        }
    }

    /**
     * Configura el estilo y comportamiento del HorizontalBarChart
     */
    private fun setupBarChart() {
        barChartSkills.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)

            // Configurar eje X (arriba)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textSize = 12f
                textColor = android.graphics.Color.BLACK
            }

            // Configurar eje Y izquierdo
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = android.graphics.Color.LTGRAY
                gridLineWidth = 0.5f
                axisMinimum = 0f
                textSize = 12f
            }

            // Deshabilitar eje Y derecho
            axisRight.isEnabled = false

            // Configurar leyenda
            legend.isEnabled = false

            // Animación
            animateY(1400)
        }
    }

    /**
     * Carga los datos del Map<String, Int> en el PieChart
     * @param vacantesPorArea Map con el nombre del área y cantidad de vacantes
     */
    private fun loadPieChartData(vacantesPorArea: Map<String, Int>) {
        Log.d(TAG, "Cargando PieChart con ${vacantesPorArea.size} áreas")

        val entries = ArrayList<PieEntry>()

        vacantesPorArea.forEach { (area, cantidad) ->
            entries.add(PieEntry(cantidad.toFloat(), area))
            Log.d(TAG, "PieChart - Área: $area, Cantidad: $cantidad")
        }

        if (entries.isEmpty()) {
            Log.w(TAG, "No hay datos para el PieChart")
            return
        }

        val dataSet = PieDataSet(entries, "Vacantes por Área").apply {
            colors = com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 14f
            valueTextColor = android.graphics.Color.BLACK
            sliceSpace = 2f

            // Formatter para mostrar valores enteros
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
        }

        val pieData = PieData(dataSet)
        pieChartAreas.data = pieData
        pieChartAreas.invalidate() // Refrescar gráfico
        pieChartAreas.animateY(1400)

        Log.d(TAG, "PieChart cargado exitosamente")
    }

    /**
     * Carga los datos de skills en el HorizontalBarChart
     * @param skills Lista de SkillDemandadoDto
     */
    private fun loadBarChartData(skills: List<com.example.project_3_tcs_grupo4_dam.data.model.dashboard.SkillDemandadoDto>) {
        Log.d(TAG, "Cargando BarChart con ${skills.size} skills")

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        skills.forEachIndexed { index, skill ->
            entries.add(BarEntry(index.toFloat(), skill.cantidadVacantes.toFloat()))
            labels.add(skill.nombreSkill)
            Log.d(TAG, "BarChart - Skill: ${skill.nombreSkill}, Cantidad: ${skill.cantidadVacantes}")
        }

        if (entries.isEmpty()) {
            Log.w(TAG, "No hay datos para el BarChart")
            return
        }

        val dataSet = BarDataSet(entries, "Skills Demandados").apply {
            // Color corporativo TCS (Azul #0D47A1)
            color = android.graphics.Color.parseColor("#0D47A1")
            valueTextSize = 12f
            valueTextColor = android.graphics.Color.BLACK

            // Formatter para mostrar valores enteros
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.8f

        // Configurar labels del eje X con los nombres de skills
        barChartSkills.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChartSkills.xAxis.labelCount = labels.size

        barChartSkills.data = barData
        barChartSkills.invalidate() // Refrescar gráfico

        Log.d(TAG, "BarChart cargado exitosamente")
    }
}
