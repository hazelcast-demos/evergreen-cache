package org.hazelcast.evergreencache;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.Util;
import com.hazelcast.jet.cdc.ChangeRecord;
import com.hazelcast.jet.cdc.mysql.MySqlCdcSources;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamSource;

import java.util.Map;

public class UpdateCacheJob {

    private static final String DB_SERVER_NAME = "evergreen-cache";
    private static final String DB_SCHEMA = "app";
    private static final String DB_NAMESPACED_TABLE = DB_SCHEMA + ".Person";

    public static void main(String[] args) throws InterruptedException {
        waitForMySql();
        var update = new UpdateCacheJob();
        var cfg = new JobConfig().setName("mysql-monitor");
        Jet.newJetInstance().newJob(update.pipeline(), cfg);
    }

    private static void waitForMySql() throws InterruptedException {
        var env = System.getenv();
        var waitTime = Integer.parseInt(env.getOrDefault("WAIT_TIME", "0"));
        Thread.sleep(waitTime);
    }

    private StreamSource<ChangeRecord> theDatabase() {
        var env = System.getenv();
        return MySqlCdcSources.mysql("mysql-connector")
                .setDatabaseAddress(env.getOrDefault("MYSQL_HOST", "localhost"))
                .setDatabasePort(Integer.parseInt(env.getOrDefault("MYSQL_PORT", "3306")))
                .setDatabaseUser(env.getOrDefault("MYSQL_USER", "root"))
                .setDatabasePassword(env.getOrDefault("MYSQL_PASSWORD", "root"))
                .setClusterName(DB_SERVER_NAME)
                .setDatabaseWhitelist(DB_SCHEMA)
                .setTableWhitelist(DB_NAMESPACED_TABLE)
                .build();
    }

    private Sink<Map.Entry<Long, Person>> theCache() {
        var env = System.getenv();
        return Sinks.remoteMap(
                "entities",
                new CustomClientConfig(env.get("CACHE_HOST"))
        );
    }

    private Pipeline pipeline() {
        var pipeline = Pipeline.create();
        FunctionEx<ChangeRecord, Person> toPerson = change -> change.value().toObject(Person.class);
        FunctionEx<Person, Map.Entry<Long, Person>> toMapEntry = person -> Util.entry(person.id, person);
        pipeline.readFrom(theDatabase())
                .withoutTimestamps()
                .peek()
                .map(toPerson.andThen(toMapEntry))
                .writeTo(theCache());
        return pipeline;
    }
}
