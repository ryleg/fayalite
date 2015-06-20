package org.fayalite.aws

import AWS._

import java.util

import com.amazonaws.ClientConfiguration
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.regions.{Region, ServiceAbbreviations, Regions}
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.{Tag, LaunchSpecification, RequestSpotInstancesRequest, RunInstancesRequest}

import scala.collection.JavaConversions._
import com.amazonaws.auth.{EnvironmentVariableCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth._

import scala.io.Source
import scala.util.Try


/**
 * This doesn't really live up to it's name.
 */
object ServerManager {

  case class ServerStatus()

  def requestServerInfo() = {
    val inst = ec2.describeInstances()
    val names = inst.getReservations.flatMap{
      _.getInstances.flatMap {
        i =>
          // println(i.getLaunchTime)
          i.getTags.collectFirst { case t: Tag if t.getKey == "Name" => t.getValue}
      }
    }.toList
    println("getServerInfo names: " + names)
    names.mkString(",")
  }

}
