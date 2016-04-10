package org.fayalite.layer

object API {

  case class DataReport(
                               numRows: Int,
                               numCols: Int,
                               reportString: String
                               )

  case class Header(
                     columnOffsetId: Int,
                     original: String,
                     sanitized: String,
                     dataType: String
                     )

  case class Edge(
                   srcId: Int,
                   dstId: Int,
                   relationship: String,
                   weight: Float
                   )

  case class ColumnBundle(
                           id: Int,
                           groupings: Array[Edge],
                           description: String,
                           weight: Float
                           )

  case class SchemaTriplets(
                             dataEdge: Edge,
                             Edge: Edge
                             )

  case class MetaProperties(
                             id: Int,
                             name: String,
                             description: String,
                             format: String,
                             columnBundles: Array[ColumnBundle]
  )

  case class DataProperties(
                             metaProperties: MetaProperties,
                             headers: Array[Header],
                             report: DataReport
                             )

  case class DataListResponse(
                               data: Array[DataProperties]
                               )

  case class DataUpdateRequest(
                                metaProperties: MetaProperties,
                                headerDataTypes: Array[String]
                                )




}
