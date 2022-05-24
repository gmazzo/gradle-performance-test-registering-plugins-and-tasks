Sample test project to research if there is actually a **performance impact** of:
1) Applying plugins to a `Project`
2) Registering (but not configuring/running) a lot of `Task`s on a `Project`

## Test Scenario
We have an `lib` java library which generates many big `data class`es (`20` classes of `1000` fields each).
```properties
amountPlugins=1
amountTasks=20
amountLines=1000
```

### Hypothesis


### Measuring
Run the build up to the execution plan (but not running the tasks), take to total build time (mostly configuration time)
```sh
./gradlew build -PamountPlugins=1 -PamountTasks=20 -PamountLines=1000 -Pmutations=0
```

### Environment
```
MacBook Pro (16-inch, 2021)
Chip Apple M1 Max
Memory 32 GB
openjdk version "11.0.13" 2021-10-19 LTS
OpenJDK Runtime Environment Zulu11.52+13-CA (build 11.0.13+8-LTS)
OpenJDK 64-Bit Server VM Zulu11.52+13-CA (build 11.0.13+8-LTS, mixed mode)
```

### Results
Each run was run twice to avoid the initial overhead of generating `buildSrc` code.

| number of plugins | number of tasks per plugin | apply plugins? | command                                                                        | took  | memory     | build scan                         |
|-------------------|----------------------------|----------------|--------------------------------------------------------------------------------|-------|------------|------------------------------------|
| 10                | 10                         | yes            | ./gradlew build -m -PamountPlugins=10 -PamountTasks=10 --scan                  | 511ms | 	286.6 MiB | https://gradle.com/s/l3io6vjzrvvqu |
| 10                | 10                         | no             | ./gradlew build -m -PamountPlugins=10 -PamountTasks=10 -Papply=false --scan    | 598ms | 280.8 MiB  | https://gradle.com/s/qrdrlogavae6o |
| 10                | 100                        | yes            | ./gradlew build -m -PamountPlugins=10 -PamountTasks=100 -Papply=true --scan    | 661ms | 	281.8 MiB | https://gradle.com/s/izcwlfre7i75c |
| 10                | 100                        | no             | ./gradlew build -m -PamountPlugins=10 -PamountTasks=100 -Papply=false --scan   | 477ms | 	283.1 MiB | https://gradle.com/s/t6pvkgcnqakca |
| 100               | 1000                       | yes            | ./gradlew build -m -PamountPlugins=100 -PamountTasks=1000 -Papply=true --scan  | 684ms | 	289.3 MiB | https://gradle.com/s/majprdj6gk2u4 |
| 100               | 1000                       | no             | ./gradlew build -m -PamountPlugins=100 -PamountTasks=1000 -Papply=false --scan | 474ms | 	296.2 MiB | https://gradle.com/s/y4nuxulsmafg2 |

To conclude, there no evidence to sustain that applying `Plugin`s or registering `Task`s affects overall performance/configuraiton time significantly.