package com.example.transiciones

import android.app.Activity
import android.os.Bundle
import android.transition.ChangeClipBounds
import android.transition.Fade
import android.transition.Fade.IN
import android.transition.Fade.OUT
import android.transition.Scene
import android.transition.Scene.getSceneForLayout
import android.transition.Slide
import android.transition.TransitionManager.go
import android.transition.TransitionSet
import android.transition.TransitionSet.ORDERING_TOGETHER
import android.view.Gravity.END
import android.view.Gravity.START
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import com.example.transiciones.R.id.animatedFirstButton
import com.example.transiciones.R.id.animatedFirstImageButton
import com.example.transiciones.R.id.animatedHeader
import com.example.transiciones.R.id.animatedProfileImageView
import com.example.transiciones.R.id.animatedSearchField
import com.example.transiciones.R.id.animatedSecondImageButton
import com.example.transiciones.R.id.animatedListView
import com.example.transiciones.R.layout.activity_main
import com.example.transiciones.R.layout.scene_1
import com.example.transiciones.R.layout.scene_2

class MainActivity : Activity() {
    // Obtiene la vista del encabezado mediante su ID
    private val headerView: ViewGroup?
        get() = findViewById(animatedHeader)

    // Crea una escena de entrada utilizando el layout scene_1
    private val enterScene: Scene by lazy {
        getSceneForLayout(headerView, scene_1, this)
    }

    // Crea una escena de salida utilizando el layout scene_2
    private val exitScene: Scene by lazy {
        getSceneForLayout(headerView, scene_2, this)
    }

    // Lista que contiene todas las escenas disponibles
    private val allScenes: List<Scene> by lazy {
        listOf(enterScene, exitScene)
    }

    // Define un conjunto de transiciones que se aplicarán entre escenas
    private val transitionSet: TransitionSet
        get() = TransitionSet()
            .setOrdering(ORDERING_TOGETHER) // Las transiciones se ejecutarán juntas
            .setDuration(500) // Duración total de las transiciones
            .addTransition(
                Slide(END) // Transición de deslizamiento hacia el final
                    .setDuration(500)
                    .addTarget(animatedFirstButton) // Elemento objetivo
            )
            .addTransition(
                Slide(END) // Transición de deslizamiento para botones adicionales
                    .setDuration(500)
                    .addTarget(animatedFirstImageButton)
                    .addTarget(animatedSecondImageButton)
            )
            .addTransition(
                Slide(START) // Transición de deslizamiento desde el inicio
                    .setDuration(500)
                    .addTarget(animatedProfileImageView)
            )
            .addTransition(
                Fade(OUT) // Transición de desvanecimiento hacia afuera
                    .setDuration(500)
                    .addTarget(animatedFirstImageButton)
                    .addTarget(animatedSecondImageButton)
                    .addTarget(animatedProfileImageView)
            )
            .addTransition(
                Fade(IN) // Transición de desvanecimiento hacia adentro
                    .setDuration(500)
                    .addTarget(animatedFirstButton)
            )
            .addTransition(
                Fade(OUT) // Desvanecer el botón de inicio
                    .setDuration(500)
                    .addTarget(animatedFirstButton)
            )
            .addTransition(
                ChangeClipBounds() // Cambiar límites del clip
                    .setDuration(400)
                    .setStartDelay(100) // Retraso antes de iniciar esta transición
                    .setInterpolator(LinearInterpolator()) // Interpolador lineal
                    .addTarget(animatedSearchField) // Elemento objetivo
            )
            .addTransition(
                Fade(IN) // Desvanecer el ListView al entrar
                    .setDuration(500)
                    .addTarget(animatedListView)
            )

    // Método llamado al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main) // Establece el layout de la actividad
        setupScenes() // Configura las escenas
    }

    // Configura las escenas y establece las acciones al entrar
    private fun setupScenes() {
        allScenes.forEach { scene ->
            scene.setEnterAction { onEnterScene(scene) } // Define la acción al entrar a la escena
        }
        enterScene.enter() // Entra en la escena inicial
    }

    // Maneja la animación de transición entre escenas basado en el foco
    private fun animateTransition(hasFocus: Boolean) {
        if (hasFocus)
            go(exitScene, transitionSet) // Sale a la escena de salida
        else
            go(enterScene, transitionSet) // Regresa a la escena de entrada
    }

    // Maneja el cambio de foco del SearchView
    private fun onFocusChanged(hasFocus: Boolean) {
        println("MainActivity -> onFocusChanged ($hasFocus)")
        animateTransition(hasFocus) // Llama a la función de animación
    }

    // Maneja la acción cuando se inicia la búsqueda
    private fun onStartSearchIntent() {
        println("MainActivity -> onStartSearchIntent")
        animateTransition(true) // Cambia a la escena de salida
    }

    // Maneja la acción cuando se cierra la búsqueda
    private fun onCloseSearchIntent(): Boolean {
        println("MainActivity -> onCloseSearchIntent")
        animateTransition(false) // Regresa a la escena de entrada
        return true
    }

    // Configura las acciones al entrar en una escena
    private fun onEnterScene(scene: Scene) {
        println("MainActivity -> onEnterScene (${scene.sceneRoot?.id})")

        with(scene.sceneRoot) {
            // Configura el SearchView y sus listeners
            findViewById<SearchView>(animatedSearchField)?.run {
                setOnQueryTextFocusChangeListener { _, hasFocus ->
                    onFocusChanged(hasFocus) // Cambia foco
                }
                setOnSearchClickListener { onStartSearchIntent() } // Inicia búsqueda
                setOnCloseListener { onCloseSearchIntent() } // Cierra búsqueda
            }

            // Configura el botón para regresar a la escena anterior
            findViewById<Button>(animatedFirstButton)
                ?.setOnClickListener { animateTransition(false) } // Regresa a la escena de entrada

            // Configura el ListView al entrar en la escena de salida
            if (scene == exitScene) {
                val listView = findViewById<ListView>(animatedListView)
                val items = listOf(
                    "Hello Kitty", "My Melody", "Badtz-Maru", "Keroppi",
                    "Chococat", "Kuromi", "Cinnamoroll", "Pompompurin",
                    "Kiki", "Lala", "Tuxedosam"
                ) // Lista de personajes

                // Usa un ArrayAdapter para llenar el ListView
                val adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_list_item_1,
                    items
                )
                listView?.adapter = adapter // Asigna el adaptador al ListView

                // Configura el listener para manejar clics en los elementos de la lista
                listView?.setOnItemClickListener { _, _, position, _ ->
                    val personajeSeleccionado = items[position]
                    // Muestra un Toast al seleccionar un personaje
                    Toast.makeText(
                        this@MainActivity,
                        "Has seleccionado a $personajeSeleccionado y es muy lindo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}



