# Java Lead Developer Refresher
This repository contains a Java console study app focused on common Lead Developer interview topics.
## Current phase
Phase 1 is implemented, with inheritance in Phase 2, concurrency in Phase 3, collections in Phase 4, Spring MVC patterns in Phase 5, immutability/thread-safety in Phase 6, and concurrency patterns in Phase 7:
- project scaffold and package layout
- menu/list/all topic runner
- study-output formatting template
- PowerShell build/run script
- concrete inheritance/polymorphism study example
- concrete concurrency shared-resource protection study example
- concrete concurrency patterns + thread-safe pooled resource study example
- concrete collections looping/CRUD safety study example
- concrete Spring MVC layering/patterns study example
- concrete immutability + thread-safe service-state study example
## Run the app
From `C:\1_app_dev\java\java_refresher`:
- Run all topics (recommended study mode):
  - `.\scripts\study.ps1`
- Show topic keys:
  - `.\scripts\study.ps1 -Mode list`
- Run a single topic:
  - `.\scripts\study.ps1 -TopicKey inheritance`
- Interactive menu:
  - `.\scripts\study.ps1 -Mode menu`
## Smoke check (single command)
- Command:
  - `.\\scripts\\study.ps1`
- Expected outcomes:
  - build and run complete without errors (`[2/3] Compiling...`, `[3/3] Running...`)
  - each topic prints a `Lead Interview Q&A` section
  - concurrency results include `Unsafe (no lock)`, `AtomicInteger`, `Synchronized method`, and `ReentrantLock`
  - thread-safe counters (`AtomicInteger`, `Synchronized`, `ReentrantLock`) should report `lostUpdates=0`
## Topic keys
- `inheritance`
- `concurrency`
- `concurrency-patterns`
- `collections`
- `spring-mvc`
- `immutability`
