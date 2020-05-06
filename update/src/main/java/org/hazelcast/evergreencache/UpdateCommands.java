package org.hazelcast.evergreencache;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

@ShellComponent
public class UpdateCommands {

    private final JdbcTemplate template;

    public UpdateCommands(JdbcTemplate template) {
        this.template = template;
    }

    @ShellMethod("Update the first name of a single entity")
    public String update(@ShellOption(defaultValue = "1") long id,
                         @ShellOption String firstName) {
        var count = template.update(
                "UPDATE Person SET firstName = ? WHERE id = ?",
                firstName,
                id);
        return "Entity " + id + " has been updated (rows updated=" + count + ")";
    }

    @ShellMethod("List all entities")
    public Table list() {
        var modelBuilder = new TableModelBuilder<>();
        modelBuilder.addRow();
        modelBuilder.addValue("id");
        modelBuilder.addValue("firstName");
        modelBuilder.addValue("lastName");
        modelBuilder.addValue("birthdate");
        template.query(
                "SELECT * FROM Person",
                (ResultSetExtractor<String>) rs -> {
                    while (rs.next()) {
                        modelBuilder.addRow();
                        modelBuilder.addValue(rs.getLong("id"));
                        modelBuilder.addValue(rs.getString("firstName"));
                        modelBuilder.addValue(rs.getString("lastName"));
                        modelBuilder.addValue(rs.getString("birthdate"));
                    }
                    return null;
                });
        var builder = new TableBuilder(modelBuilder.build());
        builder.addFullBorder(BorderStyle.oldschool);
        return builder.build();
    }
}
