//import sigmastate.eval.CostingSigmaDslBuilder.sigmaProp
//import special.collection.Coll
//import special.sigma.{Box, Context}
//
//val SELF: Box
//val CONTEXT: Context
//val OUTPUTS: Coll[Box]
//val INPUTS: Coll[Box]
//val max: (Long, Long) => Long
//val getVar: Int => Option[Int]

{
  val timeReached = CONTEXT.preHeader.timestamp > deadline

  publicKeyAlice || publicKeyBob && sigmaProp(timeReached)
}