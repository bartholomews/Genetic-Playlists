package model.genetic

/**
  *
  */
object GASettings {

  val maxFitness = 1.0

  // number of candidate playlists
  val popSize = 2048

  // Maximum number of generations
  val maxGen = 64

  // The probability of crossover for any member of the population,
  // where 0.0 <= crossoverRatio <= 1.0
  val crossoverRatio = 0.8f

  // The portion of the population that will be retained without change
  // between evolutions, where 0.0 <= elitismRatio < 1.0
  val elitismRatio = 0.1f   // 0.1f

  // The probability of mutation for any member of the population,
  // where 0.0 <= mutationRatio <= 1.0
  val mutationRatio = 0.4f // 0.03f

}
