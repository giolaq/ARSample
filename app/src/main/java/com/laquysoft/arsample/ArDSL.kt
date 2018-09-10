package com.laquysoft.arsample

import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.Renderable

fun anchorNode(block: AnchorNodeBuilder.() -> Unit): AnchorNode = AnchorNodeBuilder().apply(block).build()


class AnchorNodeBuilder {

    lateinit var anchora: Anchor

    lateinit var toRender: Renderable

    lateinit var child: Node

    fun rotatingNode(block: RotatingNodeBuilder.() -> Unit): RotatingNode = RotatingNodeBuilder().apply(block).build()

    fun build(): AnchorNode = AnchorNode(anchora).apply {
        renderable = toRender
        addChild(child)
    }

}

class RotatingNodeBuilder {

    fun build(): RotatingNode = RotatingNode()

}