Sample test project to research if there is actually a **performance impact** of:
1) Applying plugins to a `Project`
2) Registering (but not configuring/running) a lot of `Task`s on a `Project`

## Test Scenario
We have an `lib` java library which is going to apply `amountPlugins` generated plugins (`100` by default).
Each plugin will register `amountTasks` (`1000` by default) `Task`s into the `libs` module, giving `100000` tasks in total by default.

### Hypothesis
By Gradle design, applying `Plugin`s and registering `Task`s has no cost on build time, therefore plugins should be designed to register as many tasks (and sub-plugins) as needed to archive other performance features

### Measuring
Run the build up to the execution plan (but not running the tasks), take to total build time (mostly configuration time)
```sh
./gradlew build -m
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
Each run was run 3 times to avoid the initial overhead of generating `buildSrc` code.

| number of plugins | number of tasks per plugin | apply plugins? | command                                                                        | took  | memory     | build scan                         |
|-------------------|----------------------------|----------------|--------------------------------------------------------------------------------|-------|------------|------------------------------------|
| 10                | 10                         | yes            | ./gradlew build -m -PamountPlugins=10 -PamountTasks=10 --scan                  | 472ms | 	126.5 MiB | https://gradle.com/s/a4akzgxxwmg2o |
| 10                | 10                         | no             | ./gradlew build -m -PamountPlugins=10 -PamountTasks=10 -Papply=false --scan    | 461ms | 127.5 MiB  | https://gradle.com/s/khc6sapebpih6 |
| 10                | 100                        | yes            | ./gradlew build -m -PamountPlugins=10 -PamountTasks=100 -Papply=true --scan    | 661ms | 	281.8 MiB | https://gradle.com/s/izcwlfre7i75c |
| 10                | 100                        | no             | ./gradlew build -m -PamountPlugins=10 -PamountTasks=100 -Papply=false --scan   | 529ms | 	129.5 MiB | https://gradle.com/s/5g6kywpx4twdk |
| 100               | 1000                       | yes            | ./gradlew build -m -PamountPlugins=100 -PamountTasks=1000 -Papply=true --scan  | 2s    | 	271.8 MiB | https://gradle.com/s/amwtexksrqyhq |
| 100               | 1000                       | no             | ./gradlew build -m -PamountPlugins=100 -PamountTasks=1000 -Papply=false --scan | 482ms | 	264 MiB   | https://gradle.com/s/ru2dx7j2sp6r4 |

To conclude, there no evidence to sustain that applying `Plugin`s or registering `Task`s affects overall performance/configuraiton time significantly.