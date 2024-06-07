package me.arial.zephyr.api.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.arial.zephyr.api.module.ZephyrModule
import org.bukkit.plugin.Plugin
import org.jdbi.v3.cache.caffeine.CaffeineCachePlugin
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.HandleConsumer
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.async.JdbiExecutor
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import java.io.File
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object DatabaseCreator {
    /**
     * Создаёт базовую SQLite базу данных
     *
     * @param plugin Объект плагина
     * @param fileName Название файла базы данных
     * @param templateFileName Путь до файла в .jar файле плагина с SQL запросами
     *
     * @return Объект [JdbiExecutor]
     */
    fun createSQLiteDatabase(plugin: Plugin, fileName: String = "database.db", templateFileName: String = "template.sql"): JdbiExecutor {
        val db = File(plugin.dataFolder, fileName)
        if (!db.exists()) {
            try {
                db.createNewFile()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        val config = HikariConfig()
        config.driverClassName = "org.sqlite.JDBC"
        config.connectionTestQuery = "SELECT 1"
        config.jdbcUrl = "jdbc:sqlite:$db"

        val dataSource = HikariDataSource(config)

        val jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(CaffeineCachePlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        try {
            plugin.javaClass.getResourceAsStream("/$templateFileName").use { inputStream ->
                jdbi.useHandle(
                    HandleConsumer<IOException> { handle: Handle ->
                        handle.createScript(
                            String(
                                inputStream!!.readAllBytes()
                            )
                        ).execute()
                    })
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        val executorObj: Executor = Executors.newFixedThreadPool(8)

        return JdbiExecutor.create(jdbi, executorObj)
    }

    /**
     * Создаёт базовую SQLite базу данных
     *
     * @param module Объект модуля
     * @param fileName Название файла базы данных
     * @param templateFileName Путь до файла в .jar файле модуля с SQL запросами
     *
     * @return Объект [JdbiExecutor]
     */
    fun createSQLiteDatabase(module: ZephyrModule, fileName: String = "database.db", templateFileName: String = "template.sql"): JdbiExecutor {
        val db = File(module.dataFolder, fileName)
        if (!db.exists()) {
            try {
                db.createNewFile()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        val config = HikariConfig()
        config.driverClassName = "org.sqlite.JDBC"
        config.connectionTestQuery = "SELECT 1"
        config.jdbcUrl = "jdbc:sqlite:$db"

        val dataSource = HikariDataSource(config)

        val jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(CaffeineCachePlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        try {
            module.javaClass.getResourceAsStream("/$templateFileName").use { inputStream ->
                jdbi.useHandle(
                    HandleConsumer<IOException> { handle: Handle ->
                        handle.createScript(
                            String(
                                inputStream!!.readAllBytes()
                            )
                        ).execute()
                    })
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        val executorObj: Executor = Executors.newFixedThreadPool(8)

        return JdbiExecutor.create(jdbi, executorObj)
    }

    /**
     * Подключение к MySQL базе данных
     *
     * @param plugin Объект плагина
     * @param host Хост базы данных
     * @param port Порт базы данных
     * @param database Название базы данных
     * @param user Юзер базы данных
     * @param password Пароль для юзера базы данных
     * @param templateFileName Путь до файла в .jar файле плагина с SQL запросами
     *
     * @return Объект [JdbiExecutor]
     */
    fun createMySQLDatabase(plugin: Plugin, host: String, port: Int, database: String, user: String, password: String, templateFileName: String = "template.sql"): JdbiExecutor {
        val config = HikariConfig()

        config.jdbcUrl = "jdbc:mysql://" + host +
                ":" + port +
                "/" + database + "?autoReconnect=true&useSSL=false&characterEncoding=utf8"
        config.username = user
        config.password = password

        val dataSource = HikariDataSource(config)

        val jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(CaffeineCachePlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        try {
            plugin.javaClass.getResourceAsStream("/$templateFileName").use { inputStream ->
                jdbi.useHandle(HandleConsumer<IOException> { handle: Handle ->
                    handle.createScript(
                        String(inputStream!!.readAllBytes())
                    ).execute()
                })
                jdbi.useHandle(HandleConsumer<RuntimeException> { handle: Handle ->
                    handle.createUpdate(
                        "SET @@group_concat_max_len = 1000000"
                    ).execute()
                })
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        val executorObj = Executors.newFixedThreadPool(8)

        return JdbiExecutor.create(jdbi, executorObj)
    }

    /**
     * Подключение к MySQL базе данных
     *
     * @param module Объект модуля
     * @param host Хост базы данных
     * @param port Порт базы данных
     * @param database Название базы данных
     * @param user Юзер базы данных
     * @param password Пароль для юзера базы данных
     * @param templateFileName Путь до файла в .jar файле модуля с SQL запросами
     *
     * @return Объект [JdbiExecutor]
     */
    fun createMySQLDatabase(module: ZephyrModule, host: String, port: Int, database: String, user: String, password: String, templateFileName: String = "template.sql"): JdbiExecutor {
        val config = HikariConfig()

        config.jdbcUrl = "jdbc:mysql://" + host +
                ":" + port +
                "/" + database + "?autoReconnect=true&useSSL=false&characterEncoding=utf8"
        config.username = user
        config.password = password

        val dataSource = HikariDataSource(config)

        val jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(CaffeineCachePlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        try {
            module.javaClass.getResourceAsStream("/$templateFileName").use { inputStream ->
                jdbi.useHandle(HandleConsumer<IOException> { handle: Handle ->
                    handle.createScript(
                        String(inputStream!!.readAllBytes())
                    ).execute()
                })
                jdbi.useHandle(HandleConsumer<RuntimeException> { handle: Handle ->
                    handle.createUpdate(
                        "SET @@group_concat_max_len = 1000000"
                    ).execute()
                })
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        val executorObj = Executors.newFixedThreadPool(8)

        return JdbiExecutor.create(jdbi, executorObj)
    }
}