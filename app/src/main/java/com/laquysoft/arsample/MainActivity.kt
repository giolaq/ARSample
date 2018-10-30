package com.laquysoft.arsample

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await

class MainActivity : AppCompatActivity() {

    private val GLTF_ASSET = "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf"
    private val TUI_PLANE = "TEST-02.sfb"
    private val POOL_HOUSE = "PoolGuestHouse.sfb"

    private val placeObjectJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + placeObjectJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            addObject(Uri.parse(POOL_HOUSE))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addObject(model: Uri) {
        val frame = (sceneformFragment as ArFragment).arSceneView.arFrame
        val pt = getScreenCenter()
        val hits: List<HitResult>
        if (frame != null) {
            hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    uiScope.launch {
                        placeObject((sceneformFragment as ArFragment), hit.createAnchor(), model)
                    }
                    break

                }
            }
        }
    }

    private suspend fun placeObject(fragment: ArFragment, anchor: Anchor, model: Uri) {
        val renderableFuture = ModelRenderable.builder()
                .setSource(fragment.context, model)
                .build()
        try {
            addNodeToScene(fragment, anchor, renderableFuture.await())
        } catch (e: Throwable) {
            AlertDialog.Builder(this@MainActivity)
                    .setMessage(e.message)
                    .setTitle("Codelab error!")
                    .create()
                    .show()
        }
    }

    private fun addNodeToScene(fragment: ArFragment, anchora: Anchor, renderable: Renderable) {
        val scene = scene {
            anchorNode {
                anchor = anchora
                node {
                    transformationSystem = fragment.transformationSystem
                    model = renderable
                }
            }
        }
        fragment.arSceneView.scene setTo scene
    }

    private fun getScreenCenter(): android.graphics.Point {
        val vw = findViewById<View>(android.R.id.content)
        return android.graphics.Point(vw.width / 2, vw.height / 2)
    }
}

private infix fun com.google.ar.sceneform.Scene.setTo(scene: Scene) {
    this.addChild(scene.nodes.first())
}
