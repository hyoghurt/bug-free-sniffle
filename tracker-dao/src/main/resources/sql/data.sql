INSERT INTO employees(first_name, last_name, middle_name, upn, status)
    VALUES  ('kek', 'kekov', 'kekovich', 'user@com.com', 'ACTIVE'),
            ('kek2', 'kekov2', 'kekovich2', 'user2@com.com', 'ACTIVE');

INSERT INTO projects(code, name, description, status)
    VALUES  ('test_1code', 'test_1name', 'test_1desc', 'DRAFT'),
            ('test_2code', 'test_2name', 'test_2desc', 'IN_TESTING'),
            ('test_3code', 'test_3name', 'test_3desc', 'FINISHED');

INSERT INTO teams
    VALUES  (1, 1, 'ANALYST'),
            (1, 2, 'TESTER');

INSERT INTO tasks(project_id, title, author_id, status, labor_costs_in_hours, created_datetime, deadline_datetime)
    VALUES  (1, 'title', 1, 'OPEN', 1, '2020-04-28T00:00:00.000Z', '2021-04-28T00:00:00.000Z' );

