package org.fayalite.aws

import com.amazonaws.services.ec2.model.Tag
import AWS._

import scala.collection.JavaConversions._


/**
 * Convenience methods for AWS
 */
object ServerManager {

  case class ServerStatus()

  /**
    * Prints out some standard stuff, mostly
    * for establishing cnnection and authentication.
    *
    * @return : Names of active ec2 machines.
    */
  def requestServerInfo() = {
    val inst = ec2.describeInstances()
    val names = inst.getReservations.flatMap{
      _.getInstances.flatMap {
        i =>
          // println(i.getLaunchTime)
          i.getTags.collectFirst { case t: Tag if t.getKey == "Name" =>
            t.getValue
          }
      }
    }.toList
    println("getServerInfo names: " + names)
    names.mkString(",")
  }

  def getInstances = {
    ec2.describeInstances().getReservations.flatMap{_.getInstances}
  }

  /**
    * Return every instance containing a tag exactly matching
    * supplied parameter
    *
    * @param t : Tag of instance by name or value.
    * @return matching instances
    */
  def getByTag(t: String) = {
    getInstances.withFilter({_.getTags.exists(z =>
      z.getKey == t || z.getValue == t)})
  }

}
