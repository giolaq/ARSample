package com.laquysoft.arsample

import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

@DslMarker
annotation class ArDsl


@ArDsl
class NodeBuilder(var transformationSystem: TransformationSystem?, var model: Renderable?) {

    fun build(): Node {
        transformationSystem?.let {
            return TransformableNode(transformationSystem).apply {
                renderable = model
            }
        } ?: throw IllegalArgumentException("TransformationSystem cannot be null")
    }
}

@ArDsl
class RotatingNodeBuilder(var model: Renderable?) {
    internal val nodes = mutableListOf<com.google.ar.sceneform.Node>()

    fun build(): RotatingNode {
        return RotatingNode().apply { renderable = model }
    }
}

@ArDsl
class AnchorNodeBuilder(var anchor: Anchor?) {

    private val nodes = mutableListOf<com.google.ar.sceneform.Node>()

    fun build(): com.google.ar.sceneform.AnchorNode {
        anchor?.let {
            return AnchorNode(anchor).apply { nodes.forEach { it.setParent(this) } }
        } ?: throw IllegalArgumentException("Anchor cannot be null")
    }

    fun node(transformationSystem: TransformationSystem? = null, model: Renderable? = null, setup: NodeBuilder.() -> Unit = {}) {
        val nodeBuilder = NodeBuilder(transformationSystem, model)
        nodeBuilder.setup()
        nodes += nodeBuilder.build()
    }

    fun rotatingNode(model: Renderable? = null, setup: NodeBuilder.() -> Unit = {}) {
        // val nodeBuilder = NodeBuilder(model)
        // nodeBuilder.setup()
        // nodes += nodeBuilder.build()
    }
}

@ArDsl
class SceneBuilder {

    private val nodes = mutableListOf<Node>()

    operator fun AnchorNode.unaryPlus() {
        this@SceneBuilder.nodes += this
    }

    fun anchorNode(anchor: Anchor? = null, setup: AnchorNodeBuilder.() -> Unit = {}) {
        val nodeBuilder = AnchorNodeBuilder(anchor)
        nodeBuilder.setup()
        nodes += nodeBuilder.build()
    }

    fun node(transformationSystem: TransformationSystem? = null, model: Renderable? = null, setup: NodeBuilder.() -> Unit = {}) {
        val nodeBuilder = NodeBuilder(transformationSystem, model)
        nodeBuilder.setup()
        nodes += nodeBuilder.build()
    }

    fun build(): Scene {
        return Scene(nodes)
    }

}

fun scene(setup: SceneBuilder.() -> Unit): Scene {
    val sceneBuilder = SceneBuilder()
    sceneBuilder.setup()
    return sceneBuilder.build()
}
