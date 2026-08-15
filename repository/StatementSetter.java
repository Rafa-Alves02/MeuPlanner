package br.com.MeuPlanner.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementSetter {
    void set(PreparedStatement stmt) throws SQLException;
}