package br.com.fiap.nora.conexoes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoFactory {

    public Connection conexao() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(
                System.getenv("ORACLE_URL"),
                System.getenv("ORACLE_USER"),
                System.getenv("ORACLE_PASS")
        );
    }
}
