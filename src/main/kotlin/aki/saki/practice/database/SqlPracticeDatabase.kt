package aki.saki.practice.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bson.Document
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.util.concurrent.CompletableFuture

/**
 * Persistência relacional (MySQL ou PostgreSQL): cada documento é guardado como JSON numa coluna TEXT.
 * Ordenação em [profileFindSorted] espelha o modo FLAT_FILE (carrega perfis e ordena em memória).
 */
class SqlPracticeDatabase(
    private val plugin: JavaPlugin,
    private val dialect: SqlDialect,
    jdbcUrl: String,
    username: String,
    password: String,
    poolSize: Int
) : PracticeDatabase {

    private val dataSource: HikariDataSource = HikariConfig().apply {
        this.jdbcUrl = jdbcUrl
        this.username = username
        this.password = password
        this.maximumPoolSize = poolSize.coerceAtLeast(2)
        this.minimumIdle = 1
        this.poolName = "AkiPractice-SQL"
        driverClassName = dialect.driverClass
        addDataSourceProperty("cachePrepStmts", "true")
        if (dialect == SqlDialect.MYSQL) {
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        }
    }.let { HikariDataSource(it) }

    override fun initialize(): CompletableFuture<Void> = CompletableFuture.runAsync {
        dataSource.connection.use { conn ->
            conn.createStatement().use { st ->
                for (ddl in dialect.ddlStatements) {
                    st.execute(ddl)
                }
            }
        }
        plugin.logger.info("[Database] Modo ${dialect.name} ativo: ${maskJdbcUrl(dataSource.jdbcUrl)}")
    }

    override fun profileFindById(id: String): Document? {
        return dataSource.connection.use { conn ->
            conn.prepareStatement("SELECT json_data FROM profiles WHERE id = ?").use { ps ->
                ps.setString(1, id)
                ps.executeQuery().use { rs ->
                    if (!rs.next()) return@use null
                    parseJson(rs.getString(1))
                }
            }
        }
    }

    override fun profileUpsert(document: Document, async: Boolean) {
        val id = document.getString("_id") ?: return
        val json = document.toJson()
        val op = Runnable {
            dataSource.connection.use { conn ->
                dialect.profileUpsert(conn, id, json)
            }
        }
        if (async) CompletableFuture.runAsync(op) else op.run()
    }

    override fun profileDelete(id: String) {
        dataSource.connection.use { conn ->
            conn.prepareStatement("DELETE FROM profiles WHERE id = ?").use { ps ->
                ps.setString(1, id)
                ps.executeUpdate()
            }
        }
    }

    override fun profileListAll(): List<Document> {
        val out = mutableListOf<Document>()
        dataSource.connection.use { conn ->
            conn.createStatement().use { st ->
                st.executeQuery("SELECT json_data FROM profiles").use { rs ->
                    while (rs.next()) {
                        parseJson(rs.getString(1))?.let { out.add(it) }
                    }
                }
            }
        }
        return out
    }

    override fun profileFindSorted(sortKey: String, descending: Boolean, limit: Int): List<Document> {
        val all = profileListAll()
        val sorted = if (descending) {
            all.sortedWith(
                compareByDescending<Document> { ProfileSortKeyExtractor.asDouble(it, sortKey) }
                    .thenBy { it.getString("_id") ?: "" }
            )
        } else {
            all.sortedWith(
                compareBy<Document> { ProfileSortKeyExtractor.asDouble(it, sortKey) }
                    .thenBy { it.getString("_id") ?: "" }
            )
        }
        return sorted.take(limit)
    }

    override fun arenaRatingUpsert(document: Document) {
        val uuid = document.getString("uuid") ?: return
        val json = document.toJson()
        CompletableFuture.runAsync {
            dataSource.connection.use { conn ->
                dialect.arenaRatingUpsert(conn, uuid, json)
            }
        }
    }

    override fun arenaRatingListAll(): List<Document> {
        val out = mutableListOf<Document>()
        dataSource.connection.use { conn ->
            conn.createStatement().use { st ->
                st.executeQuery("SELECT json_data FROM arena_ratings").use { rs ->
                    while (rs.next()) {
                        parseJson(rs.getString(1))?.let { out.add(it) }
                    }
                }
            }
        }
        return out
    }

    override fun campUpsert(document: Document) {
        val id = document.getString("_id") ?: return
        val json = document.toJson()
        dataSource.connection.use { conn ->
            dialect.campUpsert(conn, id, json)
        }
    }

    override fun campDelete(id: String) {
        dataSource.connection.use { conn ->
            conn.prepareStatement("DELETE FROM camps WHERE id = ?").use { ps ->
                ps.setString(1, id)
                ps.executeUpdate()
            }
        }
    }

    override fun campListAll(): List<Document> {
        val out = mutableListOf<Document>()
        dataSource.connection.use { conn ->
            conn.createStatement().use { st ->
                st.executeQuery("SELECT json_data FROM camps").use { rs ->
                    while (rs.next()) {
                        parseJson(rs.getString(1))?.let { out.add(it) }
                    }
                }
            }
        }
        return out
    }

    override fun close() {
        if (!dataSource.isClosed) {
            dataSource.close()
        }
    }

    private fun parseJson(raw: String?): Document? {
        if (raw.isNullOrBlank()) return null
        return try {
            Document.parse(raw)
        } catch (_: Exception) {
            null
        }
    }

    enum class SqlDialect {
        MYSQL {
            override val driverClass: String = "com.mysql.cj.jdbc.Driver"
            override val ddlStatements: List<String> = listOf(
                """CREATE TABLE IF NOT EXISTS profiles (
                    id VARCHAR(64) NOT NULL PRIMARY KEY,
                    json_data LONGTEXT NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4""",
                """CREATE TABLE IF NOT EXISTS arena_ratings (
                    uuid VARCHAR(64) NOT NULL PRIMARY KEY,
                    json_data LONGTEXT NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4""",
                """CREATE TABLE IF NOT EXISTS camps (
                    id VARCHAR(128) NOT NULL PRIMARY KEY,
                    json_data LONGTEXT NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"""
            )

            override fun profileUpsert(conn: Connection, id: String, json: String) {
                conn.prepareStatement(
                    """INSERT INTO profiles (id, json_data) VALUES (?, ?)
                       ON DUPLICATE KEY UPDATE json_data = VALUES(json_data)"""
                ).use { ps ->
                    ps.setString(1, id)
                    ps.setString(2, json)
                    ps.executeUpdate()
                }
            }

            override fun arenaRatingUpsert(conn: Connection, uuid: String, json: String) {
                conn.prepareStatement(
                    """INSERT INTO arena_ratings (uuid, json_data) VALUES (?, ?)
                       ON DUPLICATE KEY UPDATE json_data = VALUES(json_data)"""
                ).use { ps ->
                    ps.setString(1, uuid)
                    ps.setString(2, json)
                    ps.executeUpdate()
                }
            }

            override fun campUpsert(conn: Connection, id: String, json: String) {
                conn.prepareStatement(
                    """INSERT INTO camps (id, json_data) VALUES (?, ?)
                       ON DUPLICATE KEY UPDATE json_data = VALUES(json_data)"""
                ).use { ps ->
                    ps.setString(1, id)
                    ps.setString(2, json)
                    ps.executeUpdate()
                }
            }
        },
        POSTGRESQL {
            override val driverClass: String = "org.postgresql.Driver"
            override val ddlStatements: List<String> = listOf(
                """CREATE TABLE IF NOT EXISTS profiles (
                    id VARCHAR(64) NOT NULL PRIMARY KEY,
                    json_data TEXT NOT NULL
                )""",
                """CREATE TABLE IF NOT EXISTS arena_ratings (
                    uuid VARCHAR(64) NOT NULL PRIMARY KEY,
                    json_data TEXT NOT NULL
                )""",
                """CREATE TABLE IF NOT EXISTS camps (
                    id VARCHAR(128) NOT NULL PRIMARY KEY,
                    json_data TEXT NOT NULL
                )"""
            )

            override fun profileUpsert(conn: Connection, id: String, json: String) {
                conn.prepareStatement(
                    """INSERT INTO profiles (id, json_data) VALUES (?, ?)
                       ON CONFLICT (id) DO UPDATE SET json_data = EXCLUDED.json_data"""
                ).use { ps ->
                    ps.setString(1, id)
                    ps.setString(2, json)
                    ps.executeUpdate()
                }
            }

            override fun arenaRatingUpsert(conn: Connection, uuid: String, json: String) {
                conn.prepareStatement(
                    """INSERT INTO arena_ratings (uuid, json_data) VALUES (?, ?)
                       ON CONFLICT (uuid) DO UPDATE SET json_data = EXCLUDED.json_data"""
                ).use { ps ->
                    ps.setString(1, uuid)
                    ps.setString(2, json)
                    ps.executeUpdate()
                }
            }

            override fun campUpsert(conn: Connection, id: String, json: String) {
                conn.prepareStatement(
                    """INSERT INTO camps (id, json_data) VALUES (?, ?)
                       ON CONFLICT (id) DO UPDATE SET json_data = EXCLUDED.json_data"""
                ).use { ps ->
                    ps.setString(1, id)
                    ps.setString(2, json)
                    ps.executeUpdate()
                }
            }
        };

        abstract val driverClass: String
        abstract val ddlStatements: List<String>
        abstract fun profileUpsert(conn: Connection, id: String, json: String)
        abstract fun arenaRatingUpsert(conn: Connection, uuid: String, json: String)
        abstract fun campUpsert(conn: Connection, id: String, json: String)
    }

    companion object {
        fun fromSettings(plugin: JavaPlugin, cfg: FileConfiguration): SqlPracticeDatabase {
            val section = cfg.getConfigurationSection("DATABASE.SQL")
                ?: throw IllegalStateException("DATABASE.SQL em falta em settings.yml (HOST, USERNAME, DATABASE, etc.)")
            val topType = (cfg.getString("DATABASE.TYPE") ?: "").uppercase().replace('-', '_')
            val rawDialect = (section.getString("DIALECT") ?: section.getString("TYPE")
                ?: when (topType) {
                    "POSTGRESQL", "POSTGRES", "PG" -> "POSTGRESQL"
                    "MYSQL", "MARIADB" -> "MYSQL"
                    else -> "MYSQL"
                }).uppercase()
            val dialect = when (rawDialect) {
                "MYSQL", "MARIADB" -> SqlDialect.MYSQL
                "POSTGRESQL", "POSTGRES", "PG" -> SqlDialect.POSTGRESQL
                else -> throw IllegalArgumentException("DATABASE.SQL.DIALECT inválido: $rawDialect (use MYSQL ou POSTGRESQL)")
            }
            val jdbcOverride = section.getString("JDBC_URL")?.trim().orEmpty()
            val host = section.getString("HOST") ?: "127.0.0.1"
            val port = section.getInt("PORT", if (dialect == SqlDialect.MYSQL) 3306 else 5432)
            val database = section.getString("DATABASE") ?: "practice"
            val username = section.getString("USERNAME") ?: "root"
            val password = section.getString("PASSWORD") ?: ""
            val poolSize = section.getInt("POOL_SIZE", 10)
            val jdbcUrl = jdbcOverride.ifEmpty {
                when (dialect) {
                    SqlDialect.MYSQL ->
                        "jdbc:mysql://$host:$port/$database?useUnicode=true&characterEncoding=UTF-8" +
                            "&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                    SqlDialect.POSTGRESQL ->
                        "jdbc:postgresql://$host:$port/$database"
                }
            }
            return SqlPracticeDatabase(plugin, dialect, jdbcUrl, username, password, poolSize)
        }

        private fun maskJdbcUrl(url: String): String {
            val at = url.lastIndexOf('@')
            if (at < 0) return url
            val scheme = url.substringBefore("://", "")
            if (scheme.isEmpty()) return url
            return "$scheme://***@${url.substring(at + 1)}"
        }
    }
}
