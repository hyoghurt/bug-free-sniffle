INSERT INTO employees(first_name, last_name, middle_name, upn, status)
    VALUES  ('test_1first', 'test_1last', 'test_1middle', 'test_1upn@com.com', 'ACTIVE'),
            ('test_2first', 'test_2last', 'test_2middle', 'test_2upn@com.com', 'ACTIVE'),
            ('test_3first', 'test_3last', 'test_3middle', 'test_3upn@com.com', 'ACTIVE');

INSERT INTO projects(code, name, description, status)
    VALUES  ('test_1code', 'test_1name', 'test_1desc', 'DRAFT'),
            ('test_2code', 'test_2name', 'test_2desc', 'IN_TESTING'),
            ('test_3code', 'test_3name', 'test_3desc', 'FINISHED');

INSERT INTO teams
    VALUES  (1, 1, 'ANALYST'),
            (1, 2, 'TESTER'),
            (1, 3, 'PROJECT_MANAGER'),
            (2, 1, 'ANALYST'),
            (2, 2, 'TESTER'),
            (2, 3, 'PROJECT_MANAGER'),
            (3, 1, 'ANALYST'),
            (3, 2, 'TESTER'),
            (3, 3, 'PROJECT_MANAGER');
