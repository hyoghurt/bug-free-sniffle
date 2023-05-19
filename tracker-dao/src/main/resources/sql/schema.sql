CREATE TYPE "employee_status" AS ENUM (
  'ACTIVE',
  'DELETED'
);

CREATE TYPE "project_status" AS ENUM (
  'DRAFT',
  'IN_DEVELOPMENT',
  'IN_TESTING',
  'FINISHED'
);

CREATE TYPE "employee_role" AS ENUM (
  'ANALYST',
  'DEVELOPER',
  'TESTER',
  'PROJECT_MANAGER'
);

CREATE TYPE "task_status" AS ENUM (
  'OPEN',
  'IN_PROCESS',
  'DONE',
  'CLOSED'
);

CREATE TABLE "employees" (
  "id"              SERIAL          PRIMARY KEY,
  "first_name"      VARCHAR(64)     NOT NULL,
  "last_name"       VARCHAR(64)     NOT NULL,
  "middle_name"     VARCHAR(64),
  "position"        VARCHAR(64),
  "upn"             VARCHAR(128)    UNIQUE,
  "email"           VARCHAR(128),
  "status"          employee_status NOT NULL
);

CREATE TABLE "projects" (
  "id"              SERIAL          PRIMARY KEY,
  "code"            VARCHAR(128)    NOT NULL UNIQUE,
  "name"            VARCHAR(128)    NOT NULL,
  "description"     VARCHAR(1024),
  "status"          project_status  NOT NULL
);

CREATE TABLE "teams" (
  "project_id"      INTEGER         NOT NULL,
  "employee_id"     INTEGER         NOT NULL,
  "role"            employee_role   NOT NULL,
  PRIMARY KEY ("project_id", "employee_id")
);

CREATE TABLE "tasks" (
  "id"                      SERIAL          PRIMARY KEY,
  "project_id"              INTEGER         NOT NULL,
  "title"                   VARCHAR(64)     NOT NULL,
  "description"             VARCHAR(1024),
  "assignees_id"            INTEGER,
  "author_id"               INTEGER         NOT NULL,
  "status"                  task_status     NOT NULL,
  "labor_costs_in_hours"    INTEGER         NOT NULL,
  "created_datetime"        TIMESTAMP(3)    NOT NULL,
  "update_datetime"         TIMESTAMP(3),
  "deadline_datetime"       TIMESTAMP(3)    NOT NULL
);

ALTER TABLE "teams" ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("id");

ALTER TABLE "teams" ADD FOREIGN KEY ("employee_id") REFERENCES "employees" ("id");

ALTER TABLE "tasks" ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("id");

ALTER TABLE "tasks" ADD FOREIGN KEY ("assignees_id") REFERENCES "employees" ("id");

ALTER TABLE "tasks" ADD FOREIGN KEY ("author_id") REFERENCES "employees" ("id");
