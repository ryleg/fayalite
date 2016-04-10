package org.fayalite.ml

/**
  * For a collection of N entities
  * we must define an 'ordering' of dimension Q
  * Such that any Swap(N_i, N_j) yields a Score(State_0, State_1)
  * such that there is an 'ordering' of states according to score
  *
  * If every entity were a discrete voxel row with a heightmap, then
  * re-arranging the order of rows represents minimizing possible elevation
  * differences to yield a maximally continuous surface
  */
trait CurvatureMinimization[EntityType] {

}
