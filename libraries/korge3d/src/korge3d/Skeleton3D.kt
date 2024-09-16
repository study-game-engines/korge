package korlibs.korge3d

import korlibs.korge3d.internal.toFast
import korlibs.math.geom.MMatrix3D


open class Joint3D constructor(
	val jid: String,
	val jname: String,
	val jsid: String,
	val jointParent: Joint3D? = null,
	initialMatrix: MMatrix3D
) : Container3D() {
	init {
		this.transform.setMatrix(initialMatrix)
		this.name = jname
		this.id = jid
		if (jointParent != null) {
			this.parent = jointParent
		}

	}

	val poseMatrix = this.transform.globalMatrix.clone()
	val poseMatrixInv = poseMatrix.clone().invert()

	val childJoints = arrayListOf<Joint3D>()
	val descendants: List<Joint3D> get() = childJoints.flatMap { it.descendantsAndThis }
	val descendantsAndThis: List<Joint3D> get() = listOf(this) + descendants

	//val jointTransform = Transform3D()

	override fun toString(): String = "Joint3D(id=$jid, name=$name, sid=$jsid)"
}


data class Bone3D constructor(
	val index: Int,
	val name: String,
	val invBindMatrix: MMatrix3D
)


data class Skin3D(val bindShapeMatrix: MMatrix3D, val bones: List<Bone3D>) {
	val bindShapeMatrixInv = bindShapeMatrix.clone().invert()
	val matrices = Array(bones.size) { MMatrix3D() }
}


class Skeleton3D(val skin: Skin3D, val headJoint: Joint3D) : View3D() {
	val allJoints = headJoint.descendantsAndThis
	val jointsByName = allJoints.associateBy { it.jname }.toFast()
	val jointsBySid = allJoints.associateBy { it.jsid }.toFast()
}

