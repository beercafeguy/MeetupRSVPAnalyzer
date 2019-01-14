package com.github.beercafeguy.rsvp.analytics

import java.util

import org.apache.kafka.clients.consumer.{OffsetAndMetadata, OffsetCommitCallback}
import org.apache.kafka.common.TopicPartition

class MeetupOffsetCommitCallback extends OffsetCommitCallback{
  override def onComplete(offsets: util.Map[TopicPartition, OffsetAndMetadata], exception: Exception): Unit = {
    println("-----------------------------------------------------")
    println("{0} | {1}",offsets,exception)
    println("-----------------------------------------------------")
  }
}
