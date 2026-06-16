package graphApp.model.graph.repository

import graphApp.model.graph.*
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class SQLiteGraphRepository(dbPath: String = defaultDbPath()) : GraphRepository {

    private val url = "jdbc:sqlite:$dbPath"

    init {
        File(dbPath).parentFile?.mkdirs()
        connect().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS graphs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT UNIQUE NOT NULL
                    )
                """.trimIndent())
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS vertices (
                        graph_id  INTEGER NOT NULL,
                        vertex_id TEXT NOT NULL,
                        x REAL NOT NULL,
                        y REAL NOT NULL,
                        FOREIGN KEY (graph_id) REFERENCES graphs(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS edges (
                        graph_id INTEGER NOT NULL,
                        from_id TEXT NOT NULL,
                        to_id TEXT NOT NULL,
                        weight REAL NOT NULL,
                        edge_type TEXT NOT NULL,
                        FOREIGN KEY (graph_id) REFERENCES graphs(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                stmt.executeUpdate("PRAGMA foreign_keys = ON")
            }
        }
    }

    override fun save(graph: Graph<Edge>, name: String) {
        connect().use { conn ->
            conn.autoCommit = false
            try {
                // Удаление старой версии если есть
                val existingId = findGraphId(conn, name)
                if (existingId != null) {
                    conn.prepareStatement("DELETE FROM vertices WHERE graph_id = ?").use {
                        it.setInt(1, existingId); it.executeUpdate()
                    }

                    conn.prepareStatement("DELETE FROM edges WHERE graph_id = ?").use {
                        it.setInt(1, existingId); it.executeUpdate()
                    }

                    conn.prepareStatement("DELETE FROM graphs WHERE id = ?").use {
                        it.setInt(1, existingId); it.executeUpdate()
                    }
                }

                val graphId = insertGraph(conn, name)

                val insertVertex = conn.prepareStatement(
                    "INSERT INTO vertices (graph_id, vertex_id, x, y) VALUES (?, ?, ?, ?)"
                )
                graph.positions.forEach { (vertex, pos) ->
                    insertVertex.setInt(1, graphId)
                    insertVertex.setString(2, vertex.id)
                    insertVertex.setDouble(3, pos.x.toDouble())
                    insertVertex.setDouble(4, pos.y.toDouble())
                    insertVertex.addBatch()
                }
                insertVertex.executeBatch()

                val insertEdge = conn.prepareStatement(
                    "INSERT INTO edges (graph_id, from_id, to_id, weight, edge_type) VALUES (?, ?, ?, ?, ?)"
                )
                graph.edges.forEach { edge ->
                    insertEdge.setInt(1, graphId)
                    insertEdge.setString(2, edge.from.id)
                    insertEdge.setString(3, edge.to.id)
                    insertEdge.setDouble(4, edge.weight)
                    insertEdge.setString(5, edge.edgeType())
                    insertEdge.addBatch()
                }
                insertEdge.executeBatch()

                conn.commit()
            } catch (e: Exception) {
                conn.rollback()
                throw e
            }
        }
    }

    override fun load(name: String): Graph<Edge>? {
        connect().use { conn ->
            val graphId = findGraphId(conn, name) ?: return null
            val graph = Graph<Edge>()
            val vertexMap = mutableMapOf<String, Vertex>()

            conn.prepareStatement("SELECT vertex_id, x, y FROM vertices WHERE graph_id = ?").use { stmt ->
                stmt.setInt(1, graphId)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    val v = Vertex(rs.getString("vertex_id"))
                    graph.addVertex(v)
                    graph.setPosition(v, rs.getDouble("x").toFloat(), rs.getDouble("y").toFloat())
                    vertexMap[v.id] = v
                }
            }

            conn.prepareStatement(
                "SELECT from_id, to_id, weight, edge_type FROM edges WHERE graph_id = ?"
            ).use { stmt ->
                stmt.setInt(1, graphId)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    val from = vertexMap[rs.getString("from_id")] ?: continue
                    val to = vertexMap[rs.getString("to_id")] ?: continue
                    val weight = rs.getDouble("weight")
                    val edge: Edge = when (rs.getString("edge_type")) {
                        "directed" -> DirectedEdge(from, to)
                        "weighted" -> WeightedEdge(from, to, weight)
                        "directed_weighted" -> DirectedWeightedEdge(from, to, weight)
                        else -> Edge(from, to)
                    }
                    graph.addEdge(edge)
                }
            }

            return graph
        }
    }

    override fun listGraphs(): List<String> {
        connect().use { conn ->
            val result = mutableListOf<String>()
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT name FROM graphs ORDER BY rowid DESC")
                while (rs.next()) result.add(rs.getString("name"))
            }
            return result
        }
    }

    override fun delete(name: String) {
        connect().use { conn ->
            conn.prepareStatement("DELETE FROM graphs WHERE name = ?").use {
                it.setString(1, name)
                it.executeUpdate()
            }
        }
    }

    private fun connect(): Connection {
        Class.forName("org.sqlite.JDBC")
        return DriverManager.getConnection(url)
    }

    private fun findGraphId(conn: Connection, name: String): Int? {
        conn.prepareStatement("SELECT id FROM graphs WHERE name = ?").use { stmt ->
            stmt.setString(1, name)
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.getInt("id") else null
        }
    }

    private fun insertGraph(conn: Connection, name: String): Int {
        conn.prepareStatement("INSERT INTO graphs (name) VALUES (?)").use { stmt ->
            stmt.setString(1, name)
            stmt.executeUpdate()
        }
        return conn.createStatement().executeQuery("SELECT last_insert_rowid()").getInt(1)
    }

    private fun Edge.edgeType(): String = when (this) {
        is DirectedWeightedEdge -> "directed_weighted"
        is WeightedEdge -> "weighted"
        is DirectedEdge -> "directed"
        else -> "basic"
    }

    companion object {
        fun defaultDbPath(): String =
            "${System.getProperty("user.home")}/.graphs-on-kotlin/graphs.db"
    }
}
