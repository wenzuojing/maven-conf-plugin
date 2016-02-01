

Now , we have two environment(dev&test).

## add evn properties

```
conf-envs
├── dev.properties
└── prod.properties
```

## add conf template

```
auto-conf
├── db
│   └── db.properties
└── server.properties
```

## maven config

add profiles
```
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <env>dev</env>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>

    <profile>
        <id>prod</id>
        <properties>
            <env>prod</env>
        </properties>
    </profile>
</profiles>
```
add plugin
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-conf-plugin</artifactId>
    <version>1.1.0</version>
    <configuration>
        <evn>src/main/conf-envs/${env}.properties</evn>
        <input>src/main/auto-conf</input>
        <output>src/main/resources</output>
    </configuration>
    <executions>
        <execution>
            <id>filter</id>
            <phase>initialize</phase>
            <goals>
                <goal>conf</goal>
            </goals>
        </execution>

    </executions>
</plugin>
```
</project>

## how to use

generate dev confg
`mvn conf:conf`

generate test confg
`mvn conf:conf`
