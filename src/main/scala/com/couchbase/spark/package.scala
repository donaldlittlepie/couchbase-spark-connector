/**
 * Copyright (C) 2015 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */
package com.couchbase

import scala.reflect.ClassTag

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import com.couchbase.client.java.document.json.{JsonArray, JsonObject}
import com.couchbase.client.java.document.{JsonArrayDocument, JsonDocument, Document}

package object spark {

  implicit def toSparkContextFunctions(sc: SparkContext): SparkContextFunctions =
    new SparkContextFunctions(sc)
  implicit def toRDDFunctions[T](rdd: RDD[T]): RDDFunctions[T] = new RDDFunctions(rdd)
  implicit def toDocumentRDDFunctions[D <: Document[_]](rdd: RDD[D]): DocumentRDDFunctions[D] =
    new DocumentRDDFunctions(rdd)
  implicit def toPairRDDFunctions[V](rdd: RDD[(String, V)]): PairRDDFunctions[V] =
    new PairRDDFunctions(rdd)

  implicit object JsonObjectToJsonDocumentConverter
    extends DocumentConverter[JsonDocument, JsonObject] {
    def documentClassTag(ct: ClassTag[JsonObject]): ClassTag[JsonDocument] =
      implicitly[ClassTag[JsonDocument]]
    override def convert(id: String, content: JsonObject): JsonDocument =
      JsonDocument.create(id, content)
  }

  implicit object JsonArrayToJsonArrayDocumentConverter
    extends DocumentConverter[JsonArrayDocument, JsonArray] {
    def documentClassTag(ct: ClassTag[JsonArray]): ClassTag[JsonArrayDocument] =
      implicitly[ClassTag[JsonArrayDocument]]
    override def convert(id: String, content: JsonArray): JsonArrayDocument =
      JsonArrayDocument.create(id, content)
  }

  implicit object MapToJsonDocumentConverter
    extends DocumentConverter[JsonDocument, Map[String, _]] {
    def documentClassTag(ct: ClassTag[Map[String, _]]): ClassTag[JsonDocument] =
      implicitly[ClassTag[JsonDocument]]
    override def convert(id: String, content: Map[String, _]): JsonDocument = {
      val data = JsonObject.create()
      content.foreach(pair => data.put(pair._1, pair._2))
      JsonDocument.create(id, data)
    }
  }

  implicit object SeqToJsonArrayDocumentConverter
    extends DocumentConverter[JsonArrayDocument, Seq[_]] {
    def documentClassTag(ct: ClassTag[Seq[_]]): ClassTag[JsonArrayDocument] =
      implicitly[ClassTag[JsonArrayDocument]]
    override def convert(id: String, content: Seq[_]): JsonArrayDocument = {
      val data = JsonArray.create()
      content.foreach(item => data.add(item))
      JsonArrayDocument.create(id, data)
    }
  }
}
