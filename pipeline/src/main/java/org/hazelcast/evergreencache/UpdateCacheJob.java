package org.hazelcast.evergreencache;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.Util;
import com.hazelcast.jet.cdc.ChangeRecord;
import com.hazelcast.jet.cdc.mysql.MySqlCdcSources;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamSource;

import java.util.Map;

public class UpdateCacheJob {

    private static final String DB_SERVER_NAME = "evergreen-cache";
    private static final String DB_SCHEMA = "app";
    private static final String DB_NAMESPACED_TABLE = DB_SCHEMA + ".Person";

    public static void main(String[] args) throws InterruptedException {
        waitForMySql();
        JobConfig cfg = new JobConfig().setName("mysql-monitor");
        Jet.newJetInstance().newJob(pipeline(), cfg);
    }

    private static void waitForMySql() throws InterruptedException {
        Map<String, String> env = System.getenv();
        int waitTime = Integer.parseInt(env.getOrDefault("WAIT_TIME", "0"));
        Thread.sleep(waitTime);
    }

    private static Pipeline pipeline() {
        Pipeline pipeline = Pipeline.create();
        Map<String, String> env = System.getenv();
        StreamSource<ChangeRecord> source = MySqlCdcSources.mysql("mysql-connector")
                .setDatabaseAddress(env.getOrDefault("MYSQL_HOST", "localhost"))
                .setDatabasePort(Integer.parseInt(env.getOrDefault("MYSQL_PORT", "3306")))
                .setDatabaseUser(env.getOrDefault("MYSQL_USER", "root"))
                .setDatabasePassword(env.getOrDefault("MYSQL_PASSWORD", "root"))
                .setClusterName(DB_SERVER_NAME)
                .setDatabaseWhitelist(DB_SCHEMA)
                .setTableWhitelist(DB_NAMESPACED_TABLE)
                .build();
        FunctionEx<ChangeRecord, Person> toPerson = r -> r.value().toObject(Person.class);
        FunctionEx<Person, Map.Entry<Long, Person>> toMapEntry = p -> Util.entry(p.id, p);
        pipeline.readFrom(source)
                .withoutTimestamps()
                .peek()
                .map(toPerson.andThen(toMapEntry))
                .writeTo(Sinks.remoteMap(
                        "entities",
                        new CustomClientConfig(env.get("CACHE_HOST"))
                ));
        return pipeline;
    }
}
