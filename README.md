[![Test workflow](https://img.shields.io/github/actions/workflow/status/hyoghurt/bug-free-sniffle/test.yml?label=test&logo=github&style=flat-square)](https://github.com/hyoghurt/bug-free-sniffle/actions/workflows/test.yml)
[![CI workflow](https://img.shields.io/github/actions/workflow/status/hyoghurt/bug-free-sniffle/ci.yml?label=docker&logo=github&style=flat-square)](https://github.com/hyoghurt/bug-free-sniffle/actions/workflows/ci.yml)  
# tracker
![Modules](https://user-images.githubusercontent.com/82288235/236947348-a64ccbb0-9fa9-488e-a3e7-924b376091a3.png)  
Схема базы данных: https://dbdiagram.io/d/64650839dca9fb07c4459a0f  
Пример тест-кейсов https://docs.google.com/spreadsheets/d/1TCuw-VQgff4URld3m6r72bvZuYUPsahoyzcJ-Ur9b_E/edit?usp=share_link  

Start: `docker compose up`  
Stop: `docker compose down -v`  

OpenAPI: http://localhost:8080/swagger-ui/index.html  
- username: user@com.com
- password: example  

RabbitMQ management: http://localhost:15672  
- username: guest
- password: guest

Up RabbitMQ and PostgreSQL: `docker compose --profile dev -f docker-compose-dev.yml up`  
