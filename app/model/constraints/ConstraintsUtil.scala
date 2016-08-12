package model.constraints

import model.music.{Attribute, AudioAttribute, Song}

/**
  *
  */
object ConstraintsUtil {

  /**
    *
    */
  def compare(x: Double, that: AudioAttribute, tolerance: Double): Boolean = {
    val distance = scala.math.abs(x - that.value)
    tolerance - distance >= 0
  }

  def compare(x: Double, that: AudioAttribute, f: Double => Boolean): Boolean = {
    f(x)
  }

  def compareWithTolerance(s: Song, that: AudioAttribute, tolerance: Double, f: Double => Boolean): Boolean = {
    s.attributes.find(a => a.getClass == that.getClass) match {
      case None => false
      case Some(attr) => attr.value match {
        case x: Number =>
          // if f => x == y (dynamically refactor for others)
          isWithinDistance(x.asInstanceOf[Double], that.value, tolerance)
        case z => throw new Exception(z + ": " + z.getClass + " is not a java.lang.Number")
      }
    }
  }

  def isWithinDistance(x: Double, y: Double, tolerance: Double): Boolean = {
    val distance = scala.math.abs(x - y)
    tolerance - distance >= 0
  }


  /*
  // ==============================================================================
*/

  def extractValues(s1: Song, s2: Song, that: Attribute): Option[(Double, Double)] = {
    extractValue(s1, that) match {
      case None => None
      case Some(x) => extractValue(s2, that) match {
        case None => None
        case Some(y) => Some(x, y)
      }
    }
  }

  /**
    * @see http://stackoverflow.com/a/16751674
    * @param s
    * @param that
    * @return
    */
  def extractValue(s: Song, that: Attribute): Option[Double] = {
    s.attributes.find(a => a.getClass == that.getClass) match {
      case None => None
      case Some(attr) => attr.value match {
        case x: Number => Some(x.asInstanceOf[Double])
        case _ => None
      }
    }
  }

  // NOT USED, CONSIDER TO REMOVE
  def compareWithTolerance(s: Song, that: AudioAttribute, tolerance: Double, penalty: Double): (Boolean, Double) = {
    extractValue(s, that) match {
      case None => (false, penalty)
      case Some(x) =>
        val distance = scala.math.abs(x - that.value)
        if (distance <= tolerance) (true, 0.0) else (false, distance) // + penalty)
    }
  }

  def compareDistance(s: Song, that: AudioAttribute, f: (Double, Double) => Boolean): (Boolean, Double) = {
    extractValue(s, that) match {
      case None => (false, Double.MaxValue)
      case Some(x) => // if(f(x)) 0.0 else
        monotonicDistance(x, that.value, f(x, that.value))
    }
  }

  // either increasing or decreasing
  def monotonicDistance(x: Double, y: Double, f: Boolean): (Boolean, Double) = {
    // distance rounded to the nearest hundredth: TODO some features might need higher precision
    val distance = scala.math.abs(x - y)
    // if y is <= of x for Increasing and vice-versa for Decreasing,
    // the distance is added to a penalty value to impact a negative score
    if (!f) (false, distance) // + penalty)
    else (true, 0.0) // distance)
  }

}


  /*
  // this MIGHT work for equals f, but what about > and < ?
  def compareScore(s: Song, that: AudioAttribute, tolerance: Double, f: (Double, Double) => Boolean): Score = {
    s.attributes.find(a => a.getClass == that.getClass) match {
      case None => Score(matched = false, 1)  // 1 to low, could be even a good score
      case Some(attr) => attr.value match {
        case x: Number => calculateScoreEquals(x.asInstanceOf[Double], that.value, tolerance, f)
        case z => throw new Exception(z + ": " + z.getClass + " is not a java.lang.Number")
      }
    }
  }

  // nononononno
  private def calculateScoreEquals(x: Double, y: Double, tolerance: Double, f: (Double, Double) => Boolean) = {
    val cost = scala.math.abs(x - y); val fitness = tolerance - cost
    if(fitness >= 0) Score(matched = true, cost)
    else Score(matched = false, cost)
  }

  // ScoreGreater should have a worst fitness the more is greater than the value, and be double that bad going other way
  // so that a 1.2 > 1.1 should be the best match, 1.3 > 1.1 should have cost 0.1; 0.8 > 1.0 false(0.3)
  private def calculateScoreGreatLess(x: Double, y: Double, f: (Double, Double) => Boolean) = {
    val fitness =
    if(f(x,y)) Score(matched = false, y + 0.1 - x)
    else Score(matched = true, y - x + 0.1)

  }

  // cost = 2.2

}

*/
