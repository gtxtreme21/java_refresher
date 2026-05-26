# Java Lead Developer Refresher
This repository contains a Java console study app focused on common Lead Developer interview topics.
## Current phase
Phase 1 is implemented, with inheritance in Phase 2, concurrency in Phase 3, and collections in Phase 4:
- project scaffold and package layout
- menu/list/all topic runner
- study-output formatting template
- PowerShell build/run script
- concrete inheritance/polymorphism study example
- concrete concurrency shared-resource protection study example
- concrete collections looping/CRUD safety study example
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
## Topic keys
- `inheritance`
- `concurrency`
- `collections`
- `spring-mvc`
- `immutability`
