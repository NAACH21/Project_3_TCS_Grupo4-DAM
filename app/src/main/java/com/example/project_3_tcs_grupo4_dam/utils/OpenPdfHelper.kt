package com.example.project_3_tcs_grupo4_dam.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object OpenPdfHelper {

    /**
     * Intenta abrir un PDF con un viewer externo.
     * Devuelve true si se lanzó un intent, false si no hay app que lo abra o ocurre un error.
     */
    fun abrirPdf(context: Context, file: File): Boolean {
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK

            // Comprobar si hay actividades que puedan manejar el intent
            val packageManager = context.packageManager
            val resolveInfo = packageManager.queryIntentActivities(intent, 0)
            if (resolveInfo.isNullOrEmpty()) {
                return false
            }

            // Otorgar permisos explícitos a cada actividad que pueda abrir el PDF
            resolveInfo.forEach { ri ->
                val pkg = ri.activityInfo.packageName
                context.grantUriPermission(pkg, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Usar chooser para que el usuario seleccione su app preferida
            val chooser = Intent.createChooser(intent, "Abrir PDF")
            chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(chooser)
            true
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
