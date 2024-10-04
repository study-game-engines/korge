package com.esotericsoftware.spine.ext

import com.esotericsoftware.spine.Animation
import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.SkeletonData
import com.esotericsoftware.spine.korge.SkeletonView
import korlibs.math.geom.*

fun Animation.getAnimationMaxBounds(skeletonData: SkeletonData, out: Rectangle = Rectangle()): Rectangle {
    val animation: Animation = this
    val skeleton: Skeleton = Skeleton(skeletonData)
    val skeletonView: SkeletonView = SkeletonView(skeleton, null)
    var time: Float = 0f
    val bb: BoundsBuilder = BoundsBuilder()
    while (time < animation.duration) {
        animation.apply(skeleton, time, time, false, null, 1f, Animation.MixBlend.replace, Animation.MixDirection.`in`)
        skeleton.updateWorldTransform()
        bb.plus(skeletonView.getLocalBounds())
        time += 0.1f
    }
    return bb.bounds
}
