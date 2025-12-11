package com.example.project_3_tcs_grupo4_dam.presentation.matching

import android.content.Context
import android.os.Environment
import com.example.project_3_tcs_grupo4_dam.data.model.ResultadoMatchingItem
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

object PdfReportHelper {

    // Color corporativo azul
    private val blue = BaseColor(10, 99, 194)

    // ======================================================
    // REPORTE DE MATCHING (cuando hay colaboradores)
    // ======================================================
    fun generarReporteMatching(
        context: Context,
        vacante: VacanteResponse,
        lista: List<ResultadoMatchingItem>
    ): File {

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "Reporte_Matching_${vacante.nombrePerfil}.pdf"
        )

        val document = Document(PageSize.A4, 36f, 36f, 36f, 36f)
        PdfWriter.getInstance(document, FileOutputStream(file))
        document.open()

        // ---------- TÍTULO ----------
        val titleFont = Font(Font.FontFamily.HELVETICA, 22f, Font.BOLD, blue)
        document.add(Paragraph("Reporte de Matching", titleFont))
        document.add(Paragraph("Vacante: ${vacante.nombrePerfil}", Font(Font.FontFamily.HELVETICA, 16f)))
        document.add(Paragraph(" "))
        document.add(Paragraph(" "))

        // ---------- INFORMACIÓN DE LA VACANTE ----------
        addVacanteInfo(document, vacante)

        document.add(Paragraph(" "))
        document.add(Paragraph(" "))

        // ---------- TABLA DE RESULTADOS ----------
        val table = PdfPTable(3)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(3f, 1.2f, 1.5f))

        val headerFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD, BaseColor.WHITE)

        fun createHeader(title: String): PdfPCell {
            return PdfPCell(Phrase(title, headerFont)).apply {
                backgroundColor = blue
                paddingTop = 8f
                paddingBottom = 8f
                horizontalAlignment = Element.ALIGN_CENTER
            }
        }

        table.addCell(createHeader("Colaborador"))
        table.addCell(createHeader("Match %"))
        table.addCell(createHeader("Movilidad"))

        lista.forEach { item ->
            table.addCell(PdfPCell(Phrase("${item.nombres} ${item.apellidos}")))
            table.addCell(PdfPCell(Phrase("%.1f".format(item.puntaje))))
            table.addCell(PdfPCell(Phrase(if (item.disponibleParaMovilidad) "Sí" else "No")))
        }

        document.add(table)
        document.close()

        return file
    }

    // ======================================================
    // REPORTE DE BRECHA DE SKILLS (cuando NO hay candidatos)
    // ======================================================
    fun generarReporteBrechaSkills(
        context: Context,
        vacante: VacanteResponse,
        umbral: Int
    ): File {

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "Brecha_Skills_${vacante.nombrePerfil}.pdf"
        )

        val document = Document(PageSize.A4, 36f, 36f, 36f, 36f)
        PdfWriter.getInstance(document, FileOutputStream(file))
        document.open()

        val titleFont = Font(Font.FontFamily.HELVETICA, 22f, Font.BOLD, blue)
        document.add(Paragraph("Reporte de Brecha de Skills", titleFont))
        document.add(Paragraph("Vacante: ${vacante.nombrePerfil}", Font(Font.FontFamily.HELVETICA, 16f)))
        document.add(Paragraph(" "))
        document.add(Paragraph(" "))

        addVacanteInfo(document, vacante)

        // Info adicional para la brecha:
        document.add(Paragraph(" "))
        document.add(Paragraph("Umbral utilizado: $umbral%", Font(Font.FontFamily.HELVETICA, 14f)))
        document.add(Paragraph("No se encontraron candidatos suficientes para cubrir los requisitos.\n"))

        document.close()
        return file
    }

    // ======================================================
    // SECCIÓN REUTILIZABLE: INFO DE LA VACANTE
    // ======================================================
    private fun addVacanteInfo(document: Document, vacante: VacanteResponse) {

        val subTitle = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD, blue)
        document.add(Paragraph("Información de la Vacante", subTitle))
        document.add(Paragraph("Área: ${vacante.area}"))
        document.add(Paragraph("Urgencia: ${vacante.urgencia ?: "-"}"))
        document.add(Paragraph(" "))

        // Skills críticas
        document.add(Paragraph("Skills críticas:", Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)))
        if (vacante.skillsRequeridos.any { it.esCritico }) {
            vacante.skillsRequeridos.filter { it.esCritico }.forEach {
                document.add(Paragraph("- ${it.nombre} (Nivel ${it.nivelDeseado})"))
            }
        } else {
            document.add(Paragraph("- Ninguna"))
        }

        document.add(Paragraph(" "))

        // Skills no críticas
        document.add(Paragraph("Skills no críticas:", Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)))
        if (vacante.skillsRequeridos.any { !it.esCritico }) {
            vacante.skillsRequeridos.filter { !it.esCritico }.forEach {
                document.add(Paragraph("- ${it.nombre} (Nivel ${it.nivelDeseado})"))
            }
        } else {
            document.add(Paragraph("- Ninguna"))
        }

        document.add(Paragraph(" "))

        // Certificaciones
        document.add(Paragraph("Certificaciones requeridas:", Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)))
        if (vacante.certificacionesRequeridas.isNotEmpty()) {
            vacante.certificacionesRequeridas.forEach {
                document.add(Paragraph("- $it"))
            }
        } else {
            document.add(Paragraph("- Ninguna"))
        }
    }
}
